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
package org.eclipse.net4j.spi.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.internal.db.ddl.DBSchemaElement;
import org.eclipse.net4j.internal.db.ddl.DBTable;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;

import javax.sql.DataSource;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A useful base class for implementing custom {@link IDBSchema DB schemas}.
 *
 * @author Eike Stepper
 */
public class DBSchema extends DBSchemaElement implements IDBSchema
{
  /**
   * @since 4.2
   */
  public static final IDBTable[] NO_TABLES = {};

  private String name;

  private Map<String, DBTable> tables = new HashMap<String, DBTable>();

  private boolean locked;

  public DBSchema(String name)
  {
    this.name = name;
  }

  /**
   * @since 4.2
   */
  public DBSchema(String name, Connection connection)
  {
    this(name);
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      DatabaseMetaData metaData = connection.getMetaData();

      ResultSet tables = metaData.getTables(null, name, null, new String[] { "TABLE" });
      while (tables.next())
      {
        String tableName = tables.getString(3);

        IDBTable table = addTable(tableName);
        readFields(table, statement);

        readIndices(table, metaData, name);
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
  }

  /**
   * @since 4.2
   */
  public DBSchema(IDBSchema source)
  {
    name = source.getName();

    for (IDBTable sourceTable : source.getTables())
    {
      DBTable table = (DBTable)addTable(sourceTable.getName());

      for (IDBField sourceField : sourceTable.getFields())
      {
        table.addField(sourceField.getName(), sourceField.getType(), sourceField.getScale(), sourceField.isNotNull());
      }

      for (IDBIndex sourceIndex : sourceTable.getIndices())
      {
        table.addIndex(sourceIndex.getType(), sourceIndex.getFields());
      }
    }
  }

  public String getFullName()
  {
    return name;
  }

  public IDBSchema getSchema()
  {
    return this;
  }

  public String getName()
  {
    return name;
  }

  /**
   * @since 2.0
   */
  public IDBTable addTable(String name) throws DBException
  {
    assertUnlocked();
    if (tables.containsKey(name))
    {
      throw new DBException("DBTable exists: " + name); //$NON-NLS-1$
    }

    DBTable table = new DBTable(this, name);
    tables.put(name, table);
    return table;
  }

  /**
   * @since 4.0
   */
  public IDBTable removeTable(String name)
  {
    assertUnlocked();
    return tables.remove(name);
  }

  /**
   * @since 2.0
   */
  public IDBTable getTable(String name)
  {
    return tables.get(name);
  }

  /**
   * @since 2.0
   */
  public IDBTable[] getTables()
  {
    return tables.values().toArray(new DBTable[tables.size()]);
  }

  /**
   * @since 4.2
   */
  public boolean isEmpty()
  {
    return tables.isEmpty();
  }

  /**
   * @since 4.2
   */
  public IDBTable[] getElements()
  {
    return getTables();
  }

  /**
   * @since 4.2
   */
  public void remove()
  {
    assertUnlocked();
    tables.clear();
  }

  public boolean isLocked()
  {
    return locked;
  }

  public boolean lock()
  {
    return locked = true;
  }

  /**
   * @since 4.2
   */
  public boolean unlock()
  {
    return locked = false;
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    return dbAdapter.createTables(tables.values(), connection);
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    return create(dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      if (connection == null)
      {
        throw new DBException("No connection available from " + connectionProvider); //$NON-NLS-1$
      }

      return create(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public void drop(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    dbAdapter.dropTables(tables.values(), connection);
  }

  public void drop(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    drop(dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  public void drop(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      drop(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public void export(Connection connection, PrintStream out) throws DBException
  {
    for (IDBTable table : getTables())
    {
      export(table, connection, out);
    }
  }

  private void export(final IDBTable table, Connection connection, final PrintStream out)
  {
    if (DBUtil.select(connection, new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        if (row == 0)
        {
          String tableName = table.getName();
          out.println(tableName);
          for (int i = 0; i < tableName.length(); i++)
          {
            out.print("="); //$NON-NLS-1$
          }

          out.println();
        }

        out.println(Arrays.asList(values));
        return true;
      }
    }, table.getFields()) > 0)

    {
      out.println();
    }
  }

  public void export(DataSource dataSource, PrintStream out) throws DBException
  {
    export(DBUtil.createConnectionProvider(dataSource), out);
  }

  public void export(IDBConnectionProvider connectionProvider, PrintStream out) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      export(connection, out);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * @since 4.2
   */
  public IDBSchemaDelta compare(IDBSchema oldSchema)
  {
    return new DBSchemaDelta(this, oldSchema);
  }

  /**
   * @since 4.2
   */
  public String createIndexName(IDBTable table, IDBIndex.Type type, IDBField[] fields, int position)
  {
    return "idx_" + table.getName() + "_" + position;
  }

  public void assertUnlocked() throws DBException
  {
    if (locked)
    {
      throw new DBException("Schema locked: " + name); //$NON-NLS-1$
    }
  }

  private void readFields(IDBTable table, Statement statement) throws SQLException
  {
    ResultSet resultSet = null;

    try
    {
      resultSet = statement.executeQuery("SELECT * FROM " + table);
      ResultSetMetaData metaData = resultSet.getMetaData();

      for (int i = 0; i < metaData.getColumnCount(); i++)
      {
        int column = i + 1;

        String name = metaData.getColumnName(column);
        DBType type = DBType.getTypeByCode(metaData.getColumnType(column));
        int precision = metaData.getPrecision(column);
        int scale = metaData.getScale(column);
        boolean notNull = metaData.isNullable(column) == ResultSetMetaData.columnNoNulls;

        table.addField(name, type, precision, scale, notNull);
      }
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  private void readIndices(IDBTable table, DatabaseMetaData metaData, String schemaName) throws SQLException
  {
    String tableName = table.getName();

    ResultSet primaryKeys = metaData.getPrimaryKeys(null, schemaName, tableName);
    readIndices(table, primaryKeys, 6, 0, 4, 5);

    ResultSet indexInfo = metaData.getIndexInfo(null, schemaName, tableName, false, false);
    readIndices(table, indexInfo, 6, 4, 9, 8);
  }

  private void readIndices(IDBTable table, ResultSet resultSet, int indexNameColumn, int indexTypeColumn,
      int fieldNameColumn, int fieldPositionColumn) throws SQLException
  {
    try
    {
      String indexName = null;
      IDBIndex.Type indexType = null;
      List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

      while (resultSet.next())
      {
        String name = resultSet.getString(indexNameColumn);
        if (indexName != null && !indexName.equals(name))
        {
          addIndex(table, indexName, indexType, fieldInfos);
          fieldInfos.clear();
        }

        indexName = name;

        if (indexTypeColumn == 0)
        {
          indexType = IDBIndex.Type.PRIMARY_KEY;
        }
        else
        {
          boolean nonUnique = resultSet.getBoolean(indexTypeColumn);
          indexType = nonUnique ? IDBIndex.Type.NON_UNIQUE : IDBIndex.Type.UNIQUE;
        }

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.name = resultSet.getString(fieldNameColumn);
        fieldInfo.position = resultSet.getShort(fieldPositionColumn);
        fieldInfos.add(fieldInfo);
      }

      if (indexName != null)
      {
        addIndex(table, indexName, indexType, fieldInfos);
      }
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  private void addIndex(IDBTable table, String name, IDBIndex.Type type, List<FieldInfo> fieldInfos)
  {
    IDBField[] fields = new IDBField[fieldInfos.size()];

    Collections.sort(fieldInfos);
    for (int i = 0; i < fieldInfos.size(); i++)
    {
      FieldInfo fieldInfo = fieldInfos.get(i);
      IDBField field = table.getField(fieldInfo.name);
      if (field == null)
      {
        throw new IllegalStateException("Field not found: " + fieldInfo.name);
      }

      fields[i] = field;
    }

    table.addIndex(name, type, fields);
  }

  /**
   * @author Eike Stepper
   */
  private static final class FieldInfo implements Comparable<FieldInfo>
  {
    public String name;

    public int position;

    public int compareTo(FieldInfo o)
    {
      return o.position - position;
    }
  }
}
