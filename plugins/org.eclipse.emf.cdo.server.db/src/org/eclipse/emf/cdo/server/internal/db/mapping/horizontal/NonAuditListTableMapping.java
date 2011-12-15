/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.core.runtime.Assert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This is a list-to-table mapping optimized for non-audit-mode. It doesn't care about version and has delta support.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class NonAuditListTableMapping extends AbstractListTableMapping implements IListMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, NonAuditListTableMapping.class);

  private FieldInfo[] keyFields;

  private static final int UNBOUNDED_SHIFT = -1;

  /**
   * The central data structure which is used to calculate the outcomes of the list deltas.
   */
  private ArrayList<ManipulationElement> manipulations = new ArrayList<ManipulationElement>();

  /**
   * This is a flag to remember if a delta of type "clear" has been encountered. If so, the list in the DB has to be
   * cleared before writing out the changes.
   */
  private boolean clearFirst;

  private String sqlClear;

  private String sqlUpdateValue;

  private String sqlUpdateIndex;

  private String sqlInsertValue;

  private String sqlDeleteItem;

  private String sqlMassUpdateIndex;

  public NonAuditListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initSQLStrings();
  }

  private void initSQLStrings()
  {
    // ----------- clear list -------------------------
    StringBuilder builder = new StringBuilder();

    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? "); //$NON-NLS-1$

    sqlClear = builder.toString();

    builder.append(" AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? "); //$NON-NLS-1$

    sqlDeleteItem = builder.toString();

    // ----------- update one item --------------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateValue = builder.toString();

    // ----------- insert one item --------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" ("); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_VALUE);
    builder.append(") VALUES(?, ?, ?) "); //$NON-NLS-1$
    sqlInsertValue = builder.toString();

    // ----------- update one item index --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? "); //$NON-NLS-1$
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("=? "); //$NON-NLS-1$
    sqlUpdateIndex = builder.toString();

    // ----------- mass update item indexes --------------
    builder = new StringBuilder();
    builder.append("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("="); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append("+? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(" BETWEEN ? AND ?"); //$NON-NLS-1$
    sqlMassUpdateIndex = builder.toString();
  }

  @Override
  protected FieldInfo[] getKeyFields()
  {
    if (keyFields == null)
    {
      DBType dbType = getMappingStrategy().getStore().getIDHandler().getDBType();
      keyFields = new FieldInfo[] { new FieldInfo(CDODBSchema.LIST_REVISION_ID, dbType) };
    }

    return keyFields;
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, revision.getID());
  }

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    clearList(accessor, id);
  }

  /**
   * Clear a list of a given revision.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the id of the revision from which to remove all items
   */
  public void clearList(IDBStoreAccessor accessor, CDOID id)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlClear, ReuseProbability.HIGH);
      getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, id);
      DBUtil.update(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  public void processDelta(final IDBStoreAccessor accessor, final CDOID id, int branchId, int oldVersion,
      final int newVersion, long created, CDOListFeatureDelta delta)
  {
    CDOBranchPoint main = accessor.getStore().getRepository().getBranchManager().getMainBranch().getHead();

    InternalCDORevision originalRevision = (InternalCDORevision)accessor.getStore().getRepository()
        .getRevisionManager().getRevision(id, main, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
    int oldListSize = originalRevision.getList(getFeature()).size();

    // reset the clear-flag
    clearFirst = false;

    if (TRACER.isEnabled())
    {
      TRACER.format("ListTableMapping.processDelta for revision {0} - previous list size: {1}", originalRevision, //$NON-NLS-1$
          oldListSize);
    }

    if (manipulations == null)
    {
      manipulations = new ArrayList<ManipulationElement>();
    }
    else
    {
      manipulations.clear();
    }

    // create list and initialize with original indexes
    for (int i = 0; i < oldListSize; i++)
    {
      manipulations.add(createOriginalElement(i));
    }

    // let the visitor collect the changes
    ListDeltaVisitor visitor = new ListDeltaVisitor();

    if (TRACER.isEnabled())
    {
      TRACER.format("Procssing deltas..."); //$NON-NLS-1$
    }

    for (CDOFeatureDelta listDelta : delta.getListChanges())
    {
      listDelta.accept(visitor);
    }

    // TODO: here, we could implement further optimizations.
    // e.g., if more than 50% of the list's items are removed,
    // it's better to clear the list and reinsert all values
    // from scratch.

    // finally, write results to the database
    writeResultToDatabase(accessor, id);
  }

  /**
   * Write calculated changes to the database
   * 
   * @param accessor
   *          ,
   */
  private void writeResultToDatabase(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    PreparedStatement deleteStmt = null;
    PreparedStatement moveStmt = null;
    PreparedStatement setValueStmt = null;
    PreparedStatement insertStmt = null;

    int deleteCounter = 0;
    int moveCounter = 0;
    int setValueCounter = 0;
    int insertCounter = 0;

    int tempIndex = -1;

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing to database:"); //$NON-NLS-1$
    }

    if (clearFirst)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format(" - clear list"); //$NON-NLS-1$
      }

      clearList(accessor, id);
    }

    try
    {
      for (ManipulationElement element : manipulations)
      {
        if (element.is(ManipulationConstants.DELETE))
        {
          /*
           * Step 1: DELETE all elements e which have e.is(REMOVE) by e.sourceIdx
           */

          if (deleteStmt == null)
          {
            deleteStmt = accessor.getStatementCache().getPreparedStatement(sqlDeleteItem, ReuseProbability.HIGH);
            idHandler.setCDOID(deleteStmt, 1, id);
          }

          deleteStmt.setInt(2, element.sourceIndex);
          deleteStmt.addBatch();
          deleteCounter++;

          if (TRACER.isEnabled())
          {
            TRACER.format(" - delete at {0} ", element.sourceIndex); //$NON-NLS-1$
          }
        }

        if (element.is(ManipulationConstants.MOVE))
        {
          /*
           * Step 2: MOVE all elements e (by e.sourceIdx) which have e.is(MOVE) to temporary idx (-1, -2, -3, -4, ...)
           * and store temporary idx in e.tempIndex
           */
          if (moveStmt == null)
          {
            moveStmt = accessor.getStatementCache().getPreparedStatement(sqlUpdateIndex, ReuseProbability.HIGH);
            idHandler.setCDOID(moveStmt, 2, id);
          }

          moveStmt.setInt(3, element.sourceIndex); // from index
          moveStmt.setInt(1, --tempIndex); // to index
          element.tempIndex = tempIndex;
          moveStmt.addBatch();
          moveCounter++;

          if (TRACER.isEnabled())
          {
            TRACER.format(" - move {0} -> {1} ", element.sourceIndex, element.tempIndex); //$NON-NLS-1$
          }
        }
      }

      /* now perform deletes and moves ... */
      if (deleteCounter > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} delete operations", deleteCounter); //$NON-NLS-1$
        }

        executeBatch(deleteStmt, deleteCounter);
      }

      if (moveCounter > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} move operations", moveCounter); //$NON-NLS-1$
        }

        executeBatch(moveStmt, moveCounter);
        moveCounter = 0;
      }

      writeShiftOperations(accessor, id);

      for (ManipulationElement element : manipulations)
      {
        if (element.is(ManipulationConstants.MOVE))
        {
          /*
           * Step 4: MOVE all elements e have e.is(MOVE) from e.tempIdx to e.destinationIdx (because we have moved them
           * before, moveStmt is always initialized
           */
          moveStmt.setInt(3, element.tempIndex); // from index
          moveStmt.setInt(1, element.destinationIndex); // to index
          moveStmt.addBatch();
          moveCounter++;

          if (TRACER.isEnabled())
          {
            TRACER.format(" - move {0} -> {1} ", element.tempIndex, element.destinationIndex); //$NON-NLS-1$
          }
        }

        if (element.is(ManipulationConstants.SET_VALUE))
        {
          /*
           * Step 5: SET all elements which have e.type == SET_VALUE by index == e.destinationIdx
           */
          if (setValueStmt == null)
          {
            setValueStmt = accessor.getStatementCache().getPreparedStatement(sqlUpdateValue, ReuseProbability.HIGH);
            idHandler.setCDOID(setValueStmt, 2, id);
          }

          setValueStmt.setInt(3, element.destinationIndex);
          getTypeMapping().setValue(setValueStmt, 1, element.value);
          setValueStmt.addBatch();
          setValueCounter++;

          if (TRACER.isEnabled())
          {
            TRACER.format(" - set value at {0} to {1} ", element.destinationIndex, element.value); //$NON-NLS-1$
          }
        }

        if (element.is(ManipulationConstants.INSERT))
        {
          /*
           * Step 6: INSERT all elements which have e.type == INSERT.
           */
          if (insertStmt == null)
          {
            insertStmt = accessor.getStatementCache().getPreparedStatement(sqlInsertValue, ReuseProbability.HIGH);
            idHandler.setCDOID(insertStmt, 1, id);
          }

          insertStmt.setInt(2, element.destinationIndex);
          getTypeMapping().setValue(insertStmt, 3, element.value);
          insertStmt.addBatch();
          insertCounter++;

          if (TRACER.isEnabled())
          {
            TRACER.format(" - insert value at {0} : value {1} ", element.destinationIndex, element.value); //$NON-NLS-1$
          }
        }
      }

      // finally perform all operations
      if (moveCounter > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} move operations", moveCounter); //$NON-NLS-1$
        }

        executeBatch(moveStmt, moveCounter);
      }

      if (insertCounter > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} insert operations", insertCounter); //$NON-NLS-1$
        }

        executeBatch(insertStmt, insertCounter);
      }

      if (setValueCounter > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Performing {0} set operations", setValueCounter); //$NON-NLS-1$
        }

        executeBatch(setValueStmt, setValueCounter);
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      releaseStatement(accessor, deleteStmt, moveStmt, insertStmt, setValueStmt);
    }
  }

  public static void executeBatch(PreparedStatement stmt, int counter)
  {
    executeBatch(stmt, counter, true);
  }

  public static void executeBatch(PreparedStatement stmt, int counter, boolean checkExactlyOne)
  {
    try
    {
      int[] results = stmt.executeBatch();
      if (results.length != counter)
      {
        throw new DBException("Statement has " + results.length + " results (expected: " + counter + ")");
      }

      for (int i = 0; i < results.length; i++)
      {
        int result = results[i];
        if (result < 0 && result != Statement.SUCCESS_NO_INFO)
        {
          throw new DBException("Result " + i + " is not successful: " + result);
        }
        else if (checkExactlyOne && result != 1)
        {
          throw new DBException("Result " + i + " did not affect exactly one row: " + result);
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * Perform the shift operations to adjust indexes resulting from remove, insert, and move operations.
   * 
   * @see #writeResultToDatabase(IDBStoreAccessor, CDOID)
   * @throws SQLException
   */
  private void writeShiftOperations(IDBStoreAccessor accessor, CDOID id) throws SQLException
  {
    PreparedStatement shiftIndicesStmt = null;
    try
    {
      /*
       * Step 3: shift all elements which have to be shifted up or down because of add, remove or move of other elements
       * to their proper position. This has to be done in two phases to avoid collisions, as the index has to be unique
       * and shift up operations have to be executed in top to bottom order.
       */

      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      int size = manipulations.size();

      LinkedList<ShiftOperation> shiftOperations = new LinkedList<ShiftOperation>();

      /*
       * If a necessary shift is detected (source and destination indices differ), firstIndex is set to the current
       * index and currentOffset is set to the offset of the shift operation. When a new offset is detected or the range
       * is interrupted, we record the range and start a new one if needed.
       */
      int rangeStartIndex = ManipulationConstants.NO_INDEX;
      int rangeOffset = 0;

      // iterate through the manipulationElements and collect the necessary operations
      for (int i = 0; i < size; i++)
      {
        ManipulationElement element = manipulations.get(i);

        /*
         * shift applies only to elements which are not moved, inserted or deleted (i.e. only plain SET_VALUE and NONE
         * are affected)
         */
        if (element.type == ManipulationConstants.NONE || element.type == ManipulationConstants.SET_VALUE)
        {
          int elementOffset = element.destinationIndex - element.sourceIndex;

          /*
           * first make sure if we have to close a previous range. This is the case, if the current element's offset
           * differs from the rangeOffset and a range is open.
           */
          if (elementOffset != rangeOffset && rangeStartIndex != ManipulationConstants.NO_INDEX)
          {
            // there is an open range but the rangeOffset differs. We have to close the open range
            shiftOperations.add(new ShiftOperation(rangeStartIndex, i - 1, rangeOffset));
            // and reset the state
            rangeStartIndex = ManipulationConstants.NO_INDEX;
            rangeOffset = 0;
          }

          /*
           * at this point, either a range is open, which means that the current element also fits in the range (i.e.
           * the offsets match) or no range is open. In the latter case, we have to open one if the current element's
           * offset is not 0.
           */
          if (elementOffset != 0 && rangeStartIndex == ManipulationConstants.NO_INDEX)
          {
            rangeStartIndex = i;
            rangeOffset = elementOffset;
          }
        }
        else
        { // shift does not apply to this element because of its type
          if (rangeStartIndex != ManipulationConstants.NO_INDEX)
          {
            // if there is an open range, we have to close and remember it
            shiftOperations.add(new ShiftOperation(rangeStartIndex, i - 1, rangeOffset));
            // and reset the state
            rangeStartIndex = ManipulationConstants.NO_INDEX;
            rangeOffset = 0;
          }
        }
      }

      // after the iteration, we have to make sure that we remember the last open range, if it is there
      if (rangeStartIndex != ManipulationConstants.NO_INDEX)
      {
        shiftOperations.add(new ShiftOperation(rangeStartIndex, size - 1, rangeOffset));
      }

      /*
       * now process the operations. Move down operations can be performed directly, move up operations need to be
       * performed later in the reverse direction
       */
      int operationCounter = shiftOperations.size();
      ListIterator<ShiftOperation> operationIt = shiftOperations.listIterator();

      while (operationIt.hasNext())
      {
        ShiftOperation operation = operationIt.next();
        if (operation.offset < 0)
        {
          if (shiftIndicesStmt == null)
          {
            shiftIndicesStmt = accessor.getStatementCache().getPreparedStatement(sqlMassUpdateIndex,
                ReuseProbability.HIGH);
            idHandler.setCDOID(shiftIndicesStmt, 2, id);
          }

          if (TRACER.isEnabled())
          {
            TRACER.format(" - shift {0} ", operation); //$NON-NLS-1$
          }

          shiftIndicesStmt.setInt(1, operation.offset);
          shiftIndicesStmt.setInt(3, operation.startIndex);
          shiftIndicesStmt.setInt(4, operation.endIndex);
          shiftIndicesStmt.addBatch();

          operationIt.remove();
        }
      }
      while (operationIt.hasPrevious())
      {
        ShiftOperation operation = operationIt.previous();
        if (shiftIndicesStmt == null)
        {
          shiftIndicesStmt = accessor.getStatementCache().getPreparedStatement(sqlMassUpdateIndex,
              ReuseProbability.HIGH);
          idHandler.setCDOID(shiftIndicesStmt, 2, id);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format(" - shift {0} ", operation); //$NON-NLS-1$
        }

        shiftIndicesStmt.setInt(1, operation.offset);
        shiftIndicesStmt.setInt(3, operation.startIndex);
        shiftIndicesStmt.setInt(4, operation.endIndex);
        shiftIndicesStmt.addBatch();
      }

      if (operationCounter > 0)
      {
        executeBatch(shiftIndicesStmt, operationCounter, false);
      }
    }
    finally
    {
      releaseStatement(accessor, shiftIndicesStmt);
    }
  }

  private void releaseStatement(IDBStoreAccessor accessor, PreparedStatement... stmts)
  {
    Throwable t = null;

    for (PreparedStatement stmt : stmts)
    {
      try
      {
        if (stmt != null)
        {
          try
          {
            stmt.clearBatch();
          }
          catch (SQLException e)
          {
            throw new DBException(e);
          }
          finally
          {
            accessor.getStatementCache().releasePreparedStatement(stmt);
          }
        }
      }
      catch (Throwable th)
      {
        if (t == null)
        {
          // remember first exception
          t = th;
        }

        // more exceptions go to the log
        OM.LOG.error(t);
      }
    }

    if (t != null)
    {
      throw new DBException(t);
    }
  }

  /**
   * Helper method: shift all (destination) indexes in the interval [from,to] (inclusive at both ends) by offset
   * (positive or negative).
   */
  private void shiftIndexes(int from, int to, int offset)
  {
    for (ManipulationElement e : manipulations)
    {
      if (e.destinationIndex >= from && (to == UNBOUNDED_SHIFT || e.destinationIndex <= to))
      {
        e.destinationIndex += offset;
      }
    }
  }

  /**
   * Find a manipulation item by destination index).
   */
  private ManipulationElement findElement(int index)
  {
    for (ManipulationElement e : manipulations)
    {
      if (e.destinationIndex == index)
      {
        return e;
      }
    }

    // never reached
    Assert.isTrue(false);
    return null;
  }

  /**
   * Delete an element (used in remove and clear)
   */
  private void deleteItem(ManipulationElement e)
  {
    if (e.is(ManipulationConstants.INSERT))
    {
      // newly inserted items are simply removed, as
      // removing inserted items is equal to no change at all.
      manipulations.remove(e);
    }
    else
    {
      // mark the existing item as to be deleted.
      // (previous MOVE and SET conditions are overridden by setting
      // the exclusive DELETE type).
      e.type = ManipulationConstants.DELETE;
      e.destinationIndex = ManipulationConstants.NO_INDEX;
    }
  }

  /**
   * Create a ManipulationElement which represents an element which already is in the list.
   */
  private ManipulationElement createOriginalElement(int index)
  {
    return new ManipulationElement(index, index, ManipulationConstants.NIL, ManipulationConstants.NONE);
  }

  /**
   * Create a ManipulationElement which represents an element which is inserted in the list.
   */
  private ManipulationElement createInsertedElement(int index, Object value)
  {
    return new ManipulationElement(ManipulationConstants.NONE, index, value, ManipulationConstants.INSERT);
  }

  /**
   * @author Eike Stepper
   */
  private final class ListDeltaVisitor implements CDOFeatureDeltaVisitor
  {
    public void visit(CDOAddFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - insert at {0} value {1}", delta.getIndex(), delta.getValue()); //$NON-NLS-1$
      }

      // make room for the new item
      shiftIndexes(delta.getIndex(), UNBOUNDED_SHIFT, +1);

      // create the item
      manipulations.add(createInsertedElement(delta.getIndex(), delta.getValue()));
    }

    public void visit(CDORemoveFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - remove at {0}", delta.getIndex()); //$NON-NLS-1$
      }

      ManipulationElement e = findElement(delta.getIndex());
      deleteItem(e);

      // fill the gap by shifting all subsequent items down
      shiftIndexes(delta.getIndex() + 1, UNBOUNDED_SHIFT, -1);
    }

    public void visit(CDOSetFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - set at {0} value {1}", delta.getIndex(), delta.getValue()); //$NON-NLS-1$
      }

      ManipulationElement e = findElement(delta.getIndex());
      // set the new value
      e.value = delta.getValue();

      // if the item is freshly inserted we do not set the SET-mark.
      // setting the value of a new item results in inserting with the
      // new value at once.
      if (!e.is(ManipulationConstants.INSERT))
      {
        // else mark the existing item to be set to a new value
        e.addType(ManipulationConstants.SET_VALUE);
      }
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - clear list"); //$NON-NLS-1$
      }

      // set the clear-flag
      clearFirst = true;

      // and also clear all manipulation items
      manipulations.clear();
    }

    public void visit(CDOMoveFeatureDelta delta)
    {
      int fromIdx = delta.getOldPosition();
      int toIdx = delta.getNewPosition();

      if (TRACER.isEnabled())
      {
        TRACER.format("  - move {0} -> {1}", fromIdx, toIdx); //$NON-NLS-1$
      }

      // ignore the trivial case
      if (fromIdx == toIdx)
      {
        return;
      }

      ManipulationElement e = findElement(fromIdx);

      // adjust indexes and shift either up or down
      if (fromIdx < toIdx)
      {
        shiftIndexes(fromIdx + 1, toIdx, -1);
      }
      else
      { // fromIdx > toIdx here
        shiftIndexes(toIdx, fromIdx - 1, +1);
      }

      // set the new index
      e.destinationIndex = toIdx;

      // if it is a new element, no MOVE mark needed, because we insert it
      // at the new position
      if (!e.is(ManipulationConstants.INSERT))
      {
        // else we need to handle the move of an existing item
        e.addType(ManipulationConstants.MOVE);
      }
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      if (delta.getFeature().isUnsettable())
      {
        Assert.isTrue(false);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("  - unset list"); //$NON-NLS-1$
      }

      // set the clear-flag
      clearFirst = true;

      // and also clear all manipulation items
      manipulations.clear();
    }

    public void visit(CDOListFeatureDelta delta)
    {
      // never called
      Assert.isTrue(false);
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      // never called
      Assert.isTrue(false);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ManipulationConstants
  {
    public static final int NO_INDEX = Integer.MIN_VALUE;

    public static final int DELETE = 1 << 4;

    public static final int INSERT = 1 << 3;

    public static final int MOVE = 1 << 2;

    public static final int SET_VALUE = 1 << 1;

    public static final Object NIL = new Object();

    public static final int NONE = 0;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ManipulationElement
  {
    public int type;

    public int sourceIndex;

    public int tempIndex;

    public int destinationIndex;

    public Object value;

    public ManipulationElement(int srcIdx, int dstIdx, Object val, int t)
    {
      sourceIndex = srcIdx;
      tempIndex = ManipulationConstants.NONE;
      destinationIndex = dstIdx;
      value = val;
      type = t;
    }

    public boolean is(int t)
    {
      return (type & t) > 0;
    }

    public void addType(int t)
    {
      type |= t;
    }
  }

  private static class ShiftOperation
  {
    final int startIndex;

    final int endIndex;

    final int offset;

    ShiftOperation(int startIndex, int endIndex, int offset)
    {
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.offset = offset;
    }

    @Override
    public String toString()
    {
      return "range [" + startIndex + ".." + endIndex + "] offset " + offset;
    }
  }
}
