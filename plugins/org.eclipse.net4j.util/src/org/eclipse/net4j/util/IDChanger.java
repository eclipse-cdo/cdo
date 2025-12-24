/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.om.OMPlatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A utility class for changing IDs according to a set of changes.
 * <p>
 * The changes are applied by calling a handler for single changes, consecutive changes, and arbitrary changes.
 *
 * @see #changeIDs(Map, ChangeHandler)
 * @author Eike Stepper
 * @since 3.29
 */
public final class IDChanger
{
  private static final int NO_INITIAL_TEMP_ID = -1;

  private static final int INITIAL_TEMP_ID = OMPlatform.INSTANCE.getProperty( //
      "org.eclipse.net4j.util.IDChanger.INITIAL_TEMP_ID", NO_INITIAL_TEMP_ID);

  private IDChanger()
  {
    // No instances.
  }

  /**
   * Changes IDs according to the given map of changes.
   * <p>
   * The map's keys are the old IDs, and the values are the new IDs.
   * <p>
   * If there are conflicts among the changes (i.e., an old ID is changed to a new ID that is also an old ID),
   * temporary IDs are introduced to resolve the conflicts.
   * <p>
   * The changes are applied by calling the given handler.
   * The algorithm tries to group changes into single changes, consecutive changes, and arbitrary changes
   * to minimize the number of calls to the handler.
   */
  public static void changeIDs(Map<Integer, Integer> changes, ChangeHandler handler)
  {
    changeIDs(changes, NO_INITIAL_TEMP_ID, handler);
  }

