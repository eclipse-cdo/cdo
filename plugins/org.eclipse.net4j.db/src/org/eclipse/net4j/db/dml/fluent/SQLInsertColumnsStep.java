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
 * An INSERT with explicit columns that requires its positional values before completion. This form cannot be combined with implicit values or named assignments; the compiler checks the column/value counts.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLInsertColumnsStep
{
  /**
   * Supplies values positionally corresponding to the explicit target columns. The compiler validates that the number of values matches the number of columns.
   *
   * @param first the first expression value
   * @param others additional values in matching column order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertValuesStep values(SQLExpression first, SQLExpression... others);
}
