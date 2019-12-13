/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.tests.ThreadPoolTest.TaskManager.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
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
  // Assert.assertEquals(expected, actual);
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
}
