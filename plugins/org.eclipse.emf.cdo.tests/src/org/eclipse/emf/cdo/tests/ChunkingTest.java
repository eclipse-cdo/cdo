/*
 * Copyright (c) 2007-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.Model5Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ChunkingTest extends AbstractCDOTest
{
  public void testReadNative() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

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
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    int i = 0;
    for (Iterator<SalesOrder> it = salesOrders.iterator(); it.hasNext();)
    {
      IOUtil.OUT().println(i++);
      SalesOrder salesOrder = it.next();
      IOUtil.OUT().println(salesOrder);
    }
  }

  public void testWriteNative() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

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
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

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
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    // ************************************************************* //

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(10));
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

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
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

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

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

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
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

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

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    msg("Creating resource");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

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

  private static final String RESOURCE_PATH = "/test";

  public void testPartiallyLoadedAdd() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().add(9);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  public void testPartiallyLoadedAddAtIndex() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().add(5, 9);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 9, 5, 6, 7, 8);
  }

  public void testPartiallyLoadedSet() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().set(5, 9);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 9, 6, 7, 8);
  }

  public void testPartiallyLoadedRemoveIndex() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().remove(5);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 6, 7, 8);
  }

  private void createInitialList() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = Model5Factory.eINSTANCE.createGenListOfInt();

    list.getElements().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));

    resource.getContents().add(list);

    tx.commit();
    tx.close();
    session.close();

    clearCache(getRepository().getRevisionManager());
  }

  private void testListResult(Integer... expected)
  {
    List<Integer> expectedList = Arrays.asList(expected);

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(RESOURCE_PATH));

    EList<Integer> actualList = ((GenListOfInt)resource.getContents().get(0)).getElements();

    assertEquals("List sizes differ", expectedList.size(), actualList.size());

    for (int index = 0; index < expectedList.size(); index++)
    {
      assertEquals("Entry at index " + index + " differs", expectedList.get(index), actualList.get(index));
    }

    view.close();
    session.close();
  }
}
