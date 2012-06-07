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
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.ui.Net4jItemProvider;
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.container.ElementWizardAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class ConnectorsView extends ContainerView
{
  public final static String ID = "org.eclipse.net4j.ConnectorsView"; //$NON-NLS-1$

  private IAction newConnectorAction = new ElementWizardAction(getShell(), "New Connector", "Open a new connector",
      SharedIcons.getDescriptor(SharedIcons.ETOOL_ADD_CONNECTOR), ConnectorFactory.PRODUCT_GROUP, getContainer(), "tcp")
  {
    @Override
    public String getDefaultDescription(String factoryType)
    {
      if ("jvm".equals(factoryType))
      {
        return "default";
      }

      if ("tcp".equals(factoryType))
      {
        return "localhost";
      }

      return null;
    }
  };

  public ConnectorsView()
  {
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new Net4jItemProvider(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof IConnector;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(newConnectorAction);
    super.fillLocalToolBar(manager);
  }
}
