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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
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
public class NewResourceFolderAction implements IObjectActionDelegate
{
  private Object node;

  private Shell shell;

  private String folderName;

  public NewResourceFolderAction()
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
    Job job = new Job("Creating CDO folder")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          CDOObject parentObject = null;
          CDOTransaction transaction = null;

          if (node instanceof ResourcesNode)
          {
            transaction = ((ResourcesNode)node).getRepositoryProject().getView().getSession().openTransaction();
            parentObject = transaction.getRootResource();
          }
          else if (node instanceof CDOResourceFolder)
          {
            transaction = ((CDOResourceFolder)node).cdoView().getSession().openTransaction();
            parentObject = (CDOObject)node;
          }
          else
          {
            // TODO Vik: No exception?
            OM.LOG.error("Unrecognized selection type in " + NewResourceFolderAction.class.getName().toString());
          }

          CDOResourceFolder folder = EresourceFactory.eINSTANCE.createCDOResourceFolder();
          CDOObject parent = transaction.getObject(parentObject.cdoID());
          if (parent instanceof CDOResource)
          {
            ((CDOResource)parent).getContents().add(folder);
            folder.setPath(((CDOResource)parent).getPath() + "/" + folderName);
          }
          else if (parent instanceof CDOResourceFolder)
          {
            ((CDOResourceFolder)parent).getNodes().add(folder);
            folder.setPath(((CDOResourceFolder)parent).getPath() + "/" + folderName);
          }

          folder.setName(folderName);
          transaction.commit();
          transaction.close();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          return new Status(IStatus.ERROR, NewResourceFolderAction.class.toString(),
              "Could not create CDO resource folder", ex);
        }

        return Status.OK_STATUS;
      }
    };

    InputDialog dialog = new InputDialog(shell, "Create a new CDO folder", "Enter the folder name", "folder", null);
    if (dialog.open() == Dialog.OK)
    {
      folderName = dialog.getValue();
      job.schedule();
    }
  }
}
