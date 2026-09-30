/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.issues;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.LockingManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/**
 * Verifies that the server keeps a commit's visible lock changes atomic with respect to other lock operations.
 *
 * @author Eike Stepper
 */
public class Issue_000210_Test extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private final CountDownLatch atLockMutationBoundary = new CountDownLatch(1);

  private final CountDownLatch continueCommit = new CountDownLatch(1);

  private volatile boolean pauseAtLockMutationBoundary;

  private final AtomicInteger commitContextsCreated = new AtomicInteger();

  private Repository testRepository;

  private TestLockingManager testLockingManager;

  @Override
  protected void doSetUp() throws Exception
  {
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
    testRepository = new Repository.Default()
    {
      @Override
      public LockingManager createLockingManager()
      {
        testLockingManager = new TestLockingManager();
        return testLockingManager;
      }

      @Override
      public InternalCommitContext createCommitContext(InternalTransaction transaction)
      {
        commitContextsCreated.incrementAndGet();
        return new TransactionCommitContext(transaction)
        {
          @Override
          protected void autoReleaseExplicitLocks(CDOLockOwner lockOwner) throws InterruptedException
          {
            if (pauseAtLockMutationBoundary)
            {
              atLockMutationBoundary.countDown();
              if (!continueCommit.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
              {
                throw new AssertionError("Timed out waiting to continue the commit");
              }
            }

            super.autoReleaseExplicitLocks(lockOwner);
          }
        };
      }
    };

    testRepository.setProperties(getRepositoryProperties());
    testRepository.setName(REPOSITORY_NAME);

    Map<String, Object> testProperties = getTestProperties();
    testProperties.put(RepositoryConfig.PROP_TEST_REPOSITORY, testRepository);
  }

  @CleanRepositoriesBefore(reason = "Isolated repository needed")
  @CleanRepositoriesAfter(reason = "Isolated repository needed")
  public void testCommitLockChangesAreAtomic() throws Exception
  {
    CDOTransaction transaction = openSession(REPOSITORY_NAME).openTransaction();
    assertSame("Session is not connected to the instrumented repository", testRepository, serverTransaction(transaction).getRepository());
    assertNotNull("Instrumented LockingManager was not created", testLockingManager);

    CDOResource resource = transaction.createResource(getResourcePath("/atomicity"));
    Company existingCompany = getModel1Factory().createCompany();
    resource.getContents().add(existingCompany);
    transaction.commit();

    CDOObject existingObject = CDOUtil.getCDOObject(existingCompany);
    existingObject.cdoWriteLock().lock();
    existingCompany.setName("dirty");

    Category newCategory = getModel1Factory().createCategory();
    existingCompany.getCategories().add(newCategory);
    CDOObject newObject = CDOUtil.getCDOObject(newCategory);
    newObject.cdoWriteLock().lock();
    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.options().addAutoReleaseLocksExemptions(true, newCategory);
    int commitContextCount = commitContextsCreated.get();

    AtomicReference<Throwable> commitFailure = new AtomicReference<>();
    Thread commitThread = new Thread(() -> {
      try
      {
        transaction.commit();
      }
      catch (Throwable ex)
      {
        commitFailure.set(ex);
      }
    }, "LockingManagerCommitAtomicityTest-Commit");

    pauseAtLockMutationBoundary = true;
    commitThread.start();

    boolean reachedBoundary = false;
    boolean acquiredWriteAccessAtBoundary = false;
    try
    {
      reachedBoundary = atLockMutationBoundary.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      if (reachedBoundary)
      {
        acquiredWriteAccessAtBoundary = testLockingManager.tryWriteAccess();
      }
    }
    finally
    {
      pauseAtLockMutationBoundary = false;
      continueCommit.countDown();
      commitThread.join(DEFAULT_TIMEOUT);
    }

    assertFalse("Commit thread did not finish", commitThread.isAlive());
    if (commitFailure.get() != null)
    {
      throw new AssertionError("Commit failed", commitFailure.get());
    }

    assertTrue("Repository.createCommitContext() was not used for the target commit", commitContextsCreated.get() > commitContextCount);
    assertTrue("Commit did not reach the boundary between lock phases", reachedBoundary);
    assertFalse("Another thread acquired lock-manager write access inside the commit lock transition", acquiredWriteAccessAtBoundary);

    assertFalse("The existing explicit lock was not auto-released", existingObject.cdoWriteLock().isLocked());
    assertTrue("The lock on the committed new object was not transferred", newObject.cdoWriteLock().isLocked());
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestLockingManager extends LockingManager
  {
    public TestLockingManager()
    {
    }

    public boolean tryWriteAccess()
    {
      Lock lock = rwAccess.getLock().writeLock();
      if (!lock.tryLock())
      {
        return false;
      }

      lock.unlock();
      return true;
    }
  }
}
