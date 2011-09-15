/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.LockTimeoutException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.util.StaleRevisionLockException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewDurabilityChangedEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDODeltaNotificationImpl;
import org.eclipse.emf.internal.cdo.object.CDOInvalidationNotificationImpl;
import org.eclipse.emf.internal.cdo.object.CDONotificationBuilder;
import org.eclipse.emf.internal.cdo.util.DefaultLocksChangedEvent;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.NullProgressMonitor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends AbstractCDOView
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private InternalCDOSession session;

  private String durableLockingID;

  private CDOFeatureAnalyzer featureAnalyzer = CDOFeatureAnalyzer.NOOP;

  private ChangeSubscriptionManager changeSubscriptionManager = new ChangeSubscriptionManager();

  private AdapterManager adapterManager = new AdapterManager();

  private OptionsImpl options;

  private long lastUpdateTime;

  private QueueRunner invalidationRunner;

  @ExcludeFromDump
  private InvalidationRunnerLock invalidationRunnerLock = new InvalidationRunnerLock();

  private volatile boolean invalidationRunnerActive;

  /**
   * @since 2.0
   */
  public CDOViewImpl(CDOBranch branch, long timeStamp)
  {
    super(branch.getPoint(timeStamp), CDOUtil.isLegacyModeDefault());
    options = createOptions();
  }

  public CDOViewImpl(String durableLockingID)
  {
    super(CDOUtil.isLegacyModeDefault());
    this.durableLockingID = durableLockingID;
    options = createOptions();
  }

  /**
   * @since 2.0
   */
  public OptionsImpl options()
  {
    return options;
  }

  public int getViewID()
  {
    return viewID;
  }

  /**
   * @since 2.0
   */
  public void setViewID(int viewId)
  {
    viewID = viewId;
  }

  /**
   * @since 2.0
   */
  public InternalCDOSession getSession()
  {
    return session;
  }

  /**
   * @since 2.0
   */
  public void setSession(InternalCDOSession session)
  {
    this.session = session;
  }

  public synchronized boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    checkActive();

    long timeStamp = branchPoint.getTimeStamp();
    long creationTimeStamp = getSession().getRepositoryInfo().getCreationTime();
    if (timeStamp != UNSPECIFIED_DATE && timeStamp < creationTimeStamp)
    {
      throw new IllegalArgumentException(
          MessageFormat
              .format(
                  "timeStamp ({0}) < repository creation time ({1})", CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(creationTimeStamp))); //$NON-NLS-1$
    }

    if (branchPoint.equals(getBranchPoint()))
    {
      return false;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Changing view target to {0}", branchPoint); //$NON-NLS-1$
    }

    Map<CDOID, InternalCDORevision> oldRevisions = new HashMap<CDOID, InternalCDORevision>();
    List<CDORevisionKey> allChangedObjects = new ArrayList<CDORevisionKey>();
    List<CDOIDAndVersion> allDetachedObjects = new ArrayList<CDOIDAndVersion>();

    List<InternalCDOObject> invalidObjects = getInvalidObjects(branchPoint);
    for (InternalCDOObject object : invalidObjects)
    {
      InternalCDORevision revision = object.cdoRevision();
      if (revision != null)
      {
        oldRevisions.put(object.cdoID(), revision);
      }
    }

    CDOSessionProtocol sessionProtocol = getSession().getSessionProtocol();
    OMMonitor monitor = new EclipseMonitor(new NullProgressMonitor());
    sessionProtocol.switchTarget(viewID, branchPoint, invalidObjects, allChangedObjects, allDetachedObjects, monitor);

    basicSetBranchPoint(branchPoint);
    doInvalidate(branchPoint.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE, allChangedObjects, allDetachedObjects,
        oldRevisions);

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireViewTargetChangedEvent(listeners);
    }

    return true;
  }

  private List<InternalCDOObject> getInvalidObjects(CDOBranchPoint branchPoint)
  {
    List<InternalCDOObject> result = new ArrayList<InternalCDOObject>();
    for (InternalCDOObject object : getModifiableObjects().values())
    {
      CDORevision revision = object.cdoRevision();
      if (revision == null || !revision.isValid(branchPoint))
      {
        result.add(object);
      }
    }

    return result;
  }

  /**
   * @throws InterruptedException
   * @since 2.0
   */
  public synchronized void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout)
      throws InterruptedException
  {
    checkActive();
    checkState(getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE, "Locking not supported for historial views");

    List<CDORevisionKey> revisionKeys = new LinkedList<CDORevisionKey>();
    for (CDOObject object : objects)
    {
      InternalCDORevision revision = getRevision(object);
      if (revision != null)
      {
        revisionKeys.add(revision);
      }
    }

    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    LockObjectsResult result = sessionProtocol.lockObjects2(revisionKeys, viewID, getBranch(), lockType, timeout);

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

    // Update the lock states in this view
    updateAndNotifyLockStates(Operation.LOCK, lockType, result.getTimestamp(), result.getNewLockStates());

    if (result.isWaitForUpdate())
    {
      if (!getSession().options().isPassiveUpdateEnabled())
      {
        throw new AssertionError(
            "Lock result requires client to wait, but client does not have passiveUpdates enabled.");
      }

      long requiredTimestamp = result.getRequiredTimestamp();
      waitForUpdate(requiredTimestamp);
    }
  }

  protected void updateAndNotifyLockStates(Operation op, LockType type, long timestamp, CDOLockState[] newLockStates)
  {
    updateLockStates(newLockStates);
    notifyOtherViewsAboutLockChanges(op, type, timestamp, newLockStates);
  }

  /**
   * Updates the lock states of objects held in this view
   */
  protected void updateLockStates(CDOLockState[] newLockStates)
  {
    for (CDOLockState lockState : newLockStates)
    {
      Object o = lockState.getLockedObject();
      CDOID id;
      if (o instanceof CDOID)
      {
        id = (CDOID)o;
      }
      else
      {
        id = ((CDOIDAndBranch)o).getID();
      }

      InternalCDOObject obj = getObject(id, false);
      if (obj != null)
      {
        obj.cdoInternalSetLockState(lockState);
      }
    }
  }

  /**
   * Notifies other views of lock changes performed in this view
   */
  private void notifyOtherViewsAboutLockChanges(Operation op, LockType type, long timestamp, CDOLockState[] lockStates)
  {
    if (lockStates.length > 0)
    {
      CDOLockChangeInfo lockChangeInfo = makeLockChangeInfo(op, type, timestamp, lockStates);
      getSession().handleLockNotification(lockChangeInfo, this);
    }
  }

  private CDOLockChangeInfo makeLockChangeInfo(Operation op, LockType type, long timestamp, CDOLockState[] newLockStates)
  {
    return CDOLockUtil.createLockChangeInfo(timestamp, this, getBranch(), op, type, newLockStates);
  }

  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    if (!options().isLockNotificationEnabled())
    {
      return;
    }

    // If lockChangeInfo pertains to a different view, do nothing.
    if (!lockChangeInfo.getBranch().equals(getBranch()))
    {
      return;
    }

    // If lockChangeInfo represents lock changes authored by this view itself, do nothing.
    CDOLockOwner thisView = CDOLockUtil.createLockOwner(this);
    if (lockChangeInfo.getLockOwner().equals(thisView))
    {
      return;
    }

    // TODO (CD) I know it is Eike's desideratum that this be done asynchronously.. but beware,
    // this will require the tests to be fixed to listen for the view events instead of the
    // session events.
    updateLockStates(lockChangeInfo.getLockStates());
    fireLocksChangedEvent(sender, lockChangeInfo);
  }

  private void fireLocksChangedEvent(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new LocksChangedEvent(sender, lockChangeInfo), listeners);
    }
  }

  protected InternalCDORevision getRevision(CDOObject object)
  {
    if (object.cdoState() == CDOState.NEW)
    {
      return null;
    }

    InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
    if (revision == null)
    {
      revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    }

    return revision;
  }

  /**
   * @since 2.0
   */
  public synchronized void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    checkActive();

    List<CDOID> objectIDs = null;
    if (objects != null)
    {
      objectIDs = new LinkedList<CDOID>();
      for (CDOObject obj : objects)
      {
        objectIDs.add(obj.cdoID());
      }
    }

    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    UnlockObjectsResult result = sessionProtocol.unlockObjects2(this, objectIDs, lockType);

    updateAndNotifyLockStates(Operation.UNLOCK, lockType, result.getTimestamp(), result.getNewLockStates());
  }

  /**
   * @since 2.0
   */
  public synchronized void unlockObjects()
  {
    unlockObjects(null, null);
  }

  /**
   * @since 2.0
   */
  public synchronized boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers)
  {
    checkActive();
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    return sessionProtocol.isObjectLocked(this, object, lockType, byOthers);
  }

  public synchronized String getDurableLockingID()
  {
    return durableLockingID;
  }

  public String enableDurableLocking(boolean enable)
  {
    final String oldID = durableLockingID;

    try
    {
      synchronized (this)
      {
        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        if (enable)
        {
          if (durableLockingID == null)
          {
            durableLockingID = sessionProtocol.changeLockArea(this, true);
          }

          return durableLockingID;
        }

        if (durableLockingID != null)
        {
          sessionProtocol.changeLockArea(this, false);
          durableLockingID = null;
        }

        return oldID;
      }
    }
    finally
    {
      if (!ObjectUtil.equals(oldID, durableLockingID))
      {
        fireEvent(new CDOViewDurabilityChangedEvent()
        {
          public CDOView getSource()
          {
            return CDOViewImpl.this;
          }

          public String getOldDurableLockingID()
          {
            return oldID;
          }

          public String getNewDurableLockingID()
          {
            return durableLockingID;
          }
        });
      }
    }
  }

  /**
   * @since 2.0
   */
  public synchronized CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return featureAnalyzer;
  }

  /**
   * @since 2.0
   */
  public synchronized void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    this.featureAnalyzer = featureAnalyzer == null ? CDOFeatureAnalyzer.NOOP : featureAnalyzer;
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction toTransaction()
  {
    checkActive();
    if (this instanceof InternalCDOTransaction)
    {
      return (InternalCDOTransaction)this;
    }

    throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.0"), this)); //$NON-NLS-1$
  }

  public synchronized InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    InternalCDORevisionManager revisionManager = session.getRevisionManager();
    int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
    CDOBranchPoint branchPoint = getBranchPointForID(id);
    return revisionManager.getRevision(id, branchPoint, initialChunkSize, CDORevision.DEPTH_NONE, loadOnDemand);
  }

  public CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    CDOLockState[] lockStates = sessionProtocol.getLockStates(viewID, ids);
    return lockStates;
  }

  private CDOBranchPoint getBranchPointForID(CDOID id)
  {
    // If this view's timestamp is something other than UNSPECIFIED_DATE,
    // then this is an 'audit' view, and so this timestamp must always be
    // used without any concern for possible sticky-view behavior
    CDOBranchPoint branchPoint = getBranchPoint();
    if (branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return branchPoint;
    }

    InternalCDOSession session = getSession();
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

  public synchronized void prefetchRevisions(CDOID id, int depth)
  {
    checkArg(depth != CDORevision.DEPTH_NONE, "Prefetch depth must not be zero"); //$NON-NLS-1$
    int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
    prefetchRevisions(id, depth, initialChunkSize);
  }

  protected void prefetchRevisions(CDOID id, int depth, int initialChunkSize)
  {
    CDORevisionManager revisionManager = session.getRevisionManager();
    revisionManager.getRevision(id, this, initialChunkSize, depth, true);
  }

  /*
   * Must not by synchronized on the view!
   */
  public/* synchronized */void invalidate(CDOBranch branch, long lastUpdateTime,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions, boolean async)
  {
    if (async)
    {
      QueueRunner runner = getInvalidationRunner();
      runner.addWork(new InvalidationRunnable(branch, lastUpdateTime, allChangedObjects, allDetachedObjects,
          oldRevisions));
    }
    else
    {
      doInvalidate(branch, lastUpdateTime, allChangedObjects, allDetachedObjects, oldRevisions);
    }
  }

  protected synchronized void doInvalidate(CDOBranch branch, long lastUpdateTime,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions)
  {
    try
    {
      if (ObjectUtil.equals(branch, getBranch()))
      {
        Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = null;
        List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>();
        Map<CDOObject, CDORevisionDelta> revisionDeltas = new HashMap<CDOObject, CDORevisionDelta>();
        Set<CDOObject> detachedObjects = new HashSet<CDOObject>();

        conflicts = invalidate(lastUpdateTime, allChangedObjects, allDetachedObjects, deltas, revisionDeltas,
            detachedObjects);

        sendInvalidationNotifications(revisionDeltas.keySet(), detachedObjects);
        fireInvalidationEvent(lastUpdateTime, Collections.unmodifiableMap(revisionDeltas),
            Collections.unmodifiableSet(detachedObjects));

        // First handle the conflicts, if any.
        if (conflicts != null)
        {
          handleConflicts(conflicts, deltas);
        }

        // Then send the notifications. The deltas could have been modified by the conflict resolvers.
        if (!deltas.isEmpty() || !detachedObjects.isEmpty())
        {
          sendDeltaNotifications(deltas, detachedObjects, oldRevisions);
        }

        fireAdaptersNotifiedEvent(lastUpdateTime);
      }
    }
    finally
    {
      setLastUpdateTime(lastUpdateTime);
    }
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

  private QueueRunner createInvalidationRunner()
  {
    return new QueueRunner()
    {
      @Override
      protected String getThreadName()
      {
        return "InvalidationRunner-" + CDOViewImpl.this; //$NON-NLS-1$
      }

      @Override
      public String toString()
      {
        return getThreadName();
      }
    };
  }

  public boolean isInvalidationRunnerActive()
  {
    return invalidationRunnerActive;
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
  private void fireInvalidationEvent(long timeStamp, Map<CDOObject, CDORevisionDelta> revisionDeltas,
      Set<CDOObject> detachedObjects)
  {
    if (!revisionDeltas.isEmpty() || !detachedObjects.isEmpty())
    {
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new InvalidationEvent(timeStamp, revisionDeltas, detachedObjects), listeners);
      }
    }
  }

  /**
   * @since 2.0
   */
  public synchronized void sendDeltaNotifications(Collection<CDORevisionDelta> deltas, Set<CDOObject> detachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions)
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

    if (detachedObjects != null)
    {
      for (CDOObject detachedObject : detachedObjects)
      {
        InternalCDOObject object = (InternalCDOObject)detachedObject;
        if (object.eNotificationRequired())
        {
          // if (!isLocked(object))
          {
            NotificationImpl notification = new CDODeltaNotificationImpl(object, CDONotification.DETACH_OBJECT,
                Notification.NO_FEATURE_ID, null, null);
            notification.dispatch();
          }
        }
      }

      getChangeSubscriptionManager().handleDetachedObjects(detachedObjects);
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
  public synchronized void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    if (!FSMUtil.isNew(eObject))
    {
      subscribe(eObject, adapter);
    }

    adapterManager.attachAdapter(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public synchronized void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    if (!FSMUtil.isNew(eObject))
    {
      unsubscribe(eObject, adapter);
    }

    adapterManager.detachAdapter(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public synchronized void subscribe(EObject eObject, Adapter adapter)
  {
    changeSubscriptionManager.subscribe(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public synchronized void unsubscribe(EObject eObject, Adapter adapter)
  {
    changeSubscriptionManager.unsubscribe(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public synchronized boolean hasSubscription(CDOID id)
  {
    return changeSubscriptionManager.getSubcribeObject(id) != null;
  }

  /**
   * @since 2.0
   */
  protected final ChangeSubscriptionManager getChangeSubscriptionManager()
  {
    return changeSubscriptionManager;
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
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    if (durableLockingID != null)
    {
      CDOBranchPoint branchPoint = sessionProtocol.openView(viewID, isReadOnly(), durableLockingID);
      basicSetBranchPoint(branchPoint);
    }
    else
    {
      sessionProtocol.openView(viewID, isReadOnly(), this);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    if (invalidationRunner != null)
    {
      LifecycleUtil.deactivate(invalidationRunner, OMLogger.Level.WARN);
      invalidationRunner = null;
    }

    try
    {
      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      if (LifecycleUtil.isActive(sessionProtocol))
      {
        sessionProtocol.closeView(viewID);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    try
    {
      session.viewDetached(this);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    changeSubscriptionManager = null;
    featureAnalyzer = null;
    options = null;
    super.doDeactivate();
  }

  public synchronized long getLastUpdateTime()
  {
    return lastUpdateTime;
  }

  public synchronized void setLastUpdateTime(long lastUpdateTime)
  {
    if (this.lastUpdateTime < lastUpdateTime)
    {
      this.lastUpdateTime = lastUpdateTime;
    }

    notifyAll();
  }

  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    long end = timeoutMillis == NO_TIMEOUT ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMillis;
    for (;;)
    {
      synchronized (this)
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
          wait(end - now);
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  protected final class AdapterManager
  {
    private Set<CDOObject> objects = new HashBag<CDOObject>();

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
      // Keep the object in memory
      Set<CDOObject> oldObject = objects;
      objects = new HashBag<CDOObject>();
      if (options().getStrongReferencePolicy() != CDOAdapterPolicy.NONE)
      {
        InternalCDOObject objects[] = getObjectsArray();
        for (int i = 0; i < objects.length; i++)
        {
          InternalCDOObject object = objects[i];
          attachObject(object);
        }
      }

      oldObject.clear();
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  protected final class ChangeSubscriptionManager
  {
    private Map<CDOID, SubscribeEntry> subscriptions = new HashMap<CDOID, SubscribeEntry>();

    public ChangeSubscriptionManager()
    {
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      handleNewObjects(commitContext.getNewObjects().values());
      handleDetachedObjects(commitContext.getDetachedObjects().values());
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
    private void notifyChangeSubcriptionPolicy()
    {
      boolean policiesPresent = options().hasChangeSubscriptionPolicies();
      subscriptions.clear();
      List<CDOID> ids = new ArrayList<CDOID>();
      if (policiesPresent)
      {
        for (InternalCDOObject cdoObject : getObjectsArray())
        {
          int count = getNumberOfValidAdapter(cdoObject);
          if (count > 0)
          {
            ids.add(cdoObject.cdoID());
            addEntry(cdoObject.cdoID(), cdoObject, count);
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
        InternalCDOObject cdoDetachedObject = (InternalCDOObject)object;
        if (cdoDetachedObject != null)
        {
          int count = getNumberOfValidAdapter(cdoDetachedObject);
          if (count > 0)
          {
            subscribe(cdoDetachedObject.cdoID(), cdoDetachedObject, count);
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

    private int getNumberOfValidAdapter(InternalCDOObject object)
    {
      int count = 0;
      if (!FSMUtil.isTransient(object) && !FSMUtil.isNew(object))
      {
        if (object.eNotificationRequired())
        {
          for (Adapter adapter : object.eAdapters())
          {
            if (shouldSubscribe(object, adapter))
            {
              count++;
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
        subscriptions.remove(id);

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

    private void addEntry(CDOID key, InternalCDOObject object, int count)
    {
      subscriptions.put(key, new SubscribeEntry(object, count));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SubscribeEntry
  {
    private InternalCDOObject object;

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
   * A separate class for better monitor debugging.
   * 
   * @author Eike Stepper
   */
  private static final class InvalidationRunnerLock
  {
  }

  /**
   * @author Eike Stepper
   */
  private final class InvalidationRunnable implements Runnable
  {
    private final CDOBranch branch;

    private final long lastUpdateTime;

    private final List<CDORevisionKey> allChangedObjects;

    private final List<CDOIDAndVersion> allDetachedObjects;

    private final Map<CDOID, InternalCDORevision> oldRevisions;

    private InvalidationRunnable(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
        List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions)
    {
      this.branch = branch;
      this.lastUpdateTime = lastUpdateTime;
      this.allChangedObjects = allChangedObjects;
      this.allDetachedObjects = allDetachedObjects;
      this.oldRevisions = oldRevisions;
    }

    public void run()
    {
      try
      {
        invalidationRunnerActive = true;
        doInvalidate(branch, lastUpdateTime, allChangedObjects, allDetachedObjects, oldRevisions);
      }
      finally
      {
        invalidationRunnerActive = false;
      }
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class InvalidationEvent extends Event implements CDOViewInvalidationEvent
  {
    private static final long serialVersionUID = 1L;

    private long timeStamp;

    private Map<CDOObject, CDORevisionDelta> revisionDeltas;

    private Set<CDOObject> detachedObjects;

    public InvalidationEvent(long timeStamp, Map<CDOObject, CDORevisionDelta> revisionDeltas,
        Set<CDOObject> detachedObjects)
    {
      this.timeStamp = timeStamp;
      this.revisionDeltas = revisionDeltas;
      this.detachedObjects = detachedObjects;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public Set<CDOObject> getDirtyObjects()
    {
      return revisionDeltas.keySet();
    }

    public Map<CDOObject, CDORevisionDelta> getRevisionDeltas()
    {
      return revisionDeltas;
    }

    public Set<CDOObject> getDetachedObjects()
    {
      return detachedObjects;
    }

    @Override
    public String toString()
    {
      return "CDOViewInvalidationEvent: " + revisionDeltas; //$NON-NLS-1$
    }
  }

  /**
   * @author Caspar De Groot
   * @since 4.1
   */
  private final class LocksChangedEvent extends DefaultLocksChangedEvent implements CDOViewLocksChangedEvent
  {
    private static final long serialVersionUID = 1L;

    public LocksChangedEvent(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
    {
      super(CDOViewImpl.this, sender, lockChangeInfo);
    }

    @Override
    public InternalCDOView getSource()
    {
      return (InternalCDOView)super.getSource();
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean invalidationNotificationEnabled;

    private CDOInvalidationPolicy invalidationPolicy = CDOInvalidationPolicy.DEFAULT;

    private boolean lockNotificationsEnabled;

    private CDORevisionPrefetchingPolicy revisionPrefetchingPolicy = CDOUtil
        .createRevisionPrefetchingPolicy(NO_REVISION_PREFETCHING);

    private CDOStaleReferencePolicy staleReferencePolicy = CDOStaleReferencePolicy.EXCEPTION;

    private HashBag<CDOAdapterPolicy> changeSubscriptionPolicies = new HashBag<CDOAdapterPolicy>();

    private CDOAdapterPolicy strongReferencePolicy = CDOAdapterPolicy.ALL;

    public OptionsImpl()
    {
      setCacheReferenceType(null);
    }

    public CDOViewImpl getContainer()
    {
      return CDOViewImpl.this;
    }

    public CDOInvalidationPolicy getInvalidationPolicy()
    {
      synchronized (CDOViewImpl.this)
      {
        return invalidationPolicy;
      }
    }

    public void setInvalidationPolicy(CDOInvalidationPolicy policy)
    {
      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (invalidationPolicy != policy)
        {
          invalidationPolicy = policy;
          event = new InvalidationPolicyEventImpl();
        }
      }

      fireEvent(event);
    }

    public boolean isInvalidationNotificationEnabled()
    {
      synchronized (CDOViewImpl.this)
      {
        return invalidationNotificationEnabled;
      }
    }

    public void setInvalidationNotificationEnabled(boolean enabled)
    {
      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (invalidationNotificationEnabled != enabled)
        {
          invalidationNotificationEnabled = enabled;
          event = new InvalidationNotificationEventImpl();
        }
      }

      fireEvent(event);
    }

    public boolean isLockNotificationEnabled()
    {
      return lockNotificationsEnabled;
    }

    public void setLockNotificationEnabled(boolean enabled)
    {
      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (enabled != lockNotificationsEnabled)
        {
          CDOSessionProtocol protocol = getSession().getSessionProtocol();
          protocol.enableLockNotifications(viewID, enabled);
          lockNotificationsEnabled = enabled;
          event = new LockNotificationEventImpl(enabled);
        }
      }

      fireEvent(event);
    }

    public boolean hasChangeSubscriptionPolicies()
    {
      synchronized (CDOViewImpl.this)
      {
        return !changeSubscriptionPolicies.isEmpty();
      }
    }

    public CDOAdapterPolicy[] getChangeSubscriptionPolicies()
    {
      synchronized (CDOViewImpl.this)
      {
        return changeSubscriptionPolicies.toArray(new CDOAdapterPolicy[changeSubscriptionPolicies.size()]);
      }
    }

    public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (changeSubscriptionPolicies.add(policy))
        {
          changeSubscriptionManager.notifyChangeSubcriptionPolicy();
          event = new ChangeSubscriptionPoliciesEventImpl();
        }
      }

      fireEvent(event);
    }

    public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (changeSubscriptionPolicies.remove(policy) && !changeSubscriptionPolicies.contains(policy))
        {
          changeSubscriptionManager.notifyChangeSubcriptionPolicy();
          event = new ChangeSubscriptionPoliciesEventImpl();
        }
      }

      fireEvent(event);
    }

    public CDOAdapterPolicy getStrongReferencePolicy()
    {
      synchronized (CDOViewImpl.this)
      {
        return strongReferencePolicy;
      }
    }

    public void setStrongReferencePolicy(CDOAdapterPolicy adapterPolicy)
    {
      if (adapterPolicy == null)
      {
        adapterPolicy = CDOAdapterPolicy.ALL;
      }

      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (strongReferencePolicy != adapterPolicy)
        {
          strongReferencePolicy = adapterPolicy;
          adapterManager.reset();
          event = new ReferencePolicyEventImpl();
        }
      }

      fireEvent(event);
    }

    public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy()
    {
      synchronized (CDOViewImpl.this)
      {
        return revisionPrefetchingPolicy;
      }
    }

    public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy)
    {
      if (prefetchingPolicy == null)
      {
        prefetchingPolicy = CDORevisionPrefetchingPolicy.NO_PREFETCHING;
      }

      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (revisionPrefetchingPolicy != prefetchingPolicy)
        {
          revisionPrefetchingPolicy = prefetchingPolicy;
          event = new RevisionPrefetchingPolicyEventImpl();
        }
      }

      fireEvent(event);
    }

    @Deprecated
    public CDOStaleReferencePolicy getStaleReferenceBehaviour()
    {
      return getStaleReferencePolicy();
    }

    @Deprecated
    public void setStaleReferenceBehaviour(CDOStaleReferencePolicy policy)
    {
      setStaleReferencePolicy(policy);
    }

    public CDOStaleReferencePolicy getStaleReferencePolicy()
    {
      synchronized (CDOViewImpl.this)
      {
        return staleReferencePolicy;
      }
    }

    public void setStaleReferencePolicy(CDOStaleReferencePolicy policy)
    {
      if (policy == null)
      {
        policy = CDOStaleReferencePolicy.EXCEPTION;
      }

      IEvent event = null;
      synchronized (CDOViewImpl.this)
      {
        if (staleReferencePolicy != policy)
        {
          staleReferencePolicy = policy;
          event = new StaleReferencePolicyEventImpl();
        }
      }

      fireEvent(event);
    }

    public ReferenceType getCacheReferenceType()
    {
      synchronized (CDOViewImpl.this)
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
    }

    public boolean setCacheReferenceType(ReferenceType referenceType)
    {
      if (referenceType == null)
      {
        referenceType = ReferenceType.SOFT;
      }

      synchronized (CDOViewImpl.this)
      {
        Map<CDOID, InternalCDOObject> objects = getModifiableObjects();
        ReferenceValueMap<CDOID, InternalCDOObject> newObjects;

        switch (referenceType)
        {
        case STRONG:
          if (objects instanceof ReferenceValueMap.Strong<?, ?>)
          {
            return false;
          }

          newObjects = new ReferenceValueMap.Strong<CDOID, InternalCDOObject>();
          break;

        case SOFT:
          if (objects instanceof ReferenceValueMap.Soft<?, ?>)
          {
            return false;
          }

          newObjects = new ReferenceValueMap.Soft<CDOID, InternalCDOObject>();
          break;

        case WEAK:
          if (objects instanceof ReferenceValueMap.Weak<?, ?>)
          {
            return false;
          }

          newObjects = new ReferenceValueMap.Weak<CDOID, InternalCDOObject>();
          break;

        default:
          throw new IllegalArgumentException(Messages.getString("CDOViewImpl.29")); //$NON-NLS-1$
        }

        if (objects == null)
        {
          setObjects(newObjects);
        }
        else
        {
          for (Entry<CDOID, InternalCDOObject> entry : objects.entrySet())
          {
            InternalCDOObject object = entry.getValue();
            if (object != null)
            {
              newObjects.put(entry.getKey(), object);
            }
          }

          Map<CDOID, InternalCDOObject> oldObjects = objects;
          setObjects(newObjects);
          oldObjects.clear();
        }
      }

      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new CacheReferenceTypeEventImpl(), listeners);
      }

      return true;
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
    private final class ChangeSubscriptionPoliciesEventImpl extends OptionsEvent implements
        ChangeSubscriptionPoliciesEvent
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

      public boolean getEnabled()
      {
        return enabled;
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class RevisionPrefetchingPolicyEventImpl extends OptionsEvent implements
        RevisionPrefetchingPolicyEvent
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
  }
}
