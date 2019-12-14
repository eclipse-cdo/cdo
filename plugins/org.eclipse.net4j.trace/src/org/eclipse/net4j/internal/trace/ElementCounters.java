/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.trace;

import org.eclipse.net4j.trace.Element.BufferElement;
import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.internal.net4j.buffer.Buffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public final class ElementCounters
{
  public static final ElementCounters INSTANCE = new ElementCounters();

  private final Map<String, AtomicInteger> map = new HashMap<>();

  private ElementCounters()
  {
  }

  public int nextID(String type)
  {
    AtomicInteger counter = map.get(type);
    if (counter == null)
    {
      counter = new AtomicInteger();
      map.put(type, counter);
    }

    return counter.incrementAndGet();
  }

  public void load(Properties properties)
  {
    String lastBufferID = (String)properties.remove(BufferElement.TYPE);
    if (lastBufferID != null)
    {
      AtomicInteger lastID = getBufferCounter();
      lastID.set(Integer.parseInt(lastBufferID));
    }

    map.clear();
    for (Map.Entry<Object, Object> entry : properties.entrySet())
    {
      String type = (String)entry.getKey();
      int counter = Integer.parseInt((String)entry.getValue());
      map.put(type, new AtomicInteger(counter));
    }
  }

  public void save(Properties properties)
  {
    for (Map.Entry<String, AtomicInteger> entry : map.entrySet())
    {
      String type = entry.getKey();
      AtomicInteger counter = entry.getValue();
      properties.setProperty(type, Integer.toString(counter.get()));
    }

    AtomicInteger lastID = getBufferCounter();
    int lastBufferID = lastID.get();
    properties.setProperty(BufferElement.TYPE, Integer.toString(lastBufferID));
  }

  private static AtomicInteger getBufferCounter()
  {
    return (AtomicInteger)ReflectUtil.getValue(ReflectUtil.getField(Buffer.class, "lastID"), null);
  }
}
