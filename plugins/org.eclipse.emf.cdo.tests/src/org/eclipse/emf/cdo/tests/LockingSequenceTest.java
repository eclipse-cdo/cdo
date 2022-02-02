/*
 * Copyright (c) 2014, 2018, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.io.IOUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public class LockingSequenceTest extends AbstractLockingTest
{
  private static final int USERS = 10;

  private static final int ALLOCATIONS = 30;

  private static final int RETRIES = 5;

  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    super.doTearDown();
  }

  public void testSafeCounter() throws Exception
  {
    disableConsole();
    SalesOrder sequence = getModel1Factory().createSalesOrder();
    sequence.setId(-1);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(sequence);
    transaction.commit();

    User[] users = new User[USERS];
    CountDownLatch latch = new CountDownLatch(USERS);
    CDOLockOwner[] allocators = new CDOLockOwner[USERS * ALLOCATIONS];

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID] = new User(latch, allocators, session.openTransaction(), sequence);
    }

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID].start();
    }

    await(latch);
    IOUtil.OUT().println("FINISHED");

    Exception exception = null;
    for (int userID = 0; userID < USERS; userID++)
    {
      Exception ex = users[userID].getException();
      if (ex != null)
      {
        exception = ex;
        ex.printStackTrace();
      }
    }

    if (exception != null)
    {
      throw exception;
    }

    IOUtil.OUT().println("SUCCESS");
  }

  /**
   * @author Eike Stepper
   */
  private static final class User extends Thread
  {
    private final CountDownLatch latch;

    private final CDOLockOwner[] allocators;

    private final CDOTransaction transaction;

    private final SalesOrder sequence;

    private Exception exception;

    public User(CountDownLatch latch, CDOLockOwner[] allocators, CDOTransaction transaction, SalesOrder sequence)
    {
      super(transaction.getLockOwner().toString());
      this.latch = latch;
      this.allocators = allocators;
      this.transaction = transaction;
      this.sequence = transaction.getObject(sequence);
    }

    public Exception getException()
    {
      return exception;
    }

    @Override
    public void run()
    {
      try
      {
        CDOLock lock = CDOUtil.getCDOObject(sequence).cdoWriteLock();
        for (int allocation = 0; allocation < ALLOCATIONS; allocation++)
        {
          for (int retry = RETRIES; retry >= 0; --retry)
          {
            try
            {
              lock.lock(1000);
              break;
            }
            catch (TimeoutException ex)
            {
              if (retry == 0)
              {
                exception = ex;
                return;
              }

              msg("Lock timed out. Trying again...");
            }
          }

          try
          {
            int id = sequence.getId() + 1;
            if (allocators[id] != null)
            {
              throw new IllegalStateException(id + " already allocated by " + allocators[id]);
            }

            allocators[id] = transaction.getLockOwner();
            sequence.setId(id);
            msg("Allocated " + id);

            transaction.commit();
            msg("Committed");
            // yield();
          }
          catch (Exception ex)
          {
            exception = ex;
            return;
          }
        }
      }
      finally
      {
        transaction.close();
        latch.countDown();
      }
    }

    private void msg(String string)
    {
      IOUtil.OUT().println(getName() + ": " + string);
    }
  }
}
