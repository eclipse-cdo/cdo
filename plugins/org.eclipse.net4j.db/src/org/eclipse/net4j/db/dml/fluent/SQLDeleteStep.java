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

import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;

/**
 * A DELETE statement that may be completed directly or given a WHERE clause. Building it without a WHERE clause is valid and may remove every row in the target table.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLDeleteStep extends SQLBuildStep
{
  /**
   * Supplies a complete WHERE condition or begins a comparison that must be completed before the clause stage is available.
   *
   * @param left the left expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLComparisonStep<SQLDeleteWhereStep> where(SQLExpression left);

  /**
   * Supplies a complete WHERE condition or begins a comparison that must be completed before the clause stage is available.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLDeleteWhereStep where(SQLCondition condition);
}
