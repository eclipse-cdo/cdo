/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.postgresql.PostgreSQLAdapter;
import org.eclipse.net4j.util.io.IOUtil;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Victor Roldan Betancort
 */
public class PostgresqlConfig extends DBConfig
{
  public static final String DB_ADAPTER_NAME = "Postgresql";

  private static final long serialVersionUID = 1L;

  private transient PGSimpleDataSource dataSource;

  // private transient PGSimpleDataSource setupDataSource;

  // private transient String currentRepositoryName;

  public PostgresqlConfig(boolean supportingAudits, boolean supportingBranches,
      IDGenerationLocation idGenerationLocation)
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
    return new PostgreSQLAdapter();
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    dataSource = internalCreateDataSource(repoName);

    try
    {
      dataSource.setLogWriter(new PrintWriter(System.err));
    }
    catch (SQLException ex)
    {
      OM.LOG.warn(ex.getMessage());
    }

    dropDatabase();

    return dataSource;
  }

  @Override
  protected void deactivateRepositories()
  {
    super.deactivateRepositories();
    dropDatabase();
    dataSource = null;
  }

  private void dropDatabase()
  {
    Connection connection = null;

    try
    {
      connection = dataSource.getConnection();
      String databaseName = dataSource.getDatabaseName();

      DBUtil.dropAllTables(connection, databaseName);
    }
    catch (SQLException ex)
    {
      IOUtil.ERR().println(ex);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  private PGSimpleDataSource internalCreateDataSource(String databaseName)
  {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerName("localhost");
    dataSource.setDatabaseName(databaseName);
    dataSource.setUser("postgres");
    dataSource.setPassword("postgres");
    return dataSource;
  }
}
