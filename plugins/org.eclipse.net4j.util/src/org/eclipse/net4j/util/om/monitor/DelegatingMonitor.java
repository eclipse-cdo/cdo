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

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class DelegatingMonitor implements OMMonitor
{
  public DelegatingMonitor()
  {
  }

  public void checkCanceled() throws MonitorCanceledException
  {
    getDelegate().checkCanceled();
  }

  public boolean isCanceled()
  {
    return getDelegate().isCanceled();
  }

  public void setCanceled(boolean canceled)
  {
    getDelegate().setCanceled(canceled);
  }

  public OMSubMonitor fork()
  {
    return getDelegate().fork();
  }

  public void fork(int workFromParent, Runnable runnable, String msg)
  {
    getDelegate().fork(workFromParent, runnable, msg);
  }

  public void fork(int workFromParent, Runnable runnable)
  {
    getDelegate().fork(workFromParent, runnable);
  }

  public OMSubMonitor fork(int workFromParent)
  {
    return getDelegate().fork(workFromParent);
  }

  public void fork(Runnable runnable, String msg)
  {
    getDelegate().fork(runnable, msg);
  }

  public void fork(Runnable runnable)
  {
    getDelegate().fork(runnable);
  }

  public String getTask()
  {
    return getDelegate().getTask();
  }

  public int getTotalWork()
  {
    return getDelegate().getTotalWork();
  }

  public boolean hasBegun()
  {
    return getDelegate().hasBegun();
  }

  public void message(String msg)
  {
    getDelegate().message(msg);
  }

  public void setTask(String task)
  {
    getDelegate().setTask(task);
  }

  public void worked()
  {
    getDelegate().worked();
  }

  public void worked(int work, String msg)
  {
    getDelegate().worked(work, msg);
  }

  public void worked(int work)
  {
    getDelegate().worked(work);
  }

  public void worked(String msg)
  {
    getDelegate().worked(msg);
  }

  @Override
  public String toString()
  {
    return getDelegate().toString();
  }

  protected abstract OMMonitor getDelegate();
}
