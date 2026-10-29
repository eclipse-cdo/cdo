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
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSOptions;

import org.eclipse.spi.net4j.MessageConnectionFactory;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Verifies configurable JDK WebSocket message-connection providers. The ordinary provider tests are part of {@link AllTests}; Basic
 * CONNECT authentication scenarios are isolated in nested test classes because one requires the default JDK setting and the other
 * requires a JVM startup override. Run {@link DefaultProxyAuthenticationTest} without an override and
 * {@link EnabledProxyAuthenticationTest} in a separate JVM with {@code -Djdk.http.auth.tunneling.disabledSchemes=}.
 *
 * @author Eike Stepper
 */
public class JDKProviderConfigurationTest extends AbstractOMTest
{
  public JDKProviderConfigurationTest()
  {
  }

  public void testJDKWSOptionsDefaultsAndFactoryIsolation() throws Exception
  {
    JDKWSOptions defaults = JDKWSOptions.defaults();
    assertEquals(Duration.ofSeconds(10), defaults.getWebSocketConnectTimeout());
    assertEquals(Duration.ofSeconds(20), defaults.getHeartbeatInterval());
    assertEquals(Short.MAX_VALUE, defaults.getMaxMessageSize());

    JDKWSOptions firstOptions = JDKWSOptions.builder().heartbeatInterval(Duration.ofMillis(200)).maxMessageSize(1024).build();
    JDKWSOptions secondOptions = JDKWSOptions.builder().heartbeatInterval(Duration.ofMillis(500)).maxMessageSize(2048).build();
    IFactory firstFactory = JDKWSUtil.createMessageConnectionFactory("jdk-options-first", HttpClient.newHttpClient(), firstOptions); //$NON-NLS-1$
    IFactory secondFactory = JDKWSUtil.createMessageConnectionFactory("jdk-options-second", HttpClient.newHttpClient(), secondOptions); //$NON-NLS-1$
    Object firstConnection = firstFactory.create("ws://localhost/net4j"); //$NON-NLS-1$
    Object secondConnection = secondFactory.create("ws://localhost/net4j"); //$NON-NLS-1$

    java.lang.reflect.Field optionsField = firstConnection.getClass().getDeclaredField("options"); //$NON-NLS-1$
    optionsField.setAccessible(true);
    assertSame(firstOptions, optionsField.get(firstConnection));
    assertSame(secondOptions, optionsField.get(secondConnection));
  }

  public void testJDKWSOptionsRejectInvalidAndOutOfRangeValues()
  {
    assertInvalidOptions(JDKWSOptions.builder().webSocketConnectTimeout(Duration.ZERO));
    assertInvalidOptions(JDKWSOptions.builder().webSocketConnectTimeout(Duration.ofNanos(1)));
    assertInvalidOptions(JDKWSOptions.builder().heartbeatInterval(Duration.ofMillis(-1)));
    assertInvalidOptions(JDKWSOptions.builder().heartbeatInterval(Duration.ofSeconds(Long.MAX_VALUE)));
    assertInvalidOptions(JDKWSOptions.builder().maxMessageSize(IBuffer.HEADER_SIZE - 1));
    assertInvalidOptions(JDKWSOptions.builder().maxMessageSize(Short.MAX_VALUE + 1));
    assertEquals(Short.MAX_VALUE, JDKWSOptions.builder().maxMessageSize(Short.MAX_VALUE).build().getMaxMessageSize());
    assertEquals(IBuffer.HEADER_SIZE, JDKWSOptions.builder().maxMessageSize(IBuffer.HEADER_SIZE).build().getMaxMessageSize());
  }

  private static void assertInvalidOptions(JDKWSOptions.Builder builder)
  {
    try
    {
      builder.build();
      throw new AssertionError("Invalid JDK WebSocket options were accepted"); //$NON-NLS-1$
    }
    catch (IllegalArgumentException expected)
    {
    }
  }

