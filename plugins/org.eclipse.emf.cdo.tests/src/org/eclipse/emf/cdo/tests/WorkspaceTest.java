/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class WorkspaceTest extends AbstractCDOTest
{
  private static final String RESOURCE = "/test1";

  private static final int NUM_OF_PRODUCTS = 100;

  private static final int NUM_OF_CUSTOMERS = 20;

  private static final int NUM_OF_PRODUCTS_CUSTOMER = NUM_OF_PRODUCTS / NUM_OF_CUSTOMERS;

  private static final int NUM_OF_SALES_ORDERS = 50;

  private CDOTransaction transaction;

  private List<Product1> products = new ArrayList<Product1>();

  private List<Customer> customers = new ArrayList<Customer>();

  private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

  private List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();

  private int totalObjects;

  private IStore localStore;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    disableConsole();

    CDOSession session = openSession();
    transaction = session.openTransaction();
    createTestSet(transaction);

    JVMUtil.prepareContainer(getClientContainer());
    localStore = createLocalStore();
  }

  public void testCheckout() throws Exception
  {
    CDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    assertEquals(1 + totalObjects, dumpLocalStore(null)); // Root resource + totalObjects
    workspace.close();
  }

  public void testReadObjects() throws Exception
  {
    CDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOView view = workspace.openView();
    CDOResource resource = view.getResource(RESOURCE);
    assertEquals(totalObjects, dumpObjects(null, resource));
    workspace.close();
  }

  public void testModifyObjects() throws Exception
  {
    CDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED_" + product.getName());
      }
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(NUM_OF_PRODUCTS, transaction.getDirtyObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());

    clearCache(transaction.getSession().getRevisionManager());

    CDOView view = workspace.openView();
    resource = view.getResource(RESOURCE);
    assertEquals(totalObjects, dumpObjects(null, resource));
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        assertEquals(true, product.getName().startsWith("MODIFIED_"));
      }
    }

    workspace.close();
  }

  public void testAddObjects() throws Exception
  {
    CDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < NUM_OF_PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(1, transaction.getDirtyObjects().size());
    assertEquals(NUM_OF_PRODUCTS, transaction.getNewObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getNewObjects().size());

    clearCache(transaction.getSession().getRevisionManager());

    CDOView view = workspace.openView();
    resource = view.getResource(RESOURCE);
    assertEquals(totalObjects + NUM_OF_PRODUCTS, dumpObjects("--> ", resource));
    workspace.close();
  }

  private int dumpLocalStore(PrintStream out)
  {
    if (localStore instanceof CDOAllRevisionsProvider)
    {
      CDOAllRevisionsProvider provider = (CDOAllRevisionsProvider)localStore;
      Map<CDOBranch, List<CDORevision>> allRevisions = provider.getAllRevisions();
      int size = allRevisions.values().iterator().next().size();
      System.out.println("Local Store: " + size);

      if (out != null)
      {
        CDORevisionUtil.dumpAllRevisions(allRevisions, out);
      }

      return size;
    }

    return 0;
  }

  private int dumpObjects(String prefix, CDOObject object)
  {
    if (prefix != null)
    {
      IOUtil.OUT().println(prefix + object);
    }

    int sum = 1;
    for (EObject content : object.eContents())
    {
      sum += dumpObjects(prefix != null ? prefix + "  " : null, (CDOObject)content);
    }

    return sum;
  }

  private CDOResource createTestSet(CDOTransaction transaction) throws CommitException
  {
    disableConsole();
    CDOResource resource = transaction.createResource(RESOURCE);
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
    msg("Creating Testset");
    List<Product1> products = new ArrayList<Product1>();
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
    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

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

  protected IStore createLocalStore()
  {
    return MEMStoreUtil.createMEMStore();
  }

  protected CDOWorkspace checkout(String branchPath, long timeStamp)
  {
    disableConsole();
    CDOSessionConfigurationFactory remote = new RemoteSessionConfigurationFactory();

    CDOWorkspace workspace = CDOWorkspaceUtil.checkout(localStore, null, remote, branchPath, timeStamp);
    registerRepository(workspace.getLocalRepository());
    return workspace;
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteSessionConfigurationFactory implements CDOSessionConfigurationFactory
  {
    public CDOSessionConfiguration createSessionConfiguration()
    {
      return new CDOSessionConfiguration()
      {
        public CDOSession openSession()
        {
          return WorkspaceTest.this.openSession();
        }

        public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
        {
          throw new UnsupportedOperationException();
        }

        public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
        {
          throw new UnsupportedOperationException();
        }

        public void setExceptionHandler(ExceptionHandler exceptionHandler)
        {
          throw new UnsupportedOperationException();
        }

        public void setActivateOnOpen(boolean activateOnOpen)
        {
          throw new UnsupportedOperationException();
        }

        public boolean isSessionOpen()
        {
          throw new UnsupportedOperationException();
        }

        public boolean isPassiveUpdateEnabled()
        {
          throw new UnsupportedOperationException();
        }

        public boolean isActivateOnOpen()
        {
          throw new UnsupportedOperationException();
        }

        public PassiveUpdateMode getPassiveUpdateMode()
        {
          throw new UnsupportedOperationException();
        }

        public ExceptionHandler getExceptionHandler()
        {
          throw new UnsupportedOperationException();
        }

        public CDOAuthenticator getAuthenticator()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
  }
}
