/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019-2023 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOCommonRepository.CommitInfoStorage;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.LobsTable;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PropertiesTable;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.MappingNames;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.UnitMappingTable;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalRepository.PostActivateable;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.ddl.DBField;
import org.eclipse.net4j.internal.db.ddl.DBTable;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor.Geometric;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

/**
 * @author Eike Stepper
 */
public class DBStore extends Store implements IDBStore, CDOAllRevisionsProvider, PostActivateable
{
  public static final String TYPE = "db"; //$NON-NLS-1$

  public static final int SCHEMA_VERSION = 4;

  // public static final int SCHEMA_VERSION = 3; // Bug 404047: Indexed columns must be NOT NULL.
  // public static final int SCHEMA_VERSION = 2; // Bug 344232: Rename cdo_lobs.size to cdo_lobs.lsize.
  // public static final int SCHEMA_VERSION = 1; // Bug 351068: Delete detached objects from non-auditing stores.

  private static final int FIRST_START = -1;

  private static final String PROP_SCHEMA_VERSION = "org.eclipse.emf.cdo.server.db.schemaVersion"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_CREATED = "org.eclipse.emf.cdo.server.db.repositoryCreated"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_STOPPED = "org.eclipse.emf.cdo.server.db.repositoryStopped"; //$NON-NLS-1$

  private static final String PROP_NEXT_LOCAL_CDOID = "org.eclipse.emf.cdo.server.db.nextLocalCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_CDOID = "org.eclipse.emf.cdo.server.db.lastCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_BRANCHID = "org.eclipse.emf.cdo.server.db.lastBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_LOCAL_BRANCHID = "org.eclipse.emf.cdo.server.db.lastLocalBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_COMMITTIME = "org.eclipse.emf.cdo.server.db.lastCommitTime"; //$NON-NLS-1$

  private static final String PROP_LAST_NONLOCAL_COMMITTIME = "org.eclipse.emf.cdo.server.db.lastNonLocalCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.db.gracefullyShutDown"; //$NON-NLS-1$

  private static final int DEFAULT_CONNECTION_RETRY_COUNT = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.server.db.DEFAULT_CONNECTION_RETRY_COUNT", 0);

