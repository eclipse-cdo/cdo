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
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.SetContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author Eike Stepper
 */
public final class DBDatabase extends SetContainer<IDBTransaction> implements IDBDatabase
{
  private DBAdapter adapter;

  private IDBConnectionProvider connectionProvider;

  private int statementCacheCapacity = DEFAULT_STATEMENT_CACHE_CAPACITY;

  private DBSchema schema;

  private DBSchemaTransaction schemaTransaction;

  private final LinkedList<SchemaAccess> schemaAccessQueue = new LinkedList<SchemaAccess>();

  public DBDatabase(final DBAdapter adapter, IDBConnectionProvider connectionProvider, final String schemaName)
  {
    super(IDBTransaction.class);
    this.adapter = adapter;
    this.connectionProvider = connectionProvider;

    schema = DBUtil.execute(connectionProvider, new RunnableWithConnection<DBSchema>()
    {
      public DBSchema run(Connection connection) throws SQLException
      {
        return (DBSchema)adapter.readSchema(connection, schemaName);
      }
    });

    schema.lock();
    activate();
  }

  public DBAdapter getAdapter()
  {
    return adapter;
  }

  public IDBConnectionProvider getConnectionProvider()
  {
    return connectionProvider;
  }

  public int getStatementCacheCapacity()
  {
    return statementCacheCapacity;
  }

  public void setStatementCacheCapacity(int statementCacheCapacity)
  {
    this.statementCacheCapacity = statementCacheCapacity;
  }

  public DBSchema getSchema()
  {
    return schema;
  }

  public DBSchemaTransaction openSchemaTransaction()
  {
    beginSchemaAccess(true);

    DBSchemaTransaction schemaTransaction = new DBSchemaTransaction(this);
    this.schemaTransaction = schemaTransaction;
    return schemaTransaction;
  }

  public void closeSchemaTransaction()
  {
    try
    {
      for (IDBTransaction transaction : getTransactions())
      {
        ((DBTransaction)transaction).invalidateStatementCache();
      }
    }
    finally
    {
      schemaTransaction = null;
      endSchemaAccess();
    }
  }

  public DBSchemaTransaction getSchemaTransaction()
  {
    return schemaTransaction;
  }

  public DBTransaction openTransaction()
  {
    DBTransaction transaction = new DBTransaction(this);
    addElement(transaction);
    return transaction;
  }

  public void closeTransaction(DBTransaction transaction)
  {
    removeElement(transaction);
  }

  public IDBTransaction[] getTransactions()
  {
    return getElements();
  }

  public boolean isClosed()
  {
    return !isActive();
  }

  public void close()
  {
    deactivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IDBTransaction transaction : getTransactions())
    {
      transaction.close();
    }

    super.doDeactivate();
  }

  public void beginSchemaAccess(boolean write)
  {
    SchemaAccess schemaAccess = null;
    synchronized (schemaAccessQueue)
    {
      if (write)
      {
        schemaAccess = new WriteSchemaAccess();
        schemaAccessQueue.addLast(schemaAccess);
      }
      else
      {
        if (!schemaAccessQueue.isEmpty())
        {
          schemaAccess = schemaAccessQueue.getFirst();
          if (schemaAccess instanceof ReadSchemaAccess)
          {
            ReadSchemaAccess readSchemaAccess = (ReadSchemaAccess)schemaAccess;
            readSchemaAccess.incrementReaders();
          }
        }

        if (schemaAccess == null)
        {
          schemaAccess = new ReadSchemaAccess();
          schemaAccessQueue.addLast(schemaAccess);
        }
      }
    }

    for (;;)
    {
      synchronized (schemaAccessQueue)
      {
        if (schemaAccessQueue.getFirst() == schemaAccess)
        {
          return;
        }

        try
        {
          schemaAccessQueue.wait();
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }
  }

  public void endSchemaAccess()
  {
    synchronized (schemaAccessQueue)
    {
      SchemaAccess schemaAccess = schemaAccessQueue.getFirst();
      if (schemaAccess instanceof ReadSchemaAccess)
      {
        ReadSchemaAccess readSchemaAccess = (ReadSchemaAccess)schemaAccess;
        if (readSchemaAccess.decrementReaders())
        {
          return;
        }
      }

      schemaAccessQueue.removeFirst();
      schemaAccessQueue.notifyAll();
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface SchemaAccess
  {
  }

  /**
   * @author Eike Stepper
   */
  public final class ReadSchemaAccess implements SchemaAccess
  {
    private int readers;

    public void incrementReaders()
    {
      ++readers;
    }

    public boolean decrementReaders()
    {
      return --readers > 0;
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class WriteSchemaAccess implements SchemaAccess
  {
  }
}
