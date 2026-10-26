/*
 * Copyright (c) 2015, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.tests.ThreadPoolTest.TaskManager.Task;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test for {@link ThreadPool}.
 *
 * @author Eike Stepper
 */
public class ThreadPoolTest extends AbstractOMTest
{
  public void testWorkerBlockedInAfterExecuteIsNotAvailable() throws Exception
  {
    @SuppressWarnings("all") // resource
    AfterExecuteGateThreadPool pool = new AfterExecuteGateThreadPool();

    CountDownLatch firstTaskFinished = new CountDownLatch(1);
    CountDownLatch secondTaskStarted = new CountDownLatch(1);
    CountDownLatch releaseSecondTask = new CountDownLatch(1);
    Runnable firstTask = firstTaskFinished::countDown;
    pool.setGatedTask(firstTask);

    try
    {
      pool.execute(firstTask);
      assertTrue("First task did not enter afterExecute", pool.awaitAfterExecute());

      pool.execute(() -> {
        secondTaskStarted.countDown();
        awaitUninterruptibly(releaseSecondTask);
      });

      assertTrue("Second task did not start on an expansion worker", secondTaskStarted.await(500, TimeUnit.MILLISECONDS));
      assertEquals(2, pool.getPoolSize());
      assertEquals("Second task was queued behind the worker still in afterExecute", 0, pool.getQueue().size());
      assertTrue("First task did not complete", firstTaskFinished.await(0, TimeUnit.MILLISECONDS));
    }
    finally
    {
      pool.releaseAfterExecute();
      releaseSecondTask.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testConcurrentSubmissionsObserveSameAvailableWorker() throws Exception
  {
    ThreadPool pool = ThreadPool.create("concurrent-submissions", 1, 2, 60);
    ExecutorService submitters = Executors.newFixedThreadPool(2);
    CountDownLatch releaseTasks = new CountDownLatch(1);
    CountDownLatch firstTaskStarted = new CountDownLatch(1);
    CountDownLatch tasksStarted = new CountDownLatch(2);
    CountDownLatch allSubmissionsFinished = new CountDownLatch(2);
    CountDownLatch releaseSubmitters = new CountDownLatch(1);

    Runnable taskA = createBlockingTask(firstTaskStarted, tasksStarted, releaseTasks);
    Runnable taskB = createBlockingTask(firstTaskStarted, tasksStarted, releaseTasks);

    try
    {
      assertTrue("Core worker could not be prestarted", pool.prestartCoreThread());

      Future<?> submissionA = submitters.submit(() -> {
        awaitUninterruptibly(releaseSubmitters);
        pool.execute(taskA);
        allSubmissionsFinished.countDown();
      });
      Future<?> submissionB = submitters.submit(() -> {
        awaitUninterruptibly(releaseSubmitters);
        pool.execute(taskB);
        allSubmissionsFinished.countDown();
      });

      releaseSubmitters.countDown();
      assertTrue("Submissions did not finish", allSubmissionsFinished.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      submissionA.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      submissionB.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      assertTrue("First task did not start", firstTaskStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertTrue("Second task did not start while the first remained blocked", tasksStarted.await(500, TimeUnit.MILLISECONDS));

      assertEquals(2, pool.getPoolSize());
      assertEquals(2, pool.getActiveCount());
      assertEquals(0, pool.getQueue().size());
    }
    finally
    {
      releaseSubmitters.countDown();
      releaseTasks.countDown();
      submitters.shutdownNow();
      submitters.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testWorkerCreationFailureDoesNotStrandAcceptedTask() throws Exception
  {
    @SuppressWarnings("all") // resource
    ThreadPool pool = new ThreadPool(1, 2, 60, task -> null);

    CountDownLatch taskStarted = new CountDownLatch(1);
    boolean rejected = false;

    try
    {
      try
      {
        pool.execute(taskStarted::countDown);
      }
      catch (RejectedExecutionException ex)
      {
        rejected = true;
      }

      assertEquals(0, pool.getPoolSize());
      assertEquals(0, pool.getActiveCount());
      assertEquals(0, pool.getQueue().size());
      assertFalse("Rejected task unexpectedly ran", taskStarted.await(0, TimeUnit.MILLISECONDS));
      assertTrue("Task creation failure was not reported", rejected);
    }
    finally
    {
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testWorkerCreationFailureUsesConfiguredRejectionHandler() throws Exception
  {
    @SuppressWarnings("all") // resource
    ThreadPool pool = new ThreadPool(1, 2, 60, task -> null);

    AtomicInteger rejectedTasks = new AtomicInteger();

    try
    {
      pool.setRejectedExecutionHandler((task, executor) -> rejectedTasks.incrementAndGet());
      pool.execute(() -> {
      });

      assertEquals(1, rejectedTasks.get());
      assertEquals(0, pool.getQueue().size());
    }
    finally
    {
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testShutdownNowDrainsAndReturnsQueuedTasks() throws Exception
  {
    ThreadPool pool = ThreadPool.create("shutdown-drain", 1, 2, 60);
    CountDownLatch releaseTasks = new CountDownLatch(1);
    CountDownLatch tasksStarted = new CountDownLatch(2);
    CountDownLatch queuedTaskStarted = new CountDownLatch(1);
    Runnable taskA = createBlockingTask(tasksStarted, releaseTasks);
    Runnable taskB = createBlockingTask(tasksStarted, releaseTasks);
    Runnable queuedTask = queuedTaskStarted::countDown;

    try
    {
      pool.execute(taskA);
      pool.execute(taskB);
      assertTrue("Both workers did not start", tasksStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertEquals(2, pool.getPoolSize());

      pool.execute(queuedTask);
      assertEquals(1, pool.getQueue().size());

      List<Runnable> waitingTasks = pool.shutdownNow();
      assertEquals(1, waitingTasks.size());
      assertSame(queuedTask, waitingTasks.get(0));
      assertEquals(0, pool.getQueue().size());
      assertFalse("Task drained by shutdownNow unexpectedly ran", queuedTaskStarted.await(0, TimeUnit.MILLISECONDS));
    }
    finally
    {
      releaseTasks.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testRejectedExecutionFallbackCannotQueueAfterShutdown() throws Exception
  {
    @SuppressWarnings("all") // resource
    ShutdownRaceThreadPool pool = new ShutdownRaceThreadPool();

    ExecutorService submitter = Executors.newSingleThreadExecutor();
    CountDownLatch taskStarted = new CountDownLatch(1);
    CountDownLatch submissionFinished = new CountDownLatch(1);

    try
    {
      pool.gateNextShutdownCheck();
      Future<?> submission = submitter.submit(() -> {
        try
        {
          pool.execute(taskStarted::countDown);
        }
        finally
        {
          submissionFinished.countDown();
        }
      });

      assertTrue("Rejected-execution handler did not reach its shutdown check", pool.awaitShutdownCheck());
      pool.shutdown();
      assertTrue("Empty thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.releaseShutdownCheck();

      assertTrue("Submission did not finish", submissionFinished.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      boolean rejected = false;
      try
      {
        submission.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      }
      catch (ExecutionException ex)
      {
        assertTrue("Unexpected submission failure", ex.getCause() instanceof RejectedExecutionException);
        rejected = true;
      }

      assertTrue("Task was not rejected after shutdown", rejected);
      assertTrue("Thread pool did not remain terminated", pool.isTerminated());
      assertEquals(0, pool.getQueue().size());
      assertFalse("Rejected task unexpectedly ran", taskStarted.await(0, TimeUnit.MILLISECONDS));
    }
    finally
    {
      pool.releaseShutdownCheck();
      submitter.shutdownNow();
      submitter.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testWorkerAvailabilityRace() throws Exception
  {
    @SuppressWarnings("all") // resource
    RaceThreadPool pool = new RaceThreadPool();

    CountDownLatch taskAStarted = new CountDownLatch(1);
    CountDownLatch releaseTaskA = new CountDownLatch(1);
    CountDownLatch taskBStarted = new CountDownLatch(1);

    Runnable taskA = new Runnable()
    {
      @Override
      public void run()
      {
        taskAStarted.countDown();
        awaitUninterruptibly(releaseTaskA);
      }
    };

    pool.setGatedTask(taskA);

    try
    {
      pool.execute(taskA);
      assertTrue("First worker did not reach beforeExecute", pool.awaitBeforeExecute());

      pool.execute(new Runnable()
      {
        @Override
        public void run()
        {
          taskBStarted.countDown();
        }
      });

      assertTrue("Task B did not start while task A was paused", taskBStarted.await(500, TimeUnit.MILLISECONDS));
      assertEquals(2, pool.getPoolSize());
      assertEquals(0, pool.getQueue().size());

      pool.releaseBeforeExecute();
      assertTrue("Task A did not start", taskAStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

      assertEquals(2, pool.getPoolSize());
      assertEquals(0, pool.getQueue().size());
    }
    finally
    {
      pool.releaseBeforeExecute();
      releaseTaskA.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testRepeatedRunnableSubmissionsKeepIndependentLifecycle() throws Exception
  {
    DuplicateLifecycleThreadPool pool = new DuplicateLifecycleThreadPool();
    CountDownLatch firstRunStarted = new CountDownLatch(1);
    CountDownLatch releaseFirstRun = new CountDownLatch(1);
    CountDownLatch secondRunStarted = new CountDownLatch(1);
    CountDownLatch releaseSecondRun = new CountDownLatch(1);
    CountDownLatch thirdRunStarted = new CountDownLatch(1);
    CountDownLatch releaseThirdRun = new CountDownLatch(1);
    AtomicInteger runCount = new AtomicInteger();
    Runnable sameTask = () -> {
      int run = runCount.incrementAndGet();
      if (run == 1)
      {
        firstRunStarted.countDown();
        awaitUninterruptibly(releaseFirstRun);
      }
      else if (run == 2)
      {
        secondRunStarted.countDown();
        awaitUninterruptibly(releaseSecondRun);
      }
      else
      {
        thirdRunStarted.countDown();
        awaitUninterruptibly(releaseThirdRun);
      }
    };

    pool.setTrackedTask(sameTask);

    try
    {
      pool.execute(sameTask);
      assertTrue("First task execution did not start", firstRunStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(sameTask);
      assertTrue("Second task execution did not start", secondRunStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(sameTask);
      assertEquals(1, pool.getQueue().size());
      assertEquals(1, getPendingTaskCount(pool));

      releaseSecondRun.countDown();
      assertTrue("Second execution did not reach afterExecute", pool.awaitSecondAfterExecute());
      assertEquals("Completion consumed another submission's reservation", 1, getPendingTaskCount(pool));
      pool.releaseSecondAfterExecute();
      assertTrue("Queued duplicate execution did not start", thirdRunStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertEquals(0, getPendingTaskCount(pool));

      releaseThirdRun.countDown();
      releaseFirstRun.countDown();
      assertEquals(3, runCount.get());
    }
    finally
    {
      releaseFirstRun.countDown();
      releaseSecondRun.countDown();
      releaseThirdRun.countDown();
      pool.releaseSecondAfterExecute();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testWorkerPickupDoesNotReleaseReservationBeforeBeforeExecute() throws Exception
  {
    PickupGateThreadPool pool = new PickupGateThreadPool();
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch secondStarted = new CountDownLatch(1);
    CountDownLatch releaseFirst = new CountDownLatch(1);
    CountDownLatch releaseSecond = new CountDownLatch(1);
    AtomicInteger runCount = new AtomicInteger();
    Runnable sameTask = () -> {
      int run = runCount.incrementAndGet();
      if (run == 1)
      {
        firstStarted.countDown();
        awaitUninterruptibly(releaseFirst);
      }
      else if (run == 2)
      {
        secondStarted.countDown();
        awaitUninterruptibly(releaseSecond);
      }
    };

    pool.setTrackedTask(sameTask);

    try
    {
      pool.execute(sameTask);
      assertTrue("First execution did not start", firstStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(sameTask);
      assertTrue("Second execution did not start", secondStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(sameTask);
      pool.execute(sameTask);
      assertEquals(2, pool.getQueue().size());
      assertEquals(2, getPendingTaskCount(pool));

      releaseSecond.countDown();
      assertTrue("Worker did not pause after queue pickup", pool.awaitThirdBeforeExecute());
      assertEquals(1, pool.getQueue().size());
      assertEquals("Queue pickup released a reservation before beforeExecute", 2, getPendingTaskCount(pool));

      pool.releaseThirdBeforeExecute();
      assertTrue("Worker did not pause before the following duplicate", pool.awaitFourthBeforeExecute());
      assertEquals("beforeExecute did not release exactly the picked submission", 1, getPendingTaskCount(pool));
    }
    finally
    {
      pool.releaseThirdBeforeExecute();
      pool.releaseFourthBeforeExecute();
      releaseFirst.countDown();
      releaseSecond.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testWorkerCanRemoveAnotherQueuedTaskAndReleaseItsReservation() throws Exception
  {
    ThreadPool pool = ThreadPool.create("worker-removal", 1, 2, 60);
    CountDownLatch releaseSecondWorker = new CountDownLatch(1);
    CountDownLatch firstWorkerReady = new CountDownLatch(1);
    CountDownLatch secondWorkerReady = new CountDownLatch(1);
    CountDownLatch removeTask = new CountDownLatch(1);
    CountDownLatch removalFinished = new CountDownLatch(1);
    Runnable removedTask = () -> {
    };
    Runnable polledTask = () -> {
    };
    Runnable drainedTask = () -> {
    };
    AtomicBoolean removed = new AtomicBoolean();
    AtomicBoolean polled = new AtomicBoolean();
    AtomicBoolean drained = new AtomicBoolean();
    Runnable firstWorkerTask = () -> {
      firstWorkerReady.countDown();
      awaitUninterruptibly(removeTask);
      removed.set(pool.getQueue().remove(removedTask));
      polled.set(pool.getQueue().poll() == polledTask);
      List<Runnable> drainedTasks = new ArrayList<>();
      drained.set(pool.getQueue().drainTo(drainedTasks) == 1 && drainedTasks.get(0) == drainedTask);
      removalFinished.countDown();
    };

    try
    {
      pool.execute(firstWorkerTask);
      assertTrue("First worker task did not start", firstWorkerReady.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(createBlockingTask(secondWorkerReady, releaseSecondWorker));
      assertTrue("Second worker task did not start", secondWorkerReady.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(removedTask);
      pool.execute(polledTask);
      pool.execute(drainedTask);
      assertEquals(3, pool.getQueue().size());
      assertEquals(3, getPendingTaskCount(pool));

      removeTask.countDown();
      assertTrue("Worker did not finish queue removal", removalFinished.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertTrue("Worker did not remove the queued task", removed.get());
      assertTrue("Worker poll did not remove the queued task", polled.get());
      assertTrue("Worker drainTo did not remove the queued task", drained.get());
      assertEquals(0, pool.getQueue().size());
      assertEquals("Worker queue removal left a stale reservation", 0, getPendingTaskCount(pool));
    }
    finally
    {
      removeTask.countDown();
      releaseSecondWorker.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testEqualButDistinctQueueRemovalReleasesRemovedSubmission() throws Exception
  {
    ThreadPool pool = ThreadPool.create("equal-queue-removal", 1, 2, 60);
    CountDownLatch releaseWorkers = new CountDownLatch(1);
    CountDownLatch workersStarted = new CountDownLatch(2);
    Runnable queuedTask = new EqualRunnable(7);

    try
    {
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      assertTrue("Both workers did not start", workersStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(queuedTask);
      assertEquals(1, getPendingTaskCount(pool));

      assertTrue("Equal queue removal did not remove the queued task", pool.getQueue().remove(new EqualRunnable(7)));
      assertEquals(0, pool.getQueue().size());
      assertEquals("Equal removal released the wrong identity reservation", 0, getPendingTaskCount(pool));
    }
    finally
    {
      releaseWorkers.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testMutableHashCodeQueueRemovalReleasesReservation() throws Exception
  {
    ThreadPool pool = ThreadPool.create("mutable-hash-removal", 1, 2, 60);
    CountDownLatch releaseWorkers = new CountDownLatch(1);
    CountDownLatch workersStarted = new CountDownLatch(2);
    MutableHashRunnable queuedTask = new MutableHashRunnable();

    try
    {
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      assertTrue("Both workers did not start", workersStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(queuedTask);
      assertEquals(1, getPendingTaskCount(pool));

      queuedTask.changeHashCode();
      assertTrue("Queue did not remove the same mutable task", pool.getQueue().remove(queuedTask));
      assertEquals("Removal left the mutable task reservation behind", 0, getPendingTaskCount(pool));
    }
    finally
    {
      releaseWorkers.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testMutableHashCodeExecutionReleasesReservation() throws Exception
  {
    ThreadPool pool = ThreadPool.create("mutable-hash-execution", 1, 2, 60);
    CountDownLatch releaseWorkers = new CountDownLatch(1);
    CountDownLatch workersStarted = new CountDownLatch(2);
    CountDownLatch mutableTaskStarted = new CountDownLatch(1);
    MutableHashRunnable queuedTask = new MutableHashRunnable(mutableTaskStarted);

    try
    {
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      assertTrue("Both workers did not start", workersStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(queuedTask);
      assertEquals(1, getPendingTaskCount(pool));

      queuedTask.changeHashCode();
      releaseWorkers.countDown();
      assertTrue("Mutable task did not execute", mutableTaskStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertEquals("Execution left the mutable task reservation behind", 0, getPendingTaskCount(pool));
    }
    finally
    {
      releaseWorkers.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testShutdownNowReleasesRepeatedRunnableReservationsOnce() throws Exception
  {
    ThreadPool pool = ThreadPool.create("shutdown-duplicate-drain", 1, 2, 60);
    CountDownLatch releaseWorkers = new CountDownLatch(1);
    CountDownLatch workersStarted = new CountDownLatch(2);
    Runnable sameTask = createBlockingTask(workersStarted, releaseWorkers);

    try
    {
      pool.execute(sameTask);
      pool.execute(sameTask);
      assertTrue("Both workers did not start", workersStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      pool.execute(sameTask);
      pool.execute(sameTask);
      assertEquals(2, pool.getQueue().size());
      assertEquals(2, getPendingTaskCount(pool));

      List<Runnable> drained = pool.shutdownNow();
      assertEquals(2, drained.size());
      assertEquals(0, pool.getQueue().size());
      assertEquals("shutdownNow did not release each queued reservation exactly once", 0, getPendingTaskCount(pool));
    }
    finally
    {
      releaseWorkers.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  private static int getPendingTaskCount(ThreadPool pool) throws Exception
  {
    Field field = ThreadPool.class.getDeclaredField("pendingTaskCount");
    field.setAccessible(true);
    return field.getInt(pool);
  }

  public void testQueueRemovalOperationsDoNotExecuteRemovedTasks() throws Exception
  {
    ThreadPool pool = ThreadPool.create("queue-removals", 1, 2, 60);
    CountDownLatch releaseWorkers = new CountDownLatch(1);
    CountDownLatch workersStarted = new CountDownLatch(2);
    AtomicInteger removedTaskRuns = new AtomicInteger();
    Runnable polledTask = removedTaskRuns::incrementAndGet;
    Runnable drainedTask = removedTaskRuns::incrementAndGet;
    Runnable iteratedTask = removedTaskRuns::incrementAndGet;
    Runnable executorRemovedTask = removedTaskRuns::incrementAndGet;
    FutureTask<Void> purgedTask = new FutureTask<>(() -> {
      removedTaskRuns.incrementAndGet();
      return null;
    });

    try
    {
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      assertTrue("Both workers did not start", workersStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

      pool.execute(polledTask);
      pool.execute(drainedTask);
      pool.execute(iteratedTask);
      pool.execute(executorRemovedTask);
      pool.execute(purgedTask);
      assertEquals(5, pool.getQueue().size());

      assertSame(polledTask, pool.getQueue().poll());

      List<Runnable> drainedTasks = new ArrayList<>();
      assertEquals(1, pool.getQueue().drainTo(drainedTasks, 1));
      assertSame(drainedTask, drainedTasks.get(0));

      Iterator<Runnable> iterator = pool.getQueue().iterator();
      assertSame(iteratedTask, iterator.next());
      iterator.remove();

      assertTrue("ThreadPoolExecutor.remove did not remove the task", pool.remove(executorRemovedTask));
      purgedTask.cancel(false);
      pool.purge();

      assertEquals(0, pool.getQueue().size());
      assertEquals("Queue removals left stale task reservations", 0, getPendingTaskCount(pool));
      releaseWorkers.countDown();
      pool.shutdown();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertEquals(0, removedTaskRuns.get());
    }
    finally
    {
      releaseWorkers.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testRejectionFallbackQueuesWhenMaximumWorkersAreActive() throws Exception
  {
    ThreadPool pool = ThreadPool.create("maximum-worker-fallback", 1, 2, 60);
    CountDownLatch releaseWorkers = new CountDownLatch(1);
    CountDownLatch workersStarted = new CountDownLatch(2);
    CountDownLatch fallbackTaskStarted = new CountDownLatch(1);

    try
    {
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      pool.execute(createBlockingTask(workersStarted, releaseWorkers));
      assertTrue("Both maximum workers did not start", workersStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

      pool.rejectedExecution(fallbackTaskStarted::countDown, pool);

      assertEquals(2, pool.getPoolSize());
      assertEquals(2, pool.getActiveCount());
      assertEquals(1, pool.getQueue().size());
      assertFalse("Fallback task ran while both workers remained blocked", fallbackTaskStarted.await(0, TimeUnit.MILLISECONDS));

      releaseWorkers.countDown();
      assertTrue("Fallback task did not run after a worker became available", fallbackTaskStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
    finally
    {
      releaseWorkers.countDown();
      pool.shutdownNow();
      assertTrue("Thread pool did not terminate", pool.awaitTermination(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    }
  }

  public void testExceedMaximumPoolSize() throws Exception
  {
    final ThreadPool pool = ThreadPool.create("test", 10, 20, 60);

    try
    {
      final int tasks = pool.getMaximumPoolSize() + 100;
      final CountDownLatch latch = new CountDownLatch(tasks);

      for (int i = 0; i < tasks; i++)
      {
        final int n = i;
        msg("scheduling " + n);
        pool.execute(new Runnable()
        {
          @Override
          public void run()
          {
            msg("started " + n + " (wc=" + pool.getPoolSize() + ")");
            ConcurrencyUtil.sleep(1000);
            latch.countDown();
          }
        });
      }

      latch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      msg("FINISHED with largest pool size = " + pool.getLargestPoolSize());
    }
    finally
    {
      pool.shutdownNow();
    }
  }

  public void testWithKeepAlive() throws Exception
  {
    runTest(1000);
  }

  public void testWithoutKeepAlive() throws Exception
  {
    runTest(0);
  }

  private void runTest(long keepAliveTime)
  {
    @SuppressWarnings("all") // resource
    TaskManager taskManager = new TaskManager(10, 20, keepAliveTime);

    int max = taskManager.getMaximumPoolSize();
    int extra = 10;
    int count = max + 10;

    for (int run = 0; run < 10; run++)
    {
      System.out.println("RUN " + (run + 1));

      Task[] tasks = taskManager.createTasks(count);
      assertEquals(count, taskManager.getCreatedTasks());
      assertEquals(0, taskManager.getScheduledTasks());
      assertEquals(0, taskManager.getCurrentlyEnqueuedTasks());
      assertEquals(0, taskManager.getFinishedTasks());

      taskManager.schedule(tasks, 0, count);
      assertEquals(count, taskManager.getScheduledTasks());
      sleep(20);
      assertEquals(max, taskManager.getStartedTasks());
      assertEquals(0, taskManager.getFinishedTasks());
      assertEquals(extra, taskManager.getCurrentlyEnqueuedTasks());

      for (int i = 1; i <= extra; i++)
      {
        tasks[i - 1].finish();

        sleep(10);
        assertEquals(max + i, taskManager.getStartedTasks());
        assertEquals(i, taskManager.getFinishedTasks());
        assertEquals(extra - i, taskManager.getCurrentlyEnqueuedTasks());
      }

      assertEquals(count, taskManager.getStartedTasks());
      assertEquals(extra, taskManager.getFinishedTasks());
      assertEquals(0, taskManager.getCurrentlyEnqueuedTasks());

      for (int i = extra; i < count; i++)
      {
        tasks[i].finish();
      }

      sleep(20);
      assertEquals(count, taskManager.getStartedTasks());
      assertEquals(count, taskManager.getFinishedTasks());
      assertEquals(0, taskManager.getCurrentlyEnqueuedTasks());

      taskManager.resetStatistics();
    }
  }

  // public static void assertEquals(int expected, int actual)
  // {
  // try
  // {
  // assertEquals(expected, actual);
  // }
  // catch (RuntimeException ex)
  // {
  // ex.printStackTrace();
  // }
  // }

  private static void awaitUninterruptibly(CountDownLatch latch)
  {
    boolean interrupted = false;

    while (true)
    {
      try
      {
        latch.await();
        break;
      }
      catch (InterruptedException ex)
      {
        interrupted = true;
      }
    }

    if (interrupted)
    {
      Thread.currentThread().interrupt();
    }
  }

  private static Runnable createBlockingTask(CountDownLatch firstTaskStarted, CountDownLatch tasksStarted, CountDownLatch releaseTasks)
  {
    return () -> {
      firstTaskStarted.countDown();
      tasksStarted.countDown();
      awaitUninterruptibly(releaseTasks);
    };
  }

  private static Runnable createBlockingTask(CountDownLatch tasksStarted, CountDownLatch releaseTasks)
  {
    return () -> {
      tasksStarted.countDown();
      awaitUninterruptibly(releaseTasks);
    };
  }

  // public static void assertEquals(int expected, int actual)
  // {
  // try
  // {
  // assertEquals(expected, actual);
  // }
  // catch (RuntimeException ex)
  // {
  // ex.printStackTrace();
  // }
  // }

  /**
   * @author Eike Stepper
   */
  public static class TaskManager extends ThreadPool
  {
    private final AtomicInteger createdTasks = new AtomicInteger();

    private final AtomicInteger scheduledTasks = new AtomicInteger();

    private final AtomicInteger startedTasks = new AtomicInteger();

    private final AtomicInteger finishedTasks = new AtomicInteger();

    public TaskManager(int corePoolSize, int maximumPoolSize, long keepAliveTime)
    {
      super(corePoolSize, maximumPoolSize, keepAliveTime, createThreadFactory());
    }

    public int getCreatedTasks()
    {
      return createdTasks.get();
    }

    public int getScheduledTasks()
    {
      return scheduledTasks.get();
    }

    public int getStartedTasks()
    {
      return startedTasks.get();
    }

    public int getFinishedTasks()
    {
      return finishedTasks.get();
    }

    public int getCurrentlyEnqueuedTasks()
    {
      return getQueue().size();
    }

    public int getInactiveWorkers()
    {
      return getPoolSize() - getActiveCount();
    }

    public void resetStatistics()
    {
      createdTasks.set(0);
      scheduledTasks.set(0);
      startedTasks.set(0);
      finishedTasks.set(0);
    }

    public Task[] createTasks(int count)
    {
      Task[] result = new Task[count];
      for (int i = 0; i < result.length; i++)
      {
        result[i] = new Task(this, i + 1);
      }

      return result;
    }

    public void schedule(Task[] tasks, int start, int end)
    {
      for (int i = start; i < end; i++)
      {
        Task task = tasks[i];
        execute(task);
      }
    }

    @Override
    public void execute(Runnable command)
    {
      scheduledTasks.incrementAndGet();
      super.execute(command);
    }

    private static ThreadFactory createThreadFactory()
    {
      final ThreadFactory factory = Executors.defaultThreadFactory();

      return new ThreadFactory()
      {
        @Override
        public Thread newThread(Runnable task)
        {
          System.out.println("Creating new worker");
          return factory.newThread(task);
        }
      };
    }

    /**
     * @author Eike Stepper
     */
    public static class Task extends CountDownLatch implements Runnable
    {
      private final TaskManager manager;

      private final int id;

      private AtomicBoolean used = new AtomicBoolean();

      public Task(TaskManager manager, int id)
      {
        super(1);
        this.manager = manager;
        this.id = id;

        manager.createdTasks.incrementAndGet();
      }

      public final void finish()
      {
        countDown();
      }

      @Override
      public final void run()
      {
        if (!used.compareAndSet(false, true))
        {
          throw new IllegalStateException(this + " has already been used");
        }

        manager.startedTasks.incrementAndGet();
        System.out.println("Running " + this);

        try
        {
          await();
        }
        catch (Throwable t)
        {
          t.printStackTrace();
        }

        System.out.println("Finished " + this);
        manager.finishedTasks.incrementAndGet();
      }

      @Override
      public String toString()
      {
        return "Task " + id;
      }
    }
  }

  /**
   * Pauses the third submission of one task identity after the worker has picked it up.
   *
   * @author Eike Stepper
   */
  private static final class PickupGateThreadPool extends ThreadPool
  {
    private final CountDownLatch thirdBeforeExecute = new CountDownLatch(1);

    private final CountDownLatch releaseThirdBeforeExecute = new CountDownLatch(1);

    private final CountDownLatch fourthBeforeExecute = new CountDownLatch(1);

    private final CountDownLatch releaseFourthBeforeExecute = new CountDownLatch(1);

    private final AtomicInteger trackedExecutions = new AtomicInteger();

    private volatile Runnable trackedTask;

    public PickupGateThreadPool()
    {
      super(1, 2, 60, Executors.defaultThreadFactory());
    }

    @Override
    protected void beforeExecute(Thread worker, Runnable task)
    {
      int execution = task == trackedTask ? trackedExecutions.incrementAndGet() : 0;
      if (execution == 3)
      {
        thirdBeforeExecute.countDown();
        awaitUninterruptibly(releaseThirdBeforeExecute);
      }
      else if (execution == 4)
      {
        fourthBeforeExecute.countDown();
        awaitUninterruptibly(releaseFourthBeforeExecute);
      }

      super.beforeExecute(worker, task);
    }

    public void setTrackedTask(Runnable trackedTask)
    {
      this.trackedTask = trackedTask;
    }

    public boolean awaitThirdBeforeExecute() throws InterruptedException
    {
      return thirdBeforeExecute.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void releaseThirdBeforeExecute()
    {
      releaseThirdBeforeExecute.countDown();
    }

    public boolean awaitFourthBeforeExecute() throws InterruptedException
    {
      return fourthBeforeExecute.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void releaseFourthBeforeExecute()
    {
      releaseFourthBeforeExecute.countDown();
    }
  }

  /**
   * A Runnable whose equality is based on a value independent of its identity.
   *
   * @author Eike Stepper
   */
  private static final class EqualRunnable implements Runnable
  {
    private final int value;

    public EqualRunnable(int value)
    {
      this.value = value;
    }

    @Override
    public void run()
    {
    }

    @Override
    public int hashCode()
    {
      return value;
    }

    @Override
    public boolean equals(Object object)
    {
      return object instanceof EqualRunnable && ((EqualRunnable)object).value == value;
    }
  }

  /**
   * Pauses completion of one execution while another submission of the same Runnable remains queued.
   *
   * @author Eike Stepper
   */
  private static final class DuplicateLifecycleThreadPool extends ThreadPool
  {
    private final CountDownLatch secondAfterExecute = new CountDownLatch(1);

    private final CountDownLatch releaseSecondAfterExecute = new CountDownLatch(1);

    private final AtomicInteger trackedCompletions = new AtomicInteger();

    private volatile Runnable trackedTask;

    public DuplicateLifecycleThreadPool()
    {
      super(1, 2, 60, Executors.defaultThreadFactory());
    }

    @Override
    protected void afterExecute(Runnable task, Throwable ex)
    {
      super.afterExecute(task, ex);
      if (task == trackedTask && trackedCompletions.incrementAndGet() == 1)
      {
        secondAfterExecute.countDown();
        awaitUninterruptibly(releaseSecondAfterExecute);
      }
    }

    public void setTrackedTask(Runnable trackedTask)
    {
      this.trackedTask = trackedTask;
    }

    public boolean awaitSecondAfterExecute() throws InterruptedException
    {
      return secondAfterExecute.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void releaseSecondAfterExecute()
    {
      releaseSecondAfterExecute.countDown();
    }
  }

  /**
   * A thread pool that pauses the first task before its admission reservation is released.
   *
   * @author Eike Stepper
   */
  private static final class RaceThreadPool extends ThreadPool
  {
    private final CountDownLatch beforeExecuteEntered = new CountDownLatch(1);

    private final CountDownLatch releaseBeforeExecute = new CountDownLatch(1);

    private volatile Runnable gatedTask;

    public RaceThreadPool()
    {
      super(1, 2, 60, Executors.defaultThreadFactory());
    }

    @Override
    protected void beforeExecute(Thread worker, Runnable task)
    {
      if (task == gatedTask)
      {
        beforeExecuteEntered.countDown();
        awaitUninterruptibly(releaseBeforeExecute);
      }

      super.beforeExecute(worker, task);
    }

    public void setGatedTask(Runnable gatedTask)
    {
      this.gatedTask = gatedTask;
    }

    public boolean awaitBeforeExecute() throws InterruptedException
    {
      return beforeExecuteEntered.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void releaseBeforeExecute()
    {
      releaseBeforeExecute.countDown();
    }
  }

  /**
   * Pauses the rejected-execution handler after its shutdown check.
   *
   * @author Eike Stepper
   */
  private static final class ShutdownRaceThreadPool extends ThreadPool
  {
    private final CountDownLatch shutdownCheckEntered = new CountDownLatch(1);

    private final CountDownLatch releaseShutdownCheck = new CountDownLatch(1);

    private final AtomicBoolean gateShutdownCheck = new AtomicBoolean();

    public ShutdownRaceThreadPool()
    {
      super(1, 2, 60, task -> null);
    }

    @Override
    public boolean isShutdown()
    {
      boolean isShutdown = super.isShutdown();
      if (gateShutdownCheck.compareAndSet(true, false))
      {
        shutdownCheckEntered.countDown();
        awaitUninterruptibly(releaseShutdownCheck);
      }

      return isShutdown;
    }

    public void gateNextShutdownCheck()
    {
      gateShutdownCheck.set(true);
    }

    public boolean awaitShutdownCheck() throws InterruptedException
    {
      return shutdownCheckEntered.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void releaseShutdownCheck()
    {
      releaseShutdownCheck.countDown();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class MutableHashRunnable implements Runnable
  {
    private final CountDownLatch started;

    private volatile int hashCode = 1;

    public MutableHashRunnable()
    {
      this(null);
    }

    public MutableHashRunnable(CountDownLatch started)
    {
      this.started = started;
    }

    public void changeHashCode()
    {
      hashCode = 2;
    }

    @Override
    public int hashCode()
    {
      return hashCode;
    }

    @Override
    public void run()
    {
      if (started != null)
      {
        started.countDown();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AfterExecuteGateThreadPool extends ThreadPool
  {
    private final CountDownLatch afterExecuteEntered = new CountDownLatch(1);

    private final CountDownLatch releaseAfterExecute = new CountDownLatch(1);

    private volatile Runnable gatedTask;

    public AfterExecuteGateThreadPool()
    {
      super(1, 2, 60, Executors.defaultThreadFactory());
    }

    @Override
    protected void afterExecute(Runnable task, Throwable ex)
    {
      super.afterExecute(task, ex);
      if (task == gatedTask)
      {
        afterExecuteEntered.countDown();
        awaitUninterruptibly(releaseAfterExecute);
      }
    }

    public void setGatedTask(Runnable gatedTask)
    {
      this.gatedTask = gatedTask;
    }

    public boolean awaitAfterExecute() throws InterruptedException
    {
      return afterExecuteEntered.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void releaseAfterExecute()
    {
      releaseAfterExecute.countDown();
    }
  }
}
