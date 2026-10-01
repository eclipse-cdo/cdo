/*
 * Copyright (c) 2007-2016, 2019-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTopic;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.security.operations.OperationAuthorizer;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class Session extends Container<IView> implements InternalSession
{
  private InternalSessionManager manager;

  private ISessionProtocol protocol;

  private int sessionID;

  private String userID;

  private boolean passiveUpdateEnabled = true;

  private volatile CDOCollectionLoadingConfig collectionLoadingConfig;

  private PassiveUpdateMode passiveUpdateMode = PassiveUpdateMode.INVALIDATIONS;

  private volatile LockNotificationMode lockNotificationMode = LockNotificationMode.IF_REQUIRED_BY_VIEWS;

  private final LockChangeDispatcher<LockNotificationContext, CDOLockChangeInfo> lockChangeDispatcher = new LockChangeDispatcher<>();

  private boolean openOnClientSide;

  private long openingTime;

  private long firstUpdateTime;

  private long lastUpdateTime;

  @ExcludeFromDump
  private Object lastUpdateTimeLock = new Object();

  private Map<Integer, InternalView> views = new HashMap<>();

  private AtomicInteger lastTempViewID = new AtomicInteger();

  private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  private boolean subscribed;

  /**
   * @since 2.0
   */
  public Session(InternalSessionManager manager, ISessionProtocol protocol, int sessionID, String userID)
  {
    this.manager = manager;
    this.protocol = protocol;
    this.sessionID = sessionID;
    this.userID = userID;

    EventUtil.addListener(protocol, protocolListener);
    activate();
  }

  /**
   * @since 2.0
   */
  @Override
  public Options options()
  {
    return this;
  }

  @Override
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOCommonSession getContainer()
  {
    return this;
  }

  @Override
  public InternalSessionManager getManager()
  {
    return manager;
  }

  @Override
  public InternalRepository getRepository()
  {
    return manager.getRepository();
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return manager.getExecutorService();
  }

  @Override
  public CDORevisionManager getRevisionManager()
  {
    return getRepository().getRevisionManager();
  }

  @Override
  public CDOBranchManager getBranchManager()
  {
    return getRepository().getBranchManager();
  }

  @Override
  public CDOCommitInfoManager getCommitInfoManager()
  {
    return getRepository().getCommitInfoManager();
  }

  @Override
  public ISessionProtocol getProtocol()
  {
    return protocol;
  }

  @Override
  public int getSessionID()
  {
    return sessionID;
  }

  /**
   * @since 2.0
   */
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
  public CDOCollectionLoadingConfig getCollectionLoadingConfig()
  {
    return collectionLoadingConfig;
  }

  @Override
  public void setCollectionLoadingConfig(CDOCollectionLoadingConfig config)
  {
    checkActive();
    collectionLoadingConfig = config == null ? null : new CDOCollectionLoadingConfig(config.getDefaultChunkConfig(), config.getOverrides());
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isSubscribed()
  {
    return subscribed;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setSubscribed(boolean subscribed)
  {
    checkActive();
    if (this.subscribed != subscribed)
    {
      this.subscribed = subscribed;
      byte opcode = subscribed ? CDOProtocolConstants.REMOTE_SESSION_SUBSCRIBED : CDOProtocolConstants.REMOTE_SESSION_UNSUBSCRIBED;
      manager.sendRemoteSessionNotification(this, opcode);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    checkActive();
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  public PassiveUpdateMode getPassiveUpdateMode()
  {
    return passiveUpdateMode;
  }

  @Override
  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    checkActive();
    checkArg(passiveUpdateMode, "passiveUpdateMode"); //$NON-NLS-1$
    this.passiveUpdateMode = passiveUpdateMode;
  }

  @Override
  public boolean isLockNotificationEnabled()
  {
    switch (lockNotificationMode)
    {
    case ALWAYS:
      return true;

    case OFF:
      return false;

    case IF_REQUIRED_BY_VIEWS:
      for (InternalView view : getViews())
      {
        if (view.options().isLockNotificationEnabled())
        {
          return true;
        }
      }

      return false;

    default:
      throw new IllegalStateException("Invalid lock notification mode: " + lockNotificationMode);
    }
  }

  @Override
  public void setLockNotificationEnabled(boolean enabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LockNotificationMode getLockNotificationMode()
  {
    return lockNotificationMode;
  }

  @Override
  public void setLockNotificationMode(LockNotificationMode lockNotificationMode)
  {
    checkActive();
    checkArg(lockNotificationMode, "lockNotificationMode"); //$NON-NLS-1$
    this.lockNotificationMode = lockNotificationMode;
  }

  @Override
  public long getOpeningTime()
  {
    return openingTime;
  }

  @Override
  public void setOpeningTime(long openingTime)
  {
    checkState(this.openingTime == 0, "Opening time is already set"); //$NON-NLS-1$
    this.openingTime = openingTime;
  }

  @Override
  @Deprecated
  public long getLastUpdateTime()
  {
    synchronized (lastUpdateTimeLock)
    {
      return lastUpdateTime;
    }
  }

  @Override
  public long getFirstUpdateTime()
  {
    return firstUpdateTime;
  }

  @Override
  public void setFirstUpdateTime(long firstUpdateTime)
  {
    this.firstUpdateTime = firstUpdateTime;
  }

  @Override
  public boolean isOpenOnClientSide()
  {
    return openOnClientSide;
  }

  @Override
  public void setOpenOnClientSide()
  {
    openOnClientSide = true;
    manager.openedOnClientSide(this);
  }

  @Override
  public Entity.Store getEntityStore()
  {
    return manager.getRepository().getEntityStore();
  }

  @Override
  public InternalView[] getElements()
  {
    checkActive();
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();

    synchronized (views)
    {
      return views.isEmpty();
    }
  }

  @Override
  public InternalView[] getViews()
  {
    checkActive();
    return getViewsArray();
  }

  private InternalView[] getViewsArray()
  {
    synchronized (views)
    {
      return views.values().toArray(new InternalView[views.size()]);
    }
  }

  @Override
  public InternalView getView(int viewID)
  {
    checkActive();

    synchronized (views)
    {
      return views.get(viewID);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalView openView(int viewID, CDOBranchPoint branchPoint)
  {
    return openView(viewID, branchPoint, null);
  }

  /**
   * @since 4.19
   */
  @Override
  public InternalView openView(int viewID, CDOBranchPoint branchPoint, String durableLockingID)
  {
    checkActive();
    if (viewID == TEMP_VIEW_ID)
    {
      viewID = -lastTempViewID.incrementAndGet();
    }

    InternalView view = new View(this, viewID, branchPoint, durableLockingID);
    view.activate();
    addView(view);
    return view;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalTransaction openTransaction(int viewID, CDOBranchPoint branchPoint)
  {
    return openTransaction(viewID, branchPoint, null);
  }

  /**
   * @since 4.19
   */
  @Override
  public InternalTransaction openTransaction(int viewID, CDOBranchPoint branchPoint, String durableLockingID)
  {
    checkActive();
    if (viewID == TEMP_VIEW_ID)
    {
      viewID = -lastTempViewID.incrementAndGet();
    }

    InternalTransaction transaction = new Transaction(this, viewID, branchPoint, durableLockingID);
    transaction.activate();
    addView(transaction);
    return transaction;
  }

  private void addView(InternalView view)
  {
    checkActive();
    int viewID = view.getViewID();

    synchronized (views)
    {
      views.put(viewID, view);
    }

    fireElementAddedEvent(view);
  }

  /**
   * @since 2.0
   * @deprecated
   */
  @Override
  @Deprecated
  public void viewClosed(InternalView view)
  {
    viewClosed(view, false);
  }

  @Override
  public void viewClosed(InternalView view, boolean inverse)
  {
    int viewID = view.getViewID();
    InternalView removedView;

    synchronized (views)
    {
      removedView = views.remove(viewID);
    }

    if (removedView == view)
    {
      view.doClose();

      try
      {
        if (!inverse && protocol != null)
        {
          protocol.sendViewClosedNotification(viewID);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
      finally
      {
        fireElementRemovedEvent(view);
      }
    }
  }

  /**
   * TODO I can't see how recursion is controlled/limited
   *
   * @since 2.0
   */
  @Override
  public void collectContainedRevisions(InternalCDORevision revision, CDOBranchPoint branchPoint, int referenceChunk, Set<CDOID> revisions,
      List<CDORevision> additionalRevisions)
  {
    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    for (EStructuralFeature feature : revision.getClassInfo().getAllPersistentContainments())
    {
      if (!feature.isMany())
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!CDOIDUtil.isNull(id) && !revisions.contains(id))
          {
            Request.Config config = new Request.Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, referenceChunk);
            InternalCDORevision containedRevision = revisionManager.getRevision(id, branchPoint, config);
            additionalRevisions.add(containedRevision);

            revisions.add(id);

            // Recurse
            collectContainedRevisions(containedRevision, branchPoint, referenceChunk, revisions, additionalRevisions);
          }
        }
      }
    }
  }

  @Override
  public CDOID provideCDOID(Object idObject)
  {
    return (CDOID)idObject;
  }

  @Override
  public void loadLob(CDOLobInfo info, Object outputStreamOrWriter) throws IOException
  {
    getRepository().loadLob(info, outputStreamOrWriter);
  }

  @Override
  public String[] authorizeOperations(AuthorizableOperation... operations)
  {
    String[] result = new String[operations.length];
    OperationAuthorizer<ISession> authorizer = getRepository();

    for (int i = 0; i < operations.length; i++)
    {
      AuthorizableOperation operation = operations[i];

      try
      {
        result[i] = authorizer.authorizeOperation(this, operation);
      }
      catch (Error ex)
      {
        throw ex;
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
        result[i] = "Error: " + t.getLocalizedMessage();
      }
    }

    return result;
  }

  @Override
  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext)
  {
    IPermissionManager permissionManager = manager.getPermissionManager();
    if (permissionManager != null)
    {
      return permissionManager.getPermission(revision, securityContext, this);
    }

    return CDORevision.PERMISSION_PROVIDER.getPermission(revision, securityContext);
  }

  @Override
  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRepositoryTypeNotification(oldType, newType);
    }
  }

  @Override
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState) throws Exception
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  @Override
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState, CDOID rootResourceID) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRepositoryStateNotification(oldState, newState, rootResourceID);
    }
  }

  @Override
  @Deprecated
  public void sendBranchNotification(InternalCDOBranch branch) throws Exception
  {
    sendBranchNotification(branch, ChangeKind.CREATED);
  }

  @Deprecated
  @Override
  public void sendBranchNotification(InternalCDOBranch branch, ChangeKind changeKind) throws Exception
  {
    sendBranchNotification(changeKind, branch);
  }

  @Override
  public void sendBranchNotification(ChangeKind changeKind, CDOBranch... branches) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendBranchNotification(changeKind, branches);
    }
  }

  @Override
  public void sendTagNotification(int modCount, String oldName, String newName, CDOBranchPoint branchPoint) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendTagNotification(modCount, oldName, newName, branchPoint);
    }
  }

  @Override
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo, boolean clearResourcePathCache) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendCommitNotification(CommitNotificationInfo notificationInfo) throws Exception
  {
    if (protocol == null)
    {
      return;
    }

    if (!isPassiveUpdateEnabled())
    {
      return;
    }

    byte securityImpact = notificationInfo.getSecurityImpact();
    if (securityImpact == CommitNotificationInfo.IMPACT_PERMISSIONS)
    {
      IPermissionManager permissionManager = manager.getPermissionManager();
      Set<? extends Object> impactedRules = notificationInfo.getImpactedRules();

      if (ObjectUtil.isEmpty(impactedRules) || !permissionManager.hasAnyRule(this, impactedRules))
      {
        securityImpact = CommitNotificationInfo.IMPACT_NONE;
      }
    }

    CommitInfo sessionCommitInfo = new CommitInfo(notificationInfo);

    CommitNotificationInfo sessionNotificationInfo = new CommitNotificationInfo();
    sessionNotificationInfo.setSender(notificationInfo.getSender());
    sessionNotificationInfo.setCommitInfo(sessionCommitInfo);
    sessionNotificationInfo.setRevisionProvider(notificationInfo.getRevisionProvider());
    sessionNotificationInfo.setClearResourcePathCache(notificationInfo.isClearResourcePathCache());
    sessionNotificationInfo.setNewPermissions(sessionCommitInfo.getNewPermissions());
    sessionNotificationInfo.setSecurityImpact(securityImpact);
    sessionNotificationInfo.setModifiedByServer(notificationInfo.isModifiedByServer());

    CDOLockChangeInfo lockChangeInfo = notificationInfo.getLockChangeInfo();
    if (lockChangeInfo != null && notificationInfo.getLockModCount() > 0)
    {
      sessionNotificationInfo.setLockChangeInfo(lockChangeInfo);
      sessionNotificationInfo.setLockModCount(notificationInfo.getLockModCount());
    }

    protocol.sendCommitNotification(sessionNotificationInfo);

    synchronized (lastUpdateTimeLock)
    {
      CDOCommitInfo originalCommitInfo = notificationInfo.getCommitInfo();
      lastUpdateTime = originalCommitInfo.getTimeStamp();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo) throws Exception
  {
    if (protocol != null)
    {
      Object lockNotificationRequired = isLockNotificationRequired(lockChangeInfo);
      if (lockNotificationRequired == Boolean.TRUE)
      {
        protocol.sendLockNotification(lockChangeInfo, null);
      }
      else if (lockNotificationRequired instanceof InternalView)
      {
        InternalView view = (InternalView)lockNotificationRequired;

        try
        {
          protocol.sendLockNotification(lockChangeInfo, null);
        }
        catch (Exception ex)
        {
          if (!view.isClosed())
          {
            OM.LOG.warn("A problem occured while notifying view " + view, ex);
          }
        }
      }
      else if (lockNotificationRequired instanceof Set)
      {
        @SuppressWarnings("unchecked")
        Set<CDOID> lockedIDs = (Set<CDOID>)lockNotificationRequired;
        protocol.sendLockNotification(lockChangeInfo, lockedIDs);
      }
    }
  }

  void sendLockNotification(CDOLockChangeInfo lockChangeInfo, LockChangeDispatcher.TicketResult<CDOLockChangeInfo> result) throws Exception
  {
    if (protocol == null || result == null || result.getLockModCount() == 0)
    {
      return;
    }

    LockChangeDispatcher.Projection<CDOLockChangeInfo> projection = result.getProjection();
    if (projection == null || !projection.isVisible())
    {
      return;
    }

    Set<CDOID> filteredIDs = null;
    if (projection.getFilteredIDs() != null)
    {
      @SuppressWarnings("unchecked")
      Set<CDOID> ids = (Set<CDOID>)projection.getFilteredIDs();
      filteredIDs = ids;
    }

    protocol.sendLockNotification(projection.getValue(), filteredIDs, result.getLockModCount());
  }

  @Override
  @SuppressWarnings("deprecation")
  public void sendLockOwnerRemappedNotification(CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendLockOwnerRemappedNotification(branch, oldOwner, newOwner);
    }
  }

  @Override
  public void sendLockOwnerRemappedNotification(CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner, long lockModCount) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendLockOwnerRemappedNotification(branch, oldOwner, newOwner, lockModCount);
    }
  }

  LockChangeDispatcher.Ticket<LockNotificationContext, CDOLockChangeInfo> reserveLockChange(boolean forceFull)
  {
    if (isClosed())
    {
      return null;
    }

    LockNotificationContext context;
    try
    {
      context = captureLockNotificationContext(forceFull);
    }
    catch (LifecycleException ex)
    {
      if (!isClosed())
      {
        throw ex;
      }

      return null;
    }

    return lockChangeDispatcher.reserve(context);
  }

  private LockNotificationContext captureLockNotificationContext()
  {
    return captureLockNotificationContext(false);
  }

  private LockNotificationContext captureLockNotificationContext(boolean forceFull)
  {
    Set<CDOLockOwner> owners = new HashSet<>();
    List<ViewLockNotificationContext> viewContexts = new ArrayList<>();

    for (InternalView view : getViews())
    {
      CDOLockOwner owner = view.getLockOwner();
      owners.add(owner);
      viewContexts.add(new ViewLockNotificationContext(view.getBranch(), view.options().isLockNotificationEnabled()));
    }

    return new LockNotificationContext(options().getLockNotificationMode(), viewContexts, owners, forceFull);
  }

  /**
   * Returns the last sequence number assigned to a lock-state change visible to
   * this server session. The value is zero until its first relevant change.
   *
   * @return this session's current lock-state sequence number
   */
  @Override
  public long getLockModCount()
  {
    return lockChangeDispatcher.getLockModCount();
  }

  void setLockModCountBaseline(long lockModCount)
  {
    lockChangeDispatcher.setInitialLockModCount(lockModCount);
  }

  boolean isLockChangesQuiescent()
  {
    return lockChangeDispatcher.isQuiescent();
  }

  boolean isLockStateRelevantForSnapshot(CDOLockState state)
  {
    CDOBranch branch = state.getBranch();
    LockNotificationMode mode = options().getLockNotificationMode();
    if (mode == LockNotificationMode.ALWAYS)
    {
      return true;
    }

    Set<CDOLockOwner> owners = new HashSet<>();
    for (InternalView view : getViews())
    {
      if (mode == LockNotificationMode.IF_REQUIRED_BY_VIEWS && view.options().isLockNotificationEnabled()
          && (branch == null || view.getBranch() == branch))
      {
        return true;
      }

      owners.add(view.getLockOwner());
    }

    for (CDOLockOwner owner : state.getReadLockOwners())
    {
      if (owners.contains(owner))
      {
        return true;
      }
    }

    return owners.contains(state.getWriteLockOwner()) || owners.contains(state.getWriteOptionOwner());
  }

  void awaitLockChangesQuiescent()
  {
    lockChangeDispatcher.awaitQuiescence();
  }

  void completeLockChange(LockChangeDispatcher.Ticket<LockNotificationContext, CDOLockChangeInfo> ticket, CDOLockChangeInfo info)
  {
    ticket.ready(info, LockNotificationContext::project);
  }

  void cancelLockChange(LockChangeDispatcher.Ticket<LockNotificationContext, CDOLockChangeInfo> ticket)
  {
    ticket.cancel();
  }

  private static final class ViewLockNotificationContext
  {
    private final CDOBranch branch;

    private final boolean enabled;

    private ViewLockNotificationContext(CDOBranch branch, boolean enabled)
    {
      this.branch = branch;
      this.enabled = enabled;
    }
  }

  static final class LockNotificationContext
  {
    private final LockNotificationMode mode;

    private final List<ViewLockNotificationContext> views;

    private final Set<CDOLockOwner> owners;

    private final boolean forceFull;

    private LockNotificationContext(LockNotificationMode mode, List<ViewLockNotificationContext> views, Set<CDOLockOwner> owners, boolean forceFull)
    {
      this.mode = mode;
      this.views = Collections.unmodifiableList(views);
      this.owners = Collections.unmodifiableSet(owners);
      this.forceFull = forceFull;
    }

    private LockChangeDispatcher.Projection<CDOLockChangeInfo> project(CDOLockChangeInfo info)
    {
      if (forceFull || mode == LockNotificationMode.ALWAYS)
      {
        return new LockChangeDispatcher.Projection<>(true, info);
      }

      CDOBranch affectedBranch = info.getBranch();
      if (mode == LockNotificationMode.IF_REQUIRED_BY_VIEWS)
      {
        for (ViewLockNotificationContext view : views)
        {
          if (view.enabled && (affectedBranch == null || affectedBranch == view.branch))
          {
            return new LockChangeDispatcher.Projection<>(true, info);
          }
        }
      }

      Set<CDOID> lockedIDs = new HashSet<>();
      for (CDOLockDelta delta : info.getLockDeltas())
      {
        if (affectedBranch != null && delta.getBranch() != null && delta.getBranch() != affectedBranch)
        {
          continue;
        }

        CDOID id = delta.getID();
        boolean owned = owners.contains(delta.getOldOwner()) && delta.getOldOwner() != null;
        for (CDOLockState state : info.getLockStates())
        {
          if (state.getID().equals(id) && state.getBranch() == delta.getBranch() && isOwnedBySession(state))
          {
            owned = true;
            break;
          }
        }

        if (owned)
        {
          lockedIDs.add(id);
        }
      }

      return lockedIDs.isEmpty() ? new LockChangeDispatcher.Projection<>(false, null)
          : new LockChangeDispatcher.Projection<>(true, info, lockedIDs);
    }

    private boolean isOwnedBySession(CDOLockState state)
    {
      if (state == null)
      {
        return false;
      }

      for (CDOLockOwner owner : state.getReadLockOwners())
      {
        if (owners.contains(owner))
        {
          return true;
        }
      }

      return owners.contains(state.getWriteLockOwner()) || owners.contains(state.getWriteOptionOwner());
    }
  }

  private Object isLockNotificationRequired(CDOLockChangeInfo lockChangeInfo)
  {
    LockChangeDispatcher.Projection<CDOLockChangeInfo> projection = captureLockNotificationContext().project(lockChangeInfo);
    if (!projection.isVisible())
    {
      return null;
    }

    Set<?> filteredIDs = projection.getFilteredIDs();
    return filteredIDs == null ? Boolean.TRUE : filteredIDs;
  }

  private boolean isDeltaNeeded(CDOID id, InternalView[] views)
  {
    boolean supportingUnits = getRepository().isSupportingUnits();

    for (InternalView view : views)
    {
      try
      {
        if (view.hasSubscription(id))
        {
          return true;
        }

        if (supportingUnits && view.isInOpenUnit(id))
        {
          return true;
        }
      }
      catch (Exception ex)
      {
        if (!view.isClosed())
        {
          OM.LOG.warn("A problem occured while checking subscriptions of view " + view, ex);
        }
      }
    }

    return false;
  }

  @Override
  @Deprecated
  public void sendRemoteSessionNotification(InternalSession sender, byte opcode) throws Exception
  {
    sendRemoteSessionNotification(sender, null, opcode);
  }

  @Override
  public void sendRemoteSessionNotification(InternalSession sender, InternalTopic topic, byte opcode) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRemoteSessionNotification(sender, topic, opcode);
    }
  }

  @Override
  @Deprecated
  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message) throws Exception
  {
    sendRemoteMessageNotification(sender, null, message);
  }

  @Override
  public void sendRemoteMessageNotification(InternalSession sender, InternalTopic topic, CDORemoteSessionMessage message) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRemoteMessageNotification(sender, topic, message);
    }
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
    String name = "unknown";
    if (manager != null)
    {
      InternalRepository repository = getRepository();
      if (repository != null)
      {
        name = repository.getName();
      }
    }

    if (userID != null && userID.length() != 0)
    {
      name = userID + "@" + name;
    }

    return MessageFormat.format("Session{0} [{1}]", sessionID, name); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
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

  @Override
  protected void doDeactivate() throws Exception
  {
    lockChangeDispatcher.close();

    EventUtil.removeListener(protocol, protocolListener);
    protocolListener = null;

    LifecycleUtil.deactivate(protocol, OMLogger.Level.DEBUG);
    protocol = null;

    for (IView view : getViewsArray())
    {
      view.close();
    }

    views = null;
    manager.sessionClosed(this);
    manager = null;
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class CommitInfo extends DelegatingCommitInfo
  {
    private final CDOCommitInfo delegate;

    private final CDORevisionProvider revisionProvider;

    private final InternalView[] views;

    private final IPermissionManager permissionManager;

    private final Map<CDOID, CDOPermission> newPermissions;

    private final boolean additions;

    private final boolean changes;

    public CommitInfo(CommitNotificationInfo notificationInfo)
    {
      delegate = notificationInfo.getCommitInfo();
      revisionProvider = notificationInfo.getRevisionProvider();

      views = getViews();
      permissionManager = manager.getPermissionManager();
      if (permissionManager != null)
      {
        newPermissions = CDOIDUtil.createMap();
      }
      else
      {
        newPermissions = null;
      }

      PassiveUpdateMode passiveUpdateMode = getPassiveUpdateMode();
      additions = passiveUpdateMode == PassiveUpdateMode.ADDITIONS;
      changes = additions || passiveUpdateMode == PassiveUpdateMode.CHANGES;
    }

    @Override
    protected CDOCommitInfo getDelegate()
    {
      return delegate;
    }

    protected void addNewPermission(CDOID id, CDOPermission permission)
    {
      newPermissions.put(id, permission);
    }

    public Map<CDOID, CDOPermission> getNewPermissions()
    {
      return newPermissions;
    }

    @Override
    public List<CDOIDAndVersion> getNewObjects()
    {
      List<CDOIDAndVersion> newObjects = super.getNewObjects();
      return new IndexedList<>()
      {
        @Override
        public CDOIDAndVersion get(int index)
        {
          CDORevision revision = (CDORevision)newObjects.get(index);
          if (additions)
          {
            if (permissionManager == null)
            {
              // Return full revision
              return revision;
            }

            CDOPermission permission = permissionManager.getPermission(revision, delegate, Session.this);
            CDOID id = revision.getID();
            addNewPermission(id, permission);

            if (permission != CDOPermission.NONE)
            {
              // Return full revision
              return revision;
            }
          }

          // Prevent sending full revision by copying the id and version
          return CDOIDUtil.createIDAndVersion(revision);
        }

        @Override
        public int size()
        {
          return newObjects.size();
        }
      };
    }

    @Override
    public List<CDORevisionKey> getChangedObjects()
    {
      final List<CDORevisionKey> changedObjects = super.getChangedObjects();
      return new IndexedList<>()
      {
        @Override
        public CDORevisionKey get(int index)
        {
          CDORevisionDelta revisionDelta = (CDORevisionDelta)changedObjects.get(index);
          CDOID id = revisionDelta.getID();

          if (changes || isDeltaNeeded(id, views))
          {
            if (permissionManager == null)
            {
              // Return full delta
              return revisionDelta;
            }

            if (revisionProvider == null)
            {
              // Return full delta
              return revisionDelta;
            }

            CDORevision newRevision = revisionProvider.getRevision(id);
            CDOPermission permission = permissionManager.getPermission(newRevision, delegate, Session.this);
            addNewPermission(id, permission);

            if (permission != CDOPermission.NONE)
            {
              // Return full delta
              return revisionDelta;
            }
          }

          // Prevent sending full delta by copying the id and version
          return CDORevisionUtil.copyRevisionKey(revisionDelta);
        }

        @Override
        public int size()
        {
          return changedObjects.size();
        }
      };
    }
  }
}
