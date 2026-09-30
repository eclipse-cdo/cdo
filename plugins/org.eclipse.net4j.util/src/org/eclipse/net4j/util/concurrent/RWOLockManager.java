/*
 * Copyright (c) 2011, 2012, 2014-2016, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Eike Stepper - major reimplementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange.DeltaHandler;
import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange.Operation;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Keeps track of locks on objects. Locks are owned by contexts. A particular combination of locks and their owners, for
 * a given object, is represented by instances of the {@link LockState} class. This class is also responsible for
 * deciding whether or not a new lock can be granted, based on the locks already present.
 *
 * @author Caspar De Groot
 * @author Eike Stepper
 * @since 3.2
 * @param <OBJECT> the locked object type
 * @param <CONTEXT> the lock context type
 */
public class RWOLockManager<OBJECT, CONTEXT> extends Lifecycle implements IRWOLockManager<OBJECT, CONTEXT>
{
  private static final LockType[][] LOCK_TYPE_ARRAYS = { { LockType.values()[0] }, { LockType.values()[1] }, { LockType.values()[2] } };

  private static final LockType[] ALL_LOCK_TYPES_ARRAY = LockType.values();

  /**
   * @since 3.16
   */
  protected final ReentrantReadWriteAccess rwAccess = new ReentrantReadWriteAccess(true);

  /**
   * @since 3.16
   */
  protected final Access read = rwAccess.readAccess();

  /**
   * @since 3.16
   */
  protected final Access write = rwAccess.writeAccess();

  private final Condition unlocked = rwAccess.newCondition();

  private final Map<OBJECT, LockState<OBJECT, CONTEXT>> objectToLockStateMap = createObjectToLocksMap();

  /**
   * A mapping of contexts (owners of locks) to the lock states that they are involved in. Here, an 'involvement' means
   * that the context owns at least one lock on the object that the lock state is for. To determine exactly what kind of
   * lock, the lock state object obtained from this map must be queried.
   * <p>
   * This map is a performance optimization to avoid having to scan all lock states.
   */
  private final Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> contextToLockStates = createContextToLocksMap();

  private volatile long modCount;

  public RWOLockManager()
  {
  }

  /**
   * @category Read Access
   */
  @Override
  public long getModCount()
  {
    try (Access access = read.access())
    {
      return modCount;
    }
  }

  /**
   * @since 3.16
   * @category Write Access
   */
  @Override
  public long lock(CONTEXT context, Collection<? extends OBJECT> objects, LockType lockType, int count, long timeout, //
      LockDeltaHandler<OBJECT, CONTEXT> deltaHandler, Consumer<LockState<OBJECT, CONTEXT>> stateHandler) //
      throws InterruptedException, TimeoutRuntimeException
  {
    LockChange<OBJECT> change = new LockChange<>(Operation.LOCK, objects, lockType, count, timeout);
    return changeLocks(context, Collections.singletonList(change), adapt(deltaHandler, Operation.LOCK), stateHandler, ExecutionMode.PROGRESSIVE);
  }

  @Override
  public long changeLocks(CONTEXT context, List<? extends LockChange<OBJECT>> changes, DeltaHandler<OBJECT, CONTEXT> deltaHandler,
      Consumer<LockState<OBJECT, CONTEXT>> stateHandler) throws InterruptedException, TimeoutRuntimeException
  {
    return changeLocks(context, changes, deltaHandler, stateHandler, ExecutionMode.ATOMIC);
  }

