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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HashCacheRegistry<ID, E extends IRegistryElement<ID>> extends
    AbstractCachingRegistry<ID, E>
{
  private Map<ID, E> cache;

  public HashCacheRegistry(IRegistry<ID, E> delegate)
  {
    this(delegate, DEFAULT_RESOLVING);
  }

  public HashCacheRegistry(IRegistry<ID, E> delegate, boolean resolving)
  {
    super(delegate, resolving);
    cache = createCache();
  }

  @Override
  protected Map<ID, E> getCache()
  {
    return cache;
  }

  protected Map<ID, E> createCache()
  {
    return new HashMap();
  }
}
