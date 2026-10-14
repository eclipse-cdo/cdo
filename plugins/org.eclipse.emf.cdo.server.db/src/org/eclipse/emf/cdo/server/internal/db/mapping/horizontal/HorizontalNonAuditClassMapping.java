/*
 * Copyright (c) 2009-2013, 2015, 2016, 2018, 2019, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
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
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateSetStep;
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
    List<SQLSelectItem> attributes = new ArrayList<>();
    attributes.add(versionField);
    attributes.add(createdField);
    attributes.add(revisedField);
    attributes.add(resourceField);
    attributes.add(containerField);
    attributes.add(featureField);

    for (ITypeMapping typeMapping : getValueMappings())
    {
      attributes.add(typeMapping.getField());
    }

    if (getUnsettableFields() != null)
    {
      attributes.addAll(getUnsettableFields().values());
    }

    if (getListSizeFields() != null)
    {
      attributes.addAll(getListSizeFields().values());
    }
    sqlSelectCurrentAttributes = SQL.select(attributes.get(0), attributes.subList(1, attributes.size()).toArray(new SQLSelectItem[attributes.size() - 1])) //
        .from(table) //
        .where(idField).eq(SQL.param()) //
        .compile(getDBAdapter()).getSQL();

    sqlSelectCurrentVersion = SQL.select(versionField) //
        .from(table) //
        .where(idField).eq(SQL.param()) //
        .compile(getDBAdapter()).getSQL();

    List<IDBField> insertFields = new ArrayList<>();
    insertFields.add(idField);
    insertFields.add(versionField);
    insertFields.add(createdField);
    insertFields.add(revisedField);
    insertFields.add(resourceField);
    insertFields.add(containerField);
    insertFields.add(featureField);

    for (ITypeMapping typeMapping : getValueMappings())
    {
      insertFields.add(typeMapping.getField());
    }

    if (getUnsettableFields() != null)
    {
      insertFields.addAll(getUnsettableFields().values());
    }

    if (getListSizeFields() != null)
    {
      insertFields.addAll(getListSizeFields().values());
    }
    List<SQLExpression> insertValues = new ArrayList<>();
    for (int i = 0; i < insertFields.size(); i++)
    {
      insertValues.add(SQL.param());
    }

    sqlInsertAttributes = SQL.insertInto(table) //
        .columns(insertFields.get(0), insertFields.subList(1, insertFields.size()).toArray(new IDBField[insertFields.size() - 1])) //
        .values(insertValues.get(0), insertValues.subList(1, insertValues.size()).toArray(new SQLExpression[insertValues.size() - 1])) //
        .compile(getDBAdapter()).getSQL();

    sqlSelectAllObjectIDs = SQL.select(idField) //
        .from(table) //
        .compile(getDBAdapter()).getSQL();

    sqlDelete = SQL.deleteFrom(table) //
        .where(idField).eq(SQL.param()) //
        .compile(getDBAdapter()).getSQL();
  }

  @Override
  protected void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    IIDHandler idHandler = getStore().getIDHandler();
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

    IBatchingContext batchingContext = accessor.getBatchingContext();
    BatchedStatement stmt = batchingContext.createStatement(sqlInsertAttributes, ReuseProbability.HIGH, "NonAuditClass.attribute"); //$NON-NLS-1$

    boolean discarded = false;

    try
    {
      IIDHandler idHandler = getStore().getIDHandler();
      for (InternalCDORevision revision : revisions)
      {
        setInsertValues(idHandler, stmt, revision);
        stmt.executeUpdate();
      }

      batchingContext.flushPhase();
      validateExactlyOne(stmt, revisions.length, "Unexpected attribute insert result"); //$NON-NLS-1$
    }
    catch (SQLException ex)
    {
      batchingContext.discardStatement(stmt);
      discarded = true;
      throw new DBException(ex);
    }
    finally
    {
      if (!discarded)
      {
        batchingContext.releaseStatement(stmt);
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
    if (table == null)
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

    SQLCondition condition = SQL.gt(versionField, SQL.ZERO).and(SQL.eq(containerField, SQL.param()));
    if (name == null)
    {
      condition = condition.and(SQL.isNull(nameValueMapping.getField()));
    }
    else
    {
      condition = condition.and(exactMatch ? SQL.eq(nameValueMapping.getField(), SQL.param()) : SQL.like(nameValueMapping.getField(), SQL.param()));
    }

    IIDHandler idHandler = getStore().getIDHandler();
    String sql = SQL.select(idField) //
        .from(table) //
        .where(condition) //
        .compile(getDBAdapter()).getSQL();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.MEDIUM);

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

    DBStore store = (DBStore)getStore();
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
        boolean success = readValuesFromStatement(stmtAttributes, revision, accessor, listChunk);

        if (hasLists)
        {
          // Read multival tables only if revision exists
          if (success)
          {
            int currentVersion;

            boolean complete;
            try
            {
              complete = readLists(accessor, revision, listChunk);
              currentVersion = readVersion(stmtVersion);
            }
            catch (IndexOutOfBoundsException ex)
            {
              // A commit has appended list rows after the list size has been read in readValuesFromStatement().
              // Trigger start from scratch below.
              revision.clearValues(); // Make sure that lists are recreated
              continue;
            }

            if (!complete)
            {
              throw new DBException(new IllegalStateException("Incomplete list values in a fully loaded revision")); //$NON-NLS-1$
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

    return getDBAdapter().quoteIdentifier(revisedField.getName()) + "=0";
  }

  @Override
  protected SQLCondition getScalarXRefsCondition(QueryXRefsContext context)
  {
    if (CDORevision.UNSPECIFIED_DATE != context.getTimeStamp())
    {
      throw new IllegalArgumentException("Non-audit mode does not support timestamp specification");
    }

    if (!context.getBranch().isMainBranch())
    {
      throw new IllegalArgumentException("Non-audit mode does not support branch specification");
    }

    return SQL.eq(revisedField, SQL.ZERO);
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
    IIDHandler idHandler = getStore().getIDHandler();
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

    private final Map<String, BatchedStatement> statements = new LinkedHashMap<>();

    private final Map<BatchedStatement, Integer> counts = new LinkedHashMap<>();

    private boolean discarded;

    private NonAuditAttributeDeltaBatch(IDBStoreAccessor accessor)
    {
      this.accessor = accessor;
    }

    public void update(String sql, FeatureDeltaWriter writer)
    {
      try
      {
        BatchedStatement stmt = statements.get(sql);
        if (stmt == null)
        {
          stmt = accessor.getBatchingContext().createStatement(sql, ReuseProbability.MEDIUM, "NonAuditClass.attributeDelta"); //$NON-NLS-1$
          statements.put(sql, stmt);
          counts.put(stmt, 0);
        }

        writer.setUpdateValues(stmt);
        stmt.executeUpdate();
        counts.put(stmt, counts.get(stmt) + 1);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void flushPhase()
    {
      accessor.getBatchingContext().flushPhase();
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
          accessor.getBatchingContext().releaseStatement(stmt);
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
          accessor.getBatchingContext().discardStatement(stmt);
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
      IIDHandler idHandler = getStore().getIDHandler();
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
      SQLUpdateSetStep update = SQL.update(table) //
          .set(versionField).to(SQL.param()) //
          .set(createdField).to(SQL.param());

      if (updateContainer)
      {
        update = update.set(resourceField).to(SQL.param()) //
            .set(containerField).to(SQL.param()) //
            .set(featureField).to(SQL.param());
      }

      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        ITypeMapping typeMapping = change.getElement1();
        update = update.set(typeMapping.getField()).to(SQL.param());

        if (typeMapping.getFeature().isUnsettable())
        {
          update = update.set(getUnsettableFields().get(typeMapping.getFeature())).to(SQL.param());
        }
      }

      for (Pair<EStructuralFeature, Integer> change : listSizeChanges)
      {
        EStructuralFeature feature = change.getElement1();
        update = update.set(getListSizeFields().get(feature)).to(SQL.param());
      }

      return update //
          .where(idField).eq(SQL.param()) //
          .compile(getDBAdapter()).getSQL();
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
