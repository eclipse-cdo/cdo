/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.Access;
import org.eclipse.net4j.util.concurrent.CriticalSection.LockedCriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.NonFairReentrantLock;

import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Tests the reentrant lock and monitor-safety behavior of CDO views.
 *
 * @author Eike Stepper
 */
public class ViewSynchronizationTest extends AbstractCDOTest
{
  public void testLockIdentityAndReentrancy()
  {
    CDOTransaction transaction = openSession().openTransaction();
    InternalCDOView view = (InternalCDOView)transaction;
    LockedCriticalSection sync = (LockedCriticalSection)view.sync();
    assertTrue(sync.getLock() instanceof NonFairReentrantLock);

    try (Access access = view.access())
    {
      assertSame(sync.getLock(), access.getLock());
    }

    sync.run(() -> {
      assertEquals(1, ((NonFairReentrantLock)sync.getLock()).getHoldCount());

      try (Access access = view.access())
      {
        assertEquals(2, ((NonFairReentrantLock)sync.getLock()).getHoldCount());
      }
    });

    assertFalse(((NonFairReentrantLock)sync.getLock()).isLocked());
  }

  public void testNextViewLockIsPreserved()
  {
    NonFairReentrantLock expectedLock = new NonFairReentrantLock();
    CDOUtil.setNextViewLock(expectedLock);

    try
    {
      InternalCDOView view = (InternalCDOView)openSession().openTransaction();
      assertSame(expectedLock, ((LockedCriticalSection)view.sync()).getLock());
      try (Access access = view.access())
      {
        assertSame(expectedLock, access.getLock());
      }
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }
  }

  public void testDelegableViewLockIsPreserved()
  {
    CDOSession session = openSession();
    session.options().setDelegableViewLockEnabled(true);
    InternalCDOView view = (InternalCDOView)session.openTransaction();
    assertTrue(((LockedCriticalSection)view.sync()).getLock() instanceof DelegableReentrantLock);
  }

  public void testLexicalAccessPreservesCheckedExceptionAndUnlocks() throws Exception
  {
    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    IOException expected = new IOException("expected");

    try (Access access = view.access())
    {
      throw expected;
    }
    catch (IOException actual)
    {
      assertSame(expected, actual);
    }

    assertFalse(((NonFairReentrantLock)((LockedCriticalSection)view.sync()).getLock()).isLocked());
  }

  public void testMonitorSafetyCheckAndDeprecatedMethods()
  {
    InternalCDOView view = (InternalCDOView)openSession().openTransaction();

    synchronized (view)
    {
      try
      {
        view.access();
        fail("Intrinsic monitor use must be rejected by internal lexical access");
      }
      catch (UnsupportedOperationException expected)
      {
        assertTrue(expected.getMessage().contains("CDOView.sync()"));
      }

      try
      {
        view.sync().run(() -> fail("The callback must not run while the intrinsic monitor is held"));
        fail("Intrinsic monitor use must be rejected by public sync()");
      }
      catch (UnsupportedOperationException expected)
      {
        assertTrue(expected.getMessage().contains("CDOView.sync()"));
      }
    }

    assertDeprecatedLockingMethodFails(view, 0);
    assertDeprecatedLockingMethodFails(view, 1);
    assertDeprecatedLockingMethodFails(view, 2);
    assertDeprecatedLockingMethodFails(view, 3);
  }

