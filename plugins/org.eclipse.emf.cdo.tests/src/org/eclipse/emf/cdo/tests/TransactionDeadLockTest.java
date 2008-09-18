/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;

import org.eclipse.net4j.util.om.OMPlatform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @see http://bugs.eclipse.org/213782
 * @see http://bugs.eclipse.org/201366
 * @author Simon McDuff
 */
public class TransactionDeadLockTest extends AbstractCDOTest
{
  @Override
  protected boolean useJVMTransport()
  {
    OMPlatform.INSTANCE.setDebugging(false);
    return false;
  }

  public void _testCreateManySession() throws Exception
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

  public void testCreateManyTransaction() throws Exception
  {
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
      transaction.setUniqueResourceContents(false);
      resource = transaction.getResource("/test2");
      Category category = getModel1Factory().createCategory();
      resource.getContents().add(category);
      transaction.commit();
      transaction.close();
      lastDuration = System.currentTimeMillis() - lastDuration;
    }

    session.close();
  }

  public void _testCreateManySessionTransactionMultiThread() throws Exception
  {
    final List<Exception> exceptions = new ArrayList<Exception>();
    List<Thread> threadList = new ArrayList<Thread>();
    for (int i = 0; i < 5; i++)
    {
      final int id = i;
      threadList.add(new Thread(new Runnable()
      {
        public void run()
        {
          try
          {
            msg("Thread " + id + ": Starting");
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
            synchronized (exceptions)
            {
              System.out.println("Thread " + id + ": " + ex.getClass().getName() + ": " + ex.getMessage());
              exceptions.add(ex);
            }
          }
        }
      }));
    }

    startThreads(threadList);
    for (Exception exp : exceptions)
    {
      System.out.println();
      System.out.println();
      exp.printStackTrace();
      System.out.println();
      System.out.println();
    }

    assertEquals(0, exceptions.size());
  }

  private void startThreads(Collection<Thread> threadList)
  {
    for (Thread thread : threadList)
    {
      thread.start();
    }

    // Usually takes around 4 seconds on the build machine
    final long TIMEOUT = 120L;

    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() < start + TIMEOUT * 1000L)
    {
      int count = 0;
      for (Thread thread : threadList)
      {
        if (thread.isAlive())
        {
          break;
        }

        count++;
      }

      if (count == threadList.size())
      {
        return;
      }

      sleep(100);
    }

    fail("Timeout after " + TIMEOUT + " seconds");
  }
}
