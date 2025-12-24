/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

/**
 * CDOTransaction.postCommit not adjusting the Transaction/View reference
 * <p>
 * See bug 254489
 *
 * @author Simon McDuff
 */
public class Bugzilla_254489_Test extends AbstractCDOTest
{
  public void testBugzilla_254489() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    // ************************************************************* //
    msg("Opening transaction");
    final CDOTransaction transaction1 = session.openTransaction();
    final CDOTransaction transaction2 = session.openTransaction();

    CDOResource res1 = transaction1.createResource(getResourcePath("/res1"));
    final Company companyA1 = getModel1Factory().createCompany();
    res1.getContents().add(companyA1);

    transaction1.commit();

    transaction2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    CDOResource res2 = transaction2.getResource(getResourcePath("/res1"));
    Company companyA2 = (Company)res2.getContents().get(0);
    final TestAdapter companyA2Adapter = new TestAdapter(companyA2);

    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Adding categories");
    companyA1.getCategories().add(category1A);
    transaction1.commit();

    msg("Checking after commit");
    Notification[] notifications = companyA2Adapter.assertNoTimeout(1);

    Category category2 = (Category)CDOUtil.getEObject((EObject)notifications[0].getNewValue());
    assertNotSame(category2, category1A);
    assertSame(transaction2, CDOUtil.getCDOObject(category2).cdoView());
  }
}
