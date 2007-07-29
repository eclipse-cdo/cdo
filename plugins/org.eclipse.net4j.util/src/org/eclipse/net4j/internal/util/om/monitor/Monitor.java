package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.util.om.monitor.MONITOR;

/**
 * @author Eike Stepper
 */
public abstract class Monitor
{
  private static final int UNINITIALIZED = 0;

  private int totalWork = UNINITIALIZED;

  private int work;

  private String task;

  public Monitor()
  {
  }

  public final String getTask()
  {
    return task;
  }

  public final void setTask(String task, int level)
  {
    this.task = task;
    message(task, level);
  }

  public int getTotalWork()
  {
    return totalWork;
  }

  public void begin(int totalWork, String task, int level)
  {
    if (this.totalWork != UNINITIALIZED)
    {
      throw new IllegalStateException("Monitoring has already begun");
    }

    this.totalWork = totalWork;
    if (task != null)
    {
      setTask(task, level);
    }
  }

  public void worked(int work, String msg, int level)
  {
    if (this.totalWork == UNINITIALIZED)
    {
      throw new IllegalStateException("Monitoring has not yet begun");
    }

    this.work += work;
    if (totalWork != MONITOR.UNKNOWN && this.work > totalWork)
    {
      throw new IllegalStateException("Work has exceeded total work");
    }

    if (msg != null)
    {
      message(msg, level);
    }
  }

  public abstract void message(String msg, int level);
}