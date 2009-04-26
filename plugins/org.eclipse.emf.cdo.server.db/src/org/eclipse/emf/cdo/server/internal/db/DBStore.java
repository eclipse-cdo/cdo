/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStore extends LongIDStore implements IDBStore
{
  public static final String TYPE = "db";

  private long creationTime;

  private boolean firstTime;

  private IMappingStrategy mappingStrategy;

  private IDBSchema dbSchema;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  @ExcludeFromDump
  private transient ProgressDistributor accessorWriteDistributor = new ProgressDistributor.Geometric()
  {
    @Override
    public String toString()
    {
      String result = "accessorWriteDistributor";
      if (getRepository() != null)
      {
        result += ": " + getRepository().getName();
      }

      return result;
    }
  };

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  private transient IMetaDataManager metaDataManager;

  public DBStore()
  {
    super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE));
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
    mappingStrategy.setStore(this);

    setRevisionTemporality(mappingStrategy.hasAuditSupport() ? RevisionTemporality.AUDITING : RevisionTemporality.NONE);
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public void setDBAdapter(IDBAdapter dbAdapter)
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

  public ProgressDistributor getAccessorWriteDistributor()
  {
    return accessorWriteDistributor;
  }

  public IDBSchema getDBSchema()
  {
    return dbSchema;
  }

  @Override
  public DBStoreAccessor getReader(ISession session)
  {
    return (DBStoreAccessor)super.getReader(session);
  }

  @Override
  public DBStoreAccessor getWriter(ITransaction transaction)
  {
    return (DBStoreAccessor)super.getWriter(transaction);
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
  protected DBStoreAccessor createReader(ISession session) throws DBException
  {
    return new DBStoreAccessor(this, session);
  }

  @Override
  protected DBStoreAccessor createWriter(ITransaction transaction) throws DBException
  {
    return new DBStoreAccessor(this, transaction);
  }

  protected Connection getConnection()
  {
    Connection connection = dbConnectionProvider.getConnection();

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + dbConnectionProvider);
    }

    return connection;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public boolean isFirstTime()
  {
    return firstTime;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(mappingStrategy, "mappingStrategy is null");
    checkNull(dbAdapter, "dbAdapter is null");
    checkNull(dbConnectionProvider, "dbConnectionProvider is null");

    checkState(getRevisionTemporality() == RevisionTemporality.AUDITING == mappingStrategy.hasAuditSupport(),
        "AuditSupport of MappingStrategy and Store does not match. Please check configuration.");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    dbSchema = createSchema();
    metaDataManager = new MetaDataManager(this);
    LifecycleUtil.activate(metaDataManager);

    Connection connection = getConnection();
    LifecycleUtil.activate(mappingStrategy);

    try
    {
      Set<IDBTable> createdTables = CDODBSchema.INSTANCE.create(dbAdapter, connection);
      if (createdTables.contains(CDODBSchema.REPOSITORY))
      {
        firstStart(connection);
      }
      else
      {
        reStart(connection);
      }

      connection.commit();
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  protected void firstStart(Connection connection)
  {
    creationTime = getStartupTime();
    firstTime = true;

    DBUtil.insertRow(connection, dbAdapter, CDODBSchema.REPOSITORY, creationTime, 1, creationTime, 0, CRASHED, CRASHED);
    OM.LOG.info(MessageFormat.format("First start: {0,date} {0,time}", creationTime));
  }

  protected void reStart(Connection connection)
  {
    creationTime = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_CREATED);
    long lastMetaId = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_NEXT_METAID);
    long lastObjectID = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_NEXT_CDOID);

    if (lastObjectID == CRASHED || getLastMetaID() == CRASHED)
    {
      OM.LOG.info("Detected crash");
      lastObjectID = mappingStrategy.repairAfterCrash(dbAdapter, connection);
      lastMetaId = DBUtil.selectMaximumLong(connection, CDODBSchema.PACKAGE_INFOS_META_UB);
      OM.LOG.info(MessageFormat
          .format("Repaired after crash: maxObjectID={0}, maxMetaID={1}", lastObjectID, lastMetaId));
    }

    setLastMetaID(lastMetaId);
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
    builder.append(getStartupTime());
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

  @Override
  protected void doDeactivate() throws Exception
  {
    Connection connection = null;

    LifecycleUtil.deactivate(metaDataManager);
    metaDataManager = null;

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
      builder.append(getLastMetaID());

      String sql = builder.toString();
      int count = DBUtil.update(connection, sql);
      if (count == 0)
      {
        throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY);
      }

      connection.commit();
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

  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  @Override
  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    if (mappingStrategy.hasDeltaSupport())
    {
      return set(ChangeFormat.DELTA);
    }
    else
    {
      return set(ChangeFormat.REVISION);
    }
  }
}
