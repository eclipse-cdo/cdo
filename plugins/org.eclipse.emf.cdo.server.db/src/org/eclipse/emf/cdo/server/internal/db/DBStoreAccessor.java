/*
 * Copyright (c) 2007-2016, 2018-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Andre Dietisheim - bug 256649
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking2;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy2;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.BranchesTable;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractHorizontalClassMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.UnitMappingTable;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader5;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager.InternalObjectAttacher;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.ddl.DBField;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.TrackableTimerTask;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor extends StoreAccessor implements IDBStoreAccessor, BranchLoader5, DurableLocking2
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreAccessor.class);

  private IDBConnection connection;

  private ConnectionKeepAliveTask connectionKeepAliveTask;

  private CDOID maxID = CDOID.NULL;

  private InternalObjectAttacher objectAttacher;

  private List<IDBTable> createdTables;

  public DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    super(store, session);
  }

  public DBStoreAccessor(DBStore store, ITransaction transaction) throws DBException
  {
    super(store, transaction);
  }

  @Override
  public final DBStore getStore()
  {
    return (DBStore)super.getStore();
  }

  @Override
  public final IDBConnection getDBConnection()
  {
    return connection;
  }

  @Override
  public final Connection getConnection()
  {
    return connection;
  }

  @Override
  public DBStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new DBStoreChunkReader(this, revision, feature);
  }

  @Override
  public IDBSchemaTransaction openSchemaTransaction()
  {
    DBStore store = getStore();
    DBAdapter dbAdapter = (DBAdapter)store.getDBAdapter();
    IDBDatabase database = store.getDatabase();

    return dbAdapter.openSchemaTransaction(database, connection);
  }

  public CDOClassifierRef readObjectType(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting object type: {0}", id); //$NON-NLS-1$
    }

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    return mappingStrategy.readObjectType(this, id);
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    IRepository repository = getStore().getRepository();
    if (id.equals(repository.getRootResourceID()))
    {
      return EresourcePackage.Literals.CDO_RESOURCE;
    }

    EClass result = repository.getRevisionManager().getObjectType(id);
    if (result != null)
    {
      return result;
    }

    CommitContext commitContext = StoreThreadLocal.getCommitContext();
    if (commitContext != null)
    {
      InternalCDORevision revision = commitContext.getNewRevisions().get(id);
      if (revision != null)
      {
        return revision.getEClass();
      }
    }

    CDOClassifierRef type = readObjectType(id);
    if (type != null)
    {
      CDOPackageRegistry packageRegistry = repository.getPackageRegistry();
      return (EClass)type.resolve(packageRegistry);
    }

    return null;
  }

  @Override
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk, CDORevisionCacheAdder cache)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision {0} from {1}", id, branchPoint); //$NON-NLS-1$
    }

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();

    EClass eClass = getObjectType(id);
    if (eClass != null)
    {
      InternalCDORevision revision = getStore().createRevision(eClass, id);
      revision.setBranchPoint(branchPoint); // This is part of the search criterion, being replaced later

      IClassMapping mapping = mappingStrategy.getClassMapping(eClass);
      if (mapping.readRevision(this, revision, listChunk))
      {
        int version = revision.getVersion();
        if (version < CDOBranchVersion.UNSPECIFIED_VERSION)
        {
          return new DetachedCDORevision(eClass, id, revision.getBranch(), -version, revision.getTimeStamp(), revision.getRevised());
        }

        return revision;
      }
    }

    // Reading failed - revision does not exist.
    return null;
  }

  @Override
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk, CDORevisionCacheAdder cache)
  {
    DBStore store = getStore();
    EClass eClass = getObjectType(id);

    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(eClass);

    InternalCDORevision revision = store.createRevision(eClass, id);
    revision.setVersion(branchVersion.getVersion());
    revision.setBranchPoint(branchVersion.getBranch().getHead());

    boolean success = false;

    if (mappingStrategy.hasAuditSupport())
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Selecting revision {0} from {1}", id, branchVersion); //$NON-NLS-1$
      }

      // If audit support is present, just use the audit method
      success = ((IClassMappingAuditSupport)mapping).readRevisionByVersion(this, revision, listChunk);
      if (success && revision.getVersion() < CDOBranchVersion.FIRST_VERSION - 1)
      {
        // it is detached revision
        revision = new DetachedCDORevision(eClass, id, revision.getBranch(), -revision.getVersion(), revision.getTimeStamp(), revision.getRevised());

      }
    }
    else
    {
      // If audit support is not present, we still have to provide a method
      // to readRevisionByVersion because TransactionCommitContext.computeDirtyObject
      // needs to lookup the base revision for a change. Hence we emulate this
      // behavior by getting the current revision and asserting that the version
      // has not changed. This is valid because if the version has changed,
      // we are in trouble because of a conflict anyways.
      if (TRACER.isEnabled())
      {
        TRACER.format("Selecting current base revision: {0}", id); //$NON-NLS-1$
      }

      success = mapping.readRevision(this, revision, listChunk);

      if (success && revision.getVersion() != branchVersion.getVersion())
      {
        throw new IllegalStateException("Can only retrieve current version " + revision.getVersion() + " for " + id //$NON-NLS-1$ //$NON-NLS-2$
            + " - version requested was " + branchVersion); //$NON-NLS-1$
      }
    }

    return success ? revision : null;
  }

  /**
   * @since 2.0
   */
  @Override
  public void queryResources(QueryResourcesContext context)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.queryResources(this, context);
  }

  @Override
  public void queryXRefs(QueryXRefsContext context)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.queryXRefs(this, context);
  }

  @Override
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String queryLanguage = info.getQueryLanguage();
    if (StringUtil.equalsIgnoreCase(queryLanguage, SQLQueryHandler.QUERY_LANGUAGE))
    {
      return new SQLQueryHandler(this);
    }

    return null;
  }

  @Override
  public void queryLobs(List<byte[]> ids)
  {
    getStore().tables().lobs().queryLobs(connection, ids);
  }

  @Override
  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    getStore().tables().lobs().loadLob(connection, id, out);
  }

  @Override
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    getStore().tables().lobs().handleLobs(connection, fromTime, toTime, handler);
  }

  @Override
  protected void applyIDMappings(InternalCommitContext context, OMMonitor monitor)
  {
    super.applyIDMappings(context, monitor);

    DBStore store = getStore();
    IIDHandler idHandler = store.getIDHandler();

    // Remember maxID because it may have to be adjusted if the repository is BACKUP or CLONE. See bug 325097.
    boolean adjustMaxID = !context.getBranchPoint().getBranch().isLocal() && store.getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE;

    // Remember CDOIDs of new objects. They are cleared after writeRevisions()
    for (InternalCDORevision revision : context.getNewObjects())
    {
      CDOID id = revision.getID();

      if (adjustMaxID && (CDOIDUtil.isNull(maxID) || idHandler.compare(id, maxID) > 0))
      {
        maxID = id;
      }
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, CDOBranchPoint mergeSource,
      OMMonitor monitor)
  {
    CommitInfoTable commitInfoTable = getStore().getCommitInfoTable();
    if (commitInfoTable != null)
    {
      commitInfoTable.writeCommitInfo(this, branch, timeStamp, previousTimeStamp, userID, comment, mergeSource, monitor);
    }
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created, OMMonitor monitor)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();

    if (!mappingStrategy.hasDeltaSupport())
    {
      throw new UnsupportedOperationException("Mapping strategy does not support revision deltas"); //$NON-NLS-1$
    }

    monitor.begin(revisionDeltas.length);
    try
    {
      for (InternalCDORevisionDelta delta : revisionDeltas)
      {
        writeRevisionDelta(delta, created, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeRevisionDelta(InternalCDORevisionDelta delta, long created, OMMonitor monitor)
  {
    CDOID id = delta.getID();
    EClass eClass = getObjectType(id);
    IClassMappingDeltaSupport mapping = (IClassMappingDeltaSupport)getStore().getMappingStrategy().getClassMapping(eClass);
    mapping.writeRevisionDelta(this, delta, created, monitor);
  }

  @Override
  protected void writeNewObjectRevisions(InternalCommitContext context, InternalCDORevision[] newObjects, CDOBranch branch, OMMonitor monitor)
  {
    writeRevisions(context, true, newObjects, branch, monitor);
  }

  @Override
  protected void writeDirtyObjectRevisions(InternalCommitContext context, InternalCDORevision[] dirtyObjects, CDOBranch branch, OMMonitor monitor)
  {
    writeRevisions(context, false, dirtyObjects, branch, monitor);
  }

  protected void writeRevisions(InternalCommitContext context, boolean attachNewObjects, InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, attachNewObjects, true, monitor.fork());
      }

      if (attachNewObjects)
      {
        InternalRepository repository = getStore().getRepository();
        if (repository.isSupportingUnits())
        {
          InternalUnitManager unitManager = repository.getUnitManager();
          objectAttacher = unitManager.attachObjects(context);
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  protected void writeRevision(InternalCDORevision revision, boolean firstRevision, boolean revise, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: {0}", revision); //$NON-NLS-1$
    }

    EClass eClass = revision.getEClass();

    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(eClass);
    mapping.writeRevision(this, revision, firstRevision, revise, monitor);
  }

  @Override
  protected boolean needsRevisionPostProcessing()
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    if (mappingStrategy instanceof IMappingStrategy2)
    {
      return ((IMappingStrategy2)mappingStrategy).needsRevisionPostProcessing();
    }

    return super.needsRevisionPostProcessing();
  }

  @Override
  protected void postProcessRevisions(InternalCommitContext context, OMMonitor monitor)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    if (mappingStrategy instanceof IMappingStrategy2)
    {
      ((IMappingStrategy2)mappingStrategy).postProcessRevisions(this, context, monitor);
    }
  }

  /*
   * XXX Eike: change API from CDOID[] to CDOIDAndVersion[]
   */
  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    monitor.begin(detachedObjects.length);

    try
    {
      InternalCDORevisionManager revisionManager = getStore().getRepository().getRevisionManager();
      for (CDOID id : detachedObjects)
      {
        // TODO when CDOIDAndVersion is available:
        // CDOID id = idAndVersion.getID(); //
        // int version = idAndVersion.getVersion(); //

        // but for now:

        InternalCDORevision revision = revisionManager.getRevision(id, branch.getHead(), CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
        int version = ObjectUtil.equals(branch, revision.getBranch()) ? revision.getVersion() + 1 : CDOBranchVersion.FIRST_VERSION;

        if (TRACER.isEnabled())
        {
          TRACER.format("Detaching object: {0}", id); //$NON-NLS-1$
        }

        EClass eClass = getObjectType(id);
        IClassMapping mapping = mappingStrategy.getClassMapping(eClass);
        mapping.detachObject(this, id, version, branch, timeStamp, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    return getStore().getIDHandler().getNextCDOID(revision);
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    getStore().tables().lobs().writeBlob(connection, id, size, inputStream);
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    getStore().tables().lobs().writeClob(connection, id, size, reader);
  }

  @Override
  protected final void doCommit(OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("--- DB COMMIT ---"); //$NON-NLS-1$
    }

    Async async = null;
    monitor.begin();

    try
    {
      try
      {
        async = monitor.forkAsync();
        connection.commit();

        if (maxID != CDOID.NULL)
        {
          // See bug 325097
          getStore().getIDHandler().adjustLastObjectID(maxID);
          maxID = CDOID.NULL;
        }

        if (objectAttacher != null)
        {
          objectAttacher.finishedCommit(true);
          objectAttacher = null;
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected final void doRollback(IStoreAccessor.CommitContext commitContext)
  {
    if (objectAttacher != null)
    {
      objectAttacher.finishedCommit(false);
      objectAttacher = null;
    }

    IDBStore store = getStore();
    IMetaDataManager metaDataManager = store.getMetaDataManager();
    metaDataManager.clearMetaIDMappings();

    if (TRACER.isEnabled())
    {
      TRACER.format("--- DB ROLLBACK ---"); //$NON-NLS-1$
    }

    try
    {
      connection.rollback();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    // Bug 298632: Must rollback DBSchema to its prior state and drop the tables
    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    mappingStrategy.removeMapping(connection, commitContext.getNewPackageUnits());
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    DBStore store = getStore();
    connection = store.getDatabase().getConnection();
    connectionKeepAliveTask = new ConnectionKeepAliveTask(this);
    objectAttacher = null;

    long keepAlivePeriod = ConnectionKeepAliveTask.EXECUTION_PERIOD;
    Map<String, String> storeProps = store.getProperties();
    if (storeProps != null)
    {
      String value = storeProps.get(IDBStore.Props.CONNECTION_KEEPALIVE_PERIOD);
      if (value != null)
      {
        keepAlivePeriod = Long.parseLong(value) * 60L * 1000L;
      }
    }

    store.getConnectionKeepAliveTimer().schedule(connectionKeepAliveTask, keepAlivePeriod, keepAlivePeriod);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    connectionKeepAliveTask.cancel();
    connectionKeepAliveTask = null;

    DBUtil.close(connection);
    connection = null;

    super.doDeactivate();
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // this is called when the accessor is put back into the pool
    // we want to make sure that no DB lock is held (see Bug 276926)
    connection.rollback();

    if (createdTables != null)
    {
      createdTables.clear();
      createdTables = null;
    }
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // do nothing
  }

  @Override
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    IMetaDataManager metaDataManager = getStore().getMetaDataManager();
    return metaDataManager.loadPackageUnit(connection, packageUnit);
  }

  @Override
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    IMetaDataManager metaDataManager = getStore().getMetaDataManager();
    return metaDataManager.readPackageUnits(connection);
  }

  @Override
  protected void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    boolean wasTrackConstruction = DBField.isTrackConstruction();

    try
    {
      Map<String, String> properties = getStore().getProperties();
      if (properties != null)
      {
        String prop = properties.get(IDBStore.Props.FIELD_CONSTRUCTION_TRACKING);
        if (prop != null)
        {
          DBField.trackConstruction(Boolean.valueOf(prop));
        }
      }

      super.doWrite(context, monitor);
    }
    finally
    {
      DBField.trackConstruction(wasTrackConstruction);
    }
  }

  @Override
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin(2);

    try
    {
      DBStore store = getStore();

      IMetaDataManager metaDataManager = store.getMetaDataManager();
      metaDataManager.writePackageUnits(connection, packageUnits, monitor.fork());

      IMappingStrategy mappingStrategy = store.getMappingStrategy();
      mappingStrategy.createMapping(connection, packageUnits, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    checkBranchingSupport();
    return getStore().tables().branches().createBranch(connection, branchID, branchInfo);
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    checkBranchingSupport();
    return getStore().tables().branches().loadBranch(connection, branchID);
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int baseID)
  {
    checkBranchingSupport();
    return getStore().tables().branches().loadSubBranches(connection, baseID);
  }

  private void checkAuditingSupport()
  {
    if (!getStore().getMappingStrategy().hasAuditSupport())
    {
      throw new UnsupportedOperationException("Mapping strategy does not support auditing"); //$NON-NLS-1$
    }
  }

  private void checkBranchingSupport()
  {
    if (!getStore().getMappingStrategy().hasBranchingSupport())
    {
      throw new UnsupportedOperationException("Mapping strategy does not support branching"); //$NON-NLS-1$
    }
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler handler)
  {
    return getStore().tables().branches().loadBranches(connection, startID, endID, handler);
  }

  @Override
  public CDOBranch[] deleteBranches(int branchID, OMMonitor monitor)
  {
    return getStore().tables().branches().deleteBranches(this, branchID, monitor);
  }

  @Override
  public void renameBranch(int branchID, String oldName, String newName)
  {
    checkBranchingSupport();
    getStore().tables().branches().renameBranch(connection, branchID, oldName, newName);
  }

  @Override
  public CDOBranchPoint changeTag(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    checkAuditingSupport();
    return getStore().tables().tags().changeTag(connection, modCount, oldName, newName, branchPoint);
  }

  @Override
  public void loadTags(String name, Consumer<BranchInfo> handler)
  {
    checkAuditingSupport();
    getStore().tables().tags().loadTags(connection, name, handler);
  }

  @Override
  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    CommitInfoTable commitInfoTable = getStore().getCommitInfoTable();
    if (commitInfoTable != null)
    {
      commitInfoTable.loadCommitInfos(this, branch, startTime, endTime, handler);
    }
  }

  @Override
  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    return mappingStrategy.readChangeSet(this, monitor, segments);
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.handleRevisions(this, eClass, branch, timeStamp, exactTime, new DBRevisionHandler(handler));
  }

  @Override
  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime) throws IOException
  {
    DBStore store = getStore();
    InternalRepository repository = store.getRepository();

    if (repository.getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      out.writeCDOID(store.getIDHandler().getLastObjectID()); // See bug 325097
    }

    BranchesTable branches = store.tables().branches();
    String where = " WHERE " + branches.id() + " BETWEEN " + fromBranchID + " AND " + toBranchID;
    DBUtil.serializeTable(out, connection, branches.table(), null, where);

    CommitInfoTable commitInfoTable = store.getCommitInfoTable();
    if (commitInfoTable != null)
    {
      out.writeBoolean(true);
      commitInfoTable.rawExport(connection, out, fromCommitTime, toCommitTime);
    }
    else
    {
      out.writeBoolean(false);
    }

    DurableLockingManager durableLockingManager = store.getDurableLockingManager();
    durableLockingManager.rawExport(connection, out, fromCommitTime, toCommitTime);

    IIDHandler idHandler = store.getIDHandler();
    idHandler.rawExport(connection, out, fromCommitTime, toCommitTime);

    // IMetaDataManager metaDataManager = store.getMetaDataManager();
    // metaDataManager.rawExport(connection, out, fromCommitTime, toCommitTime);

    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    mappingStrategy.rawExport(this, out, fromBranchID, toBranchID, fromCommitTime, toCommitTime);
  }

  @Override
  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    DBStore store = getStore();
    IIDHandler idHandler = store.getIDHandler();
    if (store.getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      idHandler.setLastObjectID(in.readCDOID()); // See bug 325097
    }

    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    int size = mappingStrategy.getClassMappings().size();
    int commitWork = 5;
    monitor.begin(commitWork + size + commitWork);

    Collection<InternalCDOPackageUnit> packageUnits = new HashSet<>();

    try
    {
      BranchesTable branches = store.tables().branches();
      DBUtil.deserializeTable(in, connection, branches.table(), monitor.fork());

      CommitInfoTable commitInfoTable = store.getCommitInfoTable();
      if (in.readBoolean())
      {
        if (commitInfoTable == null)
        {
          throw new IllegalStateException("Commit info table is missing");
        }

        commitInfoTable.rawImport(connection, in, fromCommitTime, toCommitTime, monitor.fork());
      }
      else
      {
        if (commitInfoTable != null)
        {
          throw new IllegalStateException("Commit info data is expected but missing");
        }
      }

      DurableLockingManager durableLockingManager = store.getDurableLockingManager();
      durableLockingManager.rawImport(connection, in, fromCommitTime, toCommitTime, monitor.fork());

      idHandler.rawImport(connection, in, fromCommitTime, toCommitTime, monitor.fork());

      // rawImportPackageUnits(in, fromCommitTime, toCommitTime, packageUnits, monitor.fork());

      IDBSchemaTransaction schemaTransaction = openSchemaTransaction();

      try
      {
        mappingStrategy.rawImport(this, in, fromCommitTime, toCommitTime, monitor.fork(size));
        schemaTransaction.commit();
      }
      finally
      {
        schemaTransaction.close();
      }

      rawCommit(commitWork, monitor);
    }
    catch (RuntimeException ex)
    {
      rawRollback(packageUnits);
      throw ex;
    }
    catch (IOException ex)
    {
      rawRollback(packageUnits);
      throw ex;
    }
    finally
    {
      monitor.done();
    }
  }

  private void rawRollback(Collection<InternalCDOPackageUnit> packageUnits)
  {
    try
    {
      connection.rollback();
    }
    catch (SQLException ex)
    {
      OM.LOG.error(ex);
    }

    getStore().getMappingStrategy().removeMapping(connection, packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]));
  }

  protected void rawImportPackageUnits(CDODataInput in, long fromCommitTime, long toCommitTime, Collection<InternalCDOPackageUnit> packageUnits,
      OMMonitor monitor) throws IOException
  {
    monitor.begin(2);

    try
    {
      DBStore store = getStore();
      IMetaDataManager metaDataManager = store.getMetaDataManager();

      Collection<InternalCDOPackageUnit> imported = //
          metaDataManager.rawImport(connection, in, fromCommitTime, toCommitTime, monitor.fork());
      packageUnits.addAll(imported);

      if (!packageUnits.isEmpty())
      {
        InternalRepository repository = store.getRepository();
        InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);

        for (InternalCDOPackageUnit packageUnit : packageUnits)
        {
          packageRegistry.putPackageUnit(packageUnit);
        }

        IMappingStrategy mappingStrategy = store.getMappingStrategy();

        // Use another connection because CREATE TABLE (which is called in createMapping)
        // on H2 databases does a commit.
        Connection connection2 = null;

        try
        {
          connection2 = store.getConnection();

          mappingStrategy.createMapping(connection2, packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]), monitor.fork());
        }
        finally
        {
          DBUtil.close(connection2);
        }
      }
      else
      {
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void rawStore(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    writePackageUnits(packageUnits, monitor);
  }

  @Override
  public void rawStore(InternalCDORevision revision, OMMonitor monitor)
  {
    CDOID id = revision.getID();
    EClass eClass = revision.getEClass();

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    CDOClassifierRef classifierRef = mappingStrategy.readObjectType(this, id);

    boolean firstRevision = classifierRef == null;
    if (!firstRevision)
    {
      boolean namesMatch = classifierRef.getClassifierName().equals(eClass.getName());
      boolean packagesMatch = classifierRef.getPackageURI().equals(eClass.getEPackage().getNsURI());
      if (!namesMatch || !packagesMatch)
      {
        throw new IllegalStateException();
      }
    }

    writeRevision(revision, firstRevision, false, monitor);
    getStore().getIDHandler().adjustLastObjectID(id);
  }

  @Override
  public void rawStore(byte[] id, long size, InputStream inputStream) throws IOException
  {
    writeBlob(id, size, inputStream);
  }

  @Override
  public void rawStore(byte[] id, long size, Reader reader) throws IOException
  {
    writeClob(id, size, reader);
  }

  @Override
  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, OMMonitor monitor)
  {
    writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, null, monitor);
  }

  @Override
  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, CDOBranchPoint mergeSource, OMMonitor monitor)
  {
    writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, mergeSource, monitor);
  }

  @Override
  public void rawDelete(CDOID id, int version, CDOBranch branch, EClass eClass, OMMonitor monitor)
  {
    if (eClass == null)
    {
      eClass = getObjectType(id);
    }

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(eClass);
    if (mapping instanceof AbstractHorizontalClassMapping)
    {
      AbstractHorizontalClassMapping m = (AbstractHorizontalClassMapping)mapping;
      m.rawDelete(this, id, version, branch, monitor);
    }
    else
    {
      throw new UnsupportedOperationException("rawDelete() is not supported by " + mapping.getClass().getName());
    }
  }

  @Override
  public void rawCommit(double commitWork, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    return createLockArea(null, userID, branchPoint, readOnly, locks);
  }

  @Override
  public LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    return manager.createLockArea(this, durableLockingID, userID, branchPoint, readOnly, locks);
  }

  @Override
  public void updateLockArea(LockArea area)
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    manager.updateLockArea(this, area);
  }

  @Override
  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    return manager.getLockArea(this, durableLockingID);
  }

  @Override
  public void getLockAreas(String userIDPrefix, Handler handler)
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    manager.getLockAreas(this, userIDPrefix, handler);
  }

  @Override
  public void deleteLockArea(String durableLockingID)
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    manager.deleteLockArea(this, durableLockingID);
  }

  @Override
  public void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    manager.lock(this, durableLockingID, type, objectsToLock);
  }

  @Override
  public void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    DurableLockingManager manager = getStore().getDurableLockingManager();
    manager.unlock(this, durableLockingID, type, objectsToUnlock);
  }

  @Override
  public List<CDOID> readUnitRoots()
  {
    UnitMappingTable unitMappingTable = getStore().getUnitMappingTable();
    return unitMappingTable.readUnitRoots(this);
  }

  @Override
  public void readUnit(IView view, CDOID rootID, CDORevisionHandler revisionHandler, OMMonitor monitor)
  {
    UnitMappingTable unitMappingTable = getStore().getUnitMappingTable();
    unitMappingTable.readUnitRevisions(this, view, rootID, revisionHandler, monitor);
  }

  @Override
  public Object initUnit(IView view, CDOID rootID, CDORevisionHandler revisionHandler, Set<CDOID> initializedIDs, long timeStamp, OMMonitor monitor)
  {
    UnitMappingTable unitMappingTable = getStore().getUnitMappingTable();
    return unitMappingTable.initUnit(this, timeStamp, view, rootID, revisionHandler, initializedIDs, monitor);
  }

  @Override
  public void finishUnit(IView view, CDOID rootID, CDORevisionHandler revisionHandler, long timeStamp, Object initResult, List<CDOID> ids)
  {
    UnitMappingTable unitMappingTable = getStore().getUnitMappingTable();
    unitMappingTable.finishUnit((BatchedStatement)initResult, rootID, ids, timeStamp);
  }

  @Override
  public void writeUnits(Map<CDOID, CDOID> unitMappings, long timeStamp)
  {
    UnitMappingTable unitMappingTable = getStore().getUnitMappingTable();
    unitMappingTable.writeUnitMappings(this, unitMappings, timeStamp);
  }

  @Override
  public void tableCreated(IDBTable table)
  {
    if (createdTables == null)
    {
      createdTables = new ArrayList<>();
    }

    createdTables.add(table);
  }

  /**
   * @author Stefan Winkler
   */
  private static final class ConnectionKeepAliveTask extends TrackableTimerTask
  {
    public static final long EXECUTION_PERIOD = 1000 * 60 * 60 * 4; // 4 hours

    private DBStoreAccessor accessor;

    private final String sql;

    public ConnectionKeepAliveTask(DBStoreAccessor accessor)
    {
      this.accessor = accessor;
      sql = "SELECT 1 FROM " + accessor.getStore().tables().properties();
    }

    @Override
    public void run()
    {
      if (accessor == null)
      {
        return;
      }

      Statement stmt = null;

      try
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("DB connection keep-alive task activated"); //$NON-NLS-1$
        }

        Connection connection = accessor.getConnection();
        stmt = connection.createStatement();
        stmt.executeQuery(sql);
      }
      catch (java.sql.SQLException ex)
      {
        OM.LOG.error("DB connection keep-alive failed", ex); //$NON-NLS-1$

        // Assume the connection has failed.
        try
        {
          LifecycleUtil.deactivate(accessor);
          LifecycleUtil.activate(accessor);
        }
        catch (Exception ex2)
        {
          OM.LOG.error("DB connection reconnect failed", ex2); //$NON-NLS-1$
        }
      }
      catch (Exception ex) // Important: Do not throw any unchecked exceptions to the TimerThread!!!
      {
        OM.LOG.error("DB connection keep-alive failed", ex); //$NON-NLS-1$
      }
      finally
      {
        DBUtil.close(stmt);
      }
    }

    @Override
    public boolean cancel()
    {
      accessor = null;
      return super.cancel();
    }
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.server.db.IPreparedStatementCache getStatementCache()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public CloseableIterator<CDOID> readObjectIDs()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void deleteBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void renameBranch(int branchID, String newName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void unlock(String durableLockingID)
  {
    throw new UnsupportedOperationException();
  }
}
