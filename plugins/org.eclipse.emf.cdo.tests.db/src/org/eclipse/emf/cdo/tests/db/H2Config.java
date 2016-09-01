/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class H2Config extends DBConfig
{
  public static final String DB_ADAPTER_NAME = "H2";

  private static final long serialVersionUID = 1L;

  private static File reusableFolder;

  private static JdbcDataSource defaultDataSource;

  public H2Config(boolean supportingAudits, boolean supportingBranches, boolean withRanges, boolean copyOnBranch,
      IDGenerationLocation idGenerationLocation)
  {
    super(DB_ADAPTER_NAME, supportingAudits, supportingBranches, withRanges, copyOnBranch, idGenerationLocation);
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
    return dataSource;
  }

  protected File createDBFolder()
  {
    return TMPUtil.createTempFolder("h2_", "_test");
  }

  @Override
  public void mainSuiteFinished() throws Exception
  {
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
