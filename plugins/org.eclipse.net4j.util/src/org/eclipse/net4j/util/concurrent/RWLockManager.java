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
public class RWLockManager<K, V> extends Lifecycle
{
  public static final int WAIT = 0;

  public static final int NO_WAIT = 1;

  private LockStrategy<K, V> writeLockStrategy = new LockStrategy<K, V>()
  {
    public boolean canObtainLock(LockEntry<K, V> entry, V context)
    {
      return entry.canObtainWriteLock(context);
    }

    public LockEntry<K, V> lock(LockEntry<K, V> entry, V context)
    {
      return entry.writeLock(context);
    }

    public LockEntry<K, V> unlock(LockEntry<K, V> entry, V context)
    {
      return entry.writeUnlock(context);
    }

    public boolean isLocked(LockEntry<K, V> entry, V context)
    {
      return entry.isWriteLock(context);
    }

    public boolean isLockedByOthers(LockEntry<K, V> entry, V context)
    {
      return entry.isWriteLockByOthers(context);
    }
  };

  private LockStrategy<K, V> readLockStrategy = new LockStrategy<K, V>()
  {
    public boolean canObtainLock(LockEntry<K, V> entry, V context)
    {
      return entry.canObtainReadLock(context);
    }

    public LockEntry<K, V> lock(LockEntry<K, V> entry, V context)
    {
      return entry.readLock(context);
    }

    public LockEntry<K, V> unlock(LockEntry<K, V> entry, V context)
    {
      return entry.readUnlock(context);
    }

    public boolean isLocked(LockEntry<K, V> entry, V context)
    {
      return entry.isReadLock(context);
    }

    public boolean isLockedByOthers(LockEntry<K, V> entry, V context)
    {
      return entry.isReadLockByOthers(context);
    }
  };

  private Map<K, LockEntry<K, V>> lockEntries = new HashMap<K, LockEntry<K, V>>();

  private Object lockChanged = new Object();

  public void lock(RWLockManager.LockType type, V context, Collection<? extends K> objectsToLock, long timeout)
      throws InterruptedException
  {
    lock(getLockingStrategy(type), context, objectsToLock, timeout);
  }

  public void lock(RWLockManager.LockType type, V context, K objectToLock, long timeout) throws InterruptedException
  {
    lock(type, context, Collections.singletonList(objectToLock), timeout);
  }

  /**
   * Attempts to release for a given locktype, context and objects.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking objects without lock.
   */
  public void unlock(RWLockManager.LockType type, V context, Collection<? extends K> objectsToLock)
  {
    unlock(getLockingStrategy(type), context, objectsToLock);
  }

  /**
   * Attempts to release all locks(read and write) for a given context.
   */
  public void unlock(V context)
  {
    synchronized (lockChanged)
    {
      List<LockEntry<K, V>> lockEntrysToRemove = new ArrayList<LockEntry<K, V>>();
      List<LockEntry<K, V>> lockEntrysToAdd = new ArrayList<LockEntry<K, V>>();

      for (Entry<K, LockEntry<K, V>> entry : lockEntries.entrySet())
      {
        LockEntry<K, V> newEntry = entry.getValue().clearLock(context);
        if (newEntry == null)
        {
          lockEntrysToRemove.add(entry.getValue());
        }
        else if (newEntry != entry)
        {
          lockEntrysToAdd.add(newEntry);
        }
      }

      for (LockEntry<K, V> lockEntry : lockEntrysToRemove)
      {
        lockEntries.remove(lockEntry.getKey());
      }

      for (LockEntry<K, V> lockEntry : lockEntrysToAdd)
      {
        lockEntries.put(lockEntry.getKey(), lockEntry);
      }

      lockChanged.notifyAll();
    }
  }

  public boolean hasLock(RWLockManager.LockType type, V context, K objectToLock)
  {
    return hasLock(getLockingStrategy(type), context, objectToLock);
  }

  public boolean hasLockByOthers(RWLockManager.LockType type, V context, K objectToLock)
  {
    LockStrategy<K, V> lockingStrategy = getLockingStrategy(type);
    LockEntry<K, V> entry = getLockEntry(objectToLock);
    return null != entry && lockingStrategy.isLockedByOthers(entry, context);
  }

  private LockStrategy<K, V> getLockingStrategy(RWLockManager.LockType type)
  {
    if (type == RWLockManager.LockType.READ)
    {
      return readLockStrategy;
    }

    if (type == RWLockManager.LockType.WRITE)
    {
      return writeLockStrategy;
    }

    throw new IllegalArgumentException(type.toString());
  }

  /**
   * Attempts to release this lock.
   * <p>
   * If the number of context is now zero then the lock is made available for write lock attempts.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking object not locked.
   */
  private void unlock(LockStrategy<K, V> lockingStrategy, V context, Collection<? extends K> objectsToLock)
  {
    synchronized (lockChanged)
    {
      List<LockEntry<K, V>> lockEntrysToRemove = new ArrayList<LockEntry<K, V>>();
      List<LockEntry<K, V>> lockEntrysToAdd = new ArrayList<LockEntry<K, V>>();
      for (K objectToLock : objectsToLock)
      {
        LockEntry<K, V> entry = lockEntries.get(objectToLock);

        if (entry == null)
        {
          throw new IllegalMonitorStateException();
        }

        LockEntry<K, V> newEntry = lockingStrategy.unlock(entry, context);

        if (newEntry == null)
        {
          lockEntrysToRemove.add(entry);
        }
        else if (newEntry != entry)
        {
          lockEntrysToAdd.add(newEntry);
        }
      }

      for (LockEntry<K, V> lockEntry : lockEntrysToRemove)
      {
        lockEntries.remove(lockEntry.getKey());
      }

      for (LockEntry<K, V> lockEntry : lockEntrysToAdd)
      {
        lockEntries.put(lockEntry.getKey(), lockEntry);
      }

      lockChanged.notifyAll();
    }
  }

  private boolean hasLock(LockStrategy<K, V> lockingStrategy, V context, K objectToLock)
  {
    LockEntry<K, V> entry = getLockEntry(objectToLock);
    return entry != null && lockingStrategy.isLocked(entry, context);
  }

  private void lock(LockStrategy<K, V> lockStrategy, V context, Collection<? extends K> objectsToLocks, long timeout)
      throws InterruptedException
  {
    long startTime = System.currentTimeMillis();
    while (true)
    {
      synchronized (lockChanged)
      {
        K conflict = obtainLock(lockStrategy, context, objectsToLocks);
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

  private K obtainLock(LockStrategy<K, V> lockingStrategy, V context, Collection<? extends K> objectsToLock)
  {
    List<LockEntry<K, V>> lockEntrys = new ArrayList<LockEntry<K, V>>();
    for (K objectToLock : objectsToLock)
    {
      LockEntry<K, V> entry = lockEntries.get(objectToLock);
      if (entry == null)
      {
        entry = new NoLockEntry<K, V>(objectToLock);
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

    for (LockEntry<K, V> lockEntry : lockEntrys)
    {
      lockEntries.put(lockEntry.getKey(), lockingStrategy.lock(lockEntry, context));
    }

    return null;
  }

  private LockEntry<K, V> getLockEntry(K objectToLock)
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
      getReadLock().readLock(context);
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
        if (getReadLock().readUnlock(context) == null)
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
        if (getReadLock().clearLock(context) == null)
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

  /**
   * @author Simon McDuff
   */
  public static enum LockType
  {
    WRITE, READ
  }
}
