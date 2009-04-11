/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @see http://bugs.eclipse.org/213782
 * @see http://bugs.eclipse.org/201366
 * @author Simon McDuff
 */
public class TransactionTest extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    OMPlatform.INSTANCE.setDebugging(false);
  }

  public void testCreateManySessions() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/test2");
      transaction.commit();
      transaction.close();
      session.close();
    }

    for (int i = 0; i < 100; i++)
    {
      msg("Session " + i);
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test2");
      Category category = getModel1Factory().createCategory();
      resource.getContents().add(category);
      transaction.commit();
      transaction.close();
      session.close();
    }
  }

  public void testCreateManyTransactions() throws Exception
  {
    skipTest(getRepositoryConfig() instanceof RepositoryConfig.DB);
    msg("Opening session");
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test2");
    transaction.commit();
    transaction.close();

    long lastDuration = 0;
    for (int i = 0; i < 1000; i++)
    {
      msg("Transaction " + i + "    (" + lastDuration + ")");
      lastDuration = System.currentTimeMillis();
      transaction = session.openTransaction();
      resource = transaction.getResource("/test2");

      Category category = getModel1Factory().createCategory();
      resource.getContents().add(category);
      transaction.commit();
      transaction.close();
      lastDuration = System.currentTimeMillis() - lastDuration;
    }

    session.close();
  }

  public void testCreateManySessionsAndTransactionsMultiThread() throws Exception
  {
    skipTest(getRepositoryConfig() instanceof RepositoryConfig.DB);
    final long TIMEOUT = 2 * 120L;
    final int THREADS = 5;

    final List<Exception> exceptions = new ArrayList<Exception>();
    final CountDownLatch latch = new CountDownLatch(THREADS);
    List<Thread> threadList = new ArrayList<Thread>();
    for (int i = 0; i < THREADS; i++)
    {
      final int id = i;
      threadList.add(new Thread("TEST-THREAD-" + id)
      {
        @Override
        public void run()
        {
          try
          {
            msg("Thread " + id + ": Started");
            for (int i = 0; i < 100; i++)
            {
              CDOSession session = openModel1Session();
              CDOTransaction transaction = session.openTransaction();

              msg("Thread " + id + ": Session + Transaction " + i);
              transaction.close();
              session.close();
            }

            msg("Thread " + id + ": Done");
          }
          catch (Exception ex)
          {
            System.out.println("Thread " + id + ": " + ex.getClass().getName() + ": " + ex.getMessage());
            synchronized (exceptions)
            {
              exceptions.add(ex);
            }
          }
          finally
          {
            latch.countDown();
          }
        }
      });
    }

    for (Thread thread : threadList)
    {
      thread.start();
    }

    boolean timedOut = !latch.await(TIMEOUT, TimeUnit.SECONDS);

    for (Exception ex : exceptions)
    {
      System.out.println();
      System.out.println();
      IOUtil.print(ex);
    }

    if (timedOut)
    {
      fail("Timeout after " + TIMEOUT + " seconds");
    }
    else
    {
      assertEquals(0, exceptions.size());
    }
  }
}