  private long changeLocks(CONTEXT context, List<? extends LockChange<OBJECT>> changes, DeltaHandler<OBJECT, CONTEXT> deltaHandler,
      Consumer<LockState<OBJECT, CONTEXT>> stateHandler, ExecutionMode executionMode) throws InterruptedException, TimeoutRuntimeException
  {
    CheckUtil.checkArg(context, "context"); //$NON-NLS-1$
    CheckUtil.checkArg(changes, "changes"); //$NON-NLS-1$

    if (changes.isEmpty())
    {
      return getModCount();
    }

    if (executionMode == ExecutionMode.PROGRESSIVE)
    {
      return lockProgressively(context, changes.get(0), deltaHandler, stateHandler);
    }

    try (Access access = write.access())
    {
      long deadline = getDeadline(changes);
      Map<OBJECT, LockState<OBJECT, CONTEXT>> simulatedStates;
      Set<OBJECT> stateObjects;
      List<LockDeltaRecord<OBJECT>> deltas;

      for (;;)
      {
        simulatedStates = createSimulation(context);
        stateObjects = new LinkedHashSet<>();
        deltas = new ArrayList<>();

        if (simulateChanges(context, changes, simulatedStates, stateObjects, deltas))
        {
          break;
        }

        long waitTime = deadline == Long.MAX_VALUE ? Long.MAX_VALUE : deadline - currentTimeMillis();
        if (waitTime <= 0)
        {
          throw createTimeoutException(changes, simulatedStates);
        }

        unlocked.await(waitTime, TimeUnit.MILLISECONDS);
      }

      deltas.clear();
      stateObjects.clear();
      boolean effectiveRequest = false;
      boolean hasUnlock = false;
      Map<OBJECT, LockState<OBJECT, CONTEXT>> affectedStates = new LinkedHashMap<>();

      for (LockChange<OBJECT> change : changes)
      {
        boolean effectiveChange = isEffectiveRequest(context, change);
        effectiveRequest |= effectiveChange;
        hasUnlock |= change.getOperation() == Operation.UNLOCK && effectiveChange;
        applyChange(context, change, stateObjects, deltas, affectedStates);
      }

      for (Map.Entry<OBJECT, LockState<OBJECT, CONTEXT>> entry : affectedStates.entrySet())
      {
        LockState<OBJECT, CONTEXT> lockState = entry.getValue();
        if (lockState.hasLocks(context))
        {
          addContextToLockStateMapping(context, lockState);
        }
        else
        {
          Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
          if (lockStates != null)
          {
            lockStates.remove(lockState);
            if (lockStates.isEmpty())
            {
              contextToLockStates.remove(context);
            }
          }
        }

        if (lockState.hasNoLocks())
        {
          objectToLockStateMap.remove(entry.getKey());
        }
      }

      if (deltaHandler != null)
      {
        for (LockDeltaRecord<OBJECT> delta : deltas)
        {
          deltaHandler.handleLockDelta(delta.operation, context, delta.object, delta.lockType, delta.oldCount, delta.newCount);
        }
      }

      if (stateHandler != null)
      {
        for (OBJECT object : stateObjects)
        {
          LockState<OBJECT, CONTEXT> lockState = affectedStates.get(object);
          if (lockState != null)
          {
            stateHandler.accept(lockState);
          }
        }
      }

      if (hasUnlock)
      {
        unlocked.signalAll();
      }

      return effectiveRequest ? ++modCount : modCount;
    }
  }

