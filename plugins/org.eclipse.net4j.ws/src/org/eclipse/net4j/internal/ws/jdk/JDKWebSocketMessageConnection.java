/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws.jdk;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.jdk.JDKWSOptions;

import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adapts the Java 11 WebSocket client to Net4j's complete-message connection contract.
 * <p>
 * Each complete WebSocket binary message is treated as one candidate Net4j frame; WebSocket fragments are accumulated until the
 * final fragment arrives. The whole message, including the Net4j header, is limited by the immutable provider options.
 * The Net4j connector validates and interprets the frame header after delivery. Higher-level operations, including a logical CDO
 * operation carried by multiple Net4j frames, are outside this adapter's framing boundary.
 * <p>
 * One lock protects mutable connection state. WebSocket operations and listener or send-callback invocations are performed outside
 * that lock, so callbacks can race with termination and re-enter the connection. Late openings are aborted, closed notification is
 * at most once, and late send completion still ends the send's buffer borrow. Readiness and received callbacks selected before a
 * concurrent termination may race with {@code closed}; there is no total ordering between those callbacks and termination. Aborting
 * does not itself release an in-flight send's buffer; its terminal callback follows completion of the JDK send stage.
 *
 * @author Eike Stepper
 */
public final class JDKWebSocketMessageConnection implements IMessageConnection, WebSocket.Listener
{
  /**
   * Shared Provider-default client used by default provider factories and factories configured with a {@code null} SSL context.
   */
  private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  /**
   * Shared HTTP client configuration; this connection does not own or shut down the client.
   */
  private final HttpClient client;

  /**
   * Timeout for the WebSocket opening handshake, distinct from the HTTP client's connection timeout.
   */
  private final Duration webSocketConnectTimeout;

  /**
   * Immutable limits and heartbeat cadence supplied by the owning provider factory.
   */
  private final JDKWSOptions options;

  /**
   * Shared timer borrowed from the owning container or class-wide fallback; this connection never cancels it.
   */
  private final Timer heartbeatTimer;

  /**
   * Protects lifecycle, demand, fragment, and pending-send state; external calls are made after releasing it.
   */
  private final Object lock = new Object();

  /**
   * Target URI for the opening handshake.
   */
  private final URI uri;

  /**
   * Cookie header components supplied for this connection and joined when building its handshake.
   */
  private final List<String> cookies;

  /**
   * Callback recipient installed by {@link #start(Listener)}. Guarded by {@link #lock}.
   */
  private Listener listener;

  /**
   * Open WebSocket after {@link #onOpen(WebSocket)}; guarded by {@link #lock}.
   */
  private WebSocket webSocket;

  /**
   * Opening handshake future, retained so termination can cancel an unfinished upgrade. Guarded by {@link #lock}.
   */
  private CompletableFuture<WebSocket> pendingUpgrade;

  /**
   * The sole send borrowed by the JDK client until its completion stage terminates. Guarded by {@link #lock}.
   */
  private PendingSend pendingSend;

  /**
   * Records a rejected overlapping send so successful completion can signal writability. Guarded by {@link #lock}.
   */
  private boolean retryNeeded;

  /**
   * Heap accumulator for the current fragmented binary WebSocket message. Guarded by {@link #lock}.
   */
  private ByteBuffer fragments;

  /**
   * Outstanding credit for complete binary messages requested by the Net4j connector. Guarded by {@link #lock}.
   */
  private long receiveDemand;

  /**
   * Whether one WebSocket listener callback has been requested. Guarded by {@link #lock}; distinct from message credit.
   */
  private boolean callbackDemand;

  /**
   * Graceful close has stopped new sends and awaits any pending send before sending the close frame. Guarded by {@link #lock}.
   */
  private boolean closing;

  /**
   * Periodic unsolicited Pong task; guarded by {@link #lock}.
   */
  private TimerTask heartbeatTask;

  /**
   * Whether this connection has an asynchronous unsolicited Pong send outstanding; guarded by {@link #lock}.
   */
  private boolean heartbeatInFlight;

  /**
   * Terminal flag for orderly closure, failure, or abort. Guarded by {@link #lock}.
   */
  private boolean terminated;

  /**
   * Ensures at most one listener {@code closed} notification, even when termination signals race. Guarded by {@link #lock}.
   */
  private boolean closedNotified;

