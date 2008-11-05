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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * A sub progress monitor that synchronizes all methods on the parent monitor instance.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public final class SynchonizedSubProgressMonitor extends SubProgressMonitor
{
  public SynchonizedSubProgressMonitor(IProgressMonitor monitor, int ticks)
  {
    super(monitor, ticks);
  }

  @Override
  public void beginTask(String name, int totalWork)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.beginTask(name, totalWork);
    }
  }

  @Override
  public void clearBlocked()
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.clearBlocked();
    }
  }

  @Override
  public void done()
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.done();
    }
  }

  @Override
  public void internalWorked(double work)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.internalWorked(work);
    }
  }

  @Override
  public boolean isCanceled()
  {
    synchronized (getWrappedProgressMonitor())
    {
      return super.isCanceled();
    }
  }

  @Override
  public void setBlocked(IStatus reason)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.setBlocked(reason);
    }
  }

  @Override
  public void setCanceled(boolean b)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.setCanceled(b);
    }
  }

  @Override
  public void setTaskName(String name)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.setTaskName(name);
    }
  }

  @Override
  public void subTask(String name)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.subTask(name);
    }
  }

  @Override
  public void worked(int work)
  {
    synchronized (getWrappedProgressMonitor())
    {
      super.worked(work);
    }
  }
}
