/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Tests ordered application of server-authoritative lock changes on a session.
 *
 * @author Eike Stepper
 */
public class LockChangeSequencerTest extends PlainTest
{
  public void testFutureCountIsBufferedUntilItsPredecessorArrives() throws Exception
  {
    Object sequencer = newSequencer();
    List<Integer> applied = new ArrayList<>();

    enqueue(sequencer, 2L, () -> applied.add(2));
    assertTrue(applied.isEmpty());

    enqueue(sequencer, 1L, () -> applied.add(1));

    assertEquals(List.of(1, 2), applied);
    assertEquals(2L, currentCount(sequencer));
  }

  public void testStaleCountIsDiscarded() throws Exception
  {
    Object sequencer = newSequencer();
    List<Integer> applied = new ArrayList<>();

    enqueue(sequencer, 1L, () -> applied.add(1));
    enqueue(sequencer, 1L, () -> applied.add(10));

    assertEquals(List.of(1), applied);
    assertEquals(1L, currentCount(sequencer));
  }

  public void testConflictingPendingCountIsRejected() throws Exception
  {
    Object sequencer = newSequencer();
    enqueue(sequencer, 2L, () -> {
    });

    try
    {
      enqueue(sequencer, 2L, () -> {
      });
      fail("A second action must not replace the pending action for count 2");
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }
  }

  public void testOwnerRemapAndUnlockAreAppliedInCountOrder() throws Exception
  {
    Object sequencer = newSequencer();
    String[] owner = { "OLD_DURABLE" };

    enqueue(sequencer, 2L, () -> owner[0] = null);
    enqueue(sequencer, 1L, () -> owner[0] = "NEW_NORMAL");

    assertNull(owner[0]);
    assertEquals(2L, currentCount(sequencer));
  }

