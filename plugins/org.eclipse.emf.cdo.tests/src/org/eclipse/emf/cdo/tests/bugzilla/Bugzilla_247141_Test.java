/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOLazyContentAdapter;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * Create a lazy self-attaching adapter for CDOObject
 * <p>
 * See bug 247141
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_247141_Test extends AbstractCDOTest
{
  public void testContentAdapterBehavior() throws Exception
  {
    CDOID id1 = null;
    CDOID id2 = null;
    CDOID id3 = null;
    CDOID id4 = null;
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

      Customer customer1 = getModel1Factory().createCustomer();
      Customer customer2 = getModel1Factory().createCustomer();
      resource1.getContents().add(customer1);
      resource1.getContents().add(customer2);

      CDOResource resource2 = transaction.createResource(getResourcePath("/test2"));

      Customer customer3 = getModel1Factory().createCustomer();
      Customer customer4 = getModel1Factory().createCustomer();
      resource2.getContents().add(customer3);
      resource2.getContents().add(customer4);

      transaction.commit();

      id1 = CDOUtil.getCDOObject(customer1).cdoID();
      id2 = CDOUtil.getCDOObject(customer2).cdoID();
      id3 = CDOUtil.getCDOObject(customer3).cdoID();
      id4 = CDOUtil.getCDOObject(customer4).cdoID();

      transaction.close();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource(getResourcePath("/test1"));
    EContentAdapter adapter = new CDOLazyContentAdapter();
    resource1.eAdapters().add(adapter);

    CDOView view = resource1.cdoView();
    CDOObject object1 = view.getObject(id1);
    CDOObject object2 = view.getObject(id2);

    // resource1 and all its loaded contents should have been adapted
    assertEquals(true, resource1.eAdapters().contains(adapter));
    assertEquals(true, object1.eAdapters().contains(adapter));
    assertEquals(true, object2.eAdapters().contains(adapter));

    // res2 should NOT be adapted, as its not in the content tree of res1
    CDOResource resource2 = transaction.getResource(getResourcePath("/test2"));
    // neither should its children
    CDOObject object3 = view.getObject(id3);
    CDOObject object4 = view.getObject(id4);

    assertEquals(false, resource2.eAdapters().contains(adapter));
    assertEquals(false, object3.eAdapters().contains(adapter));
    assertEquals(false, object4.eAdapters().contains(adapter));

    // Removing adapter
    adapter.unsetTarget(resource1);

    assertEquals(false, object1.eAdapters().contains(adapter));
    assertEquals(false, object2.eAdapters().contains(adapter));
    assertEquals(false, resource1.eAdapters().contains(adapter));
  }

  public void testBehaviorOnUncommittedObjects() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

    Customer customer1 = getModel1Factory().createCustomer();
    Customer customer2 = getModel1Factory().createCustomer();
    resource1.getContents().add(customer1);
    resource1.getContents().add(customer2);

    transaction.commit();

    EContentAdapter adapter = new CDOLazyContentAdapter();
    resource1.eAdapters().add(adapter);

    // resource1 and all its loaded contents should have been adapted
    assertEquals(true, resource1.eAdapters().contains(adapter));
    assertEquals(true, customer1.eAdapters().contains(adapter));
    assertEquals(true, customer2.eAdapters().contains(adapter));

    // Object in "new" state
    Customer customer3 = getModel1Factory().createCustomer();
    Customer customer4 = getModel1Factory().createCustomer();
    resource1.getContents().add(customer3);
    resource1.getContents().add(customer4);

    // should have been adapted, too
    assertEquals(true, customer3.eAdapters().contains(adapter));
    assertEquals(true, customer4.eAdapters().contains(adapter));

    // Removing adapter
    adapter.unsetTarget(resource1);

    // adapters should have been removed
    assertEquals(false, resource1.eAdapters().contains(adapter));
    assertEquals(false, customer1.eAdapters().contains(adapter));
    assertEquals(false, customer2.eAdapters().contains(adapter));
    assertEquals(false, customer3.eAdapters().contains(adapter));
    assertEquals(false, customer4.eAdapters().contains(adapter));
  }
}
