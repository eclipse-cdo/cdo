/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.internal.db.DBStoreFactory;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;
import java.util.Collections;

/**
 * Regression tests for configured and automatically detected DB adapters.
 *
 * @author Eike Stepper
 */
public class DBStoreFactoryTest extends PlainTest
{
  public void testExplicitAdapterIsPreserved() throws Exception
  {
    assertNotNull(IDBAdapter.Registry.INSTANCE.getAdapter("h2")); //$NON-NLS-1$
    IDBStore store = createStore(true);
    assertEquals("h2", store.getDBAdapter().getName()); //$NON-NLS-1$
  }

  public void testAdapterIsDetectedFromH2Connection() throws Exception
  {
    assertNotNull(IDBAdapter.Registry.INSTANCE.getAdapter("h2")); //$NON-NLS-1$
    IDBStore store = createStore(false);
    assertEquals("h2", store.getDBAdapter().getName()); //$NON-NLS-1$
  }

  private static IDBStore createStore(boolean explicitAdapter) throws Exception
  {
    String adapter = explicitAdapter ? "<dbAdapter name=\"h2\"/>" : ""; //$NON-NLS-1$ //$NON-NLS-2$
    String xml = "<store>" + adapter //$NON-NLS-1$
        + "<mappingStrategy type=\"horizontal\"/>" //$NON-NLS-1$
        + "<dataSource class=\"org.h2.jdbcx.JdbcDataSource\" URL=\"jdbc:h2:mem:DBStoreFactoryTest;DB_CLOSE_DELAY=-1\"/>" //$NON-NLS-1$
        + "</store>"; //$NON-NLS-1$
    Element configuration = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();

    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      DBStoreFactory factory = new DBStoreFactory();
      factory.setManagedContainer(container);
      IDBStore store = (IDBStore)factory.createStore("test", Collections.emptyMap(), configuration); //$NON-NLS-1$
      return store;
    }
    finally
    {
      container.deactivate();
    }
  }
}
