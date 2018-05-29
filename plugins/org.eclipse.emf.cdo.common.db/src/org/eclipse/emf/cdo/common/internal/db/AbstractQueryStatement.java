/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.internal.db;

import org.eclipse.net4j.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractQueryStatement<Result> extends AbstractDBAccessor
{
  public AbstractQueryStatement()
  {
  }

  public Result query(Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = null;

    try
    {
      preparedStatement = getPreparedStatement(connection);
      ResultSet resultSet = preparedStatement.executeQuery();
      connection.commit();
      if (resultSet.next())
      {
        Result result = getResult(resultSet);
        resultSet.close();
        return result;
      }

      return null;
    }
    finally
    {
      DBUtil.close(preparedStatement);
    }
  }

  protected abstract Result getResult(ResultSet resultSet) throws Exception;
}
