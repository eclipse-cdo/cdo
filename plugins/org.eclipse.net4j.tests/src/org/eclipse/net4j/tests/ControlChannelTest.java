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
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IncompatibleControlProtocolException;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocol2;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.internal.net4j.ControlChannel;
import org.eclipse.internal.net4j.buffer.Buffer;
import org.eclipse.internal.net4j.buffer.BufferUtil;

import org.eclipse.spi.net4j.Channel;
import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.Protocol;
import org.eclipse.spi.net4j.SchedulingConnector;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises control protocol decoding and ownership through the connector's real control channel.
 *
 * @author Eike Stepper
 */
public class ControlChannelTest extends AbstractOMTest
{
  public void testHelloOpcodeIsZero()
  {
    assertEquals(0, ControlChannel.OPCODE_HELLO);
  }

  public void testUnknownOpcodeIsRejectedAndBufferReleased() throws Exception
  {
    TestConnector connector = new TestConnector();
    TrackingBufferProvider provider = new TrackingBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.getConfig().setBufferProvider(provider);
    LifecycleUtil.activate(connector);
    connector.leaveConnecting();
    receiveHello(connector);
    try
    {
      IBuffer delegate = connector.provideBuffer();
      int releasesBefore = provider.releaseCount.get();
      ByteBuffer payload = delegate.startPutting((short)0);
      payload.put((byte)99);
      delegate.flip();

      try
      {
        control(connector).handleBufferFromMultiplexer(delegate);
        fail("Unknown opcode was accepted");
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(expected.getMessage().contains("Invalid control opcode 99 for Net4j control protocol version 1")); //$NON-NLS-1$
      }

      assertEquals(releasesBefore + 1, provider.releaseCount.get());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testHelloCompletesConnectionAfterVersionCheck() throws Exception
  {
    assertEquals("The version 1 HELLO opcode changed", 0, ControlChannel.OPCODE_HELLO); //$NON-NLS-1$
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      receiveHello(connector);
      assertEquals(ConnectorState.CONNECTED, connector.getState());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testVersionMismatchIsPreservedForConnectionWaiter() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      try
      {
        receiveHello(connector, 2, 0x45494B45);
        fail("A different control protocol version was accepted"); //$NON-NLS-1$
      }
      catch (ConnectorException expected)
      {
        assertTrue(expected instanceof IncompatibleControlProtocolException);
        assertTrue(expected.getMessage().contains("local version 1, remote version 2")); //$NON-NLS-1$
      }

      assertFalse(connector.getState() == ConnectorState.CONNECTED);

      try
      {
        connector.waitForConnection(5000);
        fail("A failed control handshake reported a connection"); //$NON-NLS-1$
      }
      catch (ConnectorException expected)
      {
        IncompatibleControlProtocolException incompatibility = findIncompatibleCause(expected);
        assertNotNull(incompatibility);
        assertTrue(incompatibility.getMessage().contains("local version 1, remote version 2")); //$NON-NLS-1$
      }
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testConcurrentHandshakeCompletionAndFailureHaveOneWinner() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread completion = new Thread(() -> {
      await(start, failure);
      receiveHello(connector);
    }, "Complete control handshake"); //$NON-NLS-1$
    Thread transportFailure = new Thread(() -> {
      await(start, failure);
      connector.failControlHandshake(1, new ConnectorException("concurrent transport failure")); //$NON-NLS-1$
    }, "Fail control handshake"); //$NON-NLS-1$

    try
    {
      connector.leaveConnecting();
      completion.start();
      transportFailure.start();
      start.countDown();
      completion.join(TimeUnit.SECONDS.toMillis(5));
      transportFailure.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("Handshake completion did not finish", completion.isAlive()); //$NON-NLS-1$
      assertFalse("Handshake failure did not finish", transportFailure.isAlive()); //$NON-NLS-1$
      assertNull(failure.get());

      if (connector.getState() == ConnectorState.CONNECTED)
      {
        connector.waitForConnection(1000);
        assertTrue(connector.isConnected());
      }
      else
      {
        try
        {
          connector.waitForConnection(1000);
          fail("The failed handshake was reported as connected"); //$NON-NLS-1$
        }
        catch (ConnectorException expected)
        {
          assertNotNull(expected.getCause());
          assertTrue(expected.getCause().getMessage().contains("concurrent transport failure")); //$NON-NLS-1$
        }
      }
    }
    finally
    {
      start.countDown();
      completion.join(TimeUnit.SECONDS.toMillis(1));
      transportFailure.join(TimeUnit.SECONDS.toMillis(1));
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testMalformedHelloCannotCompleteConnection() throws Exception
  {
    assertRejectedHello(0x12345678, ControlChannel.CONTROL_PROTOCOL_VERSION, "Malformed Net4j control protocol HELLO"); //$NON-NLS-1$
  }

  public void testTruncatedHelloCannotCompleteConnection() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      IBuffer hello = connector.provideBuffer();
      hello.startPutting((short)0).put(ControlChannel.OPCODE_HELLO);
      hello.flip();
      try
      {
        control(connector).handleBufferFromMultiplexer(hello);
        fail("A truncated HELLO was accepted"); //$NON-NLS-1$
      }
      catch (ConnectorException expected)
      {
        assertTrue(expected instanceof IncompatibleControlProtocolException);
        assertTrue(expected.getMessage().contains("Malformed Net4j control protocol HELLO")); //$NON-NLS-1$
      }

      assertFalse(connector.getState() == ConnectorState.CONNECTED);
      assertHandshakeFailure(connector);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testUnexpectedControlMessageCannotCompleteConnection() throws Exception
  {
    for (byte opcode : new byte[] { 1, 5, 99 })
    {
      TestConnector connector = new TestConnector();
      connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
      LifecycleUtil.activate(connector);
      try
      {
        connector.leaveConnecting();
        IBuffer unexpected = connector.provideBuffer();
        unexpected.startPutting((short)0).put(opcode);
        unexpected.flip();
        try
        {
          control(connector).handleBufferFromMultiplexer(unexpected);
          fail("A non-HELLO control opcode was accepted before the handshake: " + opcode); //$NON-NLS-1$
        }
        catch (IncompatibleControlProtocolException expected)
        {
          assertTrue(expected.getMessage().contains("unexpected control opcode " + opcode + " before HELLO")); //$NON-NLS-1$
          assertTrue(expected.getMessage().contains("remote version is unknown")); //$NON-NLS-1$
        }

        assertFalse(connector.getState() == ConnectorState.CONNECTED);
        assertHandshakeFailure(connector);
      }
      finally
      {
        LifecycleUtil.deactivate(connector);
      }
    }
  }

  public void testDuplicateHelloIsRejected() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      try
      {
        receiveHello(connector);
        fail("A duplicate HELLO was accepted"); //$NON-NLS-1$
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(expected.getMessage().contains("Unexpected duplicate HELLO for Net4j control protocol version 1")); //$NON-NLS-1$
      }

      assertEquals(ConnectorState.CONNECTED, connector.getState());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testDuplicateHelloDuringHandshakeIsIncompatible() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.deferHelloSent = true;
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      receiveHello(connector);
      try
      {
        receiveHello(connector);
        fail("A duplicate HELLO during handshake was accepted"); //$NON-NLS-1$
      }
      catch (IncompatibleControlProtocolException expected)
      {
        assertTrue(expected.getMessage().contains("Unexpected duplicate Net4j control protocol HELLO")); //$NON-NLS-1$
      }

      assertHandshakeFailure(connector);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testMissingHelloTimesOutWithUnknownRemoteVersion() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      try
      {
        connector.waitForConnection(15000);
        fail("A peer that omitted HELLO reported a connection"); //$NON-NLS-1$
      }
      catch (ConnectorException expected)
      {
        IncompatibleControlProtocolException incompatibility = findIncompatibleCause(expected);
        assertNotNull(incompatibility);
        assertTrue(incompatibility.getMessage().contains("remote version is unknown")); //$NON-NLS-1$
      }
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testMalformedPreHelloPayloadPreservesDecodingCause() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      IBuffer malformed = connector.provideBuffer();
      malformed.startPutting((short)0);
      malformed.flip();
      IncompatibleControlProtocolException failure = null;
      try
      {
        control(connector).handleBufferFromMultiplexer(malformed);
        fail("A control frame without an opcode was accepted"); //$NON-NLS-1$
      }
      catch (IncompatibleControlProtocolException expected)
      {
        assertNotNull(expected.getCause());
        failure = expected;
      }

      try
      {
        connector.waitForConnection(5000);
        fail("The malformed control frame was reported as connected"); //$NON-NLS-1$
      }
      catch (ConnectorException expected)
      {
        assertSame(failure, findIncompatibleCause(expected));
        assertNotNull(findIncompatibleCause(expected).getCause());
      }
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testRegistrationRequestProducesAcknowledgement() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      IBuffer request = connector.provideBuffer();
      ByteBuffer payload = request.startPutting((short)0);
      payload.put((byte)2);
      payload.putShort((short)17);
      payload.putInt(1);
      BufferUtil.putString(payload, "missing.protocol", false);
      request.flip();

      control(connector).handleBufferFromMultiplexer(request);

      IBuffer acknowledgement;
      synchronized (control(connector).getSendQueue())
      {
        acknowledgement = control(connector).getSendQueue().poll();
      }

      assertNotNull(acknowledgement);
      ByteBuffer bytes = acknowledgement.getByteBuffer();
      bytes.position(IBuffer.HEADER_SIZE);
      assertEquals(3, bytes.get());
      assertEquals(17, bytes.getShort());
      assertNotNull(BufferUtil.getString(bytes));
      acknowledgement.release();
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testLateAcknowledgementWithoutWaiterIsIgnored() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      IBuffer acknowledgement = connector.provideBuffer();
      ByteBuffer payload = acknowledgement.startPutting((short)0);
      payload.put((byte)3);
      payload.putShort((short)19);
      BufferUtil.putString(payload, "", true);
      acknowledgement.flip();

      control(connector).handleBufferFromMultiplexer(acknowledgement);
      assertTrue(connector.getScheduler().isEmpty());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testDeregistrationWireFormatAndOutgoingOwnership() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      InternalChannel control = control(connector);
      Queue<IBuffer> sendQueue = control.getSendQueue();
      connector.deregister((short)23);

      IBuffer deregistration;
      synchronized (sendQueue)
      {
        deregistration = sendQueue.peek();
      }

      assertNotNull(deregistration);
      ByteBuffer bytes = deregistration.getByteBuffer();
      bytes.position(IBuffer.HEADER_SIZE);
      assertEquals(4, bytes.get());
      assertEquals(23, bytes.getShort());

      assertSame(deregistration, sendQueue.peek());
      LifecycleUtil.deactivate(connector);
      assertTrue(sendQueue.isEmpty());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testTruncatedRegistrationGetsFailureAcknowledgement() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      IBuffer request = connector.provideBuffer();
      ByteBuffer payload = request.startPutting((short)0);
      payload.put((byte)2);
      payload.putShort((short)29);
      request.flip();

      control(connector).handleBufferFromMultiplexer(request);

      IBuffer acknowledgement;
      synchronized (control(connector).getSendQueue())
      {
        acknowledgement = control(connector).getSendQueue().poll();
      }

      assertNotNull(acknowledgement);
      ByteBuffer bytes = acknowledgement.getByteBuffer();
      bytes.position(IBuffer.HEADER_SIZE);
      assertEquals(3, bytes.get());
      assertEquals(29, bytes.getShort());
      assertEquals("BufferUnderflowException", BufferUtil.getString(bytes)); //$NON-NLS-1$
      acknowledgement.release();
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testSuccessfulAndFailedCorrelatedAcknowledgements() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      connector.acknowledgement = ""; //$NON-NLS-1$
      connector.register((short)31, 1000, protocol());
      ByteBuffer registration = connector.lastRegistration.duplicate();
      registration.position(IBuffer.HEADER_SIZE);
      assertEquals(2, registration.get());
      assertEquals(31, registration.getShort());
      assertEquals(7, registration.getInt());
      assertEquals("test.protocol", BufferUtil.getString(registration)); //$NON-NLS-1$

      connector.acknowledgement = "remote rejected"; //$NON-NLS-1$
      try
      {
        connector.register((short)32, 1000, protocol());
        fail("Remote registration failure was accepted");
      }
      catch (ChannelException expected)
      {
        assertTrue(expected.getMessage().contains("remote rejected")); //$NON-NLS-1$
      }
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testUnsolicitedAndTimedOutAcknowledgementsDoNotSatisfyReuse() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      connector.acknowledgement = null;
      sendAcknowledgement(connector, (short)37, ""); //$NON-NLS-1$
      assertTimeout(connector, (short)37, 20);

      sendAcknowledgement(connector, (short)37, ""); //$NON-NLS-1$
      assertTimeout(connector, (short)37, 20);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testConcurrentRegistrationsAcceptReverseAcknowledgements() throws Exception
  {
    TestConnector connector = activateConnector();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    CountDownLatch start = new CountDownLatch(1);
    Thread first = registrationWorker(connector, (short)51, start, failure);
    Thread second = registrationWorker(connector, (short)52, start, failure);
    try
    {
      connector.acknowledgement = null;
      connector.registrationsProcessed = new CountDownLatch(2);
      first.start();
      second.start();
      start.countDown();
      assertTrue("Both registration requests were not sent", connector.registrationsProcessed.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      Short firstID = connector.sentChannelIDs.poll();
      Short secondID = connector.sentChannelIDs.poll();
      assertNotNull(firstID);
      assertNotNull(secondID);
      sendAcknowledgement(connector, secondID, ""); //$NON-NLS-1$
      sendAcknowledgement(connector, firstID, ""); //$NON-NLS-1$
      join(first);
      join(second);
      assertNull(failure.get());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
      first.interrupt();
      second.interrupt();
    }
  }

  public void testShutdownFailsPendingRegistrationWait() throws Exception
  {
    TestConnector connector = activateConnector();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread registration = new Thread(() -> {
      try
      {
        connector.register((short)41, 30000, protocol());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "control-registration"); //$NON-NLS-1$
    registration.setDaemon(true);
    try
    {
      connector.acknowledgement = null;
      registration.start();
      assertTrue("Registration request was not sent", connector.registrationsProcessed.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      LifecycleUtil.deactivate(connector);
      registration.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("Registration thread did not terminate", registration.isAlive()); //$NON-NLS-1$
      assertNotNull(failure.get());
      assertTrue(failure.get() instanceof ChannelException);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
      registration.interrupt();
    }
  }

  public void testInvalidRegistrationIDsAreRejected() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      for (short channelID : new short[] { 0, IBuffer.NO_CHANNEL })
      {
        try
        {
          connector.register(channelID, 100, protocol());
          fail("Invalid channel ID was accepted: " + channelID);
        }
        catch (IllegalArgumentException expected)
        {
          // Expected.
        }
      }
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testRegistrationSerializationFailureReleasesBufferAndClearsCorrelation() throws Exception
  {
    TestConnector connector = new TestConnector();
    TrackingBufferProvider provider = new TrackingBufferProvider(Net4jUtil.createBufferFactory((short)64));
    connector.getConfig().setBufferProvider(provider);
    LifecycleUtil.activate(connector);
    connector.leaveConnecting();
    receiveHello(connector);
    int releasesBeforeRegistration = provider.releaseCount.get();
    try
    {
      try
      {
        connector.register((short)61, 1000, protocol("x".repeat(1024)));
        fail("Oversized protocol ID was serialized");
      }
      catch (RuntimeException expected)
      {
        assertEquals(releasesBeforeRegistration + 1, provider.releaseCount.get());
      }

      connector.acknowledgement = null;
      sendAcknowledgement(connector, (short)61, ""); //$NON-NLS-1$
      assertTimeout(connector, (short)61, 20);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testInverseDeregistrationClosesPeerChannel() throws Exception
  {
    TestConnector connector = new TestConnector();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.getConfig().setReceiveExecutor(executor);
    LifecycleUtil.activate(connector);
    connector.leaveConnecting();
    receiveHello(connector);
    try
    {
      InternalChannel peerChannel = connector.openInverse((short)-44);
      assertNotNull(peerChannel);
      assertSame(peerChannel, connector.getChannel((short)-44));

      IBuffer deregistration = connector.provideBuffer();
      ByteBuffer payload = deregistration.startPutting((short)0);
      payload.put((byte)4);
      payload.putShort((short)-44);
      deregistration.flip();
      control(connector).handleBufferFromMultiplexer(deregistration);

      assertNull(connector.getChannel((short)-44));
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
      executor.shutdownNow();
    }
  }

  public void testNegotiationOpcodeUsesConfiguredReceiver() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    AtomicReference<ByteBuffer> received = new AtomicReference<>();
    connector.setNegotiator(new INegotiator()
    {
      @Override
      public boolean isInitiator()
      {
        return false;
      }

      @Override
      public void negotiate(INegotiationContext context)
      {
        context.setReceiver((ignored, buffer) -> received.set(buffer));
      }
    });
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      receiveHello(connector);
      IBuffer negotiation = connector.provideBuffer();
      ByteBuffer payload = negotiation.startPutting((short)0);
      payload.put((byte)1);
      payload.put((byte)0x5A);
      negotiation.flip();
      control(connector).handleBufferFromMultiplexer(negotiation);
      assertNotNull(received.get());
      assertEquals((byte)0x5A, received.get().get());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  private void assertTimeout(TestConnector connector, short channelID, long timeout)
  {
    try
    {
      connector.register(channelID, timeout, protocol());
      fail("Registration without an acknowledgement did not time out");
    }
    catch (TimeoutRuntimeException expected)
    {
    }
  }

  private static void sendAcknowledgement(TestConnector connector, short channelID, String error)
  {
    IBuffer acknowledgement = connector.provideBuffer();
    ByteBuffer payload = acknowledgement.startPutting((short)0);
    payload.put((byte)3);
    payload.putShort(channelID);
    BufferUtil.putString(payload, error, true);
    acknowledgement.flip();
    control(connector).handleBufferFromMultiplexer(acknowledgement);
  }

  private static IProtocol2<?> protocol()
  {
    return protocol("test.protocol"); //$NON-NLS-1$
  }

  private static IProtocol2<?> protocol(String type)
  {
    return new TestProtocol(type);
  }

  private static Thread registrationWorker(TestConnector connector, short channelID, CountDownLatch start, AtomicReference<Throwable> failure)
  {
    Thread thread = new Thread(() -> {
      try
      {
        assertTrue("Timed out waiting for registration start", start.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
        connector.register(channelID, 5000, protocol());
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, "registration-" + channelID); //$NON-NLS-1$
    thread.setDaemon(true);
    return thread;
  }

  private static void join(Thread thread) throws InterruptedException
  {
    thread.join(TimeUnit.SECONDS.toMillis(5));
    assertFalse("Registration worker did not terminate", thread.isAlive()); //$NON-NLS-1$
  }

  private static void await(CountDownLatch latch, AtomicReference<Throwable> failure)
  {
    try
    {
      latch.await();
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      failure.compareAndSet(null, ex);
    }
  }

  private TestConnector activateConnector() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    connector.leaveConnecting();
    receiveHello(connector);
    return connector;
  }

  private static void receiveHello(TestConnector connector)
  {
    receiveHello(connector, ControlChannel.CONTROL_PROTOCOL_VERSION, 0x45494B45);
  }

  private static void receiveHello(TestConnector connector, int version, int magic)
  {
    IBuffer hello = connector.provideBuffer();
    ByteBuffer payload = hello.startPutting((short)0);
    payload.put(ControlChannel.OPCODE_HELLO);
    payload.putInt(magic);
    payload.putInt(version);
    hello.flip();
    control(connector).handleBufferFromMultiplexer(hello);
  }

  private void assertRejectedHello(int magic, int version, String diagnostic) throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    try
    {
      connector.leaveConnecting();
      try
      {
        receiveHello(connector, version, magic);
        fail("An invalid HELLO was accepted"); //$NON-NLS-1$
      }
      catch (ConnectorException expected)
      {
        assertTrue(expected instanceof IncompatibleControlProtocolException);
        assertTrue(expected.getMessage().contains(diagnostic));
      }

      assertFalse(connector.getState() == ConnectorState.CONNECTED);
      assertHandshakeFailure(connector);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  private void assertHandshakeFailure(TestConnector connector)
  {
    try
    {
      connector.waitForConnection(5000);
      fail("An invalid handshake reported a connection"); //$NON-NLS-1$
    }
    catch (ConnectorException expected)
    {
      assertNotNull(findIncompatibleCause(expected));
    }
  }

  private static IncompatibleControlProtocolException findIncompatibleCause(Throwable failure)
  {
    for (Throwable cause = failure; cause != null; cause = cause.getCause())
    {
      if (cause instanceof IncompatibleControlProtocolException)
      {
        return (IncompatibleControlProtocolException)cause;
      }
    }

    return null;
  }

  private static InternalChannel control(TestConnector connector)
  {
    return (InternalChannel)connector.getControlChannel();
  }

  /**
   * Provides the negotiation context required by connector activation.
   *
   * @author Eike Stepper
   */
  private static final class TestConnector extends SchedulingConnector
  {
    private boolean deferHelloSent;

    private volatile String acknowledgement;

    private volatile CountDownLatch registrationsProcessed = new CountDownLatch(1);

    private final ConcurrentLinkedQueue<Short> sentChannelIDs = new ConcurrentLinkedQueue<>();

    private volatile ByteBuffer lastRegistration;

    @Override
    protected INegotiationContext createNegotiationContext()
    {
      return new NegotiationContext()
      {
        @Override
        public ByteBuffer getBuffer()
        {
          return null;
        }

        @Override
        public void transmitBuffer(ByteBuffer buffer)
        {
        }

        @Override
        public void setUserID(String userID)
        {
        }
      };
    }

    @Override
    protected void onSendingRequired()
    {
      InternalChannel control = (InternalChannel)getControlChannel();
      if (selectScheduledChannel() != control)
      {
        return;
      }

      IBuffer buffer;
      synchronized (control.getSendQueue())
      {
        buffer = control.getSendQueue().peek();
      }

      if (buffer == null)
      {
        return;
      }

      ByteBuffer bytes = buffer.getByteBuffer().duplicate();
      bytes.position(IBuffer.HEADER_SIZE);
      byte opcode = bytes.get();
      if (opcode == ControlChannel.OPCODE_HELLO)
      {
        if (deferHelloSent)
        {
          return;
        }

        controlBufferSent(control, buffer);
        synchronized (control.getSendQueue())
        {
          control.getSendQueue().remove(buffer);
        }

        buffer.release();
        completeScheduledChannel(control, true);
        return;
      }

      short channelID = bytes.getShort();
      if (opcode != 2)
      {
        return;
      }

      lastRegistration = bytes.duplicate();
      sentChannelIDs.add(channelID);
      registrationsProcessed.countDown();
      synchronized (control.getSendQueue())
      {
        control.getSendQueue().remove(buffer);
      }

      buffer.release();
      complete(control);
      if (acknowledgement != null)
      {
        sendAcknowledgement(this, channelID, acknowledgement);
      }
    }

    public void register(short channelID, long timeout, IProtocol2<?> protocol)
    {
      registerChannelWithPeer(channelID, timeout, protocol);
    }

    public InternalChannel openInverse(short channelID)
    {
      return inverseOpenChannel(channelID, "test.protocol", 7); //$NON-NLS-1$
    }

    @Override
    public Location getLocation()
    {
      return Location.CLIENT;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <INFRA_STRUCTURE> IProtocol<INFRA_STRUCTURE> createProtocol(String type, INFRA_STRUCTURE infraStructure)
    {
      return (IProtocol<INFRA_STRUCTURE>)new TestProtocol(type);
    }

    @Override
    public void leaveConnecting()
    {
      super.leaveConnecting();
    }

    public void complete(InternalChannel channel)
    {
      completeScheduledChannel(channel, true);
    }

    public void deregister(short channelID)
    {
      InternalChannel channel = new Channel();
      channel.setID(channelID);
      deregisterChannelFromPeer(channel);
    }
  }

  /**
   * Counts buffer releases while delegating allocation to the configured provider.
   *
   * @author Eike Stepper
   */
  private static final class TrackingBufferProvider implements IBufferProvider
  {
    private final IBufferProvider delegate;

    private final AtomicInteger releaseCount = new AtomicInteger();

    public TrackingBufferProvider(IBufferProvider delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public short getBufferCapacity()
    {
      return delegate.getBufferCapacity();
    }

    @Override
    public IBuffer provideBuffer()
    {
      return new Buffer(this, getBufferCapacity());
    }

    @Override
    public void retainBuffer(IBuffer buffer)
    {
      releaseCount.incrementAndGet();
      delegate.retainBuffer(buffer);
    }
  }

  /**
   * Supplies stable protocol metadata for control-channel registration tests.
   *
   * @author Eike Stepper
   */
  private static final class TestProtocol extends Protocol<Object>
  {
    private TestProtocol(String type)
    {
      super(type);
    }

    @Override
    public int getVersion()
    {
      return 7;
    }

    @Override
    public void handleBuffer(IBuffer buffer)
    {
      buffer.release();
    }
  }
}
