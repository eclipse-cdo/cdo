/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

/**
 * @author Eike Stepper
 * @since 3.3
 * @deprecated As of 3.10 use {@link java.util.function.Predicate}.
 */
@Deprecated
public interface Predicate<T>
{
  public boolean apply(T element);

  /**
   * @author Eike Stepper
   * @since 3.10
   * @deprecated As of 3.10 use {@link java.util.function.Predicate}.
   */
  @Deprecated
  public static final class DelegatingPredicate<T> implements Predicate<T>
  {
    private final java.util.function.Predicate<T> delegate;

    public DelegatingPredicate(java.util.function.Predicate<T> delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public boolean apply(T element)
    {
      return delegate.test(element);
    }
  }
}
