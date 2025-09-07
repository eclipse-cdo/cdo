/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019-2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Caspar De Groot - write options
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking2;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.concurrent.Access;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.core.runtime.PlatformObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Simon McDuff
 * @since 3.0
 */
public class LockingManager extends RWOLockManager<Object, IView> implements InternalLockManager
{
  private static final LockType[] ALL_LOCK_TYPES = LockType.values();

  private InternalRepository repository;

  private Map<String, InternalView> openDurableViews = new HashMap<>();

  private Map<String, DurableView> durableViews = Collections.synchronizedMap(new HashMap<>());

  private ConcurrentArray<DurableViewHandler> durableViewHandlers = new ConcurrentArray<DurableViewHandler>()
  {
    @Override
    protected DurableViewHandler[] newArray(int length)
    {
      return new DurableViewHandler[length];
    }
  };

  @ExcludeFromDump
  private transient IListener sessionManagerListener = new ContainerEventAdapter<ISession>()
  {
    @Override
    protected void onAdded(IContainer<ISession> container, ISession session)
    {
      session.addListener(sessionListener);
    }

    @Override
    protected void onRemoved(IContainer<ISession> container, ISession session)
    {
      session.removeListener(sessionListener);
    }
  };

  @ExcludeFromDump
  private transient IListener sessionListener = new ContainerEventAdapter<IView>()
  {
    @Override
    protected void onRemoved(IContainer<IView> container, IView view)
    {
      String durableLockingID = view.getDurableLockingID();
      if (durableLockingID == null)
      {
        repository.unlock((InternalView)view);
      }
      else
      {
        DurableView durableView = new DurableView(durableLockingID, view, view.isReadOnly());
        changeContext(view, durableView);
        unregisterOpenDurableView(durableLockingID, false);
        durableViews.put(durableLockingID, durableView);
      }
    }
  };

  private BiFunction<CDOID, CDOBranch, Object> lockKeyCreator;

  private Function<Object, CDOID> lockIDExtractor;

  private Function<Object, CDOBranch> lockBranchExtractor;

  public LockingManager()
  {
  }

