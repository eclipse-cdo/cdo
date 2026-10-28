/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.ILocationAware.Location;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.internal.jvm.JVMAcceptor;
import org.eclipse.net4j.internal.jvm.JVMClientConnector;
import org.eclipse.net4j.internal.ws.WSAcceptor;
import org.eclipse.net4j.internal.ws.WSAcceptorManager;
import org.eclipse.net4j.internal.ws.WSConnector;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.ws.WSSConnectorFactory;
import org.eclipse.net4j.internal.ws.WSServerConnector;
import org.eclipse.net4j.internal.ws.jdk.JDKMessageConnectionFactory;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.tests.AbstractOMTest;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.IWSNegotiationContext;
import org.eclipse.net4j.ws.WSUtil;

import org.eclipse.spi.net4j.InternalConnector;
import org.eclipse.spi.net4j.MessageConnectionFactory;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Checks provider-neutral WebSocket connector descriptions without a network provider.
 *
 * @author Eike Stepper
 */
public class WSFrameworkTest extends AbstractOMTest
{
  private ManagedContainer providerContainer;

  private TestMessageConnectionFactory providerFactory;

  private final List<Thread> workerThreads = Collections.synchronizedList(new ArrayList<>());

  private boolean workerRegistrationClosed;

  private final AtomicReference<Throwable> workerFailure = new AtomicReference<>();

  @Override
  public void tearDown() throws Exception
  {
    Throwable failure = awaitWorkerThreads();
    failure = addFailure(workerFailure.get(), failure);

    try
    {
      LifecycleUtil.deactivate(providerContainer);
    }
    catch (RuntimeException | Error ex)
    {
      failure = addFailure(failure, ex);
    }

    try
    {
      super.tearDown();
    }
    catch (Exception | Error ex)
    {
      failure = addFailure(failure, ex);
    }

    rethrow(failure);
  }

  public void testWSConnectorDescriptionRoundTrip() throws Exception
  {
    WSConnectorFactory factory = createConnectorFactory();
    IWSConnector connector = factory.create("localhost:8080/net4j/@primary\ncookie:session=abc"); //$NON-NLS-1$

    assertEquals("ws://localhost:8080/net4j/", connector.getServiceURI().toString()); //$NON-NLS-1$
    assertEquals("primary", connector.getAcceptorName()); //$NON-NLS-1$
    assertEquals("ws://localhost:8080/net4j/@primary", connector.getURL()); //$NON-NLS-1$
    assertEquals("session", connector.getCookies().get(0).getName()); //$NON-NLS-1$
    assertEquals("abc", connector.getCookies().get(0).getValue()); //$NON-NLS-1$
    assertEquals("localhost:8080/net4j/@primary|provider:jdk|cookie:session=abc", factory.getDescriptionFor(connector)); //$NON-NLS-1$
  }

  public void testWSSConnectorUsesSecureScheme()
  {
    WSSConnectorFactory factory = new WSSConnectorFactory();
    initializeFactory(factory);
    IWSConnector connector = factory.create("localhost:8443/net4j/@secure"); //$NON-NLS-1$
    assertEquals("wss://localhost:8443/net4j/", connector.getServiceURI().toString()); //$NON-NLS-1$
    assertEquals("wss://localhost:8443/net4j/@secure", connector.getURL()); //$NON-NLS-1$
  }

  public void testEncodedAcceptorNameRoundTrip() throws Exception
  {
    String name = "sales / café+west"; //$NON-NLS-1$
    String description = WSUtil.getConnectorDescription("ws://localhost:8080/net4j", name); //$NON-NLS-1$
    assertEquals("localhost:8080/net4j/@sales%20%2F%20caf%C3%A9+west", description); //$NON-NLS-1$

    WSConnectorFactory factory = createConnectorFactory();
    IWSConnector connector = factory.create(description);
    assertEquals(name, connector.getAcceptorName());
    IWSConnector roundTripped = factory.create(factory.getDescriptionFor(connector));
    assertEquals(connector.getAcceptorName(), roundTripped.getAcceptorName());
    assertEquals(connector.getServiceURI(), roundTripped.getServiceURI());
    assertEquals("ws://localhost:8080/net4j/@sales%20%2F%20caf%C3%A9+west", connector.getURL()); //$NON-NLS-1$
  }

  public void testMissingMessageConnectionFactoryFailsClearly()
  {
    providerContainer = new ManagedContainer();
    LifecycleUtil.activate(providerContainer);
    WSConnectorFactory factory = new WSConnectorFactory();
    providerContainer.registerFactory(factory);
    try
    {
      factory.create("localhost:8080/net4j/@missing|provider:unavailable"); //$NON-NLS-1$
      fail("Expected missing provider failure"); //$NON-NLS-1$
    }
    catch (ProductCreationException expected)
    {
      assertTrue(expected.getMessage().contains("unavailable")); //$NON-NLS-1$
    }
  }

  public void testStandaloneInitializedContainerRegistersJDKProvider()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    try
    {
      MessageConnectionFactory factory = (MessageConnectionFactory)container.getFactory(MessageConnectionFactory.PRODUCT_GROUP, "jdk"); //$NON-NLS-1$
      IMessageConnection first = factory.create("ws://localhost/net4j/@test"); //$NON-NLS-1$
      IMessageConnection second = factory.create("wss://localhost/net4j/@test"); //$NON-NLS-1$
      assertNotSame(first, second);
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  public void testJDKProviderHandshakeCookieAndFragmentedReceive() throws Exception
  {
    try (RawWebSocketServer server = new RawWebSocketServer())
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      try
      {
        MessageConnectionFactory factory = (MessageConnectionFactory)container.getFactory(MessageConnectionFactory.PRODUCT_GROUP, "jdk"); //$NON-NLS-1$
        IMessageConnection connection = factory.create("ws://127.0.0.1:" + server.getPort() + "/net4j/@sales-west\nsession=abc"); //$NON-NLS-1$
        CountDownLatch received = new CountDownLatch(1);
        AtomicReference<ByteBuffer> message = new AtomicReference<>();
        AtomicReference<Throwable> closed = new AtomicReference<>();
        connection.start(new IMessageConnection.Listener()
        {
          @Override
          public void ready()
          {
            connection.request(1);
          }

          @Override
          public void received(ByteBuffer data, Runnable releaseAction)
          {
            message.set(data);
            releaseAction.run();
            received.countDown();
          }

          @Override
          public void writable()
          {
          }

          @Override
          public void closed(Throwable cause)
          {
            closed.set(cause);
          }
        });

        assertTrue("fragmented message was not delivered", received.await(10, TimeUnit.SECONDS)); //$NON-NLS-1$
        assertTrue("handshake was not captured", server.awaitHandshake(10, TimeUnit.SECONDS)); //$NON-NLS-1$
        String handshake = server.getHandshake().toLowerCase(Locale.ROOT);
        assertTrue(handshake.contains("net4jacceptor: sales-west")); //$NON-NLS-1$
        assertTrue(handshake.contains("cookie: session=abc")); //$NON-NLS-1$
        ByteBuffer frame = message.get();
        assertEquals(5, frame.remaining());
        assertEquals(1, frame.getShort());
        assertEquals(1, frame.getShort());
        assertEquals(0x42, frame.get() & 0xFF);
        IBufferProvider bufferProvider = Net4jUtil.createBufferFactory((short)128);
        IBuffer outgoing = bufferProvider.provideBuffer();
        outgoing.startPutting((short)1).put((byte)0x33);
        CountDownLatch sent = new CountDownLatch(1);
        AtomicReference<Throwable> sendFailure = new AtomicReference<>();
        assertTrue(connection.send(outgoing, new IMessageConnection.SendCallback()
        {
          @Override
          public void completed()
          {
            outgoing.release();
            sent.countDown();
          }

          @Override
          public void failed(Throwable failure)
          {
            sendFailure.set(failure);
            outgoing.release();
            sent.countDown();
          }
        }));
        assertTrue("outgoing send did not complete", sent.await(10, TimeUnit.SECONDS)); //$NON-NLS-1$
        assertNull(sendFailure.get());
        assertTrue(server.awaitHandshake(10, TimeUnit.SECONDS));
        assertTrue(server.getHandshake().toLowerCase(Locale.ROOT).contains("cookie: session=abc")); //$NON-NLS-1$
        assertTrue(server.awaitOutgoingFrame(10, TimeUnit.SECONDS));
        assertEquals(5, server.getOutgoingFrame().length);
        assertEquals(1, ByteBuffer.wrap(server.getOutgoingFrame()).getShort());
        assertEquals(1, ByteBuffer.wrap(server.getOutgoingFrame()).getShort(2));
        assertEquals(0x33, server.getOutgoingFrame()[4] & 0xFF);
        connection.abort();
        assertNotNull(closed.get());
      }
      finally
      {
        LifecycleUtil.deactivate(container);
      }
    }
  }

