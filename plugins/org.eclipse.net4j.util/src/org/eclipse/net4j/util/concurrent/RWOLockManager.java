/*
 * Copyright (c) 2011, 2012, 2014-2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Eike Stepper - major reimplementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    CheckUtil.checkArg(context, "context");
    CheckUtil.checkArg(objects, "objects");
    CheckUtil.checkArg(lockType, "lockType");
    CheckUtil.checkArg(count >= 0, "count >= 0");

    long deadline = timeout == NO_TIMEOUT ? Long.MAX_VALUE : currentTimeMillis() + timeout;

    try (Access access = write.access())
    {
      if (ObjectUtil.isEmpty(objects) || count == 0)
      {
        // Nothing to do.
        return modCount;
      }

      // Populate a mutable list of objects to lock. This list will shrink while objects are successfully locked below.
      List<OBJECT> objectsToLock = new ArrayList<>(objects);

      // Remember the locked objects for the case that an exception occurs, so that their locks can be removed again.
      List<OBJECT> lockedObjects = new ArrayList<>(objectsToLock.size());

      for (;;)
      {
        for (Iterator<OBJECT> it = objectsToLock.iterator(); it.hasNext();)
        {
          OBJECT lockedObject = it.next();
          LockState<OBJECT, CONTEXT> lockState = getOrCreateLockState(lockedObject);

          if (lockState.canLock(lockType, context))
          {
            int oldCount = lockState.getLockCount(lockType, context);
            int newCount = lockState.lock(lockType, context, count);

            if (newCount != oldCount)
            {
              addContextToLockStateMapping(context, lockState);

              if (deltaHandler != null)
              {
                deltaHandler.handleLockDelta(context, lockedObject, lockType, oldCount, newCount);
              }
            }

            if (stateHandler != null)
            {
              stateHandler.accept(lockState);
            }

            lockedObjects.add(lockedObject);
            it.remove();
          }
        }

        if (objectsToLock.isEmpty())
        {
          return ++modCount;
        }

        try
        {
          long waitTime = deadline - currentTimeMillis();
          if (waitTime <= 0)
          {
            StringBuilder builder = new StringBuilder();
            builder.append("Could not lock objects within ");
            builder.append(timeout);
            builder.append(" milliseconds: ");

            StringJoiner joiner = new StringJoiner(", ", "Could not lock objects within " + timeout + " milliseconds: ", "");
            for (OBJECT objectToLock : objectsToLock)
            {
              LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(objectToLock);
              if (lockState != null)
              {
                joiner.add(lockState.toString());
              }
            }

            throw new TimeoutRuntimeException(builder.toString());
          }

          // Give others a chance to unlock objects.
          unlocked.await(waitTime, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | TimeoutRuntimeException ex)
        {
          unlock(context, lockedObjects, lockType, count, null, null);

          --modCount; // Fix the modCount that was increased by unlock();
          throw ex;
        }
      }
    }
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
    CheckUtil.checkArg(context, "context");
    CheckUtil.checkArg(count >= -1, "count >= -1");

    LockType[] lockTypes = lockType == null ? ALL_LOCK_TYPES_ARRAY : LOCK_TYPE_ARRAYS[lockType.ordinal()];
    List<LockState<OBJECT, CONTEXT>> modifiedLockStates = new ArrayList<>();

    try (Access access = write.access())
    {
      if (objects == null)
      {
        if (count == 0)
        {
          // Nothing to do.
          return modCount;
        }

        Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
        if (lockStates == null)
        {
          // We have no locks, nothing to do.
          return modCount;
        }

        for (LockState<OBJECT, CONTEXT> lockState : lockStates)
        {
          OBJECT object = lockState.getLockedObject();
          doUnlock(context, object, lockTypes, count, deltaHandler, stateHandler, lockState, modifiedLockStates);
        }
      }
      else
      {
        if (objects.isEmpty())
        {
          return modCount;
        }

        for (OBJECT object : objects)
        {
          LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(object);
          if (lockState != null)
          {
            doUnlock(context, object, lockTypes, count, deltaHandler, stateHandler, lockState, modifiedLockStates);
          }
        }
      }

      removeLockStates(context, modifiedLockStates);

      // Wake up blocked lockers.
      unlocked.signalAll();

      return ++modCount;
    }
  }

  private void doUnlock(CONTEXT context, OBJECT object, LockType[] lockTypes, int count, //
      LockDeltaHandler<OBJECT, CONTEXT> deltaHandler, Consumer<LockState<OBJECT, CONTEXT>> stateHandler, //
      LockState<OBJECT, CONTEXT> lockState, List<LockState<OBJECT, CONTEXT>> modifiedLockStates)
  {
    for (LockType lockType : lockTypes)
    {
      if (lockState.canUnlock(lockType, context))
      {
        int oldCount = lockState.getLockCount(lockType, context);
        int newCount = lockState.unlock(lockType, context, count);

        if (newCount != oldCount)
        {
          modifiedLockStates.add(lockState);

          if (deltaHandler != null)
          {
            deltaHandler.handleLockDelta(context, lockState.getLockedObject(), lockType, oldCount, newCount);
          }
        }

        if (stateHandler != null)
        {
          stateHandler.accept(lockState);
        }
      }
    }
  }

  private void removeLockStates(CONTEXT context, List<LockState<OBJECT, CONTEXT>> modifiedLockStates)
  {
    for (LockState<OBJECT, CONTEXT> lockState : modifiedLockStates)
    {
      if (!lockState.hasLocks(context))
      {
        removeLockStateForContext(context, lockState);
      }

      if (lockState.hasNoLocks())
      {
        objectToLockStateMap.remove(lockState.getLockedObject());
      }
    }
  }

  /**
   * Removes a lockState from the set of all lockStates that the given context is involved in. If the lockState being
   * removed is the last one for the given context, then the set becomes empty, and is therefore removed from the
   * contextToLockStates map.
   */
  private void removeLockStateForContext(CONTEXT context, LockState<OBJECT, CONTEXT> lockState)
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
   */
  public static class LockState<OBJECT, CONTEXT>
  {
    private final OBJECT lockedObject;

    private HashBag<CONTEXT> readLockOwners;

    private CONTEXT writeLockOwner;

    private CONTEXT writeOptionOwner;

    private int writeLockCounter;

    LockState(OBJECT lockedObject)
    {
      CheckUtil.checkArg(lockedObject, "lockedObject");
      this.lockedObject = lockedObject;
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
      CheckUtil.checkArg(context, "context");

      switch (type)
      {
      case READ:
        return readLockOwners == null ? 0 : readLockOwners.getCounterFor(context);

      case WRITE:
        return writeLockOwner == context ? writeLockCounter : 0;

      case OPTION:
        return writeOptionOwner == context ? 1 : 0;

      default:
        throw new AssertionError();
      }
    }

    public boolean hasLock(LockType type, CONTEXT context, boolean byOthers)
    {
      CheckUtil.checkArg(context, "context");

      switch (type)
      {
      case READ:
        if (readLockOwners == null)
        {
          return false;
        }

        if (byOthers)
        {
          return readLockOwners.size() > 1 || readLockOwners.size() == 1 && !readLockOwners.contains(context);
        }

        return readLockOwners.contains(context);

      case WRITE:
        if (byOthers)
        {
          return writeLockOwner != null && writeLockOwner != context;
        }

        return writeLockOwner == context;

      case OPTION:
        if (byOthers)
        {
          return writeOptionOwner != null && writeOptionOwner != context;
        }

        return writeOptionOwner == context;

      default:
        throw new AssertionError();
      }
    }

    public boolean hasLock(LockType type)
    {
      switch (type)
      {
      case READ:
        return readLockOwners != null && readLockOwners.size() > 0;

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
      return writeLockOwner;
    }

    public CONTEXT getWriteOptionOwner()
    {
      return writeOptionOwner;
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
        builder.append(", write=");
        builder.append(writeLockOwner);
      }

      if (writeOptionOwner != null)
      {
        builder.append(", option=");
        builder.append(writeOptionOwner);
      }

      builder.append(']');
      return builder.toString();
    }

    boolean canLock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
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
      CheckUtil.checkArg(context, "context");
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
      CheckUtil.checkArg(context, "context");
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
      CheckUtil.checkArg(context, "context");
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

      if (ObjectUtil.equals(writeLockOwner, oldContext))
      {
        writeLockOwner = newContext;
      }

      if (ObjectUtil.equals(writeOptionOwner, oldContext))
      {
        writeOptionOwner = newContext;
      }
    }

    boolean hasNoLocks()
    {
      return ObjectUtil.isEmpty(readLockOwners) && writeLockOwner == null && writeOptionOwner == null;
    }

    boolean hasLocks(CONTEXT context)
    {
      return readLockOwners != null && readLockOwners.contains(context) || writeLockOwner == context || writeOptionOwner == context;
    }

    private boolean canLockRead(CONTEXT context)
    {
      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private boolean canLockWrite(CONTEXT context)
    {
      // If another context owns a writeLock, we can't write-lock
      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      // If another context owns a writeOption, we can't write-lock
      if (writeOptionOwner != null && writeOptionOwner != context)
      {
        return false;
      }

      // If another context owns a readLock, we can't write-lock
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
      if (writeOptionOwner != null && writeOptionOwner != context)
      {
        return false;
      }

      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private boolean canUnlockRead(CONTEXT context)
    {
      if (readLockOwners != null && !readLockOwners.contains(context))
      {
        return false;
      }

      return true;
    }

    private boolean canUnlockWrite(CONTEXT context)
    {
      if (writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private boolean canUnlockOption(CONTEXT context)
    {
      if (writeOptionOwner != context)
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
      writeLockOwner = context;
      return writeLockCounter += count;
    }

    private int doLockOption(CONTEXT context, int count)
    {
      writeOptionOwner = context;
      return 1;
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
        writeLockCounter = 0;
        return 0;
      }

      writeLockCounter -= count;
      if (writeLockCounter == 0)
      {
        writeLockOwner = null;
      }

      return writeLockCounter;
    }

    private int doUnlockOption(CONTEXT context, int count)
    {
      writeOptionOwner = null;
      return 0;
    }
  }
}