  public void testInjectedHttpClientRoutesWebSocketThroughConfiguredProxy() throws Exception
  {
    try (Origin origin = new Origin(); Proxy proxy = new Proxy(origin.getPort()))
    {
      RecordingProxySelector selector = new RecordingProxySelector(proxy);
      HttpClient client = HttpClient.newBuilder().proxy(selector).connectTimeout(Duration.ofSeconds(3)).build();
      try
      {
        IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-proxy-ws", client, Duration.ofSeconds(3)); //$NON-NLS-1$
        connect(factory, URI.create("ws://127.0.0.1:" + origin.getPort() + "/probe")); //$NON-NLS-1$
        assertTrue("ProxySelector.select was not invoked", selector.selections.get() > 0); //$NON-NLS-1$
        assertEquals("http", selector.selectedURI.get().getScheme()); //$NON-NLS-1$
        assertEquals(java.net.Proxy.Type.HTTP, selector.selectedProxy.get().type());
        assertNull(selector.connectionFailure.get());
        assertNotNull(proxy.firstLine);
        assertTrue("Unexpected proxy request: " + proxy.firstLine, proxy.firstLine.startsWith("CONNECT 127.0.0.1:")); //$NON-NLS-1$
        assertTrue(proxy.firstRequest.contains("Host:")); //$NON-NLS-1$
        assertNotNull(origin.firstLine);
        assertTrue("Unexpected origin request: " + origin.firstLine, origin.firstLine.startsWith("GET /probe ")); //$NON-NLS-1$
      }
      finally
      {
        closeHttpClient(client);
      }
    }
  }

  public void testSecureWebSocketUsesConfiguredProxy() throws Exception
  {
    SSLContext serverContext = createSSLContext(true);
    SSLContext clientContext = createSSLContext(false);
    try (Origin origin = new Origin(serverContext); Proxy proxy = new Proxy(origin.getPort()))
    {
      RecordingProxySelector selector = new RecordingProxySelector(proxy);
      HttpClient client = HttpClient.newBuilder().proxy(selector).sslContext(clientContext).connectTimeout(Duration.ofSeconds(4)).build();
      try
      {
        IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-proxy-wss", client, Duration.ofSeconds(4)); //$NON-NLS-1$
        connect(factory, URI.create("wss://127.0.0.1:" + origin.getPort() + "/probe")); //$NON-NLS-1$
        assertTrue("ProxySelector.select was not invoked", selector.selections.get() > 0); //$NON-NLS-1$
        assertEquals("https", selector.selectedURI.get().getScheme()); //$NON-NLS-1$
        assertEquals(java.net.Proxy.Type.HTTP, selector.selectedProxy.get().type());
        assertNull(selector.connectionFailure.get());
        assertNotNull(proxy.firstLine);
        assertTrue("Unexpected proxy request: " + proxy.firstLine, proxy.firstLine.startsWith("CONNECT 127.0.0.1:")); //$NON-NLS-1$
        assertTrue(proxy.firstRequest.contains("Host:")); //$NON-NLS-1$
        assertNotNull(origin.firstLine);
        assertTrue("Unexpected origin request: " + origin.firstLine, origin.firstLine.startsWith("GET /probe ")); //$NON-NLS-1$
      }
      finally
      {
        closeHttpClient(client);
      }
    }
  }

  public void testDefaultProviderCompletesHandshake() throws Exception
  {
    try (Origin origin = new Origin())
    {
      IManagedContainer container = ContainerUtil.createInitializedContainer();
      try
      {
        MessageConnectionFactory factory = (MessageConnectionFactory)container.getFactory(MessageConnectionFactory.PRODUCT_GROUP, "jdk"); //$NON-NLS-1$
        connect(factory, URI.create("ws://127.0.0.1:" + origin.getPort() + "/default")); //$NON-NLS-1$
        assertTrue(origin.firstLine.startsWith("GET /default ")); //$NON-NLS-1$
      }
      finally
      {
        LifecycleUtil.deactivate(container);
      }
    }
  }