  private long lockProgressively(CONTEXT context, LockChange<OBJECT> change, DeltaHandler<OBJECT, CONTEXT> deltaHandler,
      Consumer<LockState<OBJECT, CONTEXT>> stateHandler) throws InterruptedException, TimeoutRuntimeException
  {
    Collection<? extends OBJECT> objects = change.getObjects();
    CheckUtil.checkArg(objects, "objects"); //$NON-NLS-1$
    CheckUtil.checkArg(change.getLockType(), "lockType"); //$NON-NLS-1$
    CheckUtil.checkArg(change.getCount() >= 0, "count >= 0"); //$NON-NLS-1$

    try (Access access = write.access())
    {
      if (ObjectUtil.isEmpty(objects) || change.getCount() == 0)
      {
        return modCount;
      }

      long deadline = change.getTimeout() == NO_TIMEOUT ? Long.MAX_VALUE : currentTimeMillis() + change.getTimeout();
      List<OBJECT> objectsToLock = new ArrayList<>(objects);
      List<OBJECT> lockedObjects = new ArrayList<>(objectsToLock.size());

      for (;;)
      {
        for (int i = 0; i < objectsToLock.size();)
        {
          OBJECT object = objectsToLock.get(i);
          LockState<OBJECT, CONTEXT> state = getOrCreateLockState(object);
          if (!state.canLock(change.getLockType(), context))
          {
            ++i;
            continue;
          }

          int oldCount = state.getLockCount(change.getLockType(), context);
          int newCount = state.lock(change.getLockType(), context, change.getCount());
          if (newCount != oldCount)
          {
            addContextToLockStateMapping(context, state);
            if (deltaHandler != null)
            {
              deltaHandler.handleLockDelta(Operation.LOCK, context, object, change.getLockType(), oldCount, newCount);
            }
          }

          if (stateHandler != null)
          {
            stateHandler.accept(state);
          }

          lockedObjects.add(object);
          objectsToLock.remove(i);
        }

        if (objectsToLock.isEmpty())
        {
          return ++modCount;
        }

        long waitTime = deadline == Long.MAX_VALUE ? Long.MAX_VALUE : deadline - currentTimeMillis();
        if (waitTime <= 0)
        {
          TimeoutRuntimeException ex = createTimeoutException(Collections.singletonList(change), createSimulation(context));
          rollbackProgressive(context, change, lockedObjects);
          throw ex;
        }

        try
        {
          unlocked.await(waitTime, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | TimeoutRuntimeException ex)
        {
          rollbackProgressive(context, change, lockedObjects);
          throw ex;
        }
      }
    }
  }

  private void rollbackProgressive(CONTEXT context, LockChange<OBJECT> change, List<OBJECT> lockedObjects)
  {
    for (OBJECT object : lockedObjects)
    {
      LockState<OBJECT, CONTEXT> state = objectToLockStateMap.get(object);
      if (state == null)
      {
        continue;
      }

      state.unlock(change.getLockType(), context, change.getCount());
      if (!state.hasLocks(context))
      {
        Set<LockState<OBJECT, CONTEXT>> states = contextToLockStates.get(context);
        if (states != null)
        {
          states.remove(state);
          if (states.isEmpty())
          {
            contextToLockStates.remove(context);
          }
        }
      }

      if (state.hasNoLocks())
      {
        objectToLockStateMap.remove(object);
      }
    }

    unlocked.signalAll();
  }

  private Map<OBJECT, LockState<OBJECT, CONTEXT>> createSimulation(CONTEXT context)
  {
    Map<OBJECT, LockState<OBJECT, CONTEXT>> result = new HashMap<>();
    Set<LockState<OBJECT, CONTEXT>> states = contextToLockStates.get(context);
    if (states != null)
    {
      for (LockState<OBJECT, CONTEXT> state : states)
      {
        result.put(state.getLockedObject(), state.copy());
      }
    }

    return result;
  }

  private boolean simulateChanges(CONTEXT context, List<? extends LockChange<OBJECT>> changes, Map<OBJECT, LockState<OBJECT, CONTEXT>> states,
      Set<OBJECT> stateObjects, List<LockDeltaRecord<OBJECT>> deltas)
  {
    for (LockChange<OBJECT> change : changes)
    {
      if (change.getCount() == 0)
      {
        continue;
      }

      List<OBJECT> targets = new ArrayList<>();

      Collection<? extends OBJECT> objects = change.getObjects();
      if (objects == null)
      {
        for (Map.Entry<OBJECT, LockState<OBJECT, CONTEXT>> entry : states.entrySet())
        {
          if (entry.getValue().hasLocks(context))
          {
            targets.add(entry.getKey());
          }
        }

        Set<LockState<OBJECT, CONTEXT>> existing = contextToLockStates.get(context);
        if (existing != null)
        {
          for (LockState<OBJECT, CONTEXT> state : existing)
          {
            if (!states.containsKey(state.getLockedObject()))
            {
              targets.add(state.getLockedObject());
              states.put(state.getLockedObject(), state.copy());
            }
          }
        }
      }
      else
      {
        targets.addAll(objects);
      }

      for (OBJECT object : targets)
      {
        LockState<OBJECT, CONTEXT> state = states.get(object);
        if (state == null)
        {
          LockState<OBJECT, CONTEXT> currentState = objectToLockStateMap.get(object);
          if (currentState != null)
          {
            state = currentState.copy();
            states.put(object, state);
          }
          else if (change.getOperation() == Operation.UNLOCK)
          {
            continue;
          }
          else
          {
            state = new LockState<>(object);
            states.put(object, state);
          }
        }

        if (change.getOperation() == Operation.LOCK)
        {
          if (!state.canLock(change.getLockType(), context))
          {
            return false;
          }

          int oldCount = state.getLockCount(change.getLockType(), context);
          int newCount = state.lock(change.getLockType(), context, change.getCount());

          stateObjects.add(object);

          if (newCount != oldCount)
          {
            deltas.add(new LockDeltaRecord<>(change.getOperation(), object, change.getLockType(), oldCount, newCount));
          }
        }
        else
        {
          LockType[] types = change.getLockType() == null ? ALL_LOCK_TYPES_ARRAY : LOCK_TYPE_ARRAYS[change.getLockType().ordinal()];

          for (LockType type : types)
          {
            if (state.canUnlock(type, context))
            {
              int oldCount = state.getLockCount(type, context);
              int newCount = state.unlock(type, context, change.getCount());

              stateObjects.add(object);

              if (newCount != oldCount)
              {
                deltas.add(new LockDeltaRecord<>(change.getOperation(), object, type, oldCount, newCount));
              }
            }
          }
        }
      }
    }

    return true;
  }

  private void applyChange(CONTEXT context, LockChange<OBJECT> change, Set<OBJECT> stateObjects, List<LockDeltaRecord<OBJECT>> deltas,
      Map<OBJECT, LockState<OBJECT, CONTEXT>> affectedStates)
  {
    if (change.getCount() == 0)
    {
      return;
    }

    List<OBJECT> targets = new ArrayList<>();

    Collection<? extends OBJECT> objects = change.getObjects();
    if (objects == null)
    {
      Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
      if (lockStates != null)
      {
        for (LockState<OBJECT, CONTEXT> state : new ArrayList<>(lockStates))
        {
          targets.add(state.getLockedObject());
        }
      }
    }
    else
    {
      targets.addAll(objects);
    }

    for (OBJECT object : targets)
    {
      LockState<OBJECT, CONTEXT> state = change.getOperation() == Operation.LOCK ? getOrCreateLockState(object) : objectToLockStateMap.get(object);
      if (state == null)
      {
        continue;
      }

      if (change.getOperation() == Operation.LOCK)
      {
        int oldCount = state.getLockCount(change.getLockType(), context);
        int newCount = state.lock(change.getLockType(), context, change.getCount());

        stateObjects.add(object);

        if (newCount != oldCount)
        {
          deltas.add(new LockDeltaRecord<>(change.getOperation(), object, change.getLockType(), oldCount, newCount));
        }

        addContextToLockStateMapping(context, state);
      }
      else
      {
        LockType[] types = change.getLockType() == null ? ALL_LOCK_TYPES_ARRAY : LOCK_TYPE_ARRAYS[change.getLockType().ordinal()];

        for (LockType type : types)
        {
          if (state.canUnlock(type, context))
          {
            int oldCount = state.getLockCount(type, context);
            int newCount = state.unlock(type, context, change.getCount());

            stateObjects.add(object);

            if (newCount != oldCount)
            {
              deltas.add(new LockDeltaRecord<>(change.getOperation(), object, type, oldCount, newCount));
            }
          }
        }

        if (state.hasLocks(context))
        {
          addContextToLockStateMapping(context, state);
        }
        else
        {
          Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
          if (lockStates != null)
          {
            lockStates.remove(state);
            if (lockStates.isEmpty())
            {
              contextToLockStates.remove(context);
            }
          }
        }
      }

      affectedStates.put(object, state);
    }
  }

  private boolean isEffectiveRequest(CONTEXT context, LockChange<OBJECT> change)
  {
    Collection<? extends OBJECT> objects = change.getObjects();
    if (change.getOperation() == Operation.UNLOCK && objects != null && !objects.isEmpty())
    {
      return true;
    }

    if (change.getCount() == 0)
    {
      return false;
    }

    if (objects == null)
    {
      return !ObjectUtil.isEmpty(contextToLockStates.get(context));
    }

    return !objects.isEmpty();
  }

  private long getDeadline(List<? extends LockChange<OBJECT>> changes)
  {
    long now = currentTimeMillis();
    long deadline = Long.MAX_VALUE;

    for (LockChange<OBJECT> change : changes)
    {
      if (change.getOperation() == Operation.LOCK && change.getTimeout() != NO_TIMEOUT)
      {
        deadline = Math.min(deadline, now + change.getTimeout());
      }
    }

    return deadline;
  }

  private TimeoutRuntimeException createTimeoutException(List<? extends LockChange<OBJECT>> changes, Map<OBJECT, LockState<OBJECT, CONTEXT>> states)
  {
    long timeout = Long.MAX_VALUE;

    for (LockChange<OBJECT> change : changes)
    {
      if (change.getOperation() == Operation.LOCK && change.getTimeout() != NO_TIMEOUT)
      {
        timeout = Math.min(timeout, change.getTimeout());
      }
    }

    StringJoiner joiner = new StringJoiner(", ", "Could not lock objects within " + timeout + " milliseconds: ", "");

    for (LockChange<OBJECT> change : changes)
    {
      if (change.getOperation() == Operation.LOCK && change.getObjects() != null)
      {
        for (OBJECT object : change.getObjects())
        {
          LockState<OBJECT, CONTEXT> state = states.get(object);
          if (state != null)
          {
            joiner.add(state.toString());
          }
        }
      }
    }

    return new TimeoutRuntimeException(joiner.toString());
  }

  private LockState<OBJECT, CONTEXT> getOrCreateLockState(OBJECT object)
  {
    return objectToLockStateMap.computeIfAbsent(object, o -> new LockState<>(o));
  }

  private void addContextToLockStateMapping(CONTEXT context, LockState<OBJECT, CONTEXT> lockState)
  {
    contextToLockStates.computeIfAbsent(context, c -> new HashSet<>()).add(lockState);
  }

  /**
   * @category Write Access
   */
  @Override
  public long unlock(CONTEXT context, Collection<? extends OBJECT> objects, LockType lockType, int count, //
      LockDeltaHandler<OBJECT, CONTEXT> deltaHandler, Consumer<LockState<OBJECT, CONTEXT>> stateHandler)
  {
    LockChange<OBJECT> change = new LockChange<>(Operation.UNLOCK, objects, lockType, count, NO_TIMEOUT);

    try
    {
      return changeLocks(context, Collections.singletonList(change), adapt(deltaHandler, Operation.UNLOCK), stateHandler);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw WrappedException.wrap(ex);
    }
  }

  private DeltaHandler<OBJECT, CONTEXT> adapt(LockDeltaHandler<OBJECT, CONTEXT> handler, Operation operation)
  {
    return handler == null //
        ? null //
        : (op, context, object, lockType, oldCount, newCount) //
        -> handler.handleLockDelta(context, object, lockType, oldCount, newCount);
  }

  /**
   * @category Read Access
   */
  @Override
  public boolean hasLock(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    try (Access access = read.access())
    {
      LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(objectToLock);
      return lockState != null && lockState.hasLock(type, context, false);
    }
  }

  /**
   * @category Read Access
   */
  @Override
  public boolean hasLockByOthers(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    try (Access access = read.access())
    {
      LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(objectToLock);
      return lockState != null && lockState.hasLock(type, context, true);
    }
  }

  /**
   * @category Read Access
   */
  public LockState<OBJECT, CONTEXT> getLockState(OBJECT key)
  {
    try (Access access = read.access())
    {
      return objectToLockStateMap.get(key);
    }
  }

  /**
   * @since 3.16
   * @category Read Access
   */
  public void getLockStates(Collection<OBJECT> keys, BiConsumer<OBJECT, LockState<OBJECT, CONTEXT>> consumer)
  {
    try (Access access = read.access())
    {
      keys.forEach(key -> {
        LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(key);
        consumer.accept(key, lockState);
      });
    }
  }

  /**
   * @since 3.16
   * @category Read Access
   */
  public void getLockStates(Consumer<LockState<OBJECT, CONTEXT>> consumer)
  {
    try (Access access = read.access())
    {
      objectToLockStateMap.values().forEach(consumer);
    }
  }

  protected void changeContext(CONTEXT oldContext, CONTEXT newContext)
  {
    try (Access access = write.access())
    {
      for (LockState<OBJECT, CONTEXT> lockState : objectToLockStateMap.values())
      {
        lockState.replaceContext(oldContext, newContext);
      }

      Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.remove(oldContext);
      if (lockStates != null)
      {
        contextToLockStates.put(newContext, lockStates);
      }
    }
  }

  /**
   * All access to the returned map must be properly synchronized on this {@link RWOLockManager}.
   */
  protected final Map<OBJECT, LockState<OBJECT, CONTEXT>> getObjectToLocksMap()
  {
    return objectToLockStateMap;
  }

  /**
   * All access to the returned map must be properly synchronized on this {@link RWOLockManager}.
   */
  protected final Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> getContextToLocksMap()
  {
    return contextToLockStates;
  }

  protected Map<OBJECT, LockState<OBJECT, CONTEXT>> createObjectToLocksMap()
  {
    return new HashMap<>();
  }

  protected Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> createContextToLocksMap()
  {
    return new HashMap<>();
  }

  protected long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }

