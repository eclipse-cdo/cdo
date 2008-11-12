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
public class Monitor implements IMonitor
{
  private int totalWork;

  private int work;

  private boolean canceled;

  public Monitor()
  {
  }

  public synchronized void cancel()
  {
    canceled = true;
  }

  public synchronized void checkCanceled() throws MonitorCanceledException
  {
    if (isCanceled())
    {
      throw new MonitorCanceledException();
    }
  }

  public synchronized void begin(int totalWork) throws MonitorCanceledException
  {
    this.totalWork = totalWork;
  }

  public synchronized void worked(int work) throws MonitorCanceledException
  {
    this.work += work;
  }

  public synchronized IMonitor fork(int work)
  {
    return new NestedMonitor(this, work);
  }

  public synchronized void done()
  {
    int rest = totalWork - work;
    if (rest > 0)
    {
      worked(rest);
    }
  }

  public synchronized int getTotalWork()
  {
    return totalWork;
  }

  public synchronized int getWork()
  {
    return work;
  }

  public synchronized boolean isCanceled()
  {
    return canceled;
  }
}
