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
import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.trace.Element;
import org.eclipse.net4j.trace.Element.BufferElement;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.InternalBuffer;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises transport-neutral message framing and externally backed incoming buffers.
 *
 * @author Eike Stepper
 */
public class BufferFramingTest extends AbstractOMTest
{
  public void testPrepareForSendingPreservesBufferStateAndPayload()
  {
    IBufferProvider provider = Net4jUtil.createBufferFactory((short)16);
    IBuffer buffer = provider.provideBuffer();
    ByteBuffer payload = buffer.startPutting((short)-7);
    payload.put(new byte[] { 10, 20, 30 });
    int originalPosition = payload.position();
    int originalLimit = payload.limit();

    ByteBuffer message = Net4jUtil.prepareForSending(buffer);

    assertEquals(BufferState.PUTTING, buffer.getState());
    assertEquals(originalPosition, payload.position());
    assertEquals(originalLimit, payload.limit());
    assertEquals(IBuffer.HEADER_SIZE + 3, message.limit());
    assertEquals(0, message.position());
    assertEquals(-7, message.getShort(IBuffer.CHANNEL_ID_POS));
    assertEquals(3, message.getShort(IBuffer.PAYLOAD_SIZE_POS));
    assertEquals(10, message.get(IBuffer.HEADER_SIZE));
    assertEquals(20, message.get(IBuffer.HEADER_SIZE + 1));
    assertEquals(30, message.get(IBuffer.HEADER_SIZE + 2));
    byte[] encoded = new byte[message.remaining()];
    message.get(encoded);
    assertEquals(-1, encoded[0]);
    assertEquals(-7, encoded[1]);
    assertEquals(0, encoded[2]);
    assertEquals(3, encoded[3]);
  }

