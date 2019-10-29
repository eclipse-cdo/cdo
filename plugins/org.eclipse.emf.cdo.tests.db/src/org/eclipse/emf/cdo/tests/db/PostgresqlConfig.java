/*
 * Copyright (c) 2011-2013, 2016, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.postgresql.PostgreSQLAdapter;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

import java.sql.SQLException;

/**
 * @author Victor Roldan Betancort
 */
public class PostgresqlConfig extends AbstractSetupDBConfig
{
  public static final String DB_ADAPTER_NAME = "Postgresql";

  public static final String HOST = "localhost";

  public static final String USER = "postgres";

  public static final String PASS = "postgres";

  public static final String SETUP_DATABASE_NAME = "postgres";

  private static final long serialVersionUID = 1L;

  public PostgresqlConfig()
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
    return new PostgreSQLAdapter();
  }

  @SuppressWarnings("null")
  @Override
  protected DataSource createDataSourceForDB(String dbName) throws SQLException
  {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerName(HOST);
    dataSource.setDatabaseName(dbName == null ? SETUP_DATABASE_NAME : dbName);
    dataSource.setUser(USER);
    if (PASS != null)
    {
      dataSource.setPassword(PASS);
    }

    return dataSource;
  }
}
