/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBStoreManager;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.internal.util.transaction.StoreManager;
import org.eclipse.net4j.util.transaction.StoreException;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public abstract class DBStoreManager<TRANSACTION extends DBStoreTransaction> extends StoreManager<TRANSACTION>
    implements IDBStoreManager<TRANSACTION>
{
  private static final String STORE_TYPE = "JDBC";

  private IDBSchema schema;

  private IDBAdapter dbAdapter;

  private DataSource dataSource;

  public DBStoreManager(IDBSchema schema, IDBAdapter dbAdapter, DataSource dataSource)
  {
    super(STORE_TYPE);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("dbAdapter == null");
    }

    if (dataSource == null)
    {
      throw new IllegalArgumentException("dataSource == null");
    }

    this.schema = schema;
    this.dbAdapter = dbAdapter;
    this.dataSource = dataSource;
  }

  public IDBSchema getSchema()
  {
    return schema;
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

  public void initDatabase()
  {
    schema.create(dbAdapter, dataSource);
  }

  public TRANSACTION startTransaction() throws StoreException
  {
    try
    {
      Connection connection = getConnection();
      connection.setAutoCommit(false);
      return createTransaction(connection);
    }
    catch (SQLException ex)
    {
      throw new StoreException(ex);
    }
  }

  public void commitTransaction(TRANSACTION transaction) throws StoreException
  {
    try
    {
      Connection connection = transaction.getConnection();
      connection.commit();
    }
    catch (StoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new StoreException(ex);
    }
    finally
    {
      transaction.dispose();
    }
  }

  public void rollbackTransaction(TRANSACTION transaction) throws StoreException
  {
    try
    {
      Connection connection = transaction.getConnection();
      connection.rollback();
    }
    catch (StoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new StoreException(ex);
    }
    finally
    {
      transaction.dispose();
    }
  }

  protected abstract TRANSACTION createTransaction(Connection connection);
}
