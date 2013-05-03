/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.part;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecoretools.diagram.part.EcoreCreationWizard;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditor;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorUtil;
import org.eclipse.emf.ecoretools.diagram.part.Messages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.FileEditorInput;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Martin Fluegge
 */
public class DawnEcoreCreationWizard extends EcoreCreationWizard
{
  private CDOView view;

  private DawnEcoreCreationWizardPage diagPage;

  public DawnEcoreCreationWizard()
  {
    super();
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOSession session = CDOConnectionUtil.instance.openSession();
    view = session.openTransaction();
  }

  @Override
  public boolean performFinish()
  {
    IRunnableWithProgress op = new WorkspaceModifyOperation(null)
    {
      @Override
      protected void execute(IProgressMonitor monitor) throws CoreException, InterruptedException
      {
        if (diagPage.isNewModel())
        {
          diagram = EcoreDiagramEditorUtil.createDiagram(diagPage.getDiagramModelURI(), diagPage.getDomainModelURI(),
              monitor);
        }
        else
        {
          diagram = DawnEcoreDiagramEditorUtil.createDiagramOnly(diagPage.getDiagramModelURI(),
              diagPage.getDomainModelURI(), diagPage.getDiagramEObject(), diagPage.isInitialized(), monitor);
        }
        if (diagram != null)
        {
          try
          {
            DawnEcoreDiagramEditorUtil.openDiagram(diagram);
          }
          catch (PartInitException e)
          {
            ErrorDialog.openError(getContainer().getShell(), Messages.EcoreCreationWizardOpenEditorError, null,
                e.getStatus());
          }
        }
      }
    };
    try
    {
      getContainer().run(false, true, op);
    }
    catch (InterruptedException e)
    {
      return false;
    }
    catch (InvocationTargetException e)
    {
      if (e.getTargetException() instanceof CoreException)
      {
        ErrorDialog.openError(getContainer().getShell(), Messages.EcoreCreationWizardCreationError, null,
            ((CoreException)e.getTargetException()).getStatus());
      }
      else
      {
        EcoreDiagramEditorPlugin.getInstance().logError("Error creating diagram", e.getTargetException()); //$NON-NLS-1$
      }
      return false;
    }
    return diagram != null;
  }

  @Override
  public void addPages()
  {
    diagPage = new DawnEcoreCreationWizardPage("NewEcoreToolsDiagram", getSelection(), view); //$NON-NLS-1$
    diagPage.setTitle(Messages.EcoreCreationWizard_DiagramModelFilePageTitle);
    diagPage.setDescription(Messages.EcoreCreationWizard_DiagramModelFilePageDescription);
    addPage(diagPage);
  }

  @Override
  public void dispose()
  {
    view.close();
  }

  @Override
  public boolean canFinish()
  {
    return true;
  }

  public static boolean openDiagram(Resource diagram) throws PartInitException
  {
    String path = diagram.getURI().toPlatformString(true);
    IResource workspaceResource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
    if (workspaceResource instanceof IFile)
    {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      return null != page.openEditor(new FileEditorInput((IFile)workspaceResource), EcoreDiagramEditor.ID);
    }
    return false;
  }
}
