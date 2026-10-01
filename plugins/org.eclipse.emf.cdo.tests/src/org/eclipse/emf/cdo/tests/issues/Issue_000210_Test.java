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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.LockingManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange;
import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange.DeltaHandler;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

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

  private final AtomicBoolean pauseNextDurablePersistence = new AtomicBoolean();

  private final AtomicBoolean failNextDurablePersistence = new AtomicBoolean();

  private volatile boolean recordOnlyDurablePersistence;

  private volatile int persistedChangesBeforeFailure;

  private final List<Map<CDOID, LockGrade>> durableSnapshots = Collections.synchronizedList(new ArrayList<>());

  private volatile CountDownLatch durablePersistenceStarted = new CountDownLatch(0);

  private volatile CountDownLatch continueDurablePersistence = new CountDownLatch(0);

  private volatile Object observedChangeKey;

  private volatile CountDownLatch observedChange = new CountDownLatch(0);

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
        return new TransactionCommitContext(transaction);
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

  public void testDurableLockPersistenceRunsOutsideWriteAccess() throws Exception
  {
    skipStoreWithoutDurableLocking();

    CDOTransaction transaction = openSession(REPOSITORY_NAME).openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/durable-locking"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();
    transaction.enableDurableLocking();

    CDOUtil.getCDOObject(company).cdoWriteLock().lock();

    assertEquals(1, testLockingManager.durablePersistenceCount);
    assertTrue("Durable store I/O ran while another thread could not acquire the write access", testLockingManager.writeAccessAvailableDuringPersistence);
  }

  @CleanRepositoriesBefore(reason = "Isolated repository needed")
  @CleanRepositoriesAfter(reason = "Isolated repository needed")
  public void testDurableChangesPersistInLinearizationOrder() throws Exception
  {
    skipStoreWithoutDurableLocking();

    CDOTransaction transaction = openSession(REPOSITORY_NAME).openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/durable-order"));
    Company first = getModel1Factory().createCompany();
    Company second = getModel1Factory().createCompany();
    resource.getContents().add(first);
    resource.getContents().add(second);
    transaction.commit();
    transaction.enableDurableLocking();

    IView view = serverTransaction(transaction);
    Object firstKey = testLockingManager.getLockKey(CDOUtil.getCDOObject(first).cdoID());
    Object secondKey = testLockingManager.getLockKey(CDOUtil.getCDOObject(second).cdoID());
    durableSnapshots.clear();
    recordOnlyDurablePersistence = true;
    durablePersistenceStarted = new CountDownLatch(1);
    continueDurablePersistence = new CountDownLatch(1);
    pauseNextDurablePersistence.set(true);
    observedChangeKey = secondKey;
    observedChange = new CountDownLatch(1);
    AtomicReference<Throwable> firstFailure = new AtomicReference<>();
    AtomicReference<Throwable> secondFailure = new AtomicReference<>();
    Thread firstChange = durableChangeThread(view, firstKey, firstFailure, "DurableChange-First");
    Thread secondChange = durableChangeThread(view, secondKey, secondFailure, "DurableChange-Second");

    firstChange.start();
    assertTrue("First durable persistence did not pause", durablePersistenceStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    secondChange.start();
    try
    {
      assertTrue("Second change did not reach the lock manager", observedChange.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertNull("Second durable mutation became visible before the first area write completed", testLockingManager.getLockState(secondKey));
    }
    finally
    {
      continueDurablePersistence.countDown();
    }

    firstChange.join(DEFAULT_TIMEOUT);
    secondChange.join(DEFAULT_TIMEOUT);
    assertFalse("First durable change did not finish", firstChange.isAlive());
    assertFalse("Second durable change did not finish", secondChange.isAlive());
    assertNull(firstFailure.get());
    assertNull(secondFailure.get());
    assertEquals("Durable area snapshots were persisted in a different order from their mutations", 2, durableSnapshots.size());
    assertTrue("First snapshot omitted its lock", durableSnapshots.get(0).containsKey(CDOUtil.getCDOObject(first).cdoID()));
    assertFalse("First snapshot included a later mutation", durableSnapshots.get(0).containsKey(CDOUtil.getCDOObject(second).cdoID()));
    assertTrue("Second snapshot omitted the first lock", durableSnapshots.get(1).containsKey(CDOUtil.getCDOObject(first).cdoID()));
    assertTrue("Second snapshot omitted its own lock", durableSnapshots.get(1).containsKey(CDOUtil.getCDOObject(second).cdoID()));
  }

  @CleanRepositoriesBefore(reason = "Isolated repository needed")
  @CleanRepositoriesAfter(reason = "Isolated repository needed")
  public void testDurablePersistenceFailureReleasesAreaSequence() throws Exception
  {
    skipStoreWithoutDurableLocking();

    CDOTransaction transaction = openSession(REPOSITORY_NAME).openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/durable-failure"));
    Company company = getModel1Factory().createCompany();
    Company otherCompany = getModel1Factory().createCompany();
    resource.getContents().add(company);
    resource.getContents().add(otherCompany);
    transaction.commit();
    transaction.enableDurableLocking();
    IView view = serverTransaction(transaction);
    Object firstKey = testLockingManager.getLockKey(CDOUtil.getCDOObject(company).cdoID());
    Object secondKey = testLockingManager.getLockKey(CDOUtil.getCDOObject(otherCompany).cdoID());
    recordOnlyDurablePersistence = true;
    testLockingManager.changeLocks(view,
        Collections.singletonList(LockChange.lock(Collections.singleton(firstKey), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT)), false, true, null, null);
    failNextDurablePersistence.set(true);

    try
    {
      testLockingManager.changeLocks(view, List.of( //
          LockChange.lock(Collections.singleton(secondKey), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT),
          LockChange.unlock(Collections.singleton(firstKey), null, IRWOLockManager.ALL_LOCKS)), false, true, null, null);
      fail("Injected durable persistence failure was not reported");
    }
    catch (IllegalStateException expected)
    {
      assertNull("Failed mixed batch unexpectedly restored the released lock", testLockingManager.getLockState(firstKey));
      assertNull("Failed new lock was not compensated in memory", testLockingManager.getLockState(secondKey));
      assertTrue("Initial durable snapshot omitted the lock", durableSnapshots.get(0).containsKey(CDOUtil.getCDOObject(company).cdoID()));
      assertFalse("Failed mixed batch snapshot incorrectly retained its released lock",
          durableSnapshots.get(1).containsKey(CDOUtil.getCDOObject(company).cdoID()));
      assertTrue("Failed mixed batch snapshot omitted its requested lock", durableSnapshots.get(1).containsKey(CDOUtil.getCDOObject(otherCompany).cdoID()));
    }

    testLockingManager.changeLocks(view,
        Collections.singletonList(LockChange.lock(Collections.singleton(secondKey), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT)), false, true, null, null);
    assertTrue("Persistence sequencing remained blocked after failure", testLockingManager.getLockState(secondKey).hasLock(LockType.WRITE, view, false));
  }

  @CleanRepositoriesBefore(reason = "Isolated repository needed")
  @CleanRepositoriesAfter(reason = "Isolated repository needed")
  public void testDurablePersistenceFailurePreservesPersistedPrefix() throws Exception
  {
    skipStoreWithoutDurableLocking();

    CDOTransaction transaction = openSession(REPOSITORY_NAME).openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/durable-prefix"));
    Company first = getModel1Factory().createCompany();
    Company second = getModel1Factory().createCompany();
    resource.getContents().add(first);
    resource.getContents().add(second);
    transaction.commit();
    transaction.enableDurableLocking();

    IView view = serverTransaction(transaction);
    Object firstKey = testLockingManager.getLockKey(CDOUtil.getCDOObject(first).cdoID());
    Object secondKey = testLockingManager.getLockKey(CDOUtil.getCDOObject(second).cdoID());
    recordOnlyDurablePersistence = true;
    testLockingManager.changeLocks(view,
        Collections.singletonList(LockChange.lock(Collections.singleton(firstKey), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT)), false, true, null, null);
    persistedChangesBeforeFailure = 1;
    failNextDurablePersistence.set(true);

    try
    {
      testLockingManager.changeLocks(view, List.of( //
          LockChange.lock(Collections.singleton(secondKey), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT),
          LockChange.unlock(Collections.singleton(firstKey), null, IRWOLockManager.ALL_LOCKS)), false, true, null, null);
      fail("Injected later durable operation failure was not reported");
    }
    catch (IllegalStateException expected)
    {
      assertNull("Earlier successful unlock was unexpectedly restored", testLockingManager.getLockState(firstKey));
      assertTrue("Successfully persisted lock prefix was incorrectly compensated",
          testLockingManager.getLockState(secondKey).hasLock(LockType.WRITE, view, false));
    }

    testLockingManager.changeLocks(view, Collections.singletonList(LockChange.unlock(Collections.singleton(secondKey), null, IRWOLockManager.ALL_LOCKS)), false,
        true, null, null);
    assertNull("Persistence sequencing remained blocked after the later-operation failure", testLockingManager.getLockState(secondKey));
  }

  private Thread durableChangeThread(IView view, Object key, AtomicReference<Throwable> failure, String name)
  {
    return new Thread(() -> {
      try
      {
        testLockingManager.changeLocks(view,
            Collections.singletonList(LockChange.lock(Collections.singleton(key), LockType.WRITE, 1, IRWOLockManager.NO_TIMEOUT)), false, true, null, null);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, name);
  }

  /**
   * @author Eike Stepper
   */
  private final class TestLockingManager extends LockingManager
  {
    private int durablePersistenceCount;

    private boolean writeAccessAvailableDuringPersistence;

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

    @Override
    protected void persistDurableChanges(IView view, String durableLockingID, List<? extends LockChange<Object>> changes, Map<CDOID, LockGrade> durableSnapshot,
        AtomicInteger persistedChangeCount)
    {
      ++durablePersistenceCount;
      durableSnapshots.add(new HashMap<>(durableSnapshot));
      if (pauseNextDurablePersistence.compareAndSet(true, false))
      {
        durablePersistenceStarted.countDown();
        try
        {
          if (!continueDurablePersistence.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
          {
            throw new AssertionError("Timed out pausing durable persistence");
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new AssertionError("Interrupted while pausing durable persistence", ex);
        }
      }

      if (failNextDurablePersistence.compareAndSet(true, false))
      {
        persistedChangeCount.set(persistedChangesBeforeFailure);
        throw new IllegalStateException("Injected durable persistence failure");
      }

      if (recordOnlyDurablePersistence)
      {
        persistedChangeCount.set(changes.size());
        return;
      }

      CountDownLatch checked = new CountDownLatch(1);
      AtomicReference<Boolean> available = new AtomicReference<>();
      Thread checker = new Thread(() -> {
        available.set(tryWriteAccess());
        checked.countDown();
      }, "DurableLockWriteAccessCheck");
      checker.start();
      try
      {
        if (!checked.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
        {
          throw new AssertionError("Timed out checking lock-manager write access");
        }

        checker.join(DEFAULT_TIMEOUT);
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new AssertionError("Interrupted checking lock-manager write access", ex);
      }

      writeAccessAvailableDuringPersistence = Boolean.TRUE.equals(available.get());
      super.persistDurableChanges(view, durableLockingID, changes, durableSnapshot, persistedChangeCount);
    }

    @Override
    public InternalLockManager.LockChangeOperationResult changeLocksWithReservation(IView view, List<? extends LockChange<Object>> changes, boolean recursive,
        boolean explicit, DeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler)
        throws InterruptedException, TimeoutRuntimeException
    {
      boolean observed = observedChangeKey != null
          && changes.stream().anyMatch(change -> change.getObjects() != null && change.getObjects().contains(observedChangeKey));
      if (observed)
      {
        observedChange.countDown();
      }

      boolean locks = changes.stream().anyMatch(LockChange::isLock);
      boolean unlocks = changes.stream().anyMatch(LockChange::isUnlock);
      if (!locks || !unlocks || !pauseAtLockMutationBoundary)
      {
        return super.changeLocksWithReservation(view, changes, recursive, explicit, deltaHandler, stateHandler);
      }

      Consumer<LockState<Object, IView>> pausingHandler = state -> {
        if (pauseAtLockMutationBoundary)
        {
          pauseAtLockMutationBoundary = false;
          atLockMutationBoundary.countDown();
          try
          {
            if (!continueCommit.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
            {
              throw new AssertionError("Timed out waiting to continue the commit");
            }
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting to continue the commit", ex);
          }
        }

        stateHandler.accept(state);
      };

      return super.changeLocksWithReservation(view, changes, recursive, explicit, deltaHandler, pausingHandler);
    }
  }
}
