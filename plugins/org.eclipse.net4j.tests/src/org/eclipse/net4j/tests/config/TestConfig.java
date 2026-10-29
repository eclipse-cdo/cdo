/*
 * Copyright (c) 2020, 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Maxime Porhel (Obeo) - re-enable WebSocket tests after move to Jetty 10.0.12
 *    Maxime Porhel (Obeo) - WebSocket support adaptation to Jetty 12
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.tests.config;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.internal.jvm.JVMAcceptorFactory;
import org.eclipse.net4j.internal.jvm.JVMConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnector;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnectorFactory;
import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.ws.WSSAcceptorFactory;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSOptions;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.core.runtime.URIUtil;
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
import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.spi.net4j.MessageConnector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 */
public abstract class TestConfig
{
  private static final String WS_TEST_PROVIDER_TYPE = "jdk-ws-test"; //$NON-NLS-1$

  public TestConfig()
  {
  }

  public abstract boolean needsSeparateContainers();

  public IAcceptor getAcceptor(IManagedContainer container)
  {
    return getAcceptor(container, true);
  }

  public abstract IAcceptor getAcceptor(IManagedContainer container, boolean activate);

  public IConnector getConnector(IManagedContainer container)
  {
    return getConnector(container, true);
  }

  public abstract IConnector getConnector(IManagedContainer container, boolean activate);

  public void configureWebSocketIdleTimeout(Duration serverIdleTimeout, JDKWSOptions options)
  {
    throw new UnsupportedOperationException("This test configuration does not support WebSocket idle-timeout settings"); //$NON-NLS-1$
  }

  public void closeUnderlyingConnection(IConnector connector) throws IOException
  {
    // Underlying connection is unknown on this generic level.
    // Let subclasses decide if there are special opportunities.
    connector.close();
  }

  public void setUp() throws Exception
  {
    // Do nothing.
  }

  public void tearDown() throws Exception
  {
    // Do nothing.
  }

  /**
   * Allows a transport-specific configuration to complete active connections before the test
   * fixture deactivates its managed containers. The default implementation does nothing, so
   * transports that do not require a special shutdown handshake retain their existing behavior.
   *
   * @param connectorContainer the existing client container
   * @param acceptorContainer the existing server container
   * @throws Exception if a transport-specific shutdown operation fails
   */
  protected void beforeContainerDeactivation(IManagedContainer connectorContainer, IManagedContainer acceptorContainer) throws Exception
  {
    // Do nothing.
  }

  /**
   * @author Eike Stepper
   */
  public interface Factory
  {
    public TestConfig createConfig();
  }

  /**
   * @author Eike Stepper
   */
  public static class JVM implements Factory
  {
    public static final String NAME = "default";

    public JVM()
    {
    }

