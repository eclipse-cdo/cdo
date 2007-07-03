/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.internal.net4j.bundle.OM;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class LongRunningAction extends SafeAction
{
  private IWorkbenchPage page;

  private IStatus status;

  private int totalWork;

  public LongRunningAction(IWorkbenchPage page)
  {
    this.page = page;
  }

  public LongRunningAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image)
  {
    super(text, toolTipText, image);
    this.page = page;
  }

  public LongRunningAction(IWorkbenchPage page, String text, ImageDescriptor image)
  {
    super(text, image);
    this.page = page;
  }

  public LongRunningAction(IWorkbenchPage page, String text, String toolTipText)
  {
    super(text, toolTipText);
    this.page = page;
  }

  public LongRunningAction(IWorkbenchPage page, String text, int style)
  {
    super(text, style);
    this.page = page;
  }

  public LongRunningAction(IWorkbenchPage page, String text)
  {
    super(text);
    this.page = page;
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

  protected final void setStatus(IStatus status)
  {
    this.status = status;
  }

  @Override
  protected final void doRun() throws Exception
  {
    totalWork = IProgressMonitor.UNKNOWN;
    preRun(page);
    if (totalWork != 0)
    {
      new Job(getText())
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          monitor.beginTask("", totalWork);
          try
          {
            setStatus(Status.OK_STATUS);
            doRun(page, monitor);
            return status;
          }
          catch (Exception ex)
          {
            return handleException(ex);
          }
          finally
          {
            monitor.done();
          }
        }
      }.schedule();
    }
  }

  protected void preRun(IWorkbenchPage page) throws Exception
  {
  }

  protected abstract void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception;

  protected IStatus handleException(Exception ex)
  {
    return new Status(IStatus.ERROR, OM.BUNDLE_ID, "An error has occured.", ex);
  }

  protected final void checkCancelation(IProgressMonitor monitor)
  {
    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }
}