  @Override
  public InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
  }

  @Override
  public Object getLockKey(CDOID id)
  {
    return getLockKey(id, null);
  }

  @Override
  public Object getLockKey(CDOID id, CDOBranch branch)
  {
    return lockKeyCreator.apply(id, branch);
  }

  @Override
  public CDOID getLockKeyID(Object key)
  {
    return lockIDExtractor.apply(key);
  }

  @Override
  public CDOBranch getLockKeyBranch(Object key)
  {
    return lockBranchExtractor.apply(key);
  }

  /**
   * @category Read Access
   */
  @Override
  public Map<CDOID, LockGrade> getLocks(final IView view)
  {
    final Map<CDOID, LockGrade> result = CDOIDUtil.createMap();

    try (Access access = read.access())
    {
      for (LockState<Object, IView> lockState : getObjectToLocksMap().values())
      {
        LockGrade grade = LockGrade.NONE;
        if (lockState.hasLock(LockType.READ, view, false))
        {
          grade = grade.getUpdated(LockType.READ, true);
        }

        if (lockState.hasLock(LockType.WRITE, view, false))
        {
          grade = grade.getUpdated(LockType.WRITE, true);
        }

        if (lockState.hasLock(LockType.OPTION, view, false))
        {
          grade = grade.getUpdated(LockType.OPTION, true);
        }

        if (grade != LockGrade.NONE)
        {
          CDOID id = getLockKeyID(lockState.getLockedObject());
          result.put(id, grade);
        }
      }
    }

    return result;
  }

  /**
   * Synchronized by the caller.
   */
  private Set<? extends Object> createContentSet(Collection<? extends Object> objectsToLock, IView view)
  {
    CDOBranch branch = view.getBranch();

    CDORevisionManager revisionManager = view.getSession().getRepository().getRevisionManager();
    CDORevisionProvider revisionProvider = new ManagedRevisionProvider(revisionManager, branch.getHead());

    Set<Object> contents = new HashSet<>();

    for (Object objectToLock : objectsToLock)
    {
      contents.add(objectToLock);

      CDOID id = getLockKeyID(objectToLock);
      CDORevision revision = revisionProvider.getRevision(id);
      createContentSet(branch, revisionProvider, revision, contents);
    }

    return contents;
  }

  /**
   * Synchronized by the caller.
   */
  private void createContentSet(CDOBranch branch, CDORevisionProvider revisionProvider, CDORevision revision, Set<Object> contents)
  {
    for (CDORevision child : CDORevisionUtil.getChildRevisions(revision, revisionProvider))
    {
      CDOID childID = child.getID();
      Object key = getLockKey(childID, branch);
      contents.add(key);
      createContentSet(branch, revisionProvider, child, contents);
    }
  }

  /**
   * @category Write Access
   */
  @Override
  public long lock(IView view, Collection<? extends Object> objects, LockType lockType, int count, long timeout, //
      boolean recursive, boolean explicit, //
      LockDeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler) //
      throws InterruptedException, TimeoutRuntimeException
  {
    long modCount;
    try (Access access = write.access())
    {
      if (recursive)
      {
        objects = createContentSet(objects, view);
      }

      modCount = super.lock(view, objects, lockType, count, timeout, deltaHandler, stateHandler);
    }

    if (explicit)
    {
      try
      {
        lockDurably(view, objects, lockType);
      }
      catch (Exception | Error ex)
      {
        super.unlock(view, objects, lockType, count, null, null);
        throw ex;
      }
    }

    return modCount;
  }

  private void lockDurably(IView view, Collection<? extends Object> objects, LockType lockType)
  {
    String durableLockingID = view.getDurableLockingID();
    if (durableLockingID != null)
    {
      DurableLocking accessor = getDurableLocking();
      accessor.lock(durableLockingID, lockType, objects);
    }
  }

  /**
   * @category Write Access
   */
  @Override
  public long unlock(IView view, Collection<? extends Object> objects, LockType lockType, int count, //
      boolean recursive, boolean explicit, //
      LockDeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler)
  {
    long modCount;
    try (Access access = write.access())
    {
      if (recursive)
      {
        objects = createContentSet(objects, view);
      }

      modCount = super.unlock(view, objects, lockType, count, deltaHandler, stateHandler);
    }

    if (explicit)
    {
      unlockDurably(view, objects, lockType);
    }

    return modCount;
  }

  private void unlockDurably(IView view, Collection<? extends Object> objects, LockType lockType)
  {
    String durableLockingID = view.getDurableLockingID();
    if (durableLockingID != null)
    {
      DurableLocking accessor = getDurableLocking();
      accessor.unlock(durableLockingID, lockType, objects);
    }
  }

  @Override
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    return createLockArea(userID, branchPoint, readOnly, locks, null);
  }

  private LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks, String lockAreaID)
  {
    if (lockAreaID == null)
    {
      DurableLocking accessor = getDurableLocking();
      return accessor.createLockArea(userID, branchPoint, readOnly, locks);
    }

    DurableLocking2 accessor = getDurableLocking2();
    return accessor.createLockArea(lockAreaID, userID, branchPoint, readOnly, locks);
  }

  @Override
  public LockArea createLockArea(InternalView view)
  {
    return createLockArea(view, null);
  }

  @Override
  public LockArea createLockArea(InternalView view, String lockAreaID)
  {
    String userID = view.getSession().getUserID();
    CDOBranchPoint branchPoint = CDOBranchUtil.copyBranchPoint(view);
    boolean readOnly = view.isReadOnly();
    Map<CDOID, LockGrade> locks = getLocks(view);

    LockArea area = createLockArea(userID, branchPoint, readOnly, locks, lockAreaID);
    synchronized (openDurableViews)
    {
      openDurableViews.put(area.getDurableLockingID(), view);
    }

    return area;
  }

  @Override
  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    DurableLocking accessor = getDurableLocking();
    return accessor.getLockArea(durableLockingID);
  }

  @Override
  public void getLockAreas(String userIDPrefix, LockArea.Handler handler)
  {
    if (userIDPrefix == null)
    {
      userIDPrefix = "";
    }

    DurableLocking accessor = getDurableLocking();
    accessor.getLockAreas(userIDPrefix, handler);
  }

  @Override
  public void deleteLockArea(String durableLockingID)
  {
    DurableLocking accessor = getDurableLocking();
    accessor.deleteLockArea(durableLockingID);

    unregisterOpenDurableView(durableLockingID, true);
  }

  @Override
  public void openView(ISession session, int viewID, boolean readOnly, String durableLockingID, Consumer<IView> viewConsumer,
      BiConsumer<CDOID, LockGrade> lockConsumer)
  {
    synchronized (openDurableViews)
    {
      InternalView view = openDurableViews.get(durableLockingID);
      if (view != null)
      {
        throw new IllegalStateException("Durable view is already open: " + view);
      }

      LockArea area = getLockArea(durableLockingID);
      if (area.isReadOnly() != readOnly)
      {
        throw new IllegalStateException("Durable read-only state does not match the request");
      }

      for (DurableViewHandler handler : durableViewHandlers.get())
      {
        try
        {
          handler.openingView(session, viewID, readOnly, area);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      if (readOnly)
      {
        view = (InternalView)session.openView(viewID, area, durableLockingID);
      }
      else
      {
        view = (InternalView)session.openTransaction(viewID, area, durableLockingID);
      }

      DurableView durableView = durableViews.get(durableLockingID);
      changeContext(durableView, view);

      view.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          synchronized (openDurableViews)
          {
            openDurableViews.remove(durableLockingID);
          }
        }
      });

      openDurableViews.put(durableLockingID, view);
      viewConsumer.accept(view);

      if (lockConsumer != null)
      {
        for (Map.Entry<CDOID, LockGrade> entry : area.getLocks().entrySet())
        {
          CDOID id = entry.getKey();
          LockGrade lockGrade = entry.getValue();
          lockConsumer.accept(id, lockGrade);
        }
      }
    }
  }

  @Override
  protected void changeContext(IView oldContext, IView newContext)
  {
    super.changeContext(oldContext, newContext);

    InternalSession session = (InternalSession)oldContext.getSession();
    if (session == null)
    {
      session = (InternalSession)newContext.getSession();
    }

    CDOBranch branch = oldContext.getBranch();
    if (branch == null)
    {
      branch = newContext.getBranch();
    }

    CDOLockOwner oldOwner = oldContext.getLockOwner();
    CDOLockOwner newOwner = newContext.getLockOwner();

    repository.getSessionManager().sendLockOwnerRemappedNotification(session, branch, oldOwner, newOwner);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    InternalCDOBranch mainBranch = repository.getBranchManager().getMainBranch();

    if (repository.isSupportingBranches())
    {
      lockKeyCreator = (id, branch) -> CDOIDUtil.createIDAndBranch(id, branch == null ? mainBranch : branch);
      lockIDExtractor = key -> ((CDOIDAndBranch)key).getID();
      lockBranchExtractor = key -> ((CDOIDAndBranch)key).getBranch();
    }
    else
    {
      lockKeyCreator = (id, branch) -> id;
      lockIDExtractor = key -> (CDOID)key;
      lockBranchExtractor = key -> mainBranch;
    }

    loadLocks();

    InternalSessionManager sessionManager = repository.getSessionManager();
    sessionManager.addListener(sessionManagerListener);

    for (ISession session : sessionManager.getSessions())
    {
      session.addListener(sessionListener);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    ISessionManager sessionManager = repository.getSessionManager();
    sessionManager.removeListener(sessionManagerListener);

    for (ISession session : sessionManager.getSessions())
    {
      session.removeListener(sessionListener);
    }

    super.doDeactivate();
  }

  private DurableLocking getDurableLocking()
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (accessor instanceof DurableLocking)
    {
      return (DurableLocking)accessor;
    }

    throw new IllegalStateException("Store does not implement " + DurableLocking.class.getSimpleName());
  }

  private DurableLocking2 getDurableLocking2()
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (accessor instanceof DurableLocking2)
    {
      return (DurableLocking2)accessor;
    }

    throw new IllegalStateException("Store does not implement " + DurableLocking2.class.getSimpleName());
  }

  @Override
  public Set<IView> getLockOwners(Object key, LockType... lockTypes)
  {
    LockState<Object, IView> lockState = getLockState(key);
    if (lockState == null)
    {
      return Collections.emptySet();
    }

    Set<IView> result = new HashSet<>();

    if (lockTypes.length == 0)
    {
      lockTypes = ALL_LOCK_TYPES;
    }

    for (LockType lockType : lockTypes)
    {
      switch (lockType)
      {
      case READ:
        result.addAll(lockState.getReadLockOwners());
        break;

      case WRITE:
        CollectionUtil.addNotNull(result, lockState.getWriteLockOwner());
        break;

      case OPTION:
        CollectionUtil.addNotNull(result, lockState.getWriteOptionOwner());
        break;
      }
    }

    return result;
  }

  @Override
  public void reloadLocks()
  {
    DurableLockLoader handler = new DurableLockLoader();
    getLockAreas(null, handler);
  }

  private void loadLocks()
  {
    InternalStore store = repository.getStore();
    IStoreAccessor reader = null;

    try
    {
      reader = store.getReader(null);
      if (reader instanceof DurableLocking)
      {
        StoreThreadLocal.setAccessor(reader);
        reloadLocks();
      }
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  private void unregisterOpenDurableView(String durableLockingID, boolean notifyOtherSessions)
  {
    InternalView view;
    CDOLockOwner oldOwner = null;

    synchronized (openDurableViews)
    {
      view = openDurableViews.remove(durableLockingID);
      if (view != null)
      {
        oldOwner = view.getLockOwner();
        view.setDurableLockingID(null);
      }
    }

    if (notifyOtherSessions && view != null)
    {
      CDOLockOwner newOwner = view.getLockOwner();
      if (newOwner != oldOwner)
      {
        CDOBranch branch = view.getBranch();
        InternalSession session = view.getSession();
        InternalSessionManager manager = session.getManager();
        manager.sendLockOwnerRemappedNotification(session, branch, oldOwner, newOwner);
      }
    }
  }

  @Override
  public void addDurableViewHandler(DurableViewHandler handler)
  {
    durableViewHandlers.add(handler);
  }

  @Override
  public void removeDurableViewHandler(DurableViewHandler handler)
  {
    durableViewHandlers.remove(handler);
  }

  @Override
  public DurableViewHandler[] getDurableViewHandlers()
  {
    return durableViewHandlers.get();
  }

  /**
   * @category Read Access
   */
  @Override
  public LockGrade getLockGrade(Object key)
  {
    try (Access access = read.access())
    {
      LockState<Object, IView> lockState = getObjectToLocksMap().get(key);
      LockGrade grade = LockGrade.NONE;
      if (lockState != null)
      {
        for (LockType type : ALL_LOCK_TYPES)
        {
          if (lockState.hasLock(type))
          {
            grade = grade.getUpdated(type, true);
          }
        }
      }

      return grade;
    }
  }

  private LockArea getLockAreaNoEx(String durableLockingID)
  {
    try
    {
      return getLockArea(durableLockingID);
    }
    catch (LockAreaNotFoundException e)
    {
      return null;
    }
  }

  @Override
  public void updateLockArea(LockArea lockArea)
  {
    String durableLockingID = lockArea.getDurableLockingID();
    DurableLocking2 accessor = getDurableLocking2();

    if (lockArea.isMissing())
    {
      LockArea localLockArea = getLockAreaNoEx(durableLockingID);
      if (localLockArea != null && localLockArea.getLocks().size() > 0)
      {
        accessor.deleteLockArea(durableLockingID);
        DurableView deletedView = durableViews.remove(durableLockingID);
        CheckUtil.checkNull(deletedView, "deletedView");
      }
    }
    else
    {
      accessor.updateLockArea(lockArea);
      new DurableLockLoader().handleLockArea(lockArea);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DurableView extends PlatformObject implements IView, CDOCommonView.Options
  {
    private final String durableLockingID;

    private final CDOBranchPoint branchPoint;

    private final boolean readOnly;

    private final CDOLockOwner lockOwner;

    private IRegistry<String, Object> properties;

    public DurableView(String durableLockingID, CDOBranchPoint branchPoint, boolean readOnly)
    {
      this.durableLockingID = durableLockingID;
      this.readOnly = readOnly;
      this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
      lockOwner = CDOLockUtil.createLockOwner(this);
    }

    @Override
    public String getDurableLockingID()
    {
      return durableLockingID;
    }

    @Override
    public boolean isDurableView()
    {
      return true;
    }

    @Override
    public CDOLockOwner getLockOwner()
    {
      return lockOwner;
    }

    @Override
    public int getSessionID()
    {
      return CDOLockUtil.DURABLE_SESSION_ID;
    }

    @Override
    public int getViewID()
    {
      return CDOLockUtil.DURABLE_VIEW_ID;
    }

    @Override
    public boolean isReadOnly()
    {
      return readOnly;
    }

    @Override
    public boolean isHistorical()
    {
      return branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
    }

    @Override
    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    @Override
    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    @Override
    public CDORevision getRevision(CDOID id)
    {
      CDORevisionManager revisionManager = getRepository().getRevisionManager();
      return revisionManager.getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
    }

    @Override
    public void close()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed()
    {
      return false;
    }

    @Override
    public IRepository getRepository()
    {
      return LockingManager.this.getRepository();
    }

    @Override
    public ISession getSession()
    {
      return null;
    }

    @Override
    public void loadLob(CDOLobInfo info, Object outputStreamOrWriter) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode()
    {
      return durableLockingID.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (obj instanceof DurableView)
      {
        DurableView that = (DurableView)obj;
        return durableLockingID.equals(that.getDurableLockingID());
      }

      return false;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("DurableView[0:0:{0}]", durableLockingID);
    }

    @Override
    public IOptionsContainer getContainer()
    {
      return this;
    }

    @Override
    public void addListener(IListener listener)
    {
    }

    @Override
    public void removeListener(IListener listener)
    {
    }

    @Override
    public boolean hasListeners()
    {
      return false;
    }

    @Override
    public IListener[] getListeners()
    {
      return EventUtil.NO_LISTENERS;
    }

    @Override
    public Options options()
    {
      return this;
    }

    @Override
    public synchronized IRegistry<String, Object> properties()
    {
      if (properties == null)
      {
        properties = new HashMapRegistry.AutoCommit<>();
      }

      return properties;
    }

    @Override
    public boolean isLockNotificationEnabled()
    {
      return false;
    }

    @Override
    public void setLockNotificationEnabled(boolean enabled)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DurableLockLoader implements LockArea.Handler
  {
    public DurableLockLoader()
    {
    }

    private IView getView(String lockAreaID)
    {
      IView view = openDurableViews.get(lockAreaID);
      if (view == null)
      {
        view = durableViews.get(lockAreaID);
      }

      return view;
    }

    @Override
    public boolean handleLockArea(LockArea area)
    {
      String durableLockingID = area.getDurableLockingID();

      IView view = getView(durableLockingID);
      if (view != null)
      {
        LockingManager.super.unlock(view, null, null, ALL_LOCKS, null, null);
      }
      else
      {
        view = new DurableView(durableLockingID, area, area.isReadOnly());
        durableViews.put(durableLockingID, (DurableView)view);
      }

      Collection<Object> readLocks = new ArrayList<>();
      Collection<Object> writeLocks = new ArrayList<>();
      Collection<Object> writeOptions = new ArrayList<>();
      for (Map.Entry<CDOID, LockGrade> entry : area.getLocks().entrySet())
      {
        Object key = getLockKey(entry.getKey(), area.getBranch());
        LockGrade grade = entry.getValue();
        if (grade.isRead())
        {
          readLocks.add(key);
        }

        if (grade.isWrite())
        {
          writeLocks.add(key);
        }

        if (grade.isOption())
        {
          writeOptions.add(key);
        }
      }

      try
      {
        LockingManager.super.lock(view, readLocks, LockType.READ, 1, 1000L, null, null);
        LockingManager.super.lock(view, writeLocks, LockType.WRITE, 1, 1000L, null, null);
        LockingManager.super.lock(view, writeOptions, LockType.OPTION, 1, 1000L, null, null);
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }

      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LockDeltaCollector extends ArrayList<CDOLockDelta> implements LockDeltaHandler<Object, IView>
  {
    private static final long serialVersionUID = 1L;

    private Operation operation;

    public LockDeltaCollector(Operation operation)
    {
      this.operation = operation;
    }

    public LockDeltaCollector()
    {
    }

    public Operation getOperation()
    {
      return operation;
    }

    public void setOperation(Operation operation)
    {
      this.operation = operation;
    }

    @Override
    public void handleLockDelta(IView context, Object object, LockType lockType, int oldCount, int newCount)
    {
      switch (operation)
      {
      case LOCK:
        if (oldCount == 0 && newCount > 0)
        {
          CDOLockOwner lockOwner = context.getLockOwner();
          CDOLockDelta lockDelta = CDOLockUtil.createLockDelta(object, lockType, null, lockOwner);
          add(lockDelta);
        }
        break;

      case UNLOCK:
        if (oldCount > 0 && newCount == 0)
        {
          CDOLockOwner lockOwner = context.getLockOwner();
          CDOLockDelta lockDelta = CDOLockUtil.createLockDelta(object, lockType, lockOwner, null);
          add(lockDelta);
        }
        break;

      default:
        throw new AssertionError();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LockStateCollector extends ArrayList<CDOLockState> implements Consumer<LockState<Object, IView>>
  {
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(LockState<Object, IView> serverLockState)
    {
      CDOLockState commonLockState = CDOLockUtil.convertLockState(serverLockState);
      add(commonLockState);
    }
  }

  /**
   * @deprecated
   * @category Read Access
   */
  @Deprecated
  @Override
  public Object getLockEntryObject(Object key)
  {
    try (Access access = read.access())
    {
      LockState<Object, IView> lockState = getObjectToLocksMap().get(key);
      return lockState == null ? null : lockState.getLockedObject();
    }
  }

  @Override
  @Deprecated
  public void lock(boolean explicit, LockType type, IView view, Collection<? extends Object> objectsToLock, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(boolean explicit, LockType type, IView view, Collection<? extends Object> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(boolean explicit, IView view)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void lock(LockType type, IView view, Collection<? extends Object> objectsToLock, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void lock(LockType type, IView view, Object objectToLock, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<Object, IView>> lock2(LockType type, IView view, Collection<? extends Object> objectsToLock, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<Object, IView>> lock2(boolean explicit, LockType type, IView view, Collection<? extends Object> objectsToLock, boolean recursive,
      long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(IView view)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(LockType type, IView view, Collection<? extends Object> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<Object, IView>> unlock2(boolean explicit, LockType type, IView view, Collection<? extends Object> objects, boolean recursive)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<Object, IView>> unlock2(boolean explicit, IView view)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<RWOLockManager.LockState<Object, IView>> unlock2(IView view)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<RWOLockManager.LockState<Object, IView>> unlock2(IView view, Collection<? extends Object> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<RWOLockManager.LockState<Object, IView>> unlock2(LockType type, IView view, Collection<? extends Object> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IView openView(ISession session, int viewID, boolean readOnly, String durableLockingID)
  {
    throw new UnsupportedOperationException();
  }
}
