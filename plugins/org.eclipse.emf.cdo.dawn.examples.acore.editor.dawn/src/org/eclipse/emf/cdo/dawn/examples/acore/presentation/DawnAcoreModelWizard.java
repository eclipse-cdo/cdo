/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.presentation;

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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import java.util.Collections;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreModelWizard extends AcoreModelWizard implements INewWizard
{
  private DawnCreateNewResourceWizardPage newResourceCreationPage;

  private CDOView view;

  private CDOResource resource;

  public DawnAcoreModelWizard()
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
    newResourceCreationPage = new DawnCreateNewResourceWizardPage("acore", true, view);
    addPage(newResourceCreationPage);

    initialObjectCreationPage = new AcoreModelWizardInitialObjectCreationPage("Whatever2");
    initialObjectCreationPage.setTitle(AcoreEditorPlugin.INSTANCE.getString("_UI_AcoreModelWizard_label"));
    initialObjectCreationPage.setDescription(AcoreEditorPlugin.INSTANCE
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

            // Save the contents of the resource to the file system.
            //
            // Map<Object, Object> options = new HashMap<Object, Object>();
            // options.put(XMLResource.OPTION_ENCODING, initialObjectCreationPage.getEncoding());
            // resource.save(options);
            resource.save(Collections.EMPTY_MAP);
            transaction.close();
          }
          catch (Exception exception)
          {
            AcoreEditorPlugin.INSTANCE.log(exception);
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
      AcoreEditorPlugin.INSTANCE.log(exception);
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
      page.openEditor(dawnEditorInput, DawnAcoreEditor.ID);
    }
    catch (PartInitException exception)
    {
      MessageDialog.openError(workbenchWindow.getShell(),
          AcoreEditorPlugin.INSTANCE.getString("_UI_OpenEditorError_label"), exception.getMessage());
      throw new RuntimeException(exception);
    }
  }
}
