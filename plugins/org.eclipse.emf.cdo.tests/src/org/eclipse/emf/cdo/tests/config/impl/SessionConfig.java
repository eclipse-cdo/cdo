/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - don't remove statically registered packages from registry
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDONet4jViewProvider;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class SessionConfig extends Config implements ISessionConfig
{
  public static final String PROP_TEST_SESSION_CONFIGURATION = "test.session.SessionConfiguration";

  public static final String PROP_TEST_CREDENTIALS_PROVIDER = "test.session.CredentialsProvider";

  public static final String PROP_TEST_FETCH_RULE_MANAGER = "test.session.FetchRuleManager";

  private static final Registry GLOBAL_REGISTRY = EPackage.Registry.INSTANCE;

  private static final long serialVersionUID = 1L;

  private transient Set<CDOSession> sessions;

  private transient IListener sessionListener;

  private transient Set<String> globallyRegisteredPackageURIs;

  public SessionConfig(String name)
  {
    super(name);
  }

  public void startTransport() throws Exception
  {
  }

  public void stopTransport() throws Exception
  {
  }

  public CDOSession openSession()
  {
    return openSession(IRepositoryConfig.REPOSITORY_NAME);
  }

  public CDOSession openSession(String repositoryName)
  {
    if (RepositoryConfig.REPOSITORY_NAME.equals(repositoryName))
    {
      // Start default repository
      getCurrentTest().getRepository(RepositoryConfig.REPOSITORY_NAME);
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
  public void setUp() throws Exception
  {
    super.setUp();
    sessions = new HashSet<CDOSession>();
    sessionListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle session)
      {
        synchronized (sessions)
        {
          sessions.remove(session);
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

      sessions = null;
      sessionListener = null;
      stopTransport();
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

  protected CDOFetchRuleManager getTestFetchRuleManager()
  {
    return (CDOFetchRuleManager)getTestProperty(PROP_TEST_FETCH_RULE_MANAGER);
  }

  protected abstract CDOSessionConfiguration createSessionConfiguration(String repositoryName);

  protected void configureSession(CDOSession session)
  {
    final File lobCache = getCurrentTest().createTempFolder("lobs_" + new Date().getTime() + "_", ".tmp");
    session.options().setLobCache(new CDOLobStoreImpl(lobCache));
    session.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        IOUtil.delete(lobCache);
      }
    });

    CDOUtil.setLegacyModeDefault(true);
  }

  private Set<String> captureGlobalPackageRegistry()
  {
    return new HashSet<String>(GLOBAL_REGISTRY.keySet());
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
   * @deprecated Not yet supported.
   */
  @Deprecated
  public static final class Embedded extends SessionConfig
  {
    public static final String NAME = "Embedded";

    public static final Embedded INSTANCE = new Embedded();

    private static final long serialVersionUID = 1L;

    public Embedded()
    {
      super(NAME);
    }

    public void initCapabilities(Set<String> capabilities)
    {
      capabilities.add(CAPABILITY_EMBEDDED);
    }

    public String getURIProtocol()
    {
      throw new UnsupportedOperationException();
    }

    public String getURIPrefix()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected CDOSessionConfiguration createSessionConfiguration(String repositoryName)
    {
      IRepository repository = getCurrentTest().getRepository(repositoryName);

      org.eclipse.emf.cdo.server.embedded.CDOSessionConfiguration configuration = CDOServerUtil
          .createSessionConfiguration();
      configuration.setRepository(repository);
      return configuration;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Net4j extends SessionConfig
  {
    private static final long serialVersionUID = 1L;

    private transient CDOViewProvider viewProvider;

    public Net4j(String name)
    {
      super(name);
    }

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
      ConfigTest currentTest = getCurrentTest();

      try
      {
        if (currentTest.hasClientContainer())
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
        if (currentTest.hasServerContainer())
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

    public String getURIProtocol()
    {
      return "cdo.net4j." + getTransportType();
    }

    @Override
    protected CDOSessionConfiguration createSessionConfiguration(String repositoryName)
    {
      CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
      configuration.setConnector(getConnector());
      configuration.setRepositoryName(repositoryName);
      configuration.setRevisionManager(new TestRevisionManager());
      return configuration;
    }

    @Override
    protected void configureSession(CDOSession session)
    {
      super.configureSession(session);
      ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol().setTimeout(-1);
    }

    protected abstract CDOViewProvider createViewProvider(IManagedContainer container);

    @Override
    public void setUp() throws Exception
    {
      super.setUp();

      viewProvider = createViewProvider(getCurrentTest().getClientContainer());
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

      super.tearDown();
    }

    public abstract IAcceptor getAcceptor();

    public abstract IConnector getConnector();

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

      public String getURIPrefix()
      {
        return getURIProtocol() + "://" + CONNECTOR_HOST;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return TCPUtil.getAcceptor(getCurrentTest().getServerContainer(), null);
      }

      @Override
      public IConnector getConnector()
      {
        return TCPUtil.getConnector(getCurrentTest().getClientContainer(), CONNECTOR_HOST);
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();

        IManagedContainer clientContainer = getCurrentTest().getClientContainer();
        TCPUtil.prepareContainer(clientContainer);

        IManagedContainer serverContainer = getCurrentTest().getServerContainer();
        if (serverContainer != clientContainer)
        {
          TCPUtil.prepareContainer(serverContainer);
        }
      }

      @Override
      protected CDOViewProvider createViewProvider(final IManagedContainer container)
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

      public String getURIPrefix()
      {
        return getURIProtocol() + "://" + CONNECTOR_HOST;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return SSLUtil.getAcceptor(getCurrentTest().getServerContainer(), null);
      }

      @Override
      public IConnector getConnector()
      {
        return SSLUtil.getConnector(getCurrentTest().getClientContainer(), CONNECTOR_HOST);
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();

        IManagedContainer clientContainer = getCurrentTest().getClientContainer();
        SSLUtil.prepareContainer(clientContainer);

        IManagedContainer serverContainer = getCurrentTest().getServerContainer();
        if (serverContainer != clientContainer)
        {
          SSLUtil.prepareContainer(serverContainer);
        }
      }

      @Override
      protected CDOViewProvider createViewProvider(final IManagedContainer container)
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
    public static final class JVM extends SessionConfig.Net4j
    {
      public static final String NAME = "JVM";

      public static final JVM INSTANCE = new JVM();

      public static final String ACCEPTOR_NAME = "default";

      private static final long serialVersionUID = 1L;

      public JVM()
      {
        super(NAME);
      }

      @Override
      public void initCapabilities(Set<String> capabilities)
      {
        super.initCapabilities(capabilities);
        capabilities.add(CAPABILITY_NET4J_JVM);
      }

      public String getURIPrefix()
      {
        return getURIProtocol() + "://" + ACCEPTOR_NAME;
      }

      @Override
      public IAcceptor getAcceptor()
      {
        return JVMUtil.getAcceptor(getCurrentTest().getServerContainer(), ACCEPTOR_NAME);
      }

      @Override
      public IConnector getConnector()
      {
        return JVMUtil.getConnector(getCurrentTest().getClientContainer(), ACCEPTOR_NAME);
      }

      @Override
      public void setUp() throws Exception
      {
        super.setUp();
        JVMUtil.prepareContainer(getCurrentTest().getClientContainer());
        JVMUtil.prepareContainer(getCurrentTest().getServerContainer());
      }

      @Override
      public boolean isValid(Set<IConfig> configs)
      {
        return !configs.contains(ContainerConfig.Separated.INSTANCE);
      }

      @Override
      protected CDOViewProvider createViewProvider(final IManagedContainer container)
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
    }
  }
}
