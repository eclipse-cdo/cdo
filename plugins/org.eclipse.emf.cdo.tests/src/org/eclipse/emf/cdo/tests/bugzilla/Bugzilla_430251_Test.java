/*
 * Copyright (c) 2014, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.view.CDOURIHandler;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

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
