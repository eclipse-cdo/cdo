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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class LongRunningAction extends SafeAction
{
  private IWorkbenchPage page;

  private IStatus status;

  public LongRunningAction(IWorkbenchPage page)
  {
    this.page = page;
  }

  public LongRunningAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image)
  {
    super(text, toolTipText, image);
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

  protected void setStatus(IStatus status)
  {
    this.status = status;
  }

  @Override
  protected void doRun() throws Exception
  {
    new Job(getText())
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
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
      }
    }.schedule();
  }

  protected abstract void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception;

  protected IStatus handleException(Exception ex)
  {
    return new Status(IStatus.ERROR, Net4j.BUNDLE_ID, "An error has occured.", ex);
  }
}
