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

import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLOrderItem;

/**
 * A SELECT with a completed WHERE clause and optional grouping or ordering.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLSelectWhereStep extends SQLSelectBuildStep, SQLConditionStep<SQLSelectWhereStep>
{
  /**
   * Adds the ordered expressions used to group the SELECT result.
   *
   * @param first the first expression in the GROUP BY clause
   * @param others additional grouping expressions appended in SQL order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectGroupByStep groupBy(SQLExpression first, SQLExpression... others);

  /**
   * Starts an ORDER BY clause with the supplied items in order.
   *
   * @param first the first ordering item in the ORDER BY clause
   * @param others additional ordering items appended in SQL order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectOrderByStep orderBy(SQLOrderItem first, SQLOrderItem... others);

  /**
   * Starts an ORDER BY clause with one descending expression.
   *
   * @param expression the expression used by this clause
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectOrderByStep orderByDesc(SQLExpression expression);
}
