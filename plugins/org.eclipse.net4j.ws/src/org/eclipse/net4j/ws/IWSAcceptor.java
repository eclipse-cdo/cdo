/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.acceptor.IAcceptor;

import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

/**
 * A Net4j acceptor that receives message connections from a WebSocket server integration after the WebSocket upgrade.
 * <p>
 * The integration supplies upgrade handling and creates an {@link IMessageConnection}. Ownership of the underlying
 * server and network listener depends on the integration; with Jetty, the embedding application owns the Jetty server
 * and listener. This acceptor routes the resulting connection into the Net4j connector lifecycle, applies its transport
 * configuration, and tracks the server connector.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IWSAcceptor extends IAcceptor
{
  /**
   * Returns the route name used by WebSocket clients to select this acceptor.
   *
   * @return this acceptor's name
   */
  public String getName();

  /**
   * Returns the transport factory type represented by this acceptor, either {@code ws} or {@code wss}. A server provider can use
   * this value to select the appropriate upgrade and security configuration while routing an accepted connection by name.
   *
   * @return the WS or WSS transport type
   * @since 2.0
   */
  public String getType();

  /**
   * Attaches a WebSocket message connection accepted by a server provider to this Net4j acceptor. Ownership of the open connection
   * transfers to this method on invocation. If acceptance is rejected or connector preparation or activation fails, the connection
   * is aborted before the failure is propagated. On success, the created server connector owns the connection and closes it when the
   * connector or this acceptor is deactivated. The server integration supplies protocol-upgrade handling and creates the
   * {@link IMessageConnection}; ownership of its server and listener is integration-specific. This method creates and activates the
   * server-side Net4j connector and applies this acceptor's transport configuration.
   *
   * @param connection the accepted, open message connection whose lifetime is transferred to the Net4j connector
   * @since 2.0
   */
  public void accept(IMessageConnection connection);
}