  public void testByteBufferAccessRequiresPuttingOrGetting()
  {
    IBuffer buffer = Net4jUtil.createBufferFactory((short)16).provideBuffer();
    try
    {
      buffer.getByteBuffer();
      fail("Expected INITIAL buffer access to be rejected"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
      assertTrue(expected.getMessage().contains("INITIAL")); //$NON-NLS-1$
    }

    ByteBuffer byteBuffer = buffer.startPutting((short)1);
    assertSame(byteBuffer, buffer.getByteBuffer());
    buffer.flip();
    assertEquals(BufferState.GETTING, buffer.getState());
    assertSame(byteBuffer, buffer.getByteBuffer());
    buffer.release();
  }

  public void testDisposedBufferReleaseAndClearRemainTerminal()
  {
    IBuffer buffer = Net4jUtil.createBufferFactory((short)16).provideBuffer();
    buffer.release();
    assertEquals(BufferState.DISPOSED, buffer.getState());

    buffer.release();
    assertEquals(BufferState.DISPOSED, buffer.getState());
    ((InternalBuffer)buffer).dispose();
    assertEquals(BufferState.DISPOSED, buffer.getState());

    try
    {
      buffer.clear();
      fail("Expected disposed buffer clear to be rejected"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
      assertEquals(BufferState.DISPOSED, buffer.getState());
    }

    try
    {
      buffer.getByteBuffer();
      fail("Expected disposed buffer access to be rejected"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
      assertEquals(BufferState.DISPOSED, buffer.getState());
    }
  }

  public void testReleasedPooledBufferCanBeReused()
  {
    IBufferPool pool = Net4jUtil.createBufferPool((short)16);
    LifecycleUtil.activate(pool);
    try
    {
      IBuffer buffer = pool.provideBuffer();
      buffer.startPutting((short)1).put((byte)42);
      buffer.release();
      assertEquals(BufferState.RELEASED, buffer.getState());
      assertEquals(1, Net4jUtil.getPooledBuffers(pool));

      buffer.release();
      assertEquals(1, Net4jUtil.getPooledBuffers(pool));

      IBuffer reused = pool.provideBuffer();
      assertSame(buffer, reused);
      assertEquals(BufferState.INITIAL, reused.getState());
      reused.startPutting((short)2).put((byte)24);
      reused.release();
      assertEquals(1, Net4jUtil.getPooledBuffers(pool));
    }
    finally
    {
      LifecycleUtil.deactivate(pool);
    }
  }

  public void testEosEncodingIncludesEmptyMessage()
  {
    IBufferProvider provider = Net4jUtil.createBufferFactory((short)16);
    IBuffer buffer = provider.provideBuffer();
    buffer.startPutting((short)0);
    buffer.setEOS(true);

    ByteBuffer message = Net4jUtil.prepareForSending(buffer);
    assertEquals(0, message.getShort(IBuffer.PAYLOAD_SIZE_POS));

    IBuffer wrapped = Net4jUtil.wrapBuffer(message, null);
    assertTrue(wrapped.isEOS());
    assertEquals((short)0, wrapped.getChannelID());
    wrapped.release();
  }

  public void testEosEncodingWithPayload()
  {
    IBuffer buffer = Net4jUtil.createBufferFactory((short)8).provideBuffer();
    buffer.startPutting((short)3).put((byte)7);
    buffer.setEOS(true);

    ByteBuffer message = Net4jUtil.prepareForSending(buffer);
    assertEquals(-1, message.getShort(IBuffer.PAYLOAD_SIZE_POS));
    assertEquals(7, message.get(IBuffer.HEADER_SIZE));
  }

  public void testPrepareForSendingRejectsInvalidStateChannelAndLength()
  {
    IBuffer buffer = Net4jUtil.createBufferFactory((short)8).provideBuffer();
    try
    {
      Net4jUtil.prepareForSending(buffer);
      fail("Expected INITIAL buffer to be rejected");
    }
    catch (IllegalStateException expected)
    {
      assertEquals(BufferState.INITIAL, buffer.getState());
    }

    buffer.startPutting(IBuffer.NO_CHANNEL).put((byte)1);
    try
    {
      Net4jUtil.prepareForSending(buffer);
      fail("Expected NO_CHANNEL to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(BufferState.PUTTING, buffer.getState());
    }

    buffer = Net4jUtil.createBufferFactory((short)8).provideBuffer();
    buffer.startPutting((short)1);
    buffer.setPosition(IBuffer.HEADER_SIZE - 1);
    try
    {
      Net4jUtil.prepareForSending(buffer);
      fail("Expected invalid payload position to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(BufferState.PUTTING, buffer.getState());
    }
  }

  public void testMaximumMessageSize()
  {
    IBuffer buffer = Net4jUtil.createBufferFactory(Short.MAX_VALUE).provideBuffer();
    ByteBuffer payload = buffer.startPutting((short)1);
    payload.position(Short.MAX_VALUE);

    ByteBuffer message = Net4jUtil.prepareForSending(buffer);
    assertEquals(Short.MAX_VALUE, message.limit());
    assertEquals(Short.MAX_VALUE - IBuffer.HEADER_SIZE, message.getShort(IBuffer.PAYLOAD_SIZE_POS));
  }

  @SuppressWarnings("deprecation")
  public void testWrapPreservesInputBoundsAndSharesStorage()
  {
    ByteBuffer storage = ByteBuffer.allocate(24);
    storage.position(5);
    storage.putShort((short)-2);
    storage.putShort((short)3);
    storage.put(new byte[] { 1, 2, 3 });
    storage.position(5);
    storage.limit(12);
    int originalPosition = storage.position();
    int originalLimit = storage.limit();

    IBuffer buffer = Net4jUtil.wrapBuffer(storage, null);

    assertEquals(originalPosition, storage.position());
    assertEquals(originalLimit, storage.limit());
    assertEquals(BufferState.GETTING, buffer.getState());
    assertEquals((short)-2, buffer.getChannelID());
    assertEquals(IBuffer.HEADER_SIZE, buffer.getPosition());
    assertEquals(7, buffer.getLimit());
    storage.put(9, (byte)42);
    assertEquals(42, buffer.getByteBuffer().get(IBuffer.HEADER_SIZE));
    assertNull(buffer.getBufferProvider());
    assertFalse(buffer.isCCAM());
    buffer.release();
  }

  public void testDirectReadOnlyAndSlicedMessages()
  {
    ByteBuffer direct = ByteBuffer.allocateDirect(8);
    direct.putShort((short)4).putShort((short)2).put((byte)8).put((byte)9).flip();

    IBuffer buffer = Net4jUtil.wrapBuffer(direct.asReadOnlyBuffer(), null);
    assertEquals((short)4, buffer.getChannelID());
    assertEquals(8, buffer.get());
    assertEquals(9, buffer.get());
    buffer.release();

    ByteBuffer parent = ByteBuffer.allocate(12);
    parent.position(2);
    parent.putShort((short)5).putShort((short)1).put((byte)11);
    parent.position(2);
    parent.limit(7);
    ByteBuffer slice = parent.slice();
    buffer = Net4jUtil.wrapBuffer(slice, null);
    assertEquals((short)5, buffer.getChannelID());
    assertEquals(11, buffer.get());
    buffer.release();
  }

  public void testMalformedMessageReleasesResource()
  {
    AtomicInteger releases = new AtomicInteger();
    ByteBuffer malformed = ByteBuffer.allocate(5);
    malformed.putShort((short)1).putShort((short)2).put((byte)3).flip();

    try
    {
      Net4jUtil.wrapBuffer(malformed, releases::incrementAndGet);
      fail("Expected malformed message to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(1, releases.get());
    }
  }

  public void testInvalidHeadersAndOversizedMessagesReleaseResources()
  {
    ByteBuffer[] invalidMessages = new ByteBuffer[4];
    invalidMessages[0] = ByteBuffer.allocate(3);
    invalidMessages[0].position(0).limit(3);

    invalidMessages[1] = ByteBuffer.allocate(4);
    invalidMessages[1].putShort(IBuffer.NO_CHANNEL).putShort((short)0).flip();

    invalidMessages[2] = ByteBuffer.allocate(4);
    invalidMessages[2].putShort((short)1).putShort(Short.MIN_VALUE).flip();

    invalidMessages[3] = ByteBuffer.allocate(Short.MAX_VALUE + 1);
    invalidMessages[3].position(0).limit(Short.MAX_VALUE + 1);

    for (ByteBuffer invalidMessage : invalidMessages)
    {
      AtomicInteger releases = new AtomicInteger();
      try
      {
        Net4jUtil.wrapBuffer(invalidMessage, releases::incrementAndGet);
        fail("Expected invalid message to be rejected");
      }
      catch (IllegalArgumentException expected)
      {
        assertEquals(1, releases.get());
      }
    }
  }

  public void testConcurrentReleaseRunsActionOnce() throws Exception
  {
    AtomicInteger releases = new AtomicInteger();
    ByteBuffer message = ByteBuffer.allocate(4);
    message.putShort((short)1).putShort((short)0).flip();
    IBuffer buffer = Net4jUtil.wrapBuffer(message, releases::incrementAndGet);
    int threadCount = 8;
    CountDownLatch ready = new CountDownLatch(threadCount);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(threadCount);
    AtomicReference<Throwable> workerFailure = new AtomicReference<>();
    Thread[] threads = new Thread[threadCount];

    try
    {
      for (int i = 0; i < threadCount; i++)
      {
        threads[i] = new Thread(() -> {
          ready.countDown();
          try
          {
            if (!start.await(DEFAULT_TIMEOUT_EXPECTED, TimeUnit.MILLISECONDS))
            {
              throw new AssertionError("Timed out waiting for concurrent release start"); //$NON-NLS-1$
            }

            buffer.release();
          }
          catch (Throwable ex)
          {
            workerFailure.compareAndSet(null, ex);
          }
          finally
          {
            completed.countDown();
          }
        });
        threads[i].setDaemon(true);
        threads[i].start();
      }

      assertTrue("Concurrent release workers did not initialize in time", ready.await(DEFAULT_TIMEOUT_EXPECTED, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
    }
    finally
    {
      start.countDown();
    }

    assertTrue("Concurrent release workers did not finish in time", completed.await(DEFAULT_TIMEOUT_EXPECTED, TimeUnit.MILLISECONDS)); //$NON-NLS-1$

    for (Thread thread : threads)
    {
      if (thread != null)
      {
        thread.join(DEFAULT_TIMEOUT_EXPECTED);
        assertFalse("Concurrent release worker did not terminate", thread.isAlive()); //$NON-NLS-1$
      }
    }

    Throwable failure = workerFailure.get();
    if (failure != null)
    {
      throw new AssertionError("Concurrent release worker failed", failure); //$NON-NLS-1$
    }

    assertEquals(1, releases.get());
    assertEquals(BufferState.RELEASED, buffer.getState());
  }

  public void testUnsupportedOutgoingOperation()
  {
    ByteBuffer message = ByteBuffer.allocate(4);
    message.putShort((short)1).putShort((short)0).flip();
    IBuffer buffer = Net4jUtil.wrapBuffer(message, null);

    try
    {
      buffer.startPutting((short)1);
      fail("Expected outgoing operation to be rejected");
    }
    catch (UnsupportedOperationException expected)
    {
      assertEquals(BufferState.GETTING, buffer.getState());
    }
    finally
    {
      buffer.release();
    }
  }

  public void testBufferPoolRejectsExternalBuffersSafely()
  {
    AtomicInteger releases = new AtomicInteger();
    IBufferProvider incompatibleProvider = new IBufferProvider()
    {
      @Override
      public short getBufferCapacity()
      {
        return IBuffer.HEADER_SIZE;
      }

      @Override
      public IBuffer provideBuffer()
      {
        ByteBuffer message = ByteBuffer.allocate(IBuffer.HEADER_SIZE);
        message.putShort((short)1).putShort((short)0).flip();
        return Net4jUtil.wrapBuffer(message, releases::incrementAndGet);
      }

      @Override
      public void retainBuffer(IBuffer buffer)
      {
        buffer.release();
      }
    };

    IBufferPool pool = Net4jUtil.createBufferPool(incompatibleProvider);
    try
    {
      pool.provideBuffer();
      fail("Expected incompatible provided buffer to be rejected");
    }
    catch (IllegalStateException expected)
    {
      assertEquals(1, releases.get());
    }

    IBuffer external = incompatibleProvider.provideBuffer();
    try
    {
      pool.retainBuffer(external);
      fail("Expected external buffer to be rejected by the pool");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(BufferState.GETTING, external.getState());
    }
    finally
    {
      external.release();
    }
  }

  public void testExternalBufferTracing()
  {
    ByteBuffer message = ByteBuffer.allocate(4);
    message.putShort((short)1).putShort((short)0).flip();
    IBuffer buffer = Net4jUtil.wrapBuffer(message, null);

    BufferElement element = Element.get(buffer);
    assertNotNull(element);
    assertNull(element.getProvider());
    assertEquals(BufferState.GETTING, element.getState());
    assertEquals(IBuffer.HEADER_SIZE, element.getPosition());
    assertEquals(IBuffer.HEADER_SIZE, element.getLimit());
    buffer.release();
  }
}
