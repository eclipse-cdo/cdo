/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

/**
 * Bug 349804: Session is not invalidated after commit.
 *
 * @author Egidijus Vaisnora
 */
public class Bugzilla_349804_Test extends AbstractCDOTest
{
  public void testInvalidation() throws CommitException, InterruptedException
  {
    disableConsole();
    long timeStamp;

    {
      InternalRepository repository = getRepository();

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      transaction.createResource(getResourcePath("test"));
      timeStamp = transaction.commit().getTimeStamp();

      Failure handler = new Failure();
      repository.addHandler(handler);

      try
      {
        // Creating failure commit. It will change last update time on server TimeStampAuthority
        transaction.createResource(getResourcePath("fail"));
        transaction.commit();
        fail("CommitException expected");
      }
      catch (CommitException expected)
      {
        // SUCCESS
      }
      finally
      {
        repository.removeHandler(handler);
        session.close();
      }

      assertNotSame(timeStamp, repository.getLastCommitTimeStamp());
    }

    CDOSession session = openSession();
    assertEquals(getRepository().getLastCommitTimeStamp(), session.getLastUpdateTime());

    CDOTransaction transaction = session.openTransaction();

    final CountDownLatch invalidationLatch = new CountDownLatch(1);
    session.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          invalidationLatch.countDown();
        }
      }
    });

    Company createCompany = getModel1Factory().createCompany();
    CDOResource resource = transaction.getResource(getResourcePath("test"));
    resource.getContents().add(createCompany);

    // Invalidation shall fail, because it will use lastUpdateTime from TimeStampAuthority for commit result
    transaction.commit();

    await(invalidationLatch);
    assertEquals("Invalidation was not delivered", 0, invalidationLatch.getCount());
  }

  public void testDeadlockWithLocking() throws CommitException, InterruptedException, TimeoutException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction1 = session.openTransaction();

      transaction1.createResource(getResourcePath("test"));
      transaction1.commit();

      CDOTransaction failureTransaction = session.openTransaction();
      failureTransaction.createResource(getResourcePath("fail"));

      Failure handler = new Failure();
      getRepository().addHandler(handler);

      try
      {
        // Creating failure commit. It will change last update time on server TimeStampAuthority
        failureTransaction.commit();
        fail("CommitException expected");
      }
      catch (CommitException expected)
      {
        // SUCCESS
      }
      finally
      {
        getRepository().removeHandler(handler);
        session.close();
      }
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOTransaction updaterTransaction = session.openTransaction();

    CDOResource resourceOnUpdater = updaterTransaction.getResource(getResourcePath("test"));
    // Resolve PROXY state
    resourceOnUpdater.getName();

    Company createCompany = getModel1Factory().createCompany();
    CDOResource resource = transaction.getResource(getResourcePath("test"));
    resource.getContents().add(createCompany);
    // Invalidation shall fail, because it will use lastUpdateTime from TimeStampAuthority for commit result
    transaction.commit();

    CDOLock cdoWriteLock = resourceOnUpdater.cdoWriteLock();
    // Waiting for commit which already happen
    cdoWriteLock.lock(1000);
  }

  /**
   * @author Egidijus Vaisnora
   */
  private class Failure implements WriteAccessHandler
  {
    @Override
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
    {
      throw new IllegalArgumentException("Fail on purpose");
    }

    @Override
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
      // Do nothing
    }
  }
}
