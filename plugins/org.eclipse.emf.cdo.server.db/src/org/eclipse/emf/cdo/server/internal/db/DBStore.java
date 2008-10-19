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

import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.internal.server.LongIDStore;
import org.eclipse.emf.cdo.internal.server.StoreAccessorPool;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
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
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import javax.sql.DataSource;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStore extends LongIDStore implements IDBStore
{
  public static final String TYPE = "db";

  private long creationTime;

  private IMappingStrategy mappingStrategy;

  private IDBSchema dbSchema;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient int nextPackageID;

  @ExcludeFromDump
  private transient int nextClassID;

  @ExcludeFromDump
  private transient int nextFeatureID;

  public DBStore()
  {
    super(TYPE, set(ChangeFormat.REVISION), set(RevisionTemporality.AUDITING), set(RevisionParallelism.NONE));
    setRevisionTemporality(RevisionTemporality.AUDITING);
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
    mappingStrategy.setStore(this);
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public void setDbAdapter(IDBAdapter dbAdapter)
  {
    this.dbAdapter = dbAdapter;
  }

  public IDBConnectionProvider getDBConnectionProvider()
  {
    return dbConnectionProvider;
  }

  public void setDbConnectionProvider(IDBConnectionProvider dbConnectionProvider)
  {
    this.dbConnectionProvider = dbConnectionProvider;
  }

  public void setDataSource(DataSource dataSource)
  {
    dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
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

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return readerPool;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return writerPool;
  }

  @Override
  protected DBStoreReader createReader(ISession session) throws DBException
  {
    return new DBStoreReader(this, session);
  }

  @Override
  protected DBStoreWriter createWriter(IView view) throws DBException
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

  public Connection getConnection()
  {
    Connection connection = dbConnectionProvider.getConnection();
    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + dbConnectionProvider);
    }

    return connection;
  }

  public void repairAfterCrash()
  {
    try
    {
      DBStoreWriter storeWriter = (DBStoreWriter)getWriter(null);
      StoreThreadLocal.setStoreReader(storeWriter);

      Connection connection = storeWriter.getConnection();
      long maxObjectID = mappingStrategy.repairAfterCrash(dbAdapter, connection);
      long maxMetaID = DBUtil.selectMaximumLong(connection, CDODBSchema.PACKAGES_RANGE_UB);

      OM.LOG.info(MessageFormat.format("Repaired after crash: maxObjectID={0}, maxMetaID={1}", maxObjectID, maxMetaID));
      setLastObjectID(maxObjectID);
      setLastMetaID(maxMetaID);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(mappingStrategy, "mappingStrategy is null");
    checkNull(dbAdapter, "dbAdapter is null");
    checkNull(dbConnectionProvider, "dbConnectionProvider is null");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    long startupTime = getStartupTime();
    Connection connection = null;

    try
    {
      connection = getConnection();
      Set<IDBTable> createdTables = CDODBSchema.INSTANCE.create(dbAdapter, dbConnectionProvider);
      if (createdTables.contains(CDODBSchema.REPOSITORY))
      {
        // First start
        creationTime = startupTime;
        DBUtil.insertRow(connection, dbAdapter, CDODBSchema.REPOSITORY, creationTime, 1, startupTime, 0, CRASHED,
            CRASHED);

        IMappingStrategy mappingStrategy = getMappingStrategy();
        mappingStrategy.createResourceTables(dbAdapter, connection);
      }
      else
      {
        // Restart
        creationTime = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_CREATED);
        long lastObjectID = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_NEXT_CDOID);
        setLastMetaID(DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_NEXT_METAID));
        if (lastObjectID == CRASHED || getLastMetaID() == CRASHED)
        {
          OM.LOG.warn("Detected restart after crash");
        }

        setLastObjectID(lastObjectID);

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
        builder.append(startupTime);
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
    finally
    {
      DBUtil.close(connection);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    Connection connection = null;

    try
    {
      connection = getConnection();

      LifecycleUtil.deactivate(mappingStrategy);

      StringBuilder builder = new StringBuilder();
      builder.append("UPDATE ");
      builder.append(CDODBSchema.REPOSITORY);
      builder.append(" SET ");
      builder.append(CDODBSchema.REPOSITORY_STOPPED);
      builder.append("=");
      builder.append(getShutdownTime());
      builder.append(", ");
      builder.append(CDODBSchema.REPOSITORY_NEXT_CDOID);
      builder.append("=");
      builder.append(getLastObjectID());
      builder.append(", ");
      builder.append(CDODBSchema.REPOSITORY_NEXT_METAID);
      builder.append("=");
      builder.append(getRepository().getLastMetaID());

      String sql = builder.toString();
      int count = DBUtil.update(connection, sql);
      if (count == 0)
      {
        throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY);
      }
    }
    finally
    {
      DBUtil.close(connection);
    }

    readerPool.dispose();
    writerPool.dispose();
    super.doDeactivate();
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }

  protected long getStartupTime()
  {
    return System.currentTimeMillis();
  }

  protected long getShutdownTime()
  {
    return System.currentTimeMillis();
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
    else if (type == CDOType.STRING || type == CDOType.CUSTOM)
    {
      return DBType.VARCHAR;
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }
}
