/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.compare.CDOComparisonScope;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.util.TestSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOMerger2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceConfiguration;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.merge.IMerger.RegistryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, IRepositoryConfig.CAPABILITY_UUIDS })
@Skips("DB.ranges") // Range-based mappings don't support rawDelete().
public class WorkspaceTest extends AbstractCDOTest
{
  private static final String RESOURCE = "/test1";

  private static final int PRODUCTS = 5;

  private static final int CUSTOMERS = 2;

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
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipStoreWithoutRawAccess();
    skipStoreWithoutHandleRevisions();

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

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "false");
  }

  @CleanRepositoriesBefore(reason = "Object counting")
  public void testCheckout() throws Exception
  {
    checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertEquals(2 + totalObjects, dumpLocalStore(null)); // Root resource + test folder + totalObjects
  }

  public void testReadObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOView view = workspace.openView();
    CDOResource resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects, dumpObjects(null, resource));
  }

  public void testModifyObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    resource = view.getResource(getResourcePath(RESOURCE));
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

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testAddObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects + PRODUCTS, dumpObjects("--> ", resource));
  }

  public void testDetachObjects() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    resource = view.getResource(getResourcePath(RESOURCE));
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
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    resource = view.getResource(getResourcePath(RESOURCE));
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

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testAddObjects2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects + 2 * PRODUCTS, dumpObjects("--> ", resource));
  }

  public void testDetachObjects2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    resource = view.getResource(getResourcePath(RESOURCE));
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
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testLocalChangesAfterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testLocalChangesAfterAdd2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

  public void testCheckinAfterModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED_" + product.getName());
      }
    }

    transaction.commit();

    CDOCommitInfo info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testCheckinAfterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    IOUtil.ERR().println("Checkout done");

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOCommitInfo info = workspace.checkin();
    assertEquals(PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testCheckinAfterAddExt() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    IOUtil.ERR().println("Checkout done");

    {
      CDOTransaction transaction = workspace.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
      for (int i = 0; i < PRODUCTS; i++)
      {
        resource.getContents().add(createProduct(i));
      }

      transaction.commit();
      transaction.close();
    }

    {
      CDOTransaction transaction = workspace.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
      for (int i = 0; i < PRODUCTS; i++)
      {
        resource.getContents().add(createProduct(i));
      }

      transaction.commit();
      transaction.close();
    }

    workspace.checkin();
    // workspace.update(null);

    {
      CDOTransaction transaction = workspace.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
      for (int i = 0; i < PRODUCTS; i++)
      {
        resource.getContents().add(createProduct(i));
      }

      transaction.commit();
      transaction.close();
    }

    workspace.checkin();
  }

  public void testCheckinAfterDetach() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

    CDOCommitInfo info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(SALES_ORDERS + PRODUCTS, info.getChangedObjects().size());
    assertEquals(ORDER_DETAILS, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCheckinAfterModify2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

    CDOCommitInfo info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testCheckinAfterAdd2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

    CDOCommitInfo info = workspace.checkin();
    assertEquals(2 * PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCheckinAfterDetach2() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

    CDOCommitInfo info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(1 + CUSTOMERS + PRODUCTS, info.getChangedObjects().size());
    assertEquals(ORDER_DETAILS + SALES_ORDERS, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCheckin2AfterModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED_" + product.getName());
      }
    }

    transaction.commit();

    CDOCommitInfo info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());

    transaction = workspace.openTransaction();
    resource = transaction.getResource(getResourcePath(RESOURCE));
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED2_" + product.getName());
      }
    }

    transaction.commit();

    info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(PRODUCTS, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testCheckin2AfterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    CDOCommitInfo info = workspace.checkin();
    assertEquals(PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());

    transaction = workspace.openTransaction();
    resource = transaction.getResource(getResourcePath(RESOURCE));
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    transaction.commit();

    info = workspace.checkin();
    assertEquals(PRODUCTS, info.getNewObjects().size());
    assertEquals(1, info.getChangedObjects().size());
    assertEquals(0, info.getDetachedObjects().size());

    base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testCheckin2AfterDetach() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);

    CDOTransaction transaction = workspace.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

    CDOCommitInfo info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(SALES_ORDERS + PRODUCTS, info.getChangedObjects().size());
    assertEquals(ORDER_DETAILS, info.getDetachedObjects().size());

    CDOWorkspaceBase base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());

    transaction = workspace.openTransaction();
    resource = transaction.getResource(getResourcePath(RESOURCE));
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

    info = workspace.checkin();
    assertEquals(0, info.getNewObjects().size());
    assertEquals(1 + CUSTOMERS, info.getChangedObjects().size());
    assertEquals(SALES_ORDERS, info.getDetachedObjects().size());

    base = workspace.getBase();
    assertEquals(0, base.getIDs().size());
    assertEquals(info.getBranch().getPathName(), workspace.getBranchPath());
    assertEquals(info.getTimeStamp(), workspace.getTimeStamp());
  }

  public void testUpdateAfterMasterModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    for (EObject object : resource.getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setName("MODIFIED_" + product.getName());
      }
    }

    transaction.commit();

    CDOTransaction local = workspace.update(null);
    assertEquals(true, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(PRODUCTS, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());

    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(0, workspace.getBase().getIDs().size());

    CDOView view = workspace.openView();
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects, dumpObjects(null, resource));
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testUpdateAfterMasterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    resource.getContents().add(createProduct(9999));
    transaction.commit();

    CDOTransaction local = workspace.update(null);
    assertEquals(true, local.isDirty());
    assertEquals(1, local.getNewObjects().size());
    assertEquals(1, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());

    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(0, workspace.getBase().getIDs().size());

    CDOView view = workspace.openView();
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects + 1, dumpObjects(null, resource));
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testUpdateTwiceAfterMasterAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    resource.getContents().add(createProduct(9999));
    transaction.commit();

    CDOTransaction local = workspace.update(null);
    assertEquals(true, local.isDirty());
    assertEquals(1, local.getNewObjects().size());
    assertEquals(1, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());

    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(0, workspace.getBase().getIDs().size());
    local.close();

    local = workspace.update(null);
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(0, workspace.getBase().getIDs().size());

    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(0, workspace.getBase().getIDs().size());

    CDOView view = workspace.openView();
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects + 1, dumpObjects(null, resource));
  }

  public void testUpdateAfterMasterDetach() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
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

    CDOTransaction local = workspace.update(null);
    assertEquals(true, local.isDirty());
    assertEquals(SALES_ORDERS + PRODUCTS, local.getDirtyObjects().size());
    assertEquals(ORDER_DETAILS, local.getDetachedObjects().size());

    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(0, workspace.getBase().getIDs().size());

    CDOView view = workspace.openView();
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects - ORDER_DETAILS, dumpObjects(null, resource));
  }

  public void testUpdateAfterMasterAndLocalModify() throws Exception
  {
    // Checkout local
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    // Modify master
    assertEquals(1, modifyProduct(transaction, 1, "MODIFIED_"));
    transaction.commit();

    // Modify local
    CDOTransaction local = workspace.openTransaction();
    assertEquals(1, modifyProduct(local, 2, "MODIFIED_"));
    local.commit();
    local.close();

    // Update local
    local = workspace.update(new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(1, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());

    // Commit local
    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(1, workspace.getBase().getIDs().size());

    // Verify local
    CDOView view = workspace.openView();
    assertEquals(2, countModifiedProduct(view));
  }

  @Requires(IRepositoryConfig.CAPABILITY_UUIDS)
  public void testUpdateAfterMasterAndLocalAdd() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE));
    resource.getContents().add(createProduct(9999));
    transaction.commit();

    CDOTransaction local = workspace.openTransaction();
    resource = local.getResource(getResourcePath(RESOURCE));
    for (int i = 0; i < PRODUCTS; i++)
    {
      resource.getContents().add(createProduct(i));
    }

    local.commit();
    local.close();

    local = workspace.update(new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, local.isDirty());
    assertEquals(1, local.getNewObjects().size());
    assertEquals(1, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());

    local.commit();
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());
    assertEquals(6, workspace.getBase().getIDs().size());

    CDOView view = workspace.openView();
    resource = view.getResource(getResourcePath(RESOURCE));
    assertEquals(totalObjects + 1 + PRODUCTS, dumpObjects(null, resource));
  }

  public void testNoConflictMasterAndLocalModify() throws Exception
  {
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    assertEquals(1, modifyProduct(transaction, 1, "MODIFIED_"));
    transaction.commit();

    CDOTransaction local = workspace.openTransaction();
    assertEquals(1, modifyProduct(local, 1, "MODIFIED_"));
    local.commit();
    local.close();

    DefaultCDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();
    local = workspace.update(merger);
    assertEquals(0, merger.getConflicts().size());
    assertEquals(false, local.isDirty());
    assertEquals(0, local.getNewObjects().size());
    assertEquals(0, local.getDirtyObjects().size());
    assertEquals(0, local.getDetachedObjects().size());

    CDOView view = workspace.openView();
    assertEquals(1, countModifiedProduct(view));
  }

  public void testConflictMasterAndLocalModify_DefaultMerger() throws Exception
  {
    CDOID id = CDOUtil.getCDOObject(products.get(1)).cdoID();

    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    assertEquals(1, modifyProduct(transaction, 1, "MODIFIED_1_"));
    transaction.commit();

    CDOTransaction local = workspace.openTransaction();
    assertEquals(1, modifyProduct(local, 1, "MODIFIED_2_"));
    local.commit();
    local.close();

    DefaultCDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();

    try
    {
      local = workspace.update(merger);
      fail("ConflictException expected");
    }
    catch (ConflictException expected)
    {
      // SUCCESS
    }

    Map<CDOID, Conflict> conflicts = merger.getConflicts();
    assertEquals(1, conflicts.size());
    assertEquals(id, conflicts.values().iterator().next().getID());
    assertInactive(local);

    CDOView view = workspace.openView();
    assertEquals(1, countModifiedProduct(view));
  }

  public void testConflictMasterAndLocalModify_CompareMerger() throws Exception
  {
    disableLog4j();

    @SuppressWarnings("unused")
    CDOID id = CDOUtil.getCDOObject(products.get(1)).cdoID();

    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    @SuppressWarnings("unused")
    InternalCDOWorkspaceBase base = workspace.getBase();

    assertEquals(1, modifyProduct(transaction, 1, "MODIFIED_1_"));
    transaction.commit();

    CDOTransaction local = workspace.openTransaction();
    assertEquals(1, modifyProduct(local, 1, "MODIFIED_2_"));
    local.commit();

    local.close();

    CDOMerger merger = new CDOMerger2()
    {
      @Override
      @Deprecated
      public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws UnsupportedOperationException
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public void merge(CDOTransaction localTransaction, CDOView remoteView, Set<CDOID> affectedIDs) throws ConflictException
      {
        CDOComparisonScope scope = new CDOComparisonScope.Minimal(remoteView, localTransaction, null, affectedIDs);
        Comparison comparison = CDOCompareUtil.compare(scope);
        EList<Diff> differences = comparison.getDifferences();

        IMerger.Registry mergerRegistry = RegistryImpl.createStandaloneInstance();

        IBatchMerger merger = new BatchMerger(mergerRegistry);
        merger.copyAllLeftToRight(differences, new BasicMonitor());
      }
    };

    local = workspace.update(merger);

    List<CDORevision> dirtyRevisions = getDirtyRevisions(local);
    dump("dirtyRevisions", dirtyRevisions);

    List<CDORevision> storeRevisions = getStoreRevisions(workspace, dirtyRevisions);
    dump("storeRevisions", storeRevisions);

    List<CDORevision> baseRevisions = getBaseRevisions(workspace);
    dump("baseRevisions", baseRevisions);

    dump("COMMIT", null);
    local.commit();

    dirtyRevisions = getDirtyRevisions(local);
    dump("dirtyRevisions", dirtyRevisions);

    storeRevisions = getStoreRevisions(workspace, dirtyRevisions);
    dump("storeRevisions", storeRevisions);

    baseRevisions = getBaseRevisions(workspace);
    dump("baseRevisions", baseRevisions);

    local.close();

    CDOCommitInfo commitInfo = workspace.checkin();
    System.out.println(commitInfo);
  }

  private void dump(String label, List<CDORevision> revisions)
  {
    System.out.println();
    System.out.println(label);

    if (revisions != null)
    {
      for (CDORevision revision : revisions)
      {
        System.out.println("   " + revision);
      }
    }
  }

  private boolean containsID(List<CDORevision> revisions, CDOID id)
  {
    for (CDORevision revision : revisions)
    {
      if (revision.getID() == id)
      {
        return true;
      }
    }

    return false;
  }

  private List<CDORevision> getDirtyRevisions(CDOTransaction transaction)
  {
    List<CDORevision> revisions = new ArrayList<>();

    for (CDOObject dirtyObject : transaction.getDirtyObjects().values())
    {
      CDORevision revision = dirtyObject.cdoRevision(false);
      revisions.add(revision);
    }

    return revisions;
  }

  private List<CDORevision> getStoreRevisions(InternalCDOWorkspace workspace, List<CDORevision> dirtyRevisions)
  {
    List<CDORevision> revisions = new ArrayList<>();

    IStore store = workspace.getLocalRepository().getStore();
    if (store instanceof MEMStore)
    {
      for (InternalCDORevision revision : ((MEMStore)store).getCurrentRevisions())
      {
        if (containsID(dirtyRevisions, revision.getID()))
        {
          revisions.add(revision);
        }
      }
    }

    return revisions;
  }

  private List<CDORevision> getBaseRevisions(InternalCDOWorkspace workspace)
  {
    List<CDORevision> revisions = new ArrayList<>();

    InternalCDOWorkspaceBase base = workspace.getBase();
    for (CDOID id : base.getIDs())
    {
      CDORevision revision = base.getRevision(id);
      revisions.add(revision);
    }

    return revisions;
  }

  public void testRevertNoTransaction() throws Exception
  {
    // Checkout local
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    // Modify local
    CDOTransaction local = workspace.openTransaction();
    assertEquals(1, modifyProduct(local, 2, "MODIFIED_"));

    local.commit();
    assertEquals(true, workspace.isDirty());

    local.close();
    assertEquals(true, workspace.isDirty());

    workspace.revert();
    assertEquals(false, workspace.isDirty());

    local = workspace.openTransaction();
    assertLocalEqualsMaster(local);
  }

  public void testRevertModify() throws Exception
  {
    // Checkout local
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    // Modify local
    CDOTransaction local = workspace.openTransaction();
    for (int i = 0; i < PRODUCTS; i++)
    {
      assertEquals(1, modifyProduct(local, i, "MODIFIED_"));

      local.commit();
      assertEquals(true, workspace.isDirty());
    }

    workspace.revert();
    assertEquals(false, workspace.isDirty());

    assertLocalEqualsMaster(local);
  }

  public void testRevertAdd() throws Exception
  {
    // Checkout local
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    // Add local
    CDOTransaction local = workspace.openTransaction();
    CDOResource resource = local.getResource(getResourcePath(RESOURCE));
    resource.getContents().add(createProduct(PRODUCTS));
    local.commit();
    assertEquals(true, workspace.isDirty());

    workspace.revert();
    assertEquals(false, workspace.isDirty());

    assertLocalEqualsMaster(local);
  }

  public void testRevertDetach() throws Exception
  {
    // Checkout local
    InternalCDOWorkspace workspace = checkout("MAIN", CDOBranchPoint.UNSPECIFIED_DATE);
    assertNotSame(CDOBranchPoint.UNSPECIFIED_DATE, workspace.getTimeStamp());

    // Detach local
    CDOTransaction local = workspace.openTransaction();
    CDOResource resource = local.getResource(getResourcePath(RESOURCE));
    resource.getContents().clear();
    local.commit();
    assertEquals(true, workspace.isDirty());

    workspace.revert();
    assertEquals(false, workspace.isDirty());

    assertLocalEqualsMaster(local);
  }

  protected IStore createLocalStore()
  {
    return getRepositoryConfig().createStore(CDOWorkspaceConfiguration.DEFAULT_LOCAL_REPOSITORY_NAME);
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

    final InternalCDOWorkspace workspace = (InternalCDOWorkspace)config.checkout();
    workspaces.add(workspace);
    workspace.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        workspaces.remove(workspace);
      }
    });

    InternalRepository localRepository = workspace.getLocalRepository();
    registerRepository(localRepository);
    LifecycleUtil.activate(localRepository);

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
    product.setName(getProductName(index));
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

  private String getProductName(int index)
  {
    return "Product No" + index;
  }

  private int modifyProduct(CDOTransaction transaction, int i, String prefix)
  {
    int count = 0;
    for (EObject object : transaction.getResource(getResourcePath(RESOURCE)).getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        String name = product.getName();
        if (getProductName(i).equals(name))
        {
          product.setName(prefix + name);
          ++count;
        }
      }
    }

    return count;
  }

  private int countModifiedProduct(CDOView view)
  {
    int count = 0;
    for (EObject object : view.getResource(getResourcePath(RESOURCE)).getContents())
    {
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        String name = product.getName();
        if (name.startsWith("MODIFIED"))
        {
          IOUtil.ERR().println(name);
          ++count;
        }
      }
    }

    return count;
  }

  private void assertLocalEqualsMaster(CDOTransaction local)
  {
    EList<EObject> masterContents = transaction.getResource(getResourcePath(RESOURCE)).getContents();
    EList<EObject> localContents = local.getResource(getResourcePath(RESOURCE)).getContents();
    assertEquals(masterContents.size(), localContents.size());

    for (int i = 0; i < masterContents.size(); i++)
    {
      EObject masterObject = masterContents.get(i);
      EObject localObject = localContents.get(i);
      assertEquals(true, EcoreUtil.equals(masterObject, localObject));

      CDORevision masterRevision = CDOUtil.getCDOObject(masterObject).cdoRevision();
      CDORevision localRevision = CDOUtil.getCDOObject(localObject).cdoRevision();
      assertEquals(masterRevision.getID(), localRevision.getID());
      assertEquals(masterRevision.getVersion(), localRevision.getVersion());
      assertEquals(masterRevision.getBranch().getID(), localRevision.getBranch().getID());
    }
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
          return WorkspaceTest.this.openSession();
        }
      };
    }
  }
}
