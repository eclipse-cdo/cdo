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

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.internal.server.syncing.OfflineClone.CommitContextData;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * TODO:
 * <ul>
 * <li>Handle new package units that had been committed during offline (testDisconnectAndCommitAndMergeWithNewPackages).
 * <li>Make CDOIDs of new objects temporary when merging out of temp branch.
 * <li>Provide custom branching strategies.
 * <li>Consider non-auditing masters.
 * <li>Test out-of-order commits.
 * <li>Don't create branches table if branching not supported.
 * <li>Implement raw replication for NUMERIC and DECIMAL.
 * <li>Notify new branches during raw replication.
 * </ul>
 * 
 * @author Eike Stepper
 */
public abstract class SynchronizableRepository extends Repository.Default implements InternalSynchronizableRepository
{
  protected static final CDOCommonRepository.Type MASTER = CDOCommonRepository.Type.MASTER;

  protected static final CDOCommonRepository.Type BACKUP = CDOCommonRepository.Type.BACKUP;

  protected static final CDOCommonRepository.Type CLONE = CDOCommonRepository.Type.CLONE;

  protected static final CDOCommonRepository.State INITIAL = CDOCommonRepository.State.INITIAL;

  protected static final CDOCommonRepository.State OFFLINE = CDOCommonRepository.State.OFFLINE;

  protected static final CDOCommonRepository.State SYNCING = CDOCommonRepository.State.SYNCING;

  protected static final CDOCommonRepository.State ONLINE = CDOCommonRepository.State.ONLINE;

  private static final String PROP_LAST_REPLICATED_BRANCH_ID = "org.eclipse.emf.cdo.server.lastReplicatedBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_REPLICATED_COMMIT_TIME = "org.eclipse.emf.cdo.server.lastReplicatedCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.gracefullyShutDown"; //$NON-NLS-1$

  private InternalRepositorySynchronizer synchronizer;

  private InternalSession replicatorSession;

  private int lastReplicatedBranchID = CDOBranch.MAIN_BRANCH_ID;

  private long lastReplicatedCommitTime = CDOBranchPoint.UNSPECIFIED_DATE;

  private int lastTransactionID;

  private ReadLock writeThroughCommitLock;

  private WriteLock handleCommitInfoLock;

  public SynchronizableRepository()
  {
    ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    writeThroughCommitLock = rwLock.readLock();
    handleCommitInfoLock = rwLock.writeLock();
  }

  public InternalRepositorySynchronizer getSynchronizer()
  {
    return synchronizer;
  }

  public void setSynchronizer(InternalRepositorySynchronizer synchronizer)
  {
    checkInactive();
    this.synchronizer = synchronizer;
  }

