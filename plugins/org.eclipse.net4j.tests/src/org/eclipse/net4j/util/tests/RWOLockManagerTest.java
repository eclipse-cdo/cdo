/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class RWOLockManagerTest extends AbstractOMTest
{
  private static final int USERS = 10;

  private static final int ALLOCATIONS = 10;

  private static final int RETRIES = 5;

  private static final Set<Object> EXCLUSIVE_RESOURCE = Collections.singleton(new Object()
  {
    @Override
    public String toString()
    {
      return "EXCLUSIVE_RESOURCE";
    }
  });

  public void testRWOLockManager() throws Exception
  {
    AtomicInteger resource = new AtomicInteger(-1);
    RWOLockManager<Object, User> lockManager = new RWOLockManager<>();

    User[] users = new User[USERS];
    User[] allocators = new User[USERS * ALLOCATIONS];

    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(USERS);

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID] = new User(userID, started, finished, allocators, lockManager, resource);
    }

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID].start();
    }

    started.countDown();
    await(finished);
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
    private final CountDownLatch started;

    private final CountDownLatch finished;

    private final User[] allocators;

    private final RWOLockManager<Object, User> lockManager;

    private final AtomicInteger resource;

    private Exception exception;

    public User(int userID, CountDownLatch started, CountDownLatch finished, User[] allocators, RWOLockManager<Object, User> lockManager,
        AtomicInteger resource)
    {
      super("User-" + userID);
      this.started = started;
      this.finished = finished;
      this.allocators = allocators;
      this.lockManager = lockManager;
      this.resource = resource;
    }

    public Exception getException()
    {
      return exception;
    }

    @Override
    public void run()
    {
      await(started);

      try
      {
        for (int allocation = 0; allocation < ALLOCATIONS; allocation++)
        {
          for (int retry = RETRIES; retry >= 0; --retry)
          {
            try
            {
              lockManager.lock(this, EXCLUSIVE_RESOURCE, LockType.WRITE, 1, 10000000, null, null);
              break;
            }
            catch (TimeoutRuntimeException ex)
            {
              if (retry == 0)
              {
                exception = ex;
                return;
              }

              msg("Lock timed out. Trying again...");
            }
            catch (InterruptedException ex)
            {
              exception = ex;
              return;
            }
          }

          try
          {
            int id = resource.get() + 1;
            if (allocators[id] != null)
            {
              throw new IllegalStateException(id + " already allocated by " + allocators[id]);
            }

            allocators[id] = this;
            resource.set(id);
            msg("ALLOCATED " + id);
          }
          catch (Exception ex)
          {
            exception = ex;
            return;
          }
          finally
          {
            lockManager.unlock(this, EXCLUSIVE_RESOURCE, LockType.WRITE, 1, null, null);
          }
        }
      }
      finally
      {
        finished.countDown();
      }
    }

    @Override
    public String toString()
    {
      return getName();
    }

    private void msg(String string)
    {
      IOUtil.OUT().println(getName() + ": " + string);
    }
  }
}