  /**
   * @author Eike Stepper
   */
  private enum ExecutionMode
  {
    ATOMIC, PROGRESSIVE
  }

  /**
   * @author Eike Stepper
   */
  private static final class LockDeltaRecord<OBJECT>
  {
    private final Operation operation;

    private final OBJECT object;

    private final LockType lockType;

    private final int oldCount;

    private final int newCount;

    public LockDeltaRecord(Operation operation, OBJECT object, LockType lockType, int oldCount, int newCount)
    {
      this.operation = operation;
      this.object = object;
      this.lockType = lockType;
      this.oldCount = oldCount;
      this.newCount = newCount;
    }
  }

  /**
   * Represents a combination of locks for one OBJECT. The different lock types are represented by the values of the
   * enum {@link IRWLockManager.LockType}
   * <p>
   * The locking semantics established by this class are as follows:
   * <ul>
   * <li>A read lock prevents a write lock by another, but allows read locks by others and allows a write option by
   * another, and is therefore <b>non-exclusive</b>.
   * <li>A write lock prevents read locks by others, a write lock by another, and a write option by another, and is
   * therefore <b>exclusive</b>.
   * <li>A write option prevents write locks by others and a write option by another, but allows read locks by others,
   * and is therefore <b>exclusive</b>.
   * </ul>
   *
   * @author Caspar De Groot
   * @since 3.2
   * @param <OBJECT> the locked object type
   * @param <CONTEXT> the lock context type
   */
  public static class LockState<OBJECT, CONTEXT>
  {
    private final OBJECT lockedObject;

