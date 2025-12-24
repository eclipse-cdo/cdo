/*
 * Copyright (c) 2013, 2015-2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.tests.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.oracle.OracleAdapter;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import oracle.jdbc.pool.OracleDataSource;

/**
 * @author Simon McDuff
 */
public abstract class OracleConfig extends AbstractSetupDBConfig
{
  public static final String DB_ADAPTER_NAME = OracleAdapter.NAME;

  private static final String DRIVER_TYPE = System.getProperty("test.oracle.drivertype", "thin");

  private static final String SERVER_NAME = System.getProperty("test.oracle.servername", "localhost");

  private static final int PORT_NUMBER = Integer.getInteger("test.oracle.portnumber", 1521);

  private static final String DATABASE_NAME = System.getProperty("test.oracle.databasename", "TEST");

  private static final String USER = System.getProperty("test.oracle.user", "cdotest");

  private static final String PASSWORD = System.getProperty("test.oracle.password", "oracle");

  private static final long serialVersionUID = 1L;

  public OracleConfig()
  {
    super(DB_ADAPTER_NAME);
  }

  @Override
  protected String getDBAdapterName()
  {
    return DB_ADAPTER_NAME;
  }

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new OracleAdapter();
  }

  @Override
  protected void initRepositoryProperties(Map<String, String> props)
  {
    super.initRepositoryProperties(props);
    props.put(IDBStore.Props.CONNECTION_KEEPALIVE_PERIOD, "5");
    props.put(IDBStore.Props.READER_POOL_CAPACITY, "5");
    props.put(IDBStore.Props.WRITER_POOL_CAPACITY, "5");
  }

  protected String getUserName(String dbName)
  {
    return USER;
  }

  @Override
  protected DataSource createDataSourceForDB(String dbName) throws SQLException
  {
    String userName = getUserName(dbName);
    return createDataSourceForUser(userName);
  }

  public static OracleDataSource createDataSourceForUser(String userName) throws SQLException
  {
    OracleDataSource dataSource = new OracleDataSource();
    dataSource.setDriverType(DRIVER_TYPE);
    dataSource.setServerName(SERVER_NAME);
    dataSource.setPortNumber(PORT_NUMBER);
    dataSource.setDatabaseName(DATABASE_NAME);
    dataSource.setUser(userName);
    dataSource.setPassword(PASSWORD);
    return dataSource;
  }

  /**
   * @author Eike Stepper
   */
  public static class SingleUser extends OracleConfig
  {
    private static final long serialVersionUID = 1L;

    public SingleUser()
    {
    }

    @Override
    protected void initDatabase(String dbName)
    {
      if (!getDBName(REPOSITORY_NAME).equals(dbName))
      {
        OM.LOG.info("Skipping database " + dbName + " in " + getCurrentTest());
        AbstractOMTest.skipTest();
      }

      super.initDatabase(dbName);
    }

    @Override
    protected void dropDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
    {
      OM.LOG.info("Dropping all tables...");
      DBUtil.dropAllTables(connection, null);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MultiUser extends OracleConfig
  {
    private static final long serialVersionUID = 1L;

    public MultiUser()
    {
    }

    @Override
    protected void initDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
    {
      String userName = getUserName(dbName);
      stmt.execute("CREATE USER " + userName + " IDENTIFIED BY oracle");
      stmt.execute("GRANT DBA TO " + userName);
    }

    @Override
    protected void dropDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
    {
      String userName = getUserName(dbName);
      stmt.execute("DROP USER " + userName + " CASCADE");
    }

    @Override
    protected String getUserName(String dbName)
    {
      if (dbName == null)
      {
        return USER;
      }

      return dbName;
    }
  }
}
