/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBTransaction;
import org.eclipse.net4j.internal.util.transaction.Transaction;
import org.eclipse.net4j.util.transaction.TransactionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class DBTransaction extends Transaction implements IDBTransaction
{
  private Connection connection;

  public DBTransaction(Connection connection) throws DBException
  {
    try
    {
      this.connection = connection;
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public Connection getConnection()
  {
    return connection;
  }

  @Override
  public void commit() throws TransactionException
  {
    try
    {
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new TransactionException(ex);
    }

    super.commit();
  }

  @Override
  public void rollback() throws TransactionException
  {
    try
    {
      connection.rollback();
    }
    catch (SQLException ex)
    {
      throw new TransactionException(ex);
    }

    super.rollback();
  }
}
