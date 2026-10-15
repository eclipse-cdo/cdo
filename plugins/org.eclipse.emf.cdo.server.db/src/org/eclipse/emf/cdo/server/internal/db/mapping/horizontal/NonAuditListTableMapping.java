/*
 * Copyright (c) 2009-2016, 2018, 2019, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
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

  @ExcludeFromDump
  private transient SQLStatement sqlClear;

  @ExcludeFromDump
  private transient SQLStatement sqlUpdateValue;

  @ExcludeFromDump
  private transient SQLStatement sqlUpdateIndex;

  @ExcludeFromDump
  private transient SQLStatement sqlInsertValue;

  @ExcludeFromDump
  private transient SQLStatement sqlDeleteItem;

  @ExcludeFromDump
  private transient SQLStatement sqlShiftDownIndex;

  @ExcludeFromDump
  private transient SQLStatement sqlShiftDownFinalIndex;

  @ExcludeFromDump
  private transient SQLStatement sqlReadCurrentIndexOffset;

  @ExcludeFromDump
  private transient SQLStatement sqlShiftUpIndex;

  @ExcludeFromDump
  private transient SQLStatement sqlShiftUpFinalIndex;

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
    sqlClear = SQL.deleteFrom(table) //
        .where(sourceField).eq(SQL.param()) //
        .compile(getDBAdapter());

    sqlDeleteItem = SQL.deleteFrom(table) //
        .where(sourceField).eq(SQL.param()) //
        .and(indexField).eq(SQL.param()) //
        .compile(getDBAdapter());

    sqlUpdateValue = SQL.update(table) //
        .set(valueField).to(SQL.param()) //
        .where(sourceField).eq(SQL.param()) //
        .and(indexField).eq(SQL.param()) //
        .compile(getDBAdapter());

    sqlInsertValue = SQL.insertInto(table) //
        .columns(sourceField, indexField, valueField) //
        .values(SQL.param(), SQL.param(), SQL.param()) //
        .compile(getDBAdapter());

    sqlUpdateIndex = SQL.update(table) //
        .set(indexField).to(SQL.param()) //
        .where(sourceField).eq(SQL.param()) //
        .and(indexField).eq(SQL.param()) //
        .compile(getDBAdapter());

    // Move the affected indexes to a disjoint temporary range first. This avoids transient unique-key
    // violations on databases that don't support ordered UPDATEs (for example PostgreSQL).
    sqlShiftDownIndex = SQL.update(table) //
        .set(indexField).to(SQL.add(indexField, SQL.param())) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.between(indexField, SQL.param(), SQL.param()))) //
        .compile(getDBAdapter());
    sqlShiftUpIndex = sqlShiftDownIndex;

    sqlShiftDownFinalIndex = SQL.update(table) //
        .set(indexField).to(SQL.add(SQL.subtract(indexField, SQL.param()), SQL.param())) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.between(indexField, SQL.param(), SQL.param()))) //
        .compile(getDBAdapter());
    sqlShiftUpFinalIndex = sqlShiftDownFinalIndex;

    sqlReadCurrentIndexOffset = SQL.select(SQL.min(indexField)) //
        .from(table) //
        .where(sourceField).eq(SQL.param()) //
        .compile(getDBAdapter());
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
  protected void bindKeyFields(SQLStatement sql, PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    sql.bindParameter(stmt, 1, revision.getID(), getStore().getIDHandler());
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
            sqlInsertValue.bindParameter(stmt, column++, revision.getID(), getStore().getIDHandler());
            sqlInsertValue.bindParameter(stmt, column++, index++);
            sqlInsertValue.bindParameter(stmt, column, value, getTypeMapping());
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

    IDBPreparedStatement stmt = DBUtil.prepare(sqlClear, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlClear.bindParameter(stmt, 1, id, getStore().getIDHandler());
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

    IDBPreparedStatement stmt = DBUtil.prepare(sqlReadCurrentIndexOffset, accessor.getDBConnection(), ReuseProbability.HIGH);
    ResultSet rset = null;

    try
    {
      sqlReadCurrentIndexOffset.bindParameter(stmt, 1, id, getStore().getIDHandler());
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

  /**
   * @author Eike Stepper
   */
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
        sqlDeleteItem.bindParameter(stmt, column++, id, getStore().getIDHandler());
        sqlDeleteItem.bindParameter(stmt, column, index);
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
        sqlUpdateIndex.bindParameter(stmt, column++, targetIndex);
        sqlUpdateIndex.bindParameter(stmt, column++, id, getStore().getIDHandler());
        sqlUpdateIndex.bindParameter(stmt, column, sourceIndex);
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
        sqlUpdateValue.bindParameter(stmt, column++, value, getTypeMapping());
        sqlUpdateValue.bindParameter(stmt, column++, id, getStore().getIDHandler());
        sqlUpdateValue.bindParameter(stmt, column, index);
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
        sqlInsertValue.bindParameter(stmt, column++, id, getStore().getIDHandler());
        sqlInsertValue.bindParameter(stmt, column++, index);
        sqlInsertValue.bindParameter(stmt, column, value, getTypeMapping());
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
        sqlClear.bindParameter(stmt, 1, id, getStore().getIDHandler());
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
        sqlShiftDownIndex.bindParameter(stmt, column++, temporaryIndexOffset);
        sqlShiftDownIndex.bindParameter(stmt, column++, id, getStore().getIDHandler());
        sqlShiftDownIndex.bindParameter(stmt, column++, startIndex);
        sqlShiftDownIndex.bindParameter(stmt, column, endIndex);
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
        sqlShiftDownFinalIndex.bindParameter(stmt, column++, temporaryIndexOffset);
        sqlShiftDownFinalIndex.bindParameter(stmt, column++, shiftOffset);
        sqlShiftDownFinalIndex.bindParameter(stmt, column++, id, getStore().getIDHandler());
        sqlShiftDownFinalIndex.bindParameter(stmt, column++, temporaryStartIndex);
        sqlShiftDownFinalIndex.bindParameter(stmt, column, temporaryEndIndex);
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

    private BatchedStatement createStatement(SQLStatement sql, String diagnosticName)
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
        stmtDelete = DBUtil.prepare(sqlDeleteItem, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlDeleteItem.bindParameter(stmtDelete, 1, id, idHandler);
      }

      sqlDeleteItem.bindParameter(stmtDelete, 2, index);
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
        stmtMove = DBUtil.prepare(sqlUpdateIndex, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlUpdateIndex.bindParameter(stmtMove, 2, id, idHandler);
      }

      sqlUpdateIndex.bindParameter(stmtMove, 3, sourcePhysicalIndex);
      sqlUpdateIndex.bindParameter(stmtMove, 1, targetPhysicalIndex);
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
        stmtSet = DBUtil.prepare(sqlUpdateValue, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlUpdateValue.bindParameter(stmtSet, 2, id, idHandler);
      }

      sqlUpdateValue.bindParameter(stmtSet, 3, targetPhysicalIndex);
      sqlUpdateValue.bindParameter(stmtSet, 1, value, typeMapping);
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
        stmtInsert = DBUtil.prepare(sqlInsertValue, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlInsertValue.bindParameter(stmtInsert, 1, id, idHandler);
      }

      sqlInsertValue.bindParameter(stmtInsert, 2, index);
      sqlInsertValue.bindParameter(stmtInsert, 3, value, typeMapping);
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
        stmtShiftDown = DBUtil.prepare(sqlShiftDownIndex, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlShiftDownIndex.bindParameter(stmtShiftDown, 2, id, idHandler);

        stmtShiftDownFinal = DBUtil.prepare(sqlShiftDownFinalIndex, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlShiftDownFinalIndex.bindParameter(stmtShiftDownFinal, 3, id, idHandler);
      }

      int temporaryIndexOffset = getTemporaryOffset(shiftStartPhysicalIndex, shiftEndPhysicalIndex);
      sqlShiftDownIndex.bindParameter(stmtShiftDown, 1, temporaryIndexOffset);
      sqlShiftDownIndex.bindParameter(stmtShiftDown, 3, shiftStartPhysicalIndex);
      sqlShiftDownIndex.bindParameter(stmtShiftDown, 4, shiftEndPhysicalIndex);

      sqlShiftDownFinalIndex.bindParameter(stmtShiftDownFinal, 1, temporaryIndexOffset);
      sqlShiftDownFinalIndex.bindParameter(stmtShiftDownFinal, 2, shiftOffset);
      sqlShiftDownFinalIndex.bindParameter(stmtShiftDownFinal, 4, (int)((long)shiftStartPhysicalIndex + temporaryIndexOffset));
      sqlShiftDownFinalIndex.bindParameter(stmtShiftDownFinal, 5, (int)((long)shiftEndPhysicalIndex + temporaryIndexOffset));
      stmtShiftDown.executeUpdate();
      stmtShiftDownFinal.executeUpdate();
    }

    @Override
    protected void dbShiftUp(IIDHandler idHandler, int shiftOffset, int shiftStartPhysicalIndex, int shiftEndPhysicalIndex) throws SQLException
    {
      if (stmtShiftUp == null)
      {
        stmtShiftUp = DBUtil.prepare(sqlShiftUpIndex, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlShiftUpIndex.bindParameter(stmtShiftUp, 2, id, idHandler);

        stmtShiftUpFinal = DBUtil.prepare(sqlShiftUpFinalIndex, accessor.getDBConnection(), ReuseProbability.HIGH);
        sqlShiftUpFinalIndex.bindParameter(stmtShiftUpFinal, 3, id, idHandler);
      }

      int temporaryIndexOffset = getTemporaryOffset(shiftStartPhysicalIndex, shiftEndPhysicalIndex);
      sqlShiftUpIndex.bindParameter(stmtShiftUp, 1, temporaryIndexOffset);
      sqlShiftUpIndex.bindParameter(stmtShiftUp, 3, shiftStartPhysicalIndex);
      sqlShiftUpIndex.bindParameter(stmtShiftUp, 4, shiftEndPhysicalIndex);

      sqlShiftUpFinalIndex.bindParameter(stmtShiftUpFinal, 1, temporaryIndexOffset);
      sqlShiftUpFinalIndex.bindParameter(stmtShiftUpFinal, 2, shiftOffset);
      sqlShiftUpFinalIndex.bindParameter(stmtShiftUpFinal, 4, (int)((long)shiftStartPhysicalIndex + temporaryIndexOffset));
      sqlShiftUpFinalIndex.bindParameter(stmtShiftUpFinal, 5, (int)((long)shiftEndPhysicalIndex + temporaryIndexOffset));
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
