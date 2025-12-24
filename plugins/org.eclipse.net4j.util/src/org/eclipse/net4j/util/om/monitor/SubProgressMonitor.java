/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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
