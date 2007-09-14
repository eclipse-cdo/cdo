/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.db.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class DBAdapter implements IDBAdapter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SQL, DBAdapter.class);

  private String name;

  private String version;

  public DBAdapter(String name, String version)
  {
    this.name = name;
    this.version = version;
  }

  public String getName()
  {
    return name;
  }

  public String getVersion()
  {
    return version;
  }

  public Set<IDBTable> createTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException
  {
    Set<IDBTable> createdTables = new HashSet<IDBTable>();
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      for (IDBTable table : tables)
      {
        if (createTable(table, statement))
        {
          createdTables.add(table);
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

    return createdTables;
  }

  public boolean createTable(IDBTable table, Statement statement) throws DBException
  {
    boolean created = true;

    try
    {
      doCreateTable((DBTable)table, statement);
    }
    catch (SQLException ex)
    {
      created = false;
      if (TRACER.isEnabled())
      {
        TRACER.trace("-- " + ex.getMessage());
      }
    }

    validateTable((DBTable)table, statement);
    return created;
  }

  public String mangleTableName(String name, int attempt)
  {
    return mangleName(name, getMaximumTableNameLength(), attempt);
  }

  public String mangleFieldName(String name, int attempt)
  {
    return mangleName(name, getMaximumFieldNameLength(), attempt);
  }

  public void appendValue(StringBuilder builder, IDBField field, Object value)
  {
    field.appendValue(builder, value);
  }

  @Override
  public String toString()
  {
    return getName() + "-" + getVersion();
  }

  protected void doCreateTable(DBTable table, Statement statement) throws SQLException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("CREATE TABLE ");
    builder.append(table.getName());
    builder.append(" (");
    table.appendFieldDefs(builder, createFieldDefinitions(table));
    String constraints = createConstraints(table);
    if (constraints != null)
    {
      builder.append(", ");
      builder.append(constraints);
    }

    builder.append(")");
    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    statement.execute(sql);
  }

  protected String createConstraints(DBTable table)
  {
    return null;
  }

  protected String createFieldDefinition(DBField field)
  {
    return getTypeName(field) + (field.isNotNull() ? " NOT NULL" : "");
  }

  protected String getTypeName(DBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case BOOLEAN:
    case BIT:
    case TINYINT:
    case SMALLINT:
    case INTEGER:
    case BIGINT:
    case FLOAT:
    case REAL:
    case DOUBLE:
    case DATE:
    case TIME:
    case TIMESTAMP:
    case LONGVARCHAR:
    case LONGVARBINARY:
    case BLOB:
    case CLOB:
      return type.toString();

    case CHAR:
    case VARCHAR:
    case BINARY:
    case VARBINARY:
      return type.toString() + field.formatPrecision();

    case NUMERIC:
    case DECIMAL:
      return type.toString() + field.formatPrecisionAndScale();
    }

    throw new IllegalArgumentException("Unknown type: " + type);
  }

  protected int getMaximumTableNameLength()
  {
    return 128;
  }

  protected int getMaximumFieldNameLength()
  {
    return 128;
  }

  protected String mangleName(String name, int max, int attempt)
  {
    if (isReservedWord(name))
    {
      String suffix = "_" + String.valueOf(attempt);
      if (name.length() + suffix.length() > max)
      {
        name = name.substring(0, max - suffix.length()) + suffix;
      }
      else
      {
        name += suffix;
      }
    }
    else if (name.length() > max)
    {
      name = name.substring(0, max);
    }

    return name;
  }

  protected boolean isReservedWord(String word)
  {
    return false;
  }

  protected void validateTable(DBTable table, Statement statement) throws DBException
  {
    try
    {
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT ");
      table.appendFieldNames(builder);
      builder.append(" FROM ");
      builder.append(table.getName());
      String sql = builder.toString();

      ResultSet resultSet = statement.executeQuery(sql);
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      if (columnCount != table.getFieldCount())
      {
        throw new DBException("DBTable " + table.getName() + " has " + columnCount + " columns instead of "
            + table.getFieldCount());
      }

      // for (int i = 0; i < columnCount; i++)
      // {
      // int existingCode = metaData.getColumnType(i + 1);
      // DBField field = table.getField(i);
      // int code = field.getType().getCode();
      // if (code != existingCode)
      // {
      // throw new DBException("DBField " + field.getFullName() + " has type " +
      // existingCode + " instead of " + code
      // + " (" + field.getType() + ")");
      // }
      // }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex.getMessage());
    }
  }

  private String[] createFieldDefinitions(DBTable table)
  {
    DBField[] fields = table.getFields();
    int fieldCount = fields.length;

    String[] result = new String[fieldCount];
    for (int i = 0; i < fieldCount; i++)
    {
      DBField field = fields[i];
      result[i] = createFieldDefinition(field);
    }

    return result;
  }
}
