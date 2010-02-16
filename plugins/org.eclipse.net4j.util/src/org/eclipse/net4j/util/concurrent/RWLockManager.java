/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Support Multiple reads/no write and upgrade lock from read to write. Many context could request
 * {@link LockType#WRITE write} lock at the same time. It will privileges first context that has already a
 * {@link LockType#READ read} lock. If no one has any read lock, it's "first come first serve".
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class RWLockManager<OBJECT, CONTEXT> extends Lifecycle implements IRWLockManager<OBJECT, CONTEXT>
{
  private LockStrategy<OBJECT, CONTEXT> writeLockStrategy = new LockStrategy<OBJECT, CONTEXT>()
  {
    public boolean canObtainLock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.canObtainWriteLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> lock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.writeLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> unlock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.writeUnlock(context);
    }

    public boolean isLocked(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isWriteLock(context);
    }

    public boolean isLockedByOthers(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isWriteLockByOthers(context);
    }
  };

  private LockStrategy<OBJECT, CONTEXT> readLockStrategy = new LockStrategy<OBJECT, CONTEXT>()
  {
    public boolean canObtainLock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.canObtainReadLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> lock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.readLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> unlock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.readUnlock(context);
    }

    public boolean isLocked(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isReadLock(context);
    }

    public boolean isLockedByOthers(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isReadLockByOthers(context);
    }
  };

  private Map<OBJECT, LockEntry<OBJECT, CONTEXT>> lockEntries = new HashMap<OBJECT, LockEntry<OBJECT, CONTEXT>>();

  private Object lockChanged = new Object();

  /**
   * @since 3.0
   */
  public void lock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout)
      throws InterruptedException
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    lock(lockingStrategy, context, objectsToLock, timeout);
  }

  /**
   * @since 3.0
   */
  public void lock(LockType type, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException
  {
    List<OBJECT> objectsToLock = Collections.singletonList(objectToLock);
    lock(type, context, objectsToLock, timeout);
  }

  /**
   * Attempts to release for a given locktype, context and objects.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking objects without lock.
   * @since 3.0
   */
  public void unlock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    unlock(lockingStrategy, context, objectsToUnlock);
  }

  /**
   * Attempts to release all locks(read and write) for a given context.
   */
  public void unlock(CONTEXT context)
  {
    synchronized (lockChanged)
    {
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToRemove = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToAdd = new ArrayList<LockEntry<OBJECT, CONTEXT>>();

      for (Entry<OBJECT, LockEntry<OBJECT, CONTEXT>> entry : lockEntries.entrySet())
      {
        LockEntry<OBJECT, CONTEXT> lockedContext = entry.getValue();
        LockEntry<OBJECT, CONTEXT> newEntry = lockedContext.clearLock(context);
        if (newEntry == null)
        {
          lockEntrysToRemove.add(lockedContext);
        }
        else if (newEntry != entry)
        {
          lockEntrysToAdd.add(newEntry);
        }
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToRemove)
      {
        OBJECT object = lockEntry.getKey();
        lockEntries.remove(object);
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToAdd)
      {
        OBJECT object = lockEntry.getKey();
        lockEntries.put(object, lockEntry);
      }

      lockChanged.notifyAll();
    }
  }

  /**
   * @since 3.0
   */
  public boolean hasLock(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    return hasLock(lockingStrategy, context, objectToLock);
  }

  /**
   * @since 3.0
   */
  public boolean hasLockByOthers(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    LockEntry<OBJECT, CONTEXT> entry = getLockEntry(objectToLock);
    return entry != null && lockingStrategy.isLockedByOthers(entry, context);
  }

  private LockStrategy<OBJECT, CONTEXT> getLockingStrategy(LockType type)
  {
    if (type == LockType.READ)
    {
      return readLockStrategy;
    }

    if (type == LockType.WRITE)
    {
      return writeLockStrategy;
    }

    throw new IllegalArgumentException("Invalid lock type: " + type);
  }

  /**
   * Attempts to release this lock.
   * <p>
   * If the number of context is now zero then the lock is made available for write lock attempts.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking object not locked.
   */
  private void unlock(LockStrategy<OBJECT, CONTEXT> lockingStrategy, CONTEXT context,
      Collection<? extends OBJECT> objectsToLock)
  {
    synchronized (lockChanged)
    {
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToRemove = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToAdd = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
      for (OBJECT objectToLock : objectsToLock)
      {
        LockEntry<OBJECT, CONTEXT> entry = lockEntries.get(objectToLock);
        if (entry == null)
        {
          throw new IllegalMonitorStateException();
        }

        LockEntry<OBJECT, CONTEXT> newEntry = lockingStrategy.unlock(entry, context);
        if (newEntry == null)
        {
          lockEntrysToRemove.add(entry);
        }
        else if (newEntry != entry)
        {
          lockEntrysToAdd.add(newEntry);
        }
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToRemove)
      {
        OBJECT object = lockEntry.getKey();
        lockEntries.remove(object);
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToAdd)
      {
        OBJECT object = lockEntry.getKey();
        lockEntries.put(object, lockEntry);
      }

      lockChanged.notifyAll();
    }
  }

  private boolean hasLock(LockStrategy<OBJECT, CONTEXT> lockingStrategy, CONTEXT context, OBJECT objectToLock)
  {
    LockEntry<OBJECT, CONTEXT> entry = getLockEntry(objectToLock);
    return entry != null && lockingStrategy.isLocked(entry, context);
  }

  private void lock(LockStrategy<OBJECT, CONTEXT> lockStrategy, CONTEXT context,
      Collection<? extends OBJECT> objectsToLocks, long timeout) throws InterruptedException
  {
    long startTime = System.currentTimeMillis();
    while (true)
    {
      synchronized (lockChanged)
      {
        OBJECT conflict = obtainLock(lockStrategy, context, objectsToLocks);
        if (conflict == null)
        {
          lockChanged.notifyAll();
          return;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (timeout != WAIT && elapsedTime > timeout)
        {
          throw new TimeoutRuntimeException("Conflict with " + conflict); //$NON-NLS-1$
        }

        if (timeout == WAIT)
        {
          lockChanged.wait();
        }
        else
        {
          lockChanged.wait(Math.max(1, timeout - elapsedTime));
        }
      }
    }
  }

  private OBJECT obtainLock(LockStrategy<OBJECT, CONTEXT> lockingStrategy, CONTEXT context,
      Collection<? extends OBJECT> objectsToLock)
  {
    List<LockEntry<OBJECT, CONTEXT>> lockEntrys = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
    for (OBJECT objectToLock : objectsToLock)
    {
      LockEntry<OBJECT, CONTEXT> entry = lockEntries.get(objectToLock);
      if (entry == null)
      {
        entry = new NoLockEntry<OBJECT, CONTEXT>(objectToLock);
      }

      if (lockingStrategy.canObtainLock(entry, context))
      {
        lockEntrys.add(entry);
      }
      else
      {
        return objectToLock;
      }
    }

    for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrys)
    {
      OBJECT object = lockEntry.getKey();
      LockEntry<OBJECT, CONTEXT> lock = lockingStrategy.lock(lockEntry, context);
      lockEntries.put(object, lock);
    }

    return null;
  }

  private LockEntry<OBJECT, CONTEXT> getLockEntry(OBJECT objectToLock)
  {
    synchronized (lockChanged)
    {
      return lockEntries.get(objectToLock);
    }
  }

  /**
   * @author Simon McDuff
   */
  private interface LockStrategy<K, V>
  {
    public boolean isLocked(LockEntry<K, V> entry, V context);

    public boolean canObtainLock(LockEntry<K, V> entry, V context);

    public LockEntry<K, V> lock(LockEntry<K, V> entry, V context);

    public LockEntry<K, V> unlock(LockEntry<K, V> entry, V context);

    public boolean isLockedByOthers(LockEntry<K, V> entry, V context);
  }

  /**
   * @author Simon McDuff
   */
  private interface LockEntry<K, V>
  {
    public K getKey();

    public boolean isReadLock(V context);

    public boolean isWriteLock(V context);

    public boolean isReadLockByOthers(V context);

    public boolean isWriteLockByOthers(V context);

    public boolean canObtainReadLock(V context);

    public boolean canObtainWriteLock(V context);

    public LockEntry<K, V> readLock(V context);

    public LockEntry<K, V> writeLock(V context);

    public LockEntry<K, V> readUnlock(V context);

    public LockEntry<K, V> writeUnlock(V context);

    public LockEntry<K, V> clearLock(V context);
  }

  /**
   * @author Simon McDuff
   */
  private static final class ReadLockEntry<K, V> implements LockEntry<K, V>
  {
    private K id;

    private Set<V> contexts = new HashBag<V>();

    public ReadLockEntry(K objectToLock, V context)
    {
      this.id = objectToLock;
      contexts.add(context);
    }

    public boolean canObtainReadLock(V context)
    {
      return true;
    }

    public boolean canObtainWriteLock(V context)
    {
      return contexts.size() == 1 && contexts.contains(context);
    }

    public LockEntry<K, V> readLock(V context)
    {
      contexts.add(context);
      return this;
    }

    public LockEntry<K, V> writeLock(V context)
    {
      return new WriteLockEntry<K, V>(id, context, this);
    }

    public K getKey()
    {
      return id;
    }

    public LockEntry<K, V> readUnlock(V context)
    {
      contexts.remove(context);
      return contexts.isEmpty() ? null : this;
    }

    public LockEntry<K, V> writeUnlock(V context)
    {
      throw new IllegalMonitorStateException();
    }

    public boolean isReadLock(V context)
    {
      return contexts.contains(context);
    }

    public boolean isWriteLock(V context)
    {
      return false;
    }

    public boolean isReadLockByOthers(V context)
    {
      if (contexts.isEmpty())
      {
        return false;
      }

      return contexts.size() > (isReadLock(context) ? 1 : 0);
    }

    public boolean isWriteLockByOthers(V context)
    {
      return false;
    }

    public LockEntry<K, V> clearLock(V context)
    {
      while (contexts.remove(context))
      {
      }

      return contexts.isEmpty() ? null : this;
    }
  }

  /**
   * @author Simon McDuff
   */
  private static final class WriteLockEntry<K, V> implements LockEntry<K, V>
  {
    private K objectToLock;

    private V context;

    private int count;

    private ReadLockEntry<K, V> readLock;

    public WriteLockEntry(K objectToLock, V context, ReadLockEntry<K, V> readLock)
    {
      this.objectToLock = objectToLock;
      this.context = context;
      this.readLock = readLock;
      this.count = 1;
    }

    private ReadLockEntry<K, V> getReadLock()
    {
      if (readLock == null)
      {
        readLock = new ReadLockEntry<K, V>(objectToLock, context);
      }

      return readLock;
    }

    public boolean canObtainWriteLock(V context)
    {
      return context == this.context;
    }

    public boolean canObtainReadLock(V context)
    {
      return context == this.context;
    }

    public LockEntry<K, V> readLock(V context)
    {
      ReadLockEntry<K, V> lock = getReadLock();
      lock.readLock(context);
      return this;
    }

    public LockEntry<K, V> writeLock(V context)
    {
      count++;
      return this;
    }

    public K getKey()
    {
      return objectToLock;
    }

    public LockEntry<K, V> readUnlock(V context)
    {
      if (readLock != null)
      {
        if (readLock.readUnlock(context) == null)
        {
          readLock = null;
        }

        return this;
      }

      throw new IllegalMonitorStateException();
    }

    public LockEntry<K, V> writeUnlock(V context)
    {
      return --count <= 0 ? readLock : this;
    }

    public boolean isReadLock(V context)
    {
      return readLock != null ? readLock.isReadLock(context) : false;
    }

    public boolean isWriteLock(V context)
    {
      return context == this.context;
    }

    public LockEntry<K, V> clearLock(V context)
    {
      if (readLock != null)
      {
        if (readLock.clearLock(context) == null)
        {
          readLock = null;
        }
      }

      return this.context == context ? readLock : this;
    }

    public boolean isReadLockByOthers(V context)
    {
      return readLock != null ? readLock.isReadLockByOthers(context) : false;
    }

    public boolean isWriteLockByOthers(V context)
    {
      return context != this.context;
    }
  }

  /**
   * @author Simon McDuff
   */
  private static final class NoLockEntry<K, V> implements LockEntry<K, V>
  {
    private K objectToLock;

    public NoLockEntry(K objectToLock)
    {
      this.objectToLock = objectToLock;
    }

    public boolean canObtainWriteLock(V context)
    {
      return true;
    }

    public boolean canObtainReadLock(V context)
    {
      return true;
    }

    public LockEntry<K, V> readLock(V context)
    {
      return new ReadLockEntry<K, V>(objectToLock, context);
    }

    public LockEntry<K, V> writeLock(V context)
    {
      return new WriteLockEntry<K, V>(objectToLock, context, null);
    }

    public K getKey()
    {
      return objectToLock;
    }

    public LockEntry<K, V> readUnlock(V context)
    {
      throw new UnsupportedOperationException();
    }

    public LockEntry<K, V> writeUnlock(V context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isReadLock(V context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isWriteLock(V context)
    {
      throw new UnsupportedOperationException();
    }

    public LockEntry<K, V> clearLock(V context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isReadLockByOthers(V context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isWriteLockByOthers(V context)
    {
      throw new UnsupportedOperationException();
    }
  }
}
