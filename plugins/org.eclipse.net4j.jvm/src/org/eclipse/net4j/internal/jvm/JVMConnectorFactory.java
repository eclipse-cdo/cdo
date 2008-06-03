/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class JVMConnectorFactory extends ConnectorFactory
{
  public static final String TYPE = "jvm";

  public JVMConnectorFactory()
  {
    super(TYPE);
  }

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
