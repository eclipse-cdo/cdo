/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.db.IBatchingContext;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.util.om.OMPlatform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the pending JDBC batches of one DBStoreAccessor commit write.
 * <p>
 * A context is deliberately scoped to one commit write. Mapping code registers each {@link BatchedStatement} with
 * {@link #manageStatement(BatchedStatement)} and submits parameter sets through the statement's normal update API. The context
 * observes automatic statement executions, applies the commit-wide pending limit, and provides explicit flush points
 * for ordering and semantic phase boundaries.
 * <p>
 * Statements are tracked by identity, not by SQL text. This allows independently configured statements with equal SQL
 * to remain separate while preserving their registration order for deterministic phase flushing. The context does not
 * own transaction commit or rollback; it only owns statement batching and statement cleanup.
 * <p>
 * A context is intended for use by the owning DB store write thread. It is not thread-safe; callers must serialize
 * registration, submission, flushing, and cleanup.
 *
 * @author Eike Stepper
 */
public final class BatchingContext implements IBatchingContext
{
  /**
   * The default maximum number of pending parameter sets per statement.
   */
  public static final int DEFAULT_STATEMENT_BATCH_SIZE = 1000;

  /**
   * The default maximum number of pending parameter sets across all managed statements.
   */
  public static final int DEFAULT_COMMIT_BATCH_SIZE = 10000;

  /**
   * Enables diagnostic batching statistics and one summary log message per successfully closed commit context.
   */
  private static final boolean STATISTICS_ENABLED = OMPlatform.INSTANCE //
      .isProperty("org.eclipse.emf.cdo.server.db.DBBatchingContext.ENABLE_STATISTICS");

  private static boolean statisticsEnabled = STATISTICS_ENABLED;

  private final IDBConnection connection;

  private final int statementBatchSize;

  private final int commitBatchSize;

  private final Map<BatchedStatement, Integer> statements = new IdentityHashMap<>();

  private final List<BatchedStatement> statementOrder = new ArrayList<>();

  private final Map<BatchedStatement, StatementStatistics> statistics = new IdentityHashMap<>();

  private final List<StatementStatistics> statisticsOrder = new ArrayList<>();

  private final Map<String, Long> diagnosticCounters = new LinkedHashMap<>();

  private int batchExecutionCount;

  private int capacityFlushCount;

  private int orderingFlushCount;

  private int phaseFlushCount;

  private int finalFlushCount;

  private int maximumPendingCount;

  private long totalEntriesAdded;

  /**
   * Creates a context with independent per-statement and commit-wide thresholds.
   *
   * @param connection
   *          the JDBC connection used to create statements,
   *          or {@code null} if statement creation is not needed.
   * @param statementBatchSize
   *          the threshold passed to each {@link BatchedStatement}
   * @param commitBatchSize
   *          the total pending threshold across all managed statements
   */
  public BatchingContext(IDBConnection connection, int statementBatchSize, int commitBatchSize)
  {
    this.connection = connection;
    this.statementBatchSize = statementBatchSize;
    this.commitBatchSize = commitBatchSize;
  }

  /**
   * Returns the per-statement threshold used by the mapping code when creating batched statements.
   *
   * @return the maximum pending parameter sets for one statement; a non-positive value disables automatic
   *         statement-level execution in the wrapped statement
   */
  public int getStatementBatchSize()
  {
    return statementBatchSize;
  }

  /**
   * Returns the commit-wide pending threshold.
   *
   * @return the maximum pending parameter sets across managed statements; a non-positive value disables the
   *         commit-wide capacity safeguard
   */
  public int getCommitBatchSize()
  {
    return commitBatchSize;
  }

  /**
   * Returns the total number of parameter sets currently waiting in all managed statements.
   *
   * @return the pending parameter-set count
   */
  public int getPendingCount()
  {
    int pending = 0;

    for (BatchedStatement statement : statementOrder)
    {
      pending += statement.getPendingCount();
    }

    return pending;
  }

  /**
   * Returns the number of JDBC batch executions observed by this context.
   * <p>
   * This counts executions caused by automatic statement thresholds as well as explicit capacity, ordering, phase,
   * and final flushes. It is diagnostic information and does not count individual SQL rows.
   *
   * @return the observed JDBC batch execution count
   */
  public int getBatchExecutionCount()
  {
    return batchExecutionCount;
  }

  /**
   * Returns the number of executions observed while accounting for submissions and enforcing capacity.
   * <p>
   * This includes automatic executions caused by a statement's own threshold as well as executions caused by the
   * commit-wide capacity safeguard.
   *
   * @return the capacity/accounting-triggered execution count
   */
  public int getCapacityFlushCount()
  {
    return capacityFlushCount;
  }

  /**
   * @return the number of explicit single-statement ordering flushes that executed work
   */
  public int getOrderingFlushCount()
  {
    return orderingFlushCount;
  }

  /**
   * @return the number of semantic phase flushes requested
   */
  public int getPhaseFlushCount()
  {
    return phaseFlushCount;
  }

  /**
   * @return the number of final flushes requested
   */
  public int getFinalFlushCount()
  {
    return finalFlushCount;
  }

  @Override
  public BatchedStatement createStatement(String sql, ReuseProbability reuseProbability, String diagnosticName)
  {
    IDBPreparedStatement statement = connection.prepareStatement(sql, reuseProbability);
    return DBUtil.batched(statement, statementBatchSize, this, diagnosticName);
  }

  /**
   * Registers a statement for accounting and phase flushing.
   * <p>
   * Registration is idempotent by object identity. The first registration also establishes the statement's position
   * in the deterministic flush order. A statement should be registered before its first call to
   * {@link BatchedStatement#executeUpdate()}.
   *
   * @param statement
   *          the statement owned by this context
   */
  @Override
  public void manageStatement(BatchedStatement statement)
  {
    manageStatement(statement, null);
  }

  /**
   * Registers a statement with optional diagnostic metadata.
   * <p>
   * The name is never used for statement identity, ordering, or scheduling. It is only included in the optional
   * commit summary when {@link #statisticsEnabled} is enabled.
   *
   * @param statement
   *          the statement owned by this context
   * @param diagnosticName
   *          a short diagnostic name, or {@code null} for an unnamed statement
   */
  @Override
  public void manageStatement(BatchedStatement statement, String diagnosticName)
  {
    if (statements.putIfAbsent(statement, statement.getExecutionCount()) == null)
    {
      statementOrder.add(statement);

      if (statisticsEnabled)
      {
        statistics.put(statement, new StatementStatistics(diagnosticName));
        statisticsOrder.add(statistics.get(statement));
      }
    }
  }

  /**
   * Records that a parameter set was submitted to a statement and enforces the commit-wide capacity limit.
   * <p>
   * The wrapped statement may already have executed automatically because its own threshold was reached. Such
   * executions are accounted for before the global limit is evaluated. If the global limit is reached, the currently
   * largest pending statement is flushed; ties are resolved by registration order. Executions caused by the
   * statement threshold are also recorded at this point because they become visible when the submission is accounted
   * for.
   *
   * @param statement
   *          the statement to account for
   */
  @Override
  public void afterExecuteUpdate(BatchedStatement statement)
  {
    manageStatement(statement);

    if (statisticsEnabled)
    {
      ++totalEntriesAdded;
      statistics.get(statement).entriesAdded++;
    }

    if (recordExecutions(statement) != 0)
    {
      ++capacityFlushCount;

      if (statisticsEnabled)
      {
        statistics.get(statement).capacityFlushes++;
      }
    }

    updateMaximumPendingCount();
    enforceCommitCapacity();
  }

  /**
   * Increments an optional mapping-specific diagnostic counter.
   * <p>
   * Counter names are metadata only and do not participate in batching decisions. Calls are ignored when statistics
   * are disabled.
   *
   * @param name
   *          the short counter name
   */
  @Override
  public void recordDiagnosticCounter(String name)
  {
    recordDiagnosticCounter(name, 1);
  }

  /**
   * Adds a value to an optional mapping-specific diagnostic counter.
   * <p>
   * Counter names are metadata only and do not participate in batching decisions. Calls are ignored when statistics
   * are disabled.
   *
   * @param name
   *          the short counter name
   * @param value
   *          the value to add
   */
  @Override
  public void recordDiagnosticCounter(String name, long value)
  {
    if (statisticsEnabled)
    {
      diagnosticCounters.merge(name, value, Long::sum);
    }
  }

  /**
   * Flushes one statement at an explicit ordering barrier.
   * <p>
   * This is used when a later DML operation must observe the effects of the selected statement. Only the supplied
   * statement is flushed. The execution is counted as an ordering flush if it executes at least one JDBC batch.
   *
   * @param statement
   *          the statement to flush; it must have been registered with this context
   * @throws DBException
   *           if JDBC execution fails
   */
  @Override
  public void flushStatement(BatchedStatement statement)
  {
    try
    {
      statement.flush();

      int executions = recordExecutions(statement);
      if (executions != 0)
      {
        ++orderingFlushCount;

        if (statisticsEnabled)
        {
          statistics.get(statement).orderingFlushes++;
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * Flushes all managed statements in registration order at a semantic phase boundary.
   * <p>
   * This method does not release statements. They can be reused in a later phase or explicitly released afterwards.
   */
  @Override
  public void flushPhase()
  {
    flushAll(FlushReason.PHASE);
    ++phaseFlushCount;
  }

  /**
   * Flushes all managed statements in registration order at the end of a commit write.
   * <p>
   * This method does not release statements. Use {@link #close()} when the whole context is no longer needed.
   */
  public void flushFinal()
  {
    flushAll(FlushReason.FINAL);
    ++finalFlushCount;
  }

  /**
   * Releases one successfully managed statement.
   * <p>
   * Any pending work is expected to have been flushed before release. The statement is removed from all context
   * accounting and then closed. Releasing does not trigger a flush itself.
   *
   * @param statement
   *          the statement to remove and close
   */
  @Override
  public void releaseStatement(BatchedStatement statement)
  {
    recordExecutions(statement);
    statements.remove(statement);
    removeFromOrder(statement);

    if (statisticsEnabled)
    {
      statistics.remove(statement);
    }

    DBUtil.close(statement);
  }

  /**
   * Discards one managed statement without executing pending work.
   * <p>
   * Pending parameter sets are cleared, the statement is removed from the context, and the statement is closed.
   *
   * @param statement
   *          the statement to discard; it may already have been removed from the context
   * @throws DBException
   *           if clearing the JDBC batch fails
   */
  @Override
  public void discardStatement(BatchedStatement statement)
  {
    statements.remove(statement);
    removeFromOrder(statement);

    if (statisticsEnabled)
    {
      statistics.remove(statement);
    }

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

  /**
   * Discards and closes all statements managed by this context without executing pending work.
   * <p>
   * The context is empty when this method returns or throws.
   */
  public void discard()
  {
    DBException failure = null;
    List<BatchedStatement> statementsToDiscard = new ArrayList<>(statementOrder);

    try
    {
      for (BatchedStatement statement : statementsToDiscard)
      {
        try
        {
          statement.clearBatch();
        }
        catch (SQLException ex)
        {
          failure = addCleanupFailure(failure, ex);
        }

        try
        {
          Exception closeFailure = DBUtil.close(statement);
          if (closeFailure != null)
          {
            failure = addCleanupFailure(failure, closeFailure);
          }
        }
        catch (RuntimeException ex)
        {
          failure = addCleanupFailure(failure, ex);
        }
      }
    }
    finally
    {
      clearState();
    }

    if (failure != null)
    {
      throw failure;
    }
  }

  /**
   * Flushes and closes all statements managed by this context.
   * <p>
   * On a successful flush, all statements are closed after the final flush. If the final flush fails, pending batches
   * are cleared during cleanup so that closing a statement cannot retry the failed work; the original failure remains
   * the relevant exception. The context is empty when this method returns or throws.
   */
  public void close()
  {
    boolean flushed = false;

    try
    {
      flushFinal();
      flushed = true;

      if (statisticsEnabled)
      {
        logStatistics();
      }
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

      if (statisticsEnabled)
      {
        statistics.clear();
        statisticsOrder.clear();
        diagnosticCounters.clear();
      }
    }
  }

  /**
   * Flushes the largest pending statement until the commit-wide threshold is satisfied. Iterating the stable
   * registration list makes equal-size choices deterministic and avoids depending on map iteration order.
   */
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

        int executions = recordExecutions(largest);
        if (executions != 0)
        {
          ++capacityFlushCount;

          if (statisticsEnabled)
          {
            statistics.get(largest).capacityFlushes++;
          }
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
  }

  /**
   * Flushes every currently managed statement in registration order.
   */
  private void flushAll(FlushReason reason)
  {
    for (BatchedStatement statement : statementOrder)
    {
      try
      {
        statement.flush();

        int executions = recordExecutions(statement);
        if (executions != 0 && statisticsEnabled)
        {
          StatementStatistics statementStatistics = statistics.get(statement);

          if (reason == FlushReason.PHASE)
          {
            ++statementStatistics.phaseFlushes;
          }
          else
          {
            ++statementStatistics.finalFlushes;
          }
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
  }

  /**
   * Converts the statement's cumulative execution count into a context-local delta.
   *
   * @return the number of executions observed since the previous accounting point
   */
  private int recordExecutions(BatchedStatement statement)
  {
    Integer previous = statements.put(statement, statement.getExecutionCount());
    if (previous != null)
    {
      int executions = statement.getExecutionCount() - previous;
      batchExecutionCount += executions;

      if (statisticsEnabled)
      {
        statistics.get(statement).executionCount += executions;
      }

      return executions;
    }

    return 0;
  }

  /**
   * Removes a statement by identity from the stable flush-order list.
   */
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

  private void updateMaximumPendingCount()
  {
    if (statisticsEnabled)
    {
      int pending = getPendingCount();
      maximumPendingCount = Math.max(maximumPendingCount, pending);

      for (BatchedStatement statement : statementOrder)
      {
        StatementStatistics statementStatistics = statistics.get(statement);
        statementStatistics.maximumPendingCount = Math.max(statementStatistics.maximumPendingCount, statement.getPendingCount());
      }
    }
  }

  private void logStatistics()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DB batching statistics:\n"); //$NON-NLS-1$
    builder.append("  Entries: ").append(totalEntriesAdded).append('\n'); //$NON-NLS-1$
    builder.append("  Batch executions: ").append(batchExecutionCount).append('\n'); //$NON-NLS-1$
    builder.append("  Max pending: ").append(maximumPendingCount).append('\n'); //$NON-NLS-1$
    builder.append('\n');
    builder.append("  Flushes:\n"); //$NON-NLS-1$
    builder.append("    Capacity: ").append(capacityFlushCount).append('\n'); //$NON-NLS-1$
    builder.append("    Ordering: ").append(orderingFlushCount).append('\n'); //$NON-NLS-1$
    builder.append("    Phase: ").append(phaseFlushCount).append('\n'); //$NON-NLS-1$
    builder.append("    Final: ").append(finalFlushCount).append('\n'); //$NON-NLS-1$

    if (!diagnosticCounters.isEmpty())
    {
      builder.append('\n');
      builder.append("  Counters:\n"); //$NON-NLS-1$

      for (Map.Entry<String, Long> entry : diagnosticCounters.entrySet())
      {
        builder.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append('\n'); //$NON-NLS-1$
      }
    }

    builder.append('\n');
    builder.append("  Statements:\n"); //$NON-NLS-1$

    for (StatementStatistics statementStatistics : statisticsOrder)
    {
      builder.append("    ").append(statementStatistics.name).append(" entries=").append(statementStatistics.entriesAdded) //$NON-NLS-1$
          .append(" executions=").append(statementStatistics.executionCount).append(" avg=") //$NON-NLS-1$
          .append(statementStatistics.getAverageBatchSize()).append(" max=").append(statementStatistics.maximumPendingCount) //$NON-NLS-1$
          .append(" flushes(capacity=").append(statementStatistics.capacityFlushes).append(", ordering=") //$NON-NLS-1$
          .append(statementStatistics.orderingFlushes).append(", phase=").append(statementStatistics.phaseFlushes) //$NON-NLS-1$
          .append(", final=").append(statementStatistics.finalFlushes).append(")\n"); //$NON-NLS-1$
    }

    OM.LOG.info(builder.toString());
  }

  private void clearState()
  {
    statements.clear();
    statementOrder.clear();
    statistics.clear();
    statisticsOrder.clear();
    diagnosticCounters.clear();
  }

  private static DBException addCleanupFailure(DBException first, Exception failure)
  {
    if (first == null)
    {
      return new DBException(failure);
    }

    first.addSuppressed(failure);
    return first;
  }

  public static boolean isStatisticsEnabled()
  {
    return statisticsEnabled;
  }

  public static void setStatisticsEnabled(boolean statisticsEnabled)
  {
    BatchingContext.statisticsEnabled = statisticsEnabled;
  }

  /**
   * @author Eike Stepper
   */
  private enum FlushReason
  {
    PHASE, FINAL
  }

  /**
   * @author Eike Stepper
   */
  private static final class StatementStatistics
  {
    private final String name;

    private long entriesAdded;

    private int executionCount;

    private int maximumPendingCount;

    private int capacityFlushes;

    private int orderingFlushes;

    private int phaseFlushes;

    private int finalFlushes;

    public StatementStatistics(String name)
    {
      this.name = name == null ? "<unnamed>" : name; //$NON-NLS-1$
    }

    private double getAverageBatchSize()
    {
      return executionCount == 0 ? 0.0 : (double)entriesAdded / executionCount;
    }
  }
}
