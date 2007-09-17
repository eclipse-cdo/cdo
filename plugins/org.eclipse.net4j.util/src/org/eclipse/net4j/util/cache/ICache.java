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
package org.eclipse.net4j.util.cache;

/**
 * @author Eike Stepper
 */
public interface ICache
{

  public ICacheMonitor getCacheMonitor();

  /**
   * Returns the number of elements in the cache that would currently be subject to eviction.
   */
  public int getEvictableElementCount();

  /**
   * Returns the average size of an element currently in the cache or <code>UNKNOWN_ELEMENT_SIZE</code> if the
   * implementor of this method is not able or willing to calculate the average element size.
   */
  public int getAverageElementSize();

  /**
   * Returns the average time in milliseconds needed to reconstruct an element during a cache miss or
   * <code>UNKNOWN_RECONSTRUCTION_TIME</code> if the implementor of this method is not able or willing to calculate
   * the average reconstruction time.
   */
  public long getAverageReconstructionTime();

  /**
   * Instructs this cache to evict <b>elementCount</b> elements and return the number of actually evicted elements.
   */
  public void evictElements(int elementCount);
}
