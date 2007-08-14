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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.Store;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBAdapter;

import javax.sql.DataSource;

/**
 * @author Eike Stepper
 */
public class DBStore extends Store
{
  private static final String TYPE = "db";

  private IDBAdapter dbAdapter;

  private DataSource dataSource;

  public DBStore(IDBAdapter dbAdapter, DataSource dataSource)
  {
    super(TYPE);
    this.dbAdapter = dbAdapter;
    this.dataSource = dataSource;
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public IStoreReader getReader() throws DBException
  {
    return new DBStoreReader(this);
  }

  public IStoreWriter getWriter(IView view) throws DBException
  {
    return new DBStoreWriter(this, view);
  }
}
