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
package org.eclipse.net4j.util.concurrent;

/**
 * Provides an execution order key that execution facilities can use to group related executions.
 * <p>
 * Executions with equal non-null keys can be treated as belonging to the same ordered execution group. The exact ordering
 * and execution semantics are defined by the facility that consumes this interface. Implementations should normally
 * return stable, low-cardinality keys.
 * <p>
 * A class object is often a natural key. For example:
 * <pre>
 *   &commat;Override
 *   public Object getExecutionOrderKey()
 *   {
 *     return MySensibleOperation.class;
 *   }
 * </pre>
 *
 * @author Eike Stepper
 * @since 3.31
 */
public interface OrderedExecution
{
  /**
   * Returns the key that identifies the ordered execution group for this object.
   *
   * @return the execution order key, or {@code null} if no ordering is requested
   */
  public Object getExecutionOrderKey();
}
