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
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.util.om.OMPlatform;

import junit.framework.TestCase;

/**
 * Tests standalone discovery of DB adapter contributions from workspace classpaths.
 *
 * @author Eike Stepper
 */
public class DBAdapterRegistryStandaloneTest extends TestCase
{
  public void testWorkspaceContributionsAreDiscoveredLazily()
  {
    assertFalse("This test must run as Plain Java", OMPlatform.INSTANCE.isOSGiRunning());

    DBAdapterRegistry registry = DBAdapterRegistry.INSTANCE;
    IDBAdapterID h2ID = findAdapterID(registry, "h2");
    IDBAdapterID postgresqlID = findAdapterID(registry, "postgresql");

    assertNotNull("H2 descriptor was not discovered", h2ID);
    assertNotNull("PostgreSQL descriptor was not discovered", postgresqlID);
    assertEquals("1.1.114", h2ID.getVersion());
    assertEquals("9.0", postgresqlID.getVersion());

    IDBAdapter h2 = registry.getAdapter(h2ID);
    assertNotNull(h2);
    assertEquals("h2", h2.getName());
    assertEquals("1.1.114", h2.getVersion());
    assertEquals("h2", h2.getDatabaseProductNames()[0]);
    assertSame("Adapter lookup must return the cached instance", h2, registry.getAdapter(h2ID));
  }

  private static IDBAdapterID findAdapterID(DBAdapterRegistry registry, String name)
  {
    for (IDBAdapterID id : registry.getAdapterIDs())
    {
      if (name.equalsIgnoreCase(id.getName()))
      {
        return id;
      }
    }

    return null;
  }
}
