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
public class HashMapDelegatingRegistry<K, V> extends DelegatingRegistry<K, V>
{
  private Map<K, V> map;

  public HashMapDelegatingRegistry(IRegistry<K, V> delegate)
  {
    super(delegate);
    map = new HashMap<>();
  }

  public HashMapDelegatingRegistry(IRegistry<K, V> delegate, int initialCapacity)
  {
    super(delegate);
    map = new HashMap<>(initialCapacity);
  }

  public HashMapDelegatingRegistry(IRegistry<K, V> delegate, int initialCapacity, float loadFactor)
  {
    super(delegate);
    map = new HashMap<>(initialCapacity, loadFactor);
  }

  public HashMapDelegatingRegistry(IRegistry<K, V> delegate, Map<? extends K, ? extends V> m)
  {
    super(delegate);
    map = new HashMap<>(m);
  }

  @Override
  protected Map<K, V> getMap()
  {
    return map;
  }
}
