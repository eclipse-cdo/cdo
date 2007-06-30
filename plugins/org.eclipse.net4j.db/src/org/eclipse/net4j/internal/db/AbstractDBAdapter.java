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
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IField.Type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public abstract class AbstractDBAdapter implements IDBAdapter
{
  private String name;

  private String version;

  public AbstractDBAdapter(String name, String version)
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

  public void createTable(Table table, Statement statement)
  {
    try
    {
      doCreateTable(table, statement);
    }
    catch (SQLException ex)
    {
      System.out.println(ex.getMessage());
    }

    validateTable(table, statement);
  }

  @Override
  public String toString()
  {
    return getName() + "-" + getVersion();
  }

  protected void doCreateTable(Table table, Statement statement) throws SQLException
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
    System.out.println(sql);
    statement.execute(sql);
  }

  protected String createConstraints(Table table)
  {
    return null;
  }

  protected String createFieldDefinition(Field field)
  {
    return getTypeName(field) + (field.isNotNull() ? " NOT NULL" : "");
  }

  protected String getTypeName(Field field)
  {
    Type type = field.getType();
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
    case BLOB:
    case CLOB:
      return type.toString();

    case CHAR:
    case VARCHAR:
    case LONGVARCHAR:
    case BINARY:
    case VARBINARY:
    case LONGVARBINARY:
      return type.toString() + field.formatPrecision();

    case NUMERIC:
    case DECIMAL:
      return type.toString() + field.formatPrecisionAndScale();
    }

    throw new IllegalArgumentException("Unknown type: " + type);
  }

  protected void validateTable(Table table, Statement statement)
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
        throw new DBException("Table " + table.getName() + " has " + columnCount + " columns instead of "
            + table.getFieldCount());
      }

      for (int i = 0; i < columnCount; i++)
      {
        int existingCode = metaData.getColumnType(i + 1);
        Field field = table.getField(i);
        int code = field.getType().getCode();
        if (code != existingCode)
        {
          throw new DBException("Field " + field.getFullName() + " has type " + existingCode + " instead of " + code
              + " (" + field.getType() + ")");
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex.getMessage());
    }
  }

  private String[] createFieldDefinitions(Table table)
  {
    Field[] fields = table.getFields();
    int fieldCount = fields.length;

    String[] result = new String[fieldCount];
    for (int i = 0; i < fieldCount; i++)
    {
      Field field = fields[i];
      result[i] = createFieldDefinition(field);
    }

    return result;
  }
}
