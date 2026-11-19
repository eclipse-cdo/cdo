/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IConnectorStateEvent;
import org.eclipse.net4j.connector.IncompatibleControlProtocolException;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.internal.net4j.ControlChannel;
import org.eclipse.internal.net4j.buffer.Buffer;

import org.eclipse.spi.net4j.Connector;
import org.eclipse.spi.net4j.ConnectorFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises the control handshake against a peer using a real TCP socket.
 *
 * @author Eike Stepper
 */
public class TCPControlProtocolTest extends AbstractOMTest
{
  public void testReentrantDeactivationAfterTCPHelloSendReleasesBufferOnce() throws Exception
  {
    try (ServerSocket server = new ServerSocket(0))
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      AtomicReference<Throwable> peerFailure = new AtomicReference<>();
      CountDownLatch clientHelloReceived = new CountDownLatch(1);
      Thread peer = new Thread(() -> {
        try (Socket socket = server.accept())
        {
          socket.setSoTimeout(5000);
          DataOutputStream output = new DataOutputStream(socket.getOutputStream());
          output.writeShort(0);
          output.writeShort(9);
          output.writeByte(ControlChannel.OPCODE_HELLO);
          output.writeInt(0x45494B45);
          output.writeInt(1);
          output.flush();

          DataInputStream input = new DataInputStream(socket.getInputStream());
          assertEquals(0, input.readUnsignedShort());
          assertEquals(9, input.readUnsignedShort());
          assertEquals(ControlChannel.OPCODE_HELLO, input.readUnsignedByte());
          assertEquals(0x45494B45, input.readInt());
          assertEquals(1, input.readInt());
          clientHelloReceived.countDown();
        }
        catch (Throwable ex)
        {
          peerFailure.set(ex);
          clientHelloReceived.countDown();
        }
      }, "reentrant-tcp-control-peer"); //$NON-NLS-1$
      peer.setDaemon(true);
      peer.start();

      try
      {
        ITCPConnector connector = (ITCPConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE,
            "127.0.0.1:" + server.getLocalPort(), false); //$NON-NLS-1$
        TrackingBufferProvider provider = new TrackingBufferProvider();
        ((Connector)connector).getConfig().setBufferProvider(provider);
        CountDownLatch deactivated = new CountDownLatch(1);
        AtomicReference<Throwable> listenerFailure = new AtomicReference<>();
        connector.addListener(event -> {
          if (event instanceof IConnectorStateEvent && ((IConnectorStateEvent)event).getNewState() == ConnectorState.CONNECTED)
          {
            try
            {
              LifecycleUtil.deactivate(connector);
            }
            catch (Throwable ex)
            {
              listenerFailure.set(ex);
            }
            finally
            {
              deactivated.countDown();
            }
          }
        });

        LifecycleUtil.activate(connector);
        assertTrue("The reentrant state listener did not deactivate the connector", deactivated.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
        assertTrue("The peer did not receive the client's HELLO", clientHelloReceived.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
        assertNull(listenerFailure.get());
        assertNull(peerFailure.get());
        assertEquals("The sent and received HELLO buffers were not each returned once", 2, provider.retainedCount()); //$NON-NLS-1$
        assertEquals("Reentrant deactivation returned a buffer more than once", 0, provider.duplicateRetains()); //$NON-NLS-1$
      }
      finally
      {
        LifecycleUtil.deactivate(container);
        peer.join(TimeUnit.SECONDS.toMillis(5));
      }
    }
  }

  public void testTCPPeerClosingWithoutAdvertisingAVersionFailsAsUnknown() throws Exception
  {
    try (ServerSocket server = new ServerSocket(0))
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      AtomicReference<Throwable> peerFailure = new AtomicReference<>();
      Thread peer = new Thread(() -> {
        try (Socket socket = server.accept())
        {
          socket.setSoTimeout(5000);
          DataInputStream input = new DataInputStream(socket.getInputStream());
          input.readUnsignedShort();
          int payloadLength = input.readUnsignedShort();
          assertEquals(9, payloadLength);
          assertEquals(ControlChannel.OPCODE_HELLO, input.readUnsignedByte());
          input.readInt();
          input.readInt();
        }
        catch (Throwable ex)
        {
          peerFailure.set(ex);
        }
      }, "closing-tcp-control-peer"); //$NON-NLS-1$
      peer.setDaemon(true);
      peer.start();

      try
      {
        ITCPConnector connector = (ITCPConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE,
            "127.0.0.1:" + server.getLocalPort(), false); //$NON-NLS-1$
        LifecycleUtil.activate(connector);
        try
        {
          try
          {
            connector.waitForConnection(10000);
            fail("A peer that closed before HELLO unexpectedly connected"); //$NON-NLS-1$
          }
          catch (ConnectorException expected)
          {
            IncompatibleControlProtocolException incompatibility = findIncompatibleCause(expected);
            assertNotNull(incompatibility);
            assertTrue(incompatibility.getMessage().contains("remote version is unknown")); //$NON-NLS-1$
            assertNotNull("The transport close cause was lost", incompatibility.getCause()); //$NON-NLS-1$
          }

          peer.join(TimeUnit.SECONDS.toMillis(5));
          assertFalse("Peer remained blocked reading HELLO", peer.isAlive()); //$NON-NLS-1$
          assertNull(peerFailure.get());
        }
        finally
        {
          LifecycleUtil.deactivate(connector);
        }
      }
      finally
      {
        LifecycleUtil.deactivate(container);
      }
    }
  }

