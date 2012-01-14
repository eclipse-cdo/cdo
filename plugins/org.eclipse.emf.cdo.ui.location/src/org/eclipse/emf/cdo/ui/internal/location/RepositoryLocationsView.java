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
package org.eclipse.emf.cdo.ui.internal.location;

import org.eclipse.emf.cdo.location.IRepositoryLocationManager;
import org.eclipse.emf.cdo.ui.internal.location.bundle.OM;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Eike Stepper
 */
public class RepositoryLocationsView extends ContainerView
{
  private NewLocationAction newAction;

  public RepositoryLocationsView()
  {
  }

  @Override
  protected IRepositoryLocationManager getContainer()
  {
    return IRepositoryLocationManager.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new RepositoryLocationItemProvider();
  }

  @Override
  protected Control createUI(Composite parent)
  {
    newAction = new NewLocationAction();
    return super.createUI(parent);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(newAction);
    manager.add(getRefreshAction());
    super.fillLocalToolBar(manager);
  }

  /**
   * @author Eike Stepper
   */
  private class NewLocationAction extends Action
  {
    public NewLocationAction()
    {
      setText("New Location");
      setToolTipText("Add a new repository location");
      setImageDescriptor(OM.getImageDescriptor("icons/add.gif"));
    }

    @Override
    public void run()
    {
      try
      {
        NewRepositoryLocationDialog dialog = new NewRepositoryLocationDialog(getSite().getShell());
        if (dialog.open() == NewRepositoryLocationDialog.OK)
        {
          String connectorType = dialog.getConnectorType();
          String connectorDescription = dialog.getConnectorDescription();
          String repositoryName = dialog.getRepositoryName();

          IRepositoryLocationManager.INSTANCE
              .addRepositoryLocation(connectorType, connectorDescription, repositoryName);
        }
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
        throw ex;
      }
    }
  }
}
