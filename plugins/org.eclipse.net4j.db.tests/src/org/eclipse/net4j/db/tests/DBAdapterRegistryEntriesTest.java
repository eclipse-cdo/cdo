/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;
import org.eclipse.net4j.internal.db.DBAdapterDescriptor;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;

import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests registry entries and duplicate descriptor registration.
 *
 * @author Eike Stepper
 */
public class DBAdapterRegistryEntriesTest extends TestCase
{
  public void testDuplicateDescriptorRegistrationIsRejected()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.addDescriptor(new DBAdapterDescriptor("h2", "2.4")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        return new H2Adapter();
      }
    });

    try
    {
      registry.addDescriptor(new DBAdapterDescriptor("H2", "2.4.0")
      {
        @Override
        public IDBAdapter createDBAdapter()
        {
          return new H2Adapter();
        }
      });
      fail("Expected duplicate descriptor identity");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }
  }

  public void testGetElementsReturnsTypedEntriesInRegistryOrder()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    IDBAdapter mysql = new MYSQLAdapter();
    IDBAdapter h2 = new H2Adapter();
    registry.register(mysql);
    registry.register(h2);

    Map.Entry<String, IDBAdapter>[] elements = registry.getElements();
    assertEquals(2, elements.length);
    assertEquals("h2", elements[0].getKey());
    assertSame(h2, elements[0].getValue());
    assertEquals("mysql", elements[1].getKey());
    assertSame(mysql, elements[1].getValue());
  }
}
