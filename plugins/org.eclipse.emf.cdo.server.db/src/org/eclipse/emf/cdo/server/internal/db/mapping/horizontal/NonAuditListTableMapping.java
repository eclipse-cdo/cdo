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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingBatchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ListDeltaWork;
import org.eclipse.emf.cdo.server.internal.db.DBBatchingContext;
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
import java.util.List;
import java.util.ListIterator;

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
    monitor.begin(revisions.length);
    try
    {
      for (InternalCDORevision revision : revisions)
      {
        writeValues(accessor, revision);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void processDeltas(IDBStoreAccessor accessor, ListDeltaWork[] work, OMMonitor monitor)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

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

    private final DBBatchingContext batchingContext;

    private BatchedStatement deleteStmt;

    private BatchedStatement moveStmt;

    private BatchedStatement setStmt;

    private BatchedStatement insertStmt;

    private BatchedStatement clearStmt;

    private int deleteCount;

    private int moveCount;

    private int setCount;

    private int insertCount;

    private boolean discarded;

    private NonAuditDeltaBatch(IDBStoreAccessor accessor)
    {
      this.accessor = accessor;
      batchingContext = ((DBStoreAccessor)accessor).getBatchingContext();
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

    public void flushPhase()
    {
      batchingContext.flushPhase();
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
      }
    }

    private void add(BatchedStatement stmt) throws SQLException
    {
      stmt.executeUpdate();
      batchingContext.afterAdd(stmt);
    }

    private void flush(BatchedStatement stmt)
    {
      if (stmt != null)
      {
        batchingContext.flush(stmt);
      }
    }

    private void release(BatchedStatement stmt)
    {
      if (stmt != null)
      {
        batchingContext.release(stmt);
      }
    }

    private void discard(BatchedStatement stmt)
    {
      if (stmt != null)
      {
        batchingContext.discard(stmt);
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
        deleteStmt = createStatement(sqlDeleteItem);
      }

      return deleteStmt;
    }

    private BatchedStatement getMoveStmt()
    {
      if (moveStmt == null)
      {
        moveStmt = createStatement(sqlUpdateIndex);
      }

      return moveStmt;
    }

    private BatchedStatement getSetStmt()
    {
      if (setStmt == null)
      {
        setStmt = createStatement(sqlUpdateValue);
      }

      return setStmt;
    }

    private BatchedStatement getInsertStmt()
    {
      if (insertStmt == null)
      {
        insertStmt = createStatement(sqlInsertValue);
      }

      return insertStmt;
    }

    private BatchedStatement getClearStmt()
    {
      if (clearStmt == null)
      {
        clearStmt = createStatement(sqlClear);
      }

      return clearStmt;
    }

    private BatchedStatement createStatement(String sql)
    {
      BatchedStatement stmt = DBUtil.batched(accessor.getDBConnection().prepareStatement(sql, ReuseProbability.HIGH), batchingContext.getStatementBatchSize());
      batchingContext.manage(stmt);
      return stmt;
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
        batch.flushClearList();
      }
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
