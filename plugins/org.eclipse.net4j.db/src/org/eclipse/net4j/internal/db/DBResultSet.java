/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBResultSet;
import org.eclipse.net4j.db.jdbc.DelegatingResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public final class DBResultSet extends DelegatingResultSet implements IDBResultSet
{
  public DBResultSet(ResultSet delegate, IDBPreparedStatement preparedStatement)
  {
    super(delegate, preparedStatement);
  }

  @Override
  public IDBPreparedStatement getStatement() throws SQLException
  {
    return (IDBPreparedStatement)super.getStatement();
  }
}
