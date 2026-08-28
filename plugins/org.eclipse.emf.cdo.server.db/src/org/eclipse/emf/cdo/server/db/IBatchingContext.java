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

/**
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
