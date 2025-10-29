/*
 * Copyright (c) 2010-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.util.TestSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceConfiguration;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * See bug 327604.
 *
 * @author Martin Fluegge
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class Bugzilla_327604_Test extends AbstractCDOTest
{
  private static final String RESOURCE = "/test1";

  private static final int PRODUCTS = 1;

  private static final int CUSTOMERS = 1;

  private static final int SALES_ORDERS_PER_CUSTOMER = 1;

  private static final int SALES_ORDERS = CUSTOMERS * SALES_ORDERS_PER_CUSTOMER;

  private static final int ORDER_DETAILS = SALES_ORDERS * PRODUCTS;

  private List<CDOWorkspace> workspaces = new ArrayList<>();

  private CDOTransaction transaction;

  private List<Product1> products;

  private List<Customer> customers;

  private List<SalesOrder> salesOrders;

  private List<OrderDetail> orderDetails;

  private int totalObjects;

  private IStore localStore;

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "false");
  }

  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
    skipTest(!getRepository().getStore().getObjectIDTypes().equals(MEMStore.OBJECT_ID_TYPES));

    CDOSession session = openSession();
    transaction = session.openTransaction();

    products = new ArrayList<>();
    customers = new ArrayList<>();
    orderDetails = new ArrayList<>();
    salesOrders = new ArrayList<>();

    createTestSet(transaction);
    assertEquals(PRODUCTS, products.size());
    assertEquals(CUSTOMERS, customers.size());
    assertEquals(SALES_ORDERS, salesOrders.size());
    assertEquals(ORDER_DETAILS, orderDetails.size());

    JVMUtil.prepareContainer(getClientContainer());
    localStore = createLocalStore();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    for (CDOWorkspace workspace : workspaces)
    {
      IOUtil.closeSilent(workspace);
    }

    workspaces.clear();
    workspaces = null;
    transaction = null;
    products = null;
    customers = null;
    salesOrders = null;
    orderDetails = null;
    localStore = null;
    super.doTearDown();
  }

  public void testReadObjects() throws Exception
  {
    skipStoreWithoutHandleRevisions();

    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOView view = workspace.openView();
    CDOResource resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects, dumpObjects(null, resource));
  }

  protected IStore createLocalStore()
  {
    return MEMStoreUtil.createMEMStore();
  }

  protected InternalCDOWorkspace checkout(String branchPath, long timeStamp)
  {
    disableConsole();
    CDOSessionConfigurationFactory remote = new RemoteSessionConfigurationFactory();

    File folder = createTempFolder("cdo-");
    CDOWorkspaceBase base = CDOWorkspaceUtil.createFolderWorkspaceBase(folder);
    IOUtil.ERR().println("CDOWorkspaceBaseline: " + folder.getAbsolutePath());

    CDOWorkspaceConfiguration config = CDOWorkspaceUtil.createWorkspaceConfiguration();
    config.setStore(localStore);
    config.setBase(base);
    config.setRemote(remote);
    config.setBranchPath(branchPath);
    config.setTimeStamp(timeStamp);
    config.setIDGenerationLocation(getRepository().getIDGenerationLocation());

    InternalCDOWorkspace workspace = (InternalCDOWorkspace)config.checkout();
    workspaces.add(workspace);

    InternalRepository localRepository = workspace.getLocalRepository();
    registerRepository(localRepository);
    LifecycleUtil.activate(localRepository);

    return workspace;
  }

  private int dumpObjects(String prefix, EObject object)
  {
    System.out.println("Object: " + object);
    if (prefix != null)
    {
      IOUtil.OUT().println(prefix + object);
    }

    int sum = 1;
    for (EObject content : object.eContents())
    {
      sum += dumpObjects(prefix != null ? prefix + "  " : null, content);
    }

    return sum;
  }

  private CDOResource createTestSet(CDOTransaction transaction) throws CommitException
  {
    disableConsole();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE));
    fillResource(resource);

    totalObjects = 1;
    for (Iterator<EObject> it = resource.eAllContents(); it.hasNext();)
    {
      it.next();
      ++totalObjects;
    }

    transaction.commit();
    enableConsole();
    return resource;
  }

  private void fillResource(CDOResource resource)
  {
    for (int i = 0; i < PRODUCTS; i++)
    {
      Product1 product = createProduct(i);
      products.add(product);
      resource.getContents().add(product);
    }

    int id = 100;
    for (int i = 0; i < CUSTOMERS; i++)
    {
      Customer customer = createCustomer(i);
      customers.add(customer);
      resource.getContents().add(customer);

      for (int k = 0; k < SALES_ORDERS_PER_CUSTOMER; k++)
      {
        SalesOrder salesOrder = createSalesOrder(id++, customer);
        salesOrders.add(salesOrder);
        resource.getContents().add(salesOrder);

        for (Product1 product : products)
        {
          OrderDetail orderDetail = createOrderDetail(product, 55.123f);
          orderDetails.add(orderDetail);
          salesOrder.getOrderDetails().add(orderDetail);
        }
      }
    }
  }

  private Product1 createProduct(int index)
  {
    Product1 product = getModel1Factory().createProduct1();
    product.setName("Product No" + index);
    product.setDescription("Description " + index);
    product.setVat(VAT.VAT15);
    return product;
  }

  private Customer createCustomer(int i)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setCity("City " + i);
    customer.setName("" + i);
    customer.setStreet("Street " + i);
    return customer;
  }

  private SalesOrder createSalesOrder(int id, Customer customer)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(id);
    salesOrder.setCustomer(customer);
    return salesOrder;
  }

  private OrderDetail createOrderDetail(Product1 product, float price)
  {
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(price);
    orderDetail.setProduct(product);
    return orderDetail;
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteSessionConfigurationFactory implements CDOSessionConfigurationFactory
  {
    @Override
    public CDOSessionConfiguration createSessionConfiguration()
    {
      return new TestSessionConfiguration()
      {
        @Override
        public CDOSession openSession()
        {
          return Bugzilla_327604_Test.this.openSession();
        }
      };
    }
  }
}
