/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Test different aspects of HQL querying using the CDO query api.
 * 
 * @author Martin Taal
 */
public class HibernateQueryTest extends AbstractCDOTest
{
  private static final int NUM_OF_PRODUCTS = 20;

  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

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
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Product1");
      addCacheParameter(cdoQuery);
      final List<Product1> products = cdoQuery.getResult(Product1.class);
      assertTrue(products.get(0).getOrderDetails().size() > 0);
      assertTrue(products.get(0).getOrderDetails().get(0).getOrder() != null);
      assertEquals(NUM_OF_PRODUCTS, products.size());
    }

    {
      msg("Query for products with a specific name");
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Product1 where name=:name");
      cdoQuery.setParameter("name", "" + 1);
      addCacheParameter(cdoQuery);
      final List<Product1> products = cdoQuery.getResult(Product1.class);
      assertEquals(1, products.size());
    }

    clearCache(getRepository().getRevisionManager());

    {
      msg("Query for Customers");
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Customer");
      addCacheParameter(cdoQuery);
      final List<Customer> customers = cdoQuery.getResult(Customer.class);
      assertTrue(customers.get(0).getSalesOrders().size() > 0);

      assertEquals(NUM_OF_CUSTOMERS, customers.size());
    }

    {
      msg("Query for products with VAT15");
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Product1 where vat=:vat");
      addCacheParameter(cdoQuery);
      cdoQuery.setParameter("vat", VAT.VAT15);
      final List<Product1> products = cdoQuery.getResult(Product1.class);
      assertEquals(10, products.size());
      for (Product1 p : products)
      {
        assertEquals(p.getVat(), VAT.VAT15);
      }
    }

    transaction.commit();
    enableConsole();
  }

  public void testFunctions() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Count products");
      CDOQuery cdoQuery = transaction.createQuery("hql", "select count(*) from Product1");
      addCacheParameter(cdoQuery);
      final List<Long> counts = cdoQuery.getResult(Long.class);
      assertEquals(counts.size(), 1);
      assertEquals(counts.get(0), new Long(NUM_OF_PRODUCTS));
    }

    {
      msg("Orders with sum of order details");
      // result with arrays are tested below
      // CDOQuery cdoQuery = transaction.createQuery("hql",
      // "select so.id, sum(od.price) from SalesOrder so, OrderDetail od where od.order=so group by so.id");
      CDOQuery cdoQuery = transaction.createQuery("hql",
          "select sum(od.price) from SalesOrder so, OrderDetail od where od.order=so group by so.id");
      addCacheParameter(cdoQuery);
      final List<Double> results = cdoQuery.getResult(Double.class);
      assertEquals(NUM_OF_SALES_ORDERS * NUM_OF_CUSTOMERS, results.size());
    }

    transaction.commit();
    enableConsole();
  }

  public void testComplexQuerySalesOrderJoinCustomerProduct() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customers");
      CDOQuery customerQuery = transaction.createQuery("hql", "from Customer order by name");
      addCacheParameter(customerQuery);
      final List<Customer> customers = customerQuery.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());

      msg("Query for products");
      CDOQuery productQuery = transaction.createQuery("hql", "from Product1");
      addCacheParameter(productQuery);
      final List<Product1> products = productQuery.getResult(Product1.class);
      assertEquals(NUM_OF_PRODUCTS, products.size());

      msg("Query for all orders of a certain customer and with a certain product");
      int i = 0;
      for (Customer customer : customers)
      {
        final int customerIndex = Integer.parseInt(customer.getName());
        // check order by
        assertEquals(i++, customerIndex);

        final int productCounter = customerIndex * NUM_OF_PRODUCTS_CUSTOMER;
        for (Product1 product : products)
        {
          final int productIndex = Integer.parseInt(product.getName());
          // note the id is always used as the parameter
          // bug 282620
          final CDOQuery orderQuery = transaction
              .createQuery(
                  "hql",
                  "select so from SalesOrder so, OrderDetail od where so.customer=:customer and od in elements(so.orderDetails) and od.product=:product");
          orderQuery.setParameter("customer", customer);
          orderQuery.setParameter("product", product);
          addCacheParameter(orderQuery);

          final boolean hasOrders = productCounter <= productIndex
              && productIndex < productCounter + NUM_OF_PRODUCTS_CUSTOMER;
          if (hasOrders)
          {
            final List<SalesOrder> sos = orderQuery.getResult(SalesOrder.class);
            assertEquals(NUM_OF_SALES_ORDERS, sos.size());
            for (SalesOrder so : sos)
            {
              assertEquals(customer, so.getCustomer());
              boolean found = false;
              for (OrderDetail od : so.getOrderDetails())
              {
                found |= od.getProduct() == product;
              }

              assertTrue(found);
            }
          }
          else
          {
            assertEquals(0, orderQuery.getResult(SalesOrder.class).size());
          }
        }
      }
    }

    transaction.commit();
    enableConsole();
  }

  public void testElementsClause() throws Exception
  {
    // tests
    // bug 282620

    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customers");
      CDOQuery odQuery = transaction.createQuery("hql", "from OrderDetail");
      addCacheParameter(odQuery);
      final List<OrderDetail> orderDetails = odQuery.getResult(OrderDetail.class);
      for (OrderDetail orderDetail : orderDetails)
      {
        final CDOQuery orderQuery = transaction.createQuery("hql",
            "select so from SalesOrder so where :od in elements(so.orderDetails)");
        orderQuery.setParameter("od", orderDetail);
        final List<SalesOrder> sos = orderQuery.getResult(SalesOrder.class);
        assertEquals(1, sos.size());
        assertEquals(orderDetail.getOrder(), sos.get(0));
      }
    }

    transaction.commit();
    enableConsole();
  }

  // Tests id handling
  // See: bug 283106
  public void testQueryWithID() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for customers");
      CDOQuery odQuery = transaction.createQuery("hql", "from Customer");
      addCacheParameter(odQuery);
      final List<Customer> customers = odQuery.getResult(Customer.class);
      for (Customer customer : customers)
      {
        final CDOQuery orderQuery = transaction.createQuery("hql",
            "select so from SalesOrder so where so.customer.id=:customerId");
        final CDOObject cdoObject = CDOUtil.getCDOObject(customer);
        final CDOID cdoID = cdoObject.cdoID();
        orderQuery.setParameter("customerId", HibernateUtil.getInstance().getIdValue(cdoID));
        final List<SalesOrder> sos = orderQuery.getResult(SalesOrder.class);
        assertEquals(NUM_OF_SALES_ORDERS, sos.size());
        for (SalesOrder so : sos)
        {
          assertEquals(customer, so.getCustomer());
        }
      }
    }

    transaction.commit();
    enableConsole();
  }

  public void testQueryObjectArray() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for order details");
      CDOQuery query = transaction.createQuery("hql",
          "select od, od.order, od.product.vat, od.price from OrderDetail as od where od.product.vat=:vat");
      query.setParameter("vat", VAT.VAT15);
      addCacheParameter(query);
      for (Object[] values : query.getResult(Object[].class))
      {
        assertInstanceOf(OrderDetail.class, values[0]);
        assertInstanceOf(SalesOrder.class, values[1]);
        assertInstanceOf(VAT.class, values[2]);
        assertEquals(VAT.VAT15, values[2]);
        assertInstanceOf(Float.class, values[3]);
        final SalesOrder order = (SalesOrder)values[1];
        assertTrue(order.getOrderDetails().contains(values[0]));
      }
    }

    transaction.commit();
    enableConsole();
  }

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
      final List<Product1> allProducts = new ArrayList<Product1>();
      for (int page = 0; page < numOfPages; page++)
      {
        CDOQuery productQuery = transaction.createQuery("hql", "from Product1");
        productQuery.setMaxResults(pageSize);
        productQuery.setParameter(IHibernateStore.FIRST_RESULT, page * pageSize);
        addCacheParameter(productQuery);
        final List<Product1> queriedProducts = productQuery.getResult(Product1.class);
        assertTrue(queriedProducts.size() <= pageSize);
        // a product should not have been read yet
        for (Product1 newProduct : queriedProducts)
        {
          assertTrue(!allProducts.contains(newProduct));
        }

        allProducts.addAll(queriedProducts);
      }

      assertEquals(NUM_OF_PRODUCTS, allProducts.size());
    }

    transaction.commit();
    enableConsole();
  }

  public void testIterator() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    {
      msg("Query for products");
      CDOQuery productQuery = transaction.createQuery("hql", "from Product1");
      addCacheParameter(productQuery);
      final CloseableIterator<Product1> iterator = productQuery.getResultAsync(Product1.class);
      int counter = 0;
      while (iterator.hasNext())
      {
        final Product1 product = iterator.next();
        // meaningless but do something
        assertTrue(product != null);
        counter++;
        if (counter == NUM_OF_PRODUCTS / 2)
        {
          iterator.close();
        }
      }
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
    CDOResource resource = transaction.createResource("/test1");

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

      final List<Product1> customerProducts = products.subList(productCounter, productCounter
          + NUM_OF_PRODUCTS_CUSTOMER);
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
