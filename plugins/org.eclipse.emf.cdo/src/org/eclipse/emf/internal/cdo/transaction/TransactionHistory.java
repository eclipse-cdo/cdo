/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.om.OMPlatform;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Provides the internal history model of a CDO transaction.
 * <p>
 * A transaction history consists of ordered boundaries and their associated
 * segments. Segments retain the changes made at different transaction
 * positions, while the aggregation methods derive the transaction's current
 * effective state from that retained history. Consequently, a segment's
 * historical provenance must not be confused with the effective state exposed
 * by an aggregate result.
 * <p>
 * The history also supports invisible internal boundaries used by nested
 * transaction scopes. These boundaries participate in aggregation and
 * rollback, but they do not represent repository commits or repository epoch
 * changes.
 *
 * @author Eike Stepper
 */
public interface TransactionHistory
{
  public TransactionSegment getCurrentSegment();

  /**
   * Returns the active history boundary for internal diagnostics. The result
   * is intentionally not part of the transaction API.
   *
   * @return the current boundary.
   */
  public TransactionBoundary getCurrentBoundaryForDiagnostics();

  public int getCleanRevisionCountForDiagnostics();

  public int getLifecycleBeforeImageCountForDiagnostics();

  public int getActiveScopeDepthForDiagnostics(TransactionBoundary boundary);

  public void checkScopeInvariantsForDiagnostics();

  /**
   * Removes the revision delta and dirty-object entry for {@code id} from all
   * active segments of this history.
   * <p>
   * This operation is deliberately segment-wide. An object's effective dirty
   * state may be represented by entries in several retained segments, and
   * removing only the current entry could cause an older entry to reappear in
   * a later aggregate. Clearing every active segment keeps the effective
   * history consistent after an object leaves the dirty or new-object path.
   *
   * @param id the object identity whose revision delta and dirty entries are
   *          to be removed.
   */
  public void clearRevisionDeltaAndDirty(CDOID id);

  /**
   * Stores the revision delta and dirty-object entry for {@code id} in all
   * active segments of this history.
   * <p>
   * The segment-wide update mirrors
   * {@link #clearRevisionDeltaAndDirty(CDOID)}: effective dirty state must not
   * depend on which retained segment happens to be current when the update is
   * made.
   *
   * @param id the object identity to update.
   * @param delta the revision delta representing the object's effective
   *          modification.
   * @param object the dirty object instance associated with {@code id}.
   */
  public void setRevisionDeltaAndDirty(CDOID id, CDORevisionDelta delta, CDOObject object);

  /**
   * Aggregates new objects from the history ending at {@code boundary}.
   * <p>
   * {@code boundary} is the end point of the requested history range.
   * {@code includeEnd} determines whether the segment belonging to that end
   * point is included. The returned map is a newly created effective
   * aggregate; it is not a live map backed by any history segment. Its result
   * describes the effective new-object state after the selected segments have
   * been applied, whereas the retained segments themselves describe historical
   * provenance. Historical provenance is therefore not identical to the
   * effective state represented by this result.
   *
   * @param boundary the end point of the history range.
   * @param includeEnd whether to include the segment belonging to
   *          {@code boundary}.
   * @return a new effective aggregate of new objects.
   */
  public Map<CDOID, CDOObject> aggregateNewObjects(TransactionBoundary boundary, boolean includeEnd);

  /**
   * Aggregates dirty objects from the history ending at {@code boundary}.
   * <p>
   * {@code boundary} is the end point of the requested history range.
   * {@code includeEnd} determines whether the segment belonging to that end
   * point is included. The returned map is a newly created effective
   * aggregate, not a live map. It represents the effective dirty state after
   * applying the selected history, while the individual segments retain
   * historical provenance that must not be confused with that effective
   * state.
   *
   * @param boundary the end point of the history range.
   * @param includeEnd whether to include the segment belonging to
   *          {@code boundary}.
   * @return a new effective aggregate of dirty objects.
   */
  public Map<CDOID, CDOObject> aggregateDirtyObjects(TransactionBoundary boundary, boolean includeEnd);

  /**
   * Aggregates base revisions of new objects from the history ending at
   * {@code boundary}.
   * <p>
   * {@code boundary} is the end point of the requested history range, and
   * {@code includeEnd} controls whether its segment is included. The result
   * is a new effective aggregate and is not a live map. It describes the
   * effective base-new state, not the complete historical provenance held by
   * the individual segments.
   *
   * @param boundary the end point of the history range.
   * @param includeEnd whether to include the segment belonging to
   *          {@code boundary}.
   * @return a new effective aggregate of base revisions for new objects.
   */
  public Map<CDOID, CDORevision> aggregateBaseNewObjects(TransactionBoundary boundary, boolean includeEnd);

  /**
   * Aggregates detached objects from the history ending at {@code boundary}.
   * <p>
   * {@code boundary} is the end point of the requested history range, and
   * {@code includeEnd} controls whether its segment is included. The result
   * is a new effective aggregate and is not a live map. Effective detached
   * state is derived from the selected history and is distinct from the
   * historical provenance recorded in individual segments.
   *
   * @param boundary the end point of the history range.
   * @param includeEnd whether to include the segment belonging to
   *          {@code boundary}.
   * @return a new effective aggregate of detached objects.
   */
  public Map<CDOID, CDOObject> aggregateDetachedObjects(TransactionBoundary boundary, boolean includeEnd);

