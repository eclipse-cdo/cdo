/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockState;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.CollectionUtil.KeepMappedValue;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.spi.cdo.CDOLockStateCache;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public final class CDOLockStateCacheImpl extends Lifecycle implements CDOLockStateCache
{
  private static final Class<CDOLockOwner[]> ARRAY_CLASS = CDOLockOwner[].class;

  private static final boolean DEBUG = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.internal.cdo.session.CDOLockStateCacheImpl.DEBUG");

  private static final boolean DEBUG_STACK_TRACE = //
      OMPlatform.INSTANCE.isProperty("org.eclipse.emf.internal.cdo.session.CDOLockStateCacheImpl.DEBUG_STACK_TRACE");

  private final Map<CDOBranch, ConcurrentMap<CDOID, OwnerInfo>> ownerInfosPerBranch = new HashMap<>();

  private final ConcurrentMap<CDOID, OwnerInfo> ownerInfosOfMainBranch = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  private final ConcurrentMap<CDOLockOwner, SingleOwnerInfo>[] singleOwnerInfos = new ConcurrentMap[] { //
      new ConcurrentHashMap<>(), // SingleOwnerInfo.ReadLock.TYPE
      new ConcurrentHashMap<>(), // SingleOwnerInfo.ReadLockAndWriteLock.TYPE
      new ConcurrentHashMap<>(), // SingleOwnerInfo.ReadLockAndWriteOption.TYPE
      new ConcurrentHashMap<>(), // SingleOwnerInfo.WriteLock.TYPE
      new ConcurrentHashMap<>() // SingleOwnerInfo.WriteOption.TYPE
  };

  private final InternalCDOSession session;

  private final IListener sessionListener = new ContainerEventAdapter<CDOView>()
  {
    @Override
    protected void onAdded(IContainer<CDOView> container, CDOView view)
    {
      viewAdded(view);
    }

    @Override
    protected void onRemoved(IContainer<CDOView> container, CDOView view)
    {
      viewRemoved(view);
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  private final IListener viewListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewTargetChangedEvent)
      {
        CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
        viewTargetChanged(e.getSource(), e.getOldBranchPoint(), e.getBranchPoint());
      }
    }
  };

  private final boolean branching;

  private final CDOBranch mainBranch;

  private final HashBag<CDOBranch> branches = new HashBag<>();

  public CDOLockStateCacheImpl(CDOSession session)
  {
    this.session = (InternalCDOSession)session;

    branching = session.getRepositoryInfo().isSupportingBranches();
    mainBranch = session.getBranchManager().getMainBranch();
    ownerInfosPerBranch.put(mainBranch, ownerInfosOfMainBranch);

    activate();
  }

  @Override
  public InternalCDOSession getSession()
  {
    return session;
  }

  @Override
  public Object createKey(CDOBranch branch, CDOID id)
  {
    if (branching)
    {
      return CDOIDUtil.createIDAndBranch(id, branch);
    }

    return id;
  }

  @Override
  public CDOLockState getLockState(CDOBranch branch, CDOID id)
  {
    Object key = createKey(branch, id);
    return new LockState(key, this);
  }

  @Override
  public void getLockStates(CDOBranch branch, Collection<CDOID> ids, boolean loadOnDemand, Consumer<CDOLockState> consumer)
  {
    if (ids == null)
    {
      if (loadOnDemand)
      {
        loadLockStates(branch, null, consumer);
      }
      else
      {
        collectLockStates(branch, ids, null, consumer);
      }
    }
    else
    {
      Set<CDOID> missingIDs = loadOnDemand ? new HashSet<>() : null;
      collectLockStates(branch, ids, missingIDs, consumer);

      if (!ObjectUtil.isEmpty(missingIDs))
      {
        loadLockStates(branch, missingIDs, lockState -> {
          missingIDs.remove(lockState.getID());
          consumer.accept(lockState);
        });

        int size = missingIDs.size();
        if (size != 0)
        {
          List<CDOLockState> defaultLockStatesToCache = new ArrayList<>(size);

          for (CDOID id : missingIDs)
          {
            CDOLockState defaultLockStateToCache = getLockState(branch, id);
            defaultLockStatesToCache.add(defaultLockStateToCache);
          }

          addLockStates(branch, defaultLockStatesToCache, consumer);
        }
      }
    }
  }

  @Override
  public void forEachLockState(CDOBranch branch, CDOLockOwner owner, Consumer<CDOLockState> consumer)
  {
    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
    infos.forEach((id, info) -> {
      if (info.isLocked(null, owner, false))
      {
        notifyConsumer(branch, id, consumer);
      }
    });
  }

  @Override
  public synchronized void updateLockStates(CDOBranch branch, Collection<CDOLockDelta> lockDeltas, Collection<CDOLockState> lockStates,
      Consumer<CDOLockState> consumer)
  {
    try
    {
      ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);

      delta_loop: for (CDOLockDelta delta : lockDeltas)
      {
        CDOID id = delta.getID();

        OwnerInfo info = null;
        OwnerInfo newInfo = null;

        for (int i = 50; i >= 0; --i)
        {
          info = infos.get(id);
          if (info == null || delta.getType() == null)
          {
            for (CDOLockState lockState : lockStates)
            {
              if (lockState.getID() == id)
              {
                addLockState(infos, lockState, consumer);
                continue delta_loop;
              }
            }

            continue delta_loop;
          }

          try
          {
            newInfo = info.applyDelta(this, delta);
            break;
          }
          catch (ObjectAlreadyLockedException ex)
          {
            if (i == 0)
            {
              throw new ObjectAlreadyLockedException(id, branch, ex);
            }

            try
            {
              wait(100);
            }
            catch (InterruptedException ex1)
            {
              throw new Error(ex1);
            }
          }
        }

        if (newInfo != info)
        {
          trace(id, info, newInfo);
          infos.put(id, newInfo);
        }

        if (consumer != null)
        {
          Object target = delta.getTarget();
          CDOLockState lockState = new LockState(target, this);
          consumer.accept(lockState);
        }
      }
    }
    finally
    {
      notifyAll();
    }
  }

  @Override
  public void addLockStates(CDOBranch branch, Collection<? extends CDOLockState> lockStates, Consumer<CDOLockState> consumer)
  {
    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);

    for (CDOLockState lockState : lockStates)
    {
      try
      {
        addLockState(infos, lockState, consumer);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  @Override
  public List<CDOLockDelta> removeOwner(CDOBranch branch, CDOLockOwner owner, Consumer<CDOLockState> consumer)
  {
    List<CDOLockDelta> deltas = new ArrayList<>();
    List<Pair<CDOID, OwnerInfo>> newInfos = new ArrayList<>();

    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
    infos.forEach((id, info) -> {
      OwnerInfo newInfo = info.removeOwner(this, owner, lockType -> appendRemoveDelta(deltas, branch, id, lockType, owner));
      if (newInfo != info)
      {
        newInfos.add(Pair.create(id, newInfo));
        notifyConsumer(branch, id, consumer);
      }
    });

    for (Pair<CDOID, OwnerInfo> pair : newInfos)
    {
      CDOID id = pair.getElement1();
      OwnerInfo newInfo = pair.getElement2();

      OwnerInfo oldInfo = infos.put(id, newInfo);
      trace(id, oldInfo, newInfo);
    }

    return deltas;
  }

  @Override
  public void remapOwner(CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    for (int type = 0; type < singleOwnerInfos.length; type++)
    {
      for (SingleOwnerInfo info : singleOwnerInfos[type].values())
      {
        remapOwnerInfo(info, oldOwner, newOwner);
      }
    }

    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
    for (OwnerInfo info : infos.values())
    {
      remapOwnerInfo(info, oldOwner, newOwner);
    }
  }

  @Override
  public void removeLockStates(CDOBranch branch, Collection<CDOID> ids, Consumer<CDOLockState> consumer)
  {
    if (ObjectUtil.isEmpty(ids))
    {
      return;
    }

    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);

    for (CDOID id : ids)
    {
      OwnerInfo info = infos.remove(id);
      if (info != null)
      {
        notifyConsumer(branch, id, consumer);
      }
    }
  }

  @Override
  public void removeLockStates(CDOBranch branch)
  {
    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
    infos.clear();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    session.addListener(sessionListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    session.removeListener(sessionListener);
    super.doDeactivate();
  }

  private void viewAdded(CDOView view)
  {
    CDOLockOwner owner = view.getLockOwner();
    addSingleOwnerInfos(owner, -1);

    CDOBranch branch = view.getBranch();
    addBranch(branch);

    view.addListener(viewListener);
  }

  private void viewRemoved(CDOView view)
  {
    view.removeListener(viewListener);

    CDOBranch branch = view.getBranch();
    removeBranch(branch);

    CDOLockOwner owner = view.getLockOwner();
    removeSingleOwnerInfos(owner);
  }

  private void viewTargetChanged(CDOView view, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
  {
    CDOBranch oldBranch = oldBranchPoint.getBranch();
    CDOBranch newBranch = newBranchPoint.getBranch();

    if (newBranch != oldBranch)
    {
      removeBranch(oldBranch);
      addBranch(newBranch);
    }
  }

  private void addBranch(CDOBranch branch)
  {
    synchronized (branches)
    {
      if (branches.addAndGet(branch, 1) == 1)
      {
        if (!branch.isMainBranch())
        {
          synchronized (ownerInfosPerBranch)
          {
            ownerInfosPerBranch.put(branch, new ConcurrentHashMap<>());
          }
        }
      }
    }
  }

  private void removeBranch(CDOBranch branch)
  {
    synchronized (branches)
    {
      if (branches.removeAndGet(branch, 1) == 0)
      {
        if (!branch.isMainBranch())
        {
          synchronized (ownerInfosPerBranch)
          {
            ownerInfosPerBranch.remove(branch);
          }
        }
      }
    }
  }

  private SingleOwnerInfo addSingleOwnerInfos(CDOLockOwner owner, int typeToReturn)
  {
    SingleOwnerInfo infoToReturn = null;

    for (int type = 0; type < singleOwnerInfos.length; type++)
    {
      int finalType = type;

      // Don't overwrite existing infos. Infos can exist if the owning view is durable.
      SingleOwnerInfo info = singleOwnerInfos[type].computeIfAbsent(owner, o -> SingleOwnerInfo.create(o, finalType));

      if (type == typeToReturn)
      {
        infoToReturn = info;
      }
    }

    return infoToReturn;
  }

  private void removeSingleOwnerInfos(CDOLockOwner owner)
  {
    for (int type = 0; type < singleOwnerInfos.length; type++)
    {
      singleOwnerInfos[type].remove(owner);
    }
  }

  private <T> T withKey(Object key, BiFunction<CDOBranch, CDOID, T> function)
  {
    CDOBranch branch;
    CDOID id;

    if (branching)
    {
      CDOIDAndBranch idAndBranch = (CDOIDAndBranch)key;
      branch = idAndBranch.getBranch();
      id = idAndBranch.getID();
    }
    else
    {
      branch = mainBranch;
      id = (CDOID)key;
    }

    return function.apply(branch, id);
  }

  private void notifyConsumer(CDOBranch branch, CDOID id, Consumer<CDOLockState> consumer)
  {
    if (consumer != null)
    {
      CDOLockState lockState = getLockState(branch, id);
      consumer.accept(lockState);
    }
  }

  private SingleOwnerInfo getSingleOwnerInfo(CDOLockOwner owner, int type)
  {
    SingleOwnerInfo info = singleOwnerInfos[type].get(owner);
    if (info == null)
    {
      info = addSingleOwnerInfos(owner, type);
    }

    return info;
  }

  private ConcurrentMap<CDOID, OwnerInfo> getOwnerInfoMap(CDOBranch branch)
  {
    if (branch.isMainBranch())
    {
      return ownerInfosOfMainBranch;
    }

    synchronized (ownerInfosPerBranch)
    {
      return ownerInfosPerBranch.get(branch);
    }
  }

  @SuppressWarnings("unchecked")
  private Map.Entry<CDOBranch, ConcurrentMap<CDOID, OwnerInfo>>[] getOwnerInfoMapEntries()
  {
    synchronized (ownerInfosPerBranch)
    {
      return ownerInfosPerBranch.entrySet().toArray(new Map.Entry[ownerInfosPerBranch.size()]);
    }
  }

  private OwnerInfo getOwnerInfo(Object key)
  {
    return withKey(key, (branch, id) -> {
      ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
      return getOwnerInfo(infos, branch, id);
    });
  }

  private OwnerInfo getOwnerInfo(ConcurrentMap<CDOID, OwnerInfo> infos, CDOBranch branch, CDOID id)
  {
    OwnerInfo info = infos.get(id);
    if (info == null)
    {
      return NoOwnerInfo.INSTANCE;
    }

    return info;
  }

  private boolean changeOwnerInfo(Object key, CDOLockOwner owner, BiFunction<OwnerInfo, CDOLockOwner, OwnerInfo> changer)
  {
    return withKey(key, (branch, id) -> {
      boolean[] changed = { false };

      ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
      CollectionUtil.compute(infos, id, (unused, info) -> {
        if (info == null)
        {
          info = NoOwnerInfo.INSTANCE;
        }

        OwnerInfo newInfo = changer.apply(info, owner);
        if (newInfo != info)
        {
          trace(id, info, newInfo);
          changed[0] = true;
          return newInfo;
        }

        throw new KeepMappedValue(info);
      });

      return changed[0];
    });
  }

  private void remapOwnerInfo(OwnerInfo info, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    if (info.remapOwner(oldOwner, newOwner) && DEBUG)
    {
      IOUtil.OUT().println("Remap owner: " + info);
    }
  }

  private void collectLockStates(CDOBranch branch, Collection<CDOID> ids, Set<CDOID> missingIDs, Consumer<CDOLockState> consumer)
  {
    if (branch != null)
    {
      ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
      collectLockStates(branch, ids, infos, missingIDs, consumer);
    }
    else
    {
      for (Map.Entry<CDOBranch, ConcurrentMap<CDOID, OwnerInfo>> entry : getOwnerInfoMapEntries())
      {
        CDOBranch infosBranch = entry.getKey();
        ConcurrentMap<CDOID, OwnerInfo> infos = entry.getValue();
        collectLockStates(infosBranch, ids, infos, missingIDs, consumer);
      }
    }
  }

  private void collectLockStates(CDOBranch branch, Collection<CDOID> ids, ConcurrentMap<CDOID, OwnerInfo> infos, //
      Set<CDOID> missingIDs, Consumer<CDOLockState> consumer)
  {
    for (CDOID id : ids)
    {
      OwnerInfo info = infos.get(id);
      if (info != null)
      {
        notifyConsumer(branch, id, consumer);
      }
      else if (missingIDs != null)
      {
        missingIDs.add(id);
      }
    }
  }

  private void loadLockStates(CDOBranch branch, Set<CDOID> ids, Consumer<CDOLockState> consumer)
  {
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    List<CDOLockState> lockStates = sessionProtocol.getLockStates2(branch.getID(), ids, CDOLockState.DEPTH_NONE);

    addLockStates(branch, lockStates, consumer);
  }

  private void addLockState(ConcurrentMap<CDOID, OwnerInfo> infos, CDOLockState lockState, Consumer<CDOLockState> consumer)
  {
    CDOID id = lockState.getID();
    OwnerInfo info = createOwnerInfo(lockState);

    trace(id, null, info);
    infos.put(id, info);

    if (consumer != null)
    {
      consumer.accept(lockState);
    }
  }

  private OwnerInfo createOwnerInfo(CDOLockState lockState)
  {
    return updateOwnerInfo(NoOwnerInfo.INSTANCE, lockState);
  }

  private OwnerInfo updateOwnerInfo(OwnerInfo info, CDOLockState lockState)
  {
    try
    {
      for (CDOLockOwner readLockOwner : lockState.getReadLockOwners())
      {
        info = info.addReadLockOwner(this, readLockOwner);
      }

      info = info.addWriteLockOwner(this, lockState.getWriteLockOwner());
      info = info.addWriteOptionOwner(this, lockState.getWriteOptionOwner());
    }
    catch (ObjectAlreadyLockedException ex)
    {
      CDOBranch branch = lockState.getBranch();
      if (branch == null)
      {
        branch = mainBranch;
      }

      throw new ObjectAlreadyLockedException(lockState.getID(), branch, ex);
    }
    catch (RuntimeException | Error ex)
    {
      throw new ImplementationError("Could not modify " + info + " of " + lockState.getLockedObject(), ex);
    }

    return info;
  }

  private boolean appendRemoveDelta(List<CDOLockDelta> deltas, CDOBranch branch, CDOID id, LockType lockType, CDOLockOwner owner)
  {
    return deltas.add(CDOLockUtil.createLockDelta(createKey(branch, id), lockType, owner, null));
  }

  private static boolean isOwnerInfoLocked(OwnerInfo info, LockType lockType, CDOLockOwner lockOwner, boolean others)
  {
    if (lockType == null)
    {
      return isOwnerInfoReadLocked(info, lockOwner, others) //
          || isOwnerInfoWriteLocked(info, lockOwner, others) //
          || isOwnerInfoOptionLocked(info, lockOwner, others);
    }

    switch (lockType)
    {
    case READ:
      return isOwnerInfoReadLocked(info, lockOwner, others);

    case WRITE:
      return isOwnerInfoWriteLocked(info, lockOwner, others);

    case OPTION:
      return isOwnerInfoOptionLocked(info, lockOwner, others);
    }

    return false;
  }

  private static boolean isOwnerInfoReadLocked(OwnerInfo info, CDOLockOwner by, boolean others)
  {
    Object readLockOwners = info.getReadLockOwners();
    if (readLockOwners == null)
    {
      return false;
    }

    int n;
    boolean contained;

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      n = owners.length;
      if (n == 0)
      {
        return false;
      }

      contained = CDOLockUtil.indexOf(owners, by) != -1;
    }
    else
    {
      n = 1;
      contained = readLockOwners == by;
    }

    if (others)
    {
      int ownCount = contained ? 1 : 0;
      return n > ownCount;
    }

    return contained;
  }

  private static boolean isOwnerInfoWriteLocked(OwnerInfo info, CDOLockOwner by, boolean others)
  {
    CDOLockOwner writeLockOwner = info.getWriteLockOwner();
    if (writeLockOwner == null)
    {
      return false;
    }

    return writeLockOwner == by ^ others;
  }

  private static boolean isOwnerInfoOptionLocked(OwnerInfo info, CDOLockOwner by, boolean others)
  {
    CDOLockOwner writeOptionOwner = info.getWriteOptionOwner();
    if (writeOptionOwner == null)
    {
      return false;
    }

    return writeOptionOwner == by ^ others;
  }

  private void trace(CDOID id, OwnerInfo oldInfo, OwnerInfo newInfo)
  {
    if (DEBUG)
    {
      String message = "[" + session.getSessionID() + "] " + id + ": " + oldInfo + " --> " + newInfo;

      if (DEBUG_STACK_TRACE)
      {
        message += "\n" + ReflectUtil.dumpThread();
      }

      IOUtil.OUT().println(message);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class OwnerInfo
  {
    public OwnerInfo()
    {
    }

    public final boolean isLocked(LockType lockType, CDOLockOwner lockOwner, boolean others)
    {
      return isOwnerInfoLocked(this, lockType, lockOwner, others);
    }

    public abstract Object getReadLockOwners();

    public abstract CDOLockOwner getWriteLockOwner();

    public abstract CDOLockOwner getWriteOptionOwner();

    public abstract OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner, Consumer<LockType> consumer);

    public abstract boolean remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner);

    public final OwnerInfo applyDelta(CDOLockStateCacheImpl cache, CDOLockDelta delta)
    {
      OwnerInfo info = this;

      CDOLockOwner oldOwner = delta.getOldOwner();
      CDOLockOwner newOwner = delta.getNewOwner();

      switch (delta.getType())
      {
      case READ:
        if (oldOwner != null)
        {
          info = info.removeReadLockOwner(cache, oldOwner);
        }

        if (newOwner != null)
        {
          info = info.addReadLockOwner(cache, newOwner);
        }

        break;

      case OPTION:
        if (oldOwner != null)
        {
          info = info.removeWriteOptionOwner(cache, oldOwner);
        }

        if (newOwner != null)
        {
          info = info.addWriteOptionOwner(cache, newOwner);
        }

        break;

      case WRITE:
        if (oldOwner != null)
        {
          info = info.removeWriteLockOwner(cache, oldOwner);
        }

        if (newOwner != null)
        {
          info = info.addWriteLockOwner(cache, newOwner);
        }

        break;

      default:
        throw new AssertionError();
      }

      return info;
    }

    @Override
    public String toString()
    {
      String name = getClass().getName();
      name = name.replace(CDOLockStateCacheImpl.class.getName() + "$", "");
      name = name.replace('$', '.');
      return name;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class NoOwnerInfo extends OwnerInfo
  {
    public static final OwnerInfo INSTANCE = new NoOwnerInfo();

    private NoOwnerInfo()
    {
    }

    @Override
    public Object getReadLockOwners()
    {
      return null;
    }

    @Override
    public CDOLockOwner getWriteLockOwner()
    {
      return null;
    }

    @Override
    public CDOLockOwner getWriteOptionOwner()
    {
      return null;
    }

    @Override
    public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLock.TYPE);
    }

    @Override
    public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      return this;
    }

    @Override
    public OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
    }

    @Override
    public OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      return this;
    }

    @Override
    public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteOption.TYPE);
    }

    @Override
    public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      return this;
    }

    @Override
    public OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner, Consumer<LockType> consumer)
    {
      return this;
    }

    @Override
    public boolean remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      // Do nothing.
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class SingleOwnerInfo extends OwnerInfo
  {
    protected CDOLockOwner owner;

    public SingleOwnerInfo(CDOLockOwner owner)
    {
      this.owner = owner;
    }

    @Override
    public Object getReadLockOwners()
    {
      return null;
    }

    @Override
    public CDOLockOwner getWriteLockOwner()
    {
      return null;
    }

    @Override
    public CDOLockOwner getWriteOptionOwner()
    {
      return null;
    }

    @Override
    public final OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner, Consumer<LockType> consumer)
    {
      if (owner == this.owner)
      {
        notifyLockTypes(consumer);
        return NoOwnerInfo.INSTANCE;
      }

      return this;
    }

    @Override
    public final boolean remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      if (owner == oldOwner)
      {
        owner = newOwner;
        return true;
      }

      return false;
    }

    @Override
    public String toString()
    {
      return super.toString() + "[" + owner + "]";
    }

    protected final ObjectAlreadyLockedException failure(CDOLockOwner owner, LockType lockType)
    {
      StringJoiner joiner = new StringJoiner(" and the ", //
          owner + " could not acquire the " + lockType + " lock because the ", //
          " is already acquired by " + this.owner);

      notifyLockTypes(type -> {
        joiner.add(type + " lock");
      });

      return new ObjectAlreadyLockedException(joiner.toString());
    }

    protected abstract void notifyLockTypes(Consumer<LockType> consumer);

    public static SingleOwnerInfo create(CDOLockOwner owner, int type)
    {
      switch (type)
      {
      case SingleOwnerInfo.ReadLock.TYPE:
        return new SingleOwnerInfo.ReadLock(owner);

      case SingleOwnerInfo.ReadLockAndWriteLock.TYPE:
        return new SingleOwnerInfo.ReadLockAndWriteLock(owner);

      case SingleOwnerInfo.ReadLockAndWriteOption.TYPE:
        return new SingleOwnerInfo.ReadLockAndWriteOption(owner);

      case SingleOwnerInfo.WriteLock.TYPE:
        return new SingleOwnerInfo.WriteLock(owner);

      case SingleOwnerInfo.WriteOption.TYPE:
        return new SingleOwnerInfo.WriteOption(owner);

      default:
        throw new IllegalArgumentException("Illegal type: " + type);
      }
    }

    /**
     * @author Eike Stepper
     */
    protected static final class ReadLock extends SingleOwnerInfo
    {
      public static final int TYPE = 0;

      public ReadLock(CDOLockOwner owner)
      {
        super(owner);
      }

      @Override
      public Object getReadLockOwners()
      {
        return owner;
      }

      @Override
      public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        return new MultiOwnerInfo.ReadLock(this.owner, owner);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return removeOwner(cache, owner, null);
      }

      @Override
      public OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLockAndWriteLock.TYPE);
        }

        throw failure(owner, LockType.WRITE);
      }

      @Override
      public OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLockAndWriteOption.TYPE);
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(owner, this.owner);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      protected void notifyLockTypes(Consumer<LockType> consumer)
      {
        if (consumer != null)
        {
          consumer.accept(LockType.READ);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    protected static final class ReadLockAndWriteLock extends SingleOwnerInfo
    {
      public static final int TYPE = 1;

      public ReadLockAndWriteLock(CDOLockOwner owner)
      {
        super(owner);
      }

      @Override
      public Object getReadLockOwners()
      {
        return owner;
      }

      @Override
      public CDOLockOwner getWriteLockOwner()
      {
        return owner;
      }

      @Override
      public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw failure(owner, LockType.READ);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
        }

        return this;
      }

      @Override
      public OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw failure(owner, LockType.WRITE);
      }

      @Override
      public OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLock.TYPE);
        }

        return this;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        throw failure(owner, LockType.OPTION);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      protected void notifyLockTypes(Consumer<LockType> consumer)
      {
        consumer.accept(LockType.READ);
        consumer.accept(LockType.WRITE);
      }
    }

    /**
     * @author Eike Stepper
     */
    protected static final class ReadLockAndWriteOption extends SingleOwnerInfo
    {
      public static final int TYPE = 2;

      public ReadLockAndWriteOption(CDOLockOwner owner)
      {
        super(owner);
      }

      @Override
      public Object getReadLockOwners()
      {
        return owner;
      }

      @Override
      public CDOLockOwner getWriteOptionOwner()
      {
        return owner;
      }

      @Override
      public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(this.owner, this.owner, owner);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteOption.TYPE);
        }

        return this;
      }

      @Override
      public OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
        }

        throw failure(owner, LockType.WRITE);
      }

      @Override
      public OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw failure(owner, LockType.OPTION);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLock.TYPE);
        }

        return this;
      }

      @Override
      protected void notifyLockTypes(Consumer<LockType> consumer)
      {
        consumer.accept(LockType.READ);
        consumer.accept(LockType.OPTION);
      }
    }

    /**
     * @author Eike Stepper
     */
    protected static final class WriteLock extends SingleOwnerInfo
    {
      public static final int TYPE = 3;

      public WriteLock(CDOLockOwner owner)
      {
        super(owner);
      }

      @Override
      public CDOLockOwner getWriteLockOwner()
      {
        return owner;
      }

      @Override
      public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLockAndWriteLock.TYPE);
        }

        throw failure(owner, LockType.READ);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      public OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw failure(owner, LockType.WRITE);
      }

      @Override
      public OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == this.owner)
        {
          return NoOwnerInfo.INSTANCE;
        }

        return this;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        throw failure(owner, LockType.OPTION);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      protected void notifyLockTypes(Consumer<LockType> consumer)
      {
        consumer.accept(LockType.WRITE);
      }
    }

    /**
     * @author Eike Stepper
     */
    protected static final class WriteOption extends SingleOwnerInfo
    {
      public static final int TYPE = 4;

      public WriteOption(CDOLockOwner owner)
      {
        super(owner);
      }

      @Override
      public CDOLockOwner getWriteOptionOwner()
      {
        return owner;
      }

      @Override
      public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLockAndWriteOption.TYPE);
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(this.owner, owner);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      public OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
        }

        throw failure(owner, LockType.WRITE);
      }

      @Override
      public OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw failure(owner, LockType.OPTION);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == this.owner)
        {
          return NoOwnerInfo.INSTANCE;
        }

        return this;
      }

      @Override
      protected void notifyLockTypes(Consumer<LockType> consumer)
      {
        consumer.accept(LockType.OPTION);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class MultiOwnerInfo extends OwnerInfo
  {
    public MultiOwnerInfo()
    {
    }

    @Override
    public final CDOLockOwner getWriteLockOwner()
    {
      return null;
    }

    @Override
    public final OwnerInfo addWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      throw failure(owner, LockType.WRITE);
    }

    @Override
    public final OwnerInfo removeWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      return this;
    }

    protected final ObjectAlreadyLockedException failure(CDOLockOwner owner, LockType lockType)
    {
      String message = owner + " could not acquire the " + lockType + " lock because the READ lock is already acquired by " + getReadLockOwners();

      CDOLockOwner writeOptionOwner = getWriteOptionOwner();
      if (writeOptionOwner != null)
      {
        message += " and the OPTION lock is already acquired by " + writeOptionOwner;
      }

      return new ObjectAlreadyLockedException(message);
    }

    /**
     * @author Eike Stepper
     */
    protected static class ReadLock extends MultiOwnerInfo
    {
      protected final CDOLockOwner[] readLockOwners;

      public ReadLock(CDOLockOwner... readLockOwners)
      {
        CheckUtil.checkArg(readLockOwners.length != 0, "No read lock owners");
        this.readLockOwners = readLockOwners;
      }

      @Override
      public Object getReadLockOwners()
      {
        return readLockOwners;
      }

      @Override
      public OwnerInfo addReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        CDOLockOwner[] owners = readLockOwners;
        if (CDOLockUtil.indexOf(owners, owner) == -1)
        {
          int oldLength = owners.length;
          CDOLockOwner[] newOwners = new CDOLockOwner[oldLength + 1];
          System.arraycopy(owners, 0, newOwners, 0, oldLength);
          newOwners[oldLength] = owner;
          return createMultiOwnerInfo(getWriteOptionOwner(), newOwners);
        }

        return this;
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        CDOLockOwner[] owners = readLockOwners;
        int index = CDOLockUtil.indexOf(owners, owner);
        if (index != -1)
        {
          CDOLockOwner writeOptionOwner = getWriteOptionOwner();

          int oldLength = owners.length;
          if (oldLength == 1)
          {
            // Removal will leave no readLockOwners.
            return cache.getSingleOwnerInfo(writeOptionOwner, SingleOwnerInfo.WriteOption.TYPE);
          }

          if (oldLength == 2)
          {
            // Removal will leave only one readLockOwner.
            CDOLockOwner remainingReadLockOwner = readLockOwners[index == 0 ? 1 : 0];

            // If that's also the writeOptionOwner convert to a SingleOwnerInfo.WriteOptionAndReadLock.
            if (writeOptionOwner == remainingReadLockOwner)
            {
              return cache.getSingleOwnerInfo(writeOptionOwner, SingleOwnerInfo.ReadLockAndWriteOption.TYPE);
            }

            return createMultiOwnerInfo(writeOptionOwner, remainingReadLockOwner);
          }

          CDOLockOwner[] newOwners = new CDOLockOwner[oldLength - 1];
          if (index > 0)
          {
            System.arraycopy(owners, 0, newOwners, 0, index);
          }

          int rest = oldLength - index - 1;
          if (rest > 0)
          {
            System.arraycopy(owners, index + 1, newOwners, index, rest);
          }

          return createMultiOwnerInfo(writeOptionOwner, newOwners);
        }

        return this;
      }

      @Override
      public CDOLockOwner getWriteOptionOwner()
      {
        return null;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(owner, readLockOwners);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return this;
      }

      @Override
      public OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner, Consumer<LockType> consumer)
      {
        OwnerInfo newInfo = removeReadLockOwner(cache, owner);

        if (newInfo != this)
        {
          consumer.accept(LockType.READ);
        }

        return newInfo;
      }

      @Override
      public boolean remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
      {
        CDOLockOwner[] owners = readLockOwners;
        int index = CDOLockUtil.indexOf(owners, oldOwner);
        if (index != -1)
        {
          readLockOwners[index] = newOwner;
          return true;
        }

        return false;
      }

      @Override
      public String toString()
      {
        return super.toString() + "[readLockOwners=" + Arrays.asList(readLockOwners) + "]";
      }

      private static MultiOwnerInfo createMultiOwnerInfo(CDOLockOwner writeOptionOwner, CDOLockOwner... readLockOwners)
      {
        if (writeOptionOwner != null)
        {
          return new MultiOwnerInfo.ReadLockAndWriteOption(writeOptionOwner, readLockOwners);
        }

        return new MultiOwnerInfo.ReadLock(readLockOwners);
      }
    }

    /**
     * @author Eike Stepper
     */
    protected static final class ReadLockAndWriteOption extends MultiOwnerInfo.ReadLock
    {
      private CDOLockOwner writeOptionOwner;

      public ReadLockAndWriteOption(CDOLockOwner writeOptionOwner, CDOLockOwner... readLockOwners)
      {
        super(readLockOwners);
        this.writeOptionOwner = writeOptionOwner;
      }

      @Override
      public CDOLockOwner getWriteOptionOwner()
      {
        return writeOptionOwner;
      }

      @Override
      public OwnerInfo addWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == writeOptionOwner)
        {
          return this;
        }

        throw failure(owner, LockType.OPTION);
      }

      @Override
      public OwnerInfo removeWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == writeOptionOwner)
        {
          return new MultiOwnerInfo.ReadLock(readLockOwners);
        }

        return this;
      }

      @Override
      public OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner, Consumer<LockType> consumer)
      {
        OwnerInfo newInfo = super.removeOwner(cache, owner, consumer);

        if (owner == newInfo.getWriteOptionOwner())
        {
          consumer.accept(LockType.OPTION);
          return newInfo.removeWriteOptionOwner(cache, owner);
        }

        return newInfo;
      }

      @Override
      public boolean remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
      {
        boolean remapped = super.remapOwner(oldOwner, newOwner);

        if (writeOptionOwner == oldOwner)
        {
          writeOptionOwner = newOwner;
          remapped = true;
        }

        return remapped;
      }

      @Override
      public String toString()
      {
        return super.toString() + "[readLockOwners=" + Arrays.asList(readLockOwners) + ", writeOptionOwner=" + writeOptionOwner + "]";
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LockState extends AbstractCDOLockState
  {
    private static final Set<CDOLockOwner> NO_LOCK_OWNERS = Collections.emptySet();

    private final CDOLockStateCacheImpl cache;

    public LockState(Object lockedObject, CDOLockStateCacheImpl cache)
    {
      super(lockedObject);
      this.cache = cache;
    }

    @Override
    public final boolean isLocked(LockType lockType, CDOLockOwner lockOwner, boolean others)
    {
      OwnerInfo info = getInfo();
      return info.isLocked(lockType, lockOwner, others);
    }

    @Override
    public final Set<CDOLockOwner> getReadLockOwners()
    {
      OwnerInfo info = getInfo();
      Object readLockOwners = info.getReadLockOwners();

      if (readLockOwners == null)
      {
        return NO_LOCK_OWNERS;
      }

      if (readLockOwners.getClass() == ARRAY_CLASS)
      {
        CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
        Set<CDOLockOwner> result = new HashSet<>();
        for (CDOLockOwner owner : owners)
        {
          result.add(owner);
        }

        return Collections.unmodifiableSet(result);
      }

      return Collections.singleton((CDOLockOwner)readLockOwners);
    }

    @Override
    public final CDOLockOwner getWriteLockOwner()
    {
      OwnerInfo info = getInfo();
      return info.getWriteLockOwner();
    }

    @Override
    public final CDOLockOwner getWriteOptionOwner()
    {
      OwnerInfo info = getInfo();
      return info.getWriteOptionOwner();
    }

    @Override
    protected CDOLockDelta addReadOwner(CDOLockOwner owner)
    {
      if (cache.changeOwnerInfo(lockedObject, owner, (i, o) -> i.addReadLockOwner(cache, o)))
      {
        return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, null, owner);
      }

      return null;
    }

    @Override
    protected CDOLockDelta addWriteOwner(CDOLockOwner owner)
    {
      if (cache.changeOwnerInfo(lockedObject, owner, (i, o) -> i.addWriteLockOwner(cache, o)))
      {
        return CDOLockUtil.createLockDelta(lockedObject, LockType.WRITE, null, owner);
      }

      return null;
    }

    @Override
    protected CDOLockDelta addOptionOwner(CDOLockOwner owner)
    {
      if (cache.changeOwnerInfo(lockedObject, owner, (i, o) -> i.addWriteOptionOwner(cache, o)))
      {
        return CDOLockUtil.createLockDelta(lockedObject, LockType.OPTION, null, owner);
      }

      return null;
    }

    @Override
    protected CDOLockDelta removeReadOwner(CDOLockOwner owner)
    {
      if (cache.changeOwnerInfo(lockedObject, owner, (i, o) -> i.removeReadLockOwner(cache, o)))
      {
        return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, owner, null);
      }

      return null;
    }

    @Override
    protected CDOLockDelta removeWriteOwner(CDOLockOwner owner)
    {
      if (cache.changeOwnerInfo(lockedObject, owner, (i, o) -> i.removeWriteLockOwner(cache, o)))
      {
        return CDOLockUtil.createLockDelta(lockedObject, LockType.WRITE, owner, null);
      }

      return null;
    }

    @Override
    protected CDOLockDelta removeOptionOwner(CDOLockOwner owner)
    {
      if (cache.changeOwnerInfo(lockedObject, owner, (i, o) -> i.removeWriteOptionOwner(cache, o)))
      {
        return CDOLockUtil.createLockDelta(lockedObject, LockType.OPTION, owner, null);
      }

      return null;
    }

    private OwnerInfo getInfo()
    {
      return cache.getOwnerInfo(lockedObject);
    }

    /**
     * @deprecated Call {@link CDOLockStateCacheImpl#remapOwner(CDOBranch, CDOLockOwner, CDOLockOwner)} instead.
     */
    @Deprecated
    @Override
    public CDOLockDelta[] remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      throw new UnsupportedOperationException();
    }
  }
}
