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
import org.eclipse.net4j.db.IDBAdapterID;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.internal.db.DBAdapterDescriptor;
import org.eclipse.net4j.internal.db.DBAdapterID;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.spi.db.DelegatingDBAdapter;

import junit.framework.TestCase;

/**
 * Focused tests for adapter identity and registry behavior.
 *
 * @author Eike Stepper
 */
public class DBAdapterRegistryTest extends TestCase
{
  public void testVersionedIdentityAndLookup()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.register(new VersionedAdapter("MySQL", "8"));
    registry.register(new VersionedAdapter("mysql", "8.4"));
    registry.register(new VersionedAdapter("mysql", "5.1.5"));

    assertEquals("8.4", registry.get("MYSQL").getVersion());
    assertEquals("8", registry.getAdapter(new DBAdapterID("mysql", "8.0.0")).getVersion());
    assertEquals("8.4", registry.getAdapter("MYSQL", "8.4").getVersion());
    assertEquals(3, registry.getAdapterIDs("mysql").length);
    assertEquals("5.1.5", registry.getAdapters("mysql")[0].getVersion());
    assertEquals("8.4", registry.getAdapters("mysql")[2].getVersion());

    try
    {
      registry.register(new VersionedAdapter("MYSQL", "8.0.0"));
      fail("Expected duplicate logical identity");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }
  }

  public void testMetadataQueryDoesNotInstantiateDescriptor()
  {
    final int[] creations = { 0 };
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.addDescriptor(new DBAdapterDescriptor("h2", "2.4")
    {
      @Override
      public IDBAdapter createDBAdapter()
      {
        ++creations[0];
        return new H2Adapter();
      }
    });

    IDBAdapterID[] ids = registry.getAdapterIDs();
    assertEquals(0, creations[0]);
    assertEquals("h2", ids[0].getName());
    assertEquals("2.4", ids[0].getVersion());

    IDBAdapter adapter = registry.get("h2");
    assertEquals(1, creations[0]);
    assertEquals("2.4", adapter.getVersion());
    assertTrue(adapter instanceof DelegatingDBAdapter);
  }

  public void testNumericVersionSemantics()
  {
    IDBAdapterID eight = new VersionedAdapter("mysql", "8");
    IDBAdapterID eightZero = new VersionedAdapter("MySQL", "8.0.0");
    IDBAdapterID eightFour = new VersionedAdapter("mysql", "08.004.0");
    assertEquals(0, DBAdapterID.copy(eight).compareTo(DBAdapterID.copy(eightZero)));
    assertEquals(0, DBAdapterID.copy(eightFour).compareTo(DBAdapterID.copy(new VersionedAdapter("MYSQL", "8.4"))));
    assertTrue(DBAdapterID.copy(eight).compareTo(DBAdapterID.copy(new VersionedAdapter("mysql", "8.0.1"))) < 0);
  }

  /**
   * @author Eike Stepper
   */
  private static final class VersionedAdapter extends H2Adapter
  {
    public VersionedAdapter(String name, String version)
    {
      super(name, version);
    }
  }
}
