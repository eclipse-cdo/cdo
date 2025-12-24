/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewTopLevelResourceNodeAction extends AbstractViewAction
{
  private CDOItemProvider itemProvider;

  private CDOResourceNode selectedNode;

  private Type type;

  private String resourceNodeName;

  public NewTopLevelResourceNodeAction(CDOItemProvider itemProvider, IWorkbenchPage page, CDOView view, CDOResourceNode node, Type type)
  {
    super(page, type.getTitle() + INTERACTIVE, type.getTooltip(), type.getImageDescriptor(), view);
    selectedNode = node;
    this.itemProvider = itemProvider;
    this.type = type;
  }

  @Override
  protected void preRun() throws Exception
  {
    String initialValue = (type == Type.FOLDER ? "folder" : "resource") + (AbstractViewAction.lastResourceNumber + 1);
    InputDialog dialog = new InputDialog(getShell(), getText(), Messages.getString("NewResourceNodeAction.8"), initialValue,
        new ResourceNodeNameInputValidator(selectedNode));

    if (dialog.open() == InputDialog.OK)
    {
      ++AbstractViewAction.lastResourceNumber;
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

    switch (type)
    {
    case FOLDER:
      node = EresourceFactory.eINSTANCE.createCDOResourceFolder();
      node.setName(resourceNodeName);
      if (selectedNode instanceof CDOResourceFolder)
      {
        ((CDOResourceFolder)selectedNode).getNodes().add(node);
      }
      else
      {
        ((CDOResource)selectedNode).getContents().add(node); // selectedNode is root resource
      }

      break;

    case MODEL:
      if (selectedNode instanceof CDOResourceFolder)
      {
        node = transaction.createResource(selectedNode.getPath() + "/" + resourceNodeName); //$NON-NLS-1$
      }
      else
      {
        node = transaction.createResource(resourceNodeName);
      }

      break;

    case TEXT:
      if (selectedNode instanceof CDOResourceFolder)
      {
        node = transaction.createTextResource(selectedNode.getPath() + "/" + resourceNodeName); //$NON-NLS-1$
      }
      else
      {
        node = transaction.createTextResource(resourceNodeName);
      }

      break;

    case BINARY:
      if (selectedNode instanceof CDOResourceFolder)
      {
        node = transaction.createBinaryResource(selectedNode.getPath() + "/" + resourceNodeName); //$NON-NLS-1$
      }
      else
      {
        node = transaction.createBinaryResource(resourceNodeName);
      }

      break;
    }

    transaction.commit();

    itemProvider.refreshViewer(true);
    itemProvider.selectElement(node, true);

    if (type == Type.MODEL)
    {
      String resourcePath = node.getPath();
      CDOEditorUtil.openEditor(getPage(), transaction, resourcePath);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static enum Type
  {
    FOLDER(Messages.getString("Title.Folder"), Messages.getString("Tooltip.Folder"), SharedIcons.getDescriptor(SharedIcons.ETOOL_NEW_RESOURCE_FOLDER)),

    MODEL(Messages.getString("Title.Model"), Messages.getString("Tooltip.Model"), SharedIcons.getDescriptor(SharedIcons.ETOOL_NEW_RESOURCE)),

    TEXT(Messages.getString("Title.Text"), Messages.getString("Tooltip.Text"), SharedIcons.getDescriptor(SharedIcons.ETOOL_NEW_TEXT_RESOURCE)),

    BINARY(Messages.getString("Title.Binary"), Messages.getString("Tooltip.Binary"), SharedIcons.getDescriptor(SharedIcons.ETOOL_NEW_BINARY_RESOURCE));

    private String title;

    private String tooltip;

    private ImageDescriptor imageDescriptor;

    private Type(String title, String tooltip, ImageDescriptor imageDescriptor)
    {
      this.title = title;
      this.tooltip = tooltip;
      this.imageDescriptor = imageDescriptor;
    }

    public String getTitle()
    {
      return title;
    }

    public String getTooltip()
    {
      return tooltip;
    }

    public ImageDescriptor getImageDescriptor()
    {
      return imageDescriptor;
    }
  }
}
