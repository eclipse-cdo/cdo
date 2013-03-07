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

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.IDBTransaction;
import org.eclipse.net4j.util.CheckUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Eike Stepper
 */
public final class DBTransaction implements IDBTransaction
{
  private final DBDatabase database;

  private final NavigableMap<String, DBPreparedStatement> cache = new TreeMap<String, DBPreparedStatement>();

  private final Set<DBPreparedStatement> checkOuts = new HashSet<DBPreparedStatement>();

  private int lastTouch;

  private Connection connection;

  public DBTransaction(DBDatabase database)
  {
    this.database = database;

    IDBConnectionProvider connectionProvider = database.getConnectionProvider();
    connection = connectionProvider.getConnection();
    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + connectionProvider);
    }

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex, "SET AUTO COMMIT = false");
    }
  }

  public DBDatabase getDatabase()
  {
    return database;
  }

  public void close()
  {
    DBUtil.close(connection);
    connection = null;

    database.closeTransaction(this);
  }

  public boolean isClosed()
  {
    return connection != null;
  }

  public Connection getConnection()
  {
    return connection;
  }

  public IDBSchemaTransaction openSchemaTransaction()
  {
    DBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    schemaTransaction.setTransaction(this);
    return schemaTransaction;
  }

  public IDBPreparedStatement prepareStatement(String sql, ReuseProbability reuseProbability)
  {
    database.beginSchemaAccess(false);

    DBPreparedStatement preparedStatement = cache.remove(sql);
    if (preparedStatement == null)
    {
      try
      {
        PreparedStatement delegate = connection.prepareStatement(sql);
        preparedStatement = new DBPreparedStatement(this, sql, reuseProbability, delegate);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    checkOuts.add(preparedStatement);
    return preparedStatement;
  }

  public void releasePreparedStatement(DBPreparedStatement preparedStatement)
  {
    try
    {
      if (preparedStatement == null)
      {
        // Bug 276926: Silently accept preparedStatement == null and do nothing.
        return;
      }

      checkOuts.remove(preparedStatement);
      preparedStatement.setTouch(++lastTouch);

      String sql = preparedStatement.getSQL();
      if (cache.put(sql, preparedStatement) != null)
      {
        throw new IllegalStateException(sql + " already in cache"); //$NON-NLS-1$
      }

      if (cache.size() > database.getStatementCacheCapacity())
      {
        DBPreparedStatement old = cache.remove(cache.firstKey());
        DBUtil.close(old.getDelegate());
      }
    }
    finally
    {
      database.endSchemaAccess();
    }
  }

  public void invalidateStatementCache()
  {
    CheckUtil.checkState(checkOuts.isEmpty(), "Statements are checked out: " + checkOuts);

    // Close all statements in the cache, then clear the cache.
    for (DBPreparedStatement preparedStatement : cache.values())
    {
      PreparedStatement delegate = preparedStatement.getDelegate();
      DBUtil.close(delegate);
    }

    cache.clear();
  }
}
