/*
 * Copyright (c) 2009-2016, 2018-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOLocalAdapter;
import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.UnitOpcode;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.MapRevisionProvider;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.LockTimeoutException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.util.StaleRevisionLockException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.cdo.view.CDOUnit;
import org.eclipse.emf.cdo.view.CDOUnitManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewDurabilityChangedEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDODeltaNotificationImpl;
import org.eclipse.emf.internal.cdo.object.CDOInvalidationNotificationImpl;
import org.eclipse.emf.internal.cdo.object.CDONotificationBuilder;
import org.eclipse.emf.internal.cdo.object.CDOObjectWrapperBase;
import org.eclipse.emf.internal.cdo.session.SessionUtil;
import org.eclipse.emf.internal.cdo.util.AbstractLocksChangedEvent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RunnableWithName;
import org.eclipse.net4j.util.concurrent.SerializingExecutor;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.event.ThrowableEvent;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.CDOLockStateCache;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import org.eclipse.core.runtime.IProgressMonitor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends AbstractCDOView
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private InternalCDOSession session;

  private String durableLockingID;

  private long lastUpdateTime;

  private final CDOUnitManagerImpl unitManager = new CDOUnitManagerImpl();

  private final CommitInfoDistributor commitInfoDistributor = new CommitInfoDistributor();

  private ChangeSubscriptionManager changeSubscriptionManager = new ChangeSubscriptionManager();

  private final AdapterManager adapterManager = new AdapterManager();

  private final OptionsImpl options;

  @ExcludeFromDump
  private CDOLockOwner lockOwner;

  @ExcludeFromDump
  private ViewInvalidator invalidator = new ViewInvalidator();

  @ExcludeFromDump
  private volatile boolean invalidating;

  @ExcludeFromDump
  private boolean closing;

  @ExcludeFromDump
  private boolean inverseClosing;

  private DurableLockProcessor durableLockProcessor;

  /**
   * @since 2.0
   */
  public CDOViewImpl(CDOSession session, CDOBranch branch, long timeStamp)
  {
    super(session, branch.getPoint(timeStamp));
    options = createOptions();
  }

  public CDOViewImpl(CDOSession session, String durableLockingID)
  {
    super(session);
    this.durableLockingID = durableLockingID;
    options = createOptions();
  }

  /**
   * @since 2.0
   */
  @Override
  public OptionsImpl options()
  {
    return options;
  }

  @Override
  public int getViewID()
  {
    return viewID;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setViewID(int viewId)
  {
    viewID = viewId;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return ConcurrencyUtil.getExecutorService(session);
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOSession getSession()
  {
    return session;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setSession(InternalCDOSession session)
  {
    super.setSession(session);
    this.session = session;
  }

  @Override
  public int getSessionID()
  {
    return session.getSessionID();
  }

  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint, IProgressMonitor progressMonitor)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        branchPoint = adjustBranchPoint(branchPoint);

        long timeStamp = branchPoint.getTimeStamp();
        long creationTimeStamp = session.getRepositoryInfo().getCreationTime();
        if (timeStamp != UNSPECIFIED_DATE && timeStamp < creationTimeStamp)
        {
          throw new IllegalArgumentException(MessageFormat.format("timeStamp ({0}) < repository creation time ({1})", //$NON-NLS-1$
              CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(creationTimeStamp)));
        }

        CDOBranchPoint oldBranchPoint = CDOBranchUtil.copyBranchPoint(getBranchPoint());
        if (branchPoint.equals(oldBranchPoint))
        {
          return false;
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Changing view target to {0}", branchPoint); //$NON-NLS-1$
        }

        Map<CDOID, InternalCDORevision> oldRevisions = CDOIDUtil.createMap();
        List<CDORevisionKey> allChangedObjects = new ArrayList<>();
        List<CDOIDAndVersion> allDetachedObjects = new ArrayList<>();

        List<InternalCDOObject> invalidObjects = getInvalidObjects(branchPoint);
        for (InternalCDOObject object : invalidObjects)
        {
          InternalCDORevision revision = object.cdoRevision();
          if (revision != null)
          {
            oldRevisions.put(object.cdoID(), revision);
          }
        }

        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        sessionProtocol.switchTarget(viewID, branchPoint, invalidObjects, allChangedObjects, allDetachedObjects, EclipseMonitor.convert(progressMonitor));

        basicSetBranchPoint(branchPoint);

        try
        {
          CDOStateMachine.SWITCHING_TARGET.set(Boolean.TRUE);

          ViewInvalidationData invalidationData = new ViewInvalidationData();
          invalidationData.setLastUpdateTime(UNSPECIFIED_DATE);
          invalidationData.setBranch(branchPoint.getBranch());
          invalidationData.setAllChangedObjects(allChangedObjects);
          invalidationData.setAllDetachedObjects(allDetachedObjects);
          invalidationData.setOldRevisions(oldRevisions);
          invalidationData.setClearResourcePathCache(true);

          doInvalidate(invalidationData);
        }
        finally
        {
          CDOStateMachine.SWITCHING_TARGET.remove();
        }

        IListener[] listeners = getListeners();
        if (listeners.length != 0)
        {
          fireViewTargetChangedEvent(oldBranchPoint, listeners);
        }

        return true;
      }
      finally
      {
        unlockView();
      }
    }
  }

  private List<InternalCDOObject> getInvalidObjects(CDOBranchPoint branchPoint)
  {
    List<InternalCDOObject> result = new ArrayList<>();
    for (InternalCDOObject object : getModifiableObjects().values())
    {
      CDORevision revision = object.cdoRevision(false);
      if (revision == null || !revision.isValid(branchPoint))
      {
        result.add(object);
      }
    }

    return result;
  }

  protected CDOLockStateCache forEachLockState(CDOBranch branch, BiConsumer<CDOObject, CDOLockState> consumer)
  {
    CDOLockStateCache lockStateCache = getSession().getLockStateCache();
    lockStateCache.forEachLockState(branch, lockOwner, lockState -> {
      CDOID id = lockState.getID();

      CDOObject object = getObject(id, false);
      if (object != null)
      {
        consumer.accept(object, lockState);
      }
    });

    return lockStateCache;
  }

  /**
   * @since 2.0
   */
  @Override
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout) throws InterruptedException
  {
    lockObjects(objects, lockType, timeout, false);
  }

  @Override
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive) throws InterruptedException
  {
    checkActive();
    checkState(!isHistorical(), "Locking not supported for historial views");

    List<CDOLockDelta> newObjectLockDeltas = new ArrayList<>();
    List<CDOLockState> newObjectLockStates = new ArrayList<>();

    List<CDOLockDelta> sessionLockDeltas = null;
    List<CDOLockState> sessionLockStates = null;

    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<CDORevisionKey> revisionKeys = new ArrayList<>();

        // TODO For recursive locking consider local tree changes (NEW, DIRTY).
        for (CDOObject object : CollectionUtil.setOf(objects))
        {
          if (updateLockStateOfNewObject(object, lockType, true, newObjectLockDeltas, newObjectLockStates))
          {
            if (recursive)
            {
              for (TreeIterator<EObject> it = object.eAllContents(); it.hasNext();)
              {
                CDOObject child = CDOUtil.getCDOObject(it.next());
                updateLockStateOfNewObject(child, lockType, true, newObjectLockDeltas, newObjectLockStates);
              }
            }
          }
          else
          {
            InternalCDORevision revision = getRevision(object);
            if (revision != null)
            {
              revisionKeys.add(revision);
            }
          }
        }

        LockObjectsResult result = null;
        if (!revisionKeys.isEmpty())
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          result = sessionProtocol.lockObjects2(revisionKeys, viewID, getBranch(), lockType, recursive, timeout);

          if (!result.isSuccessful())
          {
            if (result.isTimedOut())
            {
              throw new LockTimeoutException();
            }

            CDORevisionKey[] staleRevisions = result.getStaleRevisions();
            if (staleRevisions != null)
            {
              throw new StaleRevisionLockException(staleRevisions);
            }

            throw new AssertionError("Unexpected lock result state");
          }

          if (result.isWaitForUpdate())
          {
            if (!session.options().isPassiveUpdateEnabled())
            {
              throw new AssertionError("Lock result requires client to wait, but client does not have passiveUpdates enabled");
            }

            long requiredTimestamp = result.getRequiredTimestamp();
            if (!waitForUpdate(requiredTimestamp, 10000L))
            {
              throw new IllegalStateException(
                  "Lock result requires client to wait for commit " + requiredTimestamp + ", but client did not receive invalidations after " + lastUpdateTime);
            }

            InternalCDOSession session = this.session;
            InternalCDORevisionManager revisionManager = session.getRevisionManager();

            for (CDORevisionKey requiredKey : result.getStaleRevisions())
            {
              CDOID id = requiredKey.getID();
              InternalCDOObject object = getObject(id);

              CDORevision revision = object.cdoRevision(true);
              if (!requiredKey.equals(revision))
              {
                InternalCDORevision requiredRevision = revisionManager.getRevisionByVersion(id, requiredKey, CDORevision.UNCHUNKED, true);
                InternalCDORevisionDelta revisionDelta = requiredRevision.compare(revision);
                CDOStateMachine.INSTANCE.invalidate(object, revisionDelta);
              }
            }
          }
        }

        if (result != null)
        {
          timeStamp = result.getTimestamp();

          sessionLockDeltas = result.getLockDeltas();
          if (!ObjectUtil.isEmpty(sessionLockDeltas))
          {
            sessionLockStates = result.getLockStates();
            updateLockStates(sessionLockDeltas, sessionLockStates);
          }
        }
      }
      finally
      {
        unlockView();
      }
    }

    if (!newObjectLockDeltas.isEmpty())
    {
      CDOLockChangeInfo lockChangeInfo = makeLockChangeInfo(getTimeStamp(), newObjectLockDeltas, newObjectLockStates);
      fireLocksChangedEvent(this, lockChangeInfo);
    }

    if (!ObjectUtil.isEmpty(sessionLockDeltas))
    {
      notifyLockChanges(timeStamp, sessionLockDeltas, sessionLockStates);
    }
  }

  private void updateLockStates(List<CDOLockDelta> lockDeltas, List<CDOLockState> lockStates)
  {
    CDOBranch branch = getBranch();
    CDOLockStateCache lockStateCache = session.getLockStateCache();
    lockStateCache.updateLockStates(branch, lockDeltas, lockStates, null);
  }

  protected final void notifyLockChanges(long timestamp, Collection<CDOLockDelta> lockDeltas, List<CDOLockState> lockStates)
  {
    if (!lockDeltas.isEmpty())
    {
      CDOLockChangeInfo lockChangeInfo = makeLockChangeInfo(timestamp, lockDeltas, lockStates);
      notifyLockChanges(lockChangeInfo);
    }
  }

  /**
   * Notifies the session (including other views) and this view.
   */
  protected final void notifyLockChanges(CDOLockChangeInfo lockChangeInfo)
  {
    if (lockChangeInfo != null)
    {
      CDOLockDelta[] lockDeltas = lockChangeInfo.getLockDeltas();
      if (!ObjectUtil.isEmpty(lockDeltas) || lockChangeInfo.isInvalidateAll())
      {
        // Do not call out from the current thread to other views while this view is holding its view lock!
        session.handleLockNotification(lockChangeInfo, this, true);

        if (isActive())
        {
          fireLocksChangedEvent(this, lockChangeInfo);
        }
      }
    }
  }

  protected final CDOLockChangeInfo makeLockChangeInfo(long timestamp, Collection<CDOLockDelta> lockDeltas, Collection<CDOLockState> lockStates)
  {
    CDOBranchPoint branchPoint = getBranch().getPoint(timestamp);
    CDOLockOwner lockOwner = getLockOwner();
    return CDOLockUtil.createLockChangeInfo(branchPoint, lockOwner, lockDeltas, lockStates);
  }

  @Override
  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    if (!lockChangeInfo.isInvalidateAll())
    {
      // If lockChangeInfo pertains to a different branch, do nothing.
      CDOBranch lockChangeBranch = lockChangeInfo.getBranch();
      CDOBranch viewBranch = getBranch();
      if (lockChangeBranch != viewBranch)
      {
        return;
      }

      if (lockChangeInfo.getLockOwner() == lockOwner)
      {
        return;
      }
    }

    if (options().isLockNotificationEnabled())
    {
      fireLocksChangedEvent(sender, lockChangeInfo);
    }
  }

  protected final void fireLocksChangedEvent(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    IListener[] listeners = getListeners();
    if (listeners.length != 0)
    {
      fireEvent(new ViewLocksChangedEvent(sender, lockChangeInfo), listeners);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void unlockObjects()
  {
    unlockObjects(null, null);
  }

  /**
   * @since 2.0
   */
  @Override
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    unlockObjects(objects, lockType, false);
  }

  /**
   * Note: This may get called with objects == null, and lockType == null, which is a request to remove all locks on all
   * objects in this view.
   */
  @Override
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive)
  {
    checkActive();

    List<CDOLockDelta> newObjectLockDeltas = new ArrayList<>();
    List<CDOLockState> newObjectLockStates = new ArrayList<>();

    List<CDOLockDelta> sessionLockDeltas = null;
    List<CDOLockState> sessionLockStates = null;

    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<CDOID> objectIDs = null;

        if (objects != null)
        {
          objectIDs = new ArrayList<>();

          for (CDOObject object : CollectionUtil.setOf(objects))
          {
            updateLockStateOfNewObject(object, lockType, false, newObjectLockDeltas, newObjectLockStates);
            if (updateLockStateOfNewObject(object, lockType, false, newObjectLockDeltas, newObjectLockStates))
            {
              if (recursive)
              {
                for (TreeIterator<EObject> it = object.eAllContents(); it.hasNext();)
                {
                  CDOObject child = CDOUtil.getCDOObject(it.next());
                  updateLockStateOfNewObject(child, lockType, false, newObjectLockDeltas, newObjectLockStates);
                }
              }
            }
            else if (FSMUtil.isTransient(object))
            {
              CDOID id = getID((InternalCDOObject)object, true);
              if (id != null)
              {
                objectIDs.add(id);
              }
            }
            else
            {
              objectIDs.add(object.cdoID());
            }
          }
        }
        else
        {
          unlockAllNewObjects(newObjectLockDeltas, newObjectLockStates);
        }

        UnlockObjectsResult result = null;
        if (objectIDs == null || !objectIDs.isEmpty())
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          result = sessionProtocol.unlockObjects2(this, objectIDs, lockType, recursive);
        }

        if (result != null)
        {
          timeStamp = result.getTimestamp();
          sessionLockDeltas = result.getLockDeltas();
          if (!ObjectUtil.isEmpty(sessionLockDeltas))
          {
            sessionLockStates = result.getLockStates();
            updateLockStates(sessionLockDeltas, sessionLockStates);
          }
        }
      }
      finally
      {
        unlockView();
      }
    }

    if (!newObjectLockDeltas.isEmpty())
    {
      CDOLockChangeInfo lockChangeInfo = makeLockChangeInfo(getTimeStamp(), newObjectLockDeltas, newObjectLockStates);
      fireLocksChangedEvent(this, lockChangeInfo);
    }

    if (!ObjectUtil.isEmpty(sessionLockDeltas))
    {
      notifyLockChanges(timeStamp, sessionLockDeltas, sessionLockStates);
    }
  }

  protected boolean updateLockStateOfNewObject(CDOObject object, LockType lockType, boolean on, List<CDOLockDelta> lockDeltas, List<CDOLockState> lockStates)
  {
    // We have no new objects, CDOTransactionImpl overrides.
    return false;
  }

  protected void unlockAllNewObjects(List<CDOLockDelta> lockDeltas, List<CDOLockState> lockStates)
  {
    // We have no new objects, CDOTransactionImpl overrides.
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOLockState[] result = { null };

        if (FSMUtil.isNew(object))
        {
          result[0] = getLockStateOfNewObject(object);
        }
        else
        {
          CDOLockStateCache lockStateCache = session.getLockStateCache();
          CDOBranch branch = getBranch();
          Collection<CDOID> ids = Collections.singleton(object.cdoID());

          lockStateCache.getLockStates(branch, ids, true, lockState -> {
            result[0] = lockState;
          });
        }

        return result[0] == null ? false : result[0].isLocked(lockType, lockOwner, byOthers);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected InternalCDOLockState getLockStateOfNewObject(CDOObject object)
  {
    return null;
  }

  @Override
  public boolean isDurableView()
  {
    return durableLockingID != null;
  }

  @Override
  public String getDurableLockingID()
  {
    synchronized (getViewMonitor())
    {
      lockView(); // TODO ???

      try
      {
        return durableLockingID;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public String enableDurableLocking()
  {
    String oldID = null;
    String newID = null;

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        oldID = durableLockingID;
        if (oldID == null)
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          newID = sessionProtocol.changeLockArea(this, true);

          durableLockingID = newID;
          adjustLockOwner();
        }
        else
        {
          newID = oldID;
        }
      }
      finally
      {
        unlockView();
      }
    }

    fireDurabilityChangedEvent(oldID, newID);
    return newID;
  }

  @Override
  public void disableDurableLocking(boolean releaseLocks)
  {
    String oldID = null;

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        oldID = durableLockingID;
        if (oldID != null)
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          sessionProtocol.changeLockArea(this, false);

          durableLockingID = null;
          adjustLockOwner();

          if (releaseLocks)
          {
            unlockObjects();
          }
        }
      }
      finally
      {
        unlockView();
      }
    }

    fireDurabilityChangedEvent(oldID, null);
  }

  protected void adjustLockOwner()
  {
    // Recreate lockOwner with new durableLockingID.
    CDOLockOwner oldOwner = lockOwner;
    lockOwner = CDOLockUtil.createLockOwner(this);

    CDOBranch branch = getBranch();
    CDOLockStateCache lockStateCache = session.getLockStateCache();
    lockStateCache.remapOwner(branch, oldOwner, lockOwner);
  }

  private void fireDurabilityChangedEvent(String oldID, String newID)
  {
    if (!ObjectUtil.equals(oldID, newID))
    {
      fireEvent(new DurabilityChangedEvent(oldID, newID));
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOTransaction toTransaction()
  {
    checkActive();
    if (this instanceof InternalCDOTransaction)
    {
      return (InternalCDOTransaction)this;
    }

    throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.0"), this)); //$NON-NLS-1$
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        InternalCDORevisionManager revisionManager = session.getRevisionManager();
        int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
        CDOBranchPoint branchPoint = getBranchPointForID(id);
        return revisionManager.getRevision(id, branchPoint, initialChunkSize, CDORevision.DEPTH_NONE, loadOnDemand);
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  protected void objectRegistered(InternalCDOObject object)
  {
    super.objectRegistered(object);
    unitManager.addObject(object);
  }

  @Override
  public CDOLockOwner getLockOwner()
  {
    return lockOwner;
  }

  @Override
  public void refreshLockStates(Consumer<CDOLockState> consumer)
  {
    checkActive();
    checkState(getTimeStamp() == UNSPECIFIED_DATE, "Locking not supported for historial views");

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<CDOID> ids = new ArrayList<>();

        CDOBranch branch = getBranch();
        CDOLockStateCache lockStateCache = forEachLockState(branch, (object, lockState) -> ids.add(lockState.getID()));

        if (!ids.isEmpty())
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          List<CDOLockState> loadedLockStates = sessionProtocol.getLockStates2(viewID, ids, CDOLockState.DEPTH_NONE);

          if (!ObjectUtil.isEmpty(loadedLockStates))
          {
            lockStateCache.addLockStates(branch, loadedLockStates, null);
          }
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public final CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    return getLockStates(ids, true);
  }

  @Override
  public final CDOLockState[] getLockStates(Collection<CDOID> ids, boolean loadOnDemand)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return getLockStatesUnsynced(ids, loadOnDemand);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected final CDOLockState[] getLockStatesUnsynced(Collection<CDOID> ids, boolean loadOnDemand)
  {
    List<CDOLockState> result = new ArrayList<>();
    Collection<CDOID> missingIDs = collectLockStatesAndReturnMissingIDs(ids, loadOnDemand, result);

    if (!ObjectUtil.isEmpty(missingIDs))
    {
      CDOBranch branch = getBranch();
      CDOLockStateCache lockStateCache = session.getLockStateCache();
      lockStateCache.getLockStates(branch, missingIDs, loadOnDemand, result::add);
    }

    return result.toArray(new CDOLockState[result.size()]);
  }

  protected Collection<CDOID> collectLockStatesAndReturnMissingIDs(Collection<CDOID> ids, boolean loadOnDemand, List<CDOLockState> result)
  {
    if (ObjectUtil.isEmpty(ids))
    {
      return ids;
    }

    // Filter out ids that are duplicate.
    return new HashSet<>(ids);
  }

  private CDOBranchPoint getBranchPointForID(CDOID id)
  {
    // If this view's timestamp is something other than UNSPECIFIED_DATE,
    // then this is an 'audit' view, and so this timestamp must always be
    // used without any concern for possible sticky-view behavior
    CDOBranchPoint branchPoint = getNormalizedBranchPoint();
    if (branchPoint.getTimeStamp() != UNSPECIFIED_DATE)
    {
      return branchPoint;
    }

    if (session.isSticky())
    {
      branchPoint = session.getCommittedSinceLastRefresh(id);
      if (branchPoint == null)
      {
        branchPoint = getBranch().getPoint(session.getLastUpdateTime());
      }

      return branchPoint;
    }

    return this;
  }

  @Override
  public void prefetchRevisions(CDOID id, int depth)
  {
    checkArg(depth != CDORevision.DEPTH_NONE, "Prefetch depth must not be zero"); //$NON-NLS-1$

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
        prefetchRevisions(id, depth, initialChunkSize);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected void prefetchRevisions(CDOID id, int depth, int initialChunkSize)
  {
    CDORevisionManager revisionManager = session.getRevisionManager();
    revisionManager.getRevision(id, this, initialChunkSize, depth, true);
  }

  /*
   * Must not by synchronized on the view!
   */
  @Override
  public void invalidate(ViewInvalidationData invalidationData)
  {
    if (invalidationData.isAsync())
    {
      ViewInvalidation work = new ViewInvalidation(invalidationData);
      invalidator.execute(work);
    }
    else
    {
      doInvalidate(invalidationData);
    }
  }

  protected void doInvalidate(ViewInvalidationData invalidationData)
  {
    long timeStamp = invalidationData.getLastUpdateTime();

    try
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          doInvalidateUnsynced(invalidationData);
        }
        finally
        {
          unlockView();
        }
      }

      commitInfoDistributor.distribute(timeStamp);
    }
    catch (RuntimeException | Error ex)
    {
      commitInfoDistributor.error(timeStamp, ex);
    }
  }

  private void doInvalidateUnsynced(ViewInvalidationData invalidationData)
  {
    if (invalidationData.getSecurityImpact() != CommitNotificationInfo.IMPACT_NONE)
    {
      CDOBranchPoint head = session.getBranchManager().getMainBranch().getHead();
      if (!head.equals(this))
      {
        throw new IllegalStateException("Security not supported with auditing or branching");
      }
    }

    if (getTimeStamp() != UNSPECIFIED_DATE && CDOStateMachine.SWITCHING_TARGET.get() != Boolean.TRUE)
    {
      // Don't invalidate historical views unless during a branch point switch.
      return;
    }

    long lastUpdateTime = invalidationData.getLastUpdateTime();

    try
    {
      // Also false for FailureCommitInfos (because of branch==null). Only setLastUpdateTime() is called below.
      if (invalidationData.getBranch() == getBranch())
      {
        if (invalidationData.isClearResourcePathCache())
        {
          clearResourcePathCacheIfNecessary(null);
        }

        List<CDORevisionDelta> deltas = new ArrayList<>();
        Map<CDOObject, CDORevisionDelta> revisionDeltas = new HashMap<>();
        Set<CDOObject> detachedObjects = new HashSet<>();
        Map<CDOID, InternalCDORevision> oldRevisions = invalidationData.getOldRevisions();
        if (oldRevisions == null)
        {
          oldRevisions = CDOIDUtil.createMap();
        }

        Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = invalidate( //
            invalidationData.getAllChangedObjects(), //
            invalidationData.getAllDetachedObjects(), //
            deltas, //
            revisionDeltas, //
            detachedObjects, //
            oldRevisions);

        handleConflicts(lastUpdateTime, conflicts, deltas);

        sendInvalidationNotifications(revisionDeltas.keySet(), detachedObjects);
        fireInvalidationEvent(lastUpdateTime, Collections.unmodifiableMap(revisionDeltas), Collections.unmodifiableSet(detachedObjects));

        // Then send the notifications. The deltas could have been modified by the conflict resolvers.
        if (!deltas.isEmpty() || !detachedObjects.isEmpty())
        {
          sendDeltaNotifications(deltas, detachedObjects, oldRevisions);
        }

        fireAdaptersNotifiedEvent(lastUpdateTime);

        CDOLockChangeInfo lockChangeInfo = invalidationData.getLockChangeInfo();
        if (lockChangeInfo != null)
        {
          List<CDOLockDelta> lockDeltas = Arrays.asList(lockChangeInfo.getLockDeltas());
          List<CDOLockState> lockStates = Arrays.asList(lockChangeInfo.getLockStates());
          updateLockStates(lockDeltas, lockStates);
          fireLocksChangedEvent(null, lockChangeInfo);
        }
      }
    }
    catch (RuntimeException ex)
    {
      if (isActive())
      {
        fireEvent(new ThrowableEvent(this, ex));
        throw ex;
      }
    }
    finally
    {
      setLastUpdateTime(lastUpdateTime);
    }
  }

  public ViewInvalidator getInvalidator()
  {
    return invalidator;
  }

  @Override
  public boolean isInvalidating()
  {
    return invalidating;
  }

  private void sendInvalidationNotifications(Set<CDOObject> dirtyObjects, Set<CDOObject> detachedObjects)
  {
    if (options().isInvalidationNotificationEnabled())
    {
      for (CDOObject dirtyObject : dirtyObjects)
      {
        if (((InternalCDOObject)dirtyObject).eNotificationRequired())
        {
          CDOInvalidationNotificationImpl notification = new CDOInvalidationNotificationImpl(dirtyObject);
          dirtyObject.eNotify(notification);
        }
      }

      for (CDOObject detachedObject : detachedObjects)
      {
        if (((InternalCDOObject)detachedObject).eNotificationRequired())
        {
          CDOInvalidationNotificationImpl notification = new CDOInvalidationNotificationImpl(detachedObject);
          detachedObject.eNotify(notification);
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  private void fireInvalidationEvent(long timeStamp, Map<CDOObject, CDORevisionDelta> revisionDeltas, Set<CDOObject> detachedObjects)
  {
    if (!revisionDeltas.isEmpty() || !detachedObjects.isEmpty())
    {
      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new ViewInvalidationEvent(timeStamp, revisionDeltas, detachedObjects), listeners);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void sendDeltaNotifications(Collection<CDORevisionDelta> deltas, Set<CDOObject> detachedObjects, Map<CDOID, InternalCDORevision> oldRevisions)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (deltas != null)
        {
          CDONotificationBuilder builder = new CDONotificationBuilder(this);
          Map<CDOID, InternalCDOObject> objects = getModifiableObjects();
          for (CDORevisionDelta delta : deltas)
          {
            CDOID id = delta.getID();
            InternalCDOObject object = objects.get(id);
            if (object != null && object.eNotificationRequired())
            {
              // if (!isLocked(object))
              {
                InternalCDORevision oldRevision = null;
                if (oldRevisions != null)
                {
                  oldRevision = oldRevisions.get(id);
                }

                NotificationChain notification = builder.buildNotification(object, oldRevision, delta, detachedObjects);
                if (notification != null)
                {
                  notification.dispatch();
                }
              }
            }
          }
        }

        if (detachedObjects != null && !detachedObjects.isEmpty())
        {
          if (options().isDetachmentNotificationEnabled())
          {
            for (CDOObject detachedObject : detachedObjects)
            {
              InternalCDOObject object = (InternalCDOObject)detachedObject;
              if (object.eNotificationRequired())
              {
                // if (!isLocked(object))
                {
                  new CDODeltaNotificationImpl(object, CDONotification.DETACH_OBJECT, null, null, null).dispatch();
                }
              }
            }
          }

          changeSubscriptionManager.handleDetachedObjects(detachedObjects);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * TODO For this method to be useable locks must be cached locally!
   */
  @SuppressWarnings("unused")
  private boolean isLocked(InternalCDOObject object)
  {
    if (object.cdoWriteLock().isLocked())
    {
      return true;
    }

    if (object.cdoReadLock().isLocked())
    {
      return true;
    }

    return false;
  }

  /**
   * @since 2.0
   */
  protected final AdapterManager getAdapterManager()
  {
    return adapterManager;
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (!FSMUtil.isNew(eObject))
        {
          subscribe(eObject, adapter);
        }

        adapterManager.attachAdapter(eObject, adapter);
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (!FSMUtil.isNew(eObject))
        {
          unsubscribe(eObject, adapter);
        }

        adapterManager.detachAdapter(eObject, adapter);
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void subscribe(EObject eObject, Adapter adapter)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (changeSubscriptionManager != null)
        {
          changeSubscriptionManager.subscribe(eObject, adapter);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void unsubscribe(EObject eObject, Adapter adapter)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (changeSubscriptionManager != null)
        {
          changeSubscriptionManager.unsubscribe(eObject, adapter);
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean hasSubscription(CDOID id)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (changeSubscriptionManager != null)
        {
          return changeSubscriptionManager.getSubcribeObject(id) != null;
        }

        return false;
      }
      finally
      {
        unlockView();
      }
    }
  }

  /**
   * @since 2.0
   */
  protected final ChangeSubscriptionManager getChangeSubscriptionManager()
  {
    return changeSubscriptionManager;
  }

  @Override
  protected void listenerAdded(IListener listener)
  {
    if (listener instanceof CDOCommitInfoHandler)
    {
      CDOCommitInfoHandler handler = (CDOCommitInfoHandler)listener;
      commitInfoDistributor.register(handler);
    }
  }

  @Override
  protected void listenerRemoved(IListener listener)
  {
    if (listener instanceof CDOCommitInfoHandler)
    {
      CDOCommitInfoHandler handler = (CDOCommitInfoHandler)listener;
      commitInfoDistributor.deregister(handler);
    }
  }

  /**
   * @since 2.0
   */
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(session, "session"); //$NON-NLS-1$
    checkState(viewID > 0, "viewID"); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    lockOwner = CDOLockUtil.createLockOwner(this);
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();

    if (durableLockingID != null)
    {
      durableLockProcessor = createDurableLockProcessor();
      CDOBranchPoint branchPoint = sessionProtocol.openView(viewID, isReadOnly(), durableLockingID, durableLockProcessor);
      basicSetBranchPoint(branchPoint);
    }
    else
    {
      sessionProtocol.openView(viewID, isReadOnly(), this);
    }

    Runnable runnable = SessionUtil.getTestDelayInViewActivation();
    if (runnable != null)
    {
      runnable.run();
    }

    if (viewLock != null && Boolean.getBoolean("org.eclipse.emf.cdo.sync.tester"))
    {
      new SyncTester().start();
    }

    unitManager.activate();
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    if (durableLockProcessor != null)
    {
      durableLockProcessor.run();
      durableLockProcessor = null;
    }

    ExecutorService executorService = getExecutorService();
    invalidator.setDelegate(executorService);

    try
    {
      LifecycleUtil.activate(invalidator);
    }
    catch (LifecycleException ex)
    {
      // Don't pollute the log if the worker thread is interrupted due to asynchronous view.close()
      if (!(ex.getCause() instanceof InterruptedException))
      {
        throw ex;
      }
    }

    CDOViewRegistryImpl.INSTANCE.register(this);
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    closing = true;
    CDOViewRegistryImpl.INSTANCE.deregister(this);

    // Detach the view set from the view.
    InternalCDOViewSet viewSet = getViewSet();
    viewSet.remove(this);

    CDOAdapterPolicy clearAdapterPolicy = options.getClearAdapterPolicy();
    if (clearAdapterPolicy == null)
    {
      clearAdapterPolicy = viewSet.getDefaultClearAdapterPolicy();
    }

    if (clearAdapterPolicy != null && clearAdapterPolicy != CDOAdapterPolicy.NONE)
    {
      clearAdapters(clearAdapterPolicy);
    }

    super.doBeforeDeactivate();
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    unitManager.deactivate();
    commitInfoDistributor.deactivate();

    LifecycleUtil.deactivate(invalidator, OMLogger.Level.WARN);

    try
    {
      if (!inverseClosing)
      {
        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        if (LifecycleUtil.isActive(sessionProtocol))
        {
          sessionProtocol.closeView(viewID);
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      List<CDOLockDelta> lockDeltas = null;
      List<CDOLockState> lockStates = new ArrayList<>();

      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (session.isActive())
          {
            CDOBranch branch = getBranch();
            CDOLockStateCache lockStateCache = session.getLockStateCache();
            lockDeltas = lockStateCache.removeOwner(branch, lockOwner, lockStates::add);
          }
        }
        finally
        {
          unlockView();
        }

        if (!ObjectUtil.isEmpty(lockDeltas))
        {
          long timeStamp = session.getLastUpdateTime();
          notifyLockChanges(timeStamp, lockDeltas, lockStates);
        }
      }
    }

    try
    {
      session.viewDetached(this);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    lockOwner = null;
    changeSubscriptionManager = null;
    super.doDeactivate();
  }

  private void clearAdapters(CDOAdapterPolicy adapterPolicy)
  {
    try
    {
      for (CDOObject object : getModifiableObjects().values())
      {
        EList<Adapter> adapters = object.eAdapters();
        if (!adapters.isEmpty())
        {
          for (Iterator<Adapter> it = adapters.iterator(); it.hasNext();)
          {
            Adapter adapter = it.next();
            if (adapter instanceof CDOObjectWrapperBase)
            {
              // Don't remove a legacy adapter because otherwise references to this CDOObject will break.
              continue;
            }

            boolean validAdapter;

            try
            {
              validAdapter = adapterPolicy.isValid(object, adapter);
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
              continue;
            }

            if (validAdapter)
            {
              try
              {
                it.remove();
              }
              catch (Exception ex)
              {
                OM.LOG.error(ex);
              }
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  public void inverseClose()
  {
    inverseClosing = true;

    try
    {
      close();
    }
    finally
    {
      inverseClosing = false;
    }
  }

  @Override
  public boolean isClosing()
  {
    return closing;
  }

  @Override
  public long getLastUpdateTime()
  {
    synchronized (getViewMonitor())
    {
      lockView(); // TODO ???

      try
      {
        return lastUpdateTime;
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public void setLastUpdateTime(long lastUpdateTime)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (this.lastUpdateTime < lastUpdateTime)
        {
          this.lastUpdateTime = lastUpdateTime;
        }

        if (viewLockCondition != null)
        {
          viewLockCondition.signalAll();
        }
        else
        {
          notifyAll();
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    long end = timeoutMillis == NO_TIMEOUT ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMillis;
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        for (;;)
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
            long waitMillis = end - now;

            if (viewLockCondition != null)
            {
              viewLockCondition.await(waitMillis, TimeUnit.MILLISECONDS);
            }
            else
            {
              wait(waitMillis);
            }
          }
          catch (InterruptedException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  @Override
  public boolean runAfterUpdate(final long updateTime, final Runnable runnable)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        long lastUpdateTime = getLastUpdateTime();
        if (lastUpdateTime < updateTime)
        {
          addListener(new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              if (event instanceof CDOViewInvalidationEvent)
              {
                CDOViewInvalidationEvent e = (CDOViewInvalidationEvent)event;
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
      finally
      {
        unlockView();
      }
    }

    runnable.run();
    return true;
  }

  @Override
  public void resourceLoaded(CDOResourceImpl resource, boolean loaded)
  {
    if (session.getRepositoryInfo().isSupportingUnits())
    {
      unitManager.resourceLoaded(resource, loaded);
    }
  }

  @Override
  public final CDOUnitManagerImpl getUnitManager()
  {
    return unitManager;
  }

  protected final Object getLockTarget(CDOID id)
  {
    return session.getLockStateCache().createKey(getBranch(), id);
  }

  public final DurableLockProcessor createDurableLockProcessor()
  {
    return new DurableLockProcessor();
  }

  @Override
  @Deprecated
  public void updateLockStates(CDOLockState[] newLockStates, boolean loadObjectsOnDemand, Consumer<CDOLockState> consumer)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public String enableDurableLocking(boolean enable)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   */
  @Override
  @Deprecated
  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   */
  @Override
  @Deprecated
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
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
  public boolean isInvalidationRunnerActive()
  {
    throw new UnsupportedOperationException();
  }

  public static Object getLockTarget(CDOView view, CDOID id)
  {
    return ((CDOViewImpl)view).getLockTarget(id);
  }

  public static Object getLockTarget(CDOObject object)
  {
    CDOView view = object.cdoView();
    if (view == null)
    {
      return null;
    }

    CDOID id = object.cdoID();
    return getLockTarget(view, id);
  }

  /**
   * @author Eike Stepper
   */
  public final class CDOUnitManagerImpl extends Container<CDOUnit> implements CDOUnitManager
  {
    private final Map<EObject, CDOUnit> unitPerRoot = new HashMap<>();

    private final Map<EObject, CDOUnit> unitPerObject = new HashMap<>();

    private CDOUnitImpl openingUnit;

    private Set<CDOID> openingIDs;

    private Map<CDOResource, CDOUnit> resourceUnits;

    public CDOUnitManagerImpl()
    {
    }

    @Override
    public CDOView getView()
    {
      return CDOViewImpl.this;
    }

    @Override
    public boolean isUnit(EObject root)
    {
      CDOUnitImpl unit = requestUnit(root, UnitOpcode.CHECK, null);
      return unit != null;
    }

    @Override
    public CDOUnit createUnit(EObject root, boolean open, IProgressMonitor monitor) throws UnitExistsException
    {
      UnitOpcode opcode = open ? UnitOpcode.CREATE_AND_OPEN : UnitOpcode.CREATE;

      CDOUnitImpl unit = requestUnit(root, opcode, monitor);

      if (open)
      {
        if (unit == null)
        {
          throw new UnitExistsException();
        }

        fireElementAddedEvent(unit);
      }

      return unit;
    }

    @Override
    public CDOUnit openUnit(EObject root, boolean createOnDemand, IProgressMonitor monitor) throws UnitNotFoundException
    {
      UnitOpcode opcode = createOnDemand ? UnitOpcode.OPEN_DEMAND_CREATE : UnitOpcode.OPEN;

      CDOUnitImpl unit = requestUnit(root, opcode, monitor);
      if (unit == null)
      {
        throw new UnitNotFoundException();
      }

      fireElementAddedEvent(unit);
      return unit;
    }

    @Override
    public CDOUnit[] getElements()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return unitPerRoot.values().toArray(new CDOUnit[unitPerRoot.size()]);
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public CDOUnit[] getOpenUnits()
    {
      return getElements();
    }

    @Override
    public CDOUnit getOpenUnit(EObject object)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return getOpenUnitUnsynced(object);
        }
        finally
        {
          unlockView();
        }
      }
    }

    public CDOUnit getOpenUnitUnsynced(EObject object)
    {
      CDOUnit unit = unitPerObject.get(object);
      if (unit == null && openingUnit != null)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(object);
        if (cdoObject != null)
        {
          CDOID id = cdoObject.cdoID();
          if (openingIDs.contains(id))
          {
            unit = openingUnit;
          }
        }
      }

      return unit;
    }

    public void addObject(InternalCDOObject object)
    {
      if (!unitPerRoot.isEmpty())
      {
        CDOUnit unit = getOpenUnitUnsynced(object);
        if (unit == null)
        {
          EObject parent = getParent(object);
          EObject rootResource = getRootResource();

          while (parent != null && parent != rootResource)
          {
            unit = getOpenUnitUnsynced(parent);
            if (unit != null)
            {
              unitPerObject.put(object, unit);
              ++((CDOUnitImpl)unit).elements;
              break;
            }

            parent = getParent(parent);
          }
        }
      }
    }

    public void removeObject(InternalCDOObject object)
    {
      if (!unitPerRoot.isEmpty())
      {
        CDOUnit unit = unitPerObject.remove(object);
        if (unit != null)
        {
          if (unit.getRoot() == object)
          {
            unitPerRoot.remove(object);
          }

          --((CDOUnitImpl)unit).elements;
        }
      }
    }

    @Override
    public synchronized boolean isAutoResourceUnitsEnabled()
    {
      return resourceUnits != null;
    }

    @Override
    public synchronized void setAutoResourceUnitsEnabled(boolean enabled)
    {
      if (enabled)
      {
        resourceUnits = new HashMap<>();
      }
      else
      {
        resourceUnits = null;
      }
    }

    public synchronized void resourceLoaded(CDOResourceImpl resource, boolean loaded)
    {
      if (resourceUnits != null)
      {
        if (loaded)
        {
          loadResource(resource);
        }
        else
        {
          unloadResource(resource);
        }
      }
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      unitPerRoot.clear();
      unitPerObject.clear();
      super.doDeactivate();
    }

    private void loadResource(CDOResource resource)
    {
      CDOUnit unit = resourceUnits.get(resource);
      if (unit == null)
      {
        CDOUnitManager unitManager = resource.cdoView().getUnitManager();
        unit = unitManager.openUnit(resource, true, null);
        resourceUnits.put(resource, unit);
      }
    }

    private void unloadResource(CDOResource resource)
    {
      CDOUnit unit = resourceUnits.remove(resource);
      if (unit != null)
      {
        unit.close();
      }
    }

    private EObject getParent(EObject object)
    {
      EObject parent = object.eContainer();
      if (parent == null)
      {
        parent = (EObject)((InternalEObject)object).eDirectResource();
      }

      return parent;
    }

    private CDOObject getCDORoot(EObject root)
    {
      CDOObject cdoRoot = CDOUtil.getCDOObject(root);
      if (cdoRoot == null)
      {
        throw new IllegalArgumentException("Root " + root + " is not managed by CDO");
      }

      CDOView view = cdoRoot.cdoView();
      if (view != CDOViewImpl.this)
      {
        throw new IllegalArgumentException("Root " + root + " is managed by " + view);
      }

      return cdoRoot;
    }

    private CDOUnitImpl requestUnit(EObject root, UnitOpcode opcode, IProgressMonitor monitor)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (opcode.isCreate())
          {
            CDOUnit containingUnit = getOpenUnit(root);
            if (containingUnit != null)
            {
              throw new CDOException("Attempt to nest the new unit " + root + " in the existing unit " + containingUnit);
            }

            for (CDOUnit existingUnit : unitPerRoot.values())
            {
              if (EcoreUtil.isAncestor(root, existingUnit.getRoot()))
              {
                throw new CDOException("Attempt to nest the existing unit " + existingUnit + " in the new unit " + root);
              }
            }
          }

          InternalCDORevisionManager revisionManager = session.getRevisionManager();
          openingUnit = new CDOUnitImpl(root);

          int viewID = getViewID();
          CDOID rootID = getCDORoot(root).cdoID();

          CDORevisionHandler revisionHandler = null;
          MapRevisionProvider revisions = new MapRevisionProvider();

          if (opcode.isOpen())
          {
            openingIDs = new HashSet<>();

            revisionHandler = new CDORevisionHandler()
            {
              @Override
              public boolean handleRevision(CDORevision revision)
              {
                ++openingUnit.elements;
                revision = revisionManager.internRevision(revision);
                CDOID id = revision.getID();
                revisions.put(id, revision);

                changeSubscriptionManager.removeEntry(id);
                openingIDs.add(id);

                return true;
              }
            };
          }

          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          boolean success = sessionProtocol.requestUnit(viewID, rootID, opcode, revisionHandler, EclipseMonitor.safe(monitor));

          if (success)
          {
            if (opcode.isOpen())
            {
              unitPerRoot.put(root, openingUnit);
              unitPerObject.put(root, openingUnit);

              // Bug 511506: Load the unit from the root object. This ensures that all container objects are loaded
              // before their children. Thus children should always be attached to their container and resource.
              CDORevision rootRevision = getRevision(rootID);
              initializeObjectsRecursively(rootRevision, revisions);
            }

            return openingUnit;
          }

          return null;
        }
        finally
        {
          openingUnit = null;
          openingIDs = null;
          unlockView();
        }
      }
    }

    private void initializeObjectsRecursively(CDORevision revision, CDORevisionProvider revisions)
    {
      InternalCDOObject object = getObject(revision.getID());
      unitPerObject.put(object, openingUnit);

      // Recurse.
      CDORevisionUtil.forEachChildRevision(revision, revisions, false, child -> initializeObjectsRecursively(child, revisions));
    }

    private void closeUnit(CDOUnit unit, boolean resubscribe)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          requestUnit(unit.getRoot(), UnitOpcode.CLOSE, null);

          if (resubscribe && !options.hasChangeSubscriptionPolicies())
          {
            resubscribe = false;
          }

          for (Iterator<Map.Entry<EObject, CDOUnit>> it = unitPerObject.entrySet().iterator(); it.hasNext();)
          {
            Map.Entry<EObject, CDOUnit> entry = it.next();
            if (entry.getValue() == unit)
            {
              it.remove(); // Remove the object from its unit first, so that shouldSubscribe() can return true.

              if (resubscribe)
              {
                EObject object = entry.getKey();
                for (Adapter adapter : object.eAdapters())
                {
                  changeSubscriptionManager.subscribe(object, adapter);
                }
              }
            }
          }

          unitPerRoot.remove(unit.getRoot());
        }
        finally
        {
          unlockView();
        }
      }

      fireElementRemovedEvent(unit);
    }

    /**
     * @author Eike Stepper
     */
    public final class CDOUnitImpl implements CDOUnit
    {
      private final EObject root;

      private int elements;

      public CDOUnitImpl(EObject root)
      {
        this.root = root;
      }

      @Override
      public CDOUnitManagerImpl getManager()
      {
        return CDOUnitManagerImpl.this;
      }

      @Override
      public EObject getRoot()
      {
        return root;
      }

      @Override
      public int getElements()
      {
        return elements;
      }

      @Override
      public void close()
      {
        close(true);
      }

      @Override
      public void close(boolean resubscribe)
      {
        closeUnit(this, resubscribe);
      }

      @Override
      public String toString()
      {
        return "CDOUnit[" + root + "]";
      }
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  protected final class AdapterManager
  {
    private Set<CDOObject> objects = new HashBag<>();

    public AdapterManager()
    {
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      if (options().getStrongReferencePolicy() != CDOAdapterPolicy.NONE)
      {
        for (CDOObject object : commitContext.getNewObjects().values())
        {
          attachObject(object);
        }

        for (CDOObject object : commitContext.getDetachedObjects().values())
        {
          detachObject(object);
        }
      }
    }

    private void attachObject(CDOObject object)
    {
      if (((InternalEObject)object).eNotificationRequired())
      {
        CDOAdapterPolicy strongReferencePolicy = options().getStrongReferencePolicy();
        int count = 0;
        for (Adapter adapter : object.eAdapters())
        {
          if (strongReferencePolicy.isValid(object, adapter))
          {
            count++;
          }
        }

        for (int i = 0; i < count; i++)
        {
          objects.add(object);
        }
      }
    }

    private void detachObject(CDOObject object)
    {
      while (objects.remove(object))
      {
        // Do nothing
      }
    }

    private void attachAdapter(CDOObject object, Adapter adapter)
    {
      if (options().getStrongReferencePolicy().isValid(object, adapter))
      {
        objects.add(object);
      }
    }

    private void detachAdapter(CDOObject object, Adapter adapter)
    {
      if (options().getStrongReferencePolicy().isValid(object, adapter))
      {
        objects.remove(object);
      }
    }

    private void reset()
    {
      // Keep the objects in memory
      Set<CDOObject> oldObjects = objects;
      objects = new HashBag<>();
      if (options().getStrongReferencePolicy() != CDOAdapterPolicy.NONE)
      {
        for (InternalCDOObject object : getObjectsList())
        {
          attachObject(object);
        }
      }

      oldObjects.clear();
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  protected final class ChangeSubscriptionManager
  {
    private Map<CDOID, SubscribeEntry> subscriptions = CDOIDUtil.createMap();

    public ChangeSubscriptionManager()
    {
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      handleNewObjects(commitContext.getNewObjects().values());
      handleDetachedObjects(commitContext.getDetachedObjects().values());
    }

    private boolean hasSubscriptions()
    {
      return !subscriptions.isEmpty();
    }

    private void subscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, 1);
    }

    private void unsubscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, -1);
    }

    /**
     * Register to the server all objects from the active list
     */
    private void handleChangeSubcriptionPoliciesChanged()
    {
      boolean policiesPresent = options().hasChangeSubscriptionPolicies();
      subscriptions.clear();

      List<CDOID> ids = new ArrayList<>();
      if (policiesPresent)
      {
        for (InternalCDOObject object : getObjectsList())
        {
          int count = getNumberOfValidAdapters(object);
          if (count > 0)
          {
            ids.add(object.cdoID());
            addEntry(object.cdoID(), object, count);
          }
        }
      }

      request(ids, true, true);
    }

    private void handleDetachedObjects(Collection<CDOObject> detachedObjects)
    {
      for (CDOObject detachedObject : detachedObjects)
      {
        CDOID id = detachedObject.cdoID();
        SubscribeEntry entry = subscriptions.get(id);
        if (entry != null)
        {
          detachObject(id);
        }
      }
    }

    private void handleNewObjects(Collection<? extends CDOObject> newObjects)
    {
      for (CDOObject object : newObjects)
      {
        InternalCDOObject internalObject = (InternalCDOObject)object;
        if (internalObject != null)
        {
          int count = getNumberOfValidAdapters(internalObject);
          if (count > 0)
          {
            subscribe(internalObject.cdoID(), internalObject, count);
          }
        }
      }
    }

    private InternalCDOObject getSubcribeObject(CDOID id)
    {
      SubscribeEntry entry = subscriptions.get(id);
      if (entry != null)
      {
        return entry.getObject();
      }

      return null;
    }

    private void request(List<CDOID> ids, boolean clear, boolean subscribeMode)
    {
      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      sessionProtocol.changeSubscription(getViewID(), ids, subscribeMode, clear);
    }

    private int getNumberOfValidAdapters(InternalCDOObject object)
    {
      int count = 0;
      if (!FSMUtil.isTransient(object) && !FSMUtil.isNew(object))
      {
        if (object.eNotificationRequired())
        {
          EObject instance = CDOUtil.getEObject(object);
          for (Adapter adapter : instance.eAdapters())
          {
            if (shouldSubscribe(object, adapter))
            {
              ++count;
            }
          }
        }
      }

      return count;
    }

    private void subscribe(EObject eObject, Adapter adapter, int adjust)
    {
      if (shouldSubscribe(eObject, adapter))
      {
        CDOView view = CDOViewImpl.this;
        InternalCDOObject internalCDOObject = FSMUtil.adapt(eObject, view);
        if (internalCDOObject.cdoView() != view)
        {
          throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.27"), internalCDOObject)); //$NON-NLS-1$
        }

        subscribe(internalCDOObject.cdoID(), internalCDOObject, adjust);
      }
    }

    private boolean shouldSubscribe(EObject eObject, Adapter adapter)
    {
      if (adapter instanceof CDOLocalAdapter)
      {
        return false;
      }

      if (unitManager.getOpenUnitUnsynced(eObject) != null)
      {
        return false;
      }

      for (CDOAdapterPolicy policy : options().getChangeSubscriptionPolicies())
      {
        if (policy.isValid(eObject, adapter))
        {
          return true;
        }
      }

      return false;
    }

    private void subscribe(CDOID id, InternalCDOObject cdoObject, int adjust)
    {
      boolean policiesPresent = options().hasChangeSubscriptionPolicies();

      int count = 0;
      SubscribeEntry entry = subscriptions.get(id);
      if (entry == null)
      {
        // Cannot adjust negative value
        if (adjust < 0)
        {
          return;
        }

        // Notification need to be enable to send correct value to the server
        if (policiesPresent)
        {
          request(Collections.singletonList(id), false, true);
        }
      }
      else
      {
        count = entry.getCount();
      }

      count += adjust;

      // Look if objects need to be unsubscribe
      if (count <= 0)
      {
        removeEntry(id);

        // Notification need to be enable to send correct value to the server
        if (policiesPresent)
        {
          request(Collections.singletonList(id), false, false);
        }
      }
      else
      {
        if (entry == null)
        {
          addEntry(id, cdoObject, count);
        }
        else
        {
          entry.setCount(count);
        }
      }
    }

    private void detachObject(CDOID id)
    {
      subscribe(id, null, Integer.MIN_VALUE);
    }

    private void addEntry(CDOID id, InternalCDOObject object, int count)
    {
      subscriptions.put(id, new SubscribeEntry(object, count));
    }

    private void removeEntry(CDOID id)
    {
      subscriptions.remove(id);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SubscribeEntry
  {
    private final InternalCDOObject object;

    private int count;

    public SubscribeEntry(InternalCDOObject object, int count)
    {
      this.object = object;
      this.count = count;
    }

    public InternalCDOObject getObject()
    {
      return object;
    }

    public int getCount()
    {
      return count;
    }

    public void setCount(int count)
    {
      this.count = count;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ViewInvalidator extends SerializingExecutor
  {
  }

  /**
   * @author Eike Stepper
   */
  private final class ViewInvalidation extends RunnableWithName
  {
    private final ViewInvalidationData invalidationData;

    public ViewInvalidation(ViewInvalidationData invalidationData)
    {
      this.invalidationData = invalidationData;
    }

    @Override
    public String getName()
    {
      return "Invalidator-" + CDOViewImpl.this; //$NON-NLS-1$
    }

    @Override
    protected void doRun()
    {
      try
      {
        invalidating = true;
        doInvalidate(invalidationData);
      }
      catch (Exception ex)
      {
        if (isActive())
        {
          OM.LOG.error(ex);
        }
      }
      finally
      {
        invalidating = false;
      }
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class ViewInvalidationEvent extends Event implements CDOViewInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private long timeStamp;

    private Map<CDOObject, CDORevisionDelta> revisionDeltas;

    private Set<CDOObject> detachedObjects;

    public ViewInvalidationEvent(long timeStamp, Map<CDOObject, CDORevisionDelta> revisionDeltas, Set<CDOObject> detachedObjects)
    {
      this.timeStamp = timeStamp;
      this.revisionDeltas = revisionDeltas;
      this.detachedObjects = detachedObjects;
    }

    @Override
    public long getTimeStamp()
    {
      return timeStamp;
    }

    @Override
    public Set<CDOObject> getDirtyObjects()
    {
      return revisionDeltas.keySet();
    }

    @Override
    public Map<CDOObject, CDORevisionDelta> getRevisionDeltas()
    {
      return revisionDeltas;
    }

    @Override
    public Set<CDOObject> getDetachedObjects()
    {
      return detachedObjects;
    }

    @Override
    protected String formatEventName()
    {
      return "CDOViewInvalidationEvent";
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "timeStamp=" + timeStamp + ", revisionDeltas=" + revisionDeltas + ", detachedObjects=" + detachedObjects + "]";
    }
  }

  /**
   * @author Caspar De Groot
   * @since 4.1
   */
  private final class ViewLocksChangedEvent extends AbstractLocksChangedEvent implements CDOViewLocksChangedEvent
  {
    private static final long serialVersionUID = 1L;

    public ViewLocksChangedEvent(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
    {
      super(CDOViewImpl.this, sender, lockChangeInfo);
    }

    @Override
    public InternalCDOView getSource()
    {
      return (InternalCDOView)super.getSource();
    }

    @Override
    public EObject[] getAffectedObjects()
    {
      List<EObject> objects = new ArrayList<>();
      CDOView view = getSource();

      for (CDOLockDelta delta : getLockDeltas())
      {
        Object lockedObject = delta.getTarget();

        CDOID id = null;
        if (lockedObject instanceof CDOIDAndBranch)
        {
          CDOIDAndBranch idAndBranch = (CDOIDAndBranch)lockedObject;
          if (idAndBranch.getBranch().getID() == view.getBranch().getID())
          {
            id = idAndBranch.getID();
          }
        }
        else if (lockedObject instanceof CDOID)
        {
          id = (CDOID)lockedObject;
        }

        if (id != null)
        {
          CDOObject object = view.getObject(id, false);
          if (object != null)
          {
            objects.add(CDOUtil.getEObject(object));
          }
        }
      }

      return objects.toArray(new EObject[objects.size()]);
    }

    @Override
    protected InternalCDOSession getSession()
    {
      return getSource().getSession();
    }

    @Override
    protected String formatEventName()
    {
      return "CDOViewLocksChangedEvent";
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DurabilityChangedEvent extends Event implements CDOViewDurabilityChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final String oldID;

    private final String newID;

    public DurabilityChangedEvent(String oldID, String newID)
    {
      this.oldID = oldID;
      this.newID = newID;
    }

    @Override
    public String getOldDurableLockingID()
    {
      return oldID;
    }

    @Override
    public String getNewDurableLockingID()
    {
      return newID;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CommitInfoDistributor implements IListener, IDeactivateable
  {
    private final ConcurrentArray<CDOCommitInfoHandler> handlers = new ConcurrentArray<CDOCommitInfoHandler>()
    {
      @Override
      protected CDOCommitInfoHandler[] newArray(int length)
      {
        return new CDOCommitInfoHandler[length];
      }

      @Override
      protected void firstElementAdded()
      {
        getSession().addListener(CommitInfoDistributor.this);
      }

      @Override
      protected void lastElementRemoved()
      {
        getSession().removeListener(CommitInfoDistributor.this);
        deactivate();
      }
    };

    private final Map<Long, CDOCommitInfo> commitInfos = new ConcurrentHashMap<>();

    public CommitInfoDistributor()
    {
    }

    @Override
    public Exception deactivate()
    {
      commitInfos.clear();
      return null;
    }

    public void register(CDOCommitInfoHandler handler)
    {
      CheckUtil.checkArg(handler, "handler"); //$NON-NLS-1$
      handlers.add(handler);
    }

    public void deregister(CDOCommitInfoHandler handler)
    {
      CheckUtil.checkArg(handler, "handler"); //$NON-NLS-1$
      handlers.remove(handler);
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
        commitInfos.put(e.getTimeStamp(), e);
      }
    }

    public void error(long timeStamp, Throwable t)
    {
      commitInfos.remove(timeStamp);
    }

    public void distribute(long timeStamp)
    {
      CDOCommitInfo commitInfo = commitInfos.remove(timeStamp);
      if (commitInfo != null)
      {
        distributeSafe(commitInfo, handlers.get());
      }
    }

    private void distributeSafe(CDOCommitInfo commitInfo, CDOCommitInfoHandler[] handlers)
    {
      if (handlers != null)
      {
        ObjectUtil.forEachSafe(handlers, handler -> handler.handleCommitInfo(commitInfo));
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SyncTester extends Thread
  {
    private final CountDownLatch latch = new CountDownLatch(1);

    public SyncTester()
    {
      super(CDOViewImpl.this + "-sync-tester");
      setDaemon(true);
    }

    @Override
    public void run()
    {
      addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onAboutToDeactivate(ILifecycle lifecycle)
        {
          latch.countDown();
        }
      });

      synchronized (CDOViewImpl.this)
      {
        try
        {
          latch.await();
        }
        catch (InterruptedException ex)
        {
          return;
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class DurableLockProcessor implements BiConsumer<CDOID, LockGrade>, Runnable
  {
    private final Map<CDOID, LockGrade> lockGrades = CDOIDUtil.createMap();

    private DurableLockProcessor()
    {
    }

    @Override
    public void accept(CDOID id, LockGrade lockGrade)
    {
      lockGrades.put(id, lockGrade);
    }

    @Override
    public void run()
    {
      List<CDOLockDelta> lockDeltas = new ArrayList<>();
      List<CDOLockState> lockStates = new ArrayList<>();

      for (Map.Entry<CDOID, LockGrade> entry : lockGrades.entrySet())
      {
        CDOID id = entry.getKey();
        LockGrade lockGrade = entry.getValue();

        Object target = getLockTarget(id);
        InternalCDOLockState lockState = (InternalCDOLockState)CDOLockUtil.createLockState(target);

        lockGrade.forEachLockType(lockType -> {
          lockDeltas.add(CDOLockUtil.createLockDelta(target, lockType, null, lockOwner));
          lockState.addOwner(lockOwner, lockType);
        });

        lockStates.add(lockState);
      }

      if (!lockDeltas.isEmpty())
      {
        CDOBranch branch = getBranch();
        CDOLockStateCache lockStateCache = session.getLockStateCache();
        lockStateCache.updateLockStates(branch, lockDeltas, lockStates, null);
      }
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public class OptionsImpl extends Notifier implements Options
  {
    private boolean loadNotificationEnabled;

    private boolean detachmentNotificationEnabled;

    private boolean invalidationNotificationEnabled;

    private CDOInvalidationPolicy invalidationPolicy = CDOInvalidationPolicy.DEFAULT;

    private boolean lockNotificationsEnabled;

    /**
     * See bug 568778.
     */
    @Deprecated
    private CDOLockStatePrefetcher lockStatePrefetcher;

    private CDORevisionPrefetchingPolicy revisionPrefetchingPolicy = CDOUtil.createRevisionPrefetchingPolicy(NO_REVISION_PREFETCHING);

    private CDOFeatureAnalyzer featureAnalyzer = CDOFeatureAnalyzer.NOOP;

    private CDOStaleReferencePolicy staleReferencePolicy = CDOStaleReferencePolicy.DEFAULT;

    private HashBag<CDOAdapterPolicy> changeSubscriptionPolicies = new HashBag<>();

    private CDOAdapterPolicy strongReferencePolicy = CDOAdapterPolicy.ALL;

    private CDOAdapterPolicy clearAdapterPolicy;

    public OptionsImpl()
    {
    }

    public void recoverView()
    {
      if (lockNotificationsEnabled)
      {
        CDOSessionProtocol protocol = session.getSessionProtocol();
        protocol.enableLockNotifications(viewID, true);
      }

      if (changeSubscriptionManager.hasSubscriptions())
      {
        changeSubscriptionManager.handleChangeSubcriptionPoliciesChanged();
      }

      // TODO unitManager.recoverView();
    }

    @Override
    public CDOViewImpl getContainer()
    {
      return CDOViewImpl.this;
    }

    @Override
    public boolean isLoadNotificationEnabled()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return loadNotificationEnabled;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setLoadNotificationEnabled(boolean enabled)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (loadNotificationEnabled != enabled)
          {
            loadNotificationEnabled = enabled;
            event = new LoadNotificationEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public boolean isDetachmentNotificationEnabled()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return detachmentNotificationEnabled;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setDetachmentNotificationEnabled(boolean enabled)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (detachmentNotificationEnabled != enabled)
          {
            detachmentNotificationEnabled = enabled;
            event = new DetachmentNotificationEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public boolean isInvalidationNotificationEnabled()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return invalidationNotificationEnabled;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setInvalidationNotificationEnabled(boolean enabled)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (invalidationNotificationEnabled != enabled)
          {
            invalidationNotificationEnabled = enabled;
            event = new InvalidationNotificationEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public CDOInvalidationPolicy getInvalidationPolicy()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return invalidationPolicy;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setInvalidationPolicy(CDOInvalidationPolicy policy)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (invalidationPolicy != policy)
          {
            invalidationPolicy = policy;
            event = new InvalidationPolicyEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public boolean isLockNotificationEnabled()
    {
      return lockNotificationsEnabled;
    }

    @Override
    public void setLockNotificationEnabled(boolean enabled)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (enabled != lockNotificationsEnabled)
          {
            CDOSessionProtocol protocol = session.getSessionProtocol();
            protocol.enableLockNotifications(viewID, enabled);

            lockNotificationsEnabled = enabled;
            event = new LockNotificationEventImpl(enabled);
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    /**
     * @deprecated As of 4.12 use {@link CDOLockStatePrefetcher}, see bug 568778.
     */
    @Deprecated
    public boolean isLockStatePrefetchEnabled()
    {
      return lockStatePrefetcher != null;
    }

    /**
     * @deprecated As of 4.12 use {@link CDOLockStatePrefetcher}, see bug 568778.
     */
    @Deprecated
    public void setLockStatePrefetchEnabled(boolean enabled)
    {
      checkActive();

      if (enabled)
      {
        if (lockStatePrefetcher == null)
        {
          lockStatePrefetcher = new CDOLockStatePrefetcher(CDOViewImpl.this, false);
        }
      }
      else
      {
        if (lockStatePrefetcher != null)
        {
          lockStatePrefetcher.dispose();
          lockStatePrefetcher = null;
        }
      }
    }

    /**
     * @deprecated As of 4.12 use {@link CDOLockStatePrefetcher#getObjectFilter()}, see bug 568778.
     */
    @Deprecated
    public CDOLockStateLoadingPolicy getLockStateLoadingPolicy()
    {
      return new CDOLockStateLoadingPolicy()
      {
        @Override
        public boolean loadLockState(CDOID id)
        {
          Predicate<CDOID> filter = lockStatePrefetcher == null ? CDOLockStatePrefetcher.DEFAULT_OBJECT_FILTER : lockStatePrefetcher.getObjectFilter();
          return filter.test(id);
        }
      };
    }

    /**
     * @deprecated As of 4.12 use {@link CDOLockStatePrefetcher#setObjectFilter(java.util.function.Predicate)}, see bug 568778.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void setLockStateLoadingPolicy(CDOLockStateLoadingPolicy lockStateLoadingPolicy)
    {
      checkActive();
      if (lockStatePrefetcher == null)
      {
        lockStatePrefetcher = new CDOLockStatePrefetcher(CDOViewImpl.this, false);
      }

      lockStatePrefetcher.setObjectFilter(id -> lockStateLoadingPolicy.loadLockState(id));
    }

    public boolean hasChangeSubscriptionPolicies()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return !changeSubscriptionPolicies.isEmpty();
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public CDOAdapterPolicy[] getChangeSubscriptionPolicies()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return changeSubscriptionPolicies.toArray(new CDOAdapterPolicy[changeSubscriptionPolicies.size()]);
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (changeSubscriptionPolicies.add(policy))
          {
            changeSubscriptionManager.handleChangeSubcriptionPoliciesChanged();
            event = new ChangeSubscriptionPoliciesEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (changeSubscriptionPolicies.remove(policy) && !changeSubscriptionPolicies.contains(policy))
          {
            changeSubscriptionManager.handleChangeSubcriptionPoliciesChanged();
            event = new ChangeSubscriptionPoliciesEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public CDOAdapterPolicy getStrongReferencePolicy()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return strongReferencePolicy;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setStrongReferencePolicy(CDOAdapterPolicy adapterPolicy)
    {
      checkActive();

      if (adapterPolicy == null)
      {
        adapterPolicy = CDOAdapterPolicy.ALL;
      }

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (strongReferencePolicy != adapterPolicy)
          {
            strongReferencePolicy = adapterPolicy;
            adapterManager.reset();
            event = new ReferencePolicyEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return revisionPrefetchingPolicy;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy)
    {
      checkActive();

      if (prefetchingPolicy == null)
      {
        prefetchingPolicy = CDORevisionPrefetchingPolicy.NO_PREFETCHING;
      }

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (revisionPrefetchingPolicy != prefetchingPolicy)
          {
            revisionPrefetchingPolicy = prefetchingPolicy;
            event = new RevisionPrefetchingPolicyEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public CDOFeatureAnalyzer getFeatureAnalyzer()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return featureAnalyzer;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
    {
      checkActive();

      if (featureAnalyzer == null)
      {
        featureAnalyzer = CDOFeatureAnalyzer.NOOP;
      }

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (this.featureAnalyzer != featureAnalyzer)
          {
            this.featureAnalyzer = featureAnalyzer;
            event = new FeatureAnalyzerEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    @Deprecated
    public CDOStaleReferencePolicy getStaleReferenceBehaviour()
    {
      return getStaleReferencePolicy();
    }

    @Override
    @Deprecated
    public void setStaleReferenceBehaviour(CDOStaleReferencePolicy policy)
    {
      setStaleReferencePolicy(policy);
    }

    @Override
    public CDOStaleReferencePolicy getStaleReferencePolicy()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return staleReferencePolicy;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setStaleReferencePolicy(CDOStaleReferencePolicy policy)
    {
      checkActive();

      if (policy == null)
      {
        policy = CDOStaleReferencePolicy.DEFAULT;
      }

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (staleReferencePolicy != policy)
          {
            staleReferencePolicy = policy;
            event = new StaleReferencePolicyEventImpl();
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    @Override
    public ReferenceType getCacheReferenceType()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          Map<CDOID, InternalCDOObject> objects = getModifiableObjects();
          if (objects instanceof ReferenceValueMap.Strong<?, ?>)
          {
            return ReferenceType.STRONG;
          }

          if (objects instanceof ReferenceValueMap.Soft<?, ?>)
          {
            return ReferenceType.SOFT;
          }

          if (objects instanceof ReferenceValueMap.Weak<?, ?>)
          {
            return ReferenceType.WEAK;
          }

          throw new IllegalStateException(Messages.getString("CDOViewImpl.29")); //$NON-NLS-1$
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public boolean setCacheReferenceType(ReferenceType referenceType)
    {
      checkActive();

      if (referenceType == null)
      {
        referenceType = ReferenceType.SOFT;
      }

      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (!initObjectsMap(referenceType))
          {
            return false;
          }
        }
        finally
        {
          unlockView();
        }
      }

      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new CacheReferenceTypeEventImpl(), listeners);
      }

      return true;
    }

    @Override
    public CDOAdapterPolicy getClearAdapterPolicy()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return clearAdapterPolicy;
        }
        finally
        {
          unlockView();
        }
      }
    }

    @Override
    public void setClearAdapterPolicy(CDOAdapterPolicy policy)
    {
      checkActive();

      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          clearAdapterPolicy = policy;
        }
        finally
        {
          unlockView();
        }
      }

      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new ClearAdapterPolicyEventImpl(), listeners);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class CacheReferenceTypeEventImpl extends OptionsEvent implements CacheReferenceTypeEvent
    {
      private static final long serialVersionUID = 1L;

      public CacheReferenceTypeEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ChangeSubscriptionPoliciesEventImpl extends OptionsEvent implements ChangeSubscriptionPoliciesEvent
    {
      private static final long serialVersionUID = 1L;

      public ChangeSubscriptionPoliciesEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class LoadNotificationEventImpl extends OptionsEvent implements LoadNotificationEvent
    {
      private static final long serialVersionUID = 1L;

      public LoadNotificationEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class DetachmentNotificationEventImpl extends OptionsEvent implements DetachmentNotificationEvent
    {
      private static final long serialVersionUID = 1L;

      public DetachmentNotificationEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class InvalidationNotificationEventImpl extends OptionsEvent implements InvalidationNotificationEvent
    {
      private static final long serialVersionUID = 1L;

      public InvalidationNotificationEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class InvalidationPolicyEventImpl extends OptionsEvent implements InvalidationPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public InvalidationPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Caspar De Groot
     */
    private final class LockNotificationEventImpl extends OptionsEvent implements LockNotificationEvent
    {
      private static final long serialVersionUID = 1L;

      private boolean enabled;

      public LockNotificationEventImpl(boolean enabled)
      {
        super(OptionsImpl.this);
        this.enabled = enabled;
      }

      @Override
      public boolean getEnabled()
      {
        return enabled;
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class RevisionPrefetchingPolicyEventImpl extends OptionsEvent implements RevisionPrefetchingPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public RevisionPrefetchingPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class FeatureAnalyzerEventImpl extends OptionsEvent implements FeatureAnalyzerEvent
    {
      private static final long serialVersionUID = 1L;

      public FeatureAnalyzerEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    @SuppressWarnings("deprecation")
    private final class ReferencePolicyEventImpl extends OptionsEvent implements ReferencePolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public ReferencePolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Simon McDuff
     */
    private final class StaleReferencePolicyEventImpl extends OptionsEvent implements StaleReferencePolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public StaleReferencePolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ClearAdapterPolicyEventImpl extends OptionsEvent implements ClearAdapterPolicyEvent
    {
      private static final long serialVersionUID = 1L;

      public ClearAdapterPolicyEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }
}
