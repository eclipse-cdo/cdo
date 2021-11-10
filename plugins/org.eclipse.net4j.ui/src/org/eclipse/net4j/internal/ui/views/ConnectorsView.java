/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ui.container.ElementWizardAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class ConnectorsView extends AbstractTransportView
{
  public static final String ID = "org.eclipse.net4j.ConnectorsView"; //$NON-NLS-1$

  public ConnectorsView()
  {
  }

  @Override
  public boolean filter(Object element)
  {
    return element instanceof IConnector;
  }

  @Override
  protected IAction createNewAction(Shell shell, IManagedContainer container)
  {
    return createNewConnectorAction(getShell(), getContainer());
  }

  public static ElementWizardAction createNewConnectorAction(Shell shell, IManagedContainer container)
  {
    return new ElementWizardAction(shell, "New Connector", "Open a new connector", SharedIcons.getDescriptor(SharedIcons.ETOOL_ADD_CONNECTOR),
        ConnectorFactory.PRODUCT_GROUP, container, "tcp")
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
  }
}