  /**
   * Creates a connection using the shared Provider-default client and ten-second opening-handshake timeout.
   *
   * @param uri WebSocket target URI
   * @param cookies cookie header components for this connection
   */
  public JDKWebSocketMessageConnection(URI uri, List<String> cookies)
  {
    this(uri, cookies, CLIENT, Duration.ofSeconds(10));
  }

  /**
   * Creates a connection using the given client and ten-second opening-handshake timeout.
   *
   * @param uri WebSocket target URI
   * @param cookies cookie header components for this connection
   * @param client shared HTTP client, or {@code null} to use the class-wide default client
   */
  public JDKWebSocketMessageConnection(URI uri, List<String> cookies, HttpClient client)
  {
    this(uri, cookies, client, Duration.ofSeconds(10));
  }

  /**
   * Creates a connection with the specified shared HTTP client and opening-handshake timeout. Runtime state is initialized per
   * connection; the client may be shared by other connections created by its provider factory.
   *
   * @param uri WebSocket target URI
   * @param cookies cookie header components for this connection
   * @param client shared HTTP client, or {@code null} to use the class-wide default client
   * @param webSocketConnectTimeout timeout for this connection's opening handshake
   */
  public JDKWebSocketMessageConnection(URI uri, List<String> cookies, HttpClient client, Duration webSocketConnectTimeout)
  {
    this(uri, cookies, client, webSocketConnectTimeout, null);
  }

  public JDKWebSocketMessageConnection(URI uri, List<String> cookies, HttpClient client, Duration webSocketConnectTimeout, Timer heartbeatTimer)
  {
    this(uri, cookies, client, JDKWSOptions.builder().webSocketConnectTimeout(webSocketConnectTimeout).build(), heartbeatTimer);
  }

  /**
   * Creates a connection with immutable per-provider options and a timer borrowed from its managed container.
   *
   * @param uri WebSocket target URI
   * @param cookies cookie header components
   * @param client shared HTTP client, or {@code null} for the default client
   * @param options immutable provider connection options
   * @param heartbeatTimer managed timer, or {@code null} to use the daemon fallback
   */
  public JDKWebSocketMessageConnection(URI uri, List<String> cookies, HttpClient client, JDKWSOptions options, Timer heartbeatTimer)
  {
    this.uri = uri;
    this.cookies = cookies;
    this.client = client == null ? CLIENT : client;
    this.options = Objects.requireNonNull(options, "options"); //$NON-NLS-1$
    webSocketConnectTimeout = options.getWebSocketConnectTimeout();
    this.heartbeatTimer = heartbeatTimer == null ? FallbackHeartbeatTimer.INSTANCE : heartbeatTimer;
  }

  /**
   * Installs the callback target and starts the asynchronous HTTP opening handshake. The listener's {@code ready()} callback is delivered from
   * {@link #onOpen(WebSocket)} after the JDK reports a successful upgrade. A failed upgrade terminates the connection through
   * {@code closed(Throwable)}. The acceptor route header is derived from a final path segment prefixed by
   * {@link IWSConnector#ACCEPTOR_NAME_PREFIX}; configured cookies are sent as one Cookie header.
   *
   * @param listener listener for this connection's callbacks
   * @throws IllegalStateException if start was already called or the connection has terminated
   */
  @Override
  public void start(Listener listener)
  {
    synchronized (lock)
    {
      if (this.listener != null || terminated)
      {
        throw new IllegalStateException("Connection has already been started or terminated"); //$NON-NLS-1$
      }

      this.listener = listener;
    }

    WebSocket.Builder builder = client.newWebSocketBuilder().connectTimeout(webSocketConnectTimeout);

    if (!cookies.isEmpty())
    {
      builder.header("Cookie", String.join("; ", cookies)); //$NON-NLS-1$
    }

    String path = uri.getRawPath();
    int separator = path == null ? -1 : path.lastIndexOf('/');

    String route = separator < 0 ? "" : path.substring(separator + 1); //$NON-NLS-1$
    if (route.startsWith(IWSConnector.ACCEPTOR_NAME_PREFIX) && route.length() > 1)
    {
      String acceptorName = URLDecoder.decode(route.substring(1).replace("+", "%2B"), StandardCharsets.UTF_8); //$NON-NLS-1$
      builder.header(IWSConnector.ACCEPTOR_HEADER_NAME, acceptorName);
    }

    CompletableFuture<WebSocket> upgrade = builder.buildAsync(uri, this);

    synchronized (lock)
    {
      if (!terminated && webSocket == null)
      {
        pendingUpgrade = upgrade;
      }
    }

    if (isTerminated())
    {
      upgrade.cancel(true);
    }

    upgrade.whenComplete((socket, failure) -> {
      if (failure != null)
      {
        terminate(failure);
      }
      else if (socket != null && isTerminated())
      {
        socket.abort();
      }
    });
  }

