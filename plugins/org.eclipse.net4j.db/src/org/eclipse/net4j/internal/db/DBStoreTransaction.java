/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBStoreTransaction;
import org.eclipse.net4j.internal.util.transaction.StoreTransaction;
import org.eclipse.net4j.util.transaction.IStoreManager;
import org.eclipse.net4j.util.transaction.IStoreTransaction;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class DBStoreTransaction extends StoreTransaction implements IDBStoreTransaction
{
  private Connection connection;

  public DBStoreTransaction(IStoreManager<? extends IStoreTransaction> storeManager, Connection connection)
  {
    super(storeManager);
    this.connection = connection;
  }

  public Connection getConnection()
  {
    return connection;
  }

  public void dispose()
  {
    connection = null;
  }
}