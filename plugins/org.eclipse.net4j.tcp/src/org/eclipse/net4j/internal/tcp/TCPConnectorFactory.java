/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ConnectorFactory;

import java.net.MalformedURLException;
import java.net.URL;

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

  public TCPConnector create(String description)
  {
    try
    {
      // TODO Don't use URL
      // Scheme "tcp://" would be rejected!
      URL url = new URL("http://" + description);
      String userID = url.getUserInfo();
      String host = url.getHost();
      int port = url.getPort();
      if (port == -1)
      {
        port = ITCPConnector.DEFAULT_PORT;
      }

      TCPConnector connector = createConnector();
      connector.setUserID(userID);
      connector.setHost(host);
      connector.setPort(port);
      return connector;
    }
    catch (MalformedURLException ex)
    {
      throw new ProductCreationException(ex);
    }
  }

  protected TCPConnector createConnector()
  {
    return new TCPClientConnector();
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof TCPConnector)
    {
      TCPConnector connector = (TCPConnector)object;
      String description = connector.getHost();
      String userID = connector.getUserID();
      if (!StringUtil.isEmpty(userID))
      {
        description = userID + "@" + description; //$NON-NLS-1$
      }

      int port = connector.getPort();
      if (port != ITCPConnector.DEFAULT_PORT)
      {
        description = description + ":" + port; //$NON-NLS-1$
      }

      return description;
    }

    return null;
  }
}
