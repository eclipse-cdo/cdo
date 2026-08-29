/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements the semantic three-way merge used for concurrent changes of one many-valued CDO feature.
 * <p>
 * Numeric list indexes are intentionally confined to decoding the two causal delta histories and encoding the final
 * semantic result. The merge core identifies list occurrences independently of their values and represents placement
 * with immutable historical {@link Position positions} in a partial order. Consequently equal occurrences in a
 * non-unique feature stay distinct, repeated MOVE operations keep addressing the same occurrence, and a MOVE
 * destination remains an insertion boundary instead of being reduced to a fragile numeric displacement.
 * <p>
 * The per-invocation pipeline is:
 * <ol>
 * <li>create shared result-base occurrences and positions,</li>
 * <li>decode source and target independently into side-local semantic histories,</li>
 * <li>normalize their effective presence, representative value, and placement,</li>
 * <li>merge those dimensions and resolve uniqueness,</li>
 * <li>linearize only the surviving effective positions, and</li>
 * <li>encode and replay-validate a fresh executable {@link CDOListFeatureDelta}.</li>
 * </ol>
 * All mutable state belongs to one instance and therefore to one merge invocation.
 */
final class SemanticCDOListMerger
{
  /**
   * The feature whose two histories are being merged.
   */
  private final EStructuralFeature feature;

  /**
   * The source/remote executable list delta.
   */
  private final CDOListFeatureDelta sourceDelta;

  /**
   * The target/local executable list delta.
   */
  private final CDOListFeatureDelta targetDelta;

  /**
   * The revision state against which the encoded merge result will be applied. It is the materialization baseline for
   * shared occurrence identities, but on branch remerge it need not equal either side's causal start revision.
   */
  private final CDORevision resultBaseRevision;

  /**
   * The revision state from which the target/local executable history starts.
   */
  private final CDORevision targetBaseRevision;

  /**
   * The revision state from which the source/remote executable history starts.
   */
  private final CDORevision sourceBaseRevision;

  /**
   * The policy callbacks supplied by the owning {@link DefaultCDOMerger.PerFeature.ManyValued}.
   */
  private final Policies policies;

  /**
   * Shared immutable lineages for the occurrences in the result/application base.
   */
  private final List<Lineage> ancestorLineages = new ArrayList<>();

  /**
   * Shared immutable occurrence identities for the result/application base.
   */
  private final List<Occurrence> ancestorOccurrences = new ArrayList<>();

  /**
   * Shared historical positions occupied by the ancestor occurrences.
   */
  private final List<Position> ancestorPositions = new ArrayList<>();

  /**
   * The permanent lower sentinel for every placement history.
   */
  private final Position start = new Position("START", Origin.ANCESTOR, -1, -1, true);

  /**
   * The permanent upper sentinel for every placement history.
   */
  private final Position end = new Position("END", Origin.ANCESTOR, Integer.MAX_VALUE, Integer.MAX_VALUE, true);

  /**
   * Monotonic ordinal used as the final deterministic tie-breaker for positions.
   */
  private int nextPositionOrdinal;

  /**
   * Monotonic ordinal used as the final deterministic tie-breaker for occurrences.
   */
  private int nextOccurrenceOrdinal;

  /**
   * The decoded source/remote semantic history.
   */
  private SideState source;

  /**
   * The decoded target/local semantic history.
   */
  private SideState target;

  /**
   * The merged partial order containing all hard historical constraints.
   */
  private PositionOrder mergedOrder;

  /**
   * Semantic decisions for surviving and removed occurrences, in deterministic creation order.
   */
  private final List<MergedOccurrence> mergedOccurrences = new ArrayList<>();

  /**
   * Redirects created by SET replacement and uniqueness coalescing.
   */
  private final Map<Occurrence, Redirect> redirects = new IdentityHashMap<>();

  /**
   * The final semantic result after linearization and set-state resolution.
   */
  private SemanticResult semanticResult;

  /**
   * The encoded result delta, retained for lazy diagnostics after validation.
   */
  private CDOListFeatureDelta encodedDelta;

  /**
   * Semantic occurrences corresponding to emitted ADD deltas in executable order. The replay validator uses this
   * identity plan because a uniqueness-cycle fallback may re-add an ancestor lineage as well as ordinary additions.
   */
  private final List<MergedOccurrence> encodedAddOccurrences = new ArrayList<>();

  /**
   * A human-readable explanation when policy resolution reports an ordinary semantic conflict.
   */
  private String conflict;

  /**
   * Creates one isolated semantic merge engine.
   *
   * @param feature the many-valued structural feature being merged.
   * @param targetDelta the target/local executable history.
   * @param sourceDelta the source/remote executable history.
   * @param resultBaseRevision the revision against which the encoded result will be applied.
   * @param targetBaseRevision the revision from which the target/local delta starts.
   * @param sourceBaseRevision the revision from which the source/remote delta starts.
   * @param policies the immutable policy adapter owned by the merger instance.
   */
  public SemanticCDOListMerger(EStructuralFeature feature, //
      CDOListFeatureDelta targetDelta, CDOListFeatureDelta sourceDelta, //
      CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision, //
      Policies policies)
  {
    this.feature = feature;
    this.targetDelta = targetDelta;
    this.sourceDelta = sourceDelta;
    this.resultBaseRevision = resultBaseRevision;
    this.targetBaseRevision = targetBaseRevision != null ? targetBaseRevision : resultBaseRevision;
    this.sourceBaseRevision = sourceBaseRevision != null ? sourceBaseRevision : resultBaseRevision;
    this.policies = policies;
  }

  /**
   * Executes the complete semantic pipeline.
   *
   * @return a fresh executable result delta, or {@code null} for an unresolved ordinary merge conflict.
   */
  public CDOFeatureDelta merge()
  {
    if (resultBaseRevision == null && (targetDelta.getOriginSize() != 0 || sourceDelta.getOriginSize() != 0 || feature.isUnsettable()))
    {
      // Delta origin size alone cannot distinguish equal non-unique occurrences by value, detect uniqueness collisions
      // with unchanged values, or recover SET [] versus UNSET []. Refuse a content-bearing ancestor rather than
      // silently inventing values; CDO's branch and conflict-resolver call paths attach the required provider.
      conflict = "Full result-base revision is required for a non-empty semantic list merge";
      return null;
    }

    initializeAncestor();

    // Each decoder receives the same ancestor identities but its own graph and visible-list entries.
    source = decode(Origin.SOURCE, sourceDelta);
    target = decode(Origin.TARGET, targetDelta);

    // Normalization derives net effects without deleting the operation provenance kept by each side.
    source.normalize();
    target.normalize();

    if (!mergeSetStateAndOccurrences())
    {
      return null;
    }

    if (!resolveUniqueness())
    {
      return null;
    }

    List<MergedOccurrence> ordered = linearize();
    if (ordered == null)
    {
      return null;
    }

    semanticResult = new SemanticResult(ordered, resolveFinalSetState(ordered));
    encodedDelta = encode(semanticResult);
    validateReplay(encodedDelta, semanticResult);
    return encodedDelta;
  }

  /**
   * Returns a deterministic, deliberately human-readable dump of all semantic phases completed so far.
   * Text is generated lazily so successful merges pay no continuous string-building cost.
   */
  public String dump()
  {
    StringBuilder builder = new StringBuilder();
    appendFeatureDump(builder);
    appendAncestorDump(builder);
    appendSideDump(builder, source);
    appendSideDump(builder, target);
    appendMergeDump(builder);
    appendOrderingDump(builder);
    appendResultDump(builder);
    return builder.toString();
  }

  /**
   * Returns the semantic conflict description produced by a focused policy, or {@code null} if no conflict occurred.
   */
  public String getConflict()
  {
    return conflict;
  }

  /**
   * Builds shared result-base identities and the compact adjacent result-base position chain. On ordinary three-way
   * merges this is also the common ancestor. On branch remerge it is only the application baseline; each decoder gets
   * its own causal base visibility below.
   */
  private void initializeAncestor()
  {
    InternalCDORevision internalResultBase = (InternalCDORevision)resultBaseRevision;
    CDOList resultBaseValues = internalResultBase == null ? null : internalResultBase.getListOrNull(feature);
    int resultBaseSize;

    if (resultBaseValues != null)
    {
      resultBaseSize = resultBaseValues.size();
    }
    else if (internalResultBase != null)
    {
      resultBaseSize = 0;
    }
    else
    {
      int targetOriginSize = targetDelta.getOriginSize();
      int sourceOriginSize = sourceDelta.getOriginSize();
      if (targetOriginSize != sourceOriginSize)
      {
        throw invariant("Result base is unavailable and source/target origin sizes differ: " + sourceOriginSize + " != " + targetOriginSize);
      }

      resultBaseSize = targetOriginSize;
    }

    validateSideBase("target", targetDelta, targetBaseRevision);
    validateSideBase("source", sourceDelta, sourceBaseRevision);

    Position previous = start;
    nextPositionOrdinal = 1;
    nextOccurrenceOrdinal = 0;

    for (int i = 0; i < resultBaseSize; i++)
    {
      Object value = resultBaseValues == null ? CDOFeatureDelta.UNKNOWN_VALUE : resultBaseValues.get(i);
      Lineage lineage = new Lineage("L" + i, Origin.ANCESTOR, i, value);
      Position position = new Position("P" + i, Origin.ANCESTOR, i, nextPositionOrdinal++, false);
      Occurrence occurrence = new Occurrence("A" + i, lineage, value, Origin.ANCESTOR, nextOccurrenceOrdinal++);

      lineage.ancestorOccurrence = occurrence;
      lineage.ancestorPosition = position;
      ancestorLineages.add(lineage);
      ancestorPositions.add(position);
      ancestorOccurrences.add(occurrence);

      // Adjacent constraints represent the complete result-base order without materializing transitive closure.
      lineage.ancestorPredecessor = previous;
      previous = position;
    }

    // The terminal link makes beginning/end insertions use the same bounded placement model as interior insertions.
    for (int i = 0; i < ancestorLineages.size(); i++)
    {
      ancestorLineages.get(i).ancestorSuccessor = i + 1 < ancestorPositions.size() ? ancestorPositions.get(i + 1) : end;
    }
  }

  /**
   * Verifies that one executable history really starts from the supplied side-specific base revision.
   */
  private void validateSideBase(String name, CDOListFeatureDelta delta, CDORevision baseRevision)
  {
    if (baseRevision == null)
    {
      if (delta.getOriginSize() != 0)
      {
        throw invariant("Missing " + name + " base revision for origin size " + delta.getOriginSize());
      }

      return;
    }

    CDOList values = ((InternalCDORevision)baseRevision).getListOrNull(feature);
    int baseSize = values == null ? 0 : values.size();
    if (baseSize != delta.getOriginSize())
    {
      throw invariant(name + " base size " + baseSize + " differs from delta origin size " + delta.getOriginSize());
    }
  }

  /**
   * Decodes one side independently by executing numeric deltas against exactly the occurrences visible in that side's
   * causal base. Result-base occurrences absent from this base remain unobserved rather than becoming removals.
   */
  private SideState decode(Origin side, CDOListFeatureDelta delta)
  {
    CDORevision baseRevision = side == Origin.SOURCE ? sourceBaseRevision : targetBaseRevision;
    SideState state = new SideState(side, setState(baseRevision));
    initializeSideBase(state, baseRevision, delta.getOriginSize());

    int operationSequence = 0;
    for (CDOFeatureDelta change : delta.getListChanges())
    {
      decodeChange(state, change, operationSequence++);
    }

    return state;
  }

