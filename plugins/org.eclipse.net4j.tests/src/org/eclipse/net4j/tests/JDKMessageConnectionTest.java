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
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.ws.WSConnector;
import org.eclipse.net4j.internal.ws.jdk.JDKMessageConnectionFactory;
import org.eclipse.net4j.internal.ws.jdk.JDKWebSocketMessageConnection;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.InternalBuffer;
import org.eclipse.spi.net4j.MessageConnector;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import javax.net.ssl.SSLContext;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests JDK WebSocket message ownership and demand with a controlled transport double.
 *
 * @author Eike Stepper
 */
public class JDKMessageConnectionTest extends AbstractOMTest
{
  public void testOnlyOneBinarySendCanBeInFlight() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    AtomicInteger writable = new AtomicInteger();
    JDKWebSocketMessageConnection connection = createConnection(socket, new RecordingListener(writable));
    IBuffer first = createBuffer();
    IBuffer second = createBuffer();
    AtomicInteger completed = new AtomicInteger();
    AtomicInteger failed = new AtomicInteger();

    assertTrue(connection.send(first, callback(first, completed, failed)));
    assertFalse(connection.send(second, callback(second, completed, failed)));
    assertEquals(1, socket.sendCount);
    assertEquals(BufferState.PUTTING, second.getState());

