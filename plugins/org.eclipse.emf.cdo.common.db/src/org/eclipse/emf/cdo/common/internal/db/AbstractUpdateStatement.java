package org.eclipse.emf.cdo.common.internal.db;

import org.eclipse.net4j.db.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;

public abstract class AbstractUpdateStatement extends AbstractDBAccessor
{
  public void update(Connection connection) throws Exception
  {

    PreparedStatement preparedStatement = getPreparedStatement(connection);
    preparedStatement.executeUpdate();
    if (preparedStatement.getUpdateCount() == 0)
    {
      throw new DBException(MessageFormat.format("No row inserted by statement \"{0}\"", getSQL()));
    }
    connection.commit();
  }

  @Override
  protected void setParameters(PreparedStatement preparedStatement) throws Exception
  {
  }
}