  public void testConnectPreservesIncompatibleRemoteVersion() throws Exception
  {
    try (ServerSocket server = new ServerSocket(0))
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      AtomicReference<Throwable> peerFailure = new AtomicReference<>();
      CountDownLatch incompatibleHelloSent = new CountDownLatch(1);
      Thread peer = new Thread(() -> {
        try (Socket socket = server.accept())
        {
          socket.setSoTimeout(5000);
          DataInputStream input = new DataInputStream(socket.getInputStream());
          assertEquals(0, input.readUnsignedShort());
          assertEquals(9, input.readUnsignedShort());
          assertEquals(ControlChannel.OPCODE_HELLO, input.readUnsignedByte());
          assertEquals(0x45494B45, input.readInt());
          assertEquals(1, input.readInt());

          DataOutputStream output = new DataOutputStream(socket.getOutputStream());
          output.writeShort(0);
          output.writeShort(9);
          output.writeByte(ControlChannel.OPCODE_HELLO);
          output.writeInt(0x45494B45);
          output.writeInt(2);
          output.flush();
          incompatibleHelloSent.countDown();
        }
        catch (Throwable ex)
        {
          peerFailure.set(ex);
          incompatibleHelloSent.countDown();
        }
      }, "incompatible-tcp-control-peer"); //$NON-NLS-1$
      peer.setDaemon(true);
      peer.start();

      try
      {
        ITCPConnector connector = (ITCPConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE,
            "127.0.0.1:" + server.getLocalPort(), false); //$NON-NLS-1$
        try
        {
          connector.connect(10000);
          fail("connect() accepted a peer with a different control protocol version"); //$NON-NLS-1$
        }
        catch (ConnectorException expected)
        {
          IncompatibleControlProtocolException incompatibility = findIncompatibleCause(expected);
          assertNotNull(incompatibility);
          assertTrue(incompatibility.getMessage().contains("local version 1, remote version 2")); //$NON-NLS-1$
        }

        assertTrue("Peer did not send its incompatible HELLO", incompatibleHelloSent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
        assertNull(peerFailure.get());
      }
      finally
      {
        LifecycleUtil.deactivate(container);
        peer.join(TimeUnit.SECONDS.toMillis(5));
      }
    }
  }

