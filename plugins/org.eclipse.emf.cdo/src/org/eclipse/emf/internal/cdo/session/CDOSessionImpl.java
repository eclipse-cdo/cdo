/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOCloningContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.OptionsEvent;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionConfiguration;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionImpl extends Container<CDOView> implements InternalCDOSession
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, CDOSessionImpl.class);

  private static final long NO_TIMEOUT = -1;

  private InternalCDOSessionConfiguration configuration;

  private ExceptionHandler exceptionHandler;

  private CDOSessionProtocol sessionProtocol;

  @ExcludeFromDump
  private IListener sessionProtocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      CDOSessionImpl.this.deactivate();
    }
  };

  private int sessionID;

  private String userID;

  private long lastUpdateTime;

  @ExcludeFromDump
  private Object lastUpdateTimeLock = new Object();

  private CDOSession.Options options = createOptions();

  private CDORepositoryInfo repositoryInfo;

  private CDOFetchRuleManager ruleManager = CDOFetchRuleManager.NOOP;

  private IRWLockManager<CDOSessionImpl, Object> lockmanager = new RWLockManager<CDOSessionImpl, Object>();

  @ExcludeFromDump
  private Set<CDOSessionImpl> singletonCollection = Collections.singleton(this);

  private CDOAuthenticator authenticator;

  private InternalCDORemoteSessionManager remoteSessionManager;

  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  /**
   * Fixes threading problems between a committing thread and the Net4j thread that delivers incoming commit
   * notifications. The same applies to lock requests and invalidations
   */
  @ExcludeFromDump
  private Object invalidationLock = new Object();

  @ExcludeFromDump
  private QueueRunner invalidationRunner;

  @ExcludeFromDump
  private Object invalidationRunnerLock = new Object();

  @ExcludeFromDump
  private static ThreadLocal<Boolean> invalidationRunnerActive = new InheritableThreadLocal<Boolean>();

  @ExcludeFromDump
  private int lastViewID;

  public CDOSessionImpl(InternalCDOSessionConfiguration configuration)
  {
    this.configuration = configuration;
  }

  public InternalCDOSessionConfiguration getConfiguration()
  {
    return configuration;
  }

  public CDORepositoryInfo getRepositoryInfo()
  {
    return repositoryInfo;
  }

  public void setRepositoryInfo(CDORepositoryInfo repositoryInfo)
  {
    this.repositoryInfo = repositoryInfo;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public void setSessionID(int sessionID)
  {
    this.sessionID = sessionID;
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  public ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    checkInactive();
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * @since 2.0
   */
  public CDOSession.Options options()
  {
    return options;
  }

  /**
   * @since 2.0
   */
  protected CDOSession.Options createOptions()
  {
    return new OptionsImpl();
  }

  public CDOSessionProtocol getSessionProtocol()
  {
    return sessionProtocol;
  }

  public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
  {
    this.sessionProtocol = sessionProtocol;
  }

  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  public Object processPackage(Object value)
  {
    CDOFactoryImpl.prepareDynamicEPackage(value);
    return value;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    if (packageUnit.getOriginalType().isGenerated())
    {
      if (!options().isGeneratedPackageEmulationEnabled())
      {
        throw new CDOException(MessageFormat.format(Messages.getString("CDOSessionImpl.0"), packageUnit)); //$NON-NLS-1$
      }
    }

    return getSessionProtocol().loadPackages(packageUnit);
  }

  public void acquireAtomicRequestLock(Object key)
  {
    try
    {
      lockmanager.lock(LockType.WRITE, key, this, RWLockManager.WAIT);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public void releaseAtomicRequestLock(Object key)
  {
    lockmanager.unlock(LockType.WRITE, key, singletonCollection);
  }

  /**
   * @since 3.0
   */
  public CDOFetchRuleManager getFetchRuleManager()
  {
    return ruleManager;
  }

  /**
   * @since 3.0
   */
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
  {
    ruleManager = fetchRuleManager;
  }

  public CDOAuthenticator getAuthenticator()
  {
    return authenticator;
  }

  public void setAuthenticator(CDOAuthenticator authenticator)
  {
    this.authenticator = authenticator;
  }

  public InternalCDORemoteSessionManager getRemoteSessionManager()
  {
    return remoteSessionManager;
  }

  public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager)
  {
    this.remoteSessionManager = remoteSessionManager;
  }

  public InternalCDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction(branch);
    initView(transaction, resourceSet);
    return transaction;
  }

  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    return openTransaction(getBranchManager().getMainBranch(), resourceSet);
  }

  public InternalCDOTransaction openTransaction(CDOBranch branch)
  {
    return openTransaction(branch, createResourceSet());
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction openTransaction()
  {
    return openTransaction(getBranchManager().getMainBranch());
  }

  /**
   * @since 2.0
   */
  protected InternalCDOTransaction createTransaction(CDOBranch branch)
  {
    return new CDOTransactionImpl(branch);
  }

  public InternalCDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView(branch, timeStamp);
    initView(view, resourceSet);
    return view;
  }

  public InternalCDOView openView(CDOBranch branch, long timeStamp)
  {
    return openView(branch, timeStamp, createResourceSet());
  }

  public InternalCDOView openView(CDOBranch branch)
  {
    return openView(branch, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public InternalCDOView openView(long timeStamp)
  {
    return openView(getBranchManager().getMainBranch(), timeStamp);
  }

  /**
   * @since 2.0
   */
  public InternalCDOView openView()
  {
    return openView(CDOBranchPoint.UNSPECIFIED_DATE);
  }

  /**
   * @since 2.0
   */
  protected InternalCDOView createView(CDOBranch branch, long timeStamp)
  {
    return new CDOViewImpl(branch, timeStamp);
  }

  /**
   * @since 2.0
   */
  public void viewDetached(InternalCDOView view)
  {
    // Detach viewset from the view
    view.getViewSet().remove(view);
    synchronized (views)
    {
      if (!views.remove(view))
      {
        return;
      }
    }

    if (isActive())
    {
      try
      {
        LifecycleUtil.deactivate(view);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    fireElementRemovedEvent(view);
  }

  public CDOView getView(int viewID)
  {
    checkActive();
    for (InternalCDOView view : getViews())
    {
      if (view.getViewID() == viewID)
      {
        return view;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView[] getViews()
  {
    checkActive();
    synchronized (views)
    {
      return views.toArray(new InternalCDOView[views.size()]);
    }
  }

  public CDOView[] getElements()
  {
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    return views.isEmpty();
  }

  /**
   * @since 2.0
   */
  public long refresh()
  {
    checkActive();
    if (!options().isPassiveUpdateEnabled())
    {
      return refresh(false);
    }

    return 0;
  }

  private long refresh(boolean enablePassiveUpdates)
  {
    Map<CDOBranch, List<InternalCDOView>> views = new HashMap<CDOBranch, List<InternalCDOView>>();
    Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions = new HashMap<CDOBranch, Map<CDOID, InternalCDORevision>>();
    collectViewedRevisions(views, viewedRevisions);

    if (viewedRevisions.isEmpty())
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    long lastUpdateTime = getLastUpdateTime();
    int initialChunkSize = options().getCollectionLoadingPolicy().getInitialChunkSize();

    RefreshSessionResult result = sessionProtocol.refresh(lastUpdateTime, viewedRevisions, initialChunkSize,
        enablePassiveUpdates);

    lastUpdateTime = result.getLastUpdateTime();
    registerPackageUnits(result.getPackageUnits());

    for (Entry<CDOBranch, List<InternalCDOView>> entry : views.entrySet())
    {
      CDOBranch branch = entry.getKey();
      List<InternalCDOView> branchViews = entry.getValue();

      Map<CDOID, InternalCDORevision> oldRevisions = viewedRevisions.get(branch);

      List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();
      for (InternalCDORevision newRevision : result.getChangedObjects(branch))
      {
        getRevisionManager().addRevision(newRevision);

        InternalCDORevision oldRevision = oldRevisions.get(newRevision.getID());
        InternalCDORevisionDelta delta = newRevision.compare(oldRevision);
        changedObjects.add(delta);
      }

      List<CDOIDAndVersion> detachedObjects = result.getDetachedObjects(branch);
      for (CDOIDAndVersion detachedObject : detachedObjects)
      {
        getRevisionManager().reviseLatest(detachedObject.getID(), branch);
      }

      for (InternalCDOView view : branchViews)
      {
        view.invalidate(lastUpdateTime, changedObjects, detachedObjects);
      }
    }

    setLastUpdateTime(lastUpdateTime);
    return lastUpdateTime;
  }

  private void collectViewedRevisions(Map<CDOBranch, List<InternalCDOView>> views,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
  {
    for (InternalCDOView view : getViews())
    {
      if (view.getTimeStamp() == CDOView.UNSPECIFIED_DATE)
      {
        CDOBranch branch = view.getBranch();
        Map<CDOID, InternalCDORevision> revisions = viewedRevisions.get(branch);
        boolean needNewMap = revisions == null;
        if (needNewMap)
        {
          revisions = new HashMap<CDOID, InternalCDORevision>();
        }

        view.collectViewedRevisions(revisions);
        if (!revisions.isEmpty())
        {
          List<InternalCDOView> list = views.get(branch);
          if (list == null)
          {
            list = new ArrayList<InternalCDOView>();
            views.put(branch, list);
          }

          list.add(view);

          if (needNewMap)
          {
            viewedRevisions.put(branch, revisions);
          }
        }
      }
    }
  }

  public long getLastUpdateTime()
  {
    synchronized (lastUpdateTimeLock)
    {
      return lastUpdateTime;
    }
  }

  public void setLastUpdateTime(long lastUpdateTime)
  {
    synchronized (lastUpdateTimeLock)
    {
      if (this.lastUpdateTime < lastUpdateTime)
      {
        this.lastUpdateTime = lastUpdateTime;
      }

      lastUpdateTimeLock.notifyAll();
    }
  }

  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    long end = timeoutMillis == NO_TIMEOUT ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMillis;
    for (;;)
    {
      synchronized (lastUpdateTimeLock)
      {
        if (lastUpdateTime >= updateTime)
        {
          return true;
        }

        long now = System.currentTimeMillis();
        if (now >= end)
        {
          return false;
        }

        try
        {
          lastUpdateTimeLock.wait(end - now);
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }
  }

  /**
   * @since 3.0
   */
  public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    CDOCollectionLoadingPolicy policy = options().getCollectionLoadingPolicy();
    return policy.resolveProxy(this, revision, feature, accessIndex, serverIndex);
  }

  public void handleBranchNotification(InternalCDOBranch branch)
  {
    getBranchManager().handleBranchCreated(branch);
  }

  // /**
  // * @since 2.0
  // */
  // @Deprecated
  // public void handleSyncResponse(CDOBranchPoint branchPoint, Collection<CDOPackageUnit> newPackageUnits,
  // Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects)
  // {
  // handleCommitNotification___OLD(branchPoint, newPackageUnits, dirtyOIDs, detachedObjects, null, null, true, false);
  // }
  //
  // @Deprecated
  // private void handleCommitNotification___OLD(CDOBranchPoint branchPoint,
  // final Collection<CDOPackageUnit> newPackageUnits, Set<CDOIDAndVersion> dirtyOIDs,
  // final Collection<CDOID> detachedObjects, final Collection<CDORevisionDelta> deltas, InternalCDOView excludedView,
  // final boolean reviseAndInvalidate, final boolean async)
  // {
  // final CDOBranch branch = branchPoint.getBranch();
  // final long timeStamp = branchPoint.getTimeStamp();
  //
  // try
  // {
  // synchronized (invalidationLock)
  // {
  // if (reviseAndInvalidate)
  // {
  // reviseRevisions(branchPoint, dirtyOIDs, detachedObjects, excludedView);
  // }
  //
  // final Set<CDOIDAndVersion> finalDirtyOIDs = Collections.unmodifiableSet(dirtyOIDs);
  // final Collection<CDOID> finalDetachedObjects = Collections.unmodifiableCollection(detachedObjects);
  // final boolean skipChangeSubscription = (deltas == null || deltas.size() <= 0)
  // && (detachedObjects == null || detachedObjects.size() <= 0);
  //
  // for (final InternalCDOView view : getViews())
  // {
  // if (view != excludedView && view.getBranch().equals(branch))
  // {
  // final Runnable runnable = new Runnable()
  // {
  // public void run()
  // {
  // try
  // {
  // // Set<CDOObject> conflicts = null;
  // // if (reviseAndInvalidate)
  // // {
  // // conflicts = view.handleInvalidation(timeStamp, finalDirtyOIDs, finalDetachedObjects, async);
  // // }
  // //
  // // if (!skipChangeSubscription)
  // // {
  // // view.handleChangeSubscription(deltas, detachedObjects, async);
  // // }
  // //
  // // if (conflicts != null)
  // // {
  // // ((InternalCDOTransaction)view).handleConflicts(conflicts);
  // // }
  // //
  // // view.fireAdaptersNotifiedEvent(timeStamp);
  // }
  // catch (RuntimeException ex)
  // {
  // if (!async)
  // {
  // throw ex;
  // }
  //
  // if (view.isActive())
  // {
  // OM.LOG.error(ex);
  // }
  // else
  // {
  //                    OM.LOG.info(Messages.getString("CDOSessionImpl.1")); //$NON-NLS-1$
  // }
  // }
  // }
  // };
  //
  // if (async)
  // {
  // QueueRunner runner = getInvalidationRunner();
  // runner.addWork(new Runnable()
  // {
  // public void run()
  // {
  // try
  // {
  // invalidationRunnerActive.set(true);
  // runnable.run();
  // }
  // finally
  // {
  // invalidationRunnerActive.set(false);
  // }
  // }
  // });
  // }
  // else
  // {
  // runnable.run();
  // }
  // }
  // }
  // }
  // }
  // catch (RuntimeException ex)
  // {
  // if (!async)
  // {
  // throw ex;
  // }
  //
  // if (isActive())
  // {
  // OM.LOG.error(ex);
  // }
  // else
  // {
  //        OM.LOG.info(Messages.getString("CDOSessionImpl.2")); //$NON-NLS-1$
  // }
  // }
  //
  // setLastUpdateTime(timeStamp);
  // throw new UnsupportedOperationException();
  // // fireInvalidationEvent(branchPoint, newPackageUnits, dirtyOIDs, detachedObjects, excludedView);
  // }
  //
  // @Deprecated
  // private void reviseRevisions(CDOBranchPoint branchPoint, Set<CDOIDAndVersion> dirtyOIDs,
  // Collection<CDOID> detachedObjects, InternalCDOView excludedView)
  // {
  // InternalCDORevisionManager revisionManager = getRevisionManager();
  // CDOBranch branch = branchPoint.getBranch();
  // long timeStamp = branchPoint.getTimeStamp();
  //
  // if (excludedView == null || timeStamp == CDORevision.UNSPECIFIED_DATE)
  // {
  // for (CDOIDAndVersion dirtyOID : dirtyOIDs)
  // {
  // CDOID id = dirtyOID.getID();
  // int version = dirtyOID.getVersion();
  // revisionManager.reviseVersion(id, branch.getVersion(version), timeStamp);
  // }
  // }
  //
  // for (CDOID id : detachedObjects)
  // {
  // revisionManager.reviseLatest(id, branch);
  // }
  // }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(CDOCommitInfo commitInfo)
  {
    try
    {
      synchronized (invalidationLock)
      {
        registerPackageUnits(commitInfo.getNewPackageUnits());
        invalidate(commitInfo, null);
      }
    }
    catch (RuntimeException ex)
    {
      if (isActive())
      {
        OM.LOG.error(ex);
      }
      else
      {
        OM.LOG.info(Messages.getString("CDOSessionImpl.2")); //$NON-NLS-1$
      }
    }
  }

  private void registerPackageUnits(List<CDOPackageUnit> packageUnits)
  {
    InternalCDOPackageRegistry packageRegistry = getPackageRegistry();
    for (CDOPackageUnit newPackageUnit : packageUnits)
    {
      packageRegistry.putPackageUnit((InternalCDOPackageUnit)newPackageUnit);
    }
  }

  private void reviseRevisions(CDOCommitInfo commitInfo)
  {
    CDOBranch newBranch = commitInfo.getBranch();
    long timeStamp = commitInfo.getTimeStamp();
    InternalCDORevisionManager revisionManager = getRevisionManager();

    // Cache new revisions
    for (CDOIDAndVersion key : commitInfo.getNewObjects())
    {
      if (key instanceof InternalCDORevision)
      {
        InternalCDORevision newRevision = (InternalCDORevision)key;
        revisionManager.addRevision(newRevision);
      }
    }

    // Apply deltas and cache the resulting new revisions, if possible...
    for (CDORevisionKey key : commitInfo.getChangedObjects())
    {
      InternalCDORevision newRevision = createNewRevision(key, commitInfo);
      if (newRevision != null)
      {
        revisionManager.addRevision(newRevision);
      }
      else
      {
        // ... otherwise try to revise old revision if it is in the same branch
        if (key.getBranch() == newBranch)
        {
          revisionManager.reviseVersion(key.getID(), key, timeStamp);
        }
      }
    }

    // Revise old revisions
    for (CDOIDAndVersion key : commitInfo.getDetachedObjects())
    {
      CDOID id = key.getID();
      int version = key.getVersion();
      if (version == CDOBranchVersion.UNSPECIFIED_VERSION)
      {
        revisionManager.reviseLatest(id, newBranch);
      }
      else
      {
        CDOBranchVersion branchVersion = newBranch.getVersion(version);
        revisionManager.reviseVersion(id, branchVersion, timeStamp);
      }
    }
  }

  private InternalCDORevision createNewRevision(CDORevisionKey potentialDelta, CDOCommitInfo commitInfo)
  {
    if (potentialDelta instanceof CDORevisionDelta)
    {
      CDORevisionDelta delta = (CDORevisionDelta)potentialDelta;
      CDOID id = delta.getID();

      InternalCDORevisionManager revisionManager = getRevisionManager();
      InternalCDORevision oldRevision = revisionManager.getRevisionByVersion(id, potentialDelta, CDORevision.UNCHUNKED,
          false);
      if (oldRevision != null)
      {
        InternalCDORevision newRevision = oldRevision.copy();
        newRevision.adjustForCommit(commitInfo.getBranch(), commitInfo.getTimeStamp());
        delta.apply(newRevision);
        return newRevision;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender)
  {
    reviseRevisions(commitInfo);

    for (InternalCDOView view : getViews())
    {
      if (view != sender && view.getBranch() == commitInfo.getBranch())
      {
        QueueRunner runner = getInvalidationRunner();
        runner.addWork(new InvalidationRunnable(view, commitInfo));
      }
    }

    setLastUpdateTime(commitInfo.getTimeStamp());
    fireInvalidationEvent(sender, commitInfo);
  }

  public void cloneRepository(CDOCloningContext context)
  {
    getSessionProtocol().cloneRepository(context);
  }

  public Object getInvalidationLock()
  {
    return invalidationLock;
  }

  private QueueRunner getInvalidationRunner()
  {
    synchronized (invalidationRunnerLock)
    {
      if (invalidationRunner == null)
      {
        invalidationRunner = createInvalidationRunner();
        invalidationRunner.activate();
      }
    }

    return invalidationRunner;
  }

  protected QueueRunner createInvalidationRunner()
  {
    return new QueueRunner()
    {
      @Override
      protected String getThreadName()
      {
        return "InvalidationRunner"; //$NON-NLS-1$
      }

      @Override
      public String toString()
      {
        return getThreadName();
      }
    };
  }

  /**
   * @since 2.0
   */
  public void fireInvalidationEvent(InternalCDOView excludedView, CDOCommitInfo commitInfo)
  {
    fireEvent(new InvalidationEvent(excludedView, commitInfo));
  }

  @Override
  public String toString()
  {
    String name = repositoryInfo == null ? "?" : repositoryInfo.getName(); //$NON-NLS-1$
    return MessageFormat.format("CDOSession[{0}, {1}]", name, sessionID); //$NON-NLS-1$
  }

  protected ResourceSet createResourceSet()
  {
    return new ResourceSetImpl();
  }

  /**
   * @since 2.0
   */
  protected void initView(InternalCDOView view, ResourceSet resourceSet)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Initializing new {0} view", //$NON-NLS-1$
          view.isReadOnly() ? (view.getTimeStamp() != CDOView.UNSPECIFIED_DATE ? "historical" : "read-only") //$NON-NLS-1$ //$NON-NLS-2$
              : "transactional"); //$NON-NLS-1$
    }

    InternalCDOViewSet viewSet = SessionUtil.prepareResourceSet(resourceSet);
    synchronized (views)
    {
      view.setSession(this);
      view.setViewID(++lastViewID);
      views.add(view);
    }

    // Link ViewSet with View
    view.setViewSet(viewSet);
    viewSet.add(view);

    try
    {
      view.activate();
      fireElementAddedEvent(view);
    }
    catch (RuntimeException ex)
    {
      synchronized (views)
      {
        views.remove(view);
      }

      viewSet.remove(view);
      throw ex;
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    getConfiguration().activateSession(this);
    checkState(sessionProtocol, "sessionProtocol"); //$NON-NLS-1$
    checkState(remoteSessionManager, "remoteSessionManager"); //$NON-NLS-1$
    if (exceptionHandler != null)
    {
      sessionProtocol = new DelegatingSessionProtocol(sessionProtocol);
    }

    EventUtil.addListener(sessionProtocol, sessionProtocolListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (InternalCDOView view : views.toArray(new InternalCDOView[views.size()]))
    {
      try
      {
        view.close();
      }
      catch (RuntimeException ignore)
      {
      }
    }

    views.clear();

    if (invalidationRunner != null)
    {
      LifecycleUtil.deactivate(invalidationRunner, OMLogger.Level.WARN);
      invalidationRunner = null;
    }

    EventUtil.removeListener(sessionProtocol, sessionProtocolListener);
    getConfiguration().deactivateSession(this);
    super.doDeactivate();
  }

  // private Map<CDOID, CDOIDAndVersion> getAllCDOIDAndVersion()
  // {
  // Map<CDOID, CDOIDAndVersion> uniqueObjects = new HashMap<CDOID, CDOIDAndVersion>();
  // for (InternalCDOView view : getViews())
  // {
  // view.getCDOIDAndVersion(uniqueObjects, Arrays.asList(view.getObjectsArray()));
  // }
  //
  // // Need to add Revision from revisionManager since we do not have all objects in view.
  // for (CDORevision revision : getRevisionManager().getCache().getCurrentRevisions())
  // {
  // if (!uniqueObjects.containsKey(revision.getID()))
  // {
  // uniqueObjects.put(revision.getID(), CDOIDUtil.createIDAndVersion(revision.getID(), revision.getVersion()));
  // }
  // }
  //
  // return uniqueObjects;
  // }

  public static boolean isInvalidationRunnerActive()
  {
    return invalidationRunnerActive.get();
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean generatedPackageEmulationEnabled;

    private boolean passiveUpdateEnabled = true;

    private CDOCollectionLoadingPolicy collectionLoadingPolicy = CDOCollectionLoadingPolicy.DEFAULT;

    public OptionsImpl()
    {
    }

    public IOptionsContainer getContainer()
    {
      return CDOSessionImpl.this;
    }

    public boolean isGeneratedPackageEmulationEnabled()
    {
      return generatedPackageEmulationEnabled;
    }

    public synchronized void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled)
    {
      this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
      if (this.generatedPackageEmulationEnabled != generatedPackageEmulationEnabled)
      {
        this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
        // TODO Check inconsistent state if switching off?

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new GeneratedPackageEmulationEventImpl(), listeners);
        }
      }
    }

    public boolean isPassiveUpdateEnabled()
    {
      return passiveUpdateEnabled;
    }

    public synchronized int setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
      int result = 0;
      if (this.passiveUpdateEnabled != passiveUpdateEnabled)
      {
        this.passiveUpdateEnabled = passiveUpdateEnabled;
        if (passiveUpdateEnabled)
        {
          refresh(true);
        }
        else
        {
          getSessionProtocol().disablePassiveUpdates();
        }

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new PassiveUpdateEventImpl(), listeners);
        }
      }

      return result;
    }

    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      return collectionLoadingPolicy;
    }

    public synchronized void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy)
    {
      if (policy == null)
      {
        policy = CDOCollectionLoadingPolicy.DEFAULT;
      }

      if (collectionLoadingPolicy != policy)
      {
        collectionLoadingPolicy = policy;
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new CollectionLoadingPolicyEventImpl(), listeners);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class GeneratedPackageEmulationEventImpl extends OptionsEvent implements
        GeneratedPackageEmulationEvent
    {
      private static final long serialVersionUID = 1L;

      public GeneratedPackageEmulationEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class PassiveUpdateEventImpl extends OptionsEvent implements PassiveUpdateEvent
    {
      private static final long serialVersionUID = 1L;

      public PassiveUpdateEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class CollectionLoadingPolicyEventImpl extends OptionsEvent implements CollectionLoadingPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public CollectionLoadingPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class InvalidationRunnable implements Runnable
  {
    private InternalCDOView view;

    private CDOCommitInfo commitInfo;

    private InvalidationRunnable(InternalCDOView view, CDOCommitInfo commitInfo)
    {
      this.view = view;
      this.commitInfo = commitInfo;
    }

    public void run()
    {
      try
      {
        invalidationRunnerActive.set(true);
        long lastUpdateTime = commitInfo.getTimeStamp();
        List<CDORevisionKey> allChangedObjects = commitInfo.getChangedObjects();
        List<CDOIDAndVersion> allDetachedObjects = commitInfo.getDetachedObjects();
        view.invalidate(lastUpdateTime, allChangedObjects, allDetachedObjects);
      }
      catch (RuntimeException ex)
      {
        if (view.isActive())
        {
          OM.LOG.error(ex);
        }
        else
        {
          OM.LOG.info(Messages.getString("CDOSessionImpl.1")); //$NON-NLS-1$
        }
      }
      finally
      {
        invalidationRunnerActive.set(false);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class InvalidationEvent extends Event implements CDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOView view;

    private CDOCommitInfo commitInfo;

    public InvalidationEvent(InternalCDOView view, CDOCommitInfo commitInfo)
    {
      super(CDOSessionImpl.this);
      this.view = view;
      this.commitInfo = commitInfo;
    }

    @Override
    public CDOSession getSource()
    {
      return (CDOSession)super.getSource();
    }

    public CDOCommitInfoManager getCommitInfoManager()
    {
      return commitInfo.getCommitInfoManager();
    }

    public InternalCDOView getView()
    {
      return view;
    }

    public boolean isRemote()
    {
      return view == null;
    }

    public CDOBranch getBranch()
    {
      return commitInfo.getBranch();
    }

    public long getTimeStamp()
    {
      return commitInfo.getTimeStamp();
    }

    public String getUserID()
    {
      return commitInfo.getUserID();
    }

    public String getComment()
    {
      return commitInfo.getComment();
    }

    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return commitInfo.getNewPackageUnits();
    }

    public List<CDOIDAndVersion> getNewObjects()
    {
      return commitInfo.getNewObjects();
    }

    public List<CDORevisionKey> getChangedObjects()
    {
      return commitInfo.getChangedObjects();
    }

    public List<CDOIDAndVersion> getDetachedObjects()
    {
      return commitInfo.getDetachedObjects();
    }

    public int compareTo(CDOBranchPoint o)
    {
      return commitInfo.compareTo(o);
    }

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent[" + commitInfo + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * @author Eike Stepper
   */
  public class DelegatingSessionProtocol extends Lifecycle implements CDOSessionProtocol
  {
    private CDOSessionProtocol delegate;

    @ExcludeFromDump
    private IListener delegateListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        DelegatingSessionProtocol.this.deactivate();
      }
    };

    public DelegatingSessionProtocol(CDOSessionProtocol delegate)
    {
      this.delegate = delegate;
      activate();
    }

    public CDOSessionProtocol getDelegate()
    {
      return delegate;
    }

    public CDOSession getSession()
    {
      return (CDOSession)delegate.getSession();
    }

    public boolean cancelQuery(int queryId)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.cancelQuery(queryId);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void changeSubscription(int viewID, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.changeSubscription(viewID, cdoIDs, subscribeMode, clear);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void openView(int viewID, CDOBranchPoint branchPoint, boolean readOnly)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.openView(viewID, branchPoint, readOnly);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public boolean[] changeView(int viewID, CDOBranchPoint branchPoint, List<InternalCDOObject> invalidObjects)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.changeView(viewID, branchPoint, invalidObjects);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void closeView(int viewID)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.closeView(viewID);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransaction(commitContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionCancel(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionPhase1(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionPhase2(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionPhase3(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public RepositoryTimeResult getRepositoryTime()
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.getRepositoryTime();
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.isObjectLocked(view, object, lockType, byOthers);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public EPackage[] loadPackages(CDOPackageUnit packageUnit)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadPackages(packageUnit);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public int createBranch(BranchInfo branchInfo)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.createBranch(branchInfo);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public BranchInfo loadBranch(int branchID)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadBranch(branchID);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public SubBranchInfo[] loadSubBranches(int branchID)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadSubBranches(branchID);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.loadCommitInfos(branch, startTime, endTime, handler);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CDOCommitData loadCommitData(long timeStamp)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadCommitData(timeStamp);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
        int fromIndex, int toIndex)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadChunk(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint,
        int referenceChunk, int prefetchDepth)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisionByVersion(id, branchVersion, referenceChunk);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void lockObjects(long lastUpdateTime, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions,
        int viewID, LockType lockType, long timeout) throws InterruptedException
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.lockObjects(lastUpdateTime, viewedRevisions, viewID, lockType, timeout);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void query(int viewID, AbstractQueryIterator<?> queryResult)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.query(viewID, queryResult);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void disablePassiveUpdates()
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.disablePassiveUpdates();
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public RefreshSessionResult refresh(long lastUpdateTime,
        Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
        boolean enablePassiveUpdates)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.refresh(lastUpdateTime, viewedRevisions, initialChunkSize, enablePassiveUpdates);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.unlockObjects(view, objects, lockType);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.getRemoteSessions(manager, subscribe);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public Set<Integer> sendRemoteMessage(CDORemoteSessionMessage message, List<CDORemoteSession> recipients)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.sendRemoteMessage(message, recipients);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public boolean unsubscribeRemoteSessions()
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.unsubscribeRemoteSessions();
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void cloneRepository(CDOCloningContext context)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.cloneRepository(context);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();
      EventUtil.addListener(delegate, delegateListener);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      EventUtil.removeListener(delegate, delegateListener);
      LifecycleUtil.deactivate(delegate);
      delegate = null;
      super.doDeactivate();
    }

    private void handleException(int attempt, Exception exception)
    {
      try
      {
        getExceptionHandler().handleException(CDOSessionImpl.this, attempt, exception);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }
}
