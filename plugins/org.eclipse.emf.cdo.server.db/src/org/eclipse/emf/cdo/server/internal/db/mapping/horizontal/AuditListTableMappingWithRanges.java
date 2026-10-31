/*
 * Copyright (c) 2010-2013, 2015, 2016, 2018-2020, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * This class has been derived from AbstractListTableMapping
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Lothar Werzinger - Bug 296440: [DB] Change RDB schema to improve scalability of to-many references in audit mode
 *    Stefan Winkler - cleanup, merge and maintenance
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping4;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingUnitSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractBasicListTableMapping.ListLobRefsUpdater;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.fluent.SQLSelectFromStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectProjectionStep;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is a list-table mapping for audit mode. It is optimized for frequent insert operations at the list's end, which
 * causes just 1 DB row to be changed. This is achieved by introducing a version range (columns cdo_version_added and
 * cdo_version_removed) which records for which revisions a particular entry existed. Also, this mapping is mainly
 * optimized for potentially very large lists: the need for having the complete list stored in memory to do
 * in-the-middle-moved and inserts is traded in for a few more DB access operations.
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 * @author Lothar Werzinger
 */
public class AuditListTableMappingWithRanges extends AbstractBasicListTableMapping
    implements ISchemaPreparable, IListMappingBatchingSupport, IListMappingDeltaSupport, IListMappingUnitSupport, IListMapping4, ListLobRefsUpdater
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AuditListTableMappingWithRanges.class);

  private static final Config UNCHUNKED_REVISION_SCAN_CONFIG = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, CDORevision.UNCHUNKED);

  private static final Config UNCHUNKED_INITIAL_LIST_REVISION_CONFIG = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, 0);

  /**
   * Used to clean up lists for detached objects.
   */
  private static final int FINAL_VERSION = Integer.MAX_VALUE;

  private static final boolean CHECK_UNIT_ENTRIES = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.db.checkUnitEntries");

  /**
   * The table of this mapping.
   */
  private IDBTable table;

  private IDBField sourceField;

  private IDBField indexField;

  private IDBField versionAddedField;

  private IDBField versionRemovedField;

  private IDBField valueField;

  private AbstractHorizontalClassMapping classMapping;

  /**
   * The type mapping for the value field.
   */
  private ITypeMapping typeMapping;

  // --------- SQL strings - see initSQLStrings() -----------------
  @ExcludeFromDump
  private transient SQLStatement sqlSelectChunkValues;

  /**
   * This field is initialized on demand in {@link #queryUnitEntries(IDBStoreAccessor, IIDHandler, long, CDOID)}
   * and not in {@link #initSQLStrings()} because the initialization requires the {@link #classMapping} field value.
   */
  @ExcludeFromDump
  private transient SQLStatement sqlSelectUnitEntries;

  @ExcludeFromDump
  private transient SQLStatement sqlInsertEntry;

  @ExcludeFromDump
  private transient SQLStatement sqlCopyOriginalEntry;

  @ExcludeFromDump
  private transient SQLStatement sqlDeleteEntry;

  @ExcludeFromDump
  private transient SQLStatement sqlRemoveEntry;

  @ExcludeFromDump
  private transient SQLStatement sqlShiftCurrentIndexes;

  @ExcludeFromDump
  private transient SQLStatement sqlCopyShiftedEntry;

  @ExcludeFromDump
  private transient SQLStatement sqlCloseShiftedEntry;

  @ExcludeFromDump
  private transient SQLStatement sqlGetValue;

  @ExcludeFromDump
  private transient SQLStatement sqlClearList;

  @ExcludeFromDump
  private transient SQLStatement sqlDeleteList;

  public AuditListTableMappingWithRanges(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);

    IDBStoreAccessor accessor = null;
    if (AbstractHorizontalMappingStrategy.isEagerTableCreation(mappingStrategy))
    {
      accessor = (IDBStoreAccessor)StoreThreadLocal.getAccessor();
    }

    initTable(accessor);
  }

  private void initTable(IDBStoreAccessor accessor)
  {
    String tableName = getMappingStrategy().getTableName(getContainingClass(), getFeature());
    typeMapping = getMappingStrategy().createValueMapping(getFeature());

    IDBStore store = getStore();
    DBType idType = store.getIDHandler().getDBType();
    int idLength = store.getIDColumnLength();

    IDBDatabase database = getStore().getDatabase();
    table = database.getSchema().getTable(tableName);
    if (table == null)
    {
      if (accessor != null)
      {
        IDBSchemaTransaction schemaTransaction = accessor.openSchemaTransaction();

        try
        {
          IDBSchema workingCopy = schemaTransaction.getWorkingCopy();
          IDBTable table = workingCopy.addTable(tableName);

          sourceField = table.addField(MappingNames.LIST_REVISION_ID, idType, idLength, true);
          versionAddedField = table.addField(MappingNames.LIST_REVISION_VERSION_ADDED, DBType.INTEGER);
          versionRemovedField = table.addField(MappingNames.LIST_REVISION_VERSION_REMOVED, DBType.INTEGER);
          indexField = table.addField(MappingNames.LIST_IDX, DBType.INTEGER, true);

          table.addIndex(Type.NON_UNIQUE, sourceField, versionAddedField, versionRemovedField, indexField);

          typeMapping.createDBField(table, MappingNames.LIST_VALUE);

          schemaTransaction.commit();
        }
        finally
        {
          schemaTransaction.close();
        }

        initTable(null);
        accessor.tableCreated(table);
      }
    }
    else
    {
      sourceField = table.getField(MappingNames.LIST_REVISION_ID);
      versionAddedField = table.getField(MappingNames.LIST_REVISION_VERSION_ADDED);
      versionRemovedField = table.getField(MappingNames.LIST_REVISION_VERSION_REMOVED);
      indexField = table.getField(MappingNames.LIST_IDX);

      typeMapping.setDBField(table, MappingNames.LIST_VALUE);
      valueField = table.getField(MappingNames.LIST_VALUE);

      initSQLStrings();
    }
  }

  /**
   * Creates this ranged list table before the commit's transactional data-write phase begins.
   */
  @Override
  public synchronized void prepareSchema(IDBStoreAccessor accessor)
  {
    if (table == null)
    {
      initTable(accessor);
    }
  }

  void prepareForDeltaBatching(IDBStoreAccessor accessor)
  {
    if (table == null)
    {
      initTable(accessor);
    }
  }

  private void initSQLStrings()
  {
    // ---------------- read chunks ----------------------------
    sqlSelectChunkValues = createSelectChunksSQL(null);

    // ----------------- insert entry -----------------
    sqlInsertEntry = SQL.insertInto(getTable()) //
        .columns(sourceField, versionAddedField, versionRemovedField, indexField, valueField) //
        .values(SQL.param(), SQL.param(), SQL.NULL, SQL.param(), SQL.param()) //
        .compile(getDBAdapter());

    // ----------------- copy original entry for move -----------------
    sqlCopyOriginalEntry = SQL.insertInto(getTable()) //
        .columns(sourceField, versionAddedField, versionRemovedField, indexField, valueField) //
        .select(SQL.select(sourceField, SQL.param(), SQL.NULL, SQL.param(), valueField) //
            .from(getTable()) //
            .where(sourceField).eq(SQL.param()) //
            .and(indexField).eq(SQL.param()) //
            .and(versionAddedField).ne(SQL.param()) //
            .and(versionRemovedField).eq(SQL.param()) //
            .subSelect())
        .compile(getDBAdapter());

    // ----------------- remove current entry -----------------
    sqlRemoveEntry = SQL.update(getTable()) //
        .set(versionRemovedField).to(SQL.param()) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(indexField, SQL.param())) //
            .and(SQL.isNull(versionRemovedField)) //
            .and(SQL.ne(versionAddedField, SQL.param()))) //
        .compile(getDBAdapter());

    // ----------------- delete temporary entry -----------------
    sqlDeleteEntry = SQL.deleteFrom(getTable()) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(indexField, SQL.param())) //
            .and(SQL.eq(versionAddedField, SQL.param()))) //
        .compile(getDBAdapter());

    // ----------------- shift indexes in the new version -----------------
    sqlShiftCurrentIndexes = SQL.update(getTable()) //
        .set(indexField).to(SQL.add(indexField, SQL.param())) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(versionAddedField, SQL.param())) //
            .and(SQL.between(indexField, SQL.param(), SQL.param()))) //
        .compile(getDBAdapter());

    // ----------------- copy older entry for index shift -----------------
    sqlCopyShiftedEntry = SQL.insertInto(getTable()) //
        .columns(sourceField, versionAddedField, versionRemovedField, indexField, valueField) //
        .select(SQL.select(sourceField, SQL.param(), SQL.NULL, SQL.add(indexField, SQL.param()), valueField) //
            .from(getTable()) //
            .where(sourceField).eq(SQL.param()) //
            .and(SQL.between(indexField, SQL.param(), SQL.param())) //
            .and(SQL.isNull(versionRemovedField)) //
            .and(versionAddedField).ne(SQL.param()) //
            .subSelect()) //
        .compile(getDBAdapter());

    // ----------------- close older entry for index shift -----------------
    sqlCloseShiftedEntry = SQL.update(getTable()) //
        .set(versionRemovedField).to(SQL.param()) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.between(indexField, SQL.param(), SQL.param())) //
            .and(SQL.isNull(versionRemovedField)) //
            .and(SQL.ne(versionAddedField, SQL.param()))) //
        .compile(getDBAdapter());

    // ----------------- get current value -----------------
    sqlGetValue = SQL.select(valueField) //
        .from(getTable()) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(indexField, SQL.param())) //
            .and(SQL.isNull(versionRemovedField))) //
        .compile(getDBAdapter());

    // ----------- clear list items -------------------------
    sqlClearList = SQL.update(getTable()) //
        .set(versionRemovedField).to(SQL.param()) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.isNull(versionRemovedField)) //
            .and(SQL.ne(versionAddedField, SQL.param()))) //
        .compile(getDBAdapter());

    // ----------- delete temporary list items -------------------------
    sqlDeleteList = SQL.deleteFrom(getTable()) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(versionAddedField, SQL.param())) //
            .and(SQL.isNull(versionRemovedField))) //
        .compile(getDBAdapter());
  }

  private SQLStatement createSelectChunksSQL(String additionalWhere)
  {
    SQLCondition condition = SQL.eq(sourceField, SQL.param()) //
        .and(SQL.le(versionAddedField, SQL.param())) //
        .and(SQL.isNull(versionRemovedField).or(SQL.gt(versionRemovedField, SQL.param())));

    if (additionalWhere != null)
    {
      // IListMapping2 supplies this legacy literal predicate through its StringBuilder SPI; it contains no bound
      // parameters.
      condition = condition.and(SQL.fragment() //
          .sql(additionalWhere) //
          .build().condition());
    }

    return SQL.select(valueField) //
        .from(getTable()) //
        .where(condition) //
        .orderBy(SQL.asc(indexField)) //
        .compile(getDBAdapter());
  }

  @Override
  public void setClassMapping(IClassMapping classMapping)
  {
    this.classMapping = (AbstractHorizontalClassMapping)classMapping;
  }

  @Override
  public Collection<IDBTable> getDBTables()
  {
    return Collections.singleton(table);
  }

  @Override
  protected final IDBField index()
  {
    return indexField;
  }

  @Override
  public final IDBTable getTable()
  {
    return table;
  }

  @Override
  public final ITypeMapping getTypeMapping()
  {
    return typeMapping;
  }

  @Override
  public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    if (table == null)
    {
      // Nothing to read. Take shortcut.
      return;
    }

    MoveableList<Object> list = revision.getListOrNull(getFeature());
    if (list == null || listChunk == 0 || list.size() == 0)
    {
      // Nothing to read take shortcut.
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision.getID(), revision.getVersion());
    }

    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlSelectChunkValues, accessor.getDBConnection(), ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      sqlSelectChunkValues.binder(stmt) //
          .bindParameter(revision.getID(), idHandler) //
          .bind(revision.getVersion(), revision.getVersion());

      if (listChunk != CDORevision.UNCHUNKED)
      {
        stmt.setMaxRows(listChunk); // optimization - don't read unneeded rows.
      }

      resultSet = stmt.executeQuery();

      int currentIndex = 0;
      while ((listChunk == CDORevision.UNCHUNKED || --listChunk >= 0) && resultSet.next())
      {
        Object value = typeMapping.readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", currentIndex, value); //$NON-NLS-1$
        }

        list.set(currentIndex++, value);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {4} list values done for feature {0}.{1} of {2}v{3}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), revision.getID(), revision.getVersion(), list.size());
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature() {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
    }

    SQLStatement sql = where == null ? sqlSelectChunkValues : createSelectChunksSQL(where);

    IDBPreparedStatement stmt = DBUtil.prepare(sql, chunkReader.getAccessor().getDBConnection(), ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      sql.binder(stmt) //
          .bindParameter(chunkReader.getRevision().getID(), getStore().getIDHandler()) //
          .bind(chunkReader.getRevision().getVersion(), chunkReader.getRevision().getVersion());

      resultSet = stmt.executeQuery();

      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      while (resultSet.next())
      {
        Object value = typeMapping.readValue(resultSet);

        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();

          if (TRACER.isEnabled())
          {
            TRACER.format("Current chunk no. {0} is [start = {1}, size = {2}]", chunkIndex - 1, chunk.getStartIndex(), //$NON-NLS-1$
                chunkSize);
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for chunk index {0} from result set: {1}", indexInChunk, value); //$NON-NLS-1$
        }

        chunk.add(indexInChunk++, value);
        if (indexInChunk == chunkSize)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Chunk finished"); //$NON-NLS-1$
          }

          chunk = null;
          indexInChunk = 0;
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Reading list chunk values done for feature() {0}.{1} of {2}v{3}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public void writeValues(IDBStoreAccessor accessor, CDORevision revision, boolean firstRevision, boolean raw)
  {
    if (firstRevision || !raw)
    {
      writeValues(accessor, (InternalCDORevision)revision);
    }
    else
    {
      InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)getStore().getRepository().getRevisionManager();
      InternalCDORevision baseRevision = revisionManager.getBaseRevision(revision, UNCHUNKED_REVISION_SCAN_CONFIG);

      EStructuralFeature feature = getFeature();
      CDOListFeatureDelta delta = CDORevisionUtil.compareLists(baseRevision, revision, feature);

      if (delta != null && !delta.getListChanges().isEmpty())
      {
        int oldVersion = baseRevision.getVersion();
        int newVersion = revision.getVersion();

        processDelta(accessor, baseRevision, oldVersion, newVersion, delta.getListChanges());
      }
    }
  }

  @Override
  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean raw, OMMonitor monitor)
  {
    if (!firstRevision && raw)
    {
      monitor.begin(revisions.length);
      try
      {
        for (InternalCDORevision revision : revisions)
        {
          writeValues(accessor, revision, firstRevision, raw);
          monitor.worked();
        }
      }
      finally
      {
        monitor.done();
      }

      return;
    }

    if (table == null)
    {
      initTable(accessor);
    }

    IBatchingContext batchingContext = accessor.getBatchingContext();
    BatchedStatement stmt = batchingContext.createStatement(sqlInsertEntry, ReuseProbability.HIGH, "AuditList.insert"); //$NON-NLS-1$

    monitor.begin(revisions.length);
    boolean complete = false;

    try
    {
      for (InternalCDORevision revision : revisions)
      {
        CDOList values = revision.getListOrNull(getFeature());
        if (values != null)
        {
          int index = 0;

          for (Object value : values)
          {
            IIDHandler idHandler = getStore().getIDHandler();
            sqlInsertEntry.bindParameter(stmt, 1, revision.getID(), idHandler);
            sqlInsertEntry.bindParameter(stmt, 2, revision.getVersion());
            sqlInsertEntry.bindParameter(stmt, 3, index++);
            sqlInsertEntry.bindParameter(stmt, 4, value, getTypeMapping());
            stmt.executeUpdate();
          }
        }

        monitor.worked();
      }

      complete = true;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    catch (IllegalStateException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      if (complete)
      {
        batchingContext.flushPhase();
        batchingContext.releaseStatement(stmt);
      }
      else
      {
        batchingContext.discardStatement(stmt);
      }

      monitor.done();
    }
  }

  @Override
  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    CDOList values = revision.getListOrNull(getFeature());
    if (values != null && !values.isEmpty())
    {
      if (table == null)
      {
        initTable(accessor);
      }

      int idx = 0;
      for (Object element : values)
      {
        writeValue(accessor, revision, idx++, element);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Writing done"); //$NON-NLS-1$
      }
    }
  }

  protected final void writeValue(IDBStoreAccessor accessor, CDORevision revision, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing value for feature {0}.{1} index {2} of {3}v{4} : {5}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, revision.getID(), revision.getVersion(), value);
    }

    addEntry(accessor, revision.getID(), revision.getVersion(), index, value);
  }

  /**
   * Clear a list of a given revision.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id, int oldVersion, int newVersion)
  {
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmtDeleteTemp = DBUtil.prepare(sqlDeleteList, accessor.getDBConnection(), ReuseProbability.HIGH);
    IDBPreparedStatement stmtClear = DBUtil.prepare(sqlClearList, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      // delete temporary entries
      sqlDeleteList.bindParameter(stmtDeleteTemp, 1, id, idHandler);
      sqlDeleteList.bindParameter(stmtDeleteTemp, 2, newVersion);

      int deleteResult = DBUtil.update(stmtDeleteTemp, false);
      if (TRACER.isEnabled())
      {
        TRACER.format("DeleteList result: {0}", deleteResult); //$NON-NLS-1$
      }

      // clear rest of the list
      sqlClearList.bindParameter(stmtClear, 1, newVersion);
      sqlClearList.bindParameter(stmtClear, 2, id, idHandler);
      sqlClearList.bindParameter(stmtClear, 3, newVersion);

      int clearResult = DBUtil.update(stmtClear, false);
      if (TRACER.isEnabled())
      {
        TRACER.format("ClearList result: {0}", clearResult); //$NON-NLS-1$
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmtClear);
      DBUtil.close(stmtDeleteTemp);
    }
  }

  @Override
  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    if (table == null)
    {
      initTable(accessor);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("objectRevised {0}: {1}", id, revised); //$NON-NLS-1$
    }

    IRepository repository = getStore().getRepository();
    CDOBranch main = repository.getBranchManager().getMainBranch();

    // get revision from cache to find out version number
    CDORevision revision = repository.getRevisionManager().getRevision(id, main.getHead(), UNCHUNKED_INITIAL_LIST_REVISION_CONFIG);

    // set cdo_revision_removed for all list items (so we have no NULL values)
    clearList(accessor, id, revision.getVersion(), FINAL_VERSION);
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    throw new UnsupportedOperationException("Raw deletion does not work in range-based mappings");
  }

  @Override
  public ResultSet queryUnitEntries(IDBStoreAccessor accessor, IIDHandler idHandler, long timeStamp, CDOID rootID) throws SQLException
  {
    if (sqlSelectUnitEntries == null)
    {
      DBStore store = (DBStore)getStore();
      UnitMappingTable units = store.getUnitMappingTable();
      // The sqlSelectUnitEntries field is initialized here and not in initSQLStrings()
      // because the initialization requires the classMapping field value.

      SQLTableRef listRef = SQL.as(table, "cdo_list"); //$NON-NLS-1$
      SQLTableRef classRef = SQL.as(classMapping.table, "cdo_class"); //$NON-NLS-1$
      SQLSelectProjectionStep projection = CHECK_UNIT_ENTRIES ? SQL.select(classRef.column(classMapping.idField), listRef.column(valueField))
          : SQL.select(listRef.column(valueField));
      SQLSelectFromStep query = projection //
          .from(listRef) //
          .from(classRef) //
          .from(units);
      SQLCondition condition = SQL.eq(units.elem(), classRef.column(classMapping.idField))
          .and(SQL.eq(classRef.column(classMapping.idField), listRef.column(sourceField))).and(SQL.eq(units.unit(), SQL.param()))
          .and(SQL.le(classRef.column(classMapping.createdField), SQL.param()))
          .and(SQL.eq(classRef.column(classMapping.revisedField), SQL.ZERO).or(SQL.ge(classRef.column(classMapping.revisedField), SQL.param())))
          .and(SQL.le(listRef.column(versionAddedField), classRef.column(classMapping.versionField)))
          .and(SQL.isNull(listRef.column(versionRemovedField)).or(SQL.gt(listRef.column(versionRemovedField), classRef.column(classMapping.versionField))));
      sqlSelectUnitEntries = query.where(condition) //
          .orderBy(SQL.asc(classRef.column(classMapping.idField)), SQL.asc(listRef.column(indexField))) //
          .compile(getDBAdapter());
    }

    IDBPreparedStatement stmt = DBUtil.prepare(sqlSelectUnitEntries, accessor.getDBConnection(), ReuseProbability.MEDIUM);
    sqlSelectUnitEntries.bindParameter(stmt, 1, rootID, idHandler);
    sqlSelectUnitEntries.bindParameter(stmt, 2, timeStamp);
    sqlSelectUnitEntries.bindParameter(stmt, 3, timeStamp);
    return stmt.executeQuery();
  }

  @Override
  public void readUnitEntries(ResultSet resultSet, IIDHandler idHandler, CDOID id, MoveableList<Object> list) throws SQLException
  {
    int size = list.size();
    for (int i = 0; i < size; i++)
    {
      resultSet.next();

      if (CHECK_UNIT_ENTRIES)
      {
        CDOID checkID = idHandler.getCDOID(resultSet, 1);
        if (checkID != id)
        {
          throw new IllegalStateException("Result set does not deliver expected result");
        }
      }

      Object value = typeMapping.readValue(resultSet);
      list.set(i, value);
    }
  }

  private void addEntry(IDBStoreAccessor accessor, CDOID id, int version, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding value for feature() {0}.{1} index {2} of {3}v{4} : {5}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, version, value);
    }

    IDBPreparedStatement stmt = DBUtil.prepare(sqlInsertEntry, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      int column = 1;
      sqlInsertEntry.bindParameter(stmt, column++, id, getStore().getIDHandler());
      sqlInsertEntry.bindParameter(stmt, column++, version);
      sqlInsertEntry.bindParameter(stmt, column++, index);
      sqlInsertEntry.bindParameter(stmt, column++, value, typeMapping);

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  private void addEntryFromOriginal(IDBStoreAccessor accessor, CDOID id, int newVersion, int originalIndex, int targetIndex)
  {
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlCopyOriginalEntry, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      int column = 1;
      sqlCopyOriginalEntry.bindParameter(stmt, column++, newVersion);
      sqlCopyOriginalEntry.bindParameter(stmt, column++, targetIndex);
      sqlCopyOriginalEntry.bindParameter(stmt, column++, id, idHandler);
      sqlCopyOriginalEntry.bindParameter(stmt, column++, originalIndex);
      sqlCopyOriginalEntry.bindParameter(stmt, column++, newVersion);
      sqlCopyOriginalEntry.bindParameter(stmt, column++, newVersion);

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

  private void removeEntry(IDBStoreAccessor accessor, CDOID id, int oldVersion, int newVersion, int index)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing value for feature() {0}.{1} index {2} of {3}v{4}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, newVersion);
    }

    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlDeleteEntry, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      // try to delete a temporary entry first
      int column = 1;
      sqlDeleteEntry.bindParameter(stmt, column++, id, idHandler);
      sqlDeleteEntry.bindParameter(stmt, column++, index);
      sqlDeleteEntry.bindParameter(stmt, column++, newVersion);

      int deleteResult = DBUtil.update(stmt, false);

      IDBPreparedStatement removeStmt = DBUtil.prepare(sqlRemoveEntry, accessor.getDBConnection(), ReuseProbability.HIGH);
      try
      {
        column = 1;
        sqlRemoveEntry.bindParameter(removeStmt, column++, newVersion);
        sqlRemoveEntry.bindParameter(removeStmt, column++, id, idHandler);
        sqlRemoveEntry.bindParameter(removeStmt, column++, index);
        sqlRemoveEntry.bindParameter(removeStmt, column++, newVersion);

        int removeResult = DBUtil.update(removeStmt, false);
        if (deleteResult > 1)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("removeEntry Too many results: {0}: {1}", index, deleteResult); //$NON-NLS-1$
          }

          throw new DBException("Too many results"); //$NON-NLS-1$
        }

        if (removeResult > 1 || deleteResult == 0 && removeResult == 0)
        {
          throw new DBException("Unexpected remove result"); //$NON-NLS-1$
        }
      }
      finally
      {
        DBUtil.close(removeStmt);
      }
    }
    catch (SQLException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature() {0}.{1} index {2} of {3}v{4} FAILED {5}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature() {0}.{1} index {2} of {3}v{4} FAILED {5}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  private Object getValue(IDBStoreAccessor accessor, CDOID id, int index)
  {
    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlGetValue, accessor.getDBConnection(), ReuseProbability.HIGH);
    Object result = null;

    try
    {
      sqlGetValue.binder(stmt) //
          .bindParameter(id, idHandler) //
          .bind(index);

      ResultSet resultSet = stmt.executeQuery();
      if (!resultSet.next())
      {
        throw new DBException("getValue() expects exactly one result");
      }

      result = typeMapping.readValue(resultSet);
      if (TRACER.isEnabled())
      {
        TRACER.format("Read value (index {0}) from result set: {1}", index, result); //$NON-NLS-1$
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }

    return result;
  }

  @Override
  public final boolean queryXRefs(IDBStoreAccessor accessor, String mainTableName, String mainTableWhere, QueryXRefsContext context, String idString)
  {
    if (table == null)
    {
      // Nothing to read. Take shortcut.
      return true;
    }

    String listJoin = getMappingStrategy().getListJoin("a_t", "l_t");

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT l_t."); //$NON-NLS-1$
    appendField(builder, sourceField);
    builder.append(", l_t."); //$NON-NLS-1$
    appendField(builder, valueField);
    builder.append(", l_t."); //$NON-NLS-1$
    appendField(builder, indexField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getDBAdapter().quoteTableName(table));
    builder.append(" l_t, ");//$NON-NLS-1$
    builder.append(mainTableName);
    builder.append(" a_t WHERE ");//$NON-NLS-1$
    builder.append("a_t.");//$NON-NLS-1$
    builder.append(mainTableWhere);
    builder.append(listJoin);
    builder.append(" AND "); //$NON-NLS-1$
    appendField(builder, valueField);
    builder.append(" IN "); //$NON-NLS-1$
    builder.append(idString);
    String sql = builder.toString();

    if (TRACER.isEnabled())
    {
      TRACER.format("Query XRefs (list): {0}", sql);
    }

    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.MEDIUM);
    ResultSet resultSet = null;

    try
    {
      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID sourceID = idHandler.getCDOID(resultSet, 1);
        CDOID targetID = idHandler.getCDOID(resultSet, 2);
        int idx = resultSet.getInt(3);

        boolean more = context.addXRef(targetID, sourceID, (EReference)getFeature(), idx);
        if (TRACER.isEnabled())
        {
          TRACER.format("  add XRef to context: src={0}, tgt={1}, idx={2}", sourceID, targetID, idx);
        }

        if (!more)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("  result limit reached. Ignoring further results.");
          }

          return false;
        }
      }

      return true;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public void processDelta(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion, long created, CDOListFeatureDelta delta)
  {
    List<CDOFeatureDelta> listChanges = delta.getListChanges();
    if (listChanges.size() == 0)
    {
      // nothing to do.
      return;
    }

    IRepository repository = accessor.getStore().getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDOBranchPoint head = repository.getBranchManager().getMainBranch().getHead();

    InternalCDORevision originalRevision = (InternalCDORevision)revisionManager.getRevision(id, head, UNCHUNKED_INITIAL_LIST_REVISION_CONFIG);
    processDelta(accessor, originalRevision, oldVersion, newVersion, listChanges);
  }

  @Override
  public void processDeltas(IDBStoreAccessor accessor, ListDeltaWork[] work, OMMonitor monitor)
  {
    monitor.begin(work.length);
    AuditDeltaBatch batch = new AuditDeltaBatch(accessor);
    List<ListDeltaWriter> writers = new ArrayList<>();
    List<List<CDOFeatureDelta>> changes = new ArrayList<>();
    boolean complete = false;
    try
    {
      for (ListDeltaWork item : work)
      {
        List<CDOFeatureDelta> listChanges = item.getDelta().getListChanges();
        if (listChanges.isEmpty())
        {
          monitor.worked();
          continue;
        }

        IRepository repository = accessor.getStore().getRepository();
        CDORevisionManager revisionManager = repository.getRevisionManager();
        CDOBranchPoint head = repository.getBranchManager().getMainBranch().getHead();
        InternalCDORevision originalRevision = (InternalCDORevision)revisionManager.getRevision(item.getID(), head, UNCHUNKED_INITIAL_LIST_REVISION_CONFIG);

        if (table == null)
        {
          initTable(accessor);
        }

        writers.add(new ListDeltaWriter(accessor, originalRevision, item.getOldVersion(), item.getNewVersion(), batch));
        changes.add(listChanges);
      }

      // Independent list works advance one semantic delta at a time. The phase boundary makes all writes from one
      // logical step visible before the next step of the same list is processed, while equal SQL shapes from
      // different works can share a JDBC batch.
      for (int deltaIndex = 0;; ++deltaIndex)
      {
        boolean more = false;
        for (int i = 0; i < writers.size(); i++)
        {
          List<CDOFeatureDelta> listChanges = changes.get(i);
          if (deltaIndex < listChanges.size())
          {
            listChanges.get(deltaIndex).accept(writers.get(i));
            more = true;
          }
        }

        if (!more)
        {
          break;
        }

        batch.flushPhase();
      }

      for (ListDeltaWriter writer : writers)
      {
        writer.finishPendingRemove();
        monitor.worked();
      }

      batch.flushPhase();
      complete = true;
    }
    finally
    {
      if (complete)
      {
        batch.release();
      }
      else
      {
        batch.discard();
      }

      monitor.done();
    }
  }

  private void processDelta(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int oldVersion, int newVersion, List<CDOFeatureDelta> listChanges)
  {
    if (TRACER.isEnabled())
    {
      int oldListSize = originalRevision.size(getFeature());
      TRACER.format("ListTableMapping.processDelta for revision {0} - previous list size: {1}", originalRevision, //$NON-NLS-1$
          oldListSize);
    }

    if (table == null)
    {
      initTable(accessor);
    }

    // let the visitor collect the changes
    ListDeltaWriter visitor = new ListDeltaWriter(accessor, originalRevision, oldVersion, newVersion);

    if (TRACER.isEnabled())
    {
      TRACER.format("Processing deltas..."); //$NON-NLS-1$
    }

    for (CDOFeatureDelta listDelta : listChanges)
    {
      listDelta.accept(visitor);
    }

    visitor.finishPendingRemove();
  }

  /**
   * A compact, value-independent representation of the logical list state while a delta sequence is processed.
   * Unchanged original elements remain grouped in ranges; only added, set, or moved elements are represented
   * individually.
   *
   * @author Eike Stepper
   */
  static final class LogicalListPlan
  {
    private final List<Segment> segments = new ArrayList<>();

    private int size;

    private long nextAddedID;

    LogicalListPlan(int originalSize)
    {
      if (originalSize < 0)
      {
        throw new IllegalArgumentException("Negative original size: " + originalSize); //$NON-NLS-1$
      }

      size = originalSize;
      if (originalSize != 0)
      {
        segments.add(new OriginalRange(0, originalSize));
      }
    }

    public int size()
    {
      return size;
    }

    public int getSegmentCount()
    {
      return segments.size();
    }

    public PlanElement get(int index)
    {
      Location location = locate(index, false);
      return location.segment.get(location.offset);
    }

    public PlanElement add(int index, Object value)
    {
      PlanElement element = PlanElement.added(++nextAddedID, value);
      insert(index, element);
      return element;
    }

    public PlanElement remove(int index)
    {
      Location location = locate(index, false);
      PlanElement element = location.segment.get(location.offset);
      removeAt(location);
      --size;
      return element;
    }

    public PlanElement set(int index, Object value)
    {
      Location location = locate(index, false);
      PlanElement element = location.segment.get(location.offset);
      if (element.isOriginal())
      {
        element = PlanElement.original(element.getOriginalIndex(), value);
        replaceAt(location, element);
      }
      else
      {
        element.setValue(value);
      }

      return element;
    }

    public PlanElement move(int sourceIndex, int targetIndex)
    {
      if (sourceIndex == targetIndex)
      {
        return get(sourceIndex);
      }

      PlanElement element = remove(sourceIndex);
      insert(targetIndex, element);
      return element;
    }

    public void clear()
    {
      segments.clear();
      size = 0;
    }

    private void insert(int index, PlanElement element)
    {
      Location location = locate(index, true);
      if (location == null)
      {
        segments.add(new ExplicitElement(element));
      }
      else if (location.offset == 0)
      {
        segments.add(location.segmentIndex, new ExplicitElement(element));
      }
      else
      {
        OriginalRange range = (OriginalRange)location.segment;
        segments.set(location.segmentIndex, new OriginalRange(range.originalStart, location.offset));
        segments.add(location.segmentIndex + 1, new ExplicitElement(element));
        segments.add(location.segmentIndex + 2, new OriginalRange(range.originalStart + location.offset, range.length - location.offset));
      }

      ++size;
      mergeOriginalRanges();
    }

    private void removeAt(Location location)
    {
      if (location.segment instanceof ExplicitElement)
      {
        segments.remove(location.segmentIndex);
      }
      else
      {
        OriginalRange range = (OriginalRange)location.segment;
        segments.remove(location.segmentIndex);
        if (location.offset != 0)
        {
          segments.add(location.segmentIndex++, new OriginalRange(range.originalStart, location.offset));
        }

        int remainingLength = range.length - location.offset - 1;
        if (remainingLength != 0)
        {
          segments.add(location.segmentIndex, new OriginalRange(range.originalStart + location.offset + 1, remainingLength));
        }
      }

      mergeOriginalRanges();
    }

    private void replaceAt(Location location, PlanElement element)
    {
      if (location.segment instanceof ExplicitElement)
      {
        segments.set(location.segmentIndex, new ExplicitElement(element));
      }
      else
      {
        OriginalRange range = (OriginalRange)location.segment;
        segments.remove(location.segmentIndex);
        if (location.offset != 0)
        {
          segments.add(location.segmentIndex++, new OriginalRange(range.originalStart, location.offset));
        }

        segments.add(location.segmentIndex++, new ExplicitElement(element));
        int remainingLength = range.length - location.offset - 1;
        if (remainingLength != 0)
        {
          segments.add(location.segmentIndex, new OriginalRange(range.originalStart + location.offset + 1, remainingLength));
        }
      }

      mergeOriginalRanges();
    }

    private Location locate(int index, boolean allowEnd)
    {
      if (index < 0 || index > size || !allowEnd && index == size)
      {
        throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size); //$NON-NLS-1$ //$NON-NLS-2$
      }

      if (index == size)
      {
        return null;
      }

      int currentIndex = 0;
      for (int i = 0; i < segments.size(); i++)
      {
        Segment segment = segments.get(i);
        int nextIndex = currentIndex + segment.length();
        if (index < nextIndex)
        {
          return new Location(i, segment, index - currentIndex);
        }

        currentIndex = nextIndex;
      }

      throw new IllegalStateException("No segment for index " + index); //$NON-NLS-1$
    }

    private void mergeOriginalRanges()
    {
      for (int i = 1; i < segments.size();)
      {
        Segment previous = segments.get(i - 1);
        Segment current = segments.get(i);
        if (previous instanceof OriginalRange && current instanceof OriginalRange)
        {
          OriginalRange previousRange = (OriginalRange)previous;
          OriginalRange currentRange = (OriginalRange)current;
          if (previousRange.originalStart + previousRange.length == currentRange.originalStart)
          {
            previousRange.length += currentRange.length;
            segments.remove(i);
            continue;
          }
        }

        ++i;
      }
    }

    /**
     * @author Eike Stepper
     */
    static final class PlanElement
    {
      private final int originalIndex;

      private final long addedID;

      private Object value;

      private boolean hasValue;

      private PlanElement(int originalIndex, long addedID, Object value, boolean hasValue)
      {
        this.originalIndex = originalIndex;
        this.addedID = addedID;
        this.value = value;
        this.hasValue = hasValue;
      }

      static PlanElement original(int originalIndex)
      {
        return new PlanElement(originalIndex, 0, null, false);
      }

      static PlanElement original(int originalIndex, Object value)
      {
        return new PlanElement(originalIndex, 0, value, true);
      }

      static PlanElement added(long addedID, Object value)
      {
        return new PlanElement(-1, addedID, value, true);
      }

      boolean isOriginal()
      {
        return originalIndex != -1;
      }

      int getOriginalIndex()
      {
        return originalIndex;
      }

      long getAddedID()
      {
        return addedID;
      }

      boolean hasValue()
      {
        return hasValue;
      }

      Object getValue()
      {
        return value;
      }

      void setValue(Object value)
      {
        this.value = value;
        hasValue = true;
      }

      String getIdentity()
      {
        return isOriginal() ? "O" + originalIndex : "A" + addedID; //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    /**
     * @author Eike Stepper
     */
    private abstract static class Segment
    {
      public abstract int length();

      public abstract PlanElement get(int offset);
    }

    /**
     * @author Eike Stepper
     */
    private static final class OriginalRange extends Segment
    {
      private final int originalStart;

      private int length;

      public OriginalRange(int originalStart, int length)
      {
        this.originalStart = originalStart;
        this.length = length;
      }

      @Override
      public int length()
      {
        return length;
      }

      @Override
      public PlanElement get(int offset)
      {
        return PlanElement.original(originalStart + offset);
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class ExplicitElement extends Segment
    {
      private final PlanElement element;

      public ExplicitElement(PlanElement element)
      {
        this.element = element;
      }

      @Override
      public int length()
      {
        return 1;
      }

      @Override
      public PlanElement get(int offset)
      {
        return element;
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class Location
    {
      private int segmentIndex;

      private final Segment segment;

      private final int offset;

      public Location(int segmentIndex, Segment segment, int offset)
      {
        this.segmentIndex = segmentIndex;
        this.segment = segment;
        this.offset = offset;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AuditDeltaBatch
  {
    private final IDBStoreAccessor accessor;

    private BatchedStatement insertEntryStmt;

    private BatchedStatement copyOriginalEntryStmt;

    private BatchedStatement deleteEntryStmt;

    private BatchedStatement removeEntryStmt;

    private BatchedStatement clearListStmt;

    private BatchedStatement deleteListStmt;

    private int explicitEntryCount;

    private int originalEntryCount;

    private int removalCount;

    private AuditDeltaBatch(IDBStoreAccessor accessor)
    {
      this.accessor = accessor;
    }

    public void addEntry(CDOID id, int version, int index, Object value)
    {
      try
      {
        BatchedStatement stmt = getInsertEntryStmt();
        sqlInsertEntry.bindParameter(stmt, 1, id, getStore().getIDHandler());
        sqlInsertEntry.bindParameter(stmt, 2, version);
        sqlInsertEntry.bindParameter(stmt, 3, index);
        sqlInsertEntry.bindParameter(stmt, 4, value, typeMapping);
        add(stmt);
        ++explicitEntryCount;
      }
      catch (SQLException | IllegalStateException ex)
      {
        throw new DBException(ex);
      }
    }

    public void addEntryFromOriginal(CDOID id, int newVersion, int originalIndex, int targetIndex)
    {
      flushRemovalsForOriginalCopy();

      try
      {
        BatchedStatement stmt = getCopyOriginalEntryStmt();
        sqlCopyOriginalEntry.bindParameter(stmt, 1, newVersion);
        sqlCopyOriginalEntry.bindParameter(stmt, 2, targetIndex);
        sqlCopyOriginalEntry.bindParameter(stmt, 3, id, getStore().getIDHandler());
        sqlCopyOriginalEntry.bindParameter(stmt, 4, originalIndex);
        sqlCopyOriginalEntry.bindParameter(stmt, 5, newVersion);
        sqlCopyOriginalEntry.bindParameter(stmt, 6, newVersion);
        add(stmt);
        ++originalEntryCount;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void removeEntry(CDOID id, int newVersion, int index)
    {
      try
      {
        BatchedStatement deleteStmt = getDeleteEntryStmt();
        sqlDeleteEntry.bindParameter(deleteStmt, 1, id, getStore().getIDHandler());
        sqlDeleteEntry.bindParameter(deleteStmt, 2, index);
        sqlDeleteEntry.bindParameter(deleteStmt, 3, newVersion);
        add(deleteStmt);

        BatchedStatement removeStmt = getRemoveEntryStmt();
        sqlRemoveEntry.bindParameter(removeStmt, 1, newVersion);
        sqlRemoveEntry.bindParameter(removeStmt, 2, id, getStore().getIDHandler());
        sqlRemoveEntry.bindParameter(removeStmt, 3, index);
        sqlRemoveEntry.bindParameter(removeStmt, 4, newVersion);
        add(removeStmt);
        ++removalCount;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void clearList(CDOID id, int newVersion)
    {
      try
      {
        BatchedStatement deleteStmt = getDeleteListStmt();
        sqlDeleteList.bindParameter(deleteStmt, 1, id, getStore().getIDHandler());
        sqlDeleteList.bindParameter(deleteStmt, 2, newVersion);
        add(deleteStmt);

        BatchedStatement clearStmt = getClearListStmt();
        sqlClearList.bindParameter(clearStmt, 1, newVersion);
        sqlClearList.bindParameter(clearStmt, 2, id, getStore().getIDHandler());
        sqlClearList.bindParameter(clearStmt, 3, newVersion);
        add(clearStmt);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void flushForIndexShift()
    {
      flushRemovals();
      flush(insertEntryStmt);
      flush(copyOriginalEntryStmt);
      flush(clearListStmt);
      flush(deleteListStmt);
      validateResults();
    }

    public void flushPhase()
    {
      flushRemovals();
      accessor.getBatchingContext().flushPhase();
      validateResults();
    }

    public void release()
    {
      release(insertEntryStmt);
      release(copyOriginalEntryStmt);
      release(deleteEntryStmt);
      release(removeEntryStmt);
      release(clearListStmt);
      release(deleteListStmt);
    }

    public void discard()
    {
      discard(insertEntryStmt);
      discard(copyOriginalEntryStmt);
      discard(deleteEntryStmt);
      discard(removeEntryStmt);
      discard(clearListStmt);
      discard(deleteListStmt);
    }

    private void flushRemovalsForOriginalCopy()
    {
      flushRemovals();
      validateRemovals();
    }

    private void flushRemovals()
    {
      flush(deleteEntryStmt);
      flush(removeEntryStmt);
    }

    private void validateRemovals()
    {
      int knownResult = getTotalResult(deleteEntryStmt) + getTotalResult(removeEntryStmt);
      int unknownResultCount = getUnknownResultCount(deleteEntryStmt) + getUnknownResultCount(removeEntryStmt);
      if (knownResult > 2 * removalCount || knownResult + unknownResultCount < removalCount)
      {
        throw new DBException("Unexpected remove result"); //$NON-NLS-1$
      }
    }

    private void validateResults()
    {
      validateExactlyOne(insertEntryStmt, explicitEntryCount);
      validateExactlyOne(copyOriginalEntryStmt, originalEntryCount);
      validateRemovals();
    }

    private void validateExactlyOne(BatchedStatement stmt, int entryCount)
    {
      int knownResult = getTotalResult(stmt);
      int unknownResultCount = getUnknownResultCount(stmt);
      if (hasUnexpectedResult(knownResult, unknownResultCount, entryCount))
      {
        throw new DBException("Unexpected insert result"); //$NON-NLS-1$
      }
    }

    private boolean hasUnexpectedResult(int knownResult, int unknownResultCount, int expectedCount)
    {
      return knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount;
    }

    private void add(BatchedStatement stmt) throws SQLException
    {
      stmt.executeUpdate();
    }

    private void flush(BatchedStatement stmt)
    {
      if (stmt != null)
      {
        accessor.getBatchingContext().flushStatement(stmt);
      }
    }

    private void release(BatchedStatement stmt)
    {
      if (stmt != null)
      {
        accessor.getBatchingContext().releaseStatement(stmt);
      }
    }

    private void discard(BatchedStatement stmt)
    {
      if (stmt != null)
      {
        accessor.getBatchingContext().discardStatement(stmt);
      }
    }

    private int getTotalResult(BatchedStatement stmt)
    {
      return stmt == null ? 0 : stmt.getTotalResult();
    }

    private int getUnknownResultCount(BatchedStatement stmt)
    {
      return stmt == null ? 0 : stmt.getUnknownResultCount();
    }

    private BatchedStatement getInsertEntryStmt()
    {
      if (insertEntryStmt == null)
      {
        insertEntryStmt = createStatement(sqlInsertEntry, "AuditList.insert"); //$NON-NLS-1$
      }

      return insertEntryStmt;
    }

    private BatchedStatement getCopyOriginalEntryStmt()
    {
      if (copyOriginalEntryStmt == null)
      {
        copyOriginalEntryStmt = createStatement(sqlCopyOriginalEntry, "AuditList.copyOriginal"); //$NON-NLS-1$
      }

      return copyOriginalEntryStmt;
    }

    private BatchedStatement getDeleteEntryStmt()
    {
      if (deleteEntryStmt == null)
      {
        deleteEntryStmt = createStatement(sqlDeleteEntry, "AuditList.deleteEntry"); //$NON-NLS-1$
      }

      return deleteEntryStmt;
    }

    private BatchedStatement getRemoveEntryStmt()
    {
      if (removeEntryStmt == null)
      {
        removeEntryStmt = createStatement(sqlRemoveEntry, "AuditList.removeEntry"); //$NON-NLS-1$
      }

      return removeEntryStmt;
    }

    private BatchedStatement getClearListStmt()
    {
      if (clearListStmt == null)
      {
        clearListStmt = createStatement(sqlClearList, "AuditList.clear"); //$NON-NLS-1$
      }

      return clearListStmt;
    }

    private BatchedStatement getDeleteListStmt()
    {
      if (deleteListStmt == null)
      {
        deleteListStmt = createStatement(sqlDeleteList, "AuditList.deleteList"); //$NON-NLS-1$
      }

      return deleteListStmt;
    }

    private BatchedStatement createStatement(SQLStatement sql, String diagnosticName)
    {
      return accessor.getBatchingContext().createStatement(sql, ReuseProbability.HIGH, diagnosticName);
    }
  }

  /**
   * @author Stefan Winkler
   */
  private class ListDeltaWriter extends AbstractRangeListDeltaWriter
  {
    private final LogicalListPlan logicalListPlan;

    private final AuditDeltaBatch batch;

    public ListDeltaWriter(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int oldVersion, int newVersion)
    {
      this(accessor, originalRevision, oldVersion, newVersion, null);
    }

    public ListDeltaWriter(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int oldVersion, int newVersion, AuditDeltaBatch batch)
    {
      super(accessor, originalRevision, oldVersion, newVersion, TRACER);
      logicalListPlan = new LogicalListPlan(originalRevision.size(getFeature()));
      this.batch = batch;
    }

    @Override
    protected int getOldListSize(InternalCDORevision originalRevision)
    {
      return originalRevision.size(getFeature());
    }

    @Override
    protected Object getValue(int index)
    {
      return AuditListTableMappingWithRanges.this.getValue(accessor, id, index);
    }

    @Override
    protected Object getMoveValue(CDOMoveFeatureDelta delta, int sourceIndex)
    {
      return new MovePayload(logicalListPlan.move(delta.getOldPosition(), delta.getNewPosition()));
    }

    @Override
    protected void updateLogicalList(CDOFeatureDelta delta)
    {
      if (delta instanceof CDOAddFeatureDelta)
      {
        CDOAddFeatureDelta addDelta = (CDOAddFeatureDelta)delta;
        logicalListPlan.add(addDelta.getIndex(), addDelta.getValue());
      }
      else if (delta instanceof CDORemoveFeatureDelta)
      {
        logicalListPlan.remove(((CDORemoveFeatureDelta)delta).getIndex());
      }
      else if (delta instanceof CDOSetFeatureDelta)
      {
        CDOSetFeatureDelta setDelta = (CDOSetFeatureDelta)delta;
        logicalListPlan.set(setDelta.getIndex(), setDelta.getValue());
      }
      else if (delta instanceof CDOClearFeatureDelta || delta instanceof CDOUnsetFeatureDelta)
      {
        logicalListPlan.clear();
      }
      else
      {
        throw new IllegalArgumentException("Unsupported list delta: " + delta); //$NON-NLS-1$
      }
    }

    @Override
    protected void removeEntry(int index)
    {
      if (batch == null)
      {
        AuditListTableMappingWithRanges.this.removeEntry(accessor, id, oldVersion, newVersion, index);
      }
      else
      {
        batch.removeEntry(id, newVersion, index);
      }
    }

    @Override
    protected void addEntry(int index, Object value)
    {
      if (value instanceof MovePayload)
      {
        LogicalListPlan.PlanElement element = ((MovePayload)value).element;
        if (element.isOriginal() && !element.hasValue())
        {
          if (batch == null)
          {
            addEntryFromOriginal(accessor, id, newVersion, element.getOriginalIndex(), index);
          }
          else
          {
            batch.addEntryFromOriginal(id, newVersion, element.getOriginalIndex(), index);
          }

          return;
        }

        value = element.getValue();
      }

      if (batch == null)
      {
        AuditListTableMappingWithRanges.this.addEntry(accessor, id, newVersion, index, value);
      }
      else
      {
        batch.addEntry(id, newVersion, index, value);
      }
    }

    @Override
    protected void clearList()
    {
      if (batch == null)
      {
        AuditListTableMappingWithRanges.this.clearList(accessor, id, oldVersion, newVersion);
      }
      else
      {
        batch.clearList(id, newVersion);
      }
    }

    @Override
    protected void moveOneUp(int startIndex, int endIndex)
    {
      moveOneUp(accessor, id, newVersion, startIndex, endIndex);
    }

    @Override
    protected void moveOneDown(int startIndex, int endIndex)
    {
      moveOneDown(accessor, id, newVersion, startIndex, endIndex);
    }

    /**
     * @author Eike Stepper
     */
    private final class MovePayload
    {
      private final LogicalListPlan.PlanElement element;

      public MovePayload(LogicalListPlan.PlanElement element)
      {
        this.element = element;
      }
    }

    private void moveOneUp(IDBStoreAccessor accessor, CDOID id, int newVersion, int startIndex, int endIndex)
    {
      shiftRange(accessor, id, newVersion, startIndex, endIndex, -1);
    }

    private void moveOneDown(IDBStoreAccessor accessor, CDOID id, int newVersion, int startIndex, int endIndex)
    {
      shiftRange(accessor, id, newVersion, startIndex, endIndex, 1);
    }

    private void shiftRange(IDBStoreAccessor accessor, CDOID id, int newVersion, int startIndex, int endIndex, int indexDelta)
    {
      if (batch != null)
      {
        batch.flushForIndexShift();
      }

      if (startIndex > endIndex)
      {
        return;
      }

      IIDHandler idHandler = getStore().getIDHandler();
      IDBPreparedStatement updateStmt = DBUtil.prepare(sqlShiftCurrentIndexes, accessor.getDBConnection(), ReuseProbability.HIGH);
      IDBPreparedStatement copyStmt = DBUtil.prepare(sqlCopyShiftedEntry, accessor.getDBConnection(), ReuseProbability.HIGH);
      IDBPreparedStatement closeStmt = DBUtil.prepare(sqlCloseShiftedEntry, accessor.getDBConnection(), ReuseProbability.HIGH);

      try
      {
        sqlShiftCurrentIndexes.bindParameter(updateStmt, 1, indexDelta);
        sqlShiftCurrentIndexes.bindParameter(updateStmt, 2, id, idHandler);
        sqlShiftCurrentIndexes.bindParameter(updateStmt, 3, newVersion);
        sqlShiftCurrentIndexes.bindParameter(updateStmt, 4, startIndex);
        sqlShiftCurrentIndexes.bindParameter(updateStmt, 5, endIndex);
        int updated = DBUtil.update(updateStmt, false);

        sqlCopyShiftedEntry.bindParameter(copyStmt, 1, newVersion);
        sqlCopyShiftedEntry.bindParameter(copyStmt, 2, indexDelta);
        sqlCopyShiftedEntry.bindParameter(copyStmt, 3, id, idHandler);
        sqlCopyShiftedEntry.bindParameter(copyStmt, 4, startIndex);
        sqlCopyShiftedEntry.bindParameter(copyStmt, 5, endIndex);
        sqlCopyShiftedEntry.bindParameter(copyStmt, 6, newVersion);
        int copied = DBUtil.update(copyStmt, false);

        sqlCloseShiftedEntry.bindParameter(closeStmt, 1, newVersion);
        sqlCloseShiftedEntry.bindParameter(closeStmt, 2, id, idHandler);
        sqlCloseShiftedEntry.bindParameter(closeStmt, 3, startIndex);
        sqlCloseShiftedEntry.bindParameter(closeStmt, 4, endIndex);
        sqlCloseShiftedEntry.bindParameter(closeStmt, 5, newVersion);
        int closed = DBUtil.update(closeStmt, false);

        int expected = endIndex - startIndex + 1;
        if (copied != closed || copied + updated != expected)
        {
          throw new DBException("Unexpected index shift result: updated=" + updated + ", copied=" + copied + ", closed=" + closed + ", expected=" + expected); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      catch (SQLException e)
      {
        throw new DBException(e);
      }
      finally
      {
        DBUtil.close(closeStmt);
        DBUtil.close(copyStmt);
        DBUtil.close(updateStmt);
      }
    }
  }
}
