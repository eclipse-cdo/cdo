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

  public void clearRevisionDeltaAndDirty(CDOID id);

  public void setRevisionDeltaAndDirty(CDOID id, CDORevisionDelta delta, CDOObject object);

  public Map<CDOID, CDOObject> aggregateNewObjects(TransactionBoundary boundary, boolean includeEnd);

  public Map<CDOID, CDOObject> aggregateDirtyObjects(TransactionBoundary boundary, boolean includeEnd);

  public Map<CDOID, CDORevision> aggregateBaseNewObjects(TransactionBoundary boundary, boolean includeEnd);

  public Map<CDOID, CDOObject> aggregateDetachedObjects(TransactionBoundary boundary, boolean includeEnd);

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

    public static void boundary(String event, TransactionBoundary boundary)
    {
      if (ENABLED)
      {
        System.err.println("[CDO-DIAG][BOUNDARY] " + event + " " + describe(boundary));
      }
    }

    public static void aggregate(String kind, TransactionBoundary requested, boolean includeEnd, int boundaryCount, Map<CDOID, ?> result)
    {
      if (ENABLED)
      {
        System.err.println("[CDO-DIAG][AGGREGATE] kind=" + kind + " requested=" + describe(requested) + " includeEnd=" + includeEnd + " boundaries="
            + boundaryCount + " result=" + result.keySet());
      }
    }

    public static void dump(TransactionHistory history)
    {
      if (ENABLED)
      {
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
    }

    public static void checkInvariants(TransactionHistory history)
    {
      if (!ENABLED)
      {
        return;
      }

      TransactionBoundary current = history.getCurrentBoundaryForDiagnostics();
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

      System.err.println("[CDO-DIAG][INVARIANT] current boundary/segment and active predecessor chain are valid");
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
