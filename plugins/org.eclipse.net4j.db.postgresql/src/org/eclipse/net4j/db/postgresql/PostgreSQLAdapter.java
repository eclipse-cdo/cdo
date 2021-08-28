/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - Bug 276979
 *    Stefan Winkler - Bug 289445
 */
package org.eclipse.net4j.db.postgresql;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.internal.postgresql.bundle.OM;
import org.eclipse.net4j.spi.db.DBAdapter;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

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
}