  public void testPipeDescriptionEscapingAndMultipleCookies()
  {
    WSConnectorFactory factory = createConnectorFactory();
    String value = "pipe|slash=part"; //$NON-NLS-1$
    String description = WSUtil.getConnectorDescription(URI.create("ws://localhost:8080/net4j"), "name / café", //$NON-NLS-1$
        "cookie:first=" + value, "cookie:second=two", "provider:jdk"); //$NON-NLS-1$ //$NON-NLS-2$
    IWSConnector connector = factory.create(description);
    assertEquals(value, connector.getCookies().get(0).getValue());
    assertEquals("two", connector.getCookies().get(1).getValue()); //$NON-NLS-1$
    assertEquals("name / café", connector.getAcceptorName()); //$NON-NLS-1$
    IWSConnector roundTripped = factory.create(factory.getDescriptionFor(connector));
    assertEquals(connector.getCookies().get(0).getValue(), roundTripped.getCookies().get(0).getValue());
    assertEquals(connector.getCookies().get(1).getValue(), roundTripped.getCookies().get(1).getValue());
  }

  public void testUnsupportedCookieValuesAreRejected()
  {
    WSConnectorFactory factory = createConnectorFactory();
    for (String value : new String[] { "semi;colon", "back\\slash", "white space", "café" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    {
      try
      {
        factory.create(WSUtil.getConnectorDescription(URI.create("ws://localhost/net4j"), "test", "cookie:session=" + value)); //$NON-NLS-1$
        fail("Expected unsupported cookie value to be rejected: " + value); //$NON-NLS-1$
      }
      catch (ProductCreationException expected)
      {
        assertNotNull(expected.getCause());
      }
    }

    try
    {
      WSUtil.getConnectorDescription(URI.create("ws://localhost/net4j"), "test", "cookie:session=ok\r\ncookie:other=injected"); //$NON-NLS-1$
      fail("Expected CR/LF in connector argument to be rejected"); //$NON-NLS-1$
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("CR or LF")); //$NON-NLS-1$
    }
  }

  public void testLegacyPipeInCookieValueIsPreserved()
  {
    WSConnectorFactory factory = createConnectorFactory();
    IWSConnector connector = factory.create("localhost:8080/net4j/@legacy\ncookie:session=left|right"); //$NON-NLS-1$
    assertEquals("left|right", connector.getCookies().get(0).getValue()); //$NON-NLS-1$
  }

  public void testCookiesSurviveBothFactoryBoundaries() throws Exception
  {
    try (RawWebSocketServer server = new RawWebSocketServer())
    {
      providerContainer = new ManagedContainer();
      CapturingJDKMessageConnectionFactory jdkFactory = new CapturingJDKMessageConnectionFactory();
      providerContainer.registerFactory(jdkFactory);
      WSConnectorFactory connectorFactory = new WSConnectorFactory();
      providerContainer.registerFactory(connectorFactory);
      LifecycleUtil.activate(providerContainer);
      String description = WSUtil.getConnectorDescription(URI.create("ws://127.0.0.1:" + server.getPort() + "/net4j"), "sales-west", //$NON-NLS-1$
          "cookie:first=pipe|safe", "cookie:second=two", "cookie:empty=", "provider:jdk"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      connectorFactory.create(description);

      CountDownLatch ready = new CountDownLatch(1);
      jdkFactory.connection.start(new IMessageConnection.Listener()
      {
        @Override
        public void ready()
        {
          ready.countDown();
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
        }
      });

      assertTrue("JDK provider did not complete the upgrade", ready.await(10, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("handshake was not captured", server.awaitHandshake(10, TimeUnit.SECONDS)); //$NON-NLS-1$
      String handshake = server.getHandshake().toLowerCase(Locale.ROOT);
      assertTrue(handshake.contains("cookie: first=pipe|safe; second=two; empty=")); //$NON-NLS-1$
      jdkFactory.connection.abort();
    }
    finally
    {
      LifecycleUtil.deactivate(providerContainer);
    }
  }

  public void testConnectorFactoryCreatesIndependentBoundConnections()
  {
    WSConnectorFactory factory = createConnectorFactory();
    WSConnector first = (WSConnector)factory.create("localhost:8080/net4j/@one"); //$NON-NLS-1$
    WSConnector second = (WSConnector)factory.create("localhost:8080/net4j/@two"); //$NON-NLS-1$
    assertEquals(2, providerFactory.creations.get());
    assertNotSame(providerFactory.connections.get(0), providerFactory.connections.get(1));

    first.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(first);
    try
    {
      first.waitForConnection(1000);
      assertEquals(ConnectorState.CONNECTED, first.getState());
    }
    finally
    {
      LifecycleUtil.deactivate(first);
      LifecycleUtil.deactivate(second);
    }
  }

  public void testAcceptedConnectorImplementsServerConnector() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    try
    {
      acceptor.accept(connection);
      assertEquals(1, acceptor.getAcceptedConnectors().length);
      IServerConnector connector = (IServerConnector)acceptor.getAcceptedConnectors()[0];
      assertEquals(Location.SERVER, connector.getLocation());
      assertSame(acceptor, connector.getAcceptor());
      assertEquals(1, connection.readyCallbacks.get());
      assertEquals("Lifecycle state was " + LifecycleUtil.getLifecycleState(connector), ConnectorState.CONNECTED, connector.getState()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }

    assertEquals(1, connection.aborts.get());
  }

  public void testJVMAcceptorTracksAndClosesItsServerConnector() throws Exception
  {
    String name = "jvm-acceptor-" + UUID.randomUUID(); //$NON-NLS-1$
    JVMAcceptor acceptor = new JVMAcceptor();
    acceptor.setName(name);
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);

    JVMClientConnector client = new JVMClientConnector();
    client.setName(name);
    client.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    try
    {
      LifecycleUtil.activate(client);
      assertEquals(1, acceptor.getAcceptedConnectors().length);
      IServerConnector serverConnector = (IServerConnector)acceptor.getAcceptedConnectors()[0];
      assertTrue(LifecycleUtil.isActive(serverConnector));
      assertEquals(ConnectorState.CONNECTED, serverConnector.getState());

      LifecycleUtil.deactivate(acceptor);

      assertFalse(LifecycleUtil.isActive(serverConnector));
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertFalse(LifecycleUtil.isActive(client));
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(client);
    }
  }

  public void testDeferredAcceptIsTrackedUntilReady() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    try
    {
      acceptor.accept(connection);
      assertEquals(1, acceptor.getAcceptedConnectors().length);
      WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];
      assertEquals(LifecycleState.ACTIVATING, LifecycleUtil.getLifecycleState(connector));

      Thread ready = new Thread(() -> connection.listener.ready(), "ws-deferred-ready"); //$NON-NLS-1$
      startWorker(ready);
      ready.join(5000);

      assertFalse("Readiness callback remained blocked", ready.isAlive()); //$NON-NLS-1$
      assertEquals(LifecycleState.ACTIVE, LifecycleUtil.getLifecycleState(connector));
      assertEquals(ConnectorState.CONNECTED, connector.getState());
      assertEquals(1, acceptor.getAcceptedConnectors().length);
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }

    assertEquals(1, connection.aborts.get());
  }

  public void testDeferredAcceptCanNegotiateAfterReadiness() throws Exception
  {
    AtomicReference<INegotiationContext> negotiation = new AtomicReference<>();
    WSAcceptor acceptor = new WSAcceptor("ws"); //$NON-NLS-1$
    acceptor.setName("deferred-negotiation-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    acceptor.setNegotiator(new INegotiator()
    {
      @Override
      public boolean isInitiator()
      {
        return false;
      }

      @Override
      public void negotiate(INegotiationContext context)
      {
        negotiation.set(context);
      }
    });
    LifecycleUtil.activate(acceptor);

    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    try
    {
      acceptor.accept(connection);
      WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];
      connection.listener.ready();
      assertEquals(ConnectorState.NEGOTIATING, connector.getState());
      assertEquals(LifecycleState.ACTIVATING, LifecycleUtil.getLifecycleState(connector));

      negotiation.get().setFinished(true);

      assertEquals(ConnectorState.CONNECTED, connector.getState());
      assertEquals(LifecycleState.ACTIVE, LifecycleUtil.getLifecycleState(connector));
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }

    assertEquals(1, connection.aborts.get());
  }

