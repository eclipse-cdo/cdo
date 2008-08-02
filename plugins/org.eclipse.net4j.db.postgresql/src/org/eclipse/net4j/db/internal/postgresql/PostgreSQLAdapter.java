/***************************************************************************
 * Copyright (c) 2008 Open Canarias S.L. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.net4j.db.internal.postgresql;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * @author Victor Roldan Betancort
 */
public class PostgreSQLAdapter extends DBAdapter
{
  public static final String NAME = "postgresql";

  public static final String VERSION = "8.3";

  private static final String[] RESERVED_WORDS = { "ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "AS", "ASC", "ATOMIC",
      "AUTHORIZATION", "BETWEEN", "BIGINT", "BINARY", "BIT", "BOOLEAN", "BOTH", "C", "CASE", "CAST", "CHAR",
      "CHARACTER", "CHECK", "COALESCE", "COLLATE", "COLUMN", "CONSTRAINT", "CONVERT", "CREATE", "CROSS",
      "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DEC", "DECIMAL", "DEFAULT", "DEFERRABLE",
      "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "EXECUTE", "EXISTS", "EXTRACT", "FALSE", "FLOAT", "FOR",
      "FOREIGN", "FREEZE", "FROM", "FULL", "GRANT", "GROUP", "HAVING", "ILIKE", "IN", "INITIALLY", "INNER", "INT",
      "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISNULL", "JOIN", "LEADING", "LEFT", "LIKE", "LIMIT",
      "LOCALTIME", "LOCALTIMESTAMP", "NATURAL", "NCHAR", "NCLOB", "NEW", "NONE", "NOT", "NOTNULL", "NULL", "NULLIF",
      "NUMERIC", "OFF", "OFFSET", "OLD", "ON", "ONLY", "OR", "ORDER", "OUTER", "OVERLAPS", "OVERLAY", "PLACING",
      "POSITION", "PRIMARY", "REAL", "RECHECK", "REFERENCES", "RIGHT", "ROW", "SELECT", "SESSION_USER", "SETOF",
      "SIMILAR", "SMALLINT", "SOME", "SUBSTRING", "TABLE", "THEN", "TIME", "TIMESTAMP", "TO", "TRAILING", "TREAT",
      "TRIM", "TRUE", "UNION", "UNIQUE", "USER", "USING", "VARCHAR", "VERBOSE", "WHEN", "WHERE" };

  public PostgreSQLAdapter()
  {
    super(NAME, VERSION);
  }

  public Driver getJDBCDriver()
  {
    return new Driver();
  }

  public DataSource createJDBCDataSource()
  {
    return new PGSimpleDataSource();
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case VARCHAR:
    case CLOB:
      return "text";
    }
    return super.getTypeName(field);
  }

  public String[] getReservedWords()
  {
    return RESERVED_WORDS;
  }
}