  public void testUnexpectedPreHelloOpcodeFailsTheRealTCPConnection() throws Exception
  {
    try (ServerSocket server = new ServerSocket(0))
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      CountDownLatch incompatibilityWarning = new CountDownLatch(1);
      AtomicReference<Throwable> loggedThrowable = new AtomicReference<>();
      AtomicInteger tcpErrorLogs = new AtomicInteger();
      OMLogHandler logHandler = (logger, level, message, throwable) -> {
        if ("org.eclipse.net4j.tcp".equals(logger.getBundle().getBundleID())) //$NON-NLS-1$
        {
          if (level == OMLogger.Level.WARN)
          {
            loggedThrowable.set(throwable);
            incompatibilityWarning.countDown();
          }
          else if (level == OMLogger.Level.ERROR)
          {
            tcpErrorLogs.incrementAndGet();
          }
        }
      };
      OMPlatform.INSTANCE.addLogHandler(logHandler);
      AtomicReference<Throwable> peerFailure = new AtomicReference<>();
      CountDownLatch controlMessageSent = new CountDownLatch(1);
      Thread peer = new Thread(() -> {
        try (Socket socket = server.accept())
        {
          socket.setSoTimeout(5000);
          DataInputStream input = new DataInputStream(socket.getInputStream());
          int channelID = input.readUnsignedShort();
          int payloadLength = input.readUnsignedShort();
          int opcode = input.readUnsignedByte();
          assertEquals(0, channelID);
          assertEquals(9, payloadLength);
          assertEquals(ControlChannel.OPCODE_HELLO, opcode);
          input.readInt();
          input.readInt();

          DataOutputStream output = new DataOutputStream(socket.getOutputStream());
          output.writeShort(0);
          output.writeShort(1);
          output.writeByte(99);
          output.flush();
          controlMessageSent.countDown();
        }
        catch (Throwable ex)
        {
          peerFailure.set(ex);
          controlMessageSent.countDown();
        }
      }, "unexpected-prehello-tcp-peer"); //$NON-NLS-1$
      peer.setDaemon(true);
      peer.start();

      try
      {
        ITCPConnector connector = (ITCPConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE,
            "127.0.0.1:" + server.getLocalPort(), false); //$NON-NLS-1$
        LifecycleUtil.activate(connector);
        try
        {
          try
          {
            connector.waitForConnection(10000);
            fail("A non-HELLO opcode unexpectedly connected before the handshake"); //$NON-NLS-1$
          }
          catch (ConnectorException expected)
          {
            IncompatibleControlProtocolException incompatibility = findIncompatibleCause(expected);
            assertNotNull(incompatibility);
            assertTrue(incompatibility.getMessage().contains("unexpected control opcode 99 before HELLO")); //$NON-NLS-1$
          }

          assertTrue("Peer did not receive HELLO", controlMessageSent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
          assertNull(peerFailure.get());
          assertTrue("Expected incompatibility warning was not logged", incompatibilityWarning.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
          assertNull("Expected incompatibility was logged with a stack trace", loggedThrowable.get()); //$NON-NLS-1$
          assertEquals("Expected incompatibility was logged as an error", 0, tcpErrorLogs.get()); //$NON-NLS-1$
        }
        finally
        {
          LifecycleUtil.deactivate(connector);
        }
      }
      finally
      {
        OMPlatform.INSTANCE.removeLogHandler(logHandler);
        LifecycleUtil.deactivate(container);
        peer.join(TimeUnit.SECONDS.toMillis(5));
      }
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

  /**
   * Counts each buffer's return to the provider to detect duplicate release during reentrant shutdown.
   *
   * @author Eike Stepper
   */
  private static final class TrackingBufferProvider implements IBufferProvider
  {
    private final Map<IBuffer, Integer> retainCounts = new IdentityHashMap<>();

    private int duplicateRetains;

    @Override
    public short getBufferCapacity()
    {
      return (short)128;
    }

    @Override
    public IBuffer provideBuffer()
    {
      return new Buffer(this, getBufferCapacity());
    }

    @Override
    public synchronized void retainBuffer(IBuffer buffer)
    {
      int count = retainCounts.getOrDefault(buffer, 0) + 1;
      retainCounts.put(buffer, count);
      if (count > 1)
      {
        ++duplicateRetains;
      }

    }

    public synchronized int retainedCount()
    {
      return retainCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public synchronized int duplicateRetains()
    {
      return duplicateRetains;
    }
  }
}