  /**
   * Initializes one decoder's visible list by correlating its causal base occurrences with result-base identities.
   * Matching is value/ID based and consumes equal result-base occurrences in stable side-base order, which preserves
   * multiplicity for non-unique features. A side-base occurrence that is absent from the result base is represented as
   * a side-local base occurrence: it was already visible before this side's executable history, but from the encoder's
   * result-base perspective it is content that must be materialized if it survives the semantic merge.
   */
  private void initializeSideBase(SideState state, CDORevision baseRevision, int originSize)
  {
    CDOList baseValues = baseRevision == null ? null : ((InternalCDORevision)baseRevision).getListOrNull(feature);
    if (baseValues == null && originSize == 0)
    {
      state.order.addConstraint(start, end, new Provenance(state.side, -1, "base"));
      return;
    }

    if (baseValues == null || baseValues.size() != originSize)
    {
      throw invariant("Cannot initialize " + state.side + " base visibility for origin size " + originSize);
    }

    Set<Lineage> consumed = Collections.newSetFromMap(new IdentityHashMap<Lineage, Boolean>());
    Position previous = start;
    int preferredIndex = 0;

    for (int i = 0; i < baseValues.size(); i++)
    {
      Object value = baseValues.get(i);
      int resultIndex = findResultBaseOccurrence(value, consumed, preferredIndex);
      Lineage lineage;
      Position position;
      Occurrence occurrence;

      if (resultIndex == -1)
      {
        // The side base can legitimately be newer/different than the selected application base. Preserve this as an
        // occurrence that predates the side delta instead of inventing a REMOVE from the result-base snapshot.
        lineage = new Lineage(state.prefix + "BL" + i, state.side, i, CDOFeatureDelta.UNKNOWN_VALUE);
        position = new Position("PB" + state.prefix + i, state.side, -1, nextPositionOrdinal++, false);
        occurrence = new Occurrence(state.prefix + "BA" + i, lineage, value, state.side, nextOccurrenceOrdinal++);
        lineage.addedOccurrence = occurrence;

        state.addedLineages.add(lineage);
        state.allOccurrences.add(occurrence);
      }
      else
      {
        lineage = ancestorLineages.get(resultIndex);
        position = lineage.ancestorPosition;
        occurrence = lineage.ancestorOccurrence;
        consumed.add(lineage);
        state.observedBaseLineages.add(lineage);
        preferredIndex = resultIndex + 1;
      }

      state.visible.add(new Entry(occurrence, position));
      state.order.addConstraint(previous, position, new Provenance(state.side, -1, resultIndex == -1 ? "side base only" : "base"));
      previous = position;
    }

    state.order.addConstraint(previous, end, new Provenance(state.side, -1, "base"));
  }

