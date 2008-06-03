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

import org.eclipse.spi.net4j.ConnectorFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Eike Stepper
 */
public class HTTPConnectorFactory extends ConnectorFactory
{
  private static final String HTTP_SCHEME_PREFIX = "http://";

  public static final String TYPE = "http";

  public HTTPConnectorFactory()
  {
    super(TYPE);
  }

  public HTTPClientConnector create(String description)
  {
    String userID = null;
    if (!description.startsWith(HTTP_SCHEME_PREFIX))
    {
      description = HTTP_SCHEME_PREFIX + description;
    }

    try
    {
      URL url = new URL(description);
      userID = url.getUserInfo();
    }
    catch (MalformedURLException ex)
    {
      ex.printStackTrace();
    }

    HTTPClientConnector connector = new HTTPClientConnector();
    connector.setURL(description);
    connector.setUserID(userID);
    return connector;
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof HTTPClientConnector)
    {
      HTTPClientConnector connector = (HTTPClientConnector)object;
      return connector.getURL();
    }

    return null;
  }
}
