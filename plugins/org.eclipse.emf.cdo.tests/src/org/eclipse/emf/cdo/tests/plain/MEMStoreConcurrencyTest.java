/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises synchronization boundaries that are specific to the in-memory store.
 *
 * @author Eike Stepper
 */
public class MEMStoreConcurrencyTest extends PlainTest
{
  public void testBranchOperations()
  {
    MEMStore store = new MEMStore();
    store.createBranch(42, new BranchInfo("before", 0, 10));

    store.renameBranch(42, "before", "after");

    assertEquals("after", store.loadBranch(42).getName());
  }

  public void testConcurrentBranchOperations() throws Exception
  {
    MEMStore store = new MEMStore();
    int branchCount = 100;
    CountDownLatch ready = new CountDownLatch(3);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch firstBranchesCreated = new CountDownLatch(2);
    CountDownLatch readerCompletedRead = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(3);
    AtomicInteger reads = new AtomicInteger();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Runnable branchWriter = () -> {
      ready.countDown();
      try
      {
        awaitLatch(start);
        for (int i = 0; i < branchCount; i++)
        {
          int branchID = i + 1;
          store.createBranch(branchID, new BranchInfo("before-" + branchID, 0, 10));

          if (i == 0)
          {
            firstBranchesCreated.countDown();
            awaitLatch(readerCompletedRead);
          }

          store.renameBranch(branchID, "before-" + branchID, "after-" + branchID);
        }
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        finished.countDown();
      }
    };

    Thread firstWriter = new Thread(branchWriter, "MEMStore first branch writer");
    Thread secondWriter = new Thread(branchWriter, "MEMStore second branch writer");
    Thread reader = new Thread(() -> {
      ready.countDown();
      try
      {
        awaitLatch(start);
        awaitLatch(firstBranchesCreated);
        BranchInfo firstBranch = store.loadBranch(1);
        if (firstBranch == null || !firstBranch.getName().equals("before-1"))
        {
          throw new AssertionError("Did not observe the branch between creation and rename");
        }

        reads.incrementAndGet();
        readerCompletedRead.countDown();
        while (finished.getCount() > 1)
        {
          int branchID = reads.getAndIncrement() % branchCount + 1;
          BranchInfo branchInfo = store.loadBranch(branchID);
          if (branchInfo != null && !branchInfo.getName().equals("before-" + branchID) && !branchInfo.getName().equals("after-" + branchID))
          {
            throw new AssertionError("Observed an invalid branch name: " + branchInfo.getName());
          }

          readerCompletedRead.countDown();
        }
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        readerCompletedRead.countDown();
        finished.countDown();
      }
    }, "MEMStore branch reader");

    firstWriter.start();
    secondWriter.start();
    reader.start();
    try
    {
      assertTrue("Branch workers did not become ready", ready.await(10, TimeUnit.SECONDS));
    }
    finally
    {
      start.countDown();
    }
    assertTrue("Branch operations did not finish", finished.await(30, TimeUnit.SECONDS));
    firstWriter.join();
    secondWriter.join();
    reader.join();

    if (failure.get() != null)
    {
      throw new AssertionError("Concurrent branch operation failed", failure.get());
    }

    assertTrue("Branch reads were not performed", reads.get() > 0);
    for (int i = 0; i < branchCount; i++)
    {
      int branchID = i + 1;
      assertEquals("after-" + branchID, store.loadBranch(branchID).getName());
    }
  }

  public void testLobStreamingDoesNotHoldStoreLock() throws Exception
  {
    MEMStore store = new MEMStore();
    store.writeBlob(new byte[] { 1 }, 1, new ByteArrayInputStream(new byte[] { 1 }));

    CountDownLatch handlerEntered = new CountDownLatch(1);
    CountDownLatch releaseHandler = new CountDownLatch(1);
    CountDownLatch writerCompleted = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread reader = createLobReader(store, handlerEntered, releaseHandler, failure, "MEMStore LOB reader");
    Thread writer = new Thread(() -> {
      try
      {
        store.writeBlob(new byte[] { 2 }, 1, new ByteArrayInputStream(new byte[] { 2 }));
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        writerCompleted.countDown();
      }
    }, "MEMStore LOB writer");

    reader.start();
    assertTrue("LOB handler was not entered", handlerEntered.await(5, TimeUnit.SECONDS));
    writer.start();

    try
    {
      assertTrue("Store write waited for external LOB streaming", writerCompleted.await(5, TimeUnit.SECONDS));
    }
    finally
    {
      releaseHandler.countDown();
    }

    reader.join(5000);
    writer.join(5000);
    assertFalse("LOB reader did not finish", reader.isAlive());
    assertFalse("LOB writer did not finish", writer.isAlive());
    if (failure.get() != null)
    {
      throw new AssertionError("Concurrent LOB operation failed", failure.get());
    }
  }

  private Thread createLobReader(MEMStore store, CountDownLatch handlersEntered, CountDownLatch releaseHandlers, AtomicReference<Throwable> failure,
      String name)
  {
    return new Thread(() -> {
      try
      {
        store.handleLobs(0, 0, new CDOLobHandler()
        {
          @Override
          public OutputStream handleBlob(byte[] id, long size)
          {
            handlersEntered.countDown();
            try
            {
              if (!releaseHandlers.await(5, TimeUnit.SECONDS))
              {
                throw new AssertionError("Timed out waiting to release LOB handler");
              }
            }
            catch (InterruptedException ex)
            {
              Thread.currentThread().interrupt();
              throw new AssertionError(ex);
            }

            return null;
          }

          @Override
          public Writer handleClob(byte[] id, long size)
          {
            return null;
          }
        });
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, name);
  }

  private void awaitLatch(CountDownLatch latch) throws InterruptedException
  {
    if (!latch.await(10, TimeUnit.SECONDS))
    {
      throw new AssertionError("Timed out waiting for concurrent branch start");
    }
  }
}
