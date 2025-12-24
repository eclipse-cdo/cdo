/*
 * Copyright (c) 2009-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

/**
 * Change subscription are not registered correctly for new CDOResource objects
 * <p>
 * See bug 266857
 *
 * @author Simon McDuff
 */
public class Bugzilla_266857_Test extends AbstractCDOTest
{
  public void testBugzilla_266857() throws Exception
  {
    CDOSession session = openSession();

    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource1 = transaction.createResource(getResourcePath("test1"));
    final TestAdapter testAdapterForResource = new TestAdapter(resource1);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath("test1"));
    Company company2 = getModel1Factory().createCompany();
    resource2.getContents().add(company2);
    testAdapterForResource.assertNotifications(0);

    transaction2.commit();

    msg("Checking after commit");
    testAdapterForResource.assertNoTimeout(1);
  }
}
