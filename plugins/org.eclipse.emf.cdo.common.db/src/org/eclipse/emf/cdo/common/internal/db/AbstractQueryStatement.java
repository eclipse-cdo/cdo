package org.eclipse.emf.cdo.common.internal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class AbstractQueryStatement<Result> extends AbstractDBAccessor
{
  protected abstract Result getResult(ResultSet resultSet) throws Exception;

  public Result query(Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = getPreparedStatement(connection);
    ResultSet resultSet = preparedStatement.executeQuery();
    connection.commit();
    if (resultSet.next())
    {
      Result result = getResult(resultSet);
      resultSet.close();
      return result;
    }
    else
    {
      return null;
    }
  }

}
