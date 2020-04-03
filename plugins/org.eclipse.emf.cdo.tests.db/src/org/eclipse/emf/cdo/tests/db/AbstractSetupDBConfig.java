/*
 * Copyright (c) 2013, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Victor Roldan Betancort
 */
public abstract class AbstractSetupDBConfig extends DBConfig
{
  private static final long serialVersionUID = 1L;

  private transient DataSource setupDataSource;

  public AbstractSetupDBConfig(String name)
  {
    super(name);
  }

  protected String getDBName(String repoName)
  {
    return "test_" + repoName;
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    String dbName = getDBName(repoName);

    if (!isRestarting())
    {
      initDatabase(dbName);
    }

    try
    {
      return createDataSourceForDB(dbName);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected void initDatabase(String dbName)
  {
    dropDatabase(dbName);

    Connection connection = null;
    Statement stmt = null;

    try
    {
      connection = getSetupConnection();
      stmt = connection.createStatement();
      initDatabase(connection, stmt, dbName);
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

  protected void initDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
  {
    stmt.execute("CREATE DATABASE " + dbName);
  }

  protected void dropDatabase(String dbName)
  {
    Connection connection = null;
    Statement stmt = null;

    try
    {
      connection = getSetupConnection();
      stmt = connection.createStatement();
      dropDatabase(connection, stmt, dbName);
    }
    catch (SQLException ex)
    {
      if (ex.getErrorCode() != getErrorCodeDatabaseDoesNotExist())
      {
        ex.printStackTrace();
      }
    }
    finally
    {
      DBUtil.close(stmt);
      DBUtil.close(connection);
    }
  }

  protected int getErrorCodeDatabaseDoesNotExist()
  {
    return 1008;
  }

  protected void dropDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
  {
    stmt.execute("DROP DATABASE " + dbName);
  }

  protected final Connection getSetupConnection()
  {
    try
    {
      return getSetupDataSource().getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected final DataSource getSetupDataSource() throws SQLException
  {
    if (setupDataSource == null)
    {
      setupDataSource = createDataSourceForDB(null);
    }

    return setupDataSource;
  }

  /**
   * Note that <code>dbName</code> can be <code>null</code>, in which case a <i>setup</i> datasource must be returned.
   * A connection from a setup datasource can be used to create or drop other databases.
   */
  protected abstract DataSource createDataSourceForDB(String dbName) throws SQLException;
}
