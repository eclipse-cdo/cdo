/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.clone;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO:
 * <ul>
 * <li>Implement DBStoreAccessor.loadCommitData().
 * <li>Persist lastReplicatedBranchID and lastReplicatedCommitTime.
 * <li>Persist lastTempBranchID.
 * <li>Provide custom branching strategies.
 * <li>Consider non-auditing masters.
 * <li>Test out-of-order commits.
 * <li>Implement merging.
 * <li>Make CDOIDs of new objects temporary when merging out of temp branch.
 * </ul>
 * 
 * @author Eike Stepper
 */
public class CloneRepository extends Repository.Default implements CDOReplicationContext
{
  private CloneSynchronizer synchronizer;

  private InternalSession replicatorSession;

  private int lastReplicatedBranchID;

  private long lastReplicatedCommitTime;

  private int lastTransactionID;

  private AtomicInteger lastTempBranchID = new AtomicInteger();

  public CloneRepository()
  {
    setState(State.OFFLINE);
  }

  @Override
  public Type getType()
  {
    return Type.CLONE;
  }

  public CloneSynchronizer getSynchronizer()
  {
    return synchronizer;
  }

  public void setSynchronizer(CloneSynchronizer synchronizer)
  {
    checkInactive();
    this.synchronizer = synchronizer;
  }

  @Override
  public Object[] getElements()
  {
    List<Object> list = Arrays.asList(super.getElements());
    list.add(synchronizer);
    return list.toArray();
  }

  public int getLastReplicatedBranchID()
  {
    return lastReplicatedBranchID;
  }

  public long getLastReplicatedCommitTime()
  {
    return lastReplicatedCommitTime;
  }

  public void handleBranch(CDOBranch branch)
  {
    if (branch.isTemporary())
    {
      return;
    }

    int branchID = branch.getID();
    String name = branch.getName();

    CDOBranchPoint base = branch.getBase();
    InternalCDOBranch baseBranch = (InternalCDOBranch)base.getBranch();
    long baseTimeStamp = base.getTimeStamp();

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.createBranch(branchID, name, baseBranch, baseTimeStamp);
    lastReplicatedBranchID = branchID;
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    CDOBranch branch = commitInfo.getBranch();
    if (branch.isTemporary())
    {
      return;
    }

    long timeStamp = commitInfo.getTimeStamp();
    CDOBranchPoint head = branch.getHead();

    InternalTransaction transaction = replicatorSession.openTransaction(++lastTransactionID, head);
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo);
    commitContext.preWrite();
    boolean success = false;

    try
    {
      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());

      setLastCommitTimeStamp(timeStamp);
      lastReplicatedCommitTime = timeStamp;
      success = true;
    }
    finally
    {
      commitContext.postCommit(success);
      transaction.close();
    }
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    CDOBranch branch = transaction.getBranch();
    if (branch.isTemporary())
    {
      return new TransactionCommitContext(transaction);
    }

    if (getState() != State.ONLINE)
    {
      final long timeStamp = createCommitTimeStamp();
      CDOBranch offlineBranch = createOfflineBranch(branch, timeStamp - 1L);
      transaction.setBranchPoint(offlineBranch.getHead());
      return new BranchingCommitContext(transaction, timeStamp);
    }

    return new WriteThroughCommitContext(transaction);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(synchronizer, "synchronizer"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    replicatorSession = getSessionManager().openSession(null);
    replicatorSession.options().setPassiveUpdateEnabled(false);

    synchronizer.setClone(this);
    synchronizer.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronizer.deactivate();
    super.doDeactivate();
  }

  @Override
  protected void initRootResource()
  {
    setState(State.INITIAL);
  }

  protected CDOBranch createOfflineBranch(CDOBranch baseBranch, long baseTimeStamp)
  {
    int branchID = lastTempBranchID.decrementAndGet();
    InternalCDOBranchManager branchManager = getBranchManager();
    return branchManager.createBranch(branchID, "Offline" + branchID, (InternalCDOBranch)baseBranch, baseTimeStamp); //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommitContextData implements CDOCommitData
  {
    private InternalCommitContext commitContext;

    public CommitContextData(InternalCommitContext commitContext)
    {
      this.commitContext = commitContext;
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
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteThroughCommitContext extends TransactionCommitContext
  {
    public WriteThroughCommitContext(InternalTransaction transaction)
    {
      super(transaction);
    }

    @Override
    public void write(OMMonitor monitor)
    {
      // Do nothing
    }

    @Override
    public void commit(OMMonitor monitor)
    {
      CDOBranch branch = getTransaction().getBranch();
      String userID = getUserID();
      String comment = getCommitComment();
      CDOCommitData commitData = new CommitContextData(this);

      CDOSessionProtocol sessionProtocol = ((InternalCDOSession)synchronizer.getMaster()).getSessionProtocol();
      CommitTransactionResult result = sessionProtocol.commitDelegation(branch, userID, comment, commitData, monitor);

      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }

      long timeStamp = result.getTimeStamp();
      setTimeStamp(timeStamp);

      addMetaIDRanges(commitData.getNewPackageUnits());
      addIDMappings(result.getIDMappings());
    }

    private void addMetaIDRanges(List<CDOPackageUnit> newPackageUnits)
    {
      for (CDOPackageUnit newPackageUnit : newPackageUnits)
      {
        for (CDOPackageInfo packageInfo : newPackageUnit.getPackageInfos())
        {
          addMetaIDRange(packageInfo.getMetaIDRange());
        }
      }
    }

    private void addIDMappings(Map<CDOID, CDOID> idMappings)
    {
      for (Map.Entry<CDOID, CDOID> idMapping : idMappings.entrySet())
      {
        CDOID oldID = idMapping.getKey();
        CDOID newID = idMapping.getValue();
        addIDMapping(oldID, newID);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class BranchingCommitContext extends TransactionCommitContext
  {
    private long timeStamp;

    public BranchingCommitContext(InternalTransaction transaction, long timeStamp)
    {
      super(transaction);
      this.timeStamp = timeStamp;
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
    }

    @Override
    protected long createTimeStamp()
    {
      return timeStamp;
    }
  }
}
