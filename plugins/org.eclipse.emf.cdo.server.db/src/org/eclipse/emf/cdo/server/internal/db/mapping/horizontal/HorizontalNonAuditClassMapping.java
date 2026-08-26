/*
 * Copyright (c) 2009-2013, 2015, 2016, 2018, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.DBBatchingContext;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.DBStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractBasicListTableMapping.AbstractListDeltaWriter.NewListSizeResult;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalNonAuditClassMapping extends AbstractHorizontalClassMapping implements IClassMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalNonAuditClassMapping.class);

  private String sqlSelectAllObjectIDs;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectCurrentVersion;

  private String sqlInsertAttributes;

  private String sqlUpdateAffix;

  private String sqlUpdatePrefix;

  private String sqlUpdateContainerPart;

  private String sqlDelete;

  private boolean hasLists;

  public HorizontalNonAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);
  }

  @Override
  protected void initSQLStrings()
  {
    hasLists = !getListMappings().isEmpty();

    super.initSQLStrings();

    // ----------- Select Revision ---------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(createdField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(resourceField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(featureField);
    appendTypeMappingNames(builder, getValueMappings());
    appendFieldNames(builder, getUnsettableFields());
    appendFieldNames(builder, getListSizeFields());
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectCurrentAttributes = builder.toString();

    // ----------- Select Version ---------------------------
    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectCurrentVersion = builder.toString();

    // ----------- Insert Attributes -------------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append("("); //$NON-NLS-1$
    builder.append(idField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(createdField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(resourceField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(featureField);
    appendTypeMappingNames(builder, getValueMappings());
    appendFieldNames(builder, getUnsettableFields());
    appendFieldNames(builder, getListSizeFields());
    builder.append(") VALUES (?, ?, ?, ?, ?, ?, ?"); //$NON-NLS-1$
    appendTypeMappingParameters(builder, getValueMappings());
    appendFieldParameters(builder, getUnsettableFields());
    appendFieldParameters(builder, getListSizeFields());
    builder.append(")"); //$NON-NLS-1$
    sqlInsertAttributes = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    sqlSelectAllObjectIDs = builder.toString();

    // ----------- Update attributes --------------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append("=?, "); //$NON-NLS-1$
    builder.append(createdField);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdatePrefix = builder.toString();

    builder = new StringBuilder(", "); //$NON-NLS-1$
    builder.append(resourceField);
    builder.append("=?, "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append("=?, "); //$NON-NLS-1$
    builder.append(featureField);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateContainerPart = builder.toString();

    builder = new StringBuilder(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateAffix = builder.toString();

    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=? "); //$NON-NLS-1$
    sqlDelete = builder.toString();
  }

  @Override
  protected void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsertAttributes, ReuseProbability.HIGH);

    try
    {
      setInsertValues(idHandler, stmt, revision);

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  protected void writeValues(IDBStoreAccessor accessor, InternalCDORevision[] revisions)
  {
    if (revisions.length == 0)
    {
      return;
    }

    DBBatchingContext batchingContext = ((DBStoreAccessor)accessor).getBatchingContext();
    BatchedStatement stmt = DBUtil.batched(accessor.getDBConnection().prepareStatement(sqlInsertAttributes, ReuseProbability.HIGH),
        batchingContext.getStatementBatchSize());
    batchingContext.manage(stmt);
    boolean discarded = false;

    try
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      for (InternalCDORevision revision : revisions)
      {
        setInsertValues(idHandler, stmt, revision);
        stmt.executeUpdate();
        batchingContext.afterAdd(stmt);
      }

      batchingContext.flushPhase();
      validateExactlyOne(stmt, revisions.length, "Unexpected attribute insert result"); //$NON-NLS-1$
    }
    catch (SQLException ex)
    {
      batchingContext.discard(stmt);
      discarded = true;
      throw new DBException(ex);
    }
    finally
    {
      if (!discarded)
      {
        batchingContext.release(stmt);
      }
    }
  }

  private void setInsertValues(IIDHandler idHandler, PreparedStatement stmt, InternalCDORevision revision) throws SQLException
  {
    int column = 1;
    idHandler.setCDOID(stmt, column++, revision.getID());
    stmt.setInt(column++, revision.getVersion());
    stmt.setLong(column++, revision.getTimeStamp());
    stmt.setLong(column++, revision.getRevised());
    idHandler.setCDOID(stmt, column++, revision.getResourceID());
    idHandler.setCDOID(stmt, column++, (CDOID)revision.getContainerID());
    stmt.setInt(column++, revision.getContainerFeatureID());

    int isSetCol = column + getValueMappings().size();
    for (ITypeMapping mapping : getValueMappings())
    {
      EStructuralFeature feature = mapping.getFeature();
      if (feature.isUnsettable())
      {
        if (revision.getValue(feature) == null)
        {
          stmt.setBoolean(isSetCol++, false);
          mapping.setDefaultValue(stmt, column++);
          continue;
        }

        stmt.setBoolean(isSetCol++, true);
      }

      mapping.setValueFromRevision(stmt, column++, revision);
    }

    Map<EStructuralFeature, IDBField> listSizeFields = getListSizeFields();
    if (listSizeFields != null)
    {
      column = isSetCol;
      for (EStructuralFeature feature : listSizeFields.keySet())
      {
        CDOList list = revision.getListOrNull(feature);
        stmt.setInt(column++, list == null ? UNSET_LIST : list.size());
      }
    }
  }

  private void validateExactlyOne(BatchedStatement stmt, int expectedCount, String message)
  {
    int knownResult = stmt.getTotalResult();
    int unknownResultCount = stmt.getUnknownResultCount();
    if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
    {
      throw new DBException(message);
    }
  }

  @Override
  public IDBPreparedStatement createObjectIDStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIDs); //$NON-NLS-1$
    }

    return accessor.getDBConnection().prepareStatement(sqlSelectAllObjectIDs, ReuseProbability.HIGH);
  }

  @Override
  public IDBPreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name, boolean exactMatch,
      CDOBranchPoint branchPoint)
  {
    if (getTable() == null)
    {
      return null;
    }

    long timeStamp = branchPoint.getTimeStamp();
    if (timeStamp != CDORevision.UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Non-audit store does not support explicit timeStamp in resource query"); //$NON-NLS-1$
    }

    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    ITypeMapping nameValueMapping = getValueMapping(nameFeature);
    if (nameValueMapping == null)
    {
      throw new ImplementationError(nameFeature + " not found in ClassMapping " + this); //$NON-NLS-1$
    }

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(">0 AND "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(nameValueMapping.getField());
    if (name == null)
    {
      builder.append(" IS NULL"); //$NON-NLS-1$
    }
    else
    {
      builder.append(exactMatch ? "=? " : " LIKE ? "); //$NON-NLS-1$ //$NON-NLS-2$
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(builder.toString(), ReuseProbability.MEDIUM);

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, folderId);

      if (name != null)
      {
        String queryName = exactMatch ? name : name + "%"; //$NON-NLS-1$
        nameValueMapping.setValue(stmt, column++, queryName);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Created Resource Query: {0}", stmt.toString()); //$NON-NLS-1$
      }

      return stmt;
    }
    catch (Throwable ex)
    {
      DBUtil.close(stmt); // only release on error
      throw new DBException(ex);
    }
  }

  @Override
  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    long timeStamp = revision.getTimeStamp();
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      throw new UnsupportedOperationException("Mapping strategy does not support audits"); //$NON-NLS-1$
    }

    CDOID id = revision.getID();

    DBStore store = (DBStore)getMappingStrategy().getStore();
    IIDHandler idHandler = store.getIDHandler();
    IDBPreparedStatement stmtVersion = null;
    IDBPreparedStatement stmtAttributes = accessor.getDBConnection().prepareStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);

    try
    {
      if (hasLists)
      {
        // Reading all list rows of an object is not atomic.
        // After all row reads are done, check the revision version again (see below).
        stmtVersion = accessor.getDBConnection().prepareStatement(sqlSelectCurrentVersion, ReuseProbability.HIGH);
        stmtVersion.setMaxRows(1); // Optimization: only 1 row
        idHandler.setCDOID(stmtVersion, 1, id);
      }

      idHandler.setCDOID(stmtAttributes, 1, id);

      for (;;)
      {
        // Read singleval-attribute table always (even without modeled attributes!)
        boolean success = readValuesFromStatement(stmtAttributes, revision, accessor);

        if (hasLists)
        {
          // Read multival tables only if revision exists
          if (success)
          {
            int currentVersion;

            try
            {
              readLists(accessor, revision, listChunk);
              currentVersion = readVersion(stmtVersion);
            }
            catch (IndexOutOfBoundsException ex)
            {
              // A commit has appended list rows after the list size has been read in readValuesFromStatement().
              // Trigger start from scratch below.
              currentVersion = CDOBranchVersion.UNSPECIFIED_VERSION;
            }

            if (currentVersion != revision.getVersion())
            {
              // A commit has changed the revision while reading the lists. Start from scratch!
              revision.clearValues(); // Make sure that lists are recreated
              continue;
            }
          }
        }

        return success;
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmtAttributes);
      DBUtil.close(stmtVersion);
    }
  }

  private int readVersion(IDBPreparedStatement stmt)
  {
    ResultSet resultSet = null;

    try
    {
      resultSet = stmt.executeQuery();
      if (resultSet.next())
      {
        return resultSet.getInt(1);
      }

      return CDOBranchVersion.UNSPECIFIED_VERSION;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  @Override
  protected void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long timeStamp)
  {
    // do nothing
  }

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    if (CDORevision.UNSPECIFIED_DATE != context.getTimeStamp())
    {
      throw new IllegalArgumentException("Non-audit mode does not support timestamp specification");
    }

    if (!context.getBranch().isMainBranch())
    {
      throw new IllegalArgumentException("Non-audit mode does not support branch specification");
    }

    return revisedField + "=0";
  }

  @Override
  protected void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    rawDelete(accessor, id, version, branch, monitor);
    getMappingStrategy().removeObjectType(accessor, id);
  }

  @Override
  protected void rawDeleteAttributes(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version, OMMonitor monitor)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlDelete, ReuseProbability.HIGH);

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created, OMMonitor monitor)
  {
    Async async = null;
    monitor.begin();

    try
    {
      try
      {
        async = monitor.forkAsync();

        FeatureDeltaWriter writer = new FeatureDeltaWriter();
        writer.process(accessor, delta, created);
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void writeRevisionDeltasSingle(IDBStoreAccessor accessor, InternalCDORevisionDelta[] deltas, long created, OMMonitor monitor)
  {
    for (IListMapping mapping : getListMappings())
    {
      if (!(mapping instanceof NonAuditListTableMapping))
      {
        super.writeRevisionDeltasSingle(accessor, deltas, created, monitor);
        return;
      }
    }

    List<FeatureDeltaWriter> writers = new ArrayList<>();
    NonAuditAttributeDeltaBatch attributeBatch = new NonAuditAttributeDeltaBatch(accessor);
    monitor.begin(1 + getListMappings().size());
    try
    {
      OMMonitor revisionMonitor = monitor.fork();
      revisionMonitor.begin(deltas.length);
      try
      {
        for (InternalCDORevisionDelta delta : deltas)
        {
          FeatureDeltaWriter writer = new FeatureDeltaWriter(attributeBatch);
          writer.deferListDeltas = true;
          writer.process(accessor, delta, created);
          writers.add(writer);
          revisionMonitor.worked();
        }
      }
      finally
      {
        revisionMonitor.done();
      }

      attributeBatch.flushPhase();

      for (IListMapping mapping : getListMappings())
      {
        List<ListDeltaWork> work = new ArrayList<>();
        for (FeatureDeltaWriter writer : writers)
        {
          for (ListDeltaWork item : writer.listDeltaWork)
          {
            if (item.getDelta().getFeature() == mapping.getFeature())
            {
              work.add(item);
            }
          }
        }

        OMMonitor listMonitor = monitor.fork();
        if (work.isEmpty())
        {
          listMonitor.worked();
        }
        else
        {
          ListDeltaWork[] items = work.toArray(new ListDeltaWork[work.size()]);
          if (mapping instanceof IListMappingBatchingSupport)
          {
            ((IListMappingBatchingSupport)mapping).processDeltas(accessor, items, listMonitor);
          }
          else
          {
            listMonitor.begin(items.length);
            try
            {
              IListMappingDeltaSupport deltaSupport = (IListMappingDeltaSupport)mapping;
              for (ListDeltaWork item : items)
              {
                deltaSupport.processDelta(accessor, item.getID(), item.getBranchId(), item.getOldVersion(), item.getNewVersion(), item.getCreated(),
                    item.getDelta());
                listMonitor.worked();
              }
            }
            finally
            {
              listMonitor.done();
            }
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      attributeBatch.discard();
      throw ex;
    }
    finally
    {
      attributeBatch.release();
      monitor.done();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class NonAuditAttributeDeltaBatch
  {
    private final IDBStoreAccessor accessor;

    private final DBBatchingContext batchingContext;

    private final Map<String, BatchedStatement> statements = new LinkedHashMap<>();

    private final Map<BatchedStatement, Integer> counts = new LinkedHashMap<>();

    private boolean discarded;

    private NonAuditAttributeDeltaBatch(IDBStoreAccessor accessor)
    {
      this.accessor = accessor;
      batchingContext = ((DBStoreAccessor)accessor).getBatchingContext();
    }

    public void update(String sql, FeatureDeltaWriter writer)
    {
      try
      {
        BatchedStatement stmt = statements.get(sql);
        if (stmt == null)
        {
          stmt = DBUtil.batched(accessor.getDBConnection().prepareStatement(sql, ReuseProbability.MEDIUM), batchingContext.getStatementBatchSize());
          statements.put(sql, stmt);
          counts.put(stmt, 0);
          batchingContext.manage(stmt);
        }

        writer.setUpdateValues(stmt);
        stmt.executeUpdate();
        counts.put(stmt, counts.get(stmt) + 1);
        batchingContext.afterAdd(stmt);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void flushPhase()
    {
      batchingContext.flushPhase();
      for (Map.Entry<BatchedStatement, Integer> entry : counts.entrySet())
      {
        validateExactlyOne(entry.getKey(), entry.getValue());
      }
    }

    public void release()
    {
      if (!discarded)
      {
        for (BatchedStatement stmt : statements.values())
        {
          batchingContext.release(stmt);
        }
      }
    }

    public void discard()
    {
      if (!discarded)
      {
        discarded = true;
        for (BatchedStatement stmt : statements.values())
        {
          batchingContext.discard(stmt);
        }
      }
    }

    private void validateExactlyOne(BatchedStatement stmt, int expectedCount)
    {
      int knownResult = stmt.getTotalResult();
      int unknownResultCount = stmt.getUnknownResultCount();
      if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
      {
        throw new DBException("Unexpected attribute update result"); //$NON-NLS-1$
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FeatureDeltaWriter extends AbstractFeatureDeltaWriter
  {
    private final NonAuditAttributeDeltaBatch attributeBatch;

    private final List<Pair<ITypeMapping, Object>> attributeChanges = new ArrayList<>();

    private final List<Pair<EStructuralFeature, Integer>> listSizeChanges = new ArrayList<>();

    private final List<ListDeltaWork> listDeltaWork = new ArrayList<>();

    private boolean deferListDeltas;

    private int oldVersion;

    private boolean updateContainer;

    private int newContainingFeatureID;

    private CDOID newContainerID;

    private CDOID newResourceID;

    private int branchId;

    private int newVersion;

    public FeatureDeltaWriter()
    {
      this(null);
    }

    public FeatureDeltaWriter(NonAuditAttributeDeltaBatch attributeBatch)
    {
      this.attributeBatch = attributeBatch;
    }

    @Override
    protected void doProcess(InternalCDORevisionDelta delta)
    {
      // Set context
      id = delta.getID();

      branchId = delta.getBranch().getID();
      oldVersion = delta.getVersion();
      newVersion = oldVersion + 1;

      // Process revision delta tree
      delta.accept(this);

      updateAttributes();
    }

    @Override
    public void visit(CDOSetFeatureDelta delta)
    {
      if (delta.getFeature().isMany())
      {
        throw new ImplementationError("Should not be called"); //$NON-NLS-1$
      }

      ITypeMapping am = getValueMapping(delta.getFeature());
      if (am == null)
      {
        throw new IllegalArgumentException("AttributeMapping for " + delta.getFeature() + " is null!"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      attributeChanges.add(Pair.create(am, delta.getValue()));
    }

    @Override
    public void visit(CDOUnsetFeatureDelta delta)
    {
      // TODO: correct this when DBStore implements unsettable features
      // see Bugs 259868 and 263010
      ITypeMapping tm = getValueMapping(delta.getFeature());
      attributeChanges.add(Pair.create(tm, null));
    }

    @Override
    public void visit(CDOListFeatureDelta delta)
    {
      EStructuralFeature feature = delta.getFeature();
      int oldSize = delta.getOriginSize();
      int newSize = -1;

      try
      {
        IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(feature);
        if (deferListDeltas)
        {
          int newListSize = ((NonAuditListTableMapping)listMapping).planDelta(accessor, id, delta);
          listDeltaWork.add(new ListDeltaWork(id, branchId, oldVersion, oldVersion + 1, created, delta, newListSize));
          newSize = newListSize;
        }
        else
        {
          listMapping.processDelta(accessor, id, branchId, oldVersion, oldVersion + 1, created, delta);
        }
      }
      catch (NewListSizeResult result)
      {
        newSize = result.getNewListSize();
      }

      if (oldSize != newSize)
      {
        listSizeChanges.add(Pair.create(feature, newSize));
      }
    }

    @Override
    public void visit(CDOContainerFeatureDelta delta)
    {
      newContainingFeatureID = delta.getContainerFeatureID();
      newContainerID = (CDOID)delta.getContainerID();
      newResourceID = delta.getResourceID();
      updateContainer = true;
    }

    private void updateAttributes()
    {
      String sql = buildUpdateSQL();
      if (attributeBatch != null)
      {
        attributeBatch.update(sql, this);
        return;
      }

      IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.MEDIUM);

      try
      {
        setUpdateValues(stmt);

        DBUtil.update(stmt, true);
      }
      catch (SQLException e)
      {
        throw new DBException(e);
      }
      finally
      {
        DBUtil.close(stmt);
      }
    }

    private void setUpdateValues(PreparedStatement stmt) throws SQLException
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      int column = 1;
      stmt.setInt(column++, newVersion);
      stmt.setLong(column++, created);
      if (updateContainer)
      {
        idHandler.setCDOID(stmt, column++, newResourceID, created);
        idHandler.setCDOID(stmt, column++, newContainerID, created);
        stmt.setInt(column++, newContainingFeatureID);
      }

      column = setUpdateAttributeValues(attributeChanges, stmt, column);
      column = setUpdateListSizeChanges(listSizeChanges, stmt, column);
      idHandler.setCDOID(stmt, column, id);
    }

    private String buildUpdateSQL()
    {
      StringBuilder builder = new StringBuilder(sqlUpdatePrefix);
      if (updateContainer)
      {
        builder.append(sqlUpdateContainerPart);
      }

      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        builder.append(", "); //$NON-NLS-1$
        ITypeMapping typeMapping = change.getElement1();
        builder.append(typeMapping.getField());
        builder.append("=?"); //$NON-NLS-1$

        if (typeMapping.getFeature().isUnsettable())
        {
          builder.append(", "); //$NON-NLS-1$
          builder.append(getUnsettableFields().get(typeMapping.getFeature()));
          builder.append("=?"); //$NON-NLS-1$
        }
      }

      for (Pair<EStructuralFeature, Integer> change : listSizeChanges)
      {
        builder.append(", "); //$NON-NLS-1$
        EStructuralFeature feature = change.getElement1();
        builder.append(getListSizeFields().get(feature));
        builder.append("=?"); //$NON-NLS-1$
      }

      builder.append(sqlUpdateAffix);
      return builder.toString();
    }

    private int setUpdateAttributeValues(List<Pair<ITypeMapping, Object>> attributeChanges, PreparedStatement stmt, int col) throws SQLException
    {
      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        ITypeMapping typeMapping = change.getElement1();
        Object value = change.getElement2();
        if (typeMapping.getFeature().isUnsettable())
        {
          // feature is unsettable
          if (value == null)
          {
            // feature is unset
            typeMapping.setDefaultValue(stmt, col++);
            stmt.setBoolean(col++, false);
          }
          else
          {
            // feature is set
            typeMapping.setValue(stmt, col++, value);
            stmt.setBoolean(col++, true);
          }
        }
        else
        {
          typeMapping.setValue(stmt, col++, change.getElement2());
        }
      }

      return col;
    }

    private int setUpdateListSizeChanges(List<Pair<EStructuralFeature, Integer>> attributeChanges, PreparedStatement stmt, int col) throws SQLException
    {
      for (Pair<EStructuralFeature, Integer> change : listSizeChanges)
      {
        stmt.setInt(col++, change.getElement2());
      }

      return col;
    }
  }
}
