/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.dml.SQLStatement;

/**
 * Supplies the database context used while executing a batch of statements.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface IBatchingContext extends BatchedStatement.Context
{
  public default BatchedStatement createStatement(String sql, ReuseProbability reuseProbability)
  {
    return createStatement(sql, reuseProbability, null);
  }

  public BatchedStatement createStatement(String sql, ReuseProbability reuseProbability, String diagnosticName);

  /**
   * Creates a batched statement from a compiled SQL definition.
   *
   * @param sql the compiled statement definition
   * @param reuseProbability the existing statement-cache reuse hint
   * @return the managed batched statement
   * @since 4.16
   */
  public default BatchedStatement createStatement(SQLStatement sql, ReuseProbability reuseProbability)
  {
    return createStatement(sql, reuseProbability, null);
  }

  /**
   * Creates a batched statement from a compiled SQL definition through the existing connection and batching path.
   * The compiled SQL text remains the key used by the connection's prepared-statement cache.
   *
   * @param sql the compiled statement definition
   * @param reuseProbability the existing statement-cache reuse hint
   * @param diagnosticName the optional name used by batching diagnostics
   * @return the managed batched statement
   * @since 4.16
   */
  public default BatchedStatement createStatement(SQLStatement sql, ReuseProbability reuseProbability, String diagnosticName)
  {
    return createStatement(sql.getSQL(), reuseProbability, diagnosticName);
  }

  public void releaseStatement(BatchedStatement statement);

  public void discardStatement(BatchedStatement statement);

  public void flushStatement(BatchedStatement statement);

  public void flushPhase();

  public void recordDiagnosticCounter(String name);

  /**
   * Adds a value to an optional mapping-specific diagnostic counter.
   * <p>
   * Counter names and values are diagnostic metadata only. They do not participate in statement scheduling,
   * execution ordering, or batching decisions.
   *
   * @param name
   *          the short counter name
   * @param value
   *          the value to add
   */
  public void recordDiagnosticCounter(String name, long value);
}
