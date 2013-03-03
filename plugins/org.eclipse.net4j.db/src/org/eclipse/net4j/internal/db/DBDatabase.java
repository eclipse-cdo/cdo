/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.RunnableWithConnection;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBTransaction;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class DBDatabase extends DBElement implements IDBDatabase
{
  private final Set<DBTransaction> transactions = new HashSet<DBTransaction>();

  private DBAdapter adapter;

  private IDBConnectionProvider connectionProvider;

  private DBSchema schema;

  private DBSchemaTransaction schemaTransaction;

  private int statementCacheCapacity = DEFAULT_STATEMENT_CACHE_CAPACITY;

  public DBDatabase(DBAdapter adapter, IDBConnectionProvider dbConnectionProvider, final String schemaName)
  {
    this.adapter = adapter;
    connectionProvider = dbConnectionProvider;

    schema = DBUtil.execute(dbConnectionProvider, new RunnableWithConnection<DBSchema>()
    {
      public DBSchema run(Connection connection) throws SQLException
      {
        return (DBSchema)DBUtil.readSchema(schemaName, connection);
      }
    });

    schema.lock();
  }

  public DBAdapter getAdapter()
  {
    return adapter;
  }

  public DBSchema getSchema()
  {
    return schema;
  }

  public DBSchemaTransaction openSchemaTransaction()
  {
    DBSchemaTransaction schemaTransaction = new DBSchemaTransaction(this);
    this.schemaTransaction = schemaTransaction;
    return schemaTransaction;
  }

  public void closeSchemaTransaction()
  {
    schemaTransaction = null;
  }

  public DBSchemaTransaction getSchemaTransaction()
  {
    return schemaTransaction;
  }

  public IDBConnectionProvider getConnectionProvider()
  {
    return connectionProvider;
  }

  public DBTransaction openTransaction()
  {
    DBTransaction transaction = new DBTransaction(this);
    synchronized (transactions)
    {
      transactions.add(transaction);
    }

    fireEvent(new SingleDeltaContainerEvent<IDBTransaction>(this, transaction, IContainerDelta.Kind.ADDED));
    return transaction;
  }

  public void closeTransaction(DBTransaction transaction)
  {
    synchronized (transactions)
    {
      transactions.remove(transaction);
    }

    fireEvent(new SingleDeltaContainerEvent<IDBTransaction>(this, transaction, IContainerDelta.Kind.REMOVED));
  }

  public DBTransaction[] getTransactions()
  {
    synchronized (transactions)
    {
      return transactions.toArray(new DBTransaction[transactions.size()]);
    }
  }

  public boolean isEmpty()
  {
    synchronized (transactions)
    {
      return transactions.isEmpty();
    }
  }

  public DBTransaction[] getElements()
  {
    return getTransactions();
  }

  public int getStatementCacheCapacity()
  {
    return statementCacheCapacity;
  }

  public void setStatementCacheCapacity(int statementCacheCapacity)
  {
    this.statementCacheCapacity = statementCacheCapacity;
  }
}
