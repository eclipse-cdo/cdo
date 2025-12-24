/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class DelegatingMonitor implements OMMonitor
{
  private OMMonitor delegate;

  public DelegatingMonitor(OMMonitor delegate)
  {
    this.delegate = delegate;
  }

  public OMMonitor getDelegate()
  {
    return delegate;
  }

  @Override
  public boolean hasBegun() throws MonitorCanceledException
  {
    return delegate.hasBegun();
  }

  @Override
  public OMMonitor begin() throws MonitorCanceledException
  {
    return delegate.begin();
  }

  @Override
  public OMMonitor begin(double totalWork) throws MonitorCanceledException
  {
    return delegate.begin(totalWork);
  }

  @Override
  public void checkCanceled() throws MonitorCanceledException
  {
    delegate.checkCanceled();
  }

  @Override
  public void done()
  {
    delegate.done();
  }

  @Override
  public OMMonitor fork()
  {
    return delegate.fork();
  }

  @Override
  public OMMonitor fork(double work)
  {
    return delegate.fork(work);
  }

  @Override
  public Async forkAsync()
  {
    return delegate.forkAsync();
  }

  @Override
  public Async forkAsync(double work)
  {
    return delegate.forkAsync(work);
  }

  @Override
  public double getTotalWork()
  {
    return delegate.getTotalWork();
  }

  @Override
  public double getWork()
  {
    return delegate.getWork();
  }

  @Override
  public double getWorkPercent()
  {
    return delegate.getWorkPercent();
  }

  @Override
  public boolean isCanceled()
  {
    return delegate.isCanceled();
  }

  @Override
  public void worked() throws MonitorCanceledException
  {
    delegate.worked();
  }

  @Override
  public void worked(double work) throws MonitorCanceledException
  {
    delegate.worked(work);
  }
}
