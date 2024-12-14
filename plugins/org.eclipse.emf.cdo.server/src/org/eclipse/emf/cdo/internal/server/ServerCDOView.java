/*
 * Copyright (c) 2010-2016, 2019-2021, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOUserInfoManager;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.cdo.view.CDOUnitManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.SessionUtil;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;

import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.ref.KeyedReference;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap2;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.CDOLockStateCache;
import org.eclipse.emf.spi.cdo.CDOPermissionUpdater;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult.Provider;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class ServerCDOView extends AbstractCDOView implements org.eclipse.emf.cdo.view.CDOView.Options
{
  private static final CDOAdapterPolicy[] ADAPTER_POLICIES = new CDOAdapterPolicy[0];

  private static final CDORevisionPrefetchingPolicy REVISION_PREFETCHING = CDOUtil.createRevisionPrefetchingPolicy(NO_REVISION_PREFETCHING);

  private ServerCDOSession session;

  private CDORevisionProvider revisionProvider;

  private boolean closing;

  public ServerCDOView(InternalSession session, CDOBranchPoint branchPoint, CDORevisionProvider revisionProvider)
  {
    super(null, CDOBranchUtil.adjustBranchPoint(branchPoint, session.getRepository().getBranchManager()));
    this.session = new ServerCDOSession(session);
    this.revisionProvider = revisionProvider;

    InternalCDOViewSet resourceSet = SessionUtil.prepareResourceSet(new ResourceSetImpl());
    setViewSet(resourceSet);

    Map<CDOID, KeyedReference<CDOID, InternalCDOObject>> map = CDOIDUtil.createMap();
    setObjects(new ReferenceValueMap2.Weak<>(map));

    activate();
  }

  @Override
  public int getViewID()
  {
    return 1;
  }

  @Override
  public InternalCDOSession getSession()
  {
    return session;
  }

  public ISession getServerSession()
  {
    return session.getInternalSession();
  }

  public CDORevisionProvider getRevisionProvider()
  {
    return revisionProvider;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return session.getExecutorService();
  }

  @Override
  public long getLastUpdateTime()
  {
    return getTimeStamp();
  }

  @Override
  public void setLastUpdateTime(long lastUpdateTime)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Options options()
  {
    return this;
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    return (InternalCDORevision)revisionProvider.getRevision(id);
  }

  @Override
  protected void excludeNewObject(CDOID id)
  {
    // Do nothing
  }

  @Override
  public boolean isInvalidating()
  {
    return false;
  }

  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint, IProgressMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlockObjects()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean runAfterUpdate(long updateTime, Runnable runnable)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setViewID(int viewId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSession(InternalCDOSession session)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getSessionID()
  {
    return session.getSessionID();
  }

  @Override
  public boolean isDurableView()
  {
    return false;
  }

  @Override
  public void inverseClose()
  {
    // Do nothing.
  }

  @Override
  public boolean isClosing()
  {
    return closing;
  }

  @Override
  public String getDurableLockingID()
  {
    return null;
  }

  @Override
  public String enableDurableLocking()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void disableDurableLocking(boolean releaseLocks)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("deprecation")
  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return CDOFeatureAnalyzer.NOOP;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCDOTransaction toTransaction()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void invalidate(ViewInvalidationData invalidationData)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    // Do nothing
  }

  @Override
  public void resourceLoaded(CDOResourceImpl resource, boolean loaded)
  {
    // Do nothing
  }

  @Override
  public void prefetchRevisions(CDOID id, int depth)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers)
  {
    return false;
  }

  @Override
  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    // Do nothing
  }

  @Override
  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    // Do nothing
  }

  @Override
  public void subscribe(EObject eObject, Adapter adapter)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unsubscribe(EObject eObject, Adapter adapter)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasSubscription(CDOID id)
  {
    return false;
  }

  @Override
  public CDOView getContainer()
  {
    return this;
  }

  @Override
  public ReferenceType getCacheReferenceType()
  {
    return ReferenceType.WEAK;
  }

  @Override
  public boolean setCacheReferenceType(ReferenceType referenceType)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOInvalidationPolicy getInvalidationPolicy()
  {
    return CDOInvalidationPolicy.DEFAULT;
  }

  @Override
  public void setInvalidationPolicy(CDOInvalidationPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDetachmentNotificationEnabled()
  {
    return false;
  }

  @Override
  public void setDetachmentNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isInvalidationNotificationEnabled()
  {
    return false;
  }

  @Override
  public void setInvalidationNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isLoadNotificationEnabled()
  {
    return false;
  }

  @Override
  public void setLoadNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isLockNotificationEnabled()
  {
    return false;
  }

  @Override
  public void setLockNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOAdapterPolicy[] getChangeSubscriptionPolicies()
  {
    return ADAPTER_POLICIES;
  }

  @Override
  public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOAdapterPolicy getStrongReferencePolicy()
  {
    return CDOAdapterPolicy.ALL;
  }

  @Override
  public void setStrongReferencePolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOStaleReferencePolicy getStaleReferencePolicy()
  {
    return CDOStaleReferencePolicy.DEFAULT;
  }

  @Override
  public void setStaleReferencePolicy(CDOStaleReferencePolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy()
  {
    return REVISION_PREFETCHING;
  }

  @Override
  public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOAdapterPolicy getClearAdapterPolicy()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setClearAdapterPolicy(CDOAdapterPolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOLockOwner getLockOwner()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refreshLockStates(Consumer<CDOLockState> consumer)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOLockState[] getLockStates(Collection<CDOID> ids, boolean loadOnDemand)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOUnitManager getUnitManager()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    closing = true;
    super.doBeforeDeactivate();
  }

  @Override
  @Deprecated
  public boolean isInvalidationRunnerActive()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public String enableDurableLocking(boolean enable)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions, boolean async)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions, boolean async, boolean clearResourcePathCache)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void updateLockStates(CDOLockState[] newLockStates, boolean loadObjectsOnDemand, Consumer<CDOLockState> consumer)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public org.eclipse.emf.internal.cdo.view.CDOLockStateLoadingPolicy getLockStateLoadingPolicy()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public void setLockStateLoadingPolicy(org.eclipse.emf.internal.cdo.view.CDOLockStateLoadingPolicy lockStateLoadingPolicy)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public boolean isLockStatePrefetchEnabled()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public void setLockStatePrefetchEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public CDOStaleReferencePolicy getStaleReferenceBehaviour()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setStaleReferenceBehaviour(CDOStaleReferencePolicy policy)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  public final class ServerCDOSession extends PlatformObject implements InternalCDOSession, CDORepositoryInfo, org.eclipse.emf.cdo.session.CDOSession.Options
  {
    private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

    private final InternalSession internalSession;

    private final InternalRepository repository;

    private final CDOUserInfoManager userInfoManager = new CDOUserInfoManager()
    {
      @Override
      public CDOSession getSession()
      {
        return ServerCDOSession.this;
      }

      @Override
      public Map<String, Entity> getUserInfos(Iterable<String> userIDs)
      {
        Map<String, Entity> userInfos = new HashMap<>();

        Entity.Store entityStore = internalSession.getRepository().getEntityStore();
        if (entityStore != null)
        {
          for (String userID : userIDs)
          {
            Entity userInfo = entityStore.entity(CDOProtocolConstants.USER_INFO_NAMESPACE, userID);
            if (userInfo != null)
            {
              userInfos.put(userID, userInfo);
            }
          }
        }

        return userInfos;
      }
    };

    private final long openingTime;

    private boolean generatedPackageEmulationEnabled;

    public ServerCDOSession(InternalSession internalSession)
    {
      this.internalSession = internalSession;
      repository = internalSession.getRepository();
      openingTime = repository.getTimeStamp();
    }

    @Override
    public IRegistry<String, Object> properties()
    {
      return properties;
    }

    @Override
    public CDOSession getSession()
    {
      return this;
    }

    public InternalSession getInternalSession()
    {
      return internalSession;
    }

    @Override
    public String getUserID()
    {
      return internalSession.getUserID();
    }

    @Override
    public int getSessionID()
    {
      return internalSession.getSessionID();
    }

    @Override
    public CDOView[] getElements()
    {
      return new ServerCDOView[] { ServerCDOView.this };
    }

    @Override
    public ExecutorService getExecutorService()
    {
      return internalSession.getExecutorService();
    }

    @Override
    public InternalCDOTransaction getTransaction(int viewID)
    {
      return null;
    }

    @Override
    public InternalCDOTransaction[] getTransactions()
    {
      return new InternalCDOTransaction[0];
    }

    @Override
    public InternalCDOTransaction[] getTransactions(CDOBranch branch)
    {
      return new InternalCDOTransaction[0];
    }

    @Override
    public CDOView[] getViews(CDOBranch branch)
    {
      if (getBranch() == branch)
      {
        return getViews();
      }

      return new CDOView[0];
    }

    @Override
    public InternalCDOView[] getViews()
    {
      return (InternalCDOView[])getElements();
    }

    @Override
    public CDOView getView(int viewID)
    {
      return viewID == getViewID() ? ServerCDOView.this : null;
    }

    @Override
    public CDOLockStateCache getLockStateCache()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOSessionProtocol getSessionProtocol()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOLobStore getLobStore()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Entity> clientEntities()
    {
      return repository.getClientEntities();
    }

    /**
     * Server sessions may not be used to change the user's credentials: it must
     * be done client-side by interaction with the user.
     */
    @Override
    public char[] changeServerPassword()
    {
      return null;
    }

    /**
     * Server sessions may not be used to reset a user's credentials: it must
     * be done client-side by interaction with an administrator.
     *
     * @since 4.3
     */
    @Override
    public void resetCredentials(String userID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public InternalCDORevisionManager getRevisionManager()
    {
      return repository.getRevisionManager();
    }

    @Override
    public InternalCDOPackageRegistry getPackageRegistry()
    {
      if (revisionProvider instanceof IStoreAccessor.CommitContext)
      {
        IStoreAccessor.CommitContext context = (IStoreAccessor.CommitContext)revisionProvider;
        return context.getPackageRegistry();
      }

      return repository.getPackageRegistry(false);
    }

    @Override
    public InternalCDOCommitInfoManager getCommitInfoManager()
    {
      return repository.getCommitInfoManager();
    }

    @Override
    public CDOUserInfoManager getUserInfoManager()
    {
      return userInfoManager;
    }

    @Override
    public void setUserInfoManager(CDOUserInfoManager userInfoManager)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public InternalCDOBranchManager getBranchManager()
    {
      return repository.getBranchManager();
    }

    @Override
    public void setLoginPeek(boolean loginPeek)
    {
      // Do nothing
    }

    @Override
    public void setMainBranchLocal(boolean mainBranchLocal)
    {
      // Do nothing
    }

    @Override
    public boolean hasListeners()
    {
      return false;
    }

    @Override
    public IListener[] getListeners()
    {
      return null;
    }

    @Override
    public void addListener(IListener listener)
    {
      // Do nothing
    }

    @Override
    public void removeListener(IListener listener)
    {
      // Do nothing
    }

    @Override
    public void activate() throws LifecycleException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Exception deactivate()
    {
      return ServerCDOView.this.deactivate();
    }

    @Override
    public LifecycleState getLifecycleState()
    {
      return LifecycleState.ACTIVE;
    }

    @Override
    public boolean isActive()
    {
      return ServerCDOView.this.isActive();
    }

    @Override
    public boolean isClosed()
    {
      return !isActive();
    }

    @Override
    public void close()
    {
      deactivate();
    }

    @Override
    public CDORepositoryInfo getRepositoryInfo()
    {
      return this;
    }

    @Override
    public String getName()
    {
      return repository.getName();
    }

    @Override
    public String getUUID()
    {
      return repository.getUUID();
    }

    @Override
    public Type getType()
    {
      return repository.getType();
    }

    @Override
    public State getState()
    {
      return repository.getState();
    }

    @Override
    public long getCreationTime()
    {
      return repository.getCreationTime();
    }

    @Override
    public long getTimeStamp()
    {
      return repository.getTimeStamp();
    }

    @Override
    public long getTimeStamp(boolean forceRefresh)
    {
      return getTimeStamp();
    }

    @Override
    public String getStoreType()
    {
      return repository.getStoreType();
    }

    @Override
    public Set<ObjectType> getObjectIDTypes()
    {
      return repository.getObjectIDTypes();
    }

    @Override
    public CDOID getRootResourceID()
    {
      return repository.getRootResourceID();
    }

    @Override
    public boolean isAuthenticating()
    {
      return repository.isAuthenticating();
    }

    @Override
    public boolean isSupportingLoginPeeks()
    {
      return repository.isSupportingLoginPeeks();
    }

    @Override
    public boolean isSupportingAudits()
    {
      return repository.isSupportingAudits();
    }

    @Override
    public boolean isSupportingBranches()
    {
      return repository.isSupportingBranches();
    }

    @Override
    public boolean isSupportingUnits()
    {
      return repository.isSupportingUnits();
    }

    @Override
    public boolean isSerializingCommits()
    {
      return repository.isSerializingCommits();
    }

    @Override
    public boolean isEnsuringReferentialIntegrity()
    {
      return repository.isEnsuringReferentialIntegrity();
    }

    @Override
    public IDGenerationLocation getIDGenerationLocation()
    {
      return repository.getIDGenerationLocation();
    }

    @Override
    public CommitInfoStorage getCommitInfoStorage()
    {
      return repository.getCommitInfoStorage();
    }

    @Override
    public boolean waitWhileInitial(IProgressMonitor monitor)
    {
      return repository.waitWhileInitial(monitor);
    }

    @Override
    public boolean isAuthorizingOperations()
    {
      return repository.isAuthorizingOperations();
    }

    @Override
    public String[] authorizeOperations(AuthorizableOperation... operations)
    {
      return internalSession.authorizeOperations(operations);
    }

    @Override
    public void handleRepositoryTypeChanged(Type oldType, Type newType)
    {
    }

    @Override
    public void handleRepositoryStateChanged(State oldState, State newState)
    {
    }

    @Override
    public EPackage[] loadPackages(CDOPackageUnit packageUnit)
    {
      return null;
    }

    @Override
    public void releaseAtomicRequestLock(Object key)
    {
      // Do nothing
    }

    @Override
    public void acquireAtomicRequestLock(Object key)
    {
      // Do nothing
    }

    @Override
    public Object processPackage(Object value)
    {
      return value;
    }

    @Override
    public boolean isEmpty()
    {
      return false;
    }

    @Override
    public boolean runAfterUpdate(long updateTime, Runnable runnable)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean waitForUpdate(long updateTime, long timeoutMillis)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void waitForUpdate(long updateTime)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getOpeningTime()
    {
      return openingTime;
    }

    @Override
    public long getLastUpdateTime()
    {
      return getBranchPoint().getTimeStamp();
    }

    @Override
    public long refresh(Provider provider)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public long refresh()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Options options()
    {
      return this;
    }

    @Override
    public CDOView openView(String durableLockingID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(String durableLockingID, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(long timeStamp)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(CDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(CDOBranch branch, long timeStamp)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(CDOBranchPoint target)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(CDOBranchPoint target, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOView openView(CDOBranchPoint target)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(String durableLockingID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(CDOBranch branch)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOFetchRuleManager getFetchRuleManager()
    {
      return null;
    }

    @Override
    public ExceptionHandler getExceptionHandler()
    {
      return null;
    }

    @Override
    public CDOIDGenerator getIDGenerator()
    {
      return null;
    }

    @Override
    public void viewDetached(InternalCDOView view)
    {
      // Do nothing
    }

    @Override
    public void setUserID(String userID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionProtocol(CDOSessionProtocol sessionProtocol)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionID(int sessionID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setRepositoryInfo(CDORepositoryInfo repositoryInfo)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setLastUpdateTime(long lastUpdateTime)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setIDGenerator(CDOIDGenerator idGenerator)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void resolveAllElementProxies(CDORevision revision)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void ensureChunks(InternalCDORevision revision, int chunkSize)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void processRefreshSessionResult(RefreshSessionResult result, CDOBranch branch, List<InternalCDOView> branchViews,
        Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void syncExec(RunnableWithException runnable) throws Exception
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object startLocalCommit()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void endLocalCommit(Object token)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate(InvalidationData invalidationData)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void handleCommitNotification(CommitNotificationInfo info)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender, boolean async)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void handleViewClosed(int viewID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public InternalCDORemoteSessionManager getRemoteSessionManager()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public IPasswordCredentialsProvider getCredentialsProvider()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setOneTimeLoginToken(byte[] oneTimeLoginToken)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setRevisionManager(InternalCDORevisionManager revisionManager)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setBranchManager(InternalCDOBranchManager branchManager)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCommitInfoManager(InternalCDOCommitInfoManager commitInfoManager)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSticky()
    {
      return false;
    }

    @Override
    public CDOBranchPoint getCommittedSinceLastRefresh(CDOID id)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCommittedSinceLastRefresh(CDOID id, CDOBranchPoint branchPoint)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clearCommittedSinceLastRefresh()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOChangeSetData compareRevisions(CDOBranchPoint source, CDOBranchPoint target)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public MergeData getMergeData(CDOBranchPoint target, CDOBranchPoint source, CDOBranchPoint sourceBase, boolean computeChangeSets)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public MergeData getMergeData(CDOBranchPoint target, CDOBranchPoint source, CDOBranchPoint targetBase, CDOBranchPoint sourceBase, boolean computeChangeSets)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPassiveUpdateEnabled()
    {
      return false;
    }

    @Override
    public void setPassiveUpdateEnabled(boolean enabled)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public PassiveUpdateMode getPassiveUpdateMode()
    {
      return PassiveUpdateMode.INVALIDATIONS;
    }

    @Override
    public void setPassiveUpdateMode(PassiveUpdateMode mode)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLockNotificationEnabled()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setLockNotificationEnabled(boolean enabled)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public LockNotificationMode getLockNotificationMode()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setLockNotificationMode(LockNotificationMode mode)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOSession getContainer()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isGeneratedPackageEmulationEnabled()
    {
      return generatedPackageEmulationEnabled;
    }

    @Override
    public void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled)
    {
      this.generatedPackageEmulationEnabled = generatedPackageEmulationEnabled;
    }

    @Override
    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOLobStore getLobCache()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setLobCache(CDOLobStore lobCache)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOPermissionUpdater getPermissionUpdater()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setPermissionUpdater(CDOPermissionUpdater permissionUpdater)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Map<CDORevision, CDOPermission> updatePermissions()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDelegableViewLockEnabled()
    {
      return false;
    }

    @Override
    public void setDelegableViewLockEnabled(boolean enabled)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getPrefetchSendMaxRevisionKeys()
    {
      return 0;
    }

    @Override
    public void setPrefetchSendMaxRevisionKeys(int prefetchSendMaxRevisionKeys)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOClob newClob(Reader contents) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOClob newClob(String contents) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOBlob newBlob(InputStream contents) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOBlob newBlob(byte[] contents) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void changeCredentials()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean isSupportingEcore()
    {
      throw new UnsupportedOperationException();
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
    @Deprecated
    public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void handleBranchNotification(InternalCDOBranch branch)
    {
      throw new UnsupportedOperationException();
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
    @Deprecated
    public CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void cacheRevisions(CDORevisionAvailabilityInfo info)
    {
      throw new UnsupportedOperationException();
    }
  }
}
