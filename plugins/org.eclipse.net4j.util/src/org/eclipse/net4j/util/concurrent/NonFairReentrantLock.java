/*
 * Copyright (c) 2016, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class NonFairReentrantLock implements Lock, Serializable
{
  private static final long serialVersionUID = 1L;

  private final Sync sync = new Sync();

  public NonFairReentrantLock()
  {
  }

  @Override
  public void lock()
  {
    sync.lock();
  }

  @Override
  public void lockInterruptibly() throws InterruptedException
  {
    sync.acquireInterruptibly(1);
  }

  @Override
  public boolean tryLock()
  {
    return sync.tryAcquire(1);
  }

  @Override
  public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException
  {
    return sync.tryAcquireNanos(1, unit.toNanos(timeout));
  }

  @Override
  public void unlock()
  {
    sync.release(1);
  }

  @Override
  public Condition newCondition()
  {
    return sync.newCondition();
  }

  public int getHoldCount()
  {
    return sync.getHoldCount();
  }

  public boolean isHeldByCurrentThread()
  {
    return sync.isHeldExclusively();
  }

  public boolean isLocked()
  {
    return sync.isLocked();
  }

  public Thread getOwner()
  {
    return sync.getOwner();
  }

  public final boolean hasQueuedThreads()
  {
    return sync.hasQueuedThreads();
  }

  public final boolean hasQueuedThread(Thread thread)
  {
    return sync.isQueued(thread);
  }

  public final int getQueueLength()
  {
    return sync.getQueueLength();
  }

  public boolean hasWaiters(Condition condition)
  {
    if (condition == null)
    {
      throw new NullPointerException();
    }

    if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
    {
      throw new IllegalArgumentException("not owner");
    }

    return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)condition);
  }

  public int getWaitQueueLength(Condition condition)
  {
    if (condition == null)
    {
      throw new NullPointerException();
    }

    if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
    {
      throw new IllegalArgumentException("not owner");
    }

    return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)condition);
  }

  @Override
  public String toString()
  {
    Thread o = sync.getOwner();
    return super.toString() + (o == null ? "[Unlocked]" : "[Locked by thread " + o.getName() + "]");
  }

  protected Collection<Thread> getQueuedThreads()
  {
    return sync.getQueuedThreads();
  }

  protected Collection<Thread> getWaitingThreads(Condition condition)
  {
    if (condition == null)
    {
      throw new NullPointerException();
    }

    if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
    {
      throw new IllegalArgumentException("not owner");
    }

    return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)condition);
  }

  protected boolean isOwner(Thread thread, Thread owner)
  {
    return thread == owner;
  }

  /**
   * @author Eike Stepper
   */
  private final class Sync extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = 1L;

    public Thread getOwner()
    {
      return getState() == 0 ? null : getExclusiveOwnerThread();
    }

    public void lock()
    {
      if (compareAndSetState(0, 1))
      {
        setExclusiveOwnerThread(Thread.currentThread());
      }
      else
      {
        acquire(1);
      }
    }

    public ConditionObject newCondition()
    {
      return new ConditionObject();
    }

    public int getHoldCount()
    {
      return isHeldExclusively() ? getState() : 0;
    }

    public boolean isLocked()
    {
      return getState() != 0;
    }

    @Override
    public boolean tryAcquire(int acquires)
    {
      final Thread current = Thread.currentThread();
      int c = getState();
      if (c == 0)
      {
        if (compareAndSetState(0, acquires))
        {
          setExclusiveOwnerThread(current);
          return true;
        }
      }
      else if (isHeldExclusively(current))
      {
        int nextc = c + acquires;
        if (nextc < 0)
        {
          throw new Error("Maximum lock count exceeded");
        }

        setState(nextc);
        return true;
      }

      return false;
    }

    @Override
    protected boolean tryRelease(int releases)
    {
      int c = getState() - releases;
      if (!isHeldExclusively(Thread.currentThread()))
      {
        throw new IllegalMonitorStateException();
      }

      boolean free = false;
      if (c == 0)
      {
        free = true;
        setExclusiveOwnerThread(null);
      }

      setState(c);
      return free;
    }

    @Override
    protected boolean isHeldExclusively()
    {
      return isHeldExclusively(Thread.currentThread());
    }

    private boolean isHeldExclusively(Thread current)
    {
      Thread exclusiveOwnerThread = getExclusiveOwnerThread();
      if (exclusiveOwnerThread == null)
      {
        return false;
      }

      return isOwner(current, exclusiveOwnerThread);
    }

    /**
     * For {@link Serializable}.
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
    {
      s.defaultReadObject();
      setState(0);
    }
  }
}
