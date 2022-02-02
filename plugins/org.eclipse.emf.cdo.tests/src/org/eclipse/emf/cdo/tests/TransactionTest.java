/*
 * Copyright (c) 2008-2012, 2014, 2018-2022 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler2;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.signal.SignalCounter;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.notify.Notification;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
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
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // SUCCESS
    }
  }

  /**
   * See bug 335432
   */
  public void testLastUpdateTime() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(getModel1Factory().createCompany());

    long timeStamp = transaction.commit().getTimeStamp();
    assertEquals(timeStamp, session.getLastUpdateTime());
    assertEquals(timeStamp, transaction.getLastUpdateTime());
  }

  public void testCreateManySessions() throws Exception
  {
    {
      IOUtil.OUT().println("Opening session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(getResourcePath("/test2"));
      transaction.commit();
      transaction.close();
      session.close();
    }

    for (int i = 0; i < 100; i++)
    {
      IOUtil.OUT().println("Session " + i);
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test2"));
      Category category = getModel1Factory().createCategory();
      resource.getContents().add(category);
      transaction.commit();
      transaction.close();
      session.close();
    }
  }

  public void testCreateManyTransactions() throws Exception
  {
    IOUtil.OUT().println("Opening session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test2"));
    transaction.commit();
    transaction.close();

    long lastDuration = 0;
    for (int i = 0; i < 100; i++)
    {
      IOUtil.OUT().println("Transaction " + i + "    (" + lastDuration + ")");
      lastDuration = System.currentTimeMillis();
      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("/test2"));

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

    final List<Exception> exceptions = new ArrayList<>();
    final CountDownLatch latch = new CountDownLatch(THREADS);
    List<Thread> threadList = new ArrayList<>();
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
            IOUtil.OUT().println("Thread " + id + ": Started");
            for (int i = 0; i < 100; i++)
            {
              CDOSession session = openSession();
              CDOTransaction transaction = session.openTransaction();

              IOUtil.OUT().println("Thread " + id + ": Session + Transaction " + i);
              transaction.close();
              session.close();
            }

            IOUtil.OUT().println("Thread " + id + ": Done");
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

  public void testCommitManyTransactionsMultiThread() throws Exception
  {
    final int RUNS = 1;
    final int THREADS = 100;
    final boolean pessimistic = true;

    CDOSession session = openSession();

    CDOTransaction initialTransaction = session.openTransaction();
    CDOResource resource = initialTransaction.createResource(getResourcePath("myResource"));

    final Company initialCompany = getModel1Factory().createCompany();
    resource.getContents().add(initialCompany);
    initialTransaction.commit();

    SignalCounter signalCounter = new SignalCounter(((CDONet4jSession)session).options().getNet4jProtocol());

    for (int run = 1; run <= RUNS; run++)
    {
      System.out.println("RUN " + run);

      CountDownLatch latch = new CountDownLatch(THREADS);
      List<Thread> threadList = new ArrayList<>();

      for (int thread = 0; thread < THREADS; thread++)
      {
        final CDOTransaction transaction = session.openTransaction();
        final Company company = transaction.getObject(initialCompany);
        final Customer newCustomer = Model1Factory.eINSTANCE.createCustomer();

        threadList.add(new Committer(transaction, latch, new Callable<Boolean>()
        {
          @Override
          public Boolean call() throws Exception
          {
            if (pessimistic)
            {
              CDOUtil.getCDOObject(company).cdoWriteLock().lock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            }

            company.getCustomers().add(newCustomer);
            return true;
          }
        }));
      }

      for (Thread thread : threadList)
      {
        thread.start();
      }

      await(latch, 2 * DEFAULT_TIMEOUT);
      signalCounter.dump(IOUtil.OUT(), true);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Committer extends Thread
  {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("COMMITTERS");

    private final CDOTransaction transaction;

    private final CountDownLatch latch;

    private final Callable<Boolean> operation;

    public Committer(CDOTransaction transaction, CountDownLatch latch, Callable<Boolean> operation)
    {
      super(THREAD_GROUP, "Committer-" + transaction.getViewID());
      this.transaction = transaction;
      this.latch = latch;
      this.operation = operation;
    }

    @Override
    public void run()
    {
      try
      {
        transaction.commit(operation, 200, null);
      }
      catch (Exception ex)
      {
        System.out.println(ex.getClass().getName() + " --> " + ex.getMessage());
      }
      finally
      {
        try
        {
          transaction.close();
        }
        finally
        {
          latch.countDown();
        }
      }
    }
  }

  public void testPushModeNewObjects() throws Exception
  {
    IOUtil.OUT().println("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    IOUtil.OUT().println("Creating category2");
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");

    IOUtil.OUT().println("Creating category3");
    Category category3 = getModel1Factory().createCategory();
    category3.setName("category3");

    IOUtil.OUT().println("Creating company");
    Company company = getModel1Factory().createCompany();
    company.setName("Foundation");

    IOUtil.OUT().println("Adding categories");
    company.getCategories().add(category1);
    category1.getCategories().add(category2);
    category1.getCategories().add(category3);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
    File file = pushTransaction.getFile();

    CDOResource resource = pushTransaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(company);

    pushTransaction.commit();
    pushTransaction.close();
    transaction = session.openTransaction();
    pushTransaction = new CDOPushTransaction(transaction, file);
    pushTransaction.push();

    session.close();
    session = openSession();

    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test1"));
    company = (Company)resource.getContents().get(0);
    assertEquals("Foundation", company.getName());
    assertEquals(1, company.getCategories().size());
    assertEquals(2, company.getCategories().get(0).getCategories().size());
    session.close();
  }

  public void testPushModeDeltas() throws Exception
  {
    IOUtil.OUT().println("Creating category1");
    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    IOUtil.OUT().println("Creating company");
    Company company = getModel1Factory().createCompany();
    company.setName("Foundation");

    IOUtil.OUT().println("Adding categories");
    company.getCategories().add(category1);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
    File file = pushTransaction.getFile();

    CDOResource resource = pushTransaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(company);

    pushTransaction.commit();

    IOUtil.OUT().println("Creating category2");
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");
    category1.getCategories().add(category2);

    IOUtil.OUT().println("Creating category3");
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
    resource = view.getResource(getResourcePath("/test1"));
    company = (Company)resource.getContents().get(0);
    assertEquals("Foundation", company.getName());
    assertEquals(1, company.getCategories().size());
    assertEquals(2, company.getCategories().get(0).getCategories().size());
    session.close();
  }

  public void testAutoRollbackOnConflictEvent() throws Exception
  {
    final CDOSession session1 = openSession();
    final CDOTransaction transaction1 = session1.openTransaction();

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test"));
    Category category1 = getModel1Factory().createCategory();
    resource1.getContents().add(category1);
    transaction1.commit();

    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();
    transaction2.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOTransactionConflictEvent)
        {
          transaction2.rollback();
        }
      }
    });

    final CountDownLatch rollback = new CountDownLatch(1);
    transaction2.addTransactionHandler(new CDOTransactionHandler2()
    {
      @Override
      public void rolledBackTransaction(CDOTransaction transaction)
      {
        IOUtil.OUT().println("rollback");
        rollback.countDown();
      }

      @Override
      public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
      }

      @Override
      public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
      }
    });

    CDOResource resource2 = transaction2.getResource(getResourcePath("/test"));
    Category category2 = (Category)resource2.getContents().get(0);
    category2.setName("session2");

    category1.setName("session1");
    transaction1.commit();

    await(rollback);
    category2.setName("session2");
    transaction2.commit();
  }

  /**
   * Bug 283131
   */
  public void testRollbackWithNotification() throws CommitException
  {
    Category category = getModel1Factory().createCategory();
    category.setName("name1");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test"));

    resource1.getContents().add(category);
    transaction.commit();

    category.setName("name2");

    TestAdapter testAdapter = new TestAdapter(category);

    transaction.rollback();

    Notification notification = testAdapter.assertNotifications(1)[0];
    assertEquals("name2", notification.getOldStringValue());
    assertEquals("name1", notification.getNewStringValue());
  }

  public void testManualRollbackOnConflictException() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test"));
    Category category1 = getModel1Factory().createCategory();
    resource1.getContents().add(category1);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    CDOResource resource2 = transaction2.getResource(getResourcePath("/test"));
    Category category2 = (Category)resource2.getContents().get(0);
    category2.setName("session2");

    category1.setName("session1");
    commitAndSync(transaction1, transaction2);
    IOUtil.OUT().println("After transaction1.commit(): " + CDOUtil.getCDOObject(category1).cdoRevision());

    CDOObject cdoCategory2 = CDOUtil.getCDOObject(category2);

    try
    {
      IOUtil.OUT().println("Before transaction2.commit(): " + cdoCategory2.cdoRevision());
      category2.setName("session2");

      transaction2.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      IOUtil.OUT().println("Before transaction2.rollback(): " + cdoCategory2.cdoRevision());
      transaction2.rollback();
      IOUtil.OUT().println("After transaction2.rollback(): " + cdoCategory2.cdoRevision());
    }

    category2.setName("session2");

    IOUtil.OUT().println("Before transaction2.commit():");
    CDOCommitInfoUtil.dump(IOUtil.OUT(), transaction2.getChangeSetData());

    transaction2.commit();
    IOUtil.OUT().println("After transaction2.commit(): " + cdoCategory2.cdoRevision());
    assertEquals(3, cdoCategory2.cdoRevision().getVersion());
  }

  public void testLongCommit() throws Exception
  {
    OMPlatform.INSTANCE.setDebugging(true);

    CDOCommitInfoHandler handler = new CDOCommitInfoHandler()
    {
      @Override
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        sleep(3000L);
      }
    };

    CDOCommitInfoManager commitInfoManager = getRepository().getCommitInfoManager();
    commitInfoManager.addCommitInfoHandler(handler);

    try
    {
      CDOSession session = openSession();
      if (session instanceof CDONet4jSession)
      {
        ((CDONet4jSession)session).options().setCommitTimeout(1);

      }
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
      resource.getContents().add(getModel1Factory().createCompany());

      transaction.commit();
    }
    finally
    {
      commitInfoManager.removeCommitInfoHandler(handler);
    }
  }

  public void _testLongCommit2() throws Exception
  {
    OMPlatform.INSTANCE.setDebugging(true);
    Field field = ReflectUtil.getField(CommitTransactionRequest.class, "sleepMillisForTesting");

    try
    {
      ReflectUtil.setValue(field, null, 1000L);

      org.eclipse.emf.cdo.net4j.CDONet4jSession session = (org.eclipse.emf.cdo.net4j.CDONet4jSession)openSession();
      session.options().setCommitTimeout(60);

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));

      for (int i = 0; i < 300; i++)
      {
        resource.getContents().add(getModel1Factory().createCompany());
      }

      transaction.commit();
    }
    finally
    {
      ReflectUtil.setValue(field, null, 0L);
    }
  }

  /**
   * Bug 353167.
   */
  public void testReattachCommit() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    resource.getContents().remove(company);
    resource.getContents().add(company);
    transaction.commit();
  }

  /**
   * Bug 353167.
   */
  public void testReattachModifyCommit() throws Exception
  {
    {
      Company company = getModel1Factory().createCompany();

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
      resource.getContents().add(company);
      transaction.commit();

      resource.getContents().remove(company);
      resource.getContents().add(company);
      company.setName("ESC");
      transaction.commit();
    }

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0);
    assertEquals("ESC", company2.getName());
  }

  /**
   * Bug 353167.
   */
  public void testReattachCommitWithSavepoints() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    resource.getContents().remove(company);
    transaction.setSavepoint();

    resource.getContents().add(company);
    transaction.setSavepoint();
    transaction.commit();
  }

  /**
   * Bug 353167.
   */
  public void testReattachModifyCommitWithSavepoints() throws Exception
  {
    {
      Company company = getModel1Factory().createCompany();

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
      resource.getContents().add(company);
      transaction.commit();

      resource.getContents().remove(company);
      resource.getContents().add(company);
      transaction.setSavepoint();

      company.setName("ESC");
      transaction.commit();
    }

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0);
    assertEquals("ESC", company2.getName());
  }

  public void testCommitRedundantChanges() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    company.setName("Eclipse");

    resource.getContents().add(company);
    transaction.commit();

    company.setName("XYZ");
    company.setName("Eclipse");
    transaction.commit();

    company.setName("ABC");
    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testRevertTo() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    CDOBranchPoint[] commits = new CDOBranchPoint[10];
    for (int i = 0; i < commits.length; i++)
    {
      company.setName("Eclipse " + i);
      transaction.setCommitComment((i == 0 ? "Attach " : "Modify ") + company.getName());
      commits[i] = CDOBranchUtil.copyBranchPoint(transaction.commit());
      IOUtil.OUT().println("Commit " + commits[i].getTimeStamp() + " --> " + company.getName());
    }

    company.setName("Eclipse " + commits.length);
    transaction.setCommitComment("Modify " + company.getName());
    CDOBranchPoint eclipse10Commit = CDOBranchUtil.copyBranchPoint(transaction.commit());
    IOUtil.OUT().println("Commit " + eclipse10Commit.getTimeStamp() + " --> " + company.getName());
    IOUtil.OUT().println();

    for (int i = commits.length - 1; i >= 0; --i)
    {
      CDOChangeSetData changeSetData = transaction.revertTo(commits[i]);
      IOUtil.OUT().println("Revert " + commits[i].getTimeStamp() + " --> " + company.getName());

      assertEquals(0, changeSetData.getNewObjects().size());
      assertEquals(1, changeSetData.getChangedObjects().size());
      assertEquals(0, changeSetData.getDetachedObjects().size());
      assertEquals("Eclipse " + i, company.getName());
      transaction.setCommitComment("Revert to " + commits[i].getTimeStamp());
      transaction.commit();
    }

    // Attach a category.
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    transaction.setCommitComment("Attach a category");
    CDOCommitInfo attach = transaction.commit();
    long beforeAttach = attach.getTimeStamp() - 1;
    assertClean(category, transaction);

    // Revert the attachment.
    CDOChangeSetData revertAttachData = transaction.revertTo(attach.getBranch().getPoint(beforeAttach));
    assertEquals(0, revertAttachData.getNewObjects().size());
    assertEquals(1, revertAttachData.getChangedObjects().size()); // The resource.
    assertEquals(1, revertAttachData.getDetachedObjects().size()); // The category.
    assertEquals(0, company.getCategories().size());
    assertTransient(category);
    transaction.setCommitComment("Revert to " + beforeAttach);
    transaction.commit();

    // Detach the company.
    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    resource.getContents().clear();
    transaction.setCommitComment("Detach the company");
    CDOCommitInfo detach = transaction.commit();
    long beforeDetach = detach.getTimeStamp() - 1;
    assertEquals(0, resource.getContents().size());
    assertTransient(company);

    // Revert the detachment.
    CDOChangeSetData revertDetachData = transaction.revertTo(detach.getBranch().getPoint(beforeDetach));
    assertEquals(1, revertDetachData.getChangedObjects().size()); // The resource.
    assertEquals(1, revertDetachData.getNewObjects().size()); // The company.
    assertEquals(0, revertDetachData.getDetachedObjects().size());
    assertEquals(1, resource.getContents().size());

    CDOObject object = transaction.getObject(companyID);
    assertEquals(CDOState.NEW, object.cdoState());

    transaction.setCommitComment("Revert to " + beforeDetach);
    transaction.commit();

    if (getRepositoryConfig().supportingBranches())
    {
      CDOBranch subBranch = transaction.getBranch().createBranch(getBranchName("subBranch"));

      try
      {
        transaction.revertTo(subBranch.getHead());
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(expected.getMessage().startsWith("Can not revert to"));
      }

      // Commit a few times into the sub branch.
      transaction.setBranch(subBranch);
      company.setName("Sub-Eclipse");
      transaction.commit();

      company.getSuppliers().add(getModel1Factory().createSupplier());
      company.getSuppliers().add(getModel1Factory().createSupplier());
      company.getSuppliers().add(getModel1Factory().createSupplier());
      company.getSuppliers().add(getModel1Factory().createSupplier());
      company.getSuppliers().add(getModel1Factory().createSupplier());
      transaction.commit();

      company.getSuppliers().remove(0);
      company.getSuppliers().remove(0);
      transaction.commit();

      CDOChangeSetData revertDetachData2 = transaction.revertTo(eclipse10Commit);
      assertEquals(0, revertDetachData2.getNewObjects().size());
      assertEquals(1, revertDetachData2.getChangedObjects().size()); // The resource.
      assertEquals(0, revertDetachData2.getDetachedObjects().size());
      assertEquals(1, resource.getContents().size());
      assertEquals(3, company.getSuppliers().size());
    }
  }
}
