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
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises the Jetty 12 WebSocket adapter with an operator-managed server and the JDK WebSocket client.
 *
 * @author Eike Stepper
 */
public class JettyServerAdapterTest extends AbstractOMTest
{
  public void testInboundPayloadRemainsZeroCopyUntilNet4jBufferRelease()
  {
    ControlledSession controlled = new ControlledSession();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    AtomicInteger receivedCount = new AtomicInteger();
    AtomicInteger callbackCount = new AtomicInteger();
    AtomicReference<Throwable> callbackFailure = new AtomicReference<>();
    AtomicReference<ByteBuffer> firstMessage = new AtomicReference<>();
    AtomicReference<ByteBuffer> secondMessage = new AtomicReference<>();
    AtomicReference<Runnable> firstRelease = new AtomicReference<>();
    AtomicReference<Runnable> secondRelease = new AtomicReference<>();

    connection.start(new IMessageConnection.Listener()
    {
      @Override
      public void ready()
      {
      }

      @Override
      public void received(ByteBuffer data, Runnable releaseAction)
      {
        if (receivedCount.incrementAndGet() == 1)
        {
          firstMessage.set(data);
          firstRelease.set(releaseAction);
        }
        else
        {
          secondMessage.set(data);
          secondRelease.set(releaseAction);
        }
      }

      @Override
      public void writable()
      {
      }

      @Override
      public void closed(Throwable cause)
      {
      }
    });
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);
    connection.request(1);

    byte[] firstPayload = { 11, 12 };
    ((Session.Listener)connection).onWebSocketBinary(ByteBuffer.wrap(firstPayload), Callback.from(callbackCount::incrementAndGet, callbackFailure::set));

    assertEquals("Jetty must retain its payload until Net4j releases the buffer", 0, callbackCount.get()); //$NON-NLS-1$
    assertEquals(1, receivedCount.get());
    firstPayload[0] = 99;
    assertEquals("The Net4j view must share the Jetty payload storage", 99, firstMessage.get().get(0)); //$NON-NLS-1$

    connection.request(1);
    assertEquals("Further Jetty demand must not wait for the first Net4j buffer release", 2, controlled.demandCount); //$NON-NLS-1$
    byte[] secondPayload = { 21, 22 };
    ((Session.Listener)connection).onWebSocketBinary(ByteBuffer.wrap(secondPayload), Callback.from(callbackCount::incrementAndGet, callbackFailure::set));

    assertEquals("A second message must be delivered while Net4j retains the first buffer", 2, receivedCount.get()); //$NON-NLS-1$
    assertEquals(0, callbackCount.get());
    assertNull(callbackFailure.get());
    assertEquals(21, secondMessage.get().get(0));

