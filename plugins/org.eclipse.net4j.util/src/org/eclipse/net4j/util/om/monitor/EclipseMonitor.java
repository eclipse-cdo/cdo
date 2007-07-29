/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.internal.util.om.monitor.Monitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author Eike Stepper
 */
public final class EclipseMonitor extends Monitor
{
  private IProgressMonitor progressMonitor;

  private EclipseMonitor(EclipseMonitor parent, int workFromParent)
  {
    super(parent, workFromParent);
    progressMonitor = new SubProgressMonitor(parent.getProgressMonitor(), workFromParent);
  }

  private EclipseMonitor(IProgressMonitor progressMonitor)
  {
    super(null, 0);
    this.progressMonitor = progressMonitor;
  }

  public IProgressMonitor getProgressMonitor()
  {
    return progressMonitor;
  }

  @Override
  public void begin(int totalWork, String task, int level)
  {
    super.begin(totalWork, task, level);
    progressMonitor.beginTask(task == null ? "" : task, totalWork);
  }

  @Override
  public void message(String msg, int level)
  {
    super.message(msg, level);
    progressMonitor.subTask(msg);
  }

  @Override
  public void setTask(String task, int level)
  {
    super.setTask(task, level);
    progressMonitor.setTaskName(task);
  }

  @Override
  public void worked(int work, String msg, int level)
  {
    super.worked(work, msg, level);
    progressMonitor.worked(work);
    if (msg != null)
    {
      progressMonitor.subTask(msg);
    }
  }

  @Override
  public EclipseMonitor subMonitor(int workFromParent)
  {
    return new EclipseMonitor(this, workFromParent);
  }

  public static void startMonitoring(IProgressMonitor progressMonitor)
  {
    MONITOR.startMonitoring(new EclipseMonitor(progressMonitor));
  }

  public static void stopMonitoring()
  {
    MONITOR.stopMonitoring();
  }
}
