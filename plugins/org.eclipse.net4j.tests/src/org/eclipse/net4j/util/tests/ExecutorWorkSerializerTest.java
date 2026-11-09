/*
 * Copyright (c) 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.concurrent.SerializingExecutor;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A test for {@link SerializingExecutor}.
 *
 * @author Andre Dietisheim
 */
public class ExecutorWorkSerializerTest extends AbstractOMTest
{
  /**
   * Timeout to wait for execution of all work units.
   */
  private static final int WORK_COMPLETION_TIMEOUT = 10000;

  /**
   * Number of work producer threads.
   */
  private static final int NUM_WORKPRODUCER_THREADS = 10;

  /**
   * Number of working units to execute.
   */
  private static final int NUM_WORK = 40;

  /**
   * The latch to wait on for the execution of all working units.
   */
  private CountDownLatch workConsumedLatch;

  /**
   * The number of working units created.
   */
  private AtomicInteger workProduced;

  /**
   * The thread pool to execute the work unit producers in.
   */
  private ExecutorService threadPool;

  /**
   * The executor to submit the work units to.
   */
  private SerializingExecutor serializer;

  @Override
  public void setUp()
  {
    workProduced = new AtomicInteger(0);
    workConsumedLatch = new CountDownLatch(NUM_WORK);

    threadPool = Executors.newFixedThreadPool(NUM_WORKPRODUCER_THREADS);
    serializer = new SerializingExecutor(threadPool);
    serializer.activate();
  }

  @Override
  public void tearDown()
  {
    serializer.deactivate();
    threadPool.shutdown();
  }

  /**
   * Test that asserts that all submitted workers are executed
   */
  public void testAllWorkSubmittedIsConsumed() throws Throwable
  {
    createWorkProducerThreads(new WorkProducerFactory()
    {
      @Override
      public WorkProducer createWorkProducer()
      {
        return new WorkProducer()
        {
          @Override
          protected Runnable createWork(int id)
          {
            return new Work(id);
          }
        };
      }
    });

    waitForAllWorkExecuted();
    assertEquals(workProduced.get(), NUM_WORK - workConsumedLatch.getCount());
  }

  /**
   * If the workers throw Exceptions, the QueueWorker stops executing work (deactivates its working thread). Therefore
   * the first work unit gets consumed, the rest is not executed any more.
   */
  public void testGivenWorkExceptionInWorkAllWorkSubmittedOnlyTheFirstWorkerIsConsumed() throws Throwable
  {
    createWorkProducerThreads(new WorkProducerFactory()
    {
      @Override
      public WorkProducer createWorkProducer()
      {
        return new WorkProducer()
        {
          @Override
          protected Runnable createWork(int id)
          {
            return new Work(id)
            {
              @Override
              public void run()
              {
                super.run();
                throw new RuntimeException("dummy exception to simulate an error in executed workers");
              }
            };
          }
        };
      }
    });

    waitForAllWorkExecuted();
    assertEquals(NUM_WORK, workProduced.get());
  }

