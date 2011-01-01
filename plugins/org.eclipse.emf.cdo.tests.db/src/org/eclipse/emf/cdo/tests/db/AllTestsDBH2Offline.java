/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.OfflineDelayedTest;
import org.eclipse.emf.cdo.tests.OfflineRawTest;
import org.eclipse.emf.cdo.tests.OfflineTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_319552_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_325097_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_326047_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_328352_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_329014_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2Offline extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBH2Offline().getTestSuite("CDO Tests (DBStore H2 Horizontal - offline mode)");
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return true;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return true;
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, H2Offline.ReusableFolder.INSTANCE, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    // super.initTestClasses(testClasses);

    testClasses.add(Bugzilla_329014_Test.class);
    testClasses.add(Bugzilla_328352_Test.class);
    testClasses.add(Bugzilla_326047_Test.class);
    testClasses.add(Bugzilla_325097_Test.class);
    testClasses.add(Bugzilla_319552_Test.class);
    testClasses.add(OfflineRawTest.class);
    testClasses.add(OfflineDelayedTest.class);
    testClasses.add(OfflineTest.class);

    // testClasses.add(FailoverTest.class);
  }

  /**
   * @author Eike Stepper
   */
  public static class H2Offline extends OfflineConfig
  {
    public static final H2Offline INSTANCE = new H2Offline("DBStore: H2 (offline)");

    private static final long serialVersionUID = 1L;

    protected transient File dbFolder;

    public H2Offline(String name)
    {
      super(name);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    }

    @Override
    public IStore createStore(String repoName)
    {
      IMappingStrategy mappingStrategy = createMappingStrategy();
      IDBAdapter dbAdapter = createDBAdapter();
      DataSource dataSource = createDataSource(repoName);
      return CDODBUtil.createStore(mappingStrategy, dbAdapter, DBUtil.createConnectionProvider(dataSource));
    }

    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy(true, true);
    }

    protected IDBAdapter createDBAdapter()
    {
      return new H2Adapter();
    }

    protected DataSource createDataSource(String repoName)
    {
      if (dbFolder == null)
      {
        dbFolder = createDBFolder();
        tearDownClean();
      }

      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test;SCHEMA=" + repoName);
      return dataSource;
    }

    protected void tearDownClean()
    {
      IOUtil.delete(dbFolder);
    }

    protected File createDBFolder()
    {
      return TMPUtil.createTempFolder("h2_", "_test");
    }

    /**
     * @author Eike Stepper
     */
    public static class ReusableFolder extends H2Offline
    {
      private static final long serialVersionUID = 1L;

      public static final ReusableFolder INSTANCE = new ReusableFolder("DBStore: H2 (offline)");

      private static File reusableFolder;

      private static JdbcDataSource defaultDataSource;

      private transient ArrayList<String> repoNames = new ArrayList<String>();

      public ReusableFolder(String name)
      {
        super(name);
      }

      @Override
      protected DataSource createDataSource(String repoName)
      {
        if (reusableFolder == null)
        {
          reusableFolder = createDBFolder();
          IOUtil.delete(reusableFolder);
        }

        dbFolder = reusableFolder;
        if (defaultDataSource == null)
        {
          defaultDataSource = new JdbcDataSource();
          defaultDataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test");
        }

        Connection conn = null;
        Statement stmt = null;

        try
        {
          conn = defaultDataSource.getConnection();
          stmt = conn.createStatement();
          stmt.execute("DROP SCHEMA IF EXISTS " + repoName);
          stmt.execute("CREATE SCHEMA " + repoName);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
          DBUtil.close(conn);
          DBUtil.close(stmt);
        }

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test;SCHEMA=" + repoName);
        return dataSource;
      }

      @Override
      protected void tearDownClean()
      {
        for (String repoName : repoNames)
        {
          tearDownClean(repoName);
        }
      }

      protected void tearDownClean(String repoName)
      {
        reusableFolder.deleteOnExit();
        Connection connection = null;
        Statement stmt = null;

        try
        {
          connection = defaultDataSource.getConnection();
          stmt = connection.createStatement();
          stmt.execute("DROP SCHEMA " + repoName);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
        finally
        {
          DBUtil.close(stmt);
          DBUtil.close(connection);
        }
      }
    }
  }
}
