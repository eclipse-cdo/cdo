/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * @since 3.0
 */
public abstract class FastList<E>
{
  protected E[] elements;

  public FastList()
  {
  }

  public boolean isEmpty()
  {
    return elements == null;
  }

  public E[] get()
  {
    return elements;
  }

  public synchronized void add(E element)
  {
    if (elements == null)
    {
      E[] array = newArray(1);
      array[0] = element;
      elements = array;
      firstElementAdded();
    }
    else
    {
      int length = elements.length;
      E[] array = newArray(length + 1);
      System.arraycopy(elements, 0, array, 0, length);
      array[length] = element;
      elements = array;
    }
  }

  public synchronized boolean remove(E element)
  {
    if (elements != null)
    {
      int length = elements.length;
      if (length == 1)
      {
        if (elements[0] == element)
        {
          elements = null;
          lastElementRemoved();
          return true;
        }
      }
      else
      {
        for (int i = 0; i < length; i++)
        {
          E e = elements[i];
          if (e == element)
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
            return true;
          }
        }
      }
    }

    return false;
  }

  protected void firstElementAdded()
  {
  }

  protected void lastElementRemoved()
  {
  }

  protected abstract E[] newArray(int length);
}