  /**
   * Attempts to submit one complete Net4j buffer as one final binary WebSocket message. The connector buffer remains borrowed until
   * the send stage's terminal callback. Only one send is pending; a competing send is rejected and causes {@code writable()}
   * after the pending send succeeds. Sends are rejected while connecting, closing, or terminated. A synchronous exception from the
   * JDK send call is reported through {@code callback.failed} and this invocation returns {@code true}, because the provider has
   * already taken responsibility for the borrow and its terminal callback. The callback can run before this method returns.
   *
   * @param buffer complete Net4j frame to borrow for transmission
   * @param callback terminal result for an accepted send
   * @return {@code true} if this send was accepted for completion, or {@code false} if it was not borrowed
   */
  @Override
  public boolean send(IBuffer buffer, SendCallback callback)
  {
    WebSocket socket;
    PendingSend send;
    ByteBuffer data;
    boolean oversized;

    synchronized (lock)
    {
      if (terminated || closing || webSocket == null)
      {
        return false;
      }

      if (pendingSend != null)
      {
        retryNeeded = true;
        return false;
      }

      data = Net4jUtil.prepareForSending(buffer).duplicate();
      send = new PendingSend(callback);
      pendingSend = send;
      socket = webSocket;
      oversized = data.remaining() > options.getMaxMessageSize();
    }

    if (oversized)
    {
      completeSend(send, new IllegalArgumentException("Net4j frame exceeds the configured WebSocket message limit")); //$NON-NLS-1$
      return true;
    }

    CompletionStage<WebSocket> completion;

    try
    {
      completion = socket.sendBinary(data, true);
    }
    catch (RuntimeException | Error ex)
    {
      completeSend(send, ex);
      return true;
    }

    completion.whenComplete((ignored, failure) -> completeSend(send, failure));
    return true;
  }

  /**
   * Adds demand for complete incoming binary messages. The count saturates at {@link Long#MAX_VALUE}; WebSocket callback demand is
   * issued one callback at a time and is continued across fragments until a complete message is assembled. Invalid non-positive
   * demand is rejected.
   *
   * @param messages positive number of additional complete messages requested
   * @throws IllegalArgumentException if {@code messages} is not positive
   */
  @Override
  public void request(long messages)
  {
    if (messages <= 0)
    {
      throw new IllegalArgumentException("messages must be positive"); //$NON-NLS-1$
    }

    WebSocket socket;

    synchronized (lock)
    {
      receiveDemand = Math.min(Long.MAX_VALUE - receiveDemand, messages) + receiveDemand;
      socket = requestNextLocked();
    }

    if (socket != null)
    {
      socket.request(1);
    }
  }

  /**
   * Stops accepting new sends and sends a normal WebSocket close after an accepted send finishes. The call is idempotent. Closure is
   * reported later through {@code closed(Throwable)} when the WebSocket reports its close event.
   */
  @Override
  public void closeGracefully()
  {
    WebSocket socket;

    synchronized (lock)
    {
      if (terminated || closing)
      {
        return;
      }

      closing = true;
      cancelHeartbeatLocked();
      socket = pendingSend == null && !heartbeatInFlight ? webSocket : null;
    }

    if (socket != null)
    {
      closeSocket();
    }
  }

  /**
   * Immediately marks the connection terminated, cancels an unfinished opening handshake, aborts an open socket, and notifies the
   * listener once. An accepted send keeps its buffer borrow until the JDK send stage completes and invokes its terminal callback,
   * which may occur after {@code abort()} returns. Repeated aborts have no additional effect.
   */
  @Override
  public void abort()
  {
    WebSocket socket;
    CompletableFuture<WebSocket> upgrade;

    synchronized (lock)
    {
      if (terminated)
      {
        return;
      }

      terminated = true;
      cancelHeartbeatLocked();
      socket = webSocket;
      upgrade = pendingUpgrade;
      pendingUpgrade = null;
    }

    if (upgrade != null && !upgrade.isDone())
    {
      upgrade.cancel(true);
    }

    if (socket != null)
    {
      socket.abort();
    }

    notifyClosed(new IllegalStateException("WebSocket connection aborted")); //$NON-NLS-1$
  }

