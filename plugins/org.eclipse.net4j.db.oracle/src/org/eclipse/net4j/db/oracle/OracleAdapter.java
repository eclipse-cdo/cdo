/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Erdal Karaca - copied from mysql impl to adapt to oracle db
 */
package org.eclipse.net4j.db.oracle;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleAdapter extends DBAdapter
{
  public static final String NAME = "oracle"; //$NON-NLS-1$

  public static final String VERSION = "11.1.0.7"; //$NON-NLS-1$

  public OracleAdapter()
  {
    super(NAME, VERSION);
  }

  public String[] getReservedWords()
  {
    List<String> list = new ArrayList<String>(Arrays.asList(getSQL92ReservedWords()));
    list.add("INDEX");
    list.add("COMMENT");
    return list.toArray(new String[list.size()]);
  }

  @Override
  public boolean isTypeIndexable(DBType type)
  {
    switch (type)
    {
    case VARCHAR:
      return false;

    default:
      return super.isTypeIndexable(type);
    }
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case NUMERIC:
    case DECIMAL:
    case FLOAT:
    case REAL:
    case DOUBLE:
    case BIGINT:
      return "NUMBER";
    case TINYINT:
      return "NUMBER(5)";
    case SMALLINT:
    case BOOLEAN:
      return "NUMBER(7)";
    case INTEGER:
      return "NUMBER(12)";
    case DATE:
    case TIME:
      return "DATE";
    case CHAR:
    case VARCHAR:
      return "VARCHAR2(" + field.getPrecision() + ")";
    default:
      return super.getTypeName(field);
    }
  }

  @Override
  public int getFieldLength(DBType type)
  {
    if (type == DBType.VARCHAR)
    {
      return 4000; // Oracle only supports 4000 for VARCHAR
    }

    return super.getFieldLength(type);
  }

  @Override
  public DBType adaptType(DBType type)
  {
    if (type == DBType.BOOLEAN)
    {
      return DBType.SMALLINT;
    }

    return super.adaptType(type);
  }

  @Override
  public int getMaxTableNameLength()
  {
    return 30;
  }

  @Override
  public int getMaxFieldNameLength()
  {
    return 30;
  }

  @Override
  public boolean isTableNotFoundException(SQLException ex)
  {
    String message = ex.getMessage();
    return message != null && message.toLowerCase().contains("ora-00942") && "42000".equals(ex.getSQLState());
  }

  @Override
  public boolean isColumnNotFoundException(SQLException ex)
  {
    String message = ex.getMessage();
    return message != null && message.toLowerCase().contains("ora-00904") && "42000".equals(ex.getSQLState());
  }

  @Override
  public boolean isDuplicateKeyException(SQLException ex)
  {
    String message = ex.getMessage();
    return message != null && message.toLowerCase().contains("ora-00001") && "23000".equals(ex.getSQLState());
  }

  @Override
  protected String sqlModifyField(String tableName, String fieldName, String definition)
  {
    return "ALTER TABLE " + tableName + " MODIFY " + fieldName + " " + definition;
  }
}
