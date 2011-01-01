/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.syncing;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeKindCache;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class OfflineClone extends SynchronizableRepository
{
  public OfflineClone()
  {
    setState(OFFLINE);
  }

  @Override
  public final Type getType()
  {
    return CLONE;
  }

  @Override
  public final void setType(Type type)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    CDOBranch branch = transaction.getBranch();
    if (branch.isLocal())
    {
      return createNormalCommitContext(transaction);
    }

    if (getState() != ONLINE)
    {
      return createBranchingCommitContext(transaction, branch);
    }

    return createWriteThroughCommitContext(transaction);
  }

  protected InternalCommitContext createBranchingCommitContext(InternalTransaction transaction, CDOBranch branch)
  {
    long[] times = createCommitTimeStamp(new Monitor());
    CDOBranch offlineBranch = createOfflineBranch(branch, times[0] - 1L);
    transaction.setBranchPoint(offlineBranch.getHead());
    return new BranchingCommitContext(transaction, times);
  }

  protected CDOBranch createOfflineBranch(CDOBranch baseBranch, long baseTimeStamp)
  {
    try
    {
      StoreThreadLocal.setSession(getReplicatorSession());
      InternalCDOBranchManager branchManager = getBranchManager();
      return branchManager.createBranch(NEW_LOCAL_BRANCH,
          "Offline-" + baseTimeStamp, (InternalCDOBranch)baseBranch, baseTimeStamp); //$NON-NLS-1$
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class CommitContextData implements CDOCommitData
  {
    private InternalCommitContext commitContext;

    private CDOChangeKindCache changeKindCache;

    public CommitContextData(InternalCommitContext commitContext)
    {
      this.commitContext = commitContext;
    }

    public boolean isEmpty()
    {
      return false;
    }

    public CDOChangeSetData copy()
    {
      throw new UnsupportedOperationException();
    }

    public void merge(CDOChangeSetData changeSetData)
    {
      throw new UnsupportedOperationException();
    }

    public List<CDOPackageUnit> getNewPackageUnits()
    {
      final InternalCDOPackageUnit[] newPackageUnits = commitContext.getNewPackageUnits();
      return new IndexedList<CDOPackageUnit>()
      {
        @Override
        public CDOPackageUnit get(int index)
        {
          return newPackageUnits[index];
        }

        @Override
        public int size()
        {
          return newPackageUnits.length;
        }
      };
    }

    public List<CDOIDAndVersion> getNewObjects()
    {
      final InternalCDORevision[] newObjects = commitContext.getNewObjects();
      return new IndexedList<CDOIDAndVersion>()
      {
        @Override
        public CDOIDAndVersion get(int index)
        {
          return newObjects[index];
        }

        @Override
        public int size()
        {
          return newObjects.length;
        }
      };
    }

    public List<CDORevisionKey> getChangedObjects()
    {
      final InternalCDORevisionDelta[] changedObjects = commitContext.getDirtyObjectDeltas();
      return new IndexedList<CDORevisionKey>()
      {
        @Override
        public CDORevisionKey get(int index)
        {
          return changedObjects[index];
        }

        @Override
        public int size()
        {
          return changedObjects.length;
        }
      };
    }

    public List<CDOIDAndVersion> getDetachedObjects()
    {
      final CDOID[] detachedObjects = commitContext.getDetachedObjects();
      return new IndexedList<CDOIDAndVersion>()
      {
        @Override
        public CDOIDAndVersion get(int index)
        {
          return CDOIDUtil.createIDAndVersion(detachedObjects[index], CDOBranchVersion.UNSPECIFIED_VERSION);
        }

        @Override
        public int size()
        {
          return detachedObjects.length;
        }
      };
    }

    public synchronized CDOChangeKind getChangeKind(CDOID id)
    {
      if (changeKindCache == null)
      {
        changeKindCache = new CDOChangeKindCache(this);
      }

      return changeKindCache.getChangeKind(id);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected final class BranchingCommitContext extends TransactionCommitContext
  {
    private long[] times;

    public BranchingCommitContext(InternalTransaction transaction, long[] times)
    {
      super(transaction);
      this.times = times;
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
    }

    @Override
    protected long[] createTimeStamp(OMMonitor monitor)
    {
      return times;
    }
  }
}
