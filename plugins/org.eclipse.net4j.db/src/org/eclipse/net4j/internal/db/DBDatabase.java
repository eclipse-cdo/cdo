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
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.event.Event;

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

  private IDBSchema schema;

  private DBSchemaTransaction schemaTransaction;

  private final LinkedList<SchemaAccess> schemaAccessQueue = new LinkedList<SchemaAccess>();

  public DBDatabase(final DBAdapter adapter, IDBConnectionProvider connectionProvider, final String schemaName)
  {
    super(IDBTransaction.class);
    this.adapter = adapter;
    this.connectionProvider = connectionProvider;

    schema = DBUtil.execute(connectionProvider, new RunnableWithConnection<IDBSchema>()
    {
      public IDBSchema run(Connection connection) throws SQLException
      {
        return adapter.readSchema(connection, schemaName);
      }
    });

    ((InternalDBSchema)schema).lock();
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

  public IDBSchema getSchema()
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

  public void closeSchemaTransaction(DBSchemaDelta delta)
  {
    try
    {
      for (IDBTransaction transaction : getTransactions())
      {
        ((DBTransaction)transaction).invalidateStatementCache();
      }

      fireEvent(new SchemaChangedEventImpl(delta));
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

  public void updateSchema(RunnableWithSchema runnable)
  {
    DBSchemaTransaction schemaTransaction = openSchemaTransaction();

    try
    {
      IDBSchema workingCopy = schemaTransaction.getWorkingCopy();
      runnable.run(workingCopy);
      schemaTransaction.commit();
    }
    finally
    {
      schemaTransaction.close();
    }
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

  public int getStatementCacheCapacity()
  {
    return statementCacheCapacity;
  }

  public void setStatementCacheCapacity(int statementCacheCapacity)
  {
    this.statementCacheCapacity = statementCacheCapacity;
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
  private interface SchemaAccess
  {
  }

  /**
   * @author Eike Stepper
   */
  private final class ReadSchemaAccess implements SchemaAccess
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

    @Override
    public String toString()
    {
      return "READERS[" + readers + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteSchemaAccess implements SchemaAccess
  {
    @Override
    public String toString()
    {
      return "WRITER";
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SchemaChangedEventImpl extends Event implements SchemaChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final IDBSchemaDelta schemaDelta;

    public SchemaChangedEventImpl(IDBSchemaDelta schemaDelta)
    {
      super(DBDatabase.this);
      this.schemaDelta = schemaDelta;
    }

    @Override
    public IDBDatabase getSource()
    {
      return (IDBDatabase)super.getSource();
    }

    public IDBSchemaDelta getSchemaDelta()
    {
      return schemaDelta;
    }
  }
}
