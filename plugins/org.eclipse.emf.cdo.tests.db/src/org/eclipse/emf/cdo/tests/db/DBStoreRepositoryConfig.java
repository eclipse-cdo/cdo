package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.db.bundle.OM;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier.NonAudit;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class DBStoreRepositoryConfig extends RepositoryConfig
{
  private static final long serialVersionUID = 1L;

  public DBStoreRepositoryConfig(String name)
  {
    super(name);
  }

  @Override
  public IStore createStore()
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    IDBAdapter dbAdapter = createDBAdapter();
    DataSource dataSource = createDataSource();
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  protected abstract IMappingStrategy createMappingStrategy();

  protected abstract IDBAdapter createDBAdapter();

  protected abstract DataSource createDataSource();

  /**
   * @author Eike Stepper
   */
  public static class Hsqldb extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static boolean USE_VERIFIER = false;

    public static final DBStoreRepositoryConfig.Hsqldb INSTANCE = new Hsqldb("HSQLDB");

    private transient HSQLDBDataSource dataSource;

    public Hsqldb(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy();
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new HSQLDBAdapter();
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
    public void tearDown() throws Exception
    {
      try
      {
        if (USE_VERIFIER)
        {
          IRepository testRepository = getRepository(REPOSITORY_NAME);
          if (testRepository != null)
          {
            getVerifier(testRepository).verify();
          }
        }
      }
      finally
      {
        try
        {
          super.tearDown();
        }
        finally
        {
          shutDownHsqldb();
        }
      }
    }

    protected DBStoreVerifier getVerifier(IRepository repository)
    {
      return new DBStoreVerifier.Audit(repository);
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

  public static class HsqldbNonAudit extends DBStoreRepositoryConfig.Hsqldb
  {
    private static final long serialVersionUID = 1L;

    public static final DBStoreRepositoryConfig.HsqldbNonAudit INSTANCE = new HsqldbNonAudit(
        "DBStore: Hsqldb (non audit)");

    public HsqldbNonAudit(String name)
    {
      super(name);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "false");
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalNonAuditMappingStrategy();
    }

    @Override
    protected DBStoreVerifier getVerifier(IRepository repository)
    {
      return new NonAudit(repository);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Derby extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final DBStoreRepositoryConfig.Derby INSTANCE = new Derby("DBStore: Derby");

    private transient File dbFolder;

    private transient EmbeddedDataSource dataSource;

    public Derby(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy();
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new EmbeddedDerbyAdapter();
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
    public void tearDown() throws Exception
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
   * @author Simon McDuff
   */
  public static class Mysql extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final DBStoreRepositoryConfig.Mysql INSTANCE = new Mysql("DBStore: Mysql");

    private transient DataSource setupDataSource;

    private transient DataSource dataSource;

    public Mysql(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy();
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new MYSQLAdapter();
    }

    @Override
    protected DataSource createDataSource()
    {
      MysqlDataSource ds = new MysqlDataSource();
      ds.setUrl("jdbc:mysql://localhost/cdodb1");
      ds.setUser("sa");
      dataSource = ds;
      return dataSource;
    }

    @Override
    public void setUp() throws Exception
    {
      dropDatabase();
      Connection connection = null;

      try
      {
        connection = getSetupDataSource().getConnection();
        connection.prepareStatement("create database cdodb1").execute();
      }
      catch (SQLException ignore)
      {
      }
      finally
      {
        DBUtil.close(connection);
      }

      super.setUp();
    }

    @Override
    public void tearDown() throws Exception
    {
      super.tearDown();
      dropDatabase();
    }

    private void dropDatabase() throws Exception
    {
      Connection connection = null;

      try
      {
        connection = getSetupDataSource().getConnection();
        connection.prepareStatement("DROP database cdodb1").execute();
      }
      catch (SQLException ignore)
      {
      }
      finally
      {
        DBUtil.close(connection);
      }
    }

    private DataSource getSetupDataSource()
    {
      if (setupDataSource == null)
      {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost");
        ds.setUser("sa");
        setupDataSource = ds;
      }

      return setupDataSource;
    }
  }
}