    private HashBag<CONTEXT> readLockOwners;

    private ReentrantOwner<CONTEXT> writeLockOwner;

    private ReentrantOwner<CONTEXT> writeOptionOwner;

    LockState(OBJECT lockedObject)
    {
      CheckUtil.checkArg(lockedObject, "lockedObject");
      this.lockedObject = lockedObject;
    }

    private LockState<OBJECT, CONTEXT> copy()
    {
      LockState<OBJECT, CONTEXT> copy = new LockState<>(lockedObject);

      if (readLockOwners != null)
      {
        copy.readLockOwners = new HashBag<>();
        for (CONTEXT context : readLockOwners)
        {
          copy.readLockOwners.add(context, readLockOwners.getCounterFor(context));
        }
      }

      if (writeLockOwner != null)
      {
        copy.writeLockOwner = writeLockOwner.copy();
      }

      if (writeOptionOwner != null)
      {
        copy.writeOptionOwner = writeOptionOwner.copy();
      }

      return copy;
    }

    public OBJECT getLockedObject()
    {
      return lockedObject;
    }

    /**
     * @since 3.16
     */
    public int getLockCount(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context"); //$NON-NLS-1$

      switch (type)
      {
      case READ:
        return readLockOwners == null ? 0 : readLockOwners.getCounterFor(context);

      case WRITE:
        return writeLockOwner != null && writeLockOwner.getContext() == context ? writeLockOwner.getCount() : 0;

      case OPTION:
        return writeOptionOwner != null && writeOptionOwner.getContext() == context ? writeOptionOwner.getCount() : 0;

      default:
        throw new AssertionError();
      }
    }