  /**
   * Aggregates revision deltas from the history ending at {@code boundary}.
   * <p>
   * {@code boundary} is the end point of the requested history range, and
   * {@code includeEnd} controls whether its segment is included. The result
   * is a new effective aggregate and is not a live map. It represents the
   * effective revision-delta state, which is distinct from the historical
   * provenance of the individual deltas in retained segments.
   *
   * @param boundary the end point of the history range.
   * @param includeEnd whether to include the segment belonging to
   *          {@code boundary}.
   * @return a new effective aggregate of revision deltas.
   */
  public Map<CDOID, CDORevisionDelta> aggregateRevisionDeltas(TransactionBoundary boundary, boolean includeEnd);

  public Map<CDOID, CDOObject> aggregateCurrentNewObjects();

  public Map<CDOID, CDOObject> aggregateCurrentDirtyObjects();

  public Map<CDOID, CDORevision> aggregateCurrentBaseNewObjects();

  public Map<CDOID, CDORevisionDelta> aggregateCurrentRevisionDeltas();

  public Map<CDOID, CDOObject> aggregateCurrentDetachedObjects();

  public void dumpHistoryDiagnostics();

  /**
   * Internal, opt-in diagnostics for transaction history. The stable switch is
   * {@code -Dcdo.diagnostic=true}; disabled diagnostics avoid formatting and
   * traversal work in normal execution. This class is debug-only and
   * observational: historical lifecycle provenance reported here is not the
   * same thing as the transaction's current effective state.
   *
   * @author Eike Stepper
   */
  public static final class Diagnostics
  {
    private static final boolean ENABLED = OMPlatform.INSTANCE.isProperty("cdo.diagnostic");

    private Diagnostics()
    {
    }

    public static void dump(TransactionHistory history)
    {
      if (!ENABLED)
      {
        return;
      }

      TransactionBoundary current = history.getCurrentBoundaryForDiagnostics();

      System.err.println("[CDO-DIAG][HISTORY] active=" + describe(history, current, current) + " cleanRevisions="
          + history.getCleanRevisionCountForDiagnostics() + " lifecycleBeforeImages=" + history.getLifecycleBeforeImageCountForDiagnostics());

      for (TransactionBoundary boundary = current; boundary != null; boundary = boundary.getPrevious())
      {
        TransactionSegment segment = boundary.getSegment();
        System.err.println("[CDO-DIAG][SEGMENT] " + describe(history, boundary, current) + " previous=" + describe(history, boundary.getPrevious(), current)
            + " next=" + describe(boundary.getNext()) + " new=" + segment.getNewObjects().keySet() + " dirty=" + segment.getDirtyObjects().keySet()
            + " detached=" + segment.getDetachedObjects().keySet() + " reattached=" + segment.getReattachedObjects().keySet() + " deltas="
            + segment.getRevisionDeltas().keySet() + " baseNew=" + segment.getBaseNewObjects().keySet());
      }
    }

    public static void checkInvariants(TransactionHistory history)
    {
      if (!ENABLED)
      {
        return;
      }

      TransactionBoundary current = history.getCurrentBoundaryForDiagnostics();

      try
      {
        if (current == null || current.getSegment() == null || current.getSegment().getBoundary() != current)
        {
          throw new IllegalStateException("[CDO-DIAG][INVARIANT] current boundary/segment ownership mismatch");
        }

        history.checkScopeInvariantsForDiagnostics();

        Map<TransactionBoundary, Boolean> seen = new IdentityHashMap<>();
        for (TransactionBoundary boundary = current; boundary != null; boundary = boundary.getPrevious())
        {
          if (seen.put(boundary, Boolean.TRUE) != null)
          {
            throw new IllegalStateException("[CDO-DIAG][INVARIANT] cyclic predecessor chain at " + describe(boundary));
          }

          if (boundary.getNext() != null && boundary.getNext().getPrevious() != boundary)
          {
            throw new IllegalStateException("[CDO-DIAG][INVARIANT] predecessor/successor mismatch at " + describe(boundary));
          }

          if (boundary.getSavepoint() != null && boundary.getSavepoint().getBoundary() != boundary)
          {
            throw new IllegalStateException("[CDO-DIAG][INVARIANT] savepoint ownership mismatch at " + describe(boundary));
          }
        }
      }
      catch (RuntimeException ex)
      {
        dump(history);
        throw ex;
      }
    }

    private static String describe(TransactionBoundary boundary)
    {
      if (boundary == null)
      {
        return "<none>";
      }

      String kind = boundary.getSavepoint() == null ? "invisible/internal" : "public-savepoint";
      if (boundary.getPrevious() == null)
      {
        kind += ",epoch-root";
      }

      return kind + "@" + Integer.toHexString(System.identityHashCode(boundary));
    }

    private static String describe(TransactionHistory history, TransactionBoundary boundary, TransactionBoundary current)
    {
      String description = describe(boundary);
      if (boundary == current)
      {
        description += ",current";
      }

      int depth = history.getActiveScopeDepthForDiagnostics(boundary);
      if (depth != 0)
      {
        description += ",nested-scope-begin,depth=" + depth;
      }

      return description;
    }
  }
}