  public void testSubmitOverloadsPreserveFIFOAndCaptureFailures() throws Exception
  {
    ManualExecutor delegate = new ManualExecutor();
    SerializingExecutor executor = new SerializingExecutor(delegate);
    executor.activate();

    try
    {
      List<Integer> order = new ArrayList<>();
      AtomicInteger active = new AtomicInteger();
      AtomicInteger maximumActive = new AtomicInteger();
      Future<?> runnableFuture = executor.submit(() -> runSerialTask(1, order, active, maximumActive));
      Future<String> resultFuture = executor.submit(() -> runSerialTask(2, order, active, maximumActive), "result"); //$NON-NLS-1$
      Future<Integer> callableFuture = executor.submit(() -> {
        runSerialTask(3, order, active, maximumActive);
        return 42;
      });

      Future<?> failedFuture = executor.submit(() -> {
        throw new IllegalArgumentException("expected task failure"); //$NON-NLS-1$
      });

      assertEquals("Only one serial worker should be submitted", 1, delegate.size()); //$NON-NLS-1$
      delegate.runNext();
      runnableFuture.get(1, TimeUnit.SECONDS);
      assertEquals("result", resultFuture.get(1, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals(Integer.valueOf(42), callableFuture.get(1, TimeUnit.SECONDS));
      assertEquals(Arrays.asList(1, 2, 3), order);
      assertEquals(1, maximumActive.get());
      try
      {
        failedFuture.get(1, TimeUnit.SECONDS);
        fail("A failed task must complete exceptionally"); //$NON-NLS-1$
      }
      catch (ExecutionException expected)
      {
        assertTrue(expected.getCause() instanceof IllegalArgumentException);
      }
    }
    finally
    {
      executor.deactivate();
    }
  }

  public void testExecuteRejectsNullImmediately()
  {
    SerializingExecutor executor = new SerializingExecutor(new ManualExecutor());

    try
    {
      executor.execute(null);
      fail("Executing a null task must fail immediately"); //$NON-NLS-1$
    }
    catch (NullPointerException expected)
    {
      // The null task is rejected before it can be queued.
    }
  }

  public void testTasksSubmittedWhileWorkerIsRunningDoNotOverlap() throws Exception
  {
    ManualExecutor delegate = new ManualExecutor();
    SerializingExecutor executor = new SerializingExecutor(delegate);
    executor.activate();
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch releaseFirst = new CountDownLatch(1);
    CountDownLatch secondFinished = new CountDownLatch(1);
    AtomicInteger active = new AtomicInteger();
    AtomicInteger maximumActive = new AtomicInteger();
    Future<?> first = executor.submit(() -> {
      int concurrent = active.incrementAndGet();
      maximumActive.accumulateAndGet(concurrent, Math::max);
      firstStarted.countDown();
      try
      {
        if (!releaseFirst.await(1, TimeUnit.SECONDS))
        {
          throw new AssertionError("First task was not released"); //$NON-NLS-1$
        }
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new AssertionError(ex);
      }
      finally
      {
        active.decrementAndGet();
      }
    });

    Thread worker = new Thread(delegate::runNext, "SerializingExecutor worker test"); //$NON-NLS-1$
    worker.setDaemon(true);
    worker.start();

    try
    {
      assertTrue("First task did not start", firstStarted.await(1, TimeUnit.SECONDS)); //$NON-NLS-1$
      Future<?> second = executor.submit(() -> {
        int concurrent = active.incrementAndGet();
        maximumActive.accumulateAndGet(concurrent, Math::max);
        active.decrementAndGet();
        secondFinished.countDown();
      });
      assertEquals("A second delegate worker must not be scheduled concurrently", 0, delegate.size()); //$NON-NLS-1$
      releaseFirst.countDown();
      worker.join(1000);
      assertFalse("Serial worker did not finish", worker.isAlive()); //$NON-NLS-1$
      first.get(1, TimeUnit.SECONDS);
      assertTrue("Second task did not finish", secondFinished.await(1, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue(second.isDone());
      assertEquals(1, maximumActive.get());
    }
    finally
    {
      releaseFirst.countDown();
      executor.deactivate();
      delegate.shutdownNow();
    }
  }

  public void testReactivationWhileWorkerExecutesDoesNotScheduleConcurrentWorker() throws Exception
  {
    ManualExecutor delegate = new ManualExecutor();
    SerializingExecutor executor = new SerializingExecutor(delegate);
    executor.activate();
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch releaseFirst = new CountDownLatch(1);
    CountDownLatch secondFinished = new CountDownLatch(1);
    AtomicInteger active = new AtomicInteger();
    AtomicInteger maximumActive = new AtomicInteger();
    Future<?> first = executor.submit(() -> {
      int concurrent = active.incrementAndGet();
      maximumActive.accumulateAndGet(concurrent, Math::max);
      firstStarted.countDown();
      try
      {
        if (!releaseFirst.await(5, TimeUnit.SECONDS))
        {
          throw new AssertionError("First task was not released"); //$NON-NLS-1$
        }
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new AssertionError(ex);
      }
      finally
      {
        active.decrementAndGet();
      }
    });

    Thread previousWorker = new Thread(delegate::runNext, "SerializingExecutor reactivation worker test"); //$NON-NLS-1$
    previousWorker.setDaemon(true);
    previousWorker.start();

    try
    {
      assertTrue("First task did not start", firstStarted.await(1, TimeUnit.SECONDS)); //$NON-NLS-1$
      executor.deactivate();
      executor.activate();

      Future<?> second = executor.submit(() -> {
        int concurrent = active.incrementAndGet();
        maximumActive.accumulateAndGet(concurrent, Math::max);
        active.decrementAndGet();
        secondFinished.countDown();
      });

      assertEquals("Reactivation must not submit another worker while the previous worker is executing", 0, delegate.size()); //$NON-NLS-1$
      releaseFirst.countDown();
      previousWorker.join(1000);
      assertFalse("Previous worker did not finish", previousWorker.isAlive()); //$NON-NLS-1$
      first.get(1, TimeUnit.SECONDS);
      second.get(1, TimeUnit.SECONDS);
      assertTrue("Second task did not finish", secondFinished.await(1, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals(1, maximumActive.get());
    }
    finally
    {
      releaseFirst.countDown();
      executor.deactivate();
      delegate.shutdownNow();
    }
  }

  public void testCancelledFutureAndDeactivationCancelQueuedWork() throws Exception
  {
    ManualExecutor delegate = new ManualExecutor();
    SerializingExecutor executor = new SerializingExecutor(delegate);
    executor.activate();

    try
    {
      AtomicInteger executions = new AtomicInteger();
      Future<?> cancelled = executor.submit(executions::incrementAndGet);
      Future<Integer> pending = executor.submit(() -> {
        executions.incrementAndGet();
        return 7;
      });
      assertTrue(cancelled.cancel(false));
      executor.deactivate();
      assertTrue(pending.isCancelled());
      delegate.runNext();
      assertEquals(0, executions.get());

      executor.activate();
      Future<Integer> recovered = executor.submit(() -> 9);
      delegate.runNext();
      assertEquals(Integer.valueOf(9), recovered.get(1, TimeUnit.SECONDS));
    }
    finally
    {
      executor.deactivate();
    }
  }

  public void testSchedulingRejectionCancelsAcceptedFuturesAndRecovers() throws Exception
  {
    ManualExecutor delegate = new ManualExecutor();
    SerializingExecutor executor = new SerializingExecutor(delegate);
    Future<Integer> first = executor.submit(() -> 1);
    Future<Integer> second = executor.submit(() -> 2);
    delegate.rejectNext = true;
    executor.activate();

    try
    {
      assertTrue(first.isCancelled());
      assertTrue(second.isCancelled());

      delegate.rejectNext = true;
      try
      {
        executor.submit(() -> 3);
        fail("A new submission must report delegate rejection"); //$NON-NLS-1$
      }
      catch (RejectedExecutionException expected)
      {
        // The failed submission is reported to its caller.
      }

      Future<Integer> recovered = executor.submit(() -> 4);
      delegate.runNext();
      assertEquals(Integer.valueOf(4), recovered.get(1, TimeUnit.SECONDS));
    }
    finally
    {
      executor.deactivate();
    }
  }

  public void testConcurrentSubmitAndDeactivationTerminatesFuture() throws Exception
  {
    BlockingExecutor delegate = new BlockingExecutor();
    SerializingExecutor executor = new SerializingExecutor(delegate);
    executor.activate();
    AtomicReference<Future<Integer>> future = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread submitter = new Thread(() -> {
      try
      {
        future.set(executor.submit(() -> 1));
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "SerializingExecutor submit/deactivate test"); //$NON-NLS-1$
    submitter.setDaemon(true);
    submitter.start();

    try
    {
      assertTrue("Delegate submission did not reach its gate", delegate.entered.await(1, TimeUnit.SECONDS)); //$NON-NLS-1$
      executor.deactivate();
      delegate.release.countDown();
      submitter.join(1000);
      assertFalse("Submitter did not finish", submitter.isAlive()); //$NON-NLS-1$
      if (failure.get() != null)
      {
        throw new AssertionError(failure.get());
      }

      assertNotNull(future.get());
      assertTrue("Accepted future was not cancelled by deactivation", future.get().isCancelled()); //$NON-NLS-1$
      delegate.runNext();
    }
    finally
    {
      delegate.release.countDown();
      executor.deactivate();
      delegate.shutdownNow();
    }
  }

  private void runSerialTask(int value, List<Integer> order, AtomicInteger active, AtomicInteger maximumActive)
  {
    int concurrent = active.incrementAndGet();
    maximumActive.accumulateAndGet(concurrent, Math::max);
    order.add(value);
    active.decrementAndGet();
  }

  private void waitForAllWorkExecuted() throws InterruptedException
  {
    if (!workConsumedLatch.await(WORK_COMPLETION_TIMEOUT, TimeUnit.MILLISECONDS))
    {
      IOUtil.OUT().println("timeout occured before all workers were executed");
    }
  }

  private void createWorkProducerThreads(WorkProducerFactory factory)
  {
    for (int i = 0; i < NUM_WORKPRODUCER_THREADS; i++)
    {
      threadPool.submit(factory.createWorkProducer());
    }
  }

  /**
   * A factory that creates work units.
   *
   * @author Eike Stepper
   */
  private static interface WorkProducerFactory
  {
    public WorkProducer createWorkProducer();
  }

  /**
   * A Runnable that creates work units
   * @author Eike Stepper
   */
  private abstract class WorkProducer implements Runnable
  {
    private Random random = new Random();

    public WorkProducer()
    {
    }

    /**
     * Produce work: add work units to the queue worker
     */
    @Override
    public void run()
    {
      try
      {
        int currentWorkProduced;

        while ((currentWorkProduced = workProduced.getAndIncrement()) < NUM_WORK)
        {
          serializer.execute(createWork(currentWorkProduced));
          Thread.sleep(random.nextInt(1000));
        }

        // correct last increment
        workProduced.decrementAndGet();
        IOUtil.OUT().println("work producer " + this + " stopped its production");
      }
      catch (InterruptedException ex)
      {
        return;
      }
    }

    /**
     * Creates a working unit (runnable).
     *
     * @param id
     *          the id
     * @return the runnable
     */
    protected abstract Runnable createWork(int id);
  }

  /**
   * A simple work unit to be executed in the queueWorker.
   *
   * @author Andre Dietisheim
   */
  private class Work implements Runnable
  {
    private final int id;

    public Work(int id)
    {
      this.id = id;
      IOUtil.OUT().println("work unit " + id + " created");
    }

    @Override
    public void run()
    {
      workConsumedLatch.countDown();
      IOUtil.OUT().println("work unit " + id + " consumed");
    }
  }

  /**
   * Holds delegate work until a test explicitly runs it.
   *
   * @author Eike Stepper
   */
  private static class ManualExecutor extends AbstractExecutorService
  {
    private final Queue<Runnable> tasks = new ArrayDeque<>();

    private boolean shutdown;

    private boolean rejectNext;

    public ManualExecutor()
    {
    }

    @Override
    public synchronized void execute(Runnable command)
    {
      if (rejectNext)
      {
        rejectNext = false;
        throw new RejectedExecutionException("controlled rejection"); //$NON-NLS-1$
      }

      if (shutdown)
      {
        throw new RejectedExecutionException("executor is shut down"); //$NON-NLS-1$
      }

      tasks.add(command);
    }

    public synchronized int size()
    {
      return tasks.size();
    }

    public void runNext()
    {
      Runnable task;

      synchronized (this)
      {
        task = tasks.poll();
      }

      if (task == null)
      {
        throw new AssertionError("No delegate work is queued"); //$NON-NLS-1$
      }

      task.run();
    }

    @Override
    public synchronized void shutdown()
    {
      shutdown = true;
    }

    @Override
    public synchronized List<Runnable> shutdownNow()
    {
      shutdown = true;
      List<Runnable> pending = new ArrayList<>(tasks);
      tasks.clear();
      return pending;
    }

    @Override
    public synchronized boolean isShutdown()
    {
      return shutdown;
    }

    @Override
    public synchronized boolean isTerminated()
    {
      return shutdown && tasks.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
    {
      return isTerminated();
    }
  }

  /**
   * Blocks one delegate submission to force the submit/deactivate race.
   *
   * @author Eike Stepper
   */
  private static final class BlockingExecutor extends ManualExecutor
  {
    private final CountDownLatch entered = new CountDownLatch(1);

    private final CountDownLatch release = new CountDownLatch(1);

    public BlockingExecutor()
    {
    }

    @Override
    public synchronized void execute(Runnable command)
    {
      entered.countDown();

      try
      {
        if (!release.await(1, TimeUnit.SECONDS))
        {
          throw new RejectedExecutionException("controlled submission timed out"); //$NON-NLS-1$
        }
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new RejectedExecutionException(ex);
      }

      super.execute(command);
    }
  }
}
