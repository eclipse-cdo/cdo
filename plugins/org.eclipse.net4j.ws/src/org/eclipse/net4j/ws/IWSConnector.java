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

import org.eclipse.net4j.connector.IConnector;

import org.eclipse.spi.net4j.MessageConnector;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * A client-side {@link IConnector} that carries Net4j traffic over a WebSocket connection.
 * <p>
 * The connector owns Net4j framing, channel multiplexing, scheduling, and the lifetime of the
 * {@link IMessageConnection} bound to it. A provider selected by the connector factory owns the
 * WebSocket handshake and transport-specific I/O, and reports complete Net4j frames through that
 * message connection. A WebSocket message is therefore not itself a Net4j channel buffer: the
 * provider must assemble or emit complete Net4j frames as required by {@link IMessageConnection}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IWSConnector extends IConnector
{
  /**
   * Prefix used in a WebSocket path segment to identify the target acceptor.
   */
  public static final String ACCEPTOR_NAME_PREFIX = "@";

  /**
   * The HTTP header that identifies the target Net4j acceptor during a WebSocket handshake.
   *
   * @since 2.0
   */
  public static final String ACCEPTOR_HEADER_NAME = "Net4jAcceptor";

  /**
   * Returns the base service URI used for the WebSocket handshake.
   *
   * @return the service URI, including the {@code ws} or {@code wss} scheme
   */
  public URI getServiceURI();

  /**
   * Returns the named server acceptor selected by this connector.
   *
   * @return the acceptor route name
   */
  public String getAcceptorName();

  /**
   * Returns the message-connection provider selected for this connector.
   *
   * @return the factory type in the {@code org.eclipse.net4j.messageConnections} product group
   * @since 2.0
   */
  public String getProviderType();

  /**
   * Returns the cookies configured for the provider's WebSocket handshake. The returned list is unmodifiable, but its
   * {@link HttpCookie} elements are mutable.
   *
   * @return an unmodifiable list of handshake cookies
   * @since 2.0
   */
  public List<HttpCookie> getCookies();

  /**
   * Supplies the asynchronous message connection created by a WebSocket client provider. Providers must call this before activating
   * the connector; {@link MessageConnector} owns subsequent message framing, scheduling, control-channel processing, and buffer
   * lifecycle management.
   *
   * @param connection the provider-owned transport connection to bind
   * @since 2.0
   */
  public void setMessageConnection(IMessageConnection connection);
}
