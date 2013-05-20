/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractUpdateStatement extends AbstractDBAccessor
{
  public AbstractUpdateStatement()
  {
  }

  public void update(Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = null;
    try
    {
      preparedStatement = getPreparedStatement(connection);
      preparedStatement.executeUpdate();
      if (preparedStatement.getUpdateCount() == 0)
      {
        throw new DBException(MessageFormat.format("No row inserted by statement \"{0}\"", getSQL()));
      }

      connection.commit();
    }
    finally
    {
      DBUtil.close(preparedStatement);
    }
  }

  @Override
  protected void setParameters(PreparedStatement preparedStatement) throws Exception
  {
  }
}
