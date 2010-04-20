/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Monitor extends AbstractMonitor
{
  public static final long DEFAULT_ASYNC_SCHEDULE_PERIOD = 1000;

  private static TimerTask TEST_TASK = new TimerTask()
  {
    @Override
    public void run()
    {
      // Do nothing
    }
  };

  private static Timer TIMER;

  private boolean canceled;

  private RuntimeException cancelException;

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

  public boolean isCanceled()
  {
    return canceled;
  }

  public void checkCanceled() throws MonitorCanceledException
  {
    if (cancelException != null)
    {
      throw cancelException;
    }

    if (canceled)
    {
      throw new MonitorCanceledException();
    }
  }

  @Override
  protected long getAsyncSchedulePeriod()
  {
    return DEFAULT_ASYNC_SCHEDULE_PERIOD;
  }

  @Override
  protected Timer getTimer()
  {
    synchronized (Monitor.class)
    {
      try
      {
        // Check that timer exists and is not canceled.
        TIMER.schedule(TEST_TASK, 0L);
      }
      catch (Exception ex)
      {
        if (TIMER != null)
        {
          TIMER.cancel();
        }

        TIMER = new Timer("monitor-timer", true); //$NON-NLS-1$
      }

      return TIMER;
    }
  }
}
