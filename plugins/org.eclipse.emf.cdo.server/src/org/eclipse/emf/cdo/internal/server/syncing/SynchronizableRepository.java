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
package org.eclipse.emf.cdo.internal.server.syncing;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.om.monitor.Monitor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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

  public SynchronizableRepository()
  {
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
    boolean squeezed = isSqueezeCommitInfos() && lastReplicatedCommitTime != CDOBranchPoint.UNSPECIFIED_DATE;
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo, squeezed);
    commitContext.preWrite();
    boolean success = false;

    try
    {
      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());

      setLastCommitTimeStamp(timeStamp);
      setLastReplicatedCommitTime(timeStamp);
      success = true;
    }
    finally
    {
      commitContext.postCommit(success);
      transaction.close();
    }
  }

  public void replicateRaw(CDODataInput in) throws IOException
  {
    try
    {
      int fromBranchID = lastReplicatedBranchID + 1;
      int toBranchID = in.readInt();
      long fromCommitTime = lastReplicatedCommitTime + 1L;
      long toCommitTime = in.readLong();

      StoreThreadLocal.setSession(replicatorSession);
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();

      Collection<InternalCDOPackageUnit> XXXXXXXXXXXXXXXXXXXXXXXXX = //
      accessor.rawImport(in, fromBranchID, toBranchID, fromCommitTime, toCommitTime);

      setLastReplicatedBranchID(toBranchID);
      setLastReplicatedCommitTime(toCommitTime);
      setLastCommitTimeStamp(toCommitTime);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Override
  public abstract InternalCommitContext createCommitContext(InternalTransaction transaction);

  protected InternalCommitContext createNormalCommitContext(InternalTransaction transaction)
  {
    return super.createCommitContext(transaction);
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
        throw new IllegalStateException("Local repository was not gracefully shut down");
      }

      Set<String> names = new HashSet<String>();
      names.add(PROP_LAST_REPLICATED_BRANCH_ID);
      names.add(PROP_LAST_REPLICATED_COMMIT_TIME);

      map = store.getPropertyValues(names);
      setLastReplicatedBranchID(Integer.valueOf(map.get(PROP_LAST_REPLICATED_BRANCH_ID)));
      setLastReplicatedCommitTime(Long.valueOf(map.get(PROP_LAST_REPLICATED_COMMIT_TIME)));
    }
    else
    {
      store.removePropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
    }

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

  protected void doInitRootResource()
  {
    super.initRootResource();
  }

  @Override
  protected void initRootResource()
  {
    setState(INITIAL);
  }
}
