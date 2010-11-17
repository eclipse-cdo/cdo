package org.eclipse.net4j.db.db2;

import org.eclipse.net4j.db.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public final class DB2SchemaDataSource extends com.ibm.db2.jcc.DB2SimpleDataSource
{
  private static final long serialVersionUID = 1L;

  private String schemaName;

  public DB2SchemaDataSource(String schemaName)
  {
    this.schemaName = schemaName;
  }

  public String getSchemaName()
  {
    return schemaName;
  }

  @Override
  public Connection getConnection() throws SQLException
  {
    Connection connection = super.getConnection();
    setSchema(connection);
    return connection;
  }

  @Override
  public Connection getConnection(Object arg0) throws SQLException
  {
    Connection connection = super.getConnection(arg0);
    setSchema(connection);
    return connection;
  }

  @Override
  public Connection getConnection(String arg0, String arg1) throws SQLException
  {
    Connection connection = super.getConnection(arg0, arg1);
    setSchema(connection);
    return connection;
  }

  private void setSchema(Connection connection) throws SQLException
  {
    Statement stmt = null;

    try
    {
      stmt = connection.createStatement();
      stmt.execute("SET CURRENT SCHEMA " + schemaName); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }
}
