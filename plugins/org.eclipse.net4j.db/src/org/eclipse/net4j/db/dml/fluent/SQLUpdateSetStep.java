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
package org.eclipse.net4j.db.dml.fluent;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;

/**
 * An UPDATE with at least one complete assignment and optional further assignments or WHERE.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLUpdateSetStep extends SQLBuildStep
{
  /**
   * Begins an assignment to an existing schema column. The returned assignment stage must receive a value before the statement can be completed.
   *
   * @param column the existing schema column
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLUpdateAssignmentStep set(IDBField column);

  /**
   * Supplies a complete WHERE condition or begins a comparison that must be completed before the clause stage is available.
   *
   * @param left the left expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLComparisonStep<SQLUpdateWhereStep> where(SQLExpression left);

  /**
   * Supplies a complete WHERE condition or begins a comparison that must be completed before the clause stage is available.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLUpdateWhereStep where(SQLCondition condition);
}
