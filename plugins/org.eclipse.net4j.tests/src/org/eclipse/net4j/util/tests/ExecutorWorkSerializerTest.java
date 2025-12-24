/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test for {@link SerializingExecutor}.
 *
 * @author Andre Dietisheim
 */
public class ExecutorWorkSerializerTest extends AbstractOMTest
{
  /** timeout to wait for execution of all work units. */
  private static final int WORK_COMPLETION_TIMEOUT = 10000;

  /** number of work producer threads. */
  private static final int NUM_WORKPRODUCER_THREADS = 10;

  /** number of working units to execute. */
  private static final int NUM_WORK = 40;

  /** the latch to wait on for the execution of all working units. */
  private CountDownLatch workConsumedLatch;

  /** The number of working units created. */
  private AtomicInteger workProduced;

  /** The thread pool to execute the work unit producers in. */
  private ExecutorService threadPool;

  /** The executor to submit the work units to. */
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
   */
  private static interface WorkProducerFactory
  {
    public WorkProducer createWorkProducer();
  }

  /**
   * A Runnable that creates work units
   */
  private abstract class WorkProducer implements Runnable
  {
    private Random random = new Random();

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
  class Work implements Runnable
  {
    private final int id;

    private Work(int id)
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
}
