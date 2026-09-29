/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.spi.server.MatchCache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bounded access-order implementation of {@link MatchCache}.
 *
 * @author Eike Stepper
 */
public final class DefaultMatchCache implements MatchCache
{
  private static final int CAPACITY = 4096;

  private final LinkedHashMap<Object, Boolean> entries = new LinkedHashMap<>(CAPACITY, 0.75f, true)
  {
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean removeEldestEntry(Map.Entry<Object, Boolean> eldest)
    {
      return size() > CAPACITY;
    }
  };

  /**
   * Creates an empty bounded cache.
   */
  public DefaultMatchCache()
  {
  }

  @Override
  public synchronized Boolean get(Object key)
  {
    return entries.get(key);
  }

  @Override
  public synchronized void put(Object key, boolean match)
  {
    entries.put(key, Boolean.valueOf(match));
  }
}
