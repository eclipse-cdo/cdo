/*
 * Copyright (c) 2009-2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
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
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOPackageNotFoundException;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSingleValueFeatureDeltaImpl;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFetchRuleManager;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.session.remote.CDORemoteSessionManagerImpl;
import org.eclipse.emf.internal.cdo.util.DefaultLocksChangedEvent;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.concurrent.RunnableWithName;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.spi.cdo.CDOPermissionUpdater;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.MergeDataResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionInvalidationEvent;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOView.ViewInvalidationData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionImpl extends CDOTransactionContainerImpl implements InternalCDOSession, IExecutorServiceProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, CDOSessionImpl.class);

  private static final boolean DEBUG_INVALIDATION = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.internal.cdo.session.CDOSessionImpl.DEBUG_INVALIDATION");

  private ExceptionHandler exceptionHandler;

  private CDOIDGenerator idGenerator;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private InternalCDORevisionManager revisionManager;

  private InternalCDOCommitInfoManager commitInfoManager;

  private CDOSessionProtocol sessionProtocol;

  @ExcludeFromDump
  private IListener sessionProtocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      sessionProtocolDeactivated();
    }
  };

  private int sessionID;

  private String userID;

  private long lastUpdateTime;

  @ExcludeFromDump
  private LastUpdateTimeLock lastUpdateTimeLock = new LastUpdateTimeLock();

  private CDOSession.Options options = createOptions();

  private final IRegistry<String, Object> properties = new HashMapRegistry<String, Object>()
  {
    @Override
    public void setAutoCommit(boolean autoCommit)
    {
      throw new UnsupportedOperationException();
    }
  };

  private final SessionInvalidator invalidator = new SessionInvalidator();

  private CDORepositoryInfo repositoryInfo;

  private CDOFetchRuleManager fetchRuleManager;

  private IRWOLockManager<CDOSessionImpl, Object> lockManager = new RWOLockManager<>();

  @ExcludeFromDump
  private Set<CDOSessionImpl> singletonCollection = Collections.singleton(this);

  private boolean mainBranchLocal;

  private IPasswordCredentialsProvider credentialsProvider;

  private InternalCDORemoteSessionManager remoteSessionManager;

  /**
   * A map to track for every object that was committed since this session's last refresh, onto what CDOBranchPoint it
   * was committed. (Used only for sticky transactions, see bug 290032 - Sticky views.)
   */
  private Map<CDOID, CDOBranchPoint> committedSinceLastRefresh = CDOIDUtil.createMap();

  static
  {
    // Ensure that these 3 packages are registered with the global package registry in stand-alone
    EcorePackage.eINSTANCE.getClass();
    EtypesPackage.eINSTANCE.getClass();
    EresourcePackage.eINSTANCE.getClass();
  }

  public CDOSessionImpl()
  {
  }

  @Override
  public CDORepositoryInfo getRepositoryInfo()
  {
    return repositoryInfo;
  }

  @Override
  public void setRepositoryInfo(CDORepositoryInfo repositoryInfo)
  {
    this.repositoryInfo = repositoryInfo;
  }

  @Override
  public int getSessionID()
  {
    return sessionID;
  }

  @Override
  public void setSessionID(int sessionID)
  {
    this.sessionID = sessionID;
  }

  @Override
  public String getUserID()
  {
    return userID;
  }

  @Override
  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  @Override
  public ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }

  @Override
  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    checkInactive();
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public CDOIDGenerator getIDGenerator()
  {
    return idGenerator;
  }

  @Override
  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    checkInactive();
    this.idGenerator = idGenerator;
  }

  @Override
  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  @Override
  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  @Override
  public InternalCDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  @Override
  public void setBranchManager(InternalCDOBranchManager branchManager)
  {
    checkInactive();
    this.branchManager = branchManager;
  }

  @Override
  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  @Override
  public void setRevisionManager(InternalCDORevisionManager revisionManager)
  {
    checkInactive();
    this.revisionManager = revisionManager;
  }

  @Override
  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  @Override
  public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager)
  {
    checkInactive();
    this.commitInfoManager = commitInfoManager;
  }

  @Override
  public CDOSessionProtocol getSessionProtocol()
  {
    return sessionProtocol;
  }

  @Override
  public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
  {
    if (exceptionHandler == null)
    {
      this.sessionProtocol = sessionProtocol;
    }
    else
    {
      if (this.sessionProtocol instanceof DelegatingSessionProtocol)
      {
        ((DelegatingSessionProtocol)this.sessionProtocol).setDelegate(sessionProtocol);
      }
      else
      {
        this.sessionProtocol = new DelegatingSessionProtocol(sessionProtocol, exceptionHandler);
      }
    }
  }

  /**
   * @since 3.0
   */
  @Override
  public CDOFetchRuleManager getFetchRuleManager()
  {
    return fetchRuleManager;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return ConcurrencyUtil.getExecutorService(sessionProtocol);
  }

  /**
   * @since 3.0
   */
  @Override
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
  {
    if (fetchRuleManager == null)
    {
      fetchRuleManager = new NOOPFetchRuleManager()
      {
        @Override
        public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
        {
          return options().getCollectionLoadingPolicy();
        }
      };
    }

    this.fetchRuleManager = fetchRuleManager;
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setAuthenticator(org.eclipse.emf.cdo.common.protocol.CDOAuthenticator authenticator)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    return credentialsProvider;
  }

  @Override
  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    this.credentialsProvider = credentialsProvider;
  }

  public boolean isMainBranchLocal()
  {
    return mainBranchLocal;
  }

  @Override
  public void setMainBranchLocal(boolean mainBranchLocal)
  {
    this.mainBranchLocal = mainBranchLocal;
  }

  @Override
  public InternalCDORemoteSessionManager getRemoteSessionManager()
  {
    return remoteSessionManager;
  }

  @Override
  public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager)
  {
    this.remoteSessionManager = remoteSessionManager;
  }

  @Override
  public CDOLobStore getLobStore()
  {
    final CDOLobStore cache = options().getLobCache();
    return new CDOLobStore.Delegating()
    {
      @Override
      public InputStream getBinary(final CDOLobInfo info) throws IOException
      {
        for (;;)
        {
          try
          {
            return super.getBinary(info);
          }
          catch (FileNotFoundException couldNotBeRead)
          {
            try
            {
              loadBinary(info);
            }
            catch (FileNotFoundException couldNotBeCreated)
            {
              // Try to read again
            }
          }
        }
      }

      @Override
      public Reader getCharacter(CDOLobInfo info) throws IOException
      {
        for (;;)
        {
          try
          {
            return super.getCharacter(info);
          }
          catch (FileNotFoundException couldNotBeRead)
          {
            try
            {
              loadCharacter(info);
            }
            catch (FileNotFoundException couldNotBeCreated)
            {
              // Try to read again
            }
          }
        }
      }

      private void loadBinary(final CDOLobInfo info) throws IOException
      {
        File file = getDelegate().getBinaryFile(info.getID());
        FileOutputStream out = new FileOutputStream(file);
        loadLobAsync(info, file, out);
      }

      private void loadCharacter(final CDOLobInfo info) throws IOException
      {
        File file = getDelegate().getCharacterFile(info.getID());
        FileWriter out = new FileWriter(file);
        loadLobAsync(info, file, out);
      }

      @Override
      protected CDOLobStore getDelegate()
      {
        return cache;
      }
    };
  }

  protected void loadLobAsync(final CDOLobInfo info, final File file, final Object outputStreamOrWriter)
  {
    ConcurrencyUtil.execute(this, new RunnableWithName()
    {
      @Override
      public String getName()
      {
        return "CDOLobLoader-" + info.getIDString();
      }

      @Override
      protected void doRun()
      {
        try
        {
          CDOSessionProtocol sessionProtocol = getSessionProtocol();
          sessionProtocol.loadLob(info, outputStreamOrWriter);
        }
        catch (Throwable t)
        {
          OM.LOG.error(t);
          IOUtil.delete(file);
        }
      }
    });
  }

  @Override
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isClosed()
  {
    return !isActive();
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOSession.Options options()
  {
    return options;
  }

  @Override
  public IRegistry<String, Object> properties()
  {
    return properties;
  }

  /**
   * @since 2.0
   */
  protected CDOSession.Options createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  public Object processPackage(Object value)
  {
    CDOFactoryImpl.prepareDynamicEPackage(value);
    return value;
  }

  @Override
  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    if (packageUnit.getOriginalType().isGenerated())
    {
      if (!options().isGeneratedPackageEmulationEnabled())
      {
        throw new CDOPackageNotFoundException(packageUnit.getID());
      }
    }

    return getSessionProtocol().loadPackages(packageUnit);
  }

  @Override
  public void acquireAtomicRequestLock(Object key)
  {
    try
    {
      lockManager.lock(LockType.WRITE, key, this, IRWLockManager.WAIT);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public void releaseAtomicRequestLock(Object key)
  {
    lockManager.unlock(LockType.WRITE, key, singletonCollection);
  }

  @Override
  protected void initViewSynced(InternalCDOView view)
  {
    view.setSession(this);
    view.setLastUpdateTime(getLastUpdateTime());
  }

  @Override
  protected CDOBranch getMainBranch()
  {
    return getBranchManager().getMainBranch();
  }

  protected Map<CDORevision, CDOPermission> updatePermissions(CDOCommitInfo commitInfo)
  {
    CDOPermissionUpdater permissionUpdater = options().getPermissionUpdater();
    if (permissionUpdater != null)
    {
      Set<InternalCDORevision> revisions = new HashSet<>();
      revisionManager.getCache().forEachCurrentRevision(r -> {
        if (!(r instanceof SyntheticCDORevision))
        {
          revisions.add((InternalCDORevision)r);
        }
      });

      return permissionUpdater.updatePermissions(CDOSessionImpl.this, revisions);
    }

    return null;
  }

  /**
   * @since 2.0
   */
  @Override
  public long refresh()
  {
    checkActive();
    if (options().isPassiveUpdateEnabled())
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    return refresh(false);
  }

  @Override
  public long refresh(RefreshSessionResult.Provider provider)
  {
    Map<CDOBranch, List<InternalCDOView>> views = new HashMap<>();
    Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions = new HashMap<>();
    collectViewedRevisions(views, viewedRevisions);
    cleanupRevisionCache(viewedRevisions);

    RefreshSessionResult result = provider.getRefreshSessionResult(views, viewedRevisions);

    setLastUpdateTime(result.getLastUpdateTime());
    registerPackageUnits(result.getPackageUnits());

    for (Entry<CDOBranch, List<InternalCDOView>> entry : views.entrySet())
    {
      CDOBranch branch = entry.getKey();
      List<InternalCDOView> branchViews = entry.getValue();
      processRefreshSessionResult(result, branch, branchViews, viewedRevisions);
    }

    return result.getLastUpdateTime();
  }

  protected final long refresh(final boolean enablePassiveUpdates)
  {
    return refresh(new RefreshSessionResult.Provider()
    {
      @Override
      public RefreshSessionResult getRefreshSessionResult(Map<CDOBranch, List<InternalCDOView>> views,
          Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
      {
        CDOSessionProtocol sessionProtocol = getSessionProtocol();
        long lastUpdateTime = getLastUpdateTime();
        int initialChunkSize = options().getCollectionLoadingPolicy().getInitialChunkSize();

        return sessionProtocol.refresh(lastUpdateTime, viewedRevisions, initialChunkSize, enablePassiveUpdates);
      }
    });
  }

  @Override
  public void processRefreshSessionResult(RefreshSessionResult result, CDOBranch branch, List<InternalCDOView> branchViews,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
  {
    Map<CDOID, InternalCDORevision> oldRevisions = viewedRevisions.get(branch);

    InternalCDORevisionManager revisionManager = getRevisionManager();
    List<CDORevisionKey> changedObjects = new ArrayList<>();
    List<InternalCDORevision> newRevisions = result.getChangedObjects(branch);

    for (InternalCDORevision newRevision : newRevisions)
    {
      revisionManager.addRevision(newRevision);

      CDOID id = newRevision.getID();
      InternalCDORevision oldRevision = oldRevisions.get(id);
      InternalCDORevisionDelta delta = newRevision.compare(oldRevision);
      changedObjects.add(delta);
    }

    List<CDOIDAndVersion> detachedObjects = result.getDetachedObjects(branch);
    for (CDOIDAndVersion detachedObject : detachedObjects)
    {
      CDOID id = detachedObject.getID();
      revisionManager.reviseLatest(id, branch);
    }

    long lastUpdateTime = result.getLastUpdateTime();
    for (InternalCDOView view : branchViews)
    {
      ViewInvalidationData invalidationData = new ViewInvalidationData();
      invalidationData.setBranch(view.getBranch());
      invalidationData.setLastUpdateTime(lastUpdateTime);
      invalidationData.setAllChangedObjects(changedObjects);
      invalidationData.setAllDetachedObjects(detachedObjects);
      invalidationData.setOldRevisions(oldRevisions);
      invalidationData.setClearResourcePathCache(true);

      view.invalidate(invalidationData);
    }
  }

  private void collectViewedRevisions(Map<CDOBranch, List<InternalCDOView>> views, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
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
          revisions = CDOIDUtil.createMap();
        }

        view.collectViewedRevisions(revisions);
        if (!revisions.isEmpty())
        {
          List<InternalCDOView> list = views.get(branch);
          if (list == null)
          {
            list = new ArrayList<>();
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

  private void cleanupRevisionCache(Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
  {
    Set<InternalCDORevision> set = new HashSet<>();
    for (Map<CDOID, InternalCDORevision> revisions : viewedRevisions.values())
    {
      for (InternalCDORevision revision : revisions.values())
      {
        set.add(revision);
      }
    }

    InternalCDORevisionCache cache = getRevisionManager().getCache();
    List<CDORevision> currentRevisions = cache.getCurrentRevisions();
    for (CDORevision revision : currentRevisions)
    {
      if (!set.contains(revision))
      {
        cache.removeRevision(revision.getID(), revision);
      }
    }
  }

  @Override
  public long getLastUpdateTime()
  {
    synchronized (lastUpdateTimeLock)
    {
      return lastUpdateTime;
    }
  }

  @Override
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

  @Override
  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  @Override
  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    long end = timeoutMillis == NO_TIMEOUT ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMillis;
    InternalCDOView views[] = getViews();
    if (views.length > 0)
    {
      for (CDOView view : views)
      {
        long viewTimeoutMillis = timeoutMillis == NO_TIMEOUT ? NO_TIMEOUT : end - System.currentTimeMillis();
        boolean ok = view.waitForUpdate(updateTime, viewTimeoutMillis);
        if (!ok)
        {
          return false;
        }
      }

      return true;
    }

    // Session without views
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

  @Override
  public boolean runAfterUpdate(final long updateTime, final Runnable runnable)
  {
    synchronized (lastUpdateTimeLock)
    {
      if (lastUpdateTime < updateTime)
      {
        addListener(new IListener()
        {
          @Override
          public void notifyEvent(IEvent event)
          {
            if (event instanceof CDOSessionInvalidationEvent)
            {
              CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
              if (e.getTimeStamp() >= updateTime)
              {
                removeListener(this);
                runnable.run();
              }
            }
          }
        });

        return false;
      }
    }

    runnable.run();
    return true;
  }

  /**
   * @since 3.0
   */
  @Override
  public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    if (!((InternalCDORevision)revision).isUnchunked())
    {
      CDOCollectionLoadingPolicy policy = options().getCollectionLoadingPolicy();
      return policy.resolveProxy(revision, feature, accessIndex, serverIndex);
    }

    return revision.data().get(feature, accessIndex);
  }

  /**
   * @since 4.0
   */
  @Override
  public void resolveAllElementProxies(CDORevision revision)
  {
    InternalCDORevision internalRevision = (InternalCDORevision)revision;
    if (!internalRevision.isUnchunked())
    {
      CDOCollectionLoadingPolicy policy = options().getCollectionLoadingPolicy();

      for (EReference reference : internalRevision.getClassInfo().getAllPersistentReferences())
      {
        if (reference.isMany())
        {
          CDOList list = internalRevision.getListOrNull(reference);
          if (list != null)
          {
            for (Iterator<Object> it = list.iterator(); it.hasNext();)
            {
              Object element = it.next();
              if (element instanceof CDOElementProxy)
              {
                policy.resolveAllProxies(internalRevision, reference);
                break;
              }
            }
          }
        }
      }

      internalRevision.setUnchunked();
    }
  }

  @Override
  public void ensureChunks(InternalCDORevision revision, int chunkSize)
  {
    resolveAllElementProxies(revision);
  }

  @Override
  public void handleRepositoryTypeChanged(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
  {
    fireEvent(new RepositoryTypeChangedEvent(this, oldType, newType));
  }

  @Override
  public void handleRepositoryStateChanged(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
  {
    fireEvent(new RepositoryStateChangedEvent(this, oldState, newState));
  }

  @Override
  @Deprecated
  public void handleBranchNotification(InternalCDOBranch branch)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void handleCommitNotification(CDOCommitInfo commitInfo)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void handleCommitNotification(CDOCommitInfo commitInfo, boolean clearResourcePathCache)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handleCommitNotification(CommitNotificationInfo info)
  {
    try
    {
      CDOCommitInfo commitInfo = info.getCommitInfo();
      registerPackageUnits(commitInfo.getNewPackageUnits());

      InvalidationData sessionInvalidationData = new InvalidationData();
      sessionInvalidationData.setCommitInfo(commitInfo);
      sessionInvalidationData.setSender(null);
      sessionInvalidationData.setClearResourcePathCache(info.isClearResourcePathCache());
      sessionInvalidationData.setSecurityImpact(info.getSecurityImpact());
      sessionInvalidationData.setNewPermissions(info.getNewPermissions());
      sessionInvalidationData.setLockChangeInfo(info.getLockChangeInfo());

      invalidate(sessionInvalidationData);
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

  @Deprecated
  @Override
  public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender)
  {
    handleLockNotification(lockChangeInfo, sender, false);
  }

  @Override
  public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender, boolean async)
  {
    if (async)
    {
      ExecutorService executorService = getExecutorService();
      executorService.submit(() -> doHandleLockNotification(lockChangeInfo, sender));
    }
    else
    {
      doHandleLockNotification(lockChangeInfo, sender);
    }
  }

  protected void doHandleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender)
  {
    for (InternalCDOView view : getViews())
    {
      if (view != sender)
      {
        view.handleLockNotification(sender, lockChangeInfo);
      }
    }

    fireEvent(new SessionLocksChangedEvent(sender, lockChangeInfo));
  }

  private void registerPackageUnits(List<CDOPackageUnit> packageUnits)
  {
    InternalCDOPackageRegistry packageRegistry = getPackageRegistry();
    for (CDOPackageUnit newPackageUnit : packageUnits)
    {
      packageRegistry.putPackageUnit((InternalCDOPackageUnit)newPackageUnit);
    }
  }

  /**
   * Computes/adjusts CDOMoveFeatureDelta.value, CDORemoveFeatureDelta.value and CDOSetFeatureDelta.oldValue.
   * <p>
   * Implicitely adjusts the underlying CDORevisionDelta.
   */
  private void addOldValuesToDelta(final CDORevision oldRevision, CDORevisionDelta revisionDelta)
  {
    CDOFeatureDeltaVisitor visitor = new CDOFeatureDeltaVisitorImpl()
    {
      private List<Object> workList;

      @Override
      public void visit(CDOAddFeatureDelta delta)
      {
        workList.add(delta.getIndex(), delta.getValue());
      }

      @Override
      public void visit(CDOClearFeatureDelta delta)
      {
        workList.clear();
      }

      @Override
      public void visit(CDOListFeatureDelta deltas)
      {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>)((InternalCDORevision)oldRevision).getValue(deltas.getFeature());
        if (list != null)
        {
          workList = new ArrayList<>(list);
          super.visit(deltas);
        }
      }

      @Override
      public void visit(CDOMoveFeatureDelta delta)
      {
        Object value = workList.get(delta.getOldPosition());
        ((CDOMoveFeatureDeltaImpl)delta).setValue(value); // Adjust delta
        ECollections.move(workList, delta.getNewPosition(), delta.getOldPosition());
      }

      @Override
      public void visit(CDORemoveFeatureDelta delta)
      {
        Object oldValue = workList.remove(delta.getIndex());
        ((CDOSingleValueFeatureDeltaImpl)delta).setValue(oldValue); // Adjust delta
      }

      @Override
      public void visit(CDOSetFeatureDelta delta)
      {
        EStructuralFeature feature = delta.getFeature();
        Object value = null;
        if (feature.isMany())
        {
          value = workList.set(delta.getIndex(), delta.getValue());
        }
        else
        {
          value = ((InternalCDORevision)oldRevision).getValue(feature);
        }

        ((CDOSetFeatureDeltaImpl)delta).setOldValue(value); // Adjust delta
      }
    };

    for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
    {
      featureDelta.accept(visitor);
    }
  }

  @Override
  public Object startLocalCommit()
  {
    return invalidator.startLocalCommit();
  }

  @Override
  public void endLocalCommit(Object token)
  {
    invalidator.endLocalCommit(token);
  }

  @Override
  @Deprecated
  public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender, boolean clearResourcePathCache)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void invalidate(CDOCommitInfo commitInfo, InternalCDOTransaction sender, boolean clearResourcePathCache, byte securityImpact,
      Map<CDOID, CDOPermission> newPermissions)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void invalidate(InvalidationData invalidationData)
  {
    invalidator.scheduleInvalidations(invalidationData);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public String toString()
  {
    String name = repositoryInfo == null ? "?" : repositoryInfo.getName(); //$NON-NLS-1$
    if (userID != null && userID.length() != 0)
    {
      name = userID + "@" + name;
    }

    return MessageFormat.format("Session{0} [{1}]", sessionID, name); //$NON-NLS-1$
  }

  @Override
  public CDOBranchPoint getCommittedSinceLastRefresh(CDOID id)
  {
    if (isSticky())
    {
      return committedSinceLastRefresh.get(id);
    }

    return null;
  }

  @Override
  public void setCommittedSinceLastRefresh(CDOID id, CDOBranchPoint branchPoint)
  {
    if (isSticky())
    {
      committedSinceLastRefresh.put(id, branchPoint);
    }
  }

  @Override
  public void clearCommittedSinceLastRefresh()
  {
    if (isSticky())
    {
      committedSinceLastRefresh.clear();
    }
  }

  @Override
  public boolean isSticky()
  {
    return !options().isPassiveUpdateEnabled() && getRepositoryInfo().isSupportingAudits();
  }

  @Override
  public CDOChangeSetData compareRevisions(CDOBranchPoint source, CDOBranchPoint target)
  {
    long now = getLastUpdateTime();

    if (target.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      target = target.getBranch().getPoint(now);
    }

    if (source.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      source = source.getBranch().getPoint(now);
    }

    CDORevisionAvailabilityInfo targetInfo = createRevisionAvailabilityInfo2(target);
    CDORevisionAvailabilityInfo sourceInfo = createRevisionAvailabilityInfo2(source);

    MergeDataResult result = sessionProtocol.loadMergeData2(targetInfo, sourceInfo, null, null);
    Set<CDOID> ids = result.getTargetIDs();

    cacheRevisions2(targetInfo);
    cacheRevisions2(sourceInfo);

    return CDORevisionUtil.createChangeSetData(ids, sourceInfo, targetInfo);
  }

  @Override
  public MergeData getMergeData(CDOBranchPoint target, CDOBranchPoint source, CDOBranchPoint sourceBase, boolean computeChangeSets)
  {
    return getMergeData(target, source, null, sourceBase, computeChangeSets);
  }

  @Override
  public MergeData getMergeData(CDOBranchPoint target, CDOBranchPoint source, CDOBranchPoint targetBase, CDOBranchPoint sourceBase, boolean computeChangeSets)
  {
    boolean auto;
    if (sourceBase == CDOBranchUtil.AUTO_BRANCH_POINT)
    {
      auto = true;
      targetBase = CDOBranchUtil.AUTO_BRANCH_POINT;
    }
    else
    {
      auto = false;
      CDOBranchPoint ancestor = null;

      if (sourceBase == null)
      {
        ancestor = CDOBranchUtil.getAncestor(target, source);
        sourceBase = ancestor;
      }

      if (targetBase == null)
      {
        if (ancestor == null)
        {
          ancestor = CDOBranchUtil.getAncestor(target, source);
        }

        targetBase = ancestor;
      }
    }

    boolean sameBase = sourceBase.equals(targetBase);

    CDORevisionAvailabilityInfo targetInfo = createRevisionAvailabilityInfo2(target);
    CDORevisionAvailabilityInfo sourceInfo = createRevisionAvailabilityInfo2(source);
    CDORevisionAvailabilityInfo targetBaseInfo = createRevisionAvailabilityInfo2(targetBase);
    CDORevisionAvailabilityInfo sourceBaseInfo = sameBase && !auto ? null : createRevisionAvailabilityInfo2(sourceBase);

    MergeDataResult result = sessionProtocol.loadMergeData2(targetInfo, sourceInfo, targetBaseInfo, sourceBaseInfo);

    if (auto)
    {
      sourceBase = sourceBaseInfo.getBranchPoint();
      targetBase = targetBaseInfo.getBranchPoint();
      sameBase = sourceBase.equals(targetBase);
    }

    cacheRevisions2(targetInfo);
    cacheRevisions2(sourceInfo);
    cacheRevisions2(targetBaseInfo);

    if (sameBase)
    {
      sourceBaseInfo = targetBaseInfo;
    }
    else
    {
      cacheRevisions2(sourceBaseInfo);
    }

    CDOChangeSet targetChanges = null;
    CDOChangeSet sourceChanges = null;
    if (computeChangeSets)
    {
      targetChanges = createChangeSet(result.getTargetIDs(), targetBaseInfo, targetInfo);
      sourceChanges = createChangeSet(result.getSourceIDs(), sourceBaseInfo, sourceInfo);
    }

    return new MergeData(target, targetInfo, targetBase, targetBaseInfo, result.getTargetIDs(), targetChanges, source, sourceInfo, sourceBase, sourceBaseInfo,
        result.getSourceIDs(), sourceChanges, result.getResultBase());
  }

  @Override
  @Deprecated
  public CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException();
  }

  private CDORevisionAvailabilityInfo createRevisionAvailabilityInfo2(CDOBranchPoint branchPoint)
  {
    InternalCDORevisionManager revisionManager = getRevisionManager();
    CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint, revisionManager);

    if (branchPoint != CDOBranchUtil.AUTO_BRANCH_POINT)
    {
      InternalCDORevisionCache cache = revisionManager.getCache();

      List<CDORevision> revisions = cache.getRevisions(branchPoint);
      for (CDORevision revision : revisions)
      {
        if (revision instanceof PointerCDORevision)
        {
          PointerCDORevision pointer = (PointerCDORevision)revision;
          CDOBranchVersion target = pointer.getTarget();
          if (target != null)
          {
            revision = cache.getRevisionByVersion(pointer.getID(), target);
          }
        }
        else if (revision instanceof DetachedCDORevision)
        {
          revision = null;
        }

        if (revision != null)
        {
          resolveAllElementProxies(revision);
          info.addRevision(revision);
        }
      }
    }

    return info;
  }

  @Override
  @Deprecated
  public void cacheRevisions(CDORevisionAvailabilityInfo info)
  {
    throw new UnsupportedOperationException();
  }

  private void cacheRevisions2(CDORevisionAvailabilityInfo info)
  {
    InternalCDORevisionManager revisionManager = getRevisionManager();
    CDOBranch branch = info.getBranchPoint().getBranch();
    for (CDORevisionKey key : info.getAvailableRevisions().values())
    {
      CDORevision revision = (CDORevision)key;
      revisionManager.addRevision(revision);

      if (revision.getBranch() != branch)
      {
        CDOID id = revision.getID();
        CDORevision firstRevision = revisionManager.getCache().getRevisionByVersion(id, branch.getVersion(CDOBranchVersion.FIRST_VERSION));
        if (firstRevision != null)
        {
          long revised = firstRevision.getTimeStamp() - 1L;
          CDOBranchVersion target = CDOBranchUtil.copyBranchVersion(revision);
          PointerCDORevision pointer = new PointerCDORevision(revision.getEClass(), id, branch, revised, target);
          revisionManager.addRevision(pointer);
        }
      }
    }
  }

  private CDOChangeSet createChangeSet(Set<CDOID> ids, CDORevisionAvailabilityInfo startInfo, CDORevisionAvailabilityInfo endInfo)
  {
    CDOChangeSetData data = CDORevisionUtil.createChangeSetData(ids, startInfo, endInfo);
    return CDORevisionUtil.createChangeSet(startInfo.getBranchPoint(), endInfo.getBranchPoint(), data);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    Runnable runnable = SessionUtil.getTestDelayInSessionActivation();
    if (runnable != null)
    {
      runnable.run();
    }

    InternalCDORemoteSessionManager remoteSessionManager = new CDORemoteSessionManagerImpl();
    remoteSessionManager.setLocalSession(this);
    setRemoteSessionManager(remoteSessionManager);
    remoteSessionManager.activate();

    checkState(sessionProtocol, "sessionProtocol"); //$NON-NLS-1$
    checkState(remoteSessionManager, "remoteSessionManager"); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();

    unhookSessionProtocol();

    CDORemoteSessionManager remoteSessionManager = getRemoteSessionManager();
    setRemoteSessionManager(null);
    LifecycleUtil.deactivate(remoteSessionManager);

    CDOSessionProtocol sessionProtocol = getSessionProtocol();
    LifecycleUtil.deactivate(sessionProtocol);
    setSessionProtocol(null);
  }

  /**
   * Makes this session start listening to its protocol
   */
  protected CDOSessionProtocol hookSessionProtocol()
  {
    EventUtil.addListener(sessionProtocol, sessionProtocolListener);
    return sessionProtocol;
  }

  /**
   * Makes this session stop listening to its protocol
   */
  protected void unhookSessionProtocol()
  {
    EventUtil.removeListener(sessionProtocol, sessionProtocolListener);
  }

  protected void sessionProtocolDeactivated()
  {
    deactivate();
  }

  /**
   * A separate class for better monitor debugging.
   *
   * @author Eike Stepper
   */
  private static final class LastUpdateTimeLock
  {
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean generatedPackageEmulationEnabled;

    private boolean passiveUpdateEnabled = true;

    private PassiveUpdateMode passiveUpdateMode = PassiveUpdateMode.INVALIDATIONS;

    private LockNotificationMode lockNotificationMode = LockNotificationMode.IF_REQUIRED_BY_VIEWS;

    private CDOCollectionLoadingPolicy collectionLoadingPolicy;

    private CDOLobStore lobCache = CDOLobStoreImpl.INSTANCE;

    private CDOPermissionUpdater permissionUpdater = CDOPermissionUpdater.SERVER;

    private boolean delegableViewLockEnabled;

    public OptionsImpl()
    {
      setCollectionLoadingPolicy(null); // Init default
    }

    @Override
    public CDOSession getContainer()
    {
      return CDOSessionImpl.this;
    }

    @Override
    public boolean isGeneratedPackageEmulationEnabled()
    {
      return generatedPackageEmulationEnabled;
    }

    @Override
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

    @Override
    public boolean isPassiveUpdateEnabled()
    {
      return passiveUpdateEnabled;
    }

    @Override
    public synchronized void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
      if (this.passiveUpdateEnabled != passiveUpdateEnabled)
      {
        this.passiveUpdateEnabled = passiveUpdateEnabled;
        CDOSessionProtocol protocol = getSessionProtocol();
        if (protocol != null)
        {
          if (passiveUpdateEnabled)
          {
            refresh(true);
          }
          else
          {
            protocol.disablePassiveUpdate();
          }

          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(new PassiveUpdateEventImpl(!passiveUpdateEnabled, passiveUpdateEnabled, passiveUpdateMode, passiveUpdateMode), listeners);
          }
        }
      }
    }

    @Override
    public PassiveUpdateMode getPassiveUpdateMode()
    {
      return passiveUpdateMode;
    }

    @Override
    public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
    {
      checkArg(passiveUpdateMode, "passiveUpdateMode"); //$NON-NLS-1$
      if (this.passiveUpdateMode != passiveUpdateMode)
      {
        PassiveUpdateMode oldMode = this.passiveUpdateMode;
        this.passiveUpdateMode = passiveUpdateMode;
        CDOSessionProtocol protocol = getSessionProtocol();
        if (protocol != null)
        {
          protocol.setPassiveUpdateMode(passiveUpdateMode);

          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(new PassiveUpdateEventImpl(passiveUpdateEnabled, passiveUpdateEnabled, oldMode, passiveUpdateMode), listeners);
          }
        }
      }
    }

    @Override
    public LockNotificationMode getLockNotificationMode()
    {
      return lockNotificationMode;
    }

    @Override
    public void setLockNotificationMode(LockNotificationMode lockNotificationMode)
    {
      checkArg(lockNotificationMode, "lockNotificationMode"); //$NON-NLS-1$
      if (this.lockNotificationMode != lockNotificationMode)
      {
        LockNotificationMode oldMode = this.lockNotificationMode;
        this.lockNotificationMode = lockNotificationMode;
        CDOSessionProtocol protocol = getSessionProtocol();
        if (protocol != null)
        {
          protocol.setLockNotificationMode(lockNotificationMode);

          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(new LockNotificationModeEventImpl(oldMode, lockNotificationMode), listeners);
          }
        }
      }
      this.lockNotificationMode = lockNotificationMode;
    }

    @Override
    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      synchronized (this)
      {
        return collectionLoadingPolicy;
      }
    }

    @Override
    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy)
    {
      if (policy == null)
      {
        policy = CDOUtil.createCollectionLoadingPolicy(CDORevision.UNCHUNKED, CDORevision.UNCHUNKED);
      }

      CDOSession oldSession = policy.getSession();
      if (oldSession != null)
      {
        throw new IllegalArgumentException("Policy is already associated with " + oldSession);
      }

      policy.setSession(CDOSessionImpl.this);

      IListener[] listeners = getListeners();
      IEvent event = null;

      synchronized (this)
      {
        if (collectionLoadingPolicy != policy)
        {
          collectionLoadingPolicy = policy;
          if (listeners != null)
          {
            event = new CollectionLoadingPolicyEventImpl();
          }
        }
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }

    @Override
    public CDOLobStore getLobCache()
    {
      synchronized (this)
      {
        return lobCache;
      }
    }

    @Override
    public void setLobCache(CDOLobStore cache)
    {
      if (cache == null)
      {
        cache = CDOLobStoreImpl.INSTANCE;
      }

      IListener[] listeners = getListeners();
      IEvent event = null;

      synchronized (this)
      {
        if (lobCache != cache)
        {
          lobCache = cache;
          if (listeners != null)
          {
            event = new LobCacheEventImpl();
          }
        }
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }

    @Override
    public CDOPermissionUpdater getPermissionUpdater()
    {
      synchronized (this)
      {
        return permissionUpdater;
      }
    }

    @Override
    public void setPermissionUpdater(CDOPermissionUpdater permissionUpdater)
    {
      IListener[] listeners = getListeners();
      IEvent event = null;

      synchronized (this)
      {
        if (this.permissionUpdater != permissionUpdater)
        {
          this.permissionUpdater = permissionUpdater;
          if (listeners != null)
          {
            event = new PermissionUpdaterEventImpl();
          }
        }
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }

    @Override
    public boolean isDelegableViewLockEnabled()
    {
      synchronized (this)
      {
        return delegableViewLockEnabled;
      }
    }

    @Override
    public void setDelegableViewLockEnabled(boolean delegableViewLockEnabled)
    {
      IListener[] listeners = getListeners();
      IEvent event = null;

      synchronized (this)
      {
        if (this.delegableViewLockEnabled != delegableViewLockEnabled)
        {
          this.delegableViewLockEnabled = delegableViewLockEnabled;
          if (listeners != null)
          {
            event = new DelegableViewLockEventImpl();
          }
        }
      }

      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class GeneratedPackageEmulationEventImpl extends OptionsEvent implements GeneratedPackageEmulationEvent
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

      private boolean oldEnabled;

      private boolean newEnabled;

      private PassiveUpdateMode oldMode;

      private PassiveUpdateMode newMode;

      public PassiveUpdateEventImpl(boolean oldEnabled, boolean newEnabled, PassiveUpdateMode oldMode, PassiveUpdateMode newMode)
      {
        super(OptionsImpl.this);
        this.oldEnabled = oldEnabled;
        this.newEnabled = newEnabled;
        this.oldMode = oldMode;
        this.newMode = newMode;
      }

      @Override
      public boolean getOldEnabled()
      {
        return oldEnabled;
      }

      @Override
      public boolean getNewEnabled()
      {
        return newEnabled;
      }

      @Override
      public PassiveUpdateMode getOldMode()
      {
        return oldMode;
      }

      @Override
      public PassiveUpdateMode getNewMode()
      {
        return newMode;
      }
    }

    /**
     * @author Caspar De Groot
     */
    private final class LockNotificationModeEventImpl extends OptionsEvent implements LockNotificationModeEvent
    {
      private static final long serialVersionUID = 1L;

      private LockNotificationMode oldMode, newMode;

      public LockNotificationModeEventImpl(LockNotificationMode oldMode, LockNotificationMode newMode)
      {
        super(OptionsImpl.this);
        this.oldMode = oldMode;
        this.newMode = newMode;
      }

      @Override
      public LockNotificationMode getOldMode()
      {
        return oldMode;
      }

      @Override
      public LockNotificationMode getNewMode()
      {
        return newMode;
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

    /**
     * @author Eike Stepper
     */
    private final class LobCacheEventImpl extends OptionsEvent implements LobCacheEvent
    {
      private static final long serialVersionUID = 1L;

      public LobCacheEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class PermissionUpdaterEventImpl extends OptionsEvent implements PermissionUpdaterEvent
    {
      private static final long serialVersionUID = 1L;

      public PermissionUpdaterEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class DelegableViewLockEventImpl extends OptionsEvent implements DelegableViewLockEvent
    {
      private static final long serialVersionUID = 1L;

      public DelegableViewLockEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SessionInvalidator
  {
    private final Set<Object> unfinishedLocalCommits = new HashSet<>();

    private final List<SessionInvalidation> invalidationQueue = new ArrayList<>();

    private final Queue<Runnable> postInvalidationQueue = new ConcurrentLinkedQueue<>();

    private int lastCommitNumber;

    public SessionInvalidator()
    {
    }

    public synchronized Object startLocalCommit()
    {
      if (!isActive())
      {
        return null;
      }

      CommitToken token = new CommitToken(++lastCommitNumber, Thread.currentThread().getName());

      if (DEBUG_INVALIDATION)
      {
        IOUtil.OUT().println(CDOSessionImpl.this + " [" + getLastUpdateTime() % 10000 + "] startLocalCommit: " + token);
      }

      unfinishedLocalCommits.add(token);
      return token;
    }

    public synchronized void endLocalCommit(Object token)
    {
      if (DEBUG_INVALIDATION)
      {
        IOUtil.OUT().println(CDOSessionImpl.this + " [" + getLastUpdateTime() % 10000 + "] endLocalCommit: " + token);
      }

      unfinishedLocalCommits.remove(token);
    }

    public void scheduleInvalidations(InvalidationData invalidationData)
    {
      SessionInvalidation invalidation = new SessionInvalidation(invalidationData);

      synchronized (this)
      {
        invalidationQueue.add(invalidation);
        Collections.sort(invalidationQueue);

        if (DEBUG_INVALIDATION)
        {
          IOUtil.OUT().println(CDOSessionImpl.this + " [" + getLastUpdateTime() % 10000 + "] " + invalidation.getPreviousTimeStamp() % 10000 + " --> "
              + invalidation.getTimeStamp() % 10000 + "    reorderQueue=" + invalidationQueue + "    unfinishedLocalCommits=" + unfinishedLocalCommits);
        }
      }

      while (isActive())
      {
        synchronized (this)
        {
          if (invalidationQueue.isEmpty() || !canProcess(invalidationQueue.get(0)))
          {
            break;
          }

          SessionInvalidation invalidation0 = invalidationQueue.remove(0);
          Runnable postInvalidationRunnable = invalidation0.process();
          if (postInvalidationRunnable != null)
          {
            postInvalidationQueue.add(postInvalidationRunnable);
          }
        }

        Runnable postInvalidationRunnable;
        while ((postInvalidationRunnable = postInvalidationQueue.poll()) != null)
        {
          postInvalidationRunnable.run();
        }
      }
    }

    private boolean canProcess(SessionInvalidation invalidation)
    {
      if (options().isPassiveUpdateEnabled())
      {
        long previousTimeStamp = invalidation.getPreviousTimeStamp();
        long lastUpdateTime = getLastUpdateTime();
        return previousTimeStamp <= lastUpdateTime; // Can be smaller in replication scenarios
      }

      return unfinishedLocalCommits.size() == 1; // Ourselves
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SessionInvalidation implements Comparable<SessionInvalidation>
  {
    private final InvalidationData invalidationData;

    private final CDOCommitInfo commitInfo;

    public SessionInvalidation(InvalidationData invalidationData)
    {
      this.invalidationData = invalidationData;
      commitInfo = invalidationData.getCommitInfo();
    }

    public long getTimeStamp()
    {
      return commitInfo.getTimeStamp();
    }

    public long getPreviousTimeStamp()
    {
      return commitInfo.getPreviousTimeStamp();
    }

    @Override
    public int compareTo(SessionInvalidation o)
    {
      return CDOCommonUtil.compareTimeStamps(getTimeStamp(), o.getTimeStamp());
    }

    @Override
    public String toString()
    {
      return Long.toString(getTimeStamp() % 10000);
    }

    public Runnable process()
    {
      long timeStamp = getTimeStamp();

      if (DEBUG_INVALIDATION)
      {
        IOUtil.OUT().println(CDOSessionImpl.this + " [" + getLastUpdateTime() % 10000 + "] " + timeStamp % 10000 + "    INVALIDATE");
      }

      try
      {
        InternalCDOView[] views = getViews();
        Map<CDORevision, CDOPermission> oldPermissions = null;
        Map<CDOID, InternalCDORevision> oldRevisions = null;

        CDOBranch branch = commitInfo.getBranch();
        boolean success = branch != null;
        if (success)
        {
          oldRevisions = reviseRevisions();

          if (invalidationData.getSecurityImpact() != CommitNotificationInfo.IMPACT_NONE)
          {
            oldPermissions = updatePermissions(commitInfo);
          }

          commitInfoManager.setLastCommitOfBranch(branch, timeStamp);
        }

        if (options.isPassiveUpdateEnabled()/* || sender != null */)
        {
          setLastUpdateTime(timeStamp);
        }

        Map<CDORevision, CDOPermission> oldPermissionsFinal = oldPermissions;
        Map<CDOID, InternalCDORevision> oldRevisionsFinal = oldRevisions;

        return () -> {
          CDOLockChangeInfo lockChangeInfo = invalidationData.getLockChangeInfo();
          InternalCDOTransaction sender = invalidationData.getSender();

          if (success)
          {
            fireEvent(new SessionInvalidationEvent(sender, commitInfo, invalidationData.getSecurityImpact(), oldPermissionsFinal));
            fireEvent(new SessionLocksChangedEvent(sender, lockChangeInfo));

            commitInfoManager.notifyCommitInfoHandlers(commitInfo);
          }

          for (InternalCDOView view : views)
          {
            invalidateView(commitInfo, view, sender, oldRevisionsFinal, invalidationData.isClearResourcePathCache(), lockChangeInfo);
          }
        };
      }
      catch (RuntimeException ex)
      {
        if (isActive())
        {
          throw ex;
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace(Messages.getString("CDOSessionImpl.2")); //$NON-NLS-1$
        }
      }

      return null;
    }

    private Map<CDOID, InternalCDORevision> reviseRevisions()
    {
      Map<CDOID, InternalCDORevision> oldRevisions = null;
      CDOBranch newBranch = commitInfo.getBranch();
      long timeStamp = getTimeStamp();

      // Cache new revisions
      for (CDOIDAndVersion key : commitInfo.getNewObjects())
      {
        if (key instanceof InternalCDORevision)
        {
          InternalCDORevision newRevision = (InternalCDORevision)key;
          addNewRevision(newRevision);
        }
      }

      // Apply deltas and cache the resulting new revisions, if possible...
      for (CDORevisionKey key : commitInfo.getChangedObjects())
      {
        CDOID id = key.getID();
        if (key instanceof CDORevisionDelta)
        {
          CDORevisionDelta revisionDelta = (CDORevisionDelta)key;

          InternalCDORevision oldRevision = revisionManager.getRevisionByVersion(id, revisionDelta, CDORevision.UNCHUNKED, false);
          if (oldRevision != null)
          {
            addOldValuesToDelta(oldRevision, revisionDelta);

            InternalCDORevision newRevision = oldRevision.copy();
            newRevision.adjustForCommit(newBranch, timeStamp);

            CDORevisable target = revisionDelta.getTarget();
            if (target != null)
            {
              newRevision.setVersion(target.getVersion());
            }

            boolean bypassPermissionChecks = newRevision.bypassPermissionChecks(true);

            try
            {
              revisionDelta.applyTo(newRevision);
            }
            finally
            {
              newRevision.bypassPermissionChecks(bypassPermissionChecks);
              newRevision.freeze();
            }

            addNewRevision(newRevision);
            if (oldRevisions == null)
            {
              oldRevisions = CDOIDUtil.createMap();
            }

            oldRevisions.put(id, oldRevision);
          }
        }
        else
        {
          // ... otherwise try to revise old revision if it is in the same branch
          if (key.getBranch() == newBranch)
          {
            revisionManager.reviseVersion(id, key, timeStamp);
          }
        }
      }

      // Revise old revisions
      for (CDOIDAndVersion key : commitInfo.getDetachedObjects())
      {
        CDOID id = key.getID();
        revisionManager.reviseLatest(id, newBranch);
      }

      return oldRevisions;
    }

    private void addNewRevision(InternalCDORevision newRevision)
    {
      Map<CDOID, CDOPermission> newPermissions = invalidationData.getNewPermissions();
      if (newPermissions != null)
      {
        CDOPermission newPermission = newPermissions.get(newRevision.getID());
        if (newPermission != null)
        {
          newRevision.setPermission(newPermission);
        }
      }

      revisionManager.addRevision(newRevision);
    }

    private void invalidateView(CDOCommitInfo commitInfo, InternalCDOView view, InternalCDOTransaction sender, Map<CDOID, InternalCDORevision> oldRevisions,
        boolean clearResourcePathCache, CDOLockChangeInfo lockChangeInfo)
    {
      try
      {
        byte securityImpact = invalidationData.getSecurityImpact();

        ViewInvalidationData invalidationData = new ViewInvalidationData();
        invalidationData.setLastUpdateTime(getTimeStamp());
        invalidationData.setSecurityImpact(securityImpact);
        invalidationData.setAsync(true);

        // The committing view (sender) is already valid, just the timestamp must be set "in sequence".
        // Setting the sender's timestamp synchronously can lead to deadlock
        if (view != sender)
        {
          invalidationData.setBranch(commitInfo.getBranch());
          invalidationData.setAllChangedObjects(commitInfo.getChangedObjects());
          invalidationData.setAllDetachedObjects(commitInfo.getDetachedObjects());
          invalidationData.setOldRevisions(oldRevisions);
          invalidationData.setClearResourcePathCache(clearResourcePathCache);
          invalidationData.setLockChangeInfo(lockChangeInfo);
        }

        view.invalidate(invalidationData);
      }
      catch (RuntimeException ex)
      {
        if (view.isActive())
        {
          OM.LOG.error(ex);
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace(Messages.getString("CDOSessionImpl.1")); //$NON-NLS-1$
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SessionInvalidationEvent extends Event implements InternalCDOSessionInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOTransaction sender;

    private CDOCommitInfo commitInfo;

    private byte securityImpact;

    private Map<CDORevision, CDOPermission> oldPermissions;

    public SessionInvalidationEvent(InternalCDOTransaction sender, CDOCommitInfo commitInfo, byte securityImpact,
        Map<CDORevision, CDOPermission> oldPermissions)
    {
      super(CDOSessionImpl.this);
      this.sender = sender;
      this.commitInfo = commitInfo;
      this.securityImpact = securityImpact;
      this.oldPermissions = oldPermissions;
    }

    @Override
    public CDOSession getSource()
    {
      return (CDOSession)super.getSource();
    }

    @Override
    public CDOCommitInfoManager getCommitInfoManager()
    {
      return commitInfo.getCommitInfoManager();
    }

    @Override
    public CDOTransaction getLocalTransaction()
    {
      return sender;
    }

    @Override
    @Deprecated
    public InternalCDOView getView()
    {
      return sender;
    }

    @Override
    public boolean isRemote()
    {
      return sender == null;
    }

    @Override
    public CDOBranch getBranch()
    {
      return commitInfo.getBranch();
    }

    @Override
    public long getTimeStamp()
    {
      return commitInfo.getTimeStamp();
    }

    @Override
    public long getPreviousTimeStamp()
    {
      return commitInfo.getPreviousTimeStamp();
    }

    @Override
    public CDOCommitInfo getPreviousCommitInfo()
    {
      return commitInfo.getPreviousCommitInfo();
    }

    @Override
    public String getUserID()
    {
      return commitInfo.getUserID();
    }

    @Override
    public String getComment()
    {
      return commitInfo.getComment();
    }

    @Override
    public CDOBranchPoint getMergeSource()
    {
      return commitInfo.getMergeSource();
    }

    @Override
    public CDOCommitInfo getMergedCommitInfo()
    {
      CDOBranchPoint mergeSource = getMergeSource();
      return mergeSource == null ? null : commitInfoManager.getCommitInfo(mergeSource.getBranch(), mergeSource.getTimeStamp(), false);
    }

    @Override
    public boolean isEmpty()
    {
      return false;
    }

    @Override
    public boolean isInitialCommit()
    {
      return commitInfo.isInitialCommit();
    }

    @Override
    public boolean isCommitDataLoaded()
    {
      return commitInfo.isCommitDataLoaded();
    }

    @Override
    public CDOChangeSetData copy()
    {
      return commitInfo.copy();
    }

    @Override
    public void merge(CDOChangeSetData changeSetData)
    {
      commitInfo.merge(changeSetData);
    }

    @Override
    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return commitInfo.getNewPackageUnits();
    }

    @Override
    public List<CDOIDAndVersion> getNewObjects()
    {
      return commitInfo.getNewObjects();
    }

    @Override
    public List<CDORevisionKey> getChangedObjects()
    {
      return commitInfo.getChangedObjects();
    }

    @Override
    public List<CDOIDAndVersion> getDetachedObjects()
    {
      return commitInfo.getDetachedObjects();
    }

    @Override
    public Map<CDOID, CDOChangeKind> getChangeKinds()
    {
      return commitInfo.getChangeKinds();
    }

    @Override
    public CDOChangeKind getChangeKind(CDOID id)
    {
      return commitInfo.getChangeKind(id);
    }

    @Override
    public byte getSecurityImpact()
    {
      return securityImpact;
    }

    @Override
    public Map<CDORevision, CDOPermission> getOldPermissions()
    {
      return oldPermissions;
    }

    @Override
    public String toString()
    {
      return "CDOSessionInvalidationEvent[" + commitInfo + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * @author Caspar De Groot
   * @since 4.1
   */
  private final class SessionLocksChangedEvent extends DefaultLocksChangedEvent implements CDOSessionLocksChangedEvent
  {
    private static final long serialVersionUID = 1L;

    public SessionLocksChangedEvent(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
    {
      super(CDOSessionImpl.this, sender, lockChangeInfo);
    }

    @Override
    public CDOSession getSource()
    {
      return (CDOSession)super.getSource();
    }

    @Override
    protected String formatEventName()
    {
      return "CDOSessionLocksChangedEvent";
    }
  }
}
