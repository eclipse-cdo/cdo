/*
 * Copyright (c) 2014, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.util.URIHandlerRegistry;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.view.CDOURIHandler;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

/**
 * Bug 430251 - Memory leak on ResourceSet.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_430251_Test extends AbstractCDOTest
{
  /**
   * When the CDOTransaction is closed the ResourceSet should not reference it yet again,
   * because the CDOTransaction has a reference to the CDOSession which references the CDORevisionCache which can take many memory.
   */
  public void testMemoryLeakOnResourceSet() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    EList<URIHandler> uriHandlers = resourceSet.getURIConverter().getURIHandlers();
    assertNull(getCDOURIHandler(uriHandlers));

    CDOSession cdoSession = openSession();
    CDOTransaction cdoTransaction = cdoSession.openTransaction(resourceSet);
    assertNotNull(getCDOURIHandler(uriHandlers));

    cdoTransaction.close();
    assertNull(getCDOURIHandler(uriHandlers));
  }

  public void testURIHandlerRegistryInstallation()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    URIConverter uriConverter = resourceSet.getURIConverter();
    EList<URIHandler> uriHandlers = uriConverter.getURIHandlers();
    uriHandlers.clear();

    URIHandler specializedHandler = new URIHandlerImpl()
    {
      @Override
      public boolean canHandle(URI uri)
      {
        return false;
      }
    };

    URIHandler fallbackHandler = new URIHandlerImpl();
    uriHandlers.add(specializedHandler);
    uriHandlers.add(fallbackHandler);

    assertTrue(URIHandlerRegistry.INSTANCE.installTo(uriConverter));
    assertFalse(URIHandlerRegistry.INSTANCE.installTo(uriConverter));
    assertSame(specializedHandler, uriHandlers.get(0));
    assertSame(URIHandlerRegistry.INSTANCE, uriHandlers.get(1));
    assertSame(fallbackHandler, uriHandlers.get(2));

    assertTrue(URIHandlerRegistry.INSTANCE.uninstallFrom(resourceSet));
    assertFalse(URIHandlerRegistry.INSTANCE.uninstallFrom(resourceSet));
    assertSame(specializedHandler, uriHandlers.get(0));
    assertSame(fallbackHandler, uriHandlers.get(1));

    uriHandlers.clear();
    URIHandler fallbackSubclass = new URIHandlerImpl()
    {
    };

    uriHandlers.add(fallbackSubclass);

    assertTrue(URIHandlerRegistry.INSTANCE.installTo(uriConverter));
    assertSame(fallbackSubclass, uriHandlers.get(0));
    assertSame(URIHandlerRegistry.INSTANCE, uriHandlers.get(1));
    assertTrue(URIHandlerRegistry.INSTANCE.uninstallFrom(uriConverter));
  }

  private Object getCDOURIHandler(EList<URIHandler> uriHandlers)
  {
    CDOURIHandler cdoURIHandler = null;

    for (URIHandler uriHandler : uriHandlers)
    {
      if (uriHandler instanceof CDOURIHandler)
      {
        cdoURIHandler = (CDOURIHandler)uriHandler;
        break;
      }
    }

    return cdoURIHandler;
  }
}
