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

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.workspace.FolderCDOWorkspaceMemory;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceMemory;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
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

  private static final int PRODUCTS = 5;

  private static final int CUSTOMERS = 2;

  private static final int SALES_ORDERS_PER_CUSTOMER = 1;

  private static final int SALES_ORDERS = CUSTOMERS * SALES_ORDERS_PER_CUSTOMER;

  private static final int ORDER_DETAILS = SALES_ORDERS * PRODUCTS;

  private List<CDOWorkspace> workspaces = new ArrayList<CDOWorkspace>();

  private CDOTransaction transaction;

  private List<Product1> products;

  private List<Customer> customers;

  private List<SalesOrder> salesOrders;

  private List<OrderDetail> orderDetails;

  private int totalObjects;

  private IStore localStore;

  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();

    CDOSession session = openSession();
    transaction = session.openTransaction();

    products = new ArrayList<Product1>();
    customers = new ArrayList<Customer>();
    orderDetails = new ArrayList<OrderDetail>();
    salesOrders = new ArrayList<SalesOrder>();

    createTestSet(transaction);
    assertEquals(PRODUCTS, products.size());
    assertEquals(CUSTOMERS, customers.size());
    assertEquals(SALES_ORDERS, salesOrders.size());
    assertEquals(ORDER_DETAILS, orderDetails.size());

    JVMUtil.prepareContainer(getClientContainer());
    localStore = createLocalStore();

    CDOUtil.setLegacyModeDefault(true);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    CDOUtil.setLegacyModeDefault(false);
    for (CDOWorkspace workspace : workspaces)
    {
      IOUtil.closeSilent(workspace);
    }

    workspaces.clear();
    super.doTearDown();
  }

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> map = super.getTestProperties();
    map.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "false");
    return map;
  }

  public void testCheckout() throws Exception
  {
    checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertEquals(1 + totalObjects, dumpLocalStore(null)); // Root resource + totalObjects
  }

  public void testReadObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOView view = workspace.openView();
    CDOResource resource = view.getResource(RESOURCE);
    assertEquals(totalObjects, dumpObjects(null, resource));
  }

  public void testModifyObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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
    assertEquals(PRODUCTS, transaction.getDirtyObjects().size());

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
  }

  public void testAddObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(1, transaction.getDirtyObjects().size());
    assertEquals(PRODUCTS, transaction.getNewObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getNewObjects().size());

    clearCache(transaction.getSession().getRevisionManager());

    CDOView view = workspace.openView();
    resource = view.getResource(RESOURCE);
    assertEquals(totalObjects + PRODUCTS, dumpObjects("--> ", resource));
  }

  public void testDetachObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(SALES_ORDERS + PRODUCTS, transaction.getDirtyObjects().size());
    assertEquals(ORDER_DETAILS, transaction.getDetachedObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getDetachedObjects().size());

    clearCache(transaction.getSession().getRevisionManager());

    CDOView view = workspace.openView();
    resource = view.getResource(RESOURCE);
    assertEquals(totalObjects - ORDER_DETAILS, dumpObjects(null, resource));
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        if (!((SalesOrder)object).getOrderDetails().isEmpty())
        {
          fail("All order details should be detached");
        }
      }
    }
  }

  public void testModifyObjects2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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
    assertEquals(PRODUCTS, transaction.getDirtyObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());

    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED2_" + product.getName());
      }
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(PRODUCTS, transaction.getDirtyObjects().size());

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
        assertEquals(true, product.getName().startsWith("MODIFIED2_"));
      }
    }
  }

  public void testAddObjects2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(1, transaction.getDirtyObjects().size());
    assertEquals(PRODUCTS, transaction.getNewObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getNewObjects().size());

    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(1, transaction.getDirtyObjects().size());
    assertEquals(PRODUCTS, transaction.getNewObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getNewObjects().size());

    clearCache(transaction.getSession().getRevisionManager());

    CDOView view = workspace.openView();
    resource = view.getResource(RESOURCE);
    assertEquals(totalObjects + 2 * PRODUCTS, dumpObjects("--> ", resource));
  }

  public void testDetachObjects2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(SALES_ORDERS + PRODUCTS, transaction.getDirtyObjects().size());
    assertEquals(ORDER_DETAILS, transaction.getDetachedObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getDetachedObjects().size());

    for (Iterator<EObject> it = resource.getContents().iterator(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof SalesOrder)
      {
        it.remove();
      }

      if (object instanceof Customer)
      {
        ((Customer)object).getSalesOrders().clear();
      }
    }

    assertEquals(true, transaction.isDirty());
    assertEquals(1 + CUSTOMERS, transaction.getDirtyObjects().size());
    assertEquals(SALES_ORDERS, transaction.getDetachedObjects().size());

    transaction.commit();
    assertEquals(false, transaction.isDirty());
    assertEquals(0, transaction.getDirtyObjects().size());
    assertEquals(0, transaction.getDetachedObjects().size());

    clearCache(transaction.getSession().getRevisionManager());

    CDOView view = workspace.openView();
    resource = view.getResource(RESOURCE);
    assertEquals(totalObjects - ORDER_DETAILS - SALES_ORDERS, dumpObjects(null, resource));
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        if (!((SalesOrder)object).getOrderDetails().isEmpty())
        {
          fail("All order details should be detached");
        }
      }

      if (object instanceof Customer)
      {
        if (!((Customer)object).getSalesOrders().isEmpty())
        {
          fail("All sales orders should be detached");
        }
      }
    }
  }

  public void testLocalChangesAfterModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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

    transaction.commit();

    CDOChangeSetData changes = workspace.getLocalChanges();
    assertEquals(0, changes.getNewObjects().size());
    assertEquals(PRODUCTS, changes.getChangedObjects().size());
    assertEquals(0, changes.getDetachedObjects().size());
  }

  public void testLocalChangesAfterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOChangeSetData changes = workspace.getLocalChanges();
    assertEquals(PRODUCTS, changes.getNewObjects().size());
    assertEquals(1, changes.getChangedObjects().size());
    assertEquals(0, changes.getDetachedObjects().size());
  }

  public void testLocalChangesAfterDetach() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    transaction.commit();

    CDOChangeSetData changes = workspace.getLocalChanges();
    assertEquals(0, changes.getNewObjects().size());
    assertEquals(SALES_ORDERS + PRODUCTS, changes.getChangedObjects().size());
    assertEquals(ORDER_DETAILS, changes.getDetachedObjects().size());
  }

  public void testLocalChangesAfterModify2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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

    transaction.commit();

    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED2_" + product.getName());
      }
    }

    transaction.commit();

    CDOChangeSetData changes = workspace.getLocalChanges();
    assertEquals(0, changes.getNewObjects().size());
    assertEquals(PRODUCTS, changes.getChangedObjects().size());
    assertEquals(0, changes.getDetachedObjects().size());
  }

  public void testLocalChangesAfterAdd2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOChangeSetData changes = workspace.getLocalChanges();
    assertEquals(2 * PRODUCTS, changes.getNewObjects().size());
    assertEquals(1, changes.getChangedObjects().size());
    assertEquals(0, changes.getDetachedObjects().size());
  }

  public void testLocalChangesAfterDetach2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    transaction.commit();

    for (Iterator<EObject> it = resource.getContents().iterator(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof SalesOrder)
      {
        it.remove();
      }

      if (object instanceof Customer)
      {
        ((Customer)object).getSalesOrders().clear();
      }
    }

    transaction.commit();

    CDOChangeSetData changes = workspace.getLocalChanges();
    assertEquals(0, changes.getNewObjects().size());
    assertEquals(1 + CUSTOMERS + PRODUCTS, changes.getChangedObjects().size());
    assertEquals(ORDER_DETAILS + SALES_ORDERS, changes.getDetachedObjects().size());
  }

  public void testCommitAfterModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommitAfterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    System.err.println("Checkout done");

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommitAfterDetach() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(SALES_ORDERS + PRODUCTS, info.getChangedObjects().size());
    assertEquals(ORDER_DETAILS, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommitAfterModify2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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

    transaction.commit();

    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED2_" + product.getName());
      }
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommitAfterAdd2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(2 * PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommitAfterDetach2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    transaction.commit();

    for (Iterator<EObject> it = resource.getContents().iterator(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof SalesOrder)
      {
        it.remove();
      }

      if (object instanceof Customer)
      {
        ((Customer)object).getSalesOrders().clear();
      }
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(1 + CUSTOMERS + PRODUCTS, info.getChangedObjects().size());
    assertEquals(ORDER_DETAILS + SALES_ORDERS, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommit2AfterModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

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

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());

    transaction = workspace.openTransaction();
    resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED2_" + product.getName());
      }
    }

    transaction.commit();

    info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommit2AfterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());

    transaction = workspace.openTransaction();
    resource = transaction.getResource(RESOURCE);
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    info = workspace.commit();
    assertEquals(PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCommit2AfterDetach() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(RESOURCE);
    for (EObject object : resource.getContents())
    {
      if (object instanceof SalesOrder)
      {
        ((SalesOrder)object).getOrderDetails().clear();
      }

      if (object instanceof Product1)
      {
        ((Product1)object).getOrderDetails().clear();
      }
    }

    transaction.commit();

    CDOCommitInfo info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(SALES_ORDERS + PRODUCTS, info.getChangedObjects().size());
    assertEquals(ORDER_DETAILS, info.getDetachedObjects().size());

    CDOWorkspaceMemory memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());

    transaction = workspace.openTransaction();
    resource = transaction.getResource(RESOURCE);
    for (Iterator<EObject> it = resource.getContents().iterator(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof SalesOrder)
      {
        it.remove();
      }

      if (object instanceof Customer)
      {
        ((Customer)object).getSalesOrders().clear();
      }
    }

    transaction.commit();

    info = workspace.commit();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(1 + CUSTOMERS, info.getChangedObjects().size());
    assertEquals(SALES_ORDERS, info.getDetachedObjects().size());

    memory = workspace.getMemory();
    assertEquals(0, memory.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  protected IStore createLocalStore()
  {
    return MEMStoreUtil.createMEMStore();
  }

  protected InternalCDOWorkspace checkout(String branchPath, long timeStamp)
  {
    disableConsole();
    CDOSessionConfigurationFactory remote = new RemoteSessionConfigurationFactory();

    File folder = TMPUtil.createTempFolder("cdo-");
    CDOWorkspaceMemory memory = new FolderCDOWorkspaceMemory(folder);
    System.err.println("CDOWorkspaceBaseline: " + folder.getAbsolutePath());

    InternalCDOWorkspace workspace = (InternalCDOWorkspace)CDOWorkspaceUtil.checkout(localStore, memory, remote,
        branchPath, timeStamp);

    workspaces.add(workspace);
    registerRepository(workspace.getLocalRepository());
    return workspace;
  }

  private int dumpLocalStore(PrintStream out)
  {
    if (localStore instanceof CDOAllRevisionsProvider)
    {
      CDOAllRevisionsProvider provider = (CDOAllRevisionsProvider)localStore;
      Map<CDOBranch, List<CDORevision>> allRevisions = provider.getAllRevisions();
      int size = allRevisions.values().iterator().next().size();

      if (out != null)
      {
        CDORevisionUtil.dumpAllRevisions(allRevisions, out);
      }

      return size;
    }

    return 0;
  }

  private int dumpObjects(String prefix, EObject object)
  {
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