    @Override
    public TestConfig createConfig()
    {
      return new TestConfig()
      {
        @Override
        public boolean needsSeparateContainers()
        {
          return false;
        }

        @Override
        public IAcceptor getAcceptor(IManagedContainer container, boolean activate)
        {
          return (IJVMAcceptor)container.getElement(JVMAcceptorFactory.PRODUCT_GROUP, JVMAcceptorFactory.TYPE, NAME, activate);
        }

        @Override
        public IConnector getConnector(IManagedContainer container, boolean activate)
        {
          return (IJVMConnector)container.getElement(JVMConnectorFactory.PRODUCT_GROUP, JVMConnectorFactory.TYPE, NAME, activate);
        }

        @Override
        public String toString()
        {
          return JVM.class.getSimpleName();
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TCP implements Factory
  {
    public static final String HOST = "localhost";

    public TCP()
    {
    }

    @Override
    public TestConfig createConfig()
    {
      return new TestConfig()
      {
        @Override
        public boolean needsSeparateContainers()
        {
          return false;
        }

        @Override
        public IAcceptor getAcceptor(IManagedContainer container, boolean activate)
        {
          return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE, null, activate);
        }

        @Override
        public IConnector getConnector(IManagedContainer container, boolean activate)
        {
          return (ITCPConnector)container.getElement(TCPConnectorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE, HOST, activate);
        }

        @Override
        public void closeUnderlyingConnection(IConnector connector) throws IOException
        {
          ((TCPConnector)connector).getSocketChannel().close();
        }

        @Override
        public String toString()
        {
          return TCP.class.getSimpleName();
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class SSL implements Factory
  {
    public static final String HOST = "localhost";

    static
    {
      System.setProperty("org.eclipse.net4j.tcp.ssl.passphrase", "ab987c");
      System.setProperty("org.eclipse.net4j.tcp.ssl.trust", "file:sslKey/testTrust");
      System.setProperty("org.eclipse.net4j.tcp.ssl.key", "file:sslKey/testKeys");
      System.setProperty("check.validity.certificate", "false");
      // System.setProperty("javax.net.debug", "all");
    }

    public SSL()
    {
    }

    @Override
    public TestConfig createConfig()
    {
      return new TestConfig()
      {
        @Override
        public boolean needsSeparateContainers()
        {
          return true;
        }

        @Override
        public IAcceptor getAcceptor(IManagedContainer container, boolean activate)
        {
          return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, SSLAcceptorFactory.TYPE, null, activate);
        }

        @Override
        public IConnector getConnector(IManagedContainer container, boolean activate)
        {
          return (ITCPConnector)container.getElement(TCPConnectorFactory.PRODUCT_GROUP, SSLConnectorFactory.TYPE, HOST, activate);
        }

        @Override
        public void closeUnderlyingConnection(IConnector connector) throws IOException
        {
          ((SSLConnector)connector).getSocketChannel().close();
        }

        @Override
        public String toString()
        {
          return SSL.class.getSimpleName();
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class WS implements Factory
  {
    public static final String ACCEPTOR_NAME = "default";

    public WS()
    {
    }

    @Override
    public TestConfig createConfig()
    {
      return new WebSocketConfig("ws")
      {
        private Server server;

        private ServerConnector serverConnector;

        private IWSAcceptor serverAcceptor;

        private IManagedContainer providerContainer;

        private IFactory providerFactory;

        @Override
        public boolean needsSeparateContainers()
        {
          return false;
        }

        @Override
        public IAcceptor getAcceptor(IManagedContainer container, boolean activate)
        {
          return (IWSAcceptor)container.getElement(WSAcceptorFactory.PRODUCT_GROUP, WSUtil.FACTORY_TYPE, ACCEPTOR_NAME, activate);
        }

        @Override
        public IConnector getConnector(IManagedContainer container, boolean activate)
        {
          try
          {
            startServer(container);

            String description;

            if (getProviderOptions() == null)
            {
              description = WSUtil.getConnectorDescription("ws://localhost:" + serverConnector.getLocalPort() + "/net4j", ACCEPTOR_NAME);
            }
            else
            {
              registerProvider(container);

              description = WSUtil.getConnectorDescription("ws://localhost:" + serverConnector.getLocalPort() + "/net4j", ACCEPTOR_NAME,
                  WSConnectorFactory.PROVIDER_TOKEN_PREFIX + WS_TEST_PROVIDER_TYPE);
            }

            return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, WSUtil.FACTORY_TYPE, description, activate);
          }
          catch (URISyntaxException ex)
          {
            throw new RuntimeException(ex);
          }
          catch (Exception ex)
          {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void closeUnderlyingConnection(IConnector connector) throws IOException
        {
          try
          {
            stopServer();
          }
          catch (Exception ex)
          {
            throw new IOException(ex);
          }
        }

        @Override
        public void tearDown() throws Exception
        {
          stopServer();
        }

        @Override
        public String toString()
        {
          return WS.class.getSimpleName();
        }

        private void startServer(IManagedContainer container) throws Exception
        {
          IWSAcceptor acceptor = (IWSAcceptor)getAcceptor(container, true);

          if (server != null && serverAcceptor == acceptor)
          {
            return;
          }

          stopServer();

          Server newServer = new Server();
          ServerConnector newConnector = new ServerConnector(newServer);

          try
          {
            newConnector.setHost("localhost");
            newConnector.setPort(0);
            newServer.addConnector(newConnector);

            ContextHandler context = new ContextHandler("/");
            WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(newServer, context, "/net4j/*", acceptor);
            configureServerIdleTimeout(upgradeHandler);
            context.setHandler(upgradeHandler);

            newServer.setHandler(context);
            newServer.start();

            server = newServer;
            serverConnector = newConnector;
            serverAcceptor = acceptor;
          }
          catch (Exception | Error ex)
          {
            try
            {
              stopJettyServer(newServer);
            }
            catch (Exception | Error cleanupException)
            {
              ex.addSuppressed(cleanupException);
            }

            throw ex;
          }
        }

        private void stopServer() throws Exception
        {
          Server oldServer = server;

          server = null;
          serverConnector = null;
          serverAcceptor = null;

          stopJettyServer(oldServer);
        }

        private void registerProvider(IManagedContainer container)
        {
          if (container == providerContainer)
          {
            return;
          }

          if (providerFactory == null)
          {
            providerFactory = JDKWSUtil.createMessageConnectionFactory(WS_TEST_PROVIDER_TYPE, (SSLContext)null, getProviderOptions());
          }

          container.registerFactory(providerFactory);
          providerContainer = container;
        }
      };
    }
  }

  /**
   * @author Maxime Porhel
   */
  public static class WSS implements Factory
  {
    public static final String ACCEPTOR_NAME = "default";

    private static final String WSS_TEST_PROVIDER_TYPE = "jdk-wss-test"; //$NON-NLS-1$

    public WSS()
    {
    }

    @Override
    public TestConfig createConfig()
    {
      return new WebSocketConfig("wss")
      {
        private Server server;

        private ServerConnector serverConnector;

        private IWSAcceptor serverAcceptor;

        private IManagedContainer providerContainer;

        private IFactory providerFactory;

        @Override
        public boolean needsSeparateContainers()
        {
          return false;
        }

        @Override
        public IAcceptor getAcceptor(IManagedContainer container, boolean activate)
        {
          // SSL context is handled by Jetty.
          return (IWSAcceptor)container.getElement(WSSAcceptorFactory.PRODUCT_GROUP, WSSUtil.FACTORY_TYPE, ACCEPTOR_NAME, activate);
        }

        @Override
        public IConnector getConnector(IManagedContainer container, boolean activate)
        {
          try
          {
            startServer(container);
            registerProvider(container);

            String serviceURI = "wss://127.0.0.1:" + serverConnector.getLocalPort() + "/net4j"; //$NON-NLS-1$
            String description = WSSUtil.getConnectorDescription(serviceURI, ACCEPTOR_NAME, WSConnectorFactory.PROVIDER_TOKEN_PREFIX + WSS_TEST_PROVIDER_TYPE);
            return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, WSSUtil.FACTORY_TYPE, description, activate);
          }
          catch (URISyntaxException ex)
          {
            throw new RuntimeException(ex);
          }
          catch (Exception ex)
          {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void closeUnderlyingConnection(IConnector connector) throws IOException
        {
          try
          {
            stopServer();
          }
          catch (Exception ex)
          {
            throw new IOException(ex);
          }
        }

        @Override
        public void tearDown() throws Exception
        {
          stopServer();
        }

        @Override
        public String toString()
        {
          return WSS.class.getSimpleName();
        }

        private void startServer(IManagedContainer container) throws Exception
        {
          IWSAcceptor acceptor = (IWSAcceptor)getAcceptor(container, true);

          if (server != null && serverAcceptor == acceptor)
          {
            return;
          }

          stopServer();
          Server newServer = new Server();

          try
          {
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

            URL baseurl = OM.BUNDLE.getBaseURL();
            File file = URIUtil.toFile(URIUtil.toURI(baseurl));
            File keyStoreFile = new File(file.getPath() + File.separator + "sslKey" + File.separator + "testKeys");
            sslContextFactory.setKeyStorePath(keyStoreFile.getPath());
            sslContextFactory.setKeyStorePassword("ab987c");

            HttpConfiguration httpsConfig = new HttpConfiguration();
            httpsConfig.addCustomizer(new SecureRequestCustomizer(false));

            ServerConnector newConnector = new ServerConnector(newServer, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                new HttpConnectionFactory(httpsConfig));
            newConnector.setHost("127.0.0.1"); //$NON-NLS-1$
            newConnector.setPort(0);
            newServer.addConnector(newConnector);

            ContextHandler context = new ContextHandler("/");
            WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(newServer, context, "/net4j/*", acceptor);
            configureServerIdleTimeout(upgradeHandler);
            context.setHandler(upgradeHandler);
            newServer.setHandler(context);
            newServer.start();

            server = newServer;
            serverConnector = newConnector;
            serverAcceptor = acceptor;
          }
          catch (Exception | Error ex)
          {
            try
            {
              stopJettyServer(newServer);
            }
            catch (Exception | Error cleanupException)
            {
              ex.addSuppressed(cleanupException);
            }

            throw ex;
          }
        }

        private void stopServer() throws Exception
        {
          Server oldServer = server;

          server = null;
          serverConnector = null;
          serverAcceptor = null;

          stopJettyServer(oldServer);
        }

        private void registerProvider(IManagedContainer container) throws Exception
        {
          if (container == providerContainer)
          {
            return;
          }

          if (providerFactory == null)
          {
            JDKWSOptions options = getProviderOptions();
            providerFactory = options == null //
                ? JDKWSUtil.createMessageConnectionFactory(WSS_TEST_PROVIDER_TYPE, createSSLContext()) //
                : JDKWSUtil.createMessageConnectionFactory(WSS_TEST_PROVIDER_TYPE, createSSLContext(), options);
          }

          container.registerFactory(providerFactory);
          providerContainer = container;
        }

        private SSLContext createSSLContext() throws Exception
        {
          File bundleFolder = URIUtil.toFile(URIUtil.toURI(OM.BUNDLE.getBaseURL()));
          File trustStoreFile = new File(bundleFolder, "sslKey/testTrust"); //$NON-NLS-1$
          char[] password = "ab987c".toCharArray(); //$NON-NLS-1$
          KeyStore trustStore = KeyStore.getInstance("JKS"); //$NON-NLS-1$

          try (InputStream input = Files.newInputStream(trustStoreFile.toPath()))
          {
            trustStore.load(input, password);
          }

          TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
          trustManagers.init(trustStore);
          SSLContext sslContext = SSLContext.getInstance("TLS"); //$NON-NLS-1$
          sslContext.init(null, trustManagers.getTrustManagers(), new SecureRandom());
          return sslContext;
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract static class WebSocketConfig extends TestConfig
  {
    private final String transportType;

    private Duration serverIdleTimeout;

    private JDKWSOptions providerOptions;

    public WebSocketConfig(String transportType)
    {
      this.transportType = transportType;
    }

    @Override
    public void configureWebSocketIdleTimeout(Duration serverIdleTimeout, JDKWSOptions options)
    {
      this.serverIdleTimeout = serverIdleTimeout;
      providerOptions = options;
    }

    protected final JDKWSOptions getProviderOptions()
    {
      return providerOptions;
    }

    protected final void configureServerIdleTimeout(WebSocketUpgradeHandler upgradeHandler)
    {
      if (serverIdleTimeout != null)
      {
        upgradeHandler.getServerWebSocketContainer().setIdleTimeout(serverIdleTimeout);
      }
    }

    @Override
    protected void beforeContainerDeactivation(IManagedContainer connectorContainer, IManagedContainer acceptorContainer) throws Exception
    {
      if (connectorContainer == null || acceptorContainer == null)
      {
        return;
      }

      List<MessageConnector> clientConnectors = new ArrayList<>();
      Set<String> acceptorNames = new HashSet<>();

      for (Object element : connectorContainer.getElements(ConnectorFactory.PRODUCT_GROUP, transportType))
      {
        if (!(element instanceof IConnector))
        {
          throw new IllegalStateException("Connector product is not an IConnector: " + element); //$NON-NLS-1$
        }

        IConnector connector = (IConnector)element;
        if (connector instanceof IServerConnector)
        {
          continue;
        }

        if (!LifecycleUtil.isActive(connector))
        {
          continue;
        }

        if (!(connector instanceof IWSConnector))
        {
          throw new IllegalStateException("Active connector in " + transportType + " fixture is not an IWSConnector: " + connector.getClass().getName()); //$NON-NLS-1$
        }

        IWSConnector wsConnector = (IWSConnector)connector;

        if (!transportType.equals(wsConnector.getServiceURI().getScheme()))
        {
          throw new IllegalStateException("Active WebSocket connector uses an unexpected service scheme: " + wsConnector.getServiceURI()); //$NON-NLS-1$
        }

        if (!(connector instanceof MessageConnector))
        {
          throw new IllegalStateException("Active " + transportType + " connector is not a MessageConnector: " + connector.getClass().getName()); //$NON-NLS-1$
        }

        clientConnectors.add((MessageConnector)connector);
        acceptorNames.add(wsConnector.getAcceptorName());
      }

      if (clientConnectors.isEmpty())
      {
        return;
      }

      List<IConnector> connections = new ArrayList<>(clientConnectors);

      // An acceptor may already have been deactivated; the client close attempt remains bounded and container cleanup
      // is the fallback.
      for (Object element : acceptorContainer.getElements(AcceptorFactory.PRODUCT_GROUP, transportType))
      {
        if (!(element instanceof IAcceptor))
        {
          throw new IllegalStateException("Acceptor product is not an IAcceptor: " + element); //$NON-NLS-1$
        }

        IAcceptor acceptor = (IAcceptor)element;
        if (!LifecycleUtil.isActive(acceptor))
        {
          continue;
        }

        if (!(acceptor instanceof IWSAcceptor))
        {
          throw new IllegalStateException("Active acceptor in " + transportType + " fixture is not an IWSAcceptor: " + acceptor.getClass().getName()); //$NON-NLS-1$
        }

        IWSAcceptor wsAcceptor = (IWSAcceptor)acceptor;
        if (!transportType.equals(wsAcceptor.getType()))
        {
          throw new IllegalStateException("Active WebSocket acceptor has unexpected type: " + wsAcceptor.getType()); //$NON-NLS-1$
        }

        if (acceptorNames.contains(wsAcceptor.getName()))
        {
          for (IConnector serverConnector : wsAcceptor.getAcceptedConnectors())
          {
            if (LifecycleUtil.isActive(serverConnector))
            {
              if (!(serverConnector instanceof MessageConnector))
              {
                throw new IllegalStateException(
                    "Active " + transportType + " server connector is not a MessageConnector: " + serverConnector.getClass().getName());
              }

              connections.add(serverConnector);
            }
          }
        }
      }

      CountDownLatch deactivated = new CountDownLatch(connections.size());
      List<IConnector> listenedConnectors = new ArrayList<>(connections.size());
      List<LifecycleEventAdapter> listeners = new ArrayList<>(connections.size());

      long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
      Throwable failure = null;
      boolean interrupted = false;

      try
      {
        for (IConnector current : connections)
        {
          AtomicBoolean counted = new AtomicBoolean();

          LifecycleEventAdapter listener = new LifecycleEventAdapter()
          {
            @Override
            protected void onDeactivated(ILifecycle lifecycle)
            {
              if (LifecycleUtil.getLifecycleState(lifecycle) == LifecycleState.INACTIVE && counted.compareAndSet(false, true))
              {
                deactivated.countDown();
              }
            }
          };

          current.addListener(listener);
          listenedConnectors.add(current);
          listeners.add(listener);

          if (LifecycleUtil.getLifecycleState(current) == LifecycleState.INACTIVE && counted.compareAndSet(false, true))
          {
            deactivated.countDown();
          }
        }

        for (MessageConnector connector : clientConnectors)
        {
          try
          {
            connector.closeGracefully();
          }
          catch (RuntimeException | Error ex)
          {
            failure = addFailure(failure, ex);
          }
        }

        if (failure != null)
        {
          if (failure instanceof RuntimeException)
          {
            throw (RuntimeException)failure;
          }

          throw (Error)failure;
        }

        long remaining = deadline - System.nanoTime();

        if (deactivated.getCount() != 0 && remaining > 0)
        {
          deactivated.await(remaining, TimeUnit.NANOSECONDS);
        }

        // Any connectors that remain active are forcibly closed by the caller's container deactivation.
      }
      catch (InterruptedException ex)
      {
        interrupted = true;
        failure = ex;
      }
      catch (RuntimeException | Error ex)
      {
        failure = ex;
      }
      finally
      {
        for (int i = 0; i < listeners.size(); i++)
        {
          try
          {
            listenedConnectors.get(i).removeListener(listeners.get(i));
          }
          catch (RuntimeException | Error ex)
          {
            failure = addFailure(failure, ex);
          }
        }
      }

      if (interrupted)
      {
        Thread.currentThread().interrupt();
      }

      if (failure != null)
      {
        if (failure instanceof Exception)
        {
          throw (Exception)failure;
        }

        throw (Error)failure;
      }
    }

    protected static void stopJettyServer(Server server) throws Exception
    {
      if (server == null)
      {
        return;
      }

      Throwable failure = null;

      try
      {
        server.stop();
      }
      catch (Exception | Error ex)
      {
        failure = ex;
      }

      try
      {
        server.destroy();
      }
      catch (Exception | Error ex)
      {
        if (failure == null)
        {
          failure = ex;
        }
        else
        {
          failure.addSuppressed(ex);
        }
      }

      if (failure instanceof Exception)
      {
        throw (Exception)failure;
      }

      if (failure instanceof Error)
      {
        throw (Error)failure;
      }
    }

    private static Throwable addFailure(Throwable failure, Throwable additionalFailure)
    {
      if (failure == null)
      {
        return additionalFailure;
      }

      failure.addSuppressed(additionalFailure);
      return failure;
    }
  }
}