    firstRelease.get().run();
    assertEquals(1, callbackCount.get());
    secondRelease.get().run();
    assertEquals(2, callbackCount.get());
    connection.abort();
  }

  public void testControlFramesReplenishJettyDemandWithoutConsumingNet4jCredit()
  {
    ControlledSession controlled = new ControlledSession();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    AtomicInteger receivedCount = new AtomicInteger();
    AtomicInteger binaryCallbackCount = new AtomicInteger();
    connection.start(new IMessageConnection.Listener()
    {
      @Override
      public void ready()
      {
      }

      @Override
      public void received(ByteBuffer data, Runnable releaseAction)
      {
        receivedCount.incrementAndGet();
        releaseAction.run();
      }

      @Override
      public void writable()
      {
      }

      @Override
      public void closed(Throwable cause)
      {
      }
    });

    try
    {
      Session.Listener jettyListener = (Session.Listener)connection;
      jettyListener.onWebSocketOpen(controlled.session);
      connection.request(1);
      assertEquals(1, controlled.demandCount);

      jettyListener.onWebSocketPong(ByteBuffer.allocate(0));
      jettyListener.onWebSocketPong(ByteBuffer.allocate(0));
      jettyListener.onWebSocketPing(ByteBuffer.allocate(0));
      assertEquals("Each control frame must consume and replenish Jetty's callback demand", 4, controlled.demandCount); //$NON-NLS-1$
      assertEquals("Control frames must not be delivered as Net4j binary messages", 0, receivedCount.get()); //$NON-NLS-1$

      jettyListener.onWebSocketBinary(ByteBuffer.wrap(new byte[] { 0x01 }), Callback.from(binaryCallbackCount::incrementAndGet, failure -> {
        fail(failure.toString());
      }));
      assertEquals(1, receivedCount.get());
      assertEquals("Binary delivery consumes the Net4j credit and leaves no further normal demand", 4, controlled.demandCount); //$NON-NLS-1$
      assertEquals(1, binaryCallbackCount.get());

      connection.closeGracefully();
      assertEquals(1, controlled.closeCount);
      assertEquals("Graceful close must retain one demand for close progress", 5, controlled.demandCount); //$NON-NLS-1$

      jettyListener.onWebSocketPong(ByteBuffer.allocate(0));
      assertEquals("A Pong during graceful close must rearm close-progress demand", 6, controlled.demandCount); //$NON-NLS-1$
    }
    finally
    {
      connection.abort();
    }
  }

  public void testGracefulCloseAdmitsPreviouslyDemandedBinaryCallback()
  {
    ControlledSession controlled = new ControlledSession();
    AtomicInteger receivedCount = new AtomicInteger();
    AtomicInteger callbackSuccessCount = new AtomicInteger();
    AtomicReference<Throwable> callbackFailure = new AtomicReference<>();
    AtomicReference<Throwable> closedCause = new AtomicReference<>();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new IMessageConnection.Listener()
    {
      @Override
      public void ready()
      {
      }

      @Override
      public void received(ByteBuffer data, Runnable releaseAction)
      {
        receivedCount.incrementAndGet();
        releaseAction.run();
      }

      @Override
      public void writable()
      {
      }

      @Override
      public void closed(Throwable cause)
      {
        closedCause.set(cause);
      }
    });

    try
    {
      ((Session.Listener)connection).onWebSocketOpen(controlled.session);
      connection.request(1);
      assertEquals("A Net4j receive credit must issue one Jetty demand", 1, controlled.demandCount); //$NON-NLS-1$

      connection.closeGracefully();
      assertEquals(1, controlled.closeCount);

      ((Session.Listener)connection).onWebSocketBinary(ByteBuffer.wrap(new byte[] { 0x01 }),
          Callback.from(callbackSuccessCount::incrementAndGet, callbackFailure::set));

      assertEquals("The binary callback already covered by receive demand must be admitted", 1, receivedCount.get()); //$NON-NLS-1$
      assertEquals(1, callbackSuccessCount.get());
      assertEquals("An admitted binary callback must leave Jetty demand available for the close response", 2, controlled.demandCount); //$NON-NLS-1$
      assertNull(callbackFailure.get());
      assertNull(closedCause.get());
    }
    finally
    {
      connection.abort();
    }
  }

  public void testGracefulCloseRejectsBinaryWithoutNet4jReceiveDemand()
  {
    ControlledSession controlled = new ControlledSession();
    AtomicReference<Throwable> callbackFailure = new AtomicReference<>();
    AtomicReference<Throwable> closedCause = new AtomicReference<>();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener(closedCause, false));

    try
    {
      ((Session.Listener)connection).onWebSocketOpen(controlled.session);
      connection.closeGracefully();
      assertEquals("Graceful close requests Jetty demand to receive the peer close frame", 1, controlled.demandCount); //$NON-NLS-1$

      ((Session.Listener)connection).onWebSocketBinary(ByteBuffer.wrap(new byte[] { 0x01 }), Callback.from(() -> {
      }, callbackFailure::set));

      assertTrue(callbackFailure.get() instanceof IllegalStateException);
      assertEquals("Jetty delivered a binary message without Net4j receive demand", callbackFailure.get().getMessage()); //$NON-NLS-1$
      assertEquals("Unexpected WebSocket message", closedCause.get().getMessage()); //$NON-NLS-1$
    }
    finally
    {
      connection.abort();
    }
  }

  public void testOperatorManagedServerExchangeAndConnectorLifecycle() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    container.registerFactory(new TestSignalProtocol.Factory());

    IWSAcceptor acceptor = WSUtil.getAcceptor(container, "jetty-adapter-test"); //$NON-NLS-1$
    LifecycleUtil.activate(acceptor);

    CountDownLatch connectorAdded = new CountDownLatch(1);
    CountDownLatch connectorRemoved = new CountDownLatch(1);
    AtomicReference<IConnector> acceptedConnector = new AtomicReference<>();
    acceptor.addListener(new ContainerEventAdapter<IConnector>()
    {
      @Override
      protected void onAdded(IContainer<IConnector> source, IConnector element)
      {
        acceptedConnector.set(element);
        connectorAdded.countDown();
      }

      @Override
      protected void onRemoved(IContainer<IConnector> source, IConnector element)
      {
        connectorRemoved.countDown();
      }
    });

    Server server = new Server();
    ServerConnector serverConnector = new ServerConnector(server);
    serverConnector.setHost("127.0.0.1"); //$NON-NLS-1$
    serverConnector.setPort(0);
    server.addConnector(serverConnector);

    ContextHandler context = new ContextHandler("/"); //$NON-NLS-1$
    context.setHandler(JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor)); //$NON-NLS-1$
    server.setHandler(context);

    IWSConnector connector = null;
    TestSignalProtocol protocol = null;
    try
    {
      server.start();
      URI serviceURI = new URI("ws://127.0.0.1:" + serverConnector.getLocalPort() + "/net4j"); //$NON-NLS-1$
      connector = WSUtil.getConnector(container, serviceURI, "jetty-adapter-test"); //$NON-NLS-1$
      LifecycleUtil.activate(connector);
      protocol = new TestSignalProtocol(connector);

      int value = 0x5A17;
      assertEquals(value, new IntRequest(protocol, value).send().intValue());
      assertTrue("Accepted connector was not registered", connectorAdded.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(acceptedConnector.get(), acceptor.getAcceptedConnectors()[0]);

      protocol.close();
      protocol = null;
      LifecycleUtil.deactivate(connector);
      connector = null;
      assertTrue("Accepted connector was not removed", connectorRemoved.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }

      try
      {
        if (connector != null)
        {
          LifecycleUtil.deactivate(connector);
        }
      }
      finally
      {
        try
        {
          server.stop();
        }
        finally
        {
          LifecycleUtil.deactivate(acceptor);
          LifecycleUtil.deactivate(container);
        }
      }
    }
  }

  public void testRejectsInsecureWSSUpgradeBeforeAcceptorAccept() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    IWSAcceptor acceptor = WSSUtil.getAcceptor(container, "jetty-adapter-wss-negative"); //$NON-NLS-1$
    LifecycleUtil.activate(acceptor);

    Server server = createServer();
    ContextHandler context = new ContextHandler("/"); //$NON-NLS-1$
    context.setHandler(JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor)); //$NON-NLS-1$
    server.setHandler(context);
    try
    {
      server.start();
      URI uri = new URI("ws://127.0.0.1:" + ((ServerConnector)server.getConnectors()[0]).getLocalPort() + "/net4j/@jetty-adapter-wss-negative"); //$NON-NLS-1$
      HttpClient.newHttpClient().newWebSocketBuilder().header(IWSConnector.ACCEPTOR_HEADER_NAME, acceptor.getName()).buildAsync(uri, new WebSocket.Listener()
      {
      }).get(5, TimeUnit.SECONDS);
      fail("An insecure WebSocket upgrade reached a WSS acceptor"); //$NON-NLS-1$
    }
    catch (ExecutionException expected)
    {
      assertEquals("No accepted connector may be created for an insecure WSS upgrade", 0, acceptor.getAcceptedConnectors().length); //$NON-NLS-1$
    }
    finally
    {
      try
      {
        server.stop();
      }
      finally
      {
        LifecycleUtil.deactivate(acceptor);
        LifecycleUtil.deactivate(container);
      }
    }
  }

  public void testRejectsPathHeaderMismatchBeforeAcceptorAccept() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    IWSAcceptor acceptor = WSUtil.getAcceptor(container, "jetty-adapter-path-negative"); //$NON-NLS-1$
    IWSAcceptor otherAcceptor = WSUtil.getAcceptor(container, "jetty-adapter-other-context"); //$NON-NLS-1$
    LifecycleUtil.activate(acceptor);
    LifecycleUtil.activate(otherAcceptor);

    Server server = createServer();
    ContextHandler context = new ContextHandler("/"); //$NON-NLS-1$
    context.setHandler(JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor)); //$NON-NLS-1$
    server.setHandler(context);
    try
    {
      server.start();
      URI uri = new URI("ws://127.0.0.1:" + ((ServerConnector)server.getConnectors()[0]).getLocalPort() + "/net4j/@jetty-adapter-other-context"); //$NON-NLS-1$
      try
      {
        HttpClient.newHttpClient().newWebSocketBuilder().header(IWSConnector.ACCEPTOR_HEADER_NAME, otherAcceptor.getName())
            .buildAsync(uri, new WebSocket.Listener()
            {
            }).get(5, TimeUnit.SECONDS);
        fail("A different context's globally registered acceptor was routed through this endpoint"); //$NON-NLS-1$
      }
      catch (ExecutionException expected)
      {
        assertEquals("The endpoint's acceptor must remain untouched", 0, acceptor.getAcceptedConnectors().length); //$NON-NLS-1$
        assertEquals("A globally registered acceptor from another context must remain untouched", 0, otherAcceptor.getAcceptedConnectors().length); //$NON-NLS-1$
      }
    }
    finally
    {
      try
      {
        server.stop();
      }
      finally
      {
        try
        {
          LifecycleUtil.deactivate(otherAcceptor);
        }
        finally
        {
          LifecycleUtil.deactivate(acceptor);
          LifecycleUtil.deactivate(container);
        }
      }
    }
  }

  public void testGracefulCloseWaitsForAcceptedSendAndRejectsNewSends() throws Exception
  {
    ControlledSession controlled = new ControlledSession();
    controlled.blockOpenCheck = true;
    IWSAcceptor acceptor = fakeAcceptor();
    IMessageConnection connection = createAdapter(acceptor);
    connection.start(new TestConnectionListener());
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    AtomicInteger completed = new AtomicInteger();
    IBuffer acceptedBuffer = createBuffer();
    AtomicReference<Boolean> accepted = new AtomicReference<>();
    CountDownLatch sendReturned = new CountDownLatch(1);
    Thread sender = new Thread(() -> {
      accepted.set(connection.send(acceptedBuffer, new RecordingSendCallback(acceptedBuffer, completed)));
      sendReturned.countDown();
    });
    sender.start();
    assertTrue("The send did not reach the controlled session check", controlled.openCheckEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    connection.closeGracefully();
    assertEquals("Graceful close must wait for the accepted send", 0, controlled.closeCount); //$NON-NLS-1$
    IBuffer rejectedBuffer = createBuffer();
    assertFalse("No send may be accepted after graceful close begins", connection.send(rejectedBuffer, new RecordingSendCallback(rejectedBuffer, completed))); //$NON-NLS-1$
    rejectedBuffer.release();

    controlled.releaseOpenCheck.countDown();
    assertTrue("The controlled send did not return", sendReturned.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    sender.join(5000);
    assertEquals(Boolean.TRUE, accepted.get());
    assertEquals(1, controlled.sendCount);

    controlled.sendCallback.succeed();
    controlled.sendCallback.fail(new IOException("duplicate completion")); //$NON-NLS-1$
    assertEquals("The accepted send must receive one terminal callback", 1, completed.get()); //$NON-NLS-1$
    assertEquals("Close starts only after the send callback completes", 1, controlled.closeCount); //$NON-NLS-1$
  }

  public void testAbortFailsWithheldSendAndIgnoresLateJettyCallback()
  {
    ControlledSession controlled = new ControlledSession();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener());
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    IBuffer buffer = createBuffer();
    TrackingSendCallback sendCallback = new TrackingSendCallback(buffer, new AtomicBoolean());
    assertTrue(connection.send(buffer, sendCallback));
    assertEquals(0, sendCallback.terminalCalls.get());
    assertEquals(BufferState.PUTTING, buffer.getState());

    connection.abort();

    assertEquals(1, sendCallback.terminalCalls.get());
    assertEquals(0, sendCallback.completedCalls.get());
    assertEquals(1, sendCallback.failedCalls.get());
    assertTrue(sendCallback.failure.get() instanceof IOException);
    assertTrue(isBufferReleased(buffer));

    controlled.sendCallback.succeed();
    assertEquals(1, sendCallback.terminalCalls.get());
    assertEquals(1, sendCallback.failedCalls.get());
  }

  public void testAbortWithDisconnectFailureStillFailsWithheldSend()
  {
    RuntimeException disconnectFailure = new RuntimeException("disconnect failed"); //$NON-NLS-1$
    ControlledSession controlled = new ControlledSession();
    controlled.disconnectFailure = disconnectFailure;
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener());
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    IBuffer buffer = createBuffer();
    TrackingSendCallback sendCallback = new TrackingSendCallback(buffer, new AtomicBoolean());
    assertTrue(connection.send(buffer, sendCallback));

    connection.abort();

    assertEquals(1, sendCallback.terminalCalls.get());
    assertEquals(1, sendCallback.failedCalls.get());
    assertTrue(sendCallback.failure.get() instanceof IOException);
    assertEquals(1, sendCallback.failure.get().getSuppressed().length);
    assertSame(disconnectFailure, sendCallback.failure.get().getSuppressed()[0]);
    assertTrue(isBufferReleased(buffer));
  }

  public void testAbortDuringBufferCopyDefersCallbackUntilCopyCompletes() throws Exception
  {
    ControlledSession controlled = new ControlledSession();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener());
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    IBuffer buffer = createBuffer();
    AtomicBoolean callbackStarted = new AtomicBoolean();
    BlockingBuffer blockingBuffer = new BlockingBuffer(buffer, callbackStarted);
    TrackingSendCallback sendCallback = new TrackingSendCallback(buffer, callbackStarted);
    AtomicReference<Boolean> accepted = new AtomicReference<>();
    AtomicReference<Throwable> threadFailure = new AtomicReference<>();
    CountDownLatch sendReturned = new CountDownLatch(1);
    Thread sender = new Thread(() -> {
      try
      {
        accepted.set(connection.send(blockingBuffer.buffer, sendCallback));
      }
      catch (Throwable ex)
      {
        threadFailure.set(ex);
      }
      finally
      {
        sendReturned.countDown();
      }
    });
    sender.start();
    assertTrue("The send did not enter its controlled buffer copy", blockingBuffer.copyEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    connection.abort();
    assertEquals("Termination must defer the callback while the adapter reads the buffer", 0, sendCallback.terminalCalls.get()); //$NON-NLS-1$
    assertEquals(BufferState.PUTTING, buffer.getState());

    blockingBuffer.releaseCopy.countDown();
    assertTrue("The admitted send thread did not complete", sendReturned.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    sender.join(5000);
    assertNull(threadFailure.get());
    assertEquals(Boolean.TRUE, accepted.get());
    assertEquals(1, sendCallback.terminalCalls.get());
    assertEquals(1, sendCallback.failedCalls.get());
    assertTrue(sendCallback.bufferReleased.get());
    assertFalse("The adapter accessed the original buffer after its callback", blockingBuffer.accessAfterCallback.get()); //$NON-NLS-1$
  }

  public void testAcceptedSendCompletesWhenSessionOpenCheckThrows()
  {
    RuntimeException openFailure = new RuntimeException("session isOpen failed"); //$NON-NLS-1$
    ControlledSession controlled = new ControlledSession();
    controlled.openFailure = openFailure;
    AtomicReference<Throwable> closedCause = new AtomicReference<>();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener(closedCause, false));
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    IBuffer buffer = createBuffer();
    TrackingSendCallback sendCallback = new TrackingSendCallback(buffer, new AtomicBoolean());
    assertTrue(connection.send(buffer, sendCallback));

    assertEquals(1, sendCallback.terminalCalls.get());
    assertEquals(1, sendCallback.failedCalls.get());
    assertSame(openFailure, sendCallback.failure.get());
    assertSame(openFailure, closedCause.get());
    assertTrue(sendCallback.bufferReleased.get());
  }

  public void testGracefulCloseWaitsForConcurrentSendCallback() throws Exception
  {
    ControlledSession controlled = new ControlledSession();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener());
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    IBuffer buffer = createBuffer();
    BlockingSendCallback sendCallback = new BlockingSendCallback(buffer);
    assertTrue(connection.send(buffer, sendCallback));

    CountDownLatch jettyCallbackReturned = new CountDownLatch(1);
    Thread completion = new Thread(() -> {
      try
      {
        controlled.sendCallback.succeed();
      }
      finally
      {
        jettyCallbackReturned.countDown();
      }
    });
    completion.start();
    assertTrue("The send callback did not enter its controlled completion", sendCallback.callbackEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    connection.closeGracefully();
    assertEquals("Graceful close must wait until the Net4j send callback returns", 0, controlled.closeCount); //$NON-NLS-1$
    sendCallback.releaseCallback.countDown();
    assertTrue("The Jetty callback thread did not complete", jettyCallbackReturned.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    completion.join(5000);

    assertEquals(1, sendCallback.terminalCalls.get());
    assertEquals(1, controlled.closeCount);
    assertTrue(isBufferReleased(buffer));
  }

  public void testTerminationNotifiesListenerWhenJettyCleanupThrows()
  {
    RuntimeException cleanupFailure = new RuntimeException("disconnect failure"); //$NON-NLS-1$
    ControlledSession controlled = new ControlledSession();
    controlled.disconnectFailure = cleanupFailure;
    AtomicReference<Throwable> closedCause = new AtomicReference<>();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener(closedCause, false));
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    connection.abort();

    assertTrue("The original termination cause remains primary", closedCause.get() instanceof IOException); //$NON-NLS-1$
    assertEquals(1, closedCause.get().getSuppressed().length);
    assertSame(cleanupFailure, closedCause.get().getSuppressed()[0]);
  }

  public void testTerminationNotifiesListenerWhenJettyOpenCheckThrows()
  {
    RuntimeException openCheckFailure = new RuntimeException("session state failure"); //$NON-NLS-1$
    ControlledSession controlled = new ControlledSession();
    controlled.openFailure = openCheckFailure;
    AtomicReference<Throwable> closedCause = new AtomicReference<>();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener(closedCause, false));
    ((Session.Listener)connection).onWebSocketOpen(controlled.session);

    connection.abort();

    assertTrue(closedCause.get() instanceof IOException);
    assertEquals(1, closedCause.get().getSuppressed().length);
    assertSame(openCheckFailure, closedCause.get().getSuppressed()[0]);
  }

  public void testJettyCloseCallbackCompletesWhenNet4jNotificationThrows()
  {
    AtomicInteger closeCallbackCount = new AtomicInteger();
    IMessageConnection connection = createAdapter(fakeAcceptor());
    connection.start(new TestConnectionListener(new AtomicReference<>(), true));

    try
    {
      ((Session.Listener)connection).onWebSocketClose(1000, "", Callback.from(closeCallbackCount::incrementAndGet, failure -> {
        // $NON-NLS-0$
      }));
      fail("The listener failure should remain visible to Jetty"); //$NON-NLS-1$
    }
    catch (RuntimeException expected)
    {
      assertEquals(1, closeCallbackCount.get());
    }
  }

  private static Server createServer()
  {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setHost("127.0.0.1"); //$NON-NLS-1$
    connector.setPort(0);
    server.addConnector(connector);
    return server;
  }

  private static IBuffer createBuffer()
  {
    IBufferProvider provider = Net4jUtil.createBufferFactory((short)128);
    IBuffer buffer = provider.provideBuffer();
    buffer.startPutting((short)1).put((byte)0x44);
    return buffer;
  }

  private static boolean isBufferReleased(IBuffer buffer)
  {
    return buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED;
  }

  private static IMessageConnection createAdapter(IWSAcceptor acceptor)
  {
    try
    {
      Class<?> type = Class.forName("org.eclipse.net4j.ws.jetty.internal.server.JettyWebSocketMessageConnection"); //$NON-NLS-1$
      return (IMessageConnection)type.getConstructor(IWSAcceptor.class).newInstance(acceptor);
    }
    catch (ReflectiveOperationException ex)
    {
      throw new AssertionError(ex);
    }
  }

  private static IWSAcceptor fakeAcceptor()
  {
    return (IWSAcceptor)Proxy.newProxyInstance(IWSAcceptor.class.getClassLoader(), new Class<?>[] { IWSAcceptor.class }, (proxy, method, args) -> {
      if (method.getName().equals("getName")) //$NON-NLS-1$
      {
        return "fake"; //$NON-NLS-1$
      }

      if (method.getName().equals("getType")) //$NON-NLS-1$
      {
        return WSUtil.FACTORY_TYPE;
      }

      if (method.getName().equals("toString")) //$NON-NLS-1$
      {
        return "fake acceptor"; //$NON-NLS-1$
      }

      return null;
    });
  }

  /**
   * Supplies the connection callbacks used by the provider-level adapter tests.
   *
   * @author Eike Stepper
   */
  private static final class TestConnectionListener implements IMessageConnection.Listener
  {
    private final AtomicReference<Throwable> closedCause;

    private final boolean throwOnClose;

    private TestConnectionListener()
    {
      this(new AtomicReference<>(), false);
    }

    private TestConnectionListener(AtomicReference<Throwable> closedCause, boolean throwOnClose)
    {
      this.closedCause = closedCause;
      this.throwOnClose = throwOnClose;
    }

    @Override
    public void ready()
    {
    }

    @Override
    public void received(ByteBuffer data, Runnable releaseAction)
    {
      releaseAction.run();
    }

    @Override
    public void writable()
    {
    }

    @Override
    public void closed(Throwable cause)
    {
      closedCause.set(cause);
      if (throwOnClose)
      {
        throw new IllegalStateException("listener failure"); //$NON-NLS-1$
      }
    }
  }

  /**
   * Holds Jetty operations until a test controls their completion.
   *
   * @author Eike Stepper
   */
  private static final class ControlledSession implements InvocationHandler
  {
    private final Session session = (Session)Proxy.newProxyInstance(Session.class.getClassLoader(), new Class<?>[] { Session.class }, this);

    private Callback sendCallback;

    private RuntimeException disconnectFailure;

    private RuntimeException openFailure;

    private boolean blockOpenCheck;

    private final CountDownLatch openCheckEntered = new CountDownLatch(1);

    private final CountDownLatch releaseOpenCheck = new CountDownLatch(1);

    private int sendCount;

    private int closeCount;

    private int demandCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
      switch (method.getName())
      {
      case "isOpen": //$NON-NLS-1$
        if (openFailure != null)
        {
          throw openFailure;
        }

        if (blockOpenCheck)
        {
          openCheckEntered.countDown();
          try
          {
            releaseOpenCheck.await();
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
          }
        }

        return true;

      case "sendBinary": //$NON-NLS-1$
        sendCount++;
        sendCallback = (Callback)args[1];
        return null;

      case "close": //$NON-NLS-1$
        closeCount++;
        ((Callback)args[2]).succeed();
        return null;

      case "disconnect": //$NON-NLS-1$
        if (disconnectFailure != null)
        {
          throw disconnectFailure;
        }
        return null;

      case "demand": //$NON-NLS-1$
        demandCount++;
        return null;

      case "toString": //$NON-NLS-1$
        return null;

      default:
        return method.getReturnType() == boolean.class ? false : method.getReturnType() == int.class ? 0 : null;
      }
    }
  }

  /**
   * Observes callback cardinality and releases the borrowed Net4j buffer from its terminal callback.
   *
   * @author Eike Stepper
   */
  private static final class TrackingSendCallback implements IMessageConnection.SendCallback
  {
    private final IBuffer buffer;

    private final AtomicBoolean callbackStarted;

    private final AtomicInteger terminalCalls = new AtomicInteger();

    private final AtomicInteger completedCalls = new AtomicInteger();

    private final AtomicInteger failedCalls = new AtomicInteger();

    private final AtomicReference<Throwable> failure = new AtomicReference<>();

    private final AtomicBoolean bufferReleased = new AtomicBoolean();

    private TrackingSendCallback(IBuffer buffer, AtomicBoolean callbackStarted)
    {
      this.buffer = buffer;
      this.callbackStarted = callbackStarted;
    }

    @Override
    public void completed()
    {
      callbackStarted.set(true);
      terminalCalls.incrementAndGet();
      completedCalls.incrementAndGet();
      releaseBuffer();
    }

    @Override
    public void failed(Throwable failure)
    {
      callbackStarted.set(true);
      terminalCalls.incrementAndGet();
      failedCalls.incrementAndGet();
      this.failure.set(failure);
      releaseBuffer();
    }

    private void releaseBuffer()
    {
      bufferReleased.set(true);
      buffer.release();
    }
  }

  /**
   * Blocks a terminal success callback so graceful closure can be requested concurrently.
   *
   * @author Eike Stepper
   */
  private static final class BlockingSendCallback implements IMessageConnection.SendCallback
  {
    private final IBuffer buffer;

    private final CountDownLatch callbackEntered = new CountDownLatch(1);

    private final CountDownLatch releaseCallback = new CountDownLatch(1);

    private final AtomicInteger terminalCalls = new AtomicInteger();

    private BlockingSendCallback(IBuffer buffer)
    {
      this.buffer = buffer;
    }

    @Override
    public void completed()
    {
      callbackEntered.countDown();
      try
      {
        releaseCallback.await();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }
      finally
      {
        terminalCalls.incrementAndGet();
        buffer.release();
      }
    }

    @Override
    public void failed(Throwable failure)
    {
      terminalCalls.incrementAndGet();
      buffer.release();
    }
  }

  /**
   * Holds access to the original Net4j buffer until a test releases a controlled copy operation.
   *
   * @author Eike Stepper
   */
  private static final class BlockingBuffer implements InvocationHandler
  {
    private final IBuffer delegate;

    private final AtomicBoolean callbackStarted;

    private final IBuffer buffer = (IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, this);

    private final CountDownLatch copyEntered = new CountDownLatch(1);

    private final CountDownLatch releaseCopy = new CountDownLatch(1);

    private final AtomicBoolean accessAfterCallback = new AtomicBoolean();

    private BlockingBuffer(IBuffer delegate, AtomicBoolean callbackStarted)
    {
      this.delegate = delegate;
      this.callbackStarted = callbackStarted;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      if (callbackStarted.get())
      {
        accessAfterCallback.set(true);
      }

      if (method.getName().equals("getByteBuffer")) //$NON-NLS-1$
      {
        copyEntered.countDown();
        try
        {
          releaseCopy.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      try
      {
        return method.invoke(delegate, args);
      }
      catch (InvocationTargetException ex)
      {
        throw ex.getCause();
      }
    }
  }

  /**
   * Releases each buffer when its accepted send reaches a terminal callback.
   *
   * @author Eike Stepper
   */
  private static final class RecordingSendCallback implements IMessageConnection.SendCallback
  {
    private final IBuffer buffer;

    private final AtomicInteger completed;

    private RecordingSendCallback(IBuffer buffer, AtomicInteger completed)
    {
      this.buffer = buffer;
      this.completed = completed;
    }

    @Override
    public void completed()
    {
      completed.incrementAndGet();
      buffer.release();
    }

    @Override
    public void failed(Throwable failure)
    {
      completed.incrementAndGet();
      buffer.release();
    }
  }
}
