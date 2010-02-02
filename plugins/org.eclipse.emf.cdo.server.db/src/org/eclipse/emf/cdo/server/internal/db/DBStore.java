/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 259402
 *    Stefan Winkler - Bug 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - Bug 289056: [DB] Exception "ERROR: relation "cdo_external_refs" does not exist" while executing test-suite for PostgreSQL
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IExternalReferenceManager;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;
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
  public static final String TYPE = "db"; //$NON-NLS-1$

  private long creationTime;

  private boolean firstTime;

  private IMappingStrategy mappingStrategy;

  private IDBSchema dbSchema;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  private IMetaDataManager metaDataManager;

  private IExternalReferenceManager.Internal externalReferenceManager;

  @ExcludeFromDump
  private transient ProgressDistributor accessorWriteDistributor = new ProgressDistributor.Geometric()
  {
    @Override
    public String toString()
    {
      String result = "accessorWriteDistributor"; //$NON-NLS-1$
      if (getRepository() != null)
      {
        result += ": " + getRepository().getName(); //$NON-NLS-1$
      }

      return result;
    }
  };

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  public DBStore()
  {
    super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
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

  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  public IExternalReferenceManager getExternalReferenceManager()
  {
    return externalReferenceManager;
  }

  @Override
  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    if (mappingStrategy.hasDeltaSupport())
    {
      return set(ChangeFormat.DELTA);
    }

    return set(ChangeFormat.REVISION);
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
      throw new DBException("No connection from connection provider: " + dbConnectionProvider); //$NON-NLS-1$
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
    checkNull(mappingStrategy, Messages.getString("DBStore.2")); //$NON-NLS-1$
    checkNull(dbAdapter, Messages.getString("DBStore.1")); //$NON-NLS-1$
    checkNull(dbConnectionProvider, Messages.getString("DBStore.0")); //$NON-NLS-1$

    checkState(getRevisionTemporality() == RevisionTemporality.AUDITING == mappingStrategy.hasAuditSupport(), Messages
        .getString("DBStore.7")); //$NON-NLS-1$
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

    externalReferenceManager = createExternalReferenceManager();
    externalReferenceManager.setStore(this);
    LifecycleUtil.activate(externalReferenceManager);
  }

  protected void firstStart(Connection connection)
  {
    creationTime = getRepository().getTimeStamp();
    firstTime = true;

    DBUtil.insertRow(connection, dbAdapter, CDODBSchema.REPOSITORY, creationTime, 1, creationTime, 0, CRASHED_OID,
        CRASHED_OID, CRASHED_BRANCHID);
    OM.LOG.info(MessageFormat.format(Messages.getString("DBStore.8"), creationTime)); //$NON-NLS-1$
  }

  protected void reStart(Connection connection)
  {
    creationTime = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_CREATED);
    long lastMetaId = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_LAST_METAID);
    long lastObjectID = DBUtil.selectMaximumLong(connection, CDODBSchema.REPOSITORY_LAST_CDOID);
    int lastBranchID = DBUtil.selectMaximumInt(connection, CDODBSchema.BRANCHES_ID);

    if (lastObjectID == CRASHED_OID || getLastMetaID() == CRASHED_OID || getLastBranchID() == CRASHED_BRANCHID)
    {
      OM.LOG.info(Messages.getString("DBStore.9")); //$NON-NLS-1$
      lastObjectID = mappingStrategy.repairAfterCrash(dbAdapter, connection);
      lastMetaId = DBUtil.selectMaximumLong(connection, CDODBSchema.PACKAGE_INFOS_META_UB);
      lastBranchID = DBUtil.selectMaximumInt(connection, CDODBSchema.BRANCHES_ID);
      OM.LOG.info(MessageFormat.format(Messages.getString("DBStore.10"), lastObjectID, lastMetaId)); //$NON-NLS-1$
    }

    setLastMetaID(lastMetaId);
    setLastObjectID(lastObjectID);
    setLastBranchID(lastBranchID);

    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_STARTS);
    builder.append("="); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_STARTS);
    builder.append("+1, "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_STARTED);
    builder.append("="); //$NON-NLS-1$
    builder.append(getRepository().getTimeStamp());
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_STOPPED);
    builder.append("=0, "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_LAST_CDOID);
    builder.append("="); //$NON-NLS-1$
    builder.append(CRASHED_OID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_LAST_METAID);
    builder.append("="); //$NON-NLS-1$
    builder.append(CRASHED_OID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.REPOSITORY_LAST_BRANCHID);
    builder.append("="); //$NON-NLS-1$
    builder.append(CRASHED_BRANCHID);

    String sql = builder.toString();
    int count = DBUtil.update(connection, sql);
    if (count == 0)
    {
      throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    Connection connection = null;

    LifecycleUtil.deactivate(metaDataManager);
    metaDataManager = null;

    LifecycleUtil.deactivate(externalReferenceManager);
    externalReferenceManager = null;

    try
    {
      connection = getConnection();

      LifecycleUtil.deactivate(mappingStrategy);

      StringBuilder builder = new StringBuilder();
      builder.append("UPDATE "); //$NON-NLS-1$
      builder.append(CDODBSchema.REPOSITORY);
      builder.append(" SET "); //$NON-NLS-1$
      builder.append(CDODBSchema.REPOSITORY_STOPPED);
      builder.append("="); //$NON-NLS-1$
      builder.append(getRepository().getTimeStamp());
      builder.append(", "); //$NON-NLS-1$
      builder.append(CDODBSchema.REPOSITORY_LAST_CDOID);
      builder.append("="); //$NON-NLS-1$
      builder.append(getLastObjectID());
      builder.append(", "); //$NON-NLS-1$
      builder.append(CDODBSchema.REPOSITORY_LAST_METAID);
      builder.append("="); //$NON-NLS-1$
      builder.append(getLastMetaID());
      builder.append(", "); //$NON-NLS-1$
      builder.append(CDODBSchema.REPOSITORY_LAST_BRANCHID);
      builder.append("="); //$NON-NLS-1$
      builder.append(getLastBranchID());

      String sql = builder.toString();
      int count = DBUtil.update(connection, sql);
      if (count == 0)
      {
        throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY); //$NON-NLS-1$
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

  protected IExternalReferenceManager.Internal createExternalReferenceManager()
  {
    return new ExternalReferenceManager();
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }
}
