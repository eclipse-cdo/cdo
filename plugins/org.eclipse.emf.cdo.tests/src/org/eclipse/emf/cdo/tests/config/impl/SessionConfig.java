/*
 * Copyright (c) 2008-2016, 2018-2021, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - don't remove statically registered packages from registry
 *    Maxime Porhel (Obeo) - WebSocket support adaptation to Jetty 12
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jViewProvider;
import org.eclipse.emf.cdo.server.internal.embedded.ClientBranchManager;
import org.eclipse.emf.cdo.server.internal.embedded.ClientRevisionManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

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
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

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
import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.spi.net4j.MessageConnector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

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
    }

    return clientContainer;
  }

  protected IManagedContainer createClientContainer()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer("client"); //$NON-NLS-1$

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
    Throwable failure = null;

    try
    {
      closeAllSessions();
    }
    catch (Exception | Error ex)
    {
      failure = ex;
    }

    sessions = null;
    sessionListener = null;

    try
    {
      stopTransport();
    }
    catch (Exception | Error ex)
    {
      failure = addFailure(failure, ex);
    }

    IManagedContainer container = clientContainer;
    clientContainer = null;

    if (container != null)
    {
      try
      {
        LifecycleUtil.deactivate(container);
      }
      catch (Exception | Error ex)
      {
        failure = addFailure(failure, ex);
      }
    }

    try
    {
      super.tearDown();
    }
    catch (Exception | Error ex)
    {
      failure = addFailure(failure, ex);
    }

    try
    {
      removeDynamicPackagesFromGlobalRegistry(globallyRegisteredPackageURIs);
    }
    catch (Exception | Error ex)
    {
      failure = addFailure(failure, ex);
    }

    globallyRegisteredPackageURIs = null;
    throwFailure(failure);
  }

  private static Throwable addFailure(Throwable failure, Throwable ex)
  {
    if (failure == null)
    {
      return ex;
    }

    failure.addSuppressed(ex);
    return failure;
  }

  private static void throwFailure(Throwable failure) throws Exception
  {
    if (failure instanceof Exception)
    {
      throw (Exception)failure;
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
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
    final File lobCacheFolder = getCurrentTest().getTempName("lobs_" + new Date().getTime() + "_", ".tmp");
    session.options().setLobCache(new CDOLobStoreImpl(lobCacheFolder));
    session.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        IOUtil.delete(lobCacheFolder);
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
      ((CDONet4jSession)session).options().getNet4jProtocol().setTimeout(-1);
    }

    public abstract CDOViewProvider createViewProvider();

    @Override
    public void setUp() throws Exception
    {
      super.setUp();

      getClientContainer();
      viewProvider = createViewProvider();
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
      }

      @Override
      protected boolean usesServerContainer()
      {
        return true;
      }

      @Override
      public CDOViewProvider createViewProvider()
      {
        return new CDONet4jViewProvider.JVM()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return getClientContainer();
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
      }

      @Override
      public CDOViewProvider createViewProvider()
      {
        return new CDONet4jViewProvider.TCP()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return getClientContainer();
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
      }

      @Override
      public CDOViewProvider createViewProvider()
      {
        return new CDONet4jViewProvider.SSL()
        {
          @Override
          protected IManagedContainer getContainer()
          {
            return getClientContainer();
          }
        };
      }
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class AbstractWebSocket extends SessionConfig.Net4j
    {
      private static final long serialVersionUID = 1L;

      public AbstractWebSocket(String name)
      {
        super(name);
      }

      private static void stopJettyServer(Server server) throws Exception
      {
        stopJettyServer(server, null);
      }

      private static void stopJettyServer(Server server, Runnable destroySucceeded) throws Exception
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

          if (destroySucceeded != null)
          {
            destroySucceeded.run();
          }
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

      /**
       * Requests graceful closure of the active WS or WSS connectors before their Jetty server and Net4j acceptor are stopped. WebSocket
       * close-handshake processing exercises transport teardown that an immediate abort would bypass.
       * <p>
       * If either container is unavailable, or no active matching client connector exists, the helper returns without waiting.
       * Otherwise, it snapshots active client {@link IWSConnector}s from this session's client container, restricted to the current transport
       * scheme, and records their acceptor names. It then adds active server connectors accepted by active acceptors of the same transport
       * type and one of those names. Deactivation listeners are installed on this client/server set before graceful closure is requested;
       * an already inactive connector is counted immediately to cover a deactivation racing with listener installation. These snapshots do
       * not include connectors created later, and the helper does not serialize concurrent container changes.
       * <p>
       * A single two-second deadline is established before listener installation and the graceful-close requests, so setup time consumes
       * part of the wait budget. If listener registration, a graceful-close request, or the wait is interrupted or fails, or the deadline
       * expires, listeners are removed and the helper force-closes the collected client connectors followed by the collected server
       * connectors, then propagates the failure. If interrupted, it restores the thread's interrupt status. This deadline bounds only the
       * graceful-deactivation wait; the subsequent close operations have no additional timeout here.
       * <p>
       * Both WS and WSS {@code stopTransport()} implementations call this helper before stopping Jetty and closing the Net4j acceptor. They
       * attempt those later shutdown steps even if this helper reports a failure, and aggregate subsequent failures with the first one.
       *
       * @param sessionConfig the WS or WSS session configuration whose active connectors are to be closed
       * @throws Exception if graceful deactivation fails, the deadline expires, or listener removal or fallback closure fails
       */
      private static void closeWebSocketConnectionsGracefully(Net4j sessionConfig) throws Exception
      {
        if (!sessionConfig.hasClientContainer() || !sessionConfig.hasServerContainer())
        {
          return;
        }

        IManagedContainer clientContainer = sessionConfig.getClientContainer();
        IManagedContainer serverContainer = sessionConfig.getCurrentTest().getServerContainer();

        List<MessageConnector> clientConnectors = new ArrayList<>();
        Set<String> acceptorNames = new HashSet<>();

        for (Object element : clientContainer.getElements(ConnectorFactory.PRODUCT_GROUP))
        {
          if (element instanceof IWSConnector && element instanceof MessageConnector && LifecycleUtil.isActive(element))
          {
            IWSConnector connector = (IWSConnector)element;

            if (sessionConfig.getTransportType().equals(connector.getServiceURI().getScheme()))
            {
              clientConnectors.add((MessageConnector)element);
              acceptorNames.add(connector.getAcceptorName());
            }
          }
        }

        if (clientConnectors.isEmpty())
        {
          return;
        }

        List<IConnector> connections = new ArrayList<>(clientConnectors);

        for (Object element : serverContainer.getElements(AcceptorFactory.PRODUCT_GROUP))
        {
          if (element instanceof IWSAcceptor)
          {
            IWSAcceptor acceptor = (IWSAcceptor)element;

            if (sessionConfig.getTransportType().equals(acceptor.getType()) && acceptorNames.contains(acceptor.getName()) && LifecycleUtil.isActive(acceptor))
            {
              for (IConnector connector : acceptor.getAcceptedConnectors())
              {
                if (LifecycleUtil.isActive(connector))
                {
                  connections.add(connector);
                }
              }
            }
          }
        }

        CountDownLatch deactivated = new CountDownLatch(connections.size());
        List<LifecycleEventAdapter> listeners = new ArrayList<>(connections.size());

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        Throwable failure = null;
        boolean interrupted = false;
        boolean completed = false;

        try
        {
          for (IConnector connector : connections)
          {
            AtomicBoolean counted = new AtomicBoolean();

            LifecycleEventAdapter listener = new LifecycleEventAdapter()
            {
              @Override
              protected void onDeactivated(ILifecycle lifecycle)
              {
                if (counted.compareAndSet(false, true))
                {
                  deactivated.countDown();
                }
              }
            };

            connector.addListener(listener);
            listeners.add(listener);

            if (LifecycleUtil.getLifecycleState(connector) == LifecycleState.INACTIVE && counted.compareAndSet(false, true))
            {
              deactivated.countDown();
            }
          }

          for (MessageConnector connector : clientConnectors)
          {
            if (LifecycleUtil.isActive(connector))
            {
              connector.closeGracefully();
            }
          }

          long remaining = deadline - System.nanoTime();
          completed = deactivated.getCount() == 0 || remaining > 0 && deactivated.await(remaining, TimeUnit.NANOSECONDS);
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
              connections.get(i).removeListener(listeners.get(i));
            }
            catch (RuntimeException | Error ex)
            {
              failure = addFailure(failure, ex);
            }
          }
        }

        if (completed && failure == null)
        {
          return;
        }

        if (failure == null)
        {
          failure = new TimeoutException("WebSocket connections did not deactivate gracefully within two seconds");
        }

        for (MessageConnector connector : clientConnectors)
        {
          try
          {
            connector.close();
          }
          catch (RuntimeException | Error ex)
          {
            failure = addFailure(failure, ex);
          }
        }

        for (int i = clientConnectors.size(); i < connections.size(); i++)
        {
          try
          {
            connections.get(i).close();
          }
          catch (RuntimeException | Error ex)
          {
            failure = addFailure(failure, ex);
          }
        }

        if (interrupted)
        {
          Thread.currentThread().interrupt();
        }

        if (failure instanceof Exception)
        {
          throw (Exception)failure;
        }

        if (failure instanceof Error)
        {
          throw new Exception("WebSocket connector shutdown failed", failure);
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class WS extends AbstractWebSocket
      {
        public static final String NAME = "WS";

        public static final WS INSTANCE = new WS();

        public static final int HTTP_PORT = 8087;

        public static final String SERVICE_URI = "ws://localhost:" + HTTP_PORT + "/net4j";

        public static final String ACCEPTOR_NAME = "default";

        private static final long serialVersionUID = 1L;

        private static Server server;

        private static IWSAcceptor serverAcceptor;

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
          catch (Exception ex)
          {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void startTransport() throws Exception
        {
          try
          {
            startServer();
            super.startTransport();
          }
          catch (Exception | Error ex)
          {
            try
            {
              stopTransport();
            }
            catch (Exception | Error cleanupException)
            {
              ex.addSuppressed(cleanupException);
            }

            throw ex;
          }
        }

        @Override
        public void stopTransport() throws Exception
        {
          Throwable failure = null;

          try
          {
            if (hasClientContainer())
            {
              closeWebSocketConnectionsGracefully(this);
            }
          }
          catch (Exception | Error ex)
          {
            failure = ex;
          }

          try
          {
            stopServer();
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

          try
          {
            if (hasServerContainer())
            {
              getAcceptor().close();
            }
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

          if (failure instanceof Error)
          {
            failure = new Exception("WebSocket transport shutdown failed", failure);
          }

          throwFailure(failure);
        }

        private void startServer() throws Exception
        {
          IWSAcceptor acceptor = (IWSAcceptor)getAcceptor();
          LifecycleUtil.activate(acceptor);

          if (server != null && serverAcceptor == acceptor && server.isStarted())
          {
            return;
          }

          stopServer();
          Server newServer = new Server();

          try
          {
            ServerConnector connector = new ServerConnector(newServer);
            connector.setHost("localhost");
            connector.setPort(HTTP_PORT);
            newServer.addConnector(connector);

            ContextHandler context = new ContextHandler("/");
            context.setHandler(JettyWSUtil.createUpgradeHandler(newServer, context, "/net4j/*", acceptor));
            newServer.setHandler(context);
            newServer.start();

            server = newServer;
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
              server = newServer;
              serverAcceptor = acceptor;
              ex.addSuppressed(cleanupException);
            }

            throw ex;
          }
        }

        private static void stopServer() throws Exception
        {
          Server oldServer = server;

          stopJettyServer(oldServer, () -> {
            server = null;
            serverAcceptor = null;
          });
        }

        @Override
        public void mainSuiteFinished() throws Exception
        {
          Throwable failure = null;

          try
          {
            super.mainSuiteFinished();
          }
          catch (Exception | Error ex)
          {
            failure = ex;
          }

          try
          {
            stopServer();
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

          throwFailure(failure);
        }

        @Override
        public CDOViewProvider createViewProvider()
        {
          return new CDONet4jViewProvider.WS()
          {
            @Override
            protected IConnector getConnector(String authority)
            {
              try
              {
                startServer();
              }
              catch (Exception ex)
              {
                throw new RuntimeException(ex);
              }

              return super.getConnector(authority);
            }

            @Override
            protected IManagedContainer getContainer()
            {
              return getClientContainer();
            }
          };
        }
      }

      /**
       * @author Maxime Porhel
       */
      public static final class WSS extends AbstractWebSocket
      {
        public static final String NAME = "WSS";

        public static final WSS INSTANCE = new WSS();

        public static final int HTTPS_PORT = 8433;

        public static final String SERVICE_URI = "wss://127.0.0.1:" + HTTPS_PORT + "/net4j";

        public static final String ACCEPTOR_NAME = "default";

        private static final long serialVersionUID = 1L;

        private static Server server;

        private static IWSAcceptor serverAcceptor;

        private static IFactory providerFactory;

        private static IManagedContainer providerContainer;

        private static final String WSS_TEST_PROVIDER_TYPE = "cdo-wss-test";

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
          return getURIProtocol() + "://127.0.0.1:" + HTTPS_PORT + "/net4j/@" + ACCEPTOR_NAME;
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
            IManagedContainer container = getClientContainer();
            registerProvider(container);
            return WSSUtil.getConnector(container, new URI(SERVICE_URI), ACCEPTOR_NAME, "provider:" + WSS_TEST_PROVIDER_TYPE);
          }
          catch (Exception ex)
          {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void startTransport() throws Exception
        {
          try
          {
            startServer();
            super.startTransport();
          }
          catch (Exception | Error ex)
          {
            try
            {
              stopTransport();
            }
            catch (Exception | Error cleanupException)
            {
              ex.addSuppressed(cleanupException);
            }

            throw ex;
          }
        }

        @Override
        public void stopTransport() throws Exception
        {
          Throwable failure = null;

          try
          {
            if (hasClientContainer())
            {
              closeWebSocketConnectionsGracefully(this);
            }
          }
          catch (Exception | Error ex)
          {
            failure = ex;
          }

          try
          {
            stopServer();
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

          try
          {
            if (hasServerContainer())
            {
              getAcceptor().close();
            }
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

          if (failure instanceof Error)
          {
            failure = new Exception("WebSocket transport shutdown failed", failure);
          }

          throwFailure(failure);
        }

        private void startServer() throws Exception
        {
          IWSAcceptor acceptor = (IWSAcceptor)getAcceptor();
          LifecycleUtil.activate(acceptor);

          if (server != null && serverAcceptor == acceptor && server.isStarted())
          {
            return;
          }

          stopServer();
          Server newServer = new Server();

          try
          {
            URL baseURL = OM.BUNDLE.getBaseURL();
            File bundleFolder = URIUtil.toFile(URIUtil.toURI(baseURL));
            File keyStoreFile = new File(bundleFolder, "sslKey/testKeys");

            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(keyStoreFile.getPath());
            sslContextFactory.setKeyStorePassword("ab987c");

            HttpConfiguration httpsConfig = new HttpConfiguration();
            httpsConfig.addCustomizer(new SecureRequestCustomizer(false));

            ServerConnector connector = new ServerConnector(newServer, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                new HttpConnectionFactory(httpsConfig));
            connector.setHost("127.0.0.1");
            connector.setPort(HTTPS_PORT);
            newServer.addConnector(connector);

            ContextHandler context = new ContextHandler("/");
            context.setHandler(JettyWSUtil.createUpgradeHandler(newServer, context, "/net4j/*", acceptor));
            newServer.setHandler(context);
            newServer.start();

            server = newServer;
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
              server = newServer;
              serverAcceptor = acceptor;
              ex.addSuppressed(cleanupException);
            }

            throw ex;
          }
        }

        private static void stopServer() throws Exception
        {
          Server oldServer = server;

          stopJettyServer(oldServer, () -> {
            server = null;
            serverAcceptor = null;
          });
        }

        private static void registerProvider(IManagedContainer container) throws Exception
        {
          if (container == providerContainer)
          {
            return;
          }

          if (providerFactory == null)
          {
            providerFactory = JDKWSUtil.createMessageConnectionFactory(WSS_TEST_PROVIDER_TYPE, createSSLContext());
          }

          container.registerFactory(providerFactory);
          providerContainer = container;
        }

        private static SSLContext createSSLContext() throws Exception
        {
          File bundleFolder = URIUtil.toFile(URIUtil.toURI(OM.BUNDLE.getBaseURL()));
          File trustStoreFile = new File(bundleFolder, "sslKey/testTrust");
          char[] password = "ab987c".toCharArray();
          KeyStore trustStore = KeyStore.getInstance("JKS");

          try (InputStream input = Files.newInputStream(trustStoreFile.toPath()))
          {
            trustStore.load(input, password);
          }

          TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
          trustManagers.init(trustStore);

          SSLContext sslContext = SSLContext.getInstance("TLS");
          sslContext.init(null, trustManagers.getTrustManagers(), new SecureRandom());
          return sslContext;
        }

        @Override
        public void tearDown() throws Exception
        {
          try
          {
            super.tearDown();
          }
          finally
          {
            providerContainer = null;
          }
        }

        @Override
        public void mainSuiteFinished() throws Exception
        {
          Throwable failure = null;

          try
          {
            super.mainSuiteFinished();
          }
          catch (Exception | Error ex)
          {
            failure = ex;
          }

          try
          {
            stopServer();
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

          throwFailure(failure);
        }

        @Override
        public CDOViewProvider createViewProvider()
        {
          WSS sessionConfig = this;

          return new CDONet4jViewProvider.WSS()
          {
            @Override
            protected IConnector getConnector(String authority)
            {
              try
              {
                registerProvider(sessionConfig.getClientContainer());
                startServer();
              }
              catch (Exception ex)
              {
                throw new RuntimeException(ex);
              }

              return super.getConnector(authority);
            }

            @Override
            protected String getConnectorDescription(String authority)
            {
              return authority + "|provider:" + WSS_TEST_PROVIDER_TYPE;
            }

            @Override
            protected IManagedContainer getContainer()
            {
              return getClientContainer();
            }
          };
        }
      }
    }
  }
}