  /**
   * Records the successful upgrade. If termination won the race, the late socket is aborted. Otherwise readiness is called outside
   * the state lock; it may race with a concurrent termination notification.
   *
   * @param webSocket upgraded socket
   */
  @Override
  public void onOpen(WebSocket webSocket)
  {
    Listener target;
    boolean close;
    boolean abort;
    Throwable heartbeatFailure = null;

    synchronized (lock)
    {
      abort = terminated;
      if (abort)
      {
        target = null;
        close = false;
      }
      else
      {
        this.webSocket = webSocket;
        pendingUpgrade = null;
        target = listener;
        close = closing;

        if (!close && heartbeatTimer != null)
        {
          try
          {
            scheduleHeartbeatLocked();
          }
          catch (RuntimeException | Error ex)
          {
            heartbeatFailure = ex;
            terminateLocked(ex);
          }
        }
      }
    }

    if (abort)
    {
      webSocket.abort();
    }
    else if (heartbeatFailure != null)
    {
      webSocket.abort();
      notifyClosed(heartbeatFailure);
    }
    else if (close)
    {
      webSocket.sendClose(WebSocket.NORMAL_CLOSURE, ""); //$NON-NLS-1$
    }
    else if (target != null)
    {
      target.ready();
    }
  }

  /**
   * Accumulates one binary fragment. The final fragment consumes one message credit and produces one copied buffer containing the
   * complete WebSocket message. The delivered {@link ByteBuffer} wraps a heap copy. Its release action advances callback demand and
   * does not recycle that storage. Oversized messages and complete messages delivered without demand terminate the connection. The
   * downstream {@link Net4jUtil#wrapBuffer(ByteBuffer, Runnable)} validates Net4j header fields and declared payload length;
   * malformed frames therefore fail in that framing layer.
   *
   * @param webSocket socket that delivered the fragment
   * @param data fragment bytes to append
   * @param last whether this is the final fragment of the WebSocket message
   * @return {@code null}; the implementation explicitly requests further listener callbacks as needed
   */
  @Override
  public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last)
  {
    ByteBuffer completed = null;
    Listener target = null;
    WebSocket request = null;
    Throwable failure = null;

    synchronized (lock)
    {
      callbackDemand = false;

      if (terminated)
      {
        return null;
      }

      int size = data.remaining();
      int currentSize = fragments == null ? 0 : fragments.position();

      if (size > options.getMaxMessageSize() - currentSize)
      {
        failure = new IllegalArgumentException("WebSocket message exceeds the Net4j frame limit"); //$NON-NLS-1$
        terminateLocked(failure);
      }

      if (failure == null && fragments == null)
      {
        fragments = ByteBuffer.allocate(Math.min(options.getMaxMessageSize(), Math.max(256, size)));
      }
      else if (failure == null && fragments.remaining() < size)
      {
        int capacity = Math.min(options.getMaxMessageSize(), Math.max(fragments.capacity() * 2, fragments.position() + size));
        ByteBuffer expanded = ByteBuffer.allocate(capacity);
        fragments.flip();
        expanded.put(fragments);
        fragments = expanded;
      }

      if (failure == null)
      {
        fragments.put(data);

        if (last && receiveDemand == 0)
        {
          failure = new IllegalStateException("WebSocket delivered a message without receive demand"); //$NON-NLS-1$
          terminateLocked(failure);
        }
        else if (last)
        {
          receiveDemand--;
          fragments.flip();
          byte[] owned = new byte[fragments.remaining()];
          fragments.get(owned);
          completed = ByteBuffer.wrap(owned);
          fragments = null;
          target = listener;
        }
        else
        {
          request = requestNextLocked();
        }
      }
    }

    if (failure != null)
    {
      webSocket.abort();
      notifyClosed(failure);
      return null;
    }

    if (request != null)
    {
      request.request(1);
    }

    if (completed != null)
    {
      AtomicBoolean released = new AtomicBoolean();
      boolean deliver;

      synchronized (lock)
      {
        deliver = !terminated;
      }

      if (deliver)
      {
        target.received(completed, () -> {
          if (released.compareAndSet(false, true))
          {
            WebSocket socket;

            synchronized (lock)
            {
              socket = requestNextLocked();
            }

            if (socket != null)
            {
              socket.request(1);
            }
          }
        });
      }
    }

    return null;
  }

  /**
   * Rejects text messages because the Net4j message contract is binary framed data.
   *
   * @param webSocket socket that delivered the message
   * @param data text fragment
   * @param last whether this is the final text fragment
   * @return {@code null}
   */
  @Override
  public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last)
  {
    terminate(new IllegalArgumentException("Text WebSocket messages are not supported")); //$NON-NLS-1$
    webSocket.abort();
    return null;
  }

  /**
   * Ignores the ping payload and resumes one-at-a-time callback demand when message credit or a fragmented message requires it.
   *
   * @param webSocket socket that delivered the ping
   * @param message ping payload
   * @return {@code null}
   */
  @Override
  public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message)
  {
    WebSocket request;

    synchronized (lock)
    {
      callbackDemand = false;
      request = terminated ? null : requestNextLocked();
    }

    if (request != null)
    {
      request.request(1);
    }

    return null;
  }

  /**
   * Resumes one-at-a-time callback demand when message credit or a fragmented message requires it.
   *
   * @param webSocket socket that delivered the pong
   * @param message pong payload
   * @return {@code null}
   */
  @Override
  public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message)
  {
    WebSocket request;

    synchronized (lock)
    {
      callbackDemand = false;
      request = terminated ? null : requestNextLocked();
    }

    if (request != null)
    {
      request.request(1);
    }

    return null;
  }

  /**
   * Terminates on peer closure, passing {@code null} for the normal close code and a failure for other status codes.
   *
   * @param webSocket socket that closed
   * @param statusCode peer close status
   * @param reason peer-provided close reason
   * @return {@code null}
   */
  @Override
  public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason)
  {
    terminate(statusCode == WebSocket.NORMAL_CLOSURE ? null : new IllegalStateException("WebSocket closed: " + reason)); //$NON-NLS-1$
    return null;
  }

  /**
   * Terminates the connection with the JDK-reported transport or protocol error.
   *
   * @param webSocket socket reporting the error, possibly {@code null}
   * @param error reported failure
   */
  @Override
  public void onError(WebSocket webSocket, Throwable error)
  {
    terminate(error);
  }

  /**
   * Claims one JDK callback credit when a complete message was requested or a fragmented message still needs continuation. Caller
   * must hold {@link #lock}; the returned socket is requested after releasing the lock.
   *
   * @return the socket to request from, or {@code null} if no callback is currently due
   */
  private WebSocket requestNextLocked()
  {
    if (!terminated && webSocket != null && !callbackDemand && (receiveDemand > 0 || fragments != null))
    {
      callbackDemand = true;
      return webSocket;
    }

    return null;
  }

  /**
   * Ends the pending buffer borrow once, publishes the send result, and then resumes writability or graceful closure as appropriate.
   * State is changed under {@link #lock}; user callbacks run after unlocking and can race with termination.
   *
   * @param send pending send record
   * @param failure completion failure, or {@code null} after successful transmission
   */
  private void completeSend(PendingSend send, Throwable failure)
  {
    boolean wasTerminated;
    boolean close;
    boolean writable;
    Listener target;

    synchronized (lock)
    {
      if (send.completed)
      {
        return;
      }

      send.completed = true;

      if (pendingSend == send)
      {
        pendingSend = null;
      }

      wasTerminated = terminated;
      close = closing && !terminated && !heartbeatInFlight;
      writable = retryNeeded && !closing && !terminated;
      retryNeeded = false;
      target = listener;
    }

    if (failure != null || wasTerminated)
    {
      send.callback.failed(failure == null ? new IllegalStateException("WebSocket terminated during send") : failure); //$NON-NLS-1$

      if (failure != null)
      {
        terminate(failure);
      }
    }
    else
    {
      send.callback.completed();

      if (close)
      {
        closeSocket();
      }
      else if (writable && target != null)
      {
        synchronized (lock)
        {
          writable = !terminated;
        }

        if (writable)
        {
          target.writable();
        }
      }
    }
  }

  /**
   * Sends the normal close frame after the pending send has completed successfully.
   */
  private void closeSocket()
  {
    WebSocket socket;

    synchronized (lock)
    {
      socket = terminated || !closing || pendingSend != null || heartbeatInFlight ? null : webSocket;
    }

    if (socket != null)
    {
      socket.sendClose(WebSocket.NORMAL_CLOSURE, ""); //$NON-NLS-1$
    }
  }

  /**
   * Claims terminal state once, discards any partial message, cancels an unfinished upgrade, aborts on abnormal closure, and
   * publishes the single closed notification. A normal peer close uses a {@code null} cause and does not abort the socket.
   *
   * @param cause abnormal termination reason, or {@code null} for orderly peer closure
   */
  private void terminate(Throwable cause)
  {
    WebSocket socket;
    CompletableFuture<WebSocket> upgrade;

    synchronized (lock)
    {
      if (terminated)
      {
        return;
      }

      terminateLocked(cause);
      socket = cause == null ? null : webSocket;
      upgrade = pendingUpgrade;
      pendingUpgrade = null;
    }

    if (upgrade != null && !upgrade.isDone())
    {
      upgrade.cancel(true);
    }

    if (socket != null)
    {
      socket.abort();
    }

    notifyClosed(cause);
  }

  private boolean isTerminated()
  {
    synchronized (lock)
    {
      return terminated;
    }
  }

  /**
   * Marks terminal state and drops incomplete receive fragments. Caller must hold {@link #lock}.
   */
  private void terminateLocked(Throwable cause)
  {
    terminated = true;
    fragments = null;
    cancelHeartbeatLocked();
  }

  private void scheduleHeartbeatLocked()
  {
    TimerTask task = new TimerTask()
    {
      @Override
      public void run()
      {
        sendHeartbeat(this);
      }
    };

    heartbeatTask = task;

    try
    {
      long interval = options.getHeartbeatInterval().toMillis();
      heartbeatTimer.schedule(task, interval, interval);
    }
    catch (RuntimeException | Error ex)
    {
      heartbeatTask = null;
      task.cancel();
      throw ex;
    }
  }

  private void sendHeartbeat(TimerTask task)
  {
    WebSocket socket;

    synchronized (lock)
    {
      if (heartbeatTask != task || terminated || closing || heartbeatInFlight || webSocket == null)
      {
        return;
      }

      heartbeatInFlight = true;
      socket = webSocket;
    }

    try
    {
      socket.sendPong(ByteBuffer.allocate(0)).whenComplete((ignored, failure) -> completeHeartbeat(failure));
    }
    catch (RuntimeException | Error ex)
    {
      completeHeartbeat(ex);
    }
  }

  private void completeHeartbeat(Throwable failure)
  {
    boolean close;
    boolean terminate;

    synchronized (lock)
    {
      heartbeatInFlight = false;
      terminate = failure != null && !(failure instanceof IllegalStateException) && !terminated;
      close = closing && !terminated && pendingSend == null;
    }

    if (terminate)
    {
      terminate(failure);
    }
    else if (close)
    {
      closeSocket();
    }
  }

  private void cancelHeartbeatLocked()
  {
    TimerTask task = heartbeatTask;
    heartbeatTask = null;

    if (task != null)
    {
      task.cancel();
    }
  }

  /**
   * Claims the one allowed closed notification under the lock and invokes the listener after unlocking.
   *
   * @param cause abnormal termination reason, or {@code null} for normal closure
   */
  private void notifyClosed(Throwable cause)
  {
    Listener target;

    synchronized (lock)
    {
      if (closedNotified)
      {
        return;
      }

      closedNotified = true;
      target = listener;
    }

    if (target != null)
    {
      target.closed(cause);
    }
  }

  /**
   * Captures one accepted send callback so completion and abort races cannot complete it twice.
   *
   * @author Eike Stepper
   */
  private static final class PendingSend
  {
    /**
     * Terminal callback paired with this send's borrowed Net4j buffer.
     */
    private final SendCallback callback;

    /**
     * Guarded by the owning connection's lock.
     */
    private boolean completed;

    public PendingSend(SendCallback callback)
    {
      this.callback = callback;
    }
  }

  /**
   * Lazily owns the single daemon fallback for connections created without a managed container.
   *
   * @author Eike Stepper
   */
  private static final class FallbackHeartbeatTimer
  {
    public static final Timer INSTANCE = createFallbackHeartbeatTimer();

    private static Timer createFallbackHeartbeatTimer()
    {
      Thread currentThread = Thread.currentThread();
      ClassLoader contextClassLoader = currentThread.getContextClassLoader();

      try
      {
        currentThread.setContextClassLoader(null);
        return new Timer("Net4j WebSocket Heartbeats", true); //$NON-NLS-1$
      }
      finally
      {
        currentThread.setContextClassLoader(contextClassLoader);
      }
    }
  }
}
