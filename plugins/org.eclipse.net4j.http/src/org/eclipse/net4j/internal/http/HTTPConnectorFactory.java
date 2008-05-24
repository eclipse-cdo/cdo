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
package org.eclipse.net4j.internal.http;

import org.eclipse.internal.net4j.connector.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class HTTPConnectorFactory extends ConnectorFactory
{
  public static final String TYPE = "tcp";

  private static final String URL_SCHEME = "http://";

  public HTTPConnectorFactory()
  {
    super(TYPE);
  }

  public HTTPClientConnector create(String description)
  {
    // try
    // {
    // // TODO Don't use URL
    // // Scheme "tcp://" would be rejected!
    // URL url = new URL(URL_SCHEME + description);
    // String userID = url.getUserInfo();
    // String host = url.getHost();
    // int port = url.getPort();
    // if (port == -1)
    // {
    // port = IHTTPConnector.DEFAULT_PORT;
    // }

    HTTPClientConnector connector = new HTTPClientConnector();
    // connector.setUserID(userID);
    // connector.setHost(host);
    // connector.setPort(port);
    return connector;
    // }
    // catch (MalformedURLException ex)
    // {
    // throw new ProductCreationException(ex);
    // }
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    // if (object instanceof HTTPClientConnector)
    // {
    // HTTPClientConnector connector = (HTTPClientConnector)object;
    // String description = connector.getHost();
    // String userID = connector.getUserID();
    // if (!StringUtil.isEmpty(userID))
    // {
    // description = userID + "@" + description;
    // }
    //
    // int port = connector.getPort();
    // if (port != IHTTPConnector.DEFAULT_PORT)
    // {
    // description = description + ":" + port;
    // }
    //
    // return description;
    // }

    return null;
  }
}
