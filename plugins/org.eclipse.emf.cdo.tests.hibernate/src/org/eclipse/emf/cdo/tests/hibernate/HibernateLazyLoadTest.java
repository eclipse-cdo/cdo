/*
 * Copyright (c) 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.WrappedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Test lazy load behavior of hibernate
 *
 * @author Martin Taal
 */
public class HibernateLazyLoadTest extends AbstractCDOTest
{
  private static final int NUM_OF_PRODUCTS = 10;

  private static final int NUM_OF_CUSTOMERS = 2;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

  private static final int NUM_OF_SUPPLIERS = 5;

  @Override
  public void doSetUp() throws Exception
  {
    org.eclipse.emf.cdo.tests.model1.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(1);
    org.eclipse.emf.cdo.tests.model1.legacy.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(1);
    super.doSetUp();
  }

  @Override
  public void doTearDown() throws Exception
  {
    org.eclipse.emf.cdo.tests.model1.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(0);
    org.eclipse.emf.cdo.tests.model1.legacy.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(0);
    super.doTearDown();
  }

  public void testSimpleQueries() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    session.close();
    session = openSession();
    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    clearCache(getRepository().getRevisionManager());

    {
      msg("Query for products");
      CDOQuery cdoQuery = transaction.createQuery("hql", "from SalesOrder");
      addCacheParameter(cdoQuery);
      cdoQuery.setMaxResults(1);
      final List<SalesOrder> orders = cdoQuery.getResult(SalesOrder.class);
      System.err.println(orders.get(0).getOrderDetails().get(0).getPrice());
      System.err.println(orders.get(0).getCustomer().getName());
      assertEquals(orders.size(), 1);
    }

    transaction.commit();
    enableConsole();
  }

  private void createTestSet(CDOSession session)
  {
    disableConsole();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    fillResource(resource);

    try
    {
      msg("Committing");
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    enableConsole();
  }

  private void fillResource(CDOResource resource)
  {
    msg("Creating Testset");
    final List<Product1> products = new ArrayList<Product1>();
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      products.add(createProduct(i));
    }

    resource.getContents().addAll(products);

    int productCounter = 0;
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      final Customer customer = getModel1Factory().createCustomer();
      customer.setCity("City " + i);
      customer.setName(i + "");
      customer.setStreet("Street " + i);
      resource.getContents().add(customer);

      final List<Product1> customerProducts = products.subList(productCounter, productCounter + NUM_OF_PRODUCTS_CUSTOMER);
      for (int k = 0; k < NUM_OF_SALES_ORDERS; k++)
      {
        resource.getContents().add(createSalesOrder(i * 10 + k, customer, customerProducts));
      }

      productCounter += NUM_OF_PRODUCTS_CUSTOMER;
    }

    final List<Supplier> suppliers = new ArrayList<Supplier>();
    for (int i = 0; i < NUM_OF_SUPPLIERS; i++)
    {
      suppliers.add(createSupplier(i));
    }

    resource.getContents().addAll(suppliers);
  }

  private Supplier createSupplier(int i)
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setCity("City " + i);
    supplier.setName(i + "");
    supplier.setStreet("Street " + i);
    // supplier.setPreferred(false); // will be persisted with its default value
    return supplier;
  }

  private SalesOrder createSalesOrder(int num, Customer customer, List<Product1> products)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    salesOrder.setId(num);
    createOrderDetail(salesOrder, num, products);
    return salesOrder;
  }

  private List<OrderDetail> createOrderDetail(Order order, int index, List<Product1> products)
  {
    final List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
    int count = 0;
    for (Product1 product : products)
    {
      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setPrice(count++ * index * 1.1f);
      orderDetail.setProduct(product);
    }

    return orderDetails;
  }

  private Product1 createProduct(int index)
  {
    Product1 product = getModel1Factory().createProduct1();
    product.setDescription("Description " + index);
    product.setName("" + index);
    if (index < 10)
    {
      product.setVat(VAT.VAT15);
    }
    else
    {
      product.setVat(VAT.VAT7);
    }

    return product;
  }

  protected void addCacheParameter(CDOQuery query)
  {
    query.setParameter(IHibernateStore.CACHE_RESULTS, true);
  }
}
