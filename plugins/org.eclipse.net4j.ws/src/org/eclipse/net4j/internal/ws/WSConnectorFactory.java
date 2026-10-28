/*
 * Copyright (c) 2020, 2024-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSUtil;

import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.spi.net4j.MessageConnectionFactory;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses WS connector descriptions and binds each connector to a provider product from its owning container.
 * <p>
 * The registered provider factory is selected from the managed container and may be shared by multiple connectors. This
 * factory creates a separate {@link IMessageConnection} for each connector and passes that connection's URI and cookies
 * in the provider description; provider factories may also use shared configuration of their own. The WS connector
 * factory does not perform network I/O; activation starts the resulting message connection.
 *
 * @author Eike Stepper
 */
public class WSConnectorFactory extends ConnectorFactory implements IManagedContainer.ContainerAware
{
  public static final String PROVIDER_TOKEN_PREFIX = "provider:";

  public static final String DEFAULT_PROVIDER_TYPE = "jdk";

  public static final String COOKIE_TOKEN_PREFIX = "cookie:";

  public static final String TOKEN_SEPARATOR = "|";

  private static final char TOKEN_SEPARATOR_CHAR = TOKEN_SEPARATOR.charAt(0);

  /**
   * Container used to resolve and create the selected message-connection provider product.
   */
  private IManagedContainer container;

  public WSConnectorFactory()
  {
    this(WSUtil.FACTORY_TYPE);
  }

  protected WSConnectorFactory(String type)
  {
    super(type);
  }

  @Override
  public void setManagedContainer(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IWSConnector create(String description) throws ProductCreationException
  {
    if (description == null)
    {
      throw productCreationException(null, new IllegalArgumentException("description == null")); //$NON-NLS-1$
    }

    try
    {
      boolean escapedSyntax = description.indexOf('\n') < 0 && hasUnescapedSeparator(description);

      List<String> tokens = splitTokens(description, escapedSyntax);
      if (tokens.isEmpty())
      {
        throw new IllegalArgumentException("Missing service URI"); //$NON-NLS-1$
      }

      URI routedURI = new URI(getType() + "://" + tokens.get(0)); //$NON-NLS-1$

      String path = routedURI.getRawPath();
      if (path == null)
      {
        throw productCreationException(description, new IllegalArgumentException("Missing acceptor route")); //$NON-NLS-1$
      }

      int separator = path.lastIndexOf('/');

      String route = path.substring(separator + 1);
      if (!route.startsWith(IWSConnector.ACCEPTOR_NAME_PREFIX) || route.length() == 1)
      {
        throw productCreationException(description, new IllegalArgumentException("Missing acceptor route")); //$NON-NLS-1$
      }

      String acceptorName = URLDecoder.decode(route.substring(1).replace("+", "%2B"), StandardCharsets.UTF_8);
      if (acceptorName.length() == 0)
      {
        throw productCreationException(description, new IllegalArgumentException("Missing acceptor route")); //$NON-NLS-1$
      }

      URI serviceURI = routedURI.resolve("."); //$NON-NLS-1$
      List<HttpCookie> cookies = new ArrayList<>();
      String providerType = DEFAULT_PROVIDER_TYPE;
      boolean providerSpecified = false;

      for (int i = 1; i < tokens.size(); i++)
      {
        String token = tokens.get(i);
        if (token.startsWith(COOKIE_TOKEN_PREFIX))
        {
          String cookieString = token.substring(COOKIE_TOKEN_PREFIX.length());

          int equals = cookieString.indexOf('=');
          if (equals < 1)
          {
            throw new IllegalArgumentException("Invalid cookie token: " + token); //$NON-NLS-1$
          }

          String cookieName = cookieString.substring(0, equals);
          String cookieValue = cookieString.substring(equals + 1);
          validateCookieValue(cookieValue);
          cookies.add(new HttpCookie(cookieName, cookieValue));
        }
        else if (token.startsWith(PROVIDER_TOKEN_PREFIX))
        {
          if (providerSpecified)
          {
            throw new IllegalArgumentException("Message connection provider was specified more than once"); //$NON-NLS-1$
          }

          providerType = token.substring(PROVIDER_TOKEN_PREFIX.length());
          if (providerType.isEmpty())
          {
            throw new IllegalArgumentException("Missing message connection provider type"); //$NON-NLS-1$
          }

          providerSpecified = true;
        }
        else
        {
          throw new IllegalArgumentException("Unrecognized description token: " + token); //$NON-NLS-1$
        }
      }

      if (container == null)
      {
        throw new IllegalStateException("WSConnectorFactory has no managed container"); //$NON-NLS-1$
      }

      IWSConnector connector = new WSConnector(serviceURI, acceptorName, getType(), cookies, providerType);

      try
      {
        MessageConnectionFactory factory = (MessageConnectionFactory)container.getFactory(MessageConnectionFactory.PRODUCT_GROUP, providerType);
        String providerDescription = connector.getURL();

        for (HttpCookie cookie : cookies)
        {
          providerDescription += "\n" + cookie.getName() + "=" + cookie.getValue(); //$NON-NLS-1$
        }

        IMessageConnection connection = factory.create(providerDescription);
        if (connection == null)
        {
          throw new IllegalArgumentException("Message connection provider returned null: " + providerType); //$NON-NLS-1$
        }

        connector.setMessageConnection(connection);
      }
      catch (FactoryNotFoundException ex)
      {
        throw new IllegalArgumentException("No message connection provider is registered for type '" + providerType + "'", ex); //$NON-NLS-1$
      }

      return connector;
    }
    catch (URISyntaxException | IllegalArgumentException ex)
    {
      throw productCreationException(description, ex);
    }
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof IWSConnector)
    {
      IWSConnector connector = (IWSConnector)object;

      String description = WSUtil.getConnectorDescription(connector.getServiceURI(), connector.getAcceptorName());
      description += TOKEN_SEPARATOR + PROVIDER_TOKEN_PREFIX + StringUtil.escape(connector.getProviderType(), TOKEN_SEPARATOR_CHAR);

      for (HttpCookie cookie : connector.getCookies())
      {
        description += TOKEN_SEPARATOR + StringUtil.escape(COOKIE_TOKEN_PREFIX + cookie.getName() + "=" + cookie.getValue(), TOKEN_SEPARATOR_CHAR); //$NON-NLS-1$
      }

      return description;
    }

    return null;
  }

