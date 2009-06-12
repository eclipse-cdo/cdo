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
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractPreparedStatementFactory implements IPreparedStatementFactory
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractPreparedStatementFactory.class);

  private PreparedStatement preparedStatement;

  public AbstractPreparedStatementFactory()
  {
  }

  public PreparedStatement getPreparedStatement(InternalCDORevision revision, Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = getPreparedStatement(connection);
    setParameters(revision, preparedStatement);
    TRACER.trace(getSQL());
    return preparedStatement;
  }

  public void close()
  {
    DBUtil.close(preparedStatement);
  }

  protected synchronized PreparedStatement getPreparedStatement(Connection connection) throws SQLException
  {
    if (preparedStatement == null)
    {
      preparedStatement = createPreparedStatement(getSQL(), connection);
    }
    else
    {
      CheckUtil.checkArg(preparedStatement.getConnection() == connection, "Wrong connection");
    }

    return preparedStatement;
  }

  protected abstract String getSQL();

  protected abstract void setParameters(InternalCDORevision revision, PreparedStatement statement) throws Exception;

  private PreparedStatement createPreparedStatement(String sql, Connection connection) throws SQLException
  {
    return connection.prepareStatement(sql);
  }
}
