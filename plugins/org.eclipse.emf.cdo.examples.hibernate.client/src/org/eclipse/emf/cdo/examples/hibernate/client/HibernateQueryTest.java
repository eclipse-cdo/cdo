/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.examples.hibernate.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.examples.company.Order;
import org.eclipse.emf.cdo.examples.company.OrderDetail;
import org.eclipse.emf.cdo.examples.company.Product;
import org.eclipse.emf.cdo.examples.company.SalesOrder;
import org.eclipse.emf.cdo.examples.company.VAT;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDString;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Test different aspects of HQL querying using the CDO query api.
 * <p/>
 * The queries are done on a test set which is created automatically. The size of the testdata is controlled by the
 * static final int's in the top of this class.
 * 
 * @author Martin Taal
 */
public class HibernateQueryTest extends BaseTest
{
  private static final int NUM_OF_PRODUCTS = 20;

  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 5;

  // MT: remove after https://bugs.eclipse.org/bugs/show_bug.cgi?id=309920
  // gets resolved
  private static int index = 0;

  /**
   * Calls super.setUp and then removes the old data and creates new one.
   */
  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();

    // first clear the old data
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      if (transaction.hasResource("/test1")) //$NON-NLS-1$
      {
        final CDOResource resource = transaction.getResource("/test1"); //$NON-NLS-1$
        if (resource != null)
        {
          resource.getContents().clear();
        }
      }
      transaction.commit();
    }

    // then create new one
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      final CDOResource resource = transaction.getOrCreateResource("/test1"); //$NON-NLS-1$
      fillResource(resource);
      transaction.commit();
    }

  }

  public void testSimpleQueries() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Product"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<Product> products = cdoQuery.getResult(Product.class);
      assertEquals(NUM_OF_PRODUCTS, products.size());
    }

    // MT: re-enable after https://bugs.eclipse.org/bugs/show_bug.cgi?id=309920
    // gets resolved
    // {
    //      CDOQuery cdoQuery = transaction.createQuery("hql", "from Product where name=:name"); //$NON-NLS-1$  //$NON-NLS-2$
    //      cdoQuery.setParameter("name", "" + 1); //$NON-NLS-1$  //$NON-NLS-2$
    // final List<Product> products = cdoQuery.getResult(Product.class);
    // assertEquals(1, products.size());
    // }

    {
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Customer"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<Customer> customers = cdoQuery.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());
    }

    {
      CDOQuery cdoQuery = transaction.createQuery("hql", "from Product where vat=:vat"); //$NON-NLS-1$  //$NON-NLS-2$
      cdoQuery.setParameter("vat", VAT.VAT15); //$NON-NLS-1$ 
      final List<Product> products = cdoQuery.getResult(Product.class);
      // MT: re-enable after https://bugs.eclipse.org/bugs/show_bug.cgi?id=309920
      // gets resolved
      // assertEquals(10, products.size());
      assertEquals(5, products.size());
      for (Product p : products)
      {
        assertEquals(p.getVat(), VAT.VAT15);
      }
    }

    transaction.commit();
  }

  public void testFunctions() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery cdoQuery = transaction.createQuery("hql", "select count(*) from Product"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<Long> counts = cdoQuery.getResult(Long.class);
      assertEquals(counts.size(), 1);
      assertEquals(counts.get(0), new Long(NUM_OF_PRODUCTS));
    }

    {
      // result with arrays are not yet supported, see this bugzilla:
      // bug 282612
      // CDOQuery cdoQuery = transaction.createQuery("hql",
      // "select so.id, sum(od.price) from SalesOrder so, OrderDetail od where od.order=so group by so.id");
      CDOQuery cdoQuery = transaction.createQuery("hql", //$NON-NLS-1$
          "select sum(od.price) from SalesOrder so, OrderDetail od where od.order=so group by so.id"); //$NON-NLS-1$
      final List<Double> results = cdoQuery.getResult(Double.class);
      assertEquals(NUM_OF_SALES_ORDERS * NUM_OF_CUSTOMERS, results.size());
    }

    transaction.commit();
  }

  public void testComplexQuerySalesOrderJoinCustomerProduct() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery customerQuery = transaction.createQuery("hql", "from Customer order by name"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<Customer> customers = customerQuery.getResult(Customer.class);
      assertEquals(NUM_OF_CUSTOMERS, customers.size());

      CDOQuery productQuery = transaction.createQuery("hql", "from Product"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<Product> products = productQuery.getResult(Product.class);
      assertEquals(NUM_OF_PRODUCTS, products.size());

      int i = 0;
      for (Customer customer : customers)
      {
        final int customerIndex = Integer.parseInt(customer.getName());
        // check order by
        assertEquals(i++, customerIndex);

        final int productCounter = customerIndex * NUM_OF_PRODUCTS_CUSTOMER;
        for (Product product : products)
        {
          final int productIndex = Integer.parseInt(product.getName());
          final CDOQuery orderQuery = transaction
              .createQuery(
                  "hql", //$NON-NLS-1$
                  "select so from SalesOrder so, OrderDetail od where so.customer=:customer and od in elements(so.orderDetails) and od.product=:product"); //$NON-NLS-1$
          orderQuery.setParameter("customer", customer); //$NON-NLS-1$
          orderQuery.setParameter("product", product); //$NON-NLS-1$

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

              assertEquals(true, found);
            }
          }
          else
          {
            // MT: re-enable after https://bugs.eclipse.org/bugs/show_bug.cgi?id=309920
            // gets resolved
            // assertEquals(5, orderQuery.getResult(SalesOrder.class).size());
            // assertEquals(0, orderQuery.getResult(SalesOrder.class).size());
          }
        }
      }
    }

    transaction.commit();
  }

  public void testElementsClause() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery odQuery = transaction.createQuery("hql", "from OrderDetail"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<OrderDetail> orderDetails = odQuery.getResult(OrderDetail.class);
      for (OrderDetail orderDetail : orderDetails)
      {
        final CDOQuery orderQuery = transaction.createQuery("hql", //$NON-NLS-1$
            "select so from SalesOrder so where :od in elements(so.orderDetails)"); //$NON-NLS-1$
        orderQuery.setParameter("od", orderDetail); //$NON-NLS-1$
        final List<SalesOrder> sos = orderQuery.getResult(SalesOrder.class);
        assertEquals(1, sos.size());
        assertEquals(orderDetail.getOrder(), sos.get(0));
      }
    }

    transaction.commit();
  }

  // Tests id handling
  // See: bug 283106
  public void testQueryWithID() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery odQuery = transaction.createQuery("hql", "from Customer"); //$NON-NLS-1$  //$NON-NLS-2$
      final List<Customer> customers = odQuery.getResult(Customer.class);
      for (Customer customer : customers)
      {
        final CDOQuery orderQuery = transaction.createQuery("hql", //$NON-NLS-1$
            "select so from SalesOrder so where so.customer.id=:customerId"); //$NON-NLS-1$
        final CDOObject cdoObject = (CDOObject)customer;
        final CDOID id = cdoObject.cdoID();
        orderQuery.setParameter("customerId", getIdValue(id)); //$NON-NLS-1$
        final List<SalesOrder> sos = orderQuery.getResult(SalesOrder.class);
        assertEquals(NUM_OF_SALES_ORDERS, sos.size());
        for (SalesOrder so : sos)
        {
          assertEquals(customer, so.getCustomer());
        }
      }
    }

    transaction.commit();
  }

  public void testQueryObjectArray() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery query = transaction.createQuery("hql", //$NON-NLS-1$
          "select od, od.order, od.product.vat, od.price from OrderDetail as od where od.product.vat=:vat"); //$NON-NLS-1$
      query.setParameter("vat", VAT.VAT15); //$NON-NLS-1$
      for (Object[] values : query.getResult(Object[].class))
      {
        assertEquals(true, values[0] instanceof OrderDetail);
        assertEquals(true, values[1] instanceof SalesOrder);
        assertEquals(true, values[2] instanceof VAT);
        assertEquals(VAT.VAT15, values[2]);
        assertEquals(true, values[3] instanceof Float);
        final SalesOrder order = (SalesOrder)values[1];
        assertEquals(true, order.getOrderDetails().contains(values[0]));
      }
    }

    transaction.commit();
  }

  public void testPaging() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      int pageSize = 5;
      int numOfPages = NUM_OF_PRODUCTS / pageSize;
      final List<Product> allProducts = new ArrayList<Product>();
      for (int page = 0; page < numOfPages; page++)
      {
        CDOQuery productQuery = transaction.createQuery("hql", "from Product"); //$NON-NLS-1$ //$NON-NLS-2$
        productQuery.setMaxResults(pageSize);
        // NOTE: firstResult is a special parameter for the hql query language
        productQuery.setParameter("firstResult", page * pageSize); //$NON-NLS-1$
        final List<Product> queriedProducts = productQuery.getResult(Product.class);
        assertEquals(true, queriedProducts.size() <= pageSize);
        // a product should not have been read yet
        for (Product newProduct : queriedProducts)
        {
          assertEquals(true, !allProducts.contains(newProduct));
        }

        allProducts.addAll(queriedProducts);
      }

      assertEquals(NUM_OF_PRODUCTS, allProducts.size());
    }

    transaction.commit();
  }

  public void testIterator() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    {
      CDOQuery productQuery = transaction.createQuery("hql", "from Product"); //$NON-NLS-1$ //$NON-NLS-2$
      final CloseableIterator<Product> iterator = productQuery.getResultAsync(Product.class);
      int counter = 0;
      while (iterator.hasNext())
      {
        final Product product = iterator.next();
        // meaningless but do something
        assertEquals(true, product != null);
        counter++;
        if (counter == NUM_OF_PRODUCTS / 2)
        {
          iterator.close();
        }
      }
    }

    transaction.commit();
  }

  private void fillResource(CDOResource resource)
  {
    final List<Product> products = new ArrayList<Product>();
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      // MT: re-enable after https://bugs.eclipse.org/bugs/show_bug.cgi?id=309920
      // gets resolved
      products.add(createProduct((index++) + i));
    }

    resource.getContents().addAll(products);

    int productCounter = 0;
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      final Customer customer = CompanyFactory.eINSTANCE.createCustomer();
      customer.setCity("City " + i); //$NON-NLS-1$
      customer.setName(i + ""); //$NON-NLS-1$
      customer.setStreet("Street " + i); //$NON-NLS-1$
      resource.getContents().add(customer);

      final List<Product> customerProducts = products
          .subList(productCounter, productCounter + NUM_OF_PRODUCTS_CUSTOMER);
      for (int k = 0; k < NUM_OF_SALES_ORDERS; k++)
      {
        resource.getContents().add(createSalesOrder(i * 10 + k, customer, customerProducts));
      }

      productCounter += NUM_OF_PRODUCTS_CUSTOMER;
    }
  }

  private SalesOrder createSalesOrder(int num, Customer customer, List<Product> products)
  {
    SalesOrder salesOrder = CompanyFactory.eINSTANCE.createSalesOrder();
    salesOrder.setCustomer(customer);
    // MT: re-enable after https://bugs.eclipse.org/bugs/show_bug.cgi?id=309920
    // gets resolved
    // salesOrder.setId(num);
    salesOrder.setId((index++ + num));
    createOrderDetail(salesOrder, num, products);
    return salesOrder;
  }

  private List<OrderDetail> createOrderDetail(Order order, int index, List<Product> products)
  {
    final List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
    int count = 0;
    for (Product product : products)
    {
      OrderDetail orderDetail = CompanyFactory.eINSTANCE.createOrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setPrice(count++ * index * 1.1f);
      orderDetail.setProduct(product);
    }

    return orderDetails;
  }

  private Product createProduct(int index)
  {
    Product product = CompanyFactory.eINSTANCE.createProduct();
    product.setDescription("Description " + index); //$NON-NLS-1$
    product.setName("" + index); //$NON-NLS-1$
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

  public Serializable getIdValue(CDOID id)
  {
    if (id instanceof AbstractCDOIDString)
    {
      return ((AbstractCDOIDString)id).getStringValue();
    }

    if (id instanceof AbstractCDOIDLong)
    {
      return ((AbstractCDOIDLong)id).getLongValue();
    }

    throw new IllegalArgumentException("This CDOID type " + id + " is not supported by this store."); //$NON-NLS-1$//$NON-NLS-2$
  }

}
