/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
  private static final ThreadLocal<Boolean> CANCELED = new ThreadLocal<>();

  public LongRunningHandler()
  {
  }

  /**
   * @deprecated Not supported anymore.
   */
  @Deprecated
  protected final int getTotalWork()
  {
    return IProgressMonitor.UNKNOWN;
  }

  /**
   * @deprecated Not supported anymore.
   */
  @Deprecated
  protected final void setTotalWork(int totalWork)
  {
  }

  protected final void cancel()
  {
    CANCELED.set(Boolean.TRUE);
  }

  @Override
  protected final Object safeExecute(final ExecutionEvent event) throws Exception
  {
    try
    {
      CANCELED.set(Boolean.FALSE);
      preRun(event);

      if (CANCELED.get() != Boolean.TRUE)
      {
        new Job(getText())
        {
          @Override
          protected IStatus run(IProgressMonitor progressMonitor)
          {
            try
            {
              doExecute(event, progressMonitor);
              return Status.OK_STATUS;
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
              return new Status(IStatus.ERROR, getBundleID(), ex.getMessage(), ex);
            }
          }
        }.schedule();
      }

      // Cannot return anything more useful
      return null;
    }
    finally
    {
      CANCELED.remove();
    }
  }

  /**
   * @since 3.5
   */
  protected void preRun(ExecutionEvent event) throws Exception
  {
    preRun();
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
   *
   * @throws Exception
   * @since 3.5
   */
  protected void doExecute(ExecutionEvent event, IProgressMonitor progressMonitor) throws Exception
  {
    doExecute(progressMonitor);
  }

  protected void doExecute(IProgressMonitor progressMonitor) throws Exception
  {
  }

  protected final void checkCancelation(IProgressMonitor monitor)
  {
    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }
}
