/*
 * Copyright (c) 2016, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A {@link PreparedStatement} that supports batching of parameter sets. A batch is executed when the number of
 * parameter sets reaches the {@link #getBatchSize() batch size}. The total number of parameter sets submitted to
 * this statement can be retrieved using {@link #getBatchCount()}. The total number of rows affected by all
 * executions of this statement can be retrieved using {@link #getTotalResult()}.
 * <p>
 * The {@link #flush()} method can be used to execute all currently pending parameter sets, and the number of
 * parameter sets that have not yet been executed can be retrieved using {@link #getPendingCount()}.
 *
 * @author Eike Stepper
 * @since 4.5
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface BatchedStatement extends PreparedStatement
{
  /**
   * Returns the maximum number of parameter sets that can be submitted to this statement
   * before an execution is triggered.
   */
  public int getBatchSize();

  /**
   * Returns the total number of parameter sets submitted to this statement.
   */
  public int getBatchCount();

  /**
   * Returns the total number of rows affected by all executions of this statement.
   * This is the sum of the update counts for each execution.
   */
  public int getTotalResult();

  /**
   * Returns the number of parameter sets that have not yet been executed.
   *
   * @since 4.14
   */
  public int getPendingCount();

  /**
   * Returns the number of executed JDBC batches.
   *
   * @since 4.14
   */
  public int getExecutionCount();

  /**
   * Returns the number of successful JDBC batch entries whose update count is unknown.
   *
   * @since 4.14
   */
  public int getUnknownResultCount();

  /**
   * Executes all currently pending parameter sets.
   *
   * @since 4.14
   */
  public int flush() throws SQLException;

  /**
   * @author Eike Stepper
   * @since 4.14
   */
  public interface Context
  {
    public default void manageStatement(BatchedStatement statement)
    {
      manageStatement(statement, null);
    }

    public void manageStatement(BatchedStatement statement, String diagnosticName);

    public void afterExecuteUpdate(BatchedStatement statement);
  }
}
