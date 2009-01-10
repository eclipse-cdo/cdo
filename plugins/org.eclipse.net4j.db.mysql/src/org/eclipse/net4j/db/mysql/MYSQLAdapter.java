/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.db.mysql;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import com.mysql.jdbc.Driver;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class MYSQLAdapter extends DBAdapter
{
  public static final String NAME = "mysql";

  public static final String VERSION = "5.1.5";

  private static final String[] RESERVED_WORDS = { "ACTION", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC",
      "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BIT", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE",
      "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT",
      "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE",
      "DATABASES", "DATE", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE",
      "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE",
      "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ENUM", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE",
      "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP", "HAVING",
      "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE",
      "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL",
      "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINES",
      "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH",
      "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES",
      "NATURAL", "NO", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR",
      "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RAID0", "READ", "READS",
      "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN",
      "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET",
      "SHOW", "SMALLINT", "SONAME", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING",
      "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE",
      "TERMINATED", "TEXT", "THEN", "TIME", "TIMESTAMP", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING",
      "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING",
      "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN",
      "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL" };

  public MYSQLAdapter()
  {
    super(NAME, VERSION);
  }

  public Driver getJDBCDriver()
  {
    try
    {
      return new Driver();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public DataSource createJDBCDataSource()
  {
    return new MysqlDataSource();
  }

  /**
   * @since 2.0
   */
  public int getMaxTableNameLength()
  {
    return 64;
  }

  /**
   * @since 2.0
   */
  public int getMaxFieldNameLength()
  {
    return 64;
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case VARCHAR:
    case CLOB:
      return "LONGTEXT";
    }

    return super.getTypeName(field);
  }

  @Override
  protected void addIndexField(StringBuilder builder, IDBField field)
  {
    super.addIndexField(builder, field);
    if (field.getType() == DBType.VARCHAR)
    {
      builder.append("(");
      builder.append(field.getPrecision());
      builder.append(")");
    }
  }

  public String[] getReservedWords()
  {
    return RESERVED_WORDS;
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
  public void appendValue(StringBuilder builder, IDBField field, Object value)
  {
    Object newValue = value;

    if (value instanceof String)
    {
      // MySQL does Java-Style backslash-escaping
      String str = (String)value;
      StringTokenizer st = new StringTokenizer(str, "\\\'", true); // split on backslash or single quote
      StringBuilder newValueBuilder = new StringBuilder();

      while (st.hasMoreTokens())
      {
        String current = st.nextToken();
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

  private Object processEscape(char c)
  {
    if (c == '\\') // one backslash -->
    {
      return "\\\\"; // results in two backslashes
    }

    if (c == '\'') // one single quote -->
    {
      return "\\'"; // results in backslash+single quote
    }

    return c; // no escape character --> return as is
  }
}
