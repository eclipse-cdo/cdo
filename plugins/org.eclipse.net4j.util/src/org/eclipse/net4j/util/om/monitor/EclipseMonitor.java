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

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EclipseMonitor extends Monitor
{
  private IProgressMonitor progressMonitor;

  private String taskName;

  public EclipseMonitor(IProgressMonitor progressMonitor, String taskName)
  {
    this.progressMonitor = progressMonitor;
    this.taskName = taskName;
  }

  public EclipseMonitor(IProgressMonitor progressMonitor)
  {
    this(progressMonitor, StringUtil.EMPTY);
  }

  public String getTaskName()
  {
    return taskName;
  }

  @Override
  public boolean isCanceled()
  {
    if (super.isCanceled())
    {
      return true;
    }

    return progressMonitor.isCanceled();
  }

  @Override
  public synchronized void begin(int totalWork) throws MonitorCanceledException
  {
    super.begin(totalWork);
    progressMonitor.beginTask(taskName, totalWork);
  }

  @Override
  public synchronized void worked(int work) throws MonitorCanceledException
  {
    super.worked(work);
    progressMonitor.worked(work);
  }
}
