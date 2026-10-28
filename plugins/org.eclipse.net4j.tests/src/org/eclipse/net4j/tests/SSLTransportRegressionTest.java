/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnector;
import org.eclipse.net4j.internal.tcp.ssl.SSLEngineManager;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite.ExcludedConfig;
import org.eclipse.net4j.tests.config.TestConfig;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Covers TLS 1.3 connection setup, channel registration, application traffic, and shutdown.
 *
 * @author Eike Stepper
 */
@ExcludedConfig({ JVM.class, TCP.class, WS.class, WSS.class })
public class SSLTransportRegressionTest extends AbstractConfigTest
{
  private String previousProtocol;

  @Override
  protected TestConfig.Factory getDefaultFactory()
  {
    return new TestConfig.SSL();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    previousProtocol = SSLUtil.getDefaultProtocol();
    SSLUtil.setDefaultProtocol("TLSv1.3");
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    try
    {
      super.doTearDown();
    }
    finally
    {
      SSLUtil.setDefaultProtocol(previousProtocol);
    }
  }

  public void testTLS13ChannelRegistrationAndDataTransfer() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      getConnector().waitForConnection(DEFAULT_TIMEOUT);

      SSLConnector sslConnector = (SSLConnector)getConnector();
      assertNoTimeout(this::hasServerConnector);
      assertNoTimeout(() -> sslConnector.getSelectionKey() != null && sslConnector.getSelectionKey().isValid()
          && (sslConnector.getSelectionKey().interestOps() & SelectionKey.OP_READ) != 0);
      assertNoTimeout(() -> isHandshakeComplete(sslConnector));

      SSLConnector serverConnector = (SSLConnector)getServerConnector();
      assertNoTimeout(() -> isHandshakeComplete(serverConnector));

      protocol = new TestSignalProtocol(getConnector());
      int value = 0x5A17;
      assertEquals(value, new IntRequest(protocol, value).send().intValue());
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testChannelRegistrationImmediatelyAfterConnection() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      getConnector().waitForConnection(DEFAULT_TIMEOUT);

      protocol = new TestSignalProtocol(getConnector());
      int value = 0x5A18;
      assertEquals(value, new IntRequest(protocol, value).send().intValue());
      assertEquals(value + 1, new IntRequest(protocol, value + 1).send().intValue());
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testShutdownDoesNotReactivateConnector() throws Exception
  {
    startTransport();
    getConnector().waitForConnection(DEFAULT_TIMEOUT);

    SSLConnector sslConnector = (SSLConnector)getConnector();
    assertNoTimeout(this::hasServerConnector);
    SSLConnector serverConnector = (SSLConnector)getServerConnector();
    assertNoTimeout(() -> isHandshakeComplete(sslConnector) && isHandshakeComplete(serverConnector));

    SSLEngineManager serverManager = getEngineManager(serverConnector);
    byte[] pendingRecord = wrap(serverManager, "pending-shutdown-record"); //$NON-NLS-1$
    assertTrue(pendingRecord.length > 1);
    ByteBuffer packetRecvBuf = getEngineManager(sslConnector).getPacketRecvBuf();
    packetRecvBuf.clear();
    packetRecvBuf.put(pendingRecord, 0, pendingRecord.length / 2);

    assertNotNull(sslConnector.getSelectionKey());
    SelectionKey selectionKey = sslConnector.getSelectionKey();

    sslConnector.close();
    assertNoTimeout(() -> sslConnector.getState() == ConnectorState.DISCONNECTED);
    assertNoTimeout(
        () -> !selectionKey.isValid() || (selectionKey.interestOps() & (SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT)) == 0);
  }