  public InternalSession getReplicatorSession()
  {
    return replicatorSession;
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

  public void setLastReplicatedBranchID(int lastReplicatedBranchID)
  {
    if (this.lastReplicatedBranchID < lastReplicatedBranchID)
    {
      this.lastReplicatedBranchID = lastReplicatedBranchID;
    }
  }

  public long getLastReplicatedCommitTime()
  {
    return lastReplicatedCommitTime;
  }

  public void setLastReplicatedCommitTime(long lastReplicatedCommitTime)
  {
    if (this.lastReplicatedCommitTime < lastReplicatedCommitTime)
    {
      this.lastReplicatedCommitTime = lastReplicatedCommitTime;
    }
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
    setLastReplicatedBranchID(branchID);
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
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo);
    commitContext.preWrite();
    boolean success = false;

    try
    {
      handleCommitInfoLock.lock();

      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());

      setLastCommitTimeStamp(timeStamp);
      setLastReplicatedCommitTime(timeStamp);
      success = true;
    }
    finally
    {
      handleCommitInfoLock.unlock();
      commitContext.postCommit(success);
      transaction.close();
    }
  }

  public void replicateRaw(CDODataInput in, OMMonitor monitor) throws IOException
  {
    try
    {
      int fromBranchID = lastReplicatedBranchID + 1;
      int toBranchID = in.readInt();
      long fromCommitTime = lastReplicatedCommitTime + 1L;
      long toCommitTime = in.readLong();

      StoreThreadLocal.setSession(replicatorSession);
      IStoreAccessor.Raw accessor = (IStoreAccessor.Raw)StoreThreadLocal.getAccessor();
      accessor.rawImport(in, fromBranchID, toBranchID, fromCommitTime, toCommitTime, monitor);

      replicateRawReviseRevisions();
      replicateRawNotifyClients(lastReplicatedCommitTime, toCommitTime);

      setLastReplicatedBranchID(toBranchID);
      setLastReplicatedCommitTime(toCommitTime);
      setLastCommitTimeStamp(toCommitTime);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  private void replicateRawReviseRevisions()
  {
    InternalCDORevisionCache cache = getRevisionManager().getCache();
    for (CDORevision revision : cache.getCurrentRevisions())
    {
      cache.removeRevision(revision.getID(), revision);
    }
  }

  private void replicateRawNotifyClients(long fromCommitTime, long toCommitTime)
  {
    InternalCDOCommitInfoManager manager = getCommitInfoManager();
    InternalSessionManager sessionManager = getSessionManager();

    Map<CDOBranch, TimeRange> branches = replicateRawGetBranches(fromCommitTime, toCommitTime);
    for (Entry<CDOBranch, TimeRange> entry : branches.entrySet())
    {
      CDOBranch branch = entry.getKey();
      TimeRange range = entry.getValue();
      fromCommitTime = range.getTime1();
      toCommitTime = range.getTime2();

      CDOBranchPoint startPoint = branch.getPoint(fromCommitTime);
      CDOBranchPoint endPoint = branch.getPoint(toCommitTime);
      CDOChangeSetData changeSet = getChangeSet(startPoint, endPoint);

      List<CDOPackageUnit> newPackages = Collections.emptyList(); // TODO Notify about new packages
      List<CDOIDAndVersion> newObjects = changeSet.getNewObjects();
      List<CDORevisionKey> changedObjects = changeSet.getChangedObjects();
      List<CDOIDAndVersion> detachedObjects = changeSet.getDetachedObjects();
      CDOCommitData data = new CDOCommitDataImpl(newPackages, newObjects, changedObjects, detachedObjects);

      String comment = "<replicate raw commits>"; //$NON-NLS-1$
      CDOCommitInfo commitInfo = manager.createCommitInfo(branch, toCommitTime, fromCommitTime, SYSTEM_USER_ID,
          comment, data);
      sessionManager.sendCommitNotification(replicatorSession, commitInfo);
    }
  }

  private Map<CDOBranch, TimeRange> replicateRawGetBranches(long fromCommitTime, long toCommitTime)
  {
    final Map<CDOBranch, TimeRange> branches = new HashMap<CDOBranch, TimeRange>();
    CDOCommitInfoHandler handler = new CDOCommitInfoHandler()
    {
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        CDOBranch branch = commitInfo.getBranch();
        long timeStamp = commitInfo.getTimeStamp();
        TimeRange range = branches.get(branch);
        if (range == null)
        {
          branches.put(branch, new TimeRange(timeStamp));
        }
        else
        {
          range.update(timeStamp);
        }
      }
    };

    getCommitInfoManager().getCommitInfos(null, fromCommitTime, toCommitTime, handler);
    return branches;
  }

  @Override
  public abstract InternalCommitContext createCommitContext(InternalTransaction transaction);

  protected InternalCommitContext createNormalCommitContext(InternalTransaction transaction)
  {
    return super.createCommitContext(transaction);
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
    if (!store.isFirstStart())
    {
      Map<String, String> map = store.getPropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
      if (!map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
      {
        setReplicationCountersToLatest();
      }
      else
      {
        Set<String> names = new HashSet<String>();
        names.add(PROP_LAST_REPLICATED_BRANCH_ID);
        names.add(PROP_LAST_REPLICATED_COMMIT_TIME);

        map = store.getPropertyValues(names);
        setLastReplicatedBranchID(Integer.valueOf(map.get(PROP_LAST_REPLICATED_BRANCH_ID)));
        setLastReplicatedCommitTime(Long.valueOf(map.get(PROP_LAST_REPLICATED_COMMIT_TIME)));
      }
    }

    store.removePropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));

    if (getType() != MASTER)
    {
      startSynchronization();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    stopSynchronization();

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_LAST_REPLICATED_BRANCH_ID, Integer.toString(lastReplicatedBranchID));
    map.put(PROP_LAST_REPLICATED_COMMIT_TIME, Long.toString(lastReplicatedCommitTime));
    map.put(PROP_GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());

    InternalStore store = getStore();
    store.setPropertyValues(map);

    super.doDeactivate();
  }

  protected void startSynchronization()
  {
    replicatorSession = getSessionManager().openSession(null);
    replicatorSession.options().setPassiveUpdateEnabled(false);

    synchronizer.setLocalRepository(this);
    synchronizer.activate();
  }

  protected void stopSynchronization()
  {
    if (synchronizer != null)
    {
      synchronizer.deactivate();
    }
  }

  protected void setReplicationCountersToLatest()
  {
    setLastReplicatedBranchID(getStore().getLastBranchID());
    setLastReplicatedCommitTime(getStore().getLastNonLocalCommitTime());
  }

  protected void doInitRootResource()
  {
    super.initRootResource();
  }

  @Override
  protected void initRootResource()
  {
    setState(INITIAL);
  }

  /**
   * @author Eike Stepper
   */
  private static final class TimeRange
  {
    private long time1;

    private long time2;

    public TimeRange(long time)
    {
      time1 = time;
      time2 = time;
    }

    public void update(long time)
    {
      if (time < time1)
      {
        time1 = time;
      }

      if (time > time2)
      {
        time2 = time;
      }
    }

    public long getTime1()
    {
      return time1;
    }

    public long getTime2()
    {
      return time2;
    }

    @Override
    public String toString()
    {
      return "[" + CDOCommonUtil.formatTimeStamp(time1) + " - " + CDOCommonUtil.formatTimeStamp(time1) + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  protected final class WriteThroughCommitContext extends TransactionCommitContext
  {
    public WriteThroughCommitContext(InternalTransaction transaction)
    {
      super(transaction);
    }

    @Override
    public void preWrite()
    {
      // Do nothing
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
      Collection<CDOLob<?>> lobs = Collections.emptySet();

      // Delegate commit to the master
      CDOSessionProtocol sessionProtocol = getSynchronizer().getRemoteSession().getSessionProtocol();
      CommitTransactionResult result = sessionProtocol.commitDelegation(branch, userID, comment, commitData,
          getDetachedObjectTypes(), lobs, monitor);

      // Stop if commit to master failed
      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }

      // Prepare data needed for commit result and commit notifications
      long timeStamp = result.getTimeStamp();
      setTimeStamp(timeStamp);
      addIDMappings(result.getIDMappings());
      applyIDMappings(new Monitor());

      try
      {
        writeThroughCommitLock.lock();

        // Commit to the local repository
        super.preWrite();
        super.write(new Monitor());
        super.commit(new Monitor());
      }
      finally
      {
        writeThroughCommitLock.unlock();
      }

      // Remember commit time in the local repository
      setLastCommitTimeStamp(timeStamp);
      setLastReplicatedCommitTime(timeStamp);

      // Remember commit time in the replicator session.
      getSynchronizer().getRemoteSession().setLastUpdateTime(timeStamp);
    }

    @Override
    protected long[] createTimeStamp(OMMonitor monitor)
    {
      // Already set after commit to the master.
      // Do not call getTimeStamp() of the enclosing Repo class!!!
      return new long[] { WriteThroughCommitContext.this.getTimeStamp(),
          WriteThroughCommitContext.this.getPreviousTimeStamp() };
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
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
}
