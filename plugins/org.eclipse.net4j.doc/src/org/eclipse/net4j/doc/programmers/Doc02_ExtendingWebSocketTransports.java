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
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Extending WebSocket Transports
 * <p>
 * Net4j exposes a provider boundary for WebSocket implementations that need different client or server libraries. This
 * article describes the supported factory and message-connection contracts and the obligations an implementation must
 * satisfy. It deliberately does not present a fabricated third-party transport implementation.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_ExtendingWebSocketTransports
{
  /**
   * Configuring and Registering Client Providers
   * <p>
   * The built-in JDK provider is a public utility for configuring a registered factory. Give each distinct client policy
   * its own provider type and register the factory in the container that owns the connectors. The connector description's
   * <code>provider:</code> token selects that factory. This example uses the existing JDK provider with a custom opening
   * timeout; the application still owns the HTTP client.
   * Java 11 has no <code>HttpClient.close()</code>. On Java 21 and later, the application may close its client only after
   * every connector using that client has stopped; the provider factory does not own or close it.
   * {@link #registerConfiguredJDKProvider() RegisterJDKProvider.java}
   */
  public class Configuring_and_Registering_Client_Providers
  {
    /**
     * Registers a caller-owned JDK client under an application-specific provider type.
     *
     * @snip
     */
    public void registerConfiguredJDKProvider()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      HttpClient client = null;
      IWSConnector connector = null;

      try
      {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(7)).build();
        IFactory provider = JDKWSUtil.createMessageConnectionFactory("partner-client", client, Duration.ofSeconds(9));
        container.registerFactory(provider);

        connector = WSSUtil.getConnector(container, URI.create("wss://partner.example.net/net4j"), "partner", "provider:partner-client");
        connector.connect(15_000);
        // Use this connector with the partner's Net4j protocol.
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
   * The Message-Connection Factory Contract
   * <p>
   * A custom provider extends the SPI <code>MessageConnectionFactory</code> and implements
   * <code>create(String description)</code>. It returns a fresh, not-yet-started <code>IMessageConnection</code> for each
   * connector. The factory may retain shared immutable configuration or a thread-safe client, but each returned connection
   * must have independent runtime state and be owned by its requesting connector.
   * <p>
   * Register providers under <code>MessageConnectionFactory.PRODUCT_GROUP</code> with a stable type string. In OSGi, the
   * bundle can contribute a factory through the standard Net4j factories extension point; programmatic registration is
   * useful when the provider depends on application-created configuration. The selected type must be present in the
   * connector's managed container before the provider connection is created.
   */
  public class Message_Connection_Factory_Contract
  {
  }

  /**
   * Implementing a Different WebSocket Integration
   * <p>
   * The supported provider boundary is the message-connection SPI, not Net4j's internal JDK or Jetty implementation
   * classes. A client provider must perform its own handshake, emit complete Net4j frames, accept outgoing frames, and
   * report readiness, received data, writability, and closure to the message connection listener. A server provider must
   * perform upgrade routing and pass each accepted transport to <code>IWSAcceptor.accept(IMessageConnection)</code>.
   * <p>
   * On the server, Jetty's adapter demonstrates one integration policy: it scopes a path mapping to an explicit set of
   * active acceptors and requires the path route and <code>Net4jAcceptor</code> header to agree. That policy is specific to
   * the Jetty adapter; do not treat its internal classes as extension APIs.
   * <p>
   * The public <code>IWSConnector</code>, <code>IWSAcceptor</code>, and <code>IMessageConnection</code> contracts are
   * appropriate integration points. Packages named <code>internal</code> are implementation details, even when visible in
   * a developer workspace or test bundle.
   */
  public class Implementing_a_Different_WebSocket_Integration
  {
  }

  /**
   * Threading, Buffering, and Backpressure
   * <p>
   * Providers must treat send buffers as borrowed storage. When <code>send</code> accepts a buffer, it returns
   * <code>true</code> and invokes exactly one terminal callback only after it stops accessing that storage. If it returns
   * <code>false</code> or throws before accepting the buffer, it must not retain the buffer or invoke the callback. It must
   * complete accepted sends during shutdown as well as normal operation.
   * <p>
   * Receive demand counts complete messages. Each delivery consumes one requested message, deliveries remain ordered, and
   * each provided storage lease remains valid until its release action runs. The connector may request more data while an
   * earlier frame is still leased, so a provider must retain each frame's storage independently. Abort must not wait
   * indefinitely for callbacks.
   * <p>
   * Keep provider callbacks non-blocking where the underlying WebSocket API permits asynchronous completion. Do not add
   * another unbounded queue that defeats Net4j's demand and buffer ownership contracts.
   */
  public class Threading_Buffering_and_Backpressure
  {
  }

  /**
   * Lifecycle and Testing
   * <p>
   * A provider factory belongs to its managed container; each connection belongs to one Net4j connector. Connector
   * activation starts its provider connection and connector deactivation closes or aborts it according to the transport
   * lifecycle. A factory must not close a shared <code>HttpClient</code> that belongs to the application.
   * <p>
   * Test factory registration and selection, independent state across two connections, failed handshakes, send acceptance
   * and rejection, exactly-once completion, ordered receive demand, buffer release timing, graceful close, abort, and
   * reactivation. The repository's JDK provider configuration and Jetty server adapter tests are useful references for
   * scenario setup and lifecycle assertions. Run a test through the appropriate Net4j suite rather than relying only on
   * direct construction when validating integration behavior.
   */
  public class Lifecycle_and_Testing
  {
  }
}
