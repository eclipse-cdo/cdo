/*
 * Copyright (c) 2010-2013, 2015, 2016, 2018-2021, 2023-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * This class has been derived from AbstractListTableMapping
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation taken from AuditListTableMappingWithRanges
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping4;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractBasicListTableMapping.ListLobRefsUpdater;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.Batch;
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
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a list-table mapping for audit mode. It is optimized for frequent insert operations at the list's end, which
 * causes just 1 DB row to be changed. This is achieved by introducing a version range (columns cdo_version_added and
 * cdo_version_removed) which records for which revisions a particular entry existed. Also, this mapping is mainly
 * optimized for potentially very large lists: the need for having the complete list stored in memopy to do
 * in-the-middle-moved and inserts is traded in for a few more DB access operations.
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 * @author Lothar Werzinger
 */
public class BranchingListTableMappingWithRanges extends AbstractBasicListTableMapping
    implements ISchemaPreparable, IListMappingDeltaSupport, IListMappingBatchingSupport, IListMapping4, IBranchDeletionSupport, ListLobRefsUpdater
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, BranchingListTableMappingWithRanges.class);

  /**
   * Used to clean up lists for detached objects.
   */
  private static final int FINAL_VERSION = Integer.MAX_VALUE;

  private IDBTable table;

  private IDBField sourceField;

  private IDBField branchField;

  private IDBField indexField;

  private IDBField versionAddedField;

  private IDBField versionRemovedField;

  private IDBField valueField;

  /**
   * The type mapping for the value field.
   */
  private ITypeMapping typeMapping;

  // --------- SQL strings - see initSQLStrings() -----------------
  private String sqlSelectChunksPrefix;

  private String sqlOrderByIndex;

  private String sqlInsertEntry;

  private String sqlDeleteEntry;

  private String sqlRemoveEntry;

  private String sqlUpdateIndex;

  private String sqlGetValue;

  private String sqlClearList;

  private String sqlClearSuffix;

  public BranchingListTableMappingWithRanges(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
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
          branchField = table.addField(MappingNames.LIST_REVISION_BRANCH, DBType.INTEGER, true);
          versionAddedField = table.addField(MappingNames.LIST_REVISION_VERSION_ADDED, DBType.INTEGER);
          versionRemovedField = table.addField(MappingNames.LIST_REVISION_VERSION_REMOVED, DBType.INTEGER);
          indexField = table.addField(MappingNames.LIST_IDX, DBType.INTEGER, true);

          table.addIndex(Type.NON_UNIQUE, sourceField, branchField, versionAddedField, versionRemovedField, indexField);

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
      branchField = table.getField(MappingNames.LIST_REVISION_BRANCH);
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

  private void initSQLStrings()
  {
    // ---------------- read chunks ----------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL OR "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(">?)"); //$NON-NLS-1$
    sqlSelectChunksPrefix = builder.toString();

    sqlOrderByIndex = " ORDER BY " + indexField; //$NON-NLS-1$

    // ----------------- insert entry -----------------
    builder = new StringBuilder("INSERT INTO "); //$NON-NLS-1$
    builder.append(table);
    builder.append("("); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(","); //$NON-NLS-1$
    builder.append(branchField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(","); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(","); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(") VALUES (?, ?, ?, ?, ?, ?)"); //$NON-NLS-1$
    sqlInsertEntry = builder.toString();

    // ----------------- remove current entry -----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlRemoveEntry = builder.toString();

    // ----------------- delete temporary entry -----------------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
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
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionAddedField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------------- get current value -----------------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
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
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionRemovedField);
    builder.append(" IS NULL"); //$NON-NLS-1$
    sqlClearList = builder.toString();

    // ----------- clear local suffix items -----------------
    builder = new StringBuilder(sqlClearList);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(">=?"); //$NON-NLS-1$
    sqlClearSuffix = builder.toString();
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
  public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, final int listChunk)
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

    int valuesToRead = list.size();
    if (listChunk != CDORevision.UNCHUNKED && listChunk < valuesToRead)
    {
      valuesToRead = listChunk;
    }

    if (valuesToRead == 0)
    {
      // Nothing to read take shortcut.
      return;
    }

    CDOID id = revision.getID();
    int branchID = revision.getBranch().getID();

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), revision);
    }

    String sql = sqlSelectChunksPrefix + sqlOrderByIndex;

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.HIGH);
    ResultSet resultSet = null;
    ArrayList<Pair<Integer, Integer>> toReadFromBase = null; // list of chunks to be read from base revision

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branchID);
      stmt.setInt(3, revision.getVersion());
      stmt.setInt(4, revision.getVersion());
      stmt.setMaxRows(valuesToRead); // optimization - don't read unneeded rows.

      resultSet = stmt.executeQuery();

      int currentIndex = 0;

      while (valuesToRead > 0 && resultSet.next())
      {
        int index = resultSet.getInt(1);
        if (index > currentIndex)
        {
          if (toReadFromBase == null)
          {
            toReadFromBase = new ArrayList<>();
          }
          toReadFromBase.add(Pair.create(currentIndex, index));

          if (TRACER.isEnabled())
          {
            TRACER.format("Scheduling range {0}-{1} to be read from base revision", currentIndex, index); //$NON-NLS-1$
          }

          valuesToRead -= index - currentIndex;
          currentIndex = index;
        }

        Object value = typeMapping.readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", currentIndex, value); //$NON-NLS-1$
        }

        list.set(currentIndex++, value);
        valuesToRead--;
      }

      if (valuesToRead > 0)
      {
        if (toReadFromBase == null)
        {
          toReadFromBase = new ArrayList<>();
        }
        toReadFromBase.add(Pair.create(currentIndex, currentIndex + valuesToRead));
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

    // read missing values from base revision ...
    if (toReadFromBase != null)
    {
      IStoreChunkReader baseReader = createBaseChunkReader(accessor, id, branchID);

      if (TRACER.isEnabled())
      {
        TRACER.format("Reading base revision chunks for feature {0}.{1} of {2} from base revision {3}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), revision, baseReader.getRevision());
      }

      for (Pair<Integer, Integer> range : toReadFromBase)
      {
        baseReader.addRangedChunk(range.getElement1(), range.getElement2());
      }

      List<Chunk> baseChunks = baseReader.executeRead();
      for (Chunk chunk : baseChunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int i = 0; i < chunk.size(); i++)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Copying value {0} at chunk index {1}+{2} to index {3}", //$NON-NLS-1$
                chunk.get(i), startIndex, i, startIndex + i);
          }

          list.set(startIndex + i, chunk.get(i));
        }
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {3} list values done for feature {0}.{1} of {2}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), revision, list.size());
    }
  }

  @Override
  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature {0}.{1} of {2}", getContainingClass().getName(), //$NON-NLS-1$
          getFeature().getName(), chunkReader.getRevision());
    }

    CDORevision revision = chunkReader.getRevision();
    CDOID id = revision.getID();
    int branchID = revision.getBranch().getID();

    StringBuilder builder = new StringBuilder(sqlSelectChunksPrefix);
    if (where != null)
    {
      builder.append(" AND "); //$NON-NLS-1$
      builder.append(where);
    }

    builder.append(sqlOrderByIndex);
    String sql = builder.toString();

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = chunkReader.getAccessor().getDBConnection().prepareStatement(sql, ReuseProbability.LOW);
    ResultSet resultSet = null;
    IStoreChunkReader baseReader = null;

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branchID);
      stmt.setInt(3, revision.getVersion());
      stmt.setInt(4, revision.getVersion());

      if (TRACER.isEnabled())
      {
        TRACER.format("Readung Chunks: {0}", stmt); //$NON-NLS-1$
      }

      resultSet = stmt.executeQuery();

      int nextDBIndex = Integer.MAX_VALUE; // next available DB index
      if (resultSet.next())
      {
        nextDBIndex = resultSet.getInt(1);
      }

      for (Chunk chunk : chunks)
      {
        int startIndex = chunk.getStartIndex();
        int missingValueStartIndex = -1;

        for (int i = 0; i < chunk.size(); i++)
        {
          int nextListIndex = startIndex + i; // next expected list index

          if (nextDBIndex == nextListIndex)
          {
            // DB value is available. check first if missing indexes were present before.
            if (missingValueStartIndex != -1)
            {
              // read missing indexes from missingValueStartIndex to currentIndex
              if (baseReader == null)
              {
                baseReader = createBaseChunkReader(chunkReader.getAccessor(), id, branchID);
              }

              if (TRACER.isEnabled())
              {
                TRACER.format("Scheduling range {0}-{1} to be read from base revision", missingValueStartIndex, //$NON-NLS-1$
                    nextListIndex);
              }

              baseReader.addRangedChunk(missingValueStartIndex, nextListIndex);

              // reset missingValueStartIndex
              missingValueStartIndex = -1;
            }

            // now read value and set to chunk
            Object value = typeMapping.readValue(resultSet);
            if (TRACER.isEnabled())
            {
              TRACER.format("ChunkReader read value for index {0} from result set: {1}", nextDBIndex, value); //$NON-NLS-1$
            }

            chunk.add(i, value);

            // advance DB cursor and read next available index
            if (resultSet.next())
            {
              nextDBIndex = resultSet.getInt(1);
            }
            else
            {
              // no more DB indexes available, but we have to continue checking for gaps, therefore set to MAX_VALUE
              nextDBIndex = Integer.MAX_VALUE;
            }
          }
          else
          {
            // gap between next DB index and next list index detected.
            // skip until end of chunk or until DB value becomes available
            if (missingValueStartIndex == -1)
            {
              missingValueStartIndex = nextListIndex;
            }
          }
        }

        // chunk complete. check for missing values at the end of the chunk.
        if (missingValueStartIndex != -1)
        {
          // read missing indexes from missingValueStartIndex to last chunk index
          if (baseReader == null)
          {
            baseReader = createBaseChunkReader(chunkReader.getAccessor(), id, branchID);
          }

          if (TRACER.isEnabled())
          {
            TRACER.format("Scheduling range {0}-{1} to be read from base revision", missingValueStartIndex, //$NON-NLS-1$
                chunk.getStartIndex() + chunk.size());
          }

          baseReader.addRangedChunk(missingValueStartIndex, chunk.getStartIndex() + chunk.size());
        }
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

    // now read missing values from base revision.
    if (baseReader != null)
    {
      List<Chunk> baseChunks = baseReader.executeRead();

      Iterator<Chunk> thisIterator = chunks.iterator();
      Chunk thisChunk = thisIterator.next();

      for (Chunk baseChunk : baseChunks)
      {
        int baseStartIndex = baseChunk.getStartIndex();

        while (baseStartIndex > thisChunk.getStartIndex() + thisChunk.size())
        {
          // advance thisChunk, because it does not match baseChunk
          thisChunk = thisIterator.next();
        }

        // baseChunk now corresponds to thisChunk, but startIndex of baseChunk may be higher.
        // therefore calculate offset
        int offset = baseStartIndex - thisChunk.getStartIndex();

        // and copy values.
        for (int i = 0; i < baseChunk.size(); i++)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Copying base chunk reader value {0} at index {1} to current chunk reader at index {2}.", baseChunk.get(i),
                baseChunk.getStartIndex() + i, thisChunk.getStartIndex() + i + offset);
          }

          thisChunk.add(i + offset, baseChunk.get(i));
        } // finally, continue with the next baseChunk
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), revision);
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
        int branchID = revision.getBranch().getID();
        int oldVersion = baseRevision.getVersion();
        int newVersion = revision.getVersion();

        processDelta(accessor, baseRevision, branchID, oldVersion, newVersion, delta.getListChanges());
      }
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

  @Override
  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean raw, OMMonitor monitor)
  {
    if (!firstRevision && raw)
    {
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.rawSynchronousFallback"); //$NON-NLS-1$
      monitor.begin(revisions.length);

      try
      {
        // Raw branch imports compare against the base revision and must retain the existing synchronous path.
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

    if (revisions.length == 0)
    {
      return;
    }

    if (table == null)
    {
      initTable(accessor);
    }

    IBatchingContext batchingContext = accessor.getBatchingContext();
    BatchedStatement stmt = batchingContext.createStatement(sqlInsertEntry, ReuseProbability.HIGH, "BranchingList.insert"); //$NON-NLS-1$
    boolean discarded = false;
    int count = 0;

    monitor.begin(revisions.length);

    try
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      for (InternalCDORevision revision : revisions)
      {
        CDOList values = revision.getListOrNull(getFeature());
        if (values != null)
        {
          int index = 0;
          for (Object value : values)
          {
            setEntryValues(idHandler, stmt, revision.getID(), revision.getBranch().getID(), revision.getVersion(), index++, value, null);
            stmt.executeUpdate();
            count++;
          }
        }

        monitor.worked();
      }

      batchingContext.flushPhase();
      validateExactlyOne(stmt, count);
      batchingContext.recordDiagnosticCounter("BranchingList.insertRows", count); //$NON-NLS-1$
    }
    catch (SQLException ex)
    {
      batchingContext.discardStatement(stmt);
      discarded = true;
      throw new DBException(ex);
    }
    catch (IllegalStateException ex)
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

      monitor.done();
    }
  }

  /**
   * Plans and persists all list deltas for this mapping as one commit-wide
   * operation.
   * <p>
   * The method deliberately separates logical planning from JDBC execution.
   * Each {@link ListDeltaWork} identifies one object, branch, and target
   * version. The values carried by its plan are payload only; they are never
   * used as element identities, which is essential for lists containing
   * duplicate-equal values.
   * <p>
   * Every work item is first classified while its logical list coordinates
   * are still available. Append plans are the most specific readless case.
   * Other deltas are applied to a mutable base-aware logical plan, which may
   * prove a suffix rewrite or sparse stable-index update safe. Only plans
   * that cannot prove one of those restricted shapes use the complete
   * snapshot planner and resolve their original/base payloads.
   * <p>
   * The resulting DML is grouped by statement kind across independent list
   * mappings through {@link BranchingDeltaBatch}. Closing or versioning old
   * local rows is flushed before new rows are inserted because the new rows
   * must not be affected by the close phase. The method owns statement
   * lifecycle and batching barriers, but not the surrounding transaction.
   *
   * @param accessor the store accessor used for revision lookup and batched persistence
   * @param work the list delta work items belonging to the commit
   * @param monitor the progress monitor for the work items
   */
  @Override
  public void processDeltas(IDBStoreAccessor accessor, ListDeltaWork[] work, OMMonitor monitor)
  {
    BranchingDeltaBatch batch = new BranchingDeltaBatch(accessor);
    List<BranchingDeltaPlan> plans = new ArrayList<>();
    List<BranchingAppendPlan> appendPlans = new ArrayList<>();
    List<BranchingSuffixPlan> suffixPlans = new ArrayList<>();
    List<BranchingSparseSetPlan> sparseSetPlans = new ArrayList<>();
    boolean complete = false;

    monitor.begin(work.length);

    try
    {
      for (ListDeltaWork item : work)
      {
        List<CDOFeatureDelta> changes = item.getDelta().getListChanges();
        if (!changes.isEmpty())
        {
          // The transaction revision supplies the original logical size. It is
          // needed for position-aware classification even when the eventual
          // fast path does not read any original or inherited payloads.
          InternalCDORevision originalRevision = (InternalCDORevision)accessor.getTransaction().getRevision(item.getID());
          if (originalRevision == null)
          {
            throw new IllegalStateException("Original revision not found for " + item.getID()); //$NON-NLS-1$
          }

          // Append-only is checked first because it is the narrowest and
          // cheapest plan: existing local and inherited rows remain untouched.
          BranchingAppendPlan appendPlan = tryCreateAppendPlan(originalRevision, item);
          if (appendPlan == null)
          {
            // The general planner mutates logical positions as it applies the
            // delta. Its final state can prove that only an explicit suffix
            // needs rewriting, without incorrectly classifying positions up
            // front before Adds, Removes, or Moves have taken effect.
            BranchingDeltaPlan plan = new BranchingDeltaPlan(accessor, originalRevision, item);

            BranchingSuffixPlan suffixPlan = plan.createSuffixRewritePlan();
            if (suffixPlan != null)
            {
              suffixPlans.add(suffixPlan);
            }
            else
            {
              // A sparse Set plan is restricted to stable original indexes and
              // optional tail appends. Any structural ambiguity goes to the
              // authoritative full snapshot fallback.
              BranchingSparseSetPlan sparseSetPlan = tryCreateSparseSetPlan(originalRevision, item);
              if (sparseSetPlan == null)
              {
                plans.add(plan);
              }
              else
              {
                sparseSetPlans.add(sparseSetPlan);
              }
            }
          }
          else
          {
            appendPlans.add(appendPlan);
          }
        }

        monitor.worked();
      }

      // Only full snapshot plans need original/base payload resolution. The
      // readless plans already carry every value that they will insert.
      for (BranchingDeltaPlan plan : plans)
      {
        plan.resolveOriginalValues();
      }

      // Close old local overlays and changed sparse/suffix rows together. The
      // payload resolution above must precede this phase because closing rows
      // deliberately makes their old values invisible to later lookups.
      for (BranchingDeltaPlan plan : plans)
      {
        batch.clearLocalEntries(plan);
      }

      for (BranchingSuffixPlan suffixPlan : suffixPlans)
      {
        batch.clearLocalSuffix(suffixPlan);
      }

      for (BranchingSparseSetPlan sparseSetPlan : sparseSetPlans)
      {
        batch.clearSparseSetEntries(sparseSetPlan);
      }

      // This is the required ordering barrier: all old rows are closed before
      // any new-version row is inserted, while independent lists still share
      // the same homogeneous statements on both sides of the barrier.
      batch.flushClearPhase();

      // Insert all readless and snapshot results only after the close phase.
      // The common insert statement makes these independent rows eligible for
      // commit-wide JDBC batching.
      for (BranchingAppendPlan appendPlan : appendPlans)
      {
        batch.addAppends(appendPlan);
      }

      for (BranchingSparseSetPlan sparseSetPlan : sparseSetPlans)
      {
        batch.addSparseSetValues(sparseSetPlan);
      }

      for (BranchingSuffixPlan suffixPlan : suffixPlans)
      {
        batch.addSuffix(suffixPlan);
      }

      for (BranchingDeltaPlan plan : plans)
      {
        batch.addSnapshot(plan);
      }

      // Finish the insert phase and validate that every planned row was
      // accounted for by exactly one insert path before releasing statements.
      batch.flushPhase();
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.appendFastPath", appendPlans.size()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.appendRows", batch.getAppendEntryCount()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.suffixRewriteFastPath", suffixPlans.size()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.suffixRewriteRows", batch.getSuffixEntryCount()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.sparseSetFastPath", sparseSetPlans.size()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.sparseSetRows", batch.getSparseSetEntryCount()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.readlessPlan", appendPlans.size() + suffixPlans.size() + sparseSetPlans.size()); //$NON-NLS-1$
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.snapshotFallback", plans.size()); //$NON-NLS-1$
      complete = true;
    }
    finally
    {
      // A complete batch releases reusable statements; an interrupted or
      // failed batch discards them so no partially bound state can escape.
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

  private BranchingAppendPlan tryCreateAppendPlan(InternalCDORevision originalRevision, ListDeltaWork work)
  {
    int logicalSize = originalRevision.size(getFeature());
    List<Object> values = new ArrayList<>();

    for (CDOFeatureDelta delta : work.getDelta().getListChanges())
    {
      if (!(delta instanceof CDOAddFeatureDelta))
      {
        return null;
      }

      CDOAddFeatureDelta add = (CDOAddFeatureDelta)delta;
      if (add.getIndex() != logicalSize + values.size())
      {
        return null;
      }

      values.add(add.getValue());
    }

    return values.isEmpty() ? null : new BranchingAppendPlan(work, logicalSize, values);
  }

  private BranchingSparseSetPlan tryCreateSparseSetPlan(InternalCDORevision originalRevision, ListDeltaWork work)
  {
    int originalSize = originalRevision.size(getFeature());
    int logicalSize = originalSize;

    Map<Integer, Object> values = new LinkedHashMap<>();
    List<Object> appends = new ArrayList<>();

    for (CDOFeatureDelta delta : work.getDelta().getListChanges())
    {
      if (delta instanceof CDOSetFeatureDelta)
      {
        CDOSetFeatureDelta set = (CDOSetFeatureDelta)delta;
        if (set.getIndex() < 0 || set.getIndex() >= originalSize)
        {
          return null;
        }

        values.put(set.getIndex(), set.getValue());
      }
      else if (delta instanceof CDOAddFeatureDelta)
      {
        CDOAddFeatureDelta add = (CDOAddFeatureDelta)delta;
        if (add.getIndex() != logicalSize)
        {
          return null;
        }

        appends.add(add.getValue());
        ++logicalSize;
      }
      else
      {
        return null;
      }
    }

    return values.isEmpty() ? null : new BranchingSparseSetPlan(work, values, originalSize, appends);
  }

  protected final void writeValue(IDBStoreAccessor accessor, CDORevision revision, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing value for feature {0}.{1} index {2} of {3} : {4}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, revision, value);
    }

    addEntry(accessor, revision.getID(), revision.getBranch().getID(), revision.getVersion(), index, value);
  }

  @Override
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList)
  {
    if (table == null)
    {
      return;
    }

    batch.add("DELETE FROM " + table + " WHERE " + branchField + " IN (" + idList + ")");
  }

  /**
   * Clear a list of a given revision.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   * @param lastListIndex
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id, int branchID, int oldVersion, int newVersion, int lastListIndex)
  {
    // check for each index if the value exists in the current branch
    for (int i = 0; i <= lastListIndex; i++)
    {
      if (getValue(accessor, id, branchID, i, false) == null)
      {
        // if not, add a historic entry for missing ones.
        addHistoricEntry(accessor, id, branchID, 0, newVersion, i, getValueFromBase(accessor, id, branchID, i));
      }
    }

    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlClearList, ReuseProbability.HIGH);

    try
    {
      // clear rest of the list
      stmt.setInt(1, newVersion);
      getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 2, id);
      stmt.setInt(3, branchID);

      int result = DBUtil.update(stmt, false);
      if (TRACER.isEnabled())
      {
        TRACER.format("ClearList result: {0}", result); //$NON-NLS-1$
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
  }

  @Override
  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    ITransaction transaction = accessor.getTransaction();
    InternalCDORevision revision = (InternalCDORevision)transaction.getRevision(id);
    if (revision == null)
    {
      // This must be an attempt to resurrect an object, i.e., revise its detached revision
      return;
    }

    if (table == null)
    {
      initTable(accessor);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("objectDetached {1}", revision); //$NON-NLS-1$
    }

    int branchID = transaction.getBranch().getID();
    int version = revision.getVersion();
    int lastListIndex = revision.size(getFeature()) - 1;

    clearList(accessor, id, branchID, version, FINAL_VERSION, lastListIndex);
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    throw new UnsupportedOperationException("Raw deletion does not work in range-based mappings");
  }

  @Override
  public void processDelta(IDBStoreAccessor accessor, CDOID id, int branchID, int oldVersion, int newVersion, long created, CDOListFeatureDelta delta)
  {
    List<CDOFeatureDelta> listChanges = delta.getListChanges();
    if (listChanges.size() == 0)
    {
      // nothing to do.
      return;
    }

    InternalCDORevision originalRevision = (InternalCDORevision)accessor.getTransaction().getRevision(id);
    processDelta(accessor, originalRevision, branchID, oldVersion, newVersion, listChanges);
  }

  private void processDelta(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int branchID, int oldVersion, int newVersion,
      List<CDOFeatureDelta> listChanges)
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
    ListDeltaWriter visitor = new ListDeltaWriter(accessor, originalRevision, branchID, oldVersion, newVersion);

    if (TRACER.isEnabled())
    {
      TRACER.format("Processing deltas..."); //$NON-NLS-1$
    }

    // optimization: it's only necessary to process deltas
    // starting with the last feature delta which clears the list
    // (any operation before the clear is cascaded by it anyway)
    int index = listChanges.size() - 1;
    while (index > 0)
    {
      CDOFeatureDelta listDelta = listChanges.get(index);
      if (listDelta instanceof CDOClearFeatureDelta || listDelta instanceof CDOUnsetFeatureDelta)
      {
        break;
      }

      index--;
    }
    while (index < listChanges.size())
    {
      listChanges.get(index++).accept(visitor);
    }

    visitor.finishPendingRemove();
  }

  private void addEntry(IDBStoreAccessor accessor, CDOID id, int branchID, int version, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding value for feature {0}.{1} index {2} of {3}:{4}v{5} : {6}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, branchID, version, value);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsertEntry, ReuseProbability.HIGH);

    try
    {
      setEntryValues(idHandler, stmt, id, branchID, version, index, value, null);

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

  private void setEntryValues(IIDHandler idHandler, PreparedStatement stmt, CDOID id, int branchID, int versionAdded, int index, Object value,
      Integer versionRemoved) throws SQLException
  {
    int column = 1;
    idHandler.setCDOID(stmt, column++, id);
    stmt.setInt(column++, branchID);
    stmt.setInt(column++, versionAdded);

    if (versionRemoved == null)
    {
      stmt.setNull(column++, DBType.INTEGER.getCode());
    }
    else
    {
      stmt.setInt(column++, versionRemoved);
    }

    stmt.setInt(column++, index);
    typeMapping.setValue(stmt, column, value);
  }

  private void validateExactlyOne(BatchedStatement stmt, int expectedCount)
  {
    if (stmt == null)
    {
      if (expectedCount != 0)
      {
        throw new DBException("Missing branching list insert statement"); //$NON-NLS-1$
      }

      return;
    }

    int knownResult = stmt.getTotalResult();
    int unknownResultCount = stmt.getUnknownResultCount();
    if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
    {
      throw new DBException("Unexpected branching list insert result"); //$NON-NLS-1$
    }
  }

  private void addHistoricEntry(IDBStoreAccessor accessor, CDOID id, int branchID, int versionAdded, int versionRemoved, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding historic value for feature {0}.{1} index {2} of {3}:{4}v{5}-v{6} : {7}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, branchID, versionAdded, versionRemoved, value);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsertEntry, ReuseProbability.HIGH);

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchID);
      stmt.setInt(column++, versionAdded); // versionAdded
      stmt.setInt(column++, versionRemoved); // versionRemoved
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

  private void removeEntry(IDBStoreAccessor accessor, CDOID id, int branchID, int oldVersion, int newVersion, int index)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing value for feature {0}.{1} index {2} of {3}:{4}v{5}", //$NON-NLS-1$
          getContainingClass().getName(), getFeature().getName(), index, id, branchID, newVersion);
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlDeleteEntry, ReuseProbability.HIGH);

    try
    {
      // Try to delete a temporary entry first
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchID);
      stmt.setInt(column++, index);
      stmt.setInt(column++, newVersion);

      int result = DBUtil.update(stmt, false);
      if (result == 1)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("removeEntry deleted: {0}", index); //$NON-NLS-1$
        }
      }
      else if (result > 1)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("removeEntry Too many results: {0}: {1}", index, result); //$NON-NLS-1$
        }

        throw new DBException("Too many results"); //$NON-NLS-1$
      }
      else
      {
        // no temporary entry found, so mark the entry as removed
        DBUtil.close(stmt);
        stmt = accessor.getDBConnection().prepareStatement(sqlRemoveEntry, ReuseProbability.HIGH);

        column = 1;
        stmt.setInt(column++, newVersion);
        idHandler.setCDOID(stmt, column++, id);
        stmt.setInt(column++, branchID);
        stmt.setInt(column++, index);

        result = DBUtil.update(stmt, false);

        if (result == 0)
        {
          // no entry removed -> this means that we are in a branch and
          // the entry has not been modified since the branch fork.
          // therefore, we have to copy the base value and mark it as removed
          Object value = getValueFromBase(accessor, id, branchID, index);
          addHistoricEntry(accessor, id, branchID, 0, newVersion, index, value);
        }
      }
    }
    catch (SQLException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature {0}.{1} index {2} of {3}:{4}v{5} FAILED {6}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, branchID, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    catch (IllegalStateException e)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing value for feature {0}.{1} index {2} of {3}:{4}v{5} FAILED {6}", //$NON-NLS-1$
            getContainingClass().getName(), getFeature().getName(), index, id, branchID, newVersion, e.getMessage());
      }

      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  /**
   * Read a single value from the current revision's list.
   *
   * @param accessor
   *          the store accessor
   * @param id
   *          the revision's ID
   * @param branchID
   *          the revision's branch ID
   * @param index
   *          the index from which to get the value
   * @param getFromBase
   *          if <code>true</code>, the value is recursively loaded from the base revision of a branch, if it is not
   *          present in the current branch (because it has not been changed since the branch fork). If
   *          <code>false</code>, <code>null</code> is returned in the former case.
   */
  private Object getValue(IDBStoreAccessor accessor, CDOID id, int branchID, int index, boolean getFromBase)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlGetValue, ReuseProbability.HIGH);
    Object result = null;

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, branchID);
      stmt.setInt(column++, index);

      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next())
      {
        result = typeMapping.readValue(resultSet);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value (index {0}) from result set: {1}", index, result); //$NON-NLS-1$
        }
      }
      else
      {
        // value is not in this branch.
        // -> read from base revision
        if (getFromBase)
        {
          result = getValueFromBase(accessor, id, branchID, index);
        } // else: result remains null
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

  /**
   * Read a single value (at a given index) from the base revision
   *
   * @param accessor
   *          the DBStoreAccessor
   * @param id
   *          the ID of the revision
   * @param branchID
   *          the ID of the current (child) branch
   * @param index
   *          the index to read the value from
   * @return the value which is at index <code>index</code> in revision with ID <code>id</code> in the parent branch at
   *         the base of this branch (indicated by <code>branchID</code>).
   */
  private Object getValueFromBase(IDBStoreAccessor accessor, CDOID id, int branchID, int index)
  {
    IStoreChunkReader chunkReader = createBaseChunkReader(accessor, id, branchID);
    chunkReader.addSimpleChunk(index);
    List<Chunk> chunks = chunkReader.executeRead();
    return chunks.get(0).get(0);
  }

  private IStoreChunkReader createBaseChunkReader(IDBStoreAccessor accessor, CDOID id, int branchID)
  {
    InternalRepository repository = (InternalRepository)accessor.getStore().getRepository();

    CDOBranchManager branchManager = repository.getBranchManager();
    CDOBranch branch = branchManager.getBranch(branchID);
    CDOBranchPoint base = branch.getBase();
    if (base.getBranch() == null)
    {
      throw new IllegalArgumentException("Base branch is null: " + branch);
    }

    InternalCDORevisionManager revisionManager = repository.getRevisionManager();
    InternalCDORevision baseRevision = revisionManager.getRevision(id, base, 0, CDORevision.DEPTH_NONE, true);

    return accessor.createChunkReader(baseRevision, getFeature());
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
    builder.append("a_t." + mainTableWhere);//$NON-NLS-1$
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

  /**
   * Plans one branching list delta without using values as element identities.
   * <p>
   * Original elements retain their original logical position identity through all add, remove, set, and move
   * operations. Added elements use a plan-local identity. Values are deliberately resolved only after the complete
   * mutable logical plan is known. The existing range reader then resolves local rows in one query and inherited
   * gaps through grouped base chunks.
   *
   * @author Eike Stepper
   */
  private final class BranchingDeltaPlan
  {
    private final IDBStoreAccessor accessor;

    private final InternalCDORevision originalRevision;

    private final ListDeltaWork work;

    private final AuditListTableMappingWithRanges.LogicalListPlan logicalListPlan;

    private List<Object> values;

    private BranchingDeltaPlan(IDBStoreAccessor accessor, InternalCDORevision originalRevision, ListDeltaWork work)
    {
      this.accessor = accessor;
      this.originalRevision = originalRevision;
      this.work = work;
      logicalListPlan = new AuditListTableMappingWithRanges.LogicalListPlan(originalRevision.size(getFeature()));

      for (CDOFeatureDelta delta : work.getDelta().getListChanges())
      {
        apply(delta);
      }
    }

    public CDOID getID()
    {
      return work.getID();
    }

    public int getBranchID()
    {
      return work.getBranchId();
    }

    public int getNewVersion()
    {
      return work.getNewVersion();
    }

    public int getOldVersion()
    {
      return work.getOldVersion();
    }

    public int size()
    {
      return logicalListPlan.size();
    }

    public Object getValue(int index)
    {
      return values.get(index);
    }

    public void resolveOriginalValues()
    {
      List<Object> originalValues = readOriginalValues();

      values = new ArrayList<>(logicalListPlan.size());
      long inheritedOrLocalCount = 0;

      for (int index = 0; index < logicalListPlan.size(); index++)
      {
        AuditListTableMappingWithRanges.LogicalListPlan.PlanElement element = logicalListPlan.get(index);
        if (element.hasValue())
        {
          values.add(element.getValue());
        }
        else
        {
          values.add(originalValues.get(element.getOriginalIndex()));
          inheritedOrLocalCount++;
        }
      }

      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.plannerResolvedOriginal", inheritedOrLocalCount); //$NON-NLS-1$
    }

    public BranchingSuffixPlan createSuffixRewritePlan()
    {
      int suffixStart = 0;
      int size = logicalListPlan.size();

      while (suffixStart < size)
      {
        AuditListTableMappingWithRanges.LogicalListPlan.PlanElement element = logicalListPlan.get(suffixStart);
        if (!element.isOriginal() || element.hasValue() || element.getOriginalIndex() != suffixStart)
        {
          break;
        }

        ++suffixStart;
      }

      for (int index = suffixStart; index < size; index++)
      {
        if (!logicalListPlan.get(index).hasValue())
        {
          return null;
        }
      }

      if (suffixStart == size && size == originalRevision.size(getFeature()))
      {
        return null;
      }

      List<Object> values = new ArrayList<>(size - suffixStart);
      for (int index = suffixStart; index < size; index++)
      {
        values.add(logicalListPlan.get(index).getValue());
      }

      return new BranchingSuffixPlan(work, suffixStart, values);
    }

    private List<Object> readOriginalValues()
    {
      int size = originalRevision.size(getFeature());
      List<Object> originalValues = new ArrayList<>(Collections.nCopies(size, null));
      List<Pair<Integer, Integer>> missingRanges = new ArrayList<>();

      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelectChunksPrefix + sqlOrderByIndex, ReuseProbability.HIGH);
      ResultSet resultSet = null;

      try
      {
        idHandler.setCDOID(stmt, 1, getID());
        stmt.setInt(2, getBranchID());
        stmt.setInt(3, getOldVersion());
        stmt.setInt(4, getOldVersion());
        resultSet = stmt.executeQuery();

        int nextIndex = 0;

        while (resultSet.next())
        {
          int index = resultSet.getInt(1);
          if (index >= size)
          {
            break;
          }

          if (nextIndex < index)
          {
            missingRanges.add(Pair.create(nextIndex, index));
          }

          originalValues.set(index, typeMapping.readValue(resultSet));
          nextIndex = index + 1;
        }

        if (nextIndex < size)
        {
          missingRanges.add(Pair.create(nextIndex, size));
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

      if (!missingRanges.isEmpty())
      {
        IStoreChunkReader baseReader = createBaseChunkReader(accessor, getID(), getBranchID());

        for (Pair<Integer, Integer> range : missingRanges)
        {
          baseReader.addRangedChunk(range.getElement1(), range.getElement2());
        }

        for (Chunk chunk : baseReader.executeRead())
        {
          int startIndex = chunk.getStartIndex();

          for (int i = 0; i < chunk.size(); i++)
          {
            originalValues.set(startIndex + i, chunk.get(i));
          }
        }

        accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.plannerBaseRanges", missingRanges.size()); //$NON-NLS-1$
      }

      return originalValues;
    }

    private void apply(CDOFeatureDelta delta)
    {
      if (delta instanceof CDOAddFeatureDelta)
      {
        CDOAddFeatureDelta add = (CDOAddFeatureDelta)delta;
        logicalListPlan.add(add.getIndex(), add.getValue());
      }
      else if (delta instanceof CDORemoveFeatureDelta)
      {
        logicalListPlan.remove(((CDORemoveFeatureDelta)delta).getIndex());
      }
      else if (delta instanceof CDOSetFeatureDelta)
      {
        CDOSetFeatureDelta set = (CDOSetFeatureDelta)delta;
        logicalListPlan.set(set.getIndex(), set.getValue());
      }
      else if (delta instanceof CDOMoveFeatureDelta)
      {
        CDOMoveFeatureDelta move = (CDOMoveFeatureDelta)delta;
        logicalListPlan.move(move.getOldPosition(), move.getNewPosition());
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
  }

  /**
   * A readless plan for a delta consisting exclusively of appends at the
   * logical end of the list.
   * <p>
   * {@code startIndex} is the original logical list size and {@code values}
   * contains the append payloads in their final order. The plan is therefore
   * independent of the values already present in the list, including values
   * inherited from a base revision or represented by older local rows. No
   * existing row is closed and no original/base row is resolved; only the
   * new rows are inserted with the work item's branch and version metadata.
   * <p>
   * Append eligibility is established while processing the delta: every
   * change must be an Add whose index equals the current logical end. The
   * plan stores payloads, not identities, so duplicate-equal appended values
   * remain distinct by their final indexes.
   *
   * @author Eike Stepper
   */
  private final class BranchingAppendPlan
  {
    private final ListDeltaWork work;

    private final int startIndex;

    private final List<Object> values;

    private BranchingAppendPlan(ListDeltaWork work, int startIndex, List<Object> values)
    {
      this.work = work;
      this.startIndex = startIndex;
      this.values = values;
    }

    public CDOID getID()
    {
      return work.getID();
    }

    public int getBranchID()
    {
      return work.getBranchId();
    }

    public int getNewVersion()
    {
      return work.getNewVersion();
    }

    public int getStartIndex()
    {
      return startIndex;
    }

    public List<Object> getValues()
    {
      return values;
    }
  }

  /**
   * A readless plan for rewriting only a final suffix of a branching list.
   * <p>
   * The preceding range {@code [0, startIndex)} has been proven unchanged
   * relative to the original logical list and is left untouched. Every value
   * in the suffix is explicit in the completed logical plan, meaning that no
   * value from a current local row, an older local row, or the base revision
   * has to be resolved. Persistence closes active local rows at and after the
   * start index, then inserts the supplied suffix at its final indexes.
   * <p>
   * This plan is derived after all delta operations have mutated logical
   * coordinates. It consequently covers safe combinations such as tail
   * removals, tail replacements, and clear/unset followed by explicit adds,
   * while structural changes that leave an unresolved original element in
   * the suffix remain on the full snapshot path.
   *
   * @author Eike Stepper
   */
  private final class BranchingSuffixPlan
  {
    private final ListDeltaWork work;

    private final int startIndex;

    private final List<Object> values;

    private BranchingSuffixPlan(ListDeltaWork work, int startIndex, List<Object> values)
    {
      this.work = work;
      this.startIndex = startIndex;
      this.values = values;
    }

    public CDOID getID()
    {
      return work.getID();
    }

    public int getBranchID()
    {
      return work.getBranchId();
    }

    public int getNewVersion()
    {
      return work.getNewVersion();
    }

    public int getStartIndex()
    {
      return startIndex;
    }

    public List<Object> getValues()
    {
      return values;
    }
  }

  /**
   * A readless plan for value changes at stable original indexes, optionally
   * followed by appends at the tail.
   * <p>
   * The keys of {@code values} are logical indexes from the original list;
   * they are stable because this plan accepts no operation that moves,
   * removes, clears, or inserts before an existing element. The map is
   * therefore an identity/provenance record rather than a value comparison:
   * equal values at different indexes remain separate changes. At execution
   * time an active local row at each changed index is closed, and the new
   * value is inserted. If no local row exists, the old value is inherited and
   * need not be read before replacement.
   * <p>
   * {@code appendStartIndex} and {@code appends} describe the optional
   * explicit tail additions. They are persisted after the same close barrier
   * as the sparse replacements. Any delta whose structural effect cannot be
   * proven to preserve the original indexes is rejected by classification and
   * handled by the general base-aware snapshot planner.
   *
   * @author Eike Stepper
   */
  private final class BranchingSparseSetPlan
  {
    private final ListDeltaWork work;

    private final Map<Integer, Object> values;

    private final int appendStartIndex;

    private final List<Object> appends;

    private BranchingSparseSetPlan(ListDeltaWork work, Map<Integer, Object> values, int appendStartIndex, List<Object> appends)
    {
      this.work = work;
      this.values = values;
      this.appendStartIndex = appendStartIndex;
      this.appends = appends;
    }

    public CDOID getID()
    {
      return work.getID();
    }

    public int getBranchID()
    {
      return work.getBranchId();
    }

    public int getNewVersion()
    {
      return work.getNewVersion();
    }

    public Map<Integer, Object> getValues()
    {
      return values;
    }

    public int getAppendStartIndex()
    {
      return appendStartIndex;
    }

    public List<Object> getAppends()
    {
      return appends;
    }
  }

  /**
   * Owns the commit-wide DML statements used by branching list delta plans.
   * <p>
   * The batch accepts four logically different producers: complete snapshots,
   * append-only plans, explicit suffix rewrites, and sparse stable-index
   * replacements with optional appends. They share homogeneous SQL statements
   * across all independent {@link ListDeltaWork} items in the commit; the
   * plan type is used only to select the rows and counters, never to infer
   * element identity from payload equality.
   * </p>
   * The close/update statements form a first phase. {@link #flushClearPhase()}
   * is an explicit barrier before the shared insert statement is populated,
   * because new-version rows must not be closed by the old-row phase. The
   * final flush checks that the number of inserted rows equals the number
   * planned by all producers. This class does not commit or roll back the
   * surrounding transaction.
   * <p>
   * {@link #release()} returns successfully completed statements to the
   * batching context. {@link #discard()} is used after an incomplete operation
   * and drops all statements so partially bound or partially executed state
   * cannot be reused.
   *
   * @author Eike Stepper
   */
  private final class BranchingDeltaBatch
  {
    private final IDBStoreAccessor accessor;

    private BatchedStatement clearStmt;

    private BatchedStatement insertStmt;

    private BatchedStatement clearSuffixStmt;

    private BatchedStatement clearSparseSetStmt;

    private int snapshotEntryCount;

    private int appendEntryCount;

    private int suffixEntryCount;

    private int sparseSetEntryCount;

    private BranchingDeltaBatch(IDBStoreAccessor accessor)
    {
      this.accessor = accessor;
    }

    public void clearLocalEntries(BranchingDeltaPlan plan)
    {
      try
      {
        if (clearStmt == null)
        {
          clearStmt = accessor.getBatchingContext().createStatement(sqlClearList, ReuseProbability.HIGH, "BranchingList.clear"); //$NON-NLS-1$
        }

        int column = 1;
        clearStmt.setInt(column++, plan.getNewVersion());
        getMappingStrategy().getStore().getIDHandler().setCDOID(clearStmt, column++, plan.getID());
        clearStmt.setInt(column, plan.getBranchID());
        clearStmt.executeUpdate();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void addSnapshot(BranchingDeltaPlan plan)
    {
      try
      {
        if (insertStmt == null)
        {
          insertStmt = accessor.getBatchingContext().createStatement(sqlInsertEntry, ReuseProbability.HIGH, "BranchingList.deltaInsert"); //$NON-NLS-1$
        }

        IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();

        for (int index = 0; index < plan.size(); index++)
        {
          setEntryValues(idHandler, insertStmt, plan.getID(), plan.getBranchID(), plan.getNewVersion(), index, plan.getValue(index), null);
          insertStmt.executeUpdate();
          snapshotEntryCount++;
        }
      }
      catch (SQLException | IllegalStateException ex)
      {
        throw new DBException(ex);
      }
    }

    public void clearLocalSuffix(BranchingSuffixPlan plan)
    {
      try
      {
        if (clearSuffixStmt == null)
        {
          clearSuffixStmt = accessor.getBatchingContext().createStatement(sqlClearSuffix, ReuseProbability.HIGH, "BranchingList.clearSuffix"); //$NON-NLS-1$
        }

        int column = 1;
        clearSuffixStmt.setInt(column++, plan.getNewVersion());
        getMappingStrategy().getStore().getIDHandler().setCDOID(clearSuffixStmt, column++, plan.getID());
        clearSuffixStmt.setInt(column++, plan.getBranchID());
        clearSuffixStmt.setInt(column, plan.getStartIndex());
        clearSuffixStmt.executeUpdate();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void clearSparseSetEntries(BranchingSparseSetPlan plan)
    {
      try
      {
        if (clearSparseSetStmt == null)
        {
          clearSparseSetStmt = accessor.getBatchingContext().createStatement(sqlRemoveEntry, ReuseProbability.HIGH, "BranchingList.clearSparseSet"); //$NON-NLS-1$
        }

        IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
        for (Integer index : plan.getValues().keySet())
        {
          int column = 1;
          clearSparseSetStmt.setInt(column++, plan.getNewVersion());
          idHandler.setCDOID(clearSparseSetStmt, column++, plan.getID());
          clearSparseSetStmt.setInt(column++, plan.getBranchID());
          clearSparseSetStmt.setInt(column, index);
          clearSparseSetStmt.executeUpdate();
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void addAppends(BranchingAppendPlan plan)
    {
      try
      {
        if (insertStmt == null)
        {
          insertStmt = accessor.getBatchingContext().createStatement(sqlInsertEntry, ReuseProbability.HIGH, "BranchingList.deltaInsert"); //$NON-NLS-1$
        }

        IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
        int index = plan.getStartIndex();
        for (Object value : plan.getValues())
        {
          setEntryValues(idHandler, insertStmt, plan.getID(), plan.getBranchID(), plan.getNewVersion(), index++, value, null);
          insertStmt.executeUpdate();
          appendEntryCount++;
        }
      }
      catch (SQLException | IllegalStateException ex)
      {
        throw new DBException(ex);
      }
    }

    public void addSuffix(BranchingSuffixPlan plan)
    {
      try
      {
        IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
        int index = plan.getStartIndex();
        for (Object value : plan.getValues())
        {
          addEntry(idHandler, plan.getID(), plan.getBranchID(), plan.getNewVersion(), index++, value);
          ++suffixEntryCount;
        }
      }
      catch (SQLException | IllegalStateException ex)
      {
        throw new DBException(ex);
      }
    }

    public void addSparseSetValues(BranchingSparseSetPlan plan)
    {
      try
      {
        IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
        for (Map.Entry<Integer, Object> entry : plan.getValues().entrySet())
        {
          addEntry(idHandler, plan.getID(), plan.getBranchID(), plan.getNewVersion(), entry.getKey(), entry.getValue());
          ++sparseSetEntryCount;
        }

        int index = plan.getAppendStartIndex();
        for (Object value : plan.getAppends())
        {
          addEntry(idHandler, plan.getID(), plan.getBranchID(), plan.getNewVersion(), index++, value);
          ++appendEntryCount;
        }
      }
      catch (SQLException | IllegalStateException ex)
      {
        throw new DBException(ex);
      }
    }

    public int getAppendEntryCount()
    {
      return appendEntryCount;
    }

    public int getSuffixEntryCount()
    {
      return suffixEntryCount;
    }

    public int getSparseSetEntryCount()
    {
      return sparseSetEntryCount;
    }

    public void flushClearPhase()
    {
      accessor.getBatchingContext().flushPhase();
    }

    public void flushPhase()
    {
      accessor.getBatchingContext().flushPhase();
      validateExactlyOne(insertStmt, snapshotEntryCount + appendEntryCount + suffixEntryCount + sparseSetEntryCount);
      accessor.getBatchingContext().recordDiagnosticCounter("BranchingList.plannerSnapshots", snapshotEntryCount); //$NON-NLS-1$
    }

    public void release()
    {
      release(clearStmt);
      release(clearSuffixStmt);
      release(clearSparseSetStmt);
      release(insertStmt);
    }

    public void discard()
    {
      discard(clearStmt);
      discard(clearSuffixStmt);
      discard(clearSparseSetStmt);
      discard(insertStmt);
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

    private void addEntry(IIDHandler idHandler, CDOID id, int branchID, int newVersion, int index, Object value) throws SQLException
    {
      if (insertStmt == null)
      {
        insertStmt = accessor.getBatchingContext().createStatement(sqlInsertEntry, ReuseProbability.HIGH, "BranchingList.deltaInsert"); //$NON-NLS-1$
      }

      setEntryValues(idHandler, insertStmt, id, branchID, newVersion, index, value, null);
      insertStmt.executeUpdate();
    }
  }

  /**
   * @author Stefan Winkler
   * @author Andras Peteri
   */
  private class ListDeltaWriter extends AbstractRangeListDeltaWriter
  {
    private int branchID;

    public ListDeltaWriter(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int targetBranchID, int oldVersion, int newVersion)
    {
      super(accessor, originalRevision, oldVersion, newVersion, TRACER);
      branchID = targetBranchID;
    }

    @Override
    protected int getOldListSize(InternalCDORevision originalRevision)
    {
      return originalRevision.size(getFeature());
    }

    @Override
    protected Object getValue(int index)
    {
      return BranchingListTableMappingWithRanges.this.getValue(accessor, id, branchID, index, true);
    }

    @Override
    protected void removeEntry(int index)
    {
      BranchingListTableMappingWithRanges.this.removeEntry(accessor, id, branchID, oldVersion, newVersion, index);
    }

    @Override
    protected void addEntry(int index, Object value)
    {
      BranchingListTableMappingWithRanges.this.addEntry(accessor, id, branchID, newVersion, index, value);
    }

    @Override
    protected void clearList()
    {
      BranchingListTableMappingWithRanges.this.clearList(accessor, id, branchID, oldVersion, newVersion, getLastListIndex());
    }

    @Override
    protected void moveOneUp(int startIndex, int endIndex)
    {
      moveOneUp(oldVersion, newVersion, startIndex, endIndex);
    }

    @Override
    protected void moveOneDown(int startIndex, int endIndex)
    {
      moveOneDown(oldVersion, newVersion, startIndex, endIndex);
    }

    private void moveOneUp(int oldVersion, int newVersion, int startIndex, int endIndex)
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlUpdateIndex, ReuseProbability.HIGH);

      try
      {
        for (int index = startIndex; index <= endIndex; ++index)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("moveOneUp moving: {0} -> {1}", index, index - 1); //$NON-NLS-1$
          }

          int column = 1;
          stmt.setInt(column++, index - 1);
          idHandler.setCDOID(stmt, column++, id);
          stmt.setInt(column++, branchID);
          stmt.setInt(column++, newVersion);
          stmt.setInt(column++, index);

          int result = DBUtil.update(stmt, false);
          switch (result)
          {
          case 1:
            // entry for current revision was already present.
            // index update succeeded.
            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp updated: {0} -> {1}", index, index - 1); //$NON-NLS-1$
            }

            break;
          // no entry for current revision there.
          case 0:
            Object value = BranchingListTableMappingWithRanges.this.getValue(accessor, id, branchID, index, false);

            if (value != null)
            {
              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneUp remove: {0}", index); //$NON-NLS-1$
              }

              BranchingListTableMappingWithRanges.this.removeEntry(accessor, id, branchID, oldVersion, newVersion, index);
            }
            else
            {
              value = getValueFromBase(accessor, id, branchID, index);

              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneUp add historic entry at: {0}", index); //$NON-NLS-1$
              }

              addHistoricEntry(accessor, id, branchID, 0, newVersion, index, value);
            }

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp add: {0}", index - 1); //$NON-NLS-1$
            }

            BranchingListTableMappingWithRanges.this.addEntry(accessor, id, branchID, newVersion, index - 1, value);
            break;
          default:
            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneUp Too many results: {0} -> {1}: {2}", index, index + 1, result); //$NON-NLS-1$
            }

            throw new DBException("Too many results"); //$NON-NLS-1$
          }
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
    }

    private void moveOneDown(int oldVersion, int newVersion, int startIndex, int endIndex)
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlUpdateIndex, ReuseProbability.HIGH);

      try
      {
        for (int index = endIndex; index >= startIndex; --index)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("moveOneDown moving: {0} -> {1}", index, index + 1); //$NON-NLS-1$
          }

          int column = 1;
          stmt.setInt(column++, index + 1);
          idHandler.setCDOID(stmt, column++, id);
          stmt.setInt(column++, branchID);
          stmt.setInt(column++, newVersion);
          stmt.setInt(column++, index);

          int result = DBUtil.update(stmt, false);
          switch (result)
          {
          case 1:
            // entry for current revision was already present.
            // index update succeeded.

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneDown updated: {0} -> {1}", index, index + 1); //$NON-NLS-1$
            }

            break;
          case 0:
            Object value = BranchingListTableMappingWithRanges.this.getValue(accessor, id, branchID, index, false);

            if (value != null)
            {
              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneDown remove: {0}", index); //$NON-NLS-1$
              }

              BranchingListTableMappingWithRanges.this.removeEntry(accessor, id, branchID, oldVersion, newVersion, index);
            }
            else
            {
              value = getValueFromBase(accessor, id, branchID, index);

              if (TRACER.isEnabled())
              {
                TRACER.format("moveOneDown add historic entry at: {0}", index); //$NON-NLS-1$
              }

              addHistoricEntry(accessor, id, branchID, 0, newVersion, index, value);
            }

            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneDown add: {0}", index + 1); //$NON-NLS-1$
            }

            BranchingListTableMappingWithRanges.this.addEntry(accessor, id, branchID, newVersion, index + 1, value);
            break;
          default:
            if (TRACER.isEnabled())
            {
              TRACER.format("moveOneDown Too many results: {0} -> {1}: {2}", index, index + 1, result); //$NON-NLS-1$
            }

            throw new DBException("Too many results"); //$NON-NLS-1$
          }
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
    }
  }
}
