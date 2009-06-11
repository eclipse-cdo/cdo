/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.internal.db;

import org.eclipse.emf.cdo.common.internal.db.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractPreparedStatementFactory<T> implements IPreparedStatementFactory<T>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractPreparedStatementFactory.class);

  private PreparedStatement preparedStatement;

  protected PreparedStatement getPreparedStatement(Connection connection) throws SQLException
  {
    if (preparedStatement == null || preparedStatement.getConnection() == null
        || preparedStatement.getConnection().isClosed() || !preparedStatement.getConnection().equals(connection))
    {
      preparedStatement = createPreparedStatement(getSqlStatement(), connection);
    }
    return preparedStatement;
  }

  public void close()
  {
    if (preparedStatement != null)
    {
      try
      {
        preparedStatement.close();
      }
      catch (SQLException ex)
      {
        TRACER.format("Exception occured while closing preparedStatement \"{0}\"", getSqlStatement());
      }
    }
  }

  private PreparedStatement createPreparedStatement(String sql, Connection connection) throws SQLException
  {
    return connection.prepareStatement(sql);
  }

  public PreparedStatement getPreparedStatement(T t, Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = getPreparedStatement(connection);
    doSetParameters(t, preparedStatement);
    TRACER.trace(getSqlStatement());
    return preparedStatement;
  }

  protected abstract String getSqlStatement();

  protected abstract void doSetParameters(T t, PreparedStatement preparedStatement) throws Exception;
}