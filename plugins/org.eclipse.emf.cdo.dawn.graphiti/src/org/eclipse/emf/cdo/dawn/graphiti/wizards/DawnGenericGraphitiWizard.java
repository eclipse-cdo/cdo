/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.wizards;

import org.eclipse.emf.cdo.dawn.graphiti.DawnGraphitiUIPlugin;
import org.eclipse.emf.cdo.dawn.graphiti.editors.DawnGraphitiDiagramEditor;
import org.eclipse.emf.cdo.dawn.graphiti.editors.DawnGraphitiDiagramEditorFactory;
import org.eclipse.emf.cdo.dawn.graphiti.editors.DawnGraphitiEditorInput;
import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite.ResourceChooserValidator;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewDiagramResourceWizardPage;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewResourceWizardPage;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.io.IOException;

/**
 * @author Martin Fluegge
 */
public class DawnGenericGraphitiWizard extends Wizard implements INewWizard
{
  private CDOView view;

  private DawnGraphitiyDiagramTypeSelectionWizardPage dawnSelectModelPage;

  private DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage;

  private DawnCreateNewResourceWizardPage dawnDomainModelFilePage;

  public DawnGenericGraphitiWizard()
  {
    super();
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOSession session = CDOConnectionUtil.instance.openSession();
    view = CDOConnectionUtil.instance.openView(session);
  }

  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
  }

  @Override
  public void addPages()
  {
    dawnSelectModelPage = new DawnGraphitiyDiagramTypeSelectionWizardPage("title");
    addPage(dawnSelectModelPage);

    dawnDiagramModelFilePage = new DawnCreateNewDiagramResourceWizardPage("acore_graphiti", false, view)
    {
      @Override
      public void setVisible(boolean visible)
      {
        if (visible)
        {
          String extension = dawnSelectModelPage.getText();

          URI uri = dawnDiagramModelFilePage.getURI();
          String fileName = uri.lastSegment();
          fileName = fileName.substring(0, fileName.length() - ("." + extension + "_graphiti").length()); //$NON-NLS-1$
          fileName += "." + extension + "_graphiti";
          dawnDomainModelFilePage.setResourceNamePrefix(fileName);
          dawnDomainModelFilePage.setResourcePath(dawnDiagramModelFilePage.getResourcePath());
        }
        super.setVisible(visible);
      }
    };

    dawnDiagramModelFilePage.setTitle("");
    dawnDiagramModelFilePage.setDescription("");
    dawnDiagramModelFilePage.setCreateAutomaticResourceName(true);
    addPage(dawnDiagramModelFilePage);

    dawnDomainModelFilePage = new DawnCreateNewResourceWizardPage("acore", true, view)
    {
      @Override
      public void setVisible(boolean visible)
      {
        if (visible)
        {
          String extension = dawnSelectModelPage.getText();
          URI uri = dawnDiagramModelFilePage.getURI();
          String fileName = uri.lastSegment();
          fileName = fileName.substring(0, fileName.length() - ("." + extension + "_graphiti").length()); //$NON-NLS-1$
          fileName += "." + extension;
          dawnDomainModelFilePage.setResourceNamePrefix(fileName);
          dawnDomainModelFilePage.setResourcePath(dawnDiagramModelFilePage.getResourcePath());
        }
        super.setVisible(visible);
      }
    };
    dawnDomainModelFilePage.setTitle("");
    dawnDomainModelFilePage.setDescription("");

    // allows to connect to an existing resource
    dawnDomainModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_WARN);
    addPage(dawnDomainModelFilePage);
  }

  @Override
  public boolean performFinish()
  {
    String diagramTypeId = dawnSelectModelPage.getText();

    URI diagramResourceURI = dawnDiagramModelFilePage.getURI();
    @SuppressWarnings("unused")
    URI domainModelResourceURI = dawnDomainModelFilePage.getURI();

    Diagram diagram = Graphiti.getPeCreateService()
        .createDiagram(diagramTypeId, diagramResourceURI.lastSegment(), true);

    String editorID = DawnGraphitiDiagramEditor.ID;
    // String diagramTypeProviderId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagramTypeId);

    TransactionalEditingDomain domain = createEditingDomain(diagramResourceURI, diagram);

    Resource resource = createResource(diagramResourceURI, diagram, domain);

    String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
    DiagramEditorInput editorInput = new DawnGraphitiEditorInput(EcoreUtil.getURI(diagram), domain, providerId, true,
        resource);

    try
    {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, editorID);
    }
    catch (PartInitException e)
    {
      String error = "Error opening diagram";
      IStatus status = new Status(IStatus.ERROR, DawnGraphitiUIPlugin.ID, error, e);
      ErrorDialog.openError(getShell(), "Error", null, status);
      return false;
    }

    return true;
  }

  private TransactionalEditingDomain createEditingDomain(URI diagramResourceURI, final Diagram diagram)
  {
    // Create a resource set and EditingDomain
    final TransactionalEditingDomain editingDomain = DawnGraphitiDiagramEditorFactory
        .createResourceSetAndEditingDomain();

    // save(editingDomain, Collections.<Resource, Map<?, ?>> emptyMap());
    return editingDomain;
  }

  private Resource createResource(URI diagramResourceURI, final Diagram diagram,
      final TransactionalEditingDomain editingDomain)
  {
    final ResourceSet resourceSet = editingDomain.getResourceSet();
    // Create a resource for this file.
    final Resource resource = resourceSet.createResource(diagramResourceURI);
    final CommandStack commandStack = editingDomain.getCommandStack();
    commandStack.execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        // resource.setTrackingModification(true);
        resource.getContents().add(diagram);
      }
    });

    try
    {
      resource.save(null);
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    return resource;
  }

  @Override
  public void dispose()
  {
    view.close();
  }
}
