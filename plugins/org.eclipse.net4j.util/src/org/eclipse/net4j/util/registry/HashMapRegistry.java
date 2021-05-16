/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HashMapRegistry<K, V> extends Registry<K, V>
{
  private Map<K, V> map;

  public HashMapRegistry()
  {
    map = new HashMap<>();
  }

  public HashMapRegistry(int initialCapacity)
  {
    map = new HashMap<>(initialCapacity);
  }

  public HashMapRegistry(int initialCapacity, float loadFactor)
  {
    map = new HashMap<>(initialCapacity, loadFactor);
  }

  public HashMapRegistry(Map<? extends K, ? extends V> m)
  {
    map = new HashMap<>(m);
  }

  @Override
  protected Map<K, V> getMap()
  {
    return map;
  }

  /**
   * @author Eike Stepper
   * @since 3.15
   */
  public static class AutoCommit<K, V> extends HashMapRegistry<K, V>
  {
    public AutoCommit()
    {
    }

    public AutoCommit(int initialCapacity, float loadFactor)
    {
      super(initialCapacity, loadFactor);
    }

    public AutoCommit(int initialCapacity)
    {
      super(initialCapacity);
    }

    public AutoCommit(Map<? extends K, ? extends V> m)
    {
      super(m);
    }

    @Override
    public final void setAutoCommit(boolean autoCommit)
    {
      throw new UnsupportedOperationException();
    }
  }
}