  public void testMonitorSafetyCheckRunsBeforeLockAcquisition()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    try
    {
      InternalCDOView view = (InternalCDOView)openSession().openTransaction();
      assertSame(lock, ((LockedCriticalSection)view.sync()).getLock());
      int lockCalls = lock.getLockCalls();

      synchronized (view)
      {
        try
        {
          view.access();
          fail("Lexical access must be rejected before acquiring the view lock");
        }
        catch (UnsupportedOperationException expected)
        {
          assertTrue(expected.getMessage().contains("CDOView.sync()"));
        }

        try
        {
          view.sync().run(() -> fail("Public sync callback must not run while holding the intrinsic monitor"));
          fail("Public sync must reject intrinsic-monitor use before acquiring the view lock");
        }
        catch (UnsupportedOperationException expected)
        {
          assertTrue(expected.getMessage().contains("CDOView.sync()"));
        }
      }

      assertEquals(lockCalls, lock.getLockCalls());
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }
  }

  public void testInterruptedUpdateWaitPreservesWrapperAndInterrupt()
  {
    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    long updateTime = view.getLastUpdateTime() + 1;
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicReference<Boolean> interrupted = new AtomicReference<>(Boolean.FALSE);

    Thread waiter = new Thread(() -> {
      try
      {
        view.waitForUpdate(updateTime, 60000L);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
        interrupted.set(Thread.currentThread().isInterrupted());
      }
    });

    waiter.start();
    waiter.interrupt();

    try
    {
      waiter.join(5000L);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      fail("Test thread was interrupted");
    }

    assertFalse(waiter.isAlive());
    assertTrue(failure.get() instanceof WrappedException);
    assertTrue(failure.get().getCause() instanceof InterruptedException);
    assertTrue(interrupted.get());
  }

  public void testUpdateConditionReleasesAndReacquiresViewLock()
  {
    AwaitObservedLock lock = new AwaitObservedLock();
    CDOUtil.setNextViewLock(lock);
    InternalCDOView view;

    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    long updateTime = view.getLastUpdateTime() + 1;
    AtomicReference<Boolean> result = new AtomicReference<>();

    Thread waiter = new Thread(() -> result.set(view.waitForUpdate(updateTime, 30000L)));
    waiter.start();

    boolean awaitEntered = false;

    try
    {
      awaitEntered = lock.awaitEntered.await(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      fail("Test thread was interrupted");
    }

    view.setLastUpdateTime(updateTime);

    try
    {
      waiter.join(5000L);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      fail("Test thread was interrupted");
    }

    assertFalse(waiter.isAlive());
    assertTrue("Condition.await() was not reached", awaitEntered);
    assertEquals(Boolean.TRUE, result.get());
  }

  @SuppressWarnings("deprecation")
  public void testLegacyLockingCompatibilityEnabled()
  {
    if (!Boolean.getBoolean("org.eclipse.emf.cdo.view.ENABLE_LEGACY_LOCKING_API"))
    {
      return;
    }

    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    Object monitor = view.getViewMonitor();
    assertNotSame(view, monitor);
    assertSame(monitor, view.getViewMonitor());

    Lock lock = view.getViewLock();
    view.lockView();

    try
    {
      assertTrue(((NonFairReentrantLock)lock).isHeldByCurrentThread());
    }
    finally
    {
      view.unlockView();
    }
  }

  public void testIntrinsicMonitorSafetyCheckCanBeDisabled()
  {
    if (!Boolean.getBoolean("org.eclipse.emf.cdo.view.DISABLE_INTRINSIC_MONITOR_CHECK"))
    {
      return;
    }

    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    synchronized (view)
    {
      view.sync().run(() -> {
        try (Access access = view.access())
        {
          assertSame(((LockedCriticalSection)view.sync()).getLock(), access.getLock());
        }
      });
    }
  }

  @SuppressWarnings("deprecation")
  private void assertDeprecatedLockingMethodFails(InternalCDOView view, int method)
  {
    try
    {
      switch (method)
      {
      case 0:
        view.getViewMonitor();
        break;

      case 1:
        view.getViewLock();
        break;

      case 2:
        view.lockView();
        break;

      default:
        view.unlockView();
        break;
      }

      fail("Deprecated view locking API must be disabled by default");
    }
    catch (UnsupportedOperationException expected)
    {
      assertTrue(expected.getMessage().contains("ENABLE_LEGACY_LOCKING_API"));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingLock extends NonFairReentrantLock
  {
    private static final long serialVersionUID = 1L;

    private final AtomicInteger lockCalls = new AtomicInteger();

    @Override
    public void lock()
    {
      lockCalls.incrementAndGet();
      super.lock();
    }

    public int getLockCalls()
    {
      return lockCalls.get();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AwaitObservedLock extends NonFairReentrantLock
  {
    private static final long serialVersionUID = 1L;

    private final CountDownLatch awaitEntered = new CountDownLatch(1);

    @Override
    public Condition newCondition()
    {
      Condition delegate = super.newCondition();
      return new Condition()
      {
        @Override
        public void await() throws InterruptedException
        {
          delegate.await();
        }

        @Override
        public void awaitUninterruptibly()
        {
          delegate.awaitUninterruptibly();
        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException
        {
          return delegate.awaitNanos(nanosTimeout);
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException
        {
          awaitEntered.countDown();
          return delegate.await(time, unit);
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException
        {
          return delegate.awaitUntil(deadline);
        }

        @Override
        public void signal()
        {
          delegate.signal();
        }

        @Override
        public void signalAll()
        {
          delegate.signalAll();
        }
      };
    }
  }
}
