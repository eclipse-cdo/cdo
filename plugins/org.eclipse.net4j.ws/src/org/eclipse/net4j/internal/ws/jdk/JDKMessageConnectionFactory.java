/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws.jdk;

import org.eclipse.net4j.util.concurrent.TimerLifecycle.DaemonFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.MessageConnectionFactory;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import javax.net.ssl.SSLContext;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

/**
 * Creates deferred JDK HTTP client WebSocket connections for a registered provider type.
 * <p>
 * The factory's HTTP client and opening-handshake timeout are shared configuration. Every call to {@link #create(String)} parses
 * connector-specific URI and cookie input and returns a new {@link JDKWebSocketMessageConnection} with independent lifecycle,
 * send, and receive state. Creating the connection does not start the handshake.
 *
 * @author Eike Stepper
 */
public final class JDKMessageConnectionFactory extends MessageConnectionFactory implements ContainerAware
{
  /**
   * Default provider type used by the automatically registered factory.
   */
  public static final String TYPE = "jdk"; //$NON-NLS-1$

  /**
   * Opening-handshake limit used by the convenience constructors, separate from HTTP connection timeout.
   */
  private static final Duration DEFAULT_WEBSOCKET_CONNECT_TIMEOUT = Duration.ofSeconds(10);

  /**
   * Shared HTTP client configuration for this provider factory, or {@code null} to use the class-wide default client.
   */
  private final HttpClient client;

  /**
   * Shared opening-handshake limit applied when each connection starts.
   */
  private final Duration webSocketConnectTimeout;

  private volatile IManagedContainer container;

  /**
   * Creates the default {@code jdk} provider factory. Connections created by this factory use the class-wide default HTTP client
   * and the ten-second WebSocket opening-handshake timeout.
   */
  public JDKMessageConnectionFactory()
  {
    super(TYPE);
    client = null;
    webSocketConnectTimeout = DEFAULT_WEBSOCKET_CONNECT_TIMEOUT;
  }

  /**
   * Creates a provider factory that builds and shares an HTTP client configured with the given TLS context. A {@code null} context
   * selects the class-wide default HTTP client. The HTTP client connection timeout and the WebSocket opening-handshake timeout are
   * each ten seconds; they apply to different stages.
   *
   * @param type the provider name used in connector descriptions
   * @param sslContext the TLS context to use, or {@code null} to use the class-wide default client configuration
   */
  public JDKMessageConnectionFactory(String type, SSLContext sslContext)
  {
    super(type);
    client = sslContext == null ? null : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).sslContext(sslContext).build();
    webSocketConnectTimeout = DEFAULT_WEBSOCKET_CONNECT_TIMEOUT;
  }

  /**
   * Creates a provider factory that shares a caller-owned HTTP client and applies the given opening-handshake timeout to each
   * connection it creates. The factory and its connections do not close the HTTP client.
   *
   * @param type the provider name used in connector descriptions
   * @param client the caller-owned HTTP client to use for WebSocket connections
   * @param webSocketConnectTimeout the positive maximum time to complete the WebSocket opening handshake, separate from the HTTP
   *          client's connection timeout
   * @throws NullPointerException if {@code client} or {@code webSocketConnectTimeout} is {@code null}
   * @throws IllegalArgumentException if {@code webSocketConnectTimeout} is zero or negative
   */
  public JDKMessageConnectionFactory(String type, HttpClient client, Duration webSocketConnectTimeout)
  {
    super(type);
    this.client = Objects.requireNonNull(client, "client"); //$NON-NLS-1$
    Objects.requireNonNull(webSocketConnectTimeout, "webSocketConnectTimeout"); //$NON-NLS-1$

    if (webSocketConnectTimeout.isZero() || webSocketConnectTimeout.isNegative())
    {
      throw new IllegalArgumentException("webSocketConnectTimeout must be positive"); //$NON-NLS-1$
    }

    this.webSocketConnectTimeout = webSocketConnectTimeout;
  }

  @Override
  public void setManagedContainer(IManagedContainer container)
  {
    this.container = container;
  }

  /**
   * Parses one WS or WSS URI followed by zero or more newline-delimited cookie assignments and creates a fresh, unstarted
   * connection. The factory's HTTP client and timeout are reused as configuration; send, receive, and lifecycle state belong to the
   * returned connection.
   *
   * @param description a WS or WSS URI, optionally followed by newline-delimited {@code name=value} cookie entries
   * @return a new message connection with independent runtime state
   * @throws ProductCreationException if the description is {@code null}, the URI scheme is unsupported, or the URI or a cookie entry
   *          is invalid
   */
  @Override
  public IMessageConnection create(String description) throws ProductCreationException
  {
    if (description == null)
    {
      throw new ProductCreationException("JDK WebSocket connection description is null"); //$NON-NLS-1$
    }

    try
    {
      String[] lines = description.split("\n", -1); //$NON-NLS-1$
      URI uri = new URI(lines[0]);

      if (!("ws".equals(uri.getScheme()) || "wss".equals(uri.getScheme()))) //$NON-NLS-1$ //$NON-NLS-2$
      {
        throw new IllegalArgumentException("Expected a ws or wss URI"); //$NON-NLS-1$
      }

      List<String> cookies = new ArrayList<>();

      for (int i = 1; i < lines.length; i++)
      {
        int equals = lines[i].indexOf('=');
        if (equals < 1)
        {
          throw new IllegalArgumentException("Invalid cookie description: " + lines[i]); //$NON-NLS-1$
        }

        String name = lines[i].substring(0, equals);
        String value = lines[i].substring(equals + 1);
        validateCookieValue(value);

        HttpCookie cookie = new HttpCookie(name, value);
        cookies.add(cookie.getName() + "=" + cookie.getValue()); //$NON-NLS-1$
      }

      IManagedContainer currentContainer = container;
      Timer heartbeatTimer = currentContainer == null ? null : DaemonFactory.getTimer(currentContainer, null);

      return new JDKWebSocketMessageConnection(uri, cookies, client, webSocketConnectTimeout, heartbeatTimer);
    }
    catch (URISyntaxException | IllegalArgumentException | IndexOutOfBoundsException ex)
    {
      throw new ProductCreationException("Invalid JDK WebSocket connection description: " + description, ex); //$NON-NLS-1$
    }
  }

  /**
   * Rejects cookie value characters unsupported by the Cookie header format used by this provider.
   *
   * @param value cookie value to validate
   * @throws IllegalArgumentException if a character is outside the supported cookie-octet range
   */
  private static void validateCookieValue(String value)
  {
    for (int i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);

      boolean valid = c == '!' //
          || c >= '#' && c <= '+' //
          || c >= '-' && c <= ':' //
          || c >= '<' && c <= '[' //
          || c >= ']' && c <= '~';
      if (!valid)
      {
        throw new IllegalArgumentException("Unsupported character in cookie value at index " + i); //$NON-NLS-1$
      }
    }
  }
}
