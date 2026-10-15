/*
 * Copyright (c) 2009-2016, 2018, 2019, 2022, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 249610: [DB] Support external references (Implementation)
 *    Lothar Werzinger - Bug 296440: [DB] Change RDB schema to improve scalability of to-many references in audit mode
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingUnitSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingUnitSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.StubCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.view.CDOUnit;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBResultSet;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.fluent.SQLSelectFromStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectWhereStep;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalAuditClassMapping extends AbstractHorizontalClassMapping
    implements IClassMappingAuditSupport, IClassMappingDeltaSupport, IClassMappingUnitSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalAuditClassMapping.class);

  private static final ContextTracer TRACER_UNITS = new ContextTracer(OM.DEBUG_UNITS, HorizontalAuditClassMapping.class);

  private static final Config BASE_CHUNK_LOADING_CONFIG = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, 0);

  @ExcludeFromDump
  private transient SQLStatement sqlInsertAttributes;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAttributesCurrent;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAttributesByTime;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAttributesByVersion;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectUnitByTime;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAllObjectIDs;

  @ExcludeFromDump
  private transient SQLStatement sqlReviseAttributes;

  @ExcludeFromDump
  private transient SQLStatement sqlRawDeleteAttributes;

  private BatchedStatement reviseAttributesBatch;

  public HorizontalAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);
  }

  @Override
  protected void initSQLStrings()
  {
    super.initSQLStrings();

    // ----------- Select Revision ---------------------------
    sqlSelectAttributesCurrent = createSelectAttributesSQL(false, 0);
    sqlSelectAttributesByTime = createSelectAttributesSQL(false, 1);
    sqlSelectAttributesByVersion = createSelectAttributesSQL(false, 2);

    InternalRepository repository = (InternalRepository)getStore().getRepository();
    if (repository.isSupportingUnits())
    {
      sqlSelectUnitByTime = createSelectAttributesSQL(true, 1);
    }

    // ----------- Insert Attributes -------------------------
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

    sqlInsertAttributes = SQL.insertInto(table)
        .columns(insertFields.get(0), insertFields.subList(1, insertFields.size()).toArray(new IDBField[insertFields.size() - 1]))
        .values(insertValues.get(0), insertValues.subList(1, insertValues.size()).toArray(new SQLExpression[insertValues.size() - 1])).compile(getDBAdapter());

    // ----------- Update to set revised ----------------
    sqlReviseAttributes = SQL.update(table) //
        .set(revisedField).to(SQL.param()) //
        .where(SQL.eq(idField, SQL.param()) //
            .and(SQL.eq(revisedField, SQL.ZERO))) //
        .compile(getDBAdapter());

    // ----------- Select all unrevised Object IDs ------
    sqlSelectAllObjectIDs = SQL.select(idField) //
        .from(table) //
        .where(revisedField).eq(SQL.ZERO) //
        .compile(getDBAdapter());

    // ----------- Raw delete one specific revision ------
    sqlRawDeleteAttributes = SQL.deleteFrom(table) //
        .where(SQL.eq(idField, SQL.param()) //
            .and(SQL.eq(versionField, SQL.param()))) //
        .compile(getDBAdapter());
  }

  @Override
  protected void appendSelectForHandleFields(List<SQLSelectItem> items)
  {
    items.add(createdField);
    items.add(revisedField);
  }

  private SQLStatement createSelectAttributesSQL(boolean forUnits, int selection)
  {
    List<SQLSelectItem> attributes = new ArrayList<>();

    if (forUnits)
    {
      attributes.add(idField);
    }

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

    SQLSelectItem[] otherAttributes = attributes.subList(1, attributes.size()).toArray(new SQLSelectItem[attributes.size() - 1]);
    SQLSelectFromStep query = SQL.select(attributes.get(0), otherAttributes).from(table);
    SQLCondition condition;

    if (forUnits)
    {
      UnitMappingTable units = ((DBStore)getStore()).getUnitMappingTable();
      query = query.from(units);
      condition = SQL.eq(idField, units.elem()).and(SQL.eq(units.unit(), SQL.param(getStore().getIDHandler())));
    }
    else
    {
      condition = SQL.eq(idField, SQL.param(getStore().getIDHandler()));
    }

    if (selection == 0)
    {
      condition = condition.and(SQL.eq(revisedField, SQL.ZERO));
    }
    else if (selection == 1)
    {
      condition = condition.and(SQL.le(createdField, SQL.param())).and(SQL.eq(revisedField, SQL.ZERO).or(SQL.ge(revisedField, SQL.param())));

      if (forUnits)
      {
        condition = condition.and(SQL.gt(versionField, SQL.ZERO));
      }
    }
    else
    {
      condition = condition.and(SQL.eq(SQL.abs(versionField), SQL.param()));
    }

    SQLSelectWhereStep whereQuery = query.where(condition);

    if (forUnits)
    {
      return whereQuery //
          .orderBy(SQL.asc(idField)) //
          .compile(getDBAdapter());
    }

    return whereQuery.compile(getDBAdapter());
  }

  @Override
  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IDBPreparedStatement stmt = null;

    try
    {
      long timeStamp = revision.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        stmt = DBUtil.prepare(sqlSelectAttributesByTime, accessor.getDBConnection(), ReuseProbability.MEDIUM);
        sqlSelectAttributesByTime.bindParameter(stmt, 1, revision.getID());
        sqlSelectAttributesByTime.bindParameter(stmt, 2, timeStamp);
        sqlSelectAttributesByTime.bindParameter(stmt, 3, timeStamp);
      }
      else
      {
        stmt = DBUtil.prepare(sqlSelectAttributesCurrent, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlSelectAttributesCurrent.bindParameter(stmt, 1, revision.getID());
      }

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(stmt, revision, accessor, listChunk);

      // Read multival tables only if revision exists
      if (success && revision.getVersion() >= CDOBranchVersion.FIRST_VERSION)
      {
        if (!readLists(accessor, revision, listChunk))
        {
          throw new DBException(new IllegalStateException("Incomplete list values in a fully loaded revision")); //$NON-NLS-1$
        }
      }

      return success;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IDBPreparedStatement stmt = DBUtil.prepare(sqlSelectAttributesByVersion, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlSelectAttributesByVersion.bindParameter(stmt, 1, revision.getID());
      sqlSelectAttributesByVersion.bindParameter(stmt, 2, revision.getVersion());

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(stmt, revision, accessor, listChunk);

      // Read multival tables only if revision exists
      if (success)
      {
        if (!readLists(accessor, revision, listChunk))
        {
          throw new DBException(new IllegalStateException("Incomplete list values in a fully loaded revision")); //$NON-NLS-1$
        }
      }

      return success;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public IDBPreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name, boolean exactMatch,
      CDOBranchPoint branchPoint)
  {
    if (table == null)
    {
      return null;
    }

    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();
    long timeStamp = branchPoint.getTimeStamp();

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

    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      condition = condition.and(SQL.eq(revisedField, SQL.ZERO));
    }
    else
    {
      condition = condition.and(SQL.le(createdField, SQL.param())).and(SQL.eq(revisedField, SQL.ZERO).or(SQL.ge(revisedField, SQL.param())));
    }

    SQLStatement sql = SQL.select(idField) //
        .from(table) //
        .where(condition) //
        .compile(getDBAdapter());
    IDBPreparedStatement stmt = DBUtil.prepare(sql, accessor.getDBConnection(), ReuseProbability.MEDIUM);

    try
    {
      int column = 1;
      sql.bindParameter(stmt, column++, folderId, getStore().getIDHandler());

      if (name != null)
      {
        String queryName = exactMatch ? name : name + "%"; //$NON-NLS-1$
        sql.bindParameter(stmt, column++, queryName, nameValueMapping);
      }

      if (timeStamp != CDORevision.UNSPECIFIED_DATE)
      {
        sql.bindParameter(stmt, column++, timeStamp);
        sql.bindParameter(stmt, column++, timeStamp);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Created Resource Query: {0}", stmt); //$NON-NLS-1$
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
  public IDBPreparedStatement createObjectIDStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIDs); //$NON-NLS-1$
    }

    return DBUtil.prepare(sqlSelectAllObjectIDs, accessor.getDBConnection(), ReuseProbability.HIGH);
  }

  @Override
  protected final void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlInsertAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

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
    BatchedStatement stmt = batchingContext.createStatement(sqlInsertAttributes, ReuseProbability.HIGH, "AuditClass.attribute"); //$NON-NLS-1$

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
    SQLParameterBinder values = sqlInsertAttributes.binder(stmt);
    values.bindParameter(revision.getID(), idHandler) //
        .bind(revision.getVersion(), revision.getTimeStamp(), revision.getRevised()) //
        .bindParameter(revision.getResourceID(), idHandler) //
        .bindParameter((CDOID)revision.getContainerID(), idHandler) //
        .bind(revision.getContainerFeatureID());

    int isSetCol = 8 + getValueMappings().size();
    SQLParameterBinder flags = sqlInsertAttributes.binderFrom(stmt, isSetCol);

    for (ITypeMapping mapping : getValueMappings())
    {
      EStructuralFeature feature = mapping.getFeature();
      if (feature.isUnsettable())
      {
        if (revision.getValue(feature) == null)
        {
          flags.bind(false);
          values.bindParameter(mapping);
          continue;
        }

        flags.bind(true);
      }

      values.bindParameter(revision, mapping::setValueFromRevision);
    }

    Map<EStructuralFeature, IDBField> listSizeFields = getListSizeFields();
    if (listSizeFields != null)
    {
      SQLParameterBinder listSizes = sqlInsertAttributes.binderFrom(stmt, flags.getNextIndex());

      for (EStructuralFeature feature : listSizeFields.keySet())
      {
        CDOList list = revision.getListOrNull(feature);
        listSizes.bind(list == null ? UNSET_LIST : list.size());
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
  protected void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp, OMMonitor mon)
  {
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlInsertAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      SQLParameterBinder values = sqlInsertAttributes.binder(stmt);
      values.bindParameter(id, idHandler) //
          .bind(-version, timeStamp, CDOBranchPoint.UNSPECIFIED_DATE) //
          .bindParameter(CDOID.NULL, idHandler) //
          .bindParameter(CDOID.NULL, idHandler) //
          .bind(0);

      int isSetCol = 8 + getValueMappings().size();
      SQLParameterBinder flags = sqlInsertAttributes.binderFrom(stmt, isSetCol);

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          flags.bind(false);
        }

        values.bindParameter(mapping);
      }

      Map<EStructuralFeature, IDBField> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        SQLParameterBinder listSizes = sqlInsertAttributes.binderFrom(stmt, flags.getNextIndex());

        for (int i = 0; i < listSizeFields.size(); i++)
        {
          listSizes.bind(0);
        }
      }

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
  protected void rawDeleteAttributes(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version, OMMonitor fork)
  {
    IDBPreparedStatement stmt = DBUtil.prepare(sqlRawDeleteAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlRawDeleteAttributes.bindParameter(stmt, 1, id, getStore().getIDHandler());
      sqlRawDeleteAttributes.bindParameter(stmt, 2, version);
      DBUtil.update(stmt, false);
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
  protected void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long revised)
  {
    if (reviseAttributesBatch != null)
    {
      try
      {
        sqlReviseAttributes.bindParameter(reviseAttributesBatch, 1, revised);
        sqlReviseAttributes.bindParameter(reviseAttributesBatch, 2, id, getStore().getIDHandler());

        reviseAttributesBatch.executeUpdate();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }

      return;
    }

    IDBPreparedStatement stmt = DBUtil.prepare(sqlReviseAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlReviseAttributes.bindParameter(stmt, 1, revised);
      sqlReviseAttributes.bindParameter(stmt, 2, id, getStore().getIDHandler());

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
  protected void beginRevisionClassSetup(IDBStoreAccessor accessor, int revisionCount, boolean firstRevision, boolean revise)
  {
    if (firstRevision || !revise || revisionCount == 0)
    {
      return;
    }

    reviseAttributesBatch = accessor.getBatchingContext().createStatement(sqlReviseAttributes, ReuseProbability.HIGH, "AuditClass.revise"); //$NON-NLS-1$
  }

  @Override
  protected void finishRevisionClassSetup(IDBStoreAccessor accessor, int revisionCount, boolean firstRevision, boolean revise)
  {
    if (reviseAttributesBatch == null)
    {
      return;
    }

    IBatchingContext batchingContext = accessor.getBatchingContext();

    try
    {
      batchingContext.flushStatement(reviseAttributesBatch);
      validateExactlyOne(reviseAttributesBatch, revisionCount, "Unexpected revision update result"); //$NON-NLS-1$
    }
    finally
    {
      batchingContext.releaseStatement(reviseAttributesBatch);
      reviseAttributesBatch = null;
    }
  }

  @Override
  protected void abortRevisionClassSetup(IDBStoreAccessor accessor)
  {
    if (reviseAttributesBatch != null)
    {
      accessor.getBatchingContext().discardStatement(reviseAttributesBatch);
      reviseAttributesBatch = null;
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
      if (mapping instanceof AuditListTableMappingWithRanges)
      {
        ((AuditListTableMappingWithRanges)mapping).prepareForDeltaBatching(accessor);
      }
    }

    IBatchingContext batchingContext = accessor.getBatchingContext();
    BatchedStatement reviseBatch = batchingContext.createStatement(sqlReviseAttributes, ReuseProbability.HIGH, "AuditClass.reviseDelta"); //$NON-NLS-1$
    BatchedStatement insertBatch = batchingContext.createStatement(sqlInsertAttributes, ReuseProbability.HIGH, "AuditClass.attributeDelta"); //$NON-NLS-1$

    boolean discarded = false;

    List<FeatureDeltaWriter> writers = new ArrayList<>();
    monitor.begin(1 + getListMappings().size());

    try
    {
      OMMonitor revisionMonitor = monitor.fork();
      revisionMonitor.begin(deltas.length);

      for (InternalCDORevisionDelta delta : deltas)
      {
        FeatureDeltaWriter writer = new FeatureDeltaWriter(reviseBatch, insertBatch);
        writer.deferListDeltas = true;
        writer.process(accessor, delta, created);
        writers.add(writer);
        revisionMonitor.worked();
      }

      revisionMonitor.done();

      batchingContext.flushPhase();
      validateExactlyOne(reviseBatch, deltas.length, "Unexpected revision update result"); //$NON-NLS-1$
      validateExactlyOne(insertBatch, deltas.length, "Unexpected attribute insert result"); //$NON-NLS-1$

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

        if (!work.isEmpty())
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
        else
        {
          listMonitor.worked();
        }
      }
    }
    catch (RuntimeException | Error ex)
    {
      batchingContext.discardStatement(reviseBatch);
      batchingContext.discardStatement(insertBatch);
      discarded = true;
      throw ex;
    }
    finally
    {
      if (!discarded)
      {
        batchingContext.releaseStatement(reviseBatch);
        batchingContext.releaseStatement(insertBatch);
      }

      monitor.done();
    }
  }

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    if (CDOBranch.MAIN_BRANCH_ID != context.getBranch().getID())
    {
      throw new IllegalArgumentException("Non-audit mode does not support branch specification");
    }

    StringBuilder builder = new StringBuilder();
    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      appendField(builder, revisedField);
      builder.append("=0"); //$NON-NLS-1$
    }
    else
    {
      appendField(builder, createdField);
      builder.append("<=");
      builder.append(timeStamp);
      builder.append(" AND ("); //$NON-NLS-1$
      appendField(builder, revisedField);
      builder.append("=0 OR "); //$NON-NLS-1$
      appendField(builder, revisedField);
      builder.append(">=");
      builder.append(timeStamp);
      builder.append(")"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  @Override
  protected SQLCondition getScalarXRefsCondition(QueryXRefsContext context)
  {
    if (CDOBranch.MAIN_BRANCH_ID != context.getBranch().getID())
    {
      throw new IllegalArgumentException("Non-audit mode does not support branch specification");
    }

    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      return SQL.eq(revisedField, SQL.ZERO);
    }

    return SQL.le(createdField, SQL.literal(timeStamp)).and(SQL.eq(revisedField, SQL.ZERO).or(SQL.ge(revisedField, SQL.literal(timeStamp))));
  }

  @Override
  public void readUnitRevisions(IDBStoreAccessor accessor, CDOBranchPoint branchPoint, CDOID rootID, CDORevisionHandler revisionHandler) throws SQLException
  {
    DBStore store = (DBStore)getStore();
    InternalRepository repository = store.getRepository();

    CDOBranchPoint head = repository.getBranchManager().getMainBranch().getHead();
    EClass eClass = getEClass();
    long timeStamp = branchPoint.getTimeStamp();

    IIDHandler idHandler = store.getIDHandler();
    IDBPreparedStatement stmt = null;

    int jdbcFetchSize = store.getJDBCFetchSize();
    int oldFetchSize = -1;

    final long start1 = TRACER_UNITS.isEnabled() ? System.currentTimeMillis() : CDOBranchPoint.UNSPECIFIED_DATE;

    try
    {
      stmt = DBUtil.prepare(sqlSelectUnitByTime, accessor.getDBConnection(), ReuseProbability.MEDIUM);
      sqlSelectUnitByTime.bindParameter(stmt, 1, rootID);
      sqlSelectUnitByTime.bindParameter(stmt, 2, timeStamp);
      sqlSelectUnitByTime.bindParameter(stmt, 3, timeStamp);

      AsnychronousListFiller listFiller = new AsnychronousListFiller(accessor, timeStamp, rootID, revisionHandler);
      ConcurrencyUtil.execute(repository, listFiller);

      oldFetchSize = stmt.getFetchSize();
      stmt.setFetchSize(jdbcFetchSize);
      IDBResultSet resultSet = stmt.executeQuery();

      for (;;)
      {
        InternalCDORevision revision = store.createRevision(eClass, null);
        revision.setBranchPoint(head);

        if (!readValuesFromResultSet(resultSet, idHandler, revision, true, CDORevision.UNCHUNKED))
        {
          break;
        }

        listFiller.schedule(revision);
      }

      final long start2 = start1 != CDOBranchPoint.UNSPECIFIED_DATE ? System.currentTimeMillis() : start1;

      listFiller.await();

      if (start1 != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        TRACER_UNITS.format("Read {0} revisions of unit {1}: {2} millis + {3} millis", eClass.getName(), rootID, start2 - start1,
            System.currentTimeMillis() - start2);
      }
    }
    finally
    {
      if (oldFetchSize != -1)
      {
        stmt.setFetchSize(oldFetchSize);
      }

      DBUtil.close(stmt);
    }
  }

  /**
   * Asynchronous filler for list values of revisions read in
   * {@link #readUnitRevisions(IDBStoreAccessor, CDOBranchPoint, CDOID, CDORevisionHandler)}.
   * <p>
   * The filler is executed in a separate thread and reads the list values for each revision read from the database.
   * The revisions are passed to the filler via a blocking queue.
   * The filler reads the list values and then passes the revision to the provided {@link CDORevisionHandler}.
   * <p>
   * The filler is used to avoid blocking the reading of revisions while the list values are read, which can be time-consuming.
   * The filler is also used to avoid blocking the reading of revisions while the list values are read, which can be time-consuming.
   * <p>
   * The filler is only used when reading revisions for a {@link CDOUnit unit},
   * which is why the list mappings can be safely cast to {@link IListMappingUnitSupport}.
   *
   * @author Eike Stepper
   */
  private final class AsnychronousListFiller implements Runnable
  {
    private final BlockingQueue<InternalCDORevision> queue = new LinkedBlockingQueue<>();

    private final CountDownLatch latch = new CountDownLatch(1);

    private final IDBStoreAccessor accessor;

    private final long timeStamp;

    private final CDOID rootID;

    private final DBStore store;

    private final IIDHandler idHandler;

    private final IListMappingUnitSupport[] listMappings;

    private final ResultSet[] resultSets;

    private final CDORevisionHandler revisionHandler;

    private Throwable exception;

    public AsnychronousListFiller(IDBStoreAccessor accessor, long timeStamp, CDOID rootID, CDORevisionHandler revisionHandler)
    {
      this.accessor = accessor;
      this.timeStamp = timeStamp;
      this.rootID = rootID;
      this.revisionHandler = revisionHandler;

      store = (DBStore)accessor.getStore();
      idHandler = store.getIDHandler();

      List<IListMapping> tmp = getListMappings();
      int size = tmp.size();

      listMappings = new IListMappingUnitSupport[size];
      resultSets = new ResultSet[size];

      int i = 0;
      for (IListMapping listMapping : tmp)
      {
        // The filler is only used when reading revisions for a {@link CDOUnit unit},
        // which is why the list mappings can be safely cast to {@link IListMappingUnitSupport}.
        listMappings[i++] = (IListMappingUnitSupport)listMapping;
      }
    }

    public void schedule(InternalCDORevision revision)
    {
      queue.offer(revision);
    }

    public void await() throws SQLException
    {
      // Schedule an end marker revision.
      schedule(new StubCDORevision(getEClass()));

      try
      {
        latch.await();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new TimeoutRuntimeException();
      }
      finally
      {
        for (ResultSet resultSet : resultSets)
        {
          if (resultSet != null)
          {
            Statement statement = resultSet.getStatement();
            DBUtil.close(statement);
          }
        }
      }

      if (exception instanceof RuntimeException)
      {
        throw (RuntimeException)exception;
      }

      if (exception instanceof Error)
      {
        throw (Error)exception;
      }

      if (exception instanceof SQLException)
      {
        throw (SQLException)exception;
      }

      if (exception instanceof Exception)
      {
        throw WrappedException.wrap((Exception)exception);
      }
    }

    @Override
    public void run()
    {
      StoreThreadLocal.setAccessor(accessor);

      try
      {
        while (store.isActive())
        {
          InternalCDORevision revision = queue.poll(1, TimeUnit.SECONDS);
          if (revision == null)
          {
            continue;
          }

          if (revision instanceof StubCDORevision)
          {
            return;
          }

          readUnitEntries(revision);
        }
      }
      catch (Throwable ex)
      {
        exception = ex;
      }
      finally
      {
        latch.countDown();
        StoreThreadLocal.remove();
      }
    }

    private void readUnitEntries(InternalCDORevision revision) throws SQLException
    {
      CDOID id = revision.getID();

      for (int i = 0; i < listMappings.length; i++)
      {
        IListMappingUnitSupport listMapping = listMappings[i];
        EStructuralFeature feature = listMapping.getFeature();

        MoveableList<Object> list = revision.getListOrNull(feature);
        if (list != null)
        {
          int size = list.size();
          if (size != 0)
          {
            if (resultSets[i] == null)
            {
              resultSets[i] = listMapping.queryUnitEntries(accessor, idHandler, timeStamp, rootID);
            }

            listMapping.readUnitEntries(resultSets[i], idHandler, id, list);
          }
        }
      }

      synchronized (revisionHandler)
      {
        revisionHandler.handleRevision(revision);
      }
    }
  }

  /**
   * @author Stefan Winkler
   */
  private final class FeatureDeltaWriter extends AbstractFeatureDeltaWriter
  {
    private final List<ListDeltaWork> listDeltaWork = new ArrayList<>();

    private boolean deferListDeltas;

    private int oldVersion;

    private InternalCDORevision newRevision;

    private int branchId;

    private final BatchedStatement reviseBatch;

    private final BatchedStatement insertBatch;

    private FeatureDeltaWriter()
    {
      this(null, null);
    }

    private FeatureDeltaWriter(BatchedStatement reviseBatch, BatchedStatement insertBatch)
    {
      this.reviseBatch = reviseBatch;
      this.insertBatch = insertBatch;
    }

    @Override
    protected void doProcess(InternalCDORevisionDelta delta)
    {
      branchId = delta.getBranch().getID();
      oldVersion = delta.getVersion();

      if (TRACER.isEnabled())
      {
        TRACER.format("FeatureDeltaWriter: old version: {0}, new version: {1}", oldVersion, oldVersion + 1); //$NON-NLS-1$
      }

      InternalCDORevision originalRevision = (InternalCDORevision)accessor.getStore().getRepository().getRevisionManager().getRevisionByVersion(id, delta,
          BASE_CHUNK_LOADING_CONFIG);

      newRevision = originalRevision.copy();

      newRevision.setVersion(oldVersion + 1);
      newRevision.setBranchPoint(delta.getBranch().getPoint(created));

      // process revision delta tree
      delta.accept(this);

      long revised = newRevision.getTimeStamp() - 1;
      if (reviseBatch == null)
      {
        reviseOldRevision(accessor, id, delta.getBranch(), revised);
        writeValues(accessor, newRevision);
      }
      else
      {
        try
        {
          sqlReviseAttributes.bindParameter(reviseBatch, 1, revised);
          sqlReviseAttributes.bindParameter(reviseBatch, 2, id, getStore().getIDHandler());
          reviseBatch.executeUpdate();

          setInsertValues(getStore().getIDHandler(), insertBatch, newRevision);
          insertBatch.executeUpdate();
        }
        catch (SQLException ex)
        {
          throw new DBException(ex);
        }
      }
    }

    @Override
    public void visit(CDOSetFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }

    @Override
    public void visit(CDOUnsetFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }

    @Override
    public void visit(CDOListFeatureDelta delta)
    {
      delta.applyTo(newRevision);

      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
      if (deferListDeltas)
      {
        listDeltaWork.add(new ListDeltaWork(id, branchId, oldVersion, oldVersion + 1, created, delta));
      }
      else
      {
        listMapping.processDelta(accessor, id, branchId, oldVersion, oldVersion + 1, created, delta);
      }
    }

    @Override
    public void visit(CDOContainerFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }
  }
}
