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
 * A condition clause that accepts additional comparisons or complete reusable conditions.
 *
 * @param <NEXT> the stage returned when another complete condition is appended
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLConditionStep<NEXT>
{
  /**
   * Adds a comparison or complete condition to the current clause using AND.
   *
   * @param left the left expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLComparisonStep<NEXT> and(SQLExpression left);

  /**
   * Adds a comparison or complete condition to the current clause using OR.
   *
   * @param left the left expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLComparisonStep<NEXT> or(SQLExpression left);

  /**
   * Adds a comparison or complete condition to the current clause using AND.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT and(SQLCondition condition);

  /**
   * Adds a comparison or complete condition to the current clause using OR.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public NEXT or(SQLCondition condition);
}
