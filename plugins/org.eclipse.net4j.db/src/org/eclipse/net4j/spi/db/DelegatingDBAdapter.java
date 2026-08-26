/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.spi.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBAdapterID;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.internal.db.DBAdapterID;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

/**
 * Keeps extension metadata authoritative without requiring the created adapter to extend DBAdapter.
 *
 * @author Eike Stepper
 * @since 4.14
 */
public class DelegatingDBAdapter implements IDBAdapter
{
  private final IDBAdapter delegate;

  private final String name;

  private final String version;

  public DelegatingDBAdapter(IDBAdapter delegate)
  {
    this(delegate, delegate.getName(), delegate.getVersion());
  }

  public DelegatingDBAdapter(IDBAdapter delegate, String name, String version)
  {
    this.delegate = delegate;
    this.name = name;
    this.version = version;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getVersion()
  {
    return version;
  }

  @Override
  public int hashCode()
  {
    return DBAdapterID.copy(this).hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof IDBAdapterID && DBAdapterID.copy(this).equals(DBAdapterID.copy((IDBAdapterID)obj));
  }

  @Override
  public String toString()
  {
    return getName() + "-" + getVersion(); //$NON-NLS-1$
  }

  @Override
  public IDBConnectionProvider createConnectionProvider(DataSource dataSource)
  {
    return delegate.createConnectionProvider(dataSource);
  }

  @Override
  public Connection modifyConnection(Connection connection)
  {
    return delegate.modifyConnection(connection);
  }

  @Override
  public boolean isCaseSensitive()
  {
    return delegate.isCaseSensitive();
  }

  @Override
  public String getDefaultSchemaName(Connection connection)
  {
    return delegate.getDefaultSchemaName(connection);
  }

  @Override
  public void createSchema(Connection connection, String schemaName)
  {
    delegate.createSchema(connection, schemaName);
  }

  @Override
  public IDBSchemaTransaction openSchemaTransaction(IDBDatabase database, IDBConnection currentConnection)
  {
    return delegate.openSchemaTransaction(database, currentConnection);
  }

  @Override
  public IDBSchema readSchema(Connection connection, String name)
  {
    return delegate.readSchema(connection, name);
  }

  @Override
  public void readSchema(Connection connection, IDBSchema schema)
  {
    delegate.readSchema(connection, schema);
  }

  @Override
  public void updateSchema(Connection connection, IDBSchema schema, IDBSchemaDelta delta) throws DBException
  {
    delegate.updateSchema(connection, schema, delta);
  }

  @Override
  public Set<IDBTable> createTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException
  {
    return delegate.createTables(tables, connection);
  }

  @Override
  public boolean createTable(IDBTable table, Statement statement) throws DBException
  {
    return delegate.createTable(table, statement);
  }

  @Override
  public Collection<IDBTable> dropTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException
  {
    return delegate.dropTables(tables, connection);
  }

  @Override
  public boolean dropTable(IDBTable table, Statement statement)
  {
    return delegate.dropTable(table, statement);
  }

  @Override
  public String[] getReservedWords()
  {
    return delegate.getReservedWords();
  }

  @Override
  public boolean isReservedWord(String word)
  {
    return delegate.isReservedWord(word);
  }

  @Override
  public int getMaxTableNameLength()
  {
    return delegate.getMaxTableNameLength();
  }

  @Override
  public int getMaxFieldNameLength()
  {
    return delegate.getMaxFieldNameLength();
  }

  @Override
  public int getFieldLength(DBType type)
  {
    return delegate.getFieldLength(type);
  }

  @Override
  public boolean isTypeIndexable(DBType type)
  {
    return delegate.isTypeIndexable(type);
  }

  @Override
  public DBType adaptType(DBType type)
  {
    return delegate.adaptType(type);
  }

  @Override
  public int getJDBCTypeForNull(DBType type)
  {
    return delegate.getJDBCTypeForNull(type);
  }

  @Override
  public boolean isValidFirstChar(char ch)
  {
    return delegate.isValidFirstChar(ch);
  }

  @Override
  public boolean isDuplicateKeyException(SQLException ex)
  {
    return delegate.isDuplicateKeyException(ex);
  }

  @Override
  public boolean isDuplicateKeyTransactionAbort()
  {
    return delegate.isDuplicateKeyTransactionAbort();
  }

  @Override
  public boolean isTableNotFoundException(SQLException ex)
  {
    return delegate.isTableNotFoundException(ex);
  }

  @Override
  public boolean isColumnNotFoundException(SQLException ex)
  {
    return delegate.isColumnNotFoundException(ex);
  }

  @Override
  public String sqlRenameField(IDBField field, String oldName)
  {
    return delegate.sqlRenameField(field, oldName);
  }

  @Override
  public String sqlModifyField(IDBField field)
  {
    return delegate.sqlModifyField(field);
  }

  @Override
  public String sqlCharIndex(Object substring, Object string)
  {
    return delegate.sqlCharIndex(substring, string);
  }

  @Override
  public String sqlSubstring(Object string, Object startIndex, Object length)
  {
    return delegate.sqlSubstring(string, startIndex, length);
  }

  @Override
  public String sqlSubstring(Object string, Object startIndex)
  {
    return delegate.sqlSubstring(string, startIndex);
  }

  @Override
  public String sqlConcat(Object... strings)
  {
    return delegate.sqlConcat(strings);
  }

  @Override
  public String convertString(PreparedStatement preparedStatement, int parameterIndex, String value)
  {
    return delegate.convertString(preparedStatement, parameterIndex, value);
  }

  @Override
  public String convertString(ResultSet resultSet, int columnIndex, String value)
  {
    return delegate.convertString(resultSet, columnIndex, value);
  }

  @Override
  public String convertString(ResultSet resultSet, String columnLabel, String value)
  {
    return delegate.convertString(resultSet, columnLabel, value);
  }

  @Override
  public String[] getSQL92ReservedWords()
  {
    return delegate.getSQL92ReservedWords();
  }

  @Override
  public void appendFieldNames(Appendable appendable, IDBTable table)
  {
    delegate.appendFieldNames(appendable, table);
  }

  @Override
  public String format(PreparedStatement stmt)
  {
    return delegate.format(stmt);
  }

  @Override
  public String format(ResultSet resultSet)
  {
    return delegate.format(resultSet);
  }

  @Override
  public Object convertToSQL(Object value)
  {
    return delegate.convertToSQL(value);
  }

  @Override
  @Deprecated
  public Driver getJDBCDriver()
  {
    return delegate.getJDBCDriver();
  }

  @Override
  @Deprecated
  public DataSource createJDBCDataSource()
  {
    return delegate.createJDBCDataSource();
  }
}
