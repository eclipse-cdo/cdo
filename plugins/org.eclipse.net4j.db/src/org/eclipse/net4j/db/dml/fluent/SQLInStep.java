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

/**
 * An incomplete value list embedded in a fluent comparison stage. The enclosing statement remains
 * incomplete until {@link #end()} records a non-empty list and returns its normal continuation.
 *
 * @param <NEXT> the statement stage available after the list is completed
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLInStep<NEXT>
{
  /**
   * Appends one expression to the IN value list.
   *
   * @param value the value expression to append
   * @return this active list stage
   * @since 4.15
   */
  public SQLInStep<NEXT> add(SQLExpression value);

  /**
   * Appends expressions from one traversal of the iterable, preserving its iteration order.
   *
   * @param values the expressions to append
   * @return this active list stage
   * @since 4.15
   */
  public SQLInStep<NEXT> addAll(Iterable<? extends SQLExpression> values);

  /**
   * Completes the IN comparison and returns the enclosing fluent stage.
   *
   * @return the next valid statement stage
   * @throws IllegalStateException if no values have been added or this stage is no longer active
   * @since 4.15
   */
  public NEXT end();
}
