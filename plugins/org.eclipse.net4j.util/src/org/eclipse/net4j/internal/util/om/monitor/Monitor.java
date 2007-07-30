package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.util.om.monitor.MonitorCanceledException;
import org.eclipse.net4j.util.om.monitor.OMSubMonitor;
import org.eclipse.net4j.util.om.monitor.TotalWorkExceededException;

/**
 * @author Eike Stepper
 */
public abstract class Monitor implements InternalOMMonitor
{
  private static final int UNINITIALIZED = 0;

  private Monitor parent;

  private int workFromParent;

  private int totalWork = UNINITIALIZED;

  private int work;

  private String task;

  private Monitor child;

  private boolean canceled;

  public Monitor(Monitor parent, int workFromParent)
  {
    this.parent = parent;
    this.workFromParent = workFromParent;
  }

  public boolean isCanceled()
  {
    return canceled;
  }

  public void checkCanceled() throws MonitorCanceledException
  {
    if (canceled)
    {
      throw new MonitorCanceledException();
    }
  }

  public String getTask()
  {
    return task;
  }

  public void setTask(String task)
  {
    this.task = task;
    message(task);
  }

  public int getTotalWork()
  {
    return totalWork;
  }

  public boolean hasBegun()
  {
    return totalWork != UNINITIALIZED;
  }

  public void message(String msg)
  {
    if (msg != null)
    {
      message(msg, 0);
    }
  }

  public void worked(int work, String msg) throws MonitorCanceledException
  {
    MON.checkMonitor(this);
    checkWork(work);

    this.work += work;
    message(msg);
  }

  public void worked(int work) throws MonitorCanceledException
  {
    worked(work, null);
  }

  public void worked(String msg) throws MonitorCanceledException
  {
    worked(1, msg);
  }

  public void worked() throws MonitorCanceledException
  {
    worked(1, null);
  }

  public void fork(int workFromParent, Runnable runnable, String msg) throws MonitorCanceledException
  {
    MON.checkMonitor(this);
    checkWork(workFromParent);

    child = subMonitor(workFromParent);
    MON.setMonitor(child);

    try
    {
      runnable.run();
    }
    finally
    {
      MON.checkMonitor(child);
      child.done();

      MON.setMonitor(this);
      child = null;
    }

    work += workFromParent;
    message(msg);
  }

  public void fork(int workFromParent, Runnable runnable) throws MonitorCanceledException
  {
    fork(workFromParent, runnable, null);
  }

  public void fork(Runnable runnable, String msg) throws MonitorCanceledException
  {
    fork(UNKNOWN, runnable, msg);
  }

  public void fork(Runnable runnable) throws MonitorCanceledException
  {
    fork(UNKNOWN, runnable, null);
  }

  public OMSubMonitor fork(int workFromParent) throws MonitorCanceledException
  {
    MON.checkMonitor(this);
    checkWork(workFromParent);

    child = subMonitor(workFromParent);
    MON.setMonitor(child);
    return child;
  }

  public OMSubMonitor fork() throws MonitorCanceledException
  {
    return fork(UNKNOWN);
  }

  public void join(String msg) throws MonitorCanceledException
  {
    MON.checkMonitor(this);
    done();

    MON.setMonitor(parent);
    parent.setChild(null);
    parent.message(msg);
  }

  public void join() throws MonitorCanceledException
  {
    join(null);
  }

  protected Monitor getChild()
  {
    return child;
  }

  protected void setChild(Monitor child)
  {
    this.child = child;
  }

  protected void setCanceled(boolean canceled)
  {
    this.canceled = canceled;
    if (child != null)
    {
      child.setCanceled(canceled);
    }
  }

  protected Monitor getParent()
  {
    return parent;
  }

  protected int getWorkFromParent()
  {
    return workFromParent;
  }

  protected String dump()
  {
    StringBuilder builder = new StringBuilder();
    dump(builder, 0);
    return builder.toString();
  }

  protected void begin(int totalWork, String task) throws MonitorCanceledException
  {
    checkCanceled();
    this.totalWork = totalWork;
    if (task != null)
    {
      setTask(task);
    }
  }

  protected void done()
  {
  }

  protected void message(String msg, int level)
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

  protected abstract Monitor subMonitor(int workFromParent);

  private void checkWork(int work)
  {
    if (totalWork != UNKNOWN && this.work + work > totalWork)
    {
      throw new TotalWorkExceededException(("Work of " + work + " exceeded total work of " + totalWork));
    }
  }

  private int dump(StringBuilder builder, int level)
  {
    if (parent != null)
    {
      int line = parent.dump(builder, level + 1);
      builder.append(line);
      builder.append(": ");
      builder.append(task);
      builder.append("\n");
      return line + 1;
    }
    else
    {
      builder.append("1: ");
      builder.append(task);
      builder.append("\n");
      return 2;
    }
  }
}