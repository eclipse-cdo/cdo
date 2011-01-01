/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * See bug 213782, bug 201366
 * 
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

  public void testCommitAfterClose() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(true, LifecycleUtil.isActive(session));
    assertEquals(false, session.isClosed());

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource("/test1");
    resource.getContents().add(getModel1Factory().createCompany());
    assertEquals(true, LifecycleUtil.isActive(transaction));
    assertEquals(false, transaction.isClosed());

    session.close();
    assertEquals(false, LifecycleUtil.isActive(session));
    assertEquals(true, session.isClosed());
    assertEquals(false, LifecycleUtil.isActive(transaction));
    assertEquals(true, transaction.isClosed());

    try
    {
      transaction.commit();
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException success)
    {
      // SUCCESS
    }
  }

  public void testCreateManySessions() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/test2");
      transaction.commit();
      transaction.close();
      session.close();
    }

    for (int i = 0; i < 100; i++)
    {
      msg("Session " + i);
      CDOSession session = openSession();
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
    msg("Opening session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test2");
    transaction.commit();
    transaction.close();

    long lastDuration = 0;
    for (int i = 0; i < 100; i++)
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
              CDOSession session = openSession();
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

  public void testPushModeNewObjects() throws Exception
  {
    msg("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    msg("Creating category2");
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");

    msg("Creating category3");
    Category category3 = getModel1Factory().createCategory();
    category3.setName("category3");

    msg("Creating company");
    Company company = getModel1Factory().createCompany();
    company.setName("Foundation");

    msg("Adding categories");
    company.getCategories().add(category1);
    category1.getCategories().add(category2);
    category1.getCategories().add(category3);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
    File file = pushTransaction.getFile();

    CDOResource resource = pushTransaction.getOrCreateResource("/test1");
    resource.getContents().add(company);

    pushTransaction.commit();
    pushTransaction.close();
    transaction = session.openTransaction();
    pushTransaction = new CDOPushTransaction(transaction, file);
    pushTransaction.push();

    session.close();
    session = openSession();

    CDOView view = session.openView();
    resource = view.getResource("/test1");
    company = (Company)resource.getContents().get(0);
    assertEquals("Foundation", company.getName());
    assertEquals(1, company.getCategories().size());
    assertEquals(2, company.getCategories().get(0).getCategories().size());
    session.close();
  }

  public void testPushModeDeltas() throws Exception
  {
    msg("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    msg("Creating company");
    Company company = getModel1Factory().createCompany();
    company.setName("Foundation");

    msg("Adding categories");
    company.getCategories().add(category1);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
    File file = pushTransaction.getFile();

    CDOResource resource = pushTransaction.getOrCreateResource("/test1");
    resource.getContents().add(company);

    pushTransaction.commit();

    msg("Creating category2");
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");
    category1.getCategories().add(category2);

    msg("Creating category3");
    Category category3 = getModel1Factory().createCategory();
    category3.setName("category3");
    category1.getCategories().add(category3);

    pushTransaction.commit();
    pushTransaction.close();

    transaction = session.openTransaction();
    pushTransaction = new CDOPushTransaction(transaction, file);
    pushTransaction.push();

    session.close();
    session = openSession();

    CDOView view = session.openView();
    resource = view.getResource("/test1");
    company = (Company)resource.getContents().get(0);
    assertEquals("Foundation", company.getName());
    assertEquals(1, company.getCategories().size());
    assertEquals(2, company.getCategories().get(0).getCategories().size());
    session.close();
  }
}
