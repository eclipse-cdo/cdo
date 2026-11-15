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

import org.eclipse.emf.internal.cdo.session.LockChangeSequencer;
import org.eclipse.emf.internal.cdo.session.LockChangeSequencer.BaselineActionResult;
import org.eclipse.emf.internal.cdo.session.LockChangeSequencer.Completion;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Tests ordered application of server-authoritative lock changes on a session.
 *
 * @author Eike Stepper
 */
public class LockChangeSequencerTest extends PlainTest
{
  public void testRealZeroAndUnspecifiedQueryBaselinesAreDistinct()
  {
    assertEquals(-1L, CDOSessionProtocol.UNSPECIFIED_LOCK_MOD_COUNT);
    assertEquals(0L, new CDOSessionProtocol.LockStateQueryResult(0L, Collections.emptyList()).getLockModCount());
    assertEquals(-1L, new CDOSessionProtocol.LockStateQueryResult(CDOSessionProtocol.UNSPECIFIED_LOCK_MOD_COUNT, Collections.emptyList()).getLockModCount());
  }

  public void testBaselineZeroActionRunsBeforeMutationOne() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();

    assertTrue(registerBaselineAction(sequencer, 0L, () -> applied.add("query-0"))); //$NON-NLS-1$
    enqueue(sequencer, 1L, () -> applied.add("mutation-1")); //$NON-NLS-1$

