/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.wizards;

import org.eclipse.emf.cdo.internal.team.RepositoryManager;
import org.eclipse.emf.cdo.internal.team.RepositoryTeamProvider;
import org.eclipse.emf.cdo.internal.ui.perspectives.CDOExplorerPerspective;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.TeamException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * @author Victor Roldan Betancort
 */
public class CDOProjectCreationWizard extends Wizard implements IWorkbenchWizard
{
  private WizardNewProjectCreationPage projectCreationPage;

  private CDOShareProjectWizardPage sharePage;

  // Cache of newly-created project
  private IProject newProject;

  private IWorkbench workbench;

  public CDOProjectCreationWizard()
  {
  }

  @Override
  public boolean performFinish()
  {
    createNewProject();
    if (newProject == null)
    {
      return false;
    }

    try
    {
      RepositoryTeamProvider.mapProject(newProject, sharePage.getSessionDescription());
    }
    catch (TeamException ex)
    {
      OM.LOG.equals(ex);
      return false;
    }

    RepositoryManager.INSTANCE.addElement(newProject);
    if (!CDOExplorerPerspective.isCurrent())
    {
      openCDOExplorerPerspective(workbench);
    }

    return true;
  }

  @Override
  public void addPages()
  {
    super.addPages();
    projectCreationPage = new WizardNewProjectCreationPage("basicNewProjectPage"); //$NON-NLS-1$
    projectCreationPage.setTitle(Messages.getString("CDOProjectCreationWizard.4")); //$NON-NLS-1$
    projectCreationPage.setDescription(Messages.getString("CDOProjectCreationWizard.2")); //$NON-NLS-1$
    super.addPage(projectCreationPage);

    sharePage = new CDOShareProjectWizardPage("shareProject"); //$NON-NLS-1$
    sharePage.setTitle(Messages.getString("TeamConfigurationWizard_2")); //$NON-NLS-1$
    super.addPage(sharePage);
  }

  public void init(IWorkbench workbench, IStructuredSelection currentSelection)
  {
    this.workbench = workbench;
    setNeedsProgressMonitor(true);
    setWindowTitle(Messages.getString("CDOProjectCreationWizard.4")); //$NON-NLS-1$
    initializeDefaultPageImageDescriptor();
  }

  protected void initializeDefaultPageImageDescriptor()
  {
    ImageDescriptor desc = OM.getImageDescriptor("icons/full/wizban/wizard_icon.gif");//$NON-NLS-1$
    setDefaultPageImageDescriptor(desc);
  }

  private IProject createNewProject()
  {
    if (newProject != null)
    {
      return newProject;
    }

    // get a project handle
    final IProject newProjectHandle = projectCreationPage.getProjectHandle();

    // get a project descriptor
    URI location = null;
    if (!projectCreationPage.useDefaults())
    {
      location = projectCreationPage.getLocationURI();
    }

    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
    description.setLocationURI(location);

    // create the new project operation
    IRunnableWithProgress op = new IRunnableWithProgress()
    {
      public void run(IProgressMonitor monitor) throws InvocationTargetException
      {
        CreateProjectOperation op = new CreateProjectOperation(description,
            Messages.getString("CDOProjectCreationWizard.0")); //$NON-NLS-1$
        try
        {
          op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
        }
        catch (ExecutionException e)
        {
          throw new InvocationTargetException(e);
        }
      }
    };

    // run the new project creation operation
    try
    {
      getContainer().run(true, true, op);
    }
    catch (InterruptedException e)
    {
      return null;
    }

    catch (InvocationTargetException e)
    {
      Throwable t = e.getTargetException();
      if (t instanceof ExecutionException && t.getCause() instanceof CoreException)
      {
        CoreException cause = (CoreException)t.getCause();
        StatusAdapter status;
        if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS)
        {
          status = new StatusAdapter(new Status(IStatus.WARNING, OM.BUNDLE_ID, cause.getMessage(), cause));
        }
        else
        {
          status = new StatusAdapter(new Status(cause.getStatus().getSeverity(), OM.BUNDLE_ID, cause.getMessage(),
              cause));
        }

        status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.getString("CDOProjectCreationWizard.1")); //$NON-NLS-1$
        StatusManager.getManager().handle(status, StatusManager.BLOCK);
      }
      else
      {
        StatusAdapter status = new StatusAdapter(new Status(IStatus.WARNING, OM.BUNDLE_ID, t.getMessage(), t));
        status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.getString("CDOProjectCreationWizard.1")); //$NON-NLS-1$
        StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
      }

      return null;
    }

    newProject = newProjectHandle;
    return newProject;
  }

  /**
   * Opens CDO Explorer perspective
   */
  public void openCDOExplorerPerspective(final IWorkbench workbench)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          boolean result = MessageDialog.openQuestion(getShell(), Messages.getString("CDOProjectCreationWizard.5"), //$NON-NLS-1$
              Messages.getString("CDOProjectCreationWizard.6")); //$NON-NLS-1$
          if (result)
          {
            workbench.showPerspective(CDOExplorerPerspective.ID, workbench.getActiveWorkbenchWindow());
          }
        }
        catch (WorkbenchException ex)
        {
          OM.LOG.error(ex);
          // ignore
        }
      }
    });
  }
}
