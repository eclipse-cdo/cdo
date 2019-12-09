/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
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
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    final CDOSession session = openSession();

    // ************************************************************* //

    final CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);

    transaction.commit();
    final TestAdapter adapter = new TestAdapter(category1A);

    // ************************************************************* //

    final CDOTransaction transaction2 = session.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true));
    category1B.setName("CHANGED NAME");
    adapter.assertNotifications(0);

    transaction2.commit();
    adapter.assertNoTimeout(1);

    // Removing policy
    transaction.options().removeChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    adapter.assertNotifications(0);

    transaction2.commit();
    adapter.assertNoTimeout(1);
  }

  public void testSameSession_WithoutPolicy() throws Exception
  {
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    final CDOSession session = openSession();

    // ************************************************************* //

    final CDOTransaction transaction = session.openTransaction();

    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);

    transaction.commit();
    final TestAdapter adapter = new TestAdapter(category1A);

    // ************************************************************* //

    final CDOTransaction transaction2 = session.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true));
    category1B.setName("CHANGED NAME");
    adapter.assertNotifications(0);

    transaction2.commit();
    adapter.assertNoTimeout(1);

    // Adding policy
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    adapter.assertNotifications(0);

    transaction2.commit();
    adapter.assertNoTimeout(1);
  }

  public void testSeparateSession() throws Exception
  {
    Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);
    transaction.commit();

    final TestAdapter adapter = new TestAdapter(category1A);

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true));
    category1B.setName("CHANGED NAME");
    adapter.assertNotifications(0);

    transaction2.commit();
    adapter.assertNoTimeout(1);

    // Removing policy
    transaction.options().removeChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    adapter.assertNotifications(0);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // No change subscription, other session ==> no delta notification
        Notification[] notifications = adapter.getNotifications();
        return notifications.length != 0;
      }
    }.assertTimeOut();
  }

  public void testSeparateSession_WithoutPolicy() throws Exception
  {
    Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);
    transaction.commit();

    final TestAdapter adapter = new TestAdapter(category1A);

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true));
    category1B.setName("CHANGED NAME");
    adapter.assertNotifications(0);

    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        // No change subscription, other session ==> no delta notification
        Notification[] notifications = adapter.getNotifications();
        return notifications.length != 0;
      }
    }.assertTimeOut();

    // Adding policy
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter.clearNotifications();

    category1B.setName("CHANGED NAME_VERSION 2");
    adapter.assertNotifications(0);

    transaction2.commit();
    adapter.assertNoTimeout(1);
  }

  public void testTemporaryObject() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

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
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");

    final TestAdapter adapter = new TestAdapter(category1A);

    transaction.commit();

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");

    adapter.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == 1;
      }
    }.assertNoTimeOut();
  }

  public void testSeparateSession_CUSTOM() throws Exception
  {
    CDOIDFilterChangeSubscriptionPolicy customPolicy = new CDOIDFilterChangeSubscriptionPolicy();

    msg("Opening session");
    final CDOSession session = openSession();

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
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    customPolicy.getCdoIDs().add(CDOUtil.getCDOObject(category1A).cdoID());

    final TestAdapter adapter = new TestAdapter(category1A, companyA);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();

    final Category category1B = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1A).cdoID(), true));
    final Company company1B = (Company)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(companyA).cdoID(), true));

    msg("Changing name");
    category1B.setName("CHANGED NAME");
    company1B.setName("TEST1");

    adapter.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    adapter.assertNoTimeout(1);

    // Switching policy to the other
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    adapter.clearNotifications();

    msg("Changing name");
    category1B.setName("CHANGED NAME_VERSION 2");
    company1B.setName("TEST2");

    adapter.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    adapter.assertNoTimeout(2);
  }

  public void testNotificationChain() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

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
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    final TestAdapter adapter = new TestAdapter(companyA);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();

    final Company company1B = (Company)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(companyA).cdoID(), true));

    msg("Changing name");
    company1B.setName("TEST1");
    company1B.setCity("CITY1");

    final Category category2B = getModel1Factory().createCategory();
    company1B.getCategories().add(category2B);

    adapter.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    Notification[] notifications = adapter.assertNoTimeout(3);

    int count = 0;
    for (Notification notification : notifications)
    {
      CDODeltaNotification cdoNotification = (CDODeltaNotification)notification;
      if (notifications.length - 1 == count)
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

  public void testRemoveContained() throws Exception
  {
    List<Category> categories = new ArrayList<Category>();
    categories.add(getModel1Factory().createCategory());

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().addAll(categories);
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0);

    Object[] strongRefs = company2.getCategories().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter(company2);

    company.getCategories().removeAll(categories);
    transaction.commit();

    final Object[] oldValue = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        for (Notification notification : adapter.getNotifications())
        {
          if (notification.getEventType() == Notification.REMOVE && notification.getFeature() == getModel1Package().getCompany_Categories())
          {
            oldValue[0] = notification.getOldValue();
            return true;
          }
        }

        return false;
      }
    }.assertNoTimeOut();

    assertInstanceOf(Category.class, CDOUtil.getEObject((EObject)oldValue[0]));
  }

  public void testRemoveManyContained() throws Exception
  {
    final List<Category> categories = new ArrayList<Category>();
    categories.add(getModel1Factory().createCategory());
    categories.add(getModel1Factory().createCategory());
    categories.add(getModel1Factory().createCategory());
    categories.add(getModel1Factory().createCategory());

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().addAll(categories);
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0);

    Object[] strongRefs = company2.getCategories().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter(company2);

    company.getCategories().removeAll(categories);
    transaction.commit();

    // TODO Consider to uncomment the following if bug 317144 is addressed in EMF
    // final Object[] oldValue = { null };

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == categories.size();

        // TODO Consider to uncomment the following if bug 317144 is addressed in EMF
        // for (Notification notification : adapter.getNotifications())
        // {
        // if (notification.getEventType() == Notification.REMOVE_MANY
        // && notification.getFeature() == getModel1Package().getCompany_Categories())
        // {
        // oldValue[0] = notification.getOldValue();
        // return true;
        // }
        // }
        //
        // return false;
      }
    }.assertNoTimeOut();

    // TODO Consider to uncomment the following if bug 317144 is addressed in EMF
    // assertInstanceOf(Collection.class, oldValue[0]);
    // assertEquals(categories.size(), ((Collection<?>)oldValue[0]).size());
  }

  public void testRemoveXRef() throws Exception
  {
    List<OrderDetail> details = new ArrayList<OrderDetail>();
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("test1");
    product.getOrderDetails().addAll(details);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Product1 product2 = (Product1)resource2.getContents().get(0);

    Object[] strongRefs = product2.getOrderDetails().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter(product2);

    details.remove(0);
    details.remove(0);
    details.remove(0);
    details.remove(1);
    details.remove(1);
    details.remove(1);
    product.getOrderDetails().removeAll(details);
    transaction.commit();

    final Object[] oldValue = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        for (Notification notification : adapter.getNotifications())
        {
          if (notification.getEventType() == Notification.REMOVE && notification.getFeature() == getModel1Package().getProduct1_OrderDetails())
          {
            oldValue[0] = notification.getOldValue();
            return true;
          }
        }

        return false;
      }
    }.assertNoTimeOut();

    assertInstanceOf(OrderDetail.class, CDOUtil.getEObject((EObject)oldValue[0]));
  }

  public void testRemoveManyXRef() throws Exception
  {
    final List<OrderDetail> details = new ArrayList<OrderDetail>();
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());
    details.add(getModel1Factory().createOrderDetail());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("test1");
    product.getOrderDetails().addAll(details);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Product1 product2 = (Product1)resource2.getContents().get(0);

    Object[] strongRefs = product2.getOrderDetails().toArray(); // Keep those in memory
    msg(strongRefs);

    final TestAdapter adapter = new TestAdapter(product2);

    details.remove(0);
    details.remove(0);
    details.remove(0);
    details.remove(4);
    details.remove(4);
    details.remove(4);
    product.getOrderDetails().removeAll(details);
    transaction.commit();

    // TODO Consider to uncomment the following if bug 317144 is addressed in EMF
    // final Object[] oldValue = { null };

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length == details.size();

        // TODO Consider to uncomment the following if bug 317144 is addressed in EMF
        // for (Notification notification : adapter.getNotifications())
        // {
        // if (notification.getEventType() == Notification.REMOVE_MANY
        // && notification.getFeature() == getModel1Package().getProduct1_OrderDetails())
        // {
        // oldValue[0] = notification.getOldValue();
        // return true;
        // }
        // }
        //
        // return false;
      }
    }.assertNoTimeOut();

    // TODO Consider to uncomment the following if bug 317144 is addressed in EMF
    // assertInstanceOf(Collection.class, oldValue[0]);
    // assertEquals(details.size(), ((Collection<?>)oldValue[0]).size());
  }

  /**
   * See bug 315409.
   */
  public void testInvalidationWithDeltas_SameBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOView view = session.openView();

    Company company = getModel1Factory().createCompany();
    company.setName("main-v1");

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);

    commitAndSync(transaction, view);

    company.setName("main-v2");
    commitAndSync(transaction, view);

    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    Company company2 = view.getObject(company);
    company2.eAdapters().add(new AdapterImpl());

    company.setName("main-v3");
    commitAndSync(transaction, view);

    CDORevision revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(3, revision2.getVersion());
    assertEquals(transaction.getBranch(), revision2.getBranch());
    assertEquals(transaction.getLastCommitTime(), revision2.getTimeStamp());

    company.setName("main-v4");
    commitAndSync(transaction, view);

    revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(4, revision2.getVersion());
    assertEquals(transaction.getBranch(), revision2.getBranch());
    assertEquals(transaction.getLastCommitTime(), revision2.getTimeStamp());
  }

  /**
   * See bug 315409.
   */
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void _testInvalidationWithDeltas_SubBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Company company = getModel1Factory().createCompany();
    company.setName("main-v1");

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);

    transaction.commit();

    company.setName("main-v2");
    transaction.commit();

    CDOBranch subBranch = transaction.getBranch().createBranch("SUB_BRANCH");
    transaction.setBranch(subBranch);

    CDOView view = session.openView(subBranch);
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    Company company2 = view.getObject(company);
    company2.eAdapters().add(new AdapterImpl());

    company.setName("sub-v1");
    commitAndSync(transaction, view);

    CDORevision revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(1, revision2.getVersion());
    assertEquals(transaction.getBranch(), revision2.getBranch());
    assertEquals(transaction.getLastCommitTime(), revision2.getTimeStamp());

    company.setName("sub-v2");
    commitAndSync(transaction, view);

    revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(2, revision2.getVersion());
    assertEquals(transaction.getBranch(), revision2.getBranch());
    assertEquals(transaction.getLastCommitTime(), revision2.getTimeStamp());
  }

  /**
   * @author Simon McDuff
   */
  private class CDOIDFilterChangeSubscriptionPolicy implements CDOAdapterPolicy
  {
    private Set<CDOID> ids = new HashSet<CDOID>();

    public CDOIDFilterChangeSubscriptionPolicy()
    {
    }

    public boolean isValid(EObject eObject, Adapter object)
    {
      return ids.contains(((InternalCDOObject)eObject).cdoID());
    }

    public Set<CDOID> getCdoIDs()
    {
      return ids;
    }
  }
}
