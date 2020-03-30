/*
 * Copyright (c) 2010-2012, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.collection.ConcurrentArray;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class FastListTest extends AbstractOMTest
{
  public static void testAddFirst() throws Exception
  {
    TestList list = new TestList();
    list.add(5);

    Integer[] result = list.get();
    assertEquals(true, result != null);
    assertEquals(true, result.length == 1);
    assertEquals(true, list.getElements().length == 1);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 1);
    assertEquals(true, list.removed == 0);
  }

  public static void testAddSecond() throws Exception
  {
    TestList list = new TestList();
    list.add(5);
    list.add(7);

    Integer[] result = list.get();
    assertEquals(true, result != null);
    assertEquals(true, result.length == 2);
    assertEquals(true, list.getElements().length == 2);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 1);
    assertEquals(true, list.removed == 0);
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
    assertEquals(true, removed);
    assertEquals(true, result != null);
    assertEquals(true, result != old);
    assertEquals(true, result.length == 6);
    assertEquals(true, list.getElements().length == 6);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 0);
    assertEquals(true, result[0] == 7);
    assertEquals(true, result[1] == 2);
    assertEquals(true, result[2] == 9);
    assertEquals(true, result[3] == 1);
    assertEquals(true, result[4] == 4);
    assertEquals(true, result[5] == 8);
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
    assertEquals(true, removed);
    assertEquals(true, result != null);
    assertEquals(true, result != old);
    assertEquals(true, result.length == 6);
    assertEquals(true, list.getElements().length == 6);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 0);
    assertEquals(true, result[0] == 5);
    assertEquals(true, result[1] == 7);
    assertEquals(true, result[2] == 2);
    assertEquals(true, result[3] == 1);
    assertEquals(true, result[4] == 4);
    assertEquals(true, result[5] == 8);
  }

  public void testRemoveMiddleOfThree()
  {
    TestList list = new TestList();
    Integer one = Integer.valueOf(1);
    Integer two = Integer.valueOf(2);
    Integer three = Integer.valueOf(3);

    list.add(one);
    list.add(two);
    list.add(three);

    list.remove(two);

    Integer[] elements = list.get();
    assertSame(one, elements[0]);
    assertSame(three, elements[1]);
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
    assertEquals(true, removed);
    assertEquals(true, result != old);
    assertEquals(true, result != null);
    assertEquals(true, result.length == 6);
    assertEquals(true, list.getElements().length == 6);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 0);
    assertEquals(true, result[0] == 5);
    assertEquals(true, result[1] == 7);
    assertEquals(true, result[2] == 2);
    assertEquals(true, result[3] == 9);
    assertEquals(true, result[4] == 1);
    assertEquals(true, result[5] == 4);
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
    assertEquals(true, removed1);
    assertEquals(true, removed2);
    assertEquals(true, result != old);
    assertEquals(true, result == null);
    assertEquals(true, list.getElements() == null);
    assertEquals(true, list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 1);
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
    assertEquals(true, !removed);
    assertEquals(true, result != null);
    assertEquals(true, result == old);
    assertEquals(true, result.length == 7);
    assertEquals(true, list.getElements().length == 7);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 0);
    assertEquals(true, result[0] == 5);
    assertEquals(true, result[1] == 7);
    assertEquals(true, result[2] == 2);
    assertEquals(true, result[3] == 9);
    assertEquals(true, result[4] == 1);
    assertEquals(true, result[5] == 4);
    assertEquals(true, result[6] == 8);
  }

  public static void testNotFoundInOne() throws Exception
  {
    TestList list = new TestList();
    list.add(5);

    Integer[] old = list.reset();
    boolean removed = list.remove(10);

    Integer[] result = list.get();
    assertEquals(true, !removed);
    assertEquals(true, result != null);
    assertEquals(true, result == old);
    assertEquals(true, result.length == 1);
    assertEquals(true, list.getElements().length == 1);
    assertEquals(true, !list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 0);
    assertEquals(true, result[0] == 5);
  }

  public static void testNotFoundInEmpty() throws Exception
  {
    TestList list = new TestList();

    Integer[] old = list.reset();
    boolean removed = list.remove(10);

    Integer[] result = list.get();
    assertEquals(true, !removed);
    assertEquals(true, result == null);
    assertEquals(true, result == old);
    assertEquals(true, list.isEmpty());
    assertEquals(true, list.added == 0);
    assertEquals(true, list.removed == 0);
  }

  /**
   * @author Eike Stepper
   */
  public static class TestList extends ConcurrentArray<Integer>
  {
    public int added;

    public int removed;

    public TestList()
    {
    }

    public Integer[] getElements()
    {
      return elements;
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
}
