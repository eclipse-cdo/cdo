/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.ide.Node.ResourcesNode;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Victor Roldan Betancort
 */
public class NewResourceAction implements IObjectActionDelegate
{
  private Object node;

  private Shell shell;

  private String resourceName;

  public NewResourceAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    node = UIUtil.getElement(selection);
  }

  public void run(IAction action)
  {
    Job job = new Job("Creating CDO resource")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          String path = "";
          CDOTransaction transaction = null;
          if (node instanceof ResourcesNode)
          {
            transaction = ((ResourcesNode)node).getRepositoryProject().getView().getSession().openTransaction();
          }
          else if (node instanceof CDOResourceFolder)
          {
            transaction = ((CDOResourceFolder)node).cdoView().getSession().openTransaction();
            path = ((CDOResourceFolder)node).getPath();
          }
          else
          {
            // TODO Vik: No exception?
            OM.LOG.error("Unrecognized selection type in " + NewResourceAction.class.getName().toString());
          }

          transaction.getOrCreateResource(path + "/" + resourceName);
          transaction.commit();
          transaction.close();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          return new Status(IStatus.ERROR, NewResourceAction.class.toString(), "Could not create CDO resource", ex);
        }

        return Status.OK_STATUS;
      }
    };

    InputDialog dialog = new InputDialog(shell, "Create a new CDO resource", "Enter the resource name", "Resource",
        null);
    if (dialog.open() == Dialog.OK)
    {
      resourceName = dialog.getValue();
      job.schedule();
    }
  }
}
