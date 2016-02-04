/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionsLoadedEvent;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
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
import org.eclipse.emf.internal.cdo.session.SessionUtil;
import org.eclipse.emf.internal.cdo.util.DefaultLocksChangedEvent;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ExecutorWorkSerializer;
import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.concurrent.RunnableWithName;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.event.ThrowableEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.IOptionsEvent;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.ref.ReferenceType;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends AbstractCDOView implements IExecutorServiceProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private InternalCDOSession session;

  private String durableLockingID;

  private final CDOUnitManagerImpl unitManager = new CDOUnitManagerImpl();

  private ChangeSubscriptionManager changeSubscriptionManager = new ChangeSubscriptionManager();

  private AdapterManager adapterManager = new AdapterManager();

  private OptionsImpl options;

  private long lastUpdateTime;

  private CDOLockOwner lockOwner;

  private Map<CDOObject, CDOLockState> lockStates = new WeakHashMap<CDOObject, CDOLockState>();

  private ExecutorWorkSerializer invalidationRunner = createInvalidationRunner();

  private volatile boolean invalidationRunnerActive;

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

  public ExecutorService getExecutorService()
  {
    return ConcurrencyUtil.getExecutorService(session);
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
  @Override
  public void setSession(InternalCDOSession session)
  {
    super.setSession(session);
    this.session = session;
  }

  public int getSessionID()
  {
    return session.getSessionID();
  }

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

        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        OMMonitor monitor = progressMonitor != null ? new EclipseMonitor(progressMonitor) : null;
        sessionProtocol.switchTarget(viewID, branchPoint, invalidObjects, allChangedObjects, allDetachedObjects,
            monitor);

        basicSetBranchPoint(branchPoint);
        CDOBranch branch = branchPoint.getBranch();

        try
        {
          CDOStateMachine.SWITCHING_TARGET.set(Boolean.TRUE);
          doInvalidate(branch, UNSPECIFIED_DATE, allChangedObjects, allDetachedObjects, oldRevisions, true);
        }
        finally
        {
          CDOStateMachine.SWITCHING_TARGET.remove();
        }

        IListener[] listeners = getListeners();
        if (listeners != null)
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
    List<InternalCDOObject> result = new ArrayList<InternalCDOObject>();
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

  private Set<? extends CDOObject> getSet(Collection<? extends CDOObject> objects)
  {
    if (objects instanceof Set)
    {
      return (Set<? extends CDOObject>)objects;
    }

    return new HashSet<CDOObject>(objects);
  }

  /**
   * @since 2.0
   */
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout)
      throws InterruptedException
  {
    lockObjects(objects, lockType, timeout, false);
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive)
      throws InterruptedException
  {
    checkActive();
    checkState(getTimeStamp() == UNSPECIFIED_DATE, "Locking not supported for historial views");

    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        Set<? extends CDOObject> uniqueObjects = getSet(objects);
        int size = uniqueObjects.size();

        List<CDORevisionKey> revisionKeys = new ArrayList<CDORevisionKey>(size);
        List<CDOLockState> locksOnNewObjects = new ArrayList<CDOLockState>(size);

        for (CDOObject object : uniqueObjects)
        {
          if (FSMUtil.isNew(object))
          {
            CDOLockState lockState = createUpdatedLockStateForNewObject(object, lockType, true);
            locksOnNewObjects.add(lockState);
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
              throw new AssertionError(
                  "Lock result requires client to wait, but client does not have passiveUpdates enabled");
            }

            long requiredTimestamp = result.getRequiredTimestamp();
            if (!waitForUpdate(requiredTimestamp, 10000L))
            {
              throw new AssertionError("Lock result requires client to wait for commit " + requiredTimestamp
                  + ", but client did not receive invalidations after " + lastUpdateTime);
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
                InternalCDORevision requiredRevision = revisionManager.getRevisionByVersion(id, requiredKey,
                    CDORevision.UNCHUNKED, true);
                InternalCDORevisionDelta revisionDelta = requiredRevision.compare(revision);
                CDOStateMachine.INSTANCE.invalidate(object, revisionDelta);
              }
            }
          }
        }

        CDOLockState[] locksOnNewObjectsArray = locksOnNewObjects.toArray(new CDOLockState[locksOnNewObjects.size()]);
        updateLockStates(locksOnNewObjectsArray);

        if (result != null)
        {
          updateAndNotifyLockStates(Operation.LOCK, lockType, result.getTimestamp(), result.getNewLockStates());
        }
      }
      finally
      {
        unlockView();
      }
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
      Object lockedObject = lockState.getLockedObject();
      CDOID id;

      if (lockedObject instanceof CDOID)
      {
        id = (CDOID)lockedObject;
      }
      else if (lockedObject instanceof CDOIDAndBranch)
      {
        id = ((CDOIDAndBranch)lockedObject).getID();
      }
      else if (lockedObject instanceof EObject)
      {
        CDOObject newObj = CDOUtil.getCDOObject((EObject)lockedObject);
        id = newObj.cdoID();
      }
      else
      {
        throw new IllegalStateException("Unexpected: " + lockedObject.getClass().getSimpleName());
      }

      InternalCDOObject object = getObject(id, false);
      if (object != null)
      {
        InternalCDOLockState existingLockState = (InternalCDOLockState)lockStates.get(object);
        if (existingLockState != null)
        {
          existingLockState.updateFrom(lockState);
        }
        else
        {
          lockStates.put(object, lockState);
        }
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
      session.handleLockNotification(lockChangeInfo, this);

      if (isActive())
      {
        fireLocksChangedEvent(this, lockChangeInfo);
      }
    }
  }

  private CDOLockChangeInfo makeLockChangeInfo(Operation op, LockType type, long timestamp,
      CDOLockState[] newLockStates)
  {
    return CDOLockUtil.createLockChangeInfo(timestamp, this, getBranch(), op, type, newLockStates);
  }

  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    CDOLockChangeInfo event = null;

    try
    {
      synchronized (lockStates)
      {
        if (!options().isLockNotificationEnabled())
        {
          return;
        }

        if (lockChangeInfo.isInvalidateAll())
        {
          lockStates.clear();
          event = lockChangeInfo;
          return;
        }

        // If lockChangeInfo pertains to a different view, do nothing.
        CDOBranch lockChangeBranch = lockChangeInfo.getBranch();
        CDOBranch viewBranch = getBranch();
        if (lockChangeBranch != viewBranch)
        {
          return;
        }

        if (lockChangeInfo.getLockOwner().equals(lockOwner))
        {
          return;
        }

        // TODO (CD) I know it is Eike's desideratum that this be done asynchronously.. but beware,
        // this will require the tests to be fixed to listen for the view events instead of the
        // session events.
        updateLockStates(lockChangeInfo.getLockStates());
        event = lockChangeInfo;
      }
    }
    finally
    {
      if (event != null)
      {
        fireLocksChangedEvent(sender, event);
      }
    }
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
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    unlockObjects(objects, lockType, false);
  }

  /**
   * Note: This may get called with objects == null, and lockType == null, which is a request to remove all locks on all
   * objects in this view.
   */
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<CDOID> objectIDs = null;
        List<CDOLockState> locksOnNewObjects = new LinkedList<CDOLockState>();

        if (objects != null)
        {
          objectIDs = new ArrayList<CDOID>();

          for (CDOObject object : getSet(objects))
          {
            if (FSMUtil.isNew(object))
            {
              CDOLockState lockState = createUpdatedLockStateForNewObject(object, lockType, false);
              locksOnNewObjects.add(lockState);
            }
            else
            {
              objectIDs.add(object.cdoID());
            }
          }
        }
        else
        {
          locksOnNewObjects.addAll(createUnlockedLockStatesForAllNewObjects());
        }

        UnlockObjectsResult result = null;
        if (objectIDs == null || !objectIDs.isEmpty())
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          result = sessionProtocol.unlockObjects2(this, objectIDs, lockType, recursive);
        }

        CDOLockState[] locksOnNewObjectsArray = locksOnNewObjects.toArray(new CDOLockState[locksOnNewObjects.size()]);
        updateLockStates(locksOnNewObjectsArray);

        if (result != null)
        {
          updateAndNotifyLockStates(Operation.UNLOCK, lockType, result.getTimestamp(), result.getNewLockStates());
        }
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected InternalCDOLockState createUpdatedLockStateForNewObject(CDOObject object, LockType lockType, boolean on)
  {
    throw new ReadOnlyException();
  }

  protected Collection<CDOLockState> createUnlockedLockStatesForAllNewObjects()
  {
    return Collections.emptyList();
  }

  /**
   * @since 2.0
   */
  public void unlockObjects()
  {
    unlockObjects(null, null);
  }

  /**
   * @since 2.0
   */
  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers)
  {
    checkActive();
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        return sessionProtocol.isObjectLocked(this, object, lockType, byOthers);
      }
      finally
      {
        unlockView();
      }
    }
  }

  public boolean isDurableView()
  {
    return durableLockingID != null;
  }

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

  @Deprecated
  public String enableDurableLocking(boolean enable)
  {
    if (enable)
    {
      return enableDurableLocking();
    }

    disableDurableLocking(false);
    return null;
  }

  public String enableDurableLocking()
  {
    final String oldID = durableLockingID;

    try
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          if (durableLockingID == null)
          {
            durableLockingID = sessionProtocol.changeLockArea(this, true);
          }

          return durableLockingID;
        }
        finally
        {
          unlockView();
        }
      }
    }
    finally
    {
      fireDurabilityChangedEvent(oldID);
    }
  }

  public void disableDurableLocking(boolean releaseLocks)
  {
    final String oldID = durableLockingID;

    try
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          if (durableLockingID != null)
          {
            sessionProtocol.changeLockArea(this, false);
            durableLockingID = null;

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
    }
    finally
    {
      fireDurabilityChangedEvent(oldID);
    }
  }

  private void fireDurabilityChangedEvent(final String oldID)
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

  /**
   * @since 2.0
   */
  @Deprecated
  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return options().getFeatureAnalyzer();
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
  @Deprecated
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        options.setFeatureAnalyzer(featureAnalyzer);
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
  public InternalCDOTransaction toTransaction()
  {
    checkActive();
    if (this instanceof InternalCDOTransaction)
    {
      return (InternalCDOTransaction)this;
    }

    throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.0"), this)); //$NON-NLS-1$
  }

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
  public void remapObject(CDOID oldID)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        InternalCDOObject object = getObject(oldID, false);
        InternalCDOLockState oldLockState = (InternalCDOLockState)lockStates.remove(object);
        if (oldLockState != null)
        {
          Object lockedObject = getLockTarget(object); // CDOID or CDOIDAndBranch
          InternalCDOLockState newLockState = (InternalCDOLockState)CDOLockUtil.createLockState(lockedObject);
          newLockState.updateFrom(oldLockState);
          lockStates.put(object, newLockState);
        }
        else if (options().isLockStatePrefetchEnabled())
        {
          Object lockedObject = getLockTarget(object); // CDOID or CDOIDAndBranch
          CDOLockState newLockState = CDOLockUtil.createLockState(lockedObject);
          lockStates.put(object, newLockState);
        }

        super.remapObject(oldID);
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
    unitManager.addObject(object);
  }

  @Override
  protected void objectDeregistered(InternalCDOObject object)
  {
    removeLockState(object);
  }

  public CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        return getLockStates(ids, true);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected CDOLockState[] getLockStates(Collection<CDOID> ids, boolean loadOnDemand)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        List<CDOID> missing = new LinkedList<CDOID>();
        List<CDOLockState> lockStates = new LinkedList<CDOLockState>();
        List<CDOLockState> locksOnNewObjects = new ArrayList<CDOLockState>(ids.size());

        for (CDOID id : ids)
        {
          CDOLockState lockState = null;
          InternalCDOObject object = getObject(id, false);
          if (object != null)
          {
            lockState = this.lockStates.get(object);
          }

          if (lockState != null)
          {
            lockStates.add(lockState);
          }
          else if (loadOnDemand && object != null && FSMUtil.isNew(object))
          {
            Object lockedObject = getLockTarget(object); // CDOID or CDOIDAndBranch
            CDOLockState defaultLockState = CDOLockUtil.createLockState(lockedObject);
            locksOnNewObjects.add(defaultLockState);
            lockStates.add(defaultLockState);
          }
          else
          {
            missing.add(id);
          }
        }

        if (loadOnDemand && (missing.size() > 0 || ids.isEmpty()))
        {
          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          CDOLockState[] loadedLockStates = sessionProtocol.getLockStates(viewID, missing, CDOLockState.DEPTH_NONE);
          List<CDOLockState> newLockStateForCache = new ArrayList<CDOLockState>(
              loadedLockStates.length + missing.size());
          for (CDOLockState loadedLockState : loadedLockStates)
          {
            lockStates.add(loadedLockState);
            newLockStateForCache.add(loadedLockState);

            CDOID cdoID = CDOIDUtil.getCDOID(loadedLockState.getLockedObject());
            if (cdoID != null)
            {
              missing.remove(cdoID);
            }
          }

          for (CDOID missingLockStateForCDOID : missing)
          {
            Object target;

            InternalCDOObject object = getObject(missingLockStateForCDOID, false);
            if (object != null)
            {
              target = getLockTarget(object); // CDOID or CDOIDAndBranch
            }
            else
            {
              target = missingLockStateForCDOID;
            }

            CDOLockState defaultLockState = CDOLockUtil.createLockState(target);
            lockStates.add(defaultLockState);
            newLockStateForCache.add(defaultLockState);
          }

          if (options().isLockNotificationEnabled())
          {
            updateLockStates(newLockStateForCache.toArray(new CDOLockState[newLockStateForCache.size()]));
          }
        }

        CDOLockState[] locksOnNewObjectsArray = locksOnNewObjects.toArray(new CDOLockState[locksOnNewObjects.size()]);
        updateLockStates(locksOnNewObjectsArray);

        return lockStates.toArray(new CDOLockState[lockStates.size()]);
      }
      finally
      {
        unlockView();
      }
    }
  }

  protected CDOLockState removeLockState(CDOObject object)
  {
    return lockStates.remove(object);
  }

  protected CDOLockState getLockState(CDOObject object)
  {
    return lockStates.get(object);
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
  @Deprecated
  public/* synchronized */void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions, boolean async)
  {
    invalidate(branch, lastUpdateTime, allChangedObjects, allDetachedObjects, oldRevisions, async, true);
  }

  /*
   * Must not by synchronized on the view!
   */
  public/* synchronized */void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions, boolean async,
      boolean clearResourcePathCache)
  {
    if (async)
    {
      IWorkSerializer serializer = getInvalidationRunner();
      serializer.addWork(new InvalidationRunnable(branch, lastUpdateTime, allChangedObjects, allDetachedObjects,
          oldRevisions, clearResourcePathCache));
    }
    else
    {
      doInvalidate(branch, lastUpdateTime, allChangedObjects, allDetachedObjects, oldRevisions, clearResourcePathCache);
    }
  }

  protected void doInvalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions,
      boolean clearResourcePathCache)
  {
    synchronized (getViewMonitor())
    {
      lockView();

      try
      {
        if (getTimeStamp() != UNSPECIFIED_DATE && CDOStateMachine.SWITCHING_TARGET.get() != Boolean.TRUE)
        {
          // Don't invalidate historical views unless during a branch point switch.
          return;
        }

        try
        {
          // Also false for FailureCommitInfos (because of branch==null). Only setLastUpdateTime() is called below.
          if (branch == getBranch())
          {
            if (clearResourcePathCache)
            {
              clearResourcePathCacheIfNecessary(null);
            }

            List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>();
            Map<CDOObject, CDORevisionDelta> revisionDeltas = new HashMap<CDOObject, CDORevisionDelta>();
            Set<CDOObject> detachedObjects = new HashSet<CDOObject>();

            Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = invalidate(allChangedObjects,
                allDetachedObjects, deltas, revisionDeltas, detachedObjects);
            handleConflicts(lastUpdateTime, conflicts, deltas);

            sendInvalidationNotifications(revisionDeltas.keySet(), detachedObjects);
            fireInvalidationEvent(lastUpdateTime, Collections.unmodifiableMap(revisionDeltas),
                Collections.unmodifiableSet(detachedObjects));

            // Then send the notifications. The deltas could have been modified by the conflict resolvers.
            if (!deltas.isEmpty() || !detachedObjects.isEmpty())
            {
              sendDeltaNotifications(deltas, detachedObjects, oldRevisions);
            }

            fireAdaptersNotifiedEvent(lastUpdateTime);
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
      finally
      {
        unlockView();
      }
    }
  }

  public ExecutorWorkSerializer getInvalidationRunner()
  {
    return invalidationRunner;
  }

  private ExecutorWorkSerializer createInvalidationRunner()
  {
    return new ExecutorWorkSerializer()
    {
      @Override
      protected void noWork()
      {
        if (isClosed())
        {
          dispose();
        }
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
  public void sendDeltaNotifications(Collection<CDORevisionDelta> deltas, Set<CDOObject> detachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions)
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

          getChangeSubscriptionManager().handleDetachedObjects(detachedObjects);
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

    CDOViewRegistryImpl.INSTANCE.register(this);

    Runnable runnable = SessionUtil.getTestDelayInViewActivation();
    if (runnable != null)
    {
      runnable.run();
    }

    lockOwner = CDOLockUtil.createLockOwner(this);

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

    ExecutorService executorService = ConcurrencyUtil.getExecutorService(session);
    invalidationRunner.setExecutor(executorService);

    try
    {
      LifecycleUtil.activate(invalidationRunner);
    }
    catch (LifecycleException ex)
    {
      // Don't pollute the log if the worker thread is interrupted due to asynchronous view.close()
      if (!(ex.getCause() instanceof InterruptedException))
      {
        throw ex;
      }
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    // Detach viewset from the view
    InternalCDOViewSet viewSet = getViewSet();
    viewSet.remove(this);

    super.doBeforeDeactivate();
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    unitManager.deactivate();

    CDOViewRegistryImpl.INSTANCE.deregister(this);
    LifecycleUtil.deactivate(invalidationRunner, OMLogger.Level.WARN);

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
    finally
    {
      synchronized (lockStates)
      {
        if (options.lockStatePrefetcher != null)
        {
          options.lockStatePrefetcher.dispose();
          options.lockStatePrefetcher = null;
        }

        if (session.isActive() && !lockStates.isEmpty())
        {
          List<CDOLockState> result = new ArrayList<CDOLockState>();
          for (CDOLockState lockState : lockStates.values())
          {
            if (((InternalCDOLockState)lockState).removeOwner(lockOwner))
            {
              result.add(lockState);
            }
          }

          if (!result.isEmpty())
          {
            CDOLockState[] deactivateLockStates = result.toArray(new CDOLockState[result.size()]);
            long timeStamp = session.getLastUpdateTime();
            notifyOtherViewsAboutLockChanges(Operation.UNLOCK, null, timeStamp, deactivateLockStates);
          }

          lockStates.clear();
          lockOwner = null;
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

    changeSubscriptionManager = null;
    super.doDeactivate();
  }

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

  protected static Object getLockTarget(CDOObject object)
  {
    CDOView view = object.cdoView();
    if (view == null)
    {
      return null;
    }

    CDOID id = object.cdoID();
    boolean branching = view.getSession().getRepositoryInfo().isSupportingBranches();
    if (branching)
    {
      return CDOIDUtil.createIDAndBranch(id, view.getBranch());
    }

    return id;
  }

  public final CDOUnitManagerImpl getUnitManager()
  {
    return unitManager;
  }

  /**
   * @author Eike Stepper
   */
  public final class CDOUnitManagerImpl extends Container<CDOUnit> implements CDOUnitManager
  {
    private final Map<EObject, CDOUnit> unitPerRoot = new HashMap<EObject, CDOUnit>();

    private final Map<EObject, CDOUnit> unitPerObject = new HashMap<EObject, CDOUnit>();

    public CDOUnitManagerImpl()
    {
    }

    public CDOView getView()
    {
      return CDOViewImpl.this;
    }

    public boolean isUnit(EObject root)
    {
      CDOUnitImpl unit = requestUnit(root, CDOProtocolConstants.UNIT_CHECK);
      return unit != null;
    }

    public CDOUnit createUnit(EObject root) throws UnitExistsException
    {
      CDOUnitImpl unit = requestUnit(root, CDOProtocolConstants.UNIT_CREATE);
      if (unit == null)
      {
        throw new UnitExistsException();
      }

      fireElementAddedEvent(unit);
      return unit;
    }

    public CDOUnit openUnit(EObject root) throws UnitNotFoundException
    {
      CDOUnitImpl unit = requestUnit(root, CDOProtocolConstants.UNIT_OPEN);
      if (unit == null)
      {
        throw new UnitNotFoundException();
      }

      fireElementAddedEvent(unit);
      return unit;
    }

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

    public CDOUnit[] getOpenUnits()
    {
      return getElements();
    }

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
      return unitPerObject.get(object);
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
    protected void doDeactivate() throws Exception
    {
      unitPerRoot.clear();
      unitPerObject.clear();
      super.doDeactivate();
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

    private CDOUnitImpl requestUnit(EObject root, byte opcode)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (opcode == CDOProtocolConstants.UNIT_CREATE)
          {
            CDOUnit containingUnit = getOpenUnit(root);
            if (containingUnit != null)
            {
              throw new CDOException(
                  "Attempt to nest the new unit " + root + " in the existing unit " + containingUnit);
            }

            for (CDOUnit existingUnit : unitPerRoot.values())
            {
              if (EcoreUtil.isAncestor(root, existingUnit.getRoot()))
              {
                throw new CDOException(
                    "Attempt to nest the existing unit " + existingUnit + " in the new unit " + root);
              }
            }
          }

          final InternalCDORevisionManager revisionManager = session.getRevisionManager();
          final CDOUnitImpl unit = new CDOUnitImpl(root);

          int viewID = getViewID();
          CDOID rootID = getCDORoot(root).cdoID();

          CDORevisionHandler revisionHandler = opcode == CDOProtocolConstants.UNIT_CREATE
              || opcode == CDOProtocolConstants.UNIT_OPEN ? new CDORevisionHandler()
              {
                public boolean handleRevision(CDORevision revision)
                {
                  ++unit.elements;
                  revisionManager.addRevision(revision);

                  CDOID id = revision.getID();
                  changeSubscriptionManager.removeEntry(id);

                  InternalCDOObject object = getObject(id);
                  unitPerObject.put(object, unit);
                  return true;
                }
              } : null;

          CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
          boolean success = sessionProtocol.requestUnit(viewID, rootID, opcode, revisionHandler);

          if (success)
          {
            if (revisionHandler != null)
            {
              unitPerRoot.put(root, unit);
              unitPerObject.put(root, unit);
            }

            return unit;
          }

          return null;
        }
        finally
        {
          unlockView();
        }
      }
    }

    private void closeUnit(CDOUnit unit, boolean resubscribe)
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          requestUnit(unit.getRoot(), CDOProtocolConstants.UNIT_CLOSE);

          if (resubscribe && !options.hasChangeSubscriptionPolicies())
          {
            resubscribe = false;
          }

          for (Iterator<Entry<EObject, CDOUnit>> it = unitPerObject.entrySet().iterator(); it.hasNext();)
          {
            Entry<EObject, CDOUnit> entry = it.next();
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

      public CDOUnitManagerImpl getManager()
      {
        return CDOUnitManagerImpl.this;
      }

      public EObject getRoot()
      {
        return root;
      }

      public int getElements()
      {
        return elements;
      }

      public void close()
      {
        close(true);
      }

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
      // Keep the objects in memory
      Set<CDOObject> oldObjects = objects;
      objects = new HashBag<CDOObject>();
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
      List<CDOID> ids = new ArrayList<CDOID>();
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
   * A {@link IListener} to prefetch {@link CDOLockState lock states} when {@link CDORevision revisions} are loaded,
   * according to {@link Options#setLockStatePrefetchEnabled(boolean)} option.
   *
   * @author Esteban Dugueperoux
   */
  private final class LockStatePrefetcher implements IListener
  {
    public LockStatePrefetcher()
    {
      session.getRevisionManager().addListener(this);
    }

    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDORevisionsLoadedEvent)
      {
        CDORevisionsLoadedEvent revisionsLoadedEvent = (CDORevisionsLoadedEvent)event;
        updateCDOViewObjectsCache(revisionsLoadedEvent);
      }
    }

    private void updateCDOViewObjectsCache(CDORevisionsLoadedEvent revisionsLoadedEvent)
    {
      Set<CDOID> ids = new HashSet<CDOID>();
      CDOLockStateLoadingPolicy lockStateLoadingPolicy = options().getLockStateLoadingPolicy();
      for (CDORevision revision : revisionsLoadedEvent.getPrimaryLoadedRevisions())
      {
        // Bug 466721 : Check null if it is a about a DetachedRevision
        if (revision != null)
        {
          CDOID id = revision.getID();
          if (id != null && lockStateLoadingPolicy.loadLockState(id))
          {
            // - Don't ask to create an object for CDOResource as the caller of ResourceSet.getResource()
            // can have created it but not yet registered in CDOView.
            // - Don't ask others CDOResourceNode either as it will create some load revisions request
            // in addition to mode without lock state prefetch
            boolean isResourceNode = revision.isResourceNode();
            InternalCDOObject object = getObject(id, !isResourceNode);
            if (object != null)
            {
              ids.add(id);
            }
          }
        }
      }

      if (!ids.isEmpty())
      {
        // direct call the session protocol
        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        CDOLockState[] lockStates = sessionProtocol.getLockStates(viewID, ids, revisionsLoadedEvent.getPrefetchDepth());

        updateLockStatesForAllViews(lockStates);

        // add missing lock states
        List<CDOLockState> missingLockStates = new ArrayList<CDOLockState>();
        for (CDOID id : ids)
        {
          InternalCDOObject object = getObject(id, false);

          if (object != null && CDOViewImpl.this.lockStates.get(object) == null)
          {
            Object lockedObject = getLockTarget(object); // CDOID or CDOIDAndBranch
            missingLockStates.add(CDOLockUtil.createLockState(lockedObject));
          }
        }

        for (CDORevision revision : revisionsLoadedEvent.getAdditionalLoadedRevisions())
        {
          CDOID id = revision.getID();
          if (id != null && lockStateLoadingPolicy.loadLockState(id))
          {
            boolean isResourceNode = revision.isResourceNode();
            InternalCDOObject object = getObject(id, !isResourceNode);
            if (object != null && CDOViewImpl.this.lockStates.get(object) == null)
            {
              Object lockedObject = getLockTarget(object); // CDOID or CDOIDAndBranch
              missingLockStates.add(CDOLockUtil.createLockState(lockedObject));
            }
          }
        }

        updateLockStatesForAllViews(missingLockStates.toArray(new CDOLockState[missingLockStates.size()]));
      }
    }

    private void updateLockStatesForAllViews(CDOLockState[] lockStates)
    {
      updateLockStates(lockStates);

      for (CDOCommonView view : getSession().getViews())
      {
        if (view != CDOViewImpl.this && view.getBranch() == getBranch())
        {
          updateLockStates(lockStates);
        }
      }
    }

    public void dispose()
    {
      session.getRevisionManager().removeListener(this);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class InvalidationRunnable extends RunnableWithName
  {
    private final CDOBranch branch;

    private final long lastUpdateTime;

    private final List<CDORevisionKey> allChangedObjects;

    private final List<CDOIDAndVersion> allDetachedObjects;

    private final Map<CDOID, InternalCDORevision> oldRevisions;

    private boolean clearResourcePathCache;

    private InvalidationRunnable(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
        List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions,
        boolean clearResourcePathCache)
    {
      this.branch = branch;
      this.lastUpdateTime = lastUpdateTime;
      this.allChangedObjects = allChangedObjects;
      this.allDetachedObjects = allDetachedObjects;
      this.oldRevisions = oldRevisions;
      this.clearResourcePathCache = clearResourcePathCache;
    }

    @Override
    public String getName()
    {
      return "CDOViewInvalidationRunner-" + CDOViewImpl.this; //$NON-NLS-1$
    }

    @Override
    protected void doRun()
    {
      try
      {
        invalidationRunnerActive = true;
        doInvalidate(branch, lastUpdateTime, allChangedObjects, allDetachedObjects, oldRevisions,
            clearResourcePathCache);
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
      return "CDOViewInvalidationEvent" + revisionDeltas; //$NON-NLS-1$
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

    public EObject[] getAffectedObjects()
    {
      List<EObject> objects = new ArrayList<EObject>();
      CDOView view = getSource();

      CDOLockState[] lockStates = getLockStates();
      for (int i = 0; i < lockStates.length; i++)
      {
        CDOLockState lockState = lockStates[i];
        Object lockedObject = lockState.getLockedObject();

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
   * @since 2.0
   */
  public class OptionsImpl extends Notifier implements Options
  {
    private boolean loadNotificationEnabled;

    private boolean detachmentNotificationEnabled;

    private boolean invalidationNotificationEnabled;

    private CDOInvalidationPolicy invalidationPolicy = CDOInvalidationPolicy.DEFAULT;

    private boolean lockNotificationsEnabled;

    private CDOLockStateLoadingPolicy lockStateLoadingPolicy = new CDODefaultLockStateLoadingPolicy();

    private LockStatePrefetcher lockStatePrefetcher;

    private CDORevisionPrefetchingPolicy revisionPrefetchingPolicy = CDOUtil
        .createRevisionPrefetchingPolicy(NO_REVISION_PREFETCHING);

    private CDOFeatureAnalyzer featureAnalyzer = CDOFeatureAnalyzer.NOOP;

    private CDOStaleReferencePolicy staleReferencePolicy = CDOStaleReferencePolicy.DEFAULT;

    private HashBag<CDOAdapterPolicy> changeSubscriptionPolicies = new HashBag<CDOAdapterPolicy>();

    private CDOAdapterPolicy strongReferencePolicy = CDOAdapterPolicy.ALL;

    public OptionsImpl()
    {
    }

    public CDOViewImpl getContainer()
    {
      return CDOViewImpl.this;
    }

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

    public boolean isLockNotificationEnabled()
    {
      return lockNotificationsEnabled;
    }

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

    public boolean isLockStatePrefetchEnabled()
    {
      return lockStatePrefetcher != null;
    }

    public void setLockStatePrefetchEnabled(boolean enabled)
    {
      checkActive();

      IEvent event = null;
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          if (enabled)
          {
            if (lockStatePrefetcher == null)
            {
              lockStatePrefetcher = new LockStatePrefetcher();
              event = new LockStatePrefetchEventImpl();
            }
          }
          else
          {
            if (lockStatePrefetcher != null)
            {
              lockStatePrefetcher.dispose();
              lockStatePrefetcher = null;
              event = new LockStatePrefetchEventImpl();
            }
          }
        }
        finally
        {
          unlockView();
        }
      }

      fireEvent(event);
    }

    public CDOLockStateLoadingPolicy getLockStateLoadingPolicy()
    {
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          return lockStateLoadingPolicy;
        }
        finally
        {
          unlockView();
        }
      }
    }

    public void setLockStateLoadingPolicy(CDOLockStateLoadingPolicy lockStateLoadingPolicy)
    {
      checkActive();
      synchronized (getViewMonitor())
      {
        lockView();

        try
        {
          this.lockStateLoadingPolicy = lockStateLoadingPolicy;
        }
        finally
        {
          unlockView();
        }
      }
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
    private final class ChangeSubscriptionPoliciesEventImpl extends OptionsEvent
        implements ChangeSubscriptionPoliciesEvent
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

      public boolean getEnabled()
      {
        return enabled;
      }
    }

    /**
     * An {@link IOptionsEvent options event} fired from common view {@link CDOCommonView#options() options} when the
     * {@link OptionsImpl#setLockStatePrefetchEnabled(boolean) lock state prefetch enabled} option has changed.
     *
     * @author Esteban Dugueperoux
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.4
     */
    private final class LockStatePrefetchEventImpl extends OptionsEvent
    {
      private static final long serialVersionUID = 1L;

      public LockStatePrefetchEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class RevisionPrefetchingPolicyEventImpl extends OptionsEvent
        implements RevisionPrefetchingPolicyEvent
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
  }
}
