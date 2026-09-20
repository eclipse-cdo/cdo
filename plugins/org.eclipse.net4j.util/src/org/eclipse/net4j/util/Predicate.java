/*
 * Copyright (c) 2013, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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

/**
 * Evaluates a condition for an element.
 *
 * @author Eike Stepper
 * @param <T> the element type
 * @since 3.3
 * @deprecated As of 3.10 use {@link java.util.function.Predicate}.
 */
@Deprecated
public interface Predicate<T>
{
  @Deprecated
  public boolean apply(T element);

  /**
   * Delegates predicate evaluation to a Java function.
   *
   * @author Eike Stepper
   * @since 3.10
   * @deprecated As of 3.10 use {@link java.util.function.Predicate}.
   * @param <T> the element type
   */
  @Deprecated
  public static final class DelegatingPredicate<T> implements Predicate<T>
  {
    private final java.util.function.Predicate<T> delegate;

    @Deprecated
    public DelegatingPredicate(java.util.function.Predicate<T> delegate)
    {
      this.delegate = delegate;
    }

    @Deprecated
    @Override
    public boolean apply(T element)
    {
      return delegate.test(element);
    }
  }
}
