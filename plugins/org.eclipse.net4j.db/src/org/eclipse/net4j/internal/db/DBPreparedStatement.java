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

import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBResultSet;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBPreparedStatement implements IDBPreparedStatement
{
  private final DBConnection transaction;

  private final String sql;

  private final ReuseProbability reuseProbability;

  private final PreparedStatement delegate;

  private int touch;

  public DBPreparedStatement(DBConnection transaction, String sql, ReuseProbability reuseProbability,
      PreparedStatement delegate)
  {
    this.transaction = transaction;
    this.sql = sql;
    this.reuseProbability = reuseProbability;
    this.delegate = delegate;
  }

  public DBConnection getTransaction()
  {
    return transaction;
  }

  public String getSQL()
  {
    return sql;
  }

  public ReuseProbability getReuseProbability()
  {
    return reuseProbability;
  }

  public PreparedStatement getDelegate()
  {
    return delegate;
  }

  public void setTouch(int touch)
  {
    this.touch = touch;
  }

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
    return "PreparedStatement[sql=" + sql + ", probability=" + reuseProbability + ", touch=" + touch + "]";
  }

  public void close() throws SQLException
  {
    transaction.releasePreparedStatement(this);
  }

  public IDBResultSet getGeneratedKeys() throws SQLException
  {
    return new DBResultSet(delegate.getGeneratedKeys());
  }

  public IDBResultSet getResultSet() throws SQLException
  {
    return new DBResultSet(delegate.getResultSet());
  }

  public IDBResultSet executeQuery() throws SQLException
  {
    return new DBResultSet(delegate.executeQuery());
  }

  @Deprecated
  public ResultSet executeQuery(String sql) throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  public int executeUpdate(String sql) throws SQLException
  {
    return delegate.executeUpdate(sql);
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException
  {
    return delegate.isWrapperFor(iface);
  }

  public <T> T unwrap(Class<T> iface) throws SQLException
  {
    return delegate.unwrap(iface);
  }

  public int executeUpdate() throws SQLException
  {
    return delegate.executeUpdate();
  }

  public void setNull(int parameterIndex, int sqlType) throws SQLException
  {
    delegate.setNull(parameterIndex, sqlType);
  }

  public int getMaxFieldSize() throws SQLException
  {
    return delegate.getMaxFieldSize();
  }

  public void setBoolean(int parameterIndex, boolean x) throws SQLException
  {
    delegate.setBoolean(parameterIndex, x);
  }

  public void setMaxFieldSize(int max) throws SQLException
  {
    delegate.setMaxFieldSize(max);
  }

  public void setByte(int parameterIndex, byte x) throws SQLException
  {
    delegate.setByte(parameterIndex, x);
  }

  public void setShort(int parameterIndex, short x) throws SQLException
  {
    delegate.setShort(parameterIndex, x);
  }

  public int getMaxRows() throws SQLException
  {
    return delegate.getMaxRows();
  }

  public void setInt(int parameterIndex, int x) throws SQLException
  {
    delegate.setInt(parameterIndex, x);
  }

  public void setMaxRows(int max) throws SQLException
  {
    delegate.setMaxRows(max);
  }

  public void setLong(int parameterIndex, long x) throws SQLException
  {
    delegate.setLong(parameterIndex, x);
  }

  public void setEscapeProcessing(boolean enable) throws SQLException
  {
    delegate.setEscapeProcessing(enable);
  }

  public void setFloat(int parameterIndex, float x) throws SQLException
  {
    delegate.setFloat(parameterIndex, x);
  }

  public int getQueryTimeout() throws SQLException
  {
    return delegate.getQueryTimeout();
  }

  public void setDouble(int parameterIndex, double x) throws SQLException
  {
    delegate.setDouble(parameterIndex, x);
  }

  public void setQueryTimeout(int seconds) throws SQLException
  {
    delegate.setQueryTimeout(seconds);
  }

  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
  {
    delegate.setBigDecimal(parameterIndex, x);
  }

  public void cancel() throws SQLException
  {
    delegate.cancel();
  }

  public void setString(int parameterIndex, String x) throws SQLException
  {
    delegate.setString(parameterIndex, x);
  }

  public SQLWarning getWarnings() throws SQLException
  {
    return delegate.getWarnings();
  }

  public void setBytes(int parameterIndex, byte[] x) throws SQLException
  {
    delegate.setBytes(parameterIndex, x);
  }

  public void clearWarnings() throws SQLException
  {
    delegate.clearWarnings();
  }

  public void setDate(int parameterIndex, Date x) throws SQLException
  {
    delegate.setDate(parameterIndex, x);
  }

  public void setCursorName(String name) throws SQLException
  {
    delegate.setCursorName(name);
  }

  public void setTime(int parameterIndex, Time x) throws SQLException
  {
    delegate.setTime(parameterIndex, x);
  }

  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
  {
    delegate.setTimestamp(parameterIndex, x);
  }

  public boolean execute(String sql) throws SQLException
  {
    return delegate.execute(sql);
  }

  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
  {
    delegate.setAsciiStream(parameterIndex, x, length);
  }

  @Deprecated
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException
  {
    delegate.setUnicodeStream(parameterIndex, x, length);
  }

  public int getUpdateCount() throws SQLException
  {
    return delegate.getUpdateCount();
  }

  public boolean getMoreResults() throws SQLException
  {
    return delegate.getMoreResults();
  }

  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
  {
    delegate.setBinaryStream(parameterIndex, x, length);
  }

  public void setFetchDirection(int direction) throws SQLException
  {
    delegate.setFetchDirection(direction);
  }

  public void clearParameters() throws SQLException
  {
    delegate.clearParameters();
  }

  public int getFetchDirection() throws SQLException
  {
    return delegate.getFetchDirection();
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
  {
    delegate.setObject(parameterIndex, x, targetSqlType);
  }

  public void setFetchSize(int rows) throws SQLException
  {
    delegate.setFetchSize(rows);
  }

  public int getFetchSize() throws SQLException
  {
    return delegate.getFetchSize();
  }

  public void setObject(int parameterIndex, Object x) throws SQLException
  {
    delegate.setObject(parameterIndex, x);
  }

  public int getResultSetConcurrency() throws SQLException
  {
    return delegate.getResultSetConcurrency();
  }

  public int getResultSetType() throws SQLException
  {
    return delegate.getResultSetType();
  }

  public void addBatch(String sql) throws SQLException
  {
    delegate.addBatch(sql);
  }

  public void clearBatch() throws SQLException
  {
    delegate.clearBatch();
  }

  public boolean execute() throws SQLException
  {
    return delegate.execute();
  }

  public int[] executeBatch() throws SQLException
  {
    return delegate.executeBatch();
  }

  public void addBatch() throws SQLException
  {
    delegate.addBatch();
  }

  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException
  {
    delegate.setCharacterStream(parameterIndex, reader, length);
  }

  public void setRef(int parameterIndex, Ref x) throws SQLException
  {
    delegate.setRef(parameterIndex, x);
  }

  public Connection getConnection() throws SQLException
  {
    return delegate.getConnection();
  }

  public void setBlob(int parameterIndex, Blob x) throws SQLException
  {
    delegate.setBlob(parameterIndex, x);
  }

  public void setClob(int parameterIndex, Clob x) throws SQLException
  {
    delegate.setClob(parameterIndex, x);
  }

  public boolean getMoreResults(int current) throws SQLException
  {
    return delegate.getMoreResults(current);
  }

  public void setArray(int parameterIndex, Array x) throws SQLException
  {
    delegate.setArray(parameterIndex, x);
  }

  public ResultSetMetaData getMetaData() throws SQLException
  {
    return delegate.getMetaData();
  }

  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
  {
    delegate.setDate(parameterIndex, x, cal);
  }

  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
  {
    return delegate.executeUpdate(sql, autoGeneratedKeys);
  }

  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException
  {
    delegate.setTime(parameterIndex, x, cal);
  }

  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
  {
    return delegate.executeUpdate(sql, columnIndexes);
  }

  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
  {
    delegate.setTimestamp(parameterIndex, x, cal);
  }

  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
  {
    delegate.setNull(parameterIndex, sqlType, typeName);
  }

  public int executeUpdate(String sql, String[] columnNames) throws SQLException
  {
    return delegate.executeUpdate(sql, columnNames);
  }

  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
  {
    return delegate.execute(sql, autoGeneratedKeys);
  }

  public void setURL(int parameterIndex, URL x) throws SQLException
  {
    delegate.setURL(parameterIndex, x);
  }

  public ParameterMetaData getParameterMetaData() throws SQLException
  {
    return delegate.getParameterMetaData();
  }

  public void setRowId(int parameterIndex, RowId x) throws SQLException
  {
    delegate.setRowId(parameterIndex, x);
  }

  public boolean execute(String sql, int[] columnIndexes) throws SQLException
  {
    return delegate.execute(sql, columnIndexes);
  }

  public void setNString(int parameterIndex, String value) throws SQLException
  {
    delegate.setNString(parameterIndex, value);
  }

  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
  {
    delegate.setNCharacterStream(parameterIndex, value, length);
  }

  public boolean execute(String sql, String[] columnNames) throws SQLException
  {
    return delegate.execute(sql, columnNames);
  }

  public void setNClob(int parameterIndex, NClob value) throws SQLException
  {
    delegate.setNClob(parameterIndex, value);
  }

  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
  {
    delegate.setClob(parameterIndex, reader, length);
  }

  public int getResultSetHoldability() throws SQLException
  {
    return delegate.getResultSetHoldability();
  }

  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
  {
    delegate.setBlob(parameterIndex, inputStream, length);
  }

  public boolean isClosed() throws SQLException
  {
    return delegate.isClosed();
  }

  public void setPoolable(boolean poolable) throws SQLException
  {
    delegate.setPoolable(poolable);
  }

  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException
  {
    delegate.setNClob(parameterIndex, reader, length);
  }

  public boolean isPoolable() throws SQLException
  {
    return delegate.isPoolable();
  }

  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
  {
    delegate.setSQLXML(parameterIndex, xmlObject);
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException
  {
    delegate.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
  }

  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException
  {
    delegate.setAsciiStream(parameterIndex, x, length);
  }

  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException
  {
    delegate.setBinaryStream(parameterIndex, x, length);
  }

  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException
  {
    delegate.setCharacterStream(parameterIndex, reader, length);
  }

  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
  {
    delegate.setAsciiStream(parameterIndex, x);
  }

  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
  {
    delegate.setBinaryStream(parameterIndex, x);
  }

  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
  {
    delegate.setCharacterStream(parameterIndex, reader);
  }

  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
  {
    delegate.setNCharacterStream(parameterIndex, value);
  }

  public void setClob(int parameterIndex, Reader reader) throws SQLException
  {
    delegate.setClob(parameterIndex, reader);
  }

  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
  {
    delegate.setBlob(parameterIndex, inputStream);
  }

  public void setNClob(int parameterIndex, Reader reader) throws SQLException
  {
    delegate.setNClob(parameterIndex, reader);
  }

  /**
   * @author Eike Stepper
   */
  private final class DBResultSet implements IDBResultSet
  {
    private final ResultSet delegate;

    public DBResultSet(ResultSet delegate)
    {
      this.delegate = delegate;
    }

    public IDBPreparedStatement getStatement() throws SQLException
    {
      return DBPreparedStatement.this;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException
    {
      return delegate.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
      return delegate.isWrapperFor(iface);
    }

    public boolean next() throws SQLException
    {
      return delegate.next();
    }

    public void close() throws SQLException
    {
      delegate.close();
    }

    public boolean wasNull() throws SQLException
    {
      return delegate.wasNull();
    }

    public String getString(int columnIndex) throws SQLException
    {
      return delegate.getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) throws SQLException
    {
      return delegate.getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws SQLException
    {
      return delegate.getByte(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException
    {
      return delegate.getShort(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException
    {
      return delegate.getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException
    {
      return delegate.getLong(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException
    {
      return delegate.getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException
    {
      return delegate.getDouble(columnIndex);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
    {
      return delegate.getBigDecimal(columnIndex, scale);
    }

    public byte[] getBytes(int columnIndex) throws SQLException
    {
      return delegate.getBytes(columnIndex);
    }

    public Date getDate(int columnIndex) throws SQLException
    {
      return delegate.getDate(columnIndex);
    }

    public Time getTime(int columnIndex) throws SQLException
    {
      return delegate.getTime(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException
    {
      return delegate.getTimestamp(columnIndex);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException
    {
      return delegate.getAsciiStream(columnIndex);
    }

    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException
    {
      return delegate.getUnicodeStream(columnIndex);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException
    {
      return delegate.getBinaryStream(columnIndex);
    }

    public String getString(String columnLabel) throws SQLException
    {
      return delegate.getString(columnLabel);
    }

    public boolean getBoolean(String columnLabel) throws SQLException
    {
      return delegate.getBoolean(columnLabel);
    }

    public byte getByte(String columnLabel) throws SQLException
    {
      return delegate.getByte(columnLabel);
    }

    public short getShort(String columnLabel) throws SQLException
    {
      return delegate.getShort(columnLabel);
    }

    public int getInt(String columnLabel) throws SQLException
    {
      return delegate.getInt(columnLabel);
    }

    public long getLong(String columnLabel) throws SQLException
    {
      return delegate.getLong(columnLabel);
    }

    public float getFloat(String columnLabel) throws SQLException
    {
      return delegate.getFloat(columnLabel);
    }

    public double getDouble(String columnLabel) throws SQLException
    {
      return delegate.getDouble(columnLabel);
    }

    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException
    {
      return delegate.getBigDecimal(columnLabel, scale);
    }

    public byte[] getBytes(String columnLabel) throws SQLException
    {
      return delegate.getBytes(columnLabel);
    }

    public Date getDate(String columnLabel) throws SQLException
    {
      return delegate.getDate(columnLabel);
    }

    public Time getTime(String columnLabel) throws SQLException
    {
      return delegate.getTime(columnLabel);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException
    {
      return delegate.getTimestamp(columnLabel);
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException
    {
      return delegate.getAsciiStream(columnLabel);
    }

    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException
    {
      return delegate.getUnicodeStream(columnLabel);
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException
    {
      return delegate.getBinaryStream(columnLabel);
    }

    public SQLWarning getWarnings() throws SQLException
    {
      return delegate.getWarnings();
    }

    public void clearWarnings() throws SQLException
    {
      delegate.clearWarnings();
    }

    public String getCursorName() throws SQLException
    {
      return delegate.getCursorName();
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
      return delegate.getMetaData();
    }

    public Object getObject(int columnIndex) throws SQLException
    {
      return delegate.getObject(columnIndex);
    }

    public Object getObject(String columnLabel) throws SQLException
    {
      return delegate.getObject(columnLabel);
    }

    public int findColumn(String columnLabel) throws SQLException
    {
      return delegate.findColumn(columnLabel);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException
    {
      return delegate.getCharacterStream(columnIndex);
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException
    {
      return delegate.getCharacterStream(columnLabel);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException
    {
      return delegate.getBigDecimal(columnIndex);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException
    {
      return delegate.getBigDecimal(columnLabel);
    }

    public boolean isBeforeFirst() throws SQLException
    {
      return delegate.isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException
    {
      return delegate.isAfterLast();
    }

    public boolean isFirst() throws SQLException
    {
      return delegate.isFirst();
    }

    public boolean isLast() throws SQLException
    {
      return delegate.isLast();
    }

    public void beforeFirst() throws SQLException
    {
      delegate.beforeFirst();
    }

    public void afterLast() throws SQLException
    {
      delegate.afterLast();
    }

    public boolean first() throws SQLException
    {
      return delegate.first();
    }

    public boolean last() throws SQLException
    {
      return delegate.last();
    }

    public int getRow() throws SQLException
    {
      return delegate.getRow();
    }

    public boolean absolute(int row) throws SQLException
    {
      return delegate.absolute(row);
    }

    public boolean relative(int rows) throws SQLException
    {
      return delegate.relative(rows);
    }

    public boolean previous() throws SQLException
    {
      return delegate.previous();
    }

    public void setFetchDirection(int direction) throws SQLException
    {
      delegate.setFetchDirection(direction);
    }

    public int getFetchDirection() throws SQLException
    {
      return delegate.getFetchDirection();
    }

    public void setFetchSize(int rows) throws SQLException
    {
      delegate.setFetchSize(rows);
    }

    public int getFetchSize() throws SQLException
    {
      return delegate.getFetchSize();
    }

    public int getType() throws SQLException
    {
      return delegate.getType();
    }

    public int getConcurrency() throws SQLException
    {
      return delegate.getConcurrency();
    }

    public boolean rowUpdated() throws SQLException
    {
      return delegate.rowUpdated();
    }

    public boolean rowInserted() throws SQLException
    {
      return delegate.rowInserted();
    }

    public boolean rowDeleted() throws SQLException
    {
      return delegate.rowDeleted();
    }

    public void updateNull(int columnIndex) throws SQLException
    {
      delegate.updateNull(columnIndex);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException
    {
      delegate.updateBoolean(columnIndex, x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException
    {
      delegate.updateByte(columnIndex, x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException
    {
      delegate.updateShort(columnIndex, x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException
    {
      delegate.updateInt(columnIndex, x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException
    {
      delegate.updateLong(columnIndex, x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException
    {
      delegate.updateFloat(columnIndex, x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException
    {
      delegate.updateDouble(columnIndex, x);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
    {
      delegate.updateBigDecimal(columnIndex, x);
    }

    public void updateString(int columnIndex, String x) throws SQLException
    {
      delegate.updateString(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException
    {
      delegate.updateBytes(columnIndex, x);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException
    {
      delegate.updateDate(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException
    {
      delegate.updateTime(columnIndex, x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
    {
      delegate.updateTimestamp(columnIndex, x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
    {
      delegate.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
    {
      delegate.updateBinaryStream(columnIndex, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
    {
      delegate.updateCharacterStream(columnIndex, x, length);
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException
    {
      delegate.updateObject(columnIndex, x, scaleOrLength);
    }

    public void updateObject(int columnIndex, Object x) throws SQLException
    {
      delegate.updateObject(columnIndex, x);
    }

    public void updateNull(String columnLabel) throws SQLException
    {
      delegate.updateNull(columnLabel);
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException
    {
      delegate.updateBoolean(columnLabel, x);
    }

    public void updateByte(String columnLabel, byte x) throws SQLException
    {
      delegate.updateByte(columnLabel, x);
    }

    public void updateShort(String columnLabel, short x) throws SQLException
    {
      delegate.updateShort(columnLabel, x);
    }

    public void updateInt(String columnLabel, int x) throws SQLException
    {
      delegate.updateInt(columnLabel, x);
    }

    public void updateLong(String columnLabel, long x) throws SQLException
    {
      delegate.updateLong(columnLabel, x);
    }

    public void updateFloat(String columnLabel, float x) throws SQLException
    {
      delegate.updateFloat(columnLabel, x);
    }

    public void updateDouble(String columnLabel, double x) throws SQLException
    {
      delegate.updateDouble(columnLabel, x);
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException
    {
      delegate.updateBigDecimal(columnLabel, x);
    }

    public void updateString(String columnLabel, String x) throws SQLException
    {
      delegate.updateString(columnLabel, x);
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException
    {
      delegate.updateBytes(columnLabel, x);
    }

    public void updateDate(String columnLabel, Date x) throws SQLException
    {
      delegate.updateDate(columnLabel, x);
    }

    public void updateTime(String columnLabel, Time x) throws SQLException
    {
      delegate.updateTime(columnLabel, x);
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException
    {
      delegate.updateTimestamp(columnLabel, x);
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException
    {
      delegate.updateAsciiStream(columnLabel, x, length);
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException
    {
      delegate.updateBinaryStream(columnLabel, x, length);
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException
    {
      delegate.updateCharacterStream(columnLabel, reader, length);
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException
    {
      delegate.updateObject(columnLabel, x, scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x) throws SQLException
    {
      delegate.updateObject(columnLabel, x);
    }

    public void insertRow() throws SQLException
    {
      delegate.insertRow();
    }

    public void updateRow() throws SQLException
    {
      delegate.updateRow();
    }

    public void deleteRow() throws SQLException
    {
      delegate.deleteRow();
    }

    public void refreshRow() throws SQLException
    {
      delegate.refreshRow();
    }

    public void cancelRowUpdates() throws SQLException
    {
      delegate.cancelRowUpdates();
    }

    public void moveToInsertRow() throws SQLException
    {
      delegate.moveToInsertRow();
    }

    public void moveToCurrentRow() throws SQLException
    {
      delegate.moveToCurrentRow();
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException
    {
      return delegate.getObject(columnIndex, map);
    }

    public Ref getRef(int columnIndex) throws SQLException
    {
      return delegate.getRef(columnIndex);
    }

    public Blob getBlob(int columnIndex) throws SQLException
    {
      return delegate.getBlob(columnIndex);
    }

    public Clob getClob(int columnIndex) throws SQLException
    {
      return delegate.getClob(columnIndex);
    }

    public Array getArray(int columnIndex) throws SQLException
    {
      return delegate.getArray(columnIndex);
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException
    {
      return delegate.getObject(columnLabel, map);
    }

    public Ref getRef(String columnLabel) throws SQLException
    {
      return delegate.getRef(columnLabel);
    }

    public Blob getBlob(String columnLabel) throws SQLException
    {
      return delegate.getBlob(columnLabel);
    }

    public Clob getClob(String columnLabel) throws SQLException
    {
      return delegate.getClob(columnLabel);
    }

    public Array getArray(String columnLabel) throws SQLException
    {
      return delegate.getArray(columnLabel);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException
    {
      return delegate.getDate(columnIndex, cal);
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException
    {
      return delegate.getDate(columnLabel, cal);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException
    {
      return delegate.getTime(columnIndex, cal);
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException
    {
      return delegate.getTime(columnLabel, cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
    {
      return delegate.getTimestamp(columnIndex, cal);
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException
    {
      return delegate.getTimestamp(columnLabel, cal);
    }

    public URL getURL(int columnIndex) throws SQLException
    {
      return delegate.getURL(columnIndex);
    }

    public URL getURL(String columnLabel) throws SQLException
    {
      return delegate.getURL(columnLabel);
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException
    {
      delegate.updateRef(columnIndex, x);
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException
    {
      delegate.updateRef(columnLabel, x);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException
    {
      delegate.updateBlob(columnIndex, x);
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException
    {
      delegate.updateBlob(columnLabel, x);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException
    {
      delegate.updateClob(columnIndex, x);
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException
    {
      delegate.updateClob(columnLabel, x);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException
    {
      delegate.updateArray(columnIndex, x);
    }

    public void updateArray(String columnLabel, Array x) throws SQLException
    {
      delegate.updateArray(columnLabel, x);
    }

    public RowId getRowId(int columnIndex) throws SQLException
    {
      return delegate.getRowId(columnIndex);
    }

    public RowId getRowId(String columnLabel) throws SQLException
    {
      return delegate.getRowId(columnLabel);
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException
    {
      delegate.updateRowId(columnIndex, x);
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException
    {
      delegate.updateRowId(columnLabel, x);
    }

    public int getHoldability() throws SQLException
    {
      return delegate.getHoldability();
    }

    public boolean isClosed() throws SQLException
    {
      return delegate.isClosed();
    }

    public void updateNString(int columnIndex, String nString) throws SQLException
    {
      delegate.updateNString(columnIndex, nString);
    }

    public void updateNString(String columnLabel, String nString) throws SQLException
    {
      delegate.updateNString(columnLabel, nString);
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException
    {
      delegate.updateNClob(columnIndex, nClob);
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException
    {
      delegate.updateNClob(columnLabel, nClob);
    }

    public NClob getNClob(int columnIndex) throws SQLException
    {
      return delegate.getNClob(columnIndex);
    }

    public NClob getNClob(String columnLabel) throws SQLException
    {
      return delegate.getNClob(columnLabel);
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException
    {
      return delegate.getSQLXML(columnIndex);
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException
    {
      return delegate.getSQLXML(columnLabel);
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
    {
      delegate.updateSQLXML(columnIndex, xmlObject);
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
    {
      delegate.updateSQLXML(columnLabel, xmlObject);
    }

    public String getNString(int columnIndex) throws SQLException
    {
      return delegate.getNString(columnIndex);
    }

    public String getNString(String columnLabel) throws SQLException
    {
      return delegate.getNString(columnLabel);
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException
    {
      return delegate.getNCharacterStream(columnIndex);
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException
    {
      return delegate.getNCharacterStream(columnLabel);
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
    {
      delegate.updateNCharacterStream(columnIndex, x, length);
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
    {
      delegate.updateNCharacterStream(columnLabel, reader, length);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
    {
      delegate.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
    {
      delegate.updateBinaryStream(columnIndex, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
    {
      delegate.updateCharacterStream(columnIndex, x, length);
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
    {
      delegate.updateAsciiStream(columnLabel, x, length);
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
    {
      delegate.updateBinaryStream(columnLabel, x, length);
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
    {
      delegate.updateCharacterStream(columnLabel, reader, length);
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
    {
      delegate.updateBlob(columnIndex, inputStream, length);
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
    {
      delegate.updateBlob(columnLabel, inputStream, length);
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
    {
      delegate.updateClob(columnIndex, reader, length);
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
    {
      delegate.updateClob(columnLabel, reader, length);
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
    {
      delegate.updateNClob(columnIndex, reader, length);
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
    {
      delegate.updateNClob(columnLabel, reader, length);
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
    {
      delegate.updateNCharacterStream(columnIndex, x);
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
    {
      delegate.updateNCharacterStream(columnLabel, reader);
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
    {
      delegate.updateAsciiStream(columnIndex, x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
    {
      delegate.updateBinaryStream(columnIndex, x);
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
    {
      delegate.updateCharacterStream(columnIndex, x);
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
    {
      delegate.updateAsciiStream(columnLabel, x);
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
    {
      delegate.updateBinaryStream(columnLabel, x);
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
    {
      delegate.updateCharacterStream(columnLabel, reader);
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
    {
      delegate.updateBlob(columnIndex, inputStream);
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
    {
      delegate.updateBlob(columnLabel, inputStream);
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException
    {
      delegate.updateClob(columnIndex, reader);
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException
    {
      delegate.updateClob(columnLabel, reader);
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException
    {
      delegate.updateNClob(columnIndex, reader);
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException
    {
      delegate.updateNClob(columnLabel, reader);
    }

  }
}
