/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import java.util.NoSuchElementException;

/**
 * An object that iterates over the elements of an array
 *
 * @author Eike Stepper
 * @since 3.0
 */
public class ArrayIterator<T> implements Iterator<T>
{
  private T[] elements;

  private int index;

  private int lastElement;

  public ArrayIterator(T[] elements)
  {
    this(elements, 0, elements.length - 1);
  }

  public ArrayIterator(T[] elements, int firstElement)
  {
    this(elements, firstElement, elements.length - 1);
  }

  public ArrayIterator(T[] elements, int firstElement, int lastElement)
  {
    this.elements = elements;
    index = firstElement;
    this.lastElement = lastElement;
  }

  @Override
  public boolean hasNext()
  {
    return elements != null && index <= lastElement;
  }

  @Override
  public T next() throws NoSuchElementException
  {
    if (!hasNext())
    {
      throw new NoSuchElementException();
    }

    return elements[index++];
  }

  /**
   * Unsupported.
   *
   * @throws UnsupportedOperationException
   *           always
   */
  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
