/*
 * Copyright (c) 2011-2013, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_355915_Test extends AbstractCDOTest
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
    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().setInvalidationNotificationEnabled(true);

    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_PATH));

    // 1. Create a example model
    Customer customer1 = getModel1Factory().createCustomer();
    customer1.setName("Martin Fluegge");
    customer1.setStreet("ABC Street 7");
    customer1.setCity("Berlin");

    TestAdapter adapter = new TestAdapter();
    customer1.eAdapters().add(adapter);

    resource1.getContents().add(customer1);
    resource1.save(Collections.emptyMap());

    Thread thread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        CDOSession session = openSession();
        CDOTransaction transaction2 = session.openTransaction();

        Resource resource2 = transaction2.getResource(getResourcePath(RESOURCE_PATH));
        Customer customer2 = (Customer)resource2.getContents().get(0);
        customer2.setName("newName");

        try
        {
          transaction2.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
      }
    });

    thread.start();
    thread.join();

    await(latch);
    assertEquals(true, adapter.notified());
    assertEquals(true, adapter.assertCorrectNotification());
  }

  @Requires(IModelConfig.CAPABILITY_LEGACY)
  public void testDeltaNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_PATH));

    // 1. Create a example model
    Customer customer1 = getModel1Factory().createCustomer();
    customer1.setName("Martin Fluegge");
    customer1.setStreet("ABC Street 7");
    customer1.setCity("Berlin");

    TestAdapter adapter = new TestAdapter();
    customer1.eAdapters().add(adapter);

    resource1.getContents().add(customer1);
    resource1.save(Collections.emptyMap());

    Thread thread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        CDOSession session = openSession();
        CDOTransaction transaction2 = session.openTransaction();

        Resource resource2 = transaction2.getResource(getResourcePath(RESOURCE_PATH));
        Customer customer2 = (Customer)resource2.getContents().get(0);
        customer2.setName("newName");

        try
        {
          transaction2.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
      }
    });

    thread.start();
    thread.join();

    await(latch);
    assertEquals(true, adapter.notified());
    assertEquals(true, adapter.assertCorrectNotification());
  }

  /**
   * @author Martin Fluegge
   */
  class TestAdapter extends AdapterImpl
  {
    private boolean assertCorrectNotification;

    private boolean notified;

    @Override
    public void notifyChanged(Notification notification)
    {
      notified = true;
      Object notifier = notification.getNotifier();
      assertCorrectNotification = notifier instanceof Customer;
      latch.countDown();
    }

    public boolean notified()
    {
      return notified;
    }

    public boolean assertCorrectNotification()
    {
      return assertCorrectNotification;
    }
  }
}
