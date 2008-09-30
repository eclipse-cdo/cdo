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
import org.eclipse.emf.cdo.tests.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryConfig extends Config implements RepositoryProvider
{
  public static final RepositoryConfig[] CONFIGS = { MEM.INSTANCE, DBHsqldb.HSQLDB_HORIZONTAL,
      DBDerby.DERBY_HORIZONTAL, Hibernate.INSTANCE };

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
    for (Object repository : repositories.values().toArray())
    {
      LifecycleUtil.deactivate(repository);
    }

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
  public static class MEM extends RepositoryConfig
  {
    public static final MEM INSTANCE = new MEM();

    public MEM()
    {
      super("MEM");
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
    private IMappingStrategy mappingStrategy;

    public DB(String name, IMappingStrategy mappingStrategy)
    {
      super(name);
      this.mappingStrategy = mappingStrategy;
    }

    @Override
    protected IStore createStore()
    {
      IDBAdapter dbAdapter = createDBAdapter();
      DataSource dataSource = createDataSource();
      return CDODBUtil.createStore(mappingStrategy, dbAdapter, DBUtil.createConnectionProvider(dataSource));
    }

    protected abstract IDBAdapter createDBAdapter();

    protected abstract DataSource createDataSource();
  }

  /**
   * @author Eike Stepper
   */
  public static class DBHsqldb extends DB
  {
    public static final DBHsqldb HSQLDB_HORIZONTAL = new DBHsqldb("HsqldbHorizontal", CDODBUtil
        .createHorizontalMappingStrategy());

    private HSQLDBDataSource dataSource;

    public DBHsqldb(String name, IMappingStrategy mappingStrategy)
    {
      super(name, mappingStrategy);
    }

    @SuppressWarnings("restriction")
    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new org.eclipse.net4j.db.internal.hsqldb.HSQLDBAdapter();
    }

    @Override
    protected DataSource createDataSource()
    {
      dataSource = new HSQLDBDataSource();
      dataSource.setDatabase("jdbc:hsqldb:mem:dbtest");
      dataSource.setUser("sa");

      try
      {
        dataSource.setLogWriter(new PrintWriter(System.err));
      }
      catch (SQLException ex)
      {
        OM.LOG.warn(ex.getMessage());
      }

      return dataSource;
    }

    @Override
    protected void tearDown() throws Exception
    {
      super.tearDown();
      shutDownHsqldb();
    }

    private void shutDownHsqldb() throws SQLException
    {
      if (dataSource != null)
      {
        Connection connection = null;
        Statement statement = null;

        try
        {
          connection = dataSource.getConnection();
          statement = connection.createStatement();
          statement.execute("SHUTDOWN");
        }
        finally
        {
          DBUtil.close(statement);
          DBUtil.close(connection);
          dataSource = null;
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class DBDerby extends DB
  {
    public static final DBDerby DERBY_HORIZONTAL = new DBDerby("DerbyHorizontal", CDODBUtil
        .createHorizontalMappingStrategy());

    private File dbFolder;

    private EmbeddedDataSource dataSource;

    public DBDerby(String name, IMappingStrategy mappingStrategy)
    {
      super(name, mappingStrategy);
    }

    @SuppressWarnings("restriction")
    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new org.eclipse.net4j.db.internal.derby.EmbeddedDerbyAdapter();
    }

    @Override
    protected DataSource createDataSource()
    {
      dbFolder = TMPUtil.createTempFolder("derby_", null, new File("/temp"));
      deleteDBFolder();

      dataSource = new EmbeddedDataSource();
      dataSource.setDatabaseName(dbFolder.getAbsolutePath());
      dataSource.setCreateDatabase("create");
      return dataSource;
    }

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
  public static class Hibernate extends RepositoryConfig
  {
    public static final Hibernate INSTANCE = new Hibernate();

    public static final String MAPPING_FILE = "mappingfile";

    public Hibernate()
    {
      super("Hibernate");
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
