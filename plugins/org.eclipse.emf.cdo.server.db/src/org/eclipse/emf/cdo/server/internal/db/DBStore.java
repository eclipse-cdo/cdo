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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.Store;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.ConnectionProvider;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.internal.db.DBSchema;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class DBStore extends Store implements IDBStore
{
  private static final String TYPE = "db";

  private IMappingStrategy mappingStrategy;

  private IDBAdapter dbAdapter;

  private ConnectionProvider connectionProvider;

  private IDBSchema schema;

  private int nextPackageID;

  private int nextClassID;

  private int nextFeatureID;

  public DBStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter, ConnectionProvider connectionProvider)
  {
    super(TYPE);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("dbAdapter is null");
    }

    if (connectionProvider == null)
    {
      throw new IllegalArgumentException("connectionProvider is null");
    }

    this.mappingStrategy = mappingStrategy;
    this.dbAdapter = dbAdapter;
    this.connectionProvider = connectionProvider;
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public ConnectionProvider getConnectionProvider()
  {
    return connectionProvider;
  }

  public IDBSchema getSchema()
  {
    if (schema == null)
    {
      schema = createSchema();
    }

    return schema;
  }

  public boolean hasAuditingSupport()
  {
    return true;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasEfficientTypeLookup()
  {
    return mappingStrategy.hasEfficientTypeLookup();
  }

  public DBStoreAccessor getReader(ISession session) throws DBException
  {
    return new DBStoreAccessor(this, session);
  }

  public DBStoreAccessor getWriter(IView view) throws DBException
  {
    return new DBStoreAccessor(this, view);
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
    CDODBSchema.INSTANCE.create(dbAdapter, connectionProvider);
    DBStoreAccessor writer = getWriter(null);
    Connection connection = writer.getConnection();

    try
    {
      nextPackageID = DBUtil.selectMaximum(connection, CDODBSchema.PACKAGES_ID) + 1;
      nextClassID = DBUtil.selectMaximum(connection, CDODBSchema.CLASSES_ID) + 1;
      nextFeatureID = DBUtil.selectMaximum(connection, CDODBSchema.FEATURES_ID) + 1;

      writer.release();
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }
}
