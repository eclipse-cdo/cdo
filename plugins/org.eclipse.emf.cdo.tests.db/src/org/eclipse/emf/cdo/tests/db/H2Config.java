/*
 * Copyright (c) 2011-2013, 2016-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.db.bundle.OM;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class H2Config extends DBConfig
{
  public static final String DB_ADAPTER_NAME = "H2";

  private static final long serialVersionUID = 1L;

  private static final Boolean disableRepositoryRecreationOptimization = Boolean
      .getBoolean("org.eclipse.emf.cdo.tests.db.H2Config.disableRepositoryRecreationOptimization");

  private static File reusableFolder;

  private static JdbcDataSource defaultDataSource;

  /**
   * @see #optimizeRepositoryRecreation(String, JdbcDataSource)
   */
  private static final Map<String, Connection> leakyConnections = new HashMap<String, Connection>();

  public H2Config()
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
    return new H2Adapter();
  }

  protected File createDBFolder()
  {
    return TMPUtil.createTempFolder("h2_", "_test");
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    if (reusableFolder == null)
    {
      reusableFolder = createDBFolder();
      IOUtil.delete(reusableFolder);
    }

    String url = "jdbc:h2:" + reusableFolder.getAbsolutePath() + "/h2test;LOCK_TIMEOUT=10000;TRACE_LEVEL_FILE=0";

    if (defaultDataSource == null)
    {
      defaultDataSource = new JdbcDataSource();
      defaultDataSource.setURL(url);
    }

    H2Adapter.createSchema(defaultDataSource, repoName, !isRestarting());

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL(url + ";SCHEMA=" + repoName);

    if (!disableRepositoryRecreationOptimization)
    {
      optimizeRepositoryRecreation(repoName, dataSource);
    }

    return dataSource;
  }

  /**
   * This method implements kind of an evil performance optimization hack!
   *
   * As it turned out, a leaked JDBC connection leads to a cached H2 Database object
   * in org.h2.engine.Engine.DATABASES, which, in turn, speeds up the recreation of
   * CDO repositories as requested by RepositoryConfig.needsCleanRepos().
   * A standard H2 test suite with 1555 test cases executes ~3.3 times faster then.
   */
  private void optimizeRepositoryRecreation(String repoName, JdbcDataSource dataSource)
  {
    Connection leakyConnection = leakyConnections.get(repoName);
    if (leakyConnection == null)
    {
      try
      {
        leakyConnection = dataSource.getConnection();
        leakyConnections.put(repoName, leakyConnection);
      }
      catch (SQLException ex)
      {
        OM.LOG.info(ex);
      }
    }
  }

  @Override
  public void mainSuiteFinished() throws Exception
  {
    for (Connection leakyConnection : leakyConnections.values())
    {
      leakyConnection.close();
    }

    leakyConnections.clear();

    deactivateRepositories();

    // if (defaultDataSource != null)
    // {
    // H2Adapter.shutdown(defaultDataSource);
    // defaultDataSource = null;
    // }

    if (reusableFolder != null)
    {
      IOUtil.delete(reusableFolder);
      reusableFolder = null;
    }
  }
}
