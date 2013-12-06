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
import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressDialog;
import org.eclipse.emf.cdo.releng.setup.KeyBindingTask;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

import java.util.Collections;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class PerformSetupAction extends AbstractSetupAction
{
  private static final boolean TEST_SINGLE_TASK = false;

  public PerformSetupAction()
  {
  }

  public void run(IAction action)
  {
    try
    {
      final SetupTaskPerformer setupTaskPerformer = new SetupTaskPerformer(true);

      if (TEST_SINGLE_TASK)
      {
        SetupTask task = createTestTask();
        if (task.isNeeded(setupTaskPerformer))
        {
          task.perform(setupTaskPerformer);
        }
      }
      else
      {
        Shell shell = getWindow().getShell();
        ProgressDialog.run(shell, new ProgressLogRunnable()
        {
          public Set<String> run(ProgressLog log) throws Exception
          {
            setupTaskPerformer.perform();
            return setupTaskPerformer.getRestartReasons();
          }
        }, Collections.singletonList(setupTaskPerformer));
      }
    }
    catch (Exception ex)
    {
      SetupEditorPlugin.INSTANCE.log(ex);
    }
  }

  private KeyBindingTask createTestTask()
  {
    KeyBindingTask task = SetupFactory.eINSTANCE.createKeyBindingTask();
    task.setKeys("F12");
    task.setCommand("org.eclipse.emf.cdo.releng.OpenManifest");
    return task;
  }
}
