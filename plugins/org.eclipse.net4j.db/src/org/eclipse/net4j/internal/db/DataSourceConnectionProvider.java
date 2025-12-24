/*
 * Copyright (c) 2007, 2008, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBConnectionProvider2;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class DataSourceConnectionProvider implements IDBConnectionProvider2
{
  private final DataSource dataSource;

  private final String user;

  public DataSourceConnectionProvider(DataSource dataSource, String user)
  {
    this.dataSource = dataSource;
    this.user = user;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  @Override
  public String getUserID()
  {
    return user;
  }

  @Override
  public Connection getConnection()
  {
    try
    {
      return dataSource.getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public String toString()
  {
    return dataSource.toString();
  }
}
