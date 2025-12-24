/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019-2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.mysql;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.spi.db.DBAdapter;

import com.mysql.jdbc.ConnectionProperties;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A {@link IDBAdapter DB adapter} for <a href="http://www.mysql.com/">MySQL</a> databases.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class MYSQLAdapter extends DBAdapter
{
  public static final String NAME = "mysql"; //$NON-NLS-1$

  public static final String VERSION = "5.7.28"; //$NON-NLS-1$

  private static final String[] RESERVED_WORDS = { "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN",
      "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION",
      "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE",
      "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE",
      "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN",
      "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GENERATED", "GET", "GRANT", "GROUP", "HAVING",
      "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT",
      "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "KEY", "KEYS",
      "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT",
      "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT",
      "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NO_WRITE_TO_BINLOG", "NOT", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTIMIZER_COSTS",
      "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PARTITION", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ",
      "READ_WRITE", "READS", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE",
      "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL",
      "SPECIFIC", "SQL", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SSL", "STARTING", "STORED",
      "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE",
      "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER",
      "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL" };

  public MYSQLAdapter()
  {
    super(NAME, VERSION);
  }

  /**
   * @since 4.4
   */
  protected MYSQLAdapter(String name, String version)
  {
    super(name, version);
  }

  /**
   * @since 2.0
   */
  @Override
  public int getMaxTableNameLength()
  {
    return 64;
  }

  /**
   * @since 2.0
   */
  @Override
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
      if (field.isIndexed())
      {
        // Use VARCHAR(length)
        break;
      }

      if (field.getPrecision() != getDefaultDBLength(DBType.VARCHAR))
      {
        // Use VARCHAR(length)
        break;
      }

      return "LONGTEXT"; //$NON-NLS-1$

    case CLOB:
      return "LONGTEXT"; //$NON-NLS-1$

    case BLOB:
      return "LONGBLOB"; //$NON-NLS-1$
    }

    return super.getTypeName(field);
  }

  @Override
  protected void dropPrimaryKey(IDBIndex index, StringBuilder builder)
  {
    builder.append("ALTER TABLE "); //$NON-NLS-1$
    builder.append(index.getTable());
    builder.append(" DROP PRIMARY KEY"); //$NON-NLS-1$
  }

  @Override
  protected void addIndexField(StringBuilder builder, IDBField field)
  {
    super.addIndexField(builder, field);
    if (field.getType() == DBType.VARCHAR)
    {
      builder.append("("); //$NON-NLS-1$
      builder.append(field.getPrecision());
      builder.append(")"); //$NON-NLS-1$
    }
  }

  @Override
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
  public boolean isDuplicateKeyException(SQLException ex)
  {
    return "23000".equals(ex.getSQLState());
  }

  @Override
  public String sqlRenameField(IDBField field, String oldName)
  {
    return "ALTER TABLE " + field.getTable() + " CHANGE COLUMN " + oldName + " " + field + " " + createFieldDefinition(field);
  }

  @Override
  protected String sqlModifyField(String tableName, String fieldName, String definition)
  {
    return "ALTER TABLE " + tableName + " MODIFY " + fieldName + " " + definition;
  }

  @Override
  protected String sqlCharIndexFunction()
  {
    return "LOCATE";
  }

  @Override
  public Connection modifyConnection(Connection connection)
  {
    if (connection instanceof ConnectionProperties)
    {
      ConnectionProperties connectionProperties = (ConnectionProperties)connection;
      connectionProperties.setUseLocalSessionState(true);
    }

    return super.modifyConnection(connection);
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("unused")
  private static final class ReservedWordGenerator
  {
    /**
     * From https://dev.mysql.com/doc/refman/5.7/en/keywords.html
     */
    private static final String[] WORDS = { "ACCESSIBLE", "ACCOUNT", "ACTION", "ADD", "AFTER", "AGAINST", "AGGREGATE", "ALGORITHM", "ALL", "ALTER", "ALWAYS",
        "ANALYSE", "ANALYZE", "AND", "ANY", "AS", "ASC", "ASCII", "ASENSITIVE", "AT", "AUTO_INCREMENT", "AUTOEXTEND_SIZE", "AVG", "AVG_ROW_LENGTH", "BACKUP",
        "BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BINLOG", "BIT", "BLOB", "BLOCK", "BOOL", "BOOLEAN", "BOTH", "BTREE", "BY", "BYTE", "CACHE", "CALL",
        "CASCADE", "CASCADED", "CASE", "CATALOG_NAME", "CHAIN", "CHANGE", "CHANGED", "CHANNEL", "CHAR", "CHARACTER", "CHARSET", "CHECK", "CHECKSUM", "CIPHER",
        "CLASS_ORIGIN", "CLIENT", "CLOSE", "COALESCE", "CODE", "COLLATE", "COLLATION", "COLUMN", "COLUMN_FORMAT", "COLUMN_NAME", "COLUMNS", "COMMENT", "COMMIT",
        "COMMITTED", "COMPACT", "COMPLETION", "COMPRESSED", "COMPRESSION", "CONCURRENT", "CONDITION", "CONNECTION", "CONSISTENT", "CONSTRAINT",
        "CONSTRAINT_CATALOG", "CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONTAINS", "CONTEXT", "CONTINUE", "CONVERT", "CPU", "CREATE", "CROSS", "CUBE", "CURRENT",
        "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "CURSOR_NAME", "DATA", "DATABASE", "DATABASES", "DATAFILE", "DATE",
        "DATETIME", "DAY", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFAULT_AUTH",
        "DEFINER", "DELAY_KEY_WRITE", "DELAYED", "DELETE", "DES_KEY_FILE", "DESC", "DESCRIBE", "DETERMINISTIC", "DIAGNOSTICS", "DIRECTORY", "DISABLE",
        "DISCARD", "DISK", "DISTINCT", "DISTINCTROW", "DIV", "DO", "DOUBLE", "DROP", "DUAL", "DUMPFILE", "DUPLICATE", "DYNAMIC", "EACH", "ELSE", "ELSEIF",
        "ENABLE", "ENCLOSED", "ENCRYPTION", "END", "ENDS", "ENGINE", "ENGINES", "ENUM", "ERROR", "ERRORS", "ESCAPE", "ESCAPED", "EVENT", "EVENTS", "EVERY",
        "EXCHANGE", "EXECUTE", "EXISTS", "EXIT", "EXPANSION", "EXPIRE", "EXPLAIN", "EXPORT", "EXTENDED", "EXTENT_SIZE", "FALSE", "FAST", "FAULTS", "FETCH",
        "FIELDS", "FILE", "FILE_BLOCK_SIZE", "FILTER", "FIRST", "FIXED", "FLOAT", "FLOAT4", "FLOAT8", "FLUSH", "FOLLOWS", "FOR", "FORCE", "FOREIGN", "FORMAT",
        "FOUND", "FROM", "FULL", "FULLTEXT", "FUNCTION", "GENERAL", "GENERATED", "GEOMETRY", "GEOMETRYCOLLECTION", "GET", "GET_FORMAT", "GLOBAL", "GRANT",
        "GRANTS", "GROUP", "GROUP_REPLICATION", "HANDLER", "HASH", "HAVING", "HELP", "HIGH_PRIORITY", "HOST", "HOSTS", "HOUR", "HOUR_MICROSECOND",
        "HOUR_MINUTE", "HOUR_SECOND", "IDENTIFIED", "IF", "IGNORE", "IGNORE_SERVER_IDS", "IMPORT", "IN", "INDEX", "INDEXES", "INFILE", "INITIAL_SIZE", "INNER",
        "INOUT", "INSENSITIVE", "INSERT", "INSERT_METHOD", "INSTALL", "INSTANCE", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO",
        "INVOKER", "IO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IO_THREAD", "IPC", "IS", "ISOLATION", "ISSUER", "ITERATE", "JOIN", "JSON", "KEY",
        "KEY_BLOCK_SIZE", "KEYS", "KILL", "LANGUAGE", "LAST", "LEADING", "LEAVE", "LEAVES", "LEFT", "LESS", "LEVEL", "LIKE", "LIMIT", "LINEAR", "LINES",
        "LINESTRING", "LIST", "LOAD", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LOCKS", "LOGFILE", "LOGS", "LONG", "LONGBLOB", "LONGTEXT", "LOOP",
        "LOW_PRIORITY", "MASTER", "MASTER_AUTO_POSITION", "MASTER_BIND", "MASTER_CONNECT_RETRY", "MASTER_DELAY", "MASTER_HEARTBEAT_PERIOD", "MASTER_HOST",
        "MASTER_LOG_FILE", "MASTER_LOG_POS", "MASTER_PASSWORD", "MASTER_PORT", "MASTER_RETRY_COUNT", "MASTER_SERVER_ID", "MASTER_SSL", "MASTER_SSL_CA",
        "MASTER_SSL_CAPATH", "MASTER_SSL_CERT", "MASTER_SSL_CIPHER", "MASTER_SSL_CRL", "MASTER_SSL_CRLPATH", "MASTER_SSL_KEY", "MASTER_SSL_VERIFY_SERVER_CERT",
        "MASTER_TLS_VERSION", "MASTER_USER", "MATCH", "MAX_CONNECTIONS_PER_HOUR", "MAX_QUERIES_PER_HOUR", "MAX_ROWS", "MAX_SIZE", "MAX_STATEMENT_TIME",
        "MAX_UPDATES_PER_HOUR", "MAX_USER_CONNECTIONS", "MAXVALUE", "MEDIUM", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MEMORY", "MERGE", "MESSAGE_TEXT",
        "MICROSECOND", "MIDDLEINT", "MIGRATE", "MIN_ROWS", "MINUTE", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODE", "MODIFIES", "MODIFY", "MONTH",
        "MULTILINESTRING", "MULTIPOINT", "MULTIPOLYGON", "MUTEX", "MYSQL_ERRNO", "NAME", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NDB", "NDBCLUSTER", "NEVER",
        "NEW", "NEXT", "NO", "NO_WAIT", "NO_WRITE_TO_BINLOG", "NODEGROUP", "NONBLOCKING", "NONE", "NOT", "NULL", "NUMBER", "NUMERIC", "NVARCHAR", "OFFSET",
        "OLD_PASSWORD", "ON", "ONE", "ONLY", "OPEN", "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONALLY", "OPTIONS", "OR", "ORDER", "OUT", "OUTER", "OUTFILE",
        "OWNER", "PACK_KEYS", "PAGE", "PARSE_GCOL_EXPR", "PARSER", "PARTIAL", "PARTITION", "PARTITIONING", "PARTITIONS", "PASSWORD", "PHASE", "PLUGIN",
        "PLUGIN_DIR", "PLUGINS", "POINT", "POLYGON", "PORT", "PRECEDES", "PRECISION", "PREPARE", "PRESERVE", "PREV", "PRIMARY", "PRIVILEGES", "PROCEDURE",
        "PROCESSLIST", "PROFILE", "PROFILES", "PROXY", "PURGE", "QUARTER", "QUERY", "QUICK", "RANGE", "READ", "READ_ONLY", "READ_WRITE", "READS", "REAL",
        "REBUILD", "RECOVER", "REDO_BUFFER_SIZE", "REDOFILE", "REDUNDANT", "REFERENCES", "REGEXP", "RELAY", "RELAY_LOG_FILE", "RELAY_LOG_POS", "RELAY_THREAD",
        "RELAYLOG", "RELEASE", "RELOAD", "REMOVE", "RENAME", "REORGANIZE", "REPAIR", "REPEAT", "REPEATABLE", "REPLACE", "REPLICATE_DO_DB", "REPLICATE_DO_TABLE",
        "REPLICATE_IGNORE_DB", "REPLICATE_IGNORE_TABLE", "REPLICATE_REWRITE_DB", "REPLICATE_WILD_DO_TABLE", "REPLICATE_WILD_IGNORE_TABLE", "REPLICATION",
        "REQUIRE", "RESET", "RESIGNAL", "RESTORE", "RESTRICT", "RESUME", "RETURN", "RETURNED_SQLSTATE", "RETURNS", "REVERSE", "REVOKE", "RIGHT", "RLIKE",
        "ROLLBACK", "ROLLUP", "ROTATE", "ROUTINE", "ROW", "ROW_COUNT", "ROW_FORMAT", "ROWS", "RTREE", "SAVEPOINT", "SCHEDULE", "SCHEMA", "SCHEMA_NAME",
        "SCHEMAS", "SECOND", "SECOND_MICROSECOND", "SECURITY", "SELECT", "SENSITIVE", "SEPARATOR", "SERIAL", "SERIALIZABLE", "SERVER", "SESSION", "SET",
        "SHARE", "SHOW", "SHUTDOWN", "SIGNAL", "SIGNED", "SIMPLE", "SLAVE", "SLOW", "SMALLINT", "SNAPSHOT", "SOCKET", "SOME", "SONAME", "SOUNDS", "SOURCE",
        "SPATIAL", "SPECIFIC", "SQL", "SQL_AFTER_GTIDS", "SQL_AFTER_MTS_GAPS", "SQL_BEFORE_GTIDS", "SQL_BIG_RESULT", "SQL_BUFFER_RESULT", "SQL_CACHE",
        "SQL_CALC_FOUND_ROWS", "SQL_NO_CACHE", "SQL_SMALL_RESULT", "SQL_THREAD", "SQL_TSI_DAY", "SQL_TSI_HOUR", "SQL_TSI_MINUTE", "SQL_TSI_MONTH",
        "SQL_TSI_QUARTER", "SQL_TSI_SECOND", "SQL_TSI_WEEK", "SQL_TSI_YEAR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SSL", "STACKED", "START", "STARTING",
        "STARTS", "STATS_AUTO_RECALC", "STATS_PERSISTENT", "STATS_SAMPLE_PAGES", "STATUS", "STOP", "STORAGE", "STORED", "STRAIGHT_JOIN", "STRING",
        "SUBCLASS_ORIGIN", "SUBJECT", "SUBPARTITION", "SUBPARTITIONS", "SUPER", "SUSPEND", "SWAPS", "SWITCHES", "TABLE", "TABLE_CHECKSUM", "TABLE_NAME",
        "TABLES", "TABLESPACE", "TEMPORARY", "TEMPTABLE", "TERMINATED", "TEXT", "THAN", "THEN", "TIME", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF",
        "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRANSACTION", "TRIGGER", "TRIGGERS", "TRUE", "TRUNCATE", "TYPE", "TYPES", "UNCOMMITTED",
        "UNDEFINED", "UNDO", "UNDO_BUFFER_SIZE", "UNDOFILE", "UNICODE", "UNINSTALL", "UNION", "UNIQUE", "UNKNOWN", "UNLOCK", "UNSIGNED", "UNTIL", "UPDATE",
        "UPGRADE", "USAGE", "USE", "USE_FRM", "USER", "USER_RESOURCES", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALIDATION", "VALUE", "VALUES",
        "VARBINARY", "VARCHAR", "VARCHARACTER", "VARIABLES", "VARYING", "VIEW", "VIRTUAL", "WAIT", "WARNINGS", "WEEK", "WEIGHT_STRING", "WHEN", "WHERE",
        "WHILE", "WITH", "WITHOUT", "WORK", "WRAPPER", "WRITE", "X509", "XA", "XID", "XML", "XOR", "YEAR", "YEAR_MONTH", "ZEROFILL" };

    private static final String DB_NAME = "generate_reserved_words";

    /**
     * @since 4.3
     */
    public static void main(String[] args) throws SQLException
    {
      MysqlDataSource dataSource = new MysqlDataSource();
      dataSource.setUrl("jdbc:mysql://localhost");
      dataSource.setUser("root");
      // dataSource.setPassword("12345");

      Connection connection = dataSource.getConnection();

      try
      {
        try
        {
          DBUtil.execute(connection, "DROP DATABASE " + DB_NAME);
        }
        catch (Exception ignore)
        {
        }

        DBUtil.execute(connection, "CREATE DATABASE " + DB_NAME);
        DBUtil.execute(connection, "USE " + DB_NAME);

        generateReservedWords(connection, WORDS);
      }
      finally
      {
        connection.close();
      }
    }
  }
}
