/*
 * Copyright (c) 2004-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 * @since 3.6
 */
@SuppressWarnings("deprecation")
public class SubProgressMonitor extends org.eclipse.core.runtime.SubProgressMonitor
{
  public SubProgressMonitor(IProgressMonitor monitor, int ticks, int style)
  {
    super(monitor, ticks, style);
  }

  public SubProgressMonitor(IProgressMonitor monitor, int ticks)
  {
    super(monitor, ticks);
  }

  @Override
  public void beginTask(String name, int totalWork)
  {
    super.beginTask(name, totalWork);
  }

  @Override
  public void done()
  {
    super.done();
  }

  @Override
  public void internalWorked(double work)
  {
    super.internalWorked(work);
  }

  @Override
  public void subTask(String name)
  {
    super.subTask(name);
  }

  @Override
  public void worked(int work)
  {
    super.worked(work);
  }
}
