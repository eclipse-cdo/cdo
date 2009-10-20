/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.core.runtime.Assert;

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

            if (i + 1 < length - 1)
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

  /**
   * @author Eike Stepper
   */
  public static class TestList extends FastList<Integer>
  {
    public int added;

    public int removed;

    public TestList()
    {
    }

    public Integer[] reset()
    {
      added = 0;
      removed = 0;
      return elements;
    }

    @Override
    protected Integer[] newArray(int length)
    {
      return new Integer[length];
    }

    @Override
    protected void firstElementAdded()
    {
      ++added;
    }

    @Override
    protected void lastElementRemoved()
    {
      ++removed;
    }
  }

  public static void main(String[] args) throws Exception
  {
    testAddFirst();
    testAddSecond();
    testRemoveHead();
    testRemoveMiddle();
    testRemoveTail();
    testRemoveLast();
    testNotFoundInMany();
    testNotFoundInOne();
    testNotFoundInEmpty();
  }

  public static void testAddFirst() throws Exception
  {
    TestList list = new TestList();
    list.add(5);

    Integer[] result = list.get();
    Assert.isTrue(result != null);
    Assert.isTrue(result.length == 1);
    Assert.isTrue(list.elements.length == 1);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 1);
    Assert.isTrue(list.removed == 0);
  }

  public static void testAddSecond() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);

    Integer[] result = list.get();
    Assert.isTrue(result != null);
    Assert.isTrue(result.length == 2);
    Assert.isTrue(list.elements.length == 2);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 1);
    Assert.isTrue(list.removed == 0);
  }

  public static void testRemoveHead() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);
    list.add(2);
    list.add(9);
    list.add(1);
    list.add(4);
    list.add(8);

    Integer[] old = list.reset();
    boolean removed = list.remove(5);

    Integer[] result = list.get();
    Assert.isTrue(removed);
    Assert.isTrue(result != null);
    Assert.isTrue(result != old);
    Assert.isTrue(result.length == 6);
    Assert.isTrue(list.elements.length == 6);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 0);
    Assert.isTrue(result[0] == 7);
    Assert.isTrue(result[1] == 2);
    Assert.isTrue(result[2] == 9);
    Assert.isTrue(result[3] == 1);
    Assert.isTrue(result[4] == 4);
    Assert.isTrue(result[5] == 8);
  }

  public static void testRemoveMiddle() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);
    list.add(2);
    list.add(9);
    list.add(1);
    list.add(4);
    list.add(8);

    Integer[] old = list.reset();
    boolean removed = list.remove(9);

    Integer[] result = list.get();
    Assert.isTrue(removed);
    Assert.isTrue(result != null);
    Assert.isTrue(result != old);
    Assert.isTrue(result.length == 6);
    Assert.isTrue(list.elements.length == 6);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 0);
    Assert.isTrue(result[0] == 5);
    Assert.isTrue(result[1] == 7);
    Assert.isTrue(result[2] == 2);
    Assert.isTrue(result[3] == 1);
    Assert.isTrue(result[4] == 4);
    Assert.isTrue(result[5] == 8);
  }

  public static void testRemoveTail() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);
    list.add(2);
    list.add(9);
    list.add(1);
    list.add(4);
    list.add(8);

    Integer[] old = list.reset();
    boolean removed = list.remove(8);

    Integer[] result = list.get();
    Assert.isTrue(removed);
    Assert.isTrue(result != old);
    Assert.isTrue(result != null);
    Assert.isTrue(result.length == 6);
    Assert.isTrue(list.elements.length == 6);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 0);
    Assert.isTrue(result[0] == 5);
    Assert.isTrue(result[1] == 7);
    Assert.isTrue(result[2] == 2);
    Assert.isTrue(result[3] == 9);
    Assert.isTrue(result[4] == 1);
    Assert.isTrue(result[5] == 4);
  }

  public static void testRemoveLast() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);

    Integer[] old = list.reset();
    boolean removed1 = list.remove(7);
    boolean removed2 = list.remove(5);

    Integer[] result = list.get();
    Assert.isTrue(removed1);
    Assert.isTrue(removed2);
    Assert.isTrue(result != old);
    Assert.isTrue(result == null);
    Assert.isTrue(list.elements == null);
    Assert.isTrue(list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 1);
  }

  public static void testNotFoundInMany() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);
    list.add(2);
    list.add(9);
    list.add(1);
    list.add(4);
    list.add(8);

    Integer[] old = list.reset();
    boolean removed = list.remove(10);

    Integer[] result = list.get();
    Assert.isTrue(!removed);
    Assert.isTrue(result != null);
    Assert.isTrue(result == old);
    Assert.isTrue(result.length == 7);
    Assert.isTrue(list.elements.length == 7);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 0);
    Assert.isTrue(result[0] == 5);
    Assert.isTrue(result[1] == 7);
    Assert.isTrue(result[2] == 2);
    Assert.isTrue(result[3] == 9);
    Assert.isTrue(result[4] == 1);
    Assert.isTrue(result[5] == 4);
    Assert.isTrue(result[6] == 8);
  }

  public static void testNotFoundInOne() throws Exception
  {
    TestList list = new TestList();
    list.add(5);

    Integer[] old = list.reset();
    boolean removed = list.remove(10);

    Integer[] result = list.get();
    Assert.isTrue(!removed);
    Assert.isTrue(result != null);
    Assert.isTrue(result == old);
    Assert.isTrue(result.length == 1);
    Assert.isTrue(list.elements.length == 1);
    Assert.isTrue(!list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 0);
    Assert.isTrue(result[0] == 5);
  }

  public static void testNotFoundInEmpty() throws Exception
  {
    TestList list = new TestList();

    Integer[] old = list.reset();
    boolean removed = list.remove(10);

    Integer[] result = list.get();
    Assert.isTrue(!removed);
    Assert.isTrue(result == null);
    Assert.isTrue(result == old);
    Assert.isTrue(list.isEmpty());
    Assert.isTrue(list.added == 0);
    Assert.isTrue(list.removed == 0);
  }
}