  private static final int DEFAULT_CONNECTION_RETRY_SECONDS = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.server.db.DEFAULT_CONNECTION_RETRY_SECONDS",
      30);

  private static final boolean DEFAULT_PREPEND_SCHEMA_NAME = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.db.DEFAULT_PREPEND_SCHEMA_NAME");

  private static final boolean DEFAULT_CREATE_SCHEMA_IF_NEEDED = OMPlatform.INSTANCE
      .isProperty("org.eclipse.emf.cdo.server.db.DEFAULT_CREATE_SCHEMA_IF_NEEDED");

  private static final boolean SHOW_SCHEMA_CREATION_EXCEPTION = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.db.SHOW_SCHEMA_CREATION_EXCEPTION");

  private static final boolean DISABLE_LOG_OTHER_SCHEMA_INFO = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.db.DISABLE_LOG_OTHER_SCHEMA_INFO");

  private long creationTime;

  private boolean firstTime;

  private Map<String, String> properties;

  private int idColumnLength = IDBField.DEFAULT;

  private int jdbcFetchSize = 100000;

  private IIDHandler idHandler;

  private IMetaDataManager metaDataManager = createMetaDataManager();

  private DurableLockingManager durableLockingManager = createDurableLockingManager();

  private DBStoreTables tables;

  private CommitInfoTable commitInfoTable;

  private UnitMappingTable unitMappingTable;

  private IMappingStrategy mappingStrategy;

  private IDBDatabase database;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  @ExcludeFromDump
  private transient ProgressDistributor accessorWriteDistributor = createProgressDistributor();

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = createReaderPool();

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = createWriterPool();

  @ExcludeFromDump
  private transient Timer connectionKeepAliveTimer;

  public DBStore()
  {
    super(TYPE, null, set(ChangeFormat.REVISION, ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
  }

  @Override
  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
    mappingStrategy.setStore(this);
  }

  @Override
  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public void setDBAdapter(IDBAdapter dbAdapter)
  {
    this.dbAdapter = dbAdapter;
  }

  public void setProperties(Map<String, String> properties)
  {
    checkInactive();
    this.properties = properties;
  }

  @Override
  public Map<String, String> getProperties()
  {
    return properties;
  }

  private int getProperty(String key, int defaultValue)
  {
    if (properties != null)
    {
      String value = properties.get(key);
      if (value != null)
      {
        return Integer.parseInt(value);
      }
    }

    return defaultValue;
  }

  @Override
  public int getJDBCFetchSize()
  {
    return jdbcFetchSize;
  }

  @Override
  public int getIDColumnLength()
  {
    return idColumnLength;
  }

  @Override
  public IIDHandler getIDHandler()
  {
    return idHandler;
  }

  @Override
  public IDBDatabase getDatabase()
  {
    return database;
  }

  @Override
  public Connection getConnection()
  {
    Connection connection = dbConnectionProvider.getConnection();
    if (connection == null)
    {
      throw new DBException("No connection from connection provider: " + dbConnectionProvider); //$NON-NLS-1$
    }

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex, "SET AUTO COMMIT = false");
    }

    return connection;
  }

  public Connection getConnectionOrRetry()
  {
    RuntimeException exception = null;
    int attempts = 1 + getProperty(Props.CONNECTION_RETRY_COUNT, DEFAULT_CONNECTION_RETRY_COUNT);
    int seconds = getProperty(Props.CONNECTION_RETRY_SECONDS, DEFAULT_CONNECTION_RETRY_SECONDS);
    if (seconds < 1)
    {
      attempts = 1;
    }

    for (int i = 0; i < attempts; i++)
    {
      if (i != 0)
      {
        OM.LOG.info("Database connection could not be established. Next attempt is scheduled in " + seconds + " seconds...");
        ConcurrencyUtil.sleep(1000L * seconds);
      }

      try
      {
        Connection connection = getConnection();
        if (connection != null)
        {
          return connection;
        }
      }
      catch (RuntimeException ex)
      {
        if (exception == null)
        {
          exception = ex;
        }
      }
    }

    String message = "Database connection could not be established after " + attempts + " attempts";
    if (exception != null)
    {
      throw new DBException(message, exception);
    }

    throw new DBException(message);
  }

  public void setDBConnectionProvider(IDBConnectionProvider dbConnectionProvider)
  {
    this.dbConnectionProvider = dbConnectionProvider;
  }

  @Override
  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  public DurableLockingManager getDurableLockingManager()
  {
    return durableLockingManager;
  }

  public CommitInfoTable getCommitInfoTable()
  {
    return commitInfoTable;
  }

  public UnitMappingTable getUnitMappingTable()
  {
    return unitMappingTable;
  }

  public Timer getConnectionKeepAliveTimer()
  {
    return connectionKeepAliveTimer;
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

  @Override
  public IDBSchema getDBSchema()
  {
    return database.getSchema();
  }

  public DBStoreTables tables()
  {
    return tables;
  }

  @Override
  public void visitAllTables(Connection connection, IDBStore.TableVisitor visitor)
  {
    IDBSchema schema = getDBSchema();
    String schemaName = schema.getName();
    boolean qualifiedTableNames = schema.isQualifiedTableNames();
    boolean caseSensitive = dbAdapter.isCaseSensitive();

    for (String name : DBUtil.getAllTableNames(connection, schemaName, caseSensitive))
    {
      name = DBUtil.quoted(name);

      if (schemaName != null && qualifiedTableNames)
      {
        name = DBUtil.quoted(schemaName) + '.' + name;
      }

      try
      {
        visitor.visitTable(connection, name);
        connection.commit();
      }
      catch (SQLException ex)
      {
        try
        {
          connection.rollback();
        }
        catch (SQLException ex1)
        {
          throw new DBException(ex1);
        }

        if (!dbAdapter.isColumnNotFoundException(ex))
        {
          throw new DBException(ex);
        }
      }
    }
  }

  @Override
  public Map<String, String> getPersistentProperties(Set<String> names)
  {
    return tables.properties().getPersistentProperties(names);
  }

  @Override
  public void setPersistentProperties(Map<String, String> properties)
  {
    tables.properties().setPersistentProperties(properties);
  }

  public void putPersistentProperty(String key, String value)
  {
    Map<String, String> map = new HashMap<>();
    map.put(key, value);

    setPersistentProperties(map);
  }

  @Override
  public void removePersistentProperties(Set<String> names)
  {
    tables.properties().removePersistentProperties(names);
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

  @Override
  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    final Map<CDOBranch, List<CDORevision>> result = new HashMap<>();

    InternalSession session = null;
    if (!StoreThreadLocal.hasSession())
    {
      session = getRepository().getSessionManager().openSession(null);
      StoreThreadLocal.setSession(session);
    }

    try
    {
      StoreThreadLocal.getAccessor().handleRevisions(null, null, CDOBranchPoint.UNSPECIFIED_DATE, true,
          new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
          {
            @Override
            public boolean handleRevision(CDORevision revision)
            {
              CDOBranch branch = revision.getBranch();
              List<CDORevision> list = result.get(branch);
              if (list == null)
              {
                list = new ArrayList<>();
                result.put(branch, list);
              }

              list.add(revision);
              return true;
            }
          }));
    }
    finally
    {
      if (session != null)
      {
        StoreThreadLocal.release();
        session.close();
      }
    }

    return result;
  }

  @Override
  public CDOID createObjectID(String val)
  {
    return idHandler.createCDOID(val);
  }

  @Override
  @Deprecated
  public boolean isLocal(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  public CDOID getNextCDOID(LongIDStoreAccessor accessor, CDORevision revision)
  {
    return idHandler.getNextCDOID(revision);
  }

  @Override
  public long getCreationTime()
  {
    return creationTime;
  }

  @Override
  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;

    Map<String, String> map = new HashMap<>();
    map.put(PROP_REPOSITORY_CREATED, Long.toString(creationTime));
    setPersistentProperties(map);
  }

  @Override
  public boolean isFirstStart()
  {
    return firstTime;
  }

  @Override
  public void doPostActivate(InternalSession session)
  {
    // if (OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.db.MIGRATE_WRONG_CONTAINERS"))
    // {
    // DBStoreAccessor reader = getReader(session);
    // IDBConnection connection = reader.getDBConnection();
    //
    // try
    // {
    // Map<EClass, IClassMapping> classMappings = mappingStrategy.getClassMappings();
    //
    // for (IClassMapping classMapping : classMappings.values())
    // {
    // // ...
    // }
    //
    // connection.commit();
    // }
    // catch (SQLException ex)
    // {
    // throw new DBException(ex);
    // }
    // finally
    // {
    // DBUtil.close(connection);
    // }
    // }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(mappingStrategy, Messages.getString("DBStore.2")); //$NON-NLS-1$
    checkNull(dbAdapter, Messages.getString("DBStore.1")); //$NON-NLS-1$
    checkNull(dbConnectionProvider, Messages.getString("DBStore.0")); //$NON-NLS-1$

    if (properties == null)
    {
      properties = new HashMap<>();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    InternalRepository repository = getRepository();
    IDGenerationLocation idGenerationLocation = repository.getIDGenerationLocation();
    if (idGenerationLocation == IDGenerationLocation.CLIENT)
    {
      idHandler = new UUIDHandler(this);
    }
    else
    {
      idHandler = new LongIDHandler(this);
    }

    setObjectIDTypes(idHandler.getObjectIDTypes());
    connectionKeepAliveTimer = new Timer("Connection-Keep-Alive-" + this); //$NON-NLS-1$

    if (properties != null)
    {
      if (idGenerationLocation == IDGenerationLocation.CLIENT)
      {
        String prop = properties.get(Props.ID_COLUMN_LENGTH);
        if (prop != null)
        {
          idColumnLength = Integer.parseInt(prop);
        }
      }

      configureAccessorPool(readerPool, Props.READER_POOL_CAPACITY);
      configureAccessorPool(writerPool, Props.WRITER_POOL_CAPACITY);

      String prop = properties.get(Props.DROP_ALL_DATA_ON_ACTIVATE);
      if (prop != null)
      {
        setDropAllDataOnActivate(Boolean.parseBoolean(prop));
      }

      prop = properties.get(Props.JDBC_FETCH_SIZE);
      if (prop != null)
      {
        jdbcFetchSize = Integer.parseInt(prop);
      }
    }

    Connection connection = getConnectionOrRetry();
    String schemaName = getSchemaName(connection);
    boolean prependSchemaName = isPrependSchemaName();
    int schemaVersion;

    try
    {
      if (isDropAllDataOnActivate())
      {
        OM.LOG.info("Dropping all tables from repository " + repository.getName() + "...");
        DBUtil.dropAllTables(connection, schemaName);
        connection.commit();
      }

      if (isCreateSchemaIfNeeded())
      {
        try
        {
          dbAdapter.createSchema(connection, schemaName);
        }
        catch (DBException ex)
        {
          if (SHOW_SCHEMA_CREATION_EXCEPTION)
          {
            OM.LOG.error(ex);
          }
        }
      }

      schemaVersion = selectSchemaVersion(connection, prependSchemaName ? schemaName : null);
      if (0 <= schemaVersion && schemaVersion < SCHEMA_VERSION)
      {
        migrateSchema(schemaVersion);
      }

      connection.commit();
    }
    finally
    {
      DBUtil.close(connection);
    }

    boolean fixNullableIndexColumns = schemaVersion != FIRST_START && schemaVersion < FIRST_VERSION_WITH_NULLABLE_CHECKS;
    database = openDatabase(dbAdapter, dbConnectionProvider, schemaName, prependSchemaName, fixNullableIndexColumns);

    tables = new DBStoreTables(this);
    tables.activate();

    LifecycleUtil.activate(idHandler);
    LifecycleUtil.activate(metaDataManager);
    LifecycleUtil.activate(durableLockingManager);
    LifecycleUtil.activate(mappingStrategy);

    if (repository.getCommitInfoStorage() != CommitInfoStorage.NO)
    {
      commitInfoTable = new CommitInfoTable(this);
      commitInfoTable.activate();
    }

    if (repository.isSupportingUnits())
    {
      unitMappingTable = new UnitMappingTable(this);
      unitMappingTable.activate();
    }

    setRevisionTemporality(mappingStrategy.hasAuditSupport() ? RevisionTemporality.AUDITING : RevisionTemporality.NONE);
    setRevisionParallelism(mappingStrategy.hasBranchingSupport() ? RevisionParallelism.BRANCHING : RevisionParallelism.NONE);

    if (schemaVersion == FIRST_START)
    {
      firstStart();
    }
    else
    {
      reStart();
    }

    putPersistentProperty(PROP_SCHEMA_VERSION, Integer.toString(SCHEMA_VERSION));
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(unitMappingTable);
    LifecycleUtil.deactivate(commitInfoTable);
    LifecycleUtil.deactivate(mappingStrategy);
    LifecycleUtil.deactivate(durableLockingManager);
    LifecycleUtil.deactivate(metaDataManager);
    LifecycleUtil.deactivate(idHandler);

    Map<String, String> map = new HashMap<>();
    map.put(PROP_GRACEFULLY_SHUT_DOWN, StringUtil.TRUE);
    map.put(PROP_REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));

    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      map.put(PROP_NEXT_LOCAL_CDOID, Store.idToString(idHandler.getNextLocalObjectID()));
      map.put(PROP_LAST_CDOID, Store.idToString(idHandler.getLastObjectID()));
    }

    map.put(PROP_LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(PROP_LAST_LOCAL_BRANCHID, Integer.toString(getLastLocalBranchID()));
    map.put(PROP_LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    map.put(PROP_LAST_NONLOCAL_COMMITTIME, Long.toString(getLastNonLocalCommitTime()));
    setPersistentProperties(map);

    if (readerPool != null)
    {
      readerPool.dispose();
    }

    if (writerPool != null)
    {
      writerPool.dispose();
    }

    connectionKeepAliveTimer.cancel();
    connectionKeepAliveTimer = null;

    super.doDeactivate();
  }

  protected boolean isFirstStart(Set<IDBTable> createdTables)
  {
    if (createdTables.contains(tables.properties()))
    {
      return true;
    }

    Set<String> names = new HashSet<>();
    names.add(PROP_REPOSITORY_CREATED);

    Map<String, String> map = getPersistentProperties(names);
    return map.get(PROP_REPOSITORY_CREATED) == null;
  }

  protected void firstStart()
  {
    InternalRepository repository = getRepository();
    setCreationTime(repository.getTimeStamp());
    firstTime = true;
  }

  protected void reStart() throws Exception
  {
    Set<String> names = new HashSet<>();
    names.add(PROP_REPOSITORY_CREATED);
    names.add(PROP_GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPersistentProperties(names);
    creationTime = Long.valueOf(map.get(PROP_REPOSITORY_CREATED));

    if (map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
    {
      names.clear();

      InternalRepository repository = getRepository();
      boolean generatingIDs = repository.getIDGenerationLocation() == IDGenerationLocation.STORE;
      if (generatingIDs)
      {
        names.add(PROP_NEXT_LOCAL_CDOID);
        names.add(PROP_LAST_CDOID);
      }

      names.add(PROP_LAST_BRANCHID);
      names.add(PROP_LAST_LOCAL_BRANCHID);
      names.add(PROP_LAST_COMMITTIME);
      names.add(PROP_LAST_NONLOCAL_COMMITTIME);
      map = getPersistentProperties(names);

      if (generatingIDs)
      {
        idHandler.setNextLocalObjectID(Store.stringToID(map.get(PROP_NEXT_LOCAL_CDOID)));
        idHandler.setLastObjectID(Store.stringToID(map.get(PROP_LAST_CDOID)));
      }

      setLastBranchID(Integer.valueOf(map.get(PROP_LAST_BRANCHID)));
      setLastLocalBranchID(Integer.valueOf(map.get(PROP_LAST_LOCAL_BRANCHID)));
      setLastCommitTime(Long.valueOf(map.get(PROP_LAST_COMMITTIME)));
      setLastNonLocalCommitTime(Long.valueOf(map.get(PROP_LAST_NONLOCAL_COMMITTIME)));
    }
    else
    {
      repairAfterCrash();
    }

    removePersistentProperties(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
  }

  protected void repairAfterCrash()
  {
    InternalRepository repository = getRepository();
    OM.LOG.warn(MessageFormat.format(Messages.getString("DBStore.9"), repository.getName())); //$NON-NLS-1$

    Connection connection = getConnection();

    try
    {
      connection.setAutoCommit(false);
      connection.setReadOnly(true);

      mappingStrategy.repairAfterCrash(dbAdapter, connection); // Must update the idHandler

      boolean storeIDs = repository.getIDGenerationLocation() == IDGenerationLocation.STORE;
      CDOID lastObjectID = storeIDs ? idHandler.getLastObjectID() : CDOID.NULL;
      CDOID nextLocalObjectID = storeIDs ? idHandler.getNextLocalObjectID() : CDOID.NULL;

      int branchID = DBUtil.selectMaximumInt(connection, tables.branches().id());
      setLastBranchID(branchID > 0 ? branchID : 0);

      int localBranchID = DBUtil.selectMinimumInt(connection, tables.branches().id());
      setLastLocalBranchID(localBranchID < 0 ? localBranchID : 0);

      if (commitInfoTable != null)
      {
        commitInfoTable.repairAfterCrash(connection);
      }
      else
      {
        boolean branching = repository.isSupportingBranches();
        boolean caseSensitive = dbAdapter.isCaseSensitive();
        String schemaName = database.getSchema().getName();

        long lastCommitTime = CDOBranchPoint.UNSPECIFIED_DATE;
        long lastNonLocalCommitTime = CDOBranchPoint.UNSPECIFIED_DATE;

        // Unfortunately the package registry is still inactive, so the class mappings can not be used at this point.
        // Use all tables with a "CDO_CREATED" field instead.
        for (String tableName : DBUtil.getAllTableNames(connection, schemaName, caseSensitive))
        {
          try
          {
            if (DBUtil.equalNames(MappingNames.CDO_OBJECTS, tableName, caseSensitive))
            {
              continue;
            }

            IDBTable table = database.getSchema().getTable(tableName);
            IDBField createdField = table.getField(MappingNames.ATTRIBUTES_CREATED);
            if (createdField == null)
            {
              continue;
            }

            if (branching)
            {
              IDBField branchField = table.getField(MappingNames.ATTRIBUTES_BRANCH);
              if (branchField == null)
              {
                continue;
              }

              lastNonLocalCommitTime = Math.max(lastNonLocalCommitTime,
                  DBUtil.selectMaximumLong(connection, branchField, CDOBranch.MAIN_BRANCH_ID + "<=" + MappingNames.ATTRIBUTES_BRANCH));
            }

            lastCommitTime = Math.max(lastCommitTime, DBUtil.selectMaximumLong(connection, createdField));
          }
          catch (Exception ex)
          {
            OM.LOG.warn(ex.getMessage());
          }
        }

        if (lastNonLocalCommitTime == CDOBranchPoint.UNSPECIFIED_DATE)
        {
          lastNonLocalCommitTime = lastCommitTime;
        }

        setLastCommitTime(lastCommitTime);
        setLastNonLocalCommitTime(lastNonLocalCommitTime);
      }

      if (storeIDs)
      {
        OM.LOG.info(MessageFormat.format(Messages.getString("DBStore.10"), repository.getName(), lastObjectID, nextLocalObjectID, //$NON-NLS-1$
            getLastBranchID(), getLastCommitTime(), getLastNonLocalCommitTime()));
      }
      else
      {
        OM.LOG.info(MessageFormat.format(Messages.getString("DBStore.10b"), repository.getName(), getLastBranchID(), //$NON-NLS-1$
            getLastCommitTime(), getLastNonLocalCommitTime()));
      }
    }
    catch (SQLException e)
    {
      OM.LOG.error(MessageFormat.format(Messages.getString("DBStore.11"), repository.getName()), e); //$NON-NLS-1$
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  private String getSchemaName(Connection connection)
  {
    if (properties != null)
    {
      String schemaName = properties.get(Props.SCHEMA_NAME);
      if (schemaName != null)
      {
        return schemaName.length() == 0 ? null : schemaName;
      }
    }

    String schemaName = dbAdapter.getDefaultSchemaName(connection);
    if (schemaName != null)
    {
      return schemaName;
    }

    return null;
  }

  private boolean isPrependSchemaName()
  {
    if (properties != null)
    {
      String prependSchemaName = properties.get(Props.PREPEND_SCHEMA_NAME);
      if (prependSchemaName != null)
      {
        return Boolean.parseBoolean(prependSchemaName);
      }
    }

    return DEFAULT_PREPEND_SCHEMA_NAME;
  }

  private boolean isCreateSchemaIfNeeded()
  {
    if (properties != null)
    {
      String createSchemaIfNeeded = properties.get(Props.CREATE_SCHEMA_IF_NEEDED);
      if (createSchemaIfNeeded != null)
      {
        return Boolean.parseBoolean(createSchemaIfNeeded);
      }
    }

    return DEFAULT_CREATE_SCHEMA_IF_NEEDED;
  }

  protected IDBDatabase openDatabase(IDBAdapter adapter, IDBConnectionProvider connectionProvider, String schemaName, boolean prependSchemaName,
      boolean fixNullableIndexColumns)
  {
    return DBUtil.openDatabase(dbAdapter, dbConnectionProvider, schemaName, fixNullableIndexColumns, prependSchemaName);
  }

  protected IMetaDataManager createMetaDataManager()
  {
    return new MetaDataManager(this);
  }

  protected DurableLockingManager createDurableLockingManager()
  {
    return new DurableLockingManager(this);
  }

  protected Geometric createProgressDistributor()
  {
    return new ProgressDistributor.Geometric()
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
  }

  protected StoreAccessorPool createReaderPool()
  {
    return createWriterPool();
  }

  protected StoreAccessorPool createWriterPool()
  {
    return new StoreAccessorPool(this, null);
  }

  protected void configureAccessorPool(StoreAccessorPool pool, String property)
  {
    if (pool != null)
    {
      String value = properties.get(property);
      if (value != null)
      {
        int capacity = Integer.parseInt(value);
        pool.setCapacity(capacity);
      }
    }
  }

  protected int selectSchemaVersion(Connection connection, String schemaName) throws SQLException
  {
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.createStatement();
      resultSet = statement.executeQuery(PropertiesTable.sqlSelectProperty(PROP_SCHEMA_VERSION, schemaName));

      if (resultSet.next())
      {
        String value = resultSet.getString(1);
        return Integer.parseInt(value);
      }

      return 0;
    }
    catch (SQLException ex)
    {
      connection.rollback();
      if (dbAdapter.isTableNotFoundException(ex))
      {
        if (!DISABLE_LOG_OTHER_SCHEMA_INFO)
        {
          logOtherSchemaInfo(connection, schemaName);
        }

        return FIRST_START;
      }

      throw ex;
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
    }
  }

  private void logOtherSchemaInfo(Connection connection, String schemaName)
  {
    ResultSet tables = null;

    try
    {
      Set<String> schemaNames = new HashSet<>();
      tables = connection.getMetaData().getTables(connection.getCatalog(), null, null, DBUtil.ALL_TABLE_NAME_TYPES);

      while (tables.next())
      {
        String tableName = tables.getString(3);
        if (DBUtil.equalNames(tableName, PropertiesTable.tableName(), false))
        {
          schemaNames.add(tables.getString(2));
        }
      }

      if (!schemaNames.isEmpty())
      {
        String message = "The CDO system tables are not found";
        if (!StringUtil.isEmpty(schemaName))
        {
          message += " in schema " + DBUtil.quoted(schemaName);
        }

        message += "! Other schemas may contain a CDO repository: ";
        boolean first = true;

        for (String name : schemaNames)
        {
          if (first)
          {
            first = false;
          }
          else
          {
            message += ", ";
          }

          message += name;
        }

        OM.LOG.info(message.trim());
        OM.LOG.info("This can indicate an attempt to open an existing database with a newer CDO server.\n" //
            + "It may help to specify 'schemaName=...' and 'prependSchemaName=true' in cdo-server.xml,\n"//
            + "and/or the system property '-Dorg.eclipse.net4j.db.DISABLE_QUOTED_NAMES=true'\n");
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(tables);
    }
  }

  protected void migrateSchema(int fromVersion) throws Exception
  {
    Connection connection = null;

    try
    {
      connection = getConnection();

      for (int version = fromVersion; version < SCHEMA_VERSION; version++)
      {
        if (SCHEMA_MIGRATORS[version] != null)
        {
          OM.LOG.info("Migrating schema from version " + version + " to version " + (version + 1) + "...");
          SCHEMA_MIGRATORS[version].migrateSchema(connection);
        }
      }

      connection.commit();
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class SchemaMigrator
  {
    public abstract void migrateSchema(Connection connection) throws Exception;
  }

  private static final int FIRST_VERSION_WITH_NULLABLE_CHECKS = 4;

  private final SchemaMigrator NO_MIGRATION_NEEDED = null;

  private final SchemaMigrator NON_AUDIT_MIGRATION = new SchemaMigrator()
  {
    @Override
    public void migrateSchema(Connection connection) throws Exception
    {
      InternalRepository repository = getRepository();
      if (!repository.isSupportingAudits())
      {
        visitAllTables(connection, new IDBStore.TableVisitor()
        {
          @Override
          public void visitTable(Connection connection, String name) throws SQLException
          {
            Statement statement = null;

            try
            {
              statement = connection.createStatement();

              String from = " FROM " + name + " WHERE " + DBUtil.quoted(MappingNames.ATTRIBUTES_VERSION) + "<" + CDOBranchVersion.FIRST_VERSION;
              statement.executeUpdate("DELETE FROM " + DBUtil.quoted(MappingNames.CDO_OBJECTS) + " WHERE " + DBUtil.quoted(MappingNames.ATTRIBUTES_ID)
                  + " IN (SELECT " + DBUtil.quoted(MappingNames.ATTRIBUTES_ID) + from + ")");
              statement.executeUpdate("DELETE" + from);
            }
            finally
            {
              DBUtil.close(statement);
            }
          }
        });
      }
    }
  };

  private final SchemaMigrator LOB_SIZE_MIGRATION = new SchemaMigrator()
  {
    @Override
    public void migrateSchema(Connection connection) throws Exception
    {
      Statement statement = null;

      try
      {
        statement = connection.createStatement();

        // Create a fake DBField because the DBSchema is not yet initialized at this point.
        IDBSchema schema = null;

        String schemaName = getSchemaName(connection);
        if (schemaName != null)
        {
          schema = DBUtil.createSchema(schemaName, dbAdapter.isCaseSensitive(), isPrependSchemaName());
        }

        DBTable table = new DBTable(schema, LobsTable.tableName());
        IDBField field = new DBField(table, LobsTable.sizeName(), DBType.INTEGER, 0, 0, false, 0);

        String sql = dbAdapter.sqlRenameField(field, DBUtil.quoted("size"));
        statement.execute(sql);
      }
      finally
      {
        DBUtil.close(statement);
      }
    }
  };

  private final SchemaMigrator NULLABLE_COLUMNS_MIGRATION = null;

  private final SchemaMigrator[] SCHEMA_MIGRATORS = { //
      NO_MIGRATION_NEEDED, //
      NON_AUDIT_MIGRATION, //
      LOB_SIZE_MIGRATION, //
      NULLABLE_COLUMNS_MIGRATION };

  {
    if (SCHEMA_MIGRATORS.length != SCHEMA_VERSION)
    {
      throw new Error("There must be exactly " + SCHEMA_VERSION + " schema migrators provided");
    }
  }
}
