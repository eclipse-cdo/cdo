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
package org.eclipse.net4j.db;

import org.eclipse.net4j.internal.db.DBSchema;
import org.eclipse.net4j.internal.db.bundle.OM;
import org.eclipse.net4j.util.ReflectUtil;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBUtil
{
  private DBUtil()
  {
  }

  public static IDBSchema createSchema(String name)
  {
    return new DBSchema(name);
  }

  public static DataSource createDataSource(Map<Object, Object> properties)
  {
    return createDataSource(properties, null);
  }

  public static DataSource createDataSource(Map<Object, Object> properties, String namespace)
  {
    return createDataSource(properties, namespace, "driverClass");
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

  public static IDBAdapter getDBAdapter(String adapterName)
  {
    return IDBAdapter.REGISTRY.get(adapterName);
  }

  public static Exception close(Connection connection)
  {
    if (connection != null)
    {
      try
      {
        connection.close();
      }
      catch (Exception ex)
      {
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
        return ex;
      }
    }

    return null;
  }

  public static Exception close(ResultSet resultSet)
  {
    if (resultSet != null)
    {
      try
      {
        resultSet.close();
      }
      catch (Exception ex)
      {
        return ex;
      }
    }

    return null;
  }

  public static int selectMaximum(Connection connection, IDBField field) throws DBException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT MAX(");
    builder.append(field);
    builder.append(") FROM ");
    builder.append(field.getTable());

    String sql = builder.toString();
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = connection.createStatement();

      try
      {
        rs = stmt.executeQuery(sql);
        if (!rs.first())
        {
          return 0;
        }

        return rs.getInt(1);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        close(rs);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(stmt);
    }
  }

  public static void insertRow(Connection connection, IDBTable table, Object... args) throws DBException
  {
    IDBField[] fields = table.getFields();
    if (fields.length != args.length)
    {
      throw new IllegalArgumentException("fields.length != args.length");
    }

    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");

    for (int i = 0; i < fields.length; i++)
    {
      if (i > 0)
      {
        builder.append(", ");
      }

      builder.append("?");
    }

    builder.append(")");

    String sql = builder.toString();
    PreparedStatement stmt = null;

    try
    {
      stmt = connection.prepareStatement(sql);
      for (int i = 0; i < fields.length; i++)
      {
        IDBField field = fields[i];
        stmt.setObject(i, args[i], field.getType().getCode());
      }

      stmt.execute();
      if (stmt.getUpdateCount() == 0)
      {
        throw new DBException("No row inserted into table " + table);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(stmt);
    }
  }
}
