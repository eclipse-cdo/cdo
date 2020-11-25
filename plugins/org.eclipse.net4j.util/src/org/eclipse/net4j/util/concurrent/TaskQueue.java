/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.job.OMJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A queue that asynchronously, but orderly, {@link #execute(Object, IProgressMonitor) executes}
 * tasks that have been {@link #schedule(Object) scheduled} to it.
 *
 * @author Eike Stepper
 * @since 3.13
 */
public abstract class TaskQueue<T>
{
  private final Set<T> queue = new LinkedHashSet<>();

  private TaskJob job;

  public TaskQueue()
  {
  }

  public void schedule(T task)
  {
    synchronized (queue)
    {
      queue.remove(task);

      if (job != null && job.currentTask == task)
      {
        try
        {
          job.cancel();
        }
        finally
        {
          job = null;
        }
      }

      if (job != null)
      {
        queue.add(task);
      }
      else
      {
        job = new TaskJob(task);
        job.schedule();
      }
    }
  }

  protected String getJobName(T task)
  {
    return "Execute " + task;
  }

  protected void handleException(T task, Exception ex)
  {
    OM.LOG.error(getJobName(task), ex);
  }

  protected abstract void execute(T task, IProgressMonitor monitor) throws Exception;

  /**
   * @author Eike Stepper
   */
  private final class TaskJob extends OMJob
  {
    private T currentTask;

    public TaskJob(T task)
    {
      super(getJobName(task));
      currentTask = task;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
      try
      {
        execute(currentTask, monitor);
        return Status.OK_STATUS;
      }
      catch (OperationCanceledException ex)
      {
        return Status.CANCEL_STATUS;
      }
      catch (Exception ex)
      {
        handleException(currentTask, ex);
        return Status.OK_STATUS;
      }
      finally
      {
        synchronized (queue)
        {
          if (queue.isEmpty() || monitor.isCanceled())
          {
            currentTask = null;
            job = null;
          }
          else
          {
            // Dequeue first task.
            Iterator<T> iterator = queue.iterator();
            currentTask = iterator.next();
            iterator.remove();

            // Reschedule current job.
            setName("Resolve " + getJobName(currentTask));
            schedule();
          }
        }
      }
    }
  }
}
