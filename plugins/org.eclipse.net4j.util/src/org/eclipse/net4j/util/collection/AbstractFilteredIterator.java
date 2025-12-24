/*
 * Copyright (c) 2012, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.Iterator;

/**
 * An abstract base class for custom iterators that {@link #isValid(Object) filter} the elements of a delegate iterator.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public abstract class AbstractFilteredIterator<T> extends AbstractIterator<T>
{
  private Iterator<T> delegate;

  public AbstractFilteredIterator(Iterator<T> delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public void remove()
  {
    delegate.remove();
  }

  @Override
  protected Object computeNextElement()
  {
    while (delegate.hasNext())
    {
      T element = delegate.next();
      if (isValid(element))
      {
        return element;
      }
    }

    return END_OF_DATA;
  }

  protected abstract boolean isValid(T element);

  /**
   * @author Eike Stepper
   * @since 3.10
   */
  public static class Predicated<T> extends AbstractFilteredIterator<T>
  {
    private final java.util.function.Predicate<? super T> predicate;

    public Predicated(Iterator<T> delegate, java.util.function.Predicate<? super T> predicate)
    {
      super(delegate);
      this.predicate = predicate;
    }

    public Predicated(java.util.function.Predicate<? super T> predicate, Iterator<T> delegate)
    {
      super(delegate);
      this.predicate = predicate;
    }

    public java.util.function.Predicate<? super T> getPredicate()
    {
      return predicate;
    }

    @Override
    protected boolean isValid(T element)
    {
      return predicate.test(element);
    }
  }
}
