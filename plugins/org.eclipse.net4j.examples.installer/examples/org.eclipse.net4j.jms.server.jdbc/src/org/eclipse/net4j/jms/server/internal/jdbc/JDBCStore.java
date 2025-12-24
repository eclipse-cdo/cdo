/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server.internal.jdbc;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.jms.internal.server.store.AbstractStore;
import org.eclipse.net4j.jms.internal.server.store.StoreException;
import org.eclipse.net4j.jms.server.IStoreTransaction;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class JDBCStore extends AbstractStore
{
  private static final String STORE_TYPE = "JDBC"; //$NON-NLS-1$

  private IDBAdapter dbAdapter;

  private DataSource dataSource;

  public JDBCStore(IDBAdapter dbAdapter, DataSource dataSource)
  {
    super(STORE_TYPE);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("dbAdapter == null"); //$NON-NLS-1$
    }

    if (dataSource == null)
    {
      throw new IllegalArgumentException("dataSource == null"); //$NON-NLS-1$
    }

    this.dbAdapter = dbAdapter;
    this.dataSource = dataSource;
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public Connection getConnection()
  {
    try
    {
      return dataSource.getConnection();
    }
    catch (SQLException ex)
    {
      throw new StoreException(ex);
    }
  }

  public void initDatabase(String instanceID)
  {
    JMSSchema.INSTANCE.create(dbAdapter, dataSource);
    // TODO Store instanceID
  }

  @Override
  public IStoreTransaction startTransaction()
  {
    try
    {
      Connection connection = getConnection();
      connection.setAutoCommit(false);
      return new JDBCTransaction(this, connection);
    }
    catch (SQLException ex)
    {
      throw new StoreException(ex);
    }
  }

  @Override
  public void commitTransaction(IStoreTransaction transaction)
  {
    JDBCTransaction jdbcTransaction = (JDBCTransaction)transaction;
    try
    {
      Connection connection = jdbcTransaction.getConnection();
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new StoreException(ex);
    }
    finally
    {
      jdbcTransaction.dispose();
    }
  }

  @Override
  public void rollbackTransaction(IStoreTransaction transaction)
  {
    JDBCTransaction jdbcTransaction = (JDBCTransaction)transaction;
    try
    {
      Connection connection = jdbcTransaction.getConnection();
      connection.rollback();
    }
    catch (SQLException ex)
    {
      throw new StoreException(ex);
    }
    finally
    {
      jdbcTransaction.dispose();
    }
  }
}