  /**
   * Changes IDs according to the given map of changes.
   * <p>
   * The map's keys are the old IDs, and the values are the new IDs.
   * <p>
   * If there are conflicts among the changes (i.e., an old ID is changed to a new ID that is also an old ID),
   * temporary IDs are introduced to resolve the conflicts.
   * <p>
   * The changes are applied by calling the given handler.
   * The algorithm tries to group changes into single changes, consecutive changes, and arbitrary changes
   * to minimize the number of calls to the handler.
   */
  public static void changeIDs(Map<Integer, Integer> changes, int initialTempID, ChangeHandler handler)
  {
    if (ObjectUtil.isEmpty(changes))
    {
      // No changes to apply.
      return;
    }

    // Create shift operations.
    Map<Integer, ShiftOperation> operationsMap = new HashMap<>();

    // Group changes by their shift amount.
    changes.forEach((oldID, newID) -> operationsMap.computeIfAbsent(newID - oldID, ShiftOperation::new).addAffectedID(oldID));

    // Sort shift operations by their size (number of affected IDs), smallest first.
    List<ShiftOperation> operations = operationsMap.values().stream() //
        .filter(ShiftOperation::isRequired) //
        .sorted() //
        .collect(Collectors.toCollection(ArrayList::new));

    // Finalize shift operations (by sorting their affected IDs), smallest first.
    operations.forEach(ShiftOperation::finishAffectedIDs);

    // Resolve conflicts among shift operations.
    // A conflict occurs when a shift operation affects an ID that is the target of a later shift operation.
    // In that case, we need to introduce a temporary ID to break the conflict.

    // We use temporary IDs starting from initialTempID and counting upwards.
    if (initialTempID == NO_INITIAL_TEMP_ID)
    {
      // The user did not provide an initial temporary ID, so we check the system property.
      initialTempID = INITIAL_TEMP_ID;

      if (initialTempID == NO_INITIAL_TEMP_ID)
      {
        // The system property is not set, so we choose a default initial temporary ID
        // with a value of Integer.MAX_VALUE - changes.size().
        initialTempID = Integer.MAX_VALUE - changes.size();
      }
    }

    // Next temporary ID to use.
    AtomicInteger nextTempID = new AtomicInteger(initialTempID);

    // Lists to hold new operations introduced to resolve conflicts.
    // These will be added before and after the main operations.
    List<ShiftOperation> preTempOperations = new ArrayList<>();
    List<ShiftOperation> postTempOperations = new ArrayList<>();

    // Iterate over shift operations, smallest first.
    int size = operations.size();
    int i = 0;

    // For each shift operation, check for conflicts with later shift operations.
    while (i < size - 1)
    {
      // Current shift operation.
      ShiftOperation operation = operations.get(i);

      // Determine whether this shift operation conflicts with any later shift operation.
      for (ShiftOperation conflictCandidate : operations.subList(i + 1, size))
      {
        // Check all affected IDs of the conflict candidate.
        conflictCandidate.forEachAffectedID(affectedID -> //
        {
          // Determine the new ID of the affected ID.
          int newID = affectedID + conflictCandidate.getShift();

          // Check whether the new ID is affected by the current shift operation.
          if (operation.affectedIDs.contains(newID))
          {
            // Conflict detected.
            // Resolve the conflict by introducing a temporary ID.
            int tempID = nextTempID.getAndIncrement();

            // Calculate the final ID after applying the current operation's shift.
            // Must come before setShift() below.
            int finalID = newID + operation.getShift();

            // Update the current operation to no longer affect the new ID.
            if (operation.isSingleton())
            {
              // Directly change the operation to use the temporary ID.
              operation.setShift(tempID - newID);
            }
            else
            {
              // Remove the conflicting ID from the current operation.
              operation.removeAffectedID(newID);

              // Create a new operation for the shift to the temporary ID.
              ShiftOperation preTempOperation = new ShiftOperation(newID, tempID);

              // Add the new operation to the list of pre-temp operations.
              // This ensures it is applied before the conflicting operation.
              preTempOperations.add(preTempOperation);
            }

            // Create a new operation for the shift to the final ID.
            ShiftOperation postTempOperation = new ShiftOperation(tempID, finalID);

            // Add the new operation to the list of post-temp operations.
            // This ensures it is applied after the conflicting operation.
            postTempOperations.add(postTempOperation);
          }
        });
      }

      if (operation.isEmpty())
      {
        // Remove the current operation if it has become empty.
        operations.remove(i);
        size--;
      }
      else
      {
        // Move to the next operation.
        ++i;
      }
    }

    // Combine all operations: pre-temp, main, post-temp.
    operations.addAll(0, preTempOperations);
    operations.addAll(postTempOperations);

    // If any conflicts were resolved, we may have created operations with the same shift amount.
    boolean conflictsResolved = !postTempOperations.isEmpty();
    if (conflictsResolved)
    {
      // Merge operations with the same shift amount into a single map.
      // This can happen because the conflict resolution above creates new single operations.

      // Set of merged operations. They will be re-finalized later.
      Set<ShiftOperation> mergedOperations = new HashSet<>();

      // Map of shift amount to shift operation. Linked map to preserve order.
      Map<Integer, ShiftOperation> mergeMap = new LinkedHashMap<>();

      // Merge operations into the map.
      operations.forEach(op -> mergeMap.merge(op.getShift(), op, (op1, op2) -> {
        op2.forEachAffectedID(op1::addAffectedID);
        mergedOperations.add(op2);
        return op1;
      }));

      // Re-finalize merged operations (by sorting their affected IDs).
      mergedOperations.forEach(ShiftOperation::finishAffectedIDs);

      // Use merged operations as the new list of operations.
      operations = new ArrayList<>(mergeMap.values());
    }

    // Apply merged operations by calling the handler.
    operations.forEach(op -> op.handle(handler));
  }

  /**
   * A handler for ID changes.
   * <p>
   * The handler is called for single changes, consecutive changes, and arbitrary changes.
   *
   * @author Eike Stepper
   */
  public interface ChangeHandler
  {
    /**
     * Handles a single ID change from oldID to newID.
     */
    public default void handleSingleChange(int oldID, int newID)
    {
      handleArbitraryChanges(Collections.singletonList(oldID), newID - oldID);
    }

    /**
     * Handles consecutive ID changes from firstID to lastID, shifted by the given amount.
     */
    public default void handleConsecutiveChanges(int firstID, int lastID, int shift)
    {
      handleArbitraryChanges(IntStream.rangeClosed(firstID, lastID).boxed().collect(Collectors.toList()), shift);
    }

    /**
     * Handles arbitrary ID changes for the given list of affected IDs, shifted by the given amount.
     */
    public void handleArbitraryChanges(List<Integer> affectedIDs, int shift);
  }

  /**
   * An operation that shifts a set of IDs by a constant value.
   * <p>
   * The affected IDs are those that need to be changed by the same amount.
   * The resulting changes are free of conflicts among each other.
   * But a shift operation may conflict with other shift operations.
   *
   * @author Eike Stepper
   */
  private static final class ShiftOperation implements Comparable<ShiftOperation>
  {
    private final List<Integer> affectedIDs = new ArrayList<>(1);

    private int shift;

