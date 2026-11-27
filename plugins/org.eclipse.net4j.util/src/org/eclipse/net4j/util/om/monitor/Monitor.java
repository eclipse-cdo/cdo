/*
 * Copyright (c) 2008-2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.internal.util.bundle.OM;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Monitor extends AbstractMonitor
{
  public static final long DEFAULT_ASYNC_SCHEDULE_PERIOD = 1000;

  private static Timer TIMER;

  private volatile boolean canceled;

  private volatile RuntimeException cancelException;

  public Monitor()
  {
  }

  public void cancel()
  {
    cancel(null);
  }

  public void cancel(RuntimeException cancelException)
  {
    this.cancelException = cancelException;
    canceled = true;
  }

  @Override
  public boolean isCanceled()
  {
    return canceled;
  }

  @Override
  public void checkCanceled() throws MonitorCanceledException
  {
    if (cancelException != null)
    {
      throw new MonitorCanceledException(cancelException);
    }

    if (canceled)
    {
      throw new MonitorCanceledException();
    }
  }

  /**
   * Returns the cancellation flag without applying subclass-specific observation side effects.
   *
   * @return whether this monitor has been canceled
   * @since 3.31
   */
  protected final boolean isCanceledState()
  {
    return canceled;
  }

  @Override
  protected long getAsyncSchedulePeriod()
  {
    return DEFAULT_ASYNC_SCHEDULE_PERIOD;
  }

  @Override
  protected void scheduleAtFixedRate(TimerTask task, long delay, long period)
  {
    try
    {
      getTimer().scheduleAtFixedRate(task, delay, period);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  protected Timer getTimer()
  {
    return getGlobalTimer();
  }

  private static Timer getGlobalTimer()
  {
    synchronized (Monitor.class)
    {
      if (TIMER == null)
      {
        TIMER = new Timer("monitor-timer", true); //$NON-NLS-1$
      }

      return TIMER;
    }
  }

  /**
   * Schedules a periodic task on the shared daemon timer used by monitors. Task failures are logged and do not terminate the timer
   * thread. Scheduling failures are logged and leave the returned task unscheduled. Canceled tasks remain in the timer queue until
   * their scheduled time is reached.
   *
   * @param task
   *          the task to run
   * @param delay
   *          the delay before the first execution in milliseconds
   * @param period
   *          the period between executions in milliseconds
   * @throws NullPointerException if {@code task} is {@code null}
   * @return the scheduled task, which can be canceled to stop further executions
   * @since 3.31
   */
  public static TimerTask scheduleAtFixedRate(Runnable task, long delay, long period)
  {
    Objects.requireNonNull(task, "task");

    TimerTask timerTask = new TimerTask()
    {
      @Override
      public void run()
      {
        try
        {
          task.run();
        }
        catch (Throwable ex)
        {
          OM.LOG.error("Shared monitor timer task failed", ex); //$NON-NLS-1$
        }
      }
    };

    try
    {
      getGlobalTimer().scheduleAtFixedRate(timerTask, delay, period);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return timerTask;
  }
}
