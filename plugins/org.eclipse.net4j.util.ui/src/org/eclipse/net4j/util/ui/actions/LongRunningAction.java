/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public abstract class LongRunningAction extends SafeAction
{
  private IWorkbenchPage page;

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

  public LongRunningAction()
  {
  }

  public LongRunningAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public LongRunningAction(String text, int style)
  {
    super(text, style);
  }

  public LongRunningAction(String text, String toolTipText, ImageDescriptor image)
  {
    super(text, toolTipText, image);
  }

  public LongRunningAction(String text, String toolTipText)
  {
    super(text, toolTipText);
  }

  public LongRunningAction(String text)
  {
    super(text);
  }

  public IWorkbenchPage getPage()
  {
    if (page != null)
    {
      return page;
    }

    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null)
    {
      return window.getActivePage();
    }

    return null;
  }

  public void setPage(IWorkbenchPage page)
  {
    this.page = page;
  }

  public IWorkbenchWindow getWorkbenchWindow()
  {
    if (page != null)
    {
      return page.getWorkbenchWindow();
    }

    return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
  }

  /**
   * @since 2.0
   */
  public Display getDisplay()
  {
    IWorkbenchWindow workbenchWindow = getWorkbenchWindow();
    if (workbenchWindow != null)
    {
      return workbenchWindow.getShell().getDisplay();
    }

    return PlatformUI.getWorkbench().getDisplay();
  }

  public Shell getShell()
  {
    IWorkbenchWindow workbenchWindow = getWorkbenchWindow();
    if (workbenchWindow != null)
    {
      return workbenchWindow.getShell();
    }

    return new Shell();
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
