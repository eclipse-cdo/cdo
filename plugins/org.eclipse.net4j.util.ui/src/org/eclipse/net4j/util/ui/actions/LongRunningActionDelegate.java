/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Eike Stepper
 */
public abstract class LongRunningActionDelegate extends SafeActionDelegate
{
  private int totalWork;

  public LongRunningActionDelegate()
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
  protected final void safeRun() throws Exception
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
            doRun(progressMonitor);
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
  }

  protected void preRun() throws Exception
  {
  }

  protected String getBundleID()
  {
    return OM.BUNDLE_ID;
  }

  /**
   * @since 2.0
   */
  protected abstract void doRun(IProgressMonitor progressMonitor) throws Exception;

  protected final void checkCancelation(IProgressMonitor monitor)
  {
    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }
}
