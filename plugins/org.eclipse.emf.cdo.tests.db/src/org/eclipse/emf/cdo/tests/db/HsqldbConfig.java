/*
 * Copyright (c) 2011, 2012, 2015-2017, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.tests.db.bundle.OM;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;

import org.eclipse.emf.common.util.WrappedException;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HsqldbConfig extends DBConfig
{
  public static final String DB_ADAPTER_NAME = "Hsqldb";

  private static final long serialVersionUID = 1L;

  public static boolean USE_VERIFIER = false;

  private transient ArrayList<HSQLDBDataSource> dataSources;

  public HsqldbConfig()
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
    return new HSQLDBAdapter();
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    HSQLDBDataSource dataSource = new HSQLDBDataSource();
    dataSource.setDatabase("jdbc:hsqldb:mem:" + repoName);
    dataSource.setUser("sa");

    try
    {
      dataSource.setLogWriter(new PrintWriter(System.err));
    }
    catch (SQLException ex)
    {
      OM.LOG.warn(ex.getMessage());
    }

    if (dataSources == null)
    {
      dataSources = new ArrayList<>();
    }

    dataSources.add(dataSource);
    return dataSource;
  }

  @Override
  public void tearDown() throws Exception
  {
    try
    {
      if (USE_VERIFIER)
      {
        IRepository testRepository = getRepository(REPOSITORY_NAME);
        if (testRepository != null)
        {
          getVerifier(testRepository).verify();
        }
      }
    }
    finally
    {
      super.tearDown();
    }
  }

  @Override
  protected void deactivateRepositories()
  {
    try
    {
      shutDownHsqldb();
    }
    catch (SQLException e)
    {
      throw new WrappedException(e);
    }
    finally
    {
      super.deactivateRepositories();
    }
  }

  protected DBStoreVerifier getVerifier(IRepository repository)
  {
    return new DBStoreVerifier.Audit(repository);
  }

  private void shutDownHsqldb() throws SQLException
  {
    for (HSQLDBDataSource ds : dataSources)
    {
      Connection connection = null;
      Statement statement = null;

      try
      {
        connection = ds.getConnection();
        statement = connection.createStatement();
        statement.execute("SHUTDOWN");
      }
      finally
      {
        DBUtil.close(statement);
        DBUtil.close(connection);
      }
    }

    dataSources.clear();
  }

  @Override
  protected void initRepositoryProperties(Map<String, String> props)
  {
    super.initRepositoryProperties(props);
    props.put(Props.SUPPORTING_AUDITS, Boolean.TRUE.toString());
  }
}
