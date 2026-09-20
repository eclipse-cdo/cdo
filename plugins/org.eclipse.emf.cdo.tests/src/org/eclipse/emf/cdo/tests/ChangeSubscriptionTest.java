/*
 * Copyright (c) 2008-2012, 2016, 2019, 2021, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
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
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    Company company1 = getModel1Factory().createCompany();
    company1.getCategories().add(category1);

    CDOSession session1 = openSession();

    // ************************************************************* //

    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    resource1.getContents().add(company1);

    transaction1.commit();
    TestAdapter adapter1 = new TestAdapter(category1);

    // ************************************************************* //

    CDOTransaction transaction2 = session1.openTransaction();

    Category category2 = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1).cdoID(), true));
    category2.setName("CHANGED NAME");
    adapter1.assertNotifications(0);

    transaction2.commit();
    adapter1.assertNoTimeout(1);

    // Removing policy
    transaction1.options().removeChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter1.clearNotifications();

    category2.setName("CHANGED NAME_VERSION 2");
    adapter1.assertNotifications(0);

    transaction2.commit();
    adapter1.assertNoTimeout(1);
  }

  public void testSameSession_WithoutPolicy() throws Exception
  {
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    Company company1 = getModel1Factory().createCompany();
    company1.getCategories().add(category1);

    CDOSession session1 = openSession();

    // ************************************************************* //

    CDOTransaction transaction1 = session1.openTransaction();

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    resource1.getContents().add(company1);

    transaction1.commit();
    TestAdapter adapter1 = new TestAdapter(category1);

    // ************************************************************* //

    CDOTransaction transaction2 = session1.openTransaction();

    Category category2 = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1).cdoID(), true));
    category2.setName("CHANGED NAME");
    adapter1.assertNotifications(0);

    transaction2.commit();
    adapter1.assertNoTimeout(1);

    // Adding policy
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter1.clearNotifications();

    category2.setName("CHANGED NAME_VERSION 2");
    adapter1.assertNotifications(0);

    transaction2.commit();
    adapter1.assertNoTimeout(1);
  }

  public void testSeparateSession() throws Exception
  {
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    Company company1 = getModel1Factory().createCompany();
    company1.getCategories().add(category1);

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    resource1.getContents().add(company1);
    transaction1.commit();

    TestAdapter adapter1 = new TestAdapter(category1);

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category2 = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1).cdoID(), true));
    category2.setName("CHANGED NAME");
    adapter1.assertNotifications(0);

    transaction2.commit();
    adapter1.assertNoTimeout(1);

    // Removing policy
    transaction1.options().removeChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter1.clearNotifications();

    category2.setName("CHANGED NAME_VERSION 2");
    adapter1.assertNotifications(0);

    commitAndSync(transaction2, transaction1);
    adapter1.assertNotifications(0);
  }

  public void testSeparateSession_WithoutPolicy() throws Exception
  {
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    Company company1 = getModel1Factory().createCompany();
    company1.getCategories().add(category1);

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    resource1.getContents().add(company1);
    transaction1.commit();

    TestAdapter adapter1 = new TestAdapter(category1);

    // ************************************************************* //

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category2 = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1).cdoID(), true));
    category2.setName("CHANGED NAME");
    adapter1.assertNotifications(0);

    commitAndSync(transaction2, transaction1);
    adapter1.assertNotifications(0);

    // Adding policy
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    adapter1.clearNotifications();

    category2.setName("CHANGED NAME_VERSION 2");
    adapter1.assertNotifications(0);

    transaction2.commit();
    adapter1.assertNoTimeout(1);
  }

  public void testTemporaryObject() throws Exception
  {
    msg("Opening session1");
    CDOSession session1 = openSession();

    // ************************************************************* //

    msg("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    msg("Creating company");
    Company company1 = getModel1Factory().createCompany();

    msg("Adding categories");
    company1.getCategories().add(category1);

    msg("Opening transaction1");
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    msg("Creating resource");
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resource1.getContents().add(company1);

    msg("Committing");

    TestAdapter adapter1 = new TestAdapter(category1);

    transaction1.commit();

    // ************************************************************* //

    msg("Opening view");
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    Category category2 = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1).cdoID(), true));

    msg("Changing name");
    category2.setName("CHANGED NAME");
    adapter1.assertNotifications(0);

    msg("Committing");
    commitAndSync(transaction2, transaction1);

    msg("Checking after commit");
    adapter1.assertNotifications(1);
  }

  public void testSeparateSession_CUSTOM() throws Exception
  {
    CDOIDFilterChangeSubscriptionPolicy customPolicy = new CDOIDFilterChangeSubscriptionPolicy();

    msg("Opening session1");
    CDOSession session1 = openSession();

    // ************************************************************* //

    msg("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    msg("Creating company");
    Company company1 = getModel1Factory().createCompany();

    msg("Adding categories");
    company1.getCategories().add(category1);

    msg("Opening transaction1");
    CDOTransaction transaction1 = session1.openTransaction();

    transaction1.options().addChangeSubscriptionPolicy(customPolicy);

    msg("Creating resource");
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resource1.getContents().add(company1);

    msg("Committing");
    transaction1.commit();

    customPolicy.getCdoIDs().add(CDOUtil.getCDOObject(category1).cdoID());

    TestAdapter adapter1 = new TestAdapter(category1, company1);

    // ************************************************************* //

    msg("Opening view");
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Category category2 = (Category)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(category1).cdoID(), true));
    Company company2 = (Company)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(company1).cdoID(), true));

    msg("Changing name");
    category2.setName("CHANGED NAME");
    company2.setName("TEST1");

    adapter1.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    adapter1.assertNoTimeout(1);

    // Switching policy to the other
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    adapter1.clearNotifications();

    msg("Changing name");
    category2.setName("CHANGED NAME_VERSION 2");
    company2.setName("TEST2");

    adapter1.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    adapter1.assertNoTimeout(2);
  }

  public void testNotificationChain() throws Exception
  {
    msg("Opening session1");
    CDOSession session1 = openSession();

    // ************************************************************* //

    msg("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    msg("Creating company");
    Company company1 = getModel1Factory().createCompany();

    msg("Adding categories");
    company1.getCategories().add(category1);

    msg("Opening transaction1");
    CDOTransaction transaction1 = session1.openTransaction();

    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    msg("Creating resource");
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resource1.getContents().add(company1);

    msg("Committing");
    transaction1.commit();

    TestAdapter adapter1 = new TestAdapter(company1);

    // ************************************************************* //

    msg("Opening view");
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    Company company2 = (Company)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(company1).cdoID(), true));

    msg("Changing name");
    company2.setName("TEST1");
    company2.setCity("CITY1");

    Category category2B = getModel1Factory().createCategory();
    company2.getCategories().add(category2B);

    adapter1.assertNotifications(0);

    msg("Committing");
    transaction2.commit();

    msg("Checking after commit");
    Notification[] notifications = adapter1.assertNoTimeout(3);

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
        assertEquals(transaction1.getObject(CDOUtil.getCDOObject(category2B).cdoID(), true), notification.getNewValue());
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
    List<Category> categories = new ArrayList<>();
    categories.add(getModel1Factory().createCategory());

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().addAll(categories);
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0);

    Object[] strongRefs = company2.getCategories().toArray(); // Keep those in memory
    msg(strongRefs);

    TestAdapter adapter2 = new TestAdapter(company2);

    company.getCategories().removeAll(categories);
    commitAndSync(transaction1, view);

    for (Notification notification : adapter2.getNotifications())
    {
      if (notification.getEventType() == Notification.REMOVE && notification.getFeature() == getModel1Package().getCompany_Categories())
      {
        assertInstanceOf(Category.class, CDOUtil.getEObject((EObject)notification.getOldValue()));
        break;
      }
    }
  }

  public void testRemoveManyContained() throws Exception
  {
    List<Category> categories = new ArrayList<>();
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

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0);

    Object[] strongRefs = company2.getCategories().toArray(); // Keep those in memory
    msg(strongRefs);

    TestAdapter adapter2 = new TestAdapter(company2);

    company.getCategories().removeAll(categories);
    commitAndSync(transaction1, view);
    adapter2.assertNotifications(categories.size());
  }

  public void testRemoveXRef() throws Exception
  {
    List<OrderDetail> details = new ArrayList<>();
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

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Product1 product2 = (Product1)resource2.getContents().get(0);

    Object[] strongRefs = product2.getOrderDetails().toArray(); // Keep those in memory
    msg(strongRefs);

    TestAdapter adapter2 = new TestAdapter(product2);

    details.remove(0);
    details.remove(0);
    details.remove(0);
    details.remove(1);
    details.remove(1);
    details.remove(1);
    product.getOrderDetails().removeAll(details);

    commitAndSync(transaction1, view);

    for (Notification notification : adapter2.getNotifications())
    {
      if (notification.getEventType() == Notification.REMOVE && notification.getFeature() == getModel1Package().getProduct1_OrderDetails())
      {
        assertInstanceOf(OrderDetail.class, CDOUtil.getEObject((EObject)notification.getOldValue()));
        break;
      }
    }
  }

  public void testRemoveManyXRef() throws Exception
  {
    List<OrderDetail> details = new ArrayList<>();
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

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource2 = view.getResource(getResourcePath("/test1"));
    Product1 product2 = (Product1)resource2.getContents().get(0);

    Object[] strongRefs = product2.getOrderDetails().toArray(); // Keep those in memory
    msg(strongRefs);

    TestAdapter adapter2 = new TestAdapter(product2);

    details.remove(0);
    details.remove(0);
    details.remove(0);
    details.remove(4);
    details.remove(4);
    details.remove(4);
    product.getOrderDetails().removeAll(details);

    commitAndSync(transaction1, view);
    adapter2.assertNotifications(details.size());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testRemoteDeltaOldValueRequirements() throws Exception
  {
    Product1 product = getModel1Factory().createProduct1();
    List<OrderDetail> details = new ArrayList<>();
    for (int i = 0; i < 10; i++)
    {
      details.add(getModel1Factory().createOrderDetail());
    }
    product.getOrderDetails().addAll(details);
    product.getOtherVATs().add(VAT.VAT0);
    product.getOtherVATs().add(VAT.VAT7);
    product.getOtherVATs().add(VAT.VAT15);

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(product);
    resource.getContents().addAll(details);
    transaction1.commit();

    CDOSession session2 = openSession();
    session2.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(5, 1));
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    EObject product2 = view.getResource(getResourcePath("/test1")).getContents().get(0);
    InternalCDORevision revision2 = (InternalCDORevision)CDOUtil.getCDOObject(product2).cdoRevision();
    CDOList orderDetails2 = revision2.getListOrNull(getModel1Package().getProduct1_OrderDetails());
    CDOList otherVATs2 = revision2.getListOrNull(getModel1Package().getProduct1_OtherVATs());
    assertFalse(orderDetails2.isFullyLoaded());
    assertTrue(otherVATs2.isFullyLoaded());

    TestAdapter adapter2 = new TestAdapter(product2);
    OrderDetail removedDetail1 = details.get(2);
    OrderDetail removedDetail2 = details.get(4);
    CDOID removedDetail1ID = CDOUtil.getCDOObject(removedDetail1).cdoID();
    CDOID removedDetail2ID = CDOUtil.getCDOObject(removedDetail2).cdoID();
    orderDetails2.get(2);
    orderDetails2.get(4);
    product.getOrderDetails().remove(4);
    product.getOrderDetails().remove(2);
    product.getOtherVATs().clear();
    commitAndSync(transaction1, view);

    boolean detailRemoval1 = false;
    boolean detailRemoval2 = false;
    boolean vatClear = false;
    for (Notification notification : adapter2.getNotifications())
    {
      if (notification.getFeature() == getModel1Package().getProduct1_OrderDetails() && notification.getEventType() == Notification.REMOVE)
      {
        Object oldValue = notification.getOldValue();
        CDOID oldID = oldValue instanceof CDOID ? (CDOID)oldValue : oldValue instanceof CDOObject ? ((CDOObject)oldValue).cdoID() : null;
        if (removedDetail1ID.equals(oldID))
        {
          detailRemoval1 = true;
        }
        if (removedDetail2ID.equals(oldID))
        {
          detailRemoval2 = true;
        }
      }
      else if (notification.getFeature() == getModel1Package().getProduct1_OtherVATs() && notification.getEventType() == Notification.REMOVE_MANY)
      {
        assertEquals(3, ((List<?>)notification.getOldValue()).size());
        vatClear = true;
      }
    }

    assertTrue(detailRemoval1);
    assertTrue(detailRemoval2);
    assertTrue(vatClear);

    revision2 = (InternalCDORevision)CDOUtil.getCDOObject(product2).cdoRevision();
    assertFalse(revision2.getListOrNull(getModel1Package().getProduct1_OrderDetails()).isFullyLoaded());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testRemoteAddDoesNotMaterializeOldFeature() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    for (int i = 0; i < 8; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
    }

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction1.commit();

    CDOSession session2 = openSession();
    session2.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 1));
    CDOView view = session2.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    EObject company2 = view.getResource(getResourcePath("/test1")).getContents().get(0);
    InternalCDORevision revision2 = (InternalCDORevision)CDOUtil.getCDOObject(company2).cdoRevision();
    assertFalse(revision2.getListOrNull(getModel1Package().getCompany_Categories()).isFullyLoaded());

    TestAdapter adapter2 = new TestAdapter(company2);
    Category addedCategory = getModel1Factory().createCategory();
    company.getCategories().add(addedCategory);
    commitAndSync(transaction1, view);

    boolean added = false;
    for (Notification notification : adapter2.getNotifications())
    {
      if (notification.getFeature() == getModel1Package().getCompany_Categories() && notification.getEventType() == Notification.ADD)
      {
        assertNull(notification.getOldValue());
        assertEquals(CDOUtil.getCDOObject(addedCategory).cdoID(), CDOUtil.getCDOObject((EObject)notification.getNewValue()).cdoID());
        added = true;
      }
    }

    assertTrue(added);
    revision2 = (InternalCDORevision)CDOUtil.getCDOObject(company2).cdoRevision();
    assertFalse(revision2.getListOrNull(getModel1Package().getCompany_Categories()).isFullyLoaded());
  }

  /**
   * See bug 315409.
   */
  public void testInvalidationWithDeltas_SameBranch() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOView view = session1.openView();

    Company company = getModel1Factory().createCompany();
    company.setName("main-v1");

    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);

    commitAndSync(transaction1, view);

    company.setName("main-v2");
    commitAndSync(transaction1, view);

    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    Company company2 = view.getObject(company);
    company2.eAdapters().add(new AdapterImpl());

    company.setName("main-v3");
    commitAndSync(transaction1, view);

    CDORevision revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(3, revision2.getVersion());
    assertEquals(transaction1.getBranch(), revision2.getBranch());
    assertEquals(transaction1.getLastCommitTime(), revision2.getTimeStamp());

    company.setName("main-v4");
    commitAndSync(transaction1, view);

    revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(4, revision2.getVersion());
    assertEquals(transaction1.getBranch(), revision2.getBranch());
    assertEquals(transaction1.getLastCommitTime(), revision2.getTimeStamp());
  }

  /**
   * See bug 315409.
   */
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void _testInvalidationWithDeltas_SubBranch() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    Company company = getModel1Factory().createCompany();
    company.setName("main-v1");

    CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);

    transaction1.commit();

    company.setName("main-v2");
    transaction1.commit();

    CDOBranch subBranch = transaction1.getBranch().createBranch(getBranchName("SUB_BRANCH"));
    transaction1.setBranch(subBranch);

    CDOView view = session1.openView(subBranch);
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    Company company2 = view.getObject(company);
    company2.eAdapters().add(new AdapterImpl());

    company.setName("sub-v1");
    commitAndSync(transaction1, view);

    CDORevision revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(1, revision2.getVersion());
    assertEquals(transaction1.getBranch(), revision2.getBranch());
    assertEquals(transaction1.getLastCommitTime(), revision2.getTimeStamp());

    company.setName("sub-v2");
    commitAndSync(transaction1, view);

    revision2 = CDOUtil.getCDOObject(company2).cdoRevision();
    assertEquals(2, revision2.getVersion());
    assertEquals(transaction1.getBranch(), revision2.getBranch());
    assertEquals(transaction1.getLastCommitTime(), revision2.getTimeStamp());
  }

  /**
   * @author Simon McDuff
   */
  private class CDOIDFilterChangeSubscriptionPolicy implements CDOAdapterPolicy
  {
    private Set<CDOID> ids = new HashSet<>();

    public CDOIDFilterChangeSubscriptionPolicy()
    {
    }

    @Override
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
