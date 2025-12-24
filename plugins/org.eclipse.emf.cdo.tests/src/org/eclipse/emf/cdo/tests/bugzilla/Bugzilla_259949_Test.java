/*
 * Copyright (c) 2009-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Make timeouts in read-access requests configurable
 * <p>
 * See bug 241464
 *
 * @author Eike Stepper
 */
public class Bugzilla_259949_Test extends AbstractCDOTest
{
  public void testBugzilla_259949() throws Exception
  {
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);

      transaction.commit();
      session.close();
    }

    CDOSession session1 = openSession();
    session1.options().setPassiveUpdateEnabled(false);

    CDOView view1 = session1.openView();
    CDOResource res1 = view1.getResource(getResourcePath("/test1"));
    assertEquals(1, res1.getContents().size());
    assertEquals("customer", ((Customer)res1.getContents().get(0)).getName());

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource res2 = transaction2.getResource(getResourcePath("/test1"));
    assertEquals(1, res2.getContents().size());
    assertEquals("customer", ((Customer)res2.getContents().get(0)).getName());
    res2.getContents().clear();
    transaction2.commit();

    session1.refresh();

    assertEquals(0, res1.getContents().size());

    session1.close();
    session2.close();
  }
}
