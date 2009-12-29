/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.util.concurrent.QueueWorkerWorkSerializer;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andre Dietisheim
 */
public class QueueWorkerWorkSerializerTest extends AbstractProtocolTest
{
  private static final int NUM_WORKPRODUCER_THREADS = 4;

  private static final int NUM_WORK = 200;

  private CountDownLatch stopLatch;

  private AtomicInteger workProduced;

  private AtomicInteger workConsumed;

  private ExecutorService threadPool;

  private QueueWorkerWorkSerializer queueWorker;

  public QueueWorkerWorkSerializerTest()
  {
  }

  public void testAllWorkSubmittedIsConsumed() throws Throwable
  {
    createWorkProducerThreads();
    stopLatch.await();
    assertEquals(workProduced, workConsumed);
  }

  private void createWorkProducerThreads()
  {
    for (int i = 0; i < NUM_WORKPRODUCER_THREADS; i++)
    {
      threadPool.submit(new WorkProducer());
    }
  }

  /**
   * @author Andre Dietisheim
   */
  private final class WorkProducer implements Runnable
  {
    Random random = new Random();

    public void run()
    {
      try
      {
        while (workProduced.getAndIncrement() <= NUM_WORK)
        {
          int workCreated = workProduced.incrementAndGet();
          queueWorker.addWork(new Work(workCreated));
          IOUtil.OUT().println("work unit " + workCreated + " produced");
          Thread.sleep(random.nextInt(1000));
        }
        IOUtil.OUT().println("stopping work production");
        stopLatch.countDown();
      }
      catch (InterruptedException ex)
      {
        return;
      }
    }
  }

  /**
   * @author Andre Dietisheim
   */
  private class Work implements Runnable
  {
    private final int workUnit;

    private Work(int identifier)
    {
      workUnit = identifier;
    }

    public void run()
    {
      try
      {
        IOUtil.OUT().println("work unit " + workUnit + " consumed");
        IOUtil.OUT().println("work consumption counter set to " + workConsumed.incrementAndGet());
      }
      catch (Exception ex)
      {
        ex.printStackTrace(IOUtil.OUT());
      }
    }
  }

  @Override
  public void setUp()
  {
    threadPool = Executors.newFixedThreadPool(NUM_WORKPRODUCER_THREADS);
    queueWorker = new QueueWorkerWorkSerializer();
    stopLatch = new CountDownLatch(1);
    workProduced = new AtomicInteger(0);
    workConsumed = new AtomicInteger(0);
  }

  @Override
  public void tearDown()
  {
    queueWorker.dispose();
    threadPool.shutdown();
  }
}
