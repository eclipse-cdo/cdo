/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server.security;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl;
import org.eclipse.emf.cdo.server.internal.security.DefaultPermissionCacheCreator;
import org.eclipse.emf.cdo.server.spi.security.PermissionCache;
import org.eclipse.emf.cdo.server.spi.security.PermissionCacheFactory;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

/**
 * Deterministic unit tests for the built-in permission cache's slot, generation, and capacity behavior.
 *
 * @author Eike Stepper
 */
public class PermissionCacheTest extends TestCase
{
  public void testSlotsAndGenerations()
  {
    PermissionCache.Creator creator = new DefaultPermissionCacheCreator(10);
    PermissionCache first = creator.create(null, "user", null);
    PermissionCache second = creator.create(null, "user", null);
    CDOID id = CDOIDUtil.createLong(1);

    assertNull(first.get(id, false));
    first.put(id, false, CDOPermission.NONE);
    first.put(id, true, CDOPermission.WRITE);
    assertEquals(CDOPermission.NONE, first.get(id, false));
    assertEquals(CDOPermission.WRITE, first.get(id, true));
    assertNull(second.get(id, false));
    assertNull(second.get(id, true));
  }

  public void testSharedCapacityAndLRU()
  {
    PermissionCache.Creator creator = new DefaultPermissionCacheCreator(2);
    PermissionCache generation = creator.create(null, "user", null);
    CDOID first = CDOIDUtil.createLong(1);
    CDOID second = CDOIDUtil.createLong(2);
    CDOID third = CDOIDUtil.createLong(3);

    generation.put(first, false, CDOPermission.READ);
    generation.put(second, false, CDOPermission.READ);
    assertEquals(CDOPermission.READ, generation.get(first, false));
    generation.put(third, false, CDOPermission.READ);

    assertNull(generation.get(second, false));
    assertEquals(CDOPermission.READ, generation.get(first, false));
    assertEquals(CDOPermission.READ, generation.get(third, false));
  }

  public void testCapacityIsSharedAcrossGenerations()
  {
    PermissionCache.Creator creator = new DefaultPermissionCacheCreator(2);
    PermissionCache firstGeneration = creator.create(null, "first", null);
    PermissionCache secondGeneration = creator.create(null, "second", null);
    CDOID first = CDOIDUtil.createLong(4);
    CDOID second = CDOIDUtil.createLong(5);
    CDOID third = CDOIDUtil.createLong(6);

    firstGeneration.put(first, false, CDOPermission.READ);
    secondGeneration.put(second, false, CDOPermission.READ);
    firstGeneration.put(third, false, CDOPermission.READ);

    assertNull(firstGeneration.get(first, false));
    assertEquals(CDOPermission.READ, secondGeneration.get(second, false));
    assertEquals(CDOPermission.READ, firstGeneration.get(third, false));
  }

  public void testConcurrentSlotUpdatesPreserveBothValues() throws Exception
  {
    PermissionCache cache = new DefaultPermissionCacheCreator(10).create(null, "user", null);
    CDOID id = CDOIDUtil.createLong(7);
    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(2);

    Thread contained = new Thread(() -> update(cache, id, false, CDOPermission.READ, ready, start, done));
    Thread node = new Thread(() -> update(cache, id, true, CDOPermission.WRITE, ready, start, done));
    contained.start();
    node.start();
    ready.await();
    start.countDown();
    done.await();

    assertEquals(CDOPermission.READ, cache.get(id, false));
    assertEquals(CDOPermission.WRITE, cache.get(id, true));
  }

  public void testBranchIdentityHashCode()
  {
    CDOBranchImpl first = new CDOBranchImpl(null, 1, "first", null);
    CDOBranchImpl second = new CDOBranchImpl(null, 1, "second", null);

    assertNotSame(first, second);
    assertFalse(first.equals(second));
    assertEquals(System.identityHashCode(first), first.hashCode());
    assertEquals(System.identityHashCode(second), second.hashCode());
  }

  public void testDefaultFactoryCapacityValidation()
  {
    PermissionCacheFactory.Default factory = new PermissionCacheFactory.Default();
    assertNotNull(factory.create("repository:|capacity=7"));

    try
    {
      factory.create("repository:|capacity=0");
      fail("Expected zero capacity to be rejected");
    }
    catch (ProductCreationException expected)
    {
      // Expected.
    }

    try
    {
      factory.create("repository:|capacity=invalid");
      fail("Expected invalid capacity to be rejected");
    }
    catch (ProductCreationException expected)
    {
      // Expected.
    }
  }

  private static void update(PermissionCache cache, CDOID id, boolean resourceNode, CDOPermission permission, CountDownLatch ready, CountDownLatch start,
      CountDownLatch done)
  {
    ready.countDown();

    try
    {
      start.await();
      for (int i = 0; i < 1000; i++)
      {
        cache.put(id, resourceNode, permission);
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
    }
    finally
    {
      done.countDown();
    }
  }
}
