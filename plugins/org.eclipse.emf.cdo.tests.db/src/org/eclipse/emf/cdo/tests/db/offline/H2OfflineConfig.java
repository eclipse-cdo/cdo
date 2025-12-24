/*
 * Copyright (c) 2012, 2015, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.offline;

import org.eclipse.emf.cdo.tests.db.H2Config;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class H2OfflineConfig extends DBOfflineConfig
{
  private static final long serialVersionUID = 1L;

  private static File reusableFolder;

  private static JdbcDataSource defaultDataSource;

  public H2OfflineConfig()
  {
    super("H2Offline");
  }

  @Override
  public void initCapabilities(Set<String> capabilities)
  {
    super.initCapabilities(capabilities);
    capabilities.add(H2Config.DB_ADAPTER_NAME);
  }

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new H2Adapter();
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    if (reusableFolder == null)
    {
      reusableFolder = createDBFolder();
      IOUtil.delete(reusableFolder);
    }

    if (defaultDataSource == null)
    {
      defaultDataSource = new JdbcDataSource();
      defaultDataSource.setURL("jdbc:h2:" + reusableFolder.getAbsolutePath() + "/h2test");
    }

    Connection conn = null;
    Statement stmt = null;

    try
    {
      conn = defaultDataSource.getConnection();
      stmt = conn.createStatement();
      stmt.execute("DROP SCHEMA IF EXISTS " + repoName);
      stmt.execute("CREATE SCHEMA " + repoName);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      DBUtil.close(conn);
      DBUtil.close(stmt);
    }

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + reusableFolder.getAbsolutePath() + "/h2test;SCHEMA=" + repoName);
    return dataSource;
  }

  protected File createDBFolder()
  {
    return TMPUtil.createTempFolder("h2_", "_test");
  }

  protected void tearDownClean(String repoName)
  {
    reusableFolder.deleteOnExit();
    Connection connection = null;
    Statement stmt = null;

    try
    {
      connection = defaultDataSource.getConnection();
      stmt = connection.createStatement();
      stmt.execute("DROP SCHEMA " + repoName);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      DBUtil.close(stmt);
      DBUtil.close(connection);
    }
  }
}
