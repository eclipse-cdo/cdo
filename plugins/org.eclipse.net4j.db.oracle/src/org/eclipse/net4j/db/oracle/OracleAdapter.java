/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.oracle.internal.bundle.OM;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import javax.sql.DataSource;

import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleAdapter extends DBAdapter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SQL, DBAdapter.class);

  public static final String NAME = "oracle"; //$NON-NLS-1$

  public static final String VERSION = "11.1.0.7"; //$NON-NLS-1$

  public OracleAdapter()
  {
    super(NAME, VERSION);
  }

  public Driver getJDBCDriver()
  {
    return new oracle.jdbc.driver.OracleDriver();
  }

  public DataSource createJDBCDataSource()
  {
    try
    {
      return new oracle.jdbc.pool.OracleDataSource();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
  }

  @Override
  protected void createIndex(IDBIndex index, Statement statement, int num) throws SQLException
  {
    IDBTable table = index.getTable();
    StringBuilder builder = new StringBuilder();
    builder.append("CREATE "); //$NON-NLS-1$
    if (index.getType() == IDBIndex.Type.UNIQUE || index.getType() == IDBIndex.Type.PRIMARY_KEY)
    {
      builder.append("UNIQUE "); //$NON-NLS-1$
    }

    builder.append("INDEX I"); //$NON-NLS-1$
    builder.append(System.currentTimeMillis());

    try
    {
      Thread.sleep(1);
    }
    catch (InterruptedException ex)
    {
      OM.LOG.error(ex);
      return;
    }

    builder.append("_");
    builder.append(num);
    builder.append(" ON "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" ("); //$NON-NLS-1$
    IDBField[] fields = index.getFields();
    for (int i = 0; i < fields.length; i++)
    {
      if (i != 0)
      {
        builder.append(", "); //$NON-NLS-1$
      }

      addIndexField(builder, fields[i]);
    }

    builder.append(")"); //$NON-NLS-1$
    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    statement.execute(sql);
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
      return "VARCHAR2(4000)";
    default:
      return super.getTypeName(field);
    }
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
}
