/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.admin.CDOAdminClientManager;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.ui.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class CDOAdminView extends ContainerView
{
  public final static String ID = "org.eclipse.emf.cdo.ui.admin.CDOAdminView"; //$NON-NLS-1$

  private final CDOAdminClientManager adminManager = OM.getAdminManager();

  private Image connectionImage = org.eclipse.net4j.ui.shared.SharedIcons
      .getImage(org.eclipse.net4j.ui.shared.SharedIcons.OBJ_CONNECTOR);

  private Image repositoryImage = SharedIcons.getImage(SharedIcons.OBJ_REPO);

  private IAction addConnectionAction;

  public CDOAdminView()
  {
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return adminManager;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new ContainerItemProvider<IContainer<Object>>()
    {
      @Override
      public String getText(Object obj)
      {
        if (obj instanceof CDOAdminClient)
        {
          CDOAdminClient connection = (CDOAdminClient)obj;
          return connection.getURL();
        }

        if (obj instanceof CDOAdminRepository)
        {
          CDOAdminRepository repository = (CDOAdminRepository)obj;
          return repository.getName() + " [" + repository.getType() + ", " + repository.getState() + "]";
        }

        return super.getText(obj);
      }

      @Override
      public Image getImage(Object obj)
      {
        if (obj instanceof CDOAdminClient)
        {
          return connectionImage;
        }

        if (obj instanceof CDOAdminRepository)
        {
          return repositoryImage;
        }

        return super.getImage(obj);
      }

      @Override
      public Font getFont(Object obj)
      {
        if (isDisabled(obj))
        {
          return getItalicFont();
        }

        return super.getFont(obj);
      }

      @Override
      public Color getForeground(Object obj)
      {
        if (isDisabled(obj))
        {
          return getDisplay().getSystemColor(SWT.COLOR_GRAY);
        }

        return super.getForeground(obj);
      }

      private boolean isDisabled(Object obj)
      {
        if (obj instanceof CDOAdminClient)
        {
          CDOAdminClient admin = (CDOAdminClient)obj;
          return !admin.isConnected();
        }

        return false;
      }
    };
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);

    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof CDOAdminClient)
      {
        manager.add(new RemoveAction(adminManager, (CDOAdminClient)obj));
      }
    }
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    if (addConnectionAction == null)
    {
      addConnectionAction = new Action()
      {
        @Override
        public void run()
        {
          String lastURL = OM.getLastURL();
          InputDialog dialog = new InputDialog(getShell(), getText(), "Enter the connection URL:", lastURL, null);
          if (dialog.open() == InputDialog.OK)
          {
            String url = dialog.getValue();
            OM.setLastURL(url);
            adminManager.addConnection(url);
          }
        }
      };

      addConnectionAction.setText("Add Connection");
      addConnectionAction.setToolTipText("Add a new connection");
      addConnectionAction.setImageDescriptor(org.eclipse.net4j.ui.shared.SharedIcons
          .getDescriptor(org.eclipse.net4j.ui.shared.SharedIcons.ETOOL_ADD));
    }

    manager.add(addConnectionAction);
    super.fillLocalToolBar(manager);
  }

  /**
   * @author Eike Stepper
   */
  public static class RemoveAction extends org.eclipse.net4j.ui.Net4jItemProvider.RemoveAction
  {
    private CDOAdminClientManager adminManager;

    public RemoveAction(CDOAdminClientManager adminManager, CDOAdminClient admin)
    {
      super(admin);
      this.adminManager = adminManager;
    }

    public CDOAdminClientManager getAdminManager()
    {
      return adminManager;
    }

    @Override
    public CDOAdminClient getObject()
    {
      return (CDOAdminClient)super.getObject();
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      adminManager.removeConnection(getObject());
    }
  }
}
