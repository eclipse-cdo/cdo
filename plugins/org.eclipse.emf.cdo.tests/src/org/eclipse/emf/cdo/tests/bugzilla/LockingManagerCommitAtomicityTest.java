/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.bugzilla;

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
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/**
 * Verifies that the server keeps a commit's visible lock changes atomic with respect to other lock operations.
 *
 * @author Eike Stepper
 */
public class LockingManagerCommitAtomicityTest extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private final CountDownLatch atLockMutationBoundary = new CountDownLatch(1);

  private final CountDownLatch continueCommit = new CountDownLatch(1);

  private volatile boolean pauseAtLockMutationBoundary;

  private TestLockingManager testLockingManager;

  @Override
  protected void doSetUp() throws Exception
  {
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
    Repository repository = new Repository.Default()
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

    repository.setProperties(getRepositoryProperties());
    repository.setName(REPOSITORY_NAME);

    Map<String, Object> testProperties = getTestProperties();
    testProperties.put(RepositoryConfig.PROP_TEST_REPOSITORY, repository);
  }

  public void testCommitLockChangesAreAtomic() throws Exception
  {
    CDOTransaction transaction = openSession(REPOSITORY_NAME).openTransaction();
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

    assertTrue("Commit did not reach the boundary between lock phases", reachedBoundary);
    assertFalse("Another thread acquired lock-manager write access inside the commit lock transition", acquiredWriteAccessAtBoundary);
    assertFalse("Commit thread did not finish", commitThread.isAlive());
    if (commitFailure.get() != null)
    {
      throw new AssertionError("Commit failed", commitFailure.get());
    }

    assertFalse("The existing explicit lock was not auto-released", existingObject.cdoWriteLock().isLocked());
    assertTrue("The lock on the committed new object was not transferred", newObject.cdoWriteLock().isLocked());
  }

  private static final class TestLockingManager extends LockingManager
  {
    boolean tryWriteAccess()
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
