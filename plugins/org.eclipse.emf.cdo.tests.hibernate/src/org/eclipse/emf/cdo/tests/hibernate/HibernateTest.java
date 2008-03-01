/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - extended testcase
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.util.om.log.PrintLogHandler;
import org.eclipse.net4j.internal.util.om.trace.PrintTraceHandler;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLInnoDBDialect;

import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HibernateTest extends AbstractOMTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private static final int NO_OF_BUSINESS_PARTNERS = 3;

  private static final int NO_OF_PRODUCTS = 7;

  private static final int NO_OF_ORDERS = 11;

  private static final long BASE_MILLIS = new Date().getTime();

  public void testHibernate() throws Exception
  {
    IManagedContainer container = initContainer();
    JVMUtil.getAcceptor(container, "default"); // Start the JVM transport
    CDOServerUtil.addRepository(container, createRepository()); // Start a CDO respository
    IConnector connector = JVMUtil.getConnector(container, "default"); // Open a JVM connection

    CDOSession session = CDOUtil.openSession(connector, REPOSITORY_NAME, true);// Open a CDO session
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);// Not needed after first commit!!!
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource("/my/big/resource");
    resource.getContents().add(getInputModel());

    // note that specific setters are not always called because of bi-directional
    // relations, so adding an orderdetail to an order.getOrderDetails list will
    // set the order feature of the orderdetail
    final Category productCategory = createCategory("SalesPurchase");
    final List<Customer> customers = new ArrayList<Customer>();
    final List<Supplier> suppliers = new ArrayList<Supplier>();
    final List<Order> orders = new ArrayList<Order>();
    for (int j = 0; j < NO_OF_BUSINESS_PARTNERS; j++)
    {
      final Customer customer = createCustomer(j);
      customers.add(customer);
      for (int i = 0; i < NO_OF_ORDERS; i++)
      {
        customer.getSalesOrders().add(createSalesOrder(j * 1000 + i, customer, productCategory.getProducts()));
      }

      orders.addAll(customer.getSalesOrders());

      final Supplier supplier = createSupplier(j);
      suppliers.add(supplier);
      for (int i = 0; i < NO_OF_ORDERS; i++)
      {
        supplier.getPurchaseOrders().add(createPurchaseOrder(j * 1000 + i, supplier, productCategory.getProducts()));
      }

      orders.addAll(supplier.getPurchaseOrders());
    }

    resource.getContents().add(productCategory);
    resource.getContents().addAll(customers);
    resource.getContents().addAll(suppliers);
    resource.getContents().addAll(orders);

    transaction.commit();
    session.close();

    CDOSession session2 = CDOUtil.openSession(connector, REPOSITORY_NAME, true);// Open a CDO session
    CDOTransaction transaction2 = session2.openTransaction();
    Resource resource2 = transaction2.getResource("/my/big/resource");
    Category category = (Category)resource2.getContents().get(0);
    assertEquals("CAT1", category.getName());
    assertEquals("CAT2", category.getCategories().get(0).getName());
    assertEquals("P1", category.getProducts().get(0).getName());
    assertEquals("P2", category.getProducts().get(1).getName());
    assertEquals("P3", category.getCategories().get(0).getProducts().get(0).getName());
    assertEquals(category, category.getProducts().get(1).eContainer());

    checkTestData(resource2);

    transaction.close();

    // Cleanup
    session2.close();
    connector.disconnect();
  }

  private static IManagedContainer initContainer()
  {
    // Turn on tracing
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    // Prepare the standalone infra structure (not needed when running inside Eclipse)
    IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
    Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
    JVMUtil.prepareContainer(container); // Prepare the JVM transport
    CDOServerUtil.prepareContainer(container); // Prepare the CDO server
    CDOUtil.prepareContainer(container, false); // Prepare the CDO client
    return container;
  }

  private static IRepository createRepository() throws Exception
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.PROP_SUPPORTING_AUDITS, "false");
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
    props.put(Props.PROP_VERIFYING_REVISIONS, "false");
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");

    addHibernateTeneoProperties(props);

    return CDOServerUtil.createRepository(REPOSITORY_NAME, createStore(), props);
  }

  private static void addHibernateTeneoProperties(Map<String, String> props) throws Exception
  {
    DriverManager.setLogWriter(new PrintWriter(System.out));
    Driver driver = new com.mysql.jdbc.Driver();
    DriverManager.registerDriver(driver);
    String driverName = driver.getClass().getName();
    String dialectName = MySQLInnoDBDialect.class.getName();

    props.put(Environment.DRIVER, driverName);
    props.put(Environment.URL, "jdbc:mysql://localhost/cdohibernate");
    props.put(Environment.USER, "cdo");
    // props.setProperty(Environment.PASS, "root");
    props.put(Environment.DIALECT, dialectName);
    props.put(Environment.SHOW_SQL, "true");
    props.put("hibernate.hbm2ddl.auto", "create-drop");
  }

  private static IStore createStore() throws Exception
  {
    // IHibernateMappingProvider mappingProvider = new TeneoHibernateMappingProvider();
    // return new HibernateStore(props, mappingProvider);
    return new HibernateStore(null);
  }

  private static EObject getInputModel()
  {
    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    cat1.setName("CAT1");
    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product p1 = Model1Factory.eINSTANCE.createProduct();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product p2 = Model1Factory.eINSTANCE.createProduct();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product p3 = Model1Factory.eINSTANCE.createProduct();
    p3.setName("P3");
    cat2.getProducts().add(p3);
    return cat1;
  }

  private void checkTestData(Resource res)
  {
    final Category productCategory = (Category)res.getContents().get(1);

    int startIndex = 2;
    int customerIndex = 0;
    int numOfBPs = 0;
    int numOfOrders = 0;
    for (int i = startIndex; i < startIndex + NO_OF_BUSINESS_PARTNERS; i++)
    {
      final Customer c = (Customer)res.getContents().get(i);
      assertEquals(NO_OF_ORDERS, c.getSalesOrders().size());
      assertEquals("Amsterdam" + customerIndex, c.getCity());
      assertEquals("Customer" + customerIndex, c.getName());
      assertEquals("Leidseplein" + customerIndex, c.getStreet());
      int orderIndex = 0;
      for (SalesOrder so : c.getSalesOrders())
      {
        assertEquals(NO_OF_PRODUCTS, so.getOrderDetails().size());
        assertEquals(customerIndex * 1000 + orderIndex, so.getId());
        float price = 1.0f;
        int detailIndex = 0;
        for (OrderDetail od : so.getOrderDetails())
        {
          assertTrue(productCategory.getProducts().contains(od.getProduct()));
          assertEquals(so, od.getOrder());
          assertEquals(so, od.eContainer());
          assertEquals(price, od.getPrice(), 0.0001f);
          assertEquals("SalesPurchaseProduct" + detailIndex, od.getProduct().getName());
          assertTrue(od.getProduct().getOrderDetails().contains(od));
          price += 1.0f;
          detailIndex++;
        }

        assertTrue(res.getContents().contains(so));
        orderIndex++;
        numOfOrders++;
      }

      customerIndex++;
      numOfBPs++;
    }

    startIndex = 2 + NO_OF_BUSINESS_PARTNERS;
    int supplierIndex = 0;
    for (int i = startIndex; i < startIndex + NO_OF_BUSINESS_PARTNERS; i++)
    {
      Supplier s = (Supplier)res.getContents().get(i);
      assertEquals("Berlin" + supplierIndex, s.getCity());
      assertEquals("Supplier" + supplierIndex, s.getName());
      assertEquals("Potsdammer Platz" + supplierIndex, s.getStreet());

      int orderIndex = 0;
      for (PurchaseOrder po : s.getPurchaseOrders())
      {
        assertEquals(BASE_MILLIS + supplierIndex * 1000 + orderIndex, po.getDate().getTime());

        float price = 1000.0f;
        int detailIndex = 0;
        for (OrderDetail od : po.getOrderDetails())
        {
          assertTrue(productCategory.getProducts().contains(od.getProduct()));
          assertEquals(po, od.getOrder());
          assertEquals(po, od.eContainer());
          assertEquals(price, od.getPrice(), 0.0001f);
          assertEquals("SalesPurchaseProduct" + detailIndex, od.getProduct().getName());
          assertTrue(od.getProduct().getOrderDetails().contains(od));
          price += 1.0f;
          detailIndex++;
        }

        orderIndex++;
        numOfOrders++;
      }

      numOfBPs++;
      supplierIndex++;
    }

    // 2 is for the categories
    assertEquals(2 + numOfBPs + numOfOrders, res.getContents().size());
  }

  private static Category createCategory(String prefix)
  {
    Category catalog = Model1Factory.eINSTANCE.createCategory();
    catalog.setName(prefix + "Catalog");
    for (int i = 0; i < NO_OF_PRODUCTS; i++)
    {
      Product p = Model1Factory.eINSTANCE.createProduct();
      p.setName(prefix + "Product" + i);
      catalog.getProducts().add(p);
    }

    return catalog;
  }

  private static Customer createCustomer(int index)
  {
    final Customer c = Model1Factory.eINSTANCE.createCustomer();
    c.setCity("Amsterdam" + index);
    c.setName("Customer" + index);
    c.setStreet("Leidseplein" + index);
    return c;
  }

  private static Supplier createSupplier(int index)
  {
    final Supplier s = Model1Factory.eINSTANCE.createSupplier();
    s.setCity("Berlin" + index);
    s.setName("Supplier" + index);
    s.setStreet("Potsdammer Platz" + index);
    return s;
  }

  private static SalesOrder createSalesOrder(int index, Customer c, List<Product> products)
  {
    final SalesOrder so = Model1Factory.eINSTANCE.createSalesOrder();
    so.setId(index);
    so.getOrderDetails().addAll(createOrderDetails(products, 1.0f));
    return so;
  }

  private static PurchaseOrder createPurchaseOrder(int index, Supplier supplier, List<Product> products)
  {
    final PurchaseOrder po = Model1Factory.eINSTANCE.createPurchaseOrder();
    po.setDate(new Date(BASE_MILLIS + index));
    po.getOrderDetails().addAll(createOrderDetails(products, 1000.0f));
    return po;
  }

  private static List<OrderDetail> createOrderDetails(List<Product> products, float basePrice)
  {
    final List<OrderDetail> ods = new ArrayList<OrderDetail>();
    float price = basePrice;
    for (Product p : products)
    {
      ods.add(createOrderDetail(p, price));
      price += 1f;
    }

    return ods;
  }

  private static OrderDetail createOrderDetail(Product product, float price)
  {
    final OrderDetail od = Model1Factory.eINSTANCE.createOrderDetail();
    od.setPrice(price);
    od.setProduct(product);
    return od;
  }
}
