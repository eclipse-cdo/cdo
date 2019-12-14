/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.ui.UIUtil;

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
  private static final ThreadLocal<Boolean> CANCELED = new ThreadLocal<>();

  private IWorkbenchPage page;

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

    IWorkbenchWindow window = UIUtil.getActiveWorkbenchWindow();
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

    return UIUtil.getActiveWorkbenchWindow();
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

  @Override
  public Shell getShell()
  {
    IWorkbenchWindow workbenchWindow = getWorkbenchWindow();
    if (workbenchWindow != null)
    {
      return workbenchWindow.getShell();
    }

    return UIUtil.getShell();
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
  protected final void safeRun() throws Exception
  {
    try
    {
      CANCELED.set(Boolean.FALSE);
      preRun();

      if (CANCELED.get() != Boolean.TRUE)
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
              return new Status(IStatus.ERROR, getBundleID(), ex.getMessage(), ex);
            }
          }
        }.schedule();
      }
    }
    finally
    {
      CANCELED.remove();
    }
  }

  protected void preRun() throws Exception
  {
    // Do nothing.
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
