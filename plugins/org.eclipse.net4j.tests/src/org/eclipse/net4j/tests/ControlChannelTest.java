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
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocol2;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.internal.net4j.buffer.BufferUtil;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.SchedulingConnector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
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
  public void testUnknownOpcodeIsRejectedAndBufferReleased() throws Exception
  {
    TestConnector connector = activateConnector();
    try
    {
      IBuffer delegate = connector.provideBuffer();
      int[] releaseCount = { 0 };
      IBuffer buffer = (IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, (proxy, method, args) -> {
        if ("release".equals(method.getName())) //$NON-NLS-1$
        {
          ++releaseCount[0];
        }

        try
        {
          return method.invoke(delegate, args);
        }
        catch (InvocationTargetException ex)
        {
          throw ex.getCause();
        }
      });
      ByteBuffer payload = buffer.startPutting((short)0);
      payload.put((byte)99);
      buffer.flip();

      try
      {
        control(connector).handleBufferFromMultiplexer(buffer);
        fail("Unknown opcode was accepted");
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(expected.getMessage().contains("99"));
      }

      assertEquals(1, releaseCount[0]);
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
      assertTrue(failure.get().getMessage().contains("deactivated")); //$NON-NLS-1$
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
    connector.setState(ConnectorState.CONNECTED);
    try
    {
      try
      {
        connector.register((short)61, 1000, protocol("x".repeat(1024)));
        fail("Oversized protocol ID was serialized");
      }
      catch (RuntimeException expected)
      {
        assertEquals(1, provider.releaseCount.get());
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
    TestConnector connector = activateConnector();
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
    return (IProtocol2<?>)Proxy.newProxyInstance(IProtocol2.class.getClassLoader(), new Class<?>[] { IProtocol2.class }, (proxy, method, args) -> {
      if ("getType".equals(method.getName())) //$NON-NLS-1$
      {
        return type;
      }

      if ("getVersion".equals(method.getName())) //$NON-NLS-1$
      {
        return 7;
      }

      if ("toString".equals(method.getName())) //$NON-NLS-1$
      {
        return "test.protocol/7"; //$NON-NLS-1$
      }

      return null;
    });
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

  private TestConnector activateConnector() throws Exception
  {
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(connector);
    connector.setState(ConnectorState.CONNECTED);
    return connector;
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
      return (IProtocol<INFRA_STRUCTURE>)protocol();
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
      InternalChannel channel = (InternalChannel)Proxy.newProxyInstance(InternalChannel.class.getClassLoader(), new Class<?>[] { InternalChannel.class },
          (proxy, method, args) -> {
            if ("getID".equals(method.getName())) //$NON-NLS-1$
            {
              return channelID;
            }

            return null;
          });
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
      IBuffer buffer = delegate.provideBuffer();
      return (IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, (proxy, method, args) -> {
        if ("release".equals(method.getName())) //$NON-NLS-1$
        {
          releaseCount.incrementAndGet();
        }

        try
        {
          return method.invoke(buffer, args);
        }
        catch (InvocationTargetException ex)
        {
          throw ex.getCause();
        }
      });
    }

    @Override
    public void retainBuffer(IBuffer buffer)
    {
      delegate.retainBuffer(buffer);
    }
  }
}
