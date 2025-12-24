/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.concurrent.TrackableTimerTask;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractMonitor implements OMMonitor
{
  private static final boolean CHECK_BEGIN = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.util.om.monitor.CheckBegin");

  private static final long NOT_BEGUN = -1;

  private double totalWork = NOT_BEGUN;

  private double work;

  public AbstractMonitor()
  {
  }

  @Override
  public boolean hasBegun() throws MonitorCanceledException
  {
    checkCanceled();
    return totalWork != NOT_BEGUN;
  }

  @Override
  public OMMonitor begin(double totalWork) throws MonitorCanceledException
  {
    checkCanceled();
    this.totalWork = totalWork;
    return this;
  }

  @Override
  public OMMonitor begin() throws MonitorCanceledException
  {
    return begin(ONE);
  }

  @Override
  public void worked(double work) throws MonitorCanceledException
  {
    checkBegun();
    this.work += work;
  }

  @Override
  public void worked() throws MonitorCanceledException
  {
    worked(ONE);
  }

  @Override
  public OMMonitor fork(double work)
  {
    checkBegun();
    return createNestedMonitor(work);
  }

  @Override
  public OMMonitor fork()
  {
    return fork(ONE);
  }

  @Override
  public Async forkAsync(double work)
  {
    checkBegun();
    AsyncTimerTask asyncTimerTask = createAsyncTimerTask(work);
    if (asyncTimerTask == null)
    {
      throw new NullPointerException("No async timer task has been created");
    }

    long period = getAsyncSchedulePeriod();
    scheduleAtFixedRate(asyncTimerTask, period, period);
    return asyncTimerTask;
  }

  @Override
  public Async forkAsync()
  {
    return forkAsync(ONE);
  }

  @Override
  public void done()
  {
    if (!isCanceled())
    {
      double rest = totalWork - work;
      if (rest > 0)
      {
        worked(rest);
      }
    }
  }

  @Override
  public double getTotalWork()
  {
    return totalWork;
  }

  @Override
  public double getWork()
  {
    return work;
  }

  @Override
  public double getWorkPercent()
  {
    return percent(work, totalWork);
  }

  protected OMMonitor createNestedMonitor(double work)
  {
    return new NestedMonitor(this, work);
  }

  protected AsyncTimerTask createAsyncTimerTask(double work)
  {
    return new AsyncTimerTask(this, work, DEFAULT_TIME_FACTOR);
  }

  protected abstract long getAsyncSchedulePeriod();

  protected abstract Timer getTimer();

  /**
   * @since 3.0
   */
  protected abstract void scheduleAtFixedRate(TimerTask task, long delay, long period);

  private void checkBegun() throws MonitorCanceledException
  {
    if (CHECK_BEGIN && !hasBegun())
    {
      throw new IllegalStateException("begin() has not been called"); //$NON-NLS-1$
    }
  }

  /**
   * @since 3.1
   */
  protected static double percent(double part, double whole)
  {
    return Math.min(part * HUNDRED / whole, HUNDRED);
  }

  /**
   * @author Eike Stepper
   */
  public static class AsyncTimerTask extends TrackableTimerTask implements Async
  {
    private OMMonitor monitor;

    private boolean canceled;

    public AsyncTimerTask(AbstractMonitor parent, double parentWork, double timeFactor)
    {
      monitor = parent.fork(parentWork);
      monitor.begin();
    }

    @Override
    public void run()
    {
      try
      {
        if (!canceled && monitor != null)
        {
          if (monitor.isCanceled())
          {
            stop();
            return;
          }

          double work = 1 - monitor.getWork();
          monitor.worked(work / TEN);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error("AsyncTimerTask failed", ex);
      }
    }

    @Override
    public void stop()
    {
      try
      {
        if (monitor != null)
        {
          monitor.done();
        }

        cancel();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    @Override
    public boolean cancel()
    {
      canceled = true;
      monitor = null;
      return super.cancel();
    }
  }
}
