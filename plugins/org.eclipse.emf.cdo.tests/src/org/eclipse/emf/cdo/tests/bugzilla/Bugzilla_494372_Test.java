/*
 * Copyright (c) 2022, 2025 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.runtime.NullProgressMonitor;

import java.util.Map;

/**
 * Bug 494372 - UnitManager fails in checkNotNested when a deleted unit exists.
 * <p>
 * Tests that a deleted unit does not cause errors in the UnitManager.
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, "DB.ranges" })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_494372_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(Props.SUPPORTING_UNITS, "true");
  }

  public void testCreateUnitAfterUnitDeletion() throws Exception
  {
    createResource("test1");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.getResource(getResourcePath("test1"));
    transaction.getUnitManager().createUnit(resource1, true, new NullProgressMonitor());
    transaction.commit();

    EcoreUtil.remove(resource1);
    transaction.commit();

    createResource("test2");
    CDOResource resource2 = transaction.getResource(getResourcePath("test2"));
    transaction.getUnitManager().createUnit(resource2, true, new NullProgressMonitor());
    transaction.commit();
  }

  private void createResource(String name) throws ConcurrentAccessException, CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(name));

    for (int i = 0; i < 3; i++)
    {
      Company company = getModel1Factory().createCompany();
      addUnique(resource.getContents(), company);
      fillCompany(company);

      long start = System.currentTimeMillis();
      transaction.commit();
      long stop = System.currentTimeMillis();
      System.out.println("Committed: " + (stop - start));
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

  private int fillCategory(Category category, int depth)
  {
    int count = 0;

    for (int i = 0; i < 5; i++)
    {
      Category child = getModel1Factory().createCategory();
      addUnique(category.getCategories(), child);
      ++count;

      if (depth > 1)
      {
        count += fillCategory(child, depth - 1);
      }
    }

    for (int i = 0; i < 10; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      addUnique(category.getProducts(), product);
      ++count;
    }

    return count;
  }

  private static <T extends EObject> void addUnique(EList<T> list, T object)
  {
    ((InternalEList<T>)list).addUnique(object);
  }
}
