/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 *    Kai Schlamp - Bug 284812: [DB] Query non CDO object fails
 *    Stefan Winkler - Bug 284812: [DB] Query non CDO object fails
 *    Erdal Karaca - added test case for cdoObjectResultAsMap query parameter
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.internal.db.SQLQueryHandler;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.io.IOUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * Test different aspects of SQL querying using the CDO query api.
 *
 * @author Kai Schlamp
 */
public class SQLQueryTest extends AbstractCDOTest
{
  private static final int NUM_OF_PRODUCTS = 20;

  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

  private static final String model1_Product1 = DBUtil.quoted("model1_Product1");

  private static final String model1_Customer = DBUtil.quoted("model1_Customer");

  private static final String model1_PurchaseOrder = DBUtil.quoted("model1_PurchaseOrder");

  private static final String cdo_id = DBUtil.quoted("cdo_id");

  private static final String name = DBUtil.quoted("name");

  private static final String street = DBUtil.quoted("street");

  private static final String city = DBUtil.quoted("city");

  private static final String vat = DBUtil.quoted("vat");

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testSimpleQueries() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    // {
    // msg("Query for products");
    // CDOQuery query = transaction.createQuery("sql", "SELECT CDO_ID FROM PRODUCT1");
    // final List<Product1> products = query.getResult(Product1.class);
    // assertEquals(NUM_OF_PRODUCTS, products.size());
    // }

