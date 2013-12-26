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
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.RedirectionTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTask.MirrorContext;
import org.eclipse.emf.cdo.releng.setup.SetupTask.MirrorRunnable;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;

import org.eclipse.emf.common.util.EList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    Job job = new MirrorJob();
    job.schedule();
  }

  /**
   * @author Eike Stepper
   */
  private static final class MirrorJob extends Job
  {
    private MirrorJob()
    {
      super("Mirroring");
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
      try
      {
        Performer performer = new Performer();

        File mirrorsDir = new File(performer.getInstallDir(), ".mirrors");
        mirrorsDir.mkdirs();

        List<MirrorRunnable> runnables = getMirrorRunnables(performer, mirrorsDir, monitor);
        monitor.beginTask("Mirroring", runnables.size());

        for (MirrorRunnable runnable : runnables)
        {
          checkCancelation(monitor);
          run(monitor, performer, runnable);
        }
      }
      catch (OperationCanceledException ex)
      {
        return Status.CANCEL_STATUS;
      }
      catch (Exception ex)
      {
        SetupEditorPlugin.INSTANCE.log(ex);
      }
      finally
      {
        monitor.done();
      }

      return Status.OK_STATUS;
    }

    private void run(IProgressMonitor monitor, Performer performer, MirrorRunnable runnable) throws Exception
    {
      runnable.run(new SubProgressMonitor(monitor, 1));
    }

    private List<MirrorRunnable> getMirrorRunnables(Performer performer, File mirrorsDir, IProgressMonitor monitor)
        throws Exception
    {
      List<MirrorRunnable> runnables = new ArrayList<MirrorRunnable>();
      for (SetupTask task : performer.getTriggeredSetupTasks())
      {
        checkCancelation(monitor);

        MirrorRunnable runnable = task.mirror(performer, mirrorsDir, true);
        if (runnable != null)
        {
          runnables.add(runnable);
        }
      }

      return runnables;
    }

    private void checkCancelation(IProgressMonitor monitor)
    {
      if (monitor.isCanceled())
      {
        throw new OperationCanceledException();
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class Performer extends SetupTaskPerformer implements MirrorContext
    {
      private static final long serialVersionUID = 1L;

      private Performer() throws Exception
      {
        super(true);
      }

      public void addRedirection(String sourceURL, String targetURL)
      {
        System.out.println("Redirection: " + sourceURL + " --> " + targetURL);

        Setup setup = getOriginalSetup();
        CompoundSetupTask container = getContainer(setup);
        RedirectionTask redirection = getRedirection(container, sourceURL);
        redirection.setTargetURL(targetURL);

        Preferences preferences = setup.getPreferences();
        EMFUtil.saveEObject(preferences);
      }

      private RedirectionTask getRedirection(CompoundSetupTask container, String sourceURL)
      {
        EList<SetupTask> tasks = container.getSetupTasks();
        for (SetupTask task : tasks)
        {
          if (task instanceof RedirectionTask)
          {
            RedirectionTask redirection = (RedirectionTask)task;
            if (sourceURL.equals(redirection.getSourceURL()))
            {
              return redirection;
            }
          }
        }

        RedirectionTask redirection = SetupFactory.eINSTANCE.createRedirectionTask();
        redirection.setSourceURL(sourceURL);
        tasks.add(redirection);
        return redirection;
      }

      private CompoundSetupTask getContainer(Setup setup)
      {
        String name = "Mirror Configuration (" + get(SetupConstants.KEY_BRANCH_LABEL) + ")";

        EList<SetupTask> tasks = setup.getPreferences().getSetupTasks();
        Branch branch = setup.getBranch();

        for (SetupTask task : tasks)
        {
          if (task instanceof CompoundSetupTask)
          {
            CompoundSetupTask container = (CompoundSetupTask)task;
            if (name.equals(container.getName()) && container.getRestrictions().contains(branch))
            {
              return container;
            }
          }
        }

        CompoundSetupTask container = SetupFactory.eINSTANCE.createCompoundSetupTask();
        container.setName(name);
        container.getRestrictions().add(branch);

        tasks.add(container);
        return container;
      }
    }
  }
}
