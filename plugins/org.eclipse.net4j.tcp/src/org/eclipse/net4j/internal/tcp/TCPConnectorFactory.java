/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.ITCPConstants;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.internal.net4j.transport.ConnectorFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Eike Stepper
 */
public class TCPConnectorFactory extends ConnectorFactory<TCPClientConnector>
{
  private static final String URL_SCHEME = "http://";

  public TCPConnectorFactory()
  {
    super(ITCPConstants.TYPE);
  }

  public TCPClientConnector create(String description)
  {
    try
    {
      // TODO Don't use URL
      URL url = new URL(URL_SCHEME + description);
      String userID = url.getUserInfo();
      String host = url.getHost();
      int port = url.getPort();
      if (port == -1)
      {
        port = ITCPConstants.DEFAULT_PORT;
      }

      TCPClientConnector connector = new TCPClientConnector();
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

  @Override
  public String getDescriptionFor(TCPClientConnector connector)
  {
    String description = connector.getHost();
    String userID = connector.getUserID();
    if (!StringUtil.isEmpty(userID))
    {
      description = userID + "@" + description;
    }

    int port = connector.getPort();
    if (port != ITCPConstants.DEFAULT_PORT)
    {
      description = description + ":" + port;
    }

    return description;
  }
}
