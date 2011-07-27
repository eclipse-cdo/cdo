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

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class H2OfflineConfig extends OfflineConfig
{
  private static final long serialVersionUID = 1L;

  private static File reusableFolder;

  private static JdbcDataSource defaultDataSource;

  private boolean withRanges;

  private boolean copyOnBranch;

  public H2OfflineConfig(boolean withRanges, boolean copyOnBranch, IDGenerationLocation idGenerationLocation)
  {
    super("H2Offline", idGenerationLocation);
    this.withRanges = withRanges;
    this.copyOnBranch = copyOnBranch;
  }

  public boolean isWithRanges()
  {
    return withRanges;
  }

  public boolean isCopyOnBranch()
  {
    return copyOnBranch;
  }

  public IStore createStore(String repoName)
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    mappingStrategy.setProperties(createMappingStrategyProperties());

    IDBAdapter dbAdapter = createDBAdapter();

    DataSource dataSource = createDataSource(repoName);
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);

    return CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);
  }

  @Override
  public void setUp() throws Exception
  {
    CDODBUtil.prepareContainer(IPluginContainer.INSTANCE);
    super.setUp();
  }

  protected Map<String, String> createMappingStrategyProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IMappingStrategy.PROP_QUALIFIED_NAMES, "true");
    props.put(CDODBUtil.PROP_COPY_ON_BRANCH, Boolean.toString(copyOnBranch));
    return props;
  }

  protected IMappingStrategy createMappingStrategy()
  {
    return CDODBUtil.createHorizontalMappingStrategy(isSupportingAudits(), isSupportingBranches(), withRanges);
  }

  @Override
  protected String getMappingStrategySpecialization()
  {
    return (withRanges ? "-ranges" : "") + (copyOnBranch ? "-copy" : "");
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
