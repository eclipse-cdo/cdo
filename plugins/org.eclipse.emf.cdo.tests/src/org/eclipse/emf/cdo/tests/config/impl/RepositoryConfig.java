/*
 * Copyright (c) 2008-2013, 2015-2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CountedTimeProvider;
import org.eclipse.emf.cdo.internal.common.revision.NOOPRevisionCache;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.internal.server.syncing.OfflineClone;
import org.eclipse.emf.cdo.internal.server.syncing.RepositorySynchronizer;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepository.Handler;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.admin.CDOAdminServerUtil;
import org.eclipse.emf.cdo.server.internal.embedded.ServerBranchLoader;
import org.eclipse.emf.cdo.server.internal.embedded.ServerRevisionLoader;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.server.ocl.OCLQueryHandler;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.tests.util.TestSessionManager;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.DelegatingExecutorService;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.ThrowableEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.junit.Assert;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryConfig extends Config implements IRepositoryConfig
{
  public static final String PROP_TEST_REPOSITORY = "test.repository";

  public static final String PROP_TEST_TIME_PROVIDER = "test.repository.TimeProvider";

  public static final String PROP_TEST_REVISION_MANAGER = "test.repository.RevisionManager";

  public static final String PROP_TEST_UNIT_MANAGER = "test.repository.UnitManager";

  public static final String PROP_TEST_SESSION_MANAGER = "test.repository.SessionManager";

  public static final String PROP_TEST_AUTHENTICATOR = "test.repository.Authenticator";

  public static final String PROP_TEST_PERMISSION_MANAGER = "test.repository.PermissionManager";

  public static final String PROP_TEST_SECURITY_MANAGER = "test.repository.SecurityManager";

  public static final String PROP_TEST_QUERY_HANDLER_PROVIDER = "test.repository.QueryHandlerProvider";

  public static final String PROP_TEST_ENABLE_SERVER_BROWSER = "test.repository.EnableServerBrowser";

  private static final boolean LOG_MULTI_VIEW_COMMIT = false;

  private static final boolean enableServerBrowser = Boolean.getBoolean("org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.enableServerBrowser");

  private static final boolean useGlobalThreadPool = Boolean.getBoolean("org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.useGlobalThreadPool");

  private static final long serialVersionUID = 1L;

  protected static IManagedContainer serverContainer;

  protected static ExecutorService serverThreadPool = useGlobalThreadPool ? executorService : ThreadPool.create("server", 10, MAX_THREADS_PER_POOL, 10);

  protected static Map<String, InternalRepository> repositories;

  private static String lastRepoProps;

  private static String lastScenario;

  private boolean supportingAudits;

  private boolean supportingBranches;

  private boolean supportingChunks = true;

  private boolean supportingExtRefs = true;

  private IDGenerationLocation idGenerationLocation = IDGenerationLocation.STORE;

  private ListOrdering listOrdering = ListOrdering.ORDERED;

  /**
   * Flag used to signal that a repository is being restarted. This prevents cleaning and reinitialization of persistent
   * data and should only be used during {@link ConfigTest#restartRepository(String)}.
   */
  private transient boolean restarting;

  private transient CDOServerBrowser serverBrowser;

  private transient IRepository.WriteAccessHandler resourcePathChecker;

  public RepositoryConfig(String name)
  {
    super(name);
  }

  public void initCapabilities(Set<String> capabilities)
  {
    if (supportingAudits())
    {
      capabilities.add(CAPABILITY_AUDITING);
      if (supportingBranches())
      {
        capabilities.add(CAPABILITY_BRANCHING);
      }
    }

    if (supportingChunks())
    {
      capabilities.add(CAPABILITY_CHUNKING);
    }

    if (supportingExtRefs())
    {
      capabilities.add(CAPABILITY_EXTERNAL_REFS);
    }

    if (listOrdering() != ListOrdering.ORDERED)
    {
      capabilities.add(CAPABILITY_UNORDERED_LISTS);
    }

    if (idGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      capabilities.add(CAPABILITY_UUIDS);
    }

    if (isRestartable())
    {
      capabilities.add(CAPABILITY_RESTARTABLE);
    }

    capabilities.add(getStoreName());
  }

  protected abstract String getStoreName();

  public boolean isRestartable()
  {
    return true;
  }

  public boolean supportingAudits()
  {
    return supportingAudits;
  }

  public RepositoryConfig supportingAudits(boolean supportingAudits)
  {
    this.supportingAudits = supportingAudits;

    if (!supportingAudits)
    {
      supportingBranches(false);
    }

    return this;
  }

  public boolean supportingBranches()
  {
    return supportingBranches;
  }

  public RepositoryConfig supportingBranches(boolean supportingBranches)
  {
    this.supportingBranches = supportingBranches;

    if (supportingBranches)
    {
      supportingAudits(true);
    }

    return this;
  }

  public boolean supportingChunks()
  {
    return supportingChunks;
  }

  public RepositoryConfig supportingChunks(boolean supportingChunks)
  {
    this.supportingChunks = supportingChunks;
    return this;
  }

  public boolean supportingExtRefs()
  {
    return supportingExtRefs;
  }

  public RepositoryConfig supportingExtRefs(boolean supportingExtRefs)
  {
    this.supportingExtRefs = supportingExtRefs;
    return this;
  }

  public IDGenerationLocation idGenerationLocation()
  {
    return idGenerationLocation;
  }

  public RepositoryConfig idGenerationLocation(IDGenerationLocation idGenerationLocation)
  {
    this.idGenerationLocation = idGenerationLocation;
    return this;
  }

  public ListOrdering listOrdering()
  {
    return listOrdering;
  }

  public RepositoryConfig listOrdering(ListOrdering listOrdering)
  {
    this.listOrdering = listOrdering;
    return this;
  }

  @Override
  public String getName()
  {
    return super.getName() + (supportingBranches ? "-branching" : supportingAudits ? "-auditing" : "") + getMappingStrategySpecialization()
        + (idGenerationLocation == IDGenerationLocation.CLIENT ? "-uuids" : "");
  }

  protected String getMappingStrategySpecialization()
  {
    return "";
  }

  public void setRestarting(boolean on)
  {
    restarting = on;
  }

  public boolean isRestarting()
  {
    return restarting;
  }

  public boolean hasServerContainer()
  {
    return serverContainer != null;
  }

  public IManagedContainer getServerContainer()
  {
    if (serverContainer == null)
    {
      serverContainer = createServerContainer();
      LifecycleUtil.activate(serverContainer);
    }

    return serverContainer;
  }

  protected IManagedContainer createServerContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    container.setName("server");

    Net4jUtil.prepareContainer(container);
    CDONet4jServerUtil.prepareContainer(container);

    container.registerFactory(new ExecutorServiceFactory()
    {
      @Override
      public ExecutorService create(String threadGroupName)
      {
        return new DelegatingExecutorService(serverThreadPool);
      }
    });

    return container;
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
        Object value = entry.getValue();
        if (value == null)
        {
          repositoryProperties.remove(entry.getKey());
        }
        else if (value instanceof String)
        {
          repositoryProperties.put(entry.getKey(), (String)entry.getValue());
        }
      }
    }

    return repositoryProperties;
  }

  public String getRepositoryPropertiesDigest()
  {
    Map<String, String> repositoryProperties = getRepositoryProperties();
    List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(repositoryProperties.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, String>>()
    {
      public int compare(Entry<String, String> o1, Entry<String, String> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entry : list)
    {
      builder.append(entry.getKey());
      builder.append("=");
      builder.append(entry.getValue());
      builder.append("\n");
    }

    return builder.toString();
  }

  public synchronized InternalRepository[] getRepositories()
  {
    if (repositories == null)
    {
      return new InternalRepository[0];
    }

    Collection<InternalRepository> values = repositories.values();
    return values.toArray(new InternalRepository[values.size()]);
  }

  public synchronized InternalRepository getRepository(String name)
  {
    return getRepository(name, true);
  }

  public synchronized InternalRepository getRepository(String name, boolean activate)
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
      else
      {
        if (repository.getStore() == null)
        {
          IStore store = createStore(name);
          repository.setStore((InternalStore)store);
        }

        if (repository.getProperties() == null)
        {
          Map<String, String> props = getRepositoryProperties();
          repository.setProperties(props);
        }
      }

      if (repository.getContainer() == IPluginContainer.INSTANCE)
      {
        IManagedContainer serverContainer = getServerContainer();
        repository.setContainer(serverContainer);
      }

      registerRepository(repository);

      if (activate)
      {
        try
        {
          LifecycleUtil.activate(repository);

          if (hasAnnotation(CallAddRepository.class))
          {
            CDOServerUtil.addRepository(serverContainer, repository);
          }
        }
        catch (RuntimeException ex)
        {
          deactivateRepositories();
          throw ex;
        }
      }
    }

    addResourcePathChecker(repository);
    return repository;
  }

  protected void initRepositoryProperties(Map<String, String> props)
  {
    props.put(Props.OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.SUPPORTING_AUDITS, Boolean.toString(supportingAudits));
    props.put(Props.SUPPORTING_BRANCHES, Boolean.toString(supportingBranches));
    props.put(Props.ID_GENERATION_LOCATION, idGenerationLocation.toString());

    // TODO list-ordering
    // props.put(Props.LIST_ORDERING, listOrdering.toString());
  }

  public void registerRepository(final InternalRepository repository)
  {
    repository.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        IRepository repository = (IRepository)lifecycle;
        synchronized (RepositoryConfig.this)
        {
          repositories.remove(repository.getName());
        }
      }
    });

    if (LOG_MULTI_VIEW_COMMIT)
    {
      repository.addHandler(new IRepository.WriteAccessHandler()
      {
        public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
        {
          int count = 0;
          for (ISession session : repository.getSessionManager().getSessions())
          {
            CDOCommonView[] views = session.getViews();
            count += views.length;
          }

          if (count > 1)
          {
            logMultiViewCommit();
          }
        }

        public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
        {
        }
      });
    }

    repositories.put(repository.getName(), repository);
  }

  @Override
  public void setUp() throws Exception
  {
    StoreThreadLocal.release();

    super.setUp();

    if (isOptimizing() && needsCleanRepos() && repositories != null && !repositories.isEmpty())
    {
      deactivateRepositories();
    }
    else
    {
      resetRepositories();
    }

    if (repositories == null)
    {
      repositories = new HashMap<String, InternalRepository>();
    }

    IManagedContainer serverContainer = getCurrentTest().getServerContainer();
    OCLQueryHandler.prepareContainer(serverContainer);
    CDOAdminServerUtil.prepareContainer(serverContainer);
    CDONet4jServerUtil.prepareContainer(serverContainer, new IRepositoryProvider()
    {
      public IRepository getRepository(String name)
      {
        return repositories.get(name);
      }
    });

    if (enableServerBrowser || Boolean.TRUE.equals(getTestProperty(PROP_TEST_ENABLE_SERVER_BROWSER)) || getCurrentTest().hasDefaultScenario())
    {
      serverBrowser = new CDOServerBrowser(repositories);
      serverBrowser.activate();
    }
  }

  @Override
  public void tearDown() throws Exception
  {
    if (StoreThreadLocal.hasSession())
    {
      Assert.fail("Test case has left a session in StoreThreadLocal: " + getCurrentTest());
    }

    if (StoreThreadLocal.hasAccessor())
    {
      Assert.fail("Test case has left an accessor in StoreThreadLocal: " + getCurrentTest());
    }

    if (StoreThreadLocal.hasCommitContext())
    {
      Assert.fail("Test case has left a commit context in StoreThreadLocal: " + getCurrentTest());
    }

    deactivateServerBrowser();

    if (repositories != null)
    {
      if (!isOptimizing() || mustLeaveCleanRepos())
      {
        deactivateRepositories();
      }
      else
      {
        removeResourcePathChecker();
      }
    }

    resourcePathChecker = null;
    super.tearDown();
  }

  protected boolean isOptimizing()
  {
    return false;
  }

  protected void deactivateServerBrowser()
  {
    if (serverBrowser != null)
    {
      serverBrowser.deactivate();
      serverBrowser = null;
    }
  }

  protected void deactivateRepositories()
  {
    StoreThreadLocal.release();

    for (InternalRepository repository : getRepositories())
    {
      LifecycleUtil.deactivate(repository);
    }

    if (repositories != null)
    {
      repositories.clear();
      repositories = null;
    }

    if (serverContainer != null)
    {
      LifecycleUtil.deactivate(serverContainer);
      serverContainer = null;
    }
  }

  protected void resetRepositories()
  {
    for (InternalRepository repository : getRepositories())
    {
      repository.getRevisionManager().getCache().clear();
    }
  }

  protected void addResourcePathChecker(InternalRepository repository)
  {
    if (getCurrentTest().getScenario().alwaysCleanRepositories())
    {
      return;
    }

    if (resourcePathChecker == null)
    {
      resourcePathChecker = new IRepository.WriteAccessHandler()
      {
        public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
        {
          for (InternalCDORevision revision : commitContext.getNewObjects())
          {
            if (revision.isResource())
            {
              String path = CDORevisionUtil.getResourceNodePath(revision, commitContext);
              ConfigTest test = getCurrentTest();
              String prefix = test.getResourcePath("");
              if (!path.startsWith(prefix) && !hasAnnotation(CleanRepositoriesBefore.class))
              {
                throw new RuntimeException("Test case " + test.getClass().getName() + '.' + test.getName() + " does not use getResourcePath() for resource "
                    + path + ", nor does it declare @" + CleanRepositoriesBefore.class.getSimpleName());
              }
            }
          }
        }

        public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
        {
          // Do nothing
        }
      };
    }

    repository.addHandler(resourcePathChecker);
  }

  protected void removeResourcePathChecker()
  {
    for (InternalRepository repository : getRepositories())
    {
      for (Handler handler : repository.getHandlers())
      {
        repository.removeHandler(handler);
      }

      CDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
      for (CDOCommitInfoHandler handler : commitInfoManager.getCommitInfoHandlers())
      {
        commitInfoManager.removeCommitInfoHandler(handler);
      }
    }
  }

  protected InternalRepository createRepository(String name)
  {
    IStore store = createStore(name);

    Map<String, String> repoProps = getRepositoryProperties();
    InternalRepository repository = createRepository(name, store, repoProps);

    if (hasAnnotation(CountedTime.class))
    {
      repository.setTimeProvider(new CountedTimeProvider());
    }

    InternalCDORevisionManager revisionManager = getTestRevisionManager();
    if (revisionManager == null)
    {
      revisionManager = new TestRevisionManager();
    }

    repository.setRevisionManager(revisionManager);

    InternalUnitManager unitManager = getTestUnitManager();
    if (unitManager != null)
    {
      repository.setUnitManager(unitManager);
    }

    InternalSessionManager sessionManager = getTestSessionManager();
    if (sessionManager == null)
    {
      sessionManager = new TestSessionManager();
    }

    repository.setSessionManager(sessionManager);

    IAuthenticator authenticator = getTestAuthenticator();
    if (authenticator != null)
    {
      sessionManager.setAuthenticator(authenticator);
    }

    IPermissionManager permissionManager = getTestPermissionManager();
    if (permissionManager != null)
    {
      sessionManager.setPermissionManager(permissionManager);
    }

    InternalSecurityManager securityManager = (InternalSecurityManager)getTestSecurityManager();
    if (securityManager != null)
    {
      JVMUtil.prepareContainer(getCurrentTest().getServerContainer()); // Needed in SecurityManager.init()

      securityManager.setRepository(repository);
      LifecycleUtil.activate(securityManager);
    }

    IQueryHandlerProvider queryHandlerProvider = getTestQueryHandlerProvider();
    if (queryHandlerProvider != null)
    {
      repository.setQueryHandlerProvider(queryHandlerProvider);
    }

    return repository;
  }

  protected InternalRepository createRepository(String name, IStore store, Map<String, String> props)
  {
    return (InternalRepository)CDOServerUtil.createRepository(name, store, props);
  }

  protected InternalRepository getTestRepository()
  {
    return (InternalRepository)getTestProperty(PROP_TEST_REPOSITORY);
  }

  protected InternalCDORevisionManager getTestRevisionManager()
  {
    return (InternalCDORevisionManager)getTestProperty(PROP_TEST_REVISION_MANAGER);
  }

  protected InternalUnitManager getTestUnitManager()
  {
    return (InternalUnitManager)getTestProperty(PROP_TEST_UNIT_MANAGER);
  }

  protected InternalSessionManager getTestSessionManager()
  {
    return (InternalSessionManager)getTestProperty(PROP_TEST_SESSION_MANAGER);
  }

  protected IAuthenticator getTestAuthenticator()
  {
    return (IAuthenticator)getTestProperty(PROP_TEST_AUTHENTICATOR);
  }

  protected IPermissionManager getTestPermissionManager()
  {
    return (IPermissionManager)getTestProperty(PROP_TEST_PERMISSION_MANAGER);
  }

  protected ISecurityManager getTestSecurityManager()
  {
    return (ISecurityManager)getTestProperty(PROP_TEST_SECURITY_MANAGER);
  }

  protected IQueryHandlerProvider getTestQueryHandlerProvider()
  {
    return (IQueryHandlerProvider)getTestProperty(PROP_TEST_QUERY_HANDLER_PROVIDER);
  }

  protected boolean needsCleanRepos()
  {
    IScenario scenario = getCurrentTest().getScenario();
    if (scenario.alwaysCleanRepositories())
    {
      return true;
    }

    String scenarioName = scenario.toString();
    boolean sameScenario = scenarioName.equals(lastScenario);
    lastScenario = scenarioName;

    String repoProps = getRepositoryPropertiesDigest();
    boolean sameProps = repoProps.equals(lastRepoProps);
    lastRepoProps = repoProps;

    if (!sameScenario)
    {
      // New scenario is an indication for a new TestSuite with a similar set of test cases.
      // Same test cases would probably use duplicate resource paths, so start with fresh repos.
      return true;
    }

    if (!sameProps)
    {
      // If the props have changed (or if there are no lastRepoProps, which means
      // this is the first test of a run) we definitely want a clean repo.
      return true;
    }

    return hasAnnotation(CleanRepositoriesBefore.class);
  }

  protected boolean mustLeaveCleanRepos()
  {
    return hasAnnotation(CleanRepositoriesAfter.class);
  }

  private <T extends Annotation> boolean hasAnnotation(Class<T> annotationClass)
  {
    Class<? extends ConfigTest> testClass = getCurrentTest().getClass();
    String methodName = getCurrentTest().getTestMethodName();
    Method method = ReflectUtil.getMethod(testClass, methodName, new Class[0]);
    if (method.getAnnotation(annotationClass) != null)
    {
      return true;
    }

    return testClass.getAnnotation(annotationClass) != null;
  }

  static
  {
    if (LOG_MULTI_VIEW_COMMIT)
    {
      FileOutputStream out = null;

      try
      {
        out = new FileOutputStream("multi-view-commit.log", false);
      }
      catch (Exception ex)
      {
      }
      finally
      {
        IOUtil.close(out);
      }
    }
  }

  private void logMultiViewCommit()
  {
    FileOutputStream out = null;

    try
    {
      out = new FileOutputStream("multi-view-commit.log", true);
      @SuppressWarnings("resource")
      PrintStream stream = new PrintStream(out);
      stream.println(getCurrentTest().getCodeLink());
      stream.flush();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class OfflineConfig extends RepositoryConfig
  {
    public static final String PROP_TEST_FAILOVER = "test.failover";

    public static final String PROP_TEST_RAW_REPLICATION = "test.raw.replication";

    public static final String PROP_TEST_DELAYED_COMMIT_HANDLING = "test.delayed.commit.handling";

    public static final String PROP_TEST_DELAYED2_COMMIT_HANDLING = "test.delayed2.commit.handling";

    public static final String PROP_TEST_HINDER_INITIAL_REPLICATION = "test.hinder.initial.replication";

    private static final long serialVersionUID = 1L;

    private transient IAcceptor masterAcceptor;

    public OfflineConfig(String name)
    {
      super(name);
      supportingAudits(true);
      supportingBranches(true);
    }

    @Override
    public void initCapabilities(Set<String> capabilities)
    {
      super.initCapabilities(capabilities);
      capabilities.add(CAPABILITY_OFFLINE);
    }

    @Override
    public void setUp() throws Exception
    {
      JVMUtil.prepareContainer(getCurrentTest().getServerContainer());
      super.setUp();

      // Start default repository
      getRepository(REPOSITORY_NAME);
    }

    @Override
    protected void deactivateRepositories()
    {
      super.deactivateRepositories();
      stopMasterTransport();
    }

    protected InternalRepository createMasterRepository(String masterName, String name, Map<String, String> props, boolean failover)
    {
      IStore masterStore = createStore(masterName);

      InternalRepository repository;
      if (failover)
      {
        InternalRepositorySynchronizer synchronizer = createSynchronizer("backup", name);
        repository = (InternalRepository)CDOServerUtil.createFailoverParticipant(masterName, masterStore, props, synchronizer, true);
      }
      else
      {
        repository = (InternalRepository)CDOServerUtil.createRepository(masterName, masterStore, props);
      }

      setInitialPackages(repository);
      return repository;
    }

    @Override
    protected InternalRepository createRepository(String name)
    {
      boolean failover = getTestFailover();
      boolean hinderInitialReplication = getTestHinderInitialReplication();
      Map<String, String> props = getRepositoryProperties();

      final String masterName = "master";
      InternalRepository master;
      synchronized (OfflineConfig.this)
      {
        master = repositories.get(masterName);
        if (master == null)
        {
          master = createMasterRepository(masterName, name, props, failover);
          repositories.put(masterName, master);
          LifecycleUtil.activate(master);

          if (!hinderInitialReplication)
          {
            startMasterTransport();
          }
        }
      }

      InternalRepositorySynchronizer synchronizer = createSynchronizer("master", masterName);
      IStore store = createStore(name);

      InternalSynchronizableRepository repository;
      if (failover)
      {
        repository = (InternalSynchronizableRepository)CDOServerUtil.createFailoverParticipant(name, store, props, synchronizer, false);
      }
      else
      {
        repository = new OfflineClone()
        {
          @Override
          public void handleCommitInfo(CDOCommitInfo commitInfo)
          {
            waitIfLockAvailable();
            super.handleCommitInfo(commitInfo);
          }

          private void waitIfLockAvailable()
          {
            long millis = getTestDelayedCommitHandling();
            if (millis != 0L)
            {
              ConcurrencyUtil.sleep(millis);
            }
          }
        };

        repository.setName(name);
        repository.setStore((InternalStore)store);
        repository.setProperties(props);
        repository.setSynchronizer(synchronizer);
      }

      setInitialPackages(repository);
      return repository;
    }

    protected void setInitialPackages(IRepository repository)
    {
      repository.setInitialPackages(getCurrentTest().getModel1Package(), getCurrentTest().getModel3Package());
    }

    protected InternalRepositorySynchronizer createSynchronizer(final String acceptorName, final String repositoryName)
    {
      CDOSessionConfigurationFactory masterFactory = new CDOSessionConfigurationFactory()
      {
        public org.eclipse.emf.cdo.session.CDOSessionConfiguration createSessionConfiguration()
        {
          IManagedContainer container = getCurrentTest().getServerContainer();
          IConnector connector = Net4jUtil.getConnector(container, "jvm", acceptorName);

          InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)CDORevisionUtil.createRevisionManager();
          revisionManager.setCache(new NOOPRevisionCache());

          CDONet4jSessionConfiguration config = new CDONet4jSessionConfigurationImpl()
          {
            @Override
            public InternalCDOSession createSession()
            {
              return new CDONet4jSessionImpl()
              {
                volatile int counter = 1;

                @Override
                public void handleCommitNotification(CommitNotificationInfo info)
                {
                  long delay = getTestDelayed2CommitHandling();
                  if (delay != 0L && counter++ % 2 == 0)
                  {
                    AbstractOMTest.sleep(delay);
                  }

                  super.handleCommitNotification(info);
                }
              };
            }
          };

          config.setConnector(connector);
          config.setRepositoryName(repositoryName);
          config.setRevisionManager(revisionManager);
          return config;
        }
      };

      RepositorySynchronizer synchronizer = new RepositorySynchronizer();
      synchronizer.setRemoteSessionConfigurationFactory(masterFactory);
      synchronizer.setRetryInterval(1);
      synchronizer.setRawReplication(getTestRawReplication());
      synchronizer.addListener(new IListener()
      {
        public void notifyEvent(IEvent event)
        {
          if (event instanceof ThrowableEvent)
          {
            ThrowableEvent e = (ThrowableEvent)event;
            throw new RuntimeException(e.getThrowable());
          }
        }
      });

      return synchronizer;
    }

    protected boolean getTestFailover()
    {
      Boolean result = (Boolean)getTestProperty(PROP_TEST_FAILOVER);
      if (result == null)
      {
        result = false;
      }

      return result;
    }

    protected boolean getTestRawReplication()
    {
      Boolean result = (Boolean)getTestProperty(PROP_TEST_RAW_REPLICATION);
      if (result == null)
      {
        result = false;
      }

      return result;
    }

    protected long getTestDelayedCommitHandling()
    {
      Long result = (Long)getTestProperty(PROP_TEST_DELAYED_COMMIT_HANDLING);
      if (result == null)
      {
        result = 0L;
      }

      return result;
    }

    protected long getTestDelayed2CommitHandling()
    {
      Long result = (Long)getTestProperty(PROP_TEST_DELAYED2_COMMIT_HANDLING);
      if (result == null)
      {
        result = 0L;
      }

      return result;
    }

    protected boolean getTestHinderInitialReplication()
    {
      Boolean result = (Boolean)getTestProperty(PROP_TEST_HINDER_INITIAL_REPLICATION);
      if (result == null)
      {
        result = false;
      }

      return result;
    }

    public void startMasterTransport()
    {
      if (masterAcceptor == null)
      {
        IOUtil.OUT().println();
        IOUtil.OUT().println("startMasterTransport()");
        IOUtil.OUT().println();
        IManagedContainer container = getCurrentTest().getServerContainer();
        masterAcceptor = (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", "jvm", "master");
      }
    }

    public void stopMasterTransport()
    {
      if (masterAcceptor != null)
      {
        IOUtil.OUT().println();
        IOUtil.OUT().println("stopMasterTransport()");
        IOUtil.OUT().println();
        masterAcceptor.close();
        masterAcceptor = null;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MEMConfig extends RepositoryConfig
  {
    public static final String STORE_NAME = "MEM";

    private static final long serialVersionUID = 1L;

    public MEMConfig(String name)
    {
      super(name);
    }

    public MEMConfig()
    {
      this(STORE_NAME);
    }

    @Override
    protected String getStoreName()
    {
      return STORE_NAME;
    }

    @Override
    public boolean isRestartable()
    {
      return false;
    }

    public IStore createStore(String repoName)
    {
      return MEMStoreUtil.createMEMStore();
    }

    /**
     * @author Eike Stepper
     */
    public static class Embedded extends MEMConfig
    {
      private static final long serialVersionUID = 1L;

      public Embedded()
      {
        super(STORE_NAME + "Embedded");
      }

      @Override
      protected InternalRepository createRepository(String name)
      {
        InternalRepository repository = super.createRepository(name);

        InternalCDORevisionManager revisionManager = repository.getRevisionManager();
        revisionManager.setRevisionLoader(new ServerRevisionLoader(repository));

        InternalCDOBranchManager branchManager = CDOBranchUtil.createBranchManager();
        branchManager.setBranchLoader(new ServerBranchLoader(repository));
        repository.setBranchManager(branchManager);

        return repository;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MEMOfflineConfig extends OfflineConfig
  {
    private static final long serialVersionUID = 1L;

    public MEMOfflineConfig()
    {
      super(MEMConfig.STORE_NAME + "Offline");
    }

    @Override
    protected String getStoreName()
    {
      return MEMConfig.STORE_NAME;
    }

    @Override
    public boolean isRestartable()
    {
      return false;
    }

    public IStore createStore(String repoName)
    {
      return MEMStoreUtil.createMEMStore();
    }
  }
}
