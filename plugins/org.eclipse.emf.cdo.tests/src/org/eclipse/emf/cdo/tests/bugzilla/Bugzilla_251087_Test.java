/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * NPE in ChangeSubscriptionManager.isPending() while subscribing a pending TRANSIENT-by-removal object
 * <p>
 * See bug 251087
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_251087_Test extends AbstractCDOTest
{
  public void testSubscription() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();

    transaction1.options().setInvalidationNotificationEnabled(true);
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));

    Company obj2 = getModel1Factory().createCompany();

    new TestAdapter(obj2);

    res.getContents().add(obj2);
    transaction1.commit();

    res.getContents().remove(obj2);
    res.getContents().add(obj2);
    res.getContents().remove(obj2);
    transaction1.commit();
  }

  public void _testSubscriptionDetached() throws Exception
  {
    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession();
    CDOTransaction transaction1 = sessionA.openTransaction();

    transaction1.options().setInvalidationNotificationEnabled(true);
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    Company obj2 = getModel1Factory().createCompany();
    res.getContents().add(obj2);
    transaction1.commit();

    CDOTransaction transB1 = sessionB.openTransaction();
    CDOID companyID = CDOUtil.getCDOObject(obj2).cdoID();
    Company companyB = (Company)CDOUtil.getEObject(transB1.getObject(companyID));
    transB1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final TestAdapter testAdapter = new TestAdapter(companyB);
    assertEquals(true, ((InternalCDOTransaction)transB1).hasSubscription(companyID));

    res.getContents().remove(obj2);
    transaction1.commit();

    msg("Checking after commit");
    testAdapter.assertNoTimeout(1);

    assertEquals(false, ((InternalCDOTransaction)transB1).hasSubscription(companyID));
  }
}
