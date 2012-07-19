/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.part;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite.ResourceChooserValidator;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewDiagramResourceWizardPage;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewResourceWizardPage;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreCreationWizard extends AcoreCreationWizard
{
  private CDOView view;

  private DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage;

  private DawnCreateNewResourceWizardPage dawnDomainModelFilePage;

  public DawnAcoreCreationWizard()
  {
    super();
    // CDOConnectionUtil.instance.init("repo1", "tcp", "localhost");
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOSession session = CDOConnectionUtil.instance.openSession();
    view = CDOConnectionUtil.instance.openView(session);
  }

  @Override
  public boolean performFinish()
  {
    IRunnableWithProgress op = new WorkspaceModifyOperation(null)
    {
      @Override
      protected void execute(IProgressMonitor monitor) throws CoreException, InterruptedException
      {
        URI diagramResourceURI = dawnDiagramModelFilePage.getURI();
        URI domainModelResourceURI = dawnDomainModelFilePage.getURI();

        diagram = DawnAcoreDiagramEditorUtil.createDiagram(diagramResourceURI, domainModelResourceURI, monitor);

        if (isOpenNewlyCreatedDiagramEditor() && diagram != null)
        {
          try
          {
            DawnAcoreDiagramEditorUtil.openDiagram(diagram);
          }
          catch (PartInitException e)
          {
            ErrorDialog.openError(getContainer().getShell(), Messages.AcoreCreationWizardOpenEditorError, null,
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
        ErrorDialog.openError(getContainer().getShell(), Messages.AcoreCreationWizardCreationError, null,
            ((CoreException)e.getTargetException()).getStatus());
      }
      else
      {
        AcoreDiagramEditorPlugin.getInstance().logError("Error creating diagram", e.getTargetException()); //$NON-NLS-1$
      }
      return false;
    }
    return diagram != null;
  }

  @Override
  public void addPages()
  {
    dawnDiagramModelFilePage = new DawnCreateNewDiagramResourceWizardPage("acore_diagram", false, view);
    dawnDiagramModelFilePage.setTitle(Messages.AcoreCreationWizard_DiagramModelFilePageTitle);
    dawnDiagramModelFilePage.setDescription(Messages.AcoreCreationWizard_DiagramModelFilePageDescription);
    dawnDiagramModelFilePage.setCreateAutomaticResourceName(true);
    addPage(dawnDiagramModelFilePage);

    dawnDomainModelFilePage = new DawnCreateNewResourceWizardPage("acore", true, view)
    {
      @Override
      public void setVisible(boolean visible)
      {
        if (visible)
        {
          URI uri = dawnDiagramModelFilePage.getURI();
          String fileName = uri.lastSegment();
          fileName = fileName.substring(0, fileName.length() - ".acore_diagram".length()); //$NON-NLS-1$
          fileName += ".acore";
          dawnDomainModelFilePage.setResourceNamePrefix(fileName);
          dawnDomainModelFilePage.setResourcePath(dawnDiagramModelFilePage.getResourcePath());
        }
        super.setVisible(visible);
      }
    };
    dawnDomainModelFilePage.setTitle(Messages.AcoreCreationWizard_DomainModelFilePageTitle);
    dawnDomainModelFilePage.setDescription(Messages.AcoreCreationWizard_DomainModelFilePageDescription);

    // allows to connect to an existing resource
    dawnDomainModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_WARN);
    addPage(dawnDomainModelFilePage);
  }

  @Override
  public void dispose()
  {
    view.close();
  }
}
