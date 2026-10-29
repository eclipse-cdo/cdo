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

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;

import org.eclipse.spi.net4j.ConnectorFactory;

import javax.net.ssl.SSLContext;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Properties;

/**
 * Configuring WebSocket Clients
 * <p>
 * The core WS bundle registers a JDK WebSocket message-connection provider named <code>jdk</code>. Connector factories
 * select that provider by default. Applications can register additional provider factories to bind a client, TLS context,
 * proxy policy, authentication policy, or opening-handshake timeout to a distinct provider name.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_ConfiguringWebSocketClients
{
  /**
   * Default Provider and Connector Descriptions
   * <p>
   * Create connectors with <code>WSUtil</code> for WS and <code>WSSUtil</code> for WSS. A secure connection needs both
   * the WSS connector factory and a <code>wss</code> service URI. The named acceptor is encoded in the route path. With no
   * provider token, the factory selects <code>jdk</code>.
   * {@link #connectDefaultWSAndWSS() DefaultWSAndWSS.java}
   * <p>
   * Connector descriptions can include <code>provider:&lt;type&gt;</code> to select a registered message-connection
   * factory and <code>cookie:&lt;name&gt;=&lt;value&gt;</code> for a handshake cookie. Use the utility description methods
   * instead of manually constructing the route or delimiter escaping. Descriptions are configuration data; protect any
   * saved description that contains a session cookie.
   */
  public class Default_Provider_and_Connector_Descriptions
  {
    /**
     * Connects WS and WSS endpoints using the automatically registered JDK provider.
     *
     * @snip
     */
    public void connectDefaultWSAndWSS()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSConnector ws = null;
      IWSConnector wss = null;

      try
      {
        ws = WSUtil.getConnector(container, URI.create("ws://gateway.example.net/net4j"), "public");
        wss = WSSUtil.getConnector(container, URI.create("wss://gateway.example.net/net4j"), "secure");

        ws.connect(5_000);
        wss.connect(5_000);
        // Use the connected transports while the connectors remain active.
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
          try
          {
            if (wss != null)
            {
              wss.close();
            }
          }
          finally
          {
            LifecycleUtil.deactivate(container);
          }
        }
      }
    }
  }

  /**
   * Registering Configured Providers
   * <p>
   * Use <code>JDKWSUtil.createMessageConnectionFactory(type, client, timeout)</code> to configure a provider with a
   * caller-owned <code>HttpClient</code>. Register the factory in the same managed container that creates the connector,
   * and select the registered type with the provider token. Different provider names allow the same application to use
   * distinct trust stores or proxy routes without changing global JDK settings.
   * <p>
   * The following example uses an explicit proxy selector and handshake cookie. It uses the default JDK trust context as
   * a placeholder; use an application-configured trust context for private certificate authorities.
   * {@link #connectWithConfiguredProvider() ConfiguredProvider.java}
   * <p>
   * The HTTP client's <code>connectTimeout</code> limits the connection establishment stage. The provider's
   * <code>webSocketConnectTimeout</code> limits the subsequent opening handshake. A connector's
   * <code>connect(5000)</code> bounds how long the calling thread waits for the Net4j connector to become connected; these
   * are separate limits. The built-in provider defaults both network stages to ten seconds. Use
   * <code>JDKWSOptions.builder()</code> with the <code>HttpClient</code> or <code>SSLContext</code> factory overload to set
   * the opening-handshake timeout, heartbeat interval, and maximum message size. Defaults are ten seconds, twenty seconds,
   * and <code>Short.MAX_VALUE</code> bytes respectively. The message limit includes the Net4j frame header and must remain
   * within the short-sized Net4j framing limit. Options belong to one provider factory and are applied to all of its newly
   * created connections; separate provider types can use different options concurrently. The heartbeat is an unsolicited
   * WebSocket Pong sent periodically while the connection is open. Set its interval below the server's WebSocket idle timeout
   * to keep an otherwise quiet connection alive. It is cancelled when graceful closure or termination begins.
   */
  public class Registering_Configured_Providers
  {
    /**
     * Registers a caller-configured client and connects through the named provider.
     *
     * @snip
     */
    public void connectWithConfiguredProvider()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSConnector connector = null;
      HttpClient httpClient = null;

      try
      {
        SSLContext tls = SSLContext.getDefault();
        httpClient = HttpClient.newBuilder().sslContext(tls).proxy(ProxySelector.of(new InetSocketAddress("proxy.example.net", 8080)))
            .connectTimeout(Duration.ofSeconds(8)).build();
        IFactory provider = JDKWSUtil.createMessageConnectionFactory("corporate", httpClient, Duration.ofSeconds(12));
        container.registerFactory(provider);

        String description = WSSUtil.getConnectorDescription(URI.create("wss://gateway.example.net/net4j"), "secure", "provider:corporate",
            "cookie:tenant=example");
        connector = WSSUtil.getConnector(container, description);
        connector.connect(15_000);
        // Use the connector while its container and caller-owned HttpClient remain alive.
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Unable to configure or connect the WSS client", ex);
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

        // On Java 21+, close httpClient here after all connectors that use it have stopped.
      }
    }
  }

  /**
   * Cookies, Authentication, and Proxy Tunnels
   * <p>
   * Handshake cookies are sent to the WebSocket endpoint during the HTTP upgrade. Treat them like credentials: limit their
   * scope and lifetime, use WSS, protect connector descriptions that contain them, and do not log full descriptions.
   * <p>
   * Configure HTTP origin and proxy authentication on the JDK <code>Authenticator</code>. The requestor type distinguishes
   * a server challenge from a proxy challenge; do not send origin credentials to a proxy or vice versa. The application
   * should obtain usernames and passwords from its credential store rather than embedding them in code. An authenticator
   * can serve several destinations, so restrict each credential to the expected requestor host and port and return
   * <code>null</code> for every unknown endpoint.
   * <p>
   * For HTTP CONNECT, the JDK normally disables Basic authentication for tunneling. The system property
   * <code>jdk.http.auth.tunneling.disabledSchemes</code> controls disabled schemes. Setting it to an empty value can enable
   * Basic CONNECT authentication, but it changes behavior JVM-wide and may expose credentials to an untrusted proxy.
   * Review that boundary carefully and prefer a supported stronger proxy scheme where possible.
   * {@link #configureChallengeAuthentication() ChallengeAuthentication.java}
   */
  public class Cookies_Authentication_and_Proxy_Tunnels
  {
    /**
     * Supplies credentials only to the configured WSS origin and HTTP proxy.
     *
     * @snip
     */
    public void configureChallengeAuthentication()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      HttpClient httpClient = null;
      IWSConnector connector = null;

      try
      {
        String originUser = System.getenv("NET4J_ORIGIN_USER");
        String originPassword = System.getenv("NET4J_ORIGIN_PASSWORD");
        String proxyUser = System.getenv("NET4J_PROXY_USER");
        String proxyPassword = System.getenv("NET4J_PROXY_PASSWORD");
        if (originUser == null || originPassword == null || proxyUser == null || proxyPassword == null)
        {
          throw new IllegalStateException("Provide origin and proxy credentials through the deployment secret store");
        }

        Authenticator authenticator = new Authenticator()
        {
          @Override
          protected PasswordAuthentication getPasswordAuthentication()
          {
            String requestingHost = getRequestingHost();
            int requestingPort = getRequestingPort();

            if (getRequestorType() == Authenticator.RequestorType.PROXY && "proxy.example.net".equalsIgnoreCase(requestingHost) && requestingPort == 8080)
            {
              return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
            }

            if (getRequestorType() == Authenticator.RequestorType.SERVER && "gateway.example.net".equalsIgnoreCase(requestingHost) && requestingPort == 443)
            {
              return new PasswordAuthentication(originUser, originPassword.toCharArray());
            }

            // Never supply credentials to an unknown host, port, or requestor type.
            return null;
          }
        };

        httpClient = HttpClient.newBuilder().proxy(ProxySelector.of(new InetSocketAddress("proxy.example.net", 8080))).authenticator(authenticator)
            .connectTimeout(Duration.ofSeconds(8)).build();
        IFactory provider = JDKWSUtil.createMessageConnectionFactory("authenticated", httpClient, Duration.ofSeconds(10));
        container.registerFactory(provider);
        connector = WSSUtil.getConnector(container, URI.create("wss://gateway.example.net/net4j"), "secure", "provider:authenticated");
        connector.connect(15_000);
        // Use the connector while the provider, container, and caller-owned client remain alive.
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Unable to configure authenticated WSS", ex);
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
          // On Java 21+, close httpClient here after all connectors that use it have stopped.
        }
      }
    }
  }

  /**
   * Reconstructing Connectors Safely
   * <p>
   * <code>IConnector.getURL()</code> reports the endpoint URL, not the complete WS/WSS factory description. It omits the
   * selected provider and handshake cookies. Rebuilding from that URL can silently use <code>jdk</code> and lose a custom
   * TLS trust policy. Retain the original description or ask the registered connector factory for
   * <code>IFactory.getDescriptionFor(connector)</code>. Select the WS or WSS factory based on the connector's scheme.
   * <p>
   * The example retains the complete string in application configuration. It does not claim to test a restoration by
   * looking up the product again in the same container. Protect the stored value because it may contain cookie values.
   * {@link #retainCompleteWSSDescription() CompleteWSSDescription.java}
   */
  public class Reconstructing_Connectors_Safely
  {
    /**
     * Obtains and retains the complete factory description for a configured WSS connector.
     *
     * @snip
     */
    public void retainCompleteWSSDescription()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();

      try
      {
        HttpClient httpClient = HttpClient.newBuilder().build();
        IFactory provider = JDKWSUtil.createMessageConnectionFactory("corporate", httpClient, Duration.ofSeconds(10));
        container.registerFactory(provider);

        IWSConnector connector = WSSUtil.getConnector(container, URI.create("wss://gateway.example.net/net4j"), "secure", "provider:corporate",
            "cookie:tenant=example");
        IFactory connectorFactory = container.getFactory(ConnectorFactory.PRODUCT_GROUP, WSSUtil.FACTORY_TYPE);
        String completeDescription = connectorFactory.getDescriptionFor(connector);

        Properties protectedConfiguration = new Properties();
        protectedConfiguration.setProperty("connector.wss.secure", completeDescription);
        // Store protectedConfiguration with the application's credential-aware configuration service.
        // On Java 21+, close httpClient after all connectors using it have stopped.
      }
      finally
      {
        LifecycleUtil.deactivate(container);
      }
    }
  }
}
