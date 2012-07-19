/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IRepository.Props;

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

/**
 * @author Simon McDuff
 */
public class MysqlConfig extends DBConfig
{
  public static final String DB_ADAPTER_NAME = "Mysql";

  /**
   * Instructions to test with MySQL: - create a mysql instance - set HOST to the host where the DB is running
   * (listening on TCP) - set USER to a user who can create and drop databases (root, essentially) - set PASS to the
   * password of the said user
   */
  public static final String HOST = "localhost";

  public static final String USER = "root";

  public static final String PASS = "root";

  private static final long serialVersionUID = 1L;

  private transient DataSource setupDataSource;

  private transient List<String> databases = new ArrayList<String>();

  public MysqlConfig(boolean supportingAudits, boolean supportingBranches, IDGenerationLocation idGenerationLocation)
  {
    super(DB_ADAPTER_NAME, supportingAudits, supportingBranches, false, false, idGenerationLocation);
  }

  @Override
  protected String getDBAdapterName()
  {
    return DB_ADAPTER_NAME;
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

    ds.setUrl("jdbc:mysql://" + MysqlConfig.HOST + "/test_" + repoName);
    ds.setUser(MysqlConfig.USER);
    if (MysqlConfig.PASS != null)
    {
      ds.setPassword(MysqlConfig.PASS);
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
      ds.setUrl("jdbc:mysql://" + MysqlConfig.HOST);
      ds.setUser(MysqlConfig.USER);
      if (MysqlConfig.PASS != null)
      {
        ds.setPassword(MysqlConfig.PASS);
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
