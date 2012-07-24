/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.digest.ui;

import org.eclipse.emf.cdo.releng.version.Release;
import org.eclipse.emf.cdo.releng.version.ReleaseManager;
import org.eclipse.emf.cdo.releng.version.digest.DigestValidator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CreateDigestAction implements IObjectActionDelegate
{
  private Shell shell;

  private ISelection selection;

  public CreateDigestAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  public void run(IAction action)
  {
    try
    {
      final IFile file = (IFile)((IStructuredSelection)selection).getFirstElement();
      final Release release = ReleaseManager.INSTANCE.getRelease(file);
      final IFile target = DigestValidator.getDigestFile(file.getFullPath());

      new Job("Create digest")
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          try
          {
            // TODO Determine validator class from .project
            DigestValidator validator = new DigestValidator.BuildModel();

            List<String> warnings = new ArrayList<String>();
            DigestValidator.createDigest(validator, release, target, warnings, monitor);

            if (!warnings.isEmpty())
            {
              final StringBuilder builder = new StringBuilder("The following problems occured:\n");
              for (String warning : warnings)
              {
                builder.append("\n");
                builder.append(warning);
              }

              shell.getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  MessageDialog.openWarning(shell, "CDO Release Engineering Version Tool", builder.toString());
                }
              });
            }

            return Status.OK_STATUS;
          }
          catch (CoreException ex)
          {
            return ex.getStatus();
          }
        }
      }.schedule();
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      MessageDialog.openError(shell, "CDO Release Engineering Version Tool",
          "An error occured. Consult the error log for details.");
    }
  }

}
