/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.part;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import java.lang.reflect.InvocationTargetException;

/**
 * @generated
 */
public class AcoreCreationWizard extends Wizard implements INewWizard
{

  /**
   * @generated
   */
  private IWorkbench workbench;

  /**
   * @generated
   */
  protected IStructuredSelection selection;

  /**
   * @generated
   */
  protected AcoreCreationWizardPage diagramModelFilePage;

  /**
   * @generated
   */
  protected AcoreCreationWizardPage domainModelFilePage;

  /**
   * @generated
   */
  protected Resource diagram;

  /**
   * @generated
   */
  private boolean openNewlyCreatedDiagramEditor = true;

  /**
   * @generated
   */
  public IWorkbench getWorkbench()
  {
    return workbench;
  }

  /**
   * @generated
   */
  public IStructuredSelection getSelection()
  {
    return selection;
  }

  /**
   * @generated
   */
  public final Resource getDiagram()
  {
    return diagram;
  }

  /**
   * @generated
   */
  public final boolean isOpenNewlyCreatedDiagramEditor()
  {
    return openNewlyCreatedDiagramEditor;
  }

  /**
   * @generated
   */
  public void setOpenNewlyCreatedDiagramEditor(boolean openNewlyCreatedDiagramEditor)
  {
    this.openNewlyCreatedDiagramEditor = openNewlyCreatedDiagramEditor;
  }

  /**
   * @generated
   */
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    this.workbench = workbench;
    this.selection = selection;
    setWindowTitle(Messages.AcoreCreationWizardTitle);
    setDefaultPageImageDescriptor(AcoreDiagramEditorPlugin.getBundledImageDescriptor("icons/wizban/NewAcoreWizard.gif")); //$NON-NLS-1$
    setNeedsProgressMonitor(true);
  }

  /**
   * @generated
   */
  @Override
  public void addPages()
  {
    diagramModelFilePage = new AcoreCreationWizardPage("DiagramModelFile", getSelection(), "acore_diagram"); //$NON-NLS-1$ //$NON-NLS-2$
    diagramModelFilePage.setTitle(Messages.AcoreCreationWizard_DiagramModelFilePageTitle);
    diagramModelFilePage.setDescription(Messages.AcoreCreationWizard_DiagramModelFilePageDescription);
    addPage(diagramModelFilePage);

    domainModelFilePage = new AcoreCreationWizardPage("DomainModelFile", getSelection(), "acore") //$NON-NLS-1$ //$NON-NLS-2$
    {

      @Override
      public void setVisible(boolean visible)
      {
        if (visible)
        {
          String fileName = diagramModelFilePage.getFileName();
          fileName = fileName.substring(0, fileName.length() - ".acore_diagram".length()); //$NON-NLS-1$
          setFileName(AcoreDiagramEditorUtil.getUniqueFileName(getContainerFullPath(), fileName, "acore")); //$NON-NLS-1$
        }
        super.setVisible(visible);
      }
    };
    domainModelFilePage.setTitle(Messages.AcoreCreationWizard_DomainModelFilePageTitle);
    domainModelFilePage.setDescription(Messages.AcoreCreationWizard_DomainModelFilePageDescription);
    addPage(domainModelFilePage);
  }

  /**
   * @generated
   */
  @Override
  public boolean performFinish()
  {
    IRunnableWithProgress op = new WorkspaceModifyOperation(null)
    {

      @Override
      protected void execute(IProgressMonitor monitor) throws CoreException, InterruptedException
      {
        diagram = AcoreDiagramEditorUtil.createDiagram(diagramModelFilePage.getURI(), domainModelFilePage.getURI(), monitor);
        if (isOpenNewlyCreatedDiagramEditor() && diagram != null)
        {
          try
          {
            AcoreDiagramEditorUtil.openDiagram(diagram);
          }
          catch (PartInitException e)
          {
            ErrorDialog.openError(getContainer().getShell(), Messages.AcoreCreationWizardOpenEditorError, null, e.getStatus());
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
        ErrorDialog.openError(getContainer().getShell(), Messages.AcoreCreationWizardCreationError, null, ((CoreException)e.getTargetException()).getStatus());
      }
      else
      {
        AcoreDiagramEditorPlugin.getInstance().logError("Error creating diagram", e.getTargetException()); //$NON-NLS-1$
      }
      return false;
    }
    return diagram != null;
  }
}
