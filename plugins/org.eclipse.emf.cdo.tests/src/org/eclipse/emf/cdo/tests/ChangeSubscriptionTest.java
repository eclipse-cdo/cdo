/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOChangeSubscriptionPolicy;
import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;

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
    testSameSession(CDOChangeSubscriptionPolicy.ALL);
  }

  public void testSameSession_disable() throws Exception
  {
    testSameSession(CDOChangeSubscriptionPolicy.NONE);
  }

  public void testSameSession(final CDOChangeSubscriptionPolicy enabled) throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.setPassiveUpdateEnabled(false);

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

    transaction.setChangeSubscriptionPolicy(enabled);

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

    final Category category1B = (Category)transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true);

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(10, 100)
    {
      @Override
      protected boolean successful()
      {
        return enabled == CDOChangeSubscriptionPolicy.ALL && adapter.getNotifications().size() == 1
            || enabled == CDOChangeSubscriptionPolicy.NONE && adapter.getNotifications().size() == 0;
      }
    }.timedOut();

    assertEquals(false, timedOut);

    // Switching policy to the other
    final CDOChangeSubscriptionPolicy enabled2 = enabled == CDOChangeSubscriptionPolicy.ALL ? CDOChangeSubscriptionPolicy.NONE
        : CDOChangeSubscriptionPolicy.ALL;

    transaction.setChangeSubscriptionPolicy(enabled2);

    adapter.getNotifications().clear();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    timedOut = new PollingTimeOuter(10, 100)
    {
      @Override
      protected boolean successful()
      {
        return enabled2 == CDOChangeSubscriptionPolicy.ALL && adapter.getNotifications().size() == 1
            || enabled2 == CDOChangeSubscriptionPolicy.NONE && adapter.getNotifications().size() == 0;
      }
    }.timedOut();

    assertEquals(false, timedOut);
  }

  public void testSeparateSession() throws Exception
  {
    testSeparateSession(CDOChangeSubscriptionPolicy.ALL);

  }

  public void testSeparateSession_disable() throws Exception
  {
    testSeparateSession(CDOChangeSubscriptionPolicy.NONE);
  }

  public void testSeparateSession(final CDOChangeSubscriptionPolicy enabled) throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.setPassiveUpdateEnabled(false);

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
    transaction.setChangeSubscriptionPolicy(enabled);

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
    final CDOSession session2 = openModel1Session();
    session2.setPassiveUpdateEnabled(false);

    final CDOTransaction transaction2 = session2.openTransaction();

    final Category category1B = (Category)transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true);

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(10, 100)
    {
      @Override
      protected boolean successful()
      {
        return enabled == CDOChangeSubscriptionPolicy.ALL && adapter.getNotifications().size() == 1
            || enabled == CDOChangeSubscriptionPolicy.NONE && adapter.getNotifications().size() == 0;
      }
    }.timedOut();

    assertEquals(false, timedOut);

    // Switching policy to the other
    final CDOChangeSubscriptionPolicy enabled2 = enabled == CDOChangeSubscriptionPolicy.ALL ? CDOChangeSubscriptionPolicy.NONE
        : CDOChangeSubscriptionPolicy.ALL;

    transaction.setChangeSubscriptionPolicy(enabled2);

    adapter.getNotifications().clear();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    timedOut = new PollingTimeOuter(10, 100)
    {
      @Override
      protected boolean successful()
      {
        return enabled2 == CDOChangeSubscriptionPolicy.ALL && adapter.getNotifications().size() == 1
            || enabled2 == CDOChangeSubscriptionPolicy.NONE && adapter.getNotifications().size() == 0;
      }
    }.timedOut();

    assertEquals(false, timedOut);

  }

  public void testTemporaryObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.setPassiveUpdateEnabled(false);

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
    transaction.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);
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
    session2.setPassiveUpdateEnabled(false);

    final CDOTransaction transaction2 = session2.openTransaction();
    transaction.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);

    final Category category1B = (Category)transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true);

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(10, 100)
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 1;
      }
    }.timedOut();

    assertEquals(false, timedOut);
  }

  public void testSeparateSession_CUSTOM() throws Exception
  {

    CDOIDFilterChangeSubscriptionPolicy customPolicy = new CDOIDFilterChangeSubscriptionPolicy();

    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.setPassiveUpdateEnabled(false);

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

    transaction.setChangeSubscriptionPolicy(customPolicy);

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
    session2.setPassiveUpdateEnabled(false);

    final CDOTransaction transaction2 = session2.openTransaction();

    final Category category1B = (Category)transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true);
    final Company company1B = (Company)transaction2.getObject(CDOUtil.getCDOObject(companyA).cdoID(), true);

    msg("Changing name");
    category1B.setName("CHANGED NAME");
    company1B.setName("TEST1");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(5, 200)
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 1;
      }
    }.timedOut();

    assertEquals(false, timedOut);

    // Switching policy to the other
    transaction.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);

    adapter.getNotifications().clear();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");
    company1B.setName("TEST2");

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    timedOut = new PollingTimeOuter(10, 100)
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 2;
      }
    }.timedOut();

    assertEquals(false, timedOut);

  }

  public void testNotificationChain() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    session.setPassiveUpdateEnabled(false);

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

    transaction.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);

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
    session2.setPassiveUpdateEnabled(false);

    final CDOTransaction transaction2 = session2.openTransaction();

    final Company company1B = (Company)transaction2.getObject(CDOUtil.getCDOObject(companyA).cdoID(), true);

    msg("Changing name");
    company1B.setName("TEST1");
    company1B.setCity("CITY1");

    final Category category2B = getModel1Factory().createCategory();
    company1B.getCategories().add(category2B);

    assertEquals(0, adapter.getNotifications().size());

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(5, 200)
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 3;
      }
    }.timedOut();

    assertEquals(false, timedOut);
    int count = 0;
    for (Notification notification : adapter.getNotifications())
    {
      CDONotification cdoNotification = (CDONotification)notification;

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
  private class CDOIDFilterChangeSubscriptionPolicy implements CDOChangeSubscriptionPolicy
  {
    private Set<CDOID> cdoIDs = new HashSet<CDOID>();

    public CDOIDFilterChangeSubscriptionPolicy()
    {
    }

    public boolean shouldSubscribe(EObject eObject, Adapter object)
    {
      return cdoIDs.contains(((InternalCDOObject)eObject).cdoID());
    }

    public Set<CDOID> getCdoIDs()
    {
      return cdoIDs;
    }
  }
}
