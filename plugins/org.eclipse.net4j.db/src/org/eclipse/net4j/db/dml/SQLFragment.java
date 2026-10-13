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
 * An immutable sequence of trusted SQL text and structured SQL parts.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLFragment
{
  /**
   * Wraps this fragment as an expression through the canonical SQL factory.
   *
   * @return an expression backed by this fragment
   */
  public default SQLExpression expression()
  {
    return SQL.expression(this);
  }

  /**
   * Wraps this fragment as a condition through the canonical SQL factory.
   *
   * @return a condition backed by this fragment
   */
  public default SQLCondition condition()
  {
    return SQL.condition(this);
  }
}