    public boolean hasLock(LockType type, CONTEXT context, boolean byOthers)
    {
      CheckUtil.checkArg(context, "context"); //$NON-NLS-1$

      switch (type)
      {
      case READ:
        if (readLockOwners == null)
        {
          return false;
        }

        if (byOthers)
        {
          int size = readLockOwners.size();
          return size > 1 || size == 1 && !readLockOwners.contains(context);
        }

        return readLockOwners.contains(context);

      case WRITE:
        if (writeLockOwner == null)
        {
          return false;
        }

        if (byOthers)
        {
          return writeLockOwner.getContext() != context;
        }

        return writeLockOwner.getContext() == context;

      case OPTION:
        if (writeOptionOwner == null)
        {
          return false;
        }

        if (byOthers)
        {
          return writeOptionOwner.getContext() != context;
        }

        return writeOptionOwner.getContext() == context;

      default:
        throw new AssertionError();
      }
    }

    public boolean hasLock(LockType type)
    {
      switch (type)
      {
      case READ:
        return readLockOwners != null;

      case WRITE:
        return writeLockOwner != null;

      case OPTION:
        return writeOptionOwner != null;

      default:
        throw new AssertionError();
      }
    }

    public Set<CONTEXT> getReadLockOwners()
    {
      if (readLockOwners == null)
      {
        return Collections.emptySet();
      }

      return Collections.unmodifiableSet(readLockOwners);
    }

    public CONTEXT getWriteLockOwner()
    {
      return writeLockOwner == null ? null : writeLockOwner.getContext();
    }

    public CONTEXT getWriteOptionOwner()
    {
      return writeOptionOwner == null ? null : writeOptionOwner.getContext();
    }

