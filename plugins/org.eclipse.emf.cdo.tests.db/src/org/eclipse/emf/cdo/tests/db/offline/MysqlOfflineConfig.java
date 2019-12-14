/*
 * Copyright (c) 2012, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.offline;

import org.eclipse.emf.cdo.tests.db.MysqlConfig;

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
import java.util.Set;

/**
 * @author Eike Stepper
 * @author Stefan Winkler
 */
public class MysqlOfflineConfig extends DBOfflineConfig
{
  private static final long serialVersionUID = 1L;

  /**
   * Instructions to test with MySQL: - create a mysql instance - set HOST to the host where the DB is running
   * (listening on TCP) - set USER to a user who can create and drop databases (root, essentially) - set PASS to the
   * password of the said user
   */
  public static final String HOST = "10.211.55.7:3306";

  public static final String USER = "root";

  public static final String PASS = null;

  private transient DataSource setupDataSource;

  private transient List<String> databases = new ArrayList<>();

  public MysqlOfflineConfig()
  {
    super("MySqlOffline");
  }

  @Override
  public void initCapabilities(Set<String> capabilities)
  {
    super.initCapabilities(capabilities);
    capabilities.add(MysqlConfig.DB_ADAPTER_NAME);
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
      ds.setUrl("jdbc:mysql://" + HOST + "/");
      ds.setUser(USER);
      if (PASS != null)
      {
        ds.setPassword(PASS);
      }

      setupDataSource = ds;
    }

    return setupDataSource;
  }

  protected void tearDownClean(String repoName)
  {
    for (String dbName : databases)
    {
      dropDatabase(dbName);
    }
  }
}