  public void testPartialTLSRecordWaitsForRemainingBytes() throws Exception
  {
    startTransport();
    getConnector().waitForConnection(DEFAULT_TIMEOUT);

    SSLConnector clientConnector = (SSLConnector)getConnector();
    assertNoTimeout(this::hasServerConnector);
    SSLConnector serverConnector = (SSLConnector)getServerConnector();
    assertNoTimeout(() -> isHandshakeComplete(clientConnector) && isHandshakeComplete(serverConnector));

    SSLEngineManager clientManager = getEngineManager(clientConnector);
    SSLEngineManager serverManager = getEngineManager(serverConnector);
    SelectionKey serverKey = serverConnector.getSelectionKey();
    byte[] record = wrap(clientManager, "partial-tls-record"); //$NON-NLS-1$
    int split = record.length / 2;
    assertTrue(split > 0);

    assertNoTimeout(() -> !serverManager.hasBufferedReceiveData());
    synchronized (serverManager)
    {
      assertFalse(serverManager.hasBufferedReceiveData());
      serverKey.interestOps(0);
      writeFully(clientConnector, record, 0, split);

      assertEquals(0, serverManager.read(serverConnector.getSocketChannel()));
      assertTrue(serverManager.readNeedsMoreNetworkData());
      assertEquals(split, serverManager.getPacketRecvBuf().position());

      writeFully(clientConnector, record, split, record.length - split);
      assertTrue(serverManager.read(serverConnector.getSocketChannel()) > 0);
      assertEquals("partial-tls-record", readApplicationData(serverManager)); //$NON-NLS-1$
      assertFalse(serverManager.readNeedsMoreNetworkData());
    }
  }

  public void testMultipleBufferedTLSRecordsAreProcessedInOrder() throws Exception
  {
    startTransport();
    getConnector().waitForConnection(DEFAULT_TIMEOUT);

    SSLConnector clientConnector = (SSLConnector)getConnector();
    assertNoTimeout(this::hasServerConnector);
    SSLConnector serverConnector = (SSLConnector)getServerConnector();
    assertNoTimeout(() -> isHandshakeComplete(clientConnector) && isHandshakeComplete(serverConnector));

    SSLEngineManager clientManager = getEngineManager(clientConnector);
    SSLEngineManager serverManager = getEngineManager(serverConnector);
    SelectionKey serverKey = serverConnector.getSelectionKey();

    assertNoTimeout(() -> !serverManager.hasBufferedReceiveData());
    synchronized (serverManager)
    {
      assertFalse(serverManager.hasBufferedReceiveData());
      serverKey.interestOps(0);
      byte[] firstRecord = wrap(clientManager, "first-buffered-record"); //$NON-NLS-1$
      byte[] secondRecord = wrap(clientManager, "second-buffered-record"); //$NON-NLS-1$
      ByteBuffer packetRecvBuf = serverManager.getPacketRecvBuf();
      packetRecvBuf.put(firstRecord);
      packetRecvBuf.put(secondRecord);

      assertTrue(serverManager.read(serverConnector.getSocketChannel()) > 0);
      assertEquals("first-buffered-record", readApplicationData(serverManager)); //$NON-NLS-1$
      assertTrue(packetRecvBuf.position() > 0);

      assertTrue(serverManager.read(serverConnector.getSocketChannel()) > 0);
      assertEquals("second-buffered-record", readApplicationData(serverManager)); //$NON-NLS-1$
      assertEquals(0, packetRecvBuf.position());
    }
  }

  public void testPendingHandshakeOutputIsFlushedBeforeCompletion() throws Exception
  {
    SSLEngineManager manager = new SSLEngineManager(true, "localhost", 0, null); //$NON-NLS-1$
    IntermittentWriteSocketChannel socketChannel = new IntermittentWriteSocketChannel(1);
    try
    {
      byte[] pendingOutput = new byte[] { 1, 2, 3, 4 };
      ByteBuffer packetSendBuf = manager.getPacketSendBuf();
      packetSendBuf.clear();
      packetSendBuf.put(pendingOutput);

      setManagerField(manager, "handShakeComplete", false); //$NON-NLS-1$
      setManagerField(manager, "handShakeStatus", SSLEngineResult.HandshakeStatus.FINISHED); //$NON-NLS-1$
      setManagerField(manager, "needRehandShake", true); //$NON-NLS-1$
      setManagerField(manager, "handShakeTimeOut", 3); //$NON-NLS-1$
      setManagerField(manager, "handShakeWaitTime", 1); //$NON-NLS-1$

      manager.checkInitialHandshake(socketChannel);

      assertTrue(manager.isHandshakeComplete());
      assertEquals(0, packetSendBuf.position());
      assertEquals(2, socketChannel.getWriteAttempts());
      assertEquals(ByteBuffer.wrap(pendingOutput), ByteBuffer.wrap(socketChannel.getWrittenBytes()));
    }
    finally
    {
      manager.close();
      socketChannel.close();
    }
  }

