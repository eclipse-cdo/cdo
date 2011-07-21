/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public class H2OfflineConfig extends OfflineConfig
{
  private static final long serialVersionUID = 1L;

  private static File reusableFolder;

  private static JdbcDataSource defaultDataSource;

  public H2OfflineConfig()
  {
    super("H2Offline");
  }

  public IStore createStore(String repoName)
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    IDBAdapter dbAdapter = createDBAdapter();
    DataSource dataSource = createDataSource(repoName);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  protected IMappingStrategy createMappingStrategy()
  {
    return CDODBUtil.createHorizontalMappingStrategy();
  }

  protected IDBAdapter createDBAdapter()
  {
    return new H2Adapter();
  }

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

  @Override
  public void setUp() throws Exception
  {
    CDODBUtil.prepareContainer(IPluginContainer.INSTANCE);
    super.setUp();
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
