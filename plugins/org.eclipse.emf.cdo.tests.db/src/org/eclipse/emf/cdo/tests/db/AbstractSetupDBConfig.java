/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.db.DBUtil;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
public abstract class AbstractSetupDBConfig extends DBConfig
{
  private static final long serialVersionUID = 1L;

  private transient DataSource setupDataSource;

  private transient List<String> databases = new ArrayList<String>();

  public AbstractSetupDBConfig(String name, boolean supportingAudits, boolean supportingBranches, boolean withRanges,
      boolean copyOnBranch, IDGenerationLocation idGenerationLocation)
  {
    super(name, supportingAudits, supportingBranches, withRanges, copyOnBranch, idGenerationLocation);
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    String dbName = "test_" + repoName;
    initDatabase(dbName);

    return createDataSourceForDB(dbName);
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

  private void initDatabase(String dbName)
  {
    dropDatabase(dbName);
    Connection connection = null;
    Statement stmt = null;

    try
    {
      connection = getSetupDataSource().getConnection();
      stmt = connection.createStatement();
      stmt.execute("CREATE DATABASE " + dbName);
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
      stmt.execute("DROP DATABASE " + dbName);
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
      setupDataSource = createDataSourceForDB(null);
    }

    return setupDataSource;
  }

  /**
   * Note that <code>dbName</code> can be <code>null</code>, in which case a <i>setup</i> datasource must be returned.
   * A connection form a setup< datasource can be used to create or drop other databases.
   */
  protected abstract DataSource createDataSourceForDB(String dbName);
}
