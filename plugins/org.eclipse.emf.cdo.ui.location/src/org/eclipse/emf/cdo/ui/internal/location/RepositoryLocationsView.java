/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

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
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(newAction);
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
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
          .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    }

    @Override
    public void run()
    {
      NewRepositoryLocationDialog dialog = new NewRepositoryLocationDialog(getSite().getShell());
      if (dialog.open() == NewRepositoryLocationDialog.OK)
      {
        IRepositoryLocationManager.INSTANCE.addRepositoryLocation(dialog.getConnectorType(),
            dialog.getConnectorDescription(), dialog.getRepositoryName());
      }
    }
  }
}
