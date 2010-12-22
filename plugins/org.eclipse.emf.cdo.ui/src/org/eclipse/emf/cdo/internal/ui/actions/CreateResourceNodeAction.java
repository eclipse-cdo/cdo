/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class CreateResourceNodeAction extends ViewAction
{
  private static final String TITLE_RESOURCE = Messages.getString("CreateResourceAction.0"); //$NON-NLS-1$

  private static final String TITLE_FOLDER = Messages.getString("CreateResourceNodeAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP_RESOURCE = Messages.getString("CreateResourceAction.1"); //$NON-NLS-1$

  private static final String TOOL_TIP_FOLDER = Messages.getString("CreateResourceNodeAction.1"); //$NON-NLS-1$

  private CDOItemProvider itemProvider;

  private CDOResourceNode selectedNode;

  private boolean createFolder;

  private String resourceNodeName;

  public CreateResourceNodeAction(CDOItemProvider itemProvider, IWorkbenchPage page, CDOView view,
      CDOResourceNode node, boolean createFolder)
  {
    super(page, createFolder ? TITLE_FOLDER + INTERACTIVE : TITLE_RESOURCE + INTERACTIVE,
        createFolder ? TOOL_TIP_FOLDER : TOOL_TIP_RESOURCE, null, view);
    selectedNode = node;
    this.itemProvider = itemProvider;
    this.createFolder = createFolder;
  }

  @Override
  protected void preRun() throws Exception
  {
    InputDialog dialog = new InputDialog(getShell(), createFolder ? TITLE_FOLDER : TITLE_RESOURCE,
        createFolder ? "Enter folder name" : Messages.getString("CreateResourceAction.2"), "res" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            + (ViewAction.lastResourceNumber + 1), new ResourceNodeNameInputValidator(selectedNode));

    if (dialog.open() == InputDialog.OK)
    {
      ++ViewAction.lastResourceNumber;
      resourceNodeName = dialog.getValue();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOTransaction transaction = getTransaction();
    CDOResourceNode node = null;

    if (createFolder)
    {
      node = EresourceFactory.eINSTANCE.createCDOResourceFolder();
      node.setName(resourceNodeName);
      if (selectedNode instanceof CDOResourceFolder)
      {
        ((CDOResourceFolder)selectedNode).getNodes().add(node);
      }
      else
      {
        ((CDOResource)selectedNode).getContents().add(node);
      }
    }
    else
    {
      if (selectedNode instanceof CDOResourceFolder)
      {
        node = transaction.createResource(selectedNode.getPath() + "/" + resourceNodeName); //$NON-NLS-1$
      }
      else
      {
        node = transaction.createResource(resourceNodeName);
      }
    }

    transaction.commit();

    itemProvider.refreshViewer(true);
    itemProvider.selectElement(node, true);
  }
}
