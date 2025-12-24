/*
 * Copyright (c) 2016, 2018, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.UnitManager;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.IUnit;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
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
import org.eclipse.emf.cdo.view.CDOUnit;

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
 * Tests that a commit() that starts slightly before a createUnit() contributes to the new unit.
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, "DB.ranges" })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Instrumented repository")
@CleanRepositoriesAfter(reason = "Instrumented repository")
public class Bugzilla_486458b_Test extends AbstractCDOTest
{
  private CountDownLatch analyzeStarted;

  private CountDownLatch initializeFinished;

  private boolean longCommit;

  public void testShortCommitWithParallelCreateUnit() throws Exception
  {
    fillRepository();

    analyzeStarted = new CountDownLatch(1);

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

    CDOUnit unit = transaction.getUnitManager().openUnit(resource, false, null);
    assertEquals(expected, unit.getElements());

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("test"));

    count = iterateResource(resource);
    assertEquals(expected, count);
  }

  @Skips("H2")
  public void testLongCommitWithParallelCreateUnit() throws Exception
  {
    longCommit = true;
    testShortCommitWithParallelCreateUnit();
  }

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(Props.SUPPORTING_UNITS, Boolean.toString(true));
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
      protected InternalUnitManager createUnitManager()
      {
        return new UnitManager(this)
        {
          @Override
          protected void attachObjectsHook1()
          {
            try
            {
              Thread.sleep(2000);
            }
            catch (InterruptedException ex)
            {
              return;
            }

            if (analyzeStarted != null)
            {
              analyzeStarted.countDown();
            }
          }

          @Override
          public IUnit createUnit(CDOID rootID, IView view, CDORevisionHandler revisionHandler, OMMonitor monitor)
          {
            if (analyzeStarted != null)
            {
              await(analyzeStarted);
            }

            return super.createUnit(rootID, view, revisionHandler, monitor);
          }

          @Override
          protected UnitInitializer createUnitInitializer(CDOID rootID, IView view, CDORevisionHandler revisionHandler)
          {
            return new UnitInitializer(rootID, view, revisionHandler)
            {
              @Override
              public IUnit initialize(OMMonitor monitor)
              {
                if (longCommit)
                {
                  initializeFinished = new CountDownLatch(1);
                }

                try
                {
                  return super.initialize(monitor);
                }
                finally
                {
                  if (initializeFinished != null)
                  {
                    initializeFinished.countDown();
                  }
                }
              }
            };
          }

          @Override
          public void objectAttacherFinishedCommit(ObjectAttacher objectAttacher)
          {
            if (initializeFinished != null)
            {
              await(initializeFinished);
              sleep(2000);
            }

            super.objectAttacherFinishedCommit(objectAttacher);
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
