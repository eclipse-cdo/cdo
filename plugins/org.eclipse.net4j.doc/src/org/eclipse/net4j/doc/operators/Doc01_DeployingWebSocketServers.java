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
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import java.net.URI;

/**
 * Deploying WebSocket Servers
 * <p>
 * This article shows how to host Net4j WS and WSS acceptors inside an application-managed Jetty 12.1 server. Net4j supplies
 * named acceptors and routes accepted message connections into its connector lifecycle. The application remains responsible
 * for Jetty listeners, TLS, handler composition, startup, and shutdown.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc01_DeployingWebSocketServers
{
  /**
   * Features, Bundles, and Acceptor Configuration
   * <p>
   * Install the <code>org.eclipse.net4j.ws</code> feature for the provider-neutral WS/WSS factories and the JDK client
   * provider. Install <code>org.eclipse.net4j.ws.jetty.server</code> when hosting on Jetty; its feature includes the core
   * feature and the Jetty adapter and requires Jetty 12.1 server and WebSocket modules. Keep the Jetty server lifecycle
   * with the application that embeds it.
   * <p>
   * WS and WSS acceptor entries in the CDO server configuration create named Net4j acceptor products. They do not create
   * Jetty listeners or configure certificates. The following XML is a supported declaration of those named products:
   * {@link #serverAcceptorXML() WebSocketAcceptors.xml}
   * <p>
   * A managed container can also create products programmatically. <code>WSUtil.getAcceptor</code> and
   * <code>WSSUtil.getAcceptor</code> obtain the corresponding factory type; activation registers the active acceptor name
   * in the Net4j acceptor manager. Use distinct names for separately exposed routes.
   */
  public class Features_Bundles_and_Acceptors
  {
    /**
     * @snip xml ws-server-acceptors.xml
     */
    public void serverAcceptorXML()
    {
    }
  }

  /**
   * Hosting WS over HTTP
   * <p>
   * Create an initialized managed container, obtain and activate an acceptor, then construct an application-owned Jetty
   * <code>Server</code>, <code>ServerConnector</code>, and <code>ContextHandler</code>. Install
   * <code>JettyWSUtil.createUpgradeHandler</code> before starting Jetty. Its path spec must cover the service path and the
   * appended <code>/@&lt;acceptor-name&gt;</code> route; <code>/net4j/*</code> is a common mapping. The example binds to an
   * ephemeral loopback port and performs a bounded client connection to show the routing end to end.
   * {@link #deployJettyWS() DeployJettyWS.java}
   * <p>
   * The utility requires active, uniquely named acceptors and snapshots the supplied acceptor set for this handler. Each
   * upgrade must have a decoded path suffix and a <code>Net4jAcceptor</code> header naming the same acceptor. Requests to
   * an unknown or mismatched route receive 404; an insecure request sent to a WSS acceptor, or a secure request sent to a
   * WS acceptor, is rejected with 403.
   */
  public class Hosting_WS_over_HTTP
  {
    /**
     * Starts a private Jetty server and connects to its named WS acceptor.
     *
     * @snip
     */
    public void deployJettyWS()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSAcceptor acceptor = null;
      IWSConnector client = null;
      Server server = null;
      Exception startupFailure = null;

      try
      {
        acceptor = WSUtil.getAcceptor(container, "public");
        LifecycleUtil.activate(acceptor);

        server = new Server();
        ServerConnector listener = new ServerConnector(server);
        listener.setHost("127.0.0.1");
        listener.setPort(0);
        server.addConnector(listener);

        ContextHandler context = new ContextHandler("/");
        WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor);
        context.setHandler(upgradeHandler);
        server.setHandler(context);
        server.start();

        URI serviceURI = URI.create("ws://127.0.0.1:" + listener.getLocalPort() + "/net4j");
        client = WSUtil.getConnector(container, serviceURI, "public");
        client.connect(5_000);
        // Use Net4j channels while the client and server remain active.
      }
      catch (Exception ex)
      {
        startupFailure = ex;
        throw new IllegalStateException("Unable to start the WS endpoint", ex);
      }
      finally
      {
        try
        {
          if (client != null)
          {
            client.close();
          }
        }
        finally
        {
          try
          {
            if (server != null)
            {
              try
              {
                server.stop();
              }
              catch (Exception stopFailure)
              {
                if (startupFailure != null)
                {
                  startupFailure.addSuppressed(stopFailure);
                }
                else
                {
                  throw new IllegalStateException("Unable to stop the application-owned Jetty server", stopFailure);
                }
              }
            }
          }
          finally
          {
            try
            {
              if (acceptor != null)
              {
                LifecycleUtil.deactivate(acceptor);
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
  }

  /**
   * Hosting WSS over HTTPS
   * <p>
   * WSS requires a WSS acceptor and a Jetty HTTPS listener. Configure the listener with an application-managed
   * <code>SslContextFactory.Server</code>, a key store containing the server private key and certificate chain, and a
   * <code>SecureRequestCustomizer</code>. Install the Net4j handler after activating the acceptor so the adapter can verify
   * the route. The example reads the key-store location and secret from deployment configuration; it does not disable
   * certificate or hostname validation.
   * {@link #deployJettyWSS() DeployJettyWSS.java}
   * <p>
   * The certificate must be valid for the host name clients use and must chain to a trust anchor they accept. Configure the
   * server identity with the <code>net4j.tls.server.keyStore</code> system property and
   * <code>NET4J_TLS_SERVER_KEYSTORE_PASSWORD</code> secret. Store passwords in a secret manager or protected environment
   * configuration, never in source control. Mutual TLS can be enabled
   * on the Jetty SSL context with <code>setNeedClientAuth(true)</code> and on the client's <code>SSLContext</code> with
   * client key managers when the deployment requires client certificates.
   * <p>
   * In normal application-controlled shutdown, the owner signals or interrupts this lifecycle thread so it leaves
   * <code>server.join()</code>. The <code>finally</code> block then stops Jetty before deactivating the Net4j acceptor and
   * its owned container.
   * <p>
   * Jetty's <code>setStopAtShutdown(true)</code> hook stops Jetty during JVM shutdown, but does not guarantee that the
   * waiting application thread resumes and executes this method's <code>finally</code> block. Do not rely on that hook for
   * Net4j cleanup. An embedded Jetty deployment must retain the server and coordinate Jetty and Net4j shutdown through the
   * embedding application's lifecycle.
   */
  public class Hosting_WSS_over_HTTPS
  {
    /**
     * Configures a WSS acceptor behind an application-owned Jetty HTTPS listener.
     *
     * @snip
     */
    public void deployJettyWSS()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSAcceptor acceptor = null;
      Server server = null;
      Exception startupFailure = null;

      try
      {
        acceptor = WSSUtil.getAcceptor(container, "secure");
        LifecycleUtil.activate(acceptor);

        String keyStorePath = System.getProperty("net4j.tls.server.keyStore");
        String keyStorePassword = System.getenv("NET4J_TLS_SERVER_KEYSTORE_PASSWORD");
        if (keyStorePath == null || keyStorePath.isBlank() || keyStorePassword == null || keyStorePassword.isBlank())
        {
          throw new IllegalStateException("Set net4j.tls.server.keyStore and provide NET4J_TLS_SERVER_KEYSTORE_PASSWORD");
        }

        server = new Server();
        server.setStopAtShutdown(true);

        SslContextFactory.Server sslContext = new SslContextFactory.Server();
        sslContext.setKeyStorePath(keyStorePath);
        sslContext.setKeyStorePassword(keyStorePassword);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        ServerConnector listener = new ServerConnector(server, new SslConnectionFactory(sslContext, HttpVersion.HTTP_1_1.toString()),
            new HttpConnectionFactory(https));
        listener.setPort(8443);
        server.addConnector(listener);

        ContextHandler context = new ContextHandler("/");
        WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor);
        context.setHandler(upgradeHandler);
        server.setHandler(context);
        server.start();

        URI endpoint = URI.create("wss://service.example.net:" + listener.getLocalPort() + "/net4j");
        System.getLogger(Doc01_DeployingWebSocketServers.class.getName()).log(System.Logger.Level.INFO, "WSS listening at {0}", endpoint);
        server.join();
      }
      catch (Exception ex)
      {
        if (ex instanceof InterruptedException)
        {
          Thread.currentThread().interrupt();
        }

        startupFailure = ex;
        throw new IllegalStateException("Unable to start the WSS endpoint", ex);
      }
      finally
      {
        try
        {
          if (server != null)
          {
            try
            {
              server.stop();
            }
            catch (Exception stopFailure)
            {
              if (startupFailure != null)
              {
                startupFailure.addSuppressed(stopFailure);
              }
              else
              {
                throw new IllegalStateException("Unable to stop the application-owned Jetty server", stopFailure);
              }
            }
          }
        }
        finally
        {
          try
          {
            if (acceptor != null)
            {
              LifecycleUtil.deactivate(acceptor);
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
   * Multiple Acceptors and Routing Isolation
   * <p>
   * One upgrade handler can expose several active acceptors. Pass only the names intended for that Jetty context; the Jetty
   * adapter snapshots this explicit allow-list and does not choose an arbitrary acceptor from the process-wide registry.
   * Use separate contexts, mappings, or servers for trust boundaries that must not share an acceptor set. Each request still
   * has to match the selected acceptor name in both its route path and <code>Net4jAcceptor</code> header.
   * <p>
   * The following excerpt shows the relevant public API call. The acceptors must already be active in the container and
   * the handler must be installed before Jetty starts.
   * {@link #routeSeveralAcceptors() MultipleAcceptors.java}
   */
  public class Multiple_Acceptors_and_Routing_Isolation
  {
    /**
     * Creates two active routes and scopes them to one application-owned Jetty context.
     *
     * @snip
     */
    public void routeSeveralAcceptors()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSAcceptor publicAcceptor = WSUtil.getAcceptor(container, "public");
      IWSAcceptor partnerAcceptor = WSUtil.getAcceptor(container, "partner");

      try
      {
        LifecycleUtil.activate(publicAcceptor);
        LifecycleUtil.activate(partnerAcceptor);

        Server server = new Server();
        ContextHandler context = new ContextHandler("/");
        WebSocketUpgradeHandler handler = JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", publicAcceptor, partnerAcceptor);
        context.setHandler(handler);
      }
      finally
      {
        LifecycleUtil.deactivate(container);
      }
    }
  }

  /**
   * Startup, Shutdown, and Failure Cleanup
   * <p>
   * Start the Net4j container and activate each acceptor before creating the Jetty upgrade handler. Start Jetty only after
   * its listeners and handler tree are complete. During shutdown, stop admission and drain application work, close any
   * client connectors, stop the Jetty server, deactivate acceptors, and finally deactivate a container owned by this
   * application. Do not deactivate a shared container as a way to close one connector.
   * <p>
   * If startup fails after Jetty begins initialization, stop the partially started server and release the acceptor and
   * container in nested cleanup paths. A failed server start must not leave its acceptor registered as an active route.
   */
  public class Startup_Shutdown_and_Failure_Cleanup
  {
  }
}
