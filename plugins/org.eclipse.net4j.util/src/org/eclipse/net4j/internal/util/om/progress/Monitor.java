package org.eclipse.net4j.internal.util.om.progress;

import org.eclipse.net4j.util.om.PROGRESS;

/**
 * @author Eike Stepper
 */
public abstract class Monitor
{
  private static final int UNINITIALIZED = 0;

  protected int totalWork = UNINITIALIZED;

  protected int work;

  public Monitor()
  {
  }

  public int getTotalWork()
  {
    return totalWork;
  }

  public void begin(int totalWork, String task)
  {
    if (this.totalWork != UNINITIALIZED)
    {
      throw new IllegalStateException("Monitoring has already begun");
    }

    this.totalWork = totalWork;
    if (task != null)
    {
      setTask(task);
    }
  }

  public void worked(int work, String success)
  {
    if (this.totalWork == UNINITIALIZED)
    {
      throw new IllegalStateException("Monitoring has not yet begun");
    }

    this.work += work;
    if (totalWork != PROGRESS.UNKNOWN && this.work > totalWork)
    {
      throw new IllegalStateException("Work has exceeded total work");
    }

    if (success != null)
    {
      onSuccess(success);
    }
  }

  public abstract void onSuccess(String success);

  public abstract String getTask();

  public abstract void setTask(String task);
}