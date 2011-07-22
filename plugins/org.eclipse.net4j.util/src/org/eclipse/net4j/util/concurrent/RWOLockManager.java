/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Caspar De Groot
 * @since 3.2
 */
public class RWOLockManager<OBJECT, CONTEXT> extends Lifecycle implements IRWLockManager<OBJECT, CONTEXT>
{
  // TODO (CD) Ensure that CDOID and CDOIDandBranch have good hashCode implementations
  private final Map<OBJECT, LockState<OBJECT, CONTEXT>> objectToLocksMap = createObjectToLocksMap();

  // TODO (CD) Ensure that IView has a good hashCode implementation
  private final Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> contextToLocksMap = createContextToLocksMap();

  public void lock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout)
      throws InterruptedException
  {
    if (objectsToLock.isEmpty())
    {
      return;
    }

    // Must come before the synchronized block!
    long startTime = timeout == WAIT ? 0L : currentTimeMillis();

    // Do not synchronize the entire method as it would corrupt the timeout!
    synchronized (this)
    {
      int count = objectsToLock.size();
      LockState<?, ?>[] lockStates = new LockState<?, ?>[count];

      for (;;)
      {
        if (canLockInContext(type, context, objectsToLock, lockStates))
        {
          for (int i = 0; i < count; i++)
          {
            @SuppressWarnings("unchecked")
            LockState<OBJECT, CONTEXT> lockState = (LockState<OBJECT, CONTEXT>)lockStates[i];
            lockState.lock(type, context);
            addLockToContext(context, lockState);
          }

          return;
        }

        wait(startTime, timeout);
      }
    }
  }

  public void lock(LockType type, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException
  {
    // Do not synchronize the entire method as it would corrupt the timeout!
    lock(type, context, Collections.singleton(objectToLock), timeout);
  }

  public synchronized void unlock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    if (objectsToUnlock.isEmpty())
    {
      return;
    }

    List<LockState<OBJECT, CONTEXT>> lockStates = new LinkedList<LockState<OBJECT, CONTEXT>>();

    Iterator<? extends OBJECT> it = objectsToUnlock.iterator();
    while (it.hasNext())
    {
      OBJECT o = it.next();
      LockState<OBJECT, CONTEXT> lockState = objectToLocksMap.get(o);
      if (lockState == null || !lockState.canUnlock(type, context))
      {
        throw new IllegalMonitorStateException();
      }

      lockStates.add(lockState);
    }

    for (LockState<OBJECT, CONTEXT> lockState : lockStates)
    {
      lockState.unlock(type, context);
      if (!lockState.hasLocks(context))
      {
        removeLockFromContext(context, lockState);
      }

      if (lockState.hasNoLocks())
      {
        objectToLocksMap.remove(lockState.getLockedObject());
      }
    }

    notifyAll();
  }

  public synchronized void unlock(CONTEXT context)
  {
    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLocksMap.get(context);
    if (lockStates == null)
    {
      return;
    }

    List<OBJECT> objectsWithoutLocks = new LinkedList<OBJECT>();

    for (LockState<OBJECT, CONTEXT> lockState : lockStates)
    {
      for (LockType lockType : LockType.values())
      {
        if (lockState.hasLock(lockType, context, false))
        {
          lockState.unlock(lockType, context);
        }

        // TODO (CD) Consider whether WRITE_OPTIONs should be excluded from this...
      }

      removeLockFromContext(context, lockState);
      if (lockState.hasNoLocks())
      {
        OBJECT o = lockState.getLockedObject();
        objectsWithoutLocks.add(o);
      }
    }

    // This must be done outside the above iteration, in order to avoid ConcurrentModEx
    for (OBJECT o : objectsWithoutLocks)
    {
      objectToLocksMap.remove(o);
    }

    notifyAll();
  }

  public synchronized boolean hasLock(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    // TODO (CD) Should this be synced?
    LockState<OBJECT, CONTEXT> lockState = objectToLocksMap.get(objectToLock);
    return lockState != null && lockState.hasLock(type, context, false);
  }

  public synchronized boolean hasLockByOthers(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    // TODO (CD) Should this be synced?
    LockState<OBJECT, CONTEXT> lockState = objectToLocksMap.get(objectToLock);
    return lockState != null && lockState.hasLock(type, context, true);
  }

  protected synchronized void changeContext(CONTEXT oldContext, CONTEXT newContext)
  {
    for (LockState<OBJECT, CONTEXT> lockState : objectToLocksMap.values())
    {
      lockState.replaceContext(oldContext, newContext);
    }
  }

  protected long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }

  protected Map<OBJECT, LockState<OBJECT, CONTEXT>> createObjectToLocksMap()
  {
    return new HashMap<OBJECT, LockState<OBJECT, CONTEXT>>();
  }

  protected Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> createContextToLocksMap()
  {
    return new HashMap<CONTEXT, Set<LockState<OBJECT, CONTEXT>>>();
  }

  /**
   * All access to the returned map must be properly synchronized on this {@link RWOLockManager}.
   */
  protected final Map<OBJECT, LockState<OBJECT, CONTEXT>> getObjectToLocksMap()
  {
    return objectToLocksMap;
  }

  /**
   * All access to the returned map must be properly synchronized on this {@link RWOLockManager}.
   */
  protected final Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> getContextToLocksMap()
  {
    return contextToLocksMap;
  }

  private LockState<OBJECT, CONTEXT> getOrCreateLockState(OBJECT o)
  {
    LockState<OBJECT, CONTEXT> lockState = objectToLocksMap.get(o);
    if (lockState == null)
    {
      lockState = new LockState<OBJECT, CONTEXT>(o);
      objectToLocksMap.put(o, lockState);
    }

    return lockState;
  }

  private boolean canLockInContext(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock,
      LockState<?, ?>[] lockStatesToFill)
  {
    Iterator<? extends OBJECT> it = objectsToLock.iterator();
    for (int i = 0; i < lockStatesToFill.length; i++)
    {
      OBJECT o = it.next();
      LockState<OBJECT, CONTEXT> lockState = getOrCreateLockState(o);
      if (!lockState.canLock(type, context))
      {
        return false;
      }

      lockStatesToFill[i] = lockState;
    }

    return true;
  }

  private void addLockToContext(CONTEXT context, LockState<OBJECT, CONTEXT> lockState)
  {
    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLocksMap.get(context);
    if (lockStates == null)
    {
      lockStates = new HashSet<LockState<OBJECT, CONTEXT>>();
      contextToLocksMap.put(context, lockStates);
    }

    lockStates.add(lockState);
  }

  private void removeLockFromContext(CONTEXT context, LockState<OBJECT, CONTEXT> lockState)
  {
    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLocksMap.get(context);
    lockStates.remove(lockState);
    if (lockStates.isEmpty())
    {
      contextToLocksMap.remove(context);
    }
  }

  private void wait(long startTime, long timeout) throws InterruptedException
  {
    if (timeout == WAIT)
    {
      wait();
    }
    else
    {
      long elapsedTime = currentTimeMillis() - startTime;
      long waitTime = timeout - elapsedTime;
      if (waitTime < 1)
      {
        throw new TimeoutRuntimeException("Could not lock objects within " + timeout + " milli seconds");
      }

      wait(waitTime);
    }
  }

  /**
   * Represents a combination of locks for one OBJECT.
   * 
   * @author Caspar De Groot
   * @since 3.2
   */
  protected static class LockState<OBJECT, CONTEXT>
  {
    private final OBJECT lockedObject;

    // TODO (CD) Ensure that IView has a good hashCode implementation
    private final HashBag<CONTEXT> readLockOwners = new HashBag<CONTEXT>();

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

    public boolean hasLock(org.eclipse.net4j.util.concurrent.IRWLockManager.LockType type, CONTEXT view,
        boolean byOthers)
    {
      CheckUtil.checkArg(view, "view");

      switch (type)
      {
      case READ:
        if (byOthers)
        {
          return readLockOwners.size() > 1 || readLockOwners.size() == 1 && !readLockOwners.contains(view);
        }

        return readLockOwners.contains(view);

      case WRITE:
        if (byOthers)
        {
          return writeLockOwner != null && writeLockOwner != view;
        }

        return writeLockOwner == view;

      case OPTION:
        if (byOthers)
        {
          return writeOptionOwner != null && writeOptionOwner != view;
        }

        return writeOptionOwner == view;
      }

      return false;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder("LockState[target=");
      builder.append(lockedObject);

      if (readLockOwners.size() > 0)
      {
        builder.append(", read=");
        boolean first = true;
        for (CONTEXT view : readLockOwners)
        {
          if (first)
          {
            first = false;
          }
          else
          {
            builder.append(", ");
          }

          builder.append(view);
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

    void lock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        doReadLock(context);
        return;

      case WRITE:
        doWriteLock(context);
        return;

      case OPTION:
        doWriteOption(context);
        return;
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    boolean canLock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        return canReadLock(context);

      case WRITE:
        return canWriteLock(context);

      case OPTION:
        return canWriteOption(context);
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    boolean canUnlock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        return canReadUnlock(context);

      case WRITE:
        return canWriteUnlock(context);

      case OPTION:
        return canWriteUnoption(context);
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    void unlock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        doReadUnlock(context);
        return;

      case WRITE:
        doWriteUnlock(context);
        return;

      case OPTION:
        doWriteUnoption(context);
        return;
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    void replaceContext(CONTEXT oldContext, CONTEXT newContext)
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
      return readLockOwners.isEmpty() && writeLockOwner == null && writeOptionOwner == null;
    }

    boolean hasLocks(CONTEXT context)
    {
      return readLockOwners.contains(context) || writeLockOwner == context || writeOptionOwner == context;
    }

    private boolean canReadLock(CONTEXT context)
    {
      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doReadLock(CONTEXT context)
    {
      readLockOwners.add(context);
    }

    private boolean canWriteLock(CONTEXT context)
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

      return true;
    }

    private void doWriteLock(CONTEXT context)
    {
      writeLockOwner = context;
      writeLockCounter++;
    }

    private boolean canWriteOption(CONTEXT context)
    {
      if (writeOptionOwner != null && writeOptionOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doWriteOption(CONTEXT context)
    {
      writeOptionOwner = context;
    }

    private boolean canReadUnlock(CONTEXT context)
    {
      if (!readLockOwners.contains(context))
      {
        return false;
      }

      return true;
    }

    private void doReadUnlock(CONTEXT context)
    {
      readLockOwners.remove(context);
    }

    private boolean canWriteUnlock(CONTEXT context)
    {
      if (writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doWriteUnlock(CONTEXT context)
    {
      writeLockCounter--;
      if (writeLockCounter == 0)
      {
        writeLockOwner = null;
      }
    }

    private boolean canWriteUnoption(CONTEXT context)
    {
      if (writeOptionOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doWriteUnoption(CONTEXT context)
    {
      writeOptionOwner = null;
    }
  }
}
