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
 * A JOIN awaiting its ON condition. Supply a complete {@link SQLCondition} to preserve explicit
 * grouping, or use a simple comparison chain whose AND/OR operators follow SQL precedence.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLSelectJoinStep
{
  /**
   * Supplies a complete ON condition or begins a comparison whose completion returns to the joined SELECT stage. Complete conditions can express explicit grouping.
   *
   * @param left the left expression of a comparison
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLComparisonStep<SQLSelectJoinedStep> on(SQLExpression left);

  /**
   * Supplies a complete ON condition or begins a comparison whose completion returns to the joined SELECT stage. Complete conditions can express explicit grouping.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectJoinedStep on(SQLCondition condition);
}
