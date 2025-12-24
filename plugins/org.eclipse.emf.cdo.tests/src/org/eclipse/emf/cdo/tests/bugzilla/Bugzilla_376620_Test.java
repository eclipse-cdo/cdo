/*
 * Copyright (c) 2012, 2013, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSession.Options;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * @author  Esteban Dugueperoux
 */
public class Bugzilla_376620_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
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
    Supplier supplier = initializeModel(resource1);

    resource1.getContents().add(supplier);
    resource1.save(Collections.emptyMap());

    transaction1.close();
    session.close();

    session = openSession();
    transaction1 = session.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    resource1 = transaction1.getResource(getResourcePath(RESOURCE_PATH));
    supplier = (Supplier)resource1.getContents().get(0);

    CountDownLatch latch = new CountDownLatch(1);
    TestAdapter adapter = new TestAdapter(latch);
    supplier.eAdapters().add(adapter);

    doClient2();
    await(latch);

    assertEquals(true, adapter.notified());
    assertEquals(adapter.getFailureMessage(), true, adapter.assertCorrectNotification());
  }

  private Supplier initializeModel(CDOResource resource1)
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    resource1.getContents().add(supplier);

    return supplier;
  }

  private void doClient2() throws CommitException
  {
    CDOSession session = openSession();
    CDONet4jSession.Options options = (Options)session.options();
    options.setCommitTimeout(10 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);
    CDOTransaction transaction2 = session.openTransaction();

    Resource resource2 = transaction2.getResource(getResourcePath(RESOURCE_PATH));
    Supplier supplier = (Supplier)resource2.getContents().get(0);

    supplier.setPreferred(false);
    transaction2.commit();
  }

  /**
   * @author Martin Fluegge
   */
  private class TestAdapter extends AdapterImpl
  {
    private CountDownLatch latch;

    private boolean assertCorrectNotification;

    private boolean notified;

    private String failureMessage;

    public TestAdapter(CountDownLatch latch)
    {
      this.latch = latch;
    }

    @Override
    public void notifyChanged(Notification notification)
    {
      // If a previous received notification was incorrect we doesn't checks anymore
      if (!notified || notified && assertCorrectNotification)
      {
        try
        {
          Object notifier = notification.getNotifier();
          boolean newBooleanValue = notification.getNewBooleanValue();
          assertCorrectNotification = notifier instanceof Supplier && !newBooleanValue;
          if (!assertCorrectNotification)
          {
            failureMessage = "Notifier is not the expected type : " + notifier.getClass().getName();
          }

          notified = true;
        }
        finally
        {
          latch.countDown();
        }
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
