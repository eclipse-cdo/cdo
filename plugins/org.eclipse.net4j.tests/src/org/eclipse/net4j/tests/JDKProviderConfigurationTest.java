/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;

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
 * Verifies configurable JDK WebSocket message-connection providers.
 *
 * @author Eike Stepper
 */
public class JDKProviderConfigurationTest extends AbstractOMTest
{
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

    private RecordingProxySelector(Proxy proxy)
    {
      this(proxy, new AtomicInteger());
    }

    private RecordingProxySelector(Proxy proxy, AtomicInteger selections)
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

    private Origin() throws Exception
    {
      this(null, false, false);
    }

    private Origin(SSLContext context) throws Exception
    {
      this(context, false, false);
    }

    private Origin(SSLContext context, boolean authenticationRequired) throws Exception
    {
      this(context, authenticationRequired, false);
    }

    private Origin(SSLContext context, boolean authenticationRequired, boolean withholdHandshake) throws Exception
    {
      server = context == null ? new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))
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

    private int getPort()
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
    private final ServerSocket server = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));

    private final Thread worker;

    private volatile String firstLine;

    private volatile String firstRequest;

    private volatile Socket clientSocket;

    private volatile Socket targetSocket;

    private volatile Thread forwardWorker;

    private Proxy(int originPort) throws Exception
    {
      worker = new Thread(() -> {
        try (Socket socket = server.accept())
        {
          clientSocket = socket;
          socket.setSoTimeout(5000);
          String request = readHeaders(socket);
          firstRequest = request;
          firstLine = request.substring(0, request.indexOf("\r\n"));
          try (Socket target = new Socket(InetAddress.getByName("127.0.0.1"), originPort))
          {
            targetSocket = target;
            socket.getOutputStream().write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1));
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
      }, "jdk-ws-test-proxy"); //$NON-NLS-1$
      worker.setDaemon(true);
      worker.start();
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

    private int getPort()
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
  }

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
}
