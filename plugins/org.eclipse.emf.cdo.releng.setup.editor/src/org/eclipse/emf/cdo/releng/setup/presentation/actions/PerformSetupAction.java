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

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressDialog;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.FileAssociationTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
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
        MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 0, "Resource load errors", null);

        Setup setup = setupTaskPerformer.getSetup();
        Branch branch = setup.getBranch();
        if (branch.eIsProxy())
        {
          status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Branch cannot be resolved: "
              + EcoreUtil.getURI(branch)));
        }

        Eclipse eclipse = setup.getEclipseVersion();
        if (eclipse.eIsProxy())
        {
          status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Eclipse version cannot be resolved: "
              + EcoreUtil.getURI(eclipse)));
        }

        for (Resource resource : setupTaskPerformer.getResourceSet().getResources())
        {
          for (Resource.Diagnostic diagnostic : resource.getErrors())
          {
            String message = diagnostic.getMessage();
            if (diagnostic instanceof Throwable)
            {
              status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, (Throwable)diagnostic));
            }
            else
            {
              status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
            }
          }
        }

        if (!status.isOK())
        {
          CoreException exception = new CoreException(status);
          exception.setStackTrace(new StackTraceElement[0]);
          ErrorDialog.open(exception);
          return;
        }

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

  private SetupTask createTestTask()
  {
    // KeyBindingTask task = SetupFactory.eINSTANCE.createKeyBindingTask();
    // task.setKeys("F12");
    // task.setCommand("org.eclipse.emf.cdo.releng.OpenManifest");

    FileAssociationTask task = SetupFactory.eINSTANCE.createFileAssociationTask();
    task.setFilePattern(".project");
    task.setDefaultEditorID("com.objfac.xmleditor.XMLEditor");

    return task;
  }
}
