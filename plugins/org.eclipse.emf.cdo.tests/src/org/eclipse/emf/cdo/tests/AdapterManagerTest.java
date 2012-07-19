/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.emf.common.notify.Notification;

import java.lang.ref.WeakReference;

/**
 * @author Simon McDuff
 */
public class AdapterManagerTest extends AbstractCDOTest
{
  public void testStrongReferencePolicy_NONE() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    TestAdapter testAdapter = new TestAdapter();
    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.NONE);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    final CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    companyA.eAdapters().add(testAdapter);

    companyA = null;
    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));
    assertEquals(0, testAdapter.getNotifications().length);

    companyA.setCity("Ottawa");
    Notification[] notifications = testAdapter.getNotifications();
    assertEquals(1, notifications.length); // One EMF notification
  }

  public void testStrongReferencePolicy_ALL() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    TestAdapter testAdapter = new TestAdapter();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    companyA.eAdapters().add(testAdapter);
    companyA = null;

    testAdapter.clearNotifications();

    System.gc();
    assertEquals(true, transaction.isObjectRegistered(id));

    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));

    assertEquals(0, testAdapter.getNotifications().length);
    companyA.setCity("Ottawa");
    assertEquals(1, testAdapter.getNotifications().length);
  }

  public void testStrongReferencePolicy_ALL_AttachObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    TestAdapter testAdapter = new TestAdapter();

    companyA.eAdapters().add(testAdapter);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();

    companyA = null;

    testAdapter.clearNotifications();

    System.gc();
    assertEquals(true, transaction.isObjectRegistered(id));

    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));

    assertEquals(0, testAdapter.getNotifications().length);
    companyA.setCity("Ottawa");
    assertEquals(1, testAdapter.getNotifications().length);
  }

  public void testStrongReferencePolicy_ALL_DetachObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    WeakReference<Company> weakCompanyA = new WeakReference<Company>(companyA);

    TestAdapter testAdapter = new TestAdapter();

    companyA.eAdapters().add(testAdapter);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource(getResourcePath("/resA")).getContents().add(companyA);

    msg("Committing");
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
