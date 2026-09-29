/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.spi.server;

/**
 * Caches boolean results of matcher evaluations.
 * <p>
 * Implementations must be thread-safe. A {@code null} result from {@link #get(Object)} means that the key is not
 * cached; {@link Boolean#TRUE} and {@link Boolean#FALSE} represent cached matches and non-matches, respectively.
 *
 * @author Eike Stepper
 * @since 4.27
 */
public interface MatchCache
{
  /**
   * The product group used to configure match-cache implementations.
   */
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.matchCaches"; //$NON-NLS-1$

  /**
   * Returns the cached result for the given key, or {@code null} if no result is cached.
   *
   * @param key
   *          the matcher result key
   * @return the cached result, or {@code null} on a cache miss
   */
  public Boolean get(Object key);

  /**
   * Caches the result for the given key.
   *
   * @param key
   *          the matcher result key
   * @param match
   *          whether the matcher matched
   */
  public void put(Object key, boolean match);
}
