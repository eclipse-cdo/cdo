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
import org.eclipse.net4j.db.dml.SQLSubSelect;

/**
 * The right-hand comparison stage. Completing an operator returns the enclosing clause stage.
 *
 * @param <NEXT> the stage available after the comparison is completed
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLComparisonStep<NEXT>
{
  /**
   * Adds an equality comparison between the left expression supplied to this stage and the right expression.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT eq(SQLExpression right);

  /**
   * Adds an inequality comparison between the left expression supplied to this stage and the right expression.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT ne(SQLExpression right);

  /**
   * Adds a less-than comparison between the left expression supplied to this stage and the right expression.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT lt(SQLExpression right);

  /**
   * Adds a less-than-or-equal comparison between the left expression supplied to this stage and the right expression.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT le(SQLExpression right);

  /**
   * Adds a greater-than comparison between the left expression supplied to this stage and the right expression.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT gt(SQLExpression right);

  /**
   * Adds a greater-than-or-equal comparison between the left expression supplied to this stage and the right expression.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT ge(SQLExpression right);

  /**
   * Adds a LIKE comparison. Pattern and escape semantics are determined by SQL and the target dialect.
   *
   * @param right the right expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT like(SQLExpression right);

  /**
   * Adds a BETWEEN comparison with the supplied lower and upper expressions.
   *
   * @param lower the lower bound expression
   * @param upper the upper bound expression
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT between(SQLExpression lower, SQLExpression upper);

  /**
   * Adds an IN comparison using the result of a complete nested SELECT.
   *
   * @param query the complete nested SELECT definition
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT in(SQLSubSelect query);

  /**
   * Adds an IS NULL test for the left expression supplied to this stage.
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT isNull();

  /**
   * Adds an IS NOT NULL test for the left expression supplied to this stage.
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT isNotNull();
}
