/*
 * Copyright (c) 2016, 2018, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.internal.server.UnitManager;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.IUnit;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
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

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Bug 486458 - Provide support for optimized loading and notifying of object units
 * <p>
 * Tests that a createUnit() that starts slightly before a commit() considers the newly attached objects.
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, "DB.ranges" })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Instrumented repository")
@CleanRepositoriesAfter(reason = "Instrumented repository")
public class Bugzilla_486458a_Test extends AbstractCDOTest
{
  private CountDownLatch unitRegistered;

  private CountDownLatch startInitializeUnit;

  public void testCreateUnitWithParallelCommit() throws Exception
  {
    fillRepository();

    unitRegistered = new CountDownLatch(1);
    startInitializeUnit = new CountDownLatch(1);

    final int[] committed = { 0 };

    Thread committer = new Thread("Committer")
    {
      @Override
      public void run()
      {
        CDOSession session = openSession();
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.getResource(getResourcePath("test"));

        Company company = (Company)resource.getContents().get(0);
        Category category = company.getCategories().get(0);

        committed[0] = fillCategory(category, 3);

        await(unitRegistered);

        try
        {
          transaction.commit();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }

        session.close();
      }
    };

    committer.setDaemon(true);
    committer.start();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    transaction.getUnitManager().createUnit(resource, true, null);
    committer.join(DEFAULT_TIMEOUT);

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("test"));

    int count = iterateResource(resource);
    int expected = 7714 + committed[0];
    assertEquals(expected, count);

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("test"));

    transaction.getUnitManager().openUnit(resource, false, null);

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("test"));

    count = iterateResource(resource);
    assertEquals(expected, count);
  }

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> map = super.getTestProperties();
    map.put(Props.SUPPORTING_UNITS, Boolean.toString(true));
    return map;
  }

  @Override
  protected void doSetUp() throws Exception
  {
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
    Repository repository = new Repository.Default()
    {
      @Override
      public InternalCommitContext createCommitContext(InternalTransaction transaction)
      {
        return new TransactionCommitContext(transaction)
        {
          @Override
          public void write(OMMonitor monitor)
          {
            if (startInitializeUnit != null)
            {
              startInitializeUnit.countDown();
            }

            super.write(monitor);
          }
        };
      }

      @Override
      protected InternalUnitManager createUnitManager()
      {
        return new UnitManager(this)
        {
          @Override
          protected UnitInitializer createUnitInitializer(CDOID rootID, IView view, CDORevisionHandler revisionHandler)
          {
            return new UnitInitializer(rootID, view, revisionHandler)
            {
              @Override
              public IUnit initialize(OMMonitor monitor)
              {
                if (unitRegistered != null)
                {
                  unitRegistered.countDown();
                }

                await(startInitializeUnit);

                return super.initialize(monitor);
              }
            };
          }
        };
      }
    };

    Map<String, String> props = getRepositoryProperties();
    repository.setProperties(props);

    repository.setName(IRepositoryConfig.REPOSITORY_NAME);

    Map<String, Object> map = getTestProperties();
    map.put(RepositoryConfig.PROP_TEST_REPOSITORY, repository);
  }

  private void fillRepository() throws ConcurrentAccessException, CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test"));

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

  private static int iterateResource(CDOResource resource)
  {
    int count = 1;
    long start = System.currentTimeMillis();

    for (Iterator<EObject> it = resource.eAllContents(); it.hasNext();)
    {
      it.next();
      ++count;
    }

    long stop = System.currentTimeMillis();
    System.out.println("Iterated: " + (stop - start));

    return count;
  }
}
