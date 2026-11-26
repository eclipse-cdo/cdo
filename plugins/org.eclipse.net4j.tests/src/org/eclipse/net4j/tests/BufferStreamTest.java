/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.SignalProtocolDeactivatedException;
import org.eclipse.net4j.tests.data.HugeData;
import org.eclipse.net4j.util.io.IOTimeoutException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class BufferStreamTest extends AbstractOMTest
{
  private static final short CHANNEL_ID = 0;

  public void testRuntimeExceptionInjection() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    RuntimeException exception = new IllegalStateException("local cancellation"); //$NON-NLS-1$
    in.abort(exception);

    try
    {
      in.read();
      fail("Expected the injected exception"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertSame(exception, ex);
    }
    finally
    {
      in.close();
    }
  }

  public void testSignalProtocolDeactivatedExceptionInjection() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    SignalProtocolDeactivatedException exception = new SignalProtocolDeactivatedException();
    in.abort(exception);

    try
    {
      in.read();
      fail("Expected the injected exception"); //$NON-NLS-1$
    }
    catch (SignalProtocolDeactivatedException ex)
    {
      assertSame(exception, ex);
    }
    finally
    {
      in.close();
    }
  }

  public void testRemoteExceptionInjectionEnrichesLocalStackTrace() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    RemoteException exception = new RemoteException("remote failure", false); //$NON-NLS-1$
    in.abort(exception);

    try
    {
      in.read();
      fail("Expected the injected exception"); //$NON-NLS-1$
    }
    catch (RemoteException ex)
    {
      assertSame(exception, ex);
      assertNotNull(ex.getLocalStackTrace());
    }
    finally
    {
      in.close();
    }
  }

  @SuppressWarnings("deprecation")
  public void testRemoteExceptionSetterRemainsAvailable() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    RemoteException exception = new RemoteException("remote failure", false); //$NON-NLS-1$
    in.setException(exception);

    assertSame(exception, in.getException());
    try
    {
      in.read();
      fail("Expected the injected exception"); //$NON-NLS-1$
    }
    catch (RemoteException ex)
    {
      assertSame(exception, ex);
      assertNotNull(ex.getLocalStackTrace());
    }
    finally
    {
      in.close();
    }

    in.setException(null);
    assertNull(in.getException());
  }

  public void testAbortWakesReaderWaitingWithoutTimeout() throws Exception
  {
    WaitingBufferInputStream in = new WaitingBufferInputStream();
    RuntimeException cause = new IllegalStateException("aborted"); //$NON-NLS-1$
    AtomicReference<Throwable> failure = new AtomicReference<>();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = createReader(in, failure, new AtomicInteger(), started, finished);

    reader.start();
    assertTrue(started.await(5, TimeUnit.SECONDS));
    assertTrue("Reader did not enter buffer acquisition", in.waitingForBuffer.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.abort(cause);

    assertTrue("Reader did not wake after abort", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    assertSame(cause, failure.get());
    reader.join();
    in.close();
  }

  public void testAbortWakesReaderWaitingWithFiniteTimeout() throws Exception
  {
    WaitingBufferInputStream in = new WaitingBufferInputStream(30000);
    RuntimeException cause = new IllegalStateException("aborted"); //$NON-NLS-1$
    AtomicReference<Throwable> failure = new AtomicReference<>();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = createReader(in, failure, new AtomicInteger(), started, finished);

    reader.start();
    assertTrue(started.await(5, TimeUnit.SECONDS));
    assertTrue("Reader did not enter buffer acquisition", in.waitingForBuffer.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.abort(cause);

    assertTrue("Reader did not wake after abort", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    assertSame(cause, failure.get());
    reader.join();
    in.close();
  }

  public void testCloseWakesReaderWaitingWithoutTimeout() throws Exception
  {
    WaitingBufferInputStream in = new WaitingBufferInputStream();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicInteger result = new AtomicInteger();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = createReader(in, failure, result, started, finished);

    reader.start();
    assertTrue(started.await(5, TimeUnit.SECONDS));
    assertTrue("Reader did not enter buffer acquisition", in.waitingForBuffer.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.close();

    assertTrue("Reader did not wake after close", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    assertNull(failure.get());
    assertEquals(IOUtil.EOF, result.get());
    reader.join();
  }

  public void testFiniteTimeoutExpires() throws Exception
  {
    BufferInputStream in = new TimedBufferInputStream(200);
    try
    {
      in.read();
      fail("Expected a buffer read timeout"); //$NON-NLS-1$
    }
    catch (IOTimeoutException expected)
    {
      // The finite timeout remains active without periodic polling.
    }
    finally
    {
      in.close();
    }
  }

  public void testBulkReadAfterEOSReturnsEOF() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.flushWithEOS();
    out.close();

    assertEquals(IOUtil.EOF, in.read(new byte[1]));
    in.close();
  }

  public void testBulkReadAfterCloseReturnsEOFAndZeroLengthReadReturnsZero() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    in.close();

    assertEquals(IOUtil.EOF, in.read(new byte[1]));
    assertEquals(0, in.read(new byte[1], 0, 0));
  }

  public void testConcurrentCloseWakesWaitingBulkReaderWithEOF() throws Exception
  {
    WaitingBufferInputStream in = new WaitingBufferInputStream();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicInteger result = new AtomicInteger();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = new Thread(() -> {
      started.countDown();
      try
      {
        result.set(in.read(new byte[2]));
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "Waiting bulk reader"); //$NON-NLS-1$
    reader.setDaemon(true);
    reader.start();

    assertTrue(started.await(5, TimeUnit.SECONDS));
    assertTrue("Reader did not enter buffer acquisition", in.waitingForBuffer.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.close();

    assertTrue("Close did not wake the bulk reader", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    reader.join(5000);
    assertFalse(reader.isAlive());
    assertNull(failure.get());
    assertEquals(IOUtil.EOF, result.get());
  }

  public void testConcurrentCloseAfterPartialBulkReadReturnsBytesRead() throws Exception
  {
    ArrivalBufferInputStream in = new ArrivalBufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();

    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicReference<byte[]> result = new AtomicReference<>();
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = new Thread(() -> {
      try
      {
        byte[] bytes = new byte[2];
        int count = in.read(bytes);
        assertEquals(1, count);
        result.set(new byte[] { bytes[0] });
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "Partial bulk reader"); //$NON-NLS-1$
    reader.setDaemon(true);
    reader.start();

    assertTrue("Read did not request a second buffer", in.secondBufferRequested.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.close();

    assertTrue("Close did not finish the partial bulk read", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    reader.join(5000);
    assertFalse(reader.isAlive());
    assertNull(failure.get());
    assertArrayEquals(new byte[] { 0x45 }, result.get());
    out.close();
  }

  public void testRestartTimeoutExtendsWaitAndOrdinaryNotificationsDoNotFinishRead() throws Exception
  {
    RestartableBufferInputStream in = new RestartableBufferInputStream(4000);
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    AtomicInteger result = new AtomicInteger();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = new Thread(() -> {
      try
      {
        result.set(in.read());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "Timeout restart reader"); //$NON-NLS-1$
    reader.setDaemon(true);
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    try
    {
      reader.start();
      assertTrue("Read timeout was not started", in.timeoutStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      scheduler.schedule(in::restartTimeout, 1000, TimeUnit.MILLISECONDS);
      scheduler.schedule(() -> {
        try
        {
          out.write(0x45);
          out.flush();
        }
        catch (IOException ex)
        {
          failure.set(ex);
        }
      }, 4500, TimeUnit.MILLISECONDS);

      assertTrue("Reader did not receive the later buffer", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertNull(failure.get());
      assertEquals(0x45, result.get());
      reader.join();
    }
    finally
    {
      scheduler.shutdownNow();
      out.close();
      in.close();
      reader.join(1000);
    }
  }

  public void testExternalInterruptWakesReaderAndRestoresInterruptStatus() throws Exception
  {
    WaitingBufferInputStream in = new WaitingBufferInputStream();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicInteger interrupted = new AtomicInteger();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = new Thread(() -> {
      started.countDown();
      try
      {
        in.read();
      }
      catch (Throwable ex)
      {
        failure.set(ex);
        interrupted.set(Thread.currentThread().isInterrupted() ? 1 : 0);
      }
      finally
      {
        finished.countDown();
      }
    }, "Interrupted buffer reader"); //$NON-NLS-1$
    reader.setDaemon(true);

    reader.start();
    assertTrue(started.await(5, TimeUnit.SECONDS));
    assertTrue("Reader did not enter buffer acquisition", in.waitingForBuffer.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    reader.interrupt();

    assertTrue("Reader did not wake after interruption", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    assertTrue(failure.get() instanceof RuntimeException);
    assertEquals(1, interrupted.get());
    reader.join();
    in.close();
  }

  public void testAbortPrecedesQueuedBufferAndQueuedBufferIsReleasedOnClose() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();

    RuntimeException cause = new IllegalStateException("abort wins over queued data"); //$NON-NLS-1$
    in.abort(cause);
    try
    {
      in.read();
      fail("Expected the abort cause before queued data"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertSame(cause, ex);
    }
    finally
    {
      in.close();
      out.close();
    }
  }

  public void testConcurrentAbortAndBufferArrival() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    IBuffer buffer = bufferPool.provideBuffer();
    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    RuntimeException cause = new IllegalStateException("concurrent abort"); //$NON-NLS-1$
    Thread aborter = raceThread(ready, start, () -> in.abort(cause), failure);
    Thread producer = raceThread(ready, start, () -> in.handleBuffer(buffer), failure);

    aborter.start();
    producer.start();
    assertTrue(ready.await(5, TimeUnit.SECONDS));
    start.countDown();
    aborter.join(5000);
    producer.join(5000);

    assertFalse(aborter.isAlive());
    assertFalse(producer.isAlive());
    assertNull(failure.get());
    try
    {
      in.read();
      fail("Abort must take precedence over any concurrently queued buffer"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      assertSame(cause, ex);
    }
    finally
    {
      in.close();
    }
  }

  public void testConcurrentCloseAndBufferArrival() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    IBuffer buffer = bufferPool.provideBuffer();
    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread closer = raceThread(ready, start, () -> {
      try
      {
        in.close();
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }, failure);
    Thread producer = raceThread(ready, start, () -> in.handleBuffer(buffer), failure);

    closer.start();
    producer.start();
    assertTrue(ready.await(5, TimeUnit.SECONDS));
    start.countDown();
    closer.join(5000);
    producer.join(5000);

    assertFalse(closer.isAlive());
    assertFalse(producer.isAlive());
    assertNull(failure.get());
    assertEquals(IOUtil.EOF, in.read());
  }

  private static Thread raceThread(CountDownLatch ready, CountDownLatch start, Runnable action, AtomicReference<Throwable> failure)
  {
    Thread thread = new Thread(() -> {
      ready.countDown();
      try
      {
        start.await();
        action.run();
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, "Buffer stream race"); //$NON-NLS-1$
    thread.setDaemon(true);
    return thread;
  }

  public void testBufferArrivalAfterCloseIsDiscarded() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    in.close();

    out.write(0x45);
    out.flush();
    out.close();

    assertEquals(IOUtil.EOF, in.read());
  }

  public void testBuffersRemainFifo() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(1);
    out.flush();
    out.write(2);
    out.flush();

    assertEquals(1, in.read());
    assertEquals(2, in.read());

    out.close();
    in.close();
  }

  public void testBufferArrivalWakesAnOngoingBulkRead() throws Exception
  {
    ArrivalBufferInputStream in = new ArrivalBufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();

    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicReference<byte[]> result = new AtomicReference<>();
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = new Thread(() -> {
      try
      {
        byte[] bytes = new byte[2];
        assertEquals(2, in.read(bytes));
        result.set(bytes);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "Bulk reader receiving a later buffer"); //$NON-NLS-1$
    reader.setDaemon(true);
    reader.start();

    assertTrue("Read did not request a second buffer", in.secondBufferRequested.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    out.write(0x46);
    out.flush();
    out.close();

    assertTrue("Read did not continue after the buffer arrived", finished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    reader.join(5000);
    assertFalse(reader.isAlive());
    assertNull(failure.get());
    assertArrayEquals(new byte[] { 0x45, 0x46 }, result.get());
    in.close();
  }

  public void testCloseWaitsForSingleByteReadAndReleasesCurrentBufferOnce() throws Exception
  {
    ControlledBufferInputStream in = new ControlledBufferInputStream(1);
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();
    out.close();

    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicInteger result = new AtomicInteger();
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = createReader(in, failure, result, new CountDownLatch(0), finished);
    reader.start();
    assertTrue("Reader did not acquire its current buffer", in.bufferAcquired.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    Thread closer = new Thread(() -> {
      try
      {
        in.close();
      }
      catch (IOException ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, "Concurrent stream closer"); //$NON-NLS-1$
    closer.setDaemon(true);
    closer.start();
    assertTrue("Close did not start", in.closeStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.allowRead.countDown();

    assertTrue(finished.await(5, TimeUnit.SECONDS));
    reader.join(5000);
    closer.join(5000);
    assertFalse(reader.isAlive());
    assertFalse(closer.isAlive());
    assertNull(failure.get());
    assertEquals(0x45, result.get());
    assertEquals(1L, ((IBufferProvider.Introspection)bufferPool).getRetainedBuffers());
  }

  public void testCloseDuringBulkReadWaitsBetweenBuffers() throws Exception
  {
    ControlledBufferInputStream in = new ControlledBufferInputStream(2);
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();
    out.write(0x46);
    out.flush();
    out.close();

    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicReference<byte[]> result = new AtomicReference<>();
    CountDownLatch finished = new CountDownLatch(1);
    Thread reader = new Thread(() -> {
      try
      {
        byte[] bytes = new byte[2];
        assertEquals(2, in.read(bytes));
        result.set(bytes);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "Concurrent bulk reader"); //$NON-NLS-1$
    reader.setDaemon(true);
    reader.start();
    assertTrue("Reader did not acquire its second buffer", in.bufferAcquired.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    Thread closer = new Thread(() -> {
      try
      {
        in.close();
      }
      catch (IOException ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, "Concurrent bulk stream closer"); //$NON-NLS-1$
    closer.setDaemon(true);
    closer.start();
    assertTrue("Close did not start", in.closeStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    in.allowRead.countDown();

    assertTrue(finished.await(5, TimeUnit.SECONDS));
    reader.join(5000);
    closer.join(5000);
    assertFalse(reader.isAlive());
    assertFalse(closer.isAlive());
    assertNull(failure.get());
    assertArrayEquals(new byte[] { 0x45, 0x46 }, result.get());
    assertEquals(2L, ((IBufferProvider.Introspection)bufferPool).getRetainedBuffers());
  }

  public void testRepeatedCloseReleasesQueuedBuffersOnce() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();
    out.write(0x46);
    out.flush();
    out.close();

    in.close();
    in.close();
    in.close();

    assertEquals(2L, ((IBufferProvider.Introspection)bufferPool).getRetainedBuffers());
  }

  public void testConcurrentCloseCallsReleaseQueuedBuffersOnce() throws Exception
  {
    BufferInputStream in = new BufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.write(0x45);
    out.flush();
    out.write(0x46);
    out.flush();
    out.close();

    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread firstCloser = raceThread(ready, start, () -> {
      try
      {
        in.close();
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }, failure);
    Thread secondCloser = raceThread(ready, start, () -> {
      try
      {
        in.close();
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }, failure);

    firstCloser.start();
    secondCloser.start();
    assertTrue(ready.await(5, TimeUnit.SECONDS));
    start.countDown();
    firstCloser.join(5000);
    secondCloser.join(5000);

    assertFalse(firstCloser.isAlive());
    assertFalse(secondCloser.isAlive());
    assertNull(failure.get());
    assertEquals(2L, ((IBufferProvider.Introspection)bufferPool).getRetainedBuffers());
  }

  public void testEOSAndCCAMStateSurviveReadAndClose() throws Exception
  {
    ChannelClosingBufferInputStream in = new ChannelClosingBufferInputStream();
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);
    out.flushWithEOS(true);
    out.close();

    assertEquals(IOUtil.EOF, in.read());
    assertTrue(in.isCCAM());
    in.close();
    assertEquals(1, in.channelCloseCount.get());
  }

  private static Thread createReader(BufferInputStream in, AtomicReference<Throwable> failure, AtomicInteger result, CountDownLatch started,
      CountDownLatch finished)
  {
    Thread reader = new Thread(() -> {
      started.countDown();
      try
      {
        result.set(in.read());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "Buffer reader"); //$NON-NLS-1$
    reader.setDaemon(true);
    return reader;
  }

  public void testReadArray() throws Exception
  {
    byte[] data = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    BufferInputStream in = new BufferInputStream();
    byte[] result = new byte[2 * data.length];
    AtomicInteger bytesRead = new AtomicInteger();

    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);

    Thread consumer = new Thread("CONSUMER")
    {
      @Override
      public void run()
      {
        try
        {
          bytesRead.set(in.read(result));
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    consumer.setDaemon(true);
    consumer.start();

    out.write(data);
    out.flushWithEOS();
    out.close();

    consumer.join(10000000 * DEFAULT_TIMEOUT);

    assertEquals(data.length, bytesRead.get());
    byte[] actual = new byte[bytesRead.get()];
    System.arraycopy(result, 0, actual, 0, bytesRead.get());
    assertArrayEquals(data, actual);
  }

  public void testCopyBinary() throws Exception
  {
    byte[] data = HugeData.getBytes();

    BufferInputStream in = new BufferInputStream();
    ByteArrayOutputStream result = new ByteArrayOutputStream();

    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);

    Thread consumer = new Thread("CONSUMER")
    {
      @Override
      public void run()
      {
        try
        {
          IOUtil.copyBinary(in, result);
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    consumer.setDaemon(true);
    consumer.start();

    out.write(data);
    out.flushWithEOS();
    out.close();

    consumer.join(10000000 * DEFAULT_TIMEOUT);
    assertArrayEquals(data, result.toByteArray());
  }

  /**
   * @author Eike Stepper
   */
  private static final class BufferStreamPipe implements IBufferHandler
  {
    private final BufferInputStream in;

    public BufferStreamPipe(BufferInputStream in)
    {
      this.in = in;
    }

    @Override
    public void handleBuffer(IBuffer buffer)
    {
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      short payload = (short)(byteBuffer.position() - IBuffer.HEADER_SIZE);

      byteBuffer.flip();
      byteBuffer.putShort(CHANNEL_ID);
      byteBuffer.putShort(payload);
      in.handleBuffer(buffer);
    }
  }

  /**
   * Supplies a finite timeout for tests of waiting behavior.
   *
   * @author Eike Stepper
   */
  private static class TimedBufferInputStream extends BufferInputStream
  {
    private final long timeout;

    public TimedBufferInputStream(long timeout)
    {
      this.timeout = timeout;
    }

    @Override
    public long getMillisBeforeTimeout()
    {
      return timeout;
    }
  }

  /**
   * Exposes when the first timeout deadline has been established for a coordinated restart test.
   *
   * @author Eike Stepper
   */
  private static final class RestartableBufferInputStream extends TimedBufferInputStream
  {
    private final CountDownLatch timeoutStarted = new CountDownLatch(1);

    public RestartableBufferInputStream(long timeout)
    {
      super(timeout);
    }

    @Override
    public void restartTimeout()
    {
      super.restartTimeout();
      timeoutStarted.countDown();
    }
  }

  /**
   * Signals the first acquisition attempt while called under the reader's monitor, allowing a state change to race with
   * the transition into monitor waiting.
   *
   * @author Eike Stepper
   */
  private static final class WaitingBufferInputStream extends BufferInputStream
  {
    private final long timeout;

    private final CountDownLatch waitingForBuffer = new CountDownLatch(1);

    public WaitingBufferInputStream()
    {
      this(NO_TIMEOUT);
    }

    public WaitingBufferInputStream(long timeout)
    {
      this.timeout = timeout;
    }

    @Override
    public long getMillisBeforeTimeout()
    {
      return timeout;
    }

    @Override
    protected boolean ensureBuffer() throws IOException
    {
      waitingForBuffer.countDown();
      return super.ensureBuffer();
    }
  }

  /**
   * Signals when a bulk reader requests a second buffer, so a producer can deliver it during the same read operation.
   *
   * @author Eike Stepper
   */
  private static final class ArrivalBufferInputStream extends BufferInputStream
  {
    private final AtomicInteger acquisitionAttempts = new AtomicInteger();

    private final CountDownLatch secondBufferRequested = new CountDownLatch(1);

    @Override
    protected boolean ensureBuffer() throws IOException
    {
      if (acquisitionAttempts.incrementAndGet() == 2)
      {
        secondBufferRequested.countDown();
      }

      return super.ensureBuffer();
    }
  }

  /**
   * Records the CCAM lifecycle callback invoked by closing an EOS stream.
   *
   * @author Eike Stepper
   */
  private static final class ChannelClosingBufferInputStream extends BufferInputStream
  {
    private final AtomicInteger channelCloseCount = new AtomicInteger();

    @Override
    protected void closeChannel()
    {
      channelCloseCount.incrementAndGet();
    }
  }

  /**
   * Pauses after acquiring a selected buffer while the stream monitor is held, so close races can be coordinated.
   *
   * @author Eike Stepper
   */
  private static final class ControlledBufferInputStream extends BufferInputStream
  {
    private final int pauseOnAcquisition;

    private final AtomicInteger acquisitions = new AtomicInteger();

    private final CountDownLatch bufferAcquired = new CountDownLatch(1);

    private final CountDownLatch allowRead = new CountDownLatch(1);

    private final CountDownLatch closeStarted = new CountDownLatch(1);

    public ControlledBufferInputStream(int pauseOnAcquisition)
    {
      this.pauseOnAcquisition = pauseOnAcquisition;
    }

    @Override
    protected boolean ensureBuffer() throws IOException
    {
      boolean available = super.ensureBuffer();
      if (available && acquisitions.incrementAndGet() == pauseOnAcquisition)
      {
        bufferAcquired.countDown();
        try
        {
          if (!allowRead.await(5, TimeUnit.SECONDS))
          {
            throw new IllegalStateException("Timed out waiting to continue controlled read"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      return available;
    }

    @Override
    public void close() throws IOException
    {
      closeStarted.countDown();
      super.close();
    }
  }
}
