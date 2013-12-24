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

import org.eclipse.emf.cdo.releng.internal.setup.AbstractSetupTaskContext;
import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;

/**
 * @author Eike Stepper
 */
public class MirrorBranchAction extends AbstractSetupAction
{
  public MirrorBranchAction()
  {
  }

  public void run(IAction action)
  {
    new Job("Mirroring")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          EList<SetupTask> redirections = new BasicEList<SetupTask>();

          SetupTaskPerformer performer = new SetupTaskPerformer(true);
          for (SetupTask task : performer.getTriggeredSetupTasks())
          {
            if (monitor.isCanceled())
            {
              throw new OperationCanceledException();
            }

            task.mirror(performer, redirections, true);
          }

          if (!redirections.isEmpty())
          {
            Preferences preferences = AbstractSetupTaskContext.getCurrentSetup().getPreferences();
            preferences.getSetupTasks().addAll(redirections);
            EMFUtil.saveEObject(preferences);
          }

          System.out.println("FINISHED");
        }
        catch (Exception ex)
        {
          SetupEditorPlugin.INSTANCE.log(ex);
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }
}
