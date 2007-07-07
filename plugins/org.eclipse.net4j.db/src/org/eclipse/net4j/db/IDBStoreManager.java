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
package org.eclipse.net4j.db;

import org.eclipse.net4j.util.store.IStoreManager;

import javax.sql.DataSource;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public interface IDBStoreManager<TRANSACTION extends IDBStoreTransaction> extends IStoreManager<TRANSACTION>
{
  public static final String STORE_TYPE = "JDBC";

  public IDBAdapter getDBAdapter();

  public DataSource getDataSource();

  public Connection getConnection();

  public void initDatabase();
}