  public void testFailedActionFailsItsCompletionAndClosesTheSequencer() throws Exception
  {
    Object sequencer = newSequencer();

    try
    {
      enqueue(sequencer, 1L, () -> {
        throw new IllegalStateException("expected failure"); //$NON-NLS-1$
      });
      fail("The failing action must be reported to its caller");
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
      assertEquals("expected failure", ex.getCause().getMessage()); //$NON-NLS-1$
    }

    Method await = sequencer.getClass().getDeclaredMethod("await", long.class); //$NON-NLS-1$
    await.setAccessible(true);
    try
    {
      await.invoke(sequencer, 1L);
      fail("A failed sequence position must not report successful completion");
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof RuntimeException);
    }
  }

  public void testFailedActionDoesNotRunLaterCountsAndFailsTheirWaiters() throws Exception
  {
    Object sequencer = newSequencer();
    List<Integer> applied = new ArrayList<>();
    Object completion = enqueueCompletion(sequencer, 2L, () -> applied.add(2));

    try
    {
      enqueue(sequencer, 1L, () -> {
        applied.add(1);
        throw new IllegalStateException("expected failure"); //$NON-NLS-1$
      });
      fail("The failing sequence action must be reported"); //$NON-NLS-1$
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
    }

    assertEquals(List.of(1), applied);
    assertEquals(0L, currentCount(sequencer));

    Method await = completion.getClass().getDeclaredMethod("awaitUninterruptibly"); //$NON-NLS-1$
    await.setAccessible(true);
    try
    {
      await.invoke(completion);
      fail("A later pending completion must be failed when its predecessor fails"); //$NON-NLS-1$
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
    }
  }

  public void testCloseFailsPendingWaitersWithoutExecutingBufferedActions() throws Exception
  {
    Object sequencer = newSequencer();
    boolean[] applied = { false };
    Object completion = enqueueCompletion(sequencer, 2L, () -> applied[0] = true);

    Method close = sequencer.getClass().getDeclaredMethod("close", Throwable.class); //$NON-NLS-1$
    close.setAccessible(true);
    close.invoke(sequencer, new IllegalStateException("closed for test")); //$NON-NLS-1$

    Method await = completion.getClass().getDeclaredMethod("awaitUninterruptibly"); //$NON-NLS-1$
    await.setAccessible(true);
    try
    {
      await.invoke(completion);
      fail("Closing the sequencer must release pending waiters with failure"); //$NON-NLS-1$
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
      assertEquals("closed for test", ex.getCause().getMessage()); //$NON-NLS-1$
    }

    assertFalse(applied[0]);
    assertEquals(0L, currentCount(sequencer));
  }

  public void testSynchronousCompletionWaitsForMissingPredecessorOutsideAccess() throws Exception
  {
    Object sequencer = newSequencer();
    ReentrantLock access = new ReentrantLock();
    List<Integer> applied = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(1);
    Throwable[] failure = { null };

    enqueue(sequencer, 2L, () -> {
      access.lock();
      try
      {
        applied.add(2);
      }
      finally
      {
        access.unlock();
      }
    });

    Method await = sequencer.getClass().getDeclaredMethod("await", long.class); //$NON-NLS-1$
    await.setAccessible(true);
    Thread waiter = new Thread(() -> {
      access.lock();
      access.unlock();
      started.countDown();

      try
      {
        await.invoke(sequencer, 2L);
      }
      catch (Throwable ex)
      {
        failure[0] = ex;
      }
      finally
      {
        completed.countDown();
      }
    });
    waiter.start();
    started.await();

    while (waiter.getState() == Thread.State.RUNNABLE || waiter.getState() == Thread.State.BLOCKED)
    {
      Thread.yield();
    }

    assertEquals(Thread.State.WAITING, waiter.getState());
    assertEquals(1L, completed.getCount());

    enqueue(sequencer, 1L, () -> {
      access.lock();
      try
      {
        applied.add(1);
      }
      finally
      {
        access.unlock();
      }
    });
    completed.await();

    assertNull(failure[0]);
    assertEquals(List.of(1, 2), applied);
  }

  public void testCommitCountWaitsForTimestampEligibilityAndThenNextCount() throws Exception
  {
    Object sequencer = newSequencer();
    List<String> applied = new ArrayList<>();

    enqueue(sequencer, 2L, () -> applied.add("N+1")); //$NON-NLS-1$
    boolean timestampPredecessorReady = false;
    if (timestampPredecessorReady)
    {
      enqueue(sequencer, 1L, () -> applied.add("commit N")); //$NON-NLS-1$
    }

    assertTrue(applied.isEmpty());

    timestampPredecessorReady = true;
    if (timestampPredecessorReady)
    {
      enqueue(sequencer, 1L, () -> applied.add("commit N")); //$NON-NLS-1$
    }

    assertEquals(List.of("commit N", "N+1"), applied); //$NON-NLS-1$
  }

  public void testCommitWithReadyTimestampStillWaitsForEarlierLockCount() throws Exception
  {
    Object sequencer = newSequencer();
    List<String> applied = new ArrayList<>();

    enqueue(sequencer, 2L, () -> applied.add("commit N+1")); //$NON-NLS-1$
    assertTrue(applied.isEmpty());

    enqueue(sequencer, 1L, () -> applied.add("lock N")); //$NON-NLS-1$

    assertEquals(List.of("lock N", "commit N+1"), applied); //$NON-NLS-1$
  }

  public void testCountZeroCannotAdvanceThePositiveSequence() throws Exception
  {
    Object sequencer = newSequencer();

    try
    {
      enqueue(sequencer, 0L, () -> fail("Count zero must use a legacy path")); //$NON-NLS-1$
      fail("Count zero must not be accepted as a sequence position");
    }
    catch (InvocationTargetException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    assertEquals(0L, currentCount(sequencer));
  }

  public void testFailedActionResynchronizesAndResumesLaterCounts() throws Exception
  {
    Object sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    boolean[] recoveryRequested = { false };

    Method setFailureHandler = sequencer.getClass().getDeclaredMethod("setFailureHandler", Consumer.class); //$NON-NLS-1$
    setFailureHandler.setAccessible(true);
    setFailureHandler.invoke(sequencer, (Consumer<Throwable>)cause -> recoveryRequested[0] = true);

    Method enqueue = sequencer.getClass().getDeclaredMethod("enqueue", long.class, Runnable.class, Runnable.class); //$NON-NLS-1$
    enqueue.setAccessible(true);
    Object coveredCompletion = enqueue.invoke(sequencer, 2L, (Runnable)() -> applied.add("stale-lock-change"), //$NON-NLS-1$
        (Runnable)() -> applied.add("covered-commit-invalidation")); //$NON-NLS-1$
    enqueue(sequencer, 3L, () -> applied.add("after-snapshot")); //$NON-NLS-1$

    try
    {
      enqueue(sequencer, 1L, () -> {
        throw new IllegalStateException("injected lock cache failure"); //$NON-NLS-1$
      });
      fail("The original failed action must still fail its caller"); //$NON-NLS-1$
    }
    catch (InvocationTargetException ex)
    {
      assertEquals("injected lock cache failure", ex.getCause().getMessage()); //$NON-NLS-1$
    }

    assertTrue(recoveryRequested[0]);
    assertEquals(0L, currentCount(sequencer));
    assertTrue(applied.isEmpty());

    Method install = sequencer.getClass().getDeclaredMethod("installSnapshotAndCollect", long.class, Runnable.class); //$NON-NLS-1$
    install.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<Runnable> coveredActions = (List<Runnable>)install.invoke(sequencer, 2L, (Runnable)() -> applied.add("snapshot-installed")); //$NON-NLS-1$
    for (Runnable action : coveredActions)
    {
      action.run();
    }

    Method resume = sequencer.getClass().getDeclaredMethod("resume"); //$NON-NLS-1$
    resume.setAccessible(true);
    resume.invoke(sequencer);
    Method drain = sequencer.getClass().getDeclaredMethod("drain"); //$NON-NLS-1$
    drain.setAccessible(true);
    drain.invoke(sequencer);

    assertEquals(List.of("snapshot-installed", "covered-commit-invalidation", "after-snapshot"), applied); //$NON-NLS-1$
    assertEquals(3L, currentCount(sequencer));
    assertNotNull(coveredCompletion);
  }

  public void testSnapshotHealsPermanentGapAndDiscardsCoveredCarrier() throws Exception
  {
    Object sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    suspend(sequencer, 0L);
    enqueue(sequencer, 2L, () -> applied.add("stale-B+2")); //$NON-NLS-1$

    assertTrue(applied.isEmpty());
    installSnapshot(sequencer, 2L, () -> applied.add("snapshot")); //$NON-NLS-1$
    resume(sequencer);
    drain(sequencer);

    assertEquals(List.of("snapshot"), applied); //$NON-NLS-1$
    assertEquals(2L, currentCount(sequencer));
    assertFalse(isSuspended(sequencer));

    enqueue(sequencer, 3L, () -> applied.add("baseline+1")); //$NON-NLS-1$
    assertEquals(List.of("snapshot", "baseline+1"), applied); //$NON-NLS-1$
    assertEquals(3L, currentCount(sequencer));
  }

  public void testSnapshotRetainsBufferedCarrierAboveBaseline() throws Exception
  {
    Object sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    suspend(sequencer, 0L);
    enqueue(sequencer, 2L, () -> applied.add("B+2")); //$NON-NLS-1$

    installSnapshot(sequencer, 1L, () -> applied.add("snapshot")); //$NON-NLS-1$
    resume(sequencer);
    drain(sequencer);

    assertEquals(List.of("snapshot", "B+2"), applied); //$NON-NLS-1$
    assertEquals(2L, currentCount(sequencer));
    assertFalse(isSuspended(sequencer));
  }

  public void testFailedRecoveryClosesSuspendedSequencerAndFailsBufferedWaiter() throws Exception
  {
    Object sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    suspend(sequencer, 0L);
    Object completion = enqueueCompletion(sequencer, 2L, () -> applied.add("must-not-run")); //$NON-NLS-1$
    IllegalStateException failure = new IllegalStateException("snapshot request failed"); //$NON-NLS-1$

    Method close = sequencer.getClass().getDeclaredMethod("close", Throwable.class); //$NON-NLS-1$
    close.setAccessible(true);
    close.invoke(sequencer, failure);

    Method await = completion.getClass().getDeclaredMethod("awaitUninterruptibly"); //$NON-NLS-1$
    await.setAccessible(true);
    try
    {
      await.invoke(completion);
      fail("A failed recovery must release pending waiters with failure"); //$NON-NLS-1$
    }
    catch (InvocationTargetException ex)
    {
      assertSame(failure, ex.getCause());
    }

    assertEquals(Collections.emptyList(), applied);
    assertEquals(0L, currentCount(sequencer));
    assertTrue(isSuspended(sequencer));
  }

  private Object newSequencer() throws Exception
  {
    Class<?> type = Class.forName("org.eclipse.emf.internal.cdo.session.LockChangeSequencer"); //$NON-NLS-1$
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    return constructor.newInstance();
  }

  private void enqueue(Object sequencer, long count, Runnable action) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("enqueueAndDrain", long.class, Runnable.class); //$NON-NLS-1$
    method.setAccessible(true);
    method.invoke(sequencer, count, action);
  }

  private Object enqueueCompletion(Object sequencer, long count, Runnable action) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("enqueue", long.class, Runnable.class); //$NON-NLS-1$
    method.setAccessible(true);
    return method.invoke(sequencer, count, action);
  }

  private long currentCount(Object sequencer) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("getCurrentLockModCount"); //$NON-NLS-1$
    method.setAccessible(true);
    return (long)method.invoke(sequencer);
  }

  private void suspend(Object sequencer, long staleCutoff) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("suspend", long.class); //$NON-NLS-1$
    method.setAccessible(true);
    method.invoke(sequencer, staleCutoff);
  }

  private void installSnapshot(Object sequencer, long baseline, Runnable replacement) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("installSnapshotAndCollect", long.class, Runnable.class); //$NON-NLS-1$
    method.setAccessible(true);
    method.invoke(sequencer, baseline, replacement);
  }

  private void resume(Object sequencer) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("resume"); //$NON-NLS-1$
    method.setAccessible(true);
    method.invoke(sequencer);
  }

  private void drain(Object sequencer) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("drain"); //$NON-NLS-1$
    method.setAccessible(true);
    method.invoke(sequencer);
  }

  private boolean isSuspended(Object sequencer) throws Exception
  {
    Method method = sequencer.getClass().getDeclaredMethod("isSuspended"); //$NON-NLS-1$
    method.setAccessible(true);
    return (boolean)method.invoke(sequencer);
  }
}
