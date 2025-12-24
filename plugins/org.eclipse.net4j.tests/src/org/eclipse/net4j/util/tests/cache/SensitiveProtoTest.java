/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests.cache;

import org.eclipse.net4j.util.ref.KeyedWeakReference;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class SensitiveProtoTest extends AbstractOMTest
{
  @SuppressWarnings("unchecked")
  public void testSensitiveCacheWithEvictionPolicy() throws Exception
  {
    ConcurrentMap<Integer, KeyedAndValuedWeakReference<Integer, String>> map //
        = new ConcurrentHashMap<>();

    ReferenceQueue<String> queue //
        = new ReferenceQueue<>();

    for (int i = 0; i < 20; i++)
    {
      map.put(i, new KeyedAndValuedWeakReference<>(i, String.valueOf(i), queue));
    }

    for (int gc = 0; gc < 10; gc++)
    {
      System.gc();
      sleep(100);
    }

    KeyedAndValuedWeakReference<Integer, String> ref;
    while ((ref = (KeyedAndValuedWeakReference<Integer, String>)queue.poll()) != null)
    {
      int i = ref.getKey();
      System.out.println("Dequeued i=" + i); //$NON-NLS-1$
      if (i < 10)
      {
        map.put(i, new KeyedAndValuedWeakReference<>(i, ref.getValue(), queue));
      }
      else
      {
        map.remove(i, ref);
      }
    }

    assertEquals(10, map.size());
  }

  public static class KeyedAndValuedWeakReference<K, T> extends KeyedWeakReference<K, T>
  {
    private T value;

    public KeyedAndValuedWeakReference(K key, T ref, ReferenceQueue<T> queue)
    {
      super(key, ref, queue);
      value = ref;
    }

    public KeyedAndValuedWeakReference(K key, T ref)
    {
      super(key, ref);
      value = ref;
    }

    public T getValue()
    {
      return value;
    }
  }
}