    {
      msg("Query for products with a specific name");
      CDOQuery query = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Product1 + " WHERE " + name + "=:name");
      query.setParameter("name", "" + 1);
      final List<Product1> products = query.getResult(Product1.class);
      assertEquals(1, products.size());
    }

    {
      msg("Query for Customers");
      CDOQuery query = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Customer);
      final List<Customer> customers = query.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());
    }

    {
      msg("Query for products with VAT15");
      CDOQuery query = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Product1 + " WHERE " + vat + "=:vat");
      query.setParameter("vat", VAT.VAT15.getValue());
      final List<Product1> products = query.getResult(Product1.class);
      assertEquals(10, products.size());
      for (Product1 p : products)
      {
        assertEquals(p.getVat(), VAT.VAT15);
      }
    }

    transaction.commit();
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testFunctions() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Count products");
      CDOQuery query = transaction.createQuery("sql", "SELECT COUNT(*) from " + model1_Product1);
      query.setParameter(SQLQueryHandler.CDO_OBJECT_QUERY, false);

      // we need to handle objects, because different DBs produce either
      // Long or Integer results
      final List<Object> counts = query.getResult(Object.class);
      assertEquals(counts.size(), 1);

      Object result = counts.get(0);
      int intResult;
      if (result instanceof Integer)
      {
        intResult = ((Integer)result).intValue();
      }
      else
      {
        assertEquals(true, result instanceof Long);
        intResult = ((Long)result).intValue();
      }

      assertEquals(NUM_OF_PRODUCTS, intResult);
    }

    transaction.commit();
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testComplexQuerySalesOrderJoinCustomerProduct() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customers");
      CDOQuery customerQuery = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Customer + " ORDER BY " + name);
      final List<Customer> customers = customerQuery.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());

      msg("Query for products");
      CDOQuery productQuery = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Product1);
      final List<Product1> products = productQuery.getResult(Product1.class);
      assertEquals(NUM_OF_PRODUCTS, products.size());
    }

    transaction.commit();
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testDateParameter() throws Exception
  {
    // Mysql TIMESTAMP values do not support milliseconds!
    Date aDate = new GregorianCalendar(2020, 4, 2, 6, 45, 14).getTime();
    IOUtil.OUT().println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS").format(aDate));

    CDOSession session = openSession();

    {
      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
      purchaseOrder.setDate(aDate);

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(purchaseOrder);
      resource.getContents().add(getModel1Factory().createPurchaseOrder());
      transaction.commit();
    }

    CDOView view = session.openView();
    CDOQuery query = view.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_PurchaseOrder + " WHERE " + DBUtil.quoted(date()) + " = :aDate");
    query.setParameter("aDate", aDate);
    List<PurchaseOrder> orders = query.getResult(PurchaseOrder.class);
    assertEquals(1, orders.size());
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testPaging() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for products in pages");
      int pageSize = 5;
      int numOfPages = NUM_OF_PRODUCTS / pageSize;
      final List<Product1> allProducts = new ArrayList<>();
      for (int page = 0; page < numOfPages; page++)
      {
        CDOQuery productQuery = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Product1);
        productQuery.setMaxResults(pageSize);
        productQuery.setParameter(SQLQueryHandler.FIRST_RESULT, page * pageSize);
        final List<Product1> queriedProducts = productQuery.getResult(Product1.class);
        assertEquals(true, queriedProducts.size() <= pageSize);
        // a product should not have been read yet
        for (Product1 newProduct : queriedProducts)
        {
          assertEquals(true, !allProducts.contains(newProduct));
        }

        allProducts.addAll(queriedProducts);
      }

      assertEquals(NUM_OF_PRODUCTS, allProducts.size());
    }

    transaction.commit();
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testIterator() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for products");
      CDOQuery productQuery = transaction.createQuery("sql", "SELECT " + cdo_id + " FROM " + model1_Product1);
      CloseableIterator<Product1> iterator = productQuery.getResultAsync(Product1.class);
      int counter = 0;
      while (iterator.hasNext())
      {
        Product1 product = iterator.next();

        // Meaningless but do something.
        assertEquals(true, product != null);

        if (counter++ == NUM_OF_PRODUCTS / 2)
        {
          iterator.close();
        }
      }
    }

    transaction.commit();
  }

  public void testIteratorCancelation() throws Exception
  {
    CDOSession session = openSession();
    createTestSet(session);

    CDOView view = session.openView();

    int loops = 2000;

    IScenario scenario = getScenario();
    if (scenario != null && scenario.getCapabilities().contains(DerbyConfig.DB_ADAPTER_NAME))
    {
      loops = 50;
    }

    // Query many times to see whether we run out of store accessors.
    for (int i = 0; i < loops; i++)
    {
      CDOQuery query = view.createQuery("sql", "SELECT CDO_ID FROM MODEL1_PRODUCT1");
      query.getResultAsync(Product1.class).close();
    }
  }

  public void _testNonCdoObjectQueries() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customer street strings.");
      CDOQuery query = transaction.createQuery("sql", "SELECT STREET FROM MODEL1_CUSTOMER");
      query.setParameter("cdoObjectQuery", false);
      List<String> streets = new ArrayList<>(query.getResult(String.class));
      for (int i = 0; i < 5; i++)
      {
        assertEquals(true, streets.contains("Street " + i));
      }
    }
  }

  public void _testNonCdoObjectQueries_Null() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customer city strings.");
      CDOQuery query = transaction.createQuery("sql", "SELECT CITY FROM MODEL1_CUSTOMER");
      query.setParameter("cdoObjectQuery", false);
      List<String> cities = new ArrayList<>(query.getResult(String.class));

      assertEquals(true, cities.contains(null));
      for (int i = 1; i < 5; i++)
      {
        assertEquals(true, cities.contains("City " + i));
      }
    }
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testNonCDOObjectQueries_Complex() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customer fields");
      CDOQuery query = transaction.createQuery("sql", "SELECT " + street + ", " + city + ", " + name + " FROM " + model1_Customer + " ORDER BY " + street);
      query.setParameter("cdoObjectQuery", false);

      List<Object[]> results = query.getResult(Object[].class);
      for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
      {
        assertEquals("Street " + i, results.get(i)[0]);

        Object actual = results.get(i)[1];
        if (i == 0)
        {
          assertEquals(null, actual);
        }
        else
        {
          assertEquals("City " + i, actual);
        }

        assertEquals("" + i, results.get(i)[2]);
      }
    }
  }

  @CleanRepositoriesBefore(reason = "Query result counting")
  public void testNonCDOObjectQueries_Complex_MAP() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    msg("Query for customer fields");
    CDOQuery query = transaction.createQuery("sql", "SELECT " + street + ", " + city + ", " + name + " FROM " + model1_Customer + " ORDER BY " + street);
    query.setParameter("cdoObjectQuery", false);
    query.setParameter("mapQuery", true);

    List<Map<String, Object>> results = query.getResult();
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      assertEquals("Street " + i, mapGet(results.get(i), "street"));

      Object actual = mapGet(results.get(i), "city");
      if (i == 0)
      {
        assertEquals(null, actual);
      }
      else
      {
        assertEquals("City " + i, actual);
      }

      assertEquals("" + i, mapGet(results.get(i), "name"));
    }
  }

  private void createTestSet(CDOSession session)
  {
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
  }

  private void fillResource(CDOResource resource)
  {
    msg("Creating Testset");
    final List<Product1> products = new ArrayList<>();
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      products.add(createProduct(i));
    }

    resource.getContents().addAll(products);

    int productCounter = 0;
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      final Customer customer = getModel1Factory().createCustomer();

      if (i == 0)
      {
        // set first city null for null-test-case
        customer.setCity(null);
      }
      else
      {
        customer.setCity("City " + i);
      }

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
    final List<OrderDetail> orderDetails = new ArrayList<>();
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

  private String date()
  {
    String column = "date";
    if (((IDBStore)getRepository().getStore()).getDBAdapter().isReservedWord(column))
    {
      column += "0";
    }

    return column;
  }

  private static Object mapGet(Map<String, Object> map, String key)
  {
    Object value = map.get(key);
    if (value == null)
    {
      value = map.get(key.toUpperCase());
    }

    return value;
  }
}
