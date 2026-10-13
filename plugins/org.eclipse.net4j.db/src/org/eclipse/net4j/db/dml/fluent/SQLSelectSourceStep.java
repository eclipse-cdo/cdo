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

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLOrderItem;
import org.eclipse.net4j.db.dml.SQLTableRef;

/**
 * A SELECT stage with a source clause and optional further clauses. Repeated FROM additions create
 * comma-separated sources when there are no joins. If joins follow multiple FROM sources, the
 * compiler combines those initial sources with CROSS JOIN and groups them as the left operand;
 * subsequent joins associate left-to-right. Arbitrary explicit join-tree nesting is not supported.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLSelectSourceStep extends SQLSelectBuildStep
{
  /**
   * Starts an INNER JOIN that must receive a complete condition or comparison through ON before it can be completed. Joins following multiple FROM sources apply to their grouped CROSS JOIN expression.
   *
   * @param table the schema table or table occurrence
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectJoinStep join(IDBTable table);

  /**
   * Starts an INNER JOIN that must receive a complete condition or comparison through ON before it can be completed. Joins following multiple FROM sources apply to their grouped CROSS JOIN expression.
   *
   * @param table the schema table or table occurrence
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectJoinStep join(SQLTableRef table);

  /**
   * Starts a LEFT JOIN that must receive a complete condition or comparison through ON before it can be completed. Joins following multiple FROM sources apply to their grouped CROSS JOIN expression.
   *
   * @param table the schema table or table occurrence
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectJoinStep leftJoin(IDBTable table);

  /**
   * Starts a LEFT JOIN that must receive a complete condition or comparison through ON before it can be completed. Joins following multiple FROM sources apply to their grouped CROSS JOIN expression.
   *
   * @param table the schema table or table occurrence
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectJoinStep leftJoin(SQLTableRef table);

  /**
   * Supplies a complete reusable WHERE condition or begins a comparison. Complete conditions may be composed with explicit grouping; simple connector chains use SQL operator precedence.
   *
   * @param left the left expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLComparisonStep<SQLSelectWhereStep> where(SQLExpression left);

  /**
   * Supplies a complete reusable WHERE condition or begins a comparison. Complete conditions may be composed with explicit grouping; simple connector chains use SQL operator precedence.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectWhereStep where(SQLCondition condition);

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
