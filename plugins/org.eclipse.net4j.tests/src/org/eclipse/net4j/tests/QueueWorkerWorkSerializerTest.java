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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andre Dietisheim
 */
public class QueueWorkerWorkSerializerTest extends AbstractOMTest
{
  private static final int WORK_COMPLETION_TIMEOUT = 10000;

  private static final int NUM_WORKPRODUCER_THREADS = 10;

  private static final int NUM_WORK = 40;

  private CountDownLatch workConsumedLatch;

  private AtomicInteger workProduced;

  private ExecutorService threadPool;

  private QueueWorkerWorkSerializer queueWorker;

  public QueueWorkerWorkSerializerTest()
  {
  }

  public void testAllWorkSubmittedIsConsumed() throws Throwable
  {
    createWorkProducerThreads(new AbstractRunnableFactory()
    {
      @Override
      Runnable create()
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
    workConsumedLatch.await(WORK_COMPLETION_TIMEOUT, TimeUnit.MILLISECONDS);
    assertEquals(workProduced.get(), NUM_WORK - workConsumedLatch.getCount());
  }

  // public void testAllWorkSubmittedIsConsumedSeuqentially() throws Throwable
  // {
  // final List<Integer> idList = new ArrayList<Integer>();
  // createWorkProducerThreads(new AbstractRunnableFactory()
  // {
  // @Override
  // Runnable create()
  // {
  // return new WorkProducer()
  // {
  // @Override
  // protected Runnable createWork(final int id)
  // {
  // return new Work(id)
  // {
  // @Override
  // public void run()
  // {
  // idList.add(id);
  // super.run();
  // }
  // };
  // }
  // };
  // }
  // });
  // stopLatch.await();
  // Collections.sort(idList, new Comparator<Integer>()
  // {
  // public int compare(Integer thisInteger, Integer thatInteger)
  // {
  // int comparisonResult = thisInteger.compareTo(thatInteger);
  // assertTrue(comparisonResult == -1);
  // return comparisonResult;
  // }
  // });
  // }
  //
  // public void testGivenExceptionInWorkAllWorkSubmittedIsConsumed() throws Throwable
  // {
  // createWorkProducerThreads(new AbstractRunnableFactory()
  // {
  // @Override
  // Runnable create()
  // {
  // return new WorkProducer()
  // {
  // @Override
  // protected Runnable createWork(int id)
  // {
  // return new Work(id)
  // {
  // protected Random random = new Random();
  //
  // @Override
  // public void run()
  // {
  // super.run();
  // if (random.nextBoolean())
  // {
  // throw new RuntimeException("dummy exception to simulate runtime errors");
  // }
  // }
  // };
  // }
  // };
  // }
  // });
  // stopLatch.await();
  // assertEquals(workProduced.get(), workConsumed.get());
  // }

  private void createWorkProducerThreads(AbstractRunnableFactory factory)
  {
    for (int i = 0; i < NUM_WORKPRODUCER_THREADS; i++)
    {
      threadPool.submit(factory.create());
    }
  }

  private abstract class AbstractRunnableFactory
  {
    abstract Runnable create();
  }

  private abstract class WorkProducer implements Runnable
  {
    private Random random = new Random();

    public void run()
    {
      try
      {
        int currentWorkProduced;
        while ((currentWorkProduced = workProduced.getAndIncrement()) < NUM_WORK)
        {
          queueWorker.addWork(createWork(currentWorkProduced));
          Thread.sleep(random.nextInt(1000));
        }
        IOUtil.OUT().println("work producer " + this + " stopped its production");
      }
      catch (InterruptedException ex)
      {
        return;
      }
    }

    protected abstract Runnable createWork(int id);
  }

  /**
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

    public void run()
    {
      workConsumedLatch.countDown();
      IOUtil.OUT().println("work unit " + id + " consumed");
    }
  }

  @Override
  public void setUp()
  {
    threadPool = Executors.newFixedThreadPool(NUM_WORKPRODUCER_THREADS);
    workConsumedLatch = new CountDownLatch(NUM_WORK);
    queueWorker = new QueueWorkerWorkSerializer();
    workProduced = new AtomicInteger(0);
  }

  @Override
  public void tearDown()
  {
    queueWorker.dispose();
    threadPool.shutdown();
  }
}