  public void testConfiguredAuthenticatorHandlesWSAndWSSChallenges() throws Exception
  {
    SSLContext clientContext = createSSLContext(false);
    SSLContext serverContext = createSSLContext(true);
    for (boolean secure : new boolean[] { false, true })
    {
      try (Origin origin = new Origin(secure ? serverContext : null, true))
      {
        HttpClient.Builder builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).authenticator(new java.net.Authenticator()
        {
          @Override
          protected PasswordAuthentication getPasswordAuthentication()
          {
            return new PasswordAuthentication("user", "secret".toCharArray()); //$NON-NLS-1$
          }
        });
        if (secure)
        {
          builder.sslContext(clientContext);
        }

        HttpClient client = builder.build();
        try
        {
          IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-auth-" + secure, client, Duration.ofSeconds(3)); //$NON-NLS-1$
          String scheme = secure ? "wss" : "ws"; //$NON-NLS-1$ //$NON-NLS-2$
          connect(factory, URI.create(scheme + "://127.0.0.1:" + origin.getPort() + "/auth")); //$NON-NLS-1$
          assertTrue("Expected an unauthenticated challenge followed by authenticated retry", origin.requestCount.get() >= 2); //$NON-NLS-1$
          assertEquals("Basic dXNlcjpzZWNyZXQ=", origin.authorization); //$NON-NLS-1$
        }
        finally
        {
          closeHttpClient(client);
        }
      }
    }
  }

  public void testProviderFactoriesKeepProxyConfigurationIsolated() throws Exception
  {
    try (Origin origin = new Origin(); Proxy firstProxy = new Proxy(origin.getPort()); Proxy secondProxy = new Proxy(origin.getPort()))
    {
      AtomicInteger firstSelections = new AtomicInteger();
      AtomicInteger secondSelections = new AtomicInteger();
      RecordingProxySelector firstSelector = new RecordingProxySelector(firstProxy, firstSelections);
      RecordingProxySelector secondSelector = new RecordingProxySelector(secondProxy, secondSelections);
      HttpClient firstClient = HttpClient.newBuilder().proxy(firstSelector).build();
      HttpClient secondClient = null;
      try
      {
        secondClient = HttpClient.newBuilder().proxy(secondSelector).build();
        IFactory firstFactory = JDKWSUtil.createMessageConnectionFactory("jdk-isolation-first", firstClient, Duration.ofSeconds(3)); //$NON-NLS-1$
        IFactory secondFactory = JDKWSUtil.createMessageConnectionFactory("jdk-isolation-second", secondClient, Duration.ofSeconds(3)); //$NON-NLS-1$
        IManagedContainer container = ContainerUtil.createInitializedContainer();
        try
        {
          container.registerFactory(firstFactory);
          container.registerFactory(secondFactory);
          MessageConnectionFactory registeredFirst = (MessageConnectionFactory)container.getFactory(MessageConnectionFactory.PRODUCT_GROUP,
              "jdk-isolation-first"); //$NON-NLS-1$
          MessageConnectionFactory registeredSecond = (MessageConnectionFactory)container.getFactory(MessageConnectionFactory.PRODUCT_GROUP,
              "jdk-isolation-second"); //$NON-NLS-1$
          connect(registeredFirst, URI.create("ws://127.0.0.1:" + origin.getPort() + "/first")); //$NON-NLS-1$
          connect(registeredSecond, URI.create("ws://127.0.0.1:" + origin.getPort() + "/second")); //$NON-NLS-1$
          assertTrue(firstSelections.get() > 0);
          assertTrue(secondSelections.get() > 0);
          assertEquals("/first", firstSelector.selectedURI.get().getPath()); //$NON-NLS-1$
          assertEquals("/second", secondSelector.selectedURI.get().getPath()); //$NON-NLS-1$
          assertEquals(java.net.Proxy.Type.HTTP, firstSelector.selectedProxy.get().type());
          assertEquals(java.net.Proxy.Type.HTTP, secondSelector.selectedProxy.get().type());
          assertNull(firstSelector.connectionFailure.get());
          assertNull(secondSelector.connectionFailure.get());
          assertNotNull(firstProxy.firstLine);
          assertNotNull(secondProxy.firstLine);
          assertTrue("Origin requests: " + origin.requests, origin.requests.stream().anyMatch(request -> request.startsWith("GET /first "))); //$NON-NLS-1$ //$NON-NLS-2$
          assertTrue("Origin requests: " + origin.requests, origin.requests.stream().anyMatch(request -> request.startsWith("GET /second "))); //$NON-NLS-1$ //$NON-NLS-2$
        }
        finally
        {
          LifecycleUtil.deactivate(container);
        }
      }
      finally
      {
        try
        {
          if (secondClient != null)
          {
            closeHttpClient(secondClient);
          }
        }
        finally
        {
          closeHttpClient(firstClient);
        }
      }
    }
  }

  public void testConfiguredOpeningHandshakeTimeout() throws Exception
  {
    try (Origin origin = new Origin(null, false, true))
    {
      HttpClient client = HttpClient.newHttpClient();
      IMessageConnection connection = null;
      try
      {
        IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-timeout", client, Duration.ofMillis(250)); //$NON-NLS-1$
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch closed = new CountDownLatch(1);
        connection = ((MessageConnectionFactory)factory).create("ws://127.0.0.1:" + origin.getPort() + "/withheld"); //$NON-NLS-1$
        long start = System.nanoTime();
        connection.start(new IMessageConnection.Listener()
        {
          @Override
          public void ready()
          {
            fail("Withheld handshake unexpectedly completed"); //$NON-NLS-1$
          }

          @Override
          public void received(java.nio.ByteBuffer data, Runnable releaseAction)
          {
          }

          @Override
          public void writable()
          {
          }

          @Override
          public void closed(Throwable cause)
          {
            failure.set(cause);
            closed.countDown();
          }
        });
        assertTrue("Opening-handshake timeout was not applied", closed.await(3, TimeUnit.SECONDS)); //$NON-NLS-1$
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue("Handshake failed before the configured timeout", elapsedMillis >= 150);
        assertTrue("Handshake timeout exceeded its bounded allowance", elapsedMillis < 2000);
        assertNotNull(failure.get());
      }
      finally
      {
        try
        {
          if (connection != null)
          {
            connection.abort();
          }
        }
        finally
        {
          closeHttpClient(client);
        }
      }
    }
  }

  public void testSSLContextConvenienceFactoryStillConnectsWSS() throws Exception
  {
    try (Origin origin = new Origin(createSSLContext(true)))
    {
      IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-ssl-context", createSSLContext(false)); //$NON-NLS-1$
      connect(factory, URI.create("wss://127.0.0.1:" + origin.getPort() + "/ssl-context")); //$NON-NLS-1$
      assertTrue(origin.firstLine.startsWith("GET /ssl-context ")); //$NON-NLS-1$
    }
  }

  public void testNullSSLContextCallRemainsUnambiguous()
  {
    assertNotNull(JDKWSUtil.createMessageConnectionFactory("jdk-null-ssl", null)); //$NON-NLS-1$
  }

  private static void connect(IFactory provider, URI uri) throws Exception
  {
    IMessageConnection connection = ((MessageConnectionFactory)provider).create(uri.toString());
    CountDownLatch ready = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    try
    {
      connection.start(new IMessageConnection.Listener()
      {
        @Override
        public void ready()
        {
          ready.countDown();
        }

        @Override
        public void received(java.nio.ByteBuffer data, Runnable releaseAction)
        {
        }

        @Override
        public void writable()
        {
        }

        @Override
        public void closed(Throwable cause)
        {
          failure.compareAndSet(null, cause);
        }
      });
      assertTrue("WebSocket opening handshake did not complete: " + failure.get(), ready.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    }
    finally
    {
      connection.abort();
    }
  }

  private static void closeHttpClient(HttpClient client) throws Exception
  {
    // HttpClient.close() was added in Java 21; the test project compiles against Java 11.
    HttpClient.class.getMethod("close").invoke(client); //$NON-NLS-1$
  }

  private static SSLContext createSSLContext(boolean server) throws Exception
  {
    KeyStore store = KeyStore.getInstance("JKS");
    File file = new File("sslKey/" + (server ? "testKeys" : "testTrust")); //$NON-NLS-1$ //$NON-NLS-2$
    if (!file.isFile())
    {
      file = new File("plugins/org.eclipse.net4j.tests/sslKey/" + (server ? "testKeys" : "testTrust")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    try (InputStream input = new FileInputStream(file))
    {
      store.load(input, "ab987c".toCharArray());
    }

    SSLContext context = SSLContext.getInstance("TLS");
    if (server)
    {
      KeyManagerFactory keyManagers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagers.init(store, "ab987c".toCharArray());
      context.init(keyManagers.getKeyManagers(), null, new SecureRandom());
    }
    else
    {
      TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagers.init(store);
      context.init(null, trustManagers.getTrustManagers(), new SecureRandom());
    }

    return context;
  }

  private static String readHeaders(Socket socket) throws Exception
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1));
    StringBuilder headers = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null && !line.isEmpty())
    {
      headers.append(line).append("\r\n");
    }

    return headers.toString();
  }

  private static String header(String request, String name)
  {
    for (String line : request.split("\r\n"))
    {
      if (line.regionMatches(true, 0, name + ":", 0, name.length() + 1))
      {
        return line.substring(name.length() + 1).trim();
      }
    }

    throw new IllegalStateException("Missing " + name);
  }

  private static String optionalHeader(String request, String name)
  {
    for (String line : request.split("\r\n"))
    {
      if (line.regionMatches(true, 0, name + ":", 0, name.length() + 1))
      {
        return line.substring(name.length() + 1).trim();
      }
    }

    return null;
  }

  private static void respond(Socket socket, String request) throws Exception
  {
    String accept = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1")
        .digest((header(request, "Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.ISO_8859_1)));
    OutputStream output = socket.getOutputStream();
    output.write(("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + accept + "\r\n\r\n")
        .getBytes(StandardCharsets.ISO_8859_1));
    output.flush();
  }

  /**
   * Serves local WebSocket upgrade requests over plain or secure sockets.
   *
   * @author Eike Stepper
   */
  private static void closeSocket(Socket socket) throws Exception
  {
    if (socket != null)
    {
      socket.close();
    }
  }

  private static void joinWorker(Thread worker) throws InterruptedException
  {
    if (worker != null)
    {
      worker.join(2000);

      if (worker.isAlive())
      {
        throw new IllegalStateException(worker.getName() + " did not stop"); //$NON-NLS-1$
      }
    }
  }

  /**
   * Records proxy selection for the local WebSocket tests.
   *
   * @author Eike Stepper
   */
  private static final class RecordingProxySelector extends ProxySelector
  {
    private final Proxy proxy;

    private final AtomicInteger selections;

    private final AtomicReference<URI> selectedURI = new AtomicReference<>();

    private final AtomicReference<java.net.Proxy> selectedProxy = new AtomicReference<>();

    private final AtomicReference<java.io.IOException> connectionFailure = new AtomicReference<>();

    public RecordingProxySelector(Proxy proxy)
    {
      this(proxy, new AtomicInteger());
    }

    public RecordingProxySelector(Proxy proxy, AtomicInteger selections)
    {
      this.proxy = proxy;
      this.selections = selections;
    }

    @Override
    public List<java.net.Proxy> select(URI uri)
    {
      selections.incrementAndGet();
      selectedURI.set(uri);
      java.net.Proxy result = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getLoopbackAddress(), proxy.getPort()));
      selectedProxy.set(result);
      return List.of(result);
    }

    @Override
    public void connectFailed(URI uri, java.net.SocketAddress address, java.io.IOException failure)
    {
      connectionFailure.set(failure);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Origin implements AutoCloseable
  {
    private final ServerSocket server;

    private final Thread worker;

    private volatile Socket activeSocket;

    private volatile String firstLine;

    private volatile String firstRequest;

    private volatile String authorization;

    private final AtomicInteger requestCount = new AtomicInteger();

    private final List<String> requests = Collections.synchronizedList(new ArrayList<>());

    public Origin() throws Exception
    {
      this(null, false, false);
    }

    public Origin(SSLContext context) throws Exception
    {
      this(context, false, false);
    }

    public Origin(SSLContext context, boolean authenticationRequired) throws Exception
    {
      this(context, authenticationRequired, false);
    }

    public Origin(SSLContext context, boolean authenticationRequired, boolean withholdHandshake) throws Exception
    {
      server = context == null //
          ? new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1")) //
          : (ServerSocket)context.getServerSocketFactory().createServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));

      worker = new Thread(() -> {
        while (!server.isClosed())
        {
          try (Socket socket = server.accept())
          {
            activeSocket = socket;
            socket.setSoTimeout(5000);
            String request = readHeaders(socket);
            if (firstRequest == null)
            {
              firstRequest = request;
            }

            firstLine = request.substring(0, request.indexOf("\r\n"));
            requests.add(firstLine);
            requestCount.incrementAndGet();
            authorization = optionalHeader(request, "Authorization");
            if (authenticationRequired && authorization == null)
            {
              socket.getOutputStream()
                  .write("HTTP/1.1 401 Unauthorized\r\nWWW-Authenticate: Basic realm=\"local\"\r\nContent-Length: 0\r\nConnection: close\r\n\r\n"
                      .getBytes(StandardCharsets.ISO_8859_1));
              socket.getOutputStream().flush();
            }
            else if (withholdHandshake)
            {
              socket.getInputStream().read();
            }
            else
            {
              respond(socket, request);
              socket.getInputStream().read();
            }
          }
          catch (Exception ex)
          {
            if (!server.isClosed())
            {
              ex.printStackTrace();
            }
          }
        }
      }, "jdk-ws-test-origin"); //$NON-NLS-1$
      worker.setDaemon(true);
      worker.start();
    }

    public int getPort()
    {
      return server.getLocalPort();
    }

    @Override
    public void close() throws Exception
    {
      try
      {
        server.close();
      }
      finally
      {
        try
        {
          closeSocket(activeSocket);
        }
        finally
        {
          joinWorker(worker);
        }
      }
    }
  }

  /**
   * Accepts a local HTTP CONNECT request and forwards both tunnel directions.
   *
   * @author Eike Stepper
   */
  private static final class Proxy implements AutoCloseable
  {
    private static final String AUTH_USERNAME = "proxy-user"; //$NON-NLS-1$

    private static final String AUTH_PASSWORD = "proxy-secret"; //$NON-NLS-1$

    private final ServerSocket server;

    private final Thread worker;

    private final List<String> connectRequests = Collections.synchronizedList(new ArrayList<>());

    private final List<String> proxyAuthorizations = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger challenges = new AtomicInteger();

    private final int originPort;

    private final String expectedAuthorization;

    private volatile String firstLine;

    private volatile String firstRequest;

    private volatile Socket clientSocket;

    private volatile Socket targetSocket;

    private volatile Thread forwardWorker;

    public Proxy(int originPort) throws Exception
    {
      this(originPort, null, null);
    }

    public Proxy(int originPort, boolean authenticationRequired) throws Exception
    {
      this(originPort, authenticationRequired ? AUTH_USERNAME : null, authenticationRequired ? AUTH_PASSWORD : null);
    }

    public Proxy(int originPort, String username, String password) throws Exception
    {
      this.originPort = originPort;
      expectedAuthorization = username == null //
          ? null //
          : "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.ISO_8859_1)); //$NON-NLS-2$

      server = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1")); //$NON-NLS-1$

      worker = new Thread(() -> {
        while (!server.isClosed())
        {
          try (Socket socket = server.accept())
          {
            clientSocket = socket;
            socket.setSoTimeout(5000);
            String request = readHeaders(socket);
            String requestLine = request.substring(0, request.indexOf("\r\n"));
            String authorization = optionalHeader(request, "Proxy-Authorization"); //$NON-NLS-1$
            if (firstRequest == null)
            {
              firstRequest = request;
              firstLine = requestLine;
            }

            connectRequests.add(requestLine);
            proxyAuthorizations.add(authorization);
            if (expectedAuthorization != null && !expectedAuthorization.equals(authorization))
            {
              socket.getOutputStream().write(("HTTP/1.1 407 Proxy Authentication Required\r\nProxy-Authenticate: Basic realm=\"local-proxy\"\r\n"
                  + "Content-Length: 0\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1)); //$NON-NLS-1$
              socket.getOutputStream().flush();
              challenges.incrementAndGet();
              continue;
            }

            try (Socket target = new Socket(InetAddress.getByName("127.0.0.1"), this.originPort)) //$NON-NLS-1$
            {
              targetSocket = target;
              socket.getOutputStream().write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1)); //$NON-NLS-1$
              socket.getOutputStream().flush();
              forwardWorker = new Thread(() -> copy(socket, target), "jdk-ws-test-forward"); //$NON-NLS-1$
              forwardWorker.setDaemon(true);
              forwardWorker.start();
              copy(target, socket);
            }
          }
          catch (Exception ex)
          {
            if (!server.isClosed())
            {
              ex.printStackTrace();
            }
          }
        }
      }, "jdk-ws-test-proxy"); //$NON-NLS-1$
      worker.setDaemon(true);
      worker.start();
    }

    public PasswordAuthentication createPasswordAuthentication()
    {
      return new PasswordAuthentication(AUTH_USERNAME, AUTH_PASSWORD.toCharArray());
    }

    public int getPort()
    {
      return server.getLocalPort();
    }

    @Override
    public void close() throws Exception
    {
      try
      {
        server.close();
      }
      finally
      {
        try
        {
          closeSocket(clientSocket);
        }
        finally
        {
          try
          {
            closeSocket(targetSocket);
          }
          finally
          {
            try
            {
              joinWorker(forwardWorker);
            }
            finally
            {
              joinWorker(worker);
            }
          }
        }
      }
    }

    private static void copy(Socket source, Socket target)
    {
      try
      {
        byte[] buffer = new byte[4096];
        int count;
        while ((count = source.getInputStream().read(buffer)) >= 0)
        {
          target.getOutputStream().write(buffer, 0, count);
          target.getOutputStream().flush();
        }
      }
      catch (Exception ex)
      {
        // Socket closure ends the tunnel.
      }
      finally
      {
        try
        {
          target.shutdownOutput();
        }
        catch (Exception ex)
        {
          // The opposite tunnel direction may already have closed the socket.
        }
      }
    }
  }

  /**
   * Verifies the default JDK behavior when a proxy challenges Basic authentication for a CONNECT tunnel.
   *
   * @author Eike Stepper
   */
  public static final class DefaultProxyAuthenticationTest extends AbstractOMTest
  {
    public DefaultProxyAuthenticationTest()
    {
    }

    public void testDefaultJDKConfigurationRejectsBasicAuthenticationForConnectTunnels() throws Exception
    {
      try (Origin origin = new Origin(); Proxy proxy = new Proxy(origin.getPort(), true))
      {
        RecordingProxySelector selector = new RecordingProxySelector(proxy);
        AtomicInteger authenticatorCalls = new AtomicInteger();
        HttpClient client = HttpClient.newBuilder().proxy(selector).connectTimeout(Duration.ofSeconds(3)).authenticator(new Authenticator()
        {
          @Override
          protected PasswordAuthentication getPasswordAuthentication()
          {
            authenticatorCalls.incrementAndGet();
            return proxy.createPasswordAuthentication();
          }
        }).build();
        IMessageConnection connection = null;
        try
        {
          IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-proxy-auth-default", client, Duration.ofSeconds(3)); //$NON-NLS-1$
          connection = ((MessageConnectionFactory)factory).create("ws://127.0.0.1:" + origin.getPort() + "/proxy-auth"); //$NON-NLS-1$
          CountDownLatch closed = new CountDownLatch(1);
          AtomicReference<Throwable> failure = new AtomicReference<>();
          connection.start(new IMessageConnection.Listener()
          {
            @Override
            public void ready()
            {
              fail("Basic proxy authentication unexpectedly succeeded with the JDK default disabled-scheme setting"); //$NON-NLS-1$
            }

            @Override
            public void received(java.nio.ByteBuffer data, Runnable releaseAction)
            {
            }

            @Override
            public void writable()
            {
            }

            @Override
            public void closed(Throwable cause)
            {
              failure.set(cause);
              closed.countDown();
            }
          });

          assertTrue("The 407 CONNECT response did not terminate the WebSocket opening handshake", closed.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
          assertNotNull(failure.get());
          assertEquals(1, proxy.connectRequests.size());
          assertEquals(1, proxy.challenges.get());
          assertNull(proxy.proxyAuthorizations.get(0));
          assertEquals(0, authenticatorCalls.get());
          assertTrue(origin.requests.isEmpty());
        }
        finally
        {
          try
          {
            if (connection != null)
            {
              connection.abort();
            }
          }
          finally
          {
            closeHttpClient(client);
          }
        }
      }
    }
  }

  /**
   * Verifies that Basic proxy authentication succeeds when enabled for CONNECT tunnels at JVM startup.
   *
   * @author Eike Stepper
   */
  public static final class EnabledProxyAuthenticationTest extends AbstractOMTest
  {
    public EnabledProxyAuthenticationTest()
    {
    }

    public void testAuthenticatedConnectTunnelCompletesWhenBasicTunnelingIsEnabled() throws Exception
    {
      assertEquals("Launch this test in a separate JVM with -Djdk.http.auth.tunneling.disabledSchemes=", //$NON-NLS-1$
          "", System.getProperty("jdk.http.auth.tunneling.disabledSchemes")); //$NON-NLS-1$

      try (Origin origin = new Origin(); Proxy proxy = new Proxy(origin.getPort(), true))
      {
        RecordingProxySelector selector = new RecordingProxySelector(proxy);
        AtomicInteger proxyAuthenticatorCalls = new AtomicInteger();
        AtomicReference<Authenticator.RequestorType> requestorType = new AtomicReference<>();
        HttpClient client = HttpClient.newBuilder().proxy(selector).connectTimeout(Duration.ofSeconds(3)).authenticator(new Authenticator()
        {
          @Override
          protected PasswordAuthentication getPasswordAuthentication()
          {
            requestorType.set(getRequestorType());
            proxyAuthenticatorCalls.incrementAndGet();
            return proxy.createPasswordAuthentication();
          }
        }).build();
        try
        {
          IFactory factory = JDKWSUtil.createMessageConnectionFactory("jdk-proxy-auth-enabled", client, Duration.ofSeconds(3)); //$NON-NLS-1$
          connect(factory, URI.create("ws://127.0.0.1:" + origin.getPort() + "/proxy-auth")); //$NON-NLS-1$

          assertTrue("Proxy authentication challenge was not retried", proxy.connectRequests.size() >= 2); //$NON-NLS-1$
          assertEquals(1, proxy.challenges.get());
          assertNull(proxy.proxyAuthorizations.get(0));
          assertEquals(proxy.expectedAuthorization, proxy.proxyAuthorizations.get(1));
          assertEquals(Authenticator.RequestorType.PROXY, requestorType.get());
          assertTrue("Proxy Authenticator was not consulted", proxyAuthenticatorCalls.get() > 0); //$NON-NLS-1$
          assertEquals("GET /proxy-auth HTTP/1.1", origin.firstLine); //$NON-NLS-1$
        }
        finally
        {
          closeHttpClient(client);
        }
      }
    }
  }

}