    @Override
    public int hashCode()
    {
      return lockedObject.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (!(obj instanceof LockState))
      {
        return false;
      }

      LockState<?, ?> other = (LockState<?, ?>)obj;
      return lockedObject.equals(other.lockedObject);
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder("LockState[target=");
      builder.append(lockedObject);

      if (readLockOwners != null && readLockOwners.size() > 0)
      {
        builder.append(", read=");
        boolean first = true;
        for (CONTEXT context : readLockOwners)
        {
          if (first)
          {
            first = false;
          }
          else
          {
            builder.append(", ");
          }

          builder.append(context);
        }

        builder.deleteCharAt(builder.length() - 1);
      }

      if (writeLockOwner != null)
      {
        CONTEXT context = writeLockOwner.getContext();
        if (context != null)
        {
          builder.append(", write=");
          builder.append(context);
        }
      }

      if (writeOptionOwner != null)
      {
        CONTEXT context = writeOptionOwner.getContext();
        if (context != null)
        {
          builder.append(", option=");
          builder.append(context);
        }
      }

      builder.append(']');
      return builder.toString();
    }

    boolean canLock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context"); //$NON-NLS-1$
      switch (type)
      {
      case READ:
        return canLockRead(context);

      case WRITE:
        return canLockWrite(context);

      case OPTION:
        return canLockOption(context);

      default:
        throw new AssertionError();
      }
    }

