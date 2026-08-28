/*
 * Copyright (c) 2009-2016, 2018, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.DBStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractBasicListTableMapping.AbstractListDeltaWriter.NewListSizeResult;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This is a list-to-table mapping optimized for non-audit-mode. It doesn't care about version and has delta support.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class NonAuditListTableMapping extends AbstractListTableMapping implements IListMappingDeltaSupport, IListMappingBatchingSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, NonAuditListTableMapping.class);

  private String sqlClear;

  private String sqlUpdateValue;

  private String sqlUpdateIndex;

  private String sqlInsertValue;

  private String sqlDeleteItem;

  private String sqlShiftDownIndex;

  private String sqlShiftDownFinalIndex;

  private String sqlReadCurrentIndexOffset;

  private String sqlShiftUpIndex;

  private String sqlShiftUpFinalIndex;

  public NonAuditListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
  }

  @Override
  protected void initSQLStrings()
  {
    super.initSQLStrings();

    IDBTable table = getTable();

    // ----------- clear list -------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=?"); //$NON-NLS-1$
    sqlClear = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=?"); //$NON-NLS-1$
    sqlDeleteItem = builder.toString();

    // ----------- update one item --------------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateValue = builder.toString();

    // ----------- insert one item --------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" ("); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(valueField);
    builder.append(") VALUES(?, ?, ?)"); //$NON-NLS-1$
    sqlInsertValue = builder.toString();

    // ----------- update one item index --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("=?"); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------- mass update item indexes --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("="); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("+? WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(" BETWEEN ? AND ?"); //$NON-NLS-1$

    // Move the affected indexes to a disjoint temporary range first. This avoids transient unique-key
    // violations on databases that don't support ordered UPDATEs (for example PostgreSQL).
    sqlShiftDownIndex = builder.toString();
    sqlShiftUpIndex = sqlShiftDownIndex;

    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("="); //$NON-NLS-1$
    builder.append(indexField);
    builder.append("-?+? WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(" BETWEEN ? AND ?"); //$NON-NLS-1$
    sqlShiftDownFinalIndex = builder.toString();
    sqlShiftUpFinalIndex = sqlShiftDownFinalIndex;

    // ----------- read current index offset --------------
    builder = new StringBuilder();
    builder.append("SELECT MIN("); //$NON-NLS-1$
    builder.append(indexField);
    builder.append(") FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=?"); //$NON-NLS-1$
    sqlReadCurrentIndexOffset = builder.toString();
  }

  @Override
  public void addSimpleChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int index)
  {
    int offset = getCurrentIndexOffset(accessor, cdoid);
    super.addSimpleChunkWhere(accessor, cdoid, builder, index + offset);
  }

  @Override
  public void addRangedChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int fromIndex, int toIndex)
  {
    int offset = getCurrentIndexOffset(accessor, cdoid);
    super.addRangedChunkWhere(accessor, cdoid, builder, fromIndex + offset, toIndex + offset);
  }

  @Override
  protected void addKeyFields(List<FieldInfo> list)
  {
    // Do nothing.
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    idHandler.setCDOID(stmt, 1, revision.getID());
  }

  @Override
  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    clearList(accessor, id);
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    clearList(accessor, id);
  }

  @Override
  public void processDelta(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion, long created, CDOListFeatureDelta delta)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    List<CDOFeatureDelta> listChanges = delta.getListChanges();
    int oldListSize = delta.getOriginSize();

    if (TRACER.isEnabled())
    {
      TRACER.format("ListTableMapping.processDelta for object {0} - original list size: {1}", id, //$NON-NLS-1$
          oldListSize);
    }

    ListDeltaWriter writer = new ListDeltaWriter(accessor, id, listChanges, oldListSize);
    writer.writeListDeltas();
  }

  /**
   * Plans a list delta without materializing physical indexes or executing JDBC statements.
   */
  public int planDelta(IDBStoreAccessor accessor, CDOID id, CDOListFeatureDelta delta)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    ListDeltaWriter writer = new ListDeltaWriter(accessor, id, delta.getListChanges(), delta.getOriginSize());
    return writer.planListDeltas();
  }

  @Override
  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean raw, OMMonitor monitor)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    IBatchingContext batchingContext = accessor.getBatchingContext();
    BatchedStatement stmt = batchingContext.createStatement(sqlInsertValue, ReuseProbability.HIGH, "NonAuditList.insert"); //$NON-NLS-1$

    monitor.begin(revisions.length);
    boolean complete = false;
    int entryCount = 0;

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
            getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, revision.getID());
            stmt.setInt(column++, index++);
            getTypeMapping().setValue(stmt, column, value);
            stmt.executeUpdate();
            ++entryCount;
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
    finally
    {
      if (complete)
      {
        batchingContext.flushPhase();
        validateExactlyOne(stmt, entryCount, "Unexpected list insert result"); //$NON-NLS-1$
        batchingContext.releaseStatement(stmt);
      }
      else
      {
        batchingContext.discardStatement(stmt);
      }

      monitor.done();
    }
  }

  private static void validateExactlyOne(BatchedStatement stmt, int expectedCount, String message)
  {
    int knownResult = stmt.getTotalResult();
    int unknownResultCount = stmt.getUnknownResultCount();
    if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
    {
      throw new DBException(message);
    }
  }

  @Override
  public void processDeltas(IDBStoreAccessor accessor, ListDeltaWork[] work, OMMonitor monitor)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    if (!hasUniqueSources(accessor, work))
    {
      processDeltasSequentially(accessor, work, monitor);
      return;
    }

    NonAuditDeltaBatch batch = new NonAuditDeltaBatch(accessor);
    monitor.begin(work.length);
    try
    {
      List<ListDeltaWriter> writers = new ArrayList<>(work.length);
      for (ListDeltaWork item : work)
      {
        ListDeltaWriter writer = new ListDeltaWriter(accessor, item.getID(), item.getDelta().getListChanges(), item.getDelta().getOriginSize(), batch);
        int newListSize = writer.planListDeltas();
        if (item.getNewListSize() != ListDeltaWork.UNSPECIFIED_LIST_SIZE && item.getNewListSize() != newListSize)
        {
          throw new DBException("Inconsistent planned list size"); //$NON-NLS-1$
        }

        writers.add(writer);
      }

      for (ListDeltaWriter writer : writers)
      {
        writer.prepareResultToDatabase();
      }

      // Temporary moves and deletes must be visible before a shift of the same list. The batch contains only this
      // mapping's homogeneous statements, so independent source IDs are flushed together.
      batch.flushForIndexShift();

      int maximumShiftCount = 0;
      for (ListDeltaWriter writer : writers)
      {
        maximumShiftCount = Math.max(maximumShiftCount, writer.getPlannedShiftOperations().size());
        if (writer.getPlannedShiftOperations().size() > 1)
        {
          batch.recordDiagnosticCounter("NonAuditList.sameListDependencyFallback"); //$NON-NLS-1$
        }
      }

      // One round contains at most one shift per list. This preserves same-list ordering while batching each phase
      // across all independent source IDs.
      for (int shiftIndex = 0; shiftIndex < maximumShiftCount; shiftIndex++)
      {
        for (ListDeltaWriter writer : writers)
        {
          List<AbstractListDeltaWriter.Shift> shifts = writer.getPlannedShiftOperations();
          if (shiftIndex < shifts.size())
          {
            writer.writeShiftTemporary(shifts.get(shiftIndex));
          }
        }

        batch.flushShiftTemporary();

        for (ListDeltaWriter writer : writers)
        {
          List<AbstractListDeltaWriter.Shift> shifts = writer.getPlannedShiftOperations();
          if (shiftIndex < shifts.size())
          {
            writer.writeShiftFinal(shifts.get(shiftIndex));
          }
        }

        batch.flushShiftFinal();
      }

      // A clear may precede added elements of the same list, but does not constrain independent shift work.
      batch.flushClearList();

      for (ListDeltaWriter writer : writers)
      {
        writer.finishResultToDatabase();
        monitor.worked();
      }

      batch.flushPhase();
    }
    catch (SQLException ex)
    {
      batch.discard();
      throw new DBException(ex);
    }
    catch (RuntimeException ex)
    {
      batch.discard();
      throw ex;
    }
    finally
    {
      batch.release();
      monitor.done();
    }
  }

  private boolean hasUniqueSources(IDBStoreAccessor accessor, ListDeltaWork[] work)
  {
    Set<CDOID> ids = new HashSet<>();
    for (ListDeltaWork item : work)
    {
      if (!ids.add(item.getID()))
      {
        ((DBStoreAccessor)accessor).getBatchingContext().recordDiagnosticCounter("NonAuditList.identicalSourceIDFallback"); //$NON-NLS-1$
        return false;
      }
    }

    return true;
  }

  private void processDeltasSequentially(IDBStoreAccessor accessor, ListDeltaWork[] work, OMMonitor monitor)
  {
    NonAuditDeltaBatch batch = new NonAuditDeltaBatch(accessor);
    monitor.begin(work.length);
    try
    {
      for (ListDeltaWork item : work)
      {
        ListDeltaWriter writer = new ListDeltaWriter(accessor, item.getID(), item.getDelta().getListChanges(), item.getDelta().getOriginSize(), batch);
        try
        {
          writer.writeListDeltas();
        }
        catch (NewListSizeResult expected)
        {
          if (item.getNewListSize() != ListDeltaWork.UNSPECIFIED_LIST_SIZE && item.getNewListSize() != expected.getNewListSize())
          {
            throw new DBException("Inconsistent planned list size"); //$NON-NLS-1$
          }
        }

        monitor.worked();
      }

      batch.flushPhase();
    }
    catch (RuntimeException ex)
    {
      batch.discard();
      throw ex;
    }
    finally
    {
      batch.release();
      monitor.done();
    }
  }

  /**
   * Clear a list of a given revision.
   *
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   */
  private void clearList(IDBStoreAccessor accessor, CDOID id)
  {
    if (getTable() == null)
    {
      return;
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlClear, ReuseProbability.HIGH);

    try
    {
      idHandler.setCDOID(stmt, 1, id);
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

  private int getCurrentIndexOffset(IDBStoreAccessor accessor, CDOID id)
  {
    if (getTable() == null)
    {
      // List is empty. Return the default offset of 0.
      return 0;
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlReadCurrentIndexOffset, ReuseProbability.HIGH);
    ResultSet rset = null;

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      rset = stmt.executeQuery();
      if (!rset.next())
      {
        // List is empty. Return the default offset of 0.
        return 0;
      }

      // Return the minimum index which is equal to the current offset.
      return rset.getInt(1);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(rset);
      DBUtil.close(stmt);
    }
  }

  private final class NonAuditDeltaBatch
  {
    private final IDBStoreAccessor accessor;

    private BatchedStatement deleteStmt;

    private BatchedStatement moveStmt;

    private BatchedStatement setStmt;

    private BatchedStatement insertStmt;

    private BatchedStatement clearStmt;

    private BatchedStatement shiftTemporaryStmt;

    private BatchedStatement shiftFinalStmt;

    private int deleteCount;

    private int moveCount;

    private int setCount;

    private int insertCount;

    private boolean discarded;

    private NonAuditDeltaBatch(IDBStoreAccessor accessor)
    {
      this.accessor = accessor;
    }

    public void delete(CDOID id, int index)
    {
      try
      {
        BatchedStatement stmt = getDeleteStmt();
        int column = 1;
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column, index);
        add(stmt);
        ++deleteCount;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void move(CDOID id, int sourceIndex, int targetIndex)
    {
      try
      {
        BatchedStatement stmt = getMoveStmt();
        int column = 1;
        stmt.setInt(column++, targetIndex);
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column, sourceIndex);
        add(stmt);
        ++moveCount;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void set(CDOID id, int index, Object value)
    {
      try
      {
        BatchedStatement stmt = getSetStmt();
        int column = 1;
        getTypeMapping().setValue(stmt, column++, value);
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column, index);
        add(stmt);
        ++setCount;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void insert(CDOID id, int index, Object value)
    {
      try
      {
        BatchedStatement stmt = getInsertStmt();
        int column = 1;
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column++, index);
        getTypeMapping().setValue(stmt, column, value);
        add(stmt);
        ++insertCount;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void clearList(CDOID id)
    {
      try
      {
        BatchedStatement stmt = getClearStmt();
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, id);
        add(stmt);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void flushClearList()
    {
      flush(clearStmt);
    }

    public void flushForIndexShift()
    {
      flush(deleteStmt);
      flush(moveStmt);
      validateExactlyOne(deleteStmt, deleteCount, "Unexpected delete result"); //$NON-NLS-1$
      validateExactlyOne(moveStmt, moveCount, "Unexpected move result"); //$NON-NLS-1$
    }

    public void shiftTemporary(CDOID id, int temporaryIndexOffset, int startIndex, int endIndex)
    {
      try
      {
        BatchedStatement stmt = getShiftTemporaryStmt();
        int column = 1;
        stmt.setInt(column++, temporaryIndexOffset);
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column++, startIndex);
        stmt.setInt(column, endIndex);
        add(stmt);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void shiftFinal(CDOID id, int temporaryIndexOffset, int shiftOffset, int temporaryStartIndex, int temporaryEndIndex)
    {
      try
      {
        BatchedStatement stmt = getShiftFinalStmt();
        int column = 1;
        stmt.setInt(column++, temporaryIndexOffset);
        stmt.setInt(column++, shiftOffset);
        getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, column++, id);
        stmt.setInt(column++, temporaryStartIndex);
        stmt.setInt(column, temporaryEndIndex);
        add(stmt);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    public void flushShiftTemporary()
    {
      flush(shiftTemporaryStmt);
    }

    public void flushShiftFinal()
    {
      flush(shiftFinalStmt);
    }

    public void recordDiagnosticCounter(String name)
    {
      accessor.getBatchingContext().recordDiagnosticCounter(name);
    }

    public void flushPhase()
    {
      accessor.getBatchingContext().flushPhase();
      validateExactlyOne(deleteStmt, deleteCount, "Unexpected delete result"); //$NON-NLS-1$
      validateExactlyOne(moveStmt, moveCount, "Unexpected move result"); //$NON-NLS-1$
      validateExactlyOne(setStmt, setCount, "Unexpected set result"); //$NON-NLS-1$
      validateExactlyOne(insertStmt, insertCount, "Unexpected insert result"); //$NON-NLS-1$
    }

    public void release()
    {
      if (!discarded)
      {
        release(deleteStmt);
        release(moveStmt);
        release(setStmt);
        release(insertStmt);
        release(clearStmt);
        release(shiftTemporaryStmt);
        release(shiftFinalStmt);
      }
    }

    public void discard()
    {
      if (!discarded)
      {
        discarded = true;
        discard(deleteStmt);
        discard(moveStmt);
        discard(setStmt);
        discard(insertStmt);
        discard(clearStmt);
        discard(shiftTemporaryStmt);
        discard(shiftFinalStmt);
      }
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

    private void validateExactlyOne(BatchedStatement stmt, int expectedCount, String message)
    {
      if (stmt == null)
      {
        if (expectedCount != 0)
        {
          throw new DBException(message);
        }

        return;
      }

      int knownResult = stmt.getTotalResult();
      int unknownResultCount = stmt.getUnknownResultCount();
      if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
      {
        throw new DBException(message);
      }
    }

    private BatchedStatement getDeleteStmt()
    {
      if (deleteStmt == null)
      {
        deleteStmt = createStatement(sqlDeleteItem, "NonAuditList.delete"); //$NON-NLS-1$
      }

      return deleteStmt;
    }

    private BatchedStatement getMoveStmt()
    {
      if (moveStmt == null)
      {
        moveStmt = createStatement(sqlUpdateIndex, "NonAuditList.move"); //$NON-NLS-1$
      }

      return moveStmt;
    }

    private BatchedStatement getSetStmt()
    {
      if (setStmt == null)
      {
        setStmt = createStatement(sqlUpdateValue, "NonAuditList.set"); //$NON-NLS-1$
      }

      return setStmt;
    }

    private BatchedStatement getInsertStmt()
    {
      if (insertStmt == null)
      {
        insertStmt = createStatement(sqlInsertValue, "NonAuditList.insert"); //$NON-NLS-1$
      }

      return insertStmt;
    }

    private BatchedStatement getClearStmt()
    {
      if (clearStmt == null)
      {
        clearStmt = createStatement(sqlClear, "NonAuditList.clear"); //$NON-NLS-1$
      }

      return clearStmt;
    }

    private BatchedStatement getShiftTemporaryStmt()
    {
      if (shiftTemporaryStmt == null)
      {
        shiftTemporaryStmt = createStatement(sqlShiftDownIndex, "NonAuditList.shiftTemporary"); //$NON-NLS-1$
      }

      return shiftTemporaryStmt;
    }

    private BatchedStatement getShiftFinalStmt()
    {
      if (shiftFinalStmt == null)
      {
        shiftFinalStmt = createStatement(sqlShiftDownFinalIndex, "NonAuditList.shiftFinal"); //$NON-NLS-1$
      }

      return shiftFinalStmt;
    }

    private BatchedStatement createStatement(String sql, String diagnosticName)
    {
      IBatchingContext batchingContext = accessor.getBatchingContext();
      return batchingContext.createStatement(sql, ReuseProbability.HIGH, diagnosticName);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ListDeltaWriter extends AbstractListDeltaWriter
  {
    private final NonAuditDeltaBatch batch;

    private IDBPreparedStatement stmtDelete;

    private IDBPreparedStatement stmtMove;

    private IDBPreparedStatement stmtSet;

    private IDBPreparedStatement stmtInsert;

    private IDBPreparedStatement stmtShiftDown;

    private IDBPreparedStatement stmtShiftDownFinal;

    private IDBPreparedStatement stmtShiftUp;

    private IDBPreparedStatement stmtShiftUpFinal;

    private List<Shift> plannedShiftOperations;

    private int deleteCount;

    private int moveCount;

    private int setCount;

    private int insertCount;

    public ListDeltaWriter(IDBStoreAccessor accessor, CDOID id, List<CDOFeatureDelta> listChanges, int oldListSize)
    {
      this(accessor, id, listChanges, oldListSize, null);
    }

    public ListDeltaWriter(IDBStoreAccessor accessor, CDOID id, List<CDOFeatureDelta> listChanges, int oldListSize, NonAuditDeltaBatch batch)
    {
      super(accessor, id, listChanges, oldListSize);
      this.batch = batch;
    }

    @Override
    protected boolean isZeroBasedIndex()
    {
      return ((HorizontalNonAuditMappingStrategy)getMappingStrategy()).shallForceZeroBasedIndex();
    }

    @Override
    protected ITypeMapping getTypeMapping()
    {
      return NonAuditListTableMapping.this.getTypeMapping();
    }

    @Override
    protected int getCurrentIndexOffset()
    {
      return NonAuditListTableMapping.this.getCurrentIndexOffset(accessor, id);
    }

    @Override
    protected void clearList()
    {
      if (batch == null)
      {
        NonAuditListTableMapping.this.clearList(accessor, id);
      }
      else
      {
        batch.clearList(id);
      }
    }

    @Override
    protected void finishResultToDatabase() throws SQLException
    {
      if (batch != null)
      {
        batch.flushClearList();
      }

      super.finishResultToDatabase();
    }

    @Override
    protected void writeResultToDatabase() throws SQLException
    {
      if (batch != null)
      {
        super.writeResultToDatabase();
        return;
      }

      try
      {
        super.writeResultToDatabase();

        if (moveCount > 0)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Performing {0} move operations", moveCount); //$NON-NLS-1$
          }

          DBUtil.executeBatch(stmtMove, moveCount);
        }

        if (insertCount > 0)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Performing {0} insert operations", insertCount); //$NON-NLS-1$
          }

          DBUtil.executeBatch(stmtInsert, insertCount);
        }

        if (setCount > 0)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Performing {0} set operations", setCount); //$NON-NLS-1$
          }

          DBUtil.executeBatch(stmtSet, setCount);
        }
      }
      finally
      {
        close(stmtDelete, stmtMove, stmtInsert, stmtSet);
      }
    }

    @Override
    protected void writeShifts(IIDHandler idHandler) throws SQLException
    {
      if (batch != null)
      {
        batch.flushForIndexShift();
        super.writeShifts(idHandler);
        return;
      }

      if (deleteCount > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} delete operations", deleteCount); //$NON-NLS-1$
        }

        DBUtil.executeBatch(stmtDelete, deleteCount);
      }

      if (moveCount > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} move operations", moveCount); //$NON-NLS-1$
        }

        DBUtil.executeBatch(stmtMove, moveCount);
        moveCount = 0;
      }

      super.writeShifts(idHandler);
    }

    @Override
    protected void writeShiftsDown(IIDHandler idHandler, ListIterator<Shift> operationIt) throws SQLException
    {
      try
      {
        super.writeShiftsDown(idHandler, operationIt);
      }
      finally
      {
        close(stmtShiftDown, stmtShiftDownFinal);
      }
    }

    @Override
    protected void writeShiftsUp(IIDHandler idHandler, ListIterator<Shift> operationIt) throws SQLException
    {
      try
      {
        super.writeShiftsUp(idHandler, operationIt);
      }
      finally
      {
        close(stmtShiftUp, stmtShiftUpFinal);
      }
    }

    @Override
    protected void dbDelete(IIDHandler idHandler, int index) throws SQLException
    {
      if (batch != null)
      {
        batch.delete(id, index);
        return;
      }

      if (stmtDelete == null)
      {
        stmtDelete = accessor.getDBConnection().prepareStatement(sqlDeleteItem, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtDelete, 1, id);
      }

      stmtDelete.setInt(2, index);
      stmtDelete.addBatch();
      ++deleteCount;
    }

    @Override
    protected void dbMove(IIDHandler idHandler, int sourcePhysicalIndex, int targetPhysicalIndex, int sourceIndex) throws SQLException
    {
      if (batch != null)
      {
        batch.move(id, sourcePhysicalIndex, targetPhysicalIndex);
        return;
      }

      if (stmtMove == null)
      {
        stmtMove = accessor.getDBConnection().prepareStatement(sqlUpdateIndex, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtMove, 2, id);
      }

      stmtMove.setInt(3, sourcePhysicalIndex);
      stmtMove.setInt(1, targetPhysicalIndex);
      stmtMove.addBatch();
      ++moveCount;
    }

    @Override
    protected void dbSet(IIDHandler idHandler, ITypeMapping typeMapping, int targetPhysicalIndex, Object value, int sourceIndex) throws SQLException
    {
      if (batch != null)
      {
        batch.set(id, targetPhysicalIndex, value);
        return;
      }

      if (stmtSet == null)
      {
        stmtSet = accessor.getDBConnection().prepareStatement(sqlUpdateValue, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtSet, 2, id);
      }

      stmtSet.setInt(3, targetPhysicalIndex);
      typeMapping.setValue(stmtSet, 1, value);
      stmtSet.addBatch();
      ++setCount;
    }

    @Override
    protected void dbInsert(IIDHandler idHandler, ITypeMapping typeMapping, int index, Object value) throws SQLException
    {
      if (batch != null)
      {
        batch.insert(id, index, value);
        return;
      }

      if (stmtInsert == null)
      {
        stmtInsert = accessor.getDBConnection().prepareStatement(sqlInsertValue, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtInsert, 1, id);
      }

      stmtInsert.setInt(2, index);
      typeMapping.setValue(stmtInsert, 3, value);
      stmtInsert.addBatch();
      ++insertCount;
    }

    private List<Shift> getPlannedShiftOperations()
    {
      if (plannedShiftOperations == null)
      {
        plannedShiftOperations = getShiftOperations();
      }

      return plannedShiftOperations;
    }

    private void writeShiftTemporary(Shift shift)
    {
      int temporaryIndexOffset = getTemporaryOffset(shift.startIndex, shift.endIndex);
      batch.shiftTemporary(id, temporaryIndexOffset, shift.startIndex, shift.endIndex);
    }

    private void writeShiftFinal(Shift shift)
    {
      int temporaryIndexOffset = getTemporaryOffset(shift.startIndex, shift.endIndex);
      int temporaryStartIndex = (int)((long)shift.startIndex + temporaryIndexOffset);
      int temporaryEndIndex = (int)((long)shift.endIndex + temporaryIndexOffset);
      batch.shiftFinal(id, temporaryIndexOffset, shift.offset, temporaryStartIndex, temporaryEndIndex);
    }

    @Override
    protected void dbShiftDown(IIDHandler idHandler, int shiftOffset, int shiftStartPhysicalIndex, int shiftEndPhysicalIndex) throws SQLException
    {
      if (stmtShiftDown == null)
      {
        stmtShiftDown = accessor.getDBConnection().prepareStatement(sqlShiftDownIndex, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtShiftDown, 2, id);

        stmtShiftDownFinal = accessor.getDBConnection().prepareStatement(sqlShiftDownFinalIndex, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtShiftDownFinal, 3, id);
      }

      int temporaryIndexOffset = getTemporaryOffset(shiftStartPhysicalIndex, shiftEndPhysicalIndex);
      stmtShiftDown.setInt(1, temporaryIndexOffset);
      stmtShiftDown.setInt(3, shiftStartPhysicalIndex);
      stmtShiftDown.setInt(4, shiftEndPhysicalIndex);

      stmtShiftDownFinal.setInt(1, temporaryIndexOffset);
      stmtShiftDownFinal.setInt(2, shiftOffset);
      stmtShiftDownFinal.setInt(4, (int)((long)shiftStartPhysicalIndex + temporaryIndexOffset));
      stmtShiftDownFinal.setInt(5, (int)((long)shiftEndPhysicalIndex + temporaryIndexOffset));
      stmtShiftDown.executeUpdate();
      stmtShiftDownFinal.executeUpdate();
    }

    @Override
    protected void dbShiftUp(IIDHandler idHandler, int shiftOffset, int shiftStartPhysicalIndex, int shiftEndPhysicalIndex) throws SQLException
    {
      if (stmtShiftUp == null)
      {
        stmtShiftUp = accessor.getDBConnection().prepareStatement(sqlShiftUpIndex, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtShiftUp, 2, id);

        stmtShiftUpFinal = accessor.getDBConnection().prepareStatement(sqlShiftUpFinalIndex, ReuseProbability.HIGH);
        idHandler.setCDOID(stmtShiftUpFinal, 3, id);
      }

      int temporaryIndexOffset = getTemporaryOffset(shiftStartPhysicalIndex, shiftEndPhysicalIndex);
      stmtShiftUp.setInt(1, temporaryIndexOffset);
      stmtShiftUp.setInt(3, shiftStartPhysicalIndex);
      stmtShiftUp.setInt(4, shiftEndPhysicalIndex);

      stmtShiftUpFinal.setInt(1, temporaryIndexOffset);
      stmtShiftUpFinal.setInt(2, shiftOffset);
      stmtShiftUpFinal.setInt(4, (int)((long)shiftStartPhysicalIndex + temporaryIndexOffset));
      stmtShiftUpFinal.setInt(5, (int)((long)shiftEndPhysicalIndex + temporaryIndexOffset));
      stmtShiftUp.executeUpdate();
      stmtShiftUpFinal.executeUpdate();
    }

    private int getTemporaryOffset(int startIndex, int endIndex)
    {
      long offset = startIndex <= 0 ? (long)Integer.MIN_VALUE - startIndex : (long)Integer.MAX_VALUE - endIndex;
      long temporaryStart = startIndex + offset;
      long temporaryEnd = endIndex + offset;
      if (temporaryStart < Integer.MIN_VALUE || temporaryEnd > Integer.MAX_VALUE)
      {
        throw new IllegalArgumentException("List index range cannot be shifted to a temporary range: " + startIndex + ".." + endIndex);
      }

      return (int)offset;
    }
  }
}
