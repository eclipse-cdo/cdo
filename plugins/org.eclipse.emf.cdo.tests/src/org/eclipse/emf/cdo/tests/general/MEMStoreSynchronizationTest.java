/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.internal.server.mem.MEMStoreAccessor;
import org.eclipse.emf.cdo.internal.server.mem.MEMStoreChunkReader;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Verifies monitor synchronization across MEMStore reads, writes, and chunk loading.
 *
 * @author Eike Stepper
 */
@Requires({ "MEM", ISessionConfig.CAPABILITY_NET4J_JVM, IModelConfig.CAPABILITY_NATIVE })
public class MEMStoreSynchronizationTest extends AbstractCDOTest
{
  private static final int COMMIT_COUNT = 20;

  public void testConcurrentRevisionAccessAndCommitAtomicity() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/memstore-sync"));
    Company first = getModel1Factory().createCompany();
    Company second = getModel1Factory().createCompany();
    first.setName("first-0");
    second.setName("second-0");
    resource.getContents().add(first);
    resource.getContents().add(second);
    transaction.commit();

    CDOID firstID = CDOUtil.getCDOObject(first).cdoID();
    CDOID secondID = CDOUtil.getCDOObject(second).cdoID();
    MEMStore store = (MEMStore)getRepository().getStore();
    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch readerReady = new CountDownLatch(1);
    CountDownLatch firstCommitDone = new CountDownLatch(1);
    CountDownLatch readerObservedFirstCommit = new CountDownLatch(1);
    CountDownLatch writerDone = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(2);
    AtomicInteger readCount = new AtomicInteger();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread reader = new Thread(() -> {
      ready.countDown();
      try
      {
        awaitLatch(start);
        do
        {
          synchronized (store)
          {
            InternalCDORevision firstRevision = findCurrentRevision(store, firstID);
            InternalCDORevision secondRevision = findCurrentRevision(store, secondID);
            if (firstRevision == null || secondRevision == null)
            {
              throw new AssertionError("A committed revision was not visible");
            }

            if (firstRevision.getVersion() != secondRevision.getVersion())
            {
              throw new AssertionError("A reader observed only part of a commit: " + firstRevision.getVersion() + " != " + secondRevision.getVersion());
            }

          }

          int reads = readCount.incrementAndGet();
          if (reads == 1)
          {
            readerReady.countDown();
            awaitLatch(firstCommitDone);
          }
          else if (reads == 2)
          {
            readerObservedFirstCommit.countDown();
          }
        } while (writerDone.getCount() != 0);
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "MEMStore revision reader");

    Thread writer = new Thread(() -> {
      ready.countDown();
      try
      {
        awaitLatch(start);
        awaitLatch(readerReady);
        for (int i = 1; i <= COMMIT_COUNT; i++)
        {
          first.setName("first-" + i);
          second.setName("second-" + i);
          transaction.commit();

          if (i == 1)
          {
            firstCommitDone.countDown();
            awaitLatch(readerObservedFirstCommit);
          }
        }
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        firstCommitDone.countDown();
        readerObservedFirstCommit.countDown();
        writerDone.countDown();
        finished.countDown();
      }
    }, "MEMStore commit writer");

    reader.start();
    writer.start();
    assertTrue("Reader and writer did not become ready", ready.await(10, TimeUnit.SECONDS));
    start.countDown();
    assertTrue("Reader and writer did not finish", finished.await(60, TimeUnit.SECONDS));
    reader.join();
    writer.join();

    if (failure.get() != null)
    {
      throw new AssertionError("Concurrent MEMStore revision access failed", failure.get());
    }

    assertTrue("Reader did not verify the first committed revision pair", readCount.get() > 1);
    assertEquals(1 + COMMIT_COUNT, findCurrentRevision(store, firstID).getVersion());
    assertEquals(1 + COMMIT_COUNT, findCurrentRevision(store, secondID).getVersion());
    session.close();
  }

  public void testChunkReadConcurrentWithListCommit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/memstore-chunks"));
    Company company = getModel1Factory().createCompany();
    List<CDOID> oldCategoryIDs = new ArrayList<>();
    for (int i = 0; i < 8; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("old-category-" + i);
      company.getCategories().add(category);
    }

    resource.getContents().add(company);
    transaction.commit();

    MEMStore store = (MEMStore)getRepository().getStore();
    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    InternalCDORevision revision = findCurrentRevision(store, companyID);
    assertNotNull("Company revision was not stored", revision);
    for (Object categoryID : revision.data().getListOrNull(getModel1Package().getCompany_Categories()))
    {
      oldCategoryIDs.add((CDOID)categoryID);
    }

    MEMStoreAccessor accessor = new MEMStoreAccessor(store, (ISession)null);
    EStructuralFeature categoriesFeature = getModel1Package().getCompany_Categories();
    MEMStoreChunkReader reader = accessor.createChunkReader(revision, categoriesFeature);
    reader.addRangedChunk(0, 4);
    reader.addRangedChunk(4, 8);

    CDOTransaction writerTransaction = session.openTransaction();
    CDOResource writerResource = writerTransaction.getResource(getResourcePath("/memstore-chunks"));
    Company writerCompany = (Company)writerResource.getContents().get(0);
    writerCompany.getCategories().clear();
    List<Category> newCategories = new ArrayList<>();
    for (int i = 0; i < 8; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("new-category-" + i);
      writerCompany.getCategories().add(category);
      newCategories.add(category);
    }

    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(2);
    AtomicReference<List<IStoreChunkReader.Chunk>> chunks = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread chunkReader = new Thread(() -> {
      ready.countDown();
      try
      {
        awaitLatch(start);
        chunks.set(reader.executeRead());
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "MEMStore chunk reader");

    Thread commitWriter = new Thread(() -> {
      ready.countDown();
      try
      {
        awaitLatch(start);
        writerTransaction.commit();
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "MEMStore chunk commit writer");

    chunkReader.start();
    commitWriter.start();
    try
    {
      assertTrue("Chunk reader and commit writer did not become ready", ready.await(10, TimeUnit.SECONDS));
    }
    finally
    {
      start.countDown();
    }

    assertTrue("Chunk read and commit did not finish", finished.await(60, TimeUnit.SECONDS));
    chunkReader.join();
    commitWriter.join();

    if (failure.get() != null)
    {
      throw new AssertionError("Chunk read concurrent with a commit failed", failure.get());
    }

    assertNotNull(chunks.get());
    assertEquals(2, chunks.get().size());
    assertEquals(4, chunks.get().get(0).size());
    assertEquals(4, chunks.get().get(1).size());
    List<CDOID> actualCategoryIDs = new ArrayList<>();
    for (IStoreChunkReader.Chunk chunk : chunks.get())
    {
      for (int i = 0; i < chunk.size(); i++)
      {
        actualCategoryIDs.add((CDOID)chunk.get(i));
      }
    }

    assertEquals("Chunk read returned a mixed or reordered category list", oldCategoryIDs, actualCategoryIDs);
    List<CDOID> newCategoryIDs = new ArrayList<>();
    for (Category category : newCategories)
    {
      newCategoryIDs.add(CDOUtil.getCDOObject(category).cdoID());
    }

    assertEquals("Commit did not publish the replacement category list", newCategoryIDs,
        findCurrentRevision(store, companyID).data().getListOrNull(getModel1Package().getCompany_Categories()));
    session.close();
  }

  public void testIndependentRollbackSynchronizesOnStoreMonitor() throws Exception
  {
    CDOSession session = openSession();
    MEMStore store = (MEMStore)getRepository().getStore();
    RollbackTestAccessor accessor = new RollbackTestAccessor(store);
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread rollback = new Thread(() -> {
      started.countDown();
      try
      {
        accessor.invokeRollback();
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "MEMStore independent rollback");

    synchronized (store)
    {
      rollback.start();
      assertTrue("Rollback thread did not start", started.await(10, TimeUnit.SECONDS));
      awaitBlocked(rollback);
    }

    assertTrue("Rollback did not finish", finished.await(10, TimeUnit.SECONDS));
    rollback.join();
    if (failure.get() != null)
    {
      throw new AssertionError("Independent rollback failed", failure.get());
    }

    synchronized (store)
    {
      accessor.invokeRollback();
    }

    session.close();
  }

  private InternalCDORevision findCurrentRevision(MEMStore store, CDOID id)
  {
    for (InternalCDORevision revision : store.getCurrentRevisions())
    {
      if (id.equals(revision.getID()))
      {
        return revision;
      }
    }

    return null;
  }

  private void awaitLatch(CountDownLatch latch) throws InterruptedException
  {
    if (!latch.await(30, TimeUnit.SECONDS))
    {
      throw new AssertionError("Timed out waiting for a synchronization latch");
    }
  }

  private void awaitBlocked(Thread thread)
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (thread.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    assertEquals("Thread did not block on the store monitor", Thread.State.BLOCKED, thread.getState());
  }

  private static final class RollbackTestAccessor extends MEMStoreAccessor
  {
    RollbackTestAccessor(MEMStore store)
    {
      super(store, (ISession)null);
    }

    void invokeRollback()
    {
      doRollback(null);
    }
  }
}
