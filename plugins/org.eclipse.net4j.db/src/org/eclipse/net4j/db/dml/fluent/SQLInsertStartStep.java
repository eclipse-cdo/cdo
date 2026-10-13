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
import org.eclipse.net4j.db.dml.SQLExpression;

/**
 * An INSERT target offering three alternative forms: values in schema field order, an explicit
 * column list followed by positional values, or explicit named assignments. These forms cannot be
 * mixed in one INSERT; column/value counts are checked during compilation.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLInsertStartStep
{
  /**
   * Selects explicit target columns and returns a stage that requires positionally corresponding values. This form is an alternative to implicit schema-order values and named assignments.
   *
   * @param first the first target column for the INSERT
   * @param others additional target columns in value order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertColumnsStep columns(IDBField first, IDBField... others);

  /**
   * Supplies values for the INSERT target. Without an explicit column list, values correspond to schema fields in field order; after {@code columns(...)} they correspond positionally to that list. Counts are validated during compilation.
   *
   * @param first the first expression value
   * @param others additional values in matching column order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertValuesStep values(SQLExpression first, SQLExpression... others);

  /**
   * Begins an explicit named INSERT assignment. Named assignments avoid relying on implicit target-column order and cannot be mixed with {@code columns(...)} or {@code values(...)}.
   *
   * @param column the existing schema column
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertAssignmentStep set(IDBField column);
}
