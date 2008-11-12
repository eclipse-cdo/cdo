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
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.om.monitor.MON;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Eike Stepper
 */
public abstract class MonitoredJob extends Job
{
  private String bundleID;

  public MonitoredJob(String bundleID, String name)
  {
    super(name);
    this.bundleID = bundleID;
  }

  @Override
  protected void canceling()
  {
    MON.setCanceled(true);
    super.canceling();
  }

  @Override
  protected final IStatus run(IProgressMonitor monitor)
  {
    MonitorUtil.Eclipse.startMonitoring(monitor);

    try
    {
      run();
      return Status.OK_STATUS;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return new Status(IStatus.ERROR, bundleID, ex.getMessage(), ex);
    }
    finally
    {
      MonitorUtil.Eclipse.stopMonitoring();
    }
  }

  protected abstract void run() throws Exception;
}
