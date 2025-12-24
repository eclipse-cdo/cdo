/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CachedFileMap<K extends Comparable<K>, V> extends SortedFileMap<K, V>
{
  private Map<K, V> cache = new HashMap<>();

  public CachedFileMap(File file, String mode)
  {
    super(file, mode);
  }

  @Override
  public V get(K key)
  {
    V value = cache.get(key);
    if (value == null)
    {
      value = super.get(key);
      cache.put(key, value);
    }

    return value;
  }

  @Override
  public V put(K key, V value)
  {
    cache.put(key, value);
    return super.put(key, value);
  }
}
