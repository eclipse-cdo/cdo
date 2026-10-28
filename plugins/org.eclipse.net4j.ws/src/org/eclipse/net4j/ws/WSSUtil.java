/*
 * Copyright (c) 2024, 2025 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Maxime Porhel (Obeo) - initial API and implementation
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.ws.WSSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSSConnectorFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Creates and describes secure WebSocket (WSS) connector and acceptor products.
 * <p>
 * WSS uses the same Net4j framing, provider selection, and connector ownership contracts as WS. The selected provider
 * remains responsible for TLS and WebSocket transport details.
 *
 * @author mporhel
 * @since 2.0
 */
public final class WSSUtil
{
  public static final String FACTORY_TYPE = "wss"; //$NON-NLS-1$

  private WSSUtil()
  {
  }

  /**
   * Manually prepares a raw container with the WSS acceptor and connector factories.
   * Initialized containers receive these factories automatically through declarative
   * container initialization. Manual preparation remains appropriate for containers
   * created with {@code ContainerUtil.createContainer()}.
   *
   * @deprecated Initialized containers are prepared automatically.
   */
  @Deprecated
  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new WSSAcceptorFactory());
    container.registerFactory(new WSSConnectorFactory());
  }

  /**
   * Gets the WSS acceptor product for the given route name. The container may create and activate the product if it
   * does not already exist.
   *
   * @param container container that owns the acceptor product
   * @param acceptorName registered route name
   * @return the acceptor product
   * @throws org.eclipse.net4j.util.container.FactoryNotFoundException if the WSS acceptor factory is not registered
   * @throws org.eclipse.net4j.util.factory.ProductCreationException if the container cannot create or activate the product
   */
  public static IWSAcceptor getAcceptor(IManagedContainer container, String acceptorName)
  {
    return (IWSAcceptor)container.getElement(WSAcceptorFactory.PRODUCT_GROUP, FACTORY_TYPE, acceptorName);
  }

  /**
   * Creates or retrieves a secure WebSocket connector for a previously encoded description.
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
   * Creates or retrieves a secure WebSocket connector for a service URI and acceptor route.
   *
   * @param container container that owns the connector and provider factories
   * @param serviceURI base WSS service URI
   * @param acceptorName target acceptor route
   * @param arguments optional provider or handshake arguments
   * @return the configured connector
   */
  public static IWSConnector getConnector(IManagedContainer container, URI serviceURI, String acceptorName, String... arguments)
  {
    String description = getConnectorDescription(serviceURI, acceptorName, arguments);
    return getConnector(container, description);
  }

  /**
   * Encodes a string URI and acceptor route as a WSS connector factory description.
   *
   * @param serviceURI base WSS service URI
   * @param acceptorName target acceptor route
   * @param arguments provider or handshake arguments
   * @return connector factory description
   * @throws URISyntaxException if {@code serviceURI} is not a valid URI
   */
  public static String getConnectorDescription(String serviceURI, String acceptorName, String... arguments) throws URISyntaxException
  {
    return getConnectorDescription(new URI(serviceURI), acceptorName, arguments);
  }

  /**
   * Encodes a service URI and acceptor route as a WSS connector factory description.
   *
   * @param serviceURI base WSS service URI
   * @param acceptorName target acceptor route
   * @param arguments provider or handshake arguments
   * @return connector factory description understood by the WSS connector factory
   */
  public static String getConnectorDescription(URI serviceURI, String acceptorName, String... arguments)
  {
    return WSUtil.getConnectorDescription(serviceURI, acceptorName, arguments);
  }
}
