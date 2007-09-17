/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.cache;

import org.eclipse.net4j.util.cache.ICache;
import org.eclipse.net4j.util.cache.ICacheMonitor;
import org.eclipse.net4j.util.cache.ICacheRegistration;

/**
 * @author Eike Stepper
 */
public class CacheRegistration implements ICacheRegistration
{
  private ICacheMonitor cacheMonitor;

  private ICache cache;

  public CacheRegistration(ICacheMonitor cacheMonitor, ICache cache)
  {
    this.cacheMonitor = cacheMonitor;
    this.cache = cache;
  }

  public void dispose()
  {
    cacheMonitor = null;
    cache = null;
  }

  public ICacheMonitor getCacheMonitor()
  {
    return cacheMonitor;
  }

  public ICache getCache()
  {
    return cache;
  }
}