    boolean canUnlock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context"); //$NON-NLS-1$
      switch (type)
      {
      case READ:
        return canUnlockRead(context);

      case WRITE:
        return canUnlockWrite(context);

      case OPTION:
        return canUnlockOption(context);

      default:
        throw new AssertionError();
      }
    }

    int lock(LockType type, CONTEXT context, int count)
    {
      CheckUtil.checkArg(context, "context"); //$NON-NLS-1$
      switch (type)
      {
      case READ:
        return doLockRead(context, count);

      case WRITE:
        return doLockWrite(context, count);

      case OPTION:
        return doLockOption(context, count);

      default:
        throw new AssertionError();
      }
    }

    int unlock(LockType type, CONTEXT context, int count)
    {
      CheckUtil.checkArg(context, "context"); //$NON-NLS-1$
      switch (type)
      {
      case READ:
        return doUnlockRead(context, count);

      case WRITE:
        return doUnlockWrite(context, count);

      case OPTION:
        return doUnlockOption(context, count);

      default:
        throw new AssertionError();
      }
    }

    void replaceContext(CONTEXT oldContext, CONTEXT newContext)
    {
      if (readLockOwners != null)
      {
        int readLocksOwnedByOldView = readLockOwners.getCounterFor(oldContext);
        if (readLocksOwnedByOldView > 0)
        {
          for (int i = 0; i < readLocksOwnedByOldView; i++)
          {
            readLockOwners.remove(oldContext);
            readLockOwners.add(newContext);
          }
        }
      }

      if (writeLockOwner != null && ObjectUtil.equals(writeLockOwner.getContext(), oldContext))
      {
        writeLockOwner.setContext(newContext);
      }

      if (writeOptionOwner != null && ObjectUtil.equals(writeOptionOwner.getContext(), oldContext))
      {
        writeOptionOwner.setContext(newContext);
      }
    }

    boolean hasNoLocks()
    {
      return ObjectUtil.isEmpty(readLockOwners) && writeLockOwner == null && writeOptionOwner == null;
    }

    boolean hasLocks(CONTEXT context)
    {
      return readLockOwners != null && readLockOwners.contains(context) //
          || writeLockOwner != null && writeLockOwner.getContext() == context //
          || writeOptionOwner != null && writeOptionOwner.getContext() == context;
    }

    private boolean canLockRead(CONTEXT context)
    {
      if (writeLockOwner != null && writeLockOwner.getContext() != context)
      {
        return false;
      }

      return true;
    }

    private boolean canLockWrite(CONTEXT context)
    {
      // If another context owns a writeLock, we can't write-lock.
      if (writeLockOwner != null && writeLockOwner.getContext() != context)
      {
        return false;
      }

      // If another context owns a writeOption, we can't write-lock.
      if (writeOptionOwner != null && writeOptionOwner.getContext() != context)
      {
        return false;
      }

      // If another context owns a readLock, we can't write-lock.
      if (readLockOwners != null)
      {
        if (readLockOwners.size() > 1)
        {
          return false;
        }

        if (readLockOwners.size() == 1)
        {
          if (!readLockOwners.contains(context))
          {
            return false;
          }
        }
      }

      return true;
    }

    private boolean canLockOption(CONTEXT context)
    {
      if (writeOptionOwner != null && writeOptionOwner.getContext() != context)
      {
        return false;
      }

      if (writeLockOwner != null && writeLockOwner.getContext() != context)
      {
        return false;
      }

      return true;
    }

    private boolean canUnlockRead(CONTEXT context)
    {
      if (readLockOwners == null || !readLockOwners.contains(context))
      {
        return false;
      }

      return true;
    }

    private boolean canUnlockWrite(CONTEXT context)
    {
      if (writeLockOwner == null || writeLockOwner.getContext() != context)
      {
        return false;
      }

      return true;
    }

    private boolean canUnlockOption(CONTEXT context)
    {
      if (writeOptionOwner == null || writeOptionOwner.getContext() != context)
      {
        return false;
      }

      return true;
    }

    private int doLockRead(CONTEXT context, int count)
    {
      if (readLockOwners == null)
      {
        readLockOwners = new HashBag<>();
      }

      return readLockOwners.addAndGet(context, count);
    }

    private int doLockWrite(CONTEXT context, int count)
    {
      if (writeLockOwner == null)
      {
        writeLockOwner = new ReentrantOwner<>(context);
      }

      return writeLockOwner.changeCount(count);
    }

    private int doLockOption(CONTEXT context, int count)
    {
      if (writeOptionOwner == null)
      {
        writeOptionOwner = new ReentrantOwner<>(context);
      }

      return writeOptionOwner.changeCount(count);
    }

    private int doUnlockRead(CONTEXT context, int count)
    {
      if (readLockOwners == null)
      {
        return 0;
      }

      if (count == ALL_LOCKS)
      {
        readLockOwners.removeCounterFor(context);
        if (readLockOwners.isEmpty())
        {
          readLockOwners = null;
        }

        return 0;
      }

      int newCount = readLockOwners.removeAndGet(context, count);
      if (readLockOwners.isEmpty())
      {
        readLockOwners = null;
      }

      return newCount;
    }

    private int doUnlockWrite(CONTEXT context, int count)
    {
      if (count == ALL_LOCKS)
      {
        writeLockOwner = null;
        return 0;
      }

      int newCount = writeLockOwner.changeCount(-count);
      if (newCount == 0)
      {
        writeLockOwner = null;
      }

      return newCount;
    }

    private int doUnlockOption(CONTEXT context, int count)
    {
      if (count == ALL_LOCKS)
      {
        writeOptionOwner = null;
        return 0;
      }

      int newCount = writeOptionOwner.changeCount(-count);
      if (newCount == 0)
      {
        writeOptionOwner = null;
      }

      return newCount;
    }

    /**
     * @author Eike Stepper
     */
    private static final class ReentrantOwner<CONTEXT>
    {
      private CONTEXT context;

      private int count;

      public ReentrantOwner(CONTEXT context)
      {
        this.context = context;
      }

      public ReentrantOwner<CONTEXT> copy()
      {
        ReentrantOwner<CONTEXT> copy = new ReentrantOwner<>(context);
        copy.count = count;
        return copy;
      }

      public CONTEXT getContext()
      {
        return context;
      }

      public void setContext(CONTEXT context)
      {
        this.context = context;
      }

      public int getCount()
      {
        return count;
      }

      public int changeCount(int delta)
      {
        return count += delta;
      }
    }
  }

  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> getLockStates()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @category Write Access
   */
  @Deprecated
  public void setLockState(OBJECT key, LockState<OBJECT, CONTEXT> lockState)
  {
    try (Access access = write.access())
    {
      objectToLockStateMap.put(key, lockState);

      for (CONTEXT readLockOwner : lockState.getReadLockOwners())
      {
        addContextToLockStateMapping(readLockOwner, lockState);
      }

      CONTEXT writeLockOwner = lockState.getWriteLockOwner();
      if (writeLockOwner != null)
      {
        addContextToLockStateMapping(writeLockOwner, lockState);
      }

      CONTEXT writeOptionOwner = lockState.getWriteOptionOwner();
      if (writeOptionOwner != null)
      {
        addContextToLockStateMapping(writeOptionOwner, lockState);
      }
    }
  }

  @Override
  @Deprecated
  public void lock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> lock2(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout)
      throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void lock(LockType type, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(CONTEXT context)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> unlock2(CONTEXT context)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> unlock2(CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> unlock2(LockType lockType, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public static void setUnlockAll(boolean on)
  {
    throw new UnsupportedOperationException();
  }
}
