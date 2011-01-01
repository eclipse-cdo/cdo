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
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Simon McDuff
 */
public class AllTestsDBMysql extends DBConfigs
{
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
    return false;
  }

  /**
   * @author Simon McDuff
   */
  public static class Mysql extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBMysql.Mysql INSTANCE = new Mysql("DBStore: Mysql");

    private transient DataSource setupDataSource;

    private transient ArrayList<String> databases = new ArrayList<String>();

    public Mysql(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy(true);
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

      ds.setUrl("jdbc:mysql://localhost/test_" + repoName);
      ds.setUser("sa");
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
    public void tearDown() throws Exception
    {
      super.tearDown();
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
        ds.setUrl("jdbc:mysql://localhost");
        ds.setUser("sa");
        setupDataSource = ds;
      }

      return setupDataSource;
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "true");
    }
  }
}
