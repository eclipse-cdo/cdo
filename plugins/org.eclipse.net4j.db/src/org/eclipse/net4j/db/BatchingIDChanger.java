/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.IDChanger;
import org.eclipse.net4j.util.IDChanger.ChangeHandler;
import org.eclipse.net4j.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A {@link ChangeHandler} that batches SQL update statements to change IDs in a database table.
 * <p>
 * The class uses a {@link StatementBatcher} to accumulate SQL statements for execution.
 * <p>
 * This class is expected to be used with {@link IDChanger#changeIDs(Map, ChangeHandler)} to perform ID changes in bulk.
 * Here is an example of how to use it:
 * <pre>
 * Map<Integer, Integer> idChanges = ...; // Map of old IDs to new IDs.
 *
 * // Create a StatementBatcher with a try-with-resources statement.
 * // The final execution of the batch will occur when the batcher is closed.
 * try (StatementBatcher batcher = new StatementBatcher(connection))
 * {
 *   IDBField idField = ...; // Obtain the ID field from your database schema.
 *   BatchingIDChanger handler = new BatchingIDChanger(batcher, idField);
 *
 *   // Perform the ID changes using the handler.
 *   IDChanger.changeIDs(idChanges, handler);
 * }
 *
 * @author Eike Stepper
 * @since 4.13
 */
public class BatchingIDChanger implements ChangeHandler
{
  protected final StatementBatcher batcher;

  protected final IDBField idField;

  protected final IDBTable table;

  public BatchingIDChanger(StatementBatcher batcher, IDBField idField)
  {
    this(batcher, idField, idField == null ? null : idField.getTable());
  }

  public BatchingIDChanger(StatementBatcher batcher, IDBField idField, IDBTable table)
  {
    this.batcher = Objects.requireNonNull(batcher);
    this.idField = Objects.requireNonNull(idField);
    this.table = Objects.requireNonNull(table);
  }

  @Override
  public void handleSingleChange(int oldID, int newID)
  {
    batch(newID, idField + "=" + oldID);
  }

  @Override
  public void handleConsecutiveChanges(int firstID, int lastID, int shift)
  {
    batch(idField + shiftStr(shift), idField + " BETWEEN " + firstID + " AND " + lastID);
  }

  @Override
  public void handleArbitraryChanges(List<Integer> affectedIDs, int shift)
  {
    String ids = affectedIDs.stream().map(String::valueOf).collect(Collectors.joining(","));
    batch(idField + shiftStr(shift), idField + " IN (" + ids + ")");
  }

  protected String sql(Object newValue, String condition)
  {
    return "UPDATE " + table + " SET " + idField + "=" + newValue + " WHERE " + condition;
  }

  protected final void batch(Object newValueExpression, String whereCondition)
  {
    String sql = sql(newValueExpression, whereCondition);
    if (!StringUtil.isEmpty(sql))
    {
      batcher.batch(sql);
    }
  }

  protected final String shiftStr(int shift)
  {
    return shift >= 0 ? "+" + shift : String.valueOf(shift);
  }
}
