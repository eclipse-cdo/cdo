/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.ws.jdk;

import org.eclipse.net4j.internal.ws.jdk.JDKMessageConnectionFactory;
import org.eclipse.net4j.util.factory.IFactory;

import javax.net.ssl.SSLContext;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Creates configured JDK WebSocket provider factories without registering them.
 * <p>
 * A registered factory may be shared by multiple connectors; each call to its {@code create} method produces a
 * connection with independent runtime state. The overloads below determine whether those connections share the JDK
 * default HTTP client, a client configured from an {@link SSLContext}, or a caller-supplied {@link HttpClient}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public final class JDKWSUtil
{
  private JDKWSUtil()
  {
  }

  /**
   * Creates a JDK WebSocket message-connection factory that uses the given TLS context for secure WebSocket connections. Register
   * the returned factory in the client container and select its type in the WS or WSS connector description. For a non-null context,
   * this factory builds one HTTP client and shares it across the connections it creates; the JDK's normal certificate and hostname
   * verification remain enabled. A {@code null} context selects the provider's shared default HTTP client. Both paths use a ten-second
   * HTTP connection timeout and a separate ten-second WebSocket opening-handshake timeout. Use the {@link HttpClient} overload to
   * configure a proxy or authentication.
   *
   * @param providerType the unique factory type to register and select in the connector description
   * @param sslContext the TLS context to use, or {@code null} to use the provider's shared Provider-default HTTP client
   * @return the configured message-connection provider factory
   */
  public static IFactory createMessageConnectionFactory(String providerType, SSLContext sslContext)
  {
    return new JDKMessageConnectionFactory(providerType, sslContext);
  }

  /**
   * Creates a JDK WebSocket message-connection factory that shares the supplied HTTP client across the connections it creates and
   * applies the supplied timeout to each WebSocket opening handshake. The timeout is distinct from the HTTP client's connection
   * timeout. Configure TLS, proxy selection, and any authenticator on the client before passing it here; this provider does not consult
   * Eclipse {@code IProxyService} or add proxy or authentication configuration. The factory and its connections do not own or close
   * the client; the caller is responsible for its lifecycle. Register each independently configured factory under a distinct provider
   * type to isolate its client configuration.
   *
   * @param providerType the unique factory type to register and select in the connector description
   * @param client the non-null caller-owned HTTP client shared by connections created through this factory
   * @param webSocketConnectTimeout the positive maximum time to complete each WebSocket opening handshake, independent of the HTTP
   *          client's connection timeout
   * @return the configured message-connection provider factory
   * @throws NullPointerException if {@code client} or {@code webSocketConnectTimeout} is {@code null}
   * @throws IllegalArgumentException if {@code webSocketConnectTimeout} is zero or negative
   */
  public static IFactory createMessageConnectionFactory(String providerType, HttpClient client, Duration webSocketConnectTimeout)
  {
    return new JDKMessageConnectionFactory(providerType, client, webSocketConnectTimeout);
  }
}
