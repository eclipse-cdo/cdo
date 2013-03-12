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
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBNamedElement;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.DBDatabase;
import org.eclipse.net4j.internal.db.DataSourceConnectionProvider;
import org.eclipse.net4j.internal.db.bundle.OM;
import org.eclipse.net4j.internal.db.ddl.DBNamedElement;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A utility class with various static factory and convenience methods.
 *
 * @author Eike Stepper
 */
public final class DBUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SQL, DBUtil.class);

  private DBUtil()
  {
  }

  /**
   * For debugging purposes ONLY!
   *
   * @deprecated Should only be used when debugging.
   * @since 3.0
   */
  @Deprecated
  public static void sqlDump(Connection conn, String sql)
  {
    ResultSet rs = null;

    try
    {
      TRACER.format("Dumping output of {0}", sql); //$NON-NLS-1$
      rs = conn.createStatement().executeQuery(sql);
      int numCol = rs.getMetaData().getColumnCount();

      StringBuilder row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append(String.format("%10s | ", rs.getMetaData().getColumnLabel(c))); //$NON-NLS-1$
      }

      TRACER.trace(row.toString());

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+--"); //$NON-NLS-1$
      }

      TRACER.trace(row.toString());

      while (rs.next())
      {
        row = new StringBuilder();
        for (int c = 1; c <= numCol; c++)
        {
          row.append(String.format("%10s | ", rs.getString(c))); //$NON-NLS-1$
        }

        TRACER.trace(row.toString());
      }

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+-"); //$NON-NLS-1$
      }

      TRACER.trace(row.toString());
    }
    catch (SQLException ex)
    {
      // Do nothing
    }
    finally
    {
      close(rs);
    }
  }

  /**
   * For debugging purposes ONLY!
   *
   * @deprecated Should only be used when debugging.
   * @since 3.0
   */
  @Deprecated
  public static void sqlDump(IDBConnectionProvider connectionProvider, String sql)
  {
    Connection connection = connectionProvider.getConnection();

    try
    {
      sqlDump(connection, sql);
    }
    finally
    {
      close(connection);
    }
  }

  /**
   * @since 4.2
   */
  public static String dumpToString(IDBNamedElement namedElement)
  {
    return ((DBNamedElement)namedElement).dumpToString();
  }

  /**
   * @since 4.2
   */
  public static void dump(IDBNamedElement namedElement)
  {
    ((DBNamedElement)namedElement).dump();
  }

  /**
   * @since 4.2
   */
  public static void dump(IDBNamedElement namedElement, Writer writer) throws IOException
  {
    ((DBNamedElement)namedElement).dump(writer);
  }

  /**
   * @since 4.2
   */
  public static IDBDatabase openDatabase(IDBAdapter adapter, IDBConnectionProvider connectionProvider, String schemaName)
  {
    return new DBDatabase((DBAdapter)adapter, connectionProvider, schemaName);
  }

  // /**
  // * @since 4.2
  // */
  // public static <T> T updateSchema(IDBConnectionProvider connectionProvider, RunnableWithConnection<T> runnable)
  // {
  // return execute(connectionProvider, new RunnableWithConnection<T>()
  // {
  // public T run(Connection connection) throws SQLException
  // {
  // return null;
  // }
  // });
  // }
  //
  // /**
  // * @since 4.2
  // */
  // public static <T> T updateSchema(Connection connection, RunnableWithConnection<T> runnable)
  // {
  // DBConnection dbConnection = null;
  //
  // try
  // {
  // if (connection instanceof DBConnection)
  // {
  // dbConnection = (DBConnection)connection;
  // dbConnection.getDatabase().beginSchemaAccess(true);
  // }
  //
  // return runnable.run(connection);
  // }
  // catch (SQLException ex)
  // {
  // throw new DBException(ex);
  // }
  // finally
  // {
  // if (dbConnection != null)
  // {
  //
  // }
  // }
  // }

  public static IDBSchema createSchema(String name)
  {
    return new org.eclipse.net4j.internal.db.ddl.DBSchema(name);
  }

  /**
   * @since 4.2
   */
  public static IDBSchema readSchema(IDBAdapter adapter, Connection connection, String name)
  {
    IDBSchema schema = new org.eclipse.net4j.internal.db.ddl.DBSchema(name);
    readSchema(adapter, connection, schema);
    return schema;
  }

  /**
   * @since 4.2
   */
  public static void readSchema(IDBAdapter adapter, Connection connection, IDBSchema schema)
  {
    adapter.readSchema(connection, schema);
  }

  /**
   * @since 4.2
   */
  public static IDBSchema copySchema(IDBSchema source)
  {
    return new org.eclipse.net4j.internal.db.ddl.DBSchema(source);
  }

  public static DataSource createDataSource(Map<Object, Object> properties)
  {
    return createDataSource(properties, null);
  }

  public static DataSource createDataSource(Map<Object, Object> properties, String namespace)
  {
    return createDataSource(properties, namespace, "class"); //$NON-NLS-1$
  }

  public static DataSource createDataSource(Map<Object, Object> properties, String namespace, String driverClassKey)
  {
    try
    {
      return (DataSource)ReflectUtil.instantiate(properties, namespace, driverClassKey, OM.class.getClassLoader());
    }
    catch (Exception ex)
    {
      throw new DBException(ex);
    }
  }

  public static IDBConnectionProvider createConnectionProvider(DataSource dataSource)
  {
    return new DataSourceConnectionProvider(dataSource);
  }

  /**
   * Retrieves an {@link IDBAdapter adapter} from the {@link IDBAdapter#REGISTRY adapter registry}.
   * <p>
   * If Eclipse is running adapters are automatically created from descriptors that are contributed to the extension point <code>org.eclipse.net4j.db.dbAdapters</code>.
   * <p>
   * In standalone scenarios the needed adapter instances must be registered with the {@link IDBAdapter#REGISTRY adapter registry} manually.
   */
  public static IDBAdapter getDBAdapter(String adapterName)
  {
    return IDBAdapter.REGISTRY.get(adapterName);
  }

  public static Exception close(ResultSet resultSet)
  {
    if (resultSet != null)
    {
      try
      {
        Statement statement = resultSet.getStatement();
        if (statement != null && statement.getMaxRows() != 0)
        {
          statement.setMaxRows(0);
        }
      }
      catch (Exception ignore)
      {
      }

      try
      {
        resultSet.close();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        return ex;
      }
    }

    return null;
  }

  public static Exception close(Statement statement)
  {
    if (statement != null)
    {
      try
      {
        statement.close();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        return ex;
      }
    }

    return null;
  }

  public static Exception close(Connection connection)
  {
    if (connection != null)
    {
      try
      {
        // Only for connections with autoCommit = false, we try a rollback
        // first to clear any open transactions.
        if (!connection.getAutoCommit())
        {
          rollback(connection);
        }

        connection.close();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        return ex;
      }
    }

    return null;
  }

  private static void rollback(Connection connection)
  {
    try
    {
      connection.rollback();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  /**
   * @since 3.0
   * @deprecated As of 4.2 use {@link #getAllSchemaNames(Connection)}.
   */
  @Deprecated
  public static List<String> getAllSchemaTableNames(Connection connection)
  {
    return getAllSchemaNames(connection);
  }

  /**
   * @since 3.0
   * @deprecated As of 4.2 use {@link #getAllSchemaNames(DatabaseMetaData)}.
   */
  @Deprecated
  public static List<String> getAllSchemaTableNames(DatabaseMetaData metaData)
  {
    return getAllSchemaNames(metaData);
  }

  /**
   * @since 4.2
   */
  public static List<String> getAllSchemaNames(Connection connection)
  {
    try
    {
      DatabaseMetaData metaData = connection.getMetaData();
      return getAllSchemaNames(metaData);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * @since 4.2
   */
  public static List<String> getAllSchemaNames(DatabaseMetaData metaData)
  {
    ResultSet schemas = null;

    try
    {
      List<String> names = new ArrayList<String>();
      schemas = metaData.getSchemas();
      while (schemas.next())
      {
        String name = schemas.getString(1);
        names.add(name);
      }

      return names;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(schemas);
    }
  }

  public static List<String> getAllTableNames(Connection connection, String dbName)
  {
    ResultSet tables = null;

    try
    {
      List<String> names = new ArrayList<String>();
      DatabaseMetaData metaData = connection.getMetaData();
      if (dbName != null)
      {
        dbName = dbName.toUpperCase();
        List<String> schemaNames = getAllSchemaNames(metaData);
        if (!schemaNames.contains(dbName))
        {
          dbName = null;
        }
      }

      tables = metaData.getTables(null, dbName, null, new String[] { "TABLE" }); //$NON-NLS-1$
      while (tables.next())
      {
        String name = tables.getString(3);
        names.add(name);
      }

      return names;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(tables);
    }
  }

  /**
   * @since 4.0
   */
  public static List<Exception> dropAllTables(Connection connection, String dbName)
  {
    List<Exception> exceptions = new ArrayList<Exception>();
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      for (String tableName : DBUtil.getAllTableNames(connection, dbName))
      {
        String sql = "DROP TABLE " + tableName; //$NON-NLS-1$
        trace(sql);

        try
        {
          statement.execute(sql);
        }
        catch (SQLException ex)
        {
          exceptions.add(ex);
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(statement);
    }

    return exceptions;
  }

  /**
   * @since 4.2
   */
  public static int asInt(Object value)
  {
    if (value instanceof Number)
    {
      return ((Number)value).intValue();
    }

    return 0;
  }

  /**
   * @since 4.2
   */
  public static long asLong(Object value)
  {
    if (value instanceof Number)
    {
      return ((Number)value).longValue();
    }

    return 0L;
  }

  /**
   * @since 3.0
   */
  public static int selectMinimumInt(Connection connection, IDBField field, String... where) throws DBException
  {
    Number number = getFunctionResult(connection, field, "MIN", where); //$NON-NLS-1$
    return asInt(number);
  }

  /**
   * @since 3.0
   */
  public static long selectMinimumLong(Connection connection, IDBField field, String... where) throws DBException
  {
    Number number = getFunctionResult(connection, field, "MIN", where); //$NON-NLS-1$
    return asLong(number);
  }

  /**
   * @since 3.0
   */
  public static int selectMaximumInt(Connection connection, IDBField field, String... where) throws DBException
  {
    Number number = getFunctionResult(connection, field, "MAX", where); //$NON-NLS-1$
    return asInt(number);
  }

  /**
   * @since 3.0
   */
  public static long selectMaximumLong(Connection connection, IDBField field, String... where) throws DBException
  {
    Number number = getFunctionResult(connection, field, "MAX", where); //$NON-NLS-1$
    return asLong(number);
  }

  private static Number getFunctionResult(Connection connection, IDBField field, String function, String... where)
      throws DBException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(function);
    builder.append("("); //$NON-NLS-1$
    builder.append(field);
    builder.append(") FROM "); //$NON-NLS-1$
    builder.append(field.getTable());

    for (int i = 0; i < where.length; i++)
    {
      if (i == 0)
      {
        builder.append(" WHERE "); //$NON-NLS-1$
      }
      else
      {
        builder.append(" AND "); //$NON-NLS-1$
      }

      builder.append("("); //$NON-NLS-1$
      builder.append(where[i]);
      builder.append(")"); //$NON-NLS-1$
    }

    String sql = trace(builder.toString());
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.createStatement();

      try
      {
        resultSet = statement.executeQuery(sql);
        if (!resultSet.next())
        {
          return null;
        }

        return (Number)resultSet.getObject(1);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        close(resultSet);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(statement);
    }
  }

  /**
   * @since 4.2
   */
  public static <T> T execute(IDBConnectionProvider connectionProvider, RunnableWithConnection<T> runnable)
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      return runnable.run(connection);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(connection);
    }
  }

  /**
   * @since 4.2
   */
  public static void execute(Connection connection, CharSequence sql)
  {
    String string = sql.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(string);
    }

    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      statement.execute(string);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(statement);
    }
  }

  /**
   * @since 4.1
   */
  public static void executeBatch(PreparedStatement stmt, int counter)
  {
    executeBatch(stmt, counter, true);
  }

  /**
   * @since 4.1
   */
  public static void executeBatch(PreparedStatement stmt, int counter, boolean checkExactlyOne)
  {
    try
    {
      int[] results = stmt.executeBatch();
      if (results.length != counter)
      {
        throw new DBException("Statement has " + results.length + " results (expected: " + counter + ")");
      }

      for (int i = 0; i < results.length; i++)
      {
        int result = results[i];
        if (result < 0 && result != Statement.SUCCESS_NO_INFO)
        {
          throw new DBException("Result " + i + " is not successful: " + result);
        }

        if (checkExactlyOne && result != 1)
        {
          throw new DBException("Result " + i + " did not affect exactly one row: " + result);
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public static int update(Connection connection, String sql)
  {
    trace(sql);
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      return statement.executeUpdate(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(statement);
    }
  }

  /**
   * Execute update on the given prepared statement and handle common cases of return values.
   *
   * @param stmt
   *          the prepared statement
   * @param exactlyOne
   *          if <code>true</code>, the update count is checked to be <code>1</code>. Else the update result is only
   *          checked so that the update was successful (i.e. result code != Statement.EXECUTE_FAILED).
   * @return the update count / execution result as returned by {@link PreparedStatement#executeUpdate()}. Can be used
   *         by the caller to perform more advanced checks.
   * @throws SQLException
   *           if {@link PreparedStatement#executeUpdate()} throws it.
   * @throws IllegalStateException
   *           if the check indicated by <code>excatlyOne</code> indicates an error.
   * @since 4.0
   */
  public static int update(PreparedStatement stmt, boolean exactlyOne) throws SQLException
  {
    if (DBUtil.isTracerEnabled())
    {
      trace(stmt.toString());
    }

    int result = stmt.executeUpdate();

    // basic check of update result
    if (exactlyOne && result != 1)
    {
      throw new IllegalStateException(stmt.toString() + " returned Update count " + result + " (expected: 1)"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (result == Statement.EXECUTE_FAILED)
    {
      throw new IllegalStateException(stmt.toString() + " returned EXECUTE_FAILED"); //$NON-NLS-1$
    }

    return result;
  }

  /**
   * @since 4.1
   */
  public static int clearTable(Connection connection, IDBTable table)
  {
    return clearTable(connection, table.getName());
  }

  /**
   * @since 4.1
   */
  public static int clearTable(Connection connection, String tableName)
  {
    String sql = "DELETE FROM " + tableName;
    return update(connection, sql);
  }

  public static int select(Connection connection, IDBRowHandler rowHandler, String where, IDBField... fields)
      throws DBException
  {
    IDBTable table = fields[0].getTable();
    for (int i = 1; i < fields.length; i++)
    {
      if (fields[i].getTable() != table)
      {
        throw new IllegalArgumentException("Multiple tables not allowed: " + Arrays.asList(fields)); //$NON-NLS-1$
      }
    }

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    for (int i = 0; i < fields.length; i++)
    {
      if (i > 0)
      {
        builder.append(", "); //$NON-NLS-1$
      }

      builder.append(fields[i]);
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    if (where != null)
    {
      builder.append(" WHERE "); //$NON-NLS-1$
      builder.append(where);
    }

    String sql = trace(builder.toString());
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.createStatement();

      try
      {
        int rows = 0;
        boolean proceed = true;
        Object[] values = new Object[fields.length];
        resultSet = statement.executeQuery(sql);
        while (proceed && resultSet.next())
        {
          for (int i = 0; i < fields.length; i++)
          {
            values[i] = resultSet.getObject(i + 1);
            if (values[i] instanceof Blob)
            {
              Blob blob = (Blob)values[i];
              long length = blob.length();
              if (length > Integer.MAX_VALUE)
              {
                throw new IllegalStateException("byte[] too long: " + length); //$NON-NLS-1$
              }

              values[i] = blob.getBytes(1, (int)length);
            }
            else if (values[i] instanceof Clob)
            {
              Clob clob = (Clob)values[i];
              long length = clob.length();
              if (length > Integer.MAX_VALUE)
              {
                throw new IllegalStateException("String too long: " + length); //$NON-NLS-1$
              }

              values[i] = clob.getSubString(1, (int)length);
            }
          }

          proceed = rowHandler.handle(rows++, values);
        }

        return rows;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        close(resultSet);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(statement);
    }
  }

  public static int select(Connection connection, IDBRowHandler rowHandler, IDBField... fields) throws DBException
  {
    return select(connection, rowHandler, null, fields);
  }

  public static Object[] select(Connection connection, String where, IDBField... fields) throws DBException
  {
    final Object[][] result = new Object[1][];
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        result[0] = values;
        return false;
      }
    };

    select(connection, rowHandler, where, fields);
    return result[0];
  }

  /**
   * Returns the number of rows contained in the given result set.
   * <p>
   * The {@link ResultSet#getStatement() statement} of the result set must have been created with
   * {@link ResultSet#TYPE_SCROLL_INSENSITIVE TYPE_SCROLL_INSENSITIVE}.
   *
   * @since 4.0
   */
  public static int getRowCount(ResultSet resultSet) throws DBException
  {
    reset(resultSet);

    try
    {
      resultSet.last();
      return resultSet.getRow();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      reset(resultSet);
    }
  }

  private static void reset(ResultSet resultSet) throws DBException
  {
    try
    {
      resultSet.beforeFirst();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * @since 3.0
   */
  public static void serializeTable(ExtendedDataOutput out, Connection connection, IDBTable table, String tableAlias,
      String sqlSuffix) throws DBException, IOException
  {
    serializeTable(out, connection, table, tableAlias, sqlSuffix, null);
  }

  /**
   * @since 4.1
   */
  public static void serializeTable(ExtendedDataOutput out, Connection connection, IDBTable table, String tableAlias,
      String sqlSuffix, SerializeRowHandler handler) throws DBException, IOException
  {
    IDBField[] fields = table.getFields();

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    for (int i = 0; i < fields.length; i++)
    {
      if (i > 0)
      {
        builder.append(", "); //$NON-NLS-1$
      }

      if (tableAlias != null)
      {
        builder.append(tableAlias);
        builder.append("."); //$NON-NLS-1$
      }

      builder.append(fields[i]);
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    if (tableAlias != null)
    {
      builder.append(" "); //$NON-NLS-1$
      builder.append(tableAlias);
    }

    if (sqlSuffix != null)
    {
      builder.append(sqlSuffix);
    }

    String sql = trace(builder.toString());
    Statement statement = null;
    ResultSet resultSet = null;
    boolean successful = false;

    try
    {
      statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      try
      {
        resultSet = statement.executeQuery(sql);

        // Write resultSet size for progress monitoring
        int size = getRowCount(resultSet);
        out.writeInt(size);
        if (size == 0)
        {
          return;
        }

        Object[] values = handler != null ? new Object[fields.length] : null;

        while (resultSet.next())
        {
          for (int i = 0; i < fields.length; i++)
          {
            IDBField field = fields[i];
            DBType type = field.getType();
            boolean canBeNull = !field.isNotNull();

            Object value = type.writeValueWithResult(out, resultSet, i + 1, canBeNull);
            if (values != null)
            {
              values[i] = value;
            }
          }

          if (handler != null)
          {
            handler.handleRow(out, connection, fields, values);
          }
        }

        successful = true;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        close(resultSet);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      try
      {
        if (handler != null)
        {
          handler.done(successful);
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        close(statement);
      }
    }
  }

  /**
   * @since 4.0
   */
  public static void deserializeTable(ExtendedDataInput in, Connection connection, IDBTable table, OMMonitor monitor)
      throws IOException
  {
    deserializeTable(in, connection, table, monitor, null);
  }

  /**
   * @since 4.1
   */
  public static void deserializeTable(ExtendedDataInput in, Connection connection, IDBTable table, OMMonitor monitor,
      DeserializeRowHandler handler) throws IOException
  {
    int size = in.readInt();
    if (size == 0)
    {
      return;
    }

    IDBField[] fields = table.getFields();

    StringBuilder builder = new StringBuilder();
    StringBuilder params = new StringBuilder();

    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(table);
    builder.append("("); //$NON-NLS-1$

    for (int i = 0; i < fields.length; i++)
    {
      if (i > 0)
      {
        builder.append(", "); //$NON-NLS-1$
        params.append(", "); //$NON-NLS-1$
      }

      builder.append(fields[i]);
      params.append("?"); //$NON-NLS-1$
    }

    builder.append(") VALUES ("); //$NON-NLS-1$
    builder.append(params.toString());
    builder.append(")"); //$NON-NLS-1$

    String sql = trace(builder.toString());
    PreparedStatement statement = null;
    boolean successful = false;

    monitor.begin(1 + 2 * size);

    try
    {
      statement = connection.prepareStatement(sql);
      monitor.worked();

      Object[] values = handler != null ? new Object[fields.length] : null;

      for (int row = 0; row < size; row++)
      {
        for (int i = 0; i < fields.length; i++)
        {
          IDBField field = fields[i];
          DBType type = field.getType();
          boolean canBeNull = !field.isNotNull();
          Object value = type.readValueWithResult(in, statement, i + 1, canBeNull);
          if (values != null)
          {
            values[i] = value;
          }
        }

        statement.addBatch();
        if (handler != null)
        {
          handler.handleRow(in, connection, fields, values);
        }

        monitor.worked();
      }

      Async async = monitor.forkAsync(size);

      try
      {
        statement.executeBatch();
      }
      finally
      {
        async.stop();
      }

      successful = true;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      try
      {
        if (handler != null)
        {
          handler.done(successful);
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        close(statement);
        monitor.done();
      }
    }
  }

  /**
   * @since 3.0
   */
  public static String trace(String sql)
  {
    if (isTracerEnabled())
    {
      TRACER.trace(sql);
    }

    return sql;
  }

  /**
   * @since 4.2
   */
  public static boolean isTracerEnabled()
  {
    return TRACER.isEnabled();
  }

  /**
   * @since 4.2
   * @author Eike Stepper
   */
  public interface RunnableWithConnection<T>
  {
    public T run(Connection connection) throws SQLException;
  }

  /**
   * Call-back interface with a {@link #done(boolean) method} that is called <i>after</i>
   * a number of table rows have been handled by one of the subtypes of this interface.
   *
   * @since 4.1
   * @author Eike Stepper
   */
  public interface RowHandler
  {
    public void done(boolean successful) throws SQLException, IOException;
  }

  /**
   * A {@link RowHandler row handler} with a {@link #handleRow(ExtendedDataOutput, Connection, IDBField[], Object[]) method}
   * that is called once per row serialized within {@link DBUtil#serializeTable(ExtendedDataOutput, Connection, IDBTable, String, String, SerializeRowHandler) DBUtil.serializeTable()}.
   *
   * @since 4.1
   * @author Eike Stepper
   */
  public interface SerializeRowHandler extends RowHandler
  {
    public void handleRow(ExtendedDataOutput out, Connection connection, IDBField[] fields, Object[] values)
        throws SQLException, IOException;
  }

  /**
   * A {@link RowHandler row handler} with a {@link #handleRow(ExtendedDataInput, Connection, IDBField[], Object[]) method}
   * that is called once per row deserialized within {@link DBUtil#deserializeTable(ExtendedDataInput, Connection, IDBTable, OMMonitor, DeserializeRowHandler) DBUtil.deserializeTable()}.
   *
   * @since 4.1
   * @author Eike Stepper
   */
  public interface DeserializeRowHandler extends RowHandler
  {
    public void handleRow(ExtendedDataInput in, Connection connection, IDBField[] fields, Object[] values)
        throws SQLException, IOException;
  }
}
