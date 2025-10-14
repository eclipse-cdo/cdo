/*
 * Copyright (c) 2015, 2016, 2018, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

/**
 * Implementation based on LockingSequenceTest.
 *
 * @author Eike Stepper
 */
@Requires({ ISessionConfig.CAPABILITY_NET4J_JVM, IModelConfig.CAPABILITY_NATIVE })
public class Locking_SequenceWithChildListTest_DISABLED extends AbstractLockingTest
{
  private static final int USERS = 10;

  private static final int ADDITIONS = 100;

  private static final int RETRIES = 5;

  private static final long LOCK_TIMEOUT = 5000;

  private static final boolean LOG = false;

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
    final Company company = getModel1Factory().createCompany();

    final CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(company);
    transaction.commit();

    CountDownLatch latch = new CountDownLatch(USERS);
    User[] users = new User[USERS];

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID] = new User(userID, latch, company);
    }

    for (int userID = 0; userID < USERS; userID++)
    {
      users[userID].start();
    }

    // A maximum of USERS x ADDITIONS x RETRIES calls to .lock() : should not last more than USERS x ADDITIONS x
    // RETRIES x LOCK_TIMEOUT milliseconds...
    // Let's leave them twice as long, just to be sure!
    long timeout = 2 * USERS * ADDITIONS * RETRIES * LOCK_TIMEOUT;
    await(latch, timeout);

    IOUtil.OUT().println("FINISHED");

    //
    // CHECK UP TIME!
    //

    CDOObject cdoObject = CDOUtil.getCDOObject(company);
    CDOLock lock = cdoObject.cdoWriteLock();
    assertEquals("Lock hasn't been released!", false, lock.isLockedByOthers());

    assertEquals("There are unfinished users", 0, latch.getCount());

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

    for (User user : users)
    {
      if (null == user.getException())
      {
        assertEquals(ADDITIONS, user.getAdditions());
      }
    }

    IOUtil.OUT().println("SUCCESS");
  }

  private final class User extends Thread
  {
    private final CountDownLatch latch;

    private Company c;

    private int additions;

    private Exception exception;

    public User(int userID, CountDownLatch latch, Company company)
    {
      super("User" + userID);
      this.latch = latch;
      c = company;
    }

    public Exception getException()
    {
      return exception;
    }

    public int getAdditions()
    {
      return additions;
    }

    @Override
    public void run()
    {
      try
      {
        for (int allocation = 0; allocation < ADDITIONS; allocation++)
        {
          CDOSession session = Locking_SequenceWithChildListTest_DISABLED.this.openSession();
          InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction();

          try
          {
            transaction.sync().run(() -> {
              for (int retry = 0; retry < RETRIES; ++retry)
              {
                Company company = transaction.getObject(c);
                CDOObject cdoObject = CDOUtil.getCDOObject(company);
                CDOLock lock = cdoObject.cdoWriteLock();

                TimerThread timerThread = new TimerThread(getName() + ": lock(" + LOCK_TIMEOUT + ")");

                try
                {
                  timerThread.start();
                  lock.lock(LOCK_TIMEOUT);

                  Category category = getModel1Factory().createCategory();
                  company.getCategories().add(category);

                  try
                  {
                    int version = cdoObject.cdoRevision().getVersion() + 1;
                    System.out.println(getName() + ": committing version " + version);

                    transaction.setCommitComment(getName() + ": version " + version);
                    transaction.commit();

                    ++additions;
                    msg("Category added " + getAdditions());
                    break; // No more retries.
                  }
                  catch (Exception ex)
                  {
                    exception = ex;
                    return;
                  }
                }
                catch (TimeoutException ex)
                {
                  msg("Lock timed out.");

                  exception = ex;
                  if (retry == RETRIES - 1)
                  {
                    msg("Exhausted!");
                    return;
                  }

                  msg("Trying again...");
                }
                finally
                {
                  timerThread.done();
                }
              }
            });
          }
          finally
          {
            session.close();
          }
        }
      }
      finally
      {
        latch.countDown();
      }
    }

    private void msg(String string)
    {
      if (LOG)
      {
        IOUtil.OUT().println(getName() + ": " + string);
      }
    }
  }

  private final class TimerThread extends Thread
  {
    private final long timeout = LOCK_TIMEOUT * 4;

    private boolean done;

    public TimerThread(String name)
    {
      super(name);
    }

    public void done()
    {
      done = true;
    }

    @Override
    public void run()
    {
      long start = System.currentTimeMillis();

      while (!done)
      {
        long now = System.currentTimeMillis();
        if (now - start > timeout)
        {
          System.out.println();
          System.out.println(getName() + " is taking more time than required : " + (now - start) + " ms > " + timeout + " ms.");
        }

        try
        {
          sleep(10);
        }
        catch (InterruptedException ex)
        {
          ex.printStackTrace();
          if (LOG)
          {
            System.out.println(getName() + " : interrupted");
          }

          return;
        }
      }

      long end = System.currentTimeMillis();
      if (LOG)
      {
        System.out.println("" + getName() + " took " + (end - start) + " ms.");
      }
    }
  }
}