  /**
   * Finds the next unmatched result-base occurrence equal to a side-base value. The forward-then-wrap search keeps
   * unchanged order exact, follows moved unique values, and gives duplicate values a deterministic occurrence rank.
   */
  private int findResultBaseOccurrence(Object value, Set<Lineage> consumed, int preferredIndex)
  {
    for (int pass = 0; pass < 2; pass++)
    {
      int from = pass == 0 ? Math.min(preferredIndex, ancestorLineages.size()) : 0;
      int to = pass == 0 ? ancestorLineages.size() : Math.min(preferredIndex, ancestorLineages.size());

      for (int i = from; i < to; i++)
      {
        Lineage lineage = ancestorLineages.get(i);
        if (!consumed.contains(lineage) && valuesEqual(lineage.ancestorValue, value))
        {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the set-state represented by a concrete base revision. Non-unsettable features are always SET.
   */
  private SetState setState(CDORevision revision)
  {
    if (!feature.isUnsettable())
    {
      return SetState.SET;
    }

    if (revision == null)
    {
      throw invariant("A base revision is required to merge unsettable many-valued feature " + feature);
    }

    return ((InternalCDORevision)revision).getValue(feature) == null ? SetState.UNSET : SetState.SET;
  }

  /**
   * Returns the orthogonal set-state of the result/application base.
   */
  private SetState initialSetState()
  {
    return setState(resultBaseRevision);
  }

  /**
   * Executes one CDO delta according to its actual application coordinate system and records its semantic effect.
   */
  private void decodeChange(SideState state, CDOFeatureDelta change, int sequence)
  {
    switch (change.getType())
    {
    case ADD:
      decodeAdd(state, (CDOAddFeatureDelta)change, sequence);
      break;

    case REMOVE:
      decodeRemove(state, (CDORemoveFeatureDelta)change, sequence, RemovalCause.EXPLICIT_REMOVE);
      break;

    case SET:
      decodeSet(state, (CDOSetFeatureDelta)change, sequence);
      break;

    case MOVE:
      decodeMove(state, (CDOMoveFeatureDelta)change, sequence);
      break;

    case CLEAR:
      decodeClear(state, change, sequence, RemovalCause.CLEAR);
      break;

    case UNSET:
      decodeClear(state, change, sequence, RemovalCause.UNSET);
      state.setState = SetState.UNSET;
      state.unsetOperation = state.operations.get(state.operations.size() - 1);
      break;

    default:
      throw invariant("Unsupported list change type " + change.getType());
    }
  }

  /**
   * Decodes ADD by creating a side-local occurrence and a position bounded only by currently visible neighbors.
   */
  private void decodeAdd(SideState state, CDOAddFeatureDelta change, int sequence)
  {
    int index = clamp(change.getIndex(), 0, state.visible.size());
    Position position = createPosition(state, sequence, "ADD", index);
    Lineage lineage = new Lineage(state.prefix + "L" + sequence, state.side, sequence, CDOFeatureDelta.UNKNOWN_VALUE);
    lineage.addCreated = true;
    Occurrence occurrence = new Occurrence(state.prefix + "O" + sequence, lineage, change.getValue(), state.side, nextOccurrenceOrdinal++);

    lineage.addedOccurrence = occurrence;

    state.allOccurrences.add(occurrence);
    state.visible.add(index, new Entry(occurrence, position));
    state.addedLineages.add(lineage);
    state.setState = SetState.SET;
    state.operations.add(new Operation(sequence, change, "created " + occurrence.id + " at " + position.id));
  }

  /**
   * Decodes REMOVE by addressing the occurrence at the current numeric index, never by searching for an equal value.
   */
  private void decodeRemove(SideState state, CDORemoveFeatureDelta change, int sequence, RemovalCause cause)
  {
    int index = requireElementIndex(change.getIndex(), state.visible.size(), change);
    Entry removed = state.visible.remove(index);

    state.removals.put(removed.occurrence.lineage, cause);
    state.operations.add(new Operation(sequence, change, "removed " + removed.occurrence.id + " from " + removed.position.id));
  }

  /**
   * Decodes SET as a replacement occurrence in the addressed lineage while preserving its current placement.
   */
  private void decodeSet(SideState state, CDOSetFeatureDelta change, int sequence)
  {
    int index = requireElementIndex(change.getIndex(), state.visible.size(), change);
    Entry oldEntry = state.visible.get(index);
    Occurrence replacement = new Occurrence(state.prefix + "O" + sequence, oldEntry.occurrence.lineage, change.getValue(), state.side, nextOccurrenceOrdinal++);

    // SET changes occurrence identity and value, but its replacement inherits the effective historical placement.
    state.allOccurrences.add(replacement);
    state.visible.set(index, new Entry(replacement, oldEntry.position));
    redirects.put(oldEntry.occurrence, new Redirect(replacement, RedirectCause.REPLACEMENT));
    state.operations.add(new Operation(sequence, change, "replaced " + oldEntry.occurrence.id + " with " + replacement.id + " at " + oldEntry.position.id));
  }

  /**
   * Decodes MOVE by removing the addressed occurrence, interpreting newPosition in the reduced list, and creating a
   * fresh historical destination position for the same occurrence identity.
   */
  private void decodeMove(SideState state, CDOMoveFeatureDelta change, int sequence)
  {
    int oldPosition = requireElementIndex(change.getOldPosition(), state.visible.size(), change);
    Entry moved = state.visible.remove(oldPosition);
    int newPosition = clamp(change.getNewPosition(), 0, state.visible.size());

    if (newPosition == oldPosition)
    {
      // EList.move to the same final index is a semantic no-op and must not manufacture a new placement intent.
      state.visible.add(newPosition, moved);
      state.operations.add(new Operation(sequence, change, "kept " + moved.occurrence.id + " at " + moved.position.id + " (no-op)"));
      return;
    }

    Position destination = createPosition(state, sequence, "MOVE", newPosition);

    // The old position deliberately remains in the graph as a landmark for concurrent histories.
    state.visible.add(newPosition, new Entry(moved.occurrence, destination));
    state.operations.add(new Operation(sequence, change, "moved " + moved.occurrence.id + " from " + moved.position.id + " to " + destination.id));
  }

  /**
   * Decodes CLEAR or UNSET as observation and removal of exactly the occurrences currently visible to this side.
   */
  private void decodeClear(SideState state, CDOFeatureDelta change, int sequence, RemovalCause cause)
  {
    List<String> observed = new ArrayList<>();
    for (Entry entry : state.visible)
    {
      observed.add(entry.occurrence.id);
      state.removals.put(entry.occurrence.lineage, cause);
      state.observedByClearOrUnset.add(entry.occurrence.lineage);
    }

    // Historical positions survive even though every currently visible occurrence is removed.
    state.visible.clear();
    state.setState = cause == RemovalCause.UNSET ? SetState.UNSET : SetState.SET;
    Operation operation = new Operation(sequence, change, cause + " observed " + observed);
    state.operations.add(operation);
    if (cause == RemovalCause.CLEAR)
    {
      state.clearOperation = operation;
    }
  }

  /**
   * Creates a side-local position between the immediate visible lower and upper bounds at an insertion index.
   */
  private Position createPosition(SideState state, int sequence, String cause, int index)
  {
    Position lower = index == 0 ? start : state.visible.get(index - 1).position;
    Position upper = index == state.visible.size() ? end : state.visible.get(index).position;
    Position position = new Position("P" + state.prefix + sequence, state.side, sequence, nextPositionOrdinal++, false);
    Provenance provenance = new Provenance(state.side, sequence, cause);

    // These are the only relations justified by the visible insertion boundary. Invisible historical positions inside
    // the collapsed interval deliberately remain incomparable with the new position.
    state.order.addConstraint(lower, position, provenance);
    state.order.addConstraint(position, upper, provenance);
    return position;
  }

  /**
   * Validates and returns an element index used by REMOVE, SET, or MOVE-old coordinates.
   */
  private int requireElementIndex(int index, int size, CDOFeatureDelta change)
  {
    if (index < 0 || index >= size)
    {
      throw invariant("Invalid index " + index + " for " + change + " in virtual list of size " + size);
    }

    return index;
  }

  /**
   * Merges feature set-state and every ancestor-rooted lineage by the orthogonal presence, content, and placement
   * dimensions, then appends surviving side-local additions.
   */
  private boolean mergeSetStateAndOccurrences()
  {
    mergedOrder = new PositionOrder("MERGED");
    mergedOrder.addAll(source.order);
    mergedOrder.addAll(target.order);

    boolean sourceEffectiveUnset = source.hasEffectiveUnset();
    boolean targetEffectiveUnset = target.hasEffectiveUnset();
    if (sourceEffectiveUnset || targetEffectiveUnset)
    {
      if (!resolveUnsetIntent(sourceEffectiveUnset, targetEffectiveUnset))
      {
        return false;
      }
    }

    for (Lineage lineage : ancestorLineages)
    {
      SideLineage sourceState = source.lineages.get(lineage);
      SideLineage targetState = target.lineages.get(lineage);

      MergedOccurrence merged = mergeAncestorLineage(lineage, sourceState, targetState);
      if (merged == MergedOccurrence.CONFLICT)
      {
        return false;
      }

      if (merged != null)
      {
        mergedOccurrences.add(merged);
      }
    }

    // Adds are side-local identities. A CLEAR/UNSET on the other side could not have observed and addressed them.
    addSurvivingAdditions(source);
    addSurvivingAdditions(target);

    if (!applyDominatingClearOrUnset())
    {
      return false;
    }

    return true;
  }

  /**
   * Resolves an effective UNSET intent against only the other side's effective mutations.
   */
  private boolean resolveUnsetIntent(boolean sourceUnset, boolean targetUnset)
  {
    if (sourceUnset && targetUnset)
    {
      return true;
    }

    SideState unsetting = sourceUnset ? source : target;
    SideState other = sourceUnset ? target : source;

    List<Lineage> concurrentMutations = other.effectiveMutationsAgainstUnset(unsetting);
    if (concurrentMutations.isEmpty())
    {
      return true;
    }

    UnsetResolution resolution = policies.resolveUnset(new UnsetContext(unsetting.side, concurrentMutations));
    if (resolution == null || resolution == UnsetResolution.FAIL)
    {
      conflict = "UNSET conflicts with effective concurrent mutations " + lineageIDs(concurrentMutations);
      return false;
    }

    unsetting.unsetResolution = resolution;
    return true;
  }

  /**
   * Merges one shared ancestor lineage and returns its surviving semantic occurrence, if any.
   */
  private MergedOccurrence mergeAncestorLineage(Lineage lineage, SideLineage sourceState, SideLineage targetState)
  {
    if (!sourceState.observed && !targetState.observed)
    {
      // Neither causal history had an opportunity to address this result-base occurrence, so the application baseline
      // remains authoritative without invoking a conflict policy.
      return new MergedOccurrence(lineage.ancestorOccurrence, lineage.ancestorPosition, lineage, Origin.ANCESTOR, "unobserved by both sides");
    }

    if (!sourceState.observed)
    {
      return mergeObservedLineage(lineage, targetState, Origin.TARGET);
    }

    if (!targetState.observed)
    {
      return mergeObservedLineage(lineage, sourceState, Origin.SOURCE);
    }

    if (!sourceState.present && !targetState.present)
    {
      return null;
    }

    if (!sourceState.present || !targetState.present)
    {
      SideLineage removedState = sourceState.present ? targetState : sourceState;
      SideLineage presentState = sourceState.present ? sourceState : targetState;
      Origin presentSide = sourceState.present ? Origin.SOURCE : Origin.TARGET;

      if (!presentState.contentChanged && !presentState.placementChanged)
      {
        return null;
      }

      if (removedState.removalCause == RemovalCause.CLEAR)
      {
        ClearResolution resolution = policies
            .resolveClear(new ClearContext(removedState.side, lineage, presentState.contentChanged, presentState.placementChanged));
        if (resolution == ClearResolution.FAIL)
        {
          conflict = "CLEAR conflicts with mutation of " + lineage.id;
          return MergedOccurrence.CONFLICT;
        }

        if (resolution == ClearResolution.OBSERVED_REMOVE || resolution == ClearResolution.CLEAR_WINS)
        {
          return null;
        }
      }

      if (removedState.removalCause == RemovalCause.UNSET)
      {
        UnsetResolution resolution = removedState.owner.unsetResolution;
        if (resolution == null)
        {
          resolution = UnsetResolution.FAIL;
        }

        if (resolution == UnsetResolution.FAIL)
        {
          conflict = "UNSET conflicts with mutation of " + lineage.id;
          return MergedOccurrence.CONFLICT;
        }

        if (resolution == UnsetResolution.UNSET_WINS)
        {
          return null;
        }
      }

      OccurrenceConflictContext context = new OccurrenceConflictContext(OccurrenceConflictKind.REMOVE_VS_MUTATION, lineage, sourceState, targetState);
      OccurrenceResolution resolution = policies.resolveOccurrence(context);
      if (!validateOccurrenceResolution(context, resolution))
      {
        return MergedOccurrence.CONFLICT;
      }

      if (resolution == OccurrenceResolution.REMOVE)
      {
        return null;
      }

      return new MergedOccurrence(presentState.representative, presentState.position, lineage, presentSide, "kept mutation against removal by policy");
    }

    Occurrence representative = mergeContent(lineage, sourceState, targetState);
    if (representative == null)
    {
      return MergedOccurrence.CONFLICT;
    }

    Position position = mergePlacement(lineage, sourceState, targetState);
    if (position == null)
    {
      return MergedOccurrence.CONFLICT;
    }

    Origin origin = representative.origin == Origin.ANCESTOR ? position.origin : representative.origin;
    return new MergedOccurrence(representative, position, lineage, origin, "merged ancestor lineage");
  }

  /**
   * Applies the sole observed side's effective state while the other side has no semantic opinion about the lineage.
   */
  private MergedOccurrence mergeObservedLineage(Lineage lineage, SideLineage state, Origin side)
  {
    if (!state.observed)
    {
      throw invariant("Expected observed lineage state for " + lineage.id + " on " + side);
    }

    if (!state.present)
    {
      return null;
    }

    return new MergedOccurrence(state.representative, state.position, lineage, side, "other side did not observe result-base lineage");
  }

  /**
   * Merges the representative-value dimension of one present lineage.
   */
  private Occurrence mergeContent(Lineage lineage, SideLineage sourceState, SideLineage targetState)
  {
    if (!sourceState.contentChanged && !targetState.contentChanged)
    {
      return lineage.ancestorOccurrence;
    }

    if (sourceState.contentChanged && !targetState.contentChanged)
    {
      return sourceState.representative;
    }

    if (!sourceState.contentChanged)
    {
      return targetState.representative;
    }

    if (valuesEqual(sourceState.representative.value, targetState.representative.value))
    {
      // Equal replacement values are semantically compatible despite distinct side-local occurrence identities.
      redirects.put(sourceState.representative, new Redirect(targetState.representative, RedirectCause.EQUIVALENT_REPLACEMENT));
      return targetState.representative;
    }

    OccurrenceConflictContext context = new OccurrenceConflictContext(OccurrenceConflictKind.CONCURRENT_REPLACEMENT, lineage, sourceState, targetState);
    OccurrenceResolution resolution = policies.resolveOccurrence(context);
    if (!validateOccurrenceResolution(context, resolution))
    {
      return null;
    }

    if (resolution == OccurrenceResolution.SOURCE)
    {
      return sourceState.representative;
    }

    if (resolution == OccurrenceResolution.TARGET)
    {
      return targetState.representative;
    }

    conflict = "Concurrent replacement of " + lineage.id + " was not resolved to source or target";
    return null;
  }

  /**
   * Merges the placement dimension and synthesizes a position when two changed intents are jointly satisfiable.
   */
  private Position mergePlacement(Lineage lineage, SideLineage sourceState, SideLineage targetState)
  {
    if (!sourceState.placementChanged && !targetState.placementChanged)
    {
      return lineage.ancestorPosition;
    }

    if (sourceState.placementChanged && !targetState.placementChanged)
    {
      return sourceState.position;
    }

    if (!sourceState.placementChanged)
    {
      return targetState.position;
    }

    if (positionsEquivalent(sourceState.position, targetState.position))
    {
      return targetState.position;
    }

    Position synthetic = new Position("PM" + lineage.ancestorIndex, Origin.MERGED, lineage.ancestorIndex, nextPositionOrdinal++, false);
    PositionOrder candidate = mergedOrder.copy("MERGED-CANDIDATE");
    boolean compatible = copyPlacementBounds(source.order, sourceState.position, candidate, synthetic, Origin.SOURCE)
        && copyPlacementBounds(target.order, targetState.position, candidate, synthetic, Origin.TARGET);

    if (compatible && !candidate.hasCycle())
    {
      mergedOrder = candidate;
      return synthetic;
    }

    OccurrenceConflictContext context = new OccurrenceConflictContext(OccurrenceConflictKind.CONCURRENT_PLACEMENT, lineage, sourceState, targetState);
    OccurrenceResolution resolution = policies.resolveOccurrence(context);
    if (!validateOccurrenceResolution(context, resolution))
    {
      return null;
    }

    if (resolution == OccurrenceResolution.SOURCE)
    {
      return sourceState.position;
    }

    if (resolution == OccurrenceResolution.TARGET)
    {
      return targetState.position;
    }

    conflict = "Concurrent placement of " + lineage.id + " was not resolved to source or target";
    return null;
  }

  /**
   * Determines whether two positions impose the same relations to all positions known by the merged histories.
   */
  private boolean positionsEquivalent(Position first, Position second)
  {
    if (first == second)
    {
      return true;
    }

    for (Position position : mergedOrder.nodes())
    {
      if (position != first && position != second)
      {
        boolean firstBefore = mergedOrder.isBefore(first, position);
        boolean secondBefore = mergedOrder.isBefore(second, position);
        boolean firstAfter = mergedOrder.isBefore(position, first);
        boolean secondAfter = mergedOrder.isBefore(position, second);
        if (firstBefore != secondBefore || firstAfter != secondAfter)
        {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Copies only the direct historical bounds of one placement intent onto a synthetic merged position.
   */
  private boolean copyPlacementBounds(PositionOrder from, Position position, PositionOrder to, Position synthetic, Origin side)
  {
    for (Position predecessor : from.predecessors(position))
    {
      if (!to.tryAddConstraint(predecessor, synthetic, new Provenance(side, position.sequence, "merged placement lower bound")))
      {
        return false;
      }
    }

    for (Position successor : from.successors(position))
    {
      if (!to.tryAddConstraint(synthetic, successor, new Provenance(side, position.sequence, "merged placement upper bound")))
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Validates that an occurrence policy selected only an alternative offered by the immutable conflict context.
   */
  private boolean validateOccurrenceResolution(OccurrenceConflictContext context, OccurrenceResolution resolution)
  {
    if (resolution == null || resolution == OccurrenceResolution.FAIL)
    {
      conflict = context.kind + " for " + context.lineage.id + " was not resolved";
      return false;
    }

    if (context.kind == OccurrenceConflictKind.CONCURRENT_REPLACEMENT || context.kind == OccurrenceConflictKind.CONCURRENT_PLACEMENT)
    {
      if (resolution != OccurrenceResolution.SOURCE && resolution != OccurrenceResolution.TARGET)
      {
        throw invariant("Occurrence policy returned invalid resolution " + resolution + " for " + context.kind);
      }
    }
    else if (resolution != OccurrenceResolution.REMOVE && resolution != OccurrenceResolution.KEEP_MUTATION)
    {
      throw invariant("Occurrence policy returned invalid resolution " + resolution + " for " + context.kind);
    }

    return true;
  }

  /**
   * Adds effective side-local occurrences that survived their own causal history.
   */
  private void addSurvivingAdditions(SideState state)
  {
    for (Lineage lineage : state.addedLineages)
    {
      SideLineage sideLineage = state.lineages.get(lineage);
      if (sideLineage != null && sideLineage.present)
      {
        mergedOccurrences.add(new MergedOccurrence(sideLineage.representative, sideLineage.position, lineage, state.side, "surviving side-local addition"));
      }
    }
  }

  /**
   * Applies policy modes that deliberately dominate concurrent unobserved contents after ordinary occurrence merging.
   */
  private boolean applyDominatingClearOrUnset()
  {
    for (SideState state : new SideState[] { source, target })
    {
      if (state.unsetResolution == UnsetResolution.UNSET_WINS)
      {
        mergedOccurrences.clear();
        return true;
      }

      if (state.clearOperation != null && policies.getClearMode() == ClearResolution.CLEAR_WINS)
      {
        mergedOccurrences.clear();
        return true;
      }
    }

    return true;
  }

  /**
   * Enforces the feature's hard uniqueness invariant by resolving every equivalence class of surviving values.
   */
  private boolean resolveUniqueness()
  {
    if (!feature.isUnique())
    {
      return true;
    }

    List<MergedOccurrence> survivors = new ArrayList<>();

    for (MergedOccurrence candidate : mergedOccurrences)
    {
      MergedOccurrence duplicate = findEqualValue(survivors, candidate.occurrence.value);
      if (duplicate == null)
      {
        survivors.add(candidate);
        continue;
      }

      DuplicateContext context = new DuplicateContext(duplicate, candidate);
      DuplicateResolution resolution = policies.resolveDuplicate(context);
      if (resolution == null || resolution == DuplicateResolution.FAIL)
      {
        conflict = "Unique feature collision between " + duplicate.occurrence.id + " and " + candidate.occurrence.id;
        return false;
      }

      MergedOccurrence winner = chooseDuplicateWinner(duplicate, candidate, resolution);
      MergedOccurrence loser = winner == duplicate ? candidate : duplicate;
      Position position = mergeDuplicatePlacement(duplicate, candidate, winner);
      if (position == null)
      {
        return false;
      }

      winner = new MergedOccurrence(winner.occurrence, position, winner.lineage, winner.origin, "uniqueness coalescing");
      redirects.put(loser.occurrence, new Redirect(winner.occurrence, RedirectCause.DUPLICATE_COALESCING));

      survivors.remove(duplicate);
      survivors.add(winner);
    }

    mergedOccurrences.clear();
    mergedOccurrences.addAll(survivors);
    validateRedirects();

    // A policy may select a representative, but it may never leave a hard uniqueness violation behind.
    for (int i = 0; i < mergedOccurrences.size(); i++)
    {
      for (int j = i + 1; j < mergedOccurrences.size(); j++)
      {
        if (valuesEqual(mergedOccurrences.get(i).occurrence.value, mergedOccurrences.get(j).occurrence.value))
        {
          throw invariant("Duplicate policy left an unresolved uniqueness collision");
        }
      }
    }

    return true;
  }

  /**
   * Finds the first deterministic representative with a value equivalent under the feature's actual value semantics.
   */
  private MergedOccurrence findEqualValue(List<MergedOccurrence> occurrences, Object value)
  {
    for (MergedOccurrence occurrence : occurrences)
    {
      if (valuesEqual(occurrence.occurrence.value, value))
      {
        return occurrence;
      }
    }

    return null;
  }

  /**
   * Selects the representative requested by duplicate policy, preferring an ancestor lineage for COALESCE so the
   * encoder can preserve identity with MOVE/SET instead of unnecessary remove/add operations.
   */
  private MergedOccurrence chooseDuplicateWinner(MergedOccurrence first, MergedOccurrence second, DuplicateResolution resolution)
  {
    if (resolution == DuplicateResolution.FIRST)
    {
      return first;
    }

    if (resolution == DuplicateResolution.SECOND)
    {
      return second;
    }

    boolean firstAncestor = first.lineage.origin == Origin.ANCESTOR;
    boolean secondAncestor = second.lineage.origin == Origin.ANCESTOR;
    if (firstAncestor != secondAncestor)
    {
      return firstAncestor ? first : second;
    }

    // Target wins the final representative tie while both placement intents are merged independently below.
    if (first.origin != second.origin)
    {
      return first.origin == Origin.TARGET ? first : second;
    }

    return first.occurrence.ordinal <= second.occurrence.ordinal ? first : second;
  }

  /**
   * Combines the final placement intents of coalesced equal-valued occurrences instead of discarding the loser's MOVE.
   */
  private Position mergeDuplicatePlacement(MergedOccurrence first, MergedOccurrence second, MergedOccurrence winner)
  {
    if (positionsEquivalent(first.position, second.position))
    {
      return winner.position;
    }

    Position synthetic = new Position("PD" + winner.occurrence.ordinal, Origin.MERGED, winner.occurrence.ordinal, nextPositionOrdinal++, false);
    PositionOrder candidate = mergedOrder.copy("MERGED-DUPLICATE");
    PositionOrder firstOrder = first.origin == Origin.SOURCE ? source.order : target.order;
    PositionOrder secondOrder = second.origin == Origin.SOURCE ? source.order : target.order;
    boolean compatible = copyPlacementBounds(firstOrder, first.position, candidate, synthetic, first.origin)
        && copyPlacementBounds(secondOrder, second.position, candidate, synthetic, second.origin);

    if (compatible && !candidate.hasCycle())
    {
      mergedOrder = candidate;
      return synthetic;
    }

    DuplicateResolution resolution = policies.resolveDuplicate(new DuplicateContext(first, second));
    if (resolution == DuplicateResolution.FIRST)
    {
      return first.position;
    }

    if (resolution == DuplicateResolution.SECOND)
    {
      return second.position;
    }

    conflict = "Coalesced duplicate placement intents are incompatible for " + winner.occurrence.id;
    return null;
  }

  /**
   * Resolves redirects transitively, rejects foreign targets, and detects cycles as internal invariant violations.
   */
  private void validateRedirects()
  {
    Set<Occurrence> known = Collections.newSetFromMap(new IdentityHashMap<Occurrence, Boolean>());
    known.addAll(ancestorOccurrences);
    known.addAll(source.allOccurrences);
    known.addAll(target.allOccurrences);

    for (Occurrence startOccurrence : redirects.keySet())
    {
      Set<Occurrence> path = Collections.newSetFromMap(new IdentityHashMap<Occurrence, Boolean>());
      Occurrence occurrence = startOccurrence;

      while (redirects.containsKey(occurrence))
      {
        if (!path.add(occurrence))
        {
          throw invariant("Redirect cycle starting at " + startOccurrence.id);
        }

        occurrence = redirects.get(occurrence).target;
        if (!known.contains(occurrence))
        {
          throw invariant("Redirect points to foreign occurrence " + occurrence.id);
        }
      }
    }
  }

  /**
   * Produces a deterministic topological order while consulting OrderingPolicy only for genuinely incomparable active
   * positions that are simultaneously eligible under all hard constraints.
   */
  private List<MergedOccurrence> linearize()
  {
    Map<Position, MergedOccurrence> active = new IdentityHashMap<>();

    for (MergedOccurrence occurrence : mergedOccurrences)
    {
      if (occurrence.position.sentinel)
      {
        throw invariant("Surviving occurrence has sentinel position " + occurrence.position.id);
      }

      MergedOccurrence old = active.put(occurrence.position, occurrence);
      if (old != null)
      {
        throw invariant("Distinct surviving occurrences share position " + occurrence.position.id);
      }
    }

    PositionOrder.Topology topology = mergedOrder.topology();
    List<MergedOccurrence> result = new ArrayList<>(mergedOccurrences.size());

    while (topology.hasNodes())
    {
      // Consume every eligible historical-only landmark before deciding among visible output candidates. This exposes
      // all hard predecessors without letting an arbitrary invisible-node order decide the output order.
      Position historical;

      while ((historical = firstHistorical(topology.ready(), active)) != null)
      {
        topology.remove(historical);
      }

      // Removing the final historical END landmark legitimately exhausts the graph. Re-check before interpreting an
      // empty ready set as a cycle; otherwise every acyclic list ending in an inactive landmark is rejected.
      if (!topology.hasNodes())
      {
        break;
      }

      List<MergedOccurrence> eligible = new ArrayList<>();

      for (Position position : topology.ready())
      {
        MergedOccurrence occurrence = active.get(position);
        if (occurrence != null)
        {
          eligible.add(occurrence);
        }
      }

      if (eligible.isEmpty())
      {
        if (topology.ready().isEmpty())
        {
          throw invariant("Merged position constraints contain a cycle");
        }

        continue;
      }

      MergedOccurrence chosen;
      if (eligible.size() == 1)
      {
        chosen = eligible.get(0);
      }
      else
      {
        // Every candidate is legal under the DAG; policy chooses only among the genuinely underdetermined next nodes.
        chosen = policies.chooseOrdering(new OrderingContext(result, eligible));
        if (chosen == null)
        {
          conflict = "Ordering policy rejected ambiguity among " + occurrenceIDs(eligible);
          return null;
        }

        if (!eligible.contains(chosen))
        {
          throw invariant("Ordering policy selected a non-eligible occurrence");
        }
      }

      result.add(chosen);
      topology.remove(chosen.position);
    }

    if (result.size() != mergedOccurrences.size())
    {
      throw invariant("Linearization materialized " + result.size() + " of " + mergedOccurrences.size() + " occurrences");
    }

    return result;
  }

  /**
   * Returns the deterministically first ready historical node, or {@code null} when all ready nodes are active.
   */
  private Position firstHistorical(Collection<Position> ready, Map<Position, MergedOccurrence> active)
  {
    Position first = null;

    for (Position position : ready)
    {
      if (!active.containsKey(position) && (first == null || position.ordinal < first.ordinal))
      {
        first = position;
      }
    }

    return first;
  }

  /**
   * Resolves the orthogonal final set-state after content policies have selected the surviving occurrences.
   */
  private SetState resolveFinalSetState(List<MergedOccurrence> ordered)
  {
    if (!feature.isUnsettable())
    {
      return SetState.SET;
    }

    boolean unsetWins = source.unsetResolution == UnsetResolution.UNSET_WINS || target.unsetResolution == UnsetResolution.UNSET_WINS;
    if (unsetWins)
    {
      if (!ordered.isEmpty())
      {
        throw invariant("UNSET_WINS produced non-empty contents");
      }

      return SetState.UNSET;
    }

    if (source.hasEffectiveUnset() && target.hasEffectiveUnset())
    {
      return SetState.UNSET;
    }

    if (source.hasEffectiveUnset() || target.hasEffectiveUnset())
    {
      SideState unsetting = source.hasEffectiveUnset() ? source : target;
      if (unsetting.unsetResolution == UnsetResolution.MERGE_AS_CLEAR)
      {
        return SetState.SET;
      }

      if (ordered.isEmpty())
      {
        return SetState.UNSET;
      }
    }

    if (!ordered.isEmpty())
    {
      return SetState.SET;
    }

    if (source.setState == target.setState)
    {
      return source.setState;
    }

    // SET [] is an effective state choice; unchanged UNSET is not an active UNSET intent.
    return source.setState == SetState.SET || target.setState == SetState.SET ? SetState.SET : SetState.UNSET;
  }

  /**
   * Encodes the semantic result independently of source/target conflict resolution and partial-order policies.
   */
  private CDOListFeatureDelta encode(SemanticResult result)
  {
    CDOListFeatureDeltaImpl delta = new CDOListFeatureDeltaImpl(feature, ancestorLineages.size());
    List<CDOFeatureDelta> changes = delta.getListChanges();
    encodedAddOccurrences.clear();

    if (result.setState == SetState.UNSET)
    {
      // UNSET is a complete executable state transition and guarantees empty contents.
      changes.add(new CDOUnsetFeatureDeltaImpl(feature));
      return delta;
    }

    List<EncodedEntry> working = new ArrayList<>();
    Map<Lineage, MergedOccurrence> survivingAncestors = new IdentityHashMap<>();

    for (MergedOccurrence occurrence : result.occurrences)
    {
      if (occurrence.lineage.origin == Origin.ANCESTOR)
      {
        survivingAncestors.put(occurrence.lineage, occurrence);
      }
    }

    for (int i = 0; i < ancestorLineages.size(); i++)
    {
      Lineage lineage = ancestorLineages.get(i);
      working.add(new EncodedEntry(lineage, lineage.ancestorOccurrence));
    }

    // Remove doomed ancestor occurrences first. This ordering prevents temporary value duplicates in unique features.
    for (int i = working.size() - 1; i >= 0; i--)
    {
      EncodedEntry entry = working.get(i);

      if (!survivingAncestors.containsKey(entry.lineage))
      {
        CDORemoveFeatureDeltaImpl remove = new CDORemoveFeatureDeltaImpl(feature, i);
        remove.setValue(entry.occurrence.value);
        changes.add(remove);
        working.remove(i);
      }
    }

    List<MergedOccurrence> desiredAncestors = new ArrayList<>();

    for (MergedOccurrence occurrence : result.occurrences)
    {
      if (occurrence.lineage.origin == Origin.ANCESTOR)
      {
        desiredAncestors.add(occurrence);
      }
    }

    // Reorder surviving ancestor identities before introducing side-local additions.
    for (int desiredIndex = 0; desiredIndex < desiredAncestors.size(); desiredIndex++)
    {
      MergedOccurrence desired = desiredAncestors.get(desiredIndex);
      int oldIndex = indexOfLineage(working, desired.lineage);

      if (oldIndex != desiredIndex)
      {
        CDOMoveFeatureDeltaImpl move = new CDOMoveFeatureDeltaImpl(feature, desiredIndex, oldIndex);
        move.setValue(working.get(oldIndex).occurrence.value);
        changes.add(move);
        working.add(desiredIndex, working.remove(oldIndex));
      }
    }

    // Install replacements in an order that never creates a temporary duplicate. A value-dependency cycle (for
    // example A->X and X->A) has no legal first SET on a unique feature, so one deterministic lineage is temporarily
    // removed and re-added after every remaining replacement has vacated its old value.
    List<MergedOccurrence> pendingReplacements = new ArrayList<>();

    for (MergedOccurrence desired : desiredAncestors)
    {
      int index = indexOfLineage(working, desired.lineage);

      if (!valuesEqual(working.get(index).occurrence.value, desired.occurrence.value))
      {
        pendingReplacements.add(desired);
      }
    }

    while (!pendingReplacements.isEmpty())
    {
      MergedOccurrence executable = findExecutableReplacement(working, pendingReplacements);
      if (executable != null)
      {
        int index = indexOfLineage(working, executable.lineage);
        EncodedEntry entry = working.get(index);
        changes.add(new CDOSetFeatureDeltaImpl(feature, index, executable.occurrence.value, entry.occurrence.value));
        working.set(index, new EncodedEntry(entry.lineage, executable.occurrence));
        pendingReplacements.remove(executable);
        continue;
      }

      // No SET is executable, hence the remaining value dependencies contain a cycle. Removing the first stable task
      // breaks that cycle without violating uniqueness; final-order insertion below restores the semantic lineage.
      MergedOccurrence deferred = pendingReplacements.remove(0);
      int index = indexOfLineage(working, deferred.lineage);
      EncodedEntry entry = working.remove(index);
      CDORemoveFeatureDeltaImpl remove = new CDORemoveFeatureDeltaImpl(feature, index);
      remove.setValue(entry.occurrence.value);
      changes.add(remove);
    }

    // Insert new and cycle-deferred lineages at their final numeric positions after all unique-value blockers left.
    for (int i = 0; i < result.occurrences.size(); i++)
    {
      MergedOccurrence desired = result.occurrences.get(i);

      if (indexOfLineageOrMinusOne(working, desired.lineage) == -1)
      {
        changes.add(new CDOAddFeatureDeltaImpl(feature, i, desired.occurrence.value));
        working.add(i, new EncodedEntry(desired.lineage, desired.occurrence));
        encodedAddOccurrences.add(desired);
      }
    }

    SetState ancestorSetState = initialSetState();
    if (changes.isEmpty() && ancestorSetState == SetState.UNSET && result.setState == SetState.SET)
    {
      // On an unsettable many-valued feature CLEAR creates the distinct set-but-empty state.
      changes.add(new CDOClearFeatureDeltaImpl(feature));
    }

    validateEncodedWorkingState(working, result);
    return delta;
  }

  /**
   * Finds the current numeric index of an ancestor lineage in the encoder's identity-aware working list.
   */
  private int indexOfLineage(List<EncodedEntry> working, Lineage lineage)
  {
    int index = indexOfLineageOrMinusOne(working, lineage);
    if (index != -1)
    {
      return index;
    }

    throw invariant("Encoder lost surviving lineage " + lineage.id);
  }

  /**
   * Returns the current numeric index of a lineage, or {@code -1} while a uniqueness-cycle task is deferred.
   */
  private int indexOfLineageOrMinusOne(List<EncodedEntry> working, Lineage lineage)
  {
    for (int i = 0; i < working.size(); i++)
    {
      if (working.get(i).lineage == lineage)
      {
        return i;
      }
    }

    return -1;
  }

  /**
   * Finds the first stable replacement whose desired value is not currently occupied by another unique-list entry.
   */
  private MergedOccurrence findExecutableReplacement(List<EncodedEntry> working, List<MergedOccurrence> pending)
  {
    if (!feature.isUnique())
    {
      return pending.get(0);
    }

    for (MergedOccurrence desired : pending)
    {
      int ownIndex = indexOfLineage(working, desired.lineage);
      boolean blocked = false;

      for (int i = 0; i < working.size(); i++)
      {
        if (i != ownIndex && valuesEqual(working.get(i).occurrence.value, desired.occurrence.value))
        {
          blocked = true;
          break;
        }
      }

      if (!blocked)
      {
        return desired;
      }
    }

    return null;
  }

  /**
   * Validates the encoder's directly maintained state before performing the independent replay pass.
   */
  private void validateEncodedWorkingState(List<EncodedEntry> working, SemanticResult result)
  {
    if (working.size() != result.occurrences.size())
    {
      throw invariant("Encoder working size differs from semantic result");
    }

    for (int i = 0; i < working.size(); i++)
    {
      EncodedEntry actual = working.get(i);
      MergedOccurrence expected = result.occurrences.get(i);

      if (actual.lineage != expected.lineage || !valuesEqual(actual.occurrence.value, expected.occurrence.value))
      {
        throw invariant("Encoder working state differs at index " + i);
      }
    }
  }

  /**
   * Replays the encoded deltas from the ancestor and checks values, lineage identities, uniqueness, and set-state.
   */
  private void validateReplay(CDOListFeatureDelta delta, SemanticResult expected)
  {
    List<ReplayEntry> replay = new ArrayList<>();

    for (Lineage lineage : ancestorLineages)
    {
      replay.add(new ReplayEntry(lineage, lineage.ancestorOccurrence.value));
    }

    SetState replaySetState = initialSetState();
    int addCursor = 0;

    for (CDOFeatureDelta change : delta.getListChanges())
    {
      switch (change.getType())
      {
      case ADD:
      {
        CDOAddFeatureDelta add = (CDOAddFeatureDelta)change;
        MergedOccurrence added = nextEncodedAddOccurrence(addCursor++);
        replay.add(add.getIndex(), new ReplayEntry(added.lineage, add.getValue()));
        replaySetState = SetState.SET;
        break;
      }

      case REMOVE:
        replay.remove(((CDORemoveFeatureDelta)change).getIndex());
        break;

      case SET:
      {
        CDOSetFeatureDelta set = (CDOSetFeatureDelta)change;
        ReplayEntry old = replay.get(set.getIndex());
        replay.set(set.getIndex(), new ReplayEntry(old.lineage, set.getValue()));
        break;
      }

      case MOVE:
      {
        CDOMoveFeatureDelta move = (CDOMoveFeatureDelta)change;
        replay.add(move.getNewPosition(), replay.remove(move.getOldPosition()));
        break;
      }

      case CLEAR:
        replay.clear();
        replaySetState = SetState.SET;
        break;

      case UNSET:
        replay.clear();
        replaySetState = SetState.UNSET;
        break;

      default:
        throw invariant("Encoder emitted unsupported delta " + change.getType());
      }

      validateIntermediateUniqueness(replay);
    }

    if (replaySetState != expected.setState)
    {
      throw invariant("Replay set-state " + replaySetState + " differs from semantic result " + expected.setState);
    }

    if (replay.size() != expected.occurrences.size())
    {
      throw invariant("Replay size differs from semantic result");
    }

    for (int i = 0; i < replay.size(); i++)
    {
      ReplayEntry actual = replay.get(i);
      MergedOccurrence wanted = expected.occurrences.get(i);

      if (actual.lineage != wanted.lineage || !valuesEqual(actual.value, wanted.occurrence.value))
      {
        throw invariant("Replay differs from semantic result at index " + i);
      }
    }
  }

  /**
   * Returns the semantic occurrence planned for the next executable ADD, including a deferred ancestor lineage when a
   * unique-value replacement cycle required a temporary removal.
   */
  private MergedOccurrence nextEncodedAddOccurrence(int ordinal)
  {
    if (ordinal < encodedAddOccurrences.size())
    {
      return encodedAddOccurrences.get(ordinal);
    }

    throw invariant("Replay encountered more ADD deltas than the encoder identity plan");
  }

  /**
   * Ensures every intermediate replay state obeys feature uniqueness, not merely the final state.
   */
  private void validateIntermediateUniqueness(List<ReplayEntry> replay)
  {
    if (!feature.isUnique())
    {
      return;
    }

    for (int i = 0; i < replay.size(); i++)
    {
      for (int j = i + 1; j < replay.size(); j++)
      {
        if (valuesEqual(replay.get(i).value, replay.get(j).value))
        {
          throw invariant("Encoder created a temporary uniqueness violation at indexes " + i + " and " + j);
        }
      }
    }
  }

  /**
   * Compares feature values using CDO's reference identity/value and attribute value conventions.
   */
  private boolean valuesEqual(Object first, Object second)
  {
    if (first == second)
    {
      return true;
    }

    if (first == null || second == null || first == CDOFeatureDelta.UNKNOWN_VALUE || second == CDOFeatureDelta.UNKNOWN_VALUE)
    {
      return false;
    }

    if (first == CDORevisionData.NIL || second == CDORevisionData.NIL)
    {
      return first == second;
    }

    if (feature instanceof EReference)
    {
      CDOID firstID = CDOIDUtil.getCDOID(first);
      CDOID secondID = CDOIDUtil.getCDOID(second);

      if (firstID != null || secondID != null)
      {
        return firstID != null && firstID.equals(secondID);
      }

      // Uncommitted reference values have no stable CDOID yet; EMF reference-list equality is identity based.
      return false;
    }

    return first.equals(second);
  }

  /**
   * Appends the feature contract section of the diagnostic model dump.
   */
  private void appendFeatureDump(StringBuilder builder)
  {
    builder.append("=== FEATURE ===\n");
    builder.append(feature.getEContainingClass().getName()).append('.').append(feature.getName());
    builder.append(" unique=").append(feature.isUnique());
    builder.append(" unsettable=").append(feature.isUnsettable());
    builder.append(" resultBaseSetState=").append(initialSetState()).append('\n');
  }

  /**
   * Appends stable ancestor occurrence and position identities to the diagnostic model dump.
   */
  private void appendAncestorDump(StringBuilder builder)
  {
    builder.append("\n=== ANCESTOR ===\n");

    for (int i = 0; i < ancestorLineages.size(); i++)
    {
      Lineage lineage = ancestorLineages.get(i);
      builder.append(lineage.id).append(" / ").append(lineage.ancestorOccurrence.id);
      builder.append(" value=").append(formatValue(lineage.ancestorValue));
      builder.append(" position=").append(lineage.ancestorPosition.id).append('\n');
    }
  }

  /**
   * Appends one side's decoded history and normalized net effects to the diagnostic model dump.
   */
  private void appendSideDump(StringBuilder builder, SideState state)
  {
    if (state == null)
    {
      return;
    }

    builder.append("\n=== ").append(state.side).append(" HISTORY ===\n");

    for (Operation operation : state.operations)
    {
      builder.append('#').append(operation.sequence).append(' ').append(operation.delta.getType());
      builder.append(" -> ").append(operation.effect).append('\n');
    }

    builder.append("\n=== ").append(state.side).append(" NET EFFECT ===\n");

    for (SideLineage lineage : state.lineages.values())
    {
      builder.append(lineage.lineage.id).append(" observed=").append(lineage.observed);
      builder.append(" present=").append(lineage.present);

      if (!lineage.observed)
      {
        builder.append(" <unobserved>");
      }
      else if (lineage.present)
      {
        builder.append(" representative=").append(lineage.representative.id);
        builder.append(" value=").append(formatValue(lineage.representative.value));
        builder.append(" contentChanged=").append(lineage.contentChanged);
        builder.append(" position=").append(lineage.position.id);
        builder.append(" placementChanged=").append(lineage.placementChanged);
      }
      else
      {
        builder.append(" removedBy=").append(lineage.removalCause);
      }

      builder.append('\n');
    }

    builder.append("baseSetState=").append(state.baseSetState);
    builder.append(" setState=").append(state.setState).append('\n');
  }

  /**
   * Appends merged-lineage, clear/unset, uniqueness, and redirect decisions to the diagnostic dump.
   */
  private void appendMergeDump(StringBuilder builder)
  {
    builder.append("\n=== LINEAGE MERGE ===\n");

    for (MergedOccurrence occurrence : mergedOccurrences)
    {
      builder.append(occurrence.lineage.id).append(" -> ").append(occurrence.occurrence.id);
      builder.append(" @ ").append(occurrence.position.id).append(" (").append(occurrence.decision).append(")\n");
    }

    builder.append("\n=== CLEAR / UNSET ===\n");
    builder.append("source=").append(source == null ? "not decoded" : source.setState);
    builder.append(" target=").append(target == null ? "not decoded" : target.setState).append('\n');

    builder.append("\n=== UNIQUENESS ===\n");

    if (redirects.isEmpty())
    {
      builder.append("no redirects\n");
    }
    else
    {
      List<Occurrence> keys = new ArrayList<>(redirects.keySet());
      keys.sort(Comparator.comparingInt(occurrence -> occurrence.ordinal));

      for (Occurrence occurrence : keys)
      {
        Redirect redirect = redirects.get(occurrence);
        builder.append(occurrence.id).append(" -> ").append(redirect.target.id);
        builder.append(" cause=").append(redirect.cause).append('\n');
      }
    }

    if (conflict != null)
    {
      builder.append("conflict=").append(conflict).append('\n');
    }
  }

  /**
   * Appends hard partial-order constraints and their provenance to the diagnostic dump.
   */
  private void appendOrderingDump(StringBuilder builder)
  {
    builder.append("\n=== ORDERING ===\n");

    if (mergedOrder == null)
    {
      builder.append("not merged\n");
      return;
    }

    for (Constraint constraint : mergedOrder.constraints())
    {
      builder.append(constraint.before.id).append(" < ").append(constraint.after.id);
      builder.append(" [").append(constraint.provenance).append("]\n");
    }
  }

  /**
   * Appends semantic result, encoded deltas, and replay status to the diagnostic dump.
   */
  private void appendResultDump(StringBuilder builder)
  {
    builder.append("\n=== RESULT ===\n");

    if (semanticResult == null)
    {
      builder.append("not materialized\n");
      return;
    }

    builder.append("setState=").append(semanticResult.setState).append(" values=[");

    for (int i = 0; i < semanticResult.occurrences.size(); i++)
    {
      if (i != 0)
      {
        builder.append(", ");
      }

      builder.append(formatValue(semanticResult.occurrences.get(i).occurrence.value));
    }

    builder.append("]\n\n=== ENCODING ===\n");

    if (encodedDelta != null)
    {
      for (CDOFeatureDelta delta : encodedDelta.getListChanges())
      {
        builder.append(delta).append('\n');
      }
    }

    builder.append("\n=== REPLAY ===\nvalidated\n");
  }

  /**
   * Creates an invariant exception enriched with the semantic model dump available at the failure point.
   */
  private IllegalStateException invariant(String message)
  {
    return new IllegalStateException(message + "\n" + dump());
  }

  /**
   * Clamps ADD and MOVE-destination coordinates exactly as the CDO delta implementations do during application.
   */
  private static int clamp(int value, int minimum, int maximum)
  {
    return Math.max(minimum, Math.min(value, maximum));
  }

  /**
   * Formats values without JVM identity hashes so diagnostics are deterministic.
   */
  private static String formatValue(Object value)
  {
    if (value == CDOFeatureDelta.UNKNOWN_VALUE)
    {
      return "<unknown>";
    }

    if (value instanceof CDOID)
    {
      return "CDOID(" + value + ")";
    }

    return String.valueOf(value);
  }

  /**
   * Returns deterministic lineage identifiers for focused conflict diagnostics.
   */
  private static List<String> lineageIDs(Collection<Lineage> lineages)
  {
    List<String> result = new ArrayList<>();

    for (Lineage lineage : lineages)
    {
      result.add(lineage.id);
    }

    return result;
  }

  /**
   * Returns deterministic occurrence identifiers for an ordering-policy ambiguity diagnostic.
   */
  private static List<String> occurrenceIDs(Collection<MergedOccurrence> occurrences)
  {
    List<String> result = new ArrayList<>();

    for (MergedOccurrence occurrence : occurrences)
    {
      result.add(occurrence.occurrence.id);
    }

    return result;
  }

  /**
   * Identifies the provenance domain of an occurrence or position.
   *
   * @author Eike Stepper
   */
  public enum Origin
  {
    /**
     * Shared common-ancestor identity.
     */
    ANCESTOR,

    /**
     * Source/remote side-local identity.
     */
    SOURCE,

    /**
     * Target/local side-local identity.
     */
    TARGET,

    /**
     * Synthetic identity combining compatible intents from both sides.
     */
    MERGED
  }

  /**
   * Represents the orthogonal set-state of an unsettable many-valued feature.
   *
   * @author Eike Stepper
   */
  public enum SetState
  {
    /**
     * The feature is set, including the distinct set-but-empty state.
     */
    SET,

    /**
     * The feature is unset and therefore necessarily empty.
     */
    UNSET
  }

  /**
   * Distinguishes semantically significant causes for occurrence absence.
   *
   * @author Eike Stepper
   */
  public enum RemovalCause
  {
    /**
     * An explicit indexed REMOVE addressed this occurrence.
     */
    EXPLICIT_REMOVE,

    /**
     * A CLEAR observed and removed this occurrence.
     */
    CLEAR,

    /**
     * An UNSET observed and removed this occurrence.
     */
    UNSET
  }

  /**
   * Distinguishes user replacement lineage from merge-time uniqueness coalescing redirects.
   *
   * @author Eike Stepper
   */
  public enum RedirectCause
  {
    /**
     * A SET user operation replaced an occurrence.
     */
    REPLACEMENT,

    /**
     * Concurrent SETs produced semantically equal representatives.
     */
    EQUIVALENT_REPLACEMENT,

    /**
     * Duplicate resolution coalesced equal-valued distinct occurrences for a unique feature.
     */
    DUPLICATE_COALESCING
  }

  /**
   * Classifies genuine same-lineage conflicts by semantic dimension rather than delta-type pairs.
   *
   * @author Eike Stepper
   */
  public enum OccurrenceConflictKind
  {
    /**
     * One side removed a lineage while the other changed its effective content or placement.
     */
    REMOVE_VS_MUTATION,

    /**
     * Both sides selected different effective replacement values.
     */
    CONCURRENT_REPLACEMENT,

    /**
     * Both sides selected placement constraints whose union is cyclic.
     */
    CONCURRENT_PLACEMENT
  }

  /**
   * Valid semantic alternatives returned by the focused occurrence-conflict policy.
   *
   * @author Eike Stepper
   */
  public enum OccurrenceResolution
  {
    /**
     * Report the semantic conflict to the outer merger.
     */
    FAIL,

    /**
     * Select the source representative or placement.
     */
    SOURCE,

    /**
     * Select the target representative or placement.
     */
    TARGET,

    /**
     * Select absence for REMOVE_VS_MUTATION.
     */
    REMOVE,

    /**
     * Preserve the effective mutation for REMOVE_VS_MUTATION.
     */
    KEEP_MUTATION
  }

  /**
   * Valid semantic modes returned by the CLEAR-specific policy.
   *
   * @author Eike Stepper
   */
  public enum ClearResolution
  {
    /**
     * Remove exactly the occurrences observed by CLEAR and preserve unobserved concurrent additions.
     */
    OBSERVED_REMOVE,

    /**
     * Let CLEAR dominate all merged contents.
     */
    CLEAR_WINS,

    /**
     * Reject CLEAR when an observed lineage has an effective concurrent mutation.
     */
    FAIL
  }

  /**
   * Valid semantic modes returned by the UNSET-specific policy.
   *
   * @author Eike Stepper
   */
  public enum UnsetResolution
  {
    /**
     * Reject UNSET when it is incompatible with an effective concurrent mutation.
     */
    FAIL,

    /**
     * Let UNSET dominate contents and feature set-state.
     */
    UNSET_WINS,

    /**
     * Treat UNSET content removal as observed CLEAR and produce SET if concurrent contents survive.
     */
    MERGE_AS_CLEAR
  }

  /**
   * Valid representatives returned by uniqueness duplicate resolution.
   *
   * @author Eike Stepper
   */
  public enum DuplicateResolution
  {
    /**
     * Coalesce deterministically while preserving compatible placement intent from both occurrences.
     */
    COALESCE,

    /**
     * Select the first occurrence offered in the immutable context.
     */
    FIRST,

    /**
     * Select the second occurrence offered in the immutable context.
     */
    SECOND,

    /**
     * Reject the uniqueness collision.
     */
    FAIL
  }

  /**
   * Read-only policy adapter. Implementations choose only among alternatives validated by the semantic engine and may
   * not mutate occurrences, histories, or position orders.
   *
   * @author Eike Stepper
   */
  public interface Policies
  {
    /**
     * Resolves a genuine incompatible concurrent change of one ancestor lineage.
     */
    public OccurrenceResolution resolveOccurrence(OccurrenceConflictContext context);

    /**
     * Chooses one next occurrence from simultaneously eligible topological candidates.
     */
    public MergedOccurrence chooseOrdering(OrderingContext context);

    /**
     * Resolves a hard uniqueness collision between two distinct surviving occurrences.
     */
    public DuplicateResolution resolveDuplicate(DuplicateContext context);

    /**
     * Resolves an effective CLEAR against mutation of an occurrence observed by that CLEAR.
     */
    public ClearResolution resolveClear(ClearContext context);

    /**
     * Returns the configured global CLEAR mode used for concurrent unobserved additions.
     */
    public ClearResolution getClearMode();

    /**
     * Resolves an effective UNSET against incompatible effective concurrent mutation.
     */
    public UnsetResolution resolveUnset(UnsetContext context);
  }

  /**
   * Immutable context for a genuine concurrent conflict on one ancestor-rooted lineage.
   *
   * @author Eike Stepper
   */
  public static final class OccurrenceConflictContext
  {
    /**
     * Semantic dimension that could not be combined automatically.
     */
    public final OccurrenceConflictKind kind;

    /**
     * Stable common-ancestor lineage affected by the conflict.
     */
    public final Lineage lineage;

    /**
     * Read-only normalized source state for the lineage.
     */
    public final SideLineage source;

    /**
     * Read-only normalized target state for the lineage.
     */
    public final SideLineage target;

    /**
     * Creates an immutable occurrence-conflict snapshot.
     */
    public OccurrenceConflictContext(OccurrenceConflictKind kind, Lineage lineage, SideLineage source, SideLineage target)
    {
      this.kind = kind;
      this.lineage = lineage;
      this.source = source;
      this.target = target;
    }
  }

  /**
   * Immutable context exposing only currently eligible candidates at one topological choice point.
   *
   * @author Eike Stepper
   */
  public static final class OrderingContext
  {
    /**
     * Already materialized immutable output prefix.
     */
    public final List<MergedOccurrence> prefix;

    /**
     * Immutable candidates whose hard predecessors have all been satisfied.
     */
    public final List<MergedOccurrence> eligible;

    /**
     * Creates a defensive read-only ordering snapshot.
     */
    public OrderingContext(List<MergedOccurrence> prefix, List<MergedOccurrence> eligible)
    {
      this.prefix = Collections.unmodifiableList(new ArrayList<>(prefix));
      this.eligible = Collections.unmodifiableList(new ArrayList<>(eligible));
    }
  }

  /**
   * Immutable context for one hard uniqueness collision.
   *
   * @author Eike Stepper
   */
  public static final class DuplicateContext
  {
    /**
     * First deterministic surviving occurrence in the equivalence class.
     */
    public final MergedOccurrence first;

    /**
     * Newly encountered equivalent surviving occurrence.
     */
    public final MergedOccurrence second;

    /**
     * Creates an immutable duplicate-resolution snapshot.
     */
    public DuplicateContext(MergedOccurrence first, MergedOccurrence second)
    {
      this.first = first;
      this.second = second;
    }
  }

  /**
   * Immutable context for CLEAR interaction with an observed concurrently mutated lineage.
   *
   * @author Eike Stepper
   */
  public static final class ClearContext
  {
    /**
     * Side whose CLEAR observed the lineage.
     */
    public final Origin clearingSide;

    /**
     * Stable ancestor lineage affected by CLEAR.
     */
    public final Lineage lineage;

    /**
     * Whether the other side effectively changed representative value.
     */
    public final boolean contentChanged;

    /**
     * Whether the other side effectively changed placement.
     */
    public final boolean placementChanged;

    /**
     * Creates an immutable CLEAR-policy snapshot.
     */
    public ClearContext(Origin clearingSide, Lineage lineage, boolean contentChanged, boolean placementChanged)
    {
      this.clearingSide = clearingSide;
      this.lineage = lineage;
      this.contentChanged = contentChanged;
      this.placementChanged = placementChanged;
    }
  }

  /**
   * Immutable context for effective UNSET interaction with concurrent effective mutations.
   *
   * @author Eike Stepper
   */
  public static final class UnsetContext
  {
    /**
     * Side carrying the active UNSET intent.
     */
    public final Origin unsettingSide;

    /**
     * Immutable stable lineages whose concurrent mutations cannot coexist with UNSET.
     */
    public final List<Lineage> concurrentMutations;

    /**
     * Creates a defensive read-only UNSET-policy snapshot.
     */
    public UnsetContext(Origin unsettingSide, List<Lineage> concurrentMutations)
    {
      this.unsettingSide = unsettingSide;
      this.concurrentMutations = Collections.unmodifiableList(new ArrayList<>(concurrentMutations));
    }
  }

  /**
   * Stable lineage shared by source and target for an ancestor occurrence, or owned by one side for a new occurrence.
   *
   * @author Eike Stepper
   */
  public static final class Lineage
  {
    /**
     * Stable deterministic merge-local identifier.
     */
    public final String id;

    /**
     * Identity domain in which this lineage was created.
     */
    public final Origin origin;

    /**
     * Ancestor index or side-local operation sequence used for stable diagnostics.
     */
    public final int ancestorIndex;

    /**
     * Original ancestor value, unknown for side-local additions.
     */
    public final Object ancestorValue;

    /**
     * Shared original occurrence for an ancestor lineage.
     */
    public Occurrence ancestorOccurrence;

    /**
     * Shared original position for an ancestor lineage.
     */
    public Position ancestorPosition;

    /**
     * Immediate original lower landmark, retained for normalization diagnostics.
     */
    public Position ancestorPredecessor;

    /**
     * Immediate original upper landmark, retained for normalization diagnostics.
     */
    public Position ancestorSuccessor;

    /**
     * First side-local occurrence for an addition lineage.
     */
    public Occurrence addedOccurrence;

    /**
     * Whether this lineage was created by an executable ADD rather than being already visible in a side base.
     * Stable ordering keeps an established side-base occurrence ahead of such a later fresh addition when the
     * partial order intentionally leaves their relation unknown.
     */
    public boolean addCreated;

    /**
     * Creates one merge-local lineage identity.
     */
    public Lineage(String id, Origin origin, int ancestorIndex, Object ancestorValue)
    {
      this.id = id;
      this.origin = origin;
      this.ancestorIndex = ancestorIndex;
      this.ancestorValue = ancestorValue;
    }
  }

  /**
   * Immutable merge-local occurrence identity. Values may be equal while occurrences and their lineages remain
   * distinct; MOVE preserves this identity and SET creates a new identity in the same lineage.
   *
   * @author Eike Stepper
   */
  public static final class Occurrence
  {
    /**
     * Stable deterministic diagnostic identifier.
     */
    public final String id;

    /**
     * Replacement/addition lineage to which this occurrence belongs.
     */
    public final Lineage lineage;

    /**
     * CDO-level feature value represented by this occurrence.
     */
    public final Object value;

    /**
     * Side or ancestor domain that created this occurrence identity.
     */
    public final Origin origin;

    /**
     * Monotonic creation ordinal used only as a deterministic final tie-breaker.
     */
    public final int ordinal;

    /**
     * Creates an immutable occurrence identity.
     */
    public Occurrence(String id, Lineage lineage, Object value, Origin origin, int ordinal)
    {
      this.id = id;
      this.lineage = lineage;
      this.value = value;
      this.origin = origin;
      this.ordinal = ordinal;
    }
  }

  /**
   * Immutable merge-local historical placement landmark. A Position is not an integer list index and remains in its
   * {@link PositionOrder} after the occurrence that occupied or created it is removed, replaced, or moved. This retained
   * history is required to interpret concurrent insertion boundaries without inventing relations to invisible nodes.
   *
   * @author Eike Stepper
   */
  public static final class Position
  {
    /**
     * Stable deterministic diagnostic identifier.
     */
    public final String id;

    /**
     * Side or ancestor domain that created the landmark.
     */
    public final Origin origin;

    /**
     * Operation sequence that created it, or ancestor index for original positions.
     */
    public final int sequence;

    /**
     * Stable total ordinal used only for deterministic processing of otherwise unordered history nodes.
     */
    public final int ordinal;

    /**
     * Whether this landmark is the non-materializable START or END sentinel.
     */
    public final boolean sentinel;

    /**
     * Creates an immutable historical placement landmark.
     */
    public Position(String id, Origin origin, int sequence, int ordinal, boolean sentinel)
    {
      this.id = id;
      this.origin = origin;
      this.sequence = sequence;
      this.ordinal = ordinal;
      this.sentinel = sentinel;
    }
  }

  /**
   * Mutable visible-list cell coupling stable occurrence identity to its current historical position.
   *
   * @author Eike Stepper
   */
  public static final class Entry
  {
    /**
     * Stable occurrence currently visible in this cell.
     */
    public final Occurrence occurrence;

    /**
     * Effective historical placement currently occupied by the occurrence.
     */
    public final Position position;

    /**
     * Creates one side-local visible-list cell.
     */
    public Entry(Occurrence occurrence, Position position)
    {
      this.occurrence = occurrence;
      this.position = position;
    }
  }

  /**
   * Immutable provenance attached to one hard ordering constraint.
   *
   * @author Eike Stepper
   */
  public static final class Provenance
  {
    /**
     * Side whose observed history established the constraint.
     */
    public final Origin side;

    /**
     * Operation sequence, or {@code -1} for ancestor adjacency.
     */
    public final int sequence;

    /**
     * Human-readable semantic cause.
     */
    public final String cause;

    /**
     * Creates immutable constraint provenance.
     */
    public Provenance(Origin side, int sequence, String cause)
    {
      this.side = side;
      this.sequence = sequence;
      this.cause = cause;
    }

    /**
     * Returns stable compact diagnostic text.
     */
    @Override
    public String toString()
    {
      return side + "#" + sequence + " " + cause;
    }
  }

  /**
   * Immutable directed ordering edge and its semantic provenance.
   *
   * @author Eike Stepper
   */
  public static final class Constraint
  {
    /**
     * Required predecessor landmark.
     */
    public final Position before;

    /**
     * Required successor landmark.
     */
    public final Position after;

    /**
     * History observation that justified the relation.
     */
    public final Provenance provenance;

    /**
     * Creates one immutable hard ordering constraint.
     */
    public Constraint(Position before, Position after, Provenance provenance)
    {
      this.before = before;
      this.after = after;
      this.provenance = provenance;
    }
  }

  /**
   * Immutable decoded operation record retained after net-effect normalization for diagnostics and policy context.
   *
   * @author Eike Stepper
   */
  public static final class Operation
  {
    /**
     * Zero-based causal sequence in the incoming side history.
     */
    public final int sequence;

    /**
     * Original immutable CDO feature delta.
     */
    public final CDOFeatureDelta delta;

    /**
     * Human-readable occurrence/position effect produced by decoding.
     */
    public final String effect;

    /**
     * Creates one immutable semantic operation record.
     */
    public Operation(int sequence, CDOFeatureDelta delta, String effect)
    {
      this.sequence = sequence;
      this.delta = delta;
      this.effect = effect;
    }
  }

  /**
   * Effective normalized state of one lineage on one side.
   *
   * @author Eike Stepper
   */
  public static final class SideLineage
  {
    /**
     * Owner side, retained for policy diagnostics.
     */
    public final SideState owner;

    /**
     * Stable lineage being described.
     */
    public final Lineage lineage;

    /**
     * Side identity matching {@link #owner}.
     */
    public final Origin side;

    /**
     * Whether this side's causal base actually contained and therefore observed the result-base lineage.
     */
    public final boolean observed;

    /**
     * Whether one representative remains visible after the side history.
     */
    public final boolean present;

    /**
     * Effective surviving representative, or {@code null} when absent.
     */
    public final Occurrence representative;

    /**
     * Effective surviving position, or {@code null} when absent.
     */
    public final Position position;

    /**
     * Whether final representative value differs semantically from the ancestor.
     */
    public final boolean contentChanged;

    /**
     * Whether final placement relations differ semantically from the ancestor placement.
     */
    public final boolean placementChanged;

    /**
     * Effective cause of absence, or {@code null} while present.
     */
    public final RemovalCause removalCause;

    /**
     * Creates one immutable normalized lineage state.
     */
    public SideLineage(SideState owner, Lineage lineage, boolean observed, boolean present, Occurrence representative, Position position,
        boolean contentChanged, boolean placementChanged, RemovalCause removalCause)
    {
      this.owner = owner;
      this.lineage = lineage;
      side = owner.side;
      this.observed = observed;
      this.present = present;
      this.representative = representative;
      this.position = position;
      this.contentChanged = contentChanged;
      this.placementChanged = placementChanged;
      this.removalCause = removalCause;
    }
  }

  /**
   * One independently decoded side history, including its visible state, retained operation provenance, historical
   * position order, and derived immutable net effects.
   *
   * @author Eike Stepper
   */
  public final class SideState
  {
    /**
     * SOURCE or TARGET identity domain.
     */
    public final Origin side;

    /**
     * Stable short prefix used for merge-local identifiers.
     */
    public final String prefix;

    /**
     * Current causal virtual visible list while decoding.
     */
    public final List<Entry> visible = new ArrayList<>();

    /**
     * Side-local historical placement DAG.
     */
    public final PositionOrder order;

    /**
     * Lossless operation records for the incoming normalized CDO history.
     */
    public final List<Operation> operations = new ArrayList<>();

    /**
     * Side-local addition lineages in deterministic creation order.
     */
    public final List<Lineage> addedLineages = new ArrayList<>();

    /**
     * All side-created replacement/addition occurrences for redirect validation.
     */
    public final List<Occurrence> allOccurrences = new ArrayList<>();

    /**
     * Last effective removal cause recorded per lineage during decoding.
     */
    public final Map<Lineage, RemovalCause> removals = new IdentityHashMap<>();

    /**
     * Result-base lineages that were already visible in this side's causal start revision.
     */
    public final Set<Lineage> observedBaseLineages = Collections.newSetFromMap(new IdentityHashMap<Lineage, Boolean>());

    /**
     * Occurrences/lineages explicitly observed by CLEAR or UNSET.
     */
    public final Set<Lineage> observedByClearOrUnset = Collections.newSetFromMap(new IdentityHashMap<Lineage, Boolean>());

    /**
     * Deterministically ordered normalized lineage states.
     */
    public final Map<Lineage, SideLineage> lineages = new LinkedHashMap<>();

    /**
     * Set-state in this side's causal start revision.
     */
    public final SetState baseSetState;

    /**
     * Effective final feature set-state after this side's causal history.
     */
    public SetState setState;

    /**
     * Retained CLEAR operation when present in the normalized input history.
     */
    public Operation clearOperation;

    /**
     * Retained UNSET operation when present in the normalized input history.
     */
    public Operation unsetOperation;

    /**
     * Focused policy result for effective UNSET conflict, or {@code null} when not needed.
     */
    public UnsetResolution unsetResolution;

    /**
     * Creates an empty side state initialized to this side's causal base set-state.
     */
    public SideState(Origin side, SetState baseSetState)
    {
      this.side = side;
      this.baseSetState = baseSetState;
      setState = baseSetState;
      prefix = side == Origin.SOURCE ? "S" : "T";
      order = new PositionOrder(side.toString());
    }

    /**
     * Derives effective lineage states from the final visible list without deleting decoded operations or landmarks.
     */
    public void normalize()
    {
      Map<Lineage, Entry> visibleByLineage = new IdentityHashMap<>();
      for (Entry entry : visible)
      {
        Entry old = visibleByLineage.put(entry.occurrence.lineage, entry);
        if (old != null)
        {
          throw invariant("One lineage appears twice in " + side + " visible state: " + entry.occurrence.lineage.id);
        }

      }

      for (Lineage lineage : ancestorLineages)
      {
        if (!observedBaseLineages.contains(lineage))
        {
          // Absence from this side's base is lack of observation, not a user removal of the result-base occurrence.
          lineages.put(lineage, new SideLineage(this, lineage, false, false, null, null, false, false, null));
        }
        else
        {
          normalizeLineage(lineage, visibleByLineage.get(lineage));
        }
      }

      for (Lineage lineage : addedLineages)
      {
        normalizeLineage(lineage, visibleByLineage.get(lineage));
      }
    }

    /**
     * Returns whether final UNSET represents an active change relative to this side's causal base.
    */
    public boolean hasEffectiveUnset()
    {
      return setState == SetState.UNSET && baseSetState != SetState.UNSET;
    }

    /**
     * Finds effective other-side mutations that cannot coexist with this side's active UNSET intent.
     */
    public List<Lineage> effectiveMutationsAgainstUnset(SideState unsetting)
    {
      List<Lineage> result = new ArrayList<>();

      for (SideLineage lineage : lineages.values())
      {
        if (!lineage.observed && lineage.lineage.origin == Origin.ANCESTOR)
        {
          continue;
        }

        if (lineage.lineage.origin != Origin.ANCESTOR)
        {
          if (lineage.present)
          {
            result.add(lineage.lineage);
          }

          continue;
        }

        if (lineage.present && (lineage.contentChanged || lineage.placementChanged))
        {
          result.add(lineage.lineage);
        }
        else if (lineage.present && !unsetting.observedByClearOrUnset.contains(lineage.lineage))
        {
          result.add(lineage.lineage);
        }
      }

      return result;
    }

    /**
     * Derives one immutable net lineage state, including semantic rather than token-identity placement change.
     */
    private void normalizeLineage(Lineage lineage, Entry entry)
    {
      if (entry == null)
      {
        lineages.put(lineage, new SideLineage(this, lineage, true, false, null, null, false, false, removals.get(lineage)));
        return;
      }

      boolean contentChanged = lineage.origin != Origin.ANCESTOR || !valuesEqual(lineage.ancestorValue, entry.occurrence.value);
      boolean placementChanged = lineage.origin != Origin.ANCESTOR || !isPlacementEquivalentToAncestor(lineage, entry.position);
      Position effectivePosition = placementChanged ? entry.position : lineage.ancestorPosition;

      if (!placementChanged && effectivePosition != entry.position)
      {
        // Preserve relations observed around a move-away/move-back position while canonicalizing its net placement.
        order.transferBounds(entry.position, effectivePosition, new Provenance(side, entry.position.sequence, "net ancestor placement"));
      }

      lineages.put(lineage, new SideLineage(this, lineage, true, true, entry.occurrence, effectivePosition, contentChanged, placementChanged, null));
    }

    /**
     * Tests net placement by comparing relations to every other final active occurrence, not Position object identity.
     */
    private boolean isPlacementEquivalentToAncestor(Lineage lineage, Position effectivePosition)
    {
      if (effectivePosition == lineage.ancestorPosition)
      {
        return true;
      }

      for (Entry other : visible)
      {
        if (other.occurrence.lineage == lineage)
        {
          continue;
        }

        boolean ancestorBefore = order.isBefore(lineage.ancestorPosition, other.position);
        boolean effectiveBefore = order.isBefore(effectivePosition, other.position);
        boolean ancestorAfter = order.isBefore(other.position, lineage.ancestorPosition);
        boolean effectiveAfter = order.isBefore(other.position, effectivePosition);

        if (ancestorBefore != effectiveBefore || ancestorAfter != effectiveAfter)
        {
          return false;
        }
      }

      return true;
    }
  }

  /**
   * Small validated DAG of immutable historical {@link Position} landmarks.
   * <p>
   * Consider ancestor {@code [A,B,C]}, followed on one side by {@code REMOVE B} and {@code ADD X} between visible A
   * and C. The insertion establishes {@code P_A < P_X < P_C}. It deliberately establishes neither {@code P_X < P_B}
   * nor {@code P_B < P_X}: B's historical landmark was invisible when the insertion boundary was chosen. Inferring
   * either relation from the collapsed numeric index would invent information not present in that history.
   * <p>
   * The graph stores adjacent/direct constraints only and computes reachability on demand. It has no external graph
   * dependency and validates every newly introduced edge against cycles.
   *
   * @author Eike Stepper
   */
  public final class PositionOrder
  {
    /**
     * Stable diagnostic name of this side or merged order.
     */
    private final String name;

    /**
     * Direct outgoing edges, preserving deterministic insertion order.
     */
    private final Map<Position, LinkedHashSet<Position>> successors = new LinkedHashMap<>();

    /**
     * Direct incoming edges, preserving deterministic insertion order.
     */
    private final Map<Position, LinkedHashSet<Position>> predecessors = new LinkedHashMap<>();

    /**
     * Direct constraints with provenance in deterministic insertion order.
     */
    private final List<Constraint> constraints = new ArrayList<>();

    /**
     * Creates an empty named position order.
     */
    public PositionOrder(String name)
    {
      this.name = name;
    }

    /**
     * Adds one hard relation unless already reachable and rejects relations that would make the history cyclic.
     */
    public void addConstraint(Position before, Position after, Provenance provenance)
    {
      if (!tryAddConstraint(before, after, provenance))
      {
        throw invariant("Ordering constraint " + before.id + " < " + after.id + " creates a cycle in " + name);
      }
    }

    /**
     * Speculatively adds one relation and reports an incompatible cycle without converting a normal merge choice into
     * an invariant failure. Side-local causal histories use {@link #addConstraint(Position, Position, Provenance)};
     * only cross-history placement combination uses this method's negative result for focused policy dispatch.
     */
    public boolean tryAddConstraint(Position before, Position after, Provenance provenance)
    {
      if (before == after)
      {
        return false;
      }

      ensureNode(before);
      ensureNode(after);

      if (isBefore(before, after))
      {
        return true;
      }

      if (isBefore(after, before))
      {
        return false;
      }

      successors.get(before).add(after);
      predecessors.get(after).add(before);
      constraints.add(new Constraint(before, after, provenance));
      return true;
    }

    /**
     * Returns whether a directed path establishes that {@code before} precedes {@code after}.
     */
    public boolean isBefore(Position before, Position after)
    {
      if (before == after)
      {
        return false;
      }

      Set<Position> visited = Collections.newSetFromMap(new IdentityHashMap<Position, Boolean>());
      Deque<Position> pending = new ArrayDeque<>();
      pending.add(before);

      while (!pending.isEmpty())
      {
        Position position = pending.removeFirst();
        if (!visited.add(position))
        {
          continue;
        }

        Set<Position> direct = successors.get(position);
        if (direct != null)
        {
          if (direct.contains(after))
          {
            return true;
          }

          pending.addAll(direct);
        }
      }

      return false;
    }

    /**
     * Returns immutable direct predecessors of one landmark.
     */
    public Collection<Position> predecessors(Position position)
    {
      Set<Position> result = predecessors.get(position);
      return result == null ? Collections.emptyList() : Collections.unmodifiableCollection(result);
    }

    /**
     * Returns immutable direct successors of one landmark.
     */
    public Collection<Position> successors(Position position)
    {
      Set<Position> result = successors.get(position);
      return result == null ? Collections.emptyList() : Collections.unmodifiableCollection(result);
    }

    /**
     * Returns all graph nodes in deterministic insertion order.
     */
    public Collection<Position> nodes()
    {
      return Collections.unmodifiableCollection(successors.keySet());
    }

    /**
     * Returns all direct constraints in deterministic insertion order.
     */
    public List<Constraint> constraints()
    {
      return Collections.unmodifiableList(constraints);
    }

    /**
     * Adds all hard constraints from another history, preserving their original provenance.
     */
    public void addAll(PositionOrder other)
    {
      for (Position node : other.nodes())
      {
        ensureNode(node);
      }

      for (Constraint constraint : other.constraints)
      {
        addConstraint(constraint.before, constraint.after, constraint.provenance);
      }
    }

    /**
     * Creates an independent graph copy for speculative compatible-placement combination.
     */
    public PositionOrder copy(String copyName)
    {
      PositionOrder copy = new PositionOrder(copyName);
      copy.addAll(this);
      return copy;
    }

    /**
     * Transfers the direct observed bounds of a net-equivalent side-local position to its canonical ancestor position.
     */
    public void transferBounds(Position from, Position to, Provenance provenance)
    {
      List<Position> lower = new ArrayList<>(predecessors(from));
      List<Position> upper = new ArrayList<>(successors(from));

      for (Position predecessor : lower)
      {
        if (predecessor != to && !isBefore(to, predecessor))
        {
          addConstraint(predecessor, to, provenance);
        }
      }

      for (Position successor : upper)
      {
        if (successor != to && !isBefore(successor, to))
        {
          addConstraint(to, successor, provenance);
        }
      }
    }

    /**
     * Returns whether the complete direct graph contains a cycle.
     */
    public boolean hasCycle()
    {
      Topology topology = topology();
      while (!topology.ready().isEmpty())
      {
        topology.remove(topology.ready().iterator().next());
      }

      return topology.hasNodes();
    }

    /**
     * Creates a mutable topological traversal snapshot without exposing or mutating the graph.
     */
    public Topology topology()
    {
      return new Topology();
    }

    /**
     * Ensures a position participates in the graph even when it has no direct edges yet.
     */
    private void ensureNode(Position position)
    {
      successors.computeIfAbsent(position, key -> new LinkedHashSet<>());
      predecessors.computeIfAbsent(position, key -> new LinkedHashSet<>());
    }

    /**
     * Mutable Kahn traversal state private to one linearization/validation pass.
     */
    public final class Topology
    {
      /**
       * Remaining direct successors copied from the immutable semantic graph.
       */
      private final Map<Position, LinkedHashSet<Position>> remainingSuccessors = new LinkedHashMap<>();

      /**
       * Remaining predecessor counts for every graph node.
       */
      private final Map<Position, Integer> predecessorCounts = new LinkedHashMap<>();

      /**
       * Currently eligible nodes in stable graph insertion order.
       */
      private final LinkedHashSet<Position> ready = new LinkedHashSet<>();

      /**
       * Creates a complete independent Kahn traversal snapshot.
       */
      public Topology()
      {
        for (Position position : successors.keySet())
        {
          remainingSuccessors.put(position, new LinkedHashSet<>(successors.get(position)));

          int count = predecessors.get(position).size();
          predecessorCounts.put(position, count);

          if (count == 0)
          {
            ready.add(position);
          }
        }
      }

      /**
       * Returns the immutable current eligible node set.
       */
      public Collection<Position> ready()
      {
        return Collections.unmodifiableCollection(ready);
      }

      /**
       * Returns whether unprocessed nodes remain.
       */
      public boolean hasNodes()
      {
        return !predecessorCounts.isEmpty();
      }

      /**
       * Removes one eligible node and exposes successors whose last hard predecessor was satisfied.
       */
      public void remove(Position position)
      {
        if (!ready.remove(position))
        {
          throw invariant("Topological traversal removed non-ready position " + position.id);
        }

        predecessorCounts.remove(position);

        for (Position successor : remainingSuccessors.remove(position))
        {
          int count = predecessorCounts.get(successor) - 1;
          predecessorCounts.put(successor, count);

          if (count == 0)
          {
            ready.add(successor);
          }
        }
      }
    }
  }

  /**
   * Immutable transitive occurrence resolution edge with explicit semantic provenance.
   *
   * @author Eike Stepper
   */
  public static final class Redirect
  {
    /**
     * Occurrence selected as the next representative.
     */
    public final Occurrence target;

    /**
     * Replacement or uniqueness reason for this redirect.
     */
    public final RedirectCause cause;

    /**
     * Creates one immutable occurrence redirect.
     */
    public Redirect(Occurrence target, RedirectCause cause)
    {
      this.target = target;
      this.cause = cause;
    }
  }

  /**
   * Final pre-linearization semantic occurrence with one representative and one effective position.
   *
   * @author Eike Stepper
   */
  public static final class MergedOccurrence
  {
    /**
     * Private sentinel used to propagate an ordinary semantic conflict without confusing it with absence.
     */
    public static final MergedOccurrence CONFLICT = new MergedOccurrence(null, null, null, null, "conflict");

    /**
     * Selected representative occurrence identity and value.
     */
    public final Occurrence occurrence;

    /**
     * Selected or synthetic effective historical position.
     */
    public final Position position;

    /**
     * Stable lineage preserved by encoding when ancestor-rooted.
     */
    public final Lineage lineage;

    /**
     * Dominant provenance used for stable ordering and diagnostics.
     */
    public final Origin origin;

    /**
     * Human-readable semantic decision.
     */
    public final String decision;

    /**
     * Creates one immutable merged occurrence state.
     */
    public MergedOccurrence(Occurrence occurrence, Position position, Lineage lineage, Origin origin, String decision)
    {
      this.occurrence = occurrence;
      this.position = position;
      this.lineage = lineage;
      this.origin = origin;
      this.decision = decision;
    }
  }

  /**
   * Complete immutable semantic list result consumed by the numeric delta encoder.
   *
   * @author Eike Stepper
   */
  public static final class SemanticResult
  {
    /**
     * Final total order of distinct surviving semantic occurrences.
     */
    public final List<MergedOccurrence> occurrences;

    /**
     * Final orthogonal feature set-state.
     */
    public final SetState setState;

    /**
     * Creates a defensive immutable semantic result.
     */
    public SemanticResult(List<MergedOccurrence> occurrences, SetState setState)
    {
      this.occurrences = Collections.unmodifiableList(new ArrayList<>(occurrences));
      this.setState = setState;
    }
  }

  /**
   * Mutable identity-aware encoder cell for one current ancestor/result lineage.
   *
   * @author Eike Stepper
   */
  public static final class EncodedEntry
  {
    /**
     * Stable lineage used to find and reorder ancestor identities.
     */
    public final Lineage lineage;

    /**
     * Current representative after emitted SET operations.
     */
    public final Occurrence occurrence;

    /**
     * Creates one encoder working cell.
     */
    public EncodedEntry(Lineage lineage, Occurrence occurrence)
    {
      this.lineage = lineage;
      this.occurrence = occurrence;
    }
  }

  /**
   * Independent replay cell carrying the semantic lineage through numeric encoded operations.
   *
   * @author Eike Stepper
   */
  public static final class ReplayEntry
  {
    /**
     * Stable semantic lineage represented by this replay cell.
     */
    public final Lineage lineage;

    /**
     * Current replayed feature value.
     */
    public final Object value;

    /**
     * Creates one replay cell.
     */
    public ReplayEntry(Lineage lineage, Object value)
    {
      this.lineage = lineage;
      this.value = value;
    }
  }
}
