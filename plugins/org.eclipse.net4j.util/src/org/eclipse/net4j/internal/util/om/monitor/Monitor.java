/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.monitor.MonitorCanceledException;
import org.eclipse.net4j.util.om.monitor.MonitorNotBegunException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMSubMonitor;
import org.eclipse.net4j.util.om.monitor.TotalWorkExceededException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public abstract class Monitor implements OMMonitor, OMSubMonitor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Monitor.class);

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
    taskChanged(task, 0);
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
      MON.setMonitor(this);
      child.done();
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
    fork(1, runnable, msg);
  }

  public void fork(Runnable runnable) throws MonitorCanceledException
  {
    fork(1, runnable, null);
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
    return fork(1);
  }

  public void join(String msg) throws MonitorCanceledException
  {
    MON.checkMonitor(this);
    MON.setMonitor(parent);
    parent.setChild(null);
    parent.message(msg);
    done();
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
    dump(builder);
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

  protected void taskChanged(String task, int level)
  {
    if (parent != null)
    {
      parent.taskChanged(task, level + 1);
    }
    else
    {
      trace(task, level, true);
    }
  }

  protected void message(String msg, int level)
  {
    if (parent != null)
    {
      parent.message(msg, level + 1);
    }
    else
    {
      trace(msg, level, false);
    }
  }

  protected void trace(String msg, int level, boolean isTask)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(msg);
    }
  }

  protected void checkWork(int work)
  {
    if (!hasBegun())
    {
      throw new MonitorNotBegunException("Monitor has not begun");
    }

    if (totalWork != UNKNOWN && this.work + work > totalWork)
    {
      throw new TotalWorkExceededException(("Work of " + work + " exceeded total work of " + totalWork));
    }
  }

  protected void dump(StringBuilder builder)
  {
    builder.append("  ");
    builder.append(task);
    builder.append("\n");
    if (parent != null)
    {
      parent.dump(builder);
    }
  }

  protected abstract Monitor subMonitor(int workFromParent);
}
