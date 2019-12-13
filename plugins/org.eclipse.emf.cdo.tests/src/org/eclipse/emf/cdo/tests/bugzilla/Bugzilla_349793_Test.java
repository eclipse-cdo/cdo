/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.concurrent.CountDownLatch;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_349793_Test extends AbstractCDOTest
{
  /**
   * Creates new transaction at the same time when notification has been sent
   */
  public void testOpeningTransactionDuringInvalidation() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    Model1Factory factory = getModel1Factory();
    Category cat = factory.createCategory();
    resource.getContents().add(cat);
    tx.commit();

    final CountDownLatch invalidationLatch = new CountDownLatch(1);
    final CountDownLatch testExecutionLatch = new CountDownLatch(1);
    tx.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewInvalidationEvent)
        {
          testExecutionLatch.countDown();
          await(invalidationLatch);
        }
      }
    });

    long timestamp = doSecondSessionSync();
    await(testExecutionLatch);

    CDOTransaction freshTransaction = session.openTransaction();
    invalidationLatch.countDown();

    // wait for update from server
    freshTransaction.waitForUpdate(timestamp, DEFAULT_TIMEOUT);

    session.close();
  }

  private long doSecondSessionSync() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath("test"));

    Category cat = (Category)resource.getContents().get(0);
    cat.setName("dirty");
    CDOCommitInfo info;

    try
    {
      info = tx.commit();
    }
    catch (CommitException ex)
    {
      throw new RuntimeException(ex);
    }

    long timeStamp = info.getTimeStamp();
    msg(timeStamp);

    session.close();
    return timeStamp;
  }
}
