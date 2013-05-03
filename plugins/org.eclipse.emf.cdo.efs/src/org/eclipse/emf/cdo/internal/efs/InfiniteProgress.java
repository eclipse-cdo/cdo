/*
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.internal.efs;

import org.eclipse.emf.cdo.internal.efs.bundle.OM;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import java.util.concurrent.Callable;

/**
 * This class provides a simulation of progress. This is useful for situations where computing the amount of work to do
 * in advance is too costly. The monitor will accept any number of calls to {@link #worked(int)}, and will scale the
 * actual reported work appropriately so that the progress never quite completes.
 */
public class InfiniteProgress extends ProgressMonitorWrapper
{
  private static final Object NO_RESULT = new Object();

  /*
   * Fields for progress monitoring algorithm. Initially, give progress for every 4 resources, double this value at
   * halfway point, then reset halfway point to be half of remaining work. (this gives an infinite series that converges
   * at total work after an infinite number of resources).
   */
  private int totalWork;

  private int currentIncrement = 4;

  private int halfWay;

  private int nextProgress = currentIncrement;

  private int worked = 0;

  protected InfiniteProgress(IProgressMonitor monitor)
  {
    super(monitor);
  }

  @Override
  public void beginTask(String name, int work)
  {
    super.beginTask(name, work);
    totalWork = work;
    halfWay = totalWork / 2;
  }

  @Override
  public void worked(int work)
  {
    if (--nextProgress <= 0)
    {
      // we have exhausted the current increment, so report progress
      super.worked(1);
      worked++;
      if (worked >= halfWay)
      {
        // we have passed the current halfway point, so double the
        // increment and reset the halfway point.
        currentIncrement *= 2;
        halfWay += (totalWork - halfWay) / 2;
      }
      // reset the progress counter to another full increment
      nextProgress = currentIncrement;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T call(final String label, final Callable<T> callable)
  {
    final Object[] result = { NO_RESULT };
    final IProgressMonitor[] jobMonitor = { null };

    Job job = new Job(label)
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        jobMonitor[0] = new InfiniteProgress(monitor);
        jobMonitor[0].beginTask(label, 10);

        try
        {
          result[0] = callable.call();

          synchronized (result)
          {
            result.notifyAll();
          }

          return Status.OK_STATUS;
        }
        catch (Throwable t)
        {
          result[0] = t;
          if (jobMonitor[0].isCanceled())
          {
            return Status.OK_STATUS;
          }

          return new Status(IStatus.ERROR, OM.BUNDLE_ID, t.getLocalizedMessage(), t);
        }
        finally
        {
          monitor = jobMonitor[0];
          jobMonitor[0] = null;
          monitor.done();
        }
      }
    };

    job.schedule();

    while (result[0] == NO_RESULT)
    {
      synchronized (result)
      {
        if (jobMonitor[0] != null)
        {
          if (jobMonitor[0].isCanceled())
          {
            job.cancel();
            throw new OperationCanceledException();
          }

          jobMonitor[0].worked(1);
        }

        try
        {
          System.out.println("wait...");
          result.wait(100L);
        }
        catch (InterruptedException ex)
        {
          job.cancel();
          throw new OperationCanceledException();
        }
      }
    }

    if (result[0] instanceof Error)
    {
      throw (Error)result[0];
    }

    if (result[0] instanceof Exception)
    {
      throw WrappedException.wrap((Exception)result[0]);
    }

    return (T)result[0];
  }
}
