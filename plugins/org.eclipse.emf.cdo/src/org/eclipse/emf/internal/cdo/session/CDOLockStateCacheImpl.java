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
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockState;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.CollectionUtil.KeepMappedValue;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

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
    return new MutableLockState(key, this);
  }

  @Override
  public void getLockStates(CDOBranch branch, Collection<CDOID> ids, boolean loadOnDemand, Consumer<CDOLockState> consumer)
  {
    if (ids == null)
    {
      if (loadOnDemand)
      {
        loadAndCacheLockStates(branch, null, consumer);
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
        loadAndCacheLockStates(branch, missingIDs, lockState -> {
          missingIDs.remove(lockState.getID());
          consumer.accept(lockState);
        });

        int size = missingIDs.size();
        if (size != 0)
        {
          List<CDOLockState> defaultLockStatesToCache = new ArrayList<>(size);

          for (CDOID id : missingIDs)
          {
            Object key = createKey(branch, id);
            CDOLockState defaultLockStateToCache = new ImmutableLockState(key, NoOwnerInfo.INSTANCE);
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
  public void addLockStates(CDOBranch branch, Collection<? extends CDOLockState> newLockStates, Consumer<CDOLockState> consumer)
  {
    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);

    for (CDOLockState loadedLockState : newLockStates)
    {
      CDOID id = loadedLockState.getID();
      OwnerInfo info = createOwnerInfo(loadedLockState);
      infos.put(id, info);

      if (consumer != null)
      {
        consumer.accept(loadedLockState);
      }
    }
  }

  @Override
  public void removeOwner(CDOBranch branch, CDOLockOwner owner, Consumer<CDOLockState> changeConsumer)
  {
    List<Pair<CDOID, OwnerInfo>> newInfos = new ArrayList<>();
    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
    infos.forEach((id, info) -> {
      OwnerInfo newInfo = info.removeOwner(this, owner);
      if (newInfo != info)
      {
        newInfos.add(Pair.create(id, newInfo));
      }
    });

    for (Pair<CDOID, OwnerInfo> pair : newInfos)
    {
      CDOID id = pair.getElement1();
      OwnerInfo newInfo = pair.getElement2();

      if (newInfo == NoOwnerInfo.INSTANCE)
      {
        infos.remove(id);
      }
      else
      {
        infos.put(id, newInfo);
      }

      notifyConsumer(branch, id, changeConsumer);
    }
  }

  @Override
  public void remapOwner(CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    removeSingleOwnerInfos(oldOwner);
    addSingleOwnerInfos(newOwner, -1);

    ConcurrentMap<CDOID, OwnerInfo> infos = getOwnerInfoMap(branch);
    for (OwnerInfo info : infos.values())
    {
      info.remapOwner(oldOwner, newOwner);
    }
  }

  @Override
  public void removeLockStates(CDOBranch branch, Collection<CDOID> ids, Consumer<CDOLockState> consumer)
  {
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
      if (branches.add(branch))
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
      if (branches.remove(branch)) // OK because branches is a HashBag.
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
      Object key = createKey(branch, id);
      CDOLockState lockState = new MutableLockState(key, this);
      consumer.accept(lockState);
    }
  }

  private SingleOwnerInfo addSingleOwnerInfos(CDOLockOwner owner, int typeToReturn)
  {
    CheckUtil.checkArg(owner, "owner");
    SingleOwnerInfo infoToReturn = null;

    for (int type = 0; type < singleOwnerInfos.length; type++)
    {
      SingleOwnerInfo info = SingleOwnerInfo.create(owner, type);
      singleOwnerInfos[type].put(owner, info);

      if (type == typeToReturn)
      {
        infoToReturn = info;
      }
    }

    return infoToReturn;
  }

  private void removeSingleOwnerInfos(CDOLockOwner owner)
  {
    CheckUtil.checkArg(owner, "owner");
    for (int type = 0; type < singleOwnerInfos.length; type++)
    {
      singleOwnerInfos[type].remove(owner);
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
          changed[0] = true;
          return newInfo;
        }

        throw new KeepMappedValue(info);
      });

      return changed[0];
    });
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

  private void loadAndCacheLockStates(CDOBranch branch, Set<CDOID> ids, Consumer<CDOLockState> consumer)
  {
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    CDOLockState[] loadedLockStates = sessionProtocol.getLockStates(branch.getID(), ids, CDOLockState.DEPTH_NONE);

    addLockStates(branch, Arrays.asList(loadedLockStates), consumer);
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

      info = info.setWriteLockOwner(this, lockState.getWriteLockOwner());
      info = info.setWriteOptionOwner(this, lockState.getWriteOptionOwner());
    }
    catch (RuntimeException | Error ex)
    {
      throw new ImplementationError("Could not modify " + info + " of " + lockState.getLockedObject(), ex);
    }

    return info;
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

    public abstract OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner);

    public abstract void remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner);

    @Override
    public String toString()
    {
      String name = getClass().getName();
      name = StringUtil.replace(name, CDOLockStateCache.class.getName() + "$", "");
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
      CheckUtil.checkArg(owner, "owner");
      return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLock.TYPE);
    }

    @Override
    public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      CheckUtil.checkArg(owner, "owner");
      return this;
    }

    @Override
    public OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
    }

    @Override
    public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteOption.TYPE);
    }

    @Override
    public OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      return this;
    }

    @Override
    public void remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      // Do nothing.
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
    public final OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == this.owner)
      {
        return NoOwnerInfo.INSTANCE;
      }

      return this;
    }

    @Override
    public final void remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      if (owner == oldOwner)
      {
        owner = newOwner;
      }
    }

    @Override
    public String toString()
    {
      return super.toString() + "[" + owner + "]";
    }

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
        CheckUtil.checkArg(owner, "owner");

        if (owner == this.owner)
        {
          return this;
        }

        return new MultiOwnerInfo.ReadLock(this.owner, owner);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        CheckUtil.checkArg(owner, "owner");
        return removeOwner(cache, owner);
      }

      @Override
      public OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLockAndWriteLock.TYPE);
        }

        throw new IllegalStateException("Read lock already obtained");
      }

      @Override
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
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
        CheckUtil.checkArg(owner, "owner");
        if (owner == this.owner)
        {
          return this;
        }

        throw new IllegalStateException("Write lock already obtained");
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        CheckUtil.checkArg(owner, "owner");
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
        }

        return this;
      }

      @Override
      public OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return cache.getSingleOwnerInfo(this.owner, SingleOwnerInfo.ReadLock.TYPE);
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw new IllegalStateException("Read lock already obtained");
      }

      @Override
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        throw new IllegalStateException("Write lock already obtained");
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
        CheckUtil.checkArg(owner, "owner");
        if (owner == this.owner)
        {
          return this;
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(this.owner, this.owner, owner);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        CheckUtil.checkArg(owner, "owner");
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteOption.TYPE);
        }

        return this;
      }

      @Override
      public OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
        }

        throw new IllegalStateException("Read lock already obtained");
      }

      @Override
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return cache.getSingleOwnerInfo(this.owner, SingleOwnerInfo.ReadLock.TYPE);
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw new IllegalStateException("Write option already obtained");
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
        CheckUtil.checkArg(owner, "owner");
        throw new IllegalStateException("Write lock already obtained");
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        CheckUtil.checkArg(owner, "owner");
        return this;
      }

      @Override
      public OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return NoOwnerInfo.INSTANCE;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw new IllegalStateException("Write lock already obtained");
      }

      @Override
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        throw new IllegalStateException("Write lock already obtained");
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
        CheckUtil.checkArg(owner, "owner");
        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.ReadLockAndWriteOption.TYPE);
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(this.owner, owner);
      }

      @Override
      public OwnerInfo removeReadLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        CheckUtil.checkArg(owner, "owner");
        return this;
      }

      @Override
      public OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        if (owner == this.owner)
        {
          return cache.getSingleOwnerInfo(owner, SingleOwnerInfo.WriteLock.TYPE);
        }

        throw new IllegalStateException("Write option already obtained");
      }

      @Override
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return NoOwnerInfo.INSTANCE;
        }

        if (owner == this.owner)
        {
          return this;
        }

        throw new IllegalStateException("Write option already obtained");
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
    public final OwnerInfo setWriteLockOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
    {
      if (owner == null)
      {
        return this;
      }

      throw new IllegalStateException();
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
        CheckUtil.checkArg(owner, "owner");

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
        CheckUtil.checkArg(owner, "owner");

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
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return this;
        }

        return new MultiOwnerInfo.ReadLockAndWriteOption(owner, readLockOwners);
      }

      @Override
      public OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        return removeReadLockOwner(cache, owner);
      }

      @Override
      public void remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
      {
        CDOLockOwner[] owners = readLockOwners;
        int index = CDOLockUtil.indexOf(owners, oldOwner);
        if (index != -1)
        {
          readLockOwners[index] = newOwner;
        }
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
      public OwnerInfo setWriteOptionOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        if (owner == null)
        {
          return new MultiOwnerInfo.ReadLock(readLockOwners);
        }

        if (writeOptionOwner != owner)
        {
          throw new IllegalStateException();
        }

        return this;
      }

      @Override
      public OwnerInfo removeOwner(CDOLockStateCacheImpl cache, CDOLockOwner owner)
      {
        OwnerInfo newInfo = super.removeOwner(cache, owner);

        if (owner == newInfo.getWriteOptionOwner())
        {
          return newInfo.setWriteOptionOwner(cache, null);
        }

        return newInfo;
      }

      @Override
      public void remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
      {
        super.remapOwner(oldOwner, newOwner);

        if (writeOptionOwner == oldOwner)
        {
          writeOptionOwner = newOwner;
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class LockState extends AbstractCDOLockState
  {
    private static final Set<CDOLockOwner> NO_LOCK_OWNERS = Collections.emptySet();

    protected final Object key;

    public LockState(Object key)
    {
      this.key = key;
    }

    @Override
    public final Object getLockedObject()
    {
      return key;
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

    protected abstract OwnerInfo getInfo();
  }

  /**
   * @author Eike Stepper
   */
  private static final class MutableLockState extends LockState implements InternalCDOLockState
  {
    private final CDOLockStateCacheImpl cache;

    public MutableLockState(Object key, CDOLockStateCacheImpl cache)
    {
      super(key);
      this.cache = cache;
    }

    @Override
    public void addReadLockOwner(CDOLockOwner owner)
    {
      cache.changeOwnerInfo(key, owner, (i, o) -> i.addReadLockOwner(cache, o));
    }

    @Override
    public boolean removeReadLockOwner(CDOLockOwner owner)
    {
      return cache.changeOwnerInfo(key, owner, (i, o) -> i.removeReadLockOwner(cache, o));
    }

    @Override
    public void setWriteLockOwner(CDOLockOwner owner)
    {
      cache.changeOwnerInfo(key, owner, (i, o) -> i.setWriteLockOwner(cache, o));
    }

    @Override
    public void setWriteOptionOwner(CDOLockOwner owner)
    {
      cache.changeOwnerInfo(key, owner, (i, o) -> i.setWriteOptionOwner(cache, o));
    }

    @Override
    public boolean removeOwner(CDOLockOwner owner)
    {
      return cache.changeOwnerInfo(key, owner, (i, o) -> i.removeOwner(cache, o));
    }

    /**
     * @deprecated Call {@link CDOLockStateCacheImpl#remapOwner(CDOBranch, CDOLockOwner, CDOLockOwner)} instead.
     */
    @Deprecated
    @Override
    public boolean remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void updateFrom(Object object, CDOLockState source)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Only used for new objects and those are local to a transaction, i.e., not cached.
     */
    @Deprecated
    @Override
    public void updateFrom(CDOLockState source)
    {
      throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void dispose()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected OwnerInfo getInfo()
    {
      return cache.getOwnerInfo(key);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ImmutableLockState extends LockState
  {
    private final OwnerInfo info;

    public ImmutableLockState(Object key, OwnerInfo info)
    {
      super(key);
      this.info = info;
    }

    @Override
    protected OwnerInfo getInfo()
    {
      return info;
    }
  }
}
