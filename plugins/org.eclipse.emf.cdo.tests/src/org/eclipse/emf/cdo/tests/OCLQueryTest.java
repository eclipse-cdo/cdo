/*
 * Copyright (c) 2010-2013, 2015-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "Query result counting")
public class OCLQueryTest extends AbstractCDOTest
{
  private static final int NUM_OF_PRODUCTS = 20;

  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

  private static final int NUM_OF_PURCHASE_ORDERS = 3;

  private CDOTransaction transaction;

  private CDOResource resource;

  private List<Product1> products = new ArrayList<>();

  private List<Customer> customers = new ArrayList<>();

  private List<OrderDetail> orderDetails = new ArrayList<>();

  private List<SalesOrder> salesOrders = new ArrayList<>();

  private int objectCount;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipStoreWithoutHandleRevisions();

    CDOSession session = openSession();
    transaction = session.openTransaction();
    resource = createTestSet(transaction);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    salesOrders = null;
    orderDetails = null;
    customers = null;
    products = null;
    resource = null;
    transaction = null;
    super.doTearDown();
  }

  public void testAllEObjects() throws Exception
  {
    CDOQuery query = createQuery("EObject.allInstances()", EcorePackage.eINSTANCE.getEObject());
    List<EObject> eObjects = query.getResult();
    assertEquals(objectCount, eObjects.size());
  }

  public void testAllProducts() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()", getModel1Package().getProduct1());

    List<Product1> products = query.getResult();
    assertEquals(NUM_OF_PRODUCTS, products.size());
  }

  public void testAllCustomers() throws Exception
  {
    CDOQuery query = createQuery("Customer.allInstances()", getModel1Package().getCustomer());

    List<Customer> customers = query.getResult();
    assertEquals(NUM_OF_CUSTOMERS, customers.size());
  }

  public void testAllOrdersAndSubtypes() throws Exception
  {
    CDOQuery query = createQuery("Order.allInstances()", getModel1Package().getOrder());
    // CDOQuery query = createQuery("Order.allInstances()", getModel1Package().getOrder());

    List<Order> orders = query.getResult();
    assertEquals(NUM_OF_CUSTOMERS * NUM_OF_SALES_ORDERS + NUM_OF_PURCHASE_ORDERS, orders.size());
  }

  public void testAllProductsWithName() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()->select(p | p.name='1')", getModel1Package().getProduct1());

    List<Product1> products = query.getResult();
    assertEquals(1, products.size());
  }

  public void testAllProductsWithNameParameter() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()->select(p | p.name=myname)", getModel1Package().getProduct1());
    query.setParameter("myname", "1");

    List<Product1> products = query.getResult();
    assertEquals(1, products.size());
  }

  public void testAllProductsWithVAT() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()->select(p | p.vat=VAT::vat15)", getModel1Package().getProduct1());

    List<Product1> products = query.getResult();
    assertEquals(10, products.size());
    for (Product1 p : products)
    {
      assertEquals(p.getVat(), VAT.VAT15);
    }
  }

  public void testAllProductsWithVATParameter() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()->select(p | p.vat=myvat)", getModel1Package().getProduct1());
    query.setParameter("myvat", VAT.VAT15);

    List<Product1> products = query.getResult();
    assertEquals(10, products.size());
    for (Product1 p : products)
    {
      assertEquals(p.getVat(), VAT.VAT15);
    }
  }

  public void testAllProductNames() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances().name", getModel1Package().getProduct1());

    List<String> names = query.getResult(String.class);
    assertEquals(NUM_OF_PRODUCTS, names.size());

    IOUtil.OUT().println(names);
  }

  public void testSelfNavigation() throws Exception
  {
    SalesOrder salesOrder = salesOrders.get(0);
    CDOQuery query = createQuery("self.orderDetails", salesOrder);

    List<OrderDetail> orderDetails = query.getResult(OrderDetail.class);
    assertEquals(salesOrder.getOrderDetails().size(), orderDetails.size());
  }

  public void testProductIterator() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()", getModel1Package().getProduct1());

    int counter = 0;
    for (CloseableIterator<Product1> it = query.getResultAsync(Product1.class); it.hasNext();)
    {
      Product1 product = it.next();
      assertEquals(true, product != null); // meaningless but do something

      if (++counter == NUM_OF_PRODUCTS / 2)
      {
        it.close();
        break;
      }
    }
  }

  public void testNewObject() throws Exception
  {
    resource.getContents().add(getModel1Factory().createProduct1());
    assertEquals(true, transaction.isDirty());

    CDOQuery query = createQuery("Product1.allInstances()", getModel1Package().getProduct1(), true);

    List<Product1> products = query.getResult();
    assertEquals(NUM_OF_PRODUCTS + 1, products.size());
  }

  public void testDirtyObject() throws Exception
  {
    Product1 product = products.get(2);
    product.setName("1");

    CDOQuery query = createQuery("Product1.allInstances()->select(p | p.name='1')", getModel1Package().getProduct1(), true);

    List<Product1> products = query.getResult();
    assertEquals(2, products.size());
  }

  public void testDetachedObject() throws Exception
  {
    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("p1");
    resource.getContents().add(0, p1);
    transaction.commit();

    CDOQuery query = createQuery("Product1.allInstances()", getModel1Package().getProduct1(), true);

    List<Product1> products = query.getResult();
    assertEquals(NUM_OF_PRODUCTS + 1, products.size());

    resource.getContents().remove(p1);
    assertEquals(true, transaction.isDirty());

    query = createQuery("Product1.allInstances()", getModel1Package().getProduct1(), true);

    products = query.getResult();
    assertEquals(NUM_OF_PRODUCTS, products.size());
  }

  public void testDeletedObject() throws Exception
  {
    CDOQuery query = createQuery("Product1.allInstances()", getModel1Package().getProduct1(), true);

    List<Product1> products = query.getResult();
    int numOfProducts = products.size();

    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("test");
    resource.getContents().add(0, p1);
    transaction.commit();

    resource.getContents().remove(p1);
    transaction.commit();

    query = createQuery("Product1.allInstances()", getModel1Package().getProduct1(), true);

    products = query.getResult();
    assertEquals(numOfProducts, products.size());
  }

  public void testTransactionWithDetachedObject() throws Exception
  {
    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("p1");
    resource.getContents().add(0, p1);
    transaction.commit();

    resource.getContents().remove(p1);
    transaction.commit();

    CDOQuery query = createQuery("Product1.allInstances()", getModel1Package().getProduct1(), false);

    List<Product1> products = query.getResult();
    assertEquals(NUM_OF_PRODUCTS, products.size());
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testAuditWithDetachedObject() throws Exception
  {
    assertEquals(NUM_OF_PRODUCTS, products.size());

    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("p1");
    resource.getContents().add(0, p1);
    CDOCommitInfo commitInfo = transaction.commit();

    resource.getContents().remove(p1);
    transaction.commit();

    CDOView audit = transaction.getSession().openView(commitInfo);
    CDOQuery query = audit.createQuery("ocl", "Product1.allInstances()", getModel1Package().getProduct1());
    query.setParameter("cdoLazyExtents", useLazyExtents());

    List<Product1> result = query.getResult();
    assertEquals(NUM_OF_PRODUCTS + 1, result.size());
  }

  public void testMultipleQueries() throws Exception
  {
    ISession session = getRepository().getSessionManager().getElements()[0];
    int originalLength = session.getListeners().length;

    for (int counter = 0; counter < 10; counter++)
    {
      CDOQuery query = createQuery("Product1.allInstances().name", getModel1Package().getProduct1());
      query.getResult(String.class);
    }

    assertEquals(originalLength, session.getListeners().length);
  }

  private CDOResource createTestSet(CDOTransaction transaction) throws CommitException
  {
    disableConsole();

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    fillResource(resource);

    objectCount = 1 + transaction.getNewObjects().size(); // Root resource + new objects
    transaction.commit();

    enableConsole();
    return resource;
  }

  private void fillResource(CDOResource resource)
  {
    msg("Creating Testset");
    List<Product1> products = new ArrayList<>();
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      products.add(createProduct(i));
    }

    resource.getContents().addAll(products);

    int productCounter = 0;
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      Customer customer = createCustomer(i);
      resource.getContents().add(customer);

      List<Product1> customerProducts = products.subList(productCounter, productCounter + NUM_OF_PRODUCTS_CUSTOMER);
      for (int k = 0; k < NUM_OF_SALES_ORDERS; k++)
      {
        resource.getContents().add(createSalesOrder(i * 10 + k, customer, customerProducts));
      }

      productCounter += NUM_OF_PRODUCTS_CUSTOMER;
    }

    for (int k = 0; k < NUM_OF_PURCHASE_ORDERS; k++)
    {
      resource.getContents().add(getModel1Factory().createPurchaseOrder());
    }
  }

  private Customer createCustomer(int i)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setCity(i == 0 ? null : "City " + i); // set first city null for null-test-case
    customer.setName("" + i);
    customer.setStreet("Street " + i);

    customers.add(customer);
    return customer;
  }

  private SalesOrder createSalesOrder(int num, Customer customer, List<Product1> products)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    salesOrder.setId(num);
    salesOrder.getOrderDetails().addAll(createOrderDetails(num, products));

    salesOrders.add(salesOrder);
    return salesOrder;
  }

  private List<OrderDetail> createOrderDetails(int index, List<Product1> products)
  {
    List<OrderDetail> orderDetails = new ArrayList<>();

    int count = 0;
    for (Product1 product : products)
    {
      OrderDetail orderDetail = createOrderDetail(product, count++ * index * 1.1f);
      orderDetails.add(orderDetail);
    }

    return orderDetails;
  }

  private OrderDetail createOrderDetail(Product1 product, float price)
  {
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(price);
    orderDetail.setProduct(product);

    orderDetails.add(orderDetail);
    return orderDetail;
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

    products.add(product);
    return product;
  }

  private CDOQuery createQuery(String queryString, EObject context)
  {
    return createQuery(queryString, context, false);
  }

  private CDOQuery createQuery(String queryString, EObject context, boolean considerDirtyState)
  {
    CDOQuery query = transaction.createQuery("ocl", queryString, context, considerDirtyState);
    query.setParameter("cdoLazyExtents", useLazyExtents());
    return query;
  }

  protected boolean useLazyExtents()
  {
    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Lazy extends OCLQueryTest
  {
    @Override
    protected boolean useLazyExtents()
    {
      return true;
    }
  }
}
