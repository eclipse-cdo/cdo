/*
 * Copyright (c) 2020, 2023-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.ws.bundle.OM;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * Creates and describes WebSocket connectors and retrieves WebSocket acceptors from managed containers.
 * <p>
 * Connector descriptions identify the transport endpoint and named acceptor, followed by escaped provider-specific
 * arguments. The provider type is selected through the message-connection factory product group; the WS connector owns
 * the Net4j scheduling and framing layer, while that provider owns handshake configuration and transport I/O.
 *
 * @author Eike Stepper
 */
public final class WSUtil
{
  public static final String FACTORY_TYPE = "ws"; //$NON-NLS-1$

  private WSUtil()
  {
  }

  /**
   * @deprecated Containers created via {@link org.eclipse.net4j.util.container.ContainerUtil#createInitializedContainer()} or
   *             {@link org.eclipse.net4j.util.om.OMPlatform#createManagedContainer()} are initialized automatically. Advanced callers
   *             configuring a raw container can use {@link org.eclipse.net4j.util.om.OMBundle#prepareContainer(IManagedContainer)}.
   */
  @Deprecated
  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }

  /**
   * Gets the WS acceptor product for the given name. The container may create and activate the product if it does not
   * already exist.
   *
   * @param container container that owns the acceptor product
   * @param acceptorName registered route name
   * @return the acceptor product
   * @throws org.eclipse.net4j.util.container.FactoryNotFoundException if the WS acceptor factory is not registered
   * @throws org.eclipse.net4j.util.factory.ProductCreationException if the container cannot create or activate the product
   */
  public static IWSAcceptor getAcceptor(IManagedContainer container, String acceptorName)
  {
    return (IWSAcceptor)container.getElement(WSAcceptorFactory.PRODUCT_GROUP, FACTORY_TYPE, acceptorName);
  }

  /**
   * Creates or retrieves a connector product for a previously encoded description.
   *
   * @param container container that owns the connector and provider factories
   * @param description connector factory description
   * @return the configured connector
   */
  public static IWSConnector getConnector(IManagedContainer container, String description)
  {
    return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, FACTORY_TYPE, description);
  }

  /**
   * Creates a connector product for a service URI, acceptor route, and optional provider arguments.
   *
   * @param container container that owns the connector and message-connection provider factories
   * @param serviceURI base WebSocket service URI
   * @param acceptorName target acceptor route
   * @param arguments escaped provider or handshake arguments
   * @return the configured connector
   * @since 1.3
   */
  public static IWSConnector getConnector(IManagedContainer container, URI serviceURI, String acceptorName, String... arguments)
  {
    String description = getConnectorDescription(serviceURI, acceptorName, arguments);
    return getConnector(container, description);
  }

  /**
   * Encodes a string URI and route name as a connector factory description.
   *
   * @param serviceURI base WebSocket service URI
   * @param acceptorName target acceptor route
   * @param arguments provider or handshake arguments
   * @return connector factory description
   * @throws URISyntaxException if {@code serviceURI} is not a valid URI
   * @since 1.3
   */
  public static String getConnectorDescription(String serviceURI, String acceptorName, String... arguments) throws URISyntaxException
  {
    return getConnectorDescription(new URI(serviceURI), acceptorName, arguments);
  }

  /**
   * Encodes a service URI and acceptor route as a connector factory description.
   *
   * @param serviceURI base WebSocket service URI
   * @param acceptorName target acceptor route
   * @param arguments provider or handshake arguments; CR and LF are rejected because the description uses line-based
   *          compatibility syntax
   * @return connector factory description understood by the WS connector factory
   * @throws IllegalArgumentException if an argument contains CR or LF
   * @since 1.3
   */
  public static String getConnectorDescription(URI serviceURI, String acceptorName, String... arguments)
  {
    String string = serviceURI.toString();
    if (!string.endsWith("/")) //$NON-NLS-1$
    {
      string += "/"; //$NON-NLS-1$

      try
      {
        serviceURI = new URI(string);
      }
      catch (URISyntaxException ex)
      {
        // This can't realistically happen.
        throw new RuntimeException(ex);
      }
    }

    URI uri = serviceURI.resolve(IWSConnector.ACCEPTOR_NAME_PREFIX + encodePathSegment(acceptorName));

    StringBuilder description = new StringBuilder();
    description.append(uri.getAuthority()).append(uri.getRawPath());

    for (String argument : arguments)
    {
      if (argument.indexOf('\r') >= 0 || argument.indexOf('\n') >= 0)
      {
        throw new IllegalArgumentException("Connector arguments must not contain CR or LF characters"); //$NON-NLS-1$
      }

      description //
          .append(WSConnectorFactory.TOKEN_SEPARATOR) //
          .append(StringUtil.escape(argument, WSConnectorFactory.TOKEN_SEPARATOR.charAt(0)));
    }

    return description.toString();
  }

  private static String encodePathSegment(String value)
  {
    StringBuilder encoded = new StringBuilder();

    for (byte character : value.getBytes(StandardCharsets.UTF_8))
    {
      int unsigned = character & 0xFF;
      if (unsigned >= 'a' && unsigned <= 'z' //
          || unsigned >= 'A' && unsigned <= 'Z' //
          || unsigned >= '0' && unsigned <= '9' //
          || "-._~!$&'()*+,;=:@".indexOf(unsigned) >= 0)
      {
        encoded.append((char)unsigned);
      }
      else
      {
        encoded.append('%');
        encoded.append(Character.toUpperCase(Character.forDigit(unsigned >>> 4, 16)));
        encoded.append(Character.toUpperCase(Character.forDigit(unsigned & 15, 16)));
      }
    }

    return encoded.toString();
  }
}
