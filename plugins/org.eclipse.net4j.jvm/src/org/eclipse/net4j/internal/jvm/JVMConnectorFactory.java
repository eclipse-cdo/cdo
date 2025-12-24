/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class JVMConnectorFactory extends ConnectorFactory
{
  public static final String TYPE = "jvm"; //$NON-NLS-1$

  public JVMConnectorFactory()
  {
    super(TYPE);
  }

  @Override
  public JVMClientConnector create(String description) throws ProductCreationException
  {
    JVMClientConnector connector = new JVMClientConnector();
    connector.setName(description);
    return connector;
  }

  @Override
  public String getDescriptionFor(Object connector)
  {
    if (connector instanceof JVMClientConnector)
    {
      return ((JVMClientConnector)connector).getName();
    }

    return null;
  }

  public static JVMClientConnector get(IManagedContainer container, String description)
  {
    return (JVMClientConnector)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
