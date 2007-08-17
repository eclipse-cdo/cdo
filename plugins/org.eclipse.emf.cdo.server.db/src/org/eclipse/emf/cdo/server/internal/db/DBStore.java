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
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;

import javax.sql.DataSource;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class DBStore extends Store implements IDBStore
{
  private static final String TYPE = "db";

  private IMappingStrategy mappingStrategy;

  private IDBAdapter dbAdapter;

  private DataSource dataSource;

  private int nextPackageID;

  private int nextClassID;

  private int nextFeatureID;

  public DBStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter, DataSource dataSource)
  {
    super(TYPE);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("dbAdapter is null");
    }

    if (dataSource == null)
    {
      throw new IllegalArgumentException("dataSource is null");
    }

    this.mappingStrategy = mappingStrategy;
    this.dbAdapter = dbAdapter;
    this.dataSource = dataSource;
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
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

  public int getNextPackageID()
  {
    return nextPackageID++;
  }

  public int getNextClassID()
  {
    return nextClassID++;
  }

  public int getNextFeatureID()
  {
    return nextFeatureID++;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    CDODBSchema.INSTANCE.create(dbAdapter, dataSource);
    Connection connection = null;

    try
    {
      connection = dataSource.getConnection();
      nextPackageID = DBUtil.selectMaximum(connection, CDODBSchema.PACKAGES_ID) + 1;
      nextClassID = DBUtil.selectMaximum(connection, CDODBSchema.CLASSES_ID) + 1;
      nextFeatureID = DBUtil.selectMaximum(connection, CDODBSchema.FEATURES_ID) + 1;
    }
    finally
    {
      DBUtil.close(connection);
    }
  }
}
