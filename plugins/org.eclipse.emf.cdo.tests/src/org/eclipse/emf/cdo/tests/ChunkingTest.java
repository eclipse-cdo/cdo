/*
 * Copyright (c) 2007-2013, 2015, 2016, 2018, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Bernd Fuhrmann - testEnsureChunk for bug 502932
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
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
@Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
public class ChunkingTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test";

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
    GenListOfInt list = Model5Factory.eINSTANCE.createGenListOfInt();
    list.getElements().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));

    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(list);

    tx.commit();
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
      assertEquals("Map.Entry at index " + index + " differs", expectedList.get(index), actualList.get(index));
    }

    view.close();
    session.close();
  }

  public void testRemove() throws CommitException
  {
    // Init model
    Company company = getModel1Factory().createCompany();
    addCategoryAndProducts(company, "Software");

    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(company);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    // Test session
    session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 3));

    tx = session.openTransaction();
    resource = tx.getResource(getResourcePath(RESOURCE_PATH));
    company = (Company)resource.getContents().get(0);

    Category software = company.getCategories().get(0);
    software.getProducts().remove(15);

    tx.commit();
  }

  private void addCategoryAndProducts(Company company, String name)
  {
    Category category = addCategory(company, name);

    for (int i = 0; i < 20; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName(name + "-" + i);
      category.getProducts().add(product);
    }
  }

  private Category addCategory(Company company, String name)
  {
    Category category = getModel1Factory().createCategory();
    category.setName(name);
    company.getCategories().add(category);
    return category;
  }

  /**
   * Bug 502932.
   */
  @Requires("DB")
  public void testEnsureChunk() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      Company company1 = getModel1Factory().createCompany();
      company1.setName("company1");
      resource.getContents().add(company1);

      Company company2 = getModel1Factory().createCompany();
      company2.setName("company2");
      resource.getContents().add(company2);
      for (int i = 0; i < 3000; i++)
      {
        Customer customer = getModel1Factory().createCustomer();
        customer.setName("customer" + i);
        company2.getCustomers().add(customer);
      }

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 10));
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("/test1"));

    Company company1 = (Company)resource.getContents().get(0);
    Company company2 = (Company)resource.getContents().get(1);
    company1.getCustomers();
    company2.getCustomers().get(1);
  }
}
