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
package org.eclipse.net4j.db.dml;

/**
 * A SQL value that can be projected, ordered, compared, or used in another expression.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLExpression extends SQLSelectItem, SQLOrderItem
{
  /**
   * Gives this expression a result-column alias for a SELECT projection.
   *
   * @param alias the result label
   * @return a projection item carrying this expression and its result label
   */
  public default SQLSelectItem as(String alias)
  {
    return SQL.as(this, alias);
  }
}
