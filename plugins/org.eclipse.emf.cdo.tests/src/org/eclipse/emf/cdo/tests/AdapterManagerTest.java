/*
 * Copyright (c) 2008-2013, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.net4j.util.ref.ReferenceType;

import java.lang.ref.WeakReference;

/**
 * @author Simon McDuff
 */
public class AdapterManagerTest extends AbstractCDOTest
{
  public void testStrongReferencePolicy_NONE() throws Exception
  {
    final CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    Company companyA = getModel1Factory().createCompany();

    final CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.NONE);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);
    transaction.commit();

    final CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    TestAdapter testAdapter = new TestAdapter(companyA);

    companyA = null;
    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));
    testAdapter.assertNotifications(0);

    companyA.setCity("Ottawa");
    testAdapter.assertNotifications(1);
  }

  public void testStrongReferencePolicy_ALL() throws Exception
  {
    final CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    Company companyA = getModel1Factory().createCompany();

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();

    TestAdapter testAdapter = new TestAdapter(companyA);

    companyA = null;
    testAdapter.clearNotifications();

    System.gc();
    assertEquals(true, transaction.isObjectRegistered(id));

    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));
    testAdapter.assertNotifications(0);

    companyA.setCity("Ottawa");
    testAdapter.assertNotifications(1);
  }

  public void testStrongReferencePolicy_ALL_AttachObject() throws Exception
  {
    final CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    Company companyA = getModel1Factory().createCompany();
    TestAdapter testAdapter = new TestAdapter(companyA);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);

    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();

    companyA = null;
    testAdapter.clearNotifications();

    System.gc();
    assertEquals(true, transaction.isObjectRegistered(id));

    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));

    testAdapter.assertNotifications(0);
    companyA.setCity("Ottawa");
    testAdapter.assertNotifications(1);
  }

  public void testStrongReferencePolicy_ALL_DetachObject() throws Exception
  {
    final CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    Company companyA = getModel1Factory().createCompany();
    WeakReference<Company> weakCompanyA = new WeakReference<>(companyA);

    TestAdapter testAdapter = new TestAdapter(companyA);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);

    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    companyA = null;

    testAdapter.clearNotifications();

    System.gc();
    assertEquals(true, transaction.isObjectRegistered(id));

    Company companyB = (Company)CDOUtil.getEObject(transaction.getObject(id));
    assertEquals(companyB, weakCompanyA.get());
    companyB.setCity("Ottawa");
    transaction.getResource(getResourcePath("/resA")).getContents().remove(0);
    transaction.commit();
  }
}
