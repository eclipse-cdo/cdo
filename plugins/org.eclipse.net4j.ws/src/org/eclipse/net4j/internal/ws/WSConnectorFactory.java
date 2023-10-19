/*
 * Copyright (c) 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.ws.WSUtil;

import org.eclipse.spi.net4j.ConnectorFactory;

import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class WSConnectorFactory extends ConnectorFactory
{

  /** Description token separator. */
  public static final String TOKEN_SEPARATOR = "\n";

  /** Prefix in description token to signal a cookie definition. */
  public static final String COOKIE_TOKEN_PREFIX = "cookie:";

  public WSConnectorFactory()
  {
    super(WSUtil.FACTORY_TYPE);
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
    List<HttpCookie> cookies = new ArrayList<>();
    String[] descriptionTokens = description.split(TOKEN_SEPARATOR);
    String urlDescription = descriptionTokens[0]; // URL is always the first token
    for (int i = 1; i < descriptionTokens.length; i++)
    {
      String token = descriptionTokens[i];
      if (token.startsWith(COOKIE_TOKEN_PREFIX))
      {
        String cookieString = token.replaceFirst(COOKIE_TOKEN_PREFIX, "");
        int index = cookieString.indexOf("=");
        if (index < 1)
        {
          throw new ProductCreationException("Invalid cookie token: " + token);
        }
        String name = cookieString.substring(0, index);
        String value = cookieString.substring(index + 1);
        cookies.add(new HttpCookie(name, value));
      }
      else
      {
        throw new ProductCreationException("Unrecognized description token: " + token);
      }
    }

    WSClientConnector connector = createConnector();

    try
    {
      connector.setURL(getType() + "://" + urlDescription); //$NON-NLS-1$
    }
    catch (URISyntaxException ex)
    {
      throw new ProductCreationException(ex);
    }

    connector.setCookies(cookies);

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
      String description = connector.getURL();

      if (connector instanceof WSClientConnector)
      {
        WSClientConnector clientConnector = (WSClientConnector)connector;
        StringBuilder sb = new StringBuilder(description);
        for (HttpCookie cookie : clientConnector.getCookies())
        {
          sb.append(TOKEN_SEPARATOR);
          sb.append(COOKIE_TOKEN_PREFIX);
          sb.append(cookie.getName());
          sb.append("=");
          sb.append(cookie.getValue());
        }
        description = sb.toString();
      }
      return description;
    }

    return null;
  }
}