  public void testNegotiationCompletionAfterProviderClosureCannotActivate() throws Exception
  {
    AtomicReference<INegotiationContext> negotiation = new AtomicReference<>();
    CountDownLatch negotiationStarted = new CountDownLatch(1);
    CountDownLatch finishNegotiation = new CountDownLatch(1);
    WSAcceptor acceptor = new WSAcceptor("ws"); //$NON-NLS-1$
    acceptor.setName("deferred-close-negotiation-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    acceptor.setNegotiator(new INegotiator()
    {
      @Override
      public boolean isInitiator()
      {
        return false;
      }

      @Override
      public void negotiate(INegotiationContext context)
      {
        negotiation.set(context);
        negotiationStarted.countDown();
        awaitLatch(finishNegotiation, "finish deferred negotiation"); //$NON-NLS-1$
        context.setFinished(true);
      }
    });
    LifecycleUtil.activate(acceptor);
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    acceptor.accept(connection);
    WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];
    Thread ready = new Thread(() -> connection.listener.ready(), "ws-negotiation-ready"); //$NON-NLS-1$
    try
    {
      startWorker(ready);
      assertTrue("Negotiation callback was not reached", negotiationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      connection.listener.closed(new IllegalStateException("provider closed during negotiation")); //$NON-NLS-1$
      finishNegotiation.countDown();
      ready.join(5000);

      assertFalse("Readiness callback remained blocked", ready.isAlive()); //$NON-NLS-1$
      assertNotNull(negotiation.get());
      assertEquals(LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
      assertEquals(ConnectorState.DISCONNECTED, connector.getState());
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(0, connection.aborts.get());

      FakeMessageConnection replacement = new FakeMessageConnection();
      connector.setConnection(replacement);
      FutureTask<Throwable> reactivation = new FutureTask<>(() -> {
        try
        {
          LifecycleUtil.activate(connector);
          return null;
        }
        catch (Throwable ex)
        {
          return ex;
        }
      });
      Thread reactivationThread = new Thread(reactivation, "ws-deferred-reactivation"); //$NON-NLS-1$
      reactivationThread.setDaemon(true);
      startWorker(reactivationThread);
      reactivationThread.join(5000);

      assertFalse("Reactivation remained blocked", reactivationThread.isAlive()); //$NON-NLS-1$
      assertNull(reactivation.get());
      connection.listener.ready();
      connection.listener.closed(new IllegalStateException("stale provider callback")); //$NON-NLS-1$
      assertEquals(LifecycleState.ACTIVE, LifecycleUtil.getLifecycleState(connector));
      assertEquals(ConnectorState.CONNECTED, connector.getState());
      LifecycleUtil.deactivate(connector);
      assertEquals(1, replacement.aborts.get());
    }
    finally
    {
      finishNegotiation.countDown();
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testProviderClosureImmediatelyAfterDeferredActivationDeactivatesNormally() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    acceptor.accept(connection);
    WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];
    CountDownLatch activationListenerEntered = new CountDownLatch(1);
    CountDownLatch finishActivationListener = new CountDownLatch(1);
    CountDownLatch deactivated = new CountDownLatch(1);
    connector.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onActivated(ILifecycle lifecycle)
      {
        activationListenerEntered.countDown();
        awaitLatch(finishActivationListener, "finish activation listener"); //$NON-NLS-1$
      }

      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        deactivated.countDown();
      }
    });
    Thread ready = new Thread(() -> connection.listener.ready(), "ws-ready-close-race"); //$NON-NLS-1$
    try
    {
      startWorker(ready);
      assertTrue("Activation listener was not reached", activationListenerEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      connection.listener.closed(null);
      finishActivationListener.countDown();
      ready.join(5000);

      assertFalse("Readiness callback remained blocked", ready.isAlive()); //$NON-NLS-1$
      assertTrue("Normal lifecycle deactivation did not finish", deactivated.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals(LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
      assertEquals(ConnectorState.DISCONNECTED, connector.getState());
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(0, connection.aborts.get());
    }
    finally
    {
      finishActivationListener.countDown();
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testDeferredFailureBeforeReadinessRemovesAcceptedConnector() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    try
    {
      acceptor.accept(connection);
      WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];
      connection.listener.closed(new IllegalStateException("provider failed before readiness")); //$NON-NLS-1$

      assertEquals(LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
      assertEquals(0, acceptor.getAcceptedConnectors().length);
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testShutdownAbortsPendingDeferredAcceptAndIgnoresStaleReady() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    acceptor.accept(connection);
    WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];

    LifecycleUtil.deactivate(acceptor);
    connection.listener.ready();

    assertFalse(acceptor.isActive());
    assertEquals(LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
    assertEquals(0, acceptor.getAcceptedConnectors().length);
    assertEquals(1, connection.aborts.get());
  }

  public void testShutdownTerminatesPendingAcceptWithoutClosedCallback() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    connection.notifyClosedOnAbort = false;
    acceptor.accept(connection);
    WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];

    LifecycleUtil.deactivate(acceptor);
    connection.listener.ready();
    LifecycleUtil.deactivate(acceptor);

    assertFalse(acceptor.isActive());
    assertEquals(LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
    assertEquals(ConnectorState.DISCONNECTED, connector.getState());
    assertEquals(0, acceptor.getAcceptedConnectors().length);
    assertEquals(1, connection.aborts.get());
  }

  public void testDirectConnectorDeactivationTerminatesPendingAcceptWithoutClosedCallback() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    connection.notifyClosedOnAbort = false;
    acceptor.accept(connection);
    WSServerConnector connector = (WSServerConnector)acceptor.getAcceptedConnectors()[0];

    try
    {
      LifecycleUtil.deactivate(connector);
      connection.listener.ready();

      assertEquals(LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
      assertEquals(ConnectorState.DISCONNECTED, connector.getState());
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      LifecycleUtil.deactivate(connector);
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }

    assertEquals(1, connection.aborts.get());
  }

  public void testConnectorRemovalCannotPrecedeAdditionPublication() throws Exception
  {
    CountDownLatch registrationPaused = new CountDownLatch(1);
    CountDownLatch resumeRegistration = new CountDownLatch(1);
    List<String> events = Collections.synchronizedList(new ArrayList<>());
    AtomicReference<Throwable> acceptFailure = new AtomicReference<>();
    WSAcceptor acceptor = new WSAcceptor("ws") //$NON-NLS-1$
    {
      @Override
      public void fireElementAddedEvent(IConnector connector)
      {
        registrationPaused.countDown();
        awaitLatch(resumeRegistration, "resume connector registration"); //$NON-NLS-1$
        super.fireElementAddedEvent(connector);
      }
    };
    acceptor.setName("event-order-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);
    acceptor.addListener(event -> {
      if (event instanceof IContainerEvent<?>)
      {
        for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
        {
          if (delta.getElement() instanceof IConnector)
          {
            events.add(delta.getKind().name());
          }
        }
      }
    });

    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    Thread accepter = new Thread(() -> {
      try
      {
        acceptor.accept(connection);
      }
      catch (Throwable ex)
      {
        acceptFailure.set(ex);
      }
    }, "ws-event-order-accept"); //$NON-NLS-1$
    try
    {
      startWorker(accepter);
      assertTrue("Registration did not reach the publication barrier", registrationPaused.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      LifecycleUtil.deactivate(acceptor);
      resumeRegistration.countDown();
      accepter.join(5000);

      assertFalse("Accept thread remained blocked", accepter.isAlive()); //$NON-NLS-1$
      assertTrue(acceptFailure.get() instanceof IllegalStateException);
      assertEquals("[ADDED, REMOVED]", events.toString()); //$NON-NLS-1$
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(1, connection.aborts.get());
    }
    finally
    {
      resumeRegistration.countDown();
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testRemovalDuringListenerRegistrationDoesNotLeaveStaleListener() throws Exception
  {
    CountDownLatch listenerAddEntered = new CountDownLatch(1);
    CountDownLatch allowListenerAdd = new CountDownLatch(1);
    CountDownLatch removalStarted = new CountDownLatch(1);
    AtomicBoolean blockListenerAdd = new AtomicBoolean(false);
    List<String> events = Collections.synchronizedList(new ArrayList<>());
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    WSServerConnector connector = new WSServerConnector(acceptor, connection)
    {
      @Override
      public void addListener(IListener listener)
      {
        if (blockListenerAdd.compareAndSet(true, false))
        {
          listenerAddEntered.countDown();
          awaitLatch(allowListenerAdd, "allow listener registration"); //$NON-NLS-1$
        }

        super.addListener(listener);
      }
    };
    acceptor.prepareConnector(connector);
    LifecycleUtil.activate(connector);
    int initialListenerCount = connector.getListeners().length;
    acceptor.addListener(event -> recordConnectorEvents(event, connector, events));
    blockListenerAdd.set(true);
    AtomicReference<Throwable> addFailure = new AtomicReference<>();
    Thread adder = new Thread(() -> {
      try
      {
        acceptor.addConnector(connector);
      }
      catch (Throwable ex)
      {
        addFailure.set(ex);
      }
    }, "ws-listener-add"); //$NON-NLS-1$
    Thread remover = new Thread(() -> {
      removalStarted.countDown();
      acceptor.removeConnector(connector);
    }, "ws-listener-remove"); //$NON-NLS-1$

    try
    {
      startWorker(adder);
      assertTrue("Listener registration did not reach its barrier", listenerAddEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      startWorker(remover);
      assertTrue("Removal did not start", removalStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitBlockedOnMonitorOwnedBy(remover, adder, "Removal did not block on the acceptor monitor"); //$NON-NLS-1$
      allowListenerAdd.countDown();
      adder.join(5000);
      remover.join(5000);

      assertFalse("Add thread remained blocked", adder.isAlive()); //$NON-NLS-1$
      assertFalse("Remove thread remained blocked", remover.isAlive()); //$NON-NLS-1$
      assertNull(addFailure.get());
      assertEquals("[ADDED, REMOVED]", events.toString()); //$NON-NLS-1$
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(initialListenerCount, connector.getListeners().length);
    }
    finally
    {
      allowListenerAdd.countDown();
      adder.join(5000);
      if (remover.isAlive())
      {
        remover.join(5000);
      }

      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testReadditionDuringListenerRemovalRetainsListener() throws Exception
  {
    CountDownLatch listenerRemoveEntered = new CountDownLatch(1);
    CountDownLatch allowListenerRemove = new CountDownLatch(1);
    CountDownLatch additionStarted = new CountDownLatch(1);
    AtomicBoolean blockListenerRemove = new AtomicBoolean(false);
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    WSServerConnector connector = new WSServerConnector(acceptor, connection)
    {
      @Override
      public void addListener(IListener listener)
      {
        addUniqueListener(listener);
      }

      @Override
      public void removeListener(IListener listener)
      {
        if (blockListenerRemove.compareAndSet(true, false))
        {
          listenerRemoveEntered.countDown();
          awaitLatch(allowListenerRemove, "allow listener removal"); //$NON-NLS-1$
        }

        super.removeListener(listener);
      }
    };
    acceptor.prepareConnector(connector);
    LifecycleUtil.activate(connector);
    int initialListenerCount = connector.getListeners().length;
    acceptor.addConnector(connector);
    List<String> eventsAfterRegistration = Collections.synchronizedList(new ArrayList<>());
    acceptor.addListener(event -> recordConnectorEvents(event, connector, eventsAfterRegistration));
    AtomicReference<Throwable> addFailure = new AtomicReference<>();
    blockListenerRemove.set(true);
    Thread remover = new Thread(() -> acceptor.removeConnector(connector), "ws-listener-remove-readd"); //$NON-NLS-1$
    Thread adder = new Thread(() -> {
      additionStarted.countDown();
      try
      {
        acceptor.addConnector(connector);
      }
      catch (Throwable ex)
      {
        addFailure.set(ex);
      }
    }, "ws-listener-readd"); //$NON-NLS-1$

    try
    {
      startWorker(remover);
      assertTrue("Listener removal did not reach its barrier", listenerRemoveEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      startWorker(adder);
      assertTrue("Re-addition did not start", additionStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitBlockedOnMonitorOwnedBy(adder, remover, "Re-addition did not block on the acceptor monitor"); //$NON-NLS-1$
      allowListenerRemove.countDown();
      remover.join(5000);
      adder.join(5000);

      assertFalse("Remove thread remained blocked", remover.isAlive()); //$NON-NLS-1$
      assertFalse("Re-add thread remained blocked", adder.isAlive()); //$NON-NLS-1$
      assertNull(addFailure.get());
      assertEquals("[REMOVED, ADDED]", eventsAfterRegistration.toString()); //$NON-NLS-1$
      assertEquals(1, acceptor.getAcceptedConnectors().length);
      assertSame(connector, acceptor.getAcceptedConnectors()[0]);
      assertEquals(initialListenerCount + 1, connector.getListeners().length);
    }
    finally
    {
      allowListenerRemove.countDown();
      if (remover.isAlive())
      {
        remover.join(5000);
      }

      if (adder.isAlive())
      {
        adder.join(5000);
      }

      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testPublishFailureCannotRemoveReentrantNewRegistration() throws Exception
  {
    List<String> events = Collections.synchronizedList(new ArrayList<>());
    AtomicBoolean firstAddition = new AtomicBoolean(true);
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    WSServerConnector connector = new WSServerConnector(acceptor, connection);
    acceptor.prepareConnector(connector);
    LifecycleUtil.activate(connector);
    int initialListenerCount = connector.getListeners().length;
    acceptor.addListener(event -> {
      recordConnectorEvents(event, connector, events);
      if (event instanceof IContainerEvent<?> && firstAddition.compareAndSet(true, false))
      {
        acceptor.removeConnector(connector);
        acceptor.addConnector(connector);
        throw new Notifier.Cancelation(new IllegalStateException("controlled listener cancellation")); //$NON-NLS-1$
      }
    });

    try
    {
      try
      {
        acceptor.addConnector(connector);
        fail("Expected container listener cancellation"); //$NON-NLS-1$
      }
      catch (IllegalStateException expected)
      {
        assertEquals("[ADDED, REMOVED, ADDED]", events.toString()); //$NON-NLS-1$
        assertEquals(1, acceptor.getAcceptedConnectors().length);
        assertSame(connector, acceptor.getAcceptedConnectors()[0]);
        assertEquals(initialListenerCount + 1, connector.getListeners().length);
      }

      acceptor.removeConnector(connector);
      assertEquals("[ADDED, REMOVED, ADDED, REMOVED]", events.toString()); //$NON-NLS-1$
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(initialListenerCount, connector.getListeners().length);
      LifecycleUtil.deactivate(acceptor);
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(connector);
    }
  }

  private static void recordConnectorEvents(IEvent event, IConnector connector, List<String> events)
  {
    if (event instanceof IContainerEvent<?>)
    {
      for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
      {
        if (delta.getElement() == connector)
        {
          events.add(delta.getKind().name());
        }
      }
    }
  }

  private static void awaitBlockedOnMonitorOwnedBy(Thread blockedThread, Thread ownerThread, String failureMessage) throws InterruptedException
  {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    long blockedThreadID = blockedThread.getId();
    long ownerThreadID = ownerThread.getId();

    while (System.nanoTime() < deadline)
    {
      ThreadInfo threadInfo = threadMXBean.getThreadInfo(blockedThreadID);
      if (threadInfo != null && threadInfo.getThreadState() == Thread.State.BLOCKED && threadInfo.getLockOwnerId() == ownerThreadID)
      {
        return;
      }

      Thread.yield();
    }

    ThreadInfo threadInfo = threadMXBean.getThreadInfo(blockedThreadID);
    fail(failureMessage + ": expected thread " + blockedThread.getName() + " to block on a monitor owned by " + ownerThread.getName() //$NON-NLS-1$
        + ", actual state=" + (threadInfo == null ? "terminated" : threadInfo.getThreadState()) //$NON-NLS-1$
        + ", lockOwner=" + (threadInfo == null ? "none" : threadInfo.getLockOwnerName())); //$NON-NLS-1$
  }

  public void testContainerListenerCanSynchronouslyRemoveConnector() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    List<String> events = Collections.synchronizedList(new ArrayList<>());
    acceptor.addListener(event -> {
      if (event instanceof IContainerEvent<?>)
      {
        for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
        {
          if (delta.getElement() instanceof IConnector)
          {
            events.add(delta.getKind().name());
            if (delta.getKind() == IContainerDelta.Kind.ADDED)
            {
              acceptor.removeConnector((IConnector)delta.getElement());
            }
          }
        }
      }
    });

    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    try
    {
      acceptor.accept(connection);
      fail("Expected synchronous removal to reject the registration"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
      assertEquals("[ADDED, REMOVED]", events.toString()); //$NON-NLS-1$
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(1, connection.aborts.get());
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testConcurrentDuplicateConnectorRemovalPublishesOnce() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    acceptor.accept(connection);
    IConnector connector = acceptor.getAcceptedConnectors()[0];
    List<String> events = Collections.synchronizedList(new ArrayList<>());
    acceptor.addListener(event -> {
      if (event instanceof IContainerEvent<?>)
      {
        for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
        {
          if (delta.getElement() == connector)
          {
            events.add(delta.getKind().name());
          }
        }
      }
    });
    CountDownLatch start = new CountDownLatch(1);
    Thread first = new Thread(() -> {
      awaitLatch(start, "start first connector removal"); //$NON-NLS-1$
      acceptor.removeConnector(connector);
    }, "ws-remove-first"); //$NON-NLS-1$
    Thread second = new Thread(() -> {
      awaitLatch(start, "start second connector removal"); //$NON-NLS-1$
      acceptor.removeConnector(connector);
    }, "ws-remove-second"); //$NON-NLS-1$
    try
    {
      try
      {
        startWorker(first);
        startWorker(second);
      }
      finally
      {
        start.countDown();
      }

      awaitWorkers("concurrent connector removal", first, second); //$NON-NLS-1$
    }
    finally
    {
      start.countDown();
      LifecycleUtil.deactivate(connector);
      LifecycleUtil.deactivate(acceptor);
    }

    assertFalse("First remove thread remained blocked", first.isAlive()); //$NON-NLS-1$
    assertFalse("Second remove thread remained blocked", second.isAlive()); //$NON-NLS-1$
    assertEquals("[REMOVED]", events.toString()); //$NON-NLS-1$
    assertEquals(0, acceptor.getAcceptedConnectors().length);
    assertEquals(1, connection.aborts.get());
  }

  public void testSynchronousContainerListenerCanRaceAcceptorShutdown() throws Exception
  {
    CountDownLatch addedListenerEntered = new CountDownLatch(1);
    CountDownLatch shutdownStarted = new CountDownLatch(1);
    CountDownLatch allowListenerShutdown = new CountDownLatch(1);
    AtomicInteger addedEvents = new AtomicInteger();
    AtomicInteger removedEvents = new AtomicInteger();
    AtomicReference<Throwable> acceptFailure = new AtomicReference<>();
    AtomicReference<Throwable> shutdownFailure = new AtomicReference<>();
    WSAcceptor acceptor = createAcceptor();
    acceptor.addListener(event -> {
      if (event instanceof IContainerEvent<?>)
      {
        for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
        {
          if (!(delta.getElement() instanceof IConnector))
          {
            continue;
          }

          if (delta.getKind() == IContainerDelta.Kind.ADDED)
          {
            addedEvents.incrementAndGet();
            addedListenerEntered.countDown();
            awaitLatch(allowListenerShutdown, "allow listener-triggered shutdown"); //$NON-NLS-1$
            LifecycleUtil.deactivate(acceptor);
          }
          else
          {
            removedEvents.incrementAndGet();
          }
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent lifecycleEvent = (ILifecycleEvent)event;
        if (lifecycleEvent.getKind() == ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE)
        {
          shutdownStarted.countDown();
        }
      }
    });

    FakeMessageConnection connection = new FakeMessageConnection();
    connection.autoReady = false;
    Thread accepter = new Thread(() -> {
      try
      {
        acceptor.accept(connection);
      }
      catch (Throwable ex)
      {
        acceptFailure.set(ex);
      }
    }, "ws-event-accept"); //$NON-NLS-1$
    Thread shutdown = new Thread(() -> {
      try
      {
        LifecycleUtil.deactivate(acceptor);
      }
      catch (Throwable ex)
      {
        shutdownFailure.set(ex);
      }
    }, "ws-event-shutdown"); //$NON-NLS-1$
    try
    {
      startWorker(accepter);
      assertTrue("ADDED listener was not reached", addedListenerEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      startWorker(shutdown);
      assertTrue("Acceptor shutdown did not start", shutdownStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      allowListenerShutdown.countDown();
      accepter.join(5000);
      shutdown.join(5000);

      assertFalse("Accept thread remained blocked", accepter.isAlive()); //$NON-NLS-1$
      assertFalse("Shutdown thread remained blocked", shutdown.isAlive()); //$NON-NLS-1$
      assertNull(shutdownFailure.get());
      assertTrue(acceptFailure.get() instanceof IllegalStateException);
      assertFalse(acceptor.isActive());
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(1, addedEvents.get());
      assertEquals(1, removedEvents.get());
      assertEquals(1, connection.aborts.get());
    }
    finally
    {
      allowListenerShutdown.countDown();
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testActivationFailureAbortsAcceptedConnection()
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.failStart = true;
    try
    {
      try
      {
        acceptor.accept(connection);
        fail("Expected activation failure"); //$NON-NLS-1$
      }
      catch (IllegalStateException expected)
      {
        assertEquals(0, acceptor.getAcceptedConnectors().length);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }

    assertEquals(1, connection.aborts.get());
  }

  public void testAcceptorRegistrationAndLookupAreConcurrent() throws Exception
  {
    WSAcceptorManager manager = WSAcceptorManager.INSTANCE;
    String name = "registry-" + UUID.randomUUID(); //$NON-NLS-1$
    WSAcceptor first = new WSAcceptor("ws"); //$NON-NLS-1$
    WSAcceptor second = new WSAcceptor("ws"); //$NON-NLS-1$
    first.setName(name);
    second.setName(name);
    CountDownLatch registrationStart = new CountDownLatch(1);
    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Boolean> firstRegistered = new AtomicReference<>();
    AtomicReference<Boolean> secondRegistered = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    try
    {
      Thread firstRegistrar = new Thread(() -> {
        awaitLatch(registrationStart, "start first acceptor registration"); //$NON-NLS-1$
        firstRegistered.set(manager.registerAcceptor(first));
      });
      Thread secondRegistrar = new Thread(() -> {
        awaitLatch(registrationStart, "start second acceptor registration"); //$NON-NLS-1$
        secondRegistered.set(manager.registerAcceptor(second));
      });
      try
      {
        startWorker(firstRegistrar);
        startWorker(secondRegistrar);
      }
      finally
      {
        registrationStart.countDown();
      }

      awaitWorkers("concurrent acceptor registration", firstRegistrar, secondRegistrar); //$NON-NLS-1$
      assertTrue(firstRegistered.get() ^ secondRegistered.get());
      IWSAcceptor winner = manager.getAcceptor(name);
      IWSAcceptor loser = winner == first ? second : first;
      assertFalse(manager.deregisterAcceptor(loser));
      assertSame(winner, manager.getAcceptor(name));
      assertTrue(manager.deregisterAcceptor(winner));

      Thread writer = new Thread(() -> {
        try
        {
          awaitLatch(start, "start acceptor registry writer"); //$NON-NLS-1$
          for (int i = 0; i < 1000; i++)
          {
            WSAcceptor acceptor = new WSAcceptor("ws"); //$NON-NLS-1$
            acceptor.setName(name);
            manager.registerAcceptor(acceptor);
            manager.getAcceptorRegistry().entrySet().iterator().hasNext();
            manager.deregisterAcceptor(acceptor);
          }
        }
        catch (Throwable ex)
        {
          failure.set(ex);
        }
      });
      Thread reader = new Thread(() -> {
        try
        {
          awaitLatch(start, "start acceptor registry reader"); //$NON-NLS-1$
          for (int i = 0; i < 10000; i++)
          {
            manager.getAcceptor(name);
            manager.getAcceptorRegistry().entrySet().iterator().hasNext();
          }
        }
        catch (Throwable ex)
        {
          failure.set(ex);
        }
      });

      try
      {
        startWorker(writer);
        startWorker(reader);
      }
      finally
      {
        start.countDown();
      }

      awaitWorkers("concurrent acceptor registration lookup", writer, reader); //$NON-NLS-1$
      assertNull(failure.get());
      assertNull(manager.getAcceptor(name));
    }
    finally
    {
      registrationStart.countDown();
      start.countDown();
      manager.deregisterAcceptor(first);
      manager.deregisterAcceptor(second);
    }
  }

  public void testAcceptorShutdownDoesNotWaitForInProgressAcceptance() throws Exception
  {
    CountDownLatch shutdownEntered = new CountDownLatch(1);
    CountDownLatch startEntered = new CountDownLatch(1);
    CountDownLatch releaseStart = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    WSAcceptor acceptor = new WSAcceptor("ws") //$NON-NLS-1$
    {
      @Override
      protected void doBeforeDeactivate() throws Exception
      {
        shutdownEntered.countDown();
        super.doBeforeDeactivate();
      }
    };
    acceptor.setName("race-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);

    FakeMessageConnection connection = new FakeMessageConnection()
    {
      @Override
      public void start(Listener listener)
      {
        startEntered.countDown();
        awaitLatch(releaseStart, "release provider startup"); //$NON-NLS-1$

        super.start(listener);
      }
    };
    Thread accepter = new Thread(() -> {
      try
      {
        acceptor.accept(connection);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    });
    Thread shutdown = new Thread(() -> LifecycleUtil.deactivate(acceptor));
    try
    {
      startWorker(accepter);
      assertTrue(startEntered.await(10, TimeUnit.SECONDS));
      startWorker(shutdown);
      assertTrue(shutdownEntered.await(10, TimeUnit.SECONDS));
      shutdown.join(5000);
      assertFalse("Acceptor shutdown waited for provider startup", shutdown.isAlive()); //$NON-NLS-1$
    }
    finally
    {
      releaseStart.countDown();
      LifecycleUtil.deactivate(acceptor);
    }

    awaitWorkers("acceptance racing acceptor shutdown", accepter, shutdown); //$NON-NLS-1$
    assertTrue(failure.get() == null || failure.get() instanceof IllegalStateException);
    assertFalse(acceptor.isActive());
    assertEquals(0, acceptor.getAcceptedConnectors().length);
    assertEquals(1, connection.aborts.get());
  }

  public void testCrossThreadReentrantAcceptorShutdownDoesNotDeadlock() throws Exception
  {
    CountDownLatch shutdownEntered = new CountDownLatch(1);
    CountDownLatch releaseShutdown = new CountDownLatch(1);
    CountDownLatch callbackEntered = new CountDownLatch(1);
    AtomicReference<Throwable> acceptFailure = new AtomicReference<>();
    AtomicReference<Throwable> shutdownFailure = new AtomicReference<>();
    WSAcceptor acceptor = new WSAcceptor("ws") //$NON-NLS-1$
    {
      @Override
      protected void doBeforeDeactivate() throws Exception
      {
        super.doBeforeDeactivate();
        shutdownEntered.countDown();
        if (!releaseShutdown.await(5, TimeUnit.SECONDS))
        {
          throw new IllegalStateException("Timed out waiting to release test shutdown"); //$NON-NLS-1$
        }
      }
    };
    acceptor.setName("cross-thread-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);

    FakeMessageConnection connection = new FakeMessageConnection();
    connection.startAction = () -> {
      callbackEntered.countDown();
      LifecycleUtil.deactivate(acceptor);
    };
    Thread accepter = new Thread(() -> {
      try
      {
        acceptor.accept(connection);
      }
      catch (Throwable ex)
      {
        acceptFailure.set(ex);
      }
    }, "cross-thread-ws-accept"); //$NON-NLS-1$
    Thread shutdown = new Thread(() -> {
      try
      {
        LifecycleUtil.deactivate(acceptor);
      }
      catch (Throwable ex)
      {
        shutdownFailure.set(ex);
      }
    }, "cross-thread-ws-shutdown"); //$NON-NLS-1$

    startWorker(accepter);
    assertTrue("Provider start was not entered", connection.started.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    startWorker(shutdown);
    assertTrue("Shutdown did not enter its lifecycle hook", shutdownEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    assertTrue("Provider callback did not attempt reentrant shutdown", callbackEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    releaseShutdown.countDown();
    accepter.join(5000);
    shutdown.join(5000);

    assertFalse("Acceptance remained blocked", accepter.isAlive()); //$NON-NLS-1$
    assertFalse("Shutdown remained blocked", shutdown.isAlive()); //$NON-NLS-1$
    assertNull(shutdownFailure.get());
    assertTrue(acceptFailure.get() instanceof IllegalStateException);
    assertFalse(acceptor.isActive());
    assertEquals(0, acceptor.getAcceptedConnectors().length);
    assertEquals(1, connection.aborts.get());
  }

  public void testImmediateConnectorDeactivationDoesNotLeaveAStaleRegistration() throws Exception
  {
    WSAcceptor acceptor = new WSAcceptor("ws") //$NON-NLS-1$
    {
      @Override
      public void prepareConnector(InternalConnector connector)
      {
        super.prepareConnector(connector);
        connector.addListener(new LifecycleEventAdapter()
        {
          @Override
          protected void onActivated(ILifecycle lifecycle)
          {
            LifecycleUtil.deactivate(lifecycle);
          }
        });
      }
    };
    acceptor.setName("immediate-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);
    FakeMessageConnection connection = new FakeMessageConnection();

    try
    {
      acceptor.accept(connection);
      fail("Expected connector deactivation during startup"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
      assertEquals(0, acceptor.getAcceptedConnectors().length);
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }

    assertEquals(1, connection.aborts.get());
  }

  public void testDeactivationAfterActivationBeforeRegistrationIsNotLost() throws Exception
  {
    CountDownLatch registrationEntered = new CountDownLatch(1);
    CountDownLatch continueRegistration = new CountDownLatch(1);
    CountDownLatch connectorDeactivated = new CountDownLatch(1);
    AtomicReference<InternalConnector> registeringConnector = new AtomicReference<>();
    AtomicReference<Throwable> acceptFailure = new AtomicReference<>();
    AtomicReference<Throwable> deactivationFailure = new AtomicReference<>();
    AtomicInteger addedEvents = new AtomicInteger();
    AtomicInteger removedEvents = new AtomicInteger();
    AtomicInteger deactivatedEvents = new AtomicInteger();
    WSAcceptor acceptor = new WSAcceptor("ws") //$NON-NLS-1$
    {
      @Override
      public void addConnector(InternalConnector connector)
      {
        assertTrue("Connector was not active before registration", LifecycleUtil.isActive(connector)); //$NON-NLS-1$
        registeringConnector.set(connector);
        connector.addListener(new LifecycleEventAdapter()
        {
          @Override
          protected void onDeactivated(ILifecycle lifecycle)
          {
            deactivatedEvents.incrementAndGet();
            connectorDeactivated.countDown();
          }
        });
        registrationEntered.countDown();
        try
        {
          if (!continueRegistration.await(5, TimeUnit.SECONDS))
          {
            throw new IllegalStateException("Timed out waiting to continue connector registration"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }

        super.addConnector(connector);
      }
    };
    acceptor.setName("registration-race-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);
    acceptor.addListener(event -> {
      if (event instanceof IContainerEvent<?>)
      {
        for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
        {
          if (delta.getElement() instanceof IConnector)
          {
            if (delta.getKind() == IContainerDelta.Kind.ADDED)
            {
              addedEvents.incrementAndGet();
            }
            else
            {
              removedEvents.incrementAndGet();
            }
          }
        }
      }
    });

    FakeMessageConnection connection = new FakeMessageConnection();
    Thread accepter = new Thread(() -> {
      try
      {
        acceptor.accept(connection);
      }
      catch (Throwable ex)
      {
        acceptFailure.set(ex);
      }
    }, "ws-registration-accept"); //$NON-NLS-1$
    Thread deactivator = new Thread(() -> {
      try
      {
        LifecycleUtil.deactivate(registeringConnector.get());
      }
      catch (Throwable ex)
      {
        deactivationFailure.set(ex);
      }
    }, "ws-registration-deactivate"); //$NON-NLS-1$
    try
    {
      startWorker(accepter);
      assertTrue("Connector did not reach registration", registrationEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue(LifecycleUtil.isActive(registeringConnector.get()));
      startWorker(deactivator);
      assertTrue("Connector did not deactivate before registration", connectorDeactivated.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      continueRegistration.countDown();
      accepter.join(5000);
      deactivator.join(5000);

      assertFalse("Acceptor registration remained blocked", accepter.isAlive()); //$NON-NLS-1$
      assertFalse("Connector deactivation remained blocked", deactivator.isAlive()); //$NON-NLS-1$
      assertTrue(acceptFailure.get() instanceof IllegalStateException);
      assertNull(deactivationFailure.get());
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(0, addedEvents.get());
      assertEquals(0, removedEvents.get());
      assertEquals(1, deactivatedEvents.get());
      assertEquals(1, connection.aborts.get());
    }
    finally
    {
      continueRegistration.countDown();
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testDeactivationDuringRegistrationRemovesConnectorOnce() throws Exception
  {
    CountDownLatch connectorDeactivated = new CountDownLatch(1);
    AtomicReference<Throwable> deactivationFailure = new AtomicReference<>();
    AtomicReference<Throwable> acceptFailure = new AtomicReference<>();
    AtomicInteger addedEvents = new AtomicInteger();
    AtomicInteger removedEvents = new AtomicInteger();
    AtomicInteger deactivatedEvents = new AtomicInteger();
    WSAcceptor acceptor = createAcceptor();
    acceptor.addListener(event -> {
      if (event instanceof IContainerEvent<?>)
      {
        for (IContainerDelta<?> delta : ((IContainerEvent<?>)event).getDeltas())
        {
          if (!(delta.getElement() instanceof IConnector))
          {
            continue;
          }

          if (delta.getKind() == IContainerDelta.Kind.ADDED)
          {
            addedEvents.incrementAndGet();
            IConnector connector = (IConnector)delta.getElement();
            connector.addListener(new LifecycleEventAdapter()
            {
              @Override
              protected void onDeactivated(ILifecycle lifecycle)
              {
                deactivatedEvents.incrementAndGet();
                connectorDeactivated.countDown();
              }
            });
            Thread deactivator = new Thread(() -> {
              try
              {
                LifecycleUtil.deactivate(connector);
              }
              catch (Throwable ex)
              {
                deactivationFailure.set(ex);
              }
            }, "ws-registration-event-deactivate"); //$NON-NLS-1$
            startWorker(deactivator);
            try
            {
              if (!connectorDeactivated.await(5, TimeUnit.SECONDS))
              {
                deactivationFailure.set(new IllegalStateException("Connector did not deactivate during registration")); //$NON-NLS-1$
              }

              deactivator.join(5000);
              if (deactivator.isAlive())
              {
                deactivationFailure.set(new IllegalStateException("Connector deactivation remained blocked")); //$NON-NLS-1$
              }
            }
            catch (InterruptedException ex)
            {
              Thread.currentThread().interrupt();
              deactivationFailure.set(ex);
            }
          }
          else
          {
            removedEvents.incrementAndGet();
          }
        }
      }
    });

    FakeMessageConnection connection = new FakeMessageConnection();
    Thread accepter = new Thread(() -> {
      try
      {
        acceptor.accept(connection);
      }
      catch (Throwable ex)
      {
        acceptFailure.set(ex);
      }
    }, "ws-registration-event-accept"); //$NON-NLS-1$
    try
    {
      startWorker(accepter);
      accepter.join(10000);

      assertFalse("Acceptor did not finish registration", accepter.isAlive()); //$NON-NLS-1$
      assertTrue(acceptFailure.get() instanceof IllegalStateException);
      assertNull(deactivationFailure.get());
      assertEquals(0, acceptor.getAcceptedConnectors().length);
      assertEquals(1, addedEvents.get());
      assertEquals(1, removedEvents.get());
      assertEquals(1, deactivatedEvents.get());
      assertEquals(1, connection.aborts.get());
    }
    finally
    {
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testReentrantAcceptorShutdownDoesNotWaitForItsOwnAcceptance() throws Exception
  {
    WSAcceptor acceptor = createAcceptor();
    FakeMessageConnection connection = new FakeMessageConnection();
    connection.startAction = () -> LifecycleUtil.deactivate(acceptor);
    FutureTask<Throwable> acceptTask = new FutureTask<>(() -> {
      try
      {
        acceptor.accept(connection);
        return null;
      }
      catch (Throwable ex)
      {
        return ex;
      }
    });
    Thread acceptThread = new Thread(acceptTask, "reentrant-ws-accept"); //$NON-NLS-1$
    acceptThread.setDaemon(true);
    startWorker(acceptThread);
    Throwable failure = acceptTask.get(10, TimeUnit.SECONDS);

    assertTrue(failure instanceof IllegalStateException);
    assertFalse(acceptor.isActive());
    assertEquals(0, acceptor.getAcceptedConnectors().length);
    assertEquals(1, connection.aborts.get());
  }

  public void testNegotiationUsesWSContext() throws Exception
  {
    WSConnector connector = new WSConnector(new URI("ws://localhost/"), "test", "ws", Collections.emptyList()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.setMessageConnection(new FakeMessageConnection());
    AtomicReference<INegotiationContext> contextReference = new AtomicReference<>();
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
        contextReference.set(context);
        context.setFinished(true);
      }
    });

    try
    {
      LifecycleUtil.activate(connector);
      connector.waitForConnection(1000);
      assertEquals(ConnectorState.CONNECTED, connector.getState());
      assertTrue(contextReference.get() instanceof IWSNegotiationContext);
      IWSNegotiationContext context = (IWSNegotiationContext)contextReference.get();
      assertSame(connector, context.getConnector());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  private WSAcceptor createAcceptor()
  {
    WSAcceptor acceptor = new WSAcceptor("ws"); //$NON-NLS-1$
    acceptor.setName("test-" + UUID.randomUUID()); //$NON-NLS-1$
    acceptor.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    LifecycleUtil.activate(acceptor);
    return acceptor;
  }

  private WSConnectorFactory createConnectorFactory()
  {
    WSConnectorFactory factory = new WSConnectorFactory();
    initializeFactory(factory);
    return factory;
  }

  private void initializeFactory(WSConnectorFactory factory)
  {
    providerContainer = new ManagedContainer();
    providerFactory = new TestMessageConnectionFactory();
    providerContainer.registerFactory(providerFactory);
    providerContainer.registerFactory(factory);
    LifecycleUtil.activate(providerContainer);
    assertSame(factory, providerContainer.getFactory(WSConnectorFactory.PRODUCT_GROUP, factory.getType()));
  }

  private void startWorker(Thread worker)
  {
    synchronized (workerThreads)
    {
      if (workerRegistrationClosed)
      {
        IllegalStateException failure = new IllegalStateException("Cannot start a worker after test teardown has finished waiting"); //$NON-NLS-1$
        recordWorkerFailure(worker, failure);
        throw failure;
      }

      workerThreads.add(worker);
      worker.setUncaughtExceptionHandler((thread, failure) -> recordWorkerFailure(thread, failure));
      worker.start();
    }
  }

  private void awaitLatch(CountDownLatch latch, String context)
  {
    try
    {
      if (!latch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
      {
        AssertionError failure = new AssertionError("Timed out after " + DEFAULT_TIMEOUT + " ms waiting to " + context); //$NON-NLS-1$
        recordWorkerFailure(Thread.currentThread(), failure);
        throw failure;
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      AssertionError failure = new AssertionError("Interrupted while waiting to " + context, ex); //$NON-NLS-1$
      recordWorkerFailure(Thread.currentThread(), failure);
      throw failure;
    }
  }

  private void recordWorkerFailure(Thread thread, Throwable failure)
  {
    workerFailure.compareAndSet(null, new AssertionError("Worker thread failed: " + thread.getName(), failure)); //$NON-NLS-1$
  }

  private Throwable joinWorkers(String context, Thread... workers)
  {
    long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT);
    boolean interrupted = false;
    Throwable failure = null;

    for (Thread worker : workers)
    {
      long remaining = deadline - System.nanoTime();
      while (worker.isAlive() && remaining > 0)
      {
        try
        {
          long millis = TimeUnit.NANOSECONDS.toMillis(remaining);
          int nanos = (int)(remaining - TimeUnit.MILLISECONDS.toNanos(millis));
          worker.join(millis, nanos);
        }
        catch (InterruptedException ex)
        {
          interrupted = true;
          failure = addFailure(failure, new AssertionError("Interrupted while waiting for " + context, ex)); //$NON-NLS-1$
        }

        remaining = deadline - System.nanoTime();
      }
    }

    List<Thread> survivors = new ArrayList<>();
    for (Thread worker : workers)
    {
      if (worker.isAlive())
      {
        survivors.add(worker);
      }
    }

    if (!survivors.isEmpty())
    {
      AssertionError timeout = new AssertionError("Workers did not finish within " + DEFAULT_TIMEOUT + " ms after " + context); //$NON-NLS-1$
      workerFailure.compareAndSet(null, timeout);
      failure = addFailure(failure, timeout);
      for (Thread worker : survivors)
      {
        worker.interrupt();
      }

      long cleanupDeadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT_EXPECTED);
      for (Thread worker : survivors)
      {
        long remaining = cleanupDeadline - System.nanoTime();
        while (worker.isAlive() && remaining > 0)
        {
          try
          {
            long millis = TimeUnit.NANOSECONDS.toMillis(remaining);
            int nanos = (int)(remaining - TimeUnit.MILLISECONDS.toNanos(millis));
            worker.join(millis, nanos);
          }
          catch (InterruptedException ex)
          {
            interrupted = true;
            failure = addFailure(failure, new AssertionError("Interrupted while cleaning up " + context, ex)); //$NON-NLS-1$
          }

          remaining = cleanupDeadline - System.nanoTime();
        }
      }

      for (Thread worker : survivors)
      {
        if (worker.isAlive())
        {
          failure = addFailure(failure, new AssertionError("Worker remains alive after interruption: " + worker.getName())); //$NON-NLS-1$
        }
      }
    }

    if (interrupted)
    {
      Thread.currentThread().interrupt();
    }

    return failure;
  }

  private Throwable awaitWorkerThreads()
  {
    long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT);
    AtomicBoolean interrupted = new AtomicBoolean();
    List<Thread> observedWorkers = new ArrayList<>();
    Throwable failure = null;

    while (true)
    {
      Thread[] workers = getWorkerSnapshot();
      addUnobservedWorkers(workers, observedWorkers);

      for (Thread worker : observedWorkers)
      {
        failure = addFailure(failure, joinWorkerUntil(worker, deadline, "test teardown", interrupted)); //$NON-NLS-1$
      }

      synchronized (workerThreads)
      {
        if (workerThreads.size() == observedWorkers.size() && noWorkersAlive(workerThreads))
        {
          workerRegistrationClosed = true;
          restoreInterrupt(interrupted.get());
          return failure;
        }
      }

      if (System.nanoTime() >= deadline)
      {
        break;
      }
    }

    AssertionError timeout = new AssertionError("Workers did not finish within " + DEFAULT_TIMEOUT + " ms during test teardown"); //$NON-NLS-1$
    workerFailure.compareAndSet(null, timeout);
    failure = addFailure(failure, timeout);

    long cleanupDeadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT_EXPECTED);
    while (true)
    {
      Thread[] workers = getWorkerSnapshot();
      addUnobservedWorkers(workers, observedWorkers);

      for (Thread worker : observedWorkers)
      {
        if (worker.isAlive())
        {
          worker.interrupt();
          failure = addFailure(failure, joinWorkerUntil(worker, cleanupDeadline, "test teardown cleanup", interrupted)); //$NON-NLS-1$
        }
      }

      synchronized (workerThreads)
      {
        if (workerThreads.size() == observedWorkers.size() && noWorkersAlive(workerThreads))
        {
          workerRegistrationClosed = true;
          break;
        }

        if (System.nanoTime() >= cleanupDeadline)
        {
          workerRegistrationClosed = true;
          for (Thread worker : workerThreads)
          {
            if (worker.isAlive())
            {
              failure = addFailure(failure, new AssertionError("Worker remains alive after interruption: " + worker.getName())); //$NON-NLS-1$
            }
          }

          break;
        }
      }
    }

    restoreInterrupt(interrupted.get());
    return failure;
  }

  private Thread[] getWorkerSnapshot()
  {
    synchronized (workerThreads)
    {
      return workerThreads.toArray(new Thread[workerThreads.size()]);
    }
  }

  private static void addUnobservedWorkers(Thread[] workers, List<Thread> observedWorkers)
  {
    for (Thread worker : workers)
    {
      if (!observedWorkers.contains(worker))
      {
        observedWorkers.add(worker);
      }
    }
  }

  private static boolean noWorkersAlive(List<Thread> workers)
  {
    for (Thread worker : workers)
    {
      if (worker.isAlive())
      {
        return false;
      }
    }

    return true;
  }

  private Throwable joinWorkerUntil(Thread worker, long deadline, String context, AtomicBoolean interrupted)
  {
    Throwable failure = null;
    long remaining = deadline - System.nanoTime();
    while (worker.isAlive() && remaining > 0)
    {
      try
      {
        long millis = TimeUnit.NANOSECONDS.toMillis(remaining);
        int nanos = (int)(remaining - TimeUnit.MILLISECONDS.toNanos(millis));
        worker.join(millis, nanos);
      }
      catch (InterruptedException ex)
      {
        interrupted.set(true);
        failure = addFailure(failure, new AssertionError("Interrupted while waiting for " + context, ex)); //$NON-NLS-1$
      }

      remaining = deadline - System.nanoTime();
    }

    return failure;
  }

  private static void restoreInterrupt(boolean interrupted)
  {
    if (interrupted)
    {
      Thread.currentThread().interrupt();
    }
  }

  private void awaitWorkers(String context, Thread... workers) throws Exception
  {
    Throwable failure = addFailure(workerFailure.get(), joinWorkers(context, workers));
    rethrow(failure);
  }

  private static Throwable addFailure(Throwable primary, Throwable secondary)
  {
    if (primary == null)
    {
      return secondary;
    }

    if (secondary != null && secondary != primary)
    {
      primary.addSuppressed(secondary);
    }

    return primary;
  }

  private static void rethrow(Throwable failure) throws Exception
  {
    if (failure instanceof Exception)
    {
      throw (Exception)failure;
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }
  }

  /**
   * Supplies a synchronous provider connection for transport-free lifecycle tests.
   *
   * @author Eike Stepper
   */
  private static class FakeMessageConnection implements IMessageConnection
  {
    private final CountDownLatch started = new CountDownLatch(1);

    private final AtomicInteger aborts = new AtomicInteger();

    private final AtomicInteger gracefulCloses = new AtomicInteger();

    private final AtomicInteger readyCallbacks = new AtomicInteger();

    private boolean failStart;

    private boolean autoReady = true;

    private boolean notifyClosedOnAbort = true;

    private volatile Listener listener;

    private Runnable startAction;

    @Override
    public void start(Listener listener)
    {
      this.listener = listener;
      started.countDown();
      if (failStart)
      {
        throw new IllegalStateException("start failed"); //$NON-NLS-1$
      }

      if (autoReady)
      {
        readyCallbacks.incrementAndGet();
        listener.ready();
      }
      if (startAction != null)
      {
        startAction.run();
      }
    }

    @Override
    public boolean send(IBuffer buffer, SendCallback callback)
    {
      return false;
    }

    @Override
    public void request(long messages)
    {
    }

    @Override
    public void closeGracefully()
    {
      gracefulCloses.incrementAndGet();
      if (listener != null)
      {
        listener.closed(null);
      }
    }

    @Override
    public void abort()
    {
      aborts.incrementAndGet();
      if (notifyClosedOnAbort && listener != null)
      {
        listener.closed(null);
      }
    }
  }

  private static final class TestMessageConnectionFactory extends MessageConnectionFactory
  {
    private final AtomicInteger creations = new AtomicInteger();

    private final List<FakeMessageConnection> connections = new ArrayList<>();

    private TestMessageConnectionFactory()
    {
      super("jdk"); //$NON-NLS-1$
    }

    @Override
    public synchronized IMessageConnection create(String description)
    {
      creations.incrementAndGet();
      FakeMessageConnection connection = new FakeMessageConnection();
      connections.add(connection);
      return connection;
    }
  }

  /**
   * Captures a JDK provider connection created through the public WS factory path.
   *
   * @author Eike Stepper
   */
  private static final class CapturingJDKMessageConnectionFactory extends MessageConnectionFactory
  {
    private final JDKMessageConnectionFactory delegate = new JDKMessageConnectionFactory();

    private IMessageConnection connection;

    private CapturingJDKMessageConnectionFactory()
    {
      super("jdk"); //$NON-NLS-1$
    }

    @Override
    public IMessageConnection create(String description) throws ProductCreationException
    {
      connection = delegate.create(description);
      return connection;
    }
  }

  /**
   * Provides a minimal loopback WebSocket peer for deterministic JDK provider tests.
   *
   * @author Eike Stepper
   */
  private static final class RawWebSocketServer implements AutoCloseable
  {
    private final ServerSocket serverSocket;

    private final CountDownLatch handshake = new CountDownLatch(1);

    private final CountDownLatch outgoingFrame = new CountDownLatch(1);

    private final AtomicReference<String> request = new AtomicReference<>();

    private final AtomicReference<Throwable> failure = new AtomicReference<>();

    private final AtomicReference<byte[]> outgoing = new AtomicReference<>();

    private RawWebSocketServer() throws IOException
    {
      serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
      Thread thread = new Thread(this::serve, "jdk-ws-test-server"); //$NON-NLS-1$
      thread.setDaemon(true);
      thread.start();
    }

    private int getPort()
    {
      return serverSocket.getLocalPort();
    }

    private String getHandshake()
    {
      return request.get();
    }

    private byte[] getOutgoingFrame()
    {
      return outgoing.get();
    }

    private boolean awaitOutgoingFrame(long timeout, TimeUnit unit) throws InterruptedException
    {
      return outgoingFrame.await(timeout, unit);
    }

    private boolean awaitHandshake(long timeout, TimeUnit unit) throws InterruptedException
    {
      return handshake.await(timeout, unit);
    }

    private void serve()
    {
      try (Socket socket = serverSocket.accept())
      {
        socket.setSoTimeout(10000);
        BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int previous = 0;
        int value;
        while ((value = input.read()) >= 0)
        {
          bytes.write(value);
          if (previous == '\r' && value == '\n' && endsWithHeaderTerminator(bytes))
          {
            break;
          }

          previous = value;
        }

        String headers = bytes.toString(StandardCharsets.ISO_8859_1);
        request.set(headers);
        String key = null;
        for (String line : headers.split("\r\n")) //$NON-NLS-1$
        {
          if (line.regionMatches(true, 0, "Sec-WebSocket-Key:", 0, 18)) //$NON-NLS-1$
          {
            key = line.substring(18).trim();
          }
        }

        if (key == null)
        {
          throw new IOException("Missing Sec-WebSocket-Key"); //$NON-NLS-1$
        }

        String accept = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1") //$NON-NLS-1$
            .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.ISO_8859_1))); //$NON-NLS-1$
        OutputStream output = socket.getOutputStream();
        output.write(("HTTP/1.1 101 Switching Protocols\r\n" //$NON-NLS-1$
            + "Upgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + accept + "\r\n\r\n") //$NON-NLS-1$
                .getBytes(StandardCharsets.ISO_8859_1));
        output.flush();
        handshake.countDown();

        output.write(new byte[] { 0x02, 0x02, 0x00, 0x01 });
        output.write(new byte[] { (byte)0x80, 0x03, 0x00, 0x01, 0x42 });
        output.flush();
        int first = input.read();
        int second = input.read();
        if (first >= 0 && second >= 0)
        {
          int length = second & 0x7F;
          if (length == 126)
          {
            length = input.read() << 8 | input.read();
          }
          else if (length == 127)
          {
            throw new IOException("Unexpected large WebSocket test frame"); //$NON-NLS-1$
          }

          byte[] mask = new byte[4];
          input.readNBytes(mask, 0, mask.length);
          byte[] payload = input.readNBytes(length);
          for (int i = 0; i < payload.length; i++)
          {
            payload[i] ^= mask[i & 3];
          }

          outgoing.set(payload);
          outgoingFrame.countDown();
        }
      }
      catch (Throwable ex)
      {
        failure.set(ex);
        handshake.countDown();
        outgoingFrame.countDown();
      }
    }

    private static boolean endsWithHeaderTerminator(ByteArrayOutputStream bytes)
    {
      byte[] data = bytes.toByteArray();
      int length = data.length;
      return length >= 4 && data[length - 4] == '\r' && data[length - 3] == '\n' && data[length - 2] == '\r' && data[length - 1] == '\n';
    }

    @Override
    public void close() throws IOException
    {
      serverSocket.close();
      Throwable ex = failure.get();
      if (ex != null)
      {
        throw new IOException("Loopback WebSocket server failed", ex); //$NON-NLS-1$
      }
    }
  }
}
