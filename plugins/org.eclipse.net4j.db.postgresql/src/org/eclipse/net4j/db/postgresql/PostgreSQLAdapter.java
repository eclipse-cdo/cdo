/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - Bug 276979
 *    Stefan Winkler - Bug 289445
 */
package org.eclipse.net4j.db.postgresql;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.internal.postgresql.bundle.OM;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.ConsumerWithException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link IDBAdapter DB adapter} for <a href="http://www.postgresql.org/">PostgreSQL</a> databases.
 *
 * @author Victor Roldan Betancort
 */
public class PostgreSQLAdapter extends DBAdapter
{
  public static final String NAME = "postgresql"; //$NON-NLS-1$

  public static final String VERSION = "9.0"; //$NON-NLS-1$

  // private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SQL, DBAdapter.class);

  private static final String[] RESERVED_WORDS = { "ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "AS", "ASC", "ATOMIC", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      "AUTHORIZATION", "BETWEEN", "BIGINT", "BINARY", "BIT", "BOOLEAN", "BOTH", "C", "CASE", "CAST", "CHAR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
      "CHARACTER", "CHECK", "COALESCE", "COLLATE", "COLUMN", "CONSTRAINT", "CONVERT", "CREATE", "CROSS", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
      "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DEC", "DECIMAL", "DEFAULT", "DEFERRABLE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "EXECUTE", "EXISTS", "EXTRACT", "FALSE", "FLOAT", "FOR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
      "FOREIGN", "FREEZE", "FROM", "FULL", "GRANT", "GROUP", "HAVING", "ILIKE", "IN", "INITIALLY", "INNER", "INT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
      "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISNULL", "JOIN", "LEADING", "LEFT", "LIKE", "LIMIT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
      "LOCALTIME", "LOCALTIMESTAMP", "NATURAL", "NCHAR", "NCLOB", "NEW", "NONE", "NOT", "NOTNULL", "NULL", "NULLIF", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
      "NUMERIC", "OFF", "OFFSET", "OLD", "ON", "ONLY", "OR", "ORDER", "OUTER", "OVERLAPS", "OVERLAY", "PLACING", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
      "POSITION", "PRIMARY", "REAL", "RECHECK", "REFERENCES", "RIGHT", "ROW", "SELECT", "SESSION_USER", "SETOF", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
      "SIMILAR", "SMALLINT", "SOME", "SUBSTRING", "TABLE", "THEN", "TIME", "TIMESTAMP", "TO", "TRAILING", "TREAT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
      "TRIM", "TRUE", "UNION", "UNIQUE", "USER", "USING", "VARCHAR", "VERBOSE", "WHEN", "WHERE" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

  public PostgreSQLAdapter()
  {
    super(NAME, VERSION);
  }

  /**
   * @since 4.4
   */
  protected PostgreSQLAdapter(String name, String version)
  {
    super(name, version);
  }

  @Override
  public boolean isCaseSensitive()
  {
    return true;
  }

  @Override
  protected void forEachTable(Connection connection, String schemaName, boolean caseSensitive, ConsumerWithException<String, SQLException> tableNameConsumer)
  {
    if (schemaName == null)
    {
      super.forEachTable(connection, schemaName, caseSensitive, tableNameConsumer);
      return;
    }

    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement("SELECT c.relname FROM pg_catalog.pg_class c " + "JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace "
          + "WHERE n.nspname = ? AND c.relkind IN ('r', 'p', 'v', 'm', 'f')");
      statement.setString(1, schemaName);
      resultSet = statement.executeQuery();

      while (resultSet.next())
      {
        tableNameConsumer.accept(resultSet.getString(1));
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
    }
  }

  @Override
  protected void readIndices(Connection connection, java.sql.DatabaseMetaData metaData, IDBTable table, String schemaName) throws SQLException
  {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    Map<String, IndexInfo> indexInfos = new HashMap<>();

    try
    {
      statement = connection.prepareStatement("SELECT i.relname, ix.indisprimary, ix.indisunique, a.attname, s.n " + "FROM pg_catalog.pg_index ix "
          + "JOIN pg_catalog.pg_class t ON t.oid = ix.indrelid " + "JOIN pg_catalog.pg_namespace n ON n.oid = t.relnamespace "
          + "JOIN pg_catalog.pg_class i ON i.oid = ix.indexrelid " + "JOIN LATERAL generate_subscripts(ix.indkey, 1) s(n) ON true "
          + "JOIN pg_catalog.pg_attribute a ON a.attrelid = t.oid AND a.attnum = ix.indkey[s.n] "
          + "WHERE n.nspname = ? AND t.relname = ? ORDER BY i.relname, s.n");
      statement.setString(1, schemaName);
      statement.setString(2, table.getName());
      resultSet = statement.executeQuery();

      while (resultSet.next())
      {
        String name = resultSet.getString(1);
        IndexInfo indexInfo = indexInfos.computeIfAbsent(name, key -> new IndexInfo());
        indexInfo.name = name;
        indexInfo.type = resultSet.getBoolean(2) ? IDBIndex.Type.PRIMARY_KEY : resultSet.getBoolean(3) ? IDBIndex.Type.UNIQUE : IDBIndex.Type.NON_UNIQUE;

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.name = resultSet.getString(4);
        fieldInfo.position = resultSet.getInt(5);
        indexInfo.fieldInfos.add(fieldInfo);
      }
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
    }

    indexInfos.values().forEach(indexInfo -> addIndex(connection, table, indexInfo.name, indexInfo.type, indexInfo.fieldInfos));
  }

  @Override
  public String getDefaultSchemaName(Connection connection)
  {
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.createStatement();
      resultSet = statement.executeQuery("SELECT current_schema()");
      if (!resultSet.next())
      {
        throw new DBException("PostgreSQL did not return an effective schema for the connection");
      }

      String schemaName = resultSet.getString(1);
      if (schemaName == null)
      {
        throw new DBException("PostgreSQL returned a null effective schema for the connection");
      }

      return schemaName;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public int getMaxTableNameLength()
  {
    // http://www.postgresql.org/docs/9.2/static/sql-syntax-lexical.html
    return 63;
  }

  /**
   * @since 2.0
   */
  @Override
  public int getMaxFieldNameLength()
  {
    // http://www.postgresql.org/docs/9.2/static/sql-syntax-lexical.html
    return 63;
  }

  @Override
  public int getJDBCTypeForNull(DBType type)
  {
    if (type == DBType.BLOB)
    {
      return Types.VARBINARY;
    }

    return super.getJDBCTypeForNull(type);
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    // http://www.postgresql.org/docs/9.2/static/datatype.html
    DBType type = field.getType();
    switch (type)
    {
    case BIT:
      return "boolean"; //$NON-NLS-1$

    case TINYINT:
      return DBType.SMALLINT.toString();

    case VARCHAR:
      if (field.getPrecision() != getDefaultDBLength(DBType.VARCHAR))
      {
        return DBType.VARCHAR.toString() + field.formatPrecision();
      }

      return "text"; //$NON-NLS-1$

    case LONGVARCHAR:
    case CLOB:
      return "text"; //$NON-NLS-1$

    case BINARY:
    case VARBINARY:
    case LONGVARBINARY:
    case BLOB:
      return "bytea"; //$NON-NLS-1$

    case DOUBLE:
      return "double precision"; //$NON-NLS-1$
    }

    return super.getTypeName(field);
  }

  @Override
  public String[] getReservedWords()
  {
    return RESERVED_WORDS;
  }

  /**
   * See <a href="http://www.postgresql.org/docs/9.0/static/errcodes-appendix.html">Appendix A. PostgreSQL Error Codes</a>.
   */
  @Override
  public boolean isDuplicateKeyException(SQLException ex)
  {
    // RESTRICT VIOLATION || UNIQUE VIOLATION
    return super.isDuplicateKeyException(ex) || "23505".equals(ex.getSQLState());
  }

  /**
   * See <a href="http://www.postgresql.org/docs/9.0/static/errcodes-appendix.html">Appendix A. PostgreSQL Error Codes</a>.
   */
  @Override
  public boolean isTableNotFoundException(SQLException ex)
  {
    // UNDEFINED TABLE
    return "42P01".equals(ex.getSQLState());
  }

  /**
   * See <a href="http://www.postgresql.org/docs/9.0/static/errcodes-appendix.html">Appendix A. PostgreSQL Error Codes</a>.
   */
  @Override
  public boolean isColumnNotFoundException(SQLException ex)
  {
    // UNDEFINED COLUMN
    return "42703".equals(ex.getSQLState());
  }

  /**
   * @since 4.1
   */
  @Override
  protected void doCreateTable(IDBTable table, Statement statement) throws SQLException
  {
    Savepoint savepoint = statement.getConnection().setSavepoint();

    try
    {
      super.doCreateTable(table, statement);
    }
    catch (SQLException ex)
    {
      try
      {
        statement.getConnection().rollback(savepoint);
      }
      catch (SQLException ex1)
      {
        OM.LOG.error(ex1);
      }

      throw ex;
    }
  }

  @Override
  public String sqlCharIndex(Object substring, Object string)
  {
    return "STRPOS(" + string + ", " + substring + ")";
  }
}
