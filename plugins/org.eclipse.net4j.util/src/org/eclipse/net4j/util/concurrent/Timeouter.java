package org.eclipse.net4j.util.concurrent;

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

  private long touched;

  public Timeouter(Timer timer, long timeout)
  {
    this.timer = timer;
    this.timeout = timeout;
    touched = System.currentTimeMillis();
    scheduleTimeout();
  }

  public Timer getTimer()
  {
    return timer;
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

  public void dispose()
  {
    if (timeoutTask != null)
    {
      timeoutTask.cancel();
      timeoutTask = null;
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
    };

    long delay = Math.max(timeout - (System.currentTimeMillis() - touched), 0L);
    timer.schedule(timeoutTask, delay);
  }
}
