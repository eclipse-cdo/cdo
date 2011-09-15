/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ecore.presentation;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewResourceWizardPage;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.presentation.EcoreEditorPlugin;
import org.eclipse.emf.ecore.presentation.EcoreModelWizard;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import java.util.Collections;

public class DawnEcoreModelWizard extends EcoreModelWizard
{
  private DawnCreateNewResourceWizardPage newResourceCreationPage;

  private CDOView view;

  private CDOResource resource;

  public DawnEcoreModelWizard()
  {
    super();
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOSession session = CDOConnectionUtil.instance.openSession();
    view = CDOConnectionUtil.instance.openView(session);
  }

  @Override
  public void addPages()
  {
    newResourceCreationPage = new DawnCreateNewResourceWizardPage("ecore", true, view);
    addPage(newResourceCreationPage);

    initialObjectCreationPage = new EcoreModelWizardInitialObjectCreationPage("Whatever2");
    initialObjectCreationPage.setTitle(EcoreEditorPlugin.INSTANCE.getString("_UI_EcoreModelWizard_label"));
    initialObjectCreationPage.setDescription(EcoreEditorPlugin.INSTANCE
        .getString("_UI_Wizard_initial_object_description"));
    addPage(initialObjectCreationPage);
  }

  @Override
  public boolean performFinish()
  {
    try
    {
      // Do the work within an operation.
      //
      WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
      {

        @Override
        protected void execute(IProgressMonitor progressMonitor)
        {
          try
          {
            ResourceSet resourceSet = new ResourceSetImpl();

            URI resourceURI = newResourceCreationPage.getURI();

            CDOTransaction transaction = CDOConnectionUtil.instance.openCurrentTransaction(resourceSet,
                resourceURI.toString());

            resource = transaction.getOrCreateResource(resourceURI.path());

            EObject rootObject = createInitialModel();
            if (rootObject != null)
            {
              resource.getContents().add(rootObject);
            }

            resource.save(Collections.EMPTY_MAP);
            transaction.close();
          }
          catch (Exception exception)
          {
            EcoreEditorPlugin.INSTANCE.log(exception);
            throw new RuntimeException(exception);
          }
          finally
          {
            progressMonitor.done();
          }
        }
      };

      getContainer().run(false, false, operation);

      openEditor(newResourceCreationPage.getURI());

      return true;
    }
    catch (Exception exception)
    {
      EcoreEditorPlugin.INSTANCE.log(exception);
      return false;
    }
  }

  private void openEditor(URI uri)
  {
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage page = workbenchWindow.getActivePage();
    DawnEditorInput dawnEditorInput = new DawnEditorInput(uri);
    try
    {
      page.openEditor(dawnEditorInput, DawnEcoreEditor.ID);
    }
    catch (PartInitException exception)
    {
      MessageDialog.openError(workbenchWindow.getShell(),
          EcoreEditorPlugin.INSTANCE.getString("_UI_OpenEditorError_label"), exception.getMessage());
      throw new RuntimeException(exception);
    }
  }
}
