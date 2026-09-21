/*
 * Copyright (c) 2015, 2016, 2019, 2021, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSession.Options;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Bug 377173 : test commit progress/cancel.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_377173_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  public void testCancelCommitWithProgressMonitor() throws Exception
  {
    CDOSession session = openSession();
    CDONet4jSession.Options options = (Options)session.options();
    options.setCommitTimeout(100000 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);

    CDOTransaction transaction1 = session.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_PATH));

    // 1. Create a example model
    Company customer1 = initializeModel(resource1);

    resource1.getContents().add(customer1);

    CommitWaiter commitWaiter = new CommitWaiter();
    getRepository().addHandler(commitWaiter);
    ProgressMonitorAsserter progressMonitorAsserter = new ProgressMonitorAsserter();
    try
    {
      Future<Exception> commit = getExecutorService().submit(() -> {
        try
        {
          transaction1.commit(progressMonitorAsserter);
          return null;
        }
        catch (Exception ex)
        {
          return ex;
        }
      });

      await(commitWaiter.getEntered());
      await(progressMonitorAsserter.getProgressed());

      Exception exception = commit.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      assertTrue(exception instanceof OperationCanceledException);
    }
    finally
    {
      getRepository().removeHandler(commitWaiter);
    }
  }

  /**
   * @author Esteban Dugueperoux
   */
  private static class ProgressMonitorAsserter extends NullProgressMonitor
  {
    private final CountDownLatch progressed = new CountDownLatch(1);

    public CountDownLatch getProgressed()
    {
      return progressed;
    }

    @Override
    public void internalWorked(double work)
    {
      super.internalWorked(work);
      setCanceled(true);
      progressed.countDown();
    }
  }

  /**
   * @author Esteban Dugueperoux
   */
  private static class CommitWaiter implements IRepository.WriteAccessHandler
  {
    private final CountDownLatch entered = new CountDownLatch(1);

    public CountDownLatch getEntered()
    {
      return entered;
    }

    @Override
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
    {
      entered.countDown();
      monitor.worked();

      long timeout = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
      while (!monitor.isCanceled())
      {
        if (System.currentTimeMillis() > timeout)
        {
          throw new IllegalStateException("Commit monitor was not canceled");
        }

        Thread.yield();
      }
    }

    @Override
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
    }
  }

  private Company initializeModel(CDOResource resource)
  {
    Company company = getModel1Factory().createCompany();

    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Martin Fluegge");
    customer.setStreet("ABC Street 7");
    customer.setCity("Berlin");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    customer.getSalesOrders().add(salesOrder);
    resource.getContents().add(salesOrder);
    resource.getContents().add(customer);

    for (int i = 0; i < 1000; i++)
    {
      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
      company.getPurchaseOrders().add(purchaseOrder);
    }

    company.getCustomers().add(customer);

    return company;
  }
}
