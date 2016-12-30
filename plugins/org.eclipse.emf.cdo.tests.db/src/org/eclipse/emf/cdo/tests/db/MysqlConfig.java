/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.db.mysql.MYSQLAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

import java.sql.SQLException;

/**
 * @author Simon McDuff
 */
public class MysqlConfig extends AbstractSetupDBConfig
{
  public static final String DB_ADAPTER_NAME = "Mysql";

  /**
   * Instructions to test with MySQL: - create a mysql instance - set HOST to the host where the DB is running
   * (listening on TCP) - set USER to a user who can create and drop databases (root, essentially) - set PASS to the
   * password of the said user
   */
  public static final String HOST = "localhost";

  public static final String SCHEMA = "big";

  public static final String USER = "test";

  public static final String PASS = "0000";

  private static final long serialVersionUID = 1L;

  public MysqlConfig(boolean supportingAudits, boolean supportingBranches, boolean inverseLists, IDGenerationLocation idGenerationLocation)
  {
    super(DB_ADAPTER_NAME, supportingAudits, supportingBranches, false, false, inverseLists, idGenerationLocation);
  }

  @Override
  protected String getDBAdapterName()
  {
    return DB_ADAPTER_NAME;
  }

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new MYSQLAdapter();
  }

  @Override
  protected DataSource createDataSourceForDB(String dbName) throws SQLException
  {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUrl("jdbc:mysql://" + HOST + "/" + SCHEMA);
    dataSource.setUser(USER);
    if (PASS != null)
    {
      dataSource.setPassword(PASS);
    }

    return dataSource;
  }
}
