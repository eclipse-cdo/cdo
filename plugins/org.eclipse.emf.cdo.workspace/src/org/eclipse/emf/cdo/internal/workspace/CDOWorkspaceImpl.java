/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler1;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler3;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionConfiguration;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.ApplyChangeSetResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.ChangeSetOutdatedException;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOWorkspaceImpl extends Notifier implements InternalCDOWorkspace
{
  private static final String PROP_BRANCH_ID = "org.eclipse.emf.cdo.workspace.branchID"; //$NON-NLS-1$

  private static final String PROP_BRANCH_PATH = "org.eclipse.emf.cdo.workspace.branchPath"; //$NON-NLS-1$

  private static final String PROP_TIME_STAMP = "org.eclipse.emf.cdo.workspace.timeStamp"; //$NON-NLS-1$

  private static final String PROP_FIXED = "org.eclipse.emf.cdo.workspace.fixed"; //$NON-NLS-1$

  private IManagedContainer container;

  private InternalCDOWorkspaceBase base;

  private IDGenerationLocation idGenerationLocation;

  private CDOIDGenerator idGenerator;

  private InternalRepository localRepository;

  private InternalCDOSession localSession;

  private CDOBranchPoint head;

  private int branchID = NO_BRANCH_ID;

  private String branchPath;

  private long timeStamp;

  private boolean fixed;

  private boolean dirty;

  private CDOSessionConfigurationFactory remoteSessionConfigurationFactory;

  private Map<InternalCDOSession, Closeable> closeables = new ConcurrentHashMap<InternalCDOSession, Closeable>();

  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  /**
   * Checkout.
   */
  public CDOWorkspaceImpl(String localRepositoryName, IStore local, IDGenerationLocation idGenerationLocation,
      CDOIDGenerator idGenerator, InternalCDOWorkspaceBase base, CDOSessionConfigurationFactory remote, int branchID,
      String branchPath, long timeStamp)
  {
    init(localRepositoryName, local, idGenerationLocation, idGenerator, base, remote);

    this.branchID = branchID;
    this.branchPath = branchPath;
    this.timeStamp = timeStamp;
    fixed = timeStamp != CDOBranchPoint.UNSPECIFIED_DATE;

    checkout();
    saveProperties();
  }

  /**
   * Open.
   */
  public CDOWorkspaceImpl(String localRepositoryName, IStore local, IDGenerationLocation idGenerationLocation,
      CDOIDGenerator idGenerator, InternalCDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    init(localRepositoryName, local, idGenerationLocation, idGenerator, base, remote);
    loadProperties();
  }

  protected void checkout()
  {
    final OMMonitor monitor = new Monitor();
    final IStoreAccessor.Raw accessor = getLocalWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      InternalCDOSession remoteSession = openRemoteSession();

      try
      {
        localRepository.setRootResourceID(remoteSession.getRepositoryInfo().getRootResourceID());

        InternalCDOPackageUnit[] packageUnits = remoteSession.getPackageRegistry().getPackageUnits(true);
        InternalCDOPackageRegistry packageRegistry = localRepository.getPackageRegistry(false);
        for (InternalCDOPackageUnit packageUnit : packageUnits)
        {
          packageRegistry.putPackageUnit(packageUnit);
        }

        accessor.rawStore(packageUnits, monitor);

        CDORevisionHandler handler = new CDORevisionHandler()
        {
          public boolean handleRevision(CDORevision revision)
          {
            InternalCDORevision rev = (InternalCDORevision)revision;
            adjustRevisionBranch(rev, localRepository.getBranchManager());
            accessor.rawStore(rev, monitor);

            long commitTime = revision.getTimeStamp();
            if (commitTime > timeStamp)
            {
              timeStamp = commitTime;
            }

            return true;
          }
        };

        InternalCDOBranchManager branchManager = remoteSession.getBranchManager();
        CDOBranch branch;
        if (branchID == NO_BRANCH_ID)
        {
          if (StringUtil.isEmpty(branchPath))
          {
            branchPath = CDOBranch.MAIN_BRANCH_NAME;
          }

          branch = branchManager.getBranch(branchPath);
          branchID = branch.getID();
        }
        else
        {
          branch = branchManager.getBranch(branchID);
          branchPath = branch.getPathName();
        }

        remoteSession.getSessionProtocol().handleRevisions(null, branch, false, timeStamp, false, handler);
      }
      finally
      {
        remoteSession.getPackageRegistry().clear();
        closeRemoteSession(remoteSession);
      }

      accessor.rawCommit(1, monitor);
    }
    finally
    {
      StoreThreadLocal.release();
      monitor.done();
    }
  }

  protected void init(String localRepositoryName, IStore local, IDGenerationLocation idGenerationLocation,
      CDOIDGenerator idGenerator, InternalCDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    this.idGenerationLocation = idGenerationLocation;
    this.idGenerator = idGenerator;

    container = createContainer(local);
    remoteSessionConfigurationFactory = remote;

    try
    {
      localRepository = createLocalRepository(localRepositoryName, local);

      this.base = base;
      this.base.init(this);
      setDirtyFromBase();
    }
    catch (RuntimeException ex)
    {
      close();
      throw ex;
    }
    catch (Error ex)
    {
      close();
      throw ex;
    }
  }

  private void setDirtyFromBase()
  {
    setDirty(!base.isEmpty());
  }

  public int getBranchID()
  {
    return branchID;
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean isFixed()
  {
    return fixed;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  protected void setDirty(boolean dirty)
  {
    if (this.dirty != dirty)
    {
      this.dirty = dirty;
      fireEvent(new DirtyStateChangedEventImpl(this, dirty));
    }
  }

  protected void clearBase()
  {
    base.clear();
    setDirty(false);
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  public CDOIDGenerator getIDGenerator()
  {
    return idGenerator;
  }

  public InternalCDOWorkspaceBase getBase()
  {
    return base;
  }

  public InternalCDOView openView()
  {
    CDOView view = getLocalSession().openView();
    initView(view);
    return (InternalCDOView)view;
  }

  public InternalCDOView openView(ResourceSet resourceSet)
  {
    CDOView view = getLocalSession().openView(resourceSet);
    initView(view);
    return (InternalCDOView)view;
  }

  public InternalCDOTransaction openTransaction()
  {
    CDOTransaction transaction = getLocalSession().openTransaction();
    initView(transaction);
    initTransaction(transaction);
    return (InternalCDOTransaction)transaction;
  }

  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    CDOTransaction transaction = getLocalSession().openTransaction(resourceSet);
    initView(transaction);
    initTransaction(transaction);
    return (InternalCDOTransaction)transaction;
  }

  protected void initView(CDOView view)
  {
    synchronized (views)
    {
      views.add((InternalCDOView)view);
    }

    view.addListener(new ViewAdapter());

    if (view instanceof CDOTransaction)
    {
      if (fixed)
      {
        throw new ReadOnlyException("Workspace is fixed");
      }

      if (idGenerationLocation != IDGenerationLocation.CLIENT)
      {
        CDOTransaction transaction = (CDOTransaction)view;
        transaction.addTransactionHandler(new CDODefaultTransactionHandler1()
        {
          @Override
          public void attachingObject(CDOTransaction transaction, CDOObject object)
          {
            throw new IllegalStateException("Attaching new objects is only supported for IDGenerationLocation.CLIENT");
          }
        });
      }
    }
  }

  protected void initTransaction(CDOTransaction transaction)
  {
    transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
    {
      @Override
      public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
        InternalCDOTransaction tx = (InternalCDOTransaction)transaction;
        Set<CDOID> dirtyObjects = tx.getDirtyObjects().keySet();
        Set<CDOID> detachedObjects = tx.getDetachedObjects().keySet();
        for (InternalCDORevision revision : tx.getCleanRevisions().values())
        {
          CDOID id = revision.getID();
          boolean isDetached = detachedObjects.contains(id);

          if (isDetached && base.isAddedObject(id))
          {
            base.registerAddedAndDetachedObject(revision);
          }
          else if (isDetached || dirtyObjects.contains(id))
          {
            base.registerChangedOrDetachedObject(revision);
          }
        }

        // Don't use keySet() because only the values() are ID-mapped!
        for (CDOObject object : tx.getNewObjects().values())
        {
          CDOID id = object.cdoID();
          base.registerAddedObject(id);
        }

        setDirtyFromBase();
      }
    });
  }

  public InternalCDOTransaction update(CDOMerger merger)
  {
    return merge(merger, branchPath);
  }

  public InternalCDOTransaction merge(CDOMerger merger, String branchPath)
  {
    return merge(merger, branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public InternalCDOTransaction merge(CDOMerger merger, String branchPath, long timeStamp)
  {
    final InternalCDOSession remoteSession = openRemoteSession();

    try
    {
      if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        timeStamp = remoteSession.getLastUpdateTime();
      }

      final long newTimeStamp = timeStamp;

      final InternalCDOBranchManager branchManager = remoteSession.getBranchManager();
      final CDOBranchPoint basePoint = branchManager.getBranch(branchPath).getPoint(this.timeStamp);
      final CDOBranchPoint remotePoint = branchManager.getBranch(branchPath).getPoint(newTimeStamp);

      final CDOBranchPointRange range = CDOBranchUtil.createRange(basePoint, remotePoint);

      final CDOChangeSetData remoteData = remoteSession.getSessionProtocol().loadChangeSets(range)[0];
      final CDOChangeSetData localData = getLocalChanges();
      final CDOChangeSetData result = getMergeResult(merger, basePoint, remotePoint, localData, remoteData);

      final InternalCDOTransaction transaction = (InternalCDOTransaction)getLocalSession().openTransaction();
      initView(transaction);

      transaction.applyChangeSet(result, new BaseRevisionProvider(), this, null, false);
      transaction.addTransactionHandler(new CDODefaultTransactionHandler3()
      {
        @Override
        public void rolledBackTransaction(CDOTransaction transaction)
        {
          closeRemoteSession(remoteSession);
        }

        @Override
        public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext,
            CDOCommitInfo result)
        {
          try
          {
            Set<CDOID> affectedIDs = getAffectedIDs(commitContext, remoteData);

            CDORevisionProvider local = CDOWorkspaceImpl.this;
            CDORevisionProvider remote = new ManagedRevisionProvider(remoteSession.getRevisionManager(), remotePoint);

            updateBase(affectedIDs, local, remote);
            setTimeStamp(newTimeStamp);
          }
          finally
          {
            closeRemoteSession(remoteSession);
          }
        }

        private void updateBase(Set<CDOID> affectedIDs, CDORevisionProvider local, CDORevisionProvider remote)
        {
          for (CDOID id : affectedIDs)
          {
            CDORevision localRevision = getRevision(id, local);
            CDORevision remoteRevision = getRevision(id, remote);
            if (localRevision == null)
            {
              if (remoteRevision == null)
              {
                // Unchanged
                base.deregisterObject(id);
              }
              else
              {
                // Detached
                base.registerChangedOrDetachedObject((InternalCDORevision)remoteRevision);
              }
            }
            else
            {
              if (remoteRevision == null)
              {
                // Added
                base.registerAddedObject(id);
              }
              else
              {
                // Unchanged
                base.deregisterObject(id);

                CDORevisionDelta delta = localRevision.compare(remoteRevision);
                if (!delta.isEmpty())
                {
                  // Changed
                  base.registerChangedOrDetachedObject((InternalCDORevision)remoteRevision);
                }
              }
            }
          }
        }

        private Set<CDOID> getAffectedIDs(CDOCommitContext commitContext, final CDOChangeSetData remoteData)
        {
          Set<CDOID> affectedIDs = new HashSet<CDOID>();

          // Base IDs
          affectedIDs.addAll(base.getIDs());

          // Remote IDs
          affectedIDs.addAll(remoteData.getChangeKinds().keySet());

          // Local IDs
          affectedIDs.addAll(commitContext.getNewObjects().keySet());
          affectedIDs.addAll(commitContext.getDirtyObjects().keySet());
          affectedIDs.addAll(commitContext.getDetachedObjects().keySet());

          return affectedIDs;
        }

        private CDORevision getRevision(CDOID id, CDORevisionProvider revisionProvider)
        {
          CDORevision revision = revisionProvider.getRevision(id);
          if (revision instanceof DetachedCDORevision)
          {
            revision = null;
          }

          return revision;
        }
      });

      return transaction;
    }
    catch (RuntimeException ex)
    {
      closeRemoteSession(remoteSession);
      throw ex;
    }
    catch (Error ex)
    {
      closeRemoteSession(remoteSession);
      throw ex;
    }
  }

  private CDOChangeSetData getMergeResult(CDOMerger merger, CDOBranchPoint basePoint, CDOBranchPoint remotePoint,
      CDOChangeSetData localData, CDOChangeSetData remoteData)
  {
    if (localData.isEmpty())
    {
      return remoteData;
    }

    CDOChangeSet localChanges = CDORevisionUtil.createChangeSet(basePoint, null, localData);
    CDOChangeSet remoteChanges = CDORevisionUtil.createChangeSet(basePoint, remotePoint, remoteData);
    return merger.merge(localChanges, remoteChanges);
  }

  public void revert()
  {
    CDOChangeSetData revertData = getLocalChanges(false);
    revert(revertData);
  }

  public void revert(CDOChangeSetData revertData)
  {
    final CDOBranch localBranch = head.getBranch();

    IStoreAccessor.Raw accessor = getLocalWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    final List<InternalCDORevision> changedRevisions = new ArrayList<InternalCDORevision>();
    for (CDORevisionKey key : revertData.getChangedObjects())
    {
      CDOID id = key.getID();

      InternalCDORevision localRevision = (InternalCDORevision)getRevision(id);
      InternalCDORevision baseRevision = (InternalCDORevision)base.getRevision(id);
      changedRevisions.add(baseRevision);

      EClass eClass = baseRevision.getEClass();
      int version = Math.abs(localRevision.getVersion());

      for (int v = baseRevision.getVersion(); v <= version; v++)
      {
        accessor.rawDelete(id, v, localBranch, eClass, new Monitor());
      }

      accessor.rawStore(baseRevision, new Monitor());
    }

    final List<CDORevisionKey> detachedRevisions = new ArrayList<CDORevisionKey>();
    for (CDOIDAndVersion key : revertData.getDetachedObjects())
    {
      CDOID id = key.getID();
      int version = getRevision(id).getVersion();

      for (int v = 1; v <= version; v++)
      {
        accessor.rawDelete(id, v, localBranch, null, new Monitor());
      }

      detachedRevisions.add(CDORevisionUtil.createRevisionKey(id, localBranch, version));
    }

    for (CDOIDAndVersion key : revertData.getNewObjects())
    {
      CDOID id = key.getID();

      SyntheticCDORevision[] synthetics = { null };
      InternalCDORevisionManager revisionManager = localSession.getRevisionManager();
      revisionManager.getRevision(id, head, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true, synthetics);

      int max = synthetics[0].getVersion();
      EClass eClass = synthetics[0].getEClass();

      InternalCDORevision baseRevision = (InternalCDORevision)base.getRevision(id);
      for (int v = baseRevision.getVersion(); v <= max; v++)
      {
        int version = v == max ? -max : v;
        accessor.rawDelete(id, version, localBranch, eClass, new Monitor());
      }

      accessor.rawStore(baseRevision, new Monitor());
    }

    base.deleteAddedAndDetachedObjects(accessor, localBranch);
    finishRawAccess(accessor);
    base.clear();

    localSession.refresh(new RefreshSessionResult.Provider()
    {
      public RefreshSessionResult getRefreshSessionResult(Map<CDOBranch, List<InternalCDOView>> views,
          Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
      {
        RefreshSessionResult result = new RefreshSessionResult(timeStamp);
        Map<CDOID, InternalCDORevision> revisions = viewedRevisions.get(localBranch);

        for (InternalCDORevision baseRevision : changedRevisions)
        {
          CDOID id = baseRevision.getID();
          if (revisions.containsKey(id))
          {
            InternalCDORevision newRevision = baseRevision.copy();
            adjustRevisionBranch(newRevision, localSession.getBranchManager());

            result.addChangedObject(newRevision);
          }
        }

        for (CDORevisionKey key : detachedRevisions)
        {
          result.addDetachedObject(key);
        }

        localSession.getRevisionManager().getCache().clear();
        return result;
      }
    });

    setDirty(false);
  }

  public void replace(String branchPath, long timeStamp)
  {
    // TODO: implement CDOWorkspaceImpl.replace(branchPath, timeStamp)
    throw new UnsupportedOperationException();
  }

  public CDOCommitInfo checkin() throws CommitException
  {
    return checkin(null);
  }

  public CDOCommitInfo checkin(String comment) throws CommitException
  {
    InternalCDOSession remoteSession = openRemoteSession();

    try
    {
      InternalCDOBranch branch = remoteSession.getBranchManager().getBranch(branchPath);
      InternalCDOTransaction transaction = (InternalCDOTransaction)remoteSession.openTransaction(branch);

      try
      {
        CDOChangeSetData changes = getLocalChanges();

        try
        {
          ApplyChangeSetResult result = transaction.applyChangeSet(changes, base, this, head, true);
          if (!result.getIDMappings().isEmpty())
          {
            throw new IllegalStateException("Attaching new objects is only supported for IDGenerationLocation.CLIENT");
          }
        }
        catch (ChangeSetOutdatedException ex)
        {
          throw new CommitException(ex);
        }

        transaction.setCommitComment(comment);
        CDOCommitInfo info = transaction.commit();

        adjustLocalRevisions(transaction, info.getChangedObjects());
        clearBase();
        setTimeStamp(info.getTimeStamp());

        return info;
      }
      finally
      {
        transaction.close();
      }
    }
    finally
    {
      closeRemoteSession(remoteSession);
    }
  }

  protected void adjustLocalRevisions(InternalCDOTransaction transaction, List<CDORevisionKey> changedObjects)
  {
    IStoreAccessor.Raw accessor = null;
    for (CDORevisionKey key : changedObjects)
    {
      CDOID id = key.getID();
      InternalCDORevision localRevision = (InternalCDORevision)getRevision(id);
      CDORevision baseRevision = base.getRevision(id);
      CDORevision remoteRevision = transaction.getObject(id).cdoRevision();

      CDOBranch localBranch = head.getBranch();
      EClass eClass = localRevision.getEClass();

      for (int v = baseRevision.getVersion(); v < localRevision.getVersion(); v++)
      {
        if (accessor == null)
        {
          accessor = getLocalWriter(null);
          StoreThreadLocal.setAccessor(accessor);
        }

        accessor.rawDelete(id, v, localBranch, eClass, new Monitor());
      }

      if (localRevision.getVersion() != remoteRevision.getVersion())
      {
        if (accessor == null)
        {
          accessor = getLocalWriter(null);
          StoreThreadLocal.setAccessor(accessor);
        }

        accessor.rawDelete(id, localRevision.getVersion(), localBranch, eClass, new Monitor());
        localRevision.setVersion(remoteRevision.getVersion());
        adjustRevisionBranch(localRevision, localRepository.getBranchManager());
        accessor.rawStore(localRevision, new Monitor());
      }
    }

    if (accessor != null)
    {
      finishRawAccess(accessor);
      localSession.getRevisionManager().getCache().clear();
    }
  }

  private void adjustRevisionBranch(InternalCDORevision revision, InternalCDOBranchManager forBranchManager)
  {
    InternalCDOBranch branch = revision.getBranch();
    if (branch.getBranchManager() != forBranchManager)
    {
      branch = forBranchManager.getBranch(branch.getID());
      revision.setBranchPoint(branch.getPoint(revision.getTimeStamp()));
    }
  }

  private void finishRawAccess(IStoreAccessor.Raw accessor)
  {
    accessor.rawCommit(1, new Monitor());
    StoreThreadLocal.release();
    localRepository.getRevisionManager().getCache().clear();
  }

  /**
   * @deprecated Attaching new objects is only supported for IDGenerationLocation.CLIENT
   */
  @Deprecated
  protected CDOIDMapper getIDMapper(InternalCDOTransaction transaction, final Map<CDOID, CDOID> idMappings)
  {
    if (idMappings.isEmpty())
    {
      return null;
    }

    transaction.addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOTransactionFinishedEvent)
        {
          CDOTransactionFinishedEvent e = (CDOTransactionFinishedEvent)event;
          Map<CDOID, CDOID> remoteMappings = e.getIDMappings();
          for (Entry<CDOID, CDOID> entry : idMappings.entrySet())
          {
            CDOID tempID = entry.getValue();
            CDOID newID = remoteMappings.get(tempID);
            entry.setValue(newID);
          }
        }
      }
    });

    return new CDOIDMapper(idMappings);
  }

  /**
   * @deprecated Attaching new objects is only supported for IDGenerationLocation.CLIENT
   */
  @Deprecated
  protected void adjustLocalIDs(CDOIDMapper idMapper, List<CDOID> adjustedObjects)
  {
    Map<CDOID, CDOID> idMappings = idMapper.getIDMappings();
    if (!idMappings.isEmpty())
    {
      CDOTransaction transaction = null;
      OMMonitor monitor = new Monitor();

      try
      {
        transaction = localSession.openTransaction();
        ISession repoSession = localRepository.getSessionManager().getSession(localSession.getSessionID());
        ITransaction repoTransaction = (ITransaction)repoSession.getView(transaction.getViewID());

        IStoreAccessor.Raw accessor = getLocalWriter(repoTransaction);
        StoreThreadLocal.setAccessor(accessor);

        monitor.begin(idMappings.size() * 2 + adjustedObjects.size() * 2 + 10);

        for (Entry<CDOID, CDOID> entry : idMappings.entrySet())
        {
          CDOID id = entry.getKey();

          InternalCDORevision revision = accessor.readRevision(id, head, CDORevision.UNCHUNKED, null);
          int version = revision.getVersion();
          CDOBranch branch = revision.getBranch();
          EClass eClass = revision.getEClass();

          CDOID newID = entry.getValue();
          revision.setID(newID);
          revision.setVersion(CDORevision.FIRST_VERSION);

          accessor.rawDelete(id, version, branch, eClass, monitor.fork());
          revision.adjustReferences(idMapper);
          accessor.rawStore(revision, monitor.fork());
        }

        for (CDOID id : adjustedObjects)
        {
          InternalCDORevision revision = accessor.readRevision(id, head, CDORevision.UNCHUNKED, null);
          int version = revision.getVersion();
          CDOBranch branch = revision.getBranch();
          EClass eClass = revision.getEClass();

          // TODO DBStoreAccessor.rawDelete() creates a new row with -(version+1)!!!
          accessor.rawDelete(id, version, branch, eClass, monitor.fork());
          revision.adjustReferences(idMapper);
          accessor.rawStore(revision, monitor.fork());
        }

        accessor.rawCommit(1, monitor.fork(10));
      }
      finally
      {
        monitor.done();
        StoreThreadLocal.release();

        if (transaction != null)
        {
          transaction.close();
        }
      }
    }
  }

  public CDOChangeSetData compare(String branchPath)
  {
    return compare(branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public CDOChangeSetData compare(String branchPath, long timeStamp)
  {
    // TODO: implement CDOWorkspaceImpl.compare(branchPath, timeStamp)
    throw new UnsupportedOperationException();
  }

  public synchronized void close()
  {
    LifecycleUtil.deactivate(localSession);
    localSession = null;

    LifecycleUtil.deactivate(localRepository);
    localRepository = null;

    LifecycleUtil.deactivate(container);
    container = null;
  }

  public synchronized boolean isClosed()
  {
    return container == null;
  }

  public CDORevision getRevision(CDOID id)
  {
    InternalCDOSession session = getLocalSession();
    CDORevisionManager revisionManager = session.getRevisionManager();
    return revisionManager.getRevision(id, head, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  public InternalRepository getLocalRepository()
  {
    return localRepository;
  }

  public synchronized InternalCDOSession getLocalSession()
  {
    if (localSession == null)
    {
      localSession = openLocalSession();
    }

    return localSession;
  }

  public CDOChangeSetData getLocalChanges()
  {
    return getLocalChanges(true);
  }

  public CDOChangeSetData getLocalChanges(boolean forward)
  {
    Set<CDOID> ids = base.getIDs();

    if (forward)
    {
      return CDORevisionUtil.createChangeSetData(ids, base, this, true);
    }

    return CDORevisionUtil.createChangeSetData(ids, this, base, true);
  }

  public CDOSessionConfigurationFactory getRemoteSessionConfigurationFactory()
  {
    return remoteSessionConfigurationFactory;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  protected IManagedContainer createContainer(IStore local)
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    JVMUtil.prepareContainer(container);
    CDONet4jServerUtil.prepareContainer(container);
    container.activate();
    return container;
  }

  protected String getLocalAcceptorName()
  {
    return "acceptor-for-" + localRepository.getUUID();
  }

  protected IJVMAcceptor getLocalAcceptor()
  {
    String localAcceptorName = getLocalAcceptorName();
    return JVMUtil.getAcceptor(container, localAcceptorName);
  }

  protected IJVMConnector getLocalConnector()
  {
    String localAcceptorName = getLocalAcceptorName();
    return JVMUtil.getConnector(container, localAcceptorName);
  }

  protected IStoreAccessor.Raw getLocalWriter(ITransaction transaction)
  {
    return (IStoreAccessor.Raw)localRepository.getStore().getWriter(transaction);
  }

  protected InternalRepository createLocalRepository(String localRepositoryName, IStore store)
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.SUPPORTING_AUDITS, "false");
    props.put(Props.SUPPORTING_BRANCHES, "false");
    props.put(Props.ID_GENERATION_LOCATION, idGenerationLocation.toString());

    Repository repository = new Repository.Default()
    {
      @Override
      public void initMainBranch(InternalCDOBranchManager branchManager, long timeStamp)
      {
        if (idGenerationLocation == IDGenerationLocation.STORE)
        {
          // Mark the main branch local so that new objects get local IDs
          branchManager.initMainBranch(true, timeStamp);
        }
        else
        {
          super.initMainBranch(branchManager, timeStamp);
        }
      }

      @Override
      protected void initRootResource()
      {
        // Don't create the root resource as it will be checked out
        setState(State.INITIAL);
      }
    };

    repository.setName(localRepositoryName);
    repository.setStore((InternalStore)store);
    repository.setProperties(props);

    CDOServerUtil.addRepository(container, repository);
    return repository;
  }

  protected InternalCDOSession openLocalSession()
  {
    getLocalAcceptor();

    IJVMConnector connector = getLocalConnector();
    String repositoryName = localRepository.getName();

    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);
    configuration.setIDGenerator(idGenerator);
    configuration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP)); // Use repo's cache

    if (idGenerationLocation == IDGenerationLocation.STORE)
    {
      ((InternalCDOSessionConfiguration)configuration).setMainBranchLocal(true);
    }

    InternalCDOSession session = (InternalCDOSession)configuration.openNet4jSession();
    ((ISignalProtocol<?>)session.getSessionProtocol()).setTimeout(ISignalProtocol.NO_TIMEOUT);
    session.setPackageRegistry(localRepository.getPackageRegistry(false)); // Use repo's registry

    head = session.getBranchManager().getMainBranch().getHead();
    return session;
  }

  public InternalCDOView[] getViews()
  {
    synchronized (views)
    {
      return views.toArray(new InternalCDOView[views.size()]);
    }
  }

  protected InternalCDOSession openRemoteSession()
  {
    CDOSessionConfiguration configuration = remoteSessionConfigurationFactory.createSessionConfiguration();
    InternalCDOSession remoteSession = (InternalCDOSession)configuration.openSession();

    if (configuration instanceof Closeable)
    {
      Closeable closeable = (Closeable)configuration;
      closeables.put(remoteSession, closeable);
    }

    CDORepositoryInfo repositoryInfo = remoteSession.getRepositoryInfo();
    if (!repositoryInfo.isSupportingAudits())
    {
      remoteSession.close();
      throw new IllegalStateException("Remote repository does not support auditing");
    }

    IDGenerationLocation remoteLocation = repositoryInfo.getIDGenerationLocation();
    if (!remoteLocation.equals(idGenerationLocation))
    {
      remoteSession.close();
      throw new IllegalStateException("Remote repository uses different ID generation location: " + remoteLocation);
    }

    InternalCDOBranch branch = remoteSession.getBranchManager().getBranch(branchID);
    String pathName = branch.getPathName();
    if (!ObjectUtil.equals(pathName, branchPath))
    {
      branchPath = pathName;
      saveProperties();
    }

    return remoteSession;
  }

  protected void closeRemoteSession(InternalCDOSession remoteSession)
  {
    Closeable closeable = closeables.remove(remoteSession);
    if (closeable != null)
    {
      closeable.close();
    }
    else
    {
      LifecycleUtil.deactivate(remoteSession);
    }
  }

  protected void setTimeStamp(long timeStamp)
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(PROP_TIME_STAMP, String.valueOf(timeStamp));
    localRepository.getStore().setPersistentProperties(props);

    this.timeStamp = timeStamp;
  }

  protected void saveProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(PROP_BRANCH_ID, String.valueOf(branchID));
    props.put(PROP_BRANCH_PATH, branchPath);
    props.put(PROP_TIME_STAMP, String.valueOf(timeStamp));
    props.put(PROP_FIXED, String.valueOf(fixed));
    localRepository.getStore().setPersistentProperties(props);
  }

  protected void loadProperties()
  {
    Set<String> names = new HashSet<String>(
        Arrays.asList(PROP_BRANCH_ID, PROP_BRANCH_PATH, PROP_TIME_STAMP, PROP_FIXED));
    Map<String, String> props = localRepository.getStore().getPersistentProperties(names);
    String prop = props.get(PROP_BRANCH_ID);
    branchID = prop == null ? InternalCDOWorkspace.NO_BRANCH_ID : Integer.parseInt(prop);
    branchPath = props.get(PROP_BRANCH_PATH);
    timeStamp = Long.parseLong(props.get(PROP_TIME_STAMP));
    fixed = Boolean.parseBoolean(props.get(PROP_FIXED));
  }

  /**
   * @author Eike Stepper
   */
  private class BaseRevisionProvider implements CDORevisionProvider
  {
    public CDORevision getRevision(CDOID id)
    {
      CDORevision revision = base.getRevision(id);
      if (revision == null)
      {
        revision = CDOWorkspaceImpl.this.getRevision(id);
      }

      return revision;
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class ViewAdapter extends LifecycleEventAdapter
  {
    public ViewAdapter()
    {
    }

    public CDOWorkspace getWorkspace()
    {
      return CDOWorkspaceImpl.this;
    }

    @Override
    protected void onDeactivated(ILifecycle view)
    {
      synchronized (views)
      {
        views.remove(view);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DirtyStateChangedEventImpl extends Event implements DirtyStateChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private boolean dirty;

    public DirtyStateChangedEventImpl(CDOWorkspace workspace, boolean dirty)
    {
      super(workspace);
      this.dirty = dirty;
    }

    public boolean isDirty()
    {
      return dirty;
    }
  }
}
