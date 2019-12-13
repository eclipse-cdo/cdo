/*
 * Copyright (c) 2007-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil.ConnectorData;

import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class TCPConnectorFactory extends ConnectorFactory
{
  public static final String TYPE = "tcp"; //$NON-NLS-1$

  public TCPConnectorFactory()
  {
    super(TYPE);
  }

  /**
   * Allows derived classes to override the TYPE identifier
   */
  protected TCPConnectorFactory(String type)
  {
    super(type);
  }

  @Override
  public TCPConnector create(String description)
  {
    ConnectorData data = new ConnectorData(description);

    TCPConnector connector = createConnector();
    connector.setHost(data.getHost());
    connector.setPort(data.getPort());
    connector.setUserID(data.getUserID());
    return connector;

  }

  protected TCPConnector createConnector()
  {
    return new TCPClientConnector();
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof ITCPConnector)
    {
      ITCPConnector connector = (ITCPConnector)object;
      return new ConnectorData(connector).toString();
    }

    return null;
  }
}
