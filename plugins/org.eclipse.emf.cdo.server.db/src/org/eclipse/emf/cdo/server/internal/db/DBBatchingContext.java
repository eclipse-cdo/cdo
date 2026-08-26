/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the pending JDBC batches of one DBStoreAccessor commit write.
 *
 * @author Eike Stepper
 */
public final class DBBatchingContext
{
  public static final int DEFAULT_STATEMENT_BATCH_SIZE = 1000;

  public static final int DEFAULT_COMMIT_BATCH_SIZE = 10000;

  private final int statementBatchSize;

  private final int commitBatchSize;

  private final Map<BatchedStatement, Integer> statements = new IdentityHashMap<>();

  private final List<BatchedStatement> statementOrder = new ArrayList<>();

  private int batchExecutionCount;

  private int capacityFlushCount;

  private int orderingFlushCount;

  private int phaseFlushCount;

  private int finalFlushCount;

  public DBBatchingContext(int statementBatchSize, int commitBatchSize)
  {
    this.statementBatchSize = statementBatchSize;
    this.commitBatchSize = commitBatchSize;
  }

  public int getStatementBatchSize()
  {
    return statementBatchSize;
  }

  public int getCommitBatchSize()
  {
    return commitBatchSize;
  }

  public void manage(BatchedStatement statement)
  {
    if (statements.putIfAbsent(statement, statement.getExecutionCount()) == null)
    {
      statementOrder.add(statement);
    }
  }

  public void afterAdd(BatchedStatement statement)
  {
    manage(statement);
    if (recordExecutions(statement) != 0)
    {
      ++capacityFlushCount;
    }

    enforceCommitCapacity();
  }

  public void flush(BatchedStatement statement)
  {
    try
    {
      statement.flush();
      if (recordExecutions(statement) != 0)
      {
        ++orderingFlushCount;
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void flushPhase()
  {
    flushAll();
    ++phaseFlushCount;
  }

  public void flushFinal()
  {
    flushAll();
    ++finalFlushCount;
  }

  public int getPendingCount()
  {
    int pending = 0;
    for (BatchedStatement statement : statementOrder)
    {
      pending += statement.getPendingCount();
    }

    return pending;
  }

  public int getBatchExecutionCount()
  {
    return batchExecutionCount;
  }

  public int getCapacityFlushCount()
  {
    return capacityFlushCount;
  }

  public int getOrderingFlushCount()
  {
    return orderingFlushCount;
  }

  public int getPhaseFlushCount()
  {
    return phaseFlushCount;
  }

  public int getFinalFlushCount()
  {
    return finalFlushCount;
  }

  public void release(BatchedStatement statement)
  {
    recordExecutions(statement);
    statements.remove(statement);
    removeFromOrder(statement);
    DBUtil.close(statement);
  }

  public void discard(BatchedStatement statement)
  {
    statements.remove(statement);
    removeFromOrder(statement);
    try
    {
      statement.clearBatch();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(statement);
    }
  }

  public void close()
  {
    boolean flushed = false;
    try
    {
      flushFinal();
      flushed = true;
    }
    finally
    {
      for (BatchedStatement statement : statementOrder)
      {
        if (!flushed)
        {
          try
          {
            statement.clearBatch();
          }
          catch (SQLException ex)
          {
            // The failed flush is the relevant exception. Do not let close() retry pending work.
          }
        }

        DBUtil.close(statement);
      }

      statements.clear();
      statementOrder.clear();
    }
  }

  public void discard()
  {
    for (BatchedStatement statement : statementOrder)
    {
      try
      {
        statement.clearBatch();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        DBUtil.close(statement);
      }
    }

    statements.clear();
    statementOrder.clear();
  }

  private void enforceCommitCapacity()
  {
    if (commitBatchSize <= 0)
    {
      return;
    }

    while (getPendingCount() >= commitBatchSize)
    {
      BatchedStatement largest = null;
      int largestPending = 0;
      for (BatchedStatement statement : statementOrder)
      {
        int pending = statement.getPendingCount();
        if (pending > largestPending)
        {
          largest = statement;
          largestPending = pending;
        }
      }

      if (largest == null)
      {
        return;
      }

      try
      {
        largest.flush();
        if (recordExecutions(largest) != 0)
        {
          ++capacityFlushCount;
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
  }

  private void flushAll()
  {
    for (BatchedStatement statement : statementOrder)
    {
      try
      {
        statement.flush();
        recordExecutions(statement);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
  }

  private int recordExecutions(BatchedStatement statement)
  {
    Integer previous = statements.put(statement, statement.getExecutionCount());
    if (previous != null)
    {
      int executions = statement.getExecutionCount() - previous;
      batchExecutionCount += executions;
      return executions;
    }

    return 0;
  }

  private void removeFromOrder(BatchedStatement statement)
  {
    for (int i = 0; i < statementOrder.size(); i++)
    {
      if (statementOrder.get(i) == statement)
      {
        statementOrder.remove(i);
        return;
      }
    }
  }
}
