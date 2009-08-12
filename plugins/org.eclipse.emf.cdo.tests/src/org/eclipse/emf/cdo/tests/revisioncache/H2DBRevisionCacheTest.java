/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class H2DBRevisionCacheTest extends AbstractDBRevisionCacheTest
{

  private static class H2DBProvider implements IDBProvider
  {
    private static final String DB_NAME = TMPUtil.createTempFolder("h2db").getAbsolutePath();

    public DataSource createDataSource()
    {
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:" + DB_NAME);
      return dataSource;
    }

    /**
     * Drop all table on a given h2 database.
     */
    public void dropAllTables(Connection connection)
    {
      DBUtil.dropAllTables(connection, null);
    }

    public IDBAdapter getAdapter()
    {
      return new H2Adapter();
    }
  }

  @Override
  protected IDBProvider createDbProvider()
  {
    return new H2DBProvider();
  }

}
