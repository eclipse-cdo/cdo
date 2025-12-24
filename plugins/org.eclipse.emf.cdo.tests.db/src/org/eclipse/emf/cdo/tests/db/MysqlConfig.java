/*
 * Copyright (c) 2011-2013, 2016, 2017, 2019, 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Instructions on how to test with MySQL for Windows:
 * <ol>
 * <li> Review and execute <code>/org.eclipse.emf.cdo.tests.db/install-db/install-mysql.ant</code>
 * <li> Double-click <code>server-start.cmd</code> in the new MySQL installation
 * <li> Run the "CDO AllTests (Mysql)" launch configuration
 * <li> When done, double-click <code>server-stop.cmd</code> in the new MySQL installation
 * </ol>
 *
 * @author Eike Stepper
 * @author Simon McDuff
 */
public class MysqlConfig extends AbstractSetupDBConfig
{
  public static final String DB_ADAPTER_NAME = "Mysql";

  public static final String HOST = "localhost";

  public static final String USER = "root";

  public static final String PASS = "";

  private static final long serialVersionUID = 1L;

  public MysqlConfig()
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
    return new MYSQLAdapter();
  }

  @SuppressWarnings("null")
  @Override
  protected DataSource createDataSourceForDB(String dbName) throws SQLException
  {
    String url = "jdbc:mysql://" + HOST;
    if (dbName != null)
    {
      url += "/" + dbName;
    }

    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUrl(url);
    dataSource.setUser(USER);
    if (PASS != null && !PASS.isEmpty())
    {
      dataSource.setPassword(PASS);
    }

    return dataSource;
  }

  @Override
  protected void initDatabase(Connection connection, Statement stmt, String dbName) throws SQLException
  {
    stmt.execute("CREATE DATABASE " + dbName + " CHARACTER SET utf8mb4");
  }

  @Override
  protected Map<String, String> createStoreProperties(String repoName)
  {
    Map<String, String> props = super.createStoreProperties(repoName);

    // Mysql max key length is 3072 bytes.
    // Charset utf8mb4 uses 4 bytes per character.
    // 3072 / 4 = 768
    props.put("externalRefsURIColumnLength", "768");

    return props;
  }
}
