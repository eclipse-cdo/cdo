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
package org.eclipse.net4j.doc.operators;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;
import org.eclipse.spi.net4j.ConnectorFactory;

import javax.net.ssl.SSLContext;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Properties;

/**
 * Migrating the WebSocket Transport
 * <p>
 * Net4j WS 2 separates WebSocket protocol integration from the implementation that performs WebSocket I/O. This article explains
 * how to migrate existing WS and WSS deployments, clients, and Jetty servers to that design. It also identifies the parts that
 * remain compatible so that operators can limit changes to the installations that need them.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class DocXX_MigratingWebSocketTransport
{
  /**
   * Migration Overview
   * <p>
   * Issue #226 replaces a Jetty-specific transport implementation in the provider-neutral WS bundle with a Net4j framing
   * layer and message-connection providers. The built-in provider uses the JDK WebSocket client. A separate optional bundle
   * adapts Jetty 12 servers. This keeps Jetty server dependencies out of the core client transport and lets applications
   * supply clients with their own TLS, proxy, and authentication policy.
   * <p>
   * Migrate an installation when it uses the removed Jetty client classes, subclasses or configures the old
   * <code>Net4jWebSocketServlet</code>, depends on implicit Eclipse proxy discovery, or packages the old Jetty client
   * dependencies as part of Net4j WS. A server without a Jetty host can keep using the WS acceptor API with its own provider
   * integration. The WS/WSS URI schemes, named acceptor routing, Net4j channels, and WS/WSS acceptor configuration model
   * remain. Connector descriptions continue to identify the service endpoint and acceptor route; current descriptions can
   * additionally select a provider and carry handshake cookies.
   * <p>
   * The operator-facing before/after differences are:
   * <ul>
   * <li> Client: replace a connector-managed Jetty EE8 <code>WebSocketClient</code> with the JDK WebSocket provider. Configure
   *      TLS, proxy selection, and authentication on an application-owned <code>HttpClient</code> when the defaults do not fit.
   * <li> Server: replace the old <code>Net4jWebSocketServlet</code> mapping with an application-owned Jetty 12 server and
   *      context containing a handler returned by <code>JettyWSUtil</code>.
   * <li> Proxy: replace the old implicit Eclipse <code>IProxyService</code> lookup with an explicit JDK
   *      <code>ProxySelector</code> and, when needed, <code>Authenticator</code> on the client.
   * <li> Protocol core: Net4j owns framing and routing; a registered provider owns WebSocket I/O. Existing WS/WSS schemes,
   *      named acceptor routing, Net4j channels, and XML acceptor configuration remain available.
   * </ul>
   * <p>
   * Continue with {@link Doc01_DeployingWebSocketServers server deployment},
   * {@link Doc02_ConfiguringWebSocketClients client configuration},
   * {@link Doc03_SecuringWebSocketTransports TLS setup}, and
   * {@link Doc04_OperatingAndTroubleshootingWebSockets operations and troubleshooting}.
   */
  public class Migration_Overview
  {
  }

  /**
   * Dependencies and Deployment
   * <p>
   * Deploy the <code>org.eclipse.net4j.ws</code> feature (or its <code>org.eclipse.net4j.ws</code> bundle) for WS and WSS
   * connectors and acceptors. It contributes the WS and WSS factories and registers the JDK message-connection provider
   * under type <code>jdk</code>. A managed container created
   * with <code>ContainerUtil.createInitializedContainer()</code> receives these contributions through bundle initialization.
   * <p>
   * Remove the old Jetty EE8 WebSocket client and server dependencies that were required only by the former core WS bundle.
   * Do not remove Jetty modules that the application independently needs. For a Jetty-hosted endpoint, add the optional
   * <code>org.eclipse.net4j.ws.jetty.server</code> bundle and compatible Jetty 12 server and WebSocket modules. The
   * <code>org.eclipse.net4j.ws.jetty.server</code> feature includes both the core WS feature and server adapter. That
   * adapter bundle requires Jetty 12; it is not a replacement client dependency.
   * <p>
   * The embedding application now owns the Jetty <code>Server</code>, its connectors and TLS configuration, handler tree,
   * startup and shutdown. It also owns custom JDK <code>HttpClient</code> instances passed to provider factories. Net4j owns
   * the connectors and acceptors registered in its managed container.
   * <p>
   * There is no new XML setting for creating custom JDK clients or embedding the Jetty upgrade handler. Those are Java
   * object configuration tasks; keep XML acceptor declarations where they continue to describe the acceptor. This valid
   * WS acceptor declaration uses the same configuration mechanism as the CDO server configuration:
   * {@link #wsAcceptorXML() ws&#8209;acceptor.xml}
   */
  public class Dependencies_and_Deployment
  {
    /**
     * @snip xml ws-acceptor.xml
     */
    public void wsAcceptorXML()
    {
    }
  }

  /**
   * Migrating WebSocket Clients
   * <p>
   * The JDK provider is selected by default, so a normal WS or WSS connector needs no provider token. Use the utility
   * methods to construct connector descriptions rather than assembling route paths by hand. A description targets the
   * named acceptor at the service URI; optional arguments include provider selection and <code>cookie:name=value</code>
   * handshake cookies. The cookie argument is configuration data, so do not put credentials or session secrets in a
   * persistent or logged connector description.
   * <p>
   * The following example creates its own container and connects one WS connector through the default JDK provider. It
   * closes that connector individually, then deactivates the container it created. For WSS, use the WSS connector factory
   * through <code>WSSUtil</code> together with a <code>wss</code> URI; changing the URI scheme alone does not select the
   * secure connector factory.
   * {@link #defaultJDKClient() DefaultJDKClient.java}
   * <p>
   * For a custom TLS trust configuration or proxy policy, register a provider factory configured with a caller-owned
   * <code>HttpClient</code> in the same container. The example connects a WSS endpoint with the <code>corporate</code>
   * provider, a bounded timeout, and a handshake cookie, then closes the connector and its privately owned container.
   * It uses the JDK default trust context; substitute the application's configured trust context when required.
   * The provider and connector do not close the <code>HttpClient</code>; the application must retain it while any
   * connector can use it. Java 11's client has no close method; on Java 21 and later, close it only after its connectors
   * stop. The example marks that version-specific cleanup without making this Java 11 source depend on the newer method.
   * {@link #configuredJDKClient() ConfiguredJDKClient.java}
   * <p>
   * A connector can be closed without deactivating a shared container. Deactivate a container only when its owner is
   * shutting down the container and all products it manages.
   * <p>
   * <b>Retain the complete factory description.</b> {@link IConnector#getURL()} reports the transport endpoint; for
   * WS/WSS it omits provider selection and handshake cookies. Reconstructing an equivalent connector from that URL may
   * silently select the default <code>jdk</code> provider, losing a custom WSS trust configuration. Retain the original
   * description passed to <code>WSUtil</code>/<code>WSSUtil</code>, or ask the registered connector factory for its
   * description with {@link IFactory#getDescriptionFor(Object)}. For example, use the factory for
   * {@link ConnectorFactory#PRODUCT_GROUP} and the WS or WSS factory type selected from the connector's scheme.
   * {@link #completeConnectorDescription() CompleteConnectorDescription.java}
   */
  public class Migrating_WebSocket_Clients
  {
    /**
     * Connects a WS endpoint through a privately owned initialized container.
     *
     * @snip
     */
    public void defaultJDKClient()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSConnector ws = null;

      try
      {
        ws = WSUtil.getConnector(container, URI.create("ws://gateway.example.net/net4j"), "production");
        ws.connect(5_000);
        // Use the connected WS connector here.
      }
      finally
      {
        try
        {
          if (ws != null)
          {
            ws.close();
          }
        }
        finally
        {
          LifecycleUtil.deactivate(container);
        }
      }
    }

    /**
     * Registers a private provider and uses it for a secure connector.
     *
     * @snip
     */
    public void configuredJDKClient()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSConnector connector = null;
      HttpClient httpClient = null;

      try
      {
        SSLContext sslContext = SSLContext.getDefault();
        httpClient = HttpClient.newBuilder().sslContext(sslContext).proxy(ProxySelector.of(new InetSocketAddress("proxy.example.net", 8080)))
            .connectTimeout(Duration.ofSeconds(10)).build();

        IFactory provider = JDKWSUtil.createMessageConnectionFactory("corporate", httpClient, Duration.ofSeconds(10));
        container.registerFactory(provider);

        String description = WSSUtil.getConnectorDescription(URI.create("wss://gateway.example.net/net4j"), "production", "provider:corporate",
            "cookie:tenant=example");
        connector = WSSUtil.getConnector(container, description);
        connector.connect(5_000);
        // Use the connected WSS connector here.
      }
      catch (NoSuchAlgorithmException ex)
      {
        throw new IllegalStateException(ex);
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
        // On Java 21+, close httpClient here after the connector and container have stopped.
      }
    }

    /**
     * Obtains the complete WSS factory description and places it in application configuration storage.
     *
     * @snip
     */
    public void completeConnectorDescription()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      try
      {
        HttpClient httpClient = HttpClient.newBuilder().build();
        IFactory provider = JDKWSUtil.createMessageConnectionFactory("corporate", httpClient, Duration.ofSeconds(10));
        container.registerFactory(provider);

        IWSConnector connector = WSSUtil.getConnector(container, URI.create("wss://gateway.example.net/net4j"), "production", "provider:corporate",
            "cookie:tenant=example");
        IFactory factory = container.getFactory(ConnectorFactory.PRODUCT_GROUP, WSSUtil.FACTORY_TYPE);
        String completeDescription = factory.getDescriptionFor(connector);
        Properties connectorSettings = new Properties();
        connectorSettings.setProperty("wss.connector", completeDescription);

        // Hand connectorSettings to protected application storage; the description may contain sensitive cookies.
      }
      finally
      {
        LifecycleUtil.deactivate(container);
      }
    }
  }

  /**
   * Migrating WebSocket Servers
   * <p>
   * The previous <code>Net4jWebSocketServlet</code> and Jetty socket endpoint were removed from the core WS bundle. With
   * Jetty 12, create an application-owned <code>Server</code> and <code>ContextHandler</code>, then install the returned
   * upgrade handler before starting the server. Use a mapping such as <code>/net4j/*</code>; the client appends
   * <code>/@&lt;acceptor-name&gt;</code> to that path. The utility wraps the context's existing application handler and routes
   * only to the active acceptors passed to it. Both the decoded path suffix and the <code>Net4jAcceptor</code> handshake
   * header must identify the same route.
   * <p>
   * The following example creates an initialized Net4j container and named WS acceptor, installs the adapter in an
   * application-owned Jetty server/context, starts the server, and connects a bounded-timeout client. The adapter only
   * creates the upgrade handler; the application starts and stops Jetty. It closes the individual client connector, stops
   * Jetty, then deactivates the acceptor and the privately owned container. A host with a shared container should close
   * the connector and acceptor as appropriate but leave container shutdown to its owner. WSS uses the same integration
   * with a WSS acceptor, a WSS connector, and an application-configured HTTPS server connector.
   * {@link #installJetty12UpgradeHandler() Jetty12UpgradeHandler.java}
   */
  public class Migrating_WebSocket_Servers
  {
    /**
     * @snip
     */
    public void installJetty12UpgradeHandler()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSAcceptor acceptor = WSUtil.getAcceptor(container, "production");

      Server server = new Server();
      ServerConnector serverConnector = new ServerConnector(server);
      serverConnector.setHost("127.0.0.1");
      serverConnector.setPort(0);
      server.addConnector(serverConnector);

      ContextHandler context = new ContextHandler("/");
      IWSConnector clientConnector = null;

      try
      {
        LifecycleUtil.activate(acceptor);

        WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor);
        context.setHandler(upgradeHandler);
        server.setHandler(context);
        server.start();

        URI serviceURI = URI.create("ws://127.0.0.1:" + serverConnector.getLocalPort() + "/net4j");
        clientConnector = WSUtil.getConnector(container, serviceURI, "production");
        clientConnector.connect(5_000);
        // Exchange Net4j channels while the server and connector remain active.
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Unable to start the WebSocket endpoint", ex);
      }
      finally
      {
        try
        {
          if (clientConnector != null)
          {
            clientConnector.close();
          }
        }
        finally
        {
          try
          {
            server.stop();
          }
          catch (Exception ex)
          {
            throw new IllegalStateException("Unable to stop the application-owned Jetty server", ex);
          }
          finally
          {
            try
            {
              LifecycleUtil.deactivate(acceptor);
            }
            finally
            {
              LifecycleUtil.deactivate(container);
            }
          }
        }
      }
    }
  }

  /**
   * Migrating Security and Proxy Configuration
   * <p>
   * Configure TLS at the layer that owns the connection. A WSS client can use the default JDK TLS context, or a custom
   * <code>SSLContext</code> configured with the application's trust material. The JDK provider keeps normal certificate
   * chain and hostname verification enabled. Never work around trust failures by disabling verification. A Jetty-hosted
   * WSS endpoint uses the TLS configuration of the application's HTTPS server connector; the Net4j adapter does not create
   * or own TLS connectors.
   * <p>
   * HTTP origin authentication and proxy authentication belong in the caller-configured <code>HttpClient</code>, typically
   * through its <code>Authenticator</code>, <code>ProxySelector</code>, and TLS settings. WS transport no longer discovers
   * Eclipse <code>IProxyService</code> implicitly. Applications that relied on Eclipse proxy preferences must translate
   * the required policy into JDK client configuration and register a provider factory with that client.
   * <p>
   * For an HTTP CONNECT proxy, configure a proxy selector and an authenticator for proxy credentials. The JDK normally
   * disables Basic authentication for HTTP CONNECT tunneling. Enabling it with
   * <code>-Djdk.http.auth.tunneling.disabledSchemes=</code> changes JVM-wide behavior and can expose credentials to
   * untrusted proxies; use it only after reviewing the security consequences and the proxy trust boundary. Prefer a proxy
   * authentication scheme supported by the JDK without that override.
   */
  public class Migrating_Security_and_Proxy_Configuration
  {
  }

  /**
   * Migration Checklist
   * <p>
   * Work through this sequence for each client or server installation:
   * <ol>
   * <li> Inventory the WS/WSS bundles, Jetty modules, servlet mappings, client factories, TLS settings, proxy policy, and
   *      connector descriptions in the deployed product.
   * <li> Deploy the provider-neutral <code>org.eclipse.net4j.ws</code> bundle. Remove Jetty EE8 client dependencies that
   *      existed only for the former Net4j WS implementation.
   * <li> For clients, replace direct Jetty WebSocket client use with Net4j WS/WSS connectors. Add a custom JDK provider
   *      only when the application requires client-specific TLS, proxy, or authentication settings.
   * <li> For Jetty servers, deploy the optional Jetty 12 adapter, replace the old servlet integration with
   *      <code>JettyWSUtil.createUpgradeHandler</code>, and ensure each route matches the URI scheme and TLS state.
   * <li> Move proxy, TLS trust, HTTP origin credentials, and proxy credentials into application-owned client/server
   *      configuration. Review any Basic CONNECT JVM property with security owners.
   * <li> Verify route names, cookies, container startup, and ownership of custom clients and servers. Exercise both WS and
   *      WSS endpoints through the production routing and shutdown path.
   * <li> Remove obsolete servlet classes, old Jetty client configuration, and bundle requirements only after every
   *      application consumer has migrated.
   * </ol>
   */
  public class Migration_Checklist
  {
  }

  /**
   * Troubleshooting
   * <p>
   * <b>Missing provider factory:</b> check that the <code>org.eclipse.net4j.ws</code> bundle is resolved and its factories
   * are initialized in the same managed container that creates the connector. For a custom provider, register its factory
   * before connector creation and ensure the <code>provider:</code> token exactly matches the registered type.
   * <p>
   * <b>HTTP upgrade or routing rejection:</b> verify the path mapping covers the service path and appended
   * <code>/@name</code> route, the acceptor is active and registered under that name, and the handshake header and route
   * agree. A request rejected with 404 commonly indicates a missing or mismatched route; 403 commonly indicates that the
   * request's secure state does not match the WS/WSS acceptor.
   * <p>
   * <b>TLS certificate validation failure:</b> check the server certificate chain, the hostname in the service URI, and
   * the trust material used by the client provider. Keep hostname and certificate verification enabled.
   * <p>
   * <b>Authenticated proxy failure:</b> ensure the caller-configured client actually selects the proxy and its
   * authenticator supplies credentials for that proxy. If the proxy requires Basic CONNECT authentication, account for
   * the JDK's disabled-by-default behavior and assess the JVM-wide property before enabling it.
   * <p>
   * <b>Leaked or prematurely closed connections:</b> the Net4j container owns connectors and acceptors, while the
   * application owns custom <code>HttpClient</code> and Jetty server lifecycles. Stop connectors before closing a shared
   * client, deactivate the container during shutdown, and stop the Jetty server through the application's lifecycle.
   */
  public class Troubleshooting
  {
  }
}
