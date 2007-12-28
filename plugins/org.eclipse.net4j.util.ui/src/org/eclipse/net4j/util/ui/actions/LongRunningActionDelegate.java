/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.om.monitor.MonitorUtil;
import org.eclipse.net4j.util.om.monitor.MonitoredJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

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
      new MonitoredJob(getBundleID(), getText())
      {
        @Override
        protected void run() throws Exception
        {
          try
          {
            MonitorUtil.begin(totalWork);
            doRun();
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
            throw ex;
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

  protected abstract void doRun() throws Exception;

  protected final void checkCancelation(IProgressMonitor monitor)
  {
    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }

}
