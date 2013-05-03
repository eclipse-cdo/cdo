/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.util.CheckUtil;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Eike Stepper
 */
public final class DBConnection implements IDBConnection
{
  private final NavigableMap<String, DBPreparedStatement> cache = new TreeMap<String, DBPreparedStatement>();

  private final Set<DBPreparedStatement> checkOuts = new HashSet<DBPreparedStatement>();

  private final DBDatabase database;

  private final Connection delegate;

  private int lastTouch;

  private boolean closed;

  public DBConnection(DBDatabase database, Connection delegate)
  {
    this.database = database;
    this.delegate = delegate;

    try
    {
      delegate.setAutoCommit(false);
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
    DBUtil.close(delegate);
    closed = true;
    database.closeConnection(this);
  }

  public boolean isClosed()
  {
    return closed;
  }

  public IDBSchemaTransaction openSchemaTransaction()
  {
    DBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    schemaTransaction.setConnection(this);
    return schemaTransaction;
  }

  public IDBPreparedStatement prepareStatement(String sql, ReuseProbability reuseProbability)
  {
    return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, reuseProbability);
  }

  public IDBPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
      ReuseProbability reuseProbability)
  {
    database.beginSchemaAccess(false);

    DBPreparedStatement preparedStatement = cache.remove(sql);
    if (preparedStatement == null)
    {
      try
      {
        PreparedStatement delegate = this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
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

  public PreparedStatement prepareStatement(String sql) throws SQLException
  {
    return prepareStatement(sql, ReuseProbability.LOW);
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException
  {
    return prepareStatement(sql, resultSetType, resultSetConcurrency, ReuseProbability.LOW);
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

  public <T> T unwrap(Class<T> iface) throws SQLException
  {
    return delegate.unwrap(iface);
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException
  {
    return delegate.isWrapperFor(iface);
  }

  public Statement createStatement() throws SQLException
  {
    return delegate.createStatement();
  }

  public CallableStatement prepareCall(String sql) throws SQLException
  {
    return delegate.prepareCall(sql);
  }

  public String nativeSQL(String sql) throws SQLException
  {
    return delegate.nativeSQL(sql);
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException
  {
    delegate.setAutoCommit(autoCommit);
  }

  public boolean getAutoCommit() throws SQLException
  {
    return delegate.getAutoCommit();
  }

  public void commit() throws SQLException
  {
    delegate.commit();
  }

  public void rollback() throws SQLException
  {
    delegate.rollback();
  }

  public DatabaseMetaData getMetaData() throws SQLException
  {
    return delegate.getMetaData();
  }

  public void setReadOnly(boolean readOnly) throws SQLException
  {
    delegate.setReadOnly(readOnly);
  }

  public boolean isReadOnly() throws SQLException
  {
    return delegate.isReadOnly();
  }

  public void setCatalog(String catalog) throws SQLException
  {
    delegate.setCatalog(catalog);
  }

  public String getCatalog() throws SQLException
  {
    return delegate.getCatalog();
  }

  public void setTransactionIsolation(int level) throws SQLException
  {
    delegate.setTransactionIsolation(level);
  }

  public int getTransactionIsolation() throws SQLException
  {
    return delegate.getTransactionIsolation();
  }

  public SQLWarning getWarnings() throws SQLException
  {
    return delegate.getWarnings();
  }

  public void clearWarnings() throws SQLException
  {
    delegate.clearWarnings();
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
  {
    return delegate.createStatement(resultSetType, resultSetConcurrency);
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
  {
    return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  public Map<String, Class<?>> getTypeMap() throws SQLException
  {
    return delegate.getTypeMap();
  }

  public void setTypeMap(Map<String, Class<?>> map) throws SQLException
  {
    delegate.setTypeMap(map);
  }

  public void setHoldability(int holdability) throws SQLException
  {
    delegate.setHoldability(holdability);
  }

  public int getHoldability() throws SQLException
  {
    return delegate.getHoldability();
  }

  public Savepoint setSavepoint() throws SQLException
  {
    return delegate.setSavepoint();
  }

  public Savepoint setSavepoint(String name) throws SQLException
  {
    return delegate.setSavepoint(name);
  }

  public void rollback(Savepoint savepoint) throws SQLException
  {
    delegate.rollback(savepoint);
  }

  public void releaseSavepoint(Savepoint savepoint) throws SQLException
  {
    delegate.releaseSavepoint(savepoint);
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException
  {
    return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException
  {
    return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  public Clob createClob() throws SQLException
  {
    return delegate.createClob();
  }

  public Blob createBlob() throws SQLException
  {
    return delegate.createBlob();
  }

  public NClob createNClob() throws SQLException
  {
    return delegate.createNClob();
  }

  public SQLXML createSQLXML() throws SQLException
  {
    return delegate.createSQLXML();
  }

  public boolean isValid(int timeout) throws SQLException
  {
    return delegate.isValid(timeout);
  }

  public void setClientInfo(String name, String value) throws SQLClientInfoException
  {
    delegate.setClientInfo(name, value);
  }

  public void setClientInfo(Properties properties) throws SQLClientInfoException
  {
    delegate.setClientInfo(properties);
  }

  public String getClientInfo(String name) throws SQLException
  {
    return delegate.getClientInfo(name);
  }

  public Properties getClientInfo() throws SQLException
  {
    return delegate.getClientInfo();
  }

  public Array createArrayOf(String typeName, Object[] elements) throws SQLException
  {
    return delegate.createArrayOf(typeName, elements);
  }

  public Struct createStruct(String typeName, Object[] attributes) throws SQLException
  {
    return delegate.createStruct(typeName, attributes);
  }

  @Deprecated
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
}
