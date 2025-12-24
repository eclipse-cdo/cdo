/*
 * Copyright (c) 2011-2013, 2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSession.Options;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * Bug 359992.
 *
 * @author Martin Fluegge
 */
public class Bugzilla_359992_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  private CountDownLatch latch;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    latch = new CountDownLatch(1);
  }

  @Requires(IModelConfig.CAPABILITY_LEGACY)
  public void testInvalidationNotification() throws Exception
  {
    CDOSession session = openSession();
    CDONet4jSession.Options options = (Options)session.options();
    options.setCommitTimeout(10 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().setInvalidationNotificationEnabled(true);

    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_PATH));

    // 1. Create a example model
    Customer customer1 = initializeModel(resource1);

    resource1.getContents().add(customer1);
    resource1.save(Collections.emptyMap());

    transaction1.close();
    session.close();

    session = openSession();
    transaction1 = session.openTransaction();
    transaction1.options().setInvalidationNotificationEnabled(true);

    resource1 = transaction1.getResource(getResourcePath(RESOURCE_PATH));
    customer1 = (Customer)resource1.getContents().get(1);

    TestAdapter adapter = new TestAdapter();
    customer1.eAdapters().add(adapter);

    doClient2();

    await(latch, 10 * DEFAULT_TIMEOUT);
    assertEquals(true, adapter.notified());
    assertEquals(adapter.getFailureMessage(), true, adapter.assertCorrectNotification());
  }

  @Requires(IModelConfig.CAPABILITY_LEGACY)
  public void testDeltaNotification() throws Exception
  {
    CDOSession session = openSession();
    CDONet4jSession.Options options = (Options)session.options();
    options.setCommitTimeout(10 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_PATH));

    // 1. Create a example model
    Customer customer1 = initializeModel(resource1);

    resource1.getContents().add(customer1);
    resource1.save(Collections.emptyMap());

    transaction1.close();
    session.close();

    session = openSession();
    transaction1 = session.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    resource1 = transaction1.getResource(getResourcePath(RESOURCE_PATH));
    customer1 = (Customer)resource1.getContents().get(1);

    TestAdapter adapter = new TestAdapter();
    customer1.eAdapters().add(adapter);

    doClient2();

    await(latch, 10 * DEFAULT_TIMEOUT);
    assertEquals(true, adapter.notified());
    assertEquals(adapter.getFailureMessage(), true, adapter.assertCorrectNotification());
  }

  private Customer initializeModel(CDOResource resource1)
  {
    Customer customer1 = Model1Factory.eINSTANCE.createCustomer();
    customer1.setName("Martin Fluegge");
    customer1.setStreet("ABC Street 7");
    customer1.setCity("Berlin");

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    customer1.getSalesOrders().add(salesOrder);
    resource1.getContents().add(salesOrder);

    return customer1;
  }

  private void doClient2() throws CommitException
  {
    CDOSession session = openSession();
    CDONet4jSession.Options options = (Options)session.options();
    options.setCommitTimeout(10 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);
    CDOTransaction transaction2 = session.openTransaction();

    Resource resource2 = transaction2.getResource(getResourcePath(RESOURCE_PATH));
    Customer customer2 = (Customer)resource2.getContents().get(1);

    SalesOrder existingSalesOrder = customer2.getSalesOrders().get(0);
    EcoreUtil.delete(existingSalesOrder);

    SalesOrder newSalesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    customer2.getSalesOrders().add(newSalesOrder);
    resource2.getContents().add(newSalesOrder);

    transaction2.commit();
  }

  /**
   * @author Martin Fluegge
   */
  class TestAdapter extends AdapterImpl
  {
    private boolean assertCorrectNotification;

    private boolean notified;

    private String failureMessage;

    @Override
    public void notifyChanged(Notification notification)
    {
      // If a previous received notification was incorrect we doesn't checks anymore
      if (!notified || notified && assertCorrectNotification)
      {
        Object notifier = notification.getNotifier();
        assertCorrectNotification = notifier instanceof Customer;
        if (!assertCorrectNotification)
        {
          failureMessage = "Notifier is not the expected type : " + notifier.getClass().getName();
        }
        if (!(notification instanceof CDOInvalidationNotification) && assertCorrectNotification)
        {
          int eventType = notification.getEventType();
          if (Notification.ADD == eventType)
          {
            Object newValue = notification.getNewValue();
            assertCorrectNotification = newValue instanceof SalesOrder;
            if (!assertCorrectNotification)
            {
              failureMessage = "Notification.getNewValue() is not the expected type : " + newValue.getClass().getName();
            }
          }
          else if (Notification.REMOVE == eventType)
          {
            Object oldValue = notification.getOldValue();
            assertCorrectNotification = oldValue instanceof SalesOrder;
            if (!assertCorrectNotification)
            {
              failureMessage = "Notification.getOldValue() is not the expected type : " + oldValue.getClass().getName();
            }
          }
        }

        notified = true;
        latch.countDown();
      }
    }

    public boolean notified()
    {
      return notified;
    }

    public boolean assertCorrectNotification()
    {
      return assertCorrectNotification;
    }

    public String getFailureMessage()
    {
      return failureMessage;
    }
  }
}
