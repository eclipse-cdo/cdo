package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.util.om.monitor.MONITOR;

/**
 * @author Eike Stepper
 */
public abstract class Monitor
{
  private static final int UNINITIALIZED = 0;

  private Monitor parent;

  private int workFromParent;

  private int totalWork = UNINITIALIZED;

  private int work;

  private String task;

  public Monitor(Monitor parent, int workFromParent)
  {
    this.parent = parent;
    this.workFromParent = workFromParent;
  }

  public Monitor getParent()
  {
    return parent;
  }

  public int getWorkFromParent()
  {
    return workFromParent;
  }

  public String getTask()
  {
    return task;
  }

  public void setTask(String task, int level)
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

  public void message(String msg, int level)
  {
    if (parent != null)
    {
      parent.message(msg, level + 1);
    }
    else
    {
      for (int i = 0; i < level; i++)
      {
        System.out.print("  ");
      }

      System.out.println(msg);
    }
  }

  public abstract Monitor subMonitor(int workFromParent);
}