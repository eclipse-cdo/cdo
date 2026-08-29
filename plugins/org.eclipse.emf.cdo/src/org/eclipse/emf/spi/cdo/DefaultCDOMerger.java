/*
 * Copyright (c) 2010-2013, 2015-2017, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMergerBaseAware;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default three-way merger for CDO change sets.
 * <p>
 * The first ({@code target}) change set describes the current branch or transaction into which the result will be
 * applied. In {@link CDOMergingConflictResolver} this is the local transaction history. The second ({@code source})
 * change set describes the branch or repository history being merged.
 * <p>
 * Ordinary conflict-resolution histories share one start state. Automatic branch remerge can instead select different
 * source and target bases. The branch transaction path therefore supplies both original base providers plus the actual
 * result base. The semantic many-valued merger preserves that causal asymmetry so occurrences absent from one side's
 * base are treated as unobserved rather than synthesized as removals. The richer branch/remerge entry point also
 * normalizes every returned NEW, CHANGED, and DETACHED goal to the selected result base;
 * {@code CDOTransactionImpl.applyChangeSet()} subsequently reconciles that goal with the actual target state.
 * <p>
 * CDO's internal producers retain the revision providers needed by the semantic many-valued merger. That richer
 * call-local context provides full base values to distinguish equal list occurrences and the base set-state of
 * unsettable features without storing repository/session state on this reusable merger instance.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public class DefaultCDOMerger implements CDOMergerBaseAware
{
  private final ResolutionPreference resolutionPreference;

  private final ListOrdering listOrdering;

  private CDOChangeSetData result;

  private Map<CDOID, Conflict> conflicts;

  private Map<CDOID, Object> targetMap;

  private Map<CDOID, Object> sourceMap;

  public DefaultCDOMerger()
  {
    this(ResolutionPreference.NONE);
  }

  /**
   * @since 4.6
   */
  public DefaultCDOMerger(ListOrdering listOrdering)
  {
    this(ResolutionPreference.NONE, listOrdering);
  }

  /**
   * @since 4.2
   */
  public DefaultCDOMerger(ResolutionPreference resolutionPreference)
  {
    this(resolutionPreference, ListOrdering.ORDERED);
  }

  /**
   * @since 4.6
   */
  public DefaultCDOMerger(ResolutionPreference resolutionPreference, ListOrdering listOrdering)
  {
    CheckUtil.checkArg(resolutionPreference, "resolutionPreference"); //$NON-NLS-1$
    this.resolutionPreference = resolutionPreference;

    CheckUtil.checkArg(listOrdering, "listOrdering"); //$NON-NLS-1$
    this.listOrdering = listOrdering;
  }

  /**
   * @since 4.2
   */
  public final ResolutionPreference getResolutionPreference()
  {
    return resolutionPreference;
  }

  /**
   * @since 4.6
   */
  public ListOrdering getListOrdering()
  {
    return listOrdering;
  }

  public CDOChangeSetData getResult()
  {
    return result;
  }

  public Map<CDOID, Conflict> getConflicts()
  {
    return conflicts;
  }

  @Override
  public synchronized CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws ConflictException
  {
    return merge(target, source, target.getStartRevisionProvider(), source.getStartRevisionProvider(), null, false);
  }

  /**
   * Merges branch/remerge change sets while preserving their independently selected source and target bases.
   * <p>
   * Automatic remerge can legitimately produce list histories with different origin sizes because each history starts
   * at the base that side actually observed. Reconstructing endpoint snapshots relative to the result base would turn
   * an occurrence that one side never observed into a synthetic REMOVE. Instead the original executable histories are
   * retained and the three base providers are propagated to the semantic many-valued merger.
   * <p>
   * The ordinary two-argument {@link #merge(CDOChangeSet, CDOChangeSet)} contract is retained for callers whose two
   * change sets already share their merge base.
   *
   * @param target the target change set selected by the branch merge calculation.
   * @param source the source change set selected by the branch merge calculation.
   * @param targetBaseProvider the revision state from which {@code target} starts.
   * @param sourceBaseProvider the revision state from which {@code source} starts.
   * @param resultBaseProvider the revision state relative to which every returned NEW, CHANGED, or DETACHED goal is defined.
   * @return the merged change-set data relative to {@code resultBaseProvider}.
   * @throws ConflictException if the configured merger semantics cannot resolve all conflicts.
   * @since 4.30
   */
  @Override
  public synchronized CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source, CDORevisionProvider targetBaseProvider,
      CDORevisionProvider sourceBaseProvider, CDORevisionProvider resultBaseProvider) throws ConflictException
  {
    CheckUtil.checkArg(targetBaseProvider, "targetBaseProvider"); //$NON-NLS-1$
    CheckUtil.checkArg(sourceBaseProvider, "sourceBaseProvider"); //$NON-NLS-1$
    CheckUtil.checkArg(resultBaseProvider, "resultBaseProvider"); //$NON-NLS-1$

    return merge(target, source, targetBaseProvider, sourceBaseProvider, resultBaseProvider, true);
  }

  /**
   * Executes one merge with either a common retained start state or explicit asymmetric branch/remerge bases. This
   * method is called only while the public synchronized merge entry point owns the merger monitor.
   */
  private CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source, CDORevisionProvider targetBaseProvider, CDORevisionProvider sourceBaseProvider,
      CDORevisionProvider resultBaseProvider, boolean asymmetricBases) throws ConflictException
  {
    result = new CDOChangeSetDataImpl();
    conflicts = CDOIDUtil.createMap();

    targetMap = createMap(target);
    sourceMap = createMap(source);

    // The public merger contract is delta-only, but CDO's own call paths retain the revision providers needed to
    // distinguish stable occurrences and SET [] from UNSET []. Keep this richer context local to the current call.
    CDORevisionProvider effectiveTargetBaseProvider = targetBaseProvider != null ? targetBaseProvider : target.getStartRevisionProvider();
    CDORevisionProvider effectiveSourceBaseProvider = sourceBaseProvider != null ? sourceBaseProvider : source.getStartRevisionProvider();

    Set<CDOID> taken = new HashSet<>();
    for (Map.Entry<CDOID, Object> entry : targetMap.entrySet())
    {
      CDOID id = entry.getKey();

      Object targetData = entry.getValue();
      Object sourceData = sourceMap.get(id);
      CDORevision resultBaseRevision = asymmetricBases ? getRevision(id, resultBaseProvider)
          : getAncestorRevision(id, effectiveTargetBaseProvider, effectiveSourceBaseProvider);
      boolean merged;

      if (asymmetricBases)
      {
        CDORevision targetBaseRevision = getRevision(id, effectiveTargetBaseProvider);
        CDORevision sourceBaseRevision = getRevision(id, effectiveSourceBaseProvider);
        merged = merge(targetData, sourceData, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
      }
      else
      {
        // Preserve the established delta-only/common-base behavior, including callers that cannot provide a revision.
        merged = merge(targetData, sourceData, resultBaseRevision);
      }

      if (merged)
      {
        taken.add(id);
      }
    }

    for (Map.Entry<CDOID, Object> entry : sourceMap.entrySet())
    {
      CDOID id = entry.getKey();
      if (taken.add(id))
      {
        Object sourceData = entry.getValue();
        Object targetData = targetMap.get(id);
        CDORevision resultBaseRevision = asymmetricBases ? getRevision(id, resultBaseProvider)
            : getAncestorRevision(id, effectiveTargetBaseProvider, effectiveSourceBaseProvider);

        if (asymmetricBases)
        {
          CDORevision targetBaseRevision = getRevision(id, effectiveTargetBaseProvider);
          CDORevision sourceBaseRevision = getRevision(id, effectiveSourceBaseProvider);
          merge(targetData, sourceData, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
        }
        else
        {
          merge(targetData, sourceData, resultBaseRevision);
        }
      }
    }

    if (!conflicts.isEmpty())
    {
      throw new ConflictException("Merger could not resolve all conflicts: " + conflicts, this, result);
    }

    return result;
  }

  protected boolean merge(Object targetData, Object sourceData)
  {
    return merge(targetData, sourceData, (CDORevision)null);
  }

  /**
   * Merges the data for one object with the full common-ancestor revision when the producing call path retained it.
   * The extra revision is an internal execution context; it does not widen the public {@link CDOMerger} contract.
   *
   * @since 4.30
   */
  protected boolean merge(Object targetData, Object sourceData, CDORevision ancestorRevision)
  {
    Object data = null;
    if (sourceData == null)
    {
      if (targetData instanceof CDORevision)
      {
        data = addedInTarget((CDORevision)targetData);
      }
      else if (targetData instanceof CDORevisionDelta)
      {
        data = changedInTarget((CDORevisionDelta)targetData);
      }
      else if (targetData instanceof CDOID)
      {
        data = detachedInTarget((CDOID)targetData);
      }
    }
    else if (targetData == null)
    {
      if (sourceData instanceof CDORevision)
      {
        data = addedInSource((CDORevision)sourceData);
      }
      else if (sourceData instanceof CDORevisionDelta)
      {
        data = changedInSource((CDORevisionDelta)sourceData);
      }
      else if (sourceData instanceof CDOID)
      {
        data = detachedInSource((CDOID)sourceData);
      }
    }
    else if (sourceData instanceof CDOID && targetData instanceof CDOID)
    {
      data = detachedInSourceAndTarget((CDOID)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDORevisionDelta)
    {
      data = changedInSourceAndTarget((CDORevisionDelta)targetData, (CDORevisionDelta)sourceData, ancestorRevision);
    }
    else if (sourceData instanceof CDORevision && targetData instanceof CDORevision)
    {
      data = addedInSourceAndTarget((CDORevision)targetData, (CDORevision)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDOID)
    {
      data = changedInSourceAndDetachedInTarget((CDORevisionDelta)sourceData);
    }
    else if (targetData instanceof CDORevisionDelta && sourceData instanceof CDOID)
    {
      data = changedInTargetAndDetachedInSource((CDORevisionDelta)targetData);
    }

    return take(data);
  }

  /**
   * Merges one object's data with the result, target, and source base revisions needed by asymmetric branch remerge.
   * Existing extenders remain on the common-ancestor overload unless they explicitly consume the richer context.
   *
   * @since 4.30
   */
  protected boolean merge(Object targetData, Object sourceData, CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
  {
    CDORevision targetEndRevision = getEndRevision(targetData, targetBaseRevision);
    CDORevision sourceEndRevision = getEndRevision(sourceData, sourceBaseRevision);
    return merge(targetData, sourceData, resultBaseRevision, targetBaseRevision, sourceBaseRevision, targetEndRevision, sourceEndRevision);
  }

  /**
   * Merges one object's data while keeping every returned goal relative to {@code resultBaseRevision}. NEW and DETACHED
   * classifications are first normalized to that base. CHANGED histories keep their own causal bases so semantic list
   * merging does not lose unobserved-occurrence information.
   *
   * @since 4.30
   */
  protected boolean merge(Object targetData, Object sourceData, CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision,
      CDORevision targetEndRevision, CDORevision sourceEndRevision)
  {
    if (resultBaseRevision == null)
    {
      // With an absent result base every present endpoint is NEW. Converting CHANGED to its complete endpoint also
      // normalizes otherwise impossible NEW-vs-CHANGED classification combinations caused by asymmetric side bases.
      if (targetData instanceof CDORevisionDelta)
      {
        targetData = targetEndRevision;
        targetBaseRevision = null;
      }
      else if (targetData instanceof CDOID)
      {
        targetData = null;
      }

      if (sourceData instanceof CDORevisionDelta)
      {
        sourceData = sourceEndRevision;
        sourceBaseRevision = null;
      }
      else if (sourceData instanceof CDOID)
      {
        sourceData = null;
      }
    }
    else
    {
      // A side-local NEW is a complete goal. Relative to an already present result base it is therefore a CHANGED
      // history starting exactly at that result base. Ordinary CHANGED histories retain their original causal bases.
      if (targetData instanceof CDORevision)
      {
        targetData = toResultBaseChange(resultBaseRevision, (CDORevision)targetData);
        targetBaseRevision = resultBaseRevision;
      }

      if (sourceData instanceof CDORevision)
      {
        sourceData = toResultBaseChange(resultBaseRevision, (CDORevision)sourceData);
        sourceBaseRevision = resultBaseRevision;
      }
    }

    Object data = null;
    if (sourceData == null)
    {
      if (targetData instanceof CDORevision)
      {
        data = addedInTarget((CDORevision)targetData);
      }
      else if (targetData instanceof CDORevisionDelta)
      {
        data = changedInTarget((CDORevisionDelta)targetData, resultBaseRevision, targetBaseRevision, sourceBaseRevision, targetEndRevision);
      }
      else if (targetData instanceof CDOID)
      {
        data = detachedInTarget((CDOID)targetData);
      }
    }
    else if (targetData == null)
    {
      if (sourceData instanceof CDORevision)
      {
        data = addedInSource((CDORevision)sourceData);
      }
      else if (sourceData instanceof CDORevisionDelta)
      {
        data = changedInSource((CDORevisionDelta)sourceData, resultBaseRevision, targetBaseRevision, sourceBaseRevision, sourceEndRevision);
      }
      else if (sourceData instanceof CDOID)
      {
        data = detachedInSource((CDOID)sourceData);
      }
    }
    else if (sourceData instanceof CDOID && targetData instanceof CDOID)
    {
      data = detachedInSourceAndTarget((CDOID)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDORevisionDelta)
    {
      data = changedInSourceAndTarget((CDORevisionDelta)targetData, (CDORevisionDelta)sourceData, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
    }
    else if (sourceData instanceof CDORevision && targetData instanceof CDORevision)
    {
      data = addedInSourceAndTarget((CDORevision)targetData, (CDORevision)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDOID)
    {
      data = changedInSourceAndDetachedInTarget((CDORevisionDelta)sourceData, resultBaseRevision, sourceBaseRevision, sourceEndRevision);
    }
    else if (targetData instanceof CDORevisionDelta && sourceData instanceof CDOID)
    {
      data = changedInTargetAndDetachedInSource((CDORevisionDelta)targetData, resultBaseRevision, targetBaseRevision, targetEndRevision);
    }
    else
    {
      throw new IllegalStateException(
          "Unsupported normalized merge classifications: target=" + classification(targetData) + ", source=" + classification(sourceData));
    }

    return take(data);
  }

  /**
   * Returns a stable classification name for invariant diagnostics.
   */
  private static String classification(Object data)
  {
    return data == null ? "UNCHANGED" : data.getClass().getSimpleName();
  }

  protected Object addedInTarget(CDORevision revision)
  {
    return revision;
  }

  protected Object addedInSource(CDORevision revision)
  {
    return revision;
  }

  protected Object addedInSourceAndTarget(CDORevision targetRevision, CDORevision sourceRevision)
  {
    return targetRevision;
  }

  protected Object changedInTarget(CDORevisionDelta delta)
  {
    return delta;
  }

  protected Object detachedInTarget(CDOID id)
  {
    return id;
  }

  /**
   * Resolves a one-sided target change as a goal relative to the actual result base.
   * <p>
   * The base implementation treats the selected target endpoint as the complete object-level goal. {@link PerFeature}
   * refines this behavior so unchanged features stay unobserved and many-valued changes retain occurrence semantics.
   * @since 4.30
   */
  protected Object changedInTarget(CDORevisionDelta delta, CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision,
      CDORevision targetEndRevision)
  {
    Object selected = changedInTarget(delta);
    if (selected instanceof CDORevisionDelta)
    {
      CDORevision selectedEndRevision = selected == delta ? targetEndRevision : getEndRevision(selected, targetBaseRevision);
      return toResultBaseChange(resultBaseRevision, selectedEndRevision);
    }

    if (selected instanceof CDORevision)
    {
      return toResultBaseChange(resultBaseRevision, (CDORevision)selected);
    }

    if (selected instanceof CDOID && resultBaseRevision == null)
    {
      return null;
    }

    return selected;
  }

  protected Object changedInSource(CDORevisionDelta delta)
  {
    return delta;
  }

  /**
   * Resolves a one-sided source change as a goal relative to the actual result base.
   * <p>
   * The base implementation treats the selected source endpoint as the complete object-level goal. {@link PerFeature}
   * refines this behavior so unchanged features stay unobserved and many-valued changes retain occurrence semantics.
   * @since 4.30
   */
  protected Object changedInSource(CDORevisionDelta delta, CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision,
      CDORevision sourceEndRevision)
  {
    Object selected = changedInSource(delta);
    if (selected instanceof CDORevisionDelta)
    {
      CDORevision selectedEndRevision = selected == delta ? sourceEndRevision : getEndRevision(selected, sourceBaseRevision);
      return toResultBaseChange(resultBaseRevision, selectedEndRevision);
    }

    if (selected instanceof CDORevision)
    {
      return toResultBaseChange(resultBaseRevision, (CDORevision)selected);
    }

    if (selected instanceof CDOID && resultBaseRevision == null)
    {
      return null;
    }

    return selected;
  }

  protected Object detachedInSource(CDOID id)
  {
    return id;
  }

  protected Object detachedInSourceAndTarget(CDOID id)
  {
    return id;
  }

  protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
  {
    switch (resolutionPreference)
    {
    case SOURCE_OVER_TARGET:
      return sourceDelta;

    case TARGET_OVER_SOURCE:
      return targetDelta;

    case NONE:
      return new ChangedInSourceAndTargetConflict(targetDelta, sourceDelta);

    default:
      throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
    }
  }

  /**
   * Resolves concurrent revision changes with an optional full common-ancestor revision. Subclasses that need only
   * deltas retain the established two-argument hook; semantic many-valued merging overrides this overload.
   *
   * @since 4.30
   */
  protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta, CDORevision ancestorRevision)
  {
    return changedInSourceAndTarget(targetDelta, sourceDelta);
  }

  /**
   * Resolves concurrent revision changes with explicit asymmetric branch/remerge base revisions.
   *
   * @since 4.30
   */
  protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta, CDORevision resultBaseRevision,
      CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
  {
    Object selected = changedInSourceAndTarget(targetDelta, sourceDelta, resultBaseRevision);
    if (selected == targetDelta)
    {
      return toResultBaseChange(resultBaseRevision, getEndRevision(targetDelta, targetBaseRevision));
    }

    if (selected == sourceDelta)
    {
      return toResultBaseChange(resultBaseRevision, getEndRevision(sourceDelta, sourceBaseRevision));
    }

    return selected;
  }

  protected Object changedInSourceAndDetachedInTarget(CDORevisionDelta sourceDelta)
  {
    switch (resolutionPreference)
    {
    case SOURCE_OVER_TARGET:
      return sourceDelta; // TODO Do we need to "recreate" the source revision as NEW?

    case TARGET_OVER_SOURCE:
      return sourceDelta.getID(); // Indicate detachment

    case NONE:
      return new ChangedInSourceAndDetachedInTargetConflict(sourceDelta);

    default:
      throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
    }
  }

  protected Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta)
  {
    switch (resolutionPreference)
    {
    case SOURCE_OVER_TARGET:
      return targetDelta.getID();

    case TARGET_OVER_SOURCE:
      return targetDelta; // TODO Do we need to "recreate" the target revision as NEW?

    case NONE:
      return new ChangedInTargetAndDetachedInSourceConflict(targetDelta);

    default:
      throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
    }
  }

  /**
   * Resolves source-change versus target-detach as a result-base-relative goal.
   * @since 4.30
   */
  protected Object changedInSourceAndDetachedInTarget(CDORevisionDelta sourceDelta, CDORevision resultBaseRevision, CDORevision sourceBaseRevision,
      CDORevision sourceEndRevision)
  {
    Object selected = changedInSourceAndDetachedInTarget(sourceDelta);
    if (selected instanceof CDORevisionDelta)
    {
      CDORevision selectedEndRevision = selected == sourceDelta ? sourceEndRevision : getEndRevision(selected, sourceBaseRevision);
      return toResultBaseChange(resultBaseRevision, selectedEndRevision);
    }

    if (selected instanceof CDORevision)
    {
      return toResultBaseChange(resultBaseRevision, (CDORevision)selected);
    }

    if (selected instanceof CDOID && resultBaseRevision == null)
    {
      return null;
    }

    return selected;
  }

  /**
   * Resolves target-change versus source-detach as a result-base-relative goal.
   *
   * @since 4.30
   */
  protected Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta, CDORevision resultBaseRevision, CDORevision targetBaseRevision,
      CDORevision targetEndRevision)
  {
    Object selected = changedInTargetAndDetachedInSource(targetDelta);
    if (selected instanceof CDORevisionDelta)
    {
      CDORevision selectedEndRevision = selected == targetDelta ? targetEndRevision : getEndRevision(selected, targetBaseRevision);
      return toResultBaseChange(resultBaseRevision, selectedEndRevision);
    }

    if (selected instanceof CDORevision)
    {
      return toResultBaseChange(resultBaseRevision, (CDORevision)selected);
    }

    if (selected instanceof CDOID && resultBaseRevision == null)
    {
      return null;
    }

    return selected;
  }

  /**
   * Converts a complete goal revision into the change-kind representation expected relative to the result base.
   */
  private static Object toResultBaseChange(CDORevision resultBaseRevision, CDORevision goalRevision)
  {
    if (goalRevision == null)
    {
      return resultBaseRevision == null ? null : resultBaseRevision.getID();
    }

    if (resultBaseRevision == null)
    {
      return goalRevision;
    }

    InternalCDORevisionDelta delta = (InternalCDORevisionDelta)goalRevision.compare(resultBaseRevision);
    delta.setTarget(null);
    return delta.isEmpty() ? null : delta;
  }

  /**
   * Reconstructs one side's complete endpoint revision from its causal base and change classification. A CHANGED
   * classification necessarily has a present side base, so no additional endpoint provider is required.
   */
  private static CDORevision getEndRevision(Object sideData, CDORevision baseRevision)
  {
    if (sideData instanceof CDORevision)
    {
      return (CDORevision)sideData;
    }

    if (sideData instanceof CDOID)
    {
      return null;
    }

    if (sideData instanceof CDORevisionDelta)
    {
      if (baseRevision == null)
      {
        throw new IllegalStateException("CHANGED side data has no causal base revision: " + ((CDORevisionDelta)sideData).getID());
      }

      InternalCDORevision endRevision = (InternalCDORevision)baseRevision.copy();
      ((CDORevisionDelta)sideData).applyTo(endRevision);
      return endRevision;
    }

    return baseRevision;
  }

  protected Map<CDOID, Object> getTargetMap()
  {
    return targetMap;
  }

  protected Map<CDOID, Object> getSourceMap()
  {
    return sourceMap;
  }

  private Map<CDOID, Object> createMap(CDOChangeSetData changeSetData)
  {
    Map<CDOID, Object> map = CDOIDUtil.createMap();
    for (CDOIDAndVersion data : changeSetData.getNewObjects())
    {
      map.put(data.getID(), data);
    }

    for (CDORevisionKey data : changeSetData.getChangedObjects())
    {
      map.put(data.getID(), data);
    }

    for (CDOIDAndVersion data : changeSetData.getDetachedObjects())
    {
      map.put(data.getID(), data.getID());
    }

    return map;
  }

  /**
   * Obtains one revision from an optional provider without retaining provider state on this reusable merger instance.
   */
  private static CDORevision getRevision(CDOID id, CDORevisionProvider provider)
  {
    return provider == null ? null : provider.getRevision(id);
  }

  private static CDORevision getAncestorRevision(CDOID id, CDORevisionProvider targetStartProvider, CDORevisionProvider sourceStartProvider)
  {
    CDORevision revision = targetStartProvider == null ? null : targetStartProvider.getRevision(id);
    if (revision == null && sourceStartProvider != null)
    {
      revision = sourceStartProvider.getRevision(id);
    }

    return revision;
  }

  private boolean take(Object data)
  {
    if (data instanceof Pair<?, ?>)
    {
      Pair<?, ?> pair = (Pair<?, ?>)data;
      boolean taken = takeNoPair(pair.getElement1());
      taken |= takeNoPair(pair.getElement2());
      return taken;
    }

    return takeNoPair(data);
  }

  private boolean takeNoPair(Object data)
  {
    if (data instanceof CDORevision)
    {
      result.getNewObjects().add((CDORevision)data);
    }
    else if (data instanceof CDORevisionDelta)
    {
      result.getChangedObjects().add((CDORevisionDelta)data);
    }
    else if (data instanceof CDOID)
    {
      result.getDetachedObjects().add(CDOIDUtil.createIDAndVersion((CDOID)data, CDOBranchVersion.UNSPECIFIED_VERSION));
    }
    else if (data instanceof Conflict)
    {
      Conflict conflict = (Conflict)data;
      conflicts.put(conflict.getID(), conflict);
    }
    else if (data != null)
    {
      throw new IllegalArgumentException("Must be a CDORevision, a CDORevisionDelta, a CDOID, a Conflict or null: " + data);
    }
    else
    {
      return false;
    }

    return true;
  }

  /**
   * Enumerates the possible resolution preferences that can be used with a {@link DefaultCDOMerger}.
   *
   * @since 4.2
   * @author Eike Stepper
   */
  public static enum ResolutionPreference
  {
    NONE,

    SOURCE_OVER_TARGET,

    TARGET_OVER_SOURCE,

    @Deprecated
    DETACH_OVER_CHANGE,

    @Deprecated
    CHANGE_OVER_DETACH
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static abstract class Conflict
  {
    public Conflict()
    {
    }

    public abstract CDOID getID();
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class ChangedInSourceAndTargetConflict extends Conflict
  {
    private CDORevisionDelta targetDelta;

    private CDORevisionDelta sourceDelta;

    public ChangedInSourceAndTargetConflict(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
    {
      this.targetDelta = targetDelta;
      this.sourceDelta = sourceDelta;
    }

    @Override
    public CDOID getID()
    {
      return targetDelta.getID();
    }

    public CDORevisionDelta getTargetDelta()
    {
      return targetDelta;
    }

    public CDORevisionDelta getSourceDelta()
    {
      return sourceDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInSourceAndTarget[target={0}, source={1}]", targetDelta, sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class ChangedInSourceAndDetachedInTargetConflict extends Conflict
  {
    private CDORevisionDelta sourceDelta;

    public ChangedInSourceAndDetachedInTargetConflict(CDORevisionDelta sourceDelta)
    {
      this.sourceDelta = sourceDelta;
    }

    @Override
    public CDOID getID()
    {
      return sourceDelta.getID();
    }

    public CDORevisionDelta getSourceDelta()
    {
      return sourceDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInSourceAndDetachedInTarget[source={0}]", sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class ChangedInTargetAndDetachedInSourceConflict extends Conflict
  {
    private CDORevisionDelta targetDelta;

    public ChangedInTargetAndDetachedInSourceConflict(CDORevisionDelta targetDelta)
    {
      this.targetDelta = targetDelta;
    }

    @Override
    public CDOID getID()
    {
      return targetDelta.getID();
    }

    public CDORevisionDelta getTargetDelta()
    {
      return targetDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInTargetAndDetachedInSource[target={0}]", targetDelta); //$NON-NLS-1$
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class PerFeature extends DefaultCDOMerger
  {
    public PerFeature()
    {
    }

    /**
     * @since 4.2
     */
    public PerFeature(ResolutionPreference resolutionPreference)
    {
      super(resolutionPreference);
    }

    @Override
    protected Object changedInTarget(CDORevisionDelta delta, CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision,
        CDORevision targetEndRevision)
    {
      if (resultBaseRevision == null)
      {
        return targetEndRevision;
      }

      return rebaseOneSidedRevisionDelta(delta, true, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
    }

    @Override
    protected Object changedInSource(CDORevisionDelta delta, CDORevision resultBaseRevision, CDORevision targetBaseRevision, CDORevision sourceBaseRevision,
        CDORevision sourceEndRevision)
    {
      if (resultBaseRevision == null)
      {
        return sourceEndRevision;
      }

      return rebaseOneSidedRevisionDelta(delta, false, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
    }

    /**
     * Re-encodes only the features actually changed by one side so features absent from that causal history remain
     * unobserved instead of being synthesized from endpoint snapshot differences.
     */
    private CDORevisionDelta rebaseOneSidedRevisionDelta(CDORevisionDelta delta, boolean targetSide, CDORevision resultBaseRevision,
        CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
    {
      InternalCDORevisionDelta result = new CDORevisionDeltaImpl(resultBaseRevision);

      for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
      {
        CDOFeatureDelta rebased = targetSide ? changedInTarget(featureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision)
            : changedInSource(featureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision);

        if (rebased != null)
        {
          result.addFeatureDelta(rebased, null);
        }
      }

      return result.isEmpty() ? null : result;
    }

    @Override
    protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
    {
      return changedInSourceAndTarget(targetDelta, sourceDelta, null);
    }

    @Override
    protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta, CDORevision ancestorRevision)
    {
      return changedInSourceAndTarget(targetDelta, sourceDelta, ancestorRevision, ancestorRevision, ancestorRevision, false);
    }

    @Override
    protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta, CDORevision resultBaseRevision,
        CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
    {
      return changedInSourceAndTarget(targetDelta, sourceDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision, true);
    }

    /**
     * Merges per-feature revision deltas while rebasing one-sided feature histories only for the asymmetric branch/remerge
     * path. Ordinary common-base conflict resolution retains its established delta representation and dispatch behavior.
     */
    private Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta, CDORevision resultBaseRevision,
        CDORevision targetBaseRevision, CDORevision sourceBaseRevision, boolean rebaseOneSidedFeatures)
    {
      InternalCDORevisionDelta result = rebaseOneSidedFeatures && resultBaseRevision != null ? new CDORevisionDeltaImpl(resultBaseRevision)
          : new CDORevisionDeltaImpl(targetDelta, false);
      ChangedInSourceAndTargetConflict conflict = null;

      Map<EStructuralFeature, CDOFeatureDelta> targetMap = ((InternalCDORevisionDelta)targetDelta).getFeatureDeltaMap();
      Map<EStructuralFeature, CDOFeatureDelta> sourceMap = ((InternalCDORevisionDelta)sourceDelta).getFeatureDeltaMap();

      for (CDOFeatureDelta targetFeatureDelta : targetMap.values())
      {
        EStructuralFeature feature = targetFeatureDelta.getFeature();
        CDOFeatureDelta sourceFeatureDelta = sourceMap.get(feature);

        if (sourceFeatureDelta == null)
        {
          CDOFeatureDelta featureDelta = rebaseOneSidedFeatures
              ? changedInTarget(targetFeatureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision)
              : changedInTarget(targetFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta, null);
          }
        }
        else
        {
          // A common-base merge deliberately keeps the established feature-hook contract. Only an asymmetric remerge
          // needs the three distinct bases for result-relative rebasing and unobserved-occurrence handling.
          CDOFeatureDelta featureDelta = rebaseOneSidedFeatures
              ? changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision)
              : changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta, resultBaseRevision);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta, null);
          }
          else
          {
            if (conflict == null)
            {
              ResolutionPreference resolutionPreference = getResolutionPreference();
              switch (resolutionPreference)
              {
              case SOURCE_OVER_TARGET:
                // TODO: implement DefaultCDOMerger.PerFeature.changedInSourceAndTarget(targetDelta, sourceDelta)
                throw new UnsupportedOperationException();

              case TARGET_OVER_SOURCE:
                // TODO: implement DefaultCDOMerger.PerFeature.changedInSourceAndTarget(targetDelta, sourceDelta)
                throw new UnsupportedOperationException();

              case NONE:
                conflict = new ChangedInSourceAndTargetConflict(new CDORevisionDeltaImpl(targetDelta, false), new CDORevisionDeltaImpl(sourceDelta, false));
                break;

              default:
                throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
              }
            }

            ((InternalCDORevisionDelta)conflict.getTargetDelta()).addFeatureDelta(targetFeatureDelta, null);
            ((InternalCDORevisionDelta)conflict.getSourceDelta()).addFeatureDelta(sourceFeatureDelta, null);
          }
        }
      }

      for (CDOFeatureDelta sourceFeatureDelta : sourceMap.values())
      {
        EStructuralFeature feature = sourceFeatureDelta.getFeature();
        CDOFeatureDelta targetFeatureDelta = targetMap.get(feature);

        if (targetFeatureDelta == null)
        {
          CDOFeatureDelta featureDelta = rebaseOneSidedFeatures
              ? changedInSource(sourceFeatureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision)
              : changedInSource(sourceFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta, null);
          }
        }
      }

      if (result.isEmpty())
      {
        return conflict;
      }

      if (conflict != null)
      {
        return Pair.create(result, conflict);
      }

      return result;
    }

    /**
     * @return the result feature delta, or <code>null</code> to ignore the change.
     */
    protected CDOFeatureDelta changedInTarget(CDOFeatureDelta featureDelta)
    {
      return featureDelta;
    }

    /**
     * @return the result feature delta, or <code>null</code> to ignore the change.
     */
    protected CDOFeatureDelta changedInSource(CDOFeatureDelta featureDelta)
    {
      return featureDelta;
    }

    /**
     * Re-encodes a target-only feature change relative to the actual result base. Single-valued deltas are absolute and
     * therefore already base-independent; many-valued subclasses override this hook.
     * @since 4.30
     */
    protected CDOFeatureDelta changedInTarget(CDOFeatureDelta featureDelta, CDORevision resultBaseRevision, CDORevision targetBaseRevision,
        CDORevision sourceBaseRevision)
    {
      return changedInTarget(featureDelta);
    }

    /**
     * Re-encodes a source-only feature change relative to the actual result base. Single-valued deltas are absolute and
     * therefore already base-independent; many-valued subclasses override this hook.
     * @since 4.30
     */
    protected CDOFeatureDelta changedInSource(CDOFeatureDelta featureDelta, CDORevision resultBaseRevision, CDORevision targetBaseRevision,
        CDORevision sourceBaseRevision)
    {
      return changedInSource(featureDelta);
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta)
    {
      EStructuralFeature feature = targetFeatureDelta.getFeature();
      if (feature.isMany())
      {
        return changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      return changedInSourceAndTargetSingleValued(feature, targetFeatureDelta, sourceFeatureDelta);
    }

    /**
     * Resolves concurrent changes of one feature with an optional full common-ancestor revision. The default delegates
     * to the established delta-only hook so existing extenders retain their behavior.
     *
     * @since 4.30
     */
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta, CDORevision ancestorRevision)
    {
      return changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta);
    }

    /**
     * Resolves concurrent feature changes with explicit result/target/source base revisions. The default preserves the
     * existing common-ancestor hook for extenders that do not need asymmetric list visibility.
     *
     * @since 4.30
     */
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta, CDORevision resultBaseRevision,
        CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
    {
      return changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta, resultBaseRevision);
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta)
    {
      return null;
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTargetSingleValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta)
    {
      if (targetFeatureDelta.isStructurallyEqual(sourceFeatureDelta))
      {
        return targetFeatureDelta;
      }

      return null;
    }

    /**
     * Per-feature merger whose many-valued path performs a semantic three-way list merge.
     * <p>
     * Incoming numeric CDO histories are decoded independently against result-base occurrence identities plus each
     * side's own causal base visibility. ADD
     * creates a side-local occurrence, SET creates a replacement in the addressed lineage, MOVE preserves occurrence
     * identity and creates a historical placement landmark, and REMOVE/CLEAR/UNSET address the occurrences visible at
     * that point in the causal history. Historical positions and their partial order remain available after an
     * occurrence moves or disappears.
     * <p>
     * Numeric indexes are intentionally confined to decoding and final delta encoding. Normalization, dimension-wise
     * presence/content/placement merging, uniqueness resolution, and deterministic linearization reason about stable
     * occurrences and ordering constraints. A fresh engine contains all mutable state for each invocation, so merger
     * instances remain safely reusable under {@link DefaultCDOMerger#merge(CDOChangeSet, CDOChangeSet)} synchronization.
     *
     * @author Eike Stepper
     */
    public static class ManyValued extends PerFeature
    {
      /**
       * Policy for genuine conflicts on one occurrence lineage.
       */
      private final OccurrenceConflictPolicy occurrenceConflictPolicy;

      /**
       * Policy for genuinely underdetermined topological choices.
       */
      private final OrderingPolicy orderingPolicy;

      /**
       * Policy enforcing hard feature uniqueness.
       */
      private final DuplicateResolutionPolicy duplicateResolutionPolicy;

      /**
       * Policy defining CLEAR's concurrent semantic scope.
       */
      private final ClearSemanticPolicy clearSemanticPolicy;

      /**
       * Policy defining incompatible UNSET behavior.
       */
      private final UnsetSemanticPolicy unsetSemanticPolicy;

      /**
       * Creates a merger with the documented semantic defaults.
       */
      public ManyValued()
      {
        this(ResolutionPreference.NONE);
      }

      /**
       * @since 4.2
       */
      public ManyValued(ResolutionPreference resolutionPreference)
      {
        this(resolutionPreference, policyFor(resolutionPreference), OrderingPolicy.STABLE, DuplicateResolutionPolicy.COALESCE,
            ClearSemanticPolicy.OBSERVED_REMOVE, UnsetSemanticPolicy.FAIL_ON_CONCURRENT_MUTATION);
      }

      /**
       * Creates an immutable semantic-policy profile for an extender without adding mutable global configuration.
       * Policy outputs remain centrally validated by the engine and cannot relax occurrence, ordering, uniqueness, or
       * set-state invariants.
       *
       * @since 4.30
       */
      protected ManyValued(ResolutionPreference resolutionPreference, OccurrenceConflictPolicy occurrenceConflictPolicy, OrderingPolicy orderingPolicy,
          DuplicateResolutionPolicy duplicateResolutionPolicy, ClearSemanticPolicy clearSemanticPolicy, UnsetSemanticPolicy unsetSemanticPolicy)
      {
        super(resolutionPreference);

        CheckUtil.checkArg(occurrenceConflictPolicy, "occurrenceConflictPolicy"); //$NON-NLS-1$
        this.occurrenceConflictPolicy = occurrenceConflictPolicy;

        CheckUtil.checkArg(orderingPolicy, "orderingPolicy"); //$NON-NLS-1$
        this.orderingPolicy = orderingPolicy;

        CheckUtil.checkArg(duplicateResolutionPolicy, "duplicateResolutionPolicy"); //$NON-NLS-1$
        this.duplicateResolutionPolicy = duplicateResolutionPolicy;

        CheckUtil.checkArg(clearSemanticPolicy, "clearSemanticPolicy"); //$NON-NLS-1$
        this.clearSemanticPolicy = clearSemanticPolicy;

        CheckUtil.checkArg(unsetSemanticPolicy, "unsetSemanticPolicy"); //$NON-NLS-1$
        this.unsetSemanticPolicy = unsetSemanticPolicy;
      }

      /**
       * @since 4.2
       */
      protected boolean treatAsUnique(EStructuralFeature feature)
      {
        return feature.isUnique();
      }

      @Override
      protected CDOFeatureDelta changedInTarget(CDOFeatureDelta featureDelta, CDORevision resultBaseRevision, CDORevision targetBaseRevision,
          CDORevision sourceBaseRevision)
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta sourceFeatureDelta = createEmptyListDelta(featureDelta.getFeature(), sourceBaseRevision);
          return mergeSemanticList((CDOListFeatureDelta)featureDelta, sourceFeatureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
        }

        return super.changedInTarget(featureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
      }

      @Override
      protected CDOFeatureDelta changedInSource(CDOFeatureDelta featureDelta, CDORevision resultBaseRevision, CDORevision targetBaseRevision,
          CDORevision sourceBaseRevision)
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta targetFeatureDelta = createEmptyListDelta(featureDelta.getFeature(), targetBaseRevision);
          return mergeSemanticList(targetFeatureDelta, (CDOListFeatureDelta)featureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
        }

        return super.changedInSource(featureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
      }

      /**
       * Resolves two concurrent list histories that start from one common ancestor.
       * <p>
       * The ordinary {@link DefaultCDOMerger#merge(CDOChangeSet, CDOChangeSet)} path reaches this overload. It must
       * therefore enter the semantic list merger with the same revision in all three base roles. The five-argument
       * overload below is deliberately reserved for the branch/remerge path, where the two histories can have
       * different causal bases and the result is encoded relative to a third revision.
       */
      @Override
      protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta, CDORevision ancestorRevision)
      {
        if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
        {
          // A normal conflict resolution has one shared origin, so all semantic coordinate systems use that revision.
          return mergeSemanticList((CDOListFeatureDelta)targetFeatureDelta, (CDOListFeatureDelta)sourceFeatureDelta, ancestorRevision, ancestorRevision,
              ancestorRevision);
        }

        return super.changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta, ancestorRevision);
      }

      @Override
      protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta, CDORevision resultBaseRevision,
          CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
      {
        if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
        {
          return mergeSemanticList((CDOListFeatureDelta)targetFeatureDelta, (CDOListFeatureDelta)sourceFeatureDelta, resultBaseRevision, targetBaseRevision,
              sourceBaseRevision);
        }

        return super.changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta, resultBaseRevision, targetBaseRevision, sourceBaseRevision);
      }

      /**
       * Executes semantic list merging for concurrent and one-sided list histories alike.
       */
      private CDOFeatureDelta mergeSemanticList(CDOListFeatureDelta targetFeatureDelta, CDOListFeatureDelta sourceFeatureDelta, CDORevision resultBaseRevision,
          CDORevision targetBaseRevision, CDORevision sourceBaseRevision)
      {
        EStructuralFeature feature = targetFeatureDelta.getFeature();
        SemanticCDOListMerger engine = new SemanticCDOListMerger(feature, targetFeatureDelta, sourceFeatureDelta, resultBaseRevision, targetBaseRevision,
            sourceBaseRevision, new SemanticPolicies(this));
        return engine.merge();
      }

      /**
       * Creates an explicit no-op history in the coordinate system of one causal side base.
       */
      private static CDOListFeatureDelta createEmptyListDelta(EStructuralFeature feature, CDORevision baseRevision)
      {
        int originSize = 0;
        if (baseRevision != null)
        {
          CDOList list = ((InternalCDORevision)baseRevision).getListOrNull(feature);
          originSize = list == null ? 0 : list.size();
        }

        return new CDOListFeatureDeltaImpl(feature, originSize);
      }

      /**
       * Maps the established source/target preference onto the focused occurrence domain.
       */
      private static OccurrenceConflictPolicy policyFor(ResolutionPreference resolutionPreference)
      {
        if (resolutionPreference == ResolutionPreference.SOURCE_OVER_TARGET)
        {
          return OccurrenceConflictPolicy.PREFER_SOURCE;
        }

        if (resolutionPreference == ResolutionPreference.TARGET_OVER_SOURCE)
        {
          return OccurrenceConflictPolicy.PREFER_TARGET;
        }

        return OccurrenceConflictPolicy.DEFAULT;
      }

      /**
       * Immutable adapter from the established merger preferences to the semantic engine's focused policy domains.
       * The default combines every compatible intent, gives replacement content precedence over removal of the old
       * occurrence, gives removal precedence over placement-only mutation, uses target/local intent for genuinely
       * incompatible concurrent replacement or placement, coalesces uniqueness collisions, applies observed-remove
       * CLEAR semantics, and fails on incompatible UNSET.
       *
       * @author Eike Stepper
       */
      private static final class SemanticPolicies implements SemanticCDOListMerger.Policies
      {
        /**
         * Owning merger whose immutable construction preferences guide genuine conflict choices.
         */
        private final ManyValued merger;

        /**
         * Creates an immutable policy adapter for one merger instance.
         */
        public SemanticPolicies(ManyValued merger)
        {
          this.merger = merger;
        }

        /**
         * Resolves only genuine same-lineage semantic conflicts.
         */
        @Override
        public SemanticCDOListMerger.OccurrenceResolution resolveOccurrence(SemanticCDOListMerger.OccurrenceConflictContext context)
        {
          OccurrenceConflictPolicy policy = merger.occurrenceConflictPolicy;
          if (policy == OccurrenceConflictPolicy.FAIL)
          {
            return SemanticCDOListMerger.OccurrenceResolution.FAIL;
          }

          if (context.kind == SemanticCDOListMerger.OccurrenceConflictKind.REMOVE_VS_MUTATION)
          {
            if (policy == OccurrenceConflictPolicy.PREFER_SOURCE)
            {
              return context.source.present ? SemanticCDOListMerger.OccurrenceResolution.KEEP_MUTATION : SemanticCDOListMerger.OccurrenceResolution.REMOVE;
            }

            if (policy == OccurrenceConflictPolicy.PREFER_TARGET)
            {
              return context.target.present ? SemanticCDOListMerger.OccurrenceResolution.KEEP_MUTATION : SemanticCDOListMerger.OccurrenceResolution.REMOVE;
            }

            SemanticCDOListMerger.SideLineage present = context.source.present ? context.source : context.target;

            // SET replaces the removed occurrence and therefore survives; a MOVE of the removed occurrence does not.
            return present.contentChanged ? SemanticCDOListMerger.OccurrenceResolution.KEEP_MUTATION : SemanticCDOListMerger.OccurrenceResolution.REMOVE;
          }

          if (policy == OccurrenceConflictPolicy.PREFER_SOURCE)
          {
            return SemanticCDOListMerger.OccurrenceResolution.SOURCE;
          }

          // Target is the local/current side in the conflict-resolver path and the transaction branch in branch merge.
          return SemanticCDOListMerger.OccurrenceResolution.TARGET;
        }

        /**
         * Chooses the deterministic stable candidate while preserving hard partial-order constraints.
         */
        @Override
        public SemanticCDOListMerger.MergedOccurrence chooseOrdering(SemanticCDOListMerger.OrderingContext context)
        {
          if (merger.orderingPolicy == OrderingPolicy.FAIL_ON_AMBIGUITY)
          {
            return null;
          }

          SemanticCDOListMerger.MergedOccurrence result = null;
          for (SemanticCDOListMerger.MergedOccurrence candidate : context.eligible)
          {
            if (result == null || compareStable(candidate, result, merger.orderingPolicy) < 0)
            {
              result = candidate;
            }
          }

          return result;
        }

        /**
         * Coalesces equal-valued occurrences while preserving compatible effective intents.
         */
        @Override
        public SemanticCDOListMerger.DuplicateResolution resolveDuplicate(SemanticCDOListMerger.DuplicateContext context)
        {
          switch (merger.duplicateResolutionPolicy)
          {
          case COALESCE:
            return SemanticCDOListMerger.DuplicateResolution.COALESCE;

          case PREFER_SOURCE:
            return preferDuplicate(context, SemanticCDOListMerger.Origin.SOURCE);

          case PREFER_TARGET:
            return preferDuplicate(context, SemanticCDOListMerger.Origin.TARGET);

          case FAIL:
            return SemanticCDOListMerger.DuplicateResolution.FAIL;

          default:
            throw new IllegalStateException("Unknown duplicate policy " + merger.duplicateResolutionPolicy);
          }
        }

        /**
         * Removes exactly the occurrences observed by CLEAR.
         */
        @Override
        public SemanticCDOListMerger.ClearResolution resolveClear(SemanticCDOListMerger.ClearContext context)
        {
          return clearResolution(merger.clearSemanticPolicy);
        }

        /**
         * Returns the default observed-remove CLEAR mode.
         */
        @Override
        public SemanticCDOListMerger.ClearResolution getClearMode()
        {
          return clearResolution(merger.clearSemanticPolicy);
        }

        /**
         * Rejects an effective UNSET that cannot coexist with effective concurrent mutation.
         */
        @Override
        public SemanticCDOListMerger.UnsetResolution resolveUnset(SemanticCDOListMerger.UnsetContext context)
        {
          switch (merger.unsetSemanticPolicy)
          {
          case FAIL_ON_CONCURRENT_MUTATION:
            return SemanticCDOListMerger.UnsetResolution.FAIL;

          case UNSET_WINS:
            return SemanticCDOListMerger.UnsetResolution.UNSET_WINS;

          case MERGE_AS_CLEAR:
            return SemanticCDOListMerger.UnsetResolution.MERGE_AS_CLEAR;

          default:
            throw new IllegalStateException("Unknown UNSET policy " + merger.unsetSemanticPolicy);
          }
        }

        /**
         * Applies the stable source-before-target/creation-order ranking only where the DAG leaves order unknown.
         */
        private static int compareStable(SemanticCDOListMerger.MergedOccurrence first, SemanticCDOListMerger.MergedOccurrence second, OrderingPolicy policy)
        {
          boolean firstFreshAddition = isFreshAddition(first);
          boolean secondFreshAddition = isFreshAddition(second);
          if (firstFreshAddition != secondFreshAddition)
          {
            // A result-base or side-base occurrence is an established historical landmark. A fresh ADD that merely
            // falls into an unobserved gap must not displace it solely because its newly allocated Position has a
            // smaller ordinal.
            return firstFreshAddition ? 1 : -1;
          }

          // Source/target preference only resolves a genuine tie between two fresh concurrent ADD occurrences.
          if (firstFreshAddition)
          {
            int firstRank = originRank(first.origin, policy);
            int secondRank = originRank(second.origin, policy);
            if (first.origin != SemanticCDOListMerger.Origin.ANCESTOR && second.origin != SemanticCDOListMerger.Origin.ANCESTOR && firstRank != secondRank)
            {
              return Integer.compare(firstRank, secondRank);
            }
          }

          int ordinal = Integer.compare(first.position.ordinal, second.position.ordinal);
          if (ordinal != 0)
          {
            return ordinal;
          }

          int firstRank = originRank(first.origin, policy);
          int secondRank = originRank(second.origin, policy);
          if (firstRank != secondRank)
          {
            return Integer.compare(firstRank, secondRank);
          }

          return Integer.compare(first.occurrence.ordinal, second.occurrence.ordinal);
        }

        /**
         * Returns whether the occurrence was created by a decoded ADD operation, rather than being established in
         * either causal base. Replacements deliberately do not count as fresh placement additions.
         */
        private static boolean isFreshAddition(SemanticCDOListMerger.MergedOccurrence occurrence)
        {
          return occurrence.lineage != null && occurrence.lineage.addCreated;
        }

        /**
         * Returns a deterministic semantic origin rank independent of hash iteration order.
         */
        private static int originRank(SemanticCDOListMerger.Origin origin, OrderingPolicy policy)
        {
          if (policy == OrderingPolicy.PREFER_SOURCE)
          {
            return origin == SemanticCDOListMerger.Origin.SOURCE ? 0
                : origin == SemanticCDOListMerger.Origin.ANCESTOR ? 1 : origin == SemanticCDOListMerger.Origin.TARGET ? 2 : 3;
          }

          if (policy == OrderingPolicy.PREFER_TARGET)
          {
            return origin == SemanticCDOListMerger.Origin.TARGET ? 0
                : origin == SemanticCDOListMerger.Origin.ANCESTOR ? 1 : origin == SemanticCDOListMerger.Origin.SOURCE ? 2 : 3;
          }

          switch (origin)
          {
          case ANCESTOR:
            return 0;

          case SOURCE:
            return 1;

          case TARGET:
            return 2;

          case MERGED:
            return 3;

          default:
            throw new IllegalStateException("Unknown semantic origin " + origin);
          }
        }

        /**
         * Selects a side-origin duplicate when present and otherwise retains deterministic coalescing.
         */
        private static SemanticCDOListMerger.DuplicateResolution preferDuplicate(SemanticCDOListMerger.DuplicateContext context,
            SemanticCDOListMerger.Origin preferred)
        {
          if (context.first.origin == preferred)
          {
            return SemanticCDOListMerger.DuplicateResolution.FIRST;
          }

          if (context.second.origin == preferred)
          {
            return SemanticCDOListMerger.DuplicateResolution.SECOND;
          }

          return SemanticCDOListMerger.DuplicateResolution.COALESCE;
        }

        /**
         * Maps the protected SPI policy to the engine's validated internal resolution.
         */
        private static SemanticCDOListMerger.ClearResolution clearResolution(ClearSemanticPolicy policy)
        {
          switch (policy)
          {
          case OBSERVED_REMOVE:
            return SemanticCDOListMerger.ClearResolution.OBSERVED_REMOVE;

          case CLEAR_WINS:
            return SemanticCDOListMerger.ClearResolution.CLEAR_WINS;

          case FAIL_ON_CONCURRENT_MUTATION:
            return SemanticCDOListMerger.ClearResolution.FAIL;

          default:
            throw new IllegalStateException("Unknown CLEAR policy " + policy);
          }
        }
      }

      /**
       * Immutable policy for genuinely incompatible changes to one ancestor occurrence lineage.
       *
       * @author Eike Stepper
       * @since 4.30
       */
      public enum OccurrenceConflictPolicy
      {
        /**
         * Keep replacement content against removal, remove placement-only mutations, and prefer target otherwise.
         */
        DEFAULT,

        /**
         * Select source for genuine replacement/placement conflicts and source's presence choice against removal.
         */
        PREFER_SOURCE,

        /**
         * Select target for genuine replacement/placement conflicts and target's presence choice against removal.
         */
        PREFER_TARGET,

        /**
         * Report every genuine incompatible occurrence change as a merge conflict.
         */
        FAIL
      }

      /**
       * Immutable policy used only when the final position DAG has multiple simultaneously eligible occurrences.
       * Hard DAG constraints are always applied first. STABLE keeps established result-base and side-base occurrences
       * ahead of genuinely ADD-created occurrences when their relation is unknown; source/target preference is only a
       * tie-break between fresh concurrent additions. Numeric ordinals are deterministic fallbacks, never causal facts.
       *
       * @author Eike Stepper
       * @since 4.30
       */
      public enum OrderingPolicy
      {
        /**
         * Preserve historical stability, then deterministic fresh-addition preference and ordinals.
         */
        STABLE,

        /**
         * Prefer a source-origin candidate at an otherwise unconstrained choice point.
         */
        PREFER_SOURCE,

        /**
         * Prefer a target-origin candidate at an otherwise unconstrained choice point.
         */
        PREFER_TARGET,

        /**
         * Report genuinely underdetermined ordering instead of selecting a linear extension.
         */
        FAIL_ON_AMBIGUITY
      }

      /**
       * Immutable policy for hard uniqueness collisions between distinct surviving occurrences.
       *
       * @author Eike Stepper
       * @since 4.30
       */
      public enum DuplicateResolutionPolicy
      {
        /**
         * Coalesce values while combining every compatible effective semantic intent.
         */
        COALESCE,

        /**
         * Prefer a source-origin occurrence when one is available in the duplicate class.
         */
        PREFER_SOURCE,

        /**
         * Prefer a target-origin occurrence when one is available in the duplicate class.
         */
        PREFER_TARGET,

        /**
         * Report the uniqueness collision as a merge conflict.
         */
        FAIL
      }

      /**
       * Immutable policy for the semantic scope of an effective CLEAR operation.
       *
       * @author Eike Stepper
       * @since 4.30
       */
      public enum ClearSemanticPolicy
      {
        /**
         * Remove exactly occurrences observed by CLEAR and retain unobserved concurrent additions.
         */
        OBSERVED_REMOVE,

        /**
         * Remove all merged contents, including concurrent occurrences the clearing side could not observe.
         */
        CLEAR_WINS,

        /**
         * Report an observed occurrence's effective concurrent mutation as a merge conflict.
         */
        FAIL_ON_CONCURRENT_MUTATION
      }

      /**
       * Immutable policy for an effective UNSET that cannot coexist with concurrent effective mutation.
       *
       * @author Eike Stepper
       * @since 4.30
       */
      public enum UnsetSemanticPolicy
      {
        /**
         * Report the incompatible set-state/content intents as a merge conflict.
         */
        FAIL_ON_CONCURRENT_MUTATION,

        /**
         * Select UNSET and therefore remove every merged occurrence.
         */
        UNSET_WINS,

        /**
         * Apply observed-remove content semantics and return SET if concurrent content survives.
         */
        MERGE_AS_CLEAR
      }
    }

    /**
     * Compatibility name for the former virtual-element implementation.
     * <p>
     * Normal merger dispatch is inherited from {@link ManyValued} and therefore uses the semantic occurrence/position
     * engine. The legacy protected implementation remains source-compatible during the SPI transition but is no
     * longer selected by the merger call path.
     *
     * @author Eike Stepper
     * @since 4.6
     * @deprecated Instantiate {@link ManyValued}. The old numeric/offset implementation is not correctness-preserving.
     */
    @Deprecated
    public static class ManyValuedOld extends ManyValued
    {
      @Deprecated
      public ManyValuedOld()
      {
      }

      /**
       * @since 4.2
       */
      @Deprecated
      public ManyValuedOld(ResolutionPreference resolutionPreference)
      {
        super(resolutionPreference);
      }

      /**
       * @since 4.2
       */
      @Deprecated
      @Override
      protected boolean treatAsUnique(EStructuralFeature feature)
      {
        return feature.isUnique();
      }

      @Deprecated
      @Override
      protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
          CDOFeatureDelta sourceFeatureDelta)
      {
        if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
        {
          // Initialize work lists with virtual elements
          int originSize = ((CDOListFeatureDelta)sourceFeatureDelta.copy()).getOriginSize();
          BasicEList<Element> ancestorList = new BasicEList<>(originSize);
          PerSide<BasicEList<Element>> listPerSide = new PerSide<>();

          initWorkLists(originSize, ancestorList, listPerSide);

          // Apply list changes to source and target work lists
          PerSide<List<CDOFeatureDelta>> changesPerSide = new PerSide<>(copyListChanges(sourceFeatureDelta), copyListChanges(targetFeatureDelta));
          Map<Object, List<Element>> additions = new HashMap<>();
          Map<CDOFeatureDelta, Element> allElements = new HashMap<>();

          applyChangesToWorkList(Side.SOURCE, listPerSide, changesPerSide, allElements, additions);
          applyChangesToWorkList(Side.TARGET, listPerSide, changesPerSide, allElements, additions);

          // Pick changes from source and target sides into the merge result
          CDOListFeatureDelta result = new CDOListFeatureDeltaImpl(feature, originSize);
          List<CDOFeatureDelta> resultChanges = result.getListChanges();

          pickChangesIntoResult(Side.SOURCE, feature, ancestorList, changesPerSide, allElements, additions, resultChanges);
          pickChangesIntoResult(Side.TARGET, feature, ancestorList, changesPerSide, allElements, additions, resultChanges);

          return result;
        }

        return super.changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      private void initWorkLists(int originSize, BasicEList<Element> ancestorList, PerSide<BasicEList<Element>> listPerSide)
      {
        BasicEList<Element> sourceList = new BasicEList<>(originSize);
        BasicEList<Element> targetList = new BasicEList<>(originSize);

        for (int i = 0; i < originSize; i++)
        {
          Element element = new Element(i);
          ancestorList.add(element);
          sourceList.add(element);
          targetList.add(element);
        }

        listPerSide.set(Side.SOURCE, sourceList);
        listPerSide.set(Side.TARGET, targetList);
      }

      private List<CDOFeatureDelta> copyListChanges(CDOFeatureDelta featureDelta)
      {
        CDOListFeatureDelta listFeatureDelta = (CDOListFeatureDelta)featureDelta.copy();
        List<CDOFeatureDelta> copy = listFeatureDelta.getListChanges();

        if (!copy.isEmpty())
        {
          CDOFeatureDelta.Type firstType = copy.get(0).getType();
          if (firstType == Type.CLEAR || firstType == Type.UNSET)
          {
            copy.remove(0);

            List<CDOFeatureDelta> expandedDeltas = expandClearDelta(listFeatureDelta);
            copy.addAll(0, expandedDeltas);
          }
        }

        return copy;
      }

      private List<CDOFeatureDelta> expandClearDelta(CDOListFeatureDelta listFeatureDelta)
      {
        EStructuralFeature feature = listFeatureDelta.getFeature();
        int originSize = listFeatureDelta.getOriginSize();
        List<CDOFeatureDelta> expandedDeltas = new ArrayList<>(originSize);

        for (int i = 0; i < originSize; i++)
        {
          expandedDeltas.add(new CDORemoveFeatureDeltaImpl(feature, 0));
        }

        return expandedDeltas;
      }

      private void applyChangesToWorkList(Side side, PerSide<BasicEList<Element>> listPerSide, PerSide<List<CDOFeatureDelta>> changesPerSide,
          Map<CDOFeatureDelta, Element> allElements, Map<Object, List<Element>> additions)
      {
        BasicEList<Element> list = listPerSide.get(side);
        List<CDOFeatureDelta> changes = changesPerSide.get(side);
        for (CDOFeatureDelta change : changes)
        {
          Type changeType = change.getType();
          switch (changeType)
          {
          case ADD:
          {
            CDOAddFeatureDelta addChange = (CDOAddFeatureDelta)change;

            int index = addChange.getIndex();
            if (index > list.size())
            {
              index = list.size();
            }

            Element element = new Element(-1);
            element.set(side, addChange);
            allElements.put(addChange, element);

            list.add(index, element);
            rememberAddition(addChange.getValue(), element, additions);
            break;
          }

          case REMOVE:
          {
            CDORemoveFeatureDelta removeChange = (CDORemoveFeatureDelta)change;

            Element element = list.remove(removeChange.getIndex());
            element.set(side, removeChange);
            allElements.put(removeChange, element);
            break;
          }

          case SET:
          {
            CDOSetFeatureDelta setChange = (CDOSetFeatureDelta)change;

            Element newElement = new Element(-1);
            newElement.set(side, setChange);
            rememberAddition(setChange.getValue(), newElement, additions);

            Element oldElement = list.set(setChange.getIndex(), newElement);
            oldElement.set(side, setChange);
            allElements.put(setChange, oldElement);
            break;
          }

          case MOVE:
          {
            CDOMoveFeatureDelta moveChange = (CDOMoveFeatureDelta)change;

            Element element = list.move(moveChange.getNewPosition(), moveChange.getOldPosition());
            element.set(side, moveChange);
            allElements.put(moveChange, element);
            break;
          }

          case CLEAR:
          case UNSET:
            // These deltas should have been replaced by multiple REMOVE deltas in copyListChanges()
            throw new IllegalStateException("Unhandled change type: " + changeType);

          default:
            throw new IllegalStateException("Illegal change type: " + changeType);
          }
        }
      }

      private void rememberAddition(Object value, Element element, Map<Object, List<Element>> additions)
      {
        List<Element> additionsList = additions.get(value);
        if (additionsList == null)
        {
          additionsList = new ArrayList<>(1);
          additions.put(value, additionsList);
        }

        additionsList.add(element);
      }

      private void pickChangesIntoResult(Side side, EStructuralFeature feature, BasicEList<Element> ancestorList, PerSide<List<CDOFeatureDelta>> changesPerSide,
          Map<CDOFeatureDelta, Element> allElements, Map<Object, List<Element>> additions, List<CDOFeatureDelta> result)
      {
        List<CDOFeatureDelta> changes = changesPerSide.get(side);
        for (CDOFeatureDelta change : changes)
        {
          Type changeType = change.getType();
          switch (changeType)
          {
          case ADD:
          {
            CDOAddFeatureDeltaImpl addChange = (CDOAddFeatureDeltaImpl)change;
            result.add(addChange);

            int sideIndex = addChange.getIndex();
            int ancestorIndex = sideIndex;

            int ancestorEnd = ancestorList.size();
            if (ancestorIndex > ancestorEnd)
            {
              // TODO Better way to adjust ancestor indexes?
              ancestorIndex = ancestorEnd;
              addChange.setIndex(ancestorIndex);
            }

            Element newElement = allElements.get(addChange);
            ancestorList.add(ancestorIndex, newElement);

            if (treatAsUnique(feature))
            {
              // Detect and remove corresponding AddDeltas from the other side
              Object value = addChange.getValue();
              List<Element> elementsToAdd = additions.get(value);
              if (elementsToAdd != null)
              {
                for (Element element : elementsToAdd)
                {
                  CDOAddFeatureDelta otherAdd = (CDOAddFeatureDelta)element.get(other(side));
                  if (otherAdd != null)
                  {
                    element.set(other(side), null);

                    // Not taking an AddDelta has the same effect on indexes as a removal of the element
                    List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                    int otherIndex = otherAdd.getIndex();
                    adjustAfterRemoval(otherChanges, otherIndex, addChange);
                  }
                }
              }
            }

            break;
          }

          case REMOVE:
          {
            CDORemoveFeatureDeltaImpl removeChange = (CDORemoveFeatureDeltaImpl)change;
            result.add(removeChange);

            Element removedElement = allElements.get(removeChange);
            int ancestorIndex = ancestorList.indexOf(removedElement);
            removeChange.setIndex(ancestorIndex);
            ancestorList.remove(ancestorIndex);

            // Detect and remove a potential duplicate RemoveDelta from the other side
            CDOFeatureDelta otherChange = removedElement.get(other(side));
            if (otherChange != null)
            {
              Type otherChangeType = otherChange.getType();
              switch (otherChangeType)
              {
              case REMOVE:
              {
                CDORemoveFeatureDelta otherRemove = (CDORemoveFeatureDelta)otherChange;
                removedElement.set(other(side), null);

                // Not taking a RemoveDelta has the same effect on indexes as an addition of the element
                List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                int otherIndex = otherRemove.getIndex();
                adjustAfterAddition(otherChanges, otherIndex, otherRemove);
                break;
              }

              case MOVE:
              {
                CDOMoveFeatureDelta otherMove = (CDOMoveFeatureDelta)otherChange;
                removedElement.set(other(side), null);

                // Not taking a MoveDelta has the same effect on indexes as a reverse move of the element
                List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                int otherOldPosition = otherMove.getOldPosition();
                int otherNewPosition = otherMove.getNewPosition();
                adjustAfterMove(otherChanges, otherOldPosition, otherNewPosition, otherMove);
                break;
              }

              default:
                throw new IllegalStateException("Unexpected change type: " + otherChangeType);
              }
            }

            break;
          }

          case SET:
          {
            throw new IllegalStateException("Unhandled change type: " + changeType);
            // CDOSetFeatureDelta setChange = (CDOSetFeatureDelta)change;
            // break;
          }

          case MOVE:
          {
            CDOMoveFeatureDeltaImpl moveChange = (CDOMoveFeatureDeltaImpl)change;
            int sideOldPosition = moveChange.getOldPosition();
            int sideNewPosition = moveChange.getNewPosition();

            Element movedElement = allElements.get(moveChange);
            CDOFeatureDelta otherChange = movedElement.get(other(side));

            if (otherChange != null)
            {
              Type otherChangeType = otherChange.getType();
              switch (otherChangeType)
              {
              case REMOVE:
              {
                // Prioritize the RemoveDelta of the other side, delete the MoveDelta from this side
                adjustAfterMove(changes, sideOldPosition, sideNewPosition, moveChange);
                movedElement.set(side, null);
                return;
              }

              case MOVE:
              {
                CDOMoveFeatureDelta otherMove = (CDOMoveFeatureDelta)otherChange;
                movedElement.set(other(side), null);

                // Not taking a MoveDelta has the same effect on indexes as a reverse move of the element
                List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                int otherOldPosition = otherMove.getOldPosition();
                int otherNewPosition = otherMove.getNewPosition();
                adjustAfterMove(otherChanges, otherOldPosition, otherNewPosition, otherMove);
                movedElement.set(other(side), null);
                break;
              }

              default:
                throw new IllegalStateException("Unexpected change type: " + otherChangeType);
              }
            }

            int positionDelta = sideNewPosition - sideOldPosition;
            int ancestorOldPosition = ancestorList.indexOf(movedElement);
            int ancestorNewPosition = ancestorOldPosition + positionDelta;
            if (ancestorNewPosition < 0)
            {
              ancestorNewPosition = 0;
            }

            int ancestorEnd = ancestorList.size() - 1;
            if (ancestorNewPosition > ancestorEnd)
            {
              ancestorNewPosition = ancestorEnd;
            }

            moveChange.setOldPosition(ancestorOldPosition);
            moveChange.setNewPosition(ancestorNewPosition);
            result.add(moveChange);

            ancestorList.move(ancestorNewPosition, ancestorOldPosition);
            break;
          }

          case CLEAR:
          case UNSET:

          default:
            throw new IllegalStateException("Illegal change type: " + changeType);
          }
        }
      }

      private static void adjustAfterAddition(List<CDOFeatureDelta> list, int index, CDOFeatureDelta deltaToRemove)
      {
        for (Iterator<CDOFeatureDelta> it = list.iterator(); it.hasNext();)
        {
          CDOFeatureDelta delta = it.next();
          if (delta == deltaToRemove)
          {
            it.remove();
            continue;
          }

          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterAddition(index);
          }
        }
      }

      private static void adjustAfterRemoval(List<CDOFeatureDelta> list, int index, CDOFeatureDelta deltaToRemove)
      {
        for (Iterator<CDOFeatureDelta> it = list.iterator(); it.hasNext();)
        {
          CDOFeatureDelta delta = it.next();
          if (delta == deltaToRemove)
          {
            it.remove();
            continue;
          }

          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterRemoval(index);
          }
        }
      }

      private static void adjustAfterMove(List<CDOFeatureDelta> list, int oldPosition, int newPosition, CDOFeatureDelta deltaToRemove)
      {
        for (Iterator<CDOFeatureDelta> it = list.iterator(); it.hasNext();)
        {
          CDOFeatureDelta delta = it.next();
          if (delta == deltaToRemove)
          {
            it.remove();
            continue;
          }

          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterRemoval(oldPosition);
            withIndex.adjustAfterAddition(newPosition);
          }
        }
      }

      /**
       * @since 4.2
       */
      @Deprecated
      protected static Side other(Side side)
      {
        if (side == Side.SOURCE)
        {
          return Side.TARGET;
        }

        return Side.SOURCE;
      }

      /**
       * Enumerates the possible sides of a merge, i.e., {@link #SOURCE} and {@link #TARGET}.
       *
       * @author Eike Stepper
       * @since 4.2
       * @deprecated Instantiate {@link ManyValued}. The old numeric/offset implementation is not correctness-preserving.
       */
      @Deprecated
      public static enum Side
      {
        @Deprecated
        SOURCE,

        @Deprecated
        TARGET
      }

      /**
       * Holds data for the source and target sides.
       *
       * @author Eike Stepper
       * @since 4.2
       */
      @Deprecated
      public static class PerSide<T>
      {
        private T source;

        private T target;

        @Deprecated
        public PerSide()
        {
        }

        @Deprecated
        public PerSide(T source, T target)
        {
          this.source = source;
          this.target = target;
        }

        @Deprecated
        public final T get(Side side)
        {
          if (side == Side.SOURCE)
          {
            return source;
          }

          return target;
        }

        @Deprecated
        public final void set(Side side, T value)
        {
          if (side == Side.SOURCE)
          {
            source = value;
          }
          else
          {
            target = value;
          }
        }

        @Deprecated
        @Override
        public String toString()
        {
          return "source: " + source + "\ntarget: " + target;
        }
      }

      /**
       * A virtual list element to establish unique relations between ancestor, source and target sides.
       *
       * @author Eike Stepper
       * @since 4.2
       */
      @Deprecated
      public static final class Element extends PerSide<CDOFeatureDelta>
      {
        private final int ancestorIndex;

        @Deprecated
        public Element(int ancestorIndex)
        {
          this.ancestorIndex = ancestorIndex;
        }

        @Deprecated
        public int getAncestorIndex()
        {
          return ancestorIndex;
        }

        @Deprecated
        @Override
        public String toString()
        {
          return String.valueOf(ancestorIndex);
        }
      }
    }
  }
}
