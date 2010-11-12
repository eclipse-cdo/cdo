/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
public abstract class AbstractDBAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractDBAccessor.class);

  protected PreparedStatement getPreparedStatement(Connection connection) throws Exception
  {
    PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
    setParameters(preparedStatement);
    TRACER.trace(getSQL());
    return preparedStatement;
  }

  public abstract String getSQL();

  protected abstract void setParameters(PreparedStatement statement) throws SQLException, Exception;
}
