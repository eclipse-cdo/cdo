/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.doc.programmers;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSUtil;

import java.net.URI;

/**
 * WebSocket Transport Architecture
 * <p>
 * Net4j WS 2 separates the stable Net4j transport machinery from the provider that performs WebSocket I/O. This article
 * describes the division of responsibilities and the connection flow for developers integrating or diagnosing the
 * transport. Practical server and client deployment belongs to the Operator's Guide.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc01_WebSocketTransportArchitecture
{
  /**
   * Provider-Neutral Core
   * <p>
   * The <code>org.eclipse.net4j.ws</code> bundle provides WS/WSS acceptor and connector factories, route descriptions,
   * and Net4j connector integration. It does not depend on Jetty. The default <code>jdk</code> message-connection provider
   * is registered from the core bundle and implements the client side with the JDK HTTP/WebSocket APIs. The optional
   * <code>org.eclipse.net4j.ws.jetty.server</code> bundle adapts application-owned Jetty 12 servers.
   * <p>
   * A provider is selected from the <code>org.eclipse.net4j.messageConnections</code> factory product group. WS and WSS
   * connector factories look up that provider in their owning managed container, using the configured provider type or
   * the default type <code>jdk</code>.
   */
  public class Provider_Neutral_Core
  {
  }

  /**
   * Connectors, Acceptors, and WS/WSS
   * <p>
   * A client <code>IWSConnector</code> represents an outgoing Net4j connection and uses a service URI plus a named
   * acceptor route. The server-side <code>IWSAcceptor</code> receives the message connection after the server integration
   * completes the HTTP upgrade. Acceptors apply their Net4j transport configuration and create server-side Net4j
   * connectors; they do not own the HTTP server.
   * <p>
   * WS uses a <code>ws</code> URI and acceptor type; WSS uses a <code>wss</code> URI and WSS acceptor type. The scheme and
   * factory type must agree. WSS TLS configuration remains in the selected client provider and server integration.
   * {@link #createWSConnector() WSConnector.java}
   */
  public class Connectors_Acceptors_and_Schemes
  {
    /**
     * Creates and connects one WS connector using the default provider.
     *
     * @snip
     */
    @SuppressWarnings("unused")
    public void createWSConnector()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSConnector connector = null;

      try
      {
        connector = WSUtil.getConnector(container, URI.create("ws://gateway.example.net/net4j"), "public");
        connector.connect(5_000);

        String providerType = connector.getProviderType();
        String acceptorName = connector.getAcceptorName();
        // providerType is "jdk"; acceptorName is "public".
      }
      finally
      {
        try
        {
          if (connector != null)
          {
            connector.close();
          }
        }
        finally
        {
          LifecycleUtil.deactivate(container);
        }
      }
    }
  }

  /**
   * The Message-Connection Boundary
   * <p>
   * A provider factory creates one fresh <code>IMessageConnection</code> for each Net4j connector. It provides asynchronous
   * transport operations: start with a listener, send one complete framed buffer, request inbound messages, close
   * gracefully, or abort. The factory can hold shared client configuration; the returned connection has independent
   * runtime state and is not started until its Net4j connector activates.
   * <p>
   * The provider owns the WebSocket handshake and maps complete binary WebSocket messages to complete Net4j frames. A
   * WebSocket message is not a Net4j channel buffer. The message connector owns Net4j framing, channel multiplexing,
   * scheduling, control-channel processing, negotiation, and buffer lifecycle. The provider must preserve the complete
   * frame payload and honor the buffer ownership callbacks.
   */
  public class Message_Connection_Boundary
  {
  }

  /**
   * Connection Establishment and Data Flow
   * <p>
   * The client connector factory parses the endpoint, route, optional cookies, and provider token. It asks the selected
   * message-connection factory for a deferred connection and binds it to the Net4j connector. Connector activation starts
   * the provider connection and its asynchronous handshake. When the transport is ready, the message connector runs the
   * Net4j control and negotiation exchanges; application channels become usable after negotiation succeeds.
   * <p>
   * Outbound Net4j buffers are framed by the message connector and submitted through the provider send contract. Inbound
   * complete frames are parsed by the connector and routed to channels. The provider does not choose Net4j channels or
   * interpret application protocols.
   */
  public class Connection_Establishment_and_Data_Flow
  {
  }

  /**
   * Scheduling, Flow Control, and Lifecycle
   * <p>
   * Each accepted provider send has exactly one terminal callback. While the provider owns a submitted buffer, it must
   * not mutate or reuse its storage; the callback marks the point after which provider access has ended. A rejected send
   * must not retain the buffer or call the callback. During receive, demand is expressed in complete messages; the
   * provider must deliver in order and retain each supplied storage lease until its release action runs.
   * <p>
   * The connector schedules buffer work and applies Net4j receive demand independently of WebSocket library scheduling.
   * Provider implementations must not block indefinitely in abort and must complete accepted sends even during shutdown.
   * These rules allow Net4j to keep channel and buffer semantics stable across providers.
   */
  public class Scheduling_Flow_Control_and_Lifecycle
  {
  }

  /**
   * Client and Server Integration Boundaries
   * <p>
   * The client provider owns its <code>HttpClient</code> configuration and WebSocket instance. A factory configured with a
   * caller-supplied client does not own or close that client. The embedding application controls its lifetime.
   * <p>
   * On the server, <code>JettyWSUtil</code> creates only an upgrade handler and routes it to the acceptors passed to the
   * call. The application owns Jetty's server, connectors, TLS setup, handler tree, and lifecycle. Other server integrations
   * may use different routing arrangements while still supplying an <code>IMessageConnection</code> to the acceptor.
   * <p>
   * See {@link org.eclipse.net4j.doc.operators.Doc01_DeployingWebSocketServers} for Jetty deployment,
   * {@link org.eclipse.net4j.doc.operators.Doc02_ConfiguringWebSocketClients} for client configuration, and
   * {@link org.eclipse.net4j.doc.operators.Doc03_SecuringWebSocketTransports} for TLS.
   */
  public class Client_and_Server_Integration_Boundaries
  {
  }
}
