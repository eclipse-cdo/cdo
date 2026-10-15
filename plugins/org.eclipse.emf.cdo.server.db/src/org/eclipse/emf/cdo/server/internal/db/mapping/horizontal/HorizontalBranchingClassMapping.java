/*
 * Copyright (c) 2010-2016, 2018, 2019, 2021, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
 *    Stefan Winkler - derived branch mapping from audit mapping
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.Batch;
import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.fluent.SQLSelectOrderByStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectSourceStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectWhereStep;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class HorizontalBranchingClassMapping extends AbstractHorizontalClassMapping
    implements IClassMappingAuditSupport, IClassMappingDeltaSupport, IBranchDeletionSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalBranchingClassMapping.class);

  private static final Config UNCHUNKED_REVISION_SCAN_CONFIG = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, CDORevision.UNCHUNKED);

  @ExcludeFromDump
  private transient SQLStatement sqlInsertAttributes;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectCurrentAttributes;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAllObjectIDs;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAttributesByTime;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectAttributesByVersion;

  @ExcludeFromDump
  private transient SQLStatement sqlReviseAttributes;

  @ExcludeFromDump
  private transient SQLStatement sqlRawDeleteAttributes;

  public HorizontalBranchingClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);
  }

  @Override
  protected IDBField addBranchField(IDBTable table)
  {
    return table.addField(MappingNames.ATTRIBUTES_BRANCH, DBType.INTEGER, true);
  }

  @Override
  protected void initSQLStrings()
  {
    super.initSQLStrings();

    // ----------- Select Revision ---------------------------
    sqlSelectCurrentAttributes = createSelectAttributesSQL(0);
    sqlSelectAttributesByTime = createSelectAttributesSQL(1);
    sqlSelectAttributesByVersion = createSelectAttributesSQL(2);

    // ----------- Insert Attributes -------------------------
    List<IDBField> insertFields = new ArrayList<>();
    insertFields.add(idField);
    insertFields.add(versionField);
    insertFields.add(branchField);
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
            .and(SQL.eq(branchField, SQL.param())) //
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
            .and(SQL.eq(branchField, SQL.param())) //
            .and(SQL.eq(versionField, SQL.param()))) //
        .compile(getDBAdapter());
  }

  private SQLStatement createSelectAttributesSQL(int selection)
  {
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

    SQLSelectItem[] otherAttributes = attributes.subList(1, attributes.size()).toArray(new SQLSelectItem[attributes.size() - 1]);
    SQLCondition condition = SQL.eq(idField, SQL.param(getStore().getIDHandler())).and(SQL.eq(branchField, SQL.param()));
    if (selection == 0)
    {
      condition = condition.and(SQL.eq(revisedField, SQL.ZERO));
    }
    else if (selection == 1)
    {
      condition = condition.and(SQL.le(createdField, SQL.param())).and(SQL.eq(revisedField, SQL.ZERO).or(SQL.ge(revisedField, SQL.param())));
    }
    else
    {
      condition = condition.and(SQL.eq(SQL.abs(versionField), SQL.param()));
    }

    return SQL.select(attributes.get(0), otherAttributes) //
        .from(table) //
        .where(condition) //
        .compile(getDBAdapter());
  }

  @Override
  protected void appendSelectForHandleFields(List<SQLSelectItem> items)
  {
    items.add(branchField);
    items.add(createdField);
    items.add(revisedField);
  }

  @Override
  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    long timeStamp = revision.getTimeStamp();
    int branchID = revision.getBranch().getID();

    IDBPreparedStatement stmt = null;
    boolean success = false;

    try
    {
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        stmt = DBUtil.prepare(sqlSelectAttributesByTime, accessor.getDBConnection(), ReuseProbability.MEDIUM);

        sqlSelectAttributesByTime.bindParameter(stmt, 1, revision.getID());
        sqlSelectAttributesByTime.bindParameter(stmt, 2, branchID);
        sqlSelectAttributesByTime.bindParameter(stmt, 3, timeStamp);
        sqlSelectAttributesByTime.bindParameter(stmt, 4, timeStamp);
      }
      else
      {
        stmt = DBUtil.prepare(sqlSelectCurrentAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

        sqlSelectCurrentAttributes.bindParameter(stmt, 1, revision.getID());
        sqlSelectCurrentAttributes.bindParameter(stmt, 2, branchID);
      }

      // Read singleval-attribute table always (even without modeled attributes!)
      success = readValuesFromStatement(stmt, revision, accessor, listChunk);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }

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

  @Override
  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IDBPreparedStatement stmt = DBUtil.prepare(sqlSelectAttributesByVersion, accessor.getDBConnection(), ReuseProbability.HIGH);
    boolean success = false;

    try
    {
      sqlSelectAttributesByVersion.bindParameter(stmt, 1, revision.getID());
      sqlSelectAttributesByVersion.bindParameter(stmt, 2, revision.getBranch().getID());
      sqlSelectAttributesByVersion.bindParameter(stmt, 3, revision.getVersion());

      // Read singleval-attribute table always (even without modeled attributes!)
      success = readValuesFromStatement(stmt, revision, accessor, listChunk);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }

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

  @Override
  public IDBPreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name, boolean exactMatch,
      CDOBranchPoint branchPoint)
  {
    if (table == null)
    {
      return null;
    }

    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    ITypeMapping nameValueMapping = getValueMapping(nameFeature);
    if (nameValueMapping == null)
    {
      throw new ImplementationError(nameFeature + " not found in ClassMapping " + this); //$NON-NLS-1$
    }

    int branchID = branchPoint.getBranch().getID();
    long timeStamp = branchPoint.getTimeStamp();
    SQLCondition condition = SQL.gt(versionField, SQL.ZERO).and(SQL.eq(branchField, SQL.param()))
        .and(SQL.eq(containerField, SQL.param(getStore().getIDHandler())));

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
      sql.bindParameter(stmt, column++, branchID);
      sql.bindParameter(stmt, column++, folderId);

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
    IDBPreparedStatement stmt = DBUtil.prepare(sqlInsertAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      setInsertValues(stmt, revision);
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
  public void writeRevisions(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean revise, OMMonitor monitor)
  {
    boolean allFirstRevisions = firstRevision;

    for (InternalCDORevision revision : revisions)
    {
      allFirstRevisions &= revision.getVersion() == CDORevision.FIRST_VERSION;
    }

    if (!allFirstRevisions)
    {
      monitor.begin(revisions.length);

      try
      {
        // Branching revision setup has per-revision first-version and history semantics. Preserve the established
        // synchronous ordering for revisions that are not all initial versions.
        for (InternalCDORevision revision : revisions)
        {
          writeRevision(accessor, revision, firstRevision, revise, monitor.fork());
        }
      }
      finally
      {
        monitor.done();
      }

      return;
    }

    super.writeRevisions(accessor, revisions, firstRevision, revise, monitor);
  }

  @Override
  protected void writeValues(IDBStoreAccessor accessor, InternalCDORevision[] revisions)
  {
    if (revisions.length == 0)
    {
      return;
    }

    IBatchingContext batchingContext = accessor.getBatchingContext();
    BatchedStatement stmt = batchingContext.createStatement(sqlInsertAttributes, ReuseProbability.HIGH, "BranchingClass.attribute"); //$NON-NLS-1$
    boolean discarded = false;

    try
    {
      for (InternalCDORevision revision : revisions)
      {
        setInsertValues(stmt, revision);
        stmt.executeUpdate();
      }

      batchingContext.flushPhase();
      validateExactlyOne(stmt, revisions.length);
      batchingContext.recordDiagnosticCounter("BranchingClass.attributeRows", revisions.length); //$NON-NLS-1$
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

  private void setInsertValues(PreparedStatement stmt, InternalCDORevision revision) throws SQLException
  {
    IIDHandler idHandler = getStore().getIDHandler();

    SQLParameterBinder values = sqlInsertAttributes.binder(stmt);
    values //
        .bindParameter(revision.getID(), idHandler) //
        .bind(revision.getVersion(), revision.getBranch().getID(), revision.getTimeStamp(), revision.getRevised())
        .bindParameter(revision.getResourceID(), idHandler) //
        .bindParameter((CDOID)revision.getContainerID(), idHandler) //
        .bind(revision.getContainerFeatureID());

    int isSetCol = 9 + getValueMappings().size();
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

  private void validateExactlyOne(BatchedStatement stmt, int expectedCount)
  {
    int knownResult = stmt.getTotalResult();
    int unknownResultCount = stmt.getUnknownResultCount();
    if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
    {
      throw new DBException("Unexpected branching attribute insert result"); //$NON-NLS-1$
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
      values //
          .bindParameter(id, idHandler) //
          .bind(-version, branch.getID(), timeStamp, CDOBranchPoint.UNSPECIFIED_DATE) //
          .bindParameter(CDOID.NULL, idHandler) //
          .bindParameter(CDOID.NULL, idHandler) //
          .bind(0);

      int isSetCol = 9 + getValueMappings().size();
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
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlRawDeleteAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlRawDeleteAttributes.bindParameter(stmt, 1, id, idHandler);
      sqlRawDeleteAttributes.bindParameter(stmt, 2, branch.getID());
      sqlRawDeleteAttributes.bindParameter(stmt, 3, version);
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
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlReviseAttributes, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlReviseAttributes.bindParameter(stmt, 1, revised);
      sqlReviseAttributes.bindParameter(stmt, 2, id, idHandler);
      sqlReviseAttributes.bindParameter(stmt, 3, branch.getID());
      DBUtil.update(stmt, false); // No row affected if old revision from other branch!
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
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, boolean firstRevision, boolean revise, OMMonitor monitor)
  {
    if (table == null)
    {
      initTable(accessor);
    }

    CDOID id = revision.getID();
    int version = revision.getVersion();
    InternalCDOBranch branch = revision.getBranch();
    long timeStamp = revision.getTimeStamp();

    // A DetachedCDORevision can only come from DBStoreAccessor.rawStore().
    if (revision instanceof DetachedCDORevision)
    {
      detachAttributes(accessor, id, version, branch, timeStamp, monitor);

      long revised = revision.getRevised();
      if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        reviseOldRevision(accessor, id, branch, revised);
      }

      return;
    }

    monitor.begin(9);
    Async async = null;

    try
    {
      try
      {
        async = monitor.forkAsync();

        if (firstRevision)
        {
          // Put new objects into objectTypeMapper
          EClass eClass = getEClass();

          AbstractHorizontalMappingStrategy mappingStrategy = getMappingStrategy();
          if (!mappingStrategy.putObjectType(accessor, timeStamp, id, eClass))
          {
            firstRevision = false;
          }
        }

        if (!firstRevision && revise && version > CDOBranchVersion.FIRST_VERSION)
        {
          // If revision is not the first one, revise the old revision
          long revised = timeStamp - 1;

          reviseOldRevision(accessor, id, branch, revised);
          for (IListMapping mapping : getListMappings())
          {
            mapping.objectDetached(accessor, id, revised);
          }
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      try
      {
        // Write attribute table always (even without modeled attributes!)
        async = monitor.forkAsync();
        writeValues(accessor, revision);
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      try
      {
        // Write list tables only if they exist
        async = monitor.forkAsync(7);
        if (getListMappings() != null)
        {
          writeLists(accessor, revision, firstRevision, !revise);
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
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void handleRevisions(IDBStoreAccessor accessor, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    if (table == null)
    {
      return;
    }

    SQLCondition condition = null;
    if (branch != null)
    {
      condition = SQL.eq(branchField, SQL.param());
    }

    int timeParameters = 0;
    if (timeStamp != CDOBranchPoint.INVALID_DATE)
    {
      if (exactTime)
      {
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          SQLCondition timeCondition = SQL.eq(createdField, SQL.param());
          condition = condition == null ? timeCondition : condition.and(timeCondition);
          timeParameters = 1;
        }
      }
      else
      {
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          SQLCondition timeCondition = SQL.le(createdField, SQL.param()).and(SQL.eq(revisedField, SQL.ZERO).or(SQL.ge(revisedField, SQL.param())));
          condition = condition == null ? timeCondition : condition.and(timeCondition);
          timeParameters = 2;
        }
        else
        {
          SQLCondition timeCondition = SQL.eq(revisedField, SQL.ZERO);
          condition = condition == null ? timeCondition : condition.and(timeCondition);
        }
      }
    }

    SQLSelectSourceStep select = selectForHandleRevisions().from(table);
    SQLSelectOrderByStep ordered = condition == null //
        ? select.orderBy(SQL.asc(idField), SQL.asc(versionField), SQL.asc(branchField)) //
        : select.where(condition).orderBy(SQL.asc(idField), SQL.asc(versionField), SQL.asc(branchField));
    SQLStatement sql = ordered.compile(getDBAdapter());

    IRepository repository = accessor.getStore().getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDOBranchManager branchManager = repository.getBranchManager();

    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sql, accessor.getDBConnection(), ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      int column = 1;

      if (branch != null)
      {
        sql.bindParameter(stmt, column++, branch.getID());
      }

      for (int i = 0; i < timeParameters; i++)
      {
        sql.bindParameter(stmt, column++, timeStamp);
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        int version = resultSet.getInt(2);
        CDOBranch revisionBranch = branchManager.getBranch(resultSet.getInt(3));

        if (version >= CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion branchVersion = revisionBranch.getVersion(version);
          InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevisionByVersion(id, branchVersion, UNCHUNKED_REVISION_SCAN_CONFIG);

          if (!handler.handleRevision(revision))
          {
            break;
          }
        }
        else
        {
          EClass eClass = getEClass();
          long created = resultSet.getLong(4);
          long revised = resultSet.getLong(5);

          // Tell handler about detached IDs
          InternalCDORevision revision = new DetachedCDORevision(eClass, id, revisionBranch, -version, created, revised);
          if (!handler.handleRevision(revision))
          {
            break;
          }
        }
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList)
  {
    if (table == null)
    {
      return;
    }

    // Delete the revisions.
    IDBAdapter dbAdapter = getDBAdapter();
    String sql = SQL.deleteFrom(table) //
        .where(SQL.fragment() //
            .column(branchField) //
            .sql(" IN (") //
            .sql(idList) //
            .sql(")") //
            .build().condition()) //
        .compile(dbAdapter).getSQL();
    batch.add(sql);

    // Delete the type mappings.
    ObjectTypeTable objects = getMappingStrategy().objects();
    IDBField objectID = objects.id();
    IDBField objectClass = objects.clazz();
    IDBTable objectsTable = objectID.getTable();
    IDBTable classTable = idField.getTable();
    String metaID = getMetaIDStr();
    IDBField classID = classTable.getField(idField.getName());

    SQLTableRef objectRef = SQL.as(objectsTable, "o"); //$NON-NLS-1$
    SQLTableRef classRef = SQL.as(classTable, "c"); //$NON-NLS-1$
    SQLCondition deleteObjectsCondition = SQL.eq(objectRef.column(objectClass), SQL.fragment() //
        .sql(metaID) //
        .build() //
        .expression()).and(SQL.isNull(classRef.column(classID)));
    SQLSelectWhereStep orphanObjects = SQL.select(objectRef.column(objectID)) //
        .from(objectRef) //
        .leftJoin(classRef).on(SQL.eq(classRef.column(classID), objectRef.column(objectID))) //
        .where(deleteObjectsCondition);
    String deleteObjectsSQL = SQL.deleteFrom(objectsTable) //
        .where(SQL.in(objectID, orphanObjects.subSelect())) //
        .compile(dbAdapter).getSQL();
    batch.add(deleteObjectsSQL);

    // Delete the list elements.
    for (IListMapping listMapping : getListMappings())
    {
      if (listMapping instanceof IBranchDeletionSupport)
      {
        ((IBranchDeletionSupport)listMapping).deleteBranches(accessor, batch, idList);
      }
    }
  }

  @Override
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, CDOChangeSetSegment[] segments)
  {
    Set<CDOID> result = new HashSet<>();

    if (table == null)
    {
      return result;
    }

    SQLCondition condition = null;

    for (int i = 0; i < segments.length; i++)
    {
      SQLCondition range = SQL.eq(branchField, SQL.param()).and(SQL.ge(createdField, SQL.param()))
          .and(SQL.le(revisedField, SQL.param()).or(SQL.eq(revisedField, SQL.ZERO)));
      condition = condition == null ? range : condition.or(range);
    }

    if (condition == null)
    {
      return result;
    }

    SQLStatement sql = selectForChangeSet().where(condition).compile(getDBAdapter());
    IDBPreparedStatement stmt = DBUtil.prepare(sql, accessor.getDBConnection(), ReuseProbability.LOW);

    IIDHandler idHandler = getStore().getIDHandler();
    ResultSet resultSet = null;

    try
    {
      int column = 1;

      for (CDOChangeSetSegment segment : segments)
      {
        sql.bindParameter(stmt, column++, segment.getBranch().getID());
        sql.bindParameter(stmt, column++, segment.getTimeStamp());
        sql.bindParameter(stmt, column++, segment.getEndTime());
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        result.add(id);
      }

      return result;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    StringBuilder builder = new StringBuilder();
    appendField(builder, branchField);
    builder.append("=");
    builder.append(context.getBranch().getID());
    builder.append(" AND (");

    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      appendField(builder, revisedField);
      builder.append("=0)"); //$NON-NLS-1$
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
      builder.append("))"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  @Override
  protected SQLCondition getScalarXRefsCondition(QueryXRefsContext context)
  {
    SQLCondition condition = SQL.eq(branchField, SQL.literal(context.getBranch().getID()));
    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      return condition.and(SQL.eq(revisedField, SQL.ZERO));
    }

    return condition.and(SQL.le(createdField, SQL.literal(timeStamp))).and(SQL.eq(revisedField, SQL.ZERO).or(SQL.ge(revisedField, SQL.literal(timeStamp))));
  }

  @Override
  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created, OMMonitor monitor)
  {
    monitor.begin();

    try
    {
      if (accessor.getTransaction().getBranch() != delta.getBranch())
      {
        // new branch -> decide, if branch should be copied
        if (((HorizontalBranchingMappingStrategyWithRanges)getMappingStrategy()).shallCopyOnBranch())
        {
          doCopyOnBranch(accessor, delta, created, monitor.fork());
          return;
        }
      }

      Async async = null;

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
    boolean shallCopyOnBranch = ((HorizontalBranchingMappingStrategyWithRanges)getMappingStrategy()).shallCopyOnBranch();
    if (shallCopyOnBranch)
    {
      CDOBranch commitBranch = accessor.getTransaction().getBranch();

      for (InternalCDORevisionDelta delta : deltas)
      {
        if (commitBranch != delta.getBranch())
        {
          super.writeRevisionDeltasSingle(accessor, deltas, created, monitor);
          return;
        }
      }
    }

    List<IListMapping> mappings = getListMappings();
    List<FeatureDeltaWriter> writers = new ArrayList<>();
    monitor.begin(1 + mappings.size());

    try
    {
      OMMonitor revisionMonitor = monitor.fork();
      revisionMonitor.begin(deltas.length);

      try
      {
        for (InternalCDORevisionDelta delta : deltas)
        {
          FeatureDeltaWriter writer = new FeatureDeltaWriter();
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

      for (IListMapping mapping : mappings)
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
        else if (mapping instanceof IListMappingBatchingSupport)
        {
          ((IListMappingBatchingSupport)mapping).processDeltas(accessor, work.toArray(new ListDeltaWork[work.size()]), listMonitor);
        }
        else
        {
          listMonitor.begin(work.size());

          try
          {
            IListMappingDeltaSupport deltaSupport = (IListMappingDeltaSupport)mapping;

            for (ListDeltaWork item : work)
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
    finally
    {
      monitor.done();
    }
  }

  private void doCopyOnBranch(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created, OMMonitor monitor)
  {
    monitor.begin(2);

    try
    {
      InternalRepository repository = (InternalRepository)accessor.getStore().getRepository();

      InternalCDORevision oldRevision = (InternalCDORevision)accessor.getTransaction().getRevision(delta.getID());
      if (oldRevision == null)
      {
        throw new IllegalStateException("Origin revision not found for " + delta);
      }

      // Make sure all chunks are loaded
      repository.ensureChunks(oldRevision, CDORevision.UNCHUNKED);

      InternalCDORevision newRevision = oldRevision.copy();
      newRevision.adjustForCommit(accessor.getTransaction().getBranch(), created);
      delta.applyTo(newRevision);

      monitor.worked();

      writeRevision(accessor, newRevision, false, true, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @author Stefan Winkler
   */
  private final class FeatureDeltaWriter extends AbstractFeatureDeltaWriter
  {
    private CDOBranch targetBranch;

    private int oldVersion;

    private int newVersion;

    private InternalCDORevision newRevision;

    private boolean deferListDeltas;

    private final List<ListDeltaWork> listDeltaWork = new ArrayList<>();

    @Override
    protected void doProcess(InternalCDORevisionDelta delta)
    {
      oldVersion = delta.getVersion();

      if (TRACER.isEnabled())
      {
        TRACER.format("FeatureDeltaWriter: old version: {0}, new version: {1}", oldVersion, oldVersion + 1); //$NON-NLS-1$
      }

      InternalCDORevision originalRevision = (InternalCDORevision)accessor.getTransaction().getRevision(id);
      newRevision = originalRevision.copy();
      targetBranch = accessor.getTransaction().getBranch();
      newRevision.adjustForCommit(targetBranch, created);

      newVersion = newRevision.getVersion();

      // process revision delta tree
      delta.accept(this);

      if (newVersion != CDORevision.FIRST_VERSION)
      {
        reviseOldRevision(accessor, id, delta.getBranch(), newRevision.getTimeStamp() - 1);
      }

      writeValues(accessor, newRevision);
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

      if (deferListDeltas)
      {
        listDeltaWork.add(new ListDeltaWork(id, targetBranch.getID(), oldVersion, newVersion, created, delta));
      }
      else
      {
        IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
        listMapping.processDelta(accessor, id, targetBranch.getID(), oldVersion, newVersion, created, delta);
      }
    }

    @Override
    public void visit(CDOContainerFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }
  }
}
