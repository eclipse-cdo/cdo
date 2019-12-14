/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.collection.MapEntry;
import org.eclipse.net4j.util.collection.MultiMap.ListBased;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MultiMapTest extends AbstractOMTest
{
  public void testListBased() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertEquals(true, multiMap.isEmpty());
    assertEquals(0, multiMap.size());
    assertEquals(false, multiMap.containsKey(1));
    assertEquals(false, multiMap.containsValue("value" + 1)); //$NON-NLS-1$
    assertEquals(null, multiMap.get(1));

    addDelegate(multiMap, 0, 0);
    assertEquals(true, multiMap.isEmpty());
    assertEquals(0, multiMap.size());
    assertEquals(false, multiMap.containsKey(1));
    assertEquals(false, multiMap.containsValue("value" + 1)); //$NON-NLS-1$
    assertEquals(null, multiMap.get(1));

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertEquals(false, multiMap.isEmpty());
    assertEquals(30, multiMap.size());
    assertEquals(true, multiMap.containsKey(1));
    assertEquals(true, multiMap.containsValue("value" + 1)); //$NON-NLS-1$
    assertEquals("value" + 1, multiMap.get(1)); //$NON-NLS-1$
    assertEquals("value" + 6, multiMap.get(6)); //$NON-NLS-1$

    addDelegate(multiMap, 6, 10);
    assertEquals(false, multiMap.isEmpty());
    assertEquals(30, multiMap.size());
    assertEquals(true, multiMap.containsKey(1));
    assertEquals(true, multiMap.containsValue("value" + 1)); //$NON-NLS-1$
    assertEquals("value" + 1, multiMap.get(1)); //$NON-NLS-1$
    assertEquals("value" + 6, multiMap.get(6)); //$NON-NLS-1$
    assertEquals(null, multiMap.get(35));

    addDelegate(multiMap, 26, 10);
    assertEquals(false, multiMap.isEmpty());
    assertEquals(35, multiMap.size());
    assertEquals(true, multiMap.containsKey(1));
    assertEquals(true, multiMap.containsValue("value" + 1)); //$NON-NLS-1$
    assertEquals("value" + 1, multiMap.get(1)); //$NON-NLS-1$
    assertEquals("value" + 6, multiMap.get(6)); //$NON-NLS-1$
    assertEquals("value" + 35, multiMap.get(35)); //$NON-NLS-1$
  }

  public void testEntrySet() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertEquals(true, multiMap.entrySet().isEmpty());
    assertEquals(0, multiMap.entrySet().size());
    assertEquals(false, multiMap.entrySet().contains(new MapEntry<>(1, "value1"))); //$NON-NLS-1$

    addDelegate(multiMap, 0, 0);
    assertEquals(true, multiMap.entrySet().isEmpty());
    assertEquals(0, multiMap.entrySet().size());
    assertEquals(false, multiMap.entrySet().contains(new MapEntry<>(1, "value1"))); //$NON-NLS-1$

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertEquals(false, multiMap.entrySet().isEmpty());
    assertEquals(30, multiMap.entrySet().size());
    assertEquals(true, multiMap.entrySet().contains(new MapEntry<>(1, "value1"))); //$NON-NLS-1$

    addDelegate(multiMap, 6, 10);
    assertEquals(false, multiMap.entrySet().isEmpty());
    assertEquals(30, multiMap.entrySet().size());
    assertEquals(true, multiMap.entrySet().contains(new MapEntry<>(1, "value1"))); //$NON-NLS-1$
    assertEquals(false, multiMap.entrySet().contains(new MapEntry<>(35, "value35"))); //$NON-NLS-1$

    addDelegate(multiMap, 26, 10);
    assertEquals(false, multiMap.entrySet().isEmpty());
    assertEquals(35, multiMap.entrySet().size());
    assertEquals(true, multiMap.entrySet().contains(new MapEntry<>(1, "value1"))); //$NON-NLS-1$
    assertEquals(true, multiMap.entrySet().contains(new MapEntry<>(35, "value35"))); //$NON-NLS-1$
  }

  public void testKeySet() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertEquals(true, multiMap.keySet().isEmpty());
    assertEquals(0, multiMap.keySet().size());
    assertEquals(false, multiMap.keySet().contains(1));

    addDelegate(multiMap, 0, 0);
    assertEquals(true, multiMap.keySet().isEmpty());
    assertEquals(0, multiMap.keySet().size());
    assertEquals(false, multiMap.keySet().contains(1));

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertEquals(false, multiMap.keySet().isEmpty());
    assertEquals(30, multiMap.keySet().size());
    assertEquals(true, multiMap.keySet().contains(1));

    addDelegate(multiMap, 6, 10);
    assertEquals(false, multiMap.keySet().isEmpty());
    assertEquals(30, multiMap.keySet().size());
    assertEquals(true, multiMap.keySet().contains(1));
    assertEquals(false, multiMap.keySet().contains(35));

    addDelegate(multiMap, 26, 10);
    assertEquals(false, multiMap.keySet().isEmpty());
    assertEquals(35, multiMap.keySet().size());
    assertEquals(true, multiMap.keySet().contains(1));
    assertEquals(true, multiMap.keySet().contains(35));
  }

  public void testValues() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertEquals(true, multiMap.values().isEmpty());
    assertEquals(0, multiMap.values().size());
    assertEquals(false, multiMap.values().contains("value1")); //$NON-NLS-1$

    addDelegate(multiMap, 0, 0);
    assertEquals(true, multiMap.values().isEmpty());
    assertEquals(0, multiMap.values().size());
    assertEquals(false, multiMap.values().contains("value1")); //$NON-NLS-1$

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertEquals(false, multiMap.values().isEmpty());
    assertEquals(30, multiMap.values().size());
    assertEquals(true, multiMap.values().contains("value1")); //$NON-NLS-1$

    addDelegate(multiMap, 6, 10);
    assertEquals(false, multiMap.values().isEmpty());
    assertEquals(30, multiMap.values().size());
    assertEquals(true, multiMap.values().contains("value1")); //$NON-NLS-1$
    assertEquals(false, multiMap.values().contains("value35")); //$NON-NLS-1$

    addDelegate(multiMap, 26, 10);
    assertEquals(false, multiMap.values().isEmpty());
    assertEquals(35, multiMap.values().size());
    assertEquals(true, multiMap.values().contains("value1")); //$NON-NLS-1$
    assertEquals(true, multiMap.values().contains("value35")); //$NON-NLS-1$
  }

  public void testEntrySetIterator() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertIterator(new HashSet<>(), multiMap.entrySet());

    addDelegate(multiMap, 0, 0);
    assertIterator(new HashSet<>(), multiMap.entrySet());

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertIterator(createMapEntries(1, 30), multiMap.entrySet());

    addDelegate(multiMap, 6, 10);
    assertIterator(createMapEntries(1, 30), multiMap.entrySet());

    addDelegate(multiMap, 26, 10);
    assertIterator(createMapEntries(1, 35), multiMap.entrySet());
  }

  public void testKeySetIterator() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertIterator(new HashSet<>(), multiMap.keySet());

    addDelegate(multiMap, 0, 0);
    assertIterator(new HashSet<>(), multiMap.keySet());

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertIterator(createKeys(1, 30), multiMap.keySet());

    addDelegate(multiMap, 6, 10);
    assertIterator(createKeys(1, 30), multiMap.keySet());

    addDelegate(multiMap, 26, 10);
    assertIterator(createKeys(1, 35), multiMap.keySet());
  }

  public void testValuesIterator() throws Exception
  {
    ListBased<Integer, String> multiMap = new ListBased<>();
    assertIterator(new HashSet<>(), multiMap.values());

    addDelegate(multiMap, 0, 0);
    assertIterator(new HashSet<>(), multiMap.values());

    addDelegate(multiMap, 1, 10);
    addDelegate(multiMap, 11, 10);
    addDelegate(multiMap, 21, 10);
    assertIterator(createValues(1, 30), multiMap.values());

    addDelegate(multiMap, 6, 10);
    assertIterator(createValues(1, 30), multiMap.values());

    addDelegate(multiMap, 26, 10);
    assertIterator(createValues(1, 35), multiMap.values());
  }

  private void addDelegate(ListBased<Integer, String> multiMap, int start, int count)
  {
    Map<Integer, String> map = new HashMap<>();
    for (int i = 0; i < count; i++)
    {
      int key = start + i;
      map.put(key, "value" + key); //$NON-NLS-1$
    }

    multiMap.getDelegates().add(map);
  }

  private void assertIterator(Set<?> expectedObjects, Collection<?> actualObjects)
  {
    for (Object actualObject : actualObjects)
    {
      if (!expectedObjects.remove(actualObject))
      {
        fail("Unexpected object: " + actualObject); //$NON-NLS-1$
      }
    }

    if (!expectedObjects.isEmpty())
    {
      fail("Missing objects: " + expectedObjects); //$NON-NLS-1$
    }
  }

  private Set<Object> createMapEntries(int start, int count)
  {
    Set<Object> result = new HashSet<>();
    for (int i = 0; i < count; i++)
    {
      int key = start + i;
      result.add(new MapEntry<>(key, "value" + key)); //$NON-NLS-1$
    }

    return result;
  }

  private Set<Object> createKeys(int start, int count)
  {
    Set<Object> result = new HashSet<>();
    for (int i = 0; i < count; i++)
    {
      int key = start + i;
      result.add(key);
    }

    return result;
  }

  private Set<Object> createValues(int start, int count)
  {
    Set<Object> result = new HashSet<>();
    for (int i = 0; i < count; i++)
    {
      int key = start + i;
      result.add("value" + key); //$NON-NLS-1$
    }

    return result;
  }
}
