/*
 * Copyright (c) 2009-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Timeouter
{
  private Timer timer;

  private long timeout;

  private TimerTask timeoutTask;

  private volatile long touched;

  public Timeouter(Timer timer, long timeout)
  {
    this.timer = timer;

    setTimeout(timeout);
    touch();
    scheduleTimeout();
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = CheckUtil.sanitizeTimeout(timeout);
  }

  public void touch()
  {
    touched = System.currentTimeMillis();
  }

  public void dispose()
  {
    if (timeoutTask != null)
    {
      TimerTask task = timeoutTask;
      timeoutTask = null;
      task.cancel();
    }
  }

  protected boolean isDisposed()
  {
    return timeoutTask == null;
  }

  protected abstract void handleTimeout(long untouched);

  private void scheduleTimeout()
  {
    timeoutTask = new TimerTask()
    {
      @Override
      public void run()
      {
        try
        {
          if (!isDisposed())
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
        catch (Exception ex)
        {
          OM.LOG.error("TimeouterTask failed", ex);
        }
      }
    };

    try
    {
      long delay = Math.max(timeout - (System.currentTimeMillis() - touched), 0L);
      timer.schedule(timeoutTask, delay);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }
}
