/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class TimeoutMonitor extends Monitor
{
  private long timeout;

  private long touched;

  private TimerTask timeoutTask;

  public TimeoutMonitor(long timeout)
  {
    this.timeout = timeout;
    touched = System.currentTimeMillis();
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  public void touch()
  {
    touched = System.currentTimeMillis();
  }

  @Override
  public OMMonitor begin(double totalWork)
  {
    touch();
    super.begin(totalWork);
    scheduleTimeout();
    return this;
  }

  @Override
  public void worked(double work)
  {
    touch();
    super.worked(work);
  }

  @Override
  public OMMonitor fork(double work)
  {
    touch();
    return super.fork(work);
  }

  @Override
  public Async forkAsync(double work)
  {
    touch();
    return super.forkAsync(work);
  }

  @Override
  public void cancel(RuntimeException cancelException)
  {
    if (timeoutTask != null)
    {
      timeoutTask.cancel();
      timeoutTask = null;
    }

    super.cancel(cancelException);
  }

  @Override
  public boolean isCanceled()
  {
    touch();
    return super.isCanceled();
  }

  @Override
  public void checkCanceled() throws MonitorCanceledException
  {
    touch();
    super.checkCanceled();
  }

  protected void handleTimeout(long untouched)
  {
    cancel(new TimeoutRuntimeException("Timeout after " + untouched + " millis"));
  }

  private void scheduleTimeout()
  {
    timeoutTask = new TimerTask()
    {
      @Override
      public void run()
      {
        if (!isCanceled())
        {
          long untouched = System.currentTimeMillis() - touched;
          if (untouched > timeout)
          {
            timeoutTask = null;
            handleTimeout(untouched);
          }
          else
          {
            scheduleTimeout();
          }
        }
      }
    };

    long delay = timeout - (System.currentTimeMillis() - touched);
    getTimer().schedule(timeoutTask, delay);
  }
}