  public void testPendingHandshakeOutputTimesOutWithoutLosingBytes() throws Exception
  {
    SSLEngineManager manager = new SSLEngineManager(true, "localhost", 0, null); //$NON-NLS-1$
    IntermittentWriteSocketChannel socketChannel = new IntermittentWriteSocketChannel(Integer.MAX_VALUE);
    try
    {
      byte[] pendingOutput = new byte[] { 5, 6, 7, 8 };
      ByteBuffer packetSendBuf = manager.getPacketSendBuf();
      packetSendBuf.clear();
      packetSendBuf.put(pendingOutput);

      setManagerField(manager, "handShakeComplete", false); //$NON-NLS-1$
      setManagerField(manager, "handShakeStatus", SSLEngineResult.HandshakeStatus.FINISHED); //$NON-NLS-1$
      setManagerField(manager, "needRehandShake", true); //$NON-NLS-1$
      setManagerField(manager, "handShakeTimeOut", 2); //$NON-NLS-1$
      setManagerField(manager, "handShakeWaitTime", 1); //$NON-NLS-1$

      try
      {
        manager.checkInitialHandshake(socketChannel);
        fail("Expected a TLS handshake timeout"); //$NON-NLS-1$
      }
      catch (SSLException ex)
      {
        assertEquals("SSL handshake timeout", ex.getMessage()); //$NON-NLS-1$
      }

      assertFalse(manager.isHandshakeComplete());
      assertEquals(2, socketChannel.getWriteAttempts());
      assertEquals(ByteBuffer.wrap(pendingOutput), packetSendBuf.flip());
    }
    finally
    {
      manager.close();
      socketChannel.close();
    }
  }

  public void testHandshakePacketBufferOverflowRecoversAndStallsAreBounded() throws Exception
  {
    SSLEngineManager manager = new SSLEngineManager(true, "localhost", 0, null); //$NON-NLS-1$
    IntermittentWriteSocketChannel socketChannel = new IntermittentWriteSocketChannel(0);
    try
    {
      manager.setPacketSendBuf(ByteBuffer.allocate(1));
      setManagerField(manager, "handShakeTimeOut", 2); //$NON-NLS-1$
      setManagerField(manager, "handShakeWaitTime", 1); //$NON-NLS-1$

      try
      {
        manager.checkInitialHandshake(socketChannel);
        fail("Expected a TLS handshake timeout after sending the handshake flight"); //$NON-NLS-1$
      }
      catch (SSLException ex)
      {
        assertEquals("SSL handshake timeout", ex.getMessage()); //$NON-NLS-1$
      }

      assertTrue(manager.getPacketSendBuf().capacity() > 1);
      assertTrue(socketChannel.getWrittenBytes().length > 0);
      assertEquals(2, socketChannel.getReadAttempts());
      assertFalse(manager.isHandshakeComplete());
    }
    finally
    {
      manager.close();
      socketChannel.close();
    }
  }

  private static boolean isHandshakeComplete(SSLConnector connector)
  {
    try
    {
      return getEngineManager(connector).isHandshakeComplete();
    }
    catch (ReflectiveOperationException ex)
    {
      throw new AssertionError(ex);
    }
  }

  private static SSLEngineManager getEngineManager(SSLConnector connector) throws ReflectiveOperationException
  {
    Field field = SSLConnector.class.getDeclaredField("sslEngineManager"); //$NON-NLS-1$
    field.setAccessible(true);
    return (SSLEngineManager)field.get(connector);
  }

  private static void setManagerField(SSLEngineManager manager, String name, Object value) throws ReflectiveOperationException
  {
    Field field = SSLEngineManager.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(manager, value);
  }

  private boolean hasServerConnector()
  {
    try
    {
      return getServerConnector() != null;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      return false;
    }
  }

