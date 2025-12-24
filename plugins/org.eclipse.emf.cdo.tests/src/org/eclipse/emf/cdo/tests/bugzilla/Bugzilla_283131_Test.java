/*
 * Copyright (c) 2011, 2012, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Notification;

/**
 * CDOObjects should send normal EMF notifications on rollback.
 * <p>
 * See bug 283131.
 *
 * @author Pascal Lehmann
 * @since 4.0
 */
public class Bugzilla_283131_Test extends AbstractCDOTest
{
  public void testRollbackSingleNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // create initial model.
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("company1");
    resource.getContents().add(company);
    transaction.commit();

    // do a single change on object without commit.
    company.setName("company2");

    // add adapter.
    final TestAdapter testAdapter = new TestAdapter(company);

    // rollback.
    transaction.rollback();

    // access object.
    company.getName();

    // check rollback notifications.
    assertNoTimeout(DEFAULT_TIMEOUT_EXPECTED, () -> testAdapter.getNotifications().length == 1);
    assertEquals("company1", testAdapter.getNotifications()[0].getNewValue());
  }

  public void testRollbackSingleRemoveNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // create initial model.
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(getModel1Factory().createCustomer());
    resource.getContents().add(company);
    transaction.commit();

    // do add containment to object without commit.
    company.getCustomers().add(getModel1Factory().createCustomer());

    // add adapter.
    final TestAdapter testAdapter = new TestAdapter(company);

    // rollback.
    transaction.rollback();

    // access object.
    company.getName();

    // check rollback notifications.
    assertNoTimeout(DEFAULT_TIMEOUT_EXPECTED, () -> testAdapter.getNotifications().length == 1);
    assertEquals(Notification.REMOVE, testAdapter.getNotifications()[0].getEventType());
    assertEquals(getModel1Package().getCompany_Customers(), testAdapter.getNotifications()[0].getFeature());
  }

  public void testRollbackSingleAddNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // create initial model.
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(getModel1Factory().createCustomer());
    resource.getContents().add(company);
    transaction.commit();

    // do multiple changes on object without commit.
    company.getCustomers().remove(0);

    // add adapter.
    final TestAdapter testAdapter = new TestAdapter(company);

    // rollback.
    transaction.rollback();

    // access object.
    company.getName();

    // check rollback notifications.
    assertNoTimeout(DEFAULT_TIMEOUT_EXPECTED, () -> testAdapter.getNotifications().length == 1);
    assertEquals(Notification.ADD, testAdapter.getNotifications()[0].getEventType());
    assertEquals(getModel1Package().getCompany_Customers(), testAdapter.getNotifications()[0].getFeature());
  }

  public void testRollbackManyNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // create initial model.
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("company1");
    company.setStreet("street1");
    resource.getContents().add(company);
    transaction.commit();

    // do multiple changes on object without commit.
    company.setName("company2");
    company.setStreet("street2");

    // add adapter.
    final TestAdapter testAdapter = new TestAdapter(company);

    // rollback.
    transaction.rollback();

    // access object.
    company.getName();

    // check rollback notifications.
    assertNoTimeout(DEFAULT_TIMEOUT_EXPECTED, () -> testAdapter.getNotifications().length == 2);

    for (Notification notification : testAdapter.getNotifications())
    {
      if (notification.getFeature().equals(getModel1Package().getAddress_Name()))
      {
        assertEquals("company1", notification.getNewValue());
      }
      else if (notification.getFeature().equals(getModel1Package().getAddress_Street()))
      {
        assertEquals("street1", notification.getNewValue());
      }
      else
      {
        fail("Unexpected notification: " + notification);
      }
    }
  }

  public void testRollbackSingleConflictNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // create initial model.
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("company1");
    resource.getContents().add(company);
    transaction.commit();

    // do a single change on object without commit.
    company.setName("company2");

    // create a conflict on object.
    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = transaction2.getObject(company);
    company2.setName("company3");
    commitAndSync(transaction2, transaction);

    // add adapter.
    final TestAdapter testAdapter = new TestAdapter(company);

    // rollback.
    transaction.rollback();

    // access object.
    company.getName();

    // check rollback notifications.
    assertNoTimeout(DEFAULT_TIMEOUT_EXPECTED, () -> testAdapter.getNotifications().length == 1);

    assertEquals("company3", testAdapter.getNotifications()[0].getNewValue());
  }

  public void testRollbackManyConflictNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // create initial model.
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("company1");
    company.setCity("city1");
    company.getCustomers().add(getModel1Factory().createCustomer());
    resource.getContents().add(company);
    transaction.commit();

    // do multiple change on object without commit.
    company.setName("company2");
    company.setCity("city2");
    company.getCustomers().remove(0);

    // create a conflict on object.
    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)CDOUtil.getEObject(transaction2.getObject(company));
    company2.setName("company3");
    company2.setCity("city3");
    company2.getCustomers().add(getModel1Factory().createCustomer());
    commitAndSync(transaction2, transaction);

    // add adapter.
    final TestAdapter testAdapter = new TestAdapter(company);

    // rollback.
    transaction.rollback();

    // access object.
    company.getName();

    // check rollback notifications.
    assertNoTimeout(DEFAULT_TIMEOUT_EXPECTED, () -> testAdapter.getNotifications().length == 4);

    int customerCounter = 0;
    for (Notification notification : testAdapter.getNotifications())
    {
      if (notification.getFeature().equals(getModel1Package().getCompany_Customers()) && notification.getEventType() == Notification.ADD)
      {
        customerCounter++;
      }
      else if (notification.getFeature().equals(getModel1Package().getAddress_Name()))
      {
        assertEquals("company3", notification.getNewValue());
      }
      else if (notification.getFeature().equals(getModel1Package().getAddress_City()))
      {
        assertEquals("city3", notification.getNewValue());
      }
      else
      {
        fail("Unexpected notification: " + notification);
      }
    }

    assertEquals(company.getCustomers().size(), customerCounter);
  }
}
