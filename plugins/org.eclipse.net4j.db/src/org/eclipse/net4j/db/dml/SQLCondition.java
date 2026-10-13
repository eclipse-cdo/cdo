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
 * A boolean SQL expression that can also be projected or ordered when supported by a dialect.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLCondition extends SQLExpression
{
  /**
   * Combines this condition with another condition using AND.
   *
   * @param other the condition to combine
   * @return the combined condition
   */
  public default SQLCondition and(SQLCondition other)
  {
    return SQL.and(this, other);
  }

  /**
   * Combines this condition with another condition using OR.
   *
   * @param other the condition to combine
   * @return the combined condition
   */
  public default SQLCondition or(SQLCondition other)
  {
    return SQL.or(this, other);
  }

  /**
   * Negates this condition.
   *
   * @return the negated condition
   */
  public default SQLCondition not()
  {
    return SQL.not(this);
  }
}
