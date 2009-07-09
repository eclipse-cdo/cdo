/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Simon McDuff
 */
public class AllTestsDBMysql extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBMysql().getTestSuite("CDO Tests (DBStoreRepositoryConfig MySql Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBMysql.Mysql.INSTANCE, TCP, NATIVE);
  }

  /**
   * @author Simon McDuff
   */
  public static class Mysql extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBMysql.Mysql INSTANCE = new Mysql("DBStore: Mysql");

    private transient DataSource setupDataSource;

    private transient DataSource dataSource;

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
