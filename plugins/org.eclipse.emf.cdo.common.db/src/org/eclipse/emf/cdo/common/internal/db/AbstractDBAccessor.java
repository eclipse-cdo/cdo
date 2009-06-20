package org.eclipse.emf.cdo.common.internal.db;

import org.eclipse.emf.cdo.common.internal.db.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDBAccessor
{
  static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractDBAccessor.class);

  protected PreparedStatement getPreparedStatement(Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
    setParameters(preparedStatement);
    TRACER.trace(getSQL());
    return preparedStatement;
  }

  protected abstract String getSQL();

  protected abstract void setParameters(PreparedStatement statement) throws SQLException, Exception;

}
