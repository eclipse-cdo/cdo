/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide.actions;

import org.eclipse.emf.cdo.releng.setup.helper.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.ide.Buckminster;
import org.eclipse.emf.cdo.releng.setup.ui.ProgressLogDialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Eike Stepper
 */
public class BuckyAction implements IWorkbenchWindowActionDelegate
{
  private IWorkbenchWindow window;

  public BuckyAction()
  {
  }

  public void init(IWorkbenchWindow window)
  {
    this.window = window;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  public void dispose()
  {
  }

  public void run(IAction action)
  {
    ProgressLogDialog.run(window.getShell(), "Importing mspec", new ProgressLogRunnable()
    {
      public void run(ProgressLog log) throws Exception
      {
        Buckminster.importMSpec();
      }
    });
  }
}
