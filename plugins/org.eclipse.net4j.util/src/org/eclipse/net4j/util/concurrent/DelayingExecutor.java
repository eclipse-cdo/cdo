/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;

import java.util.concurrent.Executor;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public class DelayingExecutor implements Executor, Runnable
{
  private final long delay;

  private Work scheduledWork;

  private boolean running;

  public DelayingExecutor(long delay)
  {
    this.delay = delay;
  }

  @Override
  public void execute(Runnable runnable)
  {
    scheduleWork(new Work(runnable));
  }

  @Override
  public void run()
  {
    running = true;

    try
    {
      Thread thread = Thread.currentThread();
      Work currentWork = null;

      while (running && thread.isAlive() && !thread.isInterrupted())
      {
        Work scheduledWork = unscheduleWork();
        if (scheduledWork != null)
        {
          currentWork = scheduledWork;
        }

        long now = System.currentTimeMillis();
        long sleepMillis = delay;

        if (currentWork != null)
        {
          long passedMillis = now - currentWork.scheduled;
          if (passedMillis >= delay)
          {
            doExecute(currentWork);
            currentWork = null;
          }
          else
          {
            sleepMillis = delay - passedMillis;
          }
        }

        try
        {
          Thread.sleep(sleepMillis);
        }
        catch (InterruptedException ex)
        {
          break;
        }
      }
    }
    finally
    {
      running = false;
    }
  }

  public void stop()
  {
    running = false;
  }

  protected void doExecute(Runnable runnable)
  {
    runnable.run();
  }

  private synchronized void scheduleWork(Work work)
  {
    scheduledWork = work;
    notifyAll();
  }

  private synchronized Work unscheduleWork()
  {
    Work w = scheduledWork;
    scheduledWork = null;
    return w;
  }

  /**
   * @author Eike Stepper
   */
  private static final class Work implements Runnable
  {
    private final long scheduled = System.currentTimeMillis();

    private final Runnable runnable;

    public Work(Runnable runnable)
    {
      this.runnable = runnable;
    }

    @Override
    public void run()
    {
      try
      {
        runnable.run();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}