    /**
     * Creates a new shift operation with the given shift amount.
     * Initially, no affected IDs are set.
     */
    public ShiftOperation(int shift)
    {
      this.shift = shift;
    }

    /**
     * Applies the given consumer to each affected ID.
     */
    public void forEachAffectedID(IntConsumer consumer)
    {
      for (int affectedID : affectedIDs)
      {
        consumer.accept(affectedID);
      }
    }

    /**
     * Creates a new shift operation for the given old and new ID.
     * The shift amount is calculated as newID - oldID.
     * The old ID is added as the only affected ID.
     */
    public ShiftOperation(int oldID, int newID)
    {
      shift = newID - oldID;
      addAffectedID(oldID);
    }

    /**
     * Adds an affected ID to this shift operation.
     */
    public void addAffectedID(int id)
    {
      affectedIDs.add(id);
    }

    /**
     * Removes an affected ID from this shift operation.
     */
    public void removeAffectedID(int id)
    {
      affectedIDs.remove((Integer)id);
    }

    /**
     * Finalizes the affected IDs by sorting them in ascending order.
     */
    public void finishAffectedIDs()
    {
      affectedIDs.sort(null);
    }

    /**
     * Returns whether this shift operation has no affected IDs.
     */
    public boolean isEmpty()
    {
      return affectedIDs.isEmpty();
    }

    /**
     * Returns whether this shift operation affects exactly one ID.
     */
    public boolean isSingleton()
    {
      return size() == 1;
    }

    /**
     * Returns whether this shift operation is a noop (i.e., has a shift amount of zero).
     */
    public boolean isRequired()
    {
      return shift != 0;
    }

    /**
     * Returns the shift amount of this operation.
     */
    public int getShift()
    {
      return shift;
    }

    /**
     * Sets the shift amount of this operation.
     */
    public void setShift(int shift)
    {
      this.shift = shift;
    }

    /**
     * Handles this shift operation by calling the given handler.
     * <p>
     * If the operation is a singleton, the handler's handleSingleChange() method is called.
     * If the operation is consecutive, the handler's handleConsecutiveChanges() method is called.
     * Otherwise, the handler's handleArbitraryChanges() method is called.
     */
    public void handle(ChangeHandler handler)
    {
      if (isSingleton())
      {
        int oldID = first();
        int newID = oldID + shift;
        handler.handleSingleChange(oldID, newID);
      }
      else if (isConsecutive())
      {
        int firstID = first();
        int lastID = last();
        handler.handleConsecutiveChanges(firstID, lastID, shift);
      }
      else
      {
        handler.handleArbitraryChanges(affectedIDs, shift);
      }
    }

    /**
     * Compares this shift operation to another based on the number of affected IDs.
     * Smaller operations (fewer affected IDs) are considered "less than" larger ones.
     * <p>
     * This ordering is used to process smaller operations first when resolving conflicts.
     * The advantage is that smaller operations are more likely to be broken up into single changes.
     * Which leads to larger operations staying intact and causing fewer handler calls.
     */
    @Override
    public int compareTo(ShiftOperation o)
    {
      return Integer.compare(size(), o.size());
    }

    /**
     * Returns a string representation of this shift operation for debugging purposes.
     * <p>
     * Includes the shift amount, affected IDs, and resulting new IDs.
     */
    @Override
    public String toString()
    {
      return "ShiftOperation[shift=" + shift + ", affectedIDs=" + affectedIDs + ", newIDs=" + newIDs() + "]";
    }

    /**
     * Returns the list of new IDs resulting from applying this shift operation to its affected IDs.
     * <p>
     * Only used in toString() for debugging purposes.
     */
    private List<Integer> newIDs()
    {
      List<Integer> newIDs = new ArrayList<>(size());

      for (int affectedID : affectedIDs)
      {
        newIDs.add(affectedID + shift);
      }

      return newIDs;
    }

    /**
     * Returns whether the affected IDs are consecutive.
     * <p>
     * This is used to determine whether the handler's handleConsecutiveChanges() method can be called.
     * <p>
     * An affected ID list is considered consecutive if the difference between the last and first ID
     * equals the size of the list minus one.
     */
    private boolean isConsecutive()
    {
      return last() - first() + 1 == size();
    }

    private int first()
    {
      return affectedIDs.get(0);
    }

    private int last()
    {
      return affectedIDs.get(size() - 1);
    }

    private int size()
    {
      return affectedIDs.size();
    }
  }
}
