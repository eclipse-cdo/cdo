/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.util.om.monitor.ProbingProgress.ForkedMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

/**
 * @author Eike Stepper
 * @since 3.4
 */
public class Progress implements IProgressMonitorWithBlocking
{
  public static final int DEFAULT_TOTAL_TICKS = 100;

  public static final int DEFAULT_TICKS = 1;

  private final SubMonitor subMonitor;

  private int totalTicks;

  private int ticks;

  Progress(SubMonitor subMonitor, int totalTicks)
  {
    this.subMonitor = subMonitor;
    this.totalTicks = totalTicks;
  }

  public void subTask(String name)
  {
    checkCancelation();
    subMonitor.subTask(name);
  }

  public void worked(int ticks)
  {
    checkCancelation();
    this.ticks += ticks;
    subMonitor.worked(ticks);
  }

  public void worked()
  {
    worked(DEFAULT_TICKS);
  }

  public IProgressMonitorWithBlocking fork(int ticks)
  {
    checkCancelation();
    this.ticks += ticks;
    return subMonitor.newChild(ticks);
  }

  public IProgressMonitorWithBlocking fork()
  {
    return fork(DEFAULT_TICKS);
  }

  public void skipped(int ticks)
  {
    checkCancelation();
    this.ticks += ticks;
    subMonitor.setWorkRemaining(totalTicks - this.ticks);
  }

  public void setRemainingWork(int ticks)
  {
    checkCancelation();
    this.ticks = totalTicks - ticks;
    subMonitor.setWorkRemaining(ticks);
  }

  public void setBlocked(IStatus reason)
  {
    checkCancelation();
    subMonitor.setBlocked(reason);
  }

  public void clearBlocked()
  {
    checkCancelation();
    subMonitor.clearBlocked();
  }

  public void done()
  {
    checkCancelation();
    subMonitor.done();
  }

  @Override
  public String toString()
  {
    return subMonitor.toString();
  }

  public void forkDone()
  {
    // Do nothing
  }

  public boolean isCanceled()
  {
    return subMonitor.isCanceled();
  }

  public void setCanceled(boolean value)
  {
    subMonitor.setCanceled(value);
  }

  @Deprecated
  public void beginTask(String name, int totalWork)
  {
    checkCancelation();
    subMonitor.beginTask(name, totalWork);
  }

  @Deprecated
  public void setTaskName(String name)
  {
    checkCancelation();
    subMonitor.setTaskName(name);
  }

  @Deprecated
  public void internalWorked(double work)
  {
    checkCancelation();
    subMonitor.internalWorked(work);
  }

  private void checkCancelation()
  {
    if (subMonitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }

  public static Progress progress(IProgressMonitor monitor)
  {
    return progress(monitor, DEFAULT_TOTAL_TICKS, Probing.DEFAULT);
  }

  public static Progress progress(IProgressMonitor monitor, int ticks)
  {
    return progress(monitor, ticks, Probing.DEFAULT);
  }

  public static Progress progress(IProgressMonitor monitor, Probing probingMode)
  {
    return progress(monitor, DEFAULT_TOTAL_TICKS, probingMode);
  }

  public static Progress progress(IProgressMonitor monitor, int ticks, Probing probingMode)
  {
    SubMonitor subMonitor = SubMonitor.convert(monitor, ticks);
    if (probingMode == Probing.OFF)
    {
      return new Progress(subMonitor, ticks);
    }

    return ProbingHelper.createProbingProgress(monitor, subMonitor, ticks, probingMode == Probing.FULL);
  }

  /**
   * Helps to avoid unnecessary loading of the {@link ProbingProgress} class, which forks a monitoring thread.
   *
   * @author Eike Stepper
   */
  static final class ProbingHelper
  {
    static Progress createProbingProgress(IProgressMonitor monitor, SubMonitor subMonitor, int ticks, boolean full)
    {
      ProbingProgress result = new ProbingProgress(subMonitor, ticks, full);

      if (monitor instanceof ForkedMonitor)
      {
        ForkedMonitor forkedMonitor = (ForkedMonitor)monitor;
        forkedMonitor.getProgress().setForkTarget(result.getLocation());
      }

      return result;
    }
  }
}
