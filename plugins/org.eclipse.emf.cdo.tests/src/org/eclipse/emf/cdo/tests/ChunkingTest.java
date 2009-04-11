/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class ChunkingTest extends AbstractCDOTest
{
  public void testReadNative() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
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

    CDOSession session = openModel1Session();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    int i = 0;
    for (Iterator<SalesOrder> it = salesOrders.iterator(); it.hasNext();)
    {
      msg(i++);
      SalesOrder salesOrder = it.next();
      msg(salesOrder);
    }
  }

  public void testWriteNative() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
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

    CDOSession session = openModel1Session();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(salesOrder);
      salesOrders.set(i, salesOrder);
    }

    transaction.commit();
  }

  public void testChunkWithTemporaryObject() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    // ************************************************************* //

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(10));
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");
    resource.getContents().add(customer);
    for (EObject element : resource.getContents())
    {
      msg(element);
    }

    transaction.commit();
  }

  public void testReadAfterUpdateBeforeCommit() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());
    // ************************************************************* //

    CDOSession session = openModel1Session();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");

    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(i, salesOrder);
    }

    for (int i = 70; i < 120; i++)
    {
      SalesOrder saleOrders = (SalesOrder)resource.getContents().get(i);
      assertEquals(i - 20, saleOrders.getId());
    }

    transaction.commit();
  }

  public void testReadAfterUpdateAfterCommit() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());
    // ************************************************************* //

    CDOSession session = openModel1Session();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    msg("Creating resource");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");

    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(i, salesOrder);
    }

    transaction.commit();

    for (int i = 70; i < 120; i++)
    {
      SalesOrder saleOrders = (SalesOrder)resource.getContents().get(i);
      assertEquals(i - 20, saleOrders.getId());
    }
  }
}
