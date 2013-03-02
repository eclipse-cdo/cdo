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
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBSchemaTransaction;

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
public final class DBConnection extends DBElement implements IDBConnection
{
  private final DBInstance dbInstance;

  private final NavigableMap<String, DBPreparedStatement> cache = new TreeMap<String, DBPreparedStatement>();

  private final Set<DBPreparedStatement> checkOuts = new HashSet<DBPreparedStatement>();

  private int lastTouch;

  private Connection connection;

  public DBConnection(DBInstance dbInstance)
  {
    this.dbInstance = dbInstance;
    connection = dbInstance.getDBConnectionProvider().getConnection();
  }

  public DBInstance getDBInstance()
  {
    return dbInstance;
  }

  public void close()
  {
    DBUtil.close(connection);
    connection = null;

    dbInstance.closeDBConnection(this);
  }

  public boolean isClosed()
  {
    return connection != null;
  }

  public Connection getSQLConnection()
  {
    return connection;
  }

  public IDBPreparedStatement getPreparedStatement(String sql, ReuseProbability reuseProbability)
  {
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

    if (cache.size() > dbInstance.getStatementCacheCapacity())
    {
      DBPreparedStatement old = cache.remove(cache.firstKey());
      DBUtil.close(old.getDelegate());
    }
  }

  public IDBSchemaTransaction startSchemaTransaction()
  {
    DBSchemaTransaction dbSchemaTransaction = new DBSchemaTransaction(this);
    dbInstance.setDBSchemaTransaction(dbSchemaTransaction);
    return dbSchemaTransaction;
  }
}
