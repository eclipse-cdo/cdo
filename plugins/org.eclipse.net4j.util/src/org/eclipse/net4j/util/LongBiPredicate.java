/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface LongBiPredicate
{
  public boolean test(long v1, long v2);

  public default LongBiPredicate negate()
  {
    return (v1, v2) -> !test(v1, v2);
  }

  public default LongBiPredicate and(LongBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) && other.test(v1, v2);
  }

  public default LongBiPredicate or(LongBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (v1, v2) -> test(v1, v2) || other.test(v1, v2);
  }
}
