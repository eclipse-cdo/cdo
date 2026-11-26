/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.monitor.TimeoutMonitor;

import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class MonitorTest extends AbstractOMTest
{
  public void testBeginNotCalledOnMonitor() throws Exception
  {
    try
    {
      OMMonitor monitor = new Monitor();
      monitor.worked();
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      // Success
    }

    try
    {
      OMMonitor monitor = new Monitor();
      monitor.fork();
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      // Success
    }

    Async async = null;
    try
    {
      OMMonitor monitor = new Monitor();
      async = monitor.forkAsync();
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      // Success
    }
    finally
    {
      if (async != null)
      {
        async.stop();
      }
    }
  }

  public void testBeginNotCalledOnNestedMonitor() throws Exception
  {
    try
    {
      OMMonitor monitor = new Monitor().begin().fork();
      monitor.worked();
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      // Success
    }

    try
    {
      OMMonitor monitor = new Monitor().begin().fork();
      monitor.fork();
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      // Success
    }

    Async async = null;
    try
    {
      OMMonitor monitor = new Monitor().begin().fork();
      async = monitor.forkAsync();
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      // Success
    }
    finally
    {
      if (async != null)
      {
        async.stop();
      }
    }
  }

  public void testBeginCalledOnMonitor() throws Exception
  {
    {
      OMMonitor monitor = new Monitor().begin();
      monitor.worked();
    }

    {
      OMMonitor monitor = new Monitor().begin();
      monitor.fork();
    }

    Async async = null;
    try
    {
      OMMonitor monitor = new Monitor().begin();
      async = monitor.forkAsync();
    }
    finally
    {
      if (async != null)
      {
        async.stop();
      }
    }
  }

  public void testBeginCalledOnNestedMonitor() throws Exception
  {
    {
      OMMonitor monitor = new Monitor().begin().fork().begin();
      monitor.worked();
    }

    {
      OMMonitor monitor = new Monitor().begin().fork().begin();
      monitor.fork();
    }

    Async async = null;
    try
    {
      OMMonitor monitor = new Monitor().begin().fork().begin();
      async = monitor.forkAsync();
    }
    finally
    {
      if (async != null)
      {
        async.stop();
      }
    }
  }

  public void testProgress() throws Exception
  {
    // Worked completely
    {
      OMMonitor monitor = new Monitor();
      assertNotSame(OMMonitor.ZERO, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      monitor.begin(OMMonitor.TEN);
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      for (int i = 0; i < 10; i++)
      {
        monitor.worked();
        assertEquals(OMMonitor.TEN, monitor.getTotalWork());
        assertEquals((double)i + 1, monitor.getWork());
      }

      monitor.done();
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(monitor.getTotalWork(), monitor.getWork());
    }

    // Worked incompletely
    {
      OMMonitor monitor = new Monitor();
      assertNotSame(OMMonitor.ZERO, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      monitor.begin(OMMonitor.TEN);
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      for (int i = 0; i < 5; i++)
      {
        monitor.worked();
        assertEquals(OMMonitor.TEN, monitor.getTotalWork());
        assertEquals((double)i + 1, monitor.getWork());
      }

      monitor.done();
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(monitor.getTotalWork(), monitor.getWork());
    }
  }

  public void testNestedProgress() throws Exception
  {
    // Worked completely
    {
      OMMonitor main = new Monitor().begin();
      assertEquals(OMMonitor.ONE, main.getTotalWork());

      OMMonitor monitor = main.fork();
      assertNotSame(OMMonitor.ZERO, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      monitor.begin(OMMonitor.TEN);
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      for (int i = 0; i < 10; i++)
      {
        monitor.worked();
        assertEquals(OMMonitor.TEN, monitor.getTotalWork());
        assertEquals((double)i + 1, monitor.getWork());
        assertSimilar(OMMonitor.ONE / OMMonitor.TEN * (i + 1), main.getWork(), 1000);
      }

      monitor.done();
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(monitor.getTotalWork(), monitor.getWork());
      assertSimilar(OMMonitor.ONE, main.getWork(), 1000);
    }

    // Worked incompletely
    {
      OMMonitor main = new Monitor().begin();
      assertEquals(OMMonitor.ONE, main.getTotalWork());

      OMMonitor monitor = main.fork();
      assertNotSame(OMMonitor.ZERO, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      monitor.begin(OMMonitor.TEN);
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(OMMonitor.ZERO, monitor.getWork());

      for (int i = 0; i < 5; i++)
      {
        monitor.worked();
        assertEquals(OMMonitor.TEN, monitor.getTotalWork());
        assertEquals((double)i + 1, monitor.getWork());
        assertSimilar(OMMonitor.ONE / OMMonitor.TEN * (i + 1), main.getWork(), 1000);
      }

      monitor.done();
      assertEquals(OMMonitor.TEN, monitor.getTotalWork());
      assertEquals(monitor.getTotalWork(), monitor.getWork());
      assertSimilar(OMMonitor.ONE, main.getWork(), 1000);
    }
  }

  public void testAsyncProgress() throws Exception
  {
    final long PERIOD = 50;
    OMMonitor main = new Monitor()
    {
      @Override
      protected long getAsyncSchedulePeriod()
      {
        return PERIOD;
      }
    }.begin(3);

    main.worked();
    double work = main.getWork();
    assertEquals(OMMonitor.ONE, work);

    Async async = main.forkAsync();
    for (int i = 0; i < 20; i++)
    {
      sleep(2 * PERIOD);
      double newWork = main.getWork();
      System.out.println(newWork);

      // assertEquals(true, "Worked not enough: " + work, newWork > work);
      // assertEquals(true, "Worked too much: " + newWork, newWork < OMMonitor.ONE + OMMonitor.ONE);
      work = newWork;
    }

    async.stop();
    assertSimilar(OMMonitor.ONE + OMMonitor.ONE, main.getWork(), 1000);

    main.worked();
    assertSimilar(OMMonitor.ONE + OMMonitor.ONE + OMMonitor.ONE, main.getWork(), 1000);
  }

  public void testSharedTimerSurvivesTaskFailure() throws Exception
  {
    CountDownLatch secondRun = new CountDownLatch(1);
    AtomicInteger runs = new AtomicInteger();
    TimerTask task = Monitor.scheduleAtFixedRate(() -> {
      if (runs.incrementAndGet() == 1)
      {
        throw new IllegalStateException("Expected test failure"); //$NON-NLS-1$
      }

      secondRun.countDown();
    }, 1, 10);

    try
    {
      assertTrue("Shared timer did not continue after a task failure", secondRun.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    }
    finally
    {
      task.cancel();
    }
  }

  public void testPeriodicCancellationInspectionDoesNotRefreshTimeout() throws Exception
  {
    CountDownLatch timedOut = new CountDownLatch(1);
    AtomicReference<TimerTask> reportingTask = new AtomicReference<>();
    TimeoutMonitor monitor = new TimeoutMonitor(150)
    {
      @Override
      public OMMonitor begin(double totalWork)
      {
        OMMonitor result = super.begin(totalWork);
        // Simulates periodic reporting inspection without refreshing the operation timeout.
        reportingTask.set(Monitor.scheduleAtFixedRate(this::isCanceledState, 1, 10));
        return result;
      }

      @Override
      protected void handleTimeout(long untouched)
      {
        timedOut.countDown();
        super.handleTimeout(untouched);
      }
    };

    try
    {
      monitor.begin(1);
      assertTrue("Periodic cancellation inspection prevented the monitor timeout", timedOut.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    }
    finally
    {
      TimerTask task = reportingTask.get();
      if (task != null)
      {
        task.cancel();
      }

      monitor.done();
    }
  }
}
