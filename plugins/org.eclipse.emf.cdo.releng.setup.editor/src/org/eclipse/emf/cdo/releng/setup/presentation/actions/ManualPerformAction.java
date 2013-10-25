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
package org.eclipse.emf.cdo.releng.setup.presentation.actions;

import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressLogDialog;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import java.util.Collections;

/**
 * @author Eike Stepper
 */
public class ManualPerformAction implements IWorkbenchWindowActionDelegate
{
  public ManualPerformAction()
  {
  }

  public void init(IWorkbenchWindow window)
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  public void dispose()
  {
  }

  public void run(IAction action)
  {
    try
    {
      final SetupTaskPerformer setupTaskPerformer = new SetupTaskPerformer(true);

      // if (true)
      // {
      // KeyBindingTask task = SetupFactory.eINSTANCE.createKeyBindingTask();
      // task.setKeys("F12");
      // task.setCommand("org.eclipse.emf.cdo.releng.OpenManifest");
      //
      // if (task.isNeeded(setupTaskPerformer))
      // {
      // task.perform(setupTaskPerformer);
      // }
      //
      // return;
      // }

      Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
      ProgressLogDialog.run(shell, "Setting up IDE", new ProgressLogRunnable()
      {
        public boolean run(ProgressLog log) throws Exception
        {
          setupTaskPerformer.perform();
          return true;
        }
      }, Collections.singletonList(setupTaskPerformer));
    }
    catch (Exception ex)
    {
      SetupEditorPlugin.INSTANCE.log(ex);
    }
  }
}
