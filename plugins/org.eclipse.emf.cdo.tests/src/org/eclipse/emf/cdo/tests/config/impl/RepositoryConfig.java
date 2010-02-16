/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.offline.CloneSynchronizer;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IUserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryConfig extends Config implements IRepositoryConfig
{
  public static final String PROP_TEST_REPOSITORY = "test.repository";

  public static final String PROP_TEST_REVISION_MANAGER = "test.repository.RevisionManager";

  public static final String PROP_TEST_USER_MANAGER = "test.repository.UserManager";

  public static final String PROP_TEST_QUERY_HANDLER_PROVIDER = "test.repository.QueryHandlerProvider";

  private static final long serialVersionUID = 1L;

  protected transient Map<String, InternalRepository> repositories;

  public RepositoryConfig(String name)
  {
    super(name);
  }

  public Map<String, String> getRepositoryProperties()
  {
    Map<String, String> repositoryProperties = new HashMap<String, String>();
    initRepositoryProperties(repositoryProperties);

    Map<String, Object> testProperties = getTestProperties();
    if (testProperties != null)
    {
      for (Entry<String, Object> entry : testProperties.entrySet())
      {
        if (entry.getValue() instanceof String)
        {
          repositoryProperties.put(entry.getKey(), (String)entry.getValue());
        }
      }
    }

    return repositoryProperties;
  }

  public synchronized InternalRepository getRepository(String name)
  {
    InternalRepository repository = repositories.get(name);
    if (repository == null)
    {
      repository = getTestRepository();
      if (repository != null && !ObjectUtil.equals(repository.getName(), name))
      {
        repository = null;
      }

      if (repository == null)
      {
        repository = createRepository(name);
      }

      repositories.put(name, repository);
      LifecycleUtil.activate(repository);
    }

    return repository;
  }

  protected void initRepositoryProperties(Map<String, String> props)
  {
    props.put(Props.OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.REVISED_LRU_CAPACITY, "10000");
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    StoreThreadLocal.release();
    repositories = new HashMap<String, InternalRepository>();
    IManagedContainer serverContainer = getCurrentTest().getServerContainer();
    CDONet4jServerUtil.prepareContainer(serverContainer, new IRepositoryProvider()
    {
      public IRepository getRepository(String name)
      {
        return repositories.get(name);
      }
    });

    // Start default repository
    getRepository(REPOSITORY_NAME);
  }

  @Override
  public void tearDown() throws Exception
  {
    for (Object repository : repositories.values().toArray())
    {
      LifecycleUtil.deactivate(repository);
    }

    repositories.clear();
    repositories = null;
    StoreThreadLocal.release();
    super.tearDown();
  }

  protected InternalRepository createRepository(String name)
  {
    IStore store = createStore(name);
    Map<String, String> props = getRepositoryProperties();
    InternalRepository repository = (InternalRepository)CDOServerUtil.createRepository(name, store, props);
    InternalCDORevisionManager revisionManager = getTestRevisionManager();
    if (revisionManager != null)
    {
      repository.setRevisionManager(revisionManager);
    }

    IUserManager userManager = getTestUserManager();
    if (userManager != null)
    {
      InternalSessionManager sessionManager = new SessionManager();
      sessionManager.setUserManager(userManager);
      repository.setSessionManager(sessionManager);
    }

    IQueryHandlerProvider queryHandlerProvider = getTestQueryHandlerProvider();
    if (queryHandlerProvider != null)
    {
      repository.setQueryHandlerProvider(queryHandlerProvider);
    }

    return repository;
  }

  protected abstract IStore createStore(String repoName);

  protected InternalRepository getTestRepository()
  {
    return (InternalRepository)getTestProperty(PROP_TEST_REPOSITORY);
  }

  protected InternalCDORevisionManager getTestRevisionManager()
  {
    return (InternalCDORevisionManager)getTestProperty(PROP_TEST_REVISION_MANAGER);
  }

  protected IUserManager getTestUserManager()
  {
    return (IUserManager)getTestProperty(PROP_TEST_USER_MANAGER);
  }

  protected IQueryHandlerProvider getTestQueryHandlerProvider()
  {
    return (IQueryHandlerProvider)getTestProperty(PROP_TEST_QUERY_HANDLER_PROVIDER);
  }

  /**
   * @author Eike Stepper
   */
  public static class MEM extends RepositoryConfig
  {
    public static final MEM INSTANCE = new MEM();

    private static final long serialVersionUID = 1L;

    public MEM()
    {
      super("MEM");
    }

    @Override
    protected IStore createStore(String repoName)
    {
      return MEMStoreUtil.createMEMStore();
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "false");
      props.put(Props.SUPPORTING_BRANCHES, "false");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MEMAudits extends RepositoryConfig
  {
    public static final MEMAudits INSTANCE = new MEMAudits();

    private static final long serialVersionUID = 1L;

    public MEMAudits()
    {
      super("MEMAudits");
    }

    @Override
    protected IStore createStore(String repoName)
    {
      return MEMStoreUtil.createMEMStore();
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "true");
      props.put(Props.SUPPORTING_BRANCHES, "false");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MEMBranches extends RepositoryConfig
  {
    public static final MEMBranches INSTANCE = new MEMBranches();

    private static final long serialVersionUID = 1L;

    public MEMBranches()
    {
      super("MEMBranches");
    }

    @Override
    protected IStore createStore(String repoName)
    {
      return MEMStoreUtil.createMEMStore();
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "true");
      props.put(Props.SUPPORTING_BRANCHES, "true");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MEMOffline extends RepositoryConfig
  {
    public static final MEMOffline INSTANCE = new MEMOffline();

    private static final long serialVersionUID = 1L;

    private IAcceptor masterAcceptor;

    public MEMOffline()
    {
      super("MEM_OFFLINE");
    }

    @Override
    public void setUp() throws Exception
    {
      JVMUtil.prepareContainer(getCurrentTest().getServerContainer());
      super.setUp();
    }

    @Override
    protected IStore createStore(String repoName)
    {
      return MEMStoreUtil.createMEMStore();
    }

    @Override
    protected InternalRepository createRepository(String name)
    {
      Map<String, String> props = getRepositoryProperties();

      String masterName = name + "_master";
      IStore masterStore = createStore(masterName);
      InternalRepository master = (InternalRepository)CDOServerUtil.createRepository(masterName, masterStore, props);

      repositories.put(masterName, master);
      LifecycleUtil.activate(master);

      startMasterTransport();

      IManagedContainer container = getCurrentTest().getServerContainer();
      IConnector connector = (IConnector)container.getElement("org.eclipse.net4j.connectors", "jvm", "master");

      CDOSessionConfiguration config = CDONet4jUtil.createSessionConfiguration();
      config.setConnector(connector);
      config.setRepositoryName(masterName);

      CloneSynchronizer synchronizer = new CloneSynchronizer();
      synchronizer.setMasterConfiguration(config);
      synchronizer.setRetryInterval(1);
      synchronizer.setSyncedTimeStamp(CloneSynchronizer.NEVER_SYNCHRONIZED);

      IStore store = createStore(name);
      return (InternalRepository)CDOServerUtil.createCloneRepository(name, store, props, synchronizer);
    }

    public void startMasterTransport()
    {
      if (masterAcceptor == null)
      {
        IManagedContainer container = getCurrentTest().getServerContainer();
        masterAcceptor = (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", "jvm", "master");
      }
    }

    public void stopMasterTransport()
    {
      if (masterAcceptor != null)
      {
        masterAcceptor.close();
        masterAcceptor = null;
      }
    }
  }
}
