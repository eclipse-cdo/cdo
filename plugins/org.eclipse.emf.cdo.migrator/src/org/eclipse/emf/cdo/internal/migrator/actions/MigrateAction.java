/*
 * Copyright (c) 2008-2012, 2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.actions;

import org.eclipse.emf.cdo.internal.migrator.CDOMigratorUtil;
import org.eclipse.emf.cdo.internal.migrator.messages.Messages;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class MigrateAction implements IObjectActionDelegate
{
  private ISelection selection;

  public MigrateAction()
  {
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  @Override
  public void run(IAction action)
  {
    new Job(Messages.getString("MigrateAction_0")) //$NON-NLS-1$
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          IFile file = getFile();
          if (file == null)
          {
            showMessage(Messages.getString("MigrateAction_1"), true); //$NON-NLS-1$
          }
          else
          {
            GenModel genModel = getGenModel(file);
            if (genModel == null)
            {
              showMessage(Messages.getString("MigrateAction_2"), true); //$NON-NLS-1$
            }
            else
            {
              GenDelegationKind featureDelegation = getFeatureDelegation();
              String msg = CDOMigratorUtil.adjustGenModel(genModel, featureDelegation);
              if (msg == null)
              {
                showMessage(Messages.getString("MigrateAction_3"), false); //$NON-NLS-1$
              }
              else
              {
                genModel.eResource().save(null);
                showMessage(Messages.getString("MigrateAction_4") + msg, false); //$NON-NLS-1$
              }
            }
          }
        }
        catch (Exception ex)
        {
          return new Status(IStatus.ERROR, "org.eclipse.emf.cdo.internal.migrator", //$NON-NLS-1$
              Messages.getString("MigrateAction_6"), ex); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }

  protected GenDelegationKind getFeatureDelegation()
  {
    return GenDelegationKind.REFLECTIVE_LITERAL;
  }

  protected IFile getFile()
  {
    if (selection instanceof IStructuredSelection)
    {
      Object element = ((IStructuredSelection)selection).getFirstElement();
      if (element instanceof IFile)
      {
        IFile file = (IFile)element;
        if ("genmodel".equals(file.getFileExtension())) //$NON-NLS-1$
        {
          return file;
        }
      }
    }

    return null;
  }

  protected GenModel getGenModel(IFile file)
  {
    String path = file.getFullPath().toString();
    return CDOMigratorUtil.getGenModel(path);
  }

  protected void showMessage(final String msg, final boolean error)
  {
    try
    {
      final Display display = PlatformUI.getWorkbench().getDisplay();
      display.syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            if (error)
            {
              MessageDialog.openError(UIUtil.getShell(), Messages.getString("MigrateAction_10"), msg); //$NON-NLS-1$
            }
            else
            {
              MessageDialog.openInformation(UIUtil.getShell(), Messages.getString("MigrateAction_10"), msg); //$NON-NLS-1$
            }
          }
          catch (RuntimeException ignore)
          {
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
    }
  }
}
