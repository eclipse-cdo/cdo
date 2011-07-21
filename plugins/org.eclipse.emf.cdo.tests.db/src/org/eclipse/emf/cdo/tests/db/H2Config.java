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

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
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
public class H2Config extends DBConfig
{
  private static final long serialVersionUID = 1L;

  private static File reusableFolder;

  private static JdbcDataSource defaultDataSource;

  protected transient File dbFolder;

  public H2Config(boolean supportingAudits, boolean supportingBranches, boolean withRanges, boolean copyOnBranch,
      IDGenerationLocation idGenerationLocation)
  {
    super("H2", supportingAudits, supportingBranches, withRanges, copyOnBranch, idGenerationLocation);
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

    dbFolder = reusableFolder;
    if (defaultDataSource == null)
    {
      defaultDataSource = new JdbcDataSource();
      defaultDataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test");
    }

    Connection conn = null;
    Statement stmt = null;

    try
    {
      conn = defaultDataSource.getConnection();
      stmt = conn.createStatement();

      if (!isRestarting())
      {
        stmt.execute("DROP SCHEMA IF EXISTS " + repoName);
      }

      stmt.execute("CREATE SCHEMA IF NOT EXISTS " + repoName);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      DBUtil.close(conn);
      DBUtil.close(stmt);
    }

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test;SCHEMA=" + repoName);
    return dataSource;
  }

  protected File createDBFolder()
  {
    return TMPUtil.createTempFolder("h2_", "_test");
  }
}
