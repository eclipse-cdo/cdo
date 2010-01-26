/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.view.CDOAuditImpl;
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
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
   * Fixes a threading problem between a committing thread and the Net4j thread that delivers incoming commit
   * notifications. TODO This is a workaround, see Bug 294700
   */
  private Object commitLock = new Object();

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
        throw new CDOException(MessageFormat.format(Messages.getString("CDOSessionImpl.0"), packageUnit));
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

  /**
   * @since 2.0
   */
  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction();
    initView(transaction, resourceSet);
    return transaction;
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction openTransaction()
  {
    return openTransaction(createResourceSet());
  }

  /**
   * @since 2.0
   */
  protected InternalCDOTransaction createTransaction()
  {
    return new CDOTransactionImpl();
  }

  /**
   * @since 2.0
   */
  public InternalCDOView openView(ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView();
    initView(view, resourceSet);
    return view;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView openView()
  {
    return openView(createResourceSet());
  }

  /**
   * @since 2.0
   */
  protected InternalCDOView createView()
  {
    return new CDOViewImpl();
  }

  public CDOAuditImpl openAudit(ResourceSet resourceSet, long timeStamp)
  {
    checkActive();
    CDOAuditImpl audit = createAudit(timeStamp);
    initView(audit, resourceSet);
    return audit;
  }

  public CDOAuditImpl openAudit(long timeStamp)
  {
    return openAudit(createResourceSet(), timeStamp);
  }

  /**
   * @since 2.0
   */
  protected CDOAuditImpl createAudit(long timeStamp)
  {
    return new CDOAuditImpl(timeStamp);
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
  public Collection<CDOTimeStampContext> refresh()
  {
    // If passive update is turned on we don`t need to refresh.
    // We do not throw an exception since the client could turn
    // that feature on or off without affecting their code.
    checkActive();
    if (!options().isPassiveUpdateEnabled())
    {
      Map<CDOID, CDOIDAndVersion> allRevisions = getAllCDOIDAndVersion();

      try
      {
        if (!allRevisions.isEmpty())
        {
          int initialChunkSize = options().getCollectionLoadingPolicy().getInitialChunkSize();
          return getSessionProtocol().syncRevisions(allRevisions, initialChunkSize);
        }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    return Collections.emptyList();
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
      this.lastUpdateTime = lastUpdateTime;
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

  /**
   * @since 2.0
   */
  public void handleSyncResponse(long timestamp, Collection<CDOPackageUnit> newPackageUnits,
      Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects)
  {
    handleCommitNotification(timestamp, newPackageUnits, dirtyOIDs, detachedObjects, null, null, true, false);
  }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(final long timeStamp, final Collection<CDOPackageUnit> newPackageUnits,
      Set<CDOIDAndVersion> dirtyOIDs, final Collection<CDOID> detachedObjects,
      final Collection<CDORevisionDelta> deltas, InternalCDOView excludedView)
  {
    handleCommitNotification(timeStamp, newPackageUnits, dirtyOIDs, detachedObjects, deltas, excludedView, options()
        .isPassiveUpdateEnabled(), true);
  }

  private void handleCommitNotification(final long timeStamp, final Collection<CDOPackageUnit> newPackageUnits,
      Set<CDOIDAndVersion> dirtyOIDs, final Collection<CDOID> detachedObjects,
      final Collection<CDORevisionDelta> deltas, InternalCDOView excludedView, final boolean passiveUpdate,
      final boolean async)
  {
    try
    {
      synchronized (commitLock)
      {
        if (passiveUpdate)
        {
          reviseRevisions(timeStamp, dirtyOIDs, detachedObjects, excludedView);
        }

        final Set<CDOIDAndVersion> finalDirtyOIDs = Collections.unmodifiableSet(dirtyOIDs);
        final Collection<CDOID> finalDetachedObjects = Collections.unmodifiableCollection(detachedObjects);
        final boolean skipChangeSubscription = (deltas == null || deltas.size() <= 0)
            && (detachedObjects == null || detachedObjects.size() <= 0);

        for (final InternalCDOView view : getViews())
        {
          if (view != excludedView)
          {
            final Runnable runnable = new Runnable()
            {
              public void run()
              {
                try
                {
                  Set<CDOObject> conflicts = null;
                  if (passiveUpdate)
                  {
                    conflicts = view.handleInvalidation(timeStamp, finalDirtyOIDs, finalDetachedObjects);
                  }

                  if (!skipChangeSubscription)
                  {
                    view.handleChangeSubscription(deltas, detachedObjects);
                  }

                  if (conflicts != null)
                  {
                    ((InternalCDOTransaction)view).handleConflicts(conflicts);
                  }

                  view.fireAdaptersNotifiedEvent(timeStamp);
                }
                catch (RuntimeException ex)
                {
                  if (!async)
                  {
                    throw ex;
                  }

                  if (view.isActive())
                  {
                    OM.LOG.error(ex);
                  }
                  else
                  {
                    OM.LOG.info(Messages.getString("CDOSessionImpl.1"));
                  }
                }
              }
            };

            if (async)
            {
              QueueRunner runner = getInvalidationRunner();
              runner.addWork(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    invalidationRunnerActive.set(true);
                    runnable.run();
                  }
                  finally
                  {
                    invalidationRunnerActive.set(false);
                  }
                }
              });
            }
            else
            {
              runnable.run();
            }
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      if (!async)
      {
        throw ex;
      }

      if (isActive())
      {
        OM.LOG.error(ex);
      }
      else
      {
        OM.LOG.info(Messages.getString("CDOSessionImpl.2"));
      }
    }

    setLastUpdateTime(timeStamp);
    fireInvalidationEvent(timeStamp, newPackageUnits, dirtyOIDs, detachedObjects, excludedView);
  }

  public void reviseRevisions(final long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects,
      InternalCDOView excludedView)
  {
    InternalCDORevisionManager revisionManager = getRevisionManager();
    if (excludedView == null || timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      for (CDOIDAndVersion dirtyOID : dirtyOIDs)
      {
        CDOID id = dirtyOID.getID();
        int version = dirtyOID.getVersion();
        revisionManager.reviseVersion(id, version, timeStamp);
      }
    }

    for (CDOID id : detachedObjects)
    {
      revisionManager.reviseLatest(id);
    }
  }

  public Object getCommitLock()
  {
    return commitLock;
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
        return "InvalidationRunner";
      }

      @Override
      public String toString()
      {
        return getThreadName();
      }
    };
  }

  /**
   * @param packageUnits
   * @since 2.0
   */
  public void fireInvalidationEvent(long timeStamp, Collection<CDOPackageUnit> packageUnits,
      Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects, InternalCDOView excludedView)
  {
    fireEvent(new InvalidationEvent(excludedView, timeStamp, packageUnits, dirtyOIDs, detachedObjects));
  }

  @Override
  public String toString()
  {
    String name = repositoryInfo == null ? "?" : repositoryInfo.getName();
    return MessageFormat.format("CDOSession[{0}, {1}]", name, sessionID);
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
      TRACER.format("Initializing new {0} view", view.getViewType());
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
    checkState(sessionProtocol, "sessionProtocol");
    checkState(remoteSessionManager, "remoteSessionManager");
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

  private Map<CDOID, CDOIDAndVersion> getAllCDOIDAndVersion()
  {
    Map<CDOID, CDOIDAndVersion> uniqueObjects = new HashMap<CDOID, CDOIDAndVersion>();
    for (InternalCDOView view : getViews())
    {
      view.getCDOIDAndVersion(uniqueObjects, Arrays.asList(view.getObjectsArray()));
    }

    // Need to add Revision from revisionManager since we do not have all objects in view.
    for (CDORevision revision : getRevisionManager().getCache().getRevisions())
    {
      if (!uniqueObjects.containsKey(revision.getID()))
      {
        uniqueObjects.put(revision.getID(), CDOIDUtil.createIDAndVersion(revision.getID(), revision.getVersion()));
      }
    }

    return uniqueObjects;
  }

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
    private boolean generatedPackageEmulationEnabled = false;

    private boolean passiveUpdateEnabled = true;

    private CDOCollectionLoadingPolicy collectionLoadingPolicy;

    public OptionsImpl()
    {
      // TODO Remove preferences from core
      int value = OM.PREF_COLLECTION_LOADING_CHUNK_SIZE.getValue();
      collectionLoadingPolicy = CDOUtil.createCollectionLoadingPolicy(value, value);
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

    public synchronized void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
      if (this.passiveUpdateEnabled != passiveUpdateEnabled)
      {
        this.passiveUpdateEnabled = passiveUpdateEnabled;

        // Need to refresh if we change state
        Map<CDOID, CDOIDAndVersion> allRevisions = getAllCDOIDAndVersion();
        int initialChunkSize = collectionLoadingPolicy.getInitialChunkSize();
        getSessionProtocol().setPassiveUpdate(allRevisions, initialChunkSize, passiveUpdateEnabled);

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new PassiveUpdateEventImpl(), listeners);
        }
      }
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
  private final class InvalidationEvent extends Event implements CDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOView view;

    private long timeStamp;

    private Set<CDOIDAndVersion> dirtyOIDs;

    private Collection<CDOID> detachedObjects;

    private Collection<CDOPackageUnit> newPackageUnits;

    public InvalidationEvent(InternalCDOView view, long timeStamp, Collection<CDOPackageUnit> packageUnits,
        Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects)
    {
      super(CDOSessionImpl.this);
      this.view = view;
      this.timeStamp = timeStamp;
      newPackageUnits = packageUnits;
      this.dirtyOIDs = dirtyOIDs;
      this.detachedObjects = detachedObjects;
    }

    @Override
    public CDOSession getSource()
    {
      return (CDOSession)super.getSource();
    }

    public InternalCDOView getView()
    {
      return view;
    }

    public boolean isRemote()
    {
      return view == null;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public Set<CDOIDAndVersion> getDirtyOIDs()
    {
      return dirtyOIDs;
    }

    public Collection<CDOID> getDetachedObjects()
    {
      return detachedObjects;
    }

    public Collection<CDOPackageUnit> getNewPackageUnits()
    {
      return newPackageUnits;
    }

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent: " + dirtyOIDs;
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

    public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.changeSubscription(viewId, cdoIDs, subscribeMode, clear);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void closeView(int viewId)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.closeView(viewId);
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

    public InternalCDORevision loadRevision(CDOID id, int referenceChunk, int prefetchDepth)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevision(id, referenceChunk, prefetchDepth);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, int prefetchDepth, long timeStamp)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisionByTime(id, referenceChunk, prefetchDepth, timeStamp);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int prefetchDepth, int version)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisionByVersion(id, referenceChunk, prefetchDepth, version);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk, int prefetchDepth)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisions(ids, referenceChunk, prefetchDepth);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, int prefetchDepth,
        long timeStamp)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisionsByTime(ids, referenceChunk, prefetchDepth, timeStamp);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.verifyRevision(revision, referenceChunk);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void lockObjects(CDOView view, Map<CDOID, CDOIDAndVersion> objects, long timeout, LockType lockType)
        throws InterruptedException
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.lockObjects(view, objects, timeout, lockType);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void openView(int viewId, CDOCommonView.Type viewType, long timeStamp)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.openView(viewId, viewType, timeStamp);
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

    public boolean[] setAudit(int viewId, long timeStamp, List<InternalCDOObject> invalidObjects)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.setAudit(viewId, timeStamp, invalidObjects);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void setPassiveUpdate(Map<CDOID, CDOIDAndVersion> idAndVersions, int initialChunkSize,
        boolean passiveUpdateEnabled)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.setPassiveUpdate(idAndVersions, initialChunkSize, passiveUpdateEnabled);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDOIDAndVersion> allRevisions, int initialChunkSize)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.syncRevisions(allRevisions, initialChunkSize);
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
