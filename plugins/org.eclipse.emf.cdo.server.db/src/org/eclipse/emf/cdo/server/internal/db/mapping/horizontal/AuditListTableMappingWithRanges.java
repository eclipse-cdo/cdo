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
  private String sqlSelectChunksPrefix;

  /**
   * This field is initialized on demand in {@link #queryUnitEntries(IDBStoreAccessor, IIDHandler, long, CDOID)}
   * and not in {@link #initSQLStrings()} because the initialization requires the {@link #classMapping} field value.
   */
  private String sqlSelectUnitEntries;

  private String sqlInsertEntry;

  private String sqlCopyOriginalEntry;

  private String sqlDeleteEntry;

  private String sqlRemoveEntry;

  private String sqlUpdateIndex;

  private String sqlCopyShiftedEntry;

  private String sqlCloseShiftedEntry;

  private String sqlGetValue;

  private String sqlClearList;

  private String sqlDeleteList;

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

    IDBStore store = getMappingStrategy().getStore();
    DBType idType = store.getIDHandler().getDBType();
    int idLength = store.getIDColumnLength();

    IDBDatabase database = getMappingStrategy().getStore().getDatabase();
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
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL OR "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(">?)"); //$NON-NLS-1$
    sqlSelectChunksPrefix = builder.toString();

    // ----------------- insert entry -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(table);
    builder.append("("); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(","); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(") VALUES (?, ?, NULL, ?, ?)"); //$NON-NLS-1$
    sqlInsertEntry = builder.toString();

    // ----------------- copy original entry for move -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append("("); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(","); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(") SELECT "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(", ?, NULL, ?, "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<>? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append("=?"); //$NON-NLS-1$
    sqlCopyOriginalEntry = builder.toString();

    // ----------------- remove current entry -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<>?"); //$NON-NLS-1$
    sqlRemoveEntry = builder.toString();

    // ----------------- delete temporary entry -----------------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteEntry = builder.toString();

    // ----------------- update index -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------------- copy older entry for index shift -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append("("); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(","); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(") SELECT "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(", ?, NULL, ?, "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<>?"); //$NON-NLS-1$
    sqlCopyShiftedEntry = builder.toString();

    // ----------------- close older entry for index shift -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<>?"); //$NON-NLS-1$
    sqlCloseShiftedEntry = builder.toString();

    // ----------------- get current value -----------------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlGetValue = builder.toString();

    // ----------- clear list items -------------------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<>?"); //$NON-NLS-1$
    sqlClearList = builder.toString();

    // ----------- delete temporary list items -------------------------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlDeleteList = builder.toString();
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

  protected final IDBTable getTable()
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
    if (list == null)
    {
      // Nothing to read take shortcut.
      return;
    }

    if (listChunk == 0 || list.size() == 0)
    {
      // Nothing to read take shortcut.
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision.getID(), revision.getVersion());
    }

    String sql = sqlSelectChunksPrefix + " ORDER BY " + indexField;

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      idHandler.setCDOID(stmt, 1, revision.getID());
      stmt.setInt(2, revision.getVersion());
      stmt.setInt(3, revision.getVersion());

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

    StringBuilder builder = new StringBuilder(sqlSelectChunksPrefix);
    if (where != null)
    {
      builder.append(" AND "); //$NON-NLS-1$
      builder.append(where);
    }

    builder.append(" ORDER BY " + indexField);
    String sql = builder.toString();

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = chunkReader.getAccessor().getDBConnection().prepareStatement(sql, ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      idHandler.setCDOID(stmt, 1, chunkReader.getRevision().getID());
      stmt.setInt(2, chunkReader.getRevision().getVersion());
      stmt.setInt(3, chunkReader.getRevision().getVersion());

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
      InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)getMappingStrategy().getStore().getRepository().getRevisionManager();
      InternalCDORevision baseRevision = revisionManager.getBaseRevision(revision, CDORevision.UNCHUNKED, true);

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
            int column = 1;

            IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
            idHandler.setCDOID(stmt, column++, revision.getID());
            stmt.setInt(column++, revision.getVersion());
            stmt.setInt(column++, index++);
            getTypeMapping().setValue(stmt, column, value);
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
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmtDeleteTemp = accessor.getDBConnection().prepareStatement(sqlDeleteList, ReuseProbability.HIGH);
    IDBPreparedStatement stmtClear = accessor.getDBConnection().prepareStatement(sqlClearList, ReuseProbability.HIGH);

    try
    {
      // delete temporary entries
      idHandler.setCDOID(stmtDeleteTemp, 1, id);
      stmtDeleteTemp.setInt(2, newVersion);

      int deleteResult = DBUtil.update(stmtDeleteTemp, false);
      if (TRACER.isEnabled())
      {
        TRACER.format("DeleteList result: {0}", deleteResult); //$NON-NLS-1$
      }

      // clear rest of the list
      stmtClear.setInt(1, newVersion);
      idHandler.setCDOID(stmtClear, 2, id);
      stmtClear.setInt(3, newVersion);

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

    IRepository repository = getMappingStrategy().getStore().getRepository();
    CDOBranch main = repository.getBranchManager().getMainBranch();

    // get revision from cache to find out version number
    CDORevision revision = repository.getRevisionManager().getRevision(id, main.getHead(), 0, CDORevision.DEPTH_NONE, true);

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
      DBStore store = (DBStore)getMappingStrategy().getStore();
      UnitMappingTable units = store.getUnitMappingTable();

      // The sqlSelectUnitEntries field is initialized here and not in initSQLStrings()
      // because the initialization requires the classMapping field value.

      sqlSelectUnitEntries = "SELECT " + (CHECK_UNIT_ENTRIES ? classMapping.idField + ", " : "") + "cdo_list." + valueField + //
          " FROM " + table + " cdo_list, " + classMapping.table + ", " + units + //
          " WHERE " + units.elem() + "=" + classMapping.idField + //
          " AND " + classMapping.idField + "=cdo_list." + sourceField + //
          " AND " + units.unit() + "=?" + //
          " AND " + classMapping.createdField + "<=?" + //
          " AND (" + classMapping.revisedField + "=0 OR " + classMapping.revisedField + ">=?)" + //
          " AND cdo_list." + versionAddedField + "<=" + classMapping.versionField + //
          " AND (cdo_list." + versionRemovedField + " IS NULL OR cdo_list." + versionRemovedField + ">" + classMapping.versionField + ") ORDER BY cdo_list."
          + sourceField + ", cdo_list." + indexField;
    }

    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelectUnitEntries, ReuseProbability.MEDIUM);
    idHandler.setCDOID(stmt, 1, rootID);
    stmt.setLong(2, timeStamp);
    stmt.setLong(3, timeStamp);
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

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsertEntry, ReuseProbability.HIGH);

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, version);
      stmt.setInt(column++, index);
      typeMapping.setValue(stmt, column++, value);

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
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlCopyOriginalEntry, ReuseProbability.HIGH);

    try
    {
      int column = 1;
      stmt.setInt(column++, newVersion);
      stmt.setInt(column++, targetIndex);
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, originalIndex);
      stmt.setInt(column++, newVersion);
      stmt.setInt(column++, newVersion);

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

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlDeleteEntry, ReuseProbability.HIGH);

    try
    {
      // try to delete a temporary entry first
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, index);
      stmt.setInt(column++, newVersion);

      int deleteResult = DBUtil.update(stmt, false);

      IDBPreparedStatement removeStmt = accessor.getDBConnection().prepareStatement(sqlRemoveEntry, ReuseProbability.HIGH);
      try
      {
        column = 1;
        removeStmt.setInt(column++, newVersion);
        idHandler.setCDOID(removeStmt, column++, id);
        removeStmt.setInt(column++, index);
        removeStmt.setInt(column++, newVersion);

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
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlGetValue, ReuseProbability.HIGH);
    Object result = null;

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, index);

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
    builder.append(sourceField);
    builder.append(", l_t."); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(", l_t."); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" l_t, ");//$NON-NLS-1$
    builder.append(mainTableName);
    builder.append(" a_t WHERE ");//$NON-NLS-1$
    builder.append("a_t.");//$NON-NLS-1$
    builder.append(mainTableWhere);
    builder.append(listJoin);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" IN "); //$NON-NLS-1$
    builder.append(idString);
    String sql = builder.toString();

    if (TRACER.isEnabled())
    {
      TRACER.format("Query XRefs (list): {0}", sql);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
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

    InternalCDORevision originalRevision = (InternalCDORevision)revisionManager.getRevision(id, head, /* chunksize = */0, CDORevision.DEPTH_NONE, true);
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
        InternalCDORevision originalRevision = (InternalCDORevision)revisionManager.getRevision(item.getID(), head,
            /* chunksize = */0, CDORevision.DEPTH_NONE, true);

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
        int column = 1;
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column++, version);
        stmt.setInt(column++, index);
        typeMapping.setValue(stmt, column, value);
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
        int column = 1;
        stmt.setInt(column++, newVersion);
        stmt.setInt(column++, targetIndex);
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column++, originalIndex);
        stmt.setInt(column++, newVersion);
        stmt.setInt(column++, newVersion);
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
        int column = 1;
        BatchedStatement deleteStmt = getDeleteEntryStmt();
        getMappingStrategy().getStore().getIDHandler().setCDOID(deleteStmt, column++, id);
        deleteStmt.setInt(column++, index);
        deleteStmt.setInt(column++, newVersion);
        add(deleteStmt);

        column = 1;
        BatchedStatement removeStmt = getRemoveEntryStmt();
        removeStmt.setInt(column++, newVersion);
        getMappingStrategy().getStore().getIDHandler().setCDOID(removeStmt, column++, id);
        removeStmt.setInt(column++, index);
        removeStmt.setInt(column++, newVersion);
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
        int column = 1;
        BatchedStatement deleteStmt = getDeleteListStmt();
        getMappingStrategy().getStore().getIDHandler().setCDOID(deleteStmt, column++, id);
        deleteStmt.setInt(column++, newVersion);
        add(deleteStmt);

        column = 1;
        BatchedStatement clearStmt = getClearListStmt();
        clearStmt.setInt(column++, newVersion);
        getMappingStrategy().getStore().getIDHandler().setCDOID(clearStmt, column++, id);
        clearStmt.setInt(column++, newVersion);
        add(clearStmt);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void flushForIndexShift()
    {
      flush(insertEntryStmt);
      flush(copyOriginalEntryStmt);
      flushRemovals();
      flush(clearListStmt);
      flush(deleteListStmt);
      validateResults();
    }

    public void flushPhase()
    {
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

    private BatchedStatement createStatement(String sql, String diagnosticName)
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
      if (batch != null)
      {
        batch.flushForIndexShift();
      }

      moveOneUp(accessor, id, oldVersion, newVersion, startIndex, endIndex);
    }

    @Override
    protected void moveOneDown(int startIndex, int endIndex)
    {
      if (batch != null)
      {
        batch.flushForIndexShift();
      }

      moveOneDown(accessor, id, oldVersion, newVersion, startIndex, endIndex);
    }

    private final class MovePayload
    {
      private final LogicalListPlan.PlanElement element;

      public MovePayload(LogicalListPlan.PlanElement element)
      {
        this.element = element;
      }
    }

    private void moveOneUp(IDBStoreAccessor accessor, CDOID id, int oldVersion, int newVersion, int startIndex, int endIndex)
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IDBPreparedStatement moveStmt = accessor.getDBConnection().prepareStatement(sqlUpdateIndex, ReuseProbability.HIGH);
      IDBPreparedStatement copyStmt = accessor.getDBConnection().prepareStatement(sqlCopyShiftedEntry, ReuseProbability.HIGH);
      IDBPreparedStatement closeStmt = accessor.getDBConnection().prepareStatement(sqlCloseShiftedEntry, ReuseProbability.HIGH);

      try
      {
        for (int index = startIndex; index <= endIndex; ++index)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("moveOneUp moving: {0} -> {1}", index, index - 1); //$NON-NLS-1$
          }

          shiftIndex(idHandler, moveStmt, copyStmt, closeStmt, index, index - 1);
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
        DBUtil.close(moveStmt);
      }
    }

    private void moveOneDown(IDBStoreAccessor accessor, CDOID id, int oldVersion, int newVersion, int startIndex, int endIndex)
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IDBPreparedStatement moveStmt = accessor.getDBConnection().prepareStatement(sqlUpdateIndex, ReuseProbability.HIGH);
      IDBPreparedStatement copyStmt = accessor.getDBConnection().prepareStatement(sqlCopyShiftedEntry, ReuseProbability.HIGH);
      IDBPreparedStatement closeStmt = accessor.getDBConnection().prepareStatement(sqlCloseShiftedEntry, ReuseProbability.HIGH);

      try
      {
        for (int index = endIndex; index >= startIndex; --index)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("moveOneDown moving: {0} -> {1}", index, index + 1); //$NON-NLS-1$
          }

          shiftIndex(idHandler, moveStmt, copyStmt, closeStmt, index, index + 1);
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
        DBUtil.close(moveStmt);
      }
    }

    private void shiftIndex(IIDHandler idHandler, IDBPreparedStatement moveStmt, IDBPreparedStatement copyStmt, IDBPreparedStatement closeStmt, int sourceIndex,
        int targetIndex) throws SQLException
    {
      int column = 1;
      copyStmt.setInt(column++, newVersion);
      copyStmt.setInt(column++, targetIndex);
      idHandler.setCDOID(copyStmt, column++, id);
      copyStmt.setInt(column++, sourceIndex);
      copyStmt.setInt(column++, newVersion);
      int copyResult = DBUtil.update(copyStmt, false);

      column = 1;
      closeStmt.setInt(column++, newVersion);
      idHandler.setCDOID(closeStmt, column++, id);
      closeStmt.setInt(column++, sourceIndex);
      closeStmt.setInt(column++, newVersion);
      int closeResult = DBUtil.update(closeStmt, false);

      column = 1;
      moveStmt.setInt(column++, targetIndex);
      idHandler.setCDOID(moveStmt, column++, id);
      moveStmt.setInt(column++, newVersion);
      moveStmt.setInt(column++, sourceIndex);
      int moveResult = DBUtil.update(moveStmt, false);

      if (copyResult > 1 || closeResult > 1 || moveResult > 1 || copyResult != closeResult || copyResult + moveResult != 1)
      {
        throw new DBException("Unexpected index shift result"); //$NON-NLS-1$
      }
    }
  }
}
