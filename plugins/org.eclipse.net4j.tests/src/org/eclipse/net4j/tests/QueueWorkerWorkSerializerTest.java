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
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andre Dietisheim
 */
public class QueueWorkerWorkSerializerTest extends AbstractOMTest
{
  private static final int NUM_WORKPRODUCER_THREADS = 4;

  private static final int NUM_WORK = 10;

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
    assertEquals(workProduced.get(), workConsumed.get());
  }

  private void createWorkProducerThreads()
  {
    for (int i = 0; i < NUM_WORKPRODUCER_THREADS; i++)
    {
      threadPool.submit(new WorkProducer());
    }
  }

  private final class WorkProducer implements Runnable
  {
    private Random random = new Random();

    public void run()
    {
      try
      {
        int workToCreate;
        do
        {
          Thread.sleep(random.nextInt(1000));
          workToCreate = (int)stopLatch.getCount();
          queueWorker.addWork(new Work(NUM_WORK - workToCreate));
        } while (workToCreate > 0);
        IOUtil.OUT().println("work producer " + this + " stopped its production");
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
      IOUtil.OUT().println("work unit " + identifier + " created");
    }

    public void run()
    {
      try
      {
        stopLatch.countDown();
        IOUtil.OUT().println("work unit " + workUnit + " consumed");
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
    stopLatch = new CountDownLatch(NUM_WORK);
    queueWorker = new QueueWorkerWorkSerializer();
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
