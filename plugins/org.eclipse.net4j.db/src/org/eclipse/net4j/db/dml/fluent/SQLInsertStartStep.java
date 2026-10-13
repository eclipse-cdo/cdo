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
 * An INSERT target that can accept values, an explicit column list, or its first named assignment.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLInsertStartStep
{
  /**
   * Selects the INSERT target columns and returns a stage that requires a corresponding value list.
   *
   * @param first the first target column for the INSERT
   * @param others additional target columns in value order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertColumnsStep columns(IDBField first, IDBField... others);

  /**
   * Supplies values for the INSERT target. When explicit columns were selected, values correspond positionally to those columns.
   *
   * @param first the first expression value
   * @param others additional values in matching column order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertValuesStep values(SQLExpression first, SQLExpression... others);

  /**
   * Begins an assignment to an existing schema column. The returned assignment stage must receive a value before the statement can be completed.
   *
   * @param column the existing schema column
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLInsertAssignmentStep set(IDBField column);
}
