/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalBranchingMappingStrategyWithRanges;
import org.eclipse.emf.cdo.tests.BranchingSameSessionTest;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Simon McDuff
 */
public class AllTestsDBMysql extends DBConfigs
{
  /**
   * Instructions to test with MySQL: - create a mysql instance - set HOST to the host where the DB is running
   * (listening on TCP) - set USER to a user who can create and drop databases (root, essentially) - set PASS to the
   * password of the said user
   */
  public static final String HOST = "localhost";

  public static final String USER = "root";

  public static final String PASS = "root";

  public static Test suite()
  {
    return new AllTestsDBMysql().getTestSuite("CDO Tests (DBStore MySql Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBMysql.Mysql.INSTANCE, JVM, NATIVE);
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
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    // add branching tests for this testsuite
    testClasses.add(BranchingTest.class);
    testClasses.add(BranchingSameSessionTest.class);
    testClasses.add(MergingTest.class);

    super.initTestClasses(testClasses);
  }

  /**
   * @author Simon McDuff
   */
  public static class Mysql extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBMysql.Mysql INSTANCE = new Mysql("DBStore: Mysql");

    private transient DataSource setupDataSource;

    private transient List<String> databases = new ArrayList<String>();

    public Mysql(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return new HorizontalBranchingMappingStrategyWithRanges();
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new MYSQLAdapter();
    }

    @Override
    protected DataSource createDataSource(String repoName)
    {
      MysqlDataSource ds = new MysqlDataSource();

      initDatabase("test_" + repoName);

      ds.setUrl("jdbc:mysql://" + HOST + "/test_" + repoName);
      ds.setUser(USER);
      if (PASS != null)
      {
        ds.setPassword(PASS);
      }

      return ds;
    }

    private void initDatabase(String dbName)
    {
      dropDatabase(dbName);
      Connection connection = null;
      Statement stmt = null;

      try
      {
        connection = getSetupDataSource().getConnection();
        stmt = connection.createStatement();
        stmt.execute("create database " + dbName);
      }
      catch (SQLException ignore)
      {
      }
      finally
      {
        DBUtil.close(stmt);
        DBUtil.close(connection);
      }
    }

    @Override
    protected void deactivateRepositories()
    {
      super.deactivateRepositories();
      for (String dbName : databases)
      {
        dropDatabase(dbName);
      }
    }

    private void dropDatabase(String dbName)
    {
      Connection connection = null;
      Statement stmt = null;

      try
      {
        connection = getSetupDataSource().getConnection();
        stmt = connection.createStatement();
        stmt.execute("DROP database " + dbName);
      }
      catch (SQLException ignore)
      {
      }
      finally
      {
        DBUtil.close(stmt);
        DBUtil.close(connection);
      }
    }

    private DataSource getSetupDataSource()
    {
      if (setupDataSource == null)
      {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://" + HOST);
        ds.setUser(USER);
        if (PASS != null)
        {
          ds.setPassword(PASS);
        }

        setupDataSource = ds;
      }

      return setupDataSource;
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "true");
      props.put(Props.SUPPORTING_BRANCHES, "true");
    }
  }
}
