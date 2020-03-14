/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ConnectorFactory;

import java.net.URISyntaxException;

/**
 * @author Eike Stepper
 */
public class WSConnectorFactory extends ConnectorFactory
{
  public static final String TYPE = "ws"; //$NON-NLS-1$

  public WSConnectorFactory()
  {
    super(TYPE);
  }

  /**
   * Allows derived classes to override the TYPE identifier
   */
  protected WSConnectorFactory(String type)
  {
    super(type);
  }

  @Override
  public WSClientConnector create(String description) throws ProductCreationException
  {
    WSClientConnector connector = createConnector();

    try
    {
      connector.setURL(getType() + "://" + description); //$NON-NLS-1$
    }
    catch (URISyntaxException ex)
    {
      throw new ProductCreationException(ex);
    }

    return connector;

  }

  protected WSClientConnector createConnector()
  {
    return new WSClientConnector();
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof WSConnector)
    {
      WSConnector connector = (WSConnector)object;
      return connector.getURL();
    }

    return null;
  }
}
