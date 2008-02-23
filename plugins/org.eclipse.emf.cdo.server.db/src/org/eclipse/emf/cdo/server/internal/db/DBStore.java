/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.LongIDStore;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.ddl.DBSchema;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStore extends LongIDStore implements IDBStore
{
  public static final String TYPE = "db";

  private IMappingStrategy mappingStrategy;

  private IDBSchema dbSchema;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  private int nextPackageID;

  private int nextClassID;

  private int nextFeatureID;

  public DBStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter, IDBConnectionProvider dbConnectionProvider)
  {
    super(TYPE);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("dbAdapter is null");
    }

    if (dbConnectionProvider == null)
    {
      throw new IllegalArgumentException("dbConnectionProvider is null");
    }

    this.mappingStrategy = mappingStrategy;
    this.dbAdapter = dbAdapter;
    this.dbConnectionProvider = dbConnectionProvider;
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public synchronized IDBSchema getDBSchema()
  {
    // TODO Better synchronization or eager init
    if (dbSchema == null)
    {
      dbSchema = createSchema();
    }

    return dbSchema;
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public IDBConnectionProvider getDBConnectionProvider()
  {
    return dbConnectionProvider;
  }

  public boolean hasAuditingSupport()
  {
    return true;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasWriteDeltaSupport()
  {
    return false;
  }

  @Override
  public DBStoreReader getReader(ISession session)
  {
    return (DBStoreReader)super.getReader(session);
  }

  @Override
  public DBStoreReader createReader(ISession session) throws DBException
  {
    return new DBStoreReader(this, session);
  }

  @Override
  public DBStoreWriter getWriter(IView view)
  {
    return (DBStoreWriter)super.getWriter(view);
  }

  @Override
  public DBStoreWriter createWriter(IView view) throws DBException
  {
    return new DBStoreWriter(this, view);
  }

  public synchronized int getNextPackageID()
  {
    // TODO Better synchronization
    return nextPackageID++;
  }

  public synchronized int getNextClassID()
  {
    // TODO Better synchronization
    return nextClassID++;
  }

  public synchronized int getNextFeatureID()
  {
    // TODO Better synchronization
    return nextFeatureID++;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    activateOrDeactivate(true);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    activateOrDeactivate(false);
    super.doDeactivate();
  }

  protected void activateOrDeactivate(boolean started)
  {
    Repository repository = (Repository)getRepository();
    Connection connection = dbConnectionProvider.getConnection();
    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + dbConnectionProvider);
    }

    try
    {
      if (started)
      {
        activateStore(repository, connection);
      }
      else
      {
        deactivateStore(repository, connection);
      }
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  protected void activateStore(Repository repository, Connection connection)
  {
    Set<IDBTable> createdTables = CDODBSchema.INSTANCE.create(dbAdapter, dbConnectionProvider);
    if (createdTables.contains(CDODBSchema.REPOSITORY))
    {
      // First start
      DBUtil.insertRow(connection, dbAdapter, CDODBSchema.REPOSITORY, repository.getName(), repository.getUUID(), 1,
          System.currentTimeMillis(), 0, CRASHED, CRASHED);

      MappingStrategy mappingStrategy = (MappingStrategy)getMappingStrategy();

      IClassMapping resourceClassMapping = mappingStrategy.getResourceClassMapping();
      Set<IDBTable> tables = resourceClassMapping.getAffectedTables();
      if (dbAdapter.createTables(tables, connection).size() != tables.size())
      {
        throw new DBException("CDOResource tables not completely created");
      }
    }
    else
    {
      // Restart
      long lastObjectID = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_NEXT_CDOID);
      long lastMetaID = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_NEXT_METAID);
      if (lastObjectID == CRASHED || lastMetaID == CRASHED)
      {
        OM.LOG.warn("Detected restart after crash");
      }

      setLastObjectID(lastObjectID);
      repository.setLastMetaID(lastMetaID);

      StringBuilder builder = new StringBuilder();
      builder.append("UPDATE ");
      builder.append(CDODBSchema.REPOSITORY);
      builder.append(" SET ");
      builder.append(CDODBSchema.REPOSITORY_STARTS);
      builder.append("=");
      builder.append(CDODBSchema.REPOSITORY_STARTS);
      builder.append("+1, ");
      builder.append(CDODBSchema.REPOSITORY_STARTED);
      builder.append("=");
      builder.append(System.currentTimeMillis());
      builder.append(", ");
      builder.append(CDODBSchema.REPOSITORY_STOPPED);
      builder.append("=0, ");
      builder.append(CDODBSchema.REPOSITORY_NEXT_CDOID);
      builder.append("=");
      builder.append(CRASHED);
      builder.append(", ");
      builder.append(CDODBSchema.REPOSITORY_NEXT_METAID);
      builder.append("=");
      builder.append(CRASHED);

      String sql = builder.toString();
      int count = DBUtil.update(connection, sql);
      if (count == 0)
      {
        throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY);
      }
    }

    nextPackageID = DBUtil.selectMaximumInt(connection, CDODBSchema.PACKAGES_ID) + 1;
    nextClassID = DBUtil.selectMaximumInt(connection, CDODBSchema.CLASSES_ID) + 1;
    nextFeatureID = DBUtil.selectMaximumInt(connection, CDODBSchema.FEATURES_ID) + 1;
    LifecycleUtil.activate(mappingStrategy);
  }

  protected void deactivateStore(Repository repository, Connection connection)
  {
    LifecycleUtil.deactivate(mappingStrategy);
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(CDODBSchema.REPOSITORY);
    builder.append(" SET ");
    builder.append(CDODBSchema.REPOSITORY_STOPPED);
    builder.append("=");
    builder.append(System.currentTimeMillis());
    builder.append(", ");
    builder.append(CDODBSchema.REPOSITORY_NEXT_CDOID);
    builder.append("=");
    builder.append(getLastObjectID());
    builder.append(", ");
    builder.append(CDODBSchema.REPOSITORY_NEXT_METAID);
    builder.append("=");
    builder.append(repository.getLastMetaID());

    String sql = builder.toString();
    int count = DBUtil.update(connection, sql);
    if (count == 0)
    {
      throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY);
    }
  }

  public void repairAfterCrash()
  {
    Repository repository = (Repository)getRepository();
    DBStoreReader storeReader = getReader(null);
    StoreUtil.setReader(storeReader);

    try
    {
      Connection connection = storeReader.getConnection();
      long maxObjectID = mappingStrategy.repairAfterCrash(connection);
      long maxMetaID = DBUtil.selectMaximumLong(connection, CDODBSchema.PACKAGES_RANGE_UB);

      OM.LOG.info(MessageFormat.format("Repaired after crash: maxObjectID={0}, maxMetaID={1}", maxObjectID, maxMetaID));
      setLastObjectID(maxObjectID);
      repository.setLastMetaID(maxMetaID);
    }
    finally
    {
      storeReader.release();
      StoreUtil.setReader(null);
    }
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }

  public static DBType getDBType(CDOType type)
  {
    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return DBType.BOOLEAN;
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return DBType.CHAR;
    }
    else if (type == CDOType.DATE)
    {
      return DBType.DATE;
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return DBType.DOUBLE;
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return DBType.FLOAT;
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return DBType.INTEGER;
    }
    else if (type == CDOType.LONG || type == CDOType.LONG_OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.STRING)
    {
      return DBType.VARCHAR;
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }
}
