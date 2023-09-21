/*
 * Copyright (c) 2013, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBResultSet;
import org.eclipse.net4j.db.jdbc.DelegatingPreparedStatement;
import org.eclipse.net4j.util.om.OMPlatform;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class DBPreparedStatement extends DelegatingPreparedStatement implements IDBPreparedStatement
{
  private static final boolean DEFAULT_IMMEDIATE_BATCH_EXECUTION = OMPlatform.INSTANCE
      .isProperty("org.eclipse.net4j.internal.db.DBPreparedStatement.DEFAULT_IMMEDIATE_BATCH_EXECUTION");

  private final String sql;

  private final ReuseProbability reuseProbability;

  private int touch;

  private DBPreparedStatement nextCached;

  private Object schemaAccessToken;

  private boolean immediateBatchExecution = DEFAULT_IMMEDIATE_BATCH_EXECUTION;

  private int addBatchCount;

  public DBPreparedStatement(DBConnection transaction, String sql, ReuseProbability reuseProbability, PreparedStatement delegate)
  {
    super(delegate, transaction);
    this.sql = sql;
    this.reuseProbability = reuseProbability;
  }

  @Override
  public DBConnection getConnection() throws SQLException
  {
    return (DBConnection)super.getConnection();
  }

  @Override
  @Deprecated
  public DBConnection getTransaction()
  {
    try
    {
      return getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public String getSQL()
  {
    return sql;
  }

  @Override
  public ReuseProbability getReuseProbability()
  {
    return reuseProbability;
  }

  public void setTouch(int touch)
  {
    this.touch = touch;
  }

  public DBPreparedStatement getNextCached()
  {
    return nextCached;
  }

  public void setNextCached(DBPreparedStatement nextCached)
  {
    this.nextCached = nextCached;
  }

  @Override
  public int compareTo(IDBPreparedStatement o)
  {
    int result = reuseProbability.compareTo(o.getReuseProbability());
    if (result == 0)
    {
      result = ((DBPreparedStatement)o).touch - touch;
    }

    return result;
  }

  @Override
  public String toString()
  {
    return getDelegate().toString();
  }

  @Override
  public void close() throws SQLException
  {
    getConnection().releasePreparedStatement(this);
  }

  @Override
  public IDBResultSet getGeneratedKeys() throws SQLException
  {
    return new DBResultSet(getDelegate().getGeneratedKeys(), this);
  }

  @Override
  public IDBResultSet getResultSet() throws SQLException
  {
    return new DBResultSet(getDelegate().getResultSet(), this);
  }

  @Override
  public IDBResultSet executeQuery() throws SQLException
  {
    return new DBResultSet(getDelegate().executeQuery(), this);
  }

  @Override
  @Deprecated
  public ResultSet executeQuery(String sql) throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setString(int parameterIndex, String value) throws SQLException
  {
    value = getConnection().convertString(this, parameterIndex, value);
    super.setString(parameterIndex, value);
  }

  public boolean isImmediateBatchExecution()
  {
    return immediateBatchExecution;
  }

  public void setImmediateBatchExecution(boolean immediateBatchExecution)
  {
    this.immediateBatchExecution = immediateBatchExecution;
  }

  @Override
  public void addBatch() throws SQLException
  {
    if (immediateBatchExecution)
    {
      ++addBatchCount;
      execute();
    }
    else
    {
      super.addBatch();
    }
  }

  @Override
  public int[] executeBatch() throws SQLException
  {
    if (immediateBatchExecution)
    {
      int[] results = new int[addBatchCount];
      Arrays.fill(results, 1);
      addBatchCount = 0;
      return results;
    }

    return super.executeBatch();
  }

  public String convertString(DBResultSet resultSet, int columnIndex, String value) throws SQLException
  {
    return getConnection().convertString(resultSet, columnIndex, value);
  }

  public String convertString(DBResultSet resultSet, String columnLabel, String value) throws SQLException
  {
    return getConnection().convertString(resultSet, columnLabel, value);
  }

  Object setSchemaAccessToken(Object schemaAccessToken)
  {
    Object oldSchemaAccessToken = this.schemaAccessToken;
    this.schemaAccessToken = schemaAccessToken;
    return oldSchemaAccessToken;
  }
}