    assertEquals(List.of("query-0", "mutation-1"), applied); //$NON-NLS-1$
  }

  public void testImmediatelyAdmissibleBaselineActionRunsBeforeReturn() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    Runnable action = () -> applied.add("query-0"); //$NON-NLS-1$

    assertEquals(BaselineActionResult.APPLIED, sequencer.registerBaselineActionAndRunIfImmediatelyAdmissible(0L, action));
    assertEquals(List.of("query-0"), applied); //$NON-NLS-1$
    assertEquals(0L, currentCount(sequencer));
  }

  public void testImmediateBaselineDoesNotBypassAnActiveDrainer() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch actionStarted = new CountDownLatch(1);
    CountDownLatch releaseAction = new CountDownLatch(1);

    Runnable activeAction = () -> {
      actionStarted.countDown();
      try
      {
        if (!releaseAction.await(10, TimeUnit.SECONDS))
        {
          throw new IllegalStateException("The test did not release the active baseline action"); //$NON-NLS-1$
        }
        applied.add("active"); //$NON-NLS-1$
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }
    };

    Runnable queuedAction = () -> applied.add("queued"); //$NON-NLS-1$
    assertTrue(registerBaselineAction(sequencer, 0L, activeAction));

    Thread drainer = new Thread(() -> {
      try
      {
        drain(sequencer);
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }, "lock-change-baseline-drainer"); //$NON-NLS-1$

    drainer.start();

    try
    {
      assertTrue("The baseline action did not start", actionStarted.await(10, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("QUEUED", registerBaselineActionAndRunImmediately(sequencer, 0L, queuedAction).toString()); //$NON-NLS-1$
    }
    finally
    {
      releaseAction.countDown();
      drainer.join(10000);
    }

    assertFalse("The baseline drainer did not finish", drainer.isAlive()); //$NON-NLS-1$
    assertEquals(List.of("active", "queued"), applied); //$NON-NLS-1$
  }

  public void testFutureBaselineActionRunsBetweenItsMutationAndTheNext() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    Runnable action = () -> applied.add("query-2"); //$NON-NLS-1$

    assertEquals("QUEUED", registerBaselineActionAndRunImmediately(sequencer, 2L, action).toString()); //$NON-NLS-1$
    enqueue(sequencer, 1L, () -> applied.add("mutation-1")); //$NON-NLS-1$
    assertEquals(List.of("mutation-1"), applied); //$NON-NLS-1$
    enqueue(sequencer, 2L, () -> applied.add("mutation-2")); //$NON-NLS-1$

    assertEquals(List.of("mutation-1", "mutation-2", "query-2"), applied); //$NON-NLS-1$
  }

  public void testSameBaselineActionsAreFifoBeforeNextMutation() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();

    registerBaselineAction(sequencer, 0L, () -> applied.add("first")); //$NON-NLS-1$
    Runnable secondAction = () -> applied.add("second"); //$NON-NLS-1$
    assertEquals("QUEUED", registerBaselineActionAndRunImmediately(sequencer, 0L, secondAction).toString()); //$NON-NLS-1$
    enqueue(sequencer, 1L, () -> applied.add("mutation")); //$NON-NLS-1$

    assertEquals(List.of("first", "second", "mutation"), applied); //$NON-NLS-1$
  }

  public void testSnapshotSupersedesCoveredBaselineActionsAndRetainsLaterOnes() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    registerBaselineAction(sequencer, 5L, () -> applied.add("covered-query")); //$NON-NLS-1$
    registerBaselineAction(sequencer, 8L, () -> applied.add("later-query")); //$NON-NLS-1$

    installSnapshot(sequencer, 7L, () -> applied.add("snapshot")); //$NON-NLS-1$
    resume(sequencer);
    drain(sequencer);

    assertEquals(List.of("snapshot"), applied); //$NON-NLS-1$
    enqueue(sequencer, 8L, () -> applied.add("mutation-8")); //$NON-NLS-1$
    assertEquals(List.of("snapshot", "mutation-8", "later-query"), applied); //$NON-NLS-1$
  }

  public void testBaselineCompletionReportsSuccessfulExecution() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    CompletableFuture<Boolean> completion = new CompletableFuture<>();
    List<String> applied = new ArrayList<>();

    assertTrue(registerBaselineAction(sequencer, 0L, () -> applied.add("query"), completion)); //$NON-NLS-1$
    drain(sequencer);

    assertEquals(Boolean.TRUE, completion.get(5, TimeUnit.SECONDS));
    assertEquals(List.of("query"), applied); //$NON-NLS-1$
  }

  public void testSnapshotCompletesSupersededBaselineWithoutRunningIt() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    CompletableFuture<Boolean> completion = new CompletableFuture<>();
    List<String> applied = new ArrayList<>();

    assertTrue(registerBaselineAction(sequencer, 5L, () -> applied.add("stale-query"), completion)); //$NON-NLS-1$
    installSnapshot(sequencer, 5L, () -> applied.add("snapshot")); //$NON-NLS-1$

    assertEquals(Boolean.FALSE, completion.get(5, TimeUnit.SECONDS));
    assertEquals(List.of("snapshot"), applied); //$NON-NLS-1$
  }

  public void testSessionClosureCompletesPendingBaselineExceptionally() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    CompletableFuture<Boolean> completion = new CompletableFuture<>();
    List<String> applied = new ArrayList<>();
    suspend(sequencer, 0L);

    assertTrue(registerBaselineAction(sequencer, 0L, () -> applied.add("must-not-run"), completion)); //$NON-NLS-1$
    sequencer.close(null);

    try
    {
      completion.get(5, TimeUnit.SECONDS);
      fail("Session closure must release the query waiter with failure"); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
    }

    assertTrue(applied.isEmpty());
  }

  public void testSessionClosureWaitsForSelectedBaselineActionToFinish() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    CompletableFuture<Boolean> completion = new CompletableFuture<>();
    CountDownLatch actionStarted = new CountDownLatch(1);
    CountDownLatch releaseAction = new CountDownLatch(1);
    CountDownLatch actionFinished = new CountDownLatch(1);
    CountDownLatch closeStarted = new CountDownLatch(1);
    CountDownLatch closeReturned = new CountDownLatch(1);
    List<String> applied = Collections.synchronizedList(new ArrayList<>());

    assertTrue(registerBaselineAction(sequencer, 0L, () -> {
      actionStarted.countDown();
      try
      {
        if (!releaseAction.await(10, TimeUnit.SECONDS))
        {
          throw new IllegalStateException("The test did not release the executing baseline action"); //$NON-NLS-1$
        }
        applied.add("query"); //$NON-NLS-1$
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }
      finally
      {
        actionFinished.countDown();
      }
    }, completion));

    Thread drainer = new Thread(() -> {
      try
      {
        drain(sequencer);
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }, "lock-change-drainer"); //$NON-NLS-1$

    Thread closer = new Thread(() -> {
      closeStarted.countDown();

      try
      {
        sequencer.close(null);
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
      finally
      {
        closeReturned.countDown();
      }
    }, "lock-change-closer"); //$NON-NLS-1$

    drainer.setDaemon(true);
    closer.setDaemon(true);

    drainer.start();
    try
    {
      assertTrue("Baseline action did not start", actionStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      closer.start();
      assertTrue("Session closure did not start", closeStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      completion.get(5, TimeUnit.SECONDS);
      fail("Session closure must release the query waiter with failure"); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
      assertEquals("Closure must wait for an action already executing", 1L, closeReturned.getCount()); //$NON-NLS-1$
    }
    finally
    {
      releaseAction.countDown();
      assertTrue("The selected action did not finish", actionFinished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      drainer.join(TimeUnit.SECONDS.toMillis(5));
      if (closer.isAlive())
      {
        assertTrue("Session closure did not return after the action finished", closeReturned.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
        closer.join(TimeUnit.SECONDS.toMillis(5));
      }

      assertFalse("The lock-change drainer did not stop", drainer.isAlive()); //$NON-NLS-1$
      assertFalse("The lock-change closer did not stop", closer.isAlive()); //$NON-NLS-1$
    }

    assertEquals(Collections.singletonList("query"), applied); //$NON-NLS-1$
  }

  public void testRecoveryFailureCompletesPendingBaselineExceptionally() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    CompletableFuture<Boolean> completion = new CompletableFuture<>();
    assertTrue(registerBaselineAction(sequencer, 2L, () -> fail("A discarded baseline must not run"), completion)); //$NON-NLS-1$

    try
    {
      enqueue(sequencer, 1L, () -> {
        throw new IllegalStateException("injected failure"); //$NON-NLS-1$
      });

      fail("The failed carrier must propagate"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertEquals("injected failure", ex.getMessage()); //$NON-NLS-1$
    }

    try
    {
      completion.get(5, TimeUnit.SECONDS);
      fail("Recovery failure must release the baseline waiter"); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      assertEquals("injected failure", ex.getCause().getMessage()); //$NON-NLS-1$
    }
  }

  public void testSuspensionPreventsBaselineActionsFromDraining() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    suspend(sequencer, 0L);
    assertTrue(registerBaselineAction(sequencer, 0L, () -> applied.add("query"))); //$NON-NLS-1$
    drain(sequencer);
    assertTrue(applied.isEmpty());

    resume(sequencer);
    drain(sequencer);
    assertEquals(List.of("query"), applied); //$NON-NLS-1$
  }

  public void testBaselineActionFailureRequestsRecovery() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    boolean[] recoveryRequested = { false };
    sequencer.setFailureHandler(cause -> recoveryRequested[0] = true);

    registerBaselineAction(sequencer, 0L, () -> {
      throw new IllegalStateException("query cache failed"); //$NON-NLS-1$
    });

    try
    {
      drain(sequencer);
      fail("An escaping baseline action failure must propagate"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertEquals("query cache failed", ex.getMessage()); //$NON-NLS-1$
    }

    assertTrue(recoveryRequested[0]);
    assertTrue(isSuspended(sequencer));
  }

  public void testBaselineBelowCurrentCountIsStale() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    enqueue(sequencer, 1L, () -> {
    });
    assertFalse(registerBaselineAction(sequencer, 0L, () -> fail("A past boundary is stale"))); //$NON-NLS-1$
  }

  public void testCloseDiscardsPendingBaselineActions() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    registerBaselineAction(sequencer, 0L, () -> applied.add("must-not-run")); //$NON-NLS-1$

    sequencer.close(null);
    drain(sequencer);

    assertTrue(applied.isEmpty());
  }

  public void testBoundaryAlreadyCrossedByExecutingMutationIsStale() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);

    Thread mutation = new Thread(() -> {
      try
      {
        enqueue(sequencer, 1L, () -> {
          started.countDown();
          try
          {
            if (!release.await(10, TimeUnit.SECONDS))
            {
              throw new IllegalStateException("The test did not release the executing mutation"); //$NON-NLS-1$
            }
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
          }
        });
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    });

    mutation.setDaemon(true);
    mutation.start();
    try
    {
      assertTrue("The mutation did not start", started.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertFalse(registerBaselineAction(sequencer, 0L, () -> fail("A crossed boundary must not accept an action"))); //$NON-NLS-1$
    }
    finally
    {
      release.countDown();
      mutation.join(TimeUnit.SECONDS.toMillis(5));
    }

    assertFalse("The mutation worker did not stop", mutation.isAlive()); //$NON-NLS-1$
  }

  public void testFutureCountIsBufferedUntilItsPredecessorArrives() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<Integer> applied = new ArrayList<>();

    enqueue(sequencer, 2L, () -> applied.add(2));
    assertTrue(applied.isEmpty());

    enqueue(sequencer, 1L, () -> applied.add(1));

    assertEquals(List.of(1, 2), applied);
    assertEquals(2L, currentCount(sequencer));
  }

  public void testStaleCountIsDiscarded() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<Integer> applied = new ArrayList<>();

    enqueue(sequencer, 1L, () -> applied.add(1));
    enqueue(sequencer, 1L, () -> applied.add(10));

    assertEquals(List.of(1), applied);
    assertEquals(1L, currentCount(sequencer));
  }

  public void testConflictingPendingCountIsRejected() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    enqueue(sequencer, 2L, () -> {
    });

    try
    {
      enqueue(sequencer, 2L, () -> {
      });
      fail("A second action must not replace the pending action for count 2");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }
  }

  public void testOwnerRemapAndUnlockAreAppliedInCountOrder() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    String[] owner = { "OLD_DURABLE" };

    enqueue(sequencer, 2L, () -> owner[0] = null);
    enqueue(sequencer, 1L, () -> owner[0] = "NEW_NORMAL");

    assertNull(owner[0]);
    assertEquals(2L, currentCount(sequencer));
  }

  public void testFailedActionFailsItsCompletionAndClosesTheSequencer() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();

    try
    {
      enqueue(sequencer, 1L, () -> {
        throw new IllegalStateException("expected failure"); //$NON-NLS-1$
      });
      fail("The failing action must be reported to its caller");
    }
    catch (IllegalStateException ex)
    {
      assertEquals("expected failure", ex.getMessage()); //$NON-NLS-1$
    }

    try
    {
      sequencer.await(1L);
      fail("A failed sequence position must not report successful completion");
    }
    catch (RuntimeException ex)
    {
      assertEquals("expected failure", ex.getCause().getMessage()); //$NON-NLS-1$
    }
  }

  public void testFailureHandlerExceptionDoesNotReplaceActionFailure()
  {
    LockChangeSequencer sequencer = newSequencer();
    IllegalStateException actionFailure = new IllegalStateException("action failure"); //$NON-NLS-1$
    IllegalArgumentException handlerFailure = new IllegalArgumentException("handler failure"); //$NON-NLS-1$
    sequencer.setFailureHandler(cause -> {
      throw handlerFailure;
    });

    try
    {
      enqueue(sequencer, 1L, () -> {
        throw actionFailure;
      });
      fail("The original action failure must propagate"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertSame(actionFailure, ex);
      assertEquals(1, ex.getSuppressed().length);
      assertSame(handlerFailure, ex.getSuppressed()[0]);
    }
  }

  public void testFailedActionDoesNotRunLaterCountsAndFailsTheirWaiters() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<Integer> applied = new ArrayList<>();
    Completion completion = enqueueCompletion(sequencer, 2L, () -> applied.add(2));

    try
    {
      enqueue(sequencer, 1L, () -> {
        applied.add(1);
        throw new IllegalStateException("expected failure"); //$NON-NLS-1$
      });
      fail("The failing sequence action must be reported"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertEquals("expected failure", ex.getMessage()); //$NON-NLS-1$
    }

    assertEquals(List.of(1), applied);
    assertEquals(0L, currentCount(sequencer));

    try
    {
      awaitCompletion(completion);
      fail("A later pending completion must be failed when its predecessor fails"); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
    }
  }

  public void testCloseFailsPendingWaitersWithoutExecutingBufferedActions() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    boolean[] applied = { false };
    Completion completion = enqueueCompletion(sequencer, 2L, () -> applied[0] = true);

    sequencer.close(new IllegalStateException("closed for test")); //$NON-NLS-1$

    try
    {
      awaitCompletion(completion);
      fail("Closing the sequencer must release pending waiters with failure"); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      assertTrue(ex.getCause() instanceof IllegalStateException);
      assertEquals("closed for test", ex.getCause().getMessage()); //$NON-NLS-1$
    }

    assertFalse(applied[0]);
    assertEquals(0L, currentCount(sequencer));
  }

  public void testSynchronousCompletionWaitsForMissingPredecessorOutsideAccess() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
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

    Thread waiter = new Thread(() -> {
      access.lock();
      access.unlock();
      started.countDown();

      try
      {
        sequencer.await(2L);
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
    waiter.setDaemon(true);
    waiter.start();
    try
    {
      assertTrue("The waiter did not start", started.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
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
      assertTrue("The waiter did not finish", completed.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    }
    finally
    {
      if (completed.getCount() != 0L)
      {
        sequencer.close(new IllegalStateException("Test cleanup released a blocked waiter")); //$NON-NLS-1$
      }

      waiter.interrupt();
      waiter.join(TimeUnit.SECONDS.toMillis(5));
    }

    assertFalse("The waiter worker did not stop", waiter.isAlive()); //$NON-NLS-1$

    assertNull(failure[0]);
    assertEquals(List.of(1, 2), applied);
  }

  public void testCommitCountWaitsForTimestampEligibilityAndThenNextCount() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
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
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();

    enqueue(sequencer, 2L, () -> applied.add("commit N+1")); //$NON-NLS-1$
    assertTrue(applied.isEmpty());

    enqueue(sequencer, 1L, () -> applied.add("lock N")); //$NON-NLS-1$

    assertEquals(List.of("lock N", "commit N+1"), applied); //$NON-NLS-1$
  }

  public void testCountZeroCannotAdvanceThePositiveSequence() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();

    try
    {
      enqueue(sequencer, 0L, () -> fail("Count zero must use a legacy path")); //$NON-NLS-1$
      fail("Count zero must not be accepted as a sequence position");
    }
    catch (IllegalArgumentException ex)
    {
      assertTrue(ex.getMessage().contains("positive lock modification count")); //$NON-NLS-1$
    }

    assertEquals(0L, currentCount(sequencer));
  }

  public void testFailedActionResynchronizesAndResumesLaterCounts() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    boolean[] recoveryRequested = { false };

    sequencer.setFailureHandler(cause -> recoveryRequested[0] = true);

    Completion coveredCompletion = sequencer.enqueue(2L, () -> applied.add("stale-lock-change"), //$NON-NLS-1$
        () -> applied.add("covered-commit-invalidation")); //$NON-NLS-1$
    try
    {
      enqueue(sequencer, 1L, () -> {
        throw new IllegalStateException("injected lock cache failure"); //$NON-NLS-1$
      });
      fail("The original failed action must still fail its caller"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertEquals("injected lock cache failure", ex.getMessage()); //$NON-NLS-1$
    }

    assertTrue(recoveryRequested[0]);
    assertEquals(0L, currentCount(sequencer));
    assertTrue(applied.isEmpty());

    List<Runnable> coveredActions = sequencer.installSnapshotAndCollect(2L, () -> applied.add("snapshot-installed")); //$NON-NLS-1$
    for (Runnable action : coveredActions)
    {
      action.run();
    }

    sequencer.resume();
    sequencer.drain();
    enqueue(sequencer, 3L, () -> applied.add("after-snapshot")); //$NON-NLS-1$

    assertEquals(List.of("snapshot-installed", "covered-commit-invalidation", "after-snapshot"), applied); //$NON-NLS-1$
    assertEquals(3L, currentCount(sequencer));
    assertNotNull(coveredCompletion);
  }

  public void testSnapshotHealsPermanentGapAndDiscardsCoveredCarrier() throws Exception
  {
    LockChangeSequencer sequencer = newSequencer();
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
    LockChangeSequencer sequencer = newSequencer();
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
    LockChangeSequencer sequencer = newSequencer();
    List<String> applied = new ArrayList<>();
    suspend(sequencer, 0L);
    Completion completion = enqueueCompletion(sequencer, 2L, () -> applied.add("must-not-run")); //$NON-NLS-1$
    IllegalStateException failure = new IllegalStateException("snapshot request failed"); //$NON-NLS-1$

    sequencer.close(failure);

    try
    {
      awaitCompletion(completion);
      fail("A failed recovery must release pending waiters with failure"); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      assertSame(failure, ex.getCause());
    }

    assertEquals(Collections.emptyList(), applied);
    assertEquals(0L, currentCount(sequencer));
    assertTrue(isSuspended(sequencer));
  }

  private LockChangeSequencer newSequencer()
  {
    return new LockChangeSequencer();
  }

  private void enqueue(LockChangeSequencer sequencer, long count, Runnable action)
  {
    sequencer.enqueueAndDrain(count, action);
  }

  private Completion enqueueCompletion(LockChangeSequencer sequencer, long count, Runnable action)
  {
    return sequencer.enqueue(count, action);
  }

  private boolean registerBaselineAction(LockChangeSequencer sequencer, long baseline, Runnable action)
  {
    return sequencer.registerBaselineAction(baseline, action);
  }

  private BaselineActionResult registerBaselineActionAndRunImmediately(LockChangeSequencer sequencer, long baseline, Runnable action)
  {
    return sequencer.registerBaselineActionAndRunIfImmediatelyAdmissible(baseline, action);
  }

  private boolean registerBaselineAction(LockChangeSequencer sequencer, long baseline, Runnable action, CompletableFuture<Boolean> completion)
  {
    return sequencer.registerBaselineAction(baseline, action, completion);
  }

  private long currentCount(LockChangeSequencer sequencer)
  {
    return sequencer.getCurrentLockModCount();
  }

  private void suspend(LockChangeSequencer sequencer, long staleCutoff)
  {
    sequencer.suspend(staleCutoff);
  }

  private void installSnapshot(LockChangeSequencer sequencer, long baseline, Runnable replacement)
  {
    sequencer.installSnapshotAndCollect(baseline, replacement);
  }

  private void resume(LockChangeSequencer sequencer)
  {
    sequencer.resume();
  }

  private void drain(LockChangeSequencer sequencer)
  {
    sequencer.drain();
  }

  private boolean isSuspended(LockChangeSequencer sequencer)
  {
    return sequencer.isSuspended();
  }

  private void awaitCompletion(Completion completion) throws Exception
  {
    FutureTask<Void> task = new FutureTask<>(() -> {
      completion.await();
      return null;
    });
    Thread waiter = new Thread(task, "lock-change-completion-waiter"); //$NON-NLS-1$
    waiter.setDaemon(true);
    waiter.start();

    try
    {
      task.get(5, TimeUnit.SECONDS);
    }
    finally
    {
      if (!task.isDone())
      {
        task.cancel(true);
      }

      waiter.join(TimeUnit.SECONDS.toMillis(1));
      assertFalse("The completion waiter did not stop", waiter.isAlive()); //$NON-NLS-1$
    }
  }
}
