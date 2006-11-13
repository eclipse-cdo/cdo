/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractMappingRegistry<K, V> extends AbstractRegistry<K, V>
{
  public AbstractMappingRegistry()
  {
  }

  @Override
  protected V deregister(Object key)
  {
    return getMap().remove(key);
  }

  @Override
  protected V register(K key, V value)
  {
    return getMap().put(key, value);
  }

  public Set<Entry<K, V>> entrySet()
  {
    return getMap().entrySet();
  }

  public V get(Object key)
  {
    return getMap().get(key);
  }

  public Set<K> keySet()
  {
    return getMap().keySet();
  }

  public Collection<V> values()
  {
    return getMap().values();
  }

  protected abstract Map<K, V> getMap();
}