    socket.completeNext(null);
    assertEquals(1, completed.get());
    assertEquals(0, failed.get());
    assertEquals(1, writable.get());
    assertTrue(connection.send(second, callback(second, completed, failed)));
    socket.completeNext(null);
    assertEquals(2, completed.get());
    assertEquals(0, failed.get());
  }

  public void testAsynchronousSendFailureCompletesCallbackAndCloses() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    JDKWebSocketMessageConnection connection = createConnection(socket, listener);
    IBuffer buffer = createBuffer();
    AtomicInteger completed = new AtomicInteger();
    AtomicInteger failed = new AtomicInteger();

    assertTrue(connection.send(buffer, callback(buffer, completed, failed)));
    socket.completeNext(new IOException("send failed")); //$NON-NLS-1$

    assertEquals(0, completed.get());
    assertEquals(1, failed.get());
    assertEquals(1, listener.closed.get());
    assertTrue(socket.aborted);
  }

  public void testSynchronousSendFailureCompletesAcceptedSendAndTerminates() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    JDKWebSocketMessageConnection connection = createConnection(socket, listener);
    IBuffer accepted = createBuffer();
    AtomicInteger completed = new AtomicInteger();
    AtomicInteger failed = new AtomicInteger();
    socket.synchronousSendFailure = new IllegalStateException("sendBinary rejected synchronously"); //$NON-NLS-1$

    assertTrue(connection.send(accepted, callback(accepted, completed, failed)));
    assertEquals(0, completed.get());
    assertEquals(1, failed.get());
    assertTrue(accepted.getState() == BufferState.RELEASED || accepted.getState() == BufferState.DISPOSED);
    assertEquals(1, listener.closed.get());
    IBuffer afterFailure = createBuffer();
    assertFalse(connection.send(afterFailure, callback(afterFailure, completed, failed)));
    afterFailure.release();
    assertEquals(1, socket.sendCount);
  }

  public void testAbortRacingSynchronousSendFailureDoesNotCompleteTwice() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    socket.blockSend = true;
    socket.synchronousSendFailure = new IllegalStateException("sendBinary failed"); //$NON-NLS-1$
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    JDKWebSocketMessageConnection connection = createConnection(socket, listener);
    IBuffer buffer = createBuffer();
    AtomicInteger completed = new AtomicInteger();
    AtomicInteger failed = new AtomicInteger();
    AtomicReference<Boolean> accepted = new AtomicReference<>();
    Thread sender = new Thread(() -> {
      try
      {
        accepted.set(connection.send(buffer, callback(buffer, completed, failed)));
      }
      catch (Throwable ex)
      {
        accepted.set(Boolean.FALSE);
      }
    }, "jdk-ws-synchronous-send-failure"); //$NON-NLS-1$
    sender.start();
    assertTrue("sendBinary was not entered", socket.sendEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    connection.abort();
    socket.releaseSend.countDown();
    sender.join(5000);

    assertFalse("sendBinary remained blocked", sender.isAlive()); //$NON-NLS-1$
    assertEquals(Boolean.TRUE, accepted.get());
    assertEquals(0, completed.get());
    assertEquals(1, failed.get());
    assertEquals(1, listener.closed.get());
    IBuffer afterAbort = createBuffer();
    assertFalse(connection.send(afterAbort, callback(afterAbort, completed, failed)));
    afterAbort.release();
    connection.abort();
    assertEquals(1, listener.closed.get());
    assertTrue(buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED);
  }

  public void testMessageConnectorReleasesBufferWhenJdkSendBinaryThrows() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    socket.blockSend = true;
    socket.synchronousSendFailure = new IllegalStateException("sendBinary failed synchronously"); //$NON-NLS-1$
    JDKWebSocketMessageConnection provider = new JDKWebSocketMessageConnection(URI.create("ws://localhost/net4j/@test"), Collections.emptyList()); //$NON-NLS-1$
    CountingBufferProvider bufferProvider = new CountingBufferProvider(Net4jUtil.createBufferFactory((short)128));
    CountDownLatch terminalCallback = new CountDownLatch(1);
    AtomicInteger failures = new AtomicInteger();
    IMessageConnection connection = new IMessageConnection()
    {
      @Override
      public void start(Listener listener)
      {
        setListenerUnchecked(provider, listener);
        provider.onOpen(socket.webSocket);
      }

      @Override
      public boolean send(IBuffer buffer, SendCallback callback)
      {
        return provider.send(buffer, new SendCallback()
        {
          @Override
          public void completed()
          {
            callback.completed();
            terminalCallback.countDown();
          }

          @Override
          public void failed(Throwable failure)
          {
            failures.incrementAndGet();
            callback.failed(failure);
            terminalCallback.countDown();
          }
        });
      }

      @Override
      public void request(long messages)
      {
        provider.request(messages);
      }

      @Override
      public void closeGracefully()
      {
        provider.closeGracefully();
      }

      @Override
      public void abort()
      {
        provider.abort();
      }
    };
    WSConnector connector = new WSConnector(URI.create("ws://localhost/net4j/"), "test", "ws", Collections.emptyList()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    connector.getConfig().setBufferProvider(bufferProvider);
    connector.setMessageConnection(connection);
    LifecycleUtil.activate(connector);
    try
    {
      IChannel channel = connector.getControlChannel();
      IBuffer buffer = connector.provideBuffer();
      buffer.startPutting(channel.getID()).put((byte)0x55);
      bufferProvider.track(buffer);
      channel.sendBuffer(buffer);

      assertTrue("JDK sendBinary was not entered", socket.sendEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertFalse("Buffer was released while JDK sendBinary still owned its view", bufferProvider.isReleased()); //$NON-NLS-1$
      LifecycleUtil.deactivate(connector);
      assertFalse("Abort released the buffer before sendBinary returned", bufferProvider.isReleased()); //$NON-NLS-1$
      socket.releaseSend.countDown();

      assertTrue("Accepted send did not deliver its terminal callback", terminalCallback.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("Pinned Net4j buffer was not released", bufferProvider.released.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals(1, socket.synchronousThrowCount.get());
      assertEquals(1, failures.get());
      assertEquals(1, bufferProvider.releaseCount.get());
      assertTrue(buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED);
      assertNull(getInFlight(connector));
      LifecycleUtil.deactivate(connector);
      assertEquals(1, failures.get());
    }
    finally
    {
      socket.releaseSend.countDown();
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testAbortKeepsBorrowedBufferUntilLateSendCompletion() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    JDKWebSocketMessageConnection connection = createConnection(socket, listener);
    IBuffer buffer = createBuffer();
    AtomicInteger completed = new AtomicInteger();
    AtomicInteger failed = new AtomicInteger();

    assertTrue(connection.send(buffer, callback(buffer, completed, failed)));
    connection.abort();
    assertEquals(1, listener.closed.get());
    assertTrue(socket.aborted);
    assertTrue(buffer.getState() != BufferState.RELEASED && buffer.getState() != BufferState.DISPOSED);

    socket.completeNext(null);
    assertEquals(0, completed.get());
    assertEquals(1, failed.get());
    assertTrue(buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED);
  }

  public void testFragmentDemandAndFrameSizeLimit() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    AtomicReference<JDKWebSocketMessageConnection> reference = new AtomicReference<>();
    AtomicReference<ByteBuffer> received = new AtomicReference<>();
    AtomicReference<Runnable> release = new AtomicReference<>();
    AtomicInteger deliveries = new AtomicInteger();
    IMessageConnection.Listener listener = new IMessageConnection.Listener()
    {
      @Override
      public void ready()
      {
        reference.get().request(1);
      }

      @Override
      public void received(ByteBuffer data, Runnable releaseAction)
      {
        received.set(data);
        release.set(releaseAction);
        deliveries.incrementAndGet();
      }

      @Override
      public void writable()
      {
      }

      @Override
      public void closed(Throwable cause)
      {
      }
    };
    JDKWebSocketMessageConnection connection = new JDKWebSocketMessageConnection(URI.create("ws://localhost/net4j/@test"), Collections.emptyList()); //$NON-NLS-1$
    reference.set(connection);
    setListener(connection, listener);
    connection.onOpen(socket.webSocket);

    connection.onBinary(socket.webSocket, ByteBuffer.wrap(new byte[] { 0, 1 }), false);
    assertEquals(2, socket.requestCount);
    connection.onBinary(socket.webSocket, ByteBuffer.wrap(new byte[] { 0, 1, 0x42 }), true);
    assertEquals(1, deliveries.get());
    assertEquals(5, received.get().remaining());
    assertEquals(2, socket.requestCount);
    release.get().run();
    assertEquals(1, deliveries.get());
    assertEquals(2, socket.requestCount);
    connection.request(1);
    assertEquals(3, socket.requestCount);

    FakeWebSocket oversizedSocket = new FakeWebSocket();
    RecordingListener oversizedListener = new RecordingListener(new AtomicInteger());
    JDKWebSocketMessageConnection oversized = createConnection(oversizedSocket, oversizedListener);
    oversized.request(1);
    oversized.onBinary(oversizedSocket.webSocket, ByteBuffer.allocate(Short.MAX_VALUE), false);
    oversized.onBinary(oversizedSocket.webSocket, ByteBuffer.wrap(new byte[] { 1 }), true);
    assertEquals(0, oversizedListener.received.get());
    assertEquals(1, oversizedListener.closed.get());
    assertTrue(oversizedSocket.aborted);
  }

  public void testPingUsesAutomaticPongAndDoesNotReactivateTerminatedConnection() throws Exception
  {
    FakeWebSocket socket = new FakeWebSocket();
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    JDKWebSocketMessageConnection connection = createConnection(socket, listener);
    connection.request(1);
    int requestsBeforePing = socket.requestCount;
    ByteBuffer ping = ByteBuffer.wrap(new byte[] { 1, 2, 3 });

    connection.onPing(socket.webSocket, ping);

    assertEquals(0, socket.sendPongCount);
    assertEquals(requestsBeforePing + 1, socket.requestCount);
    connection.abort();
    int requestsAtTermination = socket.requestCount;
    connection.onPing(socket.webSocket, ping);
    connection.onPong(socket.webSocket, ping);
    assertEquals(requestsAtTermination, socket.requestCount);
    assertEquals(1, listener.closed.get());
  }

  public void testUnsolicitedPongHeartbeatIsSerializedAndCancelled() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      JDKMessageConnectionFactory factory = new JDKMessageConnectionFactory("jdk-heartbeat", (SSLContext)null); //$NON-NLS-1$
      factory.setManagedContainer(container);

      JDKWebSocketMessageConnection connection = (JDKWebSocketMessageConnection)factory.create("ws://localhost/net4j/@test"); //$NON-NLS-1$
      FakeWebSocket socket = new FakeWebSocket();
      RecordingListener listener = new RecordingListener(new AtomicInteger());
      setListener(connection, listener);
      connection.onOpen(socket.webSocket);

      Field heartbeatField = JDKWebSocketMessageConnection.class.getDeclaredField("heartbeatTask"); //$NON-NLS-1$
      heartbeatField.setAccessible(true);
      TimerTask heartbeatTask = (TimerTask)heartbeatField.get(connection);
      assertNotNull(heartbeatTask);

      CompletableFuture<WebSocket> pendingPong = new CompletableFuture<>();
      socket.pongCompletion = pendingPong;
      heartbeatTask.run();
      heartbeatTask.run();
      assertEquals(1, socket.sendPongCount);

      pendingPong.completeExceptionally(new IllegalStateException("A Pong send is already pending")); //$NON-NLS-1$
      assertEquals("A competing automatic Pong must not close the connection", 0, listener.closed.get()); //$NON-NLS-1$

      socket.pongCompletion = CompletableFuture.completedFuture(socket.webSocket);
      heartbeatTask.run();
      assertEquals(2, socket.sendPongCount);

      connection.abort();
      assertFalse("The scheduled heartbeat task must already be cancelled", heartbeatTask.cancel()); //$NON-NLS-1$
      heartbeatTask.run();
      assertEquals("A cancelled heartbeat task must not send again", 2, socket.sendPongCount); //$NON-NLS-1$
      assertEquals(1, listener.closed.get());
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  public void testHeartbeatFallbackIsSharedByDirectConstructionAndFactory() throws Exception
  {
    JDKWebSocketMessageConnection direct = new JDKWebSocketMessageConnection(URI.create("ws://localhost/net4j/@test"), Collections.emptyList()); //$NON-NLS-1$
    JDKMessageConnectionFactory factory = new JDKMessageConnectionFactory("jdk-direct", (SSLContext)null); //$NON-NLS-1$
    JDKWebSocketMessageConnection created = (JDKWebSocketMessageConnection)factory.create("ws://localhost/net4j/@test"); //$NON-NLS-1$
    Field timerField = JDKWebSocketMessageConnection.class.getDeclaredField("heartbeatTimer"); //$NON-NLS-1$
    timerField.setAccessible(true);

    assertNotNull("Direct construction must retain the default heartbeat", timerField.get(direct)); //$NON-NLS-1$
    assertSame("Unmanaged construction paths must share one fallback timer", timerField.get(direct), timerField.get(created)); //$NON-NLS-1$

    FakeWebSocket socket = new FakeWebSocket();
    setListener(direct, new RecordingListener(new AtomicInteger()));
    direct.onOpen(socket.webSocket);
    Field taskField = JDKWebSocketMessageConnection.class.getDeclaredField("heartbeatTask"); //$NON-NLS-1$
    taskField.setAccessible(true);
    assertNotNull("The direct constructor must schedule heartbeat after opening", taskField.get(direct)); //$NON-NLS-1$
    direct.abort();
  }

  public void testGracefulCloseWaitsForOutstandingHeartbeatPong() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      JDKMessageConnectionFactory factory = new JDKMessageConnectionFactory("jdk-heartbeat-close", (SSLContext)null); //$NON-NLS-1$
      factory.setManagedContainer(container);

      JDKWebSocketMessageConnection connection = (JDKWebSocketMessageConnection)factory.create("ws://localhost/net4j/@test"); //$NON-NLS-1$
      FakeWebSocket socket = new FakeWebSocket();
      RecordingListener listener = new RecordingListener(new AtomicInteger());
      setListener(connection, listener);
      connection.onOpen(socket.webSocket);

      Field heartbeatField = JDKWebSocketMessageConnection.class.getDeclaredField("heartbeatTask"); //$NON-NLS-1$
      heartbeatField.setAccessible(true);
      TimerTask heartbeatTask = (TimerTask)heartbeatField.get(connection);
      CompletableFuture<WebSocket> pendingPong = new CompletableFuture<>();
      socket.pongCompletion = pendingPong;
      heartbeatTask.run();

      connection.closeGracefully();
      assertEquals("The close frame must wait until the heartbeat Pong settles", 0, socket.sendCloseCount); //$NON-NLS-1$
      assertFalse("Graceful close must not wait for the Pong future", pendingPong.isDone()); //$NON-NLS-1$

      pendingPong.complete(socket.webSocket);
      assertEquals("The Pong completion must submit one close frame", 1, socket.sendCloseCount); //$NON-NLS-1$
      connection.closeGracefully();
      assertEquals("Repeated graceful close must not submit another close frame", 1, socket.sendCloseCount); //$NON-NLS-1$

      connection.abort();
      assertEquals(1, listener.closed.get());
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  public void testAbortCancelsPendingUpgradeAndLateOpenIsAborted() throws Exception
  {
    CompletableFuture<WebSocket> upgrade = new CompletableFuture<>();
    JDKWebSocketMessageConnection connection = new JDKWebSocketMessageConnection(URI.create("ws://localhost/net4j/@test"), Collections.emptyList()); //$NON-NLS-1$
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    setListener(connection, listener);
    setPendingUpgrade(connection, upgrade);

    connection.abort();
    connection.abort();
    assertTrue(upgrade.isCancelled());
    assertEquals(1, listener.closed.get());

    FakeWebSocket socket = new FakeWebSocket();
    connection.onOpen(socket.webSocket);
    assertTrue(socket.aborted);
    assertFalse(upgrade.complete(socket.webSocket));
    assertEquals(1, listener.closed.get());
  }

  public void testUpgradeFailureAfterAbortDoesNotNotifyTwice() throws Exception
  {
    CompletableFuture<WebSocket> upgrade = new CompletableFuture<>();
    JDKWebSocketMessageConnection connection = new JDKWebSocketMessageConnection(URI.create("ws://localhost/net4j/@test"), Collections.emptyList()); //$NON-NLS-1$
    RecordingListener listener = new RecordingListener(new AtomicInteger());
    setListener(connection, listener);
    setPendingUpgrade(connection, upgrade);
    connection.abort();

    assertTrue(upgrade.isCancelled());
    assertFalse(upgrade.completeExceptionally(new IOException("late failure"))); //$NON-NLS-1$
    connection.onError(null, new IOException("late failure")); //$NON-NLS-1$
    assertEquals(1, listener.closed.get());
  }

  public void testAbortDuringWithheldHandshakeCancelsUpgrade() throws Exception
  {
    try (WithheldUpgradeServer server = new WithheldUpgradeServer())
    {
      JDKWebSocketMessageConnection connection = new JDKWebSocketMessageConnection(URI.create("ws://127.0.0.1:" + server.getPort() + "/net4j/@test"), //$NON-NLS-1$
          Collections.emptyList());
      RecordingListener listener = new RecordingListener(new AtomicInteger());
      connection.start(listener);

      assertTrue("HTTP upgrade request was not received", server.awaitRequest(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      Field upgradeField = JDKWebSocketMessageConnection.class.getDeclaredField("pendingUpgrade"); //$NON-NLS-1$
      upgradeField.setAccessible(true);
      @SuppressWarnings("unchecked")
      CompletableFuture<WebSocket> upgrade = (CompletableFuture<WebSocket>)upgradeField.get(connection);
      assertNotNull(upgrade);

      connection.abort();
      connection.abort();
      assertTrue("upgrade future did not complete exceptionally after cancellation: " + upgrade, upgrade.isCompletedExceptionally()); //$NON-NLS-1$
      assertEquals(1, listener.closed.get());
    }
  }

  private static JDKWebSocketMessageConnection createConnection(FakeWebSocket socket, IMessageConnection.Listener listener) throws Exception
  {
    JDKWebSocketMessageConnection connection = new JDKWebSocketMessageConnection(URI.create("ws://localhost/net4j/@test"), Collections.emptyList()); //$NON-NLS-1$
    setListener(connection, listener);
    connection.onOpen(socket.webSocket);
    return connection;
  }

  private static void setListener(JDKWebSocketMessageConnection connection, IMessageConnection.Listener listener) throws Exception
  {
    Field listenerField = JDKWebSocketMessageConnection.class.getDeclaredField("listener"); //$NON-NLS-1$
    listenerField.setAccessible(true);
    listenerField.set(connection, listener);
  }

  private static void setListenerUnchecked(JDKWebSocketMessageConnection connection, IMessageConnection.Listener listener)
  {
    try
    {
      setListener(connection, listener);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  private static Object getInFlight(MessageConnector connector) throws Exception
  {
    Field field = MessageConnector.class.getDeclaredField("inFlight"); //$NON-NLS-1$
    field.setAccessible(true);
    return field.get(connector);
  }

  private static void setPendingUpgrade(JDKWebSocketMessageConnection connection, CompletableFuture<WebSocket> upgrade) throws Exception
  {
    Field upgradeField = JDKWebSocketMessageConnection.class.getDeclaredField("pendingUpgrade"); //$NON-NLS-1$
    upgradeField.setAccessible(true);
    upgradeField.set(connection, upgrade);
  }

  private static IBuffer createBuffer()
  {
    IBufferProvider provider = Net4jUtil.createBufferFactory((short)128);
    IBuffer buffer = provider.provideBuffer();
    buffer.startPutting((short)1).put((byte)0x44);
    return buffer;
  }

  private static IMessageConnection.SendCallback callback(IBuffer buffer, AtomicInteger completed, AtomicInteger failed)
  {
    return new IMessageConnection.SendCallback()
    {
      @Override
      public void completed()
      {
        completed.incrementAndGet();
        buffer.release();
      }

      @Override
      public void failed(Throwable failure)
      {
        failed.incrementAndGet();
        buffer.release();
      }
    };
  }

  /**
   * Records callbacks from a message connection.
   *
   * @author Eike Stepper
   */
  private static final class RecordingListener implements IMessageConnection.Listener
  {
    private final AtomicInteger writable;

    private final AtomicInteger closed = new AtomicInteger();

    private final AtomicInteger received = new AtomicInteger();

    private RecordingListener(AtomicInteger writable)
    {
      this.writable = writable;
    }

    @Override
    public void ready()
    {
    }

    @Override
    public void received(ByteBuffer data, Runnable releaseAction)
    {
      received.incrementAndGet();
      releaseAction.run();
    }

    @Override
    public void writable()
    {
      writable.incrementAndGet();
    }

    @Override
    public void closed(Throwable cause)
    {
      closed.incrementAndGet();
    }
  }

  /**
   * Counts the final release of a selected connector buffer.
   *
   * @author Eike Stepper
   */
  private static final class CountingBufferProvider implements IBufferProvider
  {
    private final IBufferProvider delegate;

    private final CountDownLatch released = new CountDownLatch(1);

    private final AtomicInteger releaseCount = new AtomicInteger();

    private volatile IBuffer trackedBuffer;

    private CountingBufferProvider(IBufferProvider delegate)
    {
      this.delegate = delegate;
    }

    private void track(IBuffer buffer)
    {
      trackedBuffer = buffer;
    }

    private boolean isReleased()
    {
      return trackedBuffer.getState() == BufferState.RELEASED || trackedBuffer.getState() == BufferState.DISPOSED;
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
      ((InternalBuffer)buffer).setBufferProvider(this);
      return buffer;
    }

    @Override
    public void retainBuffer(IBuffer buffer)
    {
      if (buffer == trackedBuffer)
      {
        releaseCount.incrementAndGet();
        released.countDown();
      }

      delegate.retainBuffer(buffer);
    }
  }

  /**
   * Holds asynchronous WebSocket operations until a test completes them.
   *
   * @author Eike Stepper
   */
  private static final class FakeWebSocket implements InvocationHandler
  {
    private final Queue<CompletableFuture<WebSocket>> sends = new ArrayDeque<>();

    private final WebSocket webSocket = (WebSocket)Proxy.newProxyInstance(WebSocket.class.getClassLoader(), new Class<?>[] { WebSocket.class }, this);

    private int sendCount;

    private int requestCount;

    private int sendPongCount;

    private int sendCloseCount;

    private CompletableFuture<WebSocket> pongCompletion;

    private final AtomicInteger synchronousThrowCount = new AtomicInteger();

    private boolean aborted;

    private RuntimeException synchronousSendFailure;

    private boolean blockSend;

    private final CountDownLatch sendEntered = new CountDownLatch(1);

    private final CountDownLatch releaseSend = new CountDownLatch(1);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
      switch (method.getName())
      {
      case "sendBinary": //$NON-NLS-1$
        ++sendCount;
        if (blockSend)
        {
          sendEntered.countDown();
          try
          {
            releaseSend.await();
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
          }
        }

        if (synchronousSendFailure != null)
        {
          RuntimeException failure = synchronousSendFailure;
          synchronousSendFailure = null;
          synchronousThrowCount.incrementAndGet();
          throw failure;
        }

        CompletableFuture<WebSocket> send = new CompletableFuture<>();
        sends.add(send);
        return send;

      case "request": //$NON-NLS-1$
        requestCount += (Long)args[0];
        return null;

      case "sendPong": //$NON-NLS-1$
        ++sendPongCount;
        return pongCompletion == null ? CompletableFuture.completedFuture(webSocket) : pongCompletion;

      case "abort": //$NON-NLS-1$
        aborted = true;
        return null;

      case "sendClose": //$NON-NLS-1$
        ++sendCloseCount;
        return CompletableFuture.completedFuture(webSocket);

      case "getSubprotocol": //$NON-NLS-1$
        return ""; //$NON-NLS-1$

      case "isInputClosed": //$NON-NLS-1$
      case "isOutputClosed": //$NON-NLS-1$
        return false;

      case "toString": //$NON-NLS-1$
        return "FakeWebSocket"; //$NON-NLS-1$

      default:
        throw new UnsupportedOperationException(method.getName());
      }
    }

    private void completeNext(Throwable failure)
    {
      CompletableFuture<WebSocket> send = sends.remove();
      if (failure == null)
      {
        send.complete(webSocket);
      }
      else
      {
        send.completeExceptionally(failure);
      }
    }
  }

  /**
   * Accepts an HTTP WebSocket upgrade request but withholds its response.
   *
   * @author Eike Stepper
   */
  private static final class WithheldUpgradeServer implements AutoCloseable
  {
    private final ServerSocket serverSocket;

    private final CountDownLatch requestReceived = new CountDownLatch(1);

    private WithheldUpgradeServer() throws IOException
    {
      serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
      Thread thread = new Thread(this::serve, "jdk-ws-withheld-upgrade-test"); //$NON-NLS-1$
      thread.setDaemon(true);
      thread.start();
    }

    private int getPort()
    {
      return serverSocket.getLocalPort();
    }

    private boolean awaitRequest(long timeout, TimeUnit unit) throws InterruptedException
    {
      return requestReceived.await(timeout, unit);
    }

    private void serve()
    {
      try (Socket socket = serverSocket.accept())
      {
        socket.setSoTimeout(5000);
        BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
        ByteArrayOutputStream request = new ByteArrayOutputStream();
        int previous = 0;
        int value;
        while ((value = input.read()) >= 0)
        {
          request.write(value);
          byte[] bytes = request.toByteArray();
          int length = bytes.length;
          if (length >= 4 && previous == '\r' && value == '\n' && bytes[length - 2] == '\r' && bytes[length - 1] == '\n')
          {
            break;
          }

          previous = value;
        }

        requestReceived.countDown();
        input.read();
      }
      catch (IOException ex)
      {
        requestReceived.countDown();
      }
    }

    @Override
    public void close() throws IOException
    {
      serverSocket.close();
    }
  }
}
