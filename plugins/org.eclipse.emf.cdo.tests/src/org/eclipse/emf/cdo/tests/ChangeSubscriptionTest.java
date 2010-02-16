/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionTest extends AbstractCDOTest
{
  public void testSameSession() throws Exception
  {
    testSameSession(CDOAdapterPolicy.ALL);
  }

  public void testSameSession_WithoutPolicy() throws Exception
  {
    testSameSession(null);
  }

  private void testSameSession(final CDOAdapterPolicy policy) throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();

    if (policy != null)
    {
      transaction.options().addChangeSubscriptionPolicy(policy);
    }

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();
    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    // ************************************************************* //

    msg("Opening view");
    final CDOTransaction transaction2 = session.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    // Be sure the threading is done before changing the policy... since if we change the policy before the
    // notifications happens... it will not produce the desire effect!
    Thread.sleep(1000);

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return policy == CDOAdapterPolicy.ALL && adapter.getNotifications().size() == 1 || policy == null
            && adapter.getNotifications().size() == 0;
      }
    }.assertNoTimeOut();

    // Switching policy to the other
    transaction.options().removeChangeSubscriptionPolicy(policy);
    final CDOAdapterPolicy enabled2 = policy == CDOAdapterPolicy.ALL ? null : CDOAdapterPolicy.ALL;
    if (enabled2 != null)
    {
      transaction.options().addChangeSubscriptionPolicy(enabled2);
    }

    adapter.getNotifications().clear();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    // Be sure the threading is done before changing the policy... since if we change the policy before the
    // notifications happens... it will not produce the desire effect!
    Thread.sleep(1000);

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return enabled2 == CDOAdapterPolicy.ALL && adapter.getNotifications().size() == 1 || enabled2 == null
            && adapter.getNotifications().size() == 0;
      }
    }.assertNoTimeOut();
  }

  public void testSeparateSession() throws Exception
  {
    testSeparateSession(CDOAdapterPolicy.ALL);
  }

  public void testSeparateSession_WithoutPolicy() throws Exception
  {
    testSeparateSession(null);
  }

  private void testSeparateSession(final CDOAdapterPolicy policy) throws Exception
  {
    Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    if (policy != null)
    {
      transaction.options().addChangeSubscriptionPolicy(policy);
    }

    CDOResource resourceA = transaction.createResource("/test1");
    resourceA.getContents().add(companyA);
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    // ************************************************************* //

    CDOSession session2 = openModel1Session();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(),
        true));
    category1B.setName("CHANGED NAME");
    assertEquals(0, adapter.getNotifications().size());

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return policy == CDOAdapterPolicy.ALL && adapter.getNotifications().size() == 1 || policy == null
            && adapter.getNotifications().size() == 0;
      }
    }.assertNoTimeOut();

    // Switching policy to the other
    transaction.options().removeChangeSubscriptionPolicy(policy);
    final CDOAdapterPolicy enabled2 = policy == CDOAdapterPolicy.ALL ? null : CDOAdapterPolicy.ALL;
    if (enabled2 != null)
    {
      transaction.options().addChangeSubscriptionPolicy(enabled2);
    }

    adapter.getNotifications().clear();

    category1B.setName("CHANGED NAME_VERSION 2");
    assertEquals(0, adapter.getNotifications().size());

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return enabled2 == CDOAdapterPolicy.ALL && adapter.getNotifications().size() == 1 || enabled2 == null
            && adapter.getNotifications().size() == 0;
      }
    }.assertNoTimeOut();
  }

  public void testTemporaryObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");

    final TestAdapter adapter = new TestAdapter();
    category1A.eAdapters().add(adapter);

    transaction.commit();

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openModel1Session();
    final CDOTransaction transaction2 = session2.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 1;
      }
    }.assertNoTimeOut();
  }

  public void testSeparateSession_CUSTOM() throws Exception
  {
    CDOIDFilterChangeSubscriptionPolicy customPolicy = new CDOIDFilterChangeSubscriptionPolicy();

    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();

    transaction.options().addChangeSubscriptionPolicy(customPolicy);

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();

    customPolicy.getCdoIDs().add(CDOUtil.getCDOObject(category1A).cdoID());

    category1A.eAdapters().add(adapter);
    companyA.eAdapters().add(adapter);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openModel1Session();
    final CDOTransaction transaction2 = session2.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A)
        .cdoID(), true));
    final Company company1B = (Company)CDOUtil.getEObject(transaction2.getObject(
        CDOUtil.getCDOObject(companyA).cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");
    company1B.setName("TEST1");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 1;
      }
    }.assertNoTimeOut();

    // Switching policy to the other
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    adapter.getNotifications().clear();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");
    company1B.setName("TEST2");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 2;
      }
    }.assertNoTimeOut();
  }

  public void testNotificationChain() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //

    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();

    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    final TestAdapter adapter = new TestAdapter();

    companyA.eAdapters().add(adapter);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openModel1Session();
    final CDOTransaction transaction2 = session2.openTransaction();

    final Company company1B = (Company)CDOUtil.getEObject(transaction2.getObject(
        CDOUtil.getCDOObject(companyA).cdoID(), true));

    msg("Changing name");
    company1B.setName("TEST1");
    company1B.setCity("CITY1");

    final Category category2B = getModel1Factory().createCategory();
    company1B.getCategories().add(category2B);

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 3;
      }
    }.assertNoTimeOut();

    int count = 0;
    for (Notification notification : adapter.getNotifications())
    {
      CDODeltaNotification cdoNotification = (CDODeltaNotification)notification;
      if (adapter.getNotifications().size() - 1 == count)
      {
        assertEquals(false, cdoNotification.hasNext());
      }
      else
      {
        assertEquals(true, cdoNotification.hasNext());
      }

      if (notification.getFeature() == getModel1Package().getCategory_Name())
      {
        assertEquals(Notification.SET, notification.getEventType());
        assertEquals("TEST1", notification.getNewStringValue());
      }
      else if (notification.getFeature() == getModel1Package().getAddress_City())
      {
        assertEquals(Notification.SET, notification.getEventType());
        assertEquals("CITY1", notification.getNewStringValue());
      }
      else if (notification.getFeature() == getModel1Package().getCompany_Categories())
      {
        assertEquals(Notification.ADD, notification.getEventType());
        assertEquals(1, notification.getPosition());
        assertEquals(transaction.getObject(CDOUtil.getCDOObject(category2B).cdoID(), true), notification.getNewValue());
      }
      else
      {
        assertEquals(false, false);
      }

      count++;
    }
  }

  /**
   * @author Simon McDuff
   */
  private static class TestAdapter implements Adapter
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    private Notifier notifier;

    public TestAdapter()
    {
    }

    public Notifier getTarget()
    {
      return notifier;
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
      notifier = newTarget;
    }
  }

  /**
   * @author Simon McDuff
   */
  private class CDOIDFilterChangeSubscriptionPolicy implements CDOAdapterPolicy
  {
    private Set<CDOID> cdoIDs = new HashSet<CDOID>();

    public CDOIDFilterChangeSubscriptionPolicy()
    {
    }

    public boolean isValid(EObject eObject, Adapter object)
    {
      return cdoIDs.contains(((InternalCDOObject)eObject).cdoID());
    }

    public Set<CDOID> getCdoIDs()
    {
      return cdoIDs;
    }
  }
}
