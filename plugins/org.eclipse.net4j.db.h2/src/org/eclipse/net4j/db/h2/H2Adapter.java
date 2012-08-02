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
package org.eclipse.net4j.db.h2;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.ReflectUtil;

import org.h2.command.CommandInterface;
import org.h2.expression.ParameterInterface;
import org.h2.jdbc.JdbcPreparedStatement;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.util.ObjectArray;
import org.h2.value.Value;

import javax.sql.DataSource;

import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A {@link IDBAdapter DB adapter} for <a href="http://www.h2database.com/html/main.html">H2</a> databases.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class H2Adapter extends DBAdapter
{
  private static final String NAME = "h2"; //$NON-NLS-1$

  public static final String VERSION = "1.1.114"; //$NON-NLS-1$

  private static final Field SQL_FIELD = ReflectUtil.getField(JdbcPreparedStatement.class, "sql");

  private static final Field COMMAND_FIELD = ReflectUtil.getField(JdbcPreparedStatement.class, "command");

  public H2Adapter()
  {
    super(NAME, VERSION);
  }

  public Driver getJDBCDriver()
  {
    return new org.h2.Driver();
  }

  public DataSource createJDBCDataSource()
  {
    return new JdbcDataSource();
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case BIT:
      return "SMALLINT"; //$NON-NLS-1$

    case FLOAT:
      return "REAL"; //$NON-NLS-1$

    case LONGVARCHAR:
      return "VARCHAR"; //$NON-NLS-1$

    case NUMERIC:
      return "DECIMAL"; //$NON-NLS-1$

    case LONGVARBINARY:
    case VARBINARY:
      return "BLOB"; //$NON-NLS-1$
    }

    return super.getTypeName(field);
  }

  public String[] getReservedWords()
  {
    return getSQL92ReservedWords();
  }

  @Override
  public boolean isDuplicateKeyException(SQLException ex)
  {
    String sqlState = ex.getSQLState();
    return "23001".equals(sqlState) || "23505".equals(sqlState);
  }

  @Override
  public String format(PreparedStatement stmt)
  {
    try
    {
      if (stmt instanceof JdbcPreparedStatement)
      {
        String sql = (String)ReflectUtil.getValue(SQL_FIELD, stmt);

        boolean insert = false;
        if (sql.toUpperCase().startsWith("INSERT INTO"))
        {
          int pos = sql.indexOf("(");
          sql = sql.substring(0, pos) + "SET " + sql.substring(pos + 1);
          sql = sql.substring(0, sql.indexOf(" VALUES"));
          insert = true;
        }

        CommandInterface command = (CommandInterface)ReflectUtil.getValue(COMMAND_FIELD, stmt);
        ObjectArray<? extends ParameterInterface> parameters = command.getParameters();

        int pos = 0;
        for (int i = 0; i < parameters.size(); i++)
        {
          ParameterInterface parameter = parameters.get(i);
          Value value = parameter.getParamValue();

          if (insert)
          {
            String string = "=" + value;

            pos = sql.indexOf(',', pos);
            if (pos == -1)
            {
              pos = sql.indexOf(')');
              sql = sql.substring(0, pos) + string;
              break;
            }

            sql = sql.substring(0, pos) + string + sql.substring(pos);
            pos += string.length() + 1;
          }
          else
          {
            pos = sql.indexOf('?');
            sql = sql.substring(0, pos) + value + sql.substring(pos + 1);
          }
        }

        return sql;
      }
    }
    catch (Throwable t)
    {
      //$FALL-THROUGH$
    }

    return super.format(stmt);
  }
}
