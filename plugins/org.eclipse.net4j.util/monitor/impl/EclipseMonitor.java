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
package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author Eike Stepper
 */
public class EclipseMonitor extends Monitor
{
  private IProgressMonitor progressMonitor;

  public EclipseMonitor(IProgressMonitor progressMonitor)
  {
    super(null, 0);
    if (progressMonitor == null)
    {
      throw new IllegalArgumentException("progressMonitor == null");
    }

    this.progressMonitor = new DelegatingProgressMonitor(progressMonitor);
  }

  private EclipseMonitor(EclipseMonitor parent, int workFromParent)
  {
    super(parent, workFromParent);
    progressMonitor = new SubProgressMonitor(new SubProgressMonitor(parent.getProgressMonitor(), workFromParent),
        workFromParent);
  }

  public IProgressMonitor getProgressMonitor()
  {
    return progressMonitor;
  }

  @Override
  public void setTask(String task)
  {
    super.setTask(task);
    progressMonitor.setTaskName(task);
  }

  @Override
  public void worked(int work, String msg)
  {
    super.worked(work, msg);
    progressMonitor.worked(work);
    if (msg != null)
    {
      progressMonitor.subTask(msg);
    }
  }

  @Override
  protected void begin(int totalWork, String task)
  {
    super.begin(totalWork, task);
    progressMonitor.beginTask(task == null ? "" : task, totalWork);
  }

  @Override
  protected void done()
  {
    super.done();
    progressMonitor.done();
  }

  @Override
  protected void message(String msg, int level)
  {
    super.message(msg, level);
    progressMonitor.subTask(msg);
  }

  @Override
  protected EclipseMonitor subMonitor(int workFromParent)
  {
    return new EclipseMonitor(this, workFromParent);
  }

  /**
   * @author Eike Stepper
   */
  private final class DelegatingProgressMonitor implements IProgressMonitor
  {
    private IProgressMonitor delegate;

    public DelegatingProgressMonitor(IProgressMonitor delegate)
    {
      this.delegate = delegate;
    }

    public IProgressMonitor getDelegate()
    {
      return delegate;
    }

    public void beginTask(String name, int totalWork)
    {
      delegate.beginTask(name, totalWork);
    }

    public void done()
    {
      delegate.done();
    }

    public void internalWorked(double work)
    {
      delegate.internalWorked(work);
    }

    public boolean isCanceled()
    {
      return delegate.isCanceled();
    }

    public void setCanceled(boolean value)
    {
      EclipseMonitor.this.setCanceled(value);
      delegate.setCanceled(value);
    }

    public void setTaskName(String name)
    {
      delegate.setTaskName(name);
    }

    public void subTask(String name)
    {
      delegate.subTask(name);
    }

    public void worked(int work)
    {
      delegate.worked(work);
    }
  }
}
