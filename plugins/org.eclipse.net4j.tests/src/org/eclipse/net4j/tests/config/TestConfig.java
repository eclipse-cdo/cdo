/*
 * Copyright (c) 2020, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.internal.jvm.JVMAcceptorFactory;
import org.eclipse.net4j.internal.jvm.JVMConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnector;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnectorFactory;
import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSConnector;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.wss.WSSAcceptorFactory;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.Net4jWebSocketServlet;
import org.eclipse.net4j.wss.WSSUtil;

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;
import org.eclipse.jetty.ee8.servlet.ServletHolder;
import org.eclipse.jetty.ee8.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Eike Stepper
 */
public abstract class TestConfig
{
  public abstract boolean needsSeparateContainers();

  public abstract void prepareContainer(IManagedContainer container);

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
        public void prepareContainer(IManagedContainer container)
        {
          JVMUtil.prepareContainer(container);
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
        public void prepareContainer(IManagedContainer container)
        {
          TCPUtil.prepareContainer(container);
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
        public void prepareContainer(IManagedContainer container)
        {
          SSLUtil.prepareContainer(container);
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
    public static final int HTTP_PORT = 8087;

    public static final String SERVICE_URI = "ws://localhost:" + HTTP_PORT + "/net4j";

    public static final String ACCEPTOR_NAME = "default";

    @Override
    public TestConfig createConfig()
    {
      return new TestConfig()
      {
        private Server server;

        @Override
        public boolean needsSeparateContainers()
        {
          return false;
        }

        @Override
        public void prepareContainer(IManagedContainer container)
        {
          WSUtil.prepareContainer(container);
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
            String description = WSUtil.getConnectorDescription(SERVICE_URI, ACCEPTOR_NAME);
            return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, WSUtil.FACTORY_TYPE, description, activate);
          }
          catch (URISyntaxException ex)
          {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void closeUnderlyingConnection(IConnector connector) throws IOException
        {
          ((WSConnector)connector).getWebSocket().close();
        }

        @Override
        public void setUp() throws Exception
        {
          IOUtil.OUT().println("Starting Jetty...");
          server = new Server(HTTP_PORT);

          ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
          handler.setContextPath("/");
          server.setHandler(handler);

          JettyWebSocketServletContainerInitializer.configure(handler, null);
          handler.addServlet(new ServletHolder("net4j", Net4jWebSocketServlet.class), "/net4j");

          server.start();
          System.out.println("Started Jetty server...");
        }

        @Override
        public void tearDown() throws Exception
        {
          IOUtil.OUT().println("Stopping Jetty...");
          server.stop();
          server = null;
        }

        @Override
        public String toString()
        {
          return WS.class.getSimpleName();
        }
      };
    }
  }

  /**
   * @author Maxime Porhel
   */
  public static class WSS implements Factory
  {
    public static final int HTTPS_PORT = 8088;

    public static final String SERVICE_URI = "wss://localhost:" + HTTPS_PORT + "/net4j";

    public static final String ACCEPTOR_NAME = "default";

    @Override
    public TestConfig createConfig()
    {
      return new TestConfig()
      {
        private Server server;

        @Override
        public boolean needsSeparateContainers()
        {
          return false;
        }

        @Override
        public void prepareContainer(IManagedContainer container)
        {
          WSSUtil.prepareContainer(container);
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
            // System.setProperty("org.eclipse.net4j.wss.ssl.endpointIdentificationAlgorithm", "null");
            // System.setProperty("org.eclipse.net4j.wss.ssl.passphrase", "secret");
            // System.setProperty("org.eclipse.net4j.wss.ssl.trust", new File("ssl/trusted.ks").toURI().toString());
            System.setProperty("org.eclipse.net4j.internal.wss.ssl.trustall", "true");

            String description = WSSUtil.getConnectorDescription(SERVICE_URI, ACCEPTOR_NAME);
            return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, WSSUtil.FACTORY_TYPE, description, activate);
          }
          catch (URISyntaxException ex)
          {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void closeUnderlyingConnection(IConnector connector) throws IOException
        {
          ((WSConnector)connector).getWebSocket().close();
        }

        @Override
        public void setUp() throws Exception
        {
          IOUtil.OUT().println("Starting Jetty...");
          server = new Server();

          SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

          URL baseurl = OM.BUNDLE.getBaseURL();
          File file = URIUtil.toFile(URIUtil.toURI(baseurl));
          File keyStoreFile = new File(file.getPath() + File.separator + "sslKey" + File.separator + "testKeys");
          sslContextFactory.setKeyStorePath(keyStoreFile.getPath());
          sslContextFactory.setKeyStorePassword("ab987c");

          HttpConfiguration httpsConfig = new HttpConfiguration();
          httpsConfig.addCustomizer(new SecureRequestCustomizer(false));

          ServerConnector wssConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
              new HttpConnectionFactory(httpsConfig));
          wssConnector.setHost("localhost");
          wssConnector.setPort(HTTPS_PORT);
          server.addConnector(wssConnector);

          ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
          handler.setContextPath("/");
          server.setHandler(handler);

          JettyWebSocketServletContainerInitializer.configure(handler, null);
          handler.addServlet(new ServletHolder("net4j", Net4jWebSocketServlet.class), "/net4j");

          server.start();
          System.out.println("Started Jetty server...");
        }

        @Override
        public void tearDown() throws Exception
        {
          IOUtil.OUT().println("Stopping Jetty...");
          server.stop();
          server = null;
        }

        @Override
        public String toString()
        {
          return WSS.class.getSimpleName();
        }
      };
    }
  }
}
