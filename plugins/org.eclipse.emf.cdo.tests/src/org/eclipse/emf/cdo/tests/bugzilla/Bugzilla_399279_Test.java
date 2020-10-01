/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Bug 399279: Tests the support for removing the {@link CDOViewSet} from the adapter list
 * of a {@link ResourceSet}.
 */
public class Bugzilla_399279_Test extends AbstractCDOTest
{
  public void testRemoveAdapterWhileResourcesStillLoaded() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Resource resource = transaction.getOrCreateResource(getResourcePath("test.model5"));
    ResourceSet resourceSet = resource.getResourceSet();
    resource.getContents().add(getModel5Factory().createParent());
    transaction.commit();

    resourceSet.eAdapters().clear(); // Should not throw an exception.
    assertTrue(resourceSet.eAdapters().contains(transaction.getViewSet()));
  }

  public void testRemoveAdapterWhileViewStillOpen() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Resource resource = transaction.getOrCreateResource(getResourcePath("test.model5"));
    ResourceSet resourceSet = resource.getResourceSet();
    resource.getContents().add(getModel5Factory().createParent());
    transaction.commit();

    resource.unload();
    resourceSet.getResources().remove(resource);

    resourceSet.eAdapters().clear(); // Should not throw an exception.
    assertTrue(resourceSet.eAdapters().contains(transaction.getViewSet()));
  }

  public void testRemoveAdapterViewsClosedAndResourcesRemoved() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Resource resource = transaction.getOrCreateResource(getResourcePath("test.model5"));
    ResourceSet resourceSet = resource.getResourceSet();
    resource.getContents().add(getModel5Factory().createParent());
    transaction.commit();

    resource.unload();
    resourceSet.getResources().remove(resource);

    transaction.close();
    resourceSet.eAdapters().clear(); // Should not throw an exception.
    assertFalse(resourceSet.eAdapters().contains(transaction.getViewSet()));
  }
}