  private static byte[] wrap(SSLEngineManager manager, String text) throws Exception
  {
    synchronized (manager)
    {
      Field field = SSLEngineManager.class.getDeclaredField("sslEngine"); //$NON-NLS-1$
      field.setAccessible(true);
      SSLEngine engine = (SSLEngine)field.get(manager);
      ByteBuffer source = ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
      ByteBuffer destination = ByteBuffer.allocate(engine.getSession().getPacketBufferSize());
      SSLEngineResult result = engine.wrap(source, destination);
      assertEquals(SSLEngineResult.Status.OK, result.getStatus());
      assertFalse(source.hasRemaining());
      destination.flip();
      byte[] record = new byte[destination.remaining()];
      destination.get(record);
      return record;
    }
  }

  private static void writeFully(SSLConnector connector, byte[] record, int offset, int length) throws Exception
  {
    ByteBuffer buffer = ByteBuffer.wrap(record, offset, length);
    long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT);

    while (buffer.hasRemaining())
    {
      if (connector.getSocketChannel().write(buffer) == 0)
      {
        if (System.nanoTime() >= deadline)
        {
          throw new AssertionError("Timed out writing a TLS record fragment"); //$NON-NLS-1$
        }

        Thread.sleep(1);
      }
    }
  }

  private static String readApplicationData(SSLEngineManager manager)
  {
    ByteBuffer appRecvBuf = manager.getAppRecvBuf();
    appRecvBuf.flip();
    byte[] data = new byte[appRecvBuf.remaining()];
    appRecvBuf.get(data);
    appRecvBuf.clear();
    return new String(data, StandardCharsets.UTF_8);
  }

  /**
   * Returns zero on the first write and captures later writes for the handshake liveness test.
   *
   * @author Eike Stepper
   */
  private static class IntermittentWriteSocketChannel extends SocketChannel
  {
    private final ByteArrayOutputStream writtenBytes = new ByteArrayOutputStream();

    private final int blockedWriteAttempts;

    private int writeAttempts;

    private int readAttempts;

    IntermittentWriteSocketChannel(int blockedWriteAttempts)
    {
      super(SelectorProvider.provider());
      this.blockedWriteAttempts = blockedWriteAttempts;
    }

    byte[] getWrittenBytes()
    {
      return writtenBytes.toByteArray();
    }

    int getWriteAttempts()
    {
      return writeAttempts;
    }

    int getReadAttempts()
    {
      return readAttempts;
    }

    @Override
    public int write(ByteBuffer source)
    {
      ++writeAttempts;
      if (writeAttempts <= blockedWriteAttempts)
      {
        return 0;
      }

      int count = source.remaining();
      byte[] bytes = new byte[count];
      source.get(bytes);
      writtenBytes.write(bytes, 0, bytes.length);
      return count;
    }

    @Override
    protected void implCloseSelectableChannel()
    {
    }

    @Override
    protected void implConfigureBlocking(boolean block)
    {
    }

    @Override
    public SocketChannel bind(SocketAddress local)
    {
      return this;
    }

    @Override
    public <T> SocketChannel setOption(SocketOption<T> name, T value)
    {
      return this;
    }

    @Override
    public SocketChannel shutdownInput()
    {
      return this;
    }

    @Override
    public SocketChannel shutdownOutput()
    {
      return this;
    }

    @Override
    public Socket socket()
    {
      return new Socket();
    }

    @Override
    public boolean isConnected()
    {
      return true;
    }

    @Override
    public boolean isConnectionPending()
    {
      return false;
    }

    @Override
    public boolean connect(SocketAddress remote)
    {
      return true;
    }

    @Override
    public boolean finishConnect()
    {
      return true;
    }

    @Override
    public SocketAddress getRemoteAddress()
    {
      return null;
    }

    @Override
    public SocketAddress getLocalAddress()
    {
      return null;
    }

    @Override
    public int read(ByteBuffer destination)
    {
      ++readAttempts;
      return 0;
    }

    @Override
    public long read(ByteBuffer[] destinations, int offset, int length)
    {
      return 0;
    }

    @Override
    public long write(ByteBuffer[] sources, int offset, int length)
    {
      long count = 0;
      for (int i = offset; i < offset + length; i++)
      {
        count += write(sources[i]);
      }

      return count;
    }

    @Override
    public <T> T getOption(SocketOption<T> name)
    {
      return null;
    }

    @Override
    public Set<SocketOption<?>> supportedOptions()
    {
      return Collections.emptySet();
    }
  }

}
