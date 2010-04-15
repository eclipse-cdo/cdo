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
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO:
 * <ul>
 * <li>Handle new package units that had been committed during offline (testDisconnectAndCommitAndMergeWithNewPackages).
 * <li>Make CDOIDs of new objects temporary when merging out of temp branch.
 * <li>Provide custom branching strategies.
 * <li>Consider non-auditing masters.
 * <li>Test out-of-order commits.
 * </ul>
 * 
 * @author Eike Stepper
 */
public abstract class SynchronizableRepository extends Repository.Default implements CDOReplicationContext
{
  private static final String PROP_LAST_REPLICATED_BRANCH_ID = "org.eclipse.emf.cdo.server.clone.lastReplicatedBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_REPLICATED_COMMIT_TIME = "org.eclipse.emf.cdo.server.clone.lastReplicatedCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.clone.gracefullyShutDown"; //$NON-NLS-1$

  private RepositorySynchronizer synchronizer;

  private InternalSession replicatorSession;

  private int lastReplicatedBranchID = CDOBranch.MAIN_BRANCH_ID;

  private long lastReplicatedCommitTime = CDOBranchPoint.UNSPECIFIED_DATE;

  private int lastTransactionID;

  public SynchronizableRepository()
  {
  }

  public RepositorySynchronizer getSynchronizer()
  {
    return synchronizer;
  }

  public void setSynchronizer(RepositorySynchronizer synchronizer)
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

  public boolean isSqueezeCommitInfos()
  {
    return synchronizer.isSqueezeCommitInfos();
  }

  public void handleBranch(CDOBranch branch)
  {
    if (branch.isLocal())
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
    if (branch.isLocal())
    {
      return;
    }

    long timeStamp = commitInfo.getTimeStamp();
    CDOBranchPoint head = branch.getHead();

    InternalTransaction transaction = replicatorSession.openTransaction(++lastTransactionID, head);
    boolean squeezed = isSqueezeCommitInfos() && lastReplicatedCommitTime != CDOBranchPoint.UNSPECIFIED_DATE;
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo, squeezed);
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
    if (branch.isLocal())
    {
      return createNormalCommitContext(transaction);
    }

    if (getState() != State.ONLINE)
    {
      return createBranchingCommitContext(transaction, branch);
    }

    return createWriteThroughCommitContext(transaction);
  }

  protected InternalCommitContext createNormalCommitContext(InternalTransaction transaction)
  {
    return super.createCommitContext(transaction);
  }

  protected InternalCommitContext createBranchingCommitContext(InternalTransaction transaction, CDOBranch branch)
  {
    long timeStamp = createCommitTimeStamp(null);
    CDOBranch offlineBranch = createOfflineBranch(branch, timeStamp - 1L);
    transaction.setBranchPoint(offlineBranch.getHead());
    return new BranchingCommitContext(transaction, timeStamp);
  }

  protected InternalCommitContext createWriteThroughCommitContext(InternalTransaction transaction)
  {
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

    InternalStore store = getStore();
    if (!store.isFirstTime())
    {
      Map<String, String> map = store.getPropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
      if (!map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
      {
        throw new IllegalStateException("Clone store was not gracefully shut down");
      }

      Set<String> names = new HashSet<String>();
      names.add(PROP_LAST_REPLICATED_BRANCH_ID);
      names.add(PROP_LAST_REPLICATED_COMMIT_TIME);

      map = store.getPropertyValues(names);
      lastReplicatedBranchID = Integer.valueOf(map.get(PROP_LAST_REPLICATED_BRANCH_ID));
      lastReplicatedCommitTime = Long.valueOf(map.get(PROP_LAST_REPLICATED_COMMIT_TIME));
    }
    else
    {
      store.removePropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
    }

    replicatorSession = getSessionManager().openSession(null);
    replicatorSession.options().setPassiveUpdateEnabled(false);

    synchronizer.setLocalRepository(this);
    synchronizer.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronizer.deactivate();

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_LAST_REPLICATED_BRANCH_ID, Integer.toString(lastReplicatedBranchID));
    map.put(PROP_LAST_REPLICATED_COMMIT_TIME, Long.toString(lastReplicatedCommitTime));
    map.put(PROP_GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());

    InternalStore store = getStore();
    store.setPropertyValues(map);

    super.doDeactivate();
  }

  @Override
  protected void initRootResource()
  {
    setState(State.INITIAL);
  }

  protected CDOBranch createOfflineBranch(CDOBranch baseBranch, long baseTimeStamp)
  {
    try
    {
      StoreThreadLocal.setSession(replicatorSession);
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
  private static final class CommitContextData implements CDOCommitData
  {
    private InternalCommitContext commitContext;

    public CommitContextData(InternalCommitContext commitContext)
    {
      this.commitContext = commitContext;
    }

    public boolean isEmpty()
    {
      return false;
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
      InternalTransaction transaction = getTransaction();

      // Prepare commit to the master
      CDOBranch branch = transaction.getBranch();
      String userID = getUserID();
      String comment = getCommitComment();
      CDOCommitData commitData = new CommitContextData(this);

      // Delegate commit to the master
      CDOSessionProtocol sessionProtocol = ((InternalCDOSession)synchronizer.getRemoteSession()).getSessionProtocol();
      CommitTransactionResult result = sessionProtocol.commitDelegation(branch, userID, comment, commitData, monitor);

      // Stop if commit to master failed
      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }

      // Prepare data needed for commit result and commit notifications
      setTimeStamp(result.getTimeStamp());
      addMetaIDRanges(commitData.getNewPackageUnits());
      addIDMappings(result.getIDMappings());
      applyIDMappings(new Monitor());

      // Commit to the clone
      super.preWrite();
      super.write(new Monitor());
      super.commit(new Monitor());

      // Remember commit time in the clone
      setLastCommitTimeStamp(result.getTimeStamp());
    }

    @Override
    protected long createTimeStamp()
    {
      // Already set after commit to the master
      return getTimeStamp();
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
    }

    @Override
    protected void adjustMetaRanges()
    {
      // Do nothing
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
