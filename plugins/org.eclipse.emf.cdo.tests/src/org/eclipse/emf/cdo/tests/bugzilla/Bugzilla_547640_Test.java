/*
 * Copyright (c) 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.ICommitConflictResolver;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.session.CDOTransactionContainerImpl.TransactionCreator;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Bug 547640 - Support server-side commit conflict resolution.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "server-side merging")
@CleanRepositoriesAfter(reason = "server-side merging")
public class Bugzilla_547640_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testCommitConflictResolver_OneCommit() throws Exception
  {
    run(new TestLogic()
    {
      private int expectedVersion;

      @Override
      public void modify2(CDOTransaction transaction2, Company company2) throws Exception
      {
        Category category2 = getModel1Factory().createCategory();
        company2.getCategories().add(category2);
        IOUtil.OUT().println("[2] Added " + category2);
      }

      @Override
      public void modifyAndCommit1(CDOTransaction transaction1, Company company1) throws Exception
      {
        Category category1 = getModel1Factory().createCategory();
        company1.getCategories().add(category1);
        IOUtil.OUT().println("[1] Added " + category1);

        transaction1.commit();
        IOUtil.OUT().println("[1] Committed");
        expectedVersion = CDOUtil.getCDOObject(company1).cdoRevision().getVersion() + 1;
      }

      @Override
      public void verify2(CDOTransaction transaction2, Company company2) throws Exception
      {
        CDORevision cdoRevision = CDOUtil.getCDOObject(company2).cdoRevision();
        assertEquals(expectedVersion, cdoRevision.getVersion());

        assertEquals(2, company2.getCategories().size());
      }
    });
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testCommitConflictResolver_TwoCommits() throws Exception
  {
    run(new TestLogic()
    {
      private int expectedVersion;

      @Override
      public void modify2(CDOTransaction transaction2, Company company2) throws Exception
      {
        company2.getCategories().add(getModel1Factory().createCategory());
      }

      @Override
      public void modifyAndCommit1(CDOTransaction transaction1, Company company1) throws Exception
      {
        company1.getCategories().add(getModel1Factory().createCategory());
        transaction1.commit();
        company1.getCategories().add(getModel1Factory().createCategory());
        transaction1.commit();
        expectedVersion = CDOUtil.getCDOObject(company1).cdoRevision().getVersion() + 1;
      }

      @Override
      public void verify2(CDOTransaction transaction2, Company company2) throws Exception
      {
        CDORevision cdoRevision = CDOUtil.getCDOObject(company2).cdoRevision();
        assertEquals(expectedVersion, cdoRevision.getVersion());
      }
    });
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    getTestProperties().put(RepositoryConfig.PROP_TEST_COMMIT_CONFLICT_RESOLVER, new ICommitConflictResolver.Merging());
    getTestProperties().put(RepositoryConfig.PROP_TEST_SESSION_MANAGER, new SuspendableSessionManager());
  }

  @Override
  protected void doTearDown() throws Exception
  {
    TransactionCreator.reset();
    super.doTearDown();
  }

  protected SuspendableSessionManager getSessionManager()
  {
    return (SuspendableSessionManager)getTestProperties().get(RepositoryConfig.PROP_TEST_SESSION_MANAGER);
  }

  private void run(TestLogic testLogic) throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    Company company1 = getModel1Factory().createCompany();
    transaction1.createResource(getResourcePath("resource")).getContents().add(company1);
    transaction1.commit();

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    TransactionCreator.set(new TransactionCreator()
    {
      @Override
      public InternalCDOTransaction createTransaction(CDOSession session, CDOBranch branch)
      {
        return new CDOTransactionImpl(session, branch)
        {
          @Override
          protected void waitForBaseline(long previousTimeStamp)
          {
            getSessionManager().resume();
            super.waitForBaseline(previousTimeStamp);
          }
        };
      }
    });

    CDOTransaction transaction2 = session2.openTransaction();
    TransactionCreator.reset();

    Company company2 = (Company)transaction2.getResource(getResourcePath("resource")).getContents().get(0);

    // Let client2 modify the model (but not commit, yet).
    testLogic.modify2(transaction2, company2);

    // Let client1 modify the model and commit (but hold back commit notifications to client2).
    getSessionManager().suspend();
    testLogic.modifyAndCommit1(transaction1, company1);

    // Let client2 commit and verify the result.
    transaction2.commit();
    testLogic.verify2(transaction2, company2);
  }

  /**
   * @author Eike Stepper
   */
  private static class SuspendableSessionManager extends SessionManager
  {
    private List<CommitNotificationInfo> queue;

    public synchronized void suspend()
    {
      IOUtil.OUT().println("[2] Suspending commit notifications");
      queue = new ArrayList<>();
    }

    public synchronized void resume()
    {
      IOUtil.OUT().println("[2] Resuming commit notifications");

      try
      {
        for (CommitNotificationInfo info : queue)
        {
          super.sendCommitNotification(info);
        }
      }
      finally
      {
        queue = null;
      }
    }

    @Override
    public synchronized void sendCommitNotification(CommitNotificationInfo info)
    {
      if (queue != null)
      {
        IOUtil.OUT().println("[2] Queuing commit notification: " + info);
        queue.add(info);
      }
      else
      {
        IOUtil.OUT().println("[2] Sending commit notification: " + info);
        super.sendCommitNotification(info);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface TestLogic
  {
    public void modify2(CDOTransaction transaction2, Company company2) throws Exception;

    public void modifyAndCommit1(CDOTransaction transaction1, Company company1) throws Exception;

    public void verify2(CDOTransaction transaction2, Company company2) throws Exception;
  }
}
