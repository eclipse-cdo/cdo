/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision.cache;

import org.eclipse.emf.cdo.internal.common.revision.cache.branch.BranchDispatcher;
import org.eclipse.emf.cdo.internal.common.revision.cache.branch.BranchRevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.mem.MEMRevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.two.TwoLevelRevisionCache;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDORevisionCacheUtil
{
  public static final int DEFAULT_CAPACITY_CURRENT = 1000;

  public static final int DEFAULT_CAPACITY_REVISED = 1000;

  private CDORevisionCacheUtil()
  {
  }

  /**
   * Creates and returns a new fixed size cache with two separate LRU (least-recently used) eviction policies for both
   * current revision and revised revisions.
   */
  public static CDORevisionCache createLRUCache(int capacityCurrent, int capacityRevised)
  {
    LRURevisionCache cache = new LRURevisionCache();
    cache.setCapacityCurrent(capacityCurrent);
    cache.setCapacityRevised(capacityRevised);
    return cache;
  }

  /**
   * Creates and returns a new memory sensitive cache.
   */
  public static CDORevisionCache createMEMCache()
  {
    return new MEMRevisionCache();
  }

  /**
   * Creates and returns a new two-level cache.
   */
  public static CDORevisionCache createTwoLevelCache(CDORevisionCache level1, CDORevisionCache level2)
  {
    TwoLevelRevisionCache cache = new TwoLevelRevisionCache();
    cache.setLevel1((InternalCDORevisionCache)level1);
    cache.setLevel2((InternalCDORevisionCache)level2);
    return cache;
  }

  /**
   * Creates and returns a new memory sensitive revision cache that supports branches.
   * 
   * @since 3.0
   */
  public static CDORevisionCache createBranchRevisionCache()
  {
    return new BranchRevisionCache();
  }

  /**
   * Creates and returns a new branch dispatcher cache.
   * 
   * @since 3.0
   */
  public static CDORevisionCache createBranchDispatcher(CDORevisionCacheFactory factory)
  {
    BranchDispatcher cache = new BranchDispatcher();
    cache.setFactory(factory);
    return cache;
  }

  /**
   * Creates and returns a new branch dispatcher cache.
   * 
   * @since 3.0
   */
  public static CDORevisionCache createBranchDispatcher(CDORevisionCache protoType)
  {
    BranchDispatcher cache = new BranchDispatcher();
    cache.setFactory(new CDORevisionCacheFactory.PrototypeInstantiator(protoType));
    return cache;
  }

  /**
   * Creates and returns a new two-level cache with the first level being an LRU cache and the second level being a
   * memory sensitive cache.
   */
  public static CDORevisionCache createDefaultCache(int capacityCurrent, int capacityRevised)
  {
    return createTwoLevelCache(createLRUCache(capacityCurrent, capacityRevised), createMEMCache());
  }

  /**
   * Identical to calling {@link #createBranchRevisionCache() createBranchRevisionCache()} if
   * <code>supportingBranches</code> is <code>true</code>, {@link #createDefaultCache(int, int)
   * createDefaultCache(DEFAULT_CAPACITY_CURRENT, DEFAULT_CAPACITY_REVISED)} otherwise.
   * 
   * @since 3.0
   */
  public static CDORevisionCache createDefaultCache(boolean supportingBranches)
  {
    // if (supportingBranches)
    {
      return createBranchRevisionCache();
    }

    // return createDefaultCache(DEFAULT_CAPACITY_CURRENT, DEFAULT_CAPACITY_REVISED);
  }
}
