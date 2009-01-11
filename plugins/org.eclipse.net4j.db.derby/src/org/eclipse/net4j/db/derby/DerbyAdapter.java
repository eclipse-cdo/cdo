/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.derby;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class DerbyAdapter extends DBAdapter
{
  public static final String VERSION = "10.3.2.1";

  private static final String[] RESERVED_WORDS = { "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "AS", "ASC",
      "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BIT", "BOOLEAN", "BOTH", "BY", "CALL",
      "CASCADE", "CASCADED", "CASE", "CAST", "CHAR", "CHARACTER", "CHECK", "CLOSE", "COALESCE", "COLLATE", "COLLATION",
      "COLUMN", "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING",
      "CREATE", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DEALLOCATE",
      "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DIAGNOSTICS",
      "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "ELSE", "END", "END-EXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC",
      "EXECUTE", "EXISTS", "EXPLAIN", "EXTERNAL", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND",
      "FROM", "FULL", "FUNCTION", "GET", "GETCURRENTCONNECTION", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING",
      "HOUR", "IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE",
      "INSERT", "INT", "INTEGER", "INTERSECT", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LAST", "LEFT", "LIKE",
      "LOWER", "LTRIM", "MATCH", "MAX", "MIN", "MINUTE", "NATIONAL", "NATURAL", "NCHAR", "NVARCHAR", "NEXT", "NO",
      "NOT", "NULL", "NULLIF", "NUMERIC", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUTER", "OUTPUT",
      "OVERLAPS", "PAD", "PARTIAL", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC",
      "READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "RTRIM", "SCHEMA",
      "SCROLL", "SECOND", "SELECT", "SESSION_USER", "SET", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR",
      "SQLSTATE", "SUBSTR", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TEMPORARY", "TIMEZONE_HOUR",
      "TIMEZONE_MINUTE", "TO", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRIM", "TRUE", "UNION", "UNIQUE", "UNKNOWN",
      "UPDATE", "UPPER", "USER", "USING", "VALUES", "VARCHAR", "VARYING", "VIEW", "WHENEVER", "WHERE", "WITH", "WORK",
      "WRITE", "XML", "XMLEXISTS", "XMLPARSE", "XMLQUERY", "XMLSERIALIZE", "YEAR" };

  public DerbyAdapter(String name)
  {
    super(name, VERSION);
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case TINYINT:
    case BOOLEAN:
    case BIT:
      return "SMALLINT";

    case LONGVARBINARY:
    case VARBINARY:
    case BINARY:
      return "BLOB";
    }

    return super.getTypeName(field);
  }

  @Override
  public void appendValue(StringBuilder builder, IDBField field, Object value)
  {
    Object newValue = value;
    if (value instanceof Boolean)
    {
      newValue = (Boolean)value ? 1 : 0;
    }
    else if (value instanceof String)
    {
      // Derby just adds one additional single quote for a single quote
      String str = (String)value;
      StringTokenizer tokenizer = new StringTokenizer(str, "\'", true); // split on single quote
      StringBuilder newValueBuilder = new StringBuilder();

      while (tokenizer.hasMoreTokens())
      {
        String current = tokenizer.nextToken();
        if (current.length() == 0)
        {
          continue;
        }

        if (current.length() > 1) // >1 -> can not be token -> normal string
        {
          newValueBuilder.append(current);
        }
        else
        { // length == 1
          newValueBuilder.append(processEscape(current.charAt(0)));
        }
      }

      newValue = newValueBuilder.toString();
    }
    else if (value instanceof Character)
    {
      newValue = processEscape((Character)value);
    }

    super.appendValue(builder, field, newValue);
  }

  public String[] getReservedWords()
  {
    return RESERVED_WORDS;
  }

  private Object processEscape(char c)
  {
    if (c == '\'') // one single quote -->
    {
      return "\'\'"; // results two single quotes
    }

    return c; // no escape character --> return as is
  }
}
