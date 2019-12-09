/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;

/**
 * @author Eike Stepper
 */
public class Bugzilla_363287_Test extends AbstractCDOTest
{
  public void testDetach() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

    resource1.getContents().add(company);
    transaction.commit();

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOView view = session2.openTransaction();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));

    final TestAdapter testAdapter = new TestAdapter(resource2);

    // ************************************************************* //

    resource1.getContents().remove(0);
    transaction.commit();

    Notification notification = testAdapter.assertNoTimeout(1)[0];
    Object oldValue = notification.getOldValue();
    assertNull(oldValue);
  }
}
