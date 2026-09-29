/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.registry.ScopedRegistry;
import org.eclipse.net4j.util.registry.ScopedRegistry.Store;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests {@link ScopedRegistry} isolation and shared storage behavior.
 */
public class ScopedRegistryTest extends AbstractOMTest
{
  public void testBasicIsolationAndMutations()
  {
    Store<String, String> store = new Store<>();
    ScopedRegistry<String, String> a = store.createRegistry();
    ScopedRegistry<String, String> b = store.createRegistry();
    a.put("same", "a"); //$NON-NLS-1$
    b.put("same", "b"); //$NON-NLS-1$
    b.put(null, "null-key"); //$NON-NLS-1$

    assertEquals("a", a.get("same")); //$NON-NLS-1$
    assertEquals("b", b.get("same")); //$NON-NLS-1$
    assertTrue(a.containsKey("same")); //$NON-NLS-1$
    assertFalse(a.containsKey(null));
    assertFalse(a.containsValue("b")); //$NON-NLS-1$
    assertTrue(b.containsValue("null-key")); //$NON-NLS-1$
    assertEquals(1, a.size());
    assertEquals(2, b.size());
    assertFalse(a.isEmpty());

    Map<String, String> additions = new HashMap<>();
    additions.put("extra", "a-extra"); //$NON-NLS-1$ //$NON-NLS-2$
    a.putAll(additions);
    assertEquals(2, a.size());
    assertEquals(2, b.size());
    assertEquals("b", b.remove("same")); //$NON-NLS-1$
    assertEquals("a", a.get("same")); //$NON-NLS-1$
    a.clear();
    assertTrue(a.isEmpty());
    assertEquals("null-key", b.get(null)); //$NON-NLS-1$
  }

  public void testViews()
  {
    Store<String, String> store = new Store<>();
    ScopedRegistry<String, String> a = store.createRegistry();
    ScopedRegistry<String, String> b = store.createRegistry();
    a.put("one", "1"); //$NON-NLS-1$ //$NON-NLS-2$
    a.put("two", "2"); //$NON-NLS-1$ //$NON-NLS-2$
    b.put("one", "other"); //$NON-NLS-1$ //$NON-NLS-2$

    assertEquals(2, a.entrySet().size());
    assertEquals(2, a.keySet().size());
    assertTrue(a.values().contains("1")); //$NON-NLS-1$
    assertFalse(a.values().contains("other")); //$NON-NLS-1$
    Map.Entry<String, String> entry = a.entrySet().iterator().next();
    String key = entry.getKey();
    String oldValue = entry.getValue();
    assertEquals(oldValue, entry.setValue("updated")); //$NON-NLS-1$
    assertEquals("updated", a.get(key)); //$NON-NLS-1$

    assertTrue(a.keySet().remove("two")); //$NON-NLS-1$
    assertEquals("updated", a.remove("one")); //$NON-NLS-1$
    a.put("remove-by-value", "remove-me"); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(a.values().remove("remove-me")); //$NON-NLS-1$
    b.put("remove-by-value", "keep-me"); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(a.isEmpty());
    assertEquals("other", b.get("one")); //$NON-NLS-1$
    assertEquals("keep-me", b.get("remove-by-value")); //$NON-NLS-1$

    a.put("entry", "remove-entry"); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(a.entrySet().remove(new AbstractMap.SimpleEntry<>("entry", "remove-entry"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(a.isEmpty());
    assertEquals("other", b.get("one")); //$NON-NLS-1$
  }

  public void testRegistryEventsAreIndependent()
  {
    Store<String, String> store = new Store<>();
    ScopedRegistry<String, String> a = store.createRegistry();
    ScopedRegistry<String, String> b = store.createRegistry();
    TestListener listenerA = new TestListener(a);
    TestListener listenerB = new TestListener(b);

    a.put("a", "one"); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals(1, listenerA.getEvents().length);
    assertEquals(0, listenerB.getEvents().length);
    b.put("b", "two"); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals(1, listenerA.getEvents().length);
    assertEquals(1, listenerB.getEvents().length);
    assertTrue(listenerA.getEvents()[0] instanceof IContainerEvent<?>);
  }

  public void testConcurrentRegistriesShareStoreSafely() throws Exception
  {
    Store<Integer, Integer> store = new Store<>();
    ScopedRegistry<Integer, Integer> a = store.createRegistry();
    ScopedRegistry<Integer, Integer> b = store.createRegistry();
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(2);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread threadA = writer(a, 0, start, finished, failure);
    Thread threadB = writer(b, 10000, start, finished, failure);
    threadA.start();
    threadB.start();
    start.countDown();
    await(finished);
    threadA.join();
    threadB.join();
    if (failure.get() != null)
    {
      throw new AssertionError(failure.get());
    }

    assertEquals(1000, a.size());
    assertEquals(1000, b.size());
    for (int i = 0; i < 1000; i++)
    {
      assertEquals(Integer.valueOf(i), a.get(i));
      assertEquals(Integer.valueOf(10000 + i), b.get(i));
    }
  }

  private Thread writer(ScopedRegistry<Integer, Integer> registry, int valueOffset, CountDownLatch start, CountDownLatch finished,
      AtomicReference<Throwable> failure)
  {
    return new Thread(() -> {
      try
      {
        await(start);
        for (int i = 0; i < 1000; i++)
        {
          registry.put(i, valueOffset + i);
        }
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        finished.countDown();
      }
    });
  }
}
