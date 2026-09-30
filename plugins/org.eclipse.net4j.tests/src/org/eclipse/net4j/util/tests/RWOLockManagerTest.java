/*
 * Copyright (c) 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange;
import org.eclipse.net4j.util.concurrent.RWLock;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 */
public class RWOLockManagerTest extends AbstractOMTest
{
  private static final int USERS = 10;

  private static final int ALLOCATIONS = 10;

  private static final int RETRIES = 5;

  private static final Set<Object> EXCLUSIVE_RESOURCE = Collections.singleton(new Object()
  {
    @Override
    public String toString()
    {
      return "EXCLUSIVE_RESOURCE";
    }
  });

  public void testRWOLockManager() throws Exception
  {
    AtomicInteger resource = new AtomicInteger(-1);
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();

    User[] users = new User[USERS];
    User[] allocators = new User[USERS * ALLOCATIONS];

    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(USERS);

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID] = new User(userID, started, finished, allocators, lockManager, resource);
    }

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID].start();
    }

    started.countDown();
    await(finished);
    IOUtil.OUT().println("FINISHED");

    Exception exception = null;
    for (int userID = 0; userID < USERS; userID++)
    {
      Exception ex = users[userID].getException();
      if (ex != null)
      {
        exception = ex;
        ex.printStackTrace();
      }
    }

    if (exception != null)
    {
      throw exception;
    }

    IOUtil.OUT().println("SUCCESS");
  }

  public void testChangeLocksMixedBatchAndCallbacks() throws Exception
  {
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();
    User owner = new User(100, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    Object released = new Object();
    Object retained = new Object();
    List<LockChange.Operation> operations = new ArrayList<>();
    List<RWOLockManager.LockState<Object, User>> states = new ArrayList<>();

    assertEquals(1, lockManager.lock(owner, Collections.singleton(released), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null));
    long modCount = lockManager.changeLocks(owner, Arrays.asList( //
        LockChange.lock(Collections.singleton(retained), LockType.READ, 1, IRWOLockManager.NO_TIMEOUT),
        LockChange.lock(Collections.singleton(retained), LockType.OPTION, 1, IRWOLockManager.NO_TIMEOUT),
        LockChange.unlock(Collections.singleton(released), null, IRWOLockManager.ALL_LOCKS)),
        (operation, context, object, type, oldCount, newCount) -> operations.add(operation), states::add);

    assertEquals(2, modCount);
    assertEquals(Arrays.asList(LockChange.Operation.LOCK, LockChange.Operation.LOCK, LockChange.Operation.UNLOCK), operations);
    assertEquals(2, states.size());
    assertEquals(retained, states.get(0).getLockedObject());
    assertEquals(1, states.get(0).getLockCount(LockType.READ, owner));
    assertEquals(1, states.get(0).getLockCount(LockType.OPTION, owner));
    assertEquals(released, states.get(1).getLockedObject());
    assertEquals(0, states.get(1).getLockCount(LockType.READ, owner));
    assertEquals(0, states.get(1).getLockCount(LockType.WRITE, owner));
    assertEquals(0, states.get(1).getLockCount(LockType.OPTION, owner));
    assertNull(lockManager.getLockState(released));
  }

  public void testLockPreservesProgressiveAcquisitionAndRollback() throws Exception
  {
    class TestLockManager extends RWOLockManager<Object, User>
    {
      boolean tryWriteAccess()
      {
        if (!rwAccess.getLock().writeLock().tryLock())
        {
          return false;
        }

        rwAccess.getLock().writeLock().unlock();
        return true;
      }
    }

    TestLockManager lockManager = new TestLockManager();
    User blocker = new User(106, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    User waiter = new User(107, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    Object free = new Object();
    Object blocked = new Object();
    lockManager.lock(blocker, Collections.singleton(blocked), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);
    long modCount = lockManager.getModCount();

    CountDownLatch freeObjectNotified = new CountDownLatch(1);
    AtomicInteger deltaCount = new AtomicInteger();
    AtomicInteger stateCount = new AtomicInteger();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread thread = new Thread(() -> {
      try
      {
        lockManager.lock(waiter, Arrays.asList(free, blocked), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, (context, object, type, oldCount, newCount) -> {
          deltaCount.incrementAndGet();
          if (object == free)
          {
            freeObjectNotified.countDown();
          }
        }, state -> stateCount.incrementAndGet());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "RWOLockManagerTest-ProgressiveLock");
    thread.start();

    assertTrue("Free object's acquisition was not reported", freeObjectNotified.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT);

    while (!lockManager.tryWriteAccess() && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    assertTrue("Lock call did not release write access while waiting", lockManager.tryWriteAccess());
    assertTrue("Free object was not progressively acquired", lockManager.getLockState(free).hasLock(LockType.WRITE, waiter, false));
    assertEquals(1, deltaCount.get());
    assertEquals(1, stateCount.get());

    thread.interrupt();
    thread.join(DEFAULT_TIMEOUT);

    assertFalse("Interrupted lock call did not finish", thread.isAlive());
    assertInstanceOf(InterruptedException.class, failure.get());
    assertNull("Progressively acquired object was not rolled back", lockManager.getLockState(free));
    assertEquals("Failed legacy lock changed the generic modification count", modCount, lockManager.getModCount());
  }

  public void testChangeLocksEvaluatesRepeatedObjectChangesInOrder() throws Exception
  {
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();
    User owner = new User(108, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    Object object = new Object();
    lockManager.lock(owner, Collections.singleton(object), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);
    List<Integer> counts = new ArrayList<>();

    long modCount = lockManager.changeLocks(owner, Arrays.asList( //
        LockChange.unlock(Collections.singleton(object), LockType.WRITE, 1),
        LockChange.lock(Collections.singleton(object), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT)),
        (operation, context, changedObject, type, oldCount, newCount) -> counts.add(newCount), null);

    assertEquals(2, modCount);
    assertEquals(Arrays.asList(0, 1), counts);
    assertTrue(lockManager.getLockState(object).hasLock(LockType.WRITE, owner, false));
  }

  public void testChangeLocksUnlockAllForContext() throws Exception
  {
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();
    User owner = new User(101, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    Object first = new Object();
    Object second = new Object();
    lockManager.lock(owner, Collections.singleton(first), LockType.READ, 2, IRWOLockManager.NO_TIMEOUT, null, null);
    lockManager.lock(owner, Collections.singleton(second), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);

    long modCount = lockManager.changeLocks(owner, Collections.singletonList(LockChange.unlock(null, null, IRWOLockManager.ALL_LOCKS)), null, null);

    assertEquals(3, modCount);
    assertNull(lockManager.getLockState(first));
    assertNull(lockManager.getLockState(second));
  }

  public void testChangeLocksTimeoutDoesNotApplyUnlock() throws Exception
  {
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();
    User firstOwner = new User(102, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    User secondOwner = new User(103, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    Object first = new Object();
    Object blocked = new Object();
    lockManager.lock(firstOwner, Collections.singleton(first), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);
    lockManager.lock(secondOwner, Collections.singleton(blocked), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);

    try
    {
      lockManager.changeLocks(firstOwner, Arrays.asList( //
          LockChange.unlock(Collections.singleton(first), null, IRWOLockManager.ALL_LOCKS),
          LockChange.lock(Collections.singleton(blocked), LockType.WRITE, 1, 20)), null, null);
      fail("TimeoutRuntimeException expected");
    }
    catch (TimeoutRuntimeException expected)
    {
      assertTrue("Failed atomic change released its first object", lockManager.getLockState(first).hasLock(LockType.WRITE));
      assertEquals(2, lockManager.getModCount());
    }
  }

  public void testChangeLocksInterruptionDoesNotApplyUnlock() throws Exception
  {
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();
    User firstOwner = new User(104, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    User secondOwner = new User(105, new CountDownLatch(0), new CountDownLatch(0), new User[0], lockManager, new AtomicInteger());
    Object first = new Object();
    Object blocked = new Object();
    lockManager.lock(firstOwner, Collections.singleton(first), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);
    lockManager.lock(secondOwner, Collections.singleton(blocked), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT, null, null);

    CountDownLatch started = new CountDownLatch(1);
    AtomicBoolean interrupted = new AtomicBoolean();
    Thread changer = new Thread(() -> {
      started.countDown();
      try
      {
        lockManager.changeLocks(firstOwner, Arrays.asList( //
            LockChange.unlock(Collections.singleton(first), null, IRWOLockManager.ALL_LOCKS),
            LockChange.lock(Collections.singleton(blocked), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT)), null, null);
      }
      catch (InterruptedException expected)
      {
        interrupted.set(true);
      }
    }, "RWOLockManagerTest-InterruptedChange");
    changer.start();
    assertTrue(started.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    changer.interrupt();
    changer.join(DEFAULT_TIMEOUT);

    assertFalse("Interrupted atomic change did not finish", changer.isAlive());
    assertTrue("Interrupted change did not report interruption", interrupted.get());
    assertTrue("Interrupted atomic change released its first object", lockManager.getLockState(first).hasLock(LockType.WRITE));
  }

  public void testRWLockInterruptStatus() throws Exception
  {
    Thread.currentThread().interrupt();

    try
    {
      RWLock.call(() -> null, new ReentrantLock(), DEFAULT_TIMEOUT);
      fail("WrappedException expected");
    }
    catch (RuntimeException ex)
    {
      assertInstanceOf(WrappedException.class, ex);
      assertInstanceOf(InterruptedException.class, ex.getCause());
      assertTrue(Thread.currentThread().isInterrupted());
    }
    finally
    {
      Thread.interrupted();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class User extends Thread
  {
    private final CountDownLatch started;

    private final CountDownLatch finished;

    private final User[] allocators;

    private final RWOLockManager<Object, User> lockManager;

    private final AtomicInteger resource;

    private Exception exception;

    public User(int userID, CountDownLatch started, CountDownLatch finished, User[] allocators, RWOLockManager<Object, User> lockManager,
        AtomicInteger resource)
    {
      super("User-" + userID);
      this.started = started;
      this.finished = finished;
      this.allocators = allocators;
      this.lockManager = lockManager;
      this.resource = resource;
    }

    public Exception getException()
    {
      return exception;
    }

    @Override
    public void run()
    {
      await(started);

      try
      {
        for (int allocation = 0; allocation < ALLOCATIONS; allocation++)
        {
          for (int retry = RETRIES; retry >= 0; --retry)
          {
            try
            {
              lockManager.lock(this, EXCLUSIVE_RESOURCE, LockType.WRITE, 1, 10000000, null, null);
              break;
            }
            catch (TimeoutRuntimeException ex)
            {
              if (retry == 0)
              {
                exception = ex;
                return;
              }

              msg("Lock timed out. Trying again...");
            }
            catch (InterruptedException ex)
            {
              Thread.currentThread().interrupt();
              exception = ex;
              return;
            }
          }

          try
          {
            int id = resource.get() + 1;
            if (allocators[id] != null)
            {
              throw new IllegalStateException(id + " already allocated by " + allocators[id]);
            }

            allocators[id] = this;
            resource.set(id);
            msg("ALLOCATED " + id);
          }
          catch (Exception ex)
          {
            exception = ex;
            return;
          }
          finally
          {
            lockManager.unlock(this, EXCLUSIVE_RESOURCE, LockType.WRITE, 1, null, null);
          }
        }
      }
      finally
      {
        finished.countDown();
      }
    }

    @Override
    public String toString()
    {
      return getName();
    }

    private void msg(String string)
    {
      IOUtil.OUT().println(getName() + ": " + string);
    }
  }
}
