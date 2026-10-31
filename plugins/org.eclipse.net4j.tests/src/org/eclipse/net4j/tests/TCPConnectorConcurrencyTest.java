/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.internal.tcp.TCPClientConnector;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.tcp.ITCPPassiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests TCP connector behavior when a read callback overlaps connector cleanup.
 *
 * @author Eike Stepper
 */
public class TCPConnectorConcurrencyTest extends AbstractOMTest
{
  public void testRegistrationDuringSelectorStartupIsNotLost() throws Exception
  {
    CountDownLatch selectorOpening = new CountDownLatch(1);
    CountDownLatch continueOpening = new CountDownLatch(1);
    CountDownLatch registrationHandled = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    TestTCPSelector selector = new TestTCPSelector(selectorOpening, continueOpening);
    ServerSocketChannel channel = ServerSocketChannel.open();
    Thread activator = worker("tcp-selector-activator", failure, () -> LifecycleUtil.activate(selector)); //$NON-NLS-1$

    try
    {
      activator.start();
      assertTrue("Selector did not enter openSelector()", selectorOpening.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      selector.orderRegistration(channel, new ITCPPassiveSelectorListener()
      {
        @Override
        public void handleRegistration(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
        {
          registrationHandled.countDown();
        }

        @Override
        public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
        {
        }
      });

      continueOpening.countDown();
      assertTrue("Queued registration was not processed after selector startup", registrationHandled.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      join(activator);
      assertNull(failure.get());
    }
    finally
    {
      continueOpening.countDown();
      if (activator.isAlive())
      {
        join(activator);
      }

      LifecycleUtil.deactivate(selector);
      channel.close();
    }
  }

  public void testCleanupWaitsForInFlightReadAndPreventsNewReadAfterDeactivation() throws Exception
  {
    TestTCPClientConnector connector = new TestTCPClientConnector();
    connector.setHost("localhost");
    connector.setPort(1);
    connector.setSelector(createSelector());
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));

    CountDownLatch readStarted = new CountDownLatch(1);
    CountDownLatch finishRead = new CountDownLatch(1);
    AtomicInteger readCalls = new AtomicInteger();
    AtomicInteger releaseCalls = new AtomicInteger();
    IBuffer inputBuffer = createInputBuffer(readStarted, finishRead, readCalls, releaseCalls);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    ExecutorService receiveExecutor = Executors.newSingleThreadExecutor();
    Thread reader = null;
    Thread deactivator = null;

    try
    {
      connector.getConfig().setReceiveExecutor(receiveExecutor);
      LifecycleUtil.activate(connector);
      connector.setInputBuffer(inputBuffer);

      reader = worker("tcp-input-reader", failure, () -> connector.handleRead(connector.getSelector(), connector.getSocketChannel())); //$NON-NLS-1$
      reader.start();
      assertTrue("Read did not enter the buffer", readStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      CountDownLatch deactivationStarted = new CountDownLatch(1);
      deactivator = worker("tcp-input-deactivator", failure, () -> { //$NON-NLS-1$
        deactivationStarted.countDown();
        connector.deactivateAsyncForTest();
      });
      deactivator.start();
      assertTrue("Deactivation did not start", deactivationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("Cleanup did not wait for the in-flight buffer read", awaitCleanupBlocked(deactivator)); //$NON-NLS-1$
      assertEquals(0, releaseCalls.get());

      finishRead.countDown();
      join(reader);
      join(deactivator);

      assertNull(failure.get());
      assertEquals(1, readCalls.get());
      assertEquals(1, releaseCalls.get());

      connector.handleRead(connector.getSelector(), connector.getSocketChannel());
      assertEquals("A deactivated connector must not acquire another input buffer", 1, readCalls.get()); //$NON-NLS-1$
    }
    finally
    {
      finishRead.countDown();
      LifecycleUtil.deactivate(connector);
      if (reader != null)
      {
        join(reader);
      }

      if (deactivator != null)
      {
        join(deactivator);
      }

      receiveExecutor.shutdownNow();
    }
  }

  private static ITCPSelector createSelector()
  {
    return (ITCPSelector)Proxy.newProxyInstance(ITCPSelector.class.getClassLoader(), new Class<?>[] { ITCPSelector.class }, (proxy, method, args) -> {
      if (method.getName().equals("toString")) //$NON-NLS-1$
      {
        return TCPConnectorConcurrencyTest.class.getSimpleName();
      }

      return null;
    });
  }

  private static IBuffer createInputBuffer(CountDownLatch readStarted, CountDownLatch finishRead, AtomicInteger readCalls, AtomicInteger releaseCalls)
  {
    return (IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, (proxy, method, args) -> {
      switch (method.getName())
      {
      case "startGetting": //$NON-NLS-1$
        readCalls.incrementAndGet();
        readStarted.countDown();
        awaitLatch(finishRead);
        return (ByteBuffer)null;

      case "release": //$NON-NLS-1$
        releaseCalls.incrementAndGet();
        return null;

      case "toString": //$NON-NLS-1$
        return "BlockingInputBuffer"; //$NON-NLS-1$

      default:
        throw new UnsupportedOperationException(method.toString());
      }
    });
  }

  private static Thread worker(String name, AtomicReference<Throwable> failure, Runnable runnable)
  {
    Thread thread = new Thread(() -> {
      try
      {
        runnable.run();
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, name);
    thread.setDaemon(true);
    return thread;
  }

  private static boolean awaitCleanupBlocked(Thread thread)
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (thread.isAlive() && System.nanoTime() < deadline)
    {
      if (thread.getState() == Thread.State.BLOCKED)
      {
        for (StackTraceElement frame : thread.getStackTrace())
        {
          if (frame.getClassName().equals("org.eclipse.net4j.internal.tcp.TCPConnector") && frame.getMethodName().equals("cleanUp")) //$NON-NLS-1$ //$NON-NLS-2$
          {
            return true;
          }
        }
      }

      if (thread.getState() == Thread.State.TERMINATED)
      {
        return false;
      }

      Thread.yield();
    }

    return false;
  }

  private static void awaitLatch(CountDownLatch latch)
  {
    try
    {
      if (!latch.await(5, TimeUnit.SECONDS))
      {
        throw new AssertionError("Timed out waiting for the read to continue"); //$NON-NLS-1$
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }

  private static void join(Thread thread)
  {
    try
    {
      thread.join(TimeUnit.SECONDS.toMillis(5));
      if (thread.isAlive())
      {
        thread.interrupt();
        throw new AssertionError("Worker did not terminate: " + thread.getName()); //$NON-NLS-1$
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }

  /**
   * Exposes the connector's normal asynchronous shutdown entry point to this test.
   *
   * @author Eike Stepper
   */
  private static final class TestTCPClientConnector extends TCPClientConnector
  {
    public void deactivateAsyncForTest()
    {
      deactivateAsync();
    }
  }

  /**
   * Holds selector creation open to exercise operations ordered during activation.
   *
   * @author Eike Stepper
   */
  private static final class TestTCPSelector extends TCPSelector
  {
    private final CountDownLatch selectorOpening;

    private final CountDownLatch continueOpening;

    public TestTCPSelector(CountDownLatch selectorOpening, CountDownLatch continueOpening)
    {
      this.selectorOpening = selectorOpening;
      this.continueOpening = continueOpening;
    }

    @Override
    protected Selector openSelector() throws IOException
    {
      selectorOpening.countDown();
      awaitLatch(continueOpening);
      return super.openSelector();
    }
  }
}
