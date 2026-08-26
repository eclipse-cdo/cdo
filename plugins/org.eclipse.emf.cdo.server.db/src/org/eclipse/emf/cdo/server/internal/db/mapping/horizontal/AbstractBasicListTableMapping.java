/*
 * Copyright (c) 2013, 2016, 2018, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeatureType;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
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
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping3;
import org.eclipse.emf.cdo.server.db.mapping.ILobRefsUpdater;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.DBIndexAnnotation;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Stefan Winkler
 */
public abstract class AbstractBasicListTableMapping implements IListMapping3
{
  private IMappingStrategy mappingStrategy;

  private EClass containingClass;

  private EStructuralFeature feature;

  public AbstractBasicListTableMapping(IMappingStrategy mappingStrategy, EClass containingClass, EStructuralFeature feature)
  {
    this.mappingStrategy = mappingStrategy;
    this.containingClass = containingClass;
    this.feature = feature;
  }

  public final IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public final EClass getContainingClass()
  {
    return containingClass;
  }

  @Override
  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  @Override
  public void addSimpleChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int index)
  {
    builder.append(index());
    builder.append('=');
    builder.append(index);
  }

  @Override
  public void addRangedChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int fromIndex, int toIndex)
  {
    builder.append(index());
    builder.append(" BETWEEN "); //$NON-NLS-1$
    builder.append(fromIndex);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(toIndex - 1);
  }

  @Override
  public void setClassMapping(IClassMapping classMapping)
  {
    // Subclasses may override.
  }

  public abstract void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version);

  protected final boolean needsIndexOnValueField(EStructuralFeature feature)
  {
    IMappingStrategy mappingStrategy = getMappingStrategy();
    Set<CDOFeatureType> forceIndexes = AbstractMappingStrategy.getForceIndexes(mappingStrategy);

    if (CDOFeatureType.matchesCombination(feature, forceIndexes))
    {
      return true;
    }

    EClass eClass = getContainingClass();
    EStructuralFeature[] allPersistentFeatures = CDOModelUtil.getClassInfo(eClass).getAllPersistentFeatures();

    for (List<EStructuralFeature> features : DBIndexAnnotation.getIndices(eClass, allPersistentFeatures))
    {
      if (features.size() == 1)
      {
        if (features.get(0) == feature)
        {
          return true;
        }
      }
    }

    return false;
  }

  protected abstract IDBField index();

  /**
   * @author Eike Stepper
   */
  protected static abstract class AbstractListDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractListDeltaWriter.class);

    private static final int UNBOUNDED_SHIFT = -1;

    private static final int NO_INDEX = Integer.MIN_VALUE;

    private static final int NONE = 0;

    private static final int SET = 1 << 1;

    private static final int MOVE = 1 << 2;

    private static final int INSERT = 1 << 3;

    private static final int DELETE = 1 << 4;

    protected final IDBStoreAccessor accessor;

    protected final CDOID id;

    private final List<CDOFeatureDelta> listChanges;

    private final List<Manipulation> manipulations;

    private boolean clearFirst;

    private int relativeTargetIndexOffset;

    /**
     * Start of a range [tempIndex, tempIndex-1, ...] which lies outside of the normal list indexes and which serve as
     * temporary space to move items temporarily to get them out of the way of other operations.
     */
    private int temporaryRelativeIndex = -1;

    private int temporaryIndex;

    private boolean physicalOffsetOptimization;

    private int newListSize;

    public AbstractListDeltaWriter(IDBStoreAccessor accessor, CDOID id, List<CDOFeatureDelta> listChanges, int oldListSize)
    {
      this.accessor = accessor;
      this.id = id;
      this.listChanges = listChanges;

      manipulations = createManipulations(id, listChanges, oldListSize);
      newListSize = oldListSize;
    }

    public void writeListDeltas()
    {
      planListDeltas();

      try
      {
        writeResultToDatabase();
      }
      catch (SQLException e)
      {
        throw new DBException(e);
      }

      throw new NewListSizeResult(newListSize);
    }

    /**
     * Applies the semantic list deltas and returns the resulting logical list size without performing JDBC work.
     */
    public int planListDeltas()
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Processing list deltas..."); //$NON-NLS-1$
      }

      for (CDOFeatureDelta listDelta : listChanges)
      {
        listDelta.accept(this);
      }

      // boolean zeroBasedIndex =
      // ((HorizontalNonAuditMappingStrategy)accessor.getStore().getMappingStrategy()).shallForceZeroBasedIndex();
      // if (!zeroBasedIndex)
      if (!isZeroBasedIndex())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Optimizing list indexes..."); //$NON-NLS-1$
        }

        optimizeListIndexes();
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Result to be written to DB:");
        for (Manipulation manipulation : manipulations)
        {
          TRACER.trace(manipulation.toString());
        }
      }

      return newListSize;
    }

    @Override
    public void visit(CDOAddFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - insert at {0} value {1}", delta.getIndex(), delta.getValue()); //$NON-NLS-1$
      }

      // Make room for the new item
      shiftIndexes(delta.getIndex(), UNBOUNDED_SHIFT, +1);

      // Create the item
      manipulations.add(Manipulation.createInsertedElement(delta.getIndex(), delta.getValue()));
      ++newListSize;
    }

    @Override
    public void visit(CDORemoveFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - remove at {0}", delta.getIndex()); //$NON-NLS-1$
      }

      Manipulation e = findManipulation(delta.getIndex());
      deleteItem(e);

      // Fill the gap by shifting all subsequent items down
      shiftIndexes(delta.getIndex() + 1, UNBOUNDED_SHIFT, -1);
      --newListSize;
    }

    @Override
    public void visit(CDOSetFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - set at {0} value {1}", delta.getIndex(), delta.getValue()); //$NON-NLS-1$
      }

      Manipulation manipulation = findManipulation(delta.getIndex());

      // Set the new value
      manipulation.value = delta.getValue();

      // If the item is freshly inserted we do not set the SET-mark.
      // Setting the value of a new item results in inserting with the new value at once.
      if (!manipulation.is(INSERT))
      {
        // Else mark the existing item to be set to a new value
        manipulation.addType(SET);
      }
    }

    @Override
    public void visit(CDOUnsetFeatureDelta delta)
    {
      if (!delta.getFeature().isUnsettable())
      {
        throw new IllegalArgumentException("Feature is not unsettable: " + delta);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("  - unset list"); //$NON-NLS-1$
      }

      // Set the clear-flag
      clearFirst = true;

      // And also clear all manipulation items
      manipulations.clear();
      newListSize = 0;
    }

    @Override
    public void visit(CDOClearFeatureDelta delta)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("  - clear list"); //$NON-NLS-1$
      }

      // Set the clear-flag
      clearFirst = true;

      // And also clear all manipulation items
      manipulations.clear();
      newListSize = 0;
    }

    @Override
    public void visit(CDOMoveFeatureDelta delta)
    {
      int sourceIndex = delta.getOldPosition();
      int targetIndex = delta.getNewPosition();

      if (TRACER.isEnabled())
      {
        TRACER.format("  - move {0} -> {1}", sourceIndex, targetIndex); //$NON-NLS-1$
      }

      // Ignore the trivial case
      if (sourceIndex == targetIndex)
      {
        return;
      }

      Manipulation manipulation = findManipulation(sourceIndex);

      // Adjust indexes and shift either up or down
      if (sourceIndex < targetIndex)
      {
        shiftIndexes(sourceIndex + 1, targetIndex, -1);
      }
      else
      {
        // sourceIndex > targetIndex here
        shiftIndexes(targetIndex, sourceIndex - 1, +1);
      }

      // Set the new index
      manipulation.targetIndex = targetIndex;

      // If it is a new element, no MOVE mark needed, because we insert it at the new position
      if (!manipulation.is(INSERT))
      {
        // Else we need to handle the move of an existing item
        manipulation.addType(MOVE);
      }
    }

    @Override
    @Deprecated
    public void visit(CDOListFeatureDelta delta)
    {
      throw new UnsupportedOperationException("Should never be called");
    }

    @Override
    @Deprecated
    public void visit(CDOContainerFeatureDelta delta)
    {
      throw new UnsupportedOperationException("Should never be called");
    }

    protected boolean isZeroBasedIndex()
    {
      return false;
    }

    protected List<Manipulation> createManipulations(CDOID id, List<CDOFeatureDelta> listChanges, int oldListSize)
    {
      List<Manipulation> manipulations = new ArrayList<>(oldListSize);

      // Create list and initialize with original indexes
      for (int i = 0; i < oldListSize; i++)
      {
        manipulations.add(Manipulation.createOriginalElement(i));
      }

      return manipulations;
    }

    /**
     * Helper method: shift all target indexes in the interval [source,target] (inclusive at both ends) by offset
     * (positive or negative).
     */
    private void shiftIndexes(int source, int target, int offset)
    {
      for (Manipulation manipulation : manipulations)
      {
        if (manipulation.targetIndex >= source && (target == UNBOUNDED_SHIFT || manipulation.targetIndex <= target))
        {
          manipulation.targetIndex += offset;
        }
      }
    }

    /**
     * Find a manipulation item by target index).
     */
    private Manipulation findManipulation(int index)
    {
      for (Manipulation manipulation : manipulations)
      {
        if (manipulation.targetIndex == index)
        {
          return manipulation;
        }
      }

      throw new IllegalStateException("Should never be reached");
    }

    /**
     * Delete an element (used in remove and clear)
     */
    private void deleteItem(Manipulation manipulation)
    {
      if (manipulation.is(INSERT))
      {
        // Newly inserted items are simply removed, as removing inserted items is equal to no change at all.
        manipulations.remove(manipulation);
      }
      else
      {
        // Mark the existing item as to be deleted.
        // Previous MOVE and SET conditions are overridden by setting the exclusive DELETE type.
        manipulation.types = DELETE;
        manipulation.targetIndex = NO_INDEX;
      }
    }

    /**
     * Called after all deltas are applied and before the results are written to the database. This method post-processes
     * the manipulation elements in order to minimize database access.
     */
    private void optimizeListIndexes()
    {
      physicalOffsetOptimization = true;
      /*
       * This is an optimization which reduces the amount of modifications on the database to maintain list indexes. For
       * the optimization, we let go of the assumption that indexes are zero-based. Instead, we work with an offset at
       * the database level which can change with every change to the list (e.g. if the second element is removed from a
       * list with 1000 elements, instead of shifting down indexes 2 to 1000 by 1, we shift up index 0 by 1 and have now
       * a list with indexes starting at 1 instead of 0. This optimization is applied by modifying the list of
       * Manipulations, which can be seen as the database modification plan.
       */

      if (TRACER.isEnabled())
      {
        TRACER.trace("Offset optimization."); //$NON-NLS-1$
      }

      relativeTargetIndexOffset = calculateOptimalOffset();

      if (TRACER.isEnabled())
      {
        TRACER.trace("Relative target offset = " + relativeTargetIndexOffset); //$NON-NLS-1$
      }

      applyOffsetToTargetIndexes(relativeTargetIndexOffset);

      temporaryRelativeIndex = Math.min(0, relativeTargetIndexOffset) - 1;
    }

    /**
     * Calculate the optimal offset w.r.t. the manipulations planned. The optimal offset is the offset which occurs the
     * most in the manipulations (because letting this offset be neutral leads to the least manipulations. Note: the
     * zero offset is also regarded as an offset as any other, because selecting an offset != 0 would also lead to
     * elements with original offset 0 to be moved.
     */
    private int calculateOptimalOffset()
    {
      HashMap<Integer, Integer> occurrences = new HashMap<>();
      int bestOffset = 0;
      int bestOffsetOccurrence = 0;

      for (Manipulation manipulation : manipulations)
      {
        int sourceIndex = manipulation.sourceIndex;
        int targetIndex = manipulation.targetIndex;

        if (sourceIndex != NO_INDEX && targetIndex != NO_INDEX)
        {
          int offset = targetIndex - sourceIndex;
          Integer oldOccurrence = occurrences.get(offset);

          int newOccurrence;
          if (oldOccurrence == null)
          {
            newOccurrence = 1;
          }
          else
          {
            newOccurrence = oldOccurrence + 1;
          }

          occurrences.put(offset, newOccurrence);

          // Remember maximum along the way
          if (newOccurrence > bestOffsetOccurrence)
          {
            bestOffsetOccurrence = newOccurrence;
            bestOffset = offset;
          }
        }
      }

      // The offset which has occurred the most has to be applied negatively to normalize the list
      // therefore return the negative offset as the new offset to be applied
      return -bestOffset;
    }

    private void applyOffsetToTargetIndexes(int targetOffset)
    {
      if (targetOffset != 0)
      {
        for (Manipulation manipulation : manipulations)
        {
          if (manipulation.targetIndex != NO_INDEX)
          {
            // Apply the offset to all indices to make them relative to the new offset
            manipulation.targetIndex += targetOffset;
          }
        }
      }
    }

    private void materializePhysicalIndexes()
    {
      if (!physicalOffsetOptimization)
      {
        temporaryIndex = -1;
        return;
      }

      int currentIndexOffset = getCurrentIndexOffset();
      boolean normalizeToZero = (long)Math.abs(currentIndexOffset) + (long)manipulations.size() > Integer.MAX_VALUE;
      int targetIndexOffset = normalizeToZero ? -relativeTargetIndexOffset : currentIndexOffset;

      if (TRACER.isEnabled())
      {
        TRACER.trace("Current offset = " + currentIndexOffset); //$NON-NLS-1$
        TRACER.trace("Target offset = " + (normalizeToZero ? 0 : currentIndexOffset + relativeTargetIndexOffset)); //$NON-NLS-1$
      }

      for (Manipulation manipulation : manipulations)
      {
        if (manipulation.sourceIndex != NO_INDEX)
        {
          manipulation.sourceIndex += currentIndexOffset;
        }

        if (manipulation.targetIndex != NO_INDEX)
        {
          manipulation.targetIndex += targetIndexOffset;
        }
      }

      temporaryIndex = normalizeToZero ? Math.min(currentIndexOffset, 0) - 1 : currentIndexOffset + temporaryRelativeIndex;
    }

    protected final int getNextTmpIndex()
    {
      return --temporaryIndex;
    }

    /**
     * Write calculated changes to the database
     */
    protected void writeResultToDatabase() throws SQLException
    {
      materializePhysicalIndexes();

      IIDHandler idHandler = accessor.getStore().getIDHandler();
      if (TRACER.isEnabled())
      {
        TRACER.trace("Writing to database:"); //$NON-NLS-1$
      }

      if (clearFirst)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(" - clear list"); //$NON-NLS-1$
        }

        clearList();
      }

      for (Manipulation manipulation : manipulations)
      {
        if (manipulation.is(DELETE))
        {
          /*
           * Step 1: DELETE all elements e which have e.is(DELETE) by e.srcIndex
           */
          dbDelete(idHandler, manipulation.sourceIndex);

          if (TRACER.isEnabled())
          {
            TRACER.format(" - delete at {0} ", manipulation.sourceIndex); //$NON-NLS-1$
          }
        }

        if (manipulation.is(MOVE))
        {
          /*
           * Step 2: MOVE all elements e (by e.srcIndex) which have e.is(MOVE) to tmpIndex (-1, -2, -3, -4, ...) and
           * store tmpIndex in e.tempIndex
           */
          manipulation.temporaryIndex = getNextTmpIndex();
          dbMove(idHandler, manipulation.sourceIndex, manipulation.temporaryIndex, manipulation.sourceIndex);

          if (TRACER.isEnabled())
          {
            TRACER.format(" - move {0} -> {1} ", manipulation.sourceIndex, manipulation.temporaryIndex); //$NON-NLS-1$
          }
        }
      }

      writeShifts(idHandler);

      ITypeMapping typeMapping = getTypeMapping();
      for (Manipulation manipulation : manipulations)
      {
        if (manipulation.is(MOVE))
        {
          /*
           * Step 4: MOVE all elements e have e.is(MOVE) from e.tempIdx to e.dstIndex (because we have moved them
           * before, moveStmt is always initialized
           */
          dbMove(idHandler, manipulation.temporaryIndex, manipulation.targetIndex, manipulation.sourceIndex);

          if (TRACER.isEnabled())
          {
            TRACER.format(" - move {0} -> {1} ", manipulation.temporaryIndex, manipulation.targetIndex); //$NON-NLS-1$
          }
        }

        if (manipulation.is(SET))
        {
          /*
           * Step 5: SET all elements which have e.type == SET by index == e.dstIndex
           */
          dbSet(idHandler, typeMapping, manipulation.targetIndex, manipulation.value, manipulation.sourceIndex);

          if (TRACER.isEnabled())
          {
            TRACER.format(" - set value at {0} to {1} ", manipulation.targetIndex, manipulation.value); //$NON-NLS-1$
          }
        }

        if (manipulation.is(INSERT))
        {
          /*
           * Step 6: INSERT all elements which have e.type == INSERT.
           */
          dbInsert(idHandler, typeMapping, manipulation.targetIndex, manipulation.value);

          if (TRACER.isEnabled())
          {
            TRACER.format(" - insert value at {0} : value {1} ", manipulation.targetIndex, manipulation.value); //$NON-NLS-1$
          }
        }
      }
    }

    /**
     * Perform the shift operations to adjust indexes resulting from remove, insert, and move operations.
     *
     * @see #writeResultToDatabase(IDBStoreAccessor, CDOID)
     * @throws SQLException
     */
    protected void writeShifts(IIDHandler idHandler) throws SQLException
    {
      /*
       * Step 3: shift all elements which have to be shifted up or down because of add, remove or move of other elements
       * to their proper position. This has to be done in two phases to avoid collisions, as the index has to be unique
       * and shift up operations have to be executed in top to bottom order.
       */
      LinkedList<Shift> shiftOperations = new LinkedList<>();

      /*
       * If a necessary shift is detected (source and target indices differ), firstIndex is set to the current index and
       * currentOffset is set to the offset of the shift operation. When a new offset is detected or the range is
       * interrupted, we record the range and start a new one if needed.
       */
      int rangeStartIndex = NO_INDEX;
      int rangeOffset = 0;
      int lastElementIndex = NO_INDEX;

      // Iterate through the manipulationElements and collect the necessary operations
      for (Manipulation manipulation : manipulations)
      {
        /*
         * Shift applies only to elements which are not moved, inserted or deleted (i.e. only plain SET and NONE are
         * affected)
         */
        if (manipulation.types == NONE || manipulation.types == SET)
        {
          int elementOffset = manipulation.targetIndex - manipulation.sourceIndex;

          /*
           * First make sure if we have to close a previous range. This is the case, if the current element's offset
           * differs from the rangeOffset and a range is open.
           */
          if (elementOffset != rangeOffset && rangeStartIndex != NO_INDEX)
          {
            // There is an open range but the rangeOffset differs. We have to close the open range
            shiftOperations.add(new Shift(rangeStartIndex, lastElementIndex, rangeOffset));

            // And reset the state
            rangeStartIndex = NO_INDEX;
            rangeOffset = 0;
          }

          /*
           * At this point, either a range is open, which means that the current element also fits in the range (i.e.
           * the offsets match) or no range is open. In the latter case, we have to open one if the current element's
           * offset is not 0.
           */
          if (elementOffset != 0 && rangeStartIndex == NO_INDEX)
          {
            rangeStartIndex = manipulation.sourceIndex;
            rangeOffset = elementOffset;
          }
        }
        else
        {
          // Shift does not apply to this element because of its type
          if (rangeStartIndex != NO_INDEX)
          {
            // If there is an open range, we have to close and remember it
            shiftOperations.add(new Shift(rangeStartIndex, lastElementIndex, rangeOffset));

            // And reset the state
            rangeStartIndex = NO_INDEX;
            rangeOffset = 0;
          }
        }

        lastElementIndex = manipulation.sourceIndex;
      }

      // After the iteration, we have to make sure that we remember the last open range, if it is there
      if (rangeStartIndex != NO_INDEX)
      {
        shiftOperations.add(new Shift(rangeStartIndex, lastElementIndex, rangeOffset));
      }

      /*
       * Now process the operations. Move down operations can be performed directly, move up operations need to be
       * performed later in the reverse direction
       */
      ListIterator<Shift> operationIt = shiftOperations.listIterator();
      writeShiftsDown(idHandler, operationIt);
      writeShiftsUp(idHandler, operationIt);
    }

    protected void writeShiftsDown(IIDHandler idHandler, ListIterator<Shift> operationIt) throws SQLException
    {
      while (operationIt.hasNext())
      {
        Shift operation = operationIt.next();
        if (operation.offset < 0)
        {
          dbShiftDown(idHandler, operation.offset, operation.startIndex, operation.endIndex);

          if (TRACER.isEnabled())
          {
            TRACER.format(" - shift down {0} ", operation); //$NON-NLS-1$
          }

          operationIt.remove();
        }
      }
    }

    protected void writeShiftsUp(IIDHandler idHandler, ListIterator<Shift> operationIt) throws SQLException
    {
      while (operationIt.hasPrevious())
      {
        Shift operation = operationIt.previous();
        dbShiftUp(idHandler, operation.offset, operation.startIndex, operation.endIndex);

        if (TRACER.isEnabled())
        {
          TRACER.format(" - shift up {0} ", operation); //$NON-NLS-1$
        }
      }
    }

    protected abstract void dbDelete(IIDHandler idHandler, int index) throws SQLException;

    protected abstract void dbMove(IIDHandler idHandler, int sourcePhysicalIndex, int targetPhysicalIndex, int sourceIndex) throws SQLException;

    protected abstract void dbSet(IIDHandler idHandler, ITypeMapping typeMapping, int targetPhysicalIndex, Object value, int sourceIndex) throws SQLException;

    protected abstract void dbInsert(IIDHandler idHandler, ITypeMapping typeMapping, int index, Object value) throws SQLException;

    protected abstract void dbShiftDown(IIDHandler idHandler, int offset, int startIndex, int endIndex) throws SQLException;

    protected abstract void dbShiftUp(IIDHandler idHandler, int offset, int startIndex, int endIndex) throws SQLException;

    protected static void close(PreparedStatement... stmts)
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
              DBUtil.close(stmt);
            }
          }
        }
        catch (Throwable th)
        {
          if (t == null)
          {
            // Remember first exception
            t = th;
          }

          // More exceptions go to the log
          OM.LOG.error(t);
        }
      }

      if (t != null)
      {
        throw new DBException(t);
      }
    }

    protected abstract ITypeMapping getTypeMapping();

    protected abstract int getCurrentIndexOffset();

    protected abstract void clearList();

    /**
     * @author Eike Stepper
     */
    public static final class NewListSizeResult extends RuntimeException
    {
      private static final long serialVersionUID = 1L;

      private final int newListSize;

      public NewListSizeResult(int newListSize)
      {
        this.newListSize = newListSize;
      }

      public int getNewListSize()
      {
        return newListSize;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Manipulation
    {
      private static final Object NIL = new Object()
      {
        @Override
        public String toString()
        {
          return "NIL";
        }
      };

      public int types;

      public int sourceIndex;

      public int temporaryIndex;

      public int targetIndex;

      public Object value;

      public Manipulation(int types, int sourceIndex, int targetIndex, Object value)
      {
        this.types = types;
        this.sourceIndex = sourceIndex;
        temporaryIndex = NO_INDEX;
        this.targetIndex = targetIndex;
        this.value = value;
      }

      public boolean is(int type)
      {
        return type == NONE ? types == NONE : (types & type) != 0;
      }

      public void addType(int type)
      {
        types |= type;
      }

      @Override
      public String toString()
      {
        return MessageFormat.format("Manipulation[types={0}, srcIndex={1}, tmpIndex={2}, dstIndex={3}, value={4}]", formatTypes(types),
            formatIndex(sourceIndex), formatIndex(temporaryIndex), formatIndex(targetIndex), String.valueOf(value));
      }

      /**
       * Create a Manipulation which represents an element which already is in the list.
       */
      public static Manipulation createOriginalElement(int index)
      {
        return new Manipulation(NONE, index, index, NIL);
      }

      /**
       * Create a Manipulation which represents an element which is inserted in the list.
       */
      public static Manipulation createInsertedElement(int index, Object value)
      {
        return new Manipulation(INSERT, NO_INDEX, index, value);
      }

      private static String formatTypes(int types)
      {
        StringBuilder builder = new StringBuilder();
        formatType(types, DELETE, "DELETE", builder);
        formatType(types, INSERT, "INSERT", builder);
        formatType(types, MOVE, "MOVE", builder);
        formatType(types, SET, "SET", builder);

        if (builder.length() != 0)
        {
          return builder.toString();
        }

        return "NONE";
      }

      private static void formatType(int types, int type, String label, StringBuilder builder)
      {
        if ((types & type) != 0)
        {
          StringUtil.appendSeparator(builder, '|');
          builder.append(label);
        }
      }

      private static String formatIndex(int index)
      {
        if (index == NO_INDEX)
        {
          return "NONE";
        }

        return Integer.toString(index);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Shift
    {
      public final int startIndex;

      public final int endIndex;

      public final int offset;

      public Shift(int startIndex, int endIndex, int offset)
      {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.offset = offset;
      }

      @Override
      public String toString()
      {
        return "Shift[" + startIndex + ".." + endIndex + ", offset=" + offset + "]";
      }
    }
  }

  /**
   * Shared delta sequencing for the audit and branching range mappings.
   * Persistence of a single list entry and a shifted range remains mapping-specific.
   *
   * @author Eike Stepper
   */
  protected static abstract class AbstractRangeListDeltaWriter implements CDOFeatureDeltaVisitor
  {
    protected final IDBStoreAccessor accessor;

    protected final CDOID id;

    protected final int oldVersion;

    protected final int newVersion;

    private final ContextTracer tracer;

    private int lastListIndex;

    private int pendingRemovedIndex = -1;

    protected AbstractRangeListDeltaWriter(IDBStoreAccessor accessor, InternalCDORevision originalRevision, int oldVersion, int newVersion,
        ContextTracer tracer)
    {
      this.accessor = accessor;
      id = originalRevision.getID();
      this.oldVersion = oldVersion;
      this.newVersion = newVersion;
      this.tracer = tracer;
      lastListIndex = getOldListSize(originalRevision) - 1;
    }

    @Override
    public final void visit(CDOMoveFeatureDelta delta)
    {
      int sourceIndex = delta.getOldPosition();
      int targetIndex = delta.getNewPosition();
      boolean optimizeMove = pendingRemovedIndex != -1 && sourceIndex == lastListIndex - 1 && targetIndex == pendingRemovedIndex;

      if (tracer.isEnabled())
      {
        tracer.format("Delta Moving: {0} to {1}", sourceIndex, targetIndex); //$NON-NLS-1$
      }

      if (optimizeMove)
      {
        ++sourceIndex;
      }
      else
      {
        finishPendingRemove();
      }

      Object value = getMoveValue(delta, sourceIndex);
      removeEntry(sourceIndex);

      if (!optimizeMove)
      {
        if (sourceIndex < targetIndex)
        {
          moveOneUp(sourceIndex + 1, targetIndex);
        }
        else
        {
          moveOneDown(targetIndex, sourceIndex - 1);
        }
      }
      else
      {
        pendingRemovedIndex = -1;
        --lastListIndex;
      }

      addEntry(targetIndex, value);
    }

    @Override
    public final void visit(CDOAddFeatureDelta delta)
    {
      finishPendingRemove();
      updateLogicalList(delta);
      int index = delta.getIndex();
      if (tracer.isEnabled())
      {
        tracer.format("Delta Adding at: {0}", index); //$NON-NLS-1$
      }

      if (index <= lastListIndex)
      {
        moveOneDown(index, lastListIndex);
      }

      addEntry(index, delta.getValue());
      ++lastListIndex;
    }

    @Override
    public final void visit(CDORemoveFeatureDelta delta)
    {
      finishPendingRemove();
      updateLogicalList(delta);
      pendingRemovedIndex = delta.getIndex();

      if (tracer.isEnabled())
      {
        tracer.format("Delta Removing at: {0}", pendingRemovedIndex); //$NON-NLS-1$
      }

      removeEntry(pendingRemovedIndex);
    }

    @Override
    public final void visit(CDOSetFeatureDelta delta)
    {
      finishPendingRemove();
      updateLogicalList(delta);
      int index = delta.getIndex();

      if (tracer.isEnabled())
      {
        tracer.format("Delta Setting at: {0}", index); //$NON-NLS-1$
      }

      removeEntry(index);
      addEntry(index, delta.getValue());
    }

    @Override
    public final void visit(CDOUnsetFeatureDelta delta)
    {
      if (tracer.isEnabled())
      {
        tracer.format("Delta Unsetting"); //$NON-NLS-1$
      }

      updateLogicalList(delta);
      clearList();
      resetIndexes();
    }

    @Override
    public final void visit(CDOClearFeatureDelta delta)
    {
      if (tracer.isEnabled())
      {
        tracer.format("Delta Clearing"); //$NON-NLS-1$
      }

      updateLogicalList(delta);
      clearList();
      resetIndexes();
    }

    @Override
    public final void visit(CDOListFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    @Override
    public final void visit(CDOContainerFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    protected final void finishPendingRemove()
    {
      if (pendingRemovedIndex != -1)
      {
        moveOneUp(pendingRemovedIndex + 1, lastListIndex);
        --lastListIndex;
        pendingRemovedIndex = -1;
      }
    }

    protected abstract Object getValue(int index);

    /**
     * Resolves the value that is reinserted for a move. Subclasses that can track logical element identities can
     * override this without changing the shared physical range sequencing.
     */
    protected Object getMoveValue(CDOMoveFeatureDelta delta, int sourceIndex)
    {
      return getValue(sourceIndex);
    }

    /**
     * Updates an optional logical-list representation before the corresponding persistence operation is performed.
     */
    protected void updateLogicalList(CDOFeatureDelta delta)
    {
    }

    protected abstract int getOldListSize(InternalCDORevision originalRevision);

    protected abstract void removeEntry(int index);

    protected abstract void addEntry(int index, Object value);

    protected abstract void clearList();

    protected abstract void moveOneUp(int startIndex, int endIndex);

    protected abstract void moveOneDown(int startIndex, int endIndex);

    protected final int getLastListIndex()
    {
      return lastListIndex;
    }

    private void resetIndexes()
    {
      lastListIndex = -1;
      pendingRemovedIndex = -1;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ListLobRefsUpdater extends ILobRefsUpdater
  {
    @Override
    public default void updateLobRefs(Connection connection) throws LobRefsUpdateNotSupportedException
    {
      ITypeMapping typeMapping = getTypeMapping();
      if (typeMapping instanceof ILobRefsUpdater)
      {
        ((ILobRefsUpdater)typeMapping).updateLobRefs(connection);
      }
      else
      {
        throw new LobRefsUpdateNotSupportedException();
      }
    }

    public ITypeMapping getTypeMapping();
  }
}
