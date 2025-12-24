/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.util.UnitIntegrityException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOUnit;
import org.eclipse.emf.cdo.view.CDOUnitManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Iterator;
import java.util.Map;

/**
 * Bug 486458 - Provide support for optimized loading and notifying of object units
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, "DB.ranges" })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Instrumented repository")
@CleanRepositoriesAfter(reason = "Instrumented repository")
public class Bugzilla_486458_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    Map<String, Object> map = getTestProperties();
    map.put(Props.SUPPORTING_UNITS, Boolean.toString(true));
    map.put(Props.CHECK_UNIT_MOVES, Boolean.toString(true));
    super.doSetUp();
  }

  public void testPrefetchBigModel() throws Exception
  {
    fillRepository();
    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    long start = System.currentTimeMillis();
    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    long stop = System.currentTimeMillis();
    System.out.println("Prefetched: " + (stop - start));

    int count = iterateResource(resource);
    assertEquals(7714, count);
  }

  public void testCreateUnit() throws Exception
  {
    fillRepository();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    assertEquals(false, transaction.getUnitManager().isUnit(resource));
    assertEquals(0, transaction.getUnitManager().getOpenUnits().length);

    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());
    CDOID childID = CDOUtil.getCDOObject(resource.getContents().get(0)).cdoID();
    assertEquals(false, serverView.isInOpenUnit(childID));

    long start = System.currentTimeMillis();
    CDOUnit unit = transaction.getUnitManager().createUnit(resource, true, null);
    long stop = System.currentTimeMillis();
    System.out.println("Created Unit: " + (stop - start));

    assertEquals(true, transaction.getUnitManager().isUnit(resource));
    assertEquals(1, transaction.getUnitManager().getOpenUnits().length);
    assertSame(unit, transaction.getUnitManager().getOpenUnits()[0]);
    assertSame(unit, transaction.getUnitManager().getOpenUnit(resource));
    assertEquals(true, serverView.isInOpenUnit(childID));

    int count = iterateResource(resource);
    assertEquals(7714, count);

    unit.close();
    assertEquals(true, transaction.getUnitManager().isUnit(resource));
    assertEquals(0, transaction.getUnitManager().getOpenUnits().length);
    assertEquals(null, transaction.getUnitManager().getOpenUnit(resource));
    assertEquals(false, serverView.isInOpenUnit(childID));
  }

  public void testDetectNestedUnit() throws Exception
  {
    fillRepository();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    CDOUnit unit = transaction.getUnitManager().createUnit(resource, true, null);
    EObject parent = resource.getFolder();
    EObject child = resource.getContents().get(0);

    try
    {
      transaction.getUnitManager().createUnit(parent, true, null);
      fail("CDOException expected");
    }
    catch (CDOException expected)
    {
      // SUCCESS
    }

    try
    {
      transaction.getUnitManager().createUnit(child, true, null);
      fail("CDOException expected");
    }
    catch (CDOException expected)
    {
      // SUCCESS
    }

    assertEquals(true, transaction.getUnitManager().isUnit(resource));
    assertEquals(false, transaction.getUnitManager().isUnit(parent));
    assertEquals(false, transaction.getUnitManager().isUnit(child));

    assertEquals(1, transaction.getUnitManager().getOpenUnits().length);
    assertSame(unit, transaction.getUnitManager().getOpenUnits()[0]);
  }

  public void testOpenUnit() throws Exception
  {
    // OMPlatform.INSTANCE.setDebugging(true);
    // OMBundle bundle = OMPlatform.INSTANCE.bundle("org.eclipse.emf.cdo.server.db", null);
    // bundle.getDebugSupport().setDebugging(true);
    // bundle.tracer("debug").tracer("units").setEnabled(true);

    fillRepository();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("test"));
      CDOUnit createdUnit = transaction.getUnitManager().createUnit(resource, true, null);
      assertEquals(7714, createdUnit.getElements());

      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("test"));

      assertEquals(true, transaction.getUnitManager().isUnit(resource));
      assertEquals(0, transaction.getUnitManager().getOpenUnits().length);

      long start = System.currentTimeMillis();
      CDOUnit openedUnit = transaction.getUnitManager().openUnit(resource, false, null);
      assertEquals(7714, openedUnit.getElements());
      long stop = System.currentTimeMillis();
      System.out.println("Opened Unit: " + (stop - start));

      assertEquals(true, transaction.getUnitManager().isUnit(resource));
      assertEquals(1, transaction.getUnitManager().getOpenUnits().length);
      assertSame(openedUnit, transaction.getUnitManager().getOpenUnits()[0]);

      int count = iterateResource(resource);
      assertEquals(7714, count);
    }
  }

  public void testUpdateUnit() throws Exception
  {
    fillRepository();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    CDOUnit unit = transaction.getUnitManager().createUnit(resource, true, null);
    int elements = unit.getElements();

    Company company = (Company)resource.getContents().get(0);
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    assertEquals(elements + 1, unit.getElements());

    transaction.commit();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    category.eAdapters().add(new AdapterImpl());
    assertEquals(false, ((InternalCDOView)transaction).hasSubscription(CDOUtil.getCDOObject(category).cdoID()));

    company.getCategories().remove(category);
    assertEquals(elements, unit.getElements());
  }

  public void testUnitMoves() throws Exception
  {
    fillRepository();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    Company company = (Company)resource.getContents().get(0);
    Category category1 = company.getCategories().get(0);
    Category category2 = company.getCategories().get(1);
    Category category3 = company.getCategories().get(2);

    CDOUnitManager unitManager = transaction.getUnitManager();
    unitManager.createUnit(category1, true, null);
    unitManager.createUnit(category2, true, null);

    category2.getProducts().add(category1.getProducts().get(0));
    category2.getProducts().add(category1.getProducts().get(1));

    try
    {
      transaction.commit();
      fail("UnitIntegrityException expected");
    }
    catch (UnitIntegrityException expected)
    {
      // SUCCESS
      IOUtil.OUT().println(expected.getMessage());
      transaction.rollback();
    }

    category3.getProducts().add(category1.getProducts().get(0));
    category3.getProducts().add(category1.getProducts().get(1));

    try
    {
      transaction.commit();
      fail("UnitIntegrityException expected");
    }
    catch (UnitIntegrityException expected)
    {
      // SUCCESS
      IOUtil.OUT().println(expected.getMessage());
      transaction.rollback();
    }

    category1.getProducts().add(category3.getProducts().get(0));
    transaction.commit();
  }

  public void testNotificationsAfterOpenUnit() throws Exception
  {
    fillRepository();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource = transaction.getResource(getResourcePath("test"));
    Company root = (Company)resource.getContents().get(0);
    Category child = (Category)root.eContents().get(0);
    Company sibling = (Company)resource.getContents().get(1);

    CDOUnit unit = transaction.getUnitManager().createUnit(root, true, null);

    new TestAdapter(root, child, sibling);

    assertEquals(false, ((InternalCDOView)transaction).hasSubscription(CDOUtil.getCDOObject(root).cdoID()));
    assertEquals(false, ((InternalCDOView)transaction).hasSubscription(CDOUtil.getCDOObject(child).cdoID()));
    assertEquals(true, ((InternalCDOView)transaction).hasSubscription(CDOUtil.getCDOObject(sibling).cdoID()));

    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());
    assertEquals(false, serverView.hasSubscription(CDOUtil.getCDOObject(root).cdoID()));
    assertEquals(false, serverView.hasSubscription(CDOUtil.getCDOObject(child).cdoID()));
    assertEquals(true, serverView.hasSubscription(CDOUtil.getCDOObject(sibling).cdoID()));

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath("test"));

    Company root2 = (Company)resource2.getContents().get(0);
    root2.setName("Name");
    commitAndSync(transaction2, transaction);
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(root).cdoState());
    assertEquals("Name", root.getName());

    Category child2 = (Category)root2.eContents().get(0);
    child2.setName("Name");
    commitAndSync(transaction2, transaction);
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(child2).cdoState());
    assertEquals("Name", child.getName());

    Company sibling2 = (Company)resource2.getContents().get(1);
    sibling2.setName("Name");
    commitAndSync(transaction2, transaction);
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(sibling2).cdoState());
    assertEquals("Name", sibling.getName());

    unit.close();

    root2.setName("Name2");
    commitAndSync(transaction2, transaction);
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(root).cdoState());
    assertEquals("Name2", root.getName());

    child2.setName("Name2");
    commitAndSync(transaction2, transaction);
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(child2).cdoState());
    assertEquals("Name2", child.getName());

    sibling2.setName("Name2");
    commitAndSync(transaction2, transaction);
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(sibling2).cdoState());
    assertEquals("Name2", sibling.getName());
  }

  public void testResourceUnits() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getUnitManager().setAutoResourceUnitsEnabled(true);

    System.out.print("Committed: ");
    for (int i = 0; i < 5; i++)
    {
      CDOResource resource = transaction.createResource(getResourcePath("test" + i));

      Company company = getModel1Factory().createCompany();
      addUnique(resource.getContents(), company);
      fillCompany(company);
    }

    long start = System.currentTimeMillis();
    transaction.commit();
    long stop = System.currentTimeMillis();
    System.out.println(stop - start);

    session.close();
    clearCache(getRepository().getRevisionManager());
    session = openSession();

    CDOView view = session.openView();
    view.getUnitManager().setAutoResourceUnitsEnabled(true);

    for (int i = 0; i < 5; i++)
    {
      CDOResource resource = view.getResource(getResourcePath("test" + i));
      iterateResource(resource);
    }

    assertEquals(5, view.getUnitManager().getOpenUnits().length);

    for (int i = 0; i < 5; i++)
    {
      CDOResource resource = view.getResource(getResourcePath("test" + i));
      resource.unload();
    }

    assertEquals(0, view.getUnitManager().getOpenUnits().length);
  }

  private void fillRepository() throws ConcurrentAccessException, CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test"));

    for (int i = 0; i < 3; i++)
    {
      System.out.print("Committed: ");
      Company company = getModel1Factory().createCompany();
      addUnique(resource.getContents(), company);
      fillCompany(company);

      long start = System.currentTimeMillis();
      transaction.commit();
      long stop = System.currentTimeMillis();
      System.out.println(stop - start);
    }

    session.close();
  }

  private void fillCompany(Company company)
  {
    for (int i = 0; i < 5; i++)
    {
      Category category = getModel1Factory().createCategory();
      addUnique(company.getCategories(), category);
      fillCategory(category, 3);
    }

    for (int i = 0; i < 10; i++)
    {
      Supplier supplier = getModel1Factory().createSupplier();
      addUnique(company.getSuppliers(), supplier);
    }

    for (int i = 0; i < 10; i++)
    {
      Customer customer = getModel1Factory().createCustomer();
      addUnique(company.getCustomers(), customer);
    }

    for (int i = 0; i < 10; i++)
    {
      PurchaseOrder order = getModel1Factory().createPurchaseOrder();
      order.setSupplier(company.getSuppliers().get(i));
      addUnique(company.getPurchaseOrders(), order);

      for (int j = 0; j < 10; j++)
      {
        OrderDetail orderDetail = getModel1Factory().createOrderDetail();
        addUnique(order.getOrderDetails(), orderDetail);
      }
    }

    for (int i = 0; i < 10; i++)
    {
      SalesOrder order = getModel1Factory().createSalesOrder();
      order.setCustomer(company.getCustomers().get(i));
      addUnique(company.getSalesOrders(), order);

      for (int j = 0; j < 10; j++)
      {
        OrderDetail orderDetail = getModel1Factory().createOrderDetail();
        addUnique(order.getOrderDetails(), orderDetail);
      }
    }
  }

  private void fillCategory(Category category, int depth)
  {
    for (int i = 0; i < 5; i++)
    {
      Category child = getModel1Factory().createCategory();
      addUnique(category.getCategories(), child);
      if (depth > 1)
      {
        fillCategory(child, depth - 1);
      }
    }

    for (int i = 0; i < 10; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      addUnique(category.getProducts(), product);
    }
  }

  private static <T extends EObject> void addUnique(EList<T> list, T object)
  {
    ((InternalEList<T>)list).addUnique(object);
  }

  private static int iterateResource(CDOResource resource)
  {
    System.out.print("Iterated: ");
    int count = 1;
    long start = System.currentTimeMillis();

    for (Iterator<EObject> it = resource.eAllContents(); it.hasNext();)
    {
      it.next();
      ++count;
    }

    long stop = System.currentTimeMillis();
    System.out.println(stop - start);

    return count;
  }
}
