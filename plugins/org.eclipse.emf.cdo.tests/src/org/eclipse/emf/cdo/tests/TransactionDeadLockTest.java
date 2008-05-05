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
import org.eclipse.emf.cdo.tests.model1.Model1Factory;

import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.ArrayList;

/**
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=213782
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=201366
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

  public void testCreateManySession() throws Exception
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
      Category category = Model1Factory.eINSTANCE.createCategory();
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
    transaction.createResource("/test2");
    transaction.commit();
    transaction.close();

    for (int i = 0; i < 1000; i++)
    {
      msg("Transaction " + i);
      transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test2");
      Category category = Model1Factory.eINSTANCE.createCategory();
      resource.getContents().add(category);
      transaction.commit();
      transaction.close();
    }

    session.close();
  }

  public void testCreateManySessionTransactionMultiThread() throws Exception
  {
    final ArrayList<Exception> exceptions = new ArrayList<Exception>();
    Runnable threadA = new Runnable()
    {
      public void run()
      {
        try
        {
          msg("Thread Starting");
          for (int i = 0; i < 100; i++)
          {
            CDOSession session = openModel1Session();
            CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

            msg("Session + Transaction " + i);
            transaction.close();
            session.close();
          }

          msg("Thread done");
        }
        catch (Exception e)
        {
          synchronized (exceptions)
          {
            exceptions.add(e);
          }
        }
      }
    };

    ArrayList<Thread> threadList = new ArrayList<Thread>();

    for (int i = 0; i < 5; i++)
    {
      threadList.add(new Thread(threadA));
    }

    for (int i = 0; i < threadList.size(); i++)
    {
      threadList.get(i).start();
    }

    while (true)
    {
      int count = 0;
      for (int i = 0; i < threadList.size(); i++)
      {
        if (threadList.get(i).isAlive())
        {
          break;
        }

        count++;
      }

      if (count == threadList.size())
      {
        break;
      }

      sleep(1000);
    }

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
}
