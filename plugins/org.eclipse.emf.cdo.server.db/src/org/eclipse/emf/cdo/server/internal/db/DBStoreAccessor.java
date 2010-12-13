/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.lob.CDOLobHandler;
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
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
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
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor extends LongIDStoreAccessor implements IDBStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreAccessor.class);

  private Connection connection;

  private ConnectionKeepAliveTask connectionKeepAliveTask;

  private IPreparedStatementCache statementCache;

  private Set<CDOID> newObjects = new HashSet<CDOID>();

  private long maxID;

  public DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    super(store, session);
  }

  public DBStoreAccessor(DBStore store, ITransaction transaction) throws DBException
  {
    super(store, transaction);
  }

  @Override
  public DBStore getStore()
  {
    return (DBStore)super.getStore();
  }

  public IPreparedStatementCache getStatementCache()
  {
    return statementCache;
  }

  public DBStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new DBStoreChunkReader(this, revision, feature);
  }

  /**
   * Returns an iterator that iterates over all objects in the store and makes their CDOIDs available for processing.
   * This method is supposed to be called very infrequently, for example during the recovery from a crash.
   * 
   * @since 2.0
   * @deprecated Not used by the framework anymore.
   */
  @Deprecated
  public CloseableIterator<CDOID> readObjectIDs()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Selecting object IDs"); //$NON-NLS-1$
    }

    return getStore().getMappingStrategy().readObjectIDs(this);
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

  protected EClass getObjectType(CDOID id)
  {
    IRepository repository = getStore().getRepository();
    if (repository.getRootResourceID().equals(id))
    {
      return EresourcePackage.Literals.CDO_RESOURCE;
    }

    EClass result = repository.getRevisionManager().getObjectType(id);
    if (result != null)
    {
      return result;
    }

    CDOClassifierRef type = readObjectType(id);
    if (type != null)
    {
      CDOPackageRegistry packageRegistry = repository.getPackageRegistry();
      return (EClass)type.resolve(packageRegistry);
    }

    throw new IllegalStateException("No type found for " + id);
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision {0} from {1}", id, branchPoint); //$NON-NLS-1$
    }

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();

    EClass eClass = getObjectType(id);
    InternalCDORevision revision = getStore().createRevision(eClass, id);
    revision.setBranchPoint(branchPoint);

    IClassMapping mapping = mappingStrategy.getClassMapping(eClass);
    if (mapping.readRevision(this, revision, listChunk))
    {
      int version = revision.getVersion();
      if (version < CDOBranchVersion.FIRST_VERSION - 1)
      {
        return new DetachedCDORevision(eClass, id, revision.getBranch(), -version, revision.getTimeStamp());
      }

      return revision;
    }

    // Reading failed - revision does not exist.
    return null;
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
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

      // if audit support is present, just use the audit method
      success = ((IClassMappingAuditSupport)mapping).readRevisionByVersion(this, revision, listChunk);
    }
    else
    {
      // if audit support is not present, we still have to provide a method
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
  public void queryResources(QueryResourcesContext context)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.queryResources(this, context);
  }

  public void queryXRefs(QueryXRefsContext context)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.queryXRefs(this, context);
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String queryLanguage = info.getQueryLanguage();
    if (StringUtil.equalsUpperOrLowerCase(queryLanguage, SQLQueryHandler.QUERY_LANGUAGE))
    {
      return new SQLQueryHandler(this);
    }

    return null;
  }

  public void queryLobs(List<byte[]> ids)
  {
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_QUERY_LOBS, ReuseProbability.MEDIUM);

      for (Iterator<byte[]> it = ids.iterator(); it.hasNext();)
      {
        byte[] id = it.next();
        pstmt.setString(1, HexUtil.bytesToHex(id));

        try
        {
          resultSet = pstmt.executeQuery();
          if (!resultSet.next())
          {
            it.remove();
          }
        }
        finally
        {
          DBUtil.close(resultSet);
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_LOAD_LOB, ReuseProbability.MEDIUM);
      pstmt.setString(1, HexUtil.bytesToHex(id));

      try
      {
        resultSet = pstmt.executeQuery();
        resultSet.next();

        long size = resultSet.getLong(1);
        Blob blob = resultSet.getBlob(2);
        if (resultSet.wasNull())
        {
          Clob clob = resultSet.getClob(3);
          Reader in = clob.getCharacterStream();
          IOUtil.copyCharacter(in, new OutputStreamWriter(out), size);
        }
        else
        {
          InputStream in = blob.getBinaryStream();
          IOUtil.copyBinary(in, out, size);
        }
      }
      finally
      {
        DBUtil.close(resultSet);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_HANDLE_LOBS, ReuseProbability.LOW);

      try
      {
        resultSet = pstmt.executeQuery();
        while (resultSet.next())
        {
          byte[] id = HexUtil.hexToBytes(resultSet.getString(1));
          long size = resultSet.getLong(2);
          Blob blob = resultSet.getBlob(3);
          if (resultSet.wasNull())
          {
            Clob clob = resultSet.getClob(4);
            Reader in = clob.getCharacterStream();
            Writer out = handler.handleClob(id, size);
            if (out != null)
            {
              try
              {
                IOUtil.copyCharacter(in, out, size);
              }
              finally
              {
                IOUtil.close(out);
              }
            }
          }
          else
          {
            InputStream in = blob.getBinaryStream();
            OutputStream out = handler.handleBlob(id, size);
            if (out != null)
            {
              try
              {
                IOUtil.copyBinary(in, out, size);
              }
              finally
              {
                IOUtil.close(out);
              }
            }
          }
        }
      }
      finally
      {
        DBUtil.close(resultSet);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  @Override
  protected void applyIDMappings(InternalCommitContext context, OMMonitor monitor)
  {
    super.applyIDMappings(context, monitor);

    // Remember CDOIDs of new objects. They are cleared after writeRevisions()
    for (InternalCDORevision revision : context.getNewObjects())
    {
      CDOID id = revision.getID();
      newObjects.add(id);

      // Remember maxID because it may have to be adjusted if the repository is BACKUP or CLONE. See bug 325097.
      if (!context.getBranchPoint().getBranch().isLocal())
      {
        long value = CDOIDUtil.getLong(id);
        if (value > maxID)
        {
          maxID = value;
        }
      }
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor)
  {
    PreparedStatement pstmt = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_CREATE_COMMIT_INFO, ReuseProbability.HIGH);
      pstmt.setLong(1, timeStamp);
      pstmt.setLong(2, previousTimeStamp);
      pstmt.setInt(3, branch.getID());
      pstmt.setString(4, userID);
      pstmt.setString(5, comment);

      CDODBUtil.sqlUpdate(pstmt, true);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
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
    IClassMappingDeltaSupport mapping = (IClassMappingDeltaSupport)getStore().getMappingStrategy().getClassMapping(
        eClass);
    mapping.writeRevisionDelta(this, delta, created, monitor);
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, newObjects.contains(revision.getID()), monitor.fork());
      }
    }
    finally
    {
      newObjects.clear();
      monitor.done();
    }
  }

  protected void writeRevision(InternalCDORevision revision, boolean newRevision, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: {0}", revision); //$NON-NLS-1$
    }

    EClass eClass = revision.getEClass();
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(eClass);
    mapping.writeRevision(this, revision, newRevision, monitor);
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

        InternalCDORevision revision = revisionManager.getRevision(id, branch.getHead(), CDORevision.UNCHUNKED,
            CDORevision.DEPTH_NONE, true);
        int version = ObjectUtil.equals(branch, revision.getBranch()) ? revision.getVersion()
            : CDOBranchVersion.FIRST_VERSION;

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

  public Connection getConnection()
  {
    return connection;
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    PreparedStatement pstmt = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_WRITE_BLOB, ReuseProbability.MEDIUM);
      pstmt.setString(1, HexUtil.bytesToHex(id));
      pstmt.setLong(2, size);
      pstmt.setBinaryStream(3, inputStream, (int)size);

      CDODBUtil.sqlUpdate(pstmt, true);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    PreparedStatement pstmt = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_WRITE_CLOB, ReuseProbability.MEDIUM);
      pstmt.setString(1, HexUtil.bytesToHex(id));
      pstmt.setLong(2, size);
      pstmt.setCharacterStream(3, reader, (int)size);

      CDODBUtil.sqlUpdate(pstmt, true);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
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
        getConnection().commit();

        DBStore store = getStore();
        if (maxID > store.getLastObjectID())
        {
          // See bug 325097
          store.setLastObjectID(maxID);
        }

        maxID = 0L;
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
  protected final void rollback(IStoreAccessor.CommitContext commitContext)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("--- DB ROLLBACK ---"); //$NON-NLS-1$
    }

    try
    {
      getConnection().rollback();

      // Bugzilla 298632: Must rollback DBSchema to its prior state and drop the tables
      getStore().getMappingStrategy().removeMapping(getConnection(), commitContext.getNewPackageUnits());
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    connection = getStore().getConnection();
    connectionKeepAliveTask = new ConnectionKeepAliveTask();
    getStore().getConnectionKeepAliveTimer().schedule(connectionKeepAliveTask,
        ConnectionKeepAliveTask.EXECUTION_PERIOD, ConnectionKeepAliveTask.EXECUTION_PERIOD);

    // TODO - make this configurable?
    statementCache = CDODBUtil.createStatementCache();
    statementCache.setConnection(connection);
    LifecycleUtil.activate(statementCache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(statementCache);
    connectionKeepAliveTask.cancel();
    DBUtil.close(connection);
    connection = null;
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // this is called when the accessor is put back into the pool
    // we want to make sure that no DB lock is held (see Bug 276926)
    connection.rollback();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // do nothing
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return getStore().getMetaDataManager().loadPackageUnit(getConnection(), packageUnit);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return getStore().getMetaDataManager().readPackageUnits(getConnection());
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin(2);

    try
    {
      DBStore store = getStore();
      Connection connection = getConnection();

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

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    checkBranchingSupport();
    if (branchID == NEW_BRANCH)
    {
      branchID = getStore().getNextBranchID();
    }
    else if (branchID == NEW_LOCAL_BRANCH)
    {
      branchID = getStore().getNextLocalBranchID();
    }

    PreparedStatement pstmt = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_CREATE_BRANCH, ReuseProbability.LOW);
      pstmt.setInt(1, branchID);
      pstmt.setString(2, branchInfo.getName());
      pstmt.setInt(3, branchInfo.getBaseBranchID());
      pstmt.setLong(4, branchInfo.getBaseTimeStamp());

      CDODBUtil.sqlUpdate(pstmt, true);
      getConnection().commit();
      return new Pair<Integer, Long>(branchID, branchInfo.getBaseTimeStamp());
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public BranchInfo loadBranch(int branchID)
  {
    checkBranchingSupport();
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_LOAD_BRANCH, ReuseProbability.HIGH);
      pstmt.setInt(1, branchID);

      resultSet = pstmt.executeQuery();
      if (!resultSet.next())
      {
        throw new DBException("Branch with ID " + branchID + " does not exist");
      }

      String name = resultSet.getString(1);
      int baseBranchID = resultSet.getInt(2);
      long baseTimeStamp = resultSet.getLong(3);
      return new BranchInfo(name, baseBranchID, baseTimeStamp);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public SubBranchInfo[] loadSubBranches(int baseID)
  {
    checkBranchingSupport();
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_LOAD_SUB_BRANCHES, ReuseProbability.HIGH);
      pstmt.setInt(1, baseID);

      resultSet = pstmt.executeQuery();
      List<SubBranchInfo> result = new ArrayList<SubBranchInfo>();
      while (resultSet.next())
      {
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        long baseTimeStamp = resultSet.getLong(3);
        result.add(new SubBranchInfo(id, name, baseTimeStamp));
      }

      return result.toArray(new SubBranchInfo[result.size()]);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  private void checkBranchingSupport()
  {
    if (!getStore().getMappingStrategy().hasBranchingSupport())
    {
      throw new UnsupportedOperationException("Mapping strategy does not support branching"); //$NON-NLS-1$
    }
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler handler)
  {
    int count = 0;
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    InternalRepository repository = getSession().getManager().getRepository();
    InternalCDOBranchManager branchManager = repository.getBranchManager();

    try
    {
      pstmt = statementCache.getPreparedStatement(CDODBSchema.SQL_LOAD_BRANCHES, ReuseProbability.HIGH);
      pstmt.setInt(1, startID);
      pstmt.setInt(2, endID > 0 ? endID : Integer.MAX_VALUE);

      resultSet = pstmt.executeQuery();
      while (resultSet.next())
      {
        int branchID = resultSet.getInt(1);
        String name = resultSet.getString(2);
        int baseBranchID = resultSet.getInt(3);
        long baseTimeStamp = resultSet.getLong(4);

        InternalCDOBranch branch = branchManager.getBranch(branchID, new BranchInfo(name, baseBranchID, baseTimeStamp));
        handler.handleBranch(branch);
        ++count;
      }

      return count;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.COMMIT_INFOS_TIMESTAMP);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.COMMIT_INFOS_PREVIOUS_TIMESTAMP);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.COMMIT_INFOS_USER);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.COMMIT_INFOS_COMMENT);
    if (branch == null)
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(CDODBSchema.COMMIT_INFOS_BRANCH);
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(CDODBSchema.COMMIT_INFOS);
    boolean where = false;

    if (branch != null)
    {
      builder.append(where ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      builder.append(CDODBSchema.COMMIT_INFOS_BRANCH);
      builder.append("="); //$NON-NLS-1$
      builder.append(branch.getID());
      where = true;
    }

    if (startTime != DBStore.UNSPECIFIED_DATE)
    {
      builder.append(where ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      builder.append(CDODBSchema.COMMIT_INFOS_TIMESTAMP);
      builder.append(">="); //$NON-NLS-1$
      builder.append(startTime);
      where = true;
    }

    if (endTime != DBStore.UNSPECIFIED_DATE)
    {
      builder.append(where ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      builder.append(CDODBSchema.COMMIT_INFOS_TIMESTAMP);
      builder.append("<="); //$NON-NLS-1$
      builder.append(endTime);
      where = true;
    }

    builder.append(" ORDER BY "); //$NON-NLS-1$
    builder.append(CDODBSchema.COMMIT_INFOS_TIMESTAMP);
    String sql = builder.toString();

    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    InternalRepository repository = getStore().getRepository();
    InternalCDOBranchManager branchManager = repository.getBranchManager();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();

    try
    {
      pstmt = statementCache.getPreparedStatement(sql, ReuseProbability.MEDIUM);

      resultSet = pstmt.executeQuery();
      while (resultSet.next())
      {
        long timeStamp = resultSet.getLong(1);
        long previousTimeStamp = resultSet.getLong(2);
        String userID = resultSet.getString(3);
        String comment = resultSet.getString(4);
        CDOBranch infoBranch = branch;
        if (infoBranch == null)
        {
          int id = resultSet.getInt(5);
          infoBranch = branchManager.getBranch(id);
        }

        CDOCommitInfo commitInfo = commitInfoManager.createCommitInfo(infoBranch, timeStamp, previousTimeStamp, userID,
            comment, null);
        handler.handleCommitInfo(commitInfo);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    return mappingStrategy.readChangeSet(this, segments);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.handleRevisions(this, eClass, branch, timeStamp, exactTime, new DBRevisionHandler(handler));
  }

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    DBStore store = getStore();
    out.writeLong(store.getLastObjectID()); // See bug 325097

    String where = " WHERE " + CDODBSchema.BRANCHES_ID + " BETWEEN " + fromBranchID + " AND " + toBranchID;
    DBUtil.serializeTable(out, connection, CDODBSchema.BRANCHES, null, where);

    where = " WHERE " + CDODBSchema.COMMIT_INFOS_TIMESTAMP + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, CDODBSchema.COMMIT_INFOS, null, where);

    where = " WHERE " + CDODBSchema.EXTERNAL_TIMESTAMP + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, CDODBSchema.EXTERNAL_REFS, null, where);

    IMetaDataManager metaDataManager = store.getMetaDataManager();
    metaDataManager.rawExport(connection, out, fromCommitTime, toCommitTime);

    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    mappingStrategy.rawExport(this, out, fromBranchID, toBranchID, fromCommitTime, toCommitTime);
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    DBStore store = getStore();
    store.setLastObjectID(in.readLong()); // See bug 325097

    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    int size = mappingStrategy.getClassMappings().size();
    int commitWork = 4;
    monitor.begin(commitWork + size + commitWork);

    try
    {
      DBUtil.deserializeTable(in, connection, CDODBSchema.BRANCHES, monitor.fork());
      DBUtil.deserializeTable(in, connection, CDODBSchema.COMMIT_INFOS, monitor.fork());
      DBUtil.deserializeTable(in, connection, CDODBSchema.EXTERNAL_REFS, monitor.fork());
      rawImportPackageUnits(in, fromCommitTime, toCommitTime, monitor.fork());

      mappingStrategy.rawImport(this, in, fromCommitTime, toCommitTime, monitor.fork(size));

      Async async = monitor.forkAsync(commitWork);

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
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void rawImportPackageUnits(CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor)
      throws IOException
  {
    monitor.begin(2);

    try
    {
      DBStore store = getStore();
      IMetaDataManager metaDataManager = store.getMetaDataManager();
      Collection<InternalCDOPackageUnit> packageUnits = metaDataManager.rawImport(getConnection(), in, fromCommitTime,
          toCommitTime, monitor.fork());

      InternalRepository repository = store.getRepository();
      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);

      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        packageRegistry.putPackageUnit(packageUnit);
      }

      IMappingStrategy mappingStrategy = store.getMappingStrategy();
      mappingStrategy.createMapping(connection, packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]),
          monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  public void rawStore(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    writePackageUnits(packageUnits, monitor);
  }

  public void rawStore(InternalCDORevision revision, OMMonitor monitor)
  {
    writeRevision(revision, true, monitor);
  }

  public void rawStore(byte[] id, long size, InputStream inputStream) throws IOException
  {
    writeBlob(id, size, inputStream);
  }

  public void rawStore(byte[] id, long size, Reader reader) throws IOException
  {
    writeClob(id, size, reader);
  }

  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment,
      OMMonitor monitor)
  {
    writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, monitor);
  }

  public void rawDelete(CDOID id, int version, CDOBranch branch, EClass eClass, OMMonitor monitor)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    if (eClass == null)
    {
      eClass = getObjectType(id);
    }

    IClassMapping mapping = mappingStrategy.getClassMapping(eClass);
    mapping.detachObject(this, id, version, branch, CDOBranchPoint.UNSPECIFIED_DATE, monitor.fork());
  }

  public void rawCommit(OMMonitor monitor)
  {
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
    }
  }

  /**
   * @author Stefan Winkler
   */
  private class ConnectionKeepAliveTask extends TimerTask
  {
    public static final long EXECUTION_PERIOD = 1000 * 60 * 60 * 4; // 4 hours

    @Override
    public void run()
    {
      Statement stmt = null;

      try
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("DB connection keep-alive task activated"); //$NON-NLS-1$
        }

        stmt = connection.createStatement();
        stmt.executeQuery("SELECT 1 FROM " + CDODBSchema.PROPERTIES); //$NON-NLS-1$
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
  }
}
