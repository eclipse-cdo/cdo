/*
 * Copyright (c) 2013, 2015, 2016, 2018-2020, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.RunnableWithConnection;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.IUserAware;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class DBDatabase extends SetContainer<IDBConnection> implements IDBDatabase
{
  private static final long TIMEOUT_SCHEMA_ACCESS = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.internal.db.DBDatabase.TIMEOUT_SCHEMA_ACCESS", 15000L);

  private static final boolean DEBUG_SCHEMA_ACCESS = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.internal.db.DBDatabase.DEBUG_SCHEMA_ACCESS");

  private static final boolean TRACK_SCHEMA_ACCESS = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.internal.db.DBDatabase.TRACK_SCHEMA_ACCESS");

  private IDBAdapter adapter;

  private IDBConnectionProvider connectionProvider;

  private int statementCacheCapacity = DEFAULT_STATEMENT_CACHE_CAPACITY;

  private IDBSchema schema;

  private final LinkedList<SchemaAccess> schemaAccessQueue = new LinkedList<>();

  private int waitingSchemaWriters;

  public DBDatabase(IDBAdapter adapter, IDBConnectionProvider connectionProvider, String schemaName, boolean fixNullableIndexColumns,
      boolean qualifiedTableNames)
  {
    super(IDBConnection.class);
    this.adapter = adapter;
    this.connectionProvider = connectionProvider;

    schema = DBUtil.execute(DBDatabase.this, new RunnableWithConnection<IDBSchema>()
    {
      @Override
      public IDBSchema run(Connection connection) throws SQLException
      {
        return DBUtil.readSchema(adapter, connection, schemaName, fixNullableIndexColumns, qualifiedTableNames);
      }
    });

    ((InternalDBSchema)schema).lock();
    activate();
  }

  /**
   * Compatibility constructor for existing internal callers.
   *
   * @deprecated Use {@link #DBDatabase(IDBAdapter, IDBConnectionProvider, String, boolean, boolean)} instead.
   */
  @Deprecated
  public DBDatabase(DBAdapter adapter, IDBConnectionProvider connectionProvider, String schemaName, boolean fixNullableIndexColumns,
      boolean qualifiedTableNames)
  {
    this((IDBAdapter)adapter, connectionProvider, schemaName, fixNullableIndexColumns, qualifiedTableNames);
  }

  @Override
  public String getUserID()
  {
    if (connectionProvider instanceof IUserAware)
    {
      return ((IUserAware)connectionProvider).getUserID();
    }

    return null;
  }

  @Override
  public IDBAdapter getAdapter()
  {
    return adapter;
  }

  @Override
  public IDBSchema getSchema()
  {
    return schema;
  }

  @Override
  public DBSchemaTransaction openSchemaTransaction()
  {
    return openSchemaTransaction(null);
  }

  @Override
  public DBSchemaTransaction openSchemaTransaction(IDBConnection connection)
  {
    DBSchemaTransaction schemaTransaction = new DBSchemaTransaction(this);
    schemaTransaction.setConnection((DBConnection)connection);
    return schemaTransaction;
  }

  /**
   * Completes a schema transaction by invalidating cached statements and notifying listeners about the schema change.
   * <p>
   * The schema write access is exclusive and waits for every checked-out statement to be closed. A caller must close
   * all statements checked out from the schema transaction's connection before committing the transaction; otherwise
   * the writer would wait for a read access held by the same connection and deadlock itself.
   *
   * @param delta
   *          the committed schema changes, or {@code null} if the schema did not change.
   */
  public void closeSchemaTransaction(DBSchemaDelta delta)
  {
    if (delta == null || delta.isEmpty())
    {
      return;
    }

    Object schemaAccessToken = beginSchemaAccess(true);

    try
    {
      for (IDBConnection transaction : getConnections())
      {
        ((DBConnection)transaction).invalidateStatementCache();
      }

      fireEvent(new SchemaChangedEventImpl(this, delta));
    }
    finally
    {
      endSchemaAccess(schemaAccessToken);
    }
  }

  @Override
  @Deprecated
  public DBSchemaTransaction getSchemaTransaction()
  {
    throw new UnsupportedOperationException();
  }

  @Override
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

  @Override
  public DBConnection getConnection()
  {
    Connection delegate = connectionProvider.getConnection();
    if (delegate == null)
    {
      throw new DBException("No connection from connection provider: " + connectionProvider);
    }

    delegate = adapter.modifyConnection(delegate);

    DBConnection connection = new DBConnection(this, delegate);
    addElement(connection);
    return connection;
  }

  public void closeConnection(DBConnection connection)
  {
    removeElement(connection);
  }

  @Override
  public IDBConnection[] getConnections()
  {
    return getElements();
  }

  @Override
  public int getStatementCacheCapacity()
  {
    return statementCacheCapacity;
  }

  @Override
  public void setStatementCacheCapacity(int statementCacheCapacity)
  {
    this.statementCacheCapacity = statementCacheCapacity;
  }

  @Override
  public boolean isClosed()
  {
    return !isActive();
  }

  @Override
  public void close()
  {
    deactivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IDBConnection connection : getConnections())
    {
      connection.close();
    }

    super.doDeactivate();
  }

  public Object beginSchemaAccess(boolean write)
  {
    if (DEBUG_SCHEMA_ACCESS)
    {
      try
      {
        throw new Exception("Begin " + (write ? "write" : "read") + " schema access: " + schema.getName());
      }
      catch (Exception ex)
      {
        ex.printStackTrace(IOUtil.OUT());
      }
    }

    synchronized (schemaAccessQueue)
    {
      SchemaAccess schemaAccess;
      Object token;

      if (write)
      {
        schemaAccess = createWriteSchemaAccess();
        token = schemaAccess;
        schemaAccessQueue.addLast(schemaAccess);
        ++waitingSchemaWriters;
      }
      else
      {
        ReadSchemaAccess readSchemaAccess = null;

        if (waitingSchemaWriters == 0)
        {
          SchemaAccess activeSchemaAccess = schemaAccessQueue.peekFirst();
          if (activeSchemaAccess instanceof ReadSchemaAccess)
          {
            readSchemaAccess = (ReadSchemaAccess)activeSchemaAccess;
          }
        }

        if (readSchemaAccess == null)
        {
          readSchemaAccess = createReadSchemaAccess();
          schemaAccessQueue.addLast(readSchemaAccess);
        }

        token = readSchemaAccess.addReader();
        schemaAccess = readSchemaAccess;
      }

      boolean success = false;

      try
      {
        long end = System.currentTimeMillis() + TIMEOUT_SCHEMA_ACCESS;

        for (;;)
        {
          // Check for activation before the timeout. An access that became active while waiting is never abandoned.
          if (schemaAccessQueue.getFirst() == schemaAccess)
          {
            success = true;
            return token;
          }

          long remaining = end - System.currentTimeMillis();
          if (remaining <= 0L)
          {
            StringBuilder builder = new StringBuilder("Schema " + schema.getName() + " could not be locked for " + (write ? "write" : "read")
                + " access within " + TIMEOUT_SCHEMA_ACCESS + " milliseconds. Schema access queue:" + StringUtil.NL);
            int i = 0;

            for (SchemaAccess blockingAccess : schemaAccessQueue)
            {
              if (blockingAccess == schemaAccess)
              {
                builder.append("--> ");
              }

              builder.append(i++);
              builder.append(": ");
              builder.append(blockingAccess);
            }

            throw new TimeoutRuntimeException(builder.toString());
          }

          try
          {
            schemaAccessQueue.wait(remaining);
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw WrappedException.wrap(ex);
          }
        }
      }
      finally
      {
        if (write)
        {
          --waitingSchemaWriters;
        }

        if (!success)
        {
          releaseSchemaAccess(schemaAccess, token);
        }
      }
    }
  }

  public void endSchemaAccess(Object token)
  {
    if (DEBUG_SCHEMA_ACCESS)
    {
      try
      {
        throw new Exception("End schema access: " + schema.getName());
      }
      catch (Exception ex)
      {
        ex.printStackTrace(IOUtil.OUT());
      }
    }

    synchronized (schemaAccessQueue)
    {
      SchemaAccess activeSchemaAccess = schemaAccessQueue.peekFirst();
      if (activeSchemaAccess == null || !activeSchemaAccess.hasToken(token))
      {
        throw new IllegalStateException("Schema " + schema.getName() + " is not accessed with token " + token);
      }

      releaseSchemaAccess(activeSchemaAccess, token);
    }
  }

  public String convertString(DBPreparedStatement preparedStatement, int parameterIndex, String value)
  {
    return adapter.convertString(preparedStatement, parameterIndex, value);
  }

  public String convertString(DBResultSet resultSet, int columnIndex, String value)
  {
    return adapter.convertString(resultSet, columnIndex, value);
  }

  public String convertString(DBResultSet resultSet, String columnLabel, String value)
  {
    return adapter.convertString(resultSet, columnLabel, value);
  }

  @Override
  public String toString()
  {
    return "DBDatabase[schema=" + schema + "]";
  }

  /**
   * Must be called while holding the monitor of the {@link #schemaAccessQueue}.
   */
  private void releaseSchemaAccess(SchemaAccess schemaAccess, Object token)
  {
    if (schemaAccess instanceof ReadSchemaAccess && ((ReadSchemaAccess)schemaAccess).removeReader(token))
    {
      // Other readers still share this access.
      return;
    }

    schemaAccessQueue.remove(schemaAccess);
    schemaAccessQueue.notifyAll();
  }

  private ReadSchemaAccess createReadSchemaAccess()
  {
    if (TRACK_SCHEMA_ACCESS)
    {
      return new ReadSchemaAccess.Tracked();
    }

    return new ReadSchemaAccess();
  }

  private WriteSchemaAccess createWriteSchemaAccess()
  {
    if (TRACK_SCHEMA_ACCESS)
    {
      return new WriteSchemaAccess.Tracked();
    }

    return new WriteSchemaAccess();
  }

  /**
   * @author Eike Stepper
   */
  private interface SchemaAccess
  {
    public boolean hasToken(Object token);
  }

  /**
   * @author Eike Stepper
   */
  private static class ReadSchemaAccess implements SchemaAccess
  {
    private final Set<Object> tokens = new HashSet<>();

    private int readers;

    public ReadSchemaAccess()
    {
    }

    public Object addReader()
    {
      Object token = new Object();
      addReader(token);
      return token;
    }

    protected final void addReader(Object token)
    {
      tokens.add(token);
      ++readers;
    }

    @Override
    public boolean hasToken(Object token)
    {
      return tokens.contains(token);
    }

    /**
     * @return <code>true</code> if at least one reader remains, <code>false</code> otherwise.
     */
    public boolean removeReader(Object token)
    {
      if (!tokens.remove(token))
      {
        throw new IllegalStateException("Unknown schema read access token: " + token);
      }

      return --readers > 0;
    }

    @Override
    public String toString()
    {
      return "READERS[" + readers + "]";
    }

    /**
     * @author Eike Stepper
     */
    private static final class Tracked extends ReadSchemaAccess
    {
      private final Map<Object, Exception> stackTraces = new LinkedHashMap<>();

      public Tracked()
      {
      }

      @Override
      public Object addReader()
      {
        Object token = new Object();
        Exception stackTrace;

        try
        {
          throw new Exception();
        }
        catch (Exception ex)
        {
          stackTrace = ex;
        }

        stackTraces.put(token, stackTrace);
        super.addReader(token);
        return token;
      }

      @Override
      public boolean hasToken(Object token)
      {
        return stackTraces.containsKey(token);
      }

      @Override
      public boolean removeReader(Object token)
      {
        if (stackTraces.remove(token) == null)
        {
          throw new IllegalStateException("Unknown tracked schema read access token: " + token);
        }

        return super.removeReader(token);
      }

      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(" --> Read access(es) started here:");
        builder.append(StringUtil.NL);

        for (Exception stackTrace : stackTraces.values())
        {
          ReflectUtil.appendStackTrace(builder, stackTrace.getStackTrace());
          builder.append(StringUtil.NL);
        }

        return builder.toString();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class WriteSchemaAccess implements SchemaAccess
  {
    public WriteSchemaAccess()
    {
    }

    @Override
    public boolean hasToken(Object token)
    {
      return token == this;
    }

    @Override
    public String toString()
    {
      return "WRITER";
    }

    /**
     * @author Eike Stepper
     */
    private static final class Tracked extends WriteSchemaAccess
    {
      private final Exception stackTrace;

      public Tracked()
      {
        try
        {
          throw new Exception();
        }
        catch (Exception ex)
        {
          stackTrace = ex;
        }
      }

      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(" --> Write access started here:");
        builder.append(StringUtil.NL);
        ReflectUtil.appendStackTrace(builder, stackTrace.getStackTrace());
        builder.append(StringUtil.NL);
        return builder.toString();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SchemaChangedEventImpl extends Event implements SchemaChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final IDBSchemaDelta schemaDelta;

    public SchemaChangedEventImpl(DBDatabase database, IDBSchemaDelta schemaDelta)
    {
      super(database);
      this.schemaDelta = schemaDelta;
    }

    @Override
    public IDBDatabase getSource()
    {
      return (IDBDatabase)super.getSource();
    }

    @Override
    public IDBSchemaDelta getSchemaDelta()
    {
      return schemaDelta;
    }
  }
}
