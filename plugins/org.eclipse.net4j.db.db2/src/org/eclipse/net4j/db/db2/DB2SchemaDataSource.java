/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.db2;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @deprecated As of 4.2 no longer supported because of IP issues for external build dependencies (the vendor driver libs).
 * @author Eike Stepper
 */
@Deprecated
public final class DB2SchemaDataSource
{
  public DB2SchemaDataSource(String schemaName)
  {
    throw new UnsupportedOperationException();
  }

  public String getSchemaName()
  {
    throw new UnsupportedOperationException();
  }

  public Connection getConnection() throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  public Connection getConnection(Object arg0) throws SQLException
  {
    throw new UnsupportedOperationException();
  }

  public Connection getConnection(String arg0, String arg1) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
}
