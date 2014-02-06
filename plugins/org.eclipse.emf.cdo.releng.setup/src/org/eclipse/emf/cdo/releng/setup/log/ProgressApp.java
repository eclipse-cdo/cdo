/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.log;

import org.eclipse.net4j.util.om.monitor.Progress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Eike Stepper
 */
public final class ProgressApp
{
  public static void main(String[] args) throws Exception
  {
    IProgressMonitor monitor = new NullProgressMonitor();
    for (int i = 0; i < 30; i++)
    {
      test(monitor);
    }

    monitor.done();
  }

  private static void test(IProgressMonitor monitor) throws InterruptedException
  {
    Progress progress = Progress.progress(monitor);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(15);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);

    Thread.sleep(10);
    progress.worked(10);
  }
}
