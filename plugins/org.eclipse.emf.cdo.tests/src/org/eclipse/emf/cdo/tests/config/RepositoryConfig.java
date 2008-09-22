/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.apache.derby.jdbc.EmbeddedDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryConfig extends Config implements RepositoryProvider
{
  public static final RepositoryConfig[] CONFIGS = { MEM.INSTANCE, DBHorizontalHsql.INSTANCE,
      DBHorizontalDerby.INSTANCE, Hibernate.INSTANCE };

  public static final String PROP_TEST_REPOSITORY = "test.repository";

  public static final String PROP_TEST_REVISION_MANAGER = "test.repository.revisionmanager";

  public static final String PROP_TEST_STORE = "test.repository.store";

  private Map<String, IRepository> repositories = new HashMap<String, IRepository>();

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

  public synchronized IRepository getRepository(String name)
  {
    IRepository repository = repositories.get(name);
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
      // IManagedContainer serverContainer = getCurrentTest().getServerContainer();
      // CDOServerUtil.addRepository(serverContainer, repository);
    }

    return repository;
  }

  protected void initRepositoryProperties(Map<String, String> props)
  {
    props.put(Props.PROP_OVERRIDE_UUID, ""); // UUID := name !!!
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
  }

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    IManagedContainer serverContainer = getCurrentTest().getServerContainer();
    CDOServerUtil.prepareContainer(serverContainer, new IRepositoryProvider()
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
  protected void tearDown() throws Exception
  {
    // TODO deactivate?
    repositories.clear();
    super.tearDown();
  }

  protected IRepository createRepository(String name)
  {
    IStore store = getTestStore();
    if (store == null)
    {
      store = createStore();
    }

    Map<String, String> props = getRepositoryProperties();
    if (store.hasWriteDeltaSupport())
    {
      props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "true");
    }

    Repository repository = (Repository)CDOServerUtil.createRepository(name, store, props);
    RevisionManager revisionManager = getTestRevisionManager();
    if (revisionManager != null)
    {
      repository.setRevisionManager(revisionManager);
    }

    return repository;
  }

  protected abstract IStore createStore();

  protected IRepository getTestRepository()
  {
    return (IRepository)getTestProperty(PROP_TEST_REPOSITORY);
  }

  protected RevisionManager getTestRevisionManager()
  {
    return (RevisionManager)getTestProperty(PROP_TEST_REVISION_MANAGER);
  }

  protected IStore getTestStore()
  {
    return (IStore)getTestProperty(PROP_TEST_STORE);
  }

  /**
   * @author Eike Stepper
   */
  public static final class MEM extends RepositoryConfig
  {
    public static final String NAME = "MEM";

    public static final MEM INSTANCE = new MEM();

    public MEM()
    {
      super(NAME);
    }

    @Override
    protected IStore createStore()
    {
      return StoreUtil.createMEMStore();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class DB extends RepositoryConfig
  {
    private File dbFolder;

    private HSQLDBDataSource dataSource;

    public DB(String name)
    {
      super(name);
    }

    @SuppressWarnings("restriction")
    protected IStore createHsqlStore()
    {
      IDBAdapter dbAdapter = new org.eclipse.net4j.db.internal.hsqldb.HSQLDBAdapter();

      dataSource = new HSQLDBDataSource();
      dataSource.setDatabase("jdbc:hsqldb:mem:dbtest");
      dataSource.setUser("sa");

      return CDODBUtil.createStore(createMappingStrategy(), dbAdapter, DBUtil.createConnectionProvider(dataSource));
    }

    @SuppressWarnings("restriction")
    protected IStore createDerbyStore()
    {
      IDBAdapter dbAdapter = new org.eclipse.net4j.db.internal.derby.EmbeddedDerbyAdapter();

      dbFolder = TMPUtil.createTempFolder("derby_", null, new File("/temp"));
      deleteDBFolder();

      EmbeddedDataSource dataSource = new EmbeddedDataSource();
      dataSource.setDatabaseName(dbFolder.getAbsolutePath());
      dataSource.setCreateDatabase("create");

      return CDODBUtil.createStore(createMappingStrategy(), dbAdapter, DBUtil.createConnectionProvider(dataSource));
    }

    protected IMappingStrategy createHorizontalMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy();
    }

    protected abstract IMappingStrategy createMappingStrategy();

    @Override
    protected void tearDown() throws Exception
    {
      deleteDBFolder();
      super.tearDown();
    }

    private void deleteDBFolder()
    {
      IOUtil.delete(dbFolder);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class DBHorizontalHsql extends DB
  {
    public static final String NAME = "DBHorizontalHsql";

    public static final DBHorizontalHsql INSTANCE = new DBHorizontalHsql();

    public DBHorizontalHsql()
    {
      super(NAME);
    }

    @Override
    protected IStore createStore()
    {
      return createHsqlStore();
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return createHorizontalMappingStrategy();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class DBHorizontalDerby extends DB
  {
    public static final String NAME = "DBHorizontalDerby";

    public static final DBHorizontalDerby INSTANCE = new DBHorizontalDerby();

    public DBHorizontalDerby()
    {
      super(NAME);
    }

    @Override
    protected IStore createStore()
    {
      return createDerbyStore();
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return createHorizontalMappingStrategy();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Hibernate extends RepositoryConfig
  {
    public static final String NAME = "Hibernate";

    public static final Hibernate INSTANCE = new Hibernate();

    public static final String MAPPING_FILE = "mappingfile";

    public Hibernate()
    {
      super(NAME);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.PROP_SUPPORTING_AUDITS, "false");
      props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
      props.put(Props.PROP_VERIFYING_REVISIONS, "false");
    }

    @Override
    protected IStore createStore()
    {
      return null;
    }
  }
}
