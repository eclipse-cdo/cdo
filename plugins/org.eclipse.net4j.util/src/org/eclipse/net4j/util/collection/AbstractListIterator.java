/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
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

import java.util.NoSuchElementException;

/**
 * An abstract base class for custom list iterators that only requires to implement the {@link #computeNextElement()} and {@link #computePreviousElement()} methods.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public abstract class AbstractListIterator<T> extends AbstractIterator<T>
{
  private boolean previousComputed;

  private T previous;

  public AbstractListIterator()
  {
  }

  public final boolean hasPrevious()
  {
    if (previousComputed)
    {
      return true;
    }

    Object object = computeNextElement();
    previousComputed = true;

    if (object == END_OF_DATA)
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    T cast = (T)object;
    previous = cast;
    return true;
  }

  public final T previous()
  {
    if (!hasPrevious())
    {
      throw new NoSuchElementException();
    }

    previousComputed = false;
    return previous;
  }

  /**
   * Returns the previous iteration element, or {@link #END_OF_DATA} if the start of the iteration has been reached.
   */
  protected abstract Object computePreviousElement();
}
