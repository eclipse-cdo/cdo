/**
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
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
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
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.emf.internal.cdo.CDODeltaNotificationImpl;
import org.eclipse.emf.internal.cdo.CDOInvalidationNotificationImpl;
import org.eclipse.emf.internal.cdo.CDONotificationBuilder;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
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
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

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
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl extends AbstractCDOView
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private int viewID;

  private InternalCDOSession session;

  private CDOFeatureAnalyzer featureAnalyzer = CDOFeatureAnalyzer.NOOP;

  private ChangeSubscriptionManager changeSubscriptionManager = createChangeSubscriptionManager();

  private AdapterManager adapterPolicyManager = createAdapterManager();

  private OptionsImpl options;

  private long lastUpdateTime;

  @ExcludeFromDump
  private Object lastUpdateTimeLock = new Object();

  /**
   * @since 2.0
   */
  public CDOViewImpl(CDOBranch branch, long timeStamp)
  {
    super(branch.getPoint(timeStamp), CDOUtil.isLegacyModeDefault());
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

  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
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

    List<InternalCDOObject> invalidObjects = getInvalidObjects(timeStamp);
    CDOSessionProtocol sessionProtocol = getSession().getSessionProtocol();
    OMMonitor monitor = new EclipseMonitor(new NullProgressMonitor());
    boolean[] existanceFlags = sessionProtocol.changeView(viewID, branchPoint, invalidObjects, monitor);

    basicSetBranchPoint(branchPoint);

    int i = 0;
    for (InternalCDOObject invalidObject : invalidObjects)
    {
      boolean existanceFlag = existanceFlags[i++];
      if (existanceFlag)
      {
        // --> PROXY
        CDOStateMachine.INSTANCE.invalidate(invalidObject, null, CDOBranchPoint.UNSPECIFIED_DATE);
      }
      else
      {
        // --> DETACHED
        CDOStateMachine.INSTANCE.detachRemote(invalidObject);
      }
    }

    clearRootResource();

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireViewTargetChangedEvent(listeners);
    }

    return true;
  }

  /**
   * @throws InterruptedException
   * @since 2.0
   */
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout)
      throws InterruptedException
  {
    checkActive();
    checkState(getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE, "Locking not supported for historial views");

    synchronized (session.getInvalidationLock())
    {
      getLock().lock();

      try
      {
        List<InternalCDORevision> revisions = new LinkedList<InternalCDORevision>();
        for (CDOObject object : objects)
        {
          if (!FSMUtil.isNew(object))
          {
            InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
            if (revision == null)
            {
              revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
            }

            revisions.add(revision);
          }
        }

        CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
        sessionProtocol.lockObjects(revisions, viewID, getBranch(), lockType, timeout);
      }
      finally
      {
        getLock().unlock();
      }
    }
  }

  /**
   * @since 2.0
   */
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    checkActive();
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    sessionProtocol.unlockObjects(this, objects, lockType);
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
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    return sessionProtocol.isObjectLocked(this, object, lockType, byOthers);
  }

  /**
   * @since 2.0
   */
  public CDOFeatureAnalyzer getFeatureAnalyzer()
  {
    return featureAnalyzer;
  }

  /**
   * @since 2.0
   */
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer)
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

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    InternalCDORevisionManager revisionManager = session.getRevisionManager();
    int initialChunkSize = session.options().getCollectionLoadingPolicy().getInitialChunkSize();
    CDOBranchPoint branchPoint = getBranchPointForID(id);
    return revisionManager.getRevision(id, branchPoint, initialChunkSize, CDORevision.DEPTH_NONE, loadOnDemand);
  }

  protected CDOBranchPoint getBranchPointForID(CDOID id)
  {
    // If this view's timestamp is something other than UNSPECIFIED_DATE,
    // then this is an 'audit' view, and so this timestamp must always be
    // used without any concern for possible sticky-view behavior
    //
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

  public void prefetchRevisions(CDOID id, int depth)
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

  /**
   * Turns registered objects into proxies and synchronously delivers invalidation events to registered event listeners.
   * <p>
   * Note that this method can block for an uncertain amount of time on the reentrant view lock!
   */
  public void invalidate(long lastUpdateTime, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects, Map<CDOID, InternalCDORevision> oldRevisions)
  {
    Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = null;
    List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>();
    Map<CDOObject, CDORevisionDelta> revisionDeltas = new HashMap<CDOObject, CDORevisionDelta>();
    Set<CDOObject> detachedObjects = new HashSet<CDOObject>();

    try
    {
      getLock().lock();
      conflicts = invalidate(lastUpdateTime, allChangedObjects, allDetachedObjects, deltas, revisionDeltas,
          detachedObjects);
    }
    finally
    {
      getLock().unlock();
    }

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
    setLastUpdateTime(lastUpdateTime);
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
  public void sendDeltaNotifications(List<CDORevisionDelta> deltas, Set<CDOObject> detachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions)
  {
    if (deltas != null)
    {
      CDONotificationBuilder builder = new CDONotificationBuilder(this);
      ConcurrentMap<CDOID, InternalCDOObject> objects = getObjects();
      for (CDORevisionDelta delta : deltas)
      {
        CDOID id = delta.getID();

        InternalCDOObject object;
        synchronized (objects)
        {
          object = objects.get(id);
        }

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
  protected ChangeSubscriptionManager createChangeSubscriptionManager()
  {
    return new ChangeSubscriptionManager(this);
  }

  /**
   * @since 2.0
   */
  public AdapterManager getAdapterManager()
  {
    return adapterPolicyManager;
  }

  /**
   * @since 2.0
   */
  protected AdapterManager createAdapterManager()
  {
    return new AdapterManager();
  }

  /**
   * @since 2.0
   */
  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    if (!FSMUtil.isNew(eObject))
    {
      subscribe(eObject, adapter);
    }

    adapterPolicyManager.attachAdapter(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter)
  {
    if (!FSMUtil.isNew(eObject))
    {
      unsubscribe(eObject, adapter);
    }

    adapterPolicyManager.detachAdapter(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public void subscribe(EObject eObject, Adapter adapter)
  {
    changeSubscriptionManager.subscribe(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public void unsubscribe(EObject eObject, Adapter adapter)
  {
    changeSubscriptionManager.unsubscribe(eObject, adapter);
  }

  /**
   * @since 2.0
   */
  public boolean hasSubscription(CDOID id)
  {
    return changeSubscriptionManager.getSubcribeObject(id) != null;
  }

  /**
   * @since 2.0
   */
  protected ChangeSubscriptionManager getChangeSubscriptionManager()
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
    sessionProtocol.openView(viewID, this, isReadOnly());
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
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
   * @author Simon McDuff
   * @since 2.0
   */
  protected class AdapterManager
  {
    private Set<CDOObject> objects = new HashBag<CDOObject>();

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

    protected synchronized void attachObject(CDOObject object)
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

    protected synchronized void detachObject(CDOObject object)
    {
      while (objects.remove(object))
      {
        // Do nothing
      }
    }

    protected synchronized void attachAdapter(CDOObject object, Adapter adapter)
    {
      if (options().getStrongReferencePolicy().isValid(object, adapter))
      {
        objects.add(object);
      }
    }

    protected synchronized void detachAdapter(CDOObject object, Adapter adapter)
    {
      if (options().getStrongReferencePolicy().isValid(object, adapter))
      {
        objects.remove(object);
      }
    }

    public synchronized void reset()
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
  protected static class ChangeSubscriptionManager
  {
    private CDOViewImpl view;

    private Map<CDOID, SubscribeEntry> subscriptions = new HashMap<CDOID, SubscribeEntry>();

    public ChangeSubscriptionManager(CDOViewImpl view)
    {
      this.view = view;
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      handleNewObjects(commitContext.getNewObjects().values());
      handleDetachedObjects(commitContext.getDetachedObjects().values());
    }

    public void subscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, 1);
    }

    public void unsubscribe(EObject eObject, Adapter adapter)
    {
      subscribe(eObject, adapter, -1);
    }

    /**
     * Register to the server all objects from the active list
     */
    protected void notifyChangeSubcriptionPolicy()
    {
      boolean policiesPresent = view.options().hasChangeSubscriptionPolicies();
      synchronized (subscriptions)
      {
        subscriptions.clear();
        List<CDOID> cdoIDs = new ArrayList<CDOID>();
        if (policiesPresent)
        {
          for (InternalCDOObject cdoObject : view.getObjectsArray())
          {
            int count = getNumberOfValidAdapter(cdoObject);
            if (count > 0)
            {
              cdoIDs.add(cdoObject.cdoID());
              addEntry(cdoObject.cdoID(), cdoObject, count);
            }
          }
        }

        request(cdoIDs, true, true);
      }
    }

    protected void handleDetachedObjects(Collection<CDOObject> detachedObjects)
    {
      synchronized (subscriptions)
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
    }

    protected void handleNewObjects(Collection<? extends CDOObject> newObjects)
    {
      synchronized (subscriptions)
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
    }

    protected InternalCDOObject getSubcribeObject(CDOID id)
    {
      synchronized (subscriptions)
      {
        SubscribeEntry entry = subscriptions.get(id);
        if (entry != null)
        {
          return entry.getObject();
        }
      }

      return null;
    }

    protected void request(List<CDOID> ids, boolean clear, boolean subscribeMode)
    {
      CDOSessionProtocol sessionProtocol = view.session.getSessionProtocol();
      int viewID = view.getViewID();
      sessionProtocol.changeSubscription(viewID, ids, subscribeMode, clear);
    }

    protected int getNumberOfValidAdapter(InternalCDOObject object)
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
      synchronized (subscriptions)
      {
        if (shouldSubscribe(eObject, adapter))
        {
          InternalCDOObject internalCDOObject = FSMUtil.adapt(eObject, view);
          if (internalCDOObject.cdoView() != view)
          {
            throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.27"), internalCDOObject)); //$NON-NLS-1$
          }

          subscribe(internalCDOObject.cdoID(), internalCDOObject, adjust);
        }
      }
    }

    private boolean shouldSubscribe(EObject eObject, Adapter adapter)
    {
      for (CDOAdapterPolicy policy : view.options().getChangeSubscriptionPolicies())
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
      boolean policiesPresent = view.options().hasChangeSubscriptionPolicies();
      synchronized (subscriptions)
      {
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
    }

    private void detachObject(CDOID id)
    {
      subscribe(id, null, Integer.MIN_VALUE);
    }

    private void addEntry(CDOID key, InternalCDOObject object, int count)
    {
      subscriptions.put(key, new SubscribeEntry(object, count));
    }

    /**
     * @author Eike Stepper
     */
    protected static final class SubscribeEntry
    {
      private int count;

      private InternalCDOObject object;

      public SubscribeEntry(InternalCDOObject object, int count)
      {
        this.count = count;
        this.object = object;
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
   * @author Eike Stepper
   * @since 2.0
   */
  protected class OptionsImpl extends Notifier implements Options
  {
    private boolean invalidationNotificationEnabled;

    private CDOInvalidationPolicy invalidationPolicy = CDOInvalidationPolicy.DEFAULT;

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
      return invalidationPolicy;
    }

    public void setInvalidationPolicy(CDOInvalidationPolicy policy)
    {
      if (invalidationPolicy != policy)
      {
        invalidationPolicy = policy;
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new InvalidationPolicyEventImpl(), listeners);
        }
      }
    }

    public boolean isInvalidationNotificationEnabled()
    {
      return invalidationNotificationEnabled;
    }

    public void setInvalidationNotificationEnabled(boolean enabled)
    {
      if (invalidationNotificationEnabled != enabled)
      {
        invalidationNotificationEnabled = enabled;
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new InvalidationNotificationEventImpl(), listeners);
        }
      }
    }

    public boolean hasChangeSubscriptionPolicies()
    {
      synchronized (changeSubscriptionPolicies)
      {
        return !changeSubscriptionPolicies.isEmpty();
      }
    }

    public CDOAdapterPolicy[] getChangeSubscriptionPolicies()
    {
      synchronized (changeSubscriptionPolicies)
      {
        return changeSubscriptionPolicies.toArray(new CDOAdapterPolicy[changeSubscriptionPolicies.size()]);
      }
    }

    public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      boolean changed = false;
      synchronized (changeSubscriptionPolicies)
      {
        if (changeSubscriptionPolicies.add(policy))
        {
          changeSubscriptionManager.notifyChangeSubcriptionPolicy();
          changed = true;
        }
      }

      if (changed)
      {
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new ChangeSubscriptionPoliciesEventImpl(), listeners);
        }
      }
    }

    public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy)
    {
      boolean changed = false;
      synchronized (changeSubscriptionPolicies)
      {
        if (changeSubscriptionPolicies.remove(policy) && !changeSubscriptionPolicies.contains(policy))
        {
          changeSubscriptionManager.notifyChangeSubcriptionPolicy();
          changed = true;
        }
      }

      if (changed)
      {
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new ChangeSubscriptionPoliciesEventImpl(), listeners);
        }
      }
    }

    public CDOAdapterPolicy getStrongReferencePolicy()
    {
      return strongReferencePolicy;
    }

    public void setStrongReferencePolicy(CDOAdapterPolicy adapterPolicy)
    {
      if (adapterPolicy == null)
      {
        adapterPolicy = CDOAdapterPolicy.ALL;
      }

      if (strongReferencePolicy != adapterPolicy)
      {
        strongReferencePolicy = adapterPolicy;
        adapterPolicyManager.reset();
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new ReferencePolicyEventImpl(), listeners);
        }
      }
    }

    public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy()
    {
      return revisionPrefetchingPolicy;
    }

    public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy)
    {
      if (prefetchingPolicy == null)
      {
        prefetchingPolicy = CDORevisionPrefetchingPolicy.NO_PREFETCHING;
      }

      if (revisionPrefetchingPolicy != prefetchingPolicy)
      {
        revisionPrefetchingPolicy = prefetchingPolicy;
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new RevisionPrefetchingPolicyEventImpl(), listeners);
        }
      }
    }

    public CDOStaleReferencePolicy getStaleReferenceBehaviour()
    {
      return staleReferencePolicy;
    }

    public void setStaleReferenceBehaviour(CDOStaleReferencePolicy policy)
    {
      if (policy == null)
      {
        policy = CDOStaleReferencePolicy.EXCEPTION;
      }

      if (staleReferencePolicy != policy)
      {
        staleReferencePolicy = policy;
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new StaleReferencePolicyEventImpl(), listeners);
        }
      }
    }

    public ReferenceType getCacheReferenceType()
    {
      ConcurrentMap<CDOID, InternalCDOObject> objects = getObjects();
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

    public boolean setCacheReferenceType(ReferenceType referenceType)
    {
      if (referenceType == null)
      {
        referenceType = ReferenceType.SOFT;
      }

      ConcurrentMap<CDOID, InternalCDOObject> objects = getObjects();
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
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new CacheReferenceTypeEventImpl(), listeners);
        }

        return true;
      }

      for (Entry<CDOID, InternalCDOObject> entry : objects.entrySet())
      {
        InternalCDOObject object = entry.getValue();
        if (object != null)
        {
          newObjects.put(entry.getKey(), object);
        }
      }

      ConcurrentMap<CDOID, InternalCDOObject> oldObjects = objects;
      synchronized (objects)
      {
        setObjects(newObjects);
      }

      oldObjects.clear();
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
