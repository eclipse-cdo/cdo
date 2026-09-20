/*
 * Copyright (c) 2011, 2012, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Map;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_337587_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testRevisionCompare() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 10; i++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    // ************************************************************* //

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test1"));

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    customer.setName("changed");

    // Read the clean revision without invoking CleanRevisionsMap.get(Object). The clean revision
    // must still contain genuinely unloaded collection slots at this point.
    InternalCDOTransaction internalTransaction = (InternalCDOTransaction)transaction;
    InternalCDORevision cleanCustomerRevision = null;
    for (Map.Entry<?, InternalCDORevision> entry : internalTransaction.getCleanRevisions().entrySet())
    {
      if (entry.getKey() == CDOUtil.getCDOObject(customer))
      {
        cleanCustomerRevision = entry.getValue();
        break;
      }
    }

    assertNotNull(cleanCustomerRevision);
    CDOList cleanSalesOrders = cleanCustomerRevision.getListOrNull(getModel1Package().getCustomer_SalesOrders());
    assertNotNull(cleanSalesOrders);
    assertFalse(cleanSalesOrders.isLoadedAt(1));

    // This is an M0 clean-revision lookup and must not materialize unrelated collection values.
    assertSame(cleanCustomerRevision, internalTransaction.getCleanRevisions().get(CDOUtil.getCDOObject(customer)));
    assertFalse(cleanSalesOrders.isLoadedAt(1));

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    salesOrders.set(5, salesOrder);

    try
    {
      CDOUtil.getCDOObject(customer).cdoRevision().compare(cleanCustomerRevision);
      fail("Expected IllegalStateException during comparing EList with CDOProxyElement");
    }
    catch (IllegalStateException expected)
    {
      // expected exception
    }

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testRollbackDetachedPartiallyLoadedObject() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("rollback"));

    Customer customer1 = getModel1Factory().createCustomer();
    customer1.setName("customer");
    resource1.getContents().add(customer1);
    for (int i = 0; i < 10; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i);
      salesOrder.setCustomer(customer1);
      resource1.getContents().add(salesOrder);
    }

    transaction1.commit();
    session1.close();
    clearCache(getRepository().getRevisionManager());

    CDOSession session2 = openSession();
    session2.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath("rollback"));
    Customer customer2 = (Customer)resource2.getContents().get(0);
    EList<SalesOrder> salesOrders = customer2.getSalesOrders();
    assertEquals(10, salesOrders.size());
    customer2.setName("changed");

    InternalCDOTransaction internalTransaction = (InternalCDOTransaction)transaction2;
    InternalCDORevision cleanRevision = null;
    for (Map.Entry<?, InternalCDORevision> entry : internalTransaction.getCleanRevisions().entrySet())
    {
      if (entry.getKey() == CDOUtil.getCDOObject(customer2))
      {
        cleanRevision = entry.getValue();
        break;
      }
    }

    assertNotNull(cleanRevision);
    CDOList cleanSalesOrders = cleanRevision.getListOrNull(getModel1Package().getCustomer_SalesOrders());
    assertNotNull(cleanSalesOrders);

    // The clean revision is genuinely partial before the detached-object rollback begins.
    assertFalse(cleanSalesOrders.isLoadedAt(5));

    resource2.getContents().remove(customer2);

    transaction2.rollback();

    assertTrue(resource2.getContents().contains(customer2));
    assertEquals("customer", customer2.getName());
    assertEquals(10, customer2.getSalesOrders().size());
    assertNotNull(customer2.getSalesOrders().get(1));
    assertSame(customer2, customer2.getSalesOrders().get(1).getCustomer());
    transaction2.close();
    session2.close();
  }
}
