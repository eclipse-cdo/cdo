/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306 - adapted from LongRunningActionDelegate
 */
package org.eclipse.net4j.util.ui.handlers;

import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Eike Stepper
 * @author Christian W. Damus (CEA LIST)
 * 
 * @since 3.4
 */
public abstract class LongRunningHandler extends SafeHandler
{
  private int totalWork;

  public LongRunningHandler()
  {
  }

  protected final int getTotalWork()
  {
    return totalWork;
  }

  protected final void setTotalWork(int totalWork)
  {
    this.totalWork = totalWork;
  }

  protected final void cancel()
  {
    totalWork = 0;
  }

  @Override
  protected final Object safeExecute(ExecutionEvent event) throws Exception
  {
    totalWork = IProgressMonitor.UNKNOWN;
    preRun();
    if (totalWork != 0)
    {
      new Job(getText())
      {
        @Override
        protected IStatus run(IProgressMonitor progressMonitor)
        {
          try
          {
            doExecute(progressMonitor);
            return Status.OK_STATUS;
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
            return new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getMessage(), ex);
          }
        }
      }.schedule();
    }

    // Cannot return anything more useful
    return null;
  }

  protected void preRun() throws Exception
  {
  }

  protected String getBundleID()
  {
    return OM.BUNDLE_ID;
  }

  /**
   * Executes the long-running handler in a background job.  Note that the original
   * {@link ExecutionEvent} is not available because it is only valid during the
   * execution of the handler call-back on the UI thread.  Any details required from
   * it must be {@linkplain SafeHandler#extractEventDetails(ExecutionEvent) extracted}
   * before the job is scheduled.
   */
  protected abstract void doExecute(IProgressMonitor progressMonitor) throws Exception;

  protected final void checkCancelation(IProgressMonitor monitor)
  {
    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }
}
