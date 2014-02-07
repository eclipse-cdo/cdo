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
 * A {@link IProgressMonitorWithBlocking progress monitor} that wraps a {@link SubMonitor} and adds some unique functionality.
 * <p>
 * Progress monitoring is generally quite invasive to the code that is monitored.
 * At the same time progress monitoring itself is typically very hard to implement correctly.
 * This class aims at reducing the invasiveness as much as possible while offering all the functionality needed
 * to do the job right.
 * <p>
 * The following aspects of this class help to keep the progress monitoring code short and nice:
 * <ul>
 * <li> It offers the full functionality of {@link SubMonitor}, which already makes progress monitoring a lot easier.
 *      Refer to the {@link SubMonitor} documentation or to this <a href="https://wiki.eclipse.org/Progress_Reporting">article</a> for details and examples.
 * <li> Instead of {@link SubMonitor#newChild(int)} it offers the shorter method name {@link #fork(int)}.
 * <li> In addition to the {@link SubMonitor#setWorkRemaining(int)} method it offers a {@link #skipped(int)} method, which redistributes the remaining work
 *      according to the last skipped {@link #worked(int)} or {@link #fork(int)} call rather than on the sum of all subsequent calls.
 * <li> It reduces the need to specify <i>tick</i> values by defining the value {@link #DEFAULT_TOTAL_TICKS 100} (as in 100%) as the default for <code>totalTicks</code>
 *      and {@link #DEFAULT_TICKS 1} (as in 1%) for the <code>ticks</code> parameter in the {@link #worked()} and {@link #fork()} calls.
 * </ul>
 * <p>
 * The following aspects of this class help to avoid common monitoring mistakes:
 * <ul>
 * <li> Basically all methods of this class implicitely check for cancelation, thereby ensuring that the monitored code is always cancelable by the user
 *      without cluttering the code with repetitions of the following idiom:<pre>
 *      if (monitor.isCanceled())
 *      {
 *        throw new OperationCanceledException();
 *      }</pre>
 * <li> It is normally very challenging to find out how much time a program really spends in the different parts of the monitored methods or how often these
 *      parts get executed. Stepping through the program with a debugger obviously leads to distortion that renders the observations meaningless and adding
 *      extra code to measure a runtime scenario realisticly is not nice from a maintenance point of view.
 *      <br><br>
 *      As a solution to this problem this class offers the possibility to transparently instrument {@link Progress} instances such that they automatically
 *      collect and report all kinds of statistics that may help to enhance the user experience. Sometimes it would even indicate to remove some progress monitoring
 *      because it turns out that almost no time is being spent in a particular part of the program. Another typical result from the analysis is the understanding of
 *      <i>one time effects</i> that might need special consideration.
 *      <br><br>
 *      For details about this <i>probing</i> mode refer to {@link ProbingProgress}.
 * </ul>
 * <p>
 *
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