  private static List<String> splitTokens(String description, boolean escapedSyntax)
  {
    List<String> tokens = new ArrayList<>();

    if (!escapedSyntax)
    {
      for (String token : description.split("\\r?\\n", -1)) //$NON-NLS-1$
      {
        tokens.add(token);
      }

      return tokens;
    }

    StringBuilder token = new StringBuilder();

    for (int i = 0; i < description.length(); i++)
    {
      char c = description.charAt(i);
      if (c == '\\' && i + 1 < description.length())
      {
        token.append(c).append(description.charAt(++i));
      }
      else if (c == '|')
      {
        tokens.add(StringUtil.unescape(token.toString(), '|'));
        token.setLength(0);
      }
      else
      {
        token.append(c);
      }
    }

    tokens.add(StringUtil.unescape(token.toString(), '|'));
    return tokens;
  }

  private static boolean hasUnescapedSeparator(String description)
  {
    boolean escaped = false;

    for (int i = 0; i < description.length(); i++)
    {
      char c = description.charAt(i);

      if (escaped)
      {
        escaped = false;
      }
      else if (c == '\\')
      {
        escaped = true;
      }
      else if (c == '|')
      {
        return true;
      }
    }

    return false;
  }

  private static void validateCookieValue(String value)
  {
    for (int i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);

      boolean valid = c == '!' || c >= '#' && c <= '+' || c >= '-' && c <= ':' || c >= '<' && c <= '[' || c >= ']' && c <= '~';
      if (!valid)
      {
        throw new IllegalArgumentException("Unsupported character in cookie value at index " + i); //$NON-NLS-1$
      }
    }
  }
}
