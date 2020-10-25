/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public abstract class ConcurrentArray<E>
{
  protected volatile E[] elements;

  private final E[] EMPTY = newArray(0);

  public ConcurrentArray()
  {
  }

  public boolean isEmpty()
  {
    return elements == null; // Atomic operation
  }

  /**
   * Returns the elements, never <code>null</code>.
   */
  public E[] get()
  {
    E[] result = elements; // Atomic operation
    return result == null ? EMPTY : result;
  }

  /**
   * @since 3.13
   */
  public synchronized boolean contains(E element)
  {
    if (elements == null)
    {
      return false;
    }

    int length = elements.length;

    for (int i = 0; i < length; i++)
    {
      E e = elements[i];
      if (equals(element, e))
      {
        return true;
      }
    }

    return false;
  }

  private boolean validateAndAdd(E element, boolean unique)
  {
    if (!validate(element))
    {
      return false;
    }

    return add(element, unique);
  }

  private synchronized boolean add(E element, boolean unique)
  {
    if (elements == null)
    {
      E[] array = newArray(1);
      array[0] = element;

      elements = array;
      elementAdded(element);
      firstElementAdded();
      return true;
    }

    int length = elements.length;

    if (unique)
    {
      for (int i = 0; i < length; i++)
      {
        E e = elements[i];
        if (equals(element, e))
        {
          return false;
        }
      }
    }

    E[] array = newArray(length + 1);
    System.arraycopy(elements, 0, array, 0, length);
    array[length] = element;
    elements = array;
    elementAdded(element);
    return true;
  }

  /**
   * @since 3.13
   */
  public synchronized boolean addUnique(E element)
  {
    return validateAndAdd(element, true);
  }

  public synchronized void add(E element)
  {
    validateAndAdd(element, false);
  }

  public synchronized boolean remove(E element)
  {
    if (elements != null)
    {
      int length = elements.length;
      if (length == 1)
      {
        if (equals(elements[0], element))
        {
          elements = null;
          elementRemoved(element);
          lastElementRemoved();
          return true;
        }
      }
      else
      {
        for (int i = 0; i < length; i++)
        {
          E e = elements[i];
          if (equals(e, element))
          {
            E[] array = newArray(length - 1);

            if (i > 0)
            {
              System.arraycopy(elements, 0, array, 0, i);
            }

            if (i + 1 <= length - 1)
            {
              System.arraycopy(elements, i + 1, array, i, length - 1 - i);
            }

            elements = array;
            elementRemoved(element);
            return true;
          }
        }
      }
    }

    return false;
  }

  protected boolean validate(E element)
  {
    return true;
  }

  /**
   * @since 3.13
   */
  protected boolean equals(E e1, E e2)
  {
    return e1 == e2;
  }

  /**
   * Synchronized through {@link #add(Object)}.
   *
   * @since 3.13
   */
  protected void elementAdded(E element)
  {
    // Do nothing.
  }

  /**
   * Synchronized through {@link #remove(Object)}.
   *
   * @since 3.13
   */
  protected void elementRemoved(E element)
  {
    // Do nothing.
  }

  /**
   * Synchronized through {@link #add(Object)}.
   */
  protected void firstElementAdded()
  {
    // Do nothing.
  }

  /**
   * Synchronized through {@link #remove(Object)}.
   */
  protected void lastElementRemoved()
  {
    // Do nothing.
  }

  /**
   * Synchronized through {@link #add(Object)} or {@link #remove(Object)}.
   */
  protected abstract E[] newArray(int length);

  /**
   * @author Eike Stepper
   */
  public abstract static class Unique<E> extends ConcurrentArray<E>
  {
    public Unique()
    {
    }

    @Override
    protected boolean validate(E element)
    {
      if (elements != null)
      {
        for (int i = 0; i < elements.length; i++)
        {
          if (equals(element, elements[i]))
          {
            violatingUniqueness(element);
            return false;
          }
        }
      }

      return true;
    }

    /**
     * Synchronized through {@link #add(Object)}.
     */
    @Override
    protected boolean equals(E e1, E e2)
    {
      return e1 == e2;
    }

    /**
     * Synchronized through {@link #add(Object)}.
     */
    protected void violatingUniqueness(E element)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract static class DuplicateCounter<E> extends ConcurrentArray<E>
  {
    private int maxDuplicates;

    public DuplicateCounter()
    {
    }

    public final int getMaxDuplicates()
    {
      return maxDuplicates;
    }

    @Override
    protected boolean validate(E element)
    {
      if (elements != null)
      {
        int duplicates = 0;
        for (int i = 0; i < elements.length; i++)
        {
          if (equals(element, elements[i]))
          {
            ++duplicates;
          }
        }

        if (duplicates > maxDuplicates)
        {
          maxDuplicates = duplicates;
        }
      }

      return true;
    }

    @Override
    protected boolean equals(E e1, E e2)
    {
      return e1 == e2;
    }
  }
}
