/*
 * Copyright (c) 2008-2016, 2018-2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - don't remove statically registered packages from registry
 *    Maxime Porhel (Obeo) - WebSocket support adaptation to Jetty 12
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDONet4jViewProvider;
import org.eclipse.emf.cdo.server.internal.embedded.ClientBranchManager;
import org.eclipse.emf.cdo.server.internal.embedded.ClientRevisionManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.concurrent.DelegatingExecutorService;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.Net4jWebSocketServlet;
import org.eclipse.net4j.wss.WSSUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

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
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class SessionConfig extends Config implements ISessionConfig
{
  public static final String PROP_TEST_SESSION_CONFIGURATION = "test.session.SessionConfiguration";

  public static final String PROP_TEST_CREDENTIALS_PROVIDER = "test.session.CredentialsProvider";

  public static final String PROP_TEST_BRANCH_MANAGER = "test.session.BranchManager";

  public static final String PROP_TEST_FETCH_RULE_MANAGER = "test.session.FetchRuleManager";

  private static final EPackage.Registry GLOBAL_REGISTRY = EPackage.Registry.INSTANCE;

  private static final long serialVersionUID = 1L;

  private transient IManagedContainer clientContainer;

  private transient Set<CDOSession> sessions;

  private transient IListener sessionListener;

  private transient Set<String> globallyRegisteredPackageURIs;

  public SessionConfig(String name)
  {
    super(name);
  }

  public IManagedContainer getServerContainer()
  {
    return getCurrentTest().getServerContainer();
  }

  public boolean hasServerContainer()
  {
    return getCurrentTest().hasServerContainer();
  }

  protected boolean usesServerContainer()
  {
    return false;
  }

  @Override
  public boolean hasClientContainer()
  {
    if (usesServerContainer())
    {
      return hasServerContainer();
    }

    return clientContainer != null;
  }

  @Override
  public IManagedContainer getClientContainer()
  {
    if (usesServerContainer())
    {
      return getServerContainer();
    }

    if (clientContainer == null)
    {
      clientContainer = createClientContainer();
      LifecycleUtil.activate(clientContainer);
    }

    return clientContainer;
  }

  protected IManagedContainer createClientContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    container.setName("client");

    Net4jUtil.prepareContainer(container);
    CDOCommonUtil.prepareContainer(container);

    container.registerFactory(new ExecutorServiceFactory()
    {
      @Override
      public ExecutorService create(String threadGroupName)
      {
        return new DelegatingExecutorService(executorService);
      }
    });

    return container;
  }

  @Override
  public void startTransport() throws Exception
  {
  }

  @Override
  public void stopTransport() throws Exception
  {
  }

  @Override
  public CDOSession openSession()
  {
    return openSession(IRepositoryConfig.REPOSITORY_NAME);
  }

  @Override
  public CDOSession openSession(String repositoryName)
  {
    if (RepositoryConfig.REPOSITORY_NAME.equals(repositoryName))
    {
      // Start default repository
      getCurrentTest().getRepository(IRepositoryConfig.REPOSITORY_NAME);
    }

    CDOSessionConfiguration configuration = getTestSessionConfiguration();
    if (configuration == null)
    {
      configuration = createSessionConfiguration(repositoryName);
    }

    IPasswordCredentialsProvider credentialsProvider = getTestCredentialsProvider();
    if (credentialsProvider != null)
    {
      configuration.setCredentialsProvider(credentialsProvider);
    }

    CDOBranchManager branchManager = getTestBranchManager();
    if (branchManager != null)
    {
      configuration.setBranchManager(branchManager);
    }

    return openSession(configuration);
  }

  @Override
  public CDOSession openSession(CDOSessionConfiguration configuration)
  {
    CDOSession session = configuration.openSession();
    configureSession(session);
    session.addListener(sessionListener);

    synchronized (sessions)
    {
      sessions.add(session);
    }

    return session;
  }

  @Override
  public void closeAllSessions()
  {
    if (sessions != null)
    {
      CDOSession[] array;
      synchronized (sessions)
      {
        array = sessions.toArray(new CDOSession[sessions.size()]);
      }

      for (CDOSession session : array)
      {
        session.removeListener(sessionListener);
        LifecycleUtil.deactivate(session);
      }

      synchronized (sessions)
      {
        sessions.clear();
      }
    }
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    sessions = new HashSet<>();
    sessionListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle session)
      {
        if (sessions != null)
        {
          synchronized (sessions)
          {
            sessions.remove(session);
          }
        }
      }
    };

    globallyRegisteredPackageURIs = captureGlobalPackageRegistry();
  }

  @Override
  public void tearDown() throws Exception
  {
    try
    {
      closeAllSessions();
      sessions = null;
      sessionListener = null;

      stopTransport();

      if (clientContainer != null)
      {
        LifecycleUtil.deactivate(clientContainer);
        clientContainer = null;
      }

      super.tearDown();
    }
    finally
    {
      removeDynamicPackagesFromGlobalRegistry(globallyRegisteredPackageURIs);
      globallyRegisteredPackageURIs = null;
    }
  }

  protected CDOSessionConfiguration getTestSessionConfiguration()
  {
    return (CDOSessionConfiguration)getTestProperty(PROP_TEST_SESSION_CONFIGURATION);
  }

  protected IPasswordCredentialsProvider getTestCredentialsProvider()
  {
    return (IPasswordCredentialsProvider)getTestProperty(PROP_TEST_CREDENTIALS_PROVIDER);
  }

  protected CDOBranchManager getTestBranchManager()
  {
    return (CDOBranchManager)getTestProperty(PROP_TEST_BRANCH_MANAGER);
  }

  protected CDOFetchRuleManager getTestFetchRuleManager()
  {
    return (CDOFetchRuleManager)getTestProperty(PROP_TEST_FETCH_RULE_MANAGER);
  }

  public abstract CDOSessionConfiguration createSessionConfiguration(String repositoryName);

  public void configureSession(CDOSession session)
  {
    final File lobCache = getCurrentTest().getTempName("lobs_" + new Date().getTime() + "_", ".tmp");
    session.options().setLobCache(new CDOLobStoreImpl(lobCache));
    session.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        IOUtil.delete(lobCache);
      }
    });
  }

  private Set<String> captureGlobalPackageRegistry()
  {
    return new HashSet<>(GLOBAL_REGISTRY.keySet());
  }

  private void removeDynamicPackagesFromGlobalRegistry(Set<String> urisToProtect)
  {
    for (String uri : GLOBAL_REGISTRY.keySet().toArray(new String[GLOBAL_REGISTRY.size()]))
    {
      if (urisToProtect == null || !urisToProtect.contains(uri))
      {
        Object object = GLOBAL_REGISTRY.get(uri); // Prevent resolving descriptors
        if (isDynamicPackage(object))
        {
          GLOBAL_REGISTRY.remove(uri);
        }
      }
    }
  }

  private boolean isDynamicPackage(Object object)
  {
    return object != null && object.getClass() == EPackageImpl.class;
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Net4j extends SessionConfig
  {
    private static final long serialVersionUID = 1L;

    private static final Field PROTOCOL_TESTING_FIELD = ReflectUtil.getField(SignalProtocol.class, "testing");

    private transient CDOViewProvider viewProvider;

    public Net4j(String name)
    {
      super(name);
    }

    @Override
    public void initCapabilities(Set<String> capabilities)
    {
      capabilities.add(CAPABILITY_NET4J);
    }

    public String getTransportType()
    {
      return getName().toLowerCase();
    }

    @Override
    public void startTransport() throws Exception
    {
      IAcceptor acceptor = getAcceptor();
      LifecycleUtil.activate(acceptor);

      IConnector connector = getConnector();
      LifecycleUtil.activate(connector);
    }

    @Override
    public void stopTransport() throws Exception
    {
      try
      {
        if (hasClientContainer())
        {
          IConnector connector = getConnector();
          connector.close();
        }
      }
      catch (Exception ex)
      {
        IOUtil.print(ex);
      }

      try
      {
        if (hasServerContainer())
        {
          IAcceptor acceptor = getAcceptor();
          acceptor.close();
        }
      }
      catch (Exception ex)
      {
        IOUtil.print(ex);
      }
    }

    @Override
    public String getURIProtocol()
    {
      return "cdo.net4j." + getTransportType();
    }

    @Override
    public CDOSessionConfiguration createSessionConfiguration(String repositoryName)
    {
      CDONet4jSessionConfiguration configuration = getCurrentTest().createNet4jSessionConfiguration(repositoryName);
      configuration.setConnector(getConnector());
      configuration.setRepositoryName(repositoryName);
      configuration.setRevisionManager(new TestRevisionManager());
      return configuration;
    }

    @Override
    public void configureSession(CDOSession session)
    {
      super.configureSession(session);
      ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol().setTimeout(-1);
    }

    public abstract CDOViewProvider createViewProvider(IManagedContainer container);

    @Override
    public void setUp() throws Exception
    {
      super.setUp();

      IManagedContainer clientContainer = getClientContainer();
      CDONet4jUtil.prepareContainer(clientContainer);

      viewProvider = createViewProvider(clientContainer);
      if (viewProvider != null)
      {
        CDOViewProviderRegistry.INSTANCE.addViewProvider(viewProvider);
      }
    }

    @Override
    public void tearDown() throws Exception
    {
      if (viewProvider != null)
      {
        CDOViewProviderRegistry.INSTANCE.removeViewProvider(viewProvider);
      }

      ReflectUtil.setValue(PROTOCOL_TESTING_FIELD, null, true);

      try
      {
        super.tearDown();
      }
      finally
      {
        ReflectUtil.setValue(PROTOCOL_TESTING_FIELD, null, false);
      }
    }

    public abstract IAcceptor getAcceptor();

    public abstract IConnector getConnector();

    /**
     * @author Eike Stepper
     */
    public static class JVM extends SessionConfig.Net4j
    {
      public static final String NAME = "JVM";

      public static final JVM INSTANCE = new JVM();

      public static final String ACCEPTOR_NAME = "default";

      private static final long serialVersionUID = 1L;

      public JVM(String name)
      {
        super(name);
      }

      public JVM()
      {
        this(NAME);
      }

      @Override
      public void initCapabilities(Set<String> capabilities)
      {
        super.initCapabilities(capabilities);
        capabilities.add(CAPABILITY_NET4J_JVM);
      }

      @Override
      public String getURIPrefix()
      {
        return getURIProtocol() + "://" + ACCEPTOR_NAME;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return JVMUtil.getAcceptor(getServerContainer(), ACCEPTOR_NAME);
      }

      @Override
      public IConnector getConnector()
      {
        return JVMUtil.getConnector(getClientContainer(), ACCEPTOR_NAME);
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();
        JVMUtil.prepareContainer(getClientContainer());
      }

      @Override
      protected boolean usesServerContainer()
      {
        return true;
      }

      @Override
      public CDOViewProvider createViewProvider(final IManagedContainer container)
      {
        return new CDONet4jViewProvider.JVM()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return container;
          }
        };
      }

      /**
       * @author Eike Stepper
       */
      public static final class Embedded extends SessionConfig.Net4j.JVM
      {
        public static final String NAME = "JVMEmbedded";

        public static final Embedded INSTANCE = new Embedded();

        private static final long serialVersionUID = 1L;

        public Embedded()
        {
          super(NAME);
        }

        @Override
        public void initCapabilities(Set<String> capabilities)
        {
          super.initCapabilities(capabilities);
          capabilities.add(CAPABILITY_NET4J_EMBEDDED);
        }

        @Override
        public CDOSessionConfiguration createSessionConfiguration(String repositoryName)
        {
          InternalRepository repository = getCurrentTest().getRepository(repositoryName);
          CDOBranchManager branchManager = new ClientBranchManager(repository.getBranchManager());
          CDORevisionManager revisionManager = new ClientRevisionManager(repository.getRevisionManager());

          CDONet4jSessionConfiguration configuration = (CDONet4jSessionConfiguration)super.createSessionConfiguration(repositoryName);
          configuration.setBranchManager(branchManager);
          configuration.setRevisionManager(revisionManager);
          configuration.setSignalTimeout(Integer.MAX_VALUE);

          return configuration;
        }

        @Override
        public void configureSession(CDOSession session)
        {
          super.configureSession(session);
          ((CDONet4jSession)session).options().setCommitTimeout(Integer.MAX_VALUE);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class TCP extends SessionConfig.Net4j
    {
      public static final String NAME = "TCP";

      public static final TCP INSTANCE = new TCP();

      public static final String CONNECTOR_HOST = "localhost";

      private static final long serialVersionUID = 1L;

      public TCP()
      {
        super(NAME);
      }

      @Override
      public void initCapabilities(Set<String> capabilities)
      {
        super.initCapabilities(capabilities);
        capabilities.add(CAPABILITY_NET4J_TCP);
      }

      @Override
      public String getURIPrefix()
      {
        return getURIProtocol() + "://" + CONNECTOR_HOST;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return TCPUtil.getAcceptor(getServerContainer(), null);
      }

      @Override
      public IConnector getConnector()
      {
        return TCPUtil.getConnector(getClientContainer(), CONNECTOR_HOST);
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();
        TCPUtil.prepareContainer(getClientContainer());

        if (!usesServerContainer())
        {
          TCPUtil.prepareContainer(getServerContainer());
        }
      }

      @Override
      public CDOViewProvider createViewProvider(final IManagedContainer container)
      {
        return new CDONet4jViewProvider.TCP()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return container;
          }
        };
      }
    }

    /**
     * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
     */
    public static final class SSL extends SessionConfig.Net4j
    {
      public static final String NAME = "SSL";

      public static final SSL INSTANCE = new SSL();

      public static final String CONNECTOR_HOST = "localhost";

      private static final long serialVersionUID = 1L;

      public SSL()
      {
        super(NAME);
      }

      @Override
      public void initCapabilities(Set<String> capabilities)
      {
        super.initCapabilities(capabilities);
        capabilities.add(CAPABILITY_NET4J_SSL);
      }

      @Override
      public String getURIPrefix()
      {
        return getURIProtocol() + "://" + CONNECTOR_HOST;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return SSLUtil.getAcceptor(getServerContainer(), null);
      }

      @Override
      public IConnector getConnector()
      {
        return SSLUtil.getConnector(getClientContainer(), CONNECTOR_HOST);
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();
        SSLUtil.prepareContainer(getClientContainer());

        if (!usesServerContainer())
        {
          SSLUtil.prepareContainer(getServerContainer());
        }
      }

      @Override
      public CDOViewProvider createViewProvider(final IManagedContainer container)
      {
        return new CDONet4jViewProvider.SSL()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return container;
          }
        };
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class WS extends SessionConfig.Net4j
    {
      public static final String NAME = "WS";

      public static final WS INSTANCE = new WS();

      public static final int HTTP_PORT = 8087;

      public static final String SERVICE_URI = "ws://localhost:" + HTTP_PORT + "/net4j";

      public static final String ACCEPTOR_NAME = "default";

      private static final long serialVersionUID = 1L;

      private static Server server;

      public WS()
      {
        super(NAME);
      }

      @Override
      public void initCapabilities(Set<String> capabilities)
      {
        super.initCapabilities(capabilities);
        capabilities.add(CAPABILITY_NET4J_WS);
      }

      @Override
      public String getURIPrefix()
      {
        return getURIProtocol() + "://localhost:" + HTTP_PORT + "/net4j/@" + ACCEPTOR_NAME;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return WSUtil.getAcceptor(getServerContainer(), ACCEPTOR_NAME);
      }

      @Override
      public IConnector getConnector()
      {
        try
        {
          return WSUtil.getConnector(getClientContainer(), new URI(SERVICE_URI), ACCEPTOR_NAME);
        }
        catch (URISyntaxException ex)
        {
          throw new RuntimeException(ex);
        }
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();
        WSUtil.prepareContainer(getClientContainer());

        if (!usesServerContainer())
        {
          WSUtil.prepareContainer(getServerContainer());
        }

        if (server == null)
        {
          System.out.println("Starting Jetty...");
          server = new Server(HTTP_PORT);

          ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
          handler.setContextPath("/");
          server.setHandler(handler);

          JettyWebSocketServletContainerInitializer.configure(handler, null);
          handler.addServlet(new ServletHolder("net4j", Net4jWebSocketServlet.class), "/net4j");

          server.start();
          System.out.println("Started Jetty server...");
        }
      }

      @Override
      public void mainSuiteFinished() throws Exception
      {
        super.mainSuiteFinished();

        if (server != null)
        {
          System.out.println("Stopping Jetty...");
          server.stop();
          server = null;
        }
      }

      @Override
      public CDOViewProvider createViewProvider(final IManagedContainer container)
      {
        return new CDONet4jViewProvider.WS()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return container;
          }
        };
      }
    }

    /**
     * @author Maxime Porhel
     */
    public static final class WSS extends SessionConfig.Net4j
    {
      public static final String NAME = "WSS";

      public static final WSS INSTANCE = new WSS();

      public static final int HTTPS_PORT = 8433;

      public static final String SERVICE_URI = "wss://localhost:" + HTTPS_PORT + "/net4j";

      public static final String ACCEPTOR_NAME = "default";

      private static final long serialVersionUID = 1L;

      private static Server server;

      public WSS()
      {
        super(NAME);
      }

      @Override
      public void initCapabilities(Set<String> capabilities)
      {
        super.initCapabilities(capabilities);
        capabilities.add(CAPABILITY_NET4J_WSS);
      }

      @Override
      public String getURIPrefix()
      {
        return getURIProtocol() + "://localhost:" + HTTPS_PORT + "/net4j/@" + ACCEPTOR_NAME;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return WSSUtil.getAcceptor(getServerContainer(), ACCEPTOR_NAME);
      }

      @Override
      public IConnector getConnector()
      {
        try
        {
          // System.setProperty("org.eclipse.net4j.wss.ssl.endpointIdentificationAlgorithm", "null");
          // System.setProperty("org.eclipse.net4j.wss.ssl.passphrase", "secret");
          // System.setProperty("org.eclipse.net4j.wss.ssl.trust", new File("ssl/trusted.ks").toURI().toString());
          System.setProperty("org.eclipse.net4j.internal.wss.ssl.trustall", "true");

          return WSSUtil.getConnector(getClientContainer(), new URI(SERVICE_URI), ACCEPTOR_NAME);
        }
        catch (URISyntaxException ex)
        {
          throw new RuntimeException(ex);
        }
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();
        WSSUtil.prepareContainer(getClientContainer());

        if (!usesServerContainer())
        {
          WSSUtil.prepareContainer(getServerContainer());
        }

        if (server == null)
        {
          System.out.println("Starting Jetty...");
          server = new Server();

          SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

          URL baseurl = org.eclipse.emf.cdo.tests.bundle.OM.BUNDLE.getBaseURL();
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
      }

      @Override
      public void mainSuiteFinished() throws Exception
      {
        super.mainSuiteFinished();

        if (server != null)
        {
          System.out.println("Stopping Jetty...");
          server.stop();
          server = null;
        }
      }

      @Override
      public CDOViewProvider createViewProvider(final IManagedContainer container)
      {
        return new CDONet4jViewProvider.WSS()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return container;
          }
        };
      }
    }
  }
}
