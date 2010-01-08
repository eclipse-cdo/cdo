/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.net4j.util.ref.ReferenceType;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class AdapterManagerTest extends AbstractCDOTest
{
  public void testAdapterPolicy_NONE() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    TestAdapter testAdapter = new TestAdapter();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.NONE);

    transaction.createResource("/resA").getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    companyA.eAdapters().add(testAdapter);
    companyA = null;

    System.gc();

    assertEquals(false, transaction.isObjectRegistered(id));
    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));
    assertEquals(0, testAdapter.getNotifications().size());
    companyA.setCity("Ottawa");
    assertEquals(0, testAdapter.getNotifications().size());
  }

  public void testAdapterPolicy_ALL() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    TestAdapter testAdapter = new TestAdapter();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    transaction.createResource("/resA").getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    companyA.eAdapters().add(testAdapter);
    companyA = null;

    testAdapter.getNotifications().clear();
    System.gc();

    assertEquals(true, transaction.isObjectRegistered(id));
    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));

    assertEquals(0, testAdapter.getNotifications().size());
    companyA.setCity("Ottawa");
    assertEquals(1, testAdapter.getNotifications().size());
  }

  public void testAdapterPolicy_AttachObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

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

    transaction.createResource("/resA").getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();

    companyA = null;

    testAdapter.getNotifications().clear();
    System.gc();

    assertEquals(true, transaction.isObjectRegistered(id));
    companyA = (Company)CDOUtil.getEObject(transaction.getObject(id));

    assertEquals(0, testAdapter.getNotifications().size());
    companyA.setCity("Ottawa");
    assertEquals(1, testAdapter.getNotifications().size());
  }

  public void testAdapterPolicy_DetachObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

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

    transaction.createResource("/resA").getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    companyA = null;

    testAdapter.getNotifications().clear();
    System.gc();

    assertEquals(true, transaction.isObjectRegistered(id));
    Company companyB = (Company)CDOUtil.getEObject(transaction.getObject(id));
    assertEquals(companyB, weakCompanyA.get());
    companyB.setCity("Ottawa");
    transaction.getResource("/resA").getContents().remove(0);
    transaction.commit();
    companyB = null;

    testAdapter.getNotifications().clear();
    System.gc();

    assertTrue(weakCompanyA.get() == null);
  }

  public void testAdapterPolicy_ChangePolicy() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();

    TestAdapter testAdapter = new TestAdapter();
    companyA.eAdapters().add(testAdapter);

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    transaction.createResource("/resA").getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(companyA).cdoID();
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);
    companyA = null;

    testAdapter.getNotifications().clear();
    System.gc();

    assertEquals(true, transaction.isObjectRegistered(id));
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.NONE);

    System.gc();

    assertEquals(false, transaction.isObjectRegistered(id));
  }

  /**
   * @author Simon McDuff
   */
  private static class TestAdapter implements Adapter
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    public TestAdapter()
    {
    }

    public Notifier getTarget()
    {
      return null;
    }

    public List<Notification> getNotifications()
    {
      return notifications;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      notifications.add(notification);
    }

    public void setTarget(Notifier newTarget)
    {
    }
  }
}
