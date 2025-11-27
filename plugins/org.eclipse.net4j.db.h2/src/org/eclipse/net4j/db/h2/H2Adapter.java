/*
 * Copyright (c) 2009-2013, 2016, 2019, 2021, 2023-2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.RunnableWithConnection;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.om.OMPlatform;

import org.h2.api.ErrorCode;

import javax.sql.DataSource;

import java.sql.Connection;
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

  public static final String VERSION = String.valueOf("2.4.240"); //$NON-NLS-1$

  private static final boolean LOWER_CASE_SCHEMA_NAME = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.db.h2.H2Adapter.LOWER_CASE_SCHEMA_NAME"); //$NON-NLS-1$

  private static final String DEFAULT_SCHEMA_NAME = LOWER_CASE_SCHEMA_NAME ? "public" : "PUBLIC"; //$NON-NLS-1$ //$NON-NLS-2$

  public H2Adapter()
  {
    super(NAME, VERSION);
  }

  /**
   * @since 4.5
   */
  protected H2Adapter(String name, String version)
  {
    super(name, version);
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
    case BLOB:
      return "BINARY LARGE OBJECT"; //$NON-NLS-1$

    case CLOB:
      return "CHARACTER LARGE OBJECT"; //$NON-NLS-1$
    }

    return super.getTypeName(field);
  }

  @Override
  public String getDefaultSchemaName(Connection connection)
  {
    return DEFAULT_SCHEMA_NAME;
  }

  @Override
  public String[] getReservedWords()
  {
    return getSQL92ReservedWords();
  }

  @Override
  protected boolean isPrimaryKeyShadow(Connection connection, IDBTable table, String name, Type type, IDBField[] fields)
  {
    if (!name.toUpperCase().startsWith("PRIMARY_KEY"))
    {
      return false;
    }

    return super.isPrimaryKeyShadow(connection, table, name, type, fields);
  }

  @Override
  public boolean isDuplicateKeyException(SQLException ex)
  {
    String sqlState = ex.getSQLState();
    return "23001".equals(sqlState) || "23505".equals(sqlState);
  }

  @Override
  public boolean isTableNotFoundException(SQLException ex)
  {
    int errorCode = ex.getErrorCode();
    switch (errorCode)
    {
    case ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1:
    case ErrorCode.TABLE_OR_VIEW_NOT_FOUND_WITH_CANDIDATES_2:
    case ErrorCode.TABLE_OR_VIEW_NOT_FOUND_DATABASE_EMPTY_1:
      return true;

    default:
      return false;
    }
  }

  @Override
  public String sqlRenameField(IDBField field, String oldName)
  {
    return "ALTER TABLE " + field.getTable() + " ALTER COLUMN " + oldName + " RENAME TO " + field;
  }

  @Override
  protected String sqlCharIndexFunction()
  {
    return "LOCATE";
  }

  /**
   * @since 4.2
   */
  public static void createSchema(DataSource dataSource, String name, boolean dropIfExists)
  {
    DBUtil.execute(DBUtil.createConnectionProvider(dataSource), new RunnableWithConnection<Object>()
    {
      @Override
      public Object run(Connection connection) throws SQLException
      {
        if (dropIfExists)
        {
          DBUtil.execute(connection, "DROP SCHEMA IF EXISTS " + DBUtil.quoted(name) + " CASCADE");
        }

        DBUtil.execute(connection, "CREATE SCHEMA IF NOT EXISTS " + DBUtil.quoted(name));
        return null;
      }
    });
  }

  /**
   * @since 4.3
   */
  public static void shutdown(DataSource dataSource)
  {
    DBUtil.execute(DBUtil.createConnectionProvider(dataSource), new RunnableWithConnection<Object>()
    {
      @Override
      public Object run(Connection connection) throws SQLException
      {
        DBUtil.execute(connection, "SHUTDOWN");
        return null;
      }
    });
  }
}
