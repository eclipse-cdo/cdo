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
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;
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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;

/**
 * Securing WebSocket Transports
 * <p>
 * WSS secures the HTTP WebSocket opening handshake and the resulting transport with TLS. Net4j selects the WSS connector
 * and acceptor factories; TLS trust, server certificates, and client identity are configured on the JDK client and the
 * embedding server. Certificate validation and host-name verification remain enabled by default.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_SecuringWebSocketTransports
{
  /**
   * Client Trust Configuration
   * <p>
   * A WSS client must trust the server certificate chain and the requested DNS name must match a certificate subject
   * alternative name. The JDK provider uses normal JDK trust when given a default client; to trust a private CA, load the
   * approved CA certificates into the client trust store named by <code>net4j.tls.client.trustStore</code> and provide its
   * password through <code>NET4J_TLS_CLIENT_TRUSTSTORE_PASSWORD</code>. Build an <code>SSLContext</code> with a
   * <code>TrustManagerFactory</code>. Pass that context to a configured <code>HttpClient</code> and register the client
   * under a distinct provider type.
   * {@link #createTrustedWSSClient() TrustedWSSClient.java}
   * <p>
   * Do not install a trust-all manager, disable endpoint identification, or replace a hostname with an unrelated address
   * merely to suppress a certificate error. Correct the certificate chain, URI host name, or intended trust anchors.
   */
  public class Client_Trust_Configuration
  {
    /**
     * Builds a WSS provider from a deployment-supplied trust store.
     *
     * @snip
     */
    public void createTrustedWSSClient()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSConnector connector = null;
      HttpClient httpClient = null;

      try
      {
        String trustStoreLocation = System.getProperty("net4j.tls.client.trustStore");
        String trustStoreSecret = System.getenv("NET4J_TLS_CLIENT_TRUSTSTORE_PASSWORD");
        if (trustStoreLocation == null || trustStoreLocation.isBlank() || trustStoreSecret == null || trustStoreSecret.isBlank())
        {
          throw new IllegalStateException("Set net4j.tls.client.trustStore and provide NET4J_TLS_CLIENT_TRUSTSTORE_PASSWORD");
        }

        Path trustStorePath = Path.of(trustStoreLocation);
        char[] trustStorePassword = trustStoreSecret.toCharArray();
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        try (InputStream input = Files.newInputStream(trustStorePath))
        {
          trustStore.load(input, trustStorePassword);
        }

        TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagers.init(trustStore);
        SSLContext tls = SSLContext.getInstance("TLS");
        tls.init(null, trustManagers.getTrustManagers(), new SecureRandom());

        httpClient = HttpClient.newBuilder().sslContext(tls).connectTimeout(Duration.ofSeconds(10)).build();
        IFactory provider = JDKWSUtil.createMessageConnectionFactory("private-ca", httpClient, Duration.ofSeconds(10));
        container.registerFactory(provider);
        connector = WSSUtil.getConnector(container, URI.create("wss://service.example.net/net4j"), "secure", "provider:private-ca");
        connector.connect(15_000);
        // Keep the caller-owned HttpClient alive until the connector has stopped.
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Unable to configure the trusted WSS client", ex);
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
          // On Java 21+, close httpClient here after all of its connectors have stopped.
        }
      }
    }
  }

  /**
   * Server Certificates and HTTPS Listeners
   * <p>
   * Jetty owns the TLS listener and certificate material. Configure its <code>SslContextFactory.Server</code> with the
   * server key store named by <code>net4j.tls.server.keyStore</code> and password from
   * <code>NET4J_TLS_SERVER_KEYSTORE_PASSWORD</code>. Keep this separate from client identity and trust stores. Place a
   * <code>SecureRequestCustomizer</code> on
   * the HTTPS connection factory. Then expose the WSS acceptor through <code>JettyWSUtil</code>. The TLS listener setup is
   * programmatic; the Net4j acceptor XML has no invented certificate attributes.
   * {@link #configureJettyWSSListener() JettyWSSListener.java}
   * <p>
   * The server certificate must cover the public service name and include any required intermediates. Configure file access
   * and passwords through deployment-managed secrets. For mutual TLS, Jetty can require a client certificate and the client
   * can provide its own key managers; only enable this when the application also maps authenticated client identities to
   * authorization decisions.
   */
  public class Server_Certificates_and_HTTPS_Listeners
  {
    /**
     * Builds the TLS connector and secure request state required by the WSS adapter.
     *
     * @snip
     */
    public void configureJettyWSSListener()
    {
      String keyStorePath = System.getProperty("net4j.tls.server.keyStore");
      String keyStorePassword = System.getenv("NET4J_TLS_SERVER_KEYSTORE_PASSWORD");
      if (keyStorePath == null || keyStorePath.isBlank() || keyStorePassword == null || keyStorePassword.isBlank())
      {
        throw new IllegalStateException("Set net4j.tls.server.keyStore and provide NET4J_TLS_SERVER_KEYSTORE_PASSWORD");
      }

      Server server = new Server();
      SslContextFactory.Server ssl = new SslContextFactory.Server();
      ssl.setKeyStorePath(keyStorePath);
      ssl.setKeyStorePassword(keyStorePassword);

      HttpConfiguration https = new HttpConfiguration();
      https.addCustomizer(new SecureRequestCustomizer());
      ServerConnector httpsListener = new ServerConnector(server, new SslConnectionFactory(ssl, HttpVersion.HTTP_1_1.toString()),
          new HttpConnectionFactory(https));
      httpsListener.setPort(8443);
      server.addConnector(httpsListener);

      // Install JettyWSUtil's upgrade handler in a context before starting this application-owned server.
    }
  }

  /**
   * Mutual TLS
   * <p>
   * Mutual TLS is supported by the standard TLS components around the transport. Configure Jetty with its server identity,
   * a trust store containing only the client CA certificates accepted by this endpoint, and required client authentication.
   * The client must present an identity certificate and validate the server certificate. Net4j does not interpret certificate
   * identities or replace application authorization; map the verified identity to application permissions after the handshake.
   * Client identity material uses <code>net4j.tls.client.keyStore</code>, with the store and private-key passwords supplied
   * by <code>NET4J_TLS_CLIENT_KEYSTORE_PASSWORD</code> and <code>NET4J_TLS_CLIENT_KEY_PASSWORD</code>. The client trust store
   * is <code>net4j.tls.client.trustStore</code>. The server identity uses <code>net4j.tls.server.keyStore</code>; the server's
   * client-CA trust store uses <code>net4j.tls.server.clientTrustStore</code> and
   * <code>NET4J_TLS_SERVER_CLIENT_TRUSTSTORE_PASSWORD</code>. Passwords are deployment secrets, never property values or log data.
   * {@link #createMutualTLSContext() MutualTLSContext.java}
   * {@link #deployMutualTLSWSS() MutualTLSWSS.java}
   * <p>
   * During normal application-controlled shutdown, stop Jetty first, then deactivate the WSS acceptor and its owned
   * container. The standalone examples' JVM shutdown hook stops Jetty only; JVM shutdown does not guarantee that a thread
   * waiting in <code>server.join()</code> resumes to execute the Net4j cleanup in <code>finally</code>. Embedded Jetty hosts
   * must coordinate all three lifecycles through their own shutdown manager.
   */
  public class Mutual_TLS
  {
    /**
     * Creates a client TLS context containing an application key and a trust store.
     *
     * @snip
     */
    public void createMutualTLSContext()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      HttpClient mutualTLSClient = null;
      IWSConnector connector = null;

      try
      {
        String identityPath = System.getProperty("net4j.tls.client.keyStore");
        String identityStoreSecret = System.getenv("NET4J_TLS_CLIENT_KEYSTORE_PASSWORD");
        String identityKeySecret = System.getenv("NET4J_TLS_CLIENT_KEY_PASSWORD");
        String trustPath = System.getProperty("net4j.tls.client.trustStore");
        String trustStoreSecret = System.getenv("NET4J_TLS_CLIENT_TRUSTSTORE_PASSWORD");
        if (identityPath == null || identityPath.isBlank() || identityStoreSecret == null || identityStoreSecret.isBlank() || identityKeySecret == null
            || identityKeySecret.isBlank() || trustPath == null || trustPath.isBlank() || trustStoreSecret == null || trustStoreSecret.isBlank())
        {
          throw new IllegalStateException("Configure the client identity and trust-store paths and provide their passwords through the secret store");
        }

        char[] identityStorePassword = identityStoreSecret.toCharArray();
        char[] identityKeyPassword = identityKeySecret.toCharArray();
        char[] trustStorePassword = trustStoreSecret.toCharArray();
        KeyStore identity = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream input = Files.newInputStream(Path.of(identityPath)))
        {
          identity.load(input, identityStorePassword);
        }

        KeyStore trust = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream input = Files.newInputStream(Path.of(trustPath)))
        {
          trust.load(input, trustStorePassword);
        }

        KeyManagerFactory keys = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keys.init(identity, identityKeyPassword);
        TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagers.init(trust);

        SSLContext tls = SSLContext.getInstance("TLS");
        tls.init(keys.getKeyManagers(), trustManagers.getTrustManagers(), new SecureRandom());
        mutualTLSClient = HttpClient.newBuilder().sslContext(tls).connectTimeout(Duration.ofSeconds(10)).build();

        IFactory provider = JDKWSUtil.createMessageConnectionFactory("client-mtls", mutualTLSClient, Duration.ofSeconds(10));
        container.registerFactory(provider);
        connector = WSSUtil.getConnector(container, URI.create("wss://service.example.net/net4j"), "mutual-tls", "provider:client-mtls");
        connector.connect(15_000);
        // Use the authenticated WSS connector while its caller-owned HttpClient remains alive.
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Unable to configure or connect the mutual-TLS WSS client", ex);
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
          // On Java 21+, close mutualTLSClient after all connectors using it have stopped.
        }
      }
    }

    /**
     * Configures a complete Jetty WSS listener that requires client certificates.
     *
     * @snip
     */
    public void deployMutualTLSWSS()
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      IWSAcceptor acceptor = null;
      Server server = null;
      Exception startupFailure = null;

      try
      {
        String keyStorePath = System.getProperty("net4j.tls.server.keyStore");
        String keyStorePassword = System.getenv("NET4J_TLS_SERVER_KEYSTORE_PASSWORD");
        String trustStorePath = System.getProperty("net4j.tls.server.clientTrustStore");
        String trustStorePassword = System.getenv("NET4J_TLS_SERVER_CLIENT_TRUSTSTORE_PASSWORD");
        if (keyStorePath == null || keyStorePath.isBlank() || keyStorePassword == null || keyStorePassword.isBlank() || trustStorePath == null
            || trustStorePath.isBlank() || trustStorePassword == null || trustStorePassword.isBlank())
        {
          throw new IllegalStateException("Configure server identity and client CA trust-store paths and provide passwords through the secret store");
        }

        acceptor = WSSUtil.getAcceptor(container, "mutual-tls");
        LifecycleUtil.activate(acceptor);

        server = new Server();
        server.setStopAtShutdown(true);

        SslContextFactory.Server tls = new SslContextFactory.Server();
        tls.setKeyStorePath(keyStorePath);
        tls.setKeyStorePassword(keyStorePassword);
        tls.setTrustStorePath(trustStorePath);
        tls.setTrustStorePassword(trustStorePassword);
        tls.setNeedClientAuth(true);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        ServerConnector listener = new ServerConnector(server, new SslConnectionFactory(tls, HttpVersion.HTTP_1_1.toString()),
            new HttpConnectionFactory(https));
        listener.setPort(8443);
        server.addConnector(listener);

        ContextHandler context = new ContextHandler("/");
        WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor);
        context.setHandler(upgradeHandler);
        server.setHandler(context);
        server.start();

        URI endpoint = URI.create("wss://service.example.net:" + listener.getLocalPort() + "/net4j");
        System.getLogger(Doc03_SecuringWebSocketTransports.class.getName()).log(System.Logger.Level.INFO, "Mutual-TLS WSS listening at {0}", endpoint);
        // The JVM hook stops Jetty only; it does not guarantee this waiting thread runs Net4j cleanup in finally.
        server.join();
      }
      catch (Exception ex)
      {
        if (ex instanceof InterruptedException)
        {
          Thread.currentThread().interrupt();
        }

        startupFailure = ex;
        throw new IllegalStateException("Unable to start the mutual-TLS WSS endpoint", ex);
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
   * TLS Termination and Secure Request Detection
   * <p>
   * The Jetty adapter requires the incoming request's secure state to match the acceptor type. It accepts WS only for
   * non-secure requests and WSS only for secure requests. If a reverse proxy terminates TLS and forwards plain HTTP, Jetty
   * sees a non-secure request unless the deployment has explicitly configured trusted forwarded-request handling. A WSS
   * acceptor will then reject the upgrade with 403. Do not trust client-supplied forwarding headers; only honor such
   * metadata from a controlled proxy and configure the Jetty edge accordingly.
   * <p>
   * The simplest arrangement is end-to-end HTTPS from the TLS terminator to Jetty. If TLS terminates before Jetty, validate
   * the trusted-proxy chain and secure-request configuration in an integration environment before exposing WSS.
   */
  public class TLS_Termination_and_Secure_Request_Detection
  {
  }

  /**
   * Deployment Checks and Common Failures
   * <p>
   * A certificate-chain error usually means a missing intermediate or untrusted CA. A host-name error means the URI host
   * does not match the certificate. A handshake failure can also result from expired certificates, incompatible protocol
   * policy, or a missing client identity when mutual TLS is required. Keep normal certificate-chain and host-name checks
   * enabled while diagnosing each case.
   * <p>
   * WSS requests rejected with 403 often indicate that Jetty reports the wrong secure state for the selected WSS acceptor.
   * A 404 usually points to a path/header mismatch or an acceptor outside the handler's explicit allow-list. For proxy
   * failures, verify the caller-owned <code>HttpClient</code> proxy selector and authenticator, then review JDK Basic CONNECT
   * restrictions. Never place credentials or long-lived cookies in logs or unprotected connector descriptions.
   * <p>
   * The matching XML entry declares only a named WSS acceptor. It does not configure key stores, listeners, or TLS policy; those
   * remain the responsibility of the HTTP server integration.
   */
  public class Deployment_Checks_and_Common_Failures
  {
  }
}
