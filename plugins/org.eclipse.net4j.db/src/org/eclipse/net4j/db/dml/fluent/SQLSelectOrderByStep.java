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
 * A complete SELECT ordering clause that can accept further ordering items.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLSelectOrderByStep extends SQLSelectBuildStep
{
  /**
   * Appends an ordering item after the current ORDER BY items.
   *
   * @param item the next ordering item
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectOrderByStep thenBy(SQLOrderItem item);

  /**
   * Appends a descending expression after the current ORDER BY items.
   *
   * @param expression the expression used by this clause
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectOrderByStep thenByDesc(SQLExpression expression);
}
