/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.internal.db.DBAdapterRegistry;

import java.util.Collections;

import junit.framework.TestCase;

/**
 * Tests the independence of explicitly constructed adapter registries.
 *
 * @author Eike Stepper
 */
public class DBAdapterRegistryConstructionTest extends TestCase
{
  public void testConstructedRegistriesAreIndependentAndEmpty()
  {
    DBAdapterRegistry first = new DBAdapterRegistry();
    DBAdapterRegistry second = new DBAdapterRegistry(4);
    DBAdapterRegistry third = new DBAdapterRegistry(4, 0.75f);
    DBAdapterRegistry fourth = new DBAdapterRegistry(Collections.emptyMap());

    assertTrue(first.isEmpty());
    assertTrue(second.isEmpty());
    assertTrue(third.isEmpty());
    assertTrue(fourth.isEmpty());
    assertEquals(0, first.getAdapterIDs().length);
    assertEquals(0, second.getAdapterIDs().length);
    assertEquals(0, third.getAdapterIDs().length);
    assertEquals(0, fourth.getAdapterIDs().length);
  }
}
