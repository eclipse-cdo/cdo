/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.ws.jetty.internal.server;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.ws.IWSAcceptor;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bridges one Jetty WebSocket session to the Net4j complete-message connection contract.
 * <p>
 * Jetty owns the upgraded session; the adapter owns the connection-level coordination and transfers the open message connection to
 * {@link IWSAcceptor#accept(IMessageConnection)} when the session opens. The Net4j acceptor then creates and activates the server
 * connector. The adapter does not own the Jetty server or listener.
 * <p>
 * All connection state below is coordinated by {@link #lock}. Jetty callbacks, Net4j listener callbacks, session operations, and send
 * completions are invoked outside that monitor so that provider or application code cannot hold the state lock while re-entering the
 * connection. Consequently, termination may race with a callback already selected for delivery; terminal checks and per-operation
 * arbitration ensure that late send and receive completions do not complete the same operation twice.
 * <p>
 * Jetty delivers a complete binary WebSocket message to this adapter; the Net4j contract expects that payload to be one complete Net4j
 * frame. A higher-level CDO operation may span many Net4j frames. Net4j receive credit counts these complete messages, while Jetty
 * demand is a separate one-message callback permit. An admitted binary callback consumes one Net4j credit; its release action succeeds
 * Jetty's callback and, during graceful closure, may issue another Jetty demand so the peer's close notification can be observed. That
 * release action does not return Net4j credit.
 *
 * @author Eike Stepper
 */
public final class JettyWebSocketMessageConnection implements IMessageConnection, Session.Listener
{
  /**
   * The acceptor that assumes Net4j connector ownership after Jetty reports the session open.
   */
  private final IWSAcceptor acceptor;

  /**
   * Protects lifecycle, demand, and send arbitration; potentially reentrant collaborators are called after leaving this lock.
   */
  private final Object lock = new Object();

  /**
   * Per-send records used to arbitrate completion and termination; each remains in the set until its Net4j send callback returns, so
   * termination can settle each accepted send and its buffer borrow independently.
   */
  private final Set<PendingSend> pendingSends = new HashSet<>();

  /**
   * Installed once by {@link #start(Listener)} and used for readiness, receive, and closure notifications.
   */
  private Listener listener;

  /**
   * The Jetty-owned transport session, published on open and retained for termination and close coordination.
   */
  private Session session;

  /**
   * Unconsumed Net4j credit for complete binary messages, independent of Jetty's callback demand.
   */
  private long receiveDemand;

  /**
   * Whether a Jetty callback permit has been issued and is awaiting a callback.
   */
  private boolean jettyDemanded;

  /**
   * Prevents new sends and normal receive demand while allowing already demanded input to finish.
   */
  private boolean closing;

  /**
   * Ensures the graceful WebSocket close handshake is initiated at most once after accepted sends finish.
   */
  private boolean gracefulCloseStarted;

  /**
   * Aggregate number of accepted sends whose Net4j callbacks have not returned; graceful close waits for this count to reach zero.
   */
  private int outstandingSends;

  /**
   * Terminal state set once; it is distinct from the physical Jetty session's open/closed state.
   */
  private boolean terminated;

  /**
   * Indicates disconnect cleanup is finished, allowing sends that raced with cleanup to receive their terminal callback.
   */
  private boolean terminationCleanupComplete;

  /**
   * Primary terminal reason, with cleanup or callback failures attached as suppressed exceptions where possible.
   */
  private Throwable terminationCause;

  /**
   * Failure delivered to accepted sends terminated with the connection.
   */
  private Throwable terminationSendFailure;

  /**
   * Makes the Net4j listener's terminal notification at most once, including races with Jetty close callbacks.
   */
  private boolean closedNotified;

  /**
   * Creates a connection associated with a Net4j acceptor. The acceptor must still admit the connection when Jetty opens the session;
   * failed acceptance terminates the adapter and disconnects the session.
   *
   * @param acceptor the Net4j acceptor used to create the server connector for the opened session
   */
  public JettyWebSocketMessageConnection(IWSAcceptor acceptor)
  {
    if (acceptor == null)
    {
      throw new IllegalArgumentException("acceptor == null"); //$NON-NLS-1$
    }

    this.acceptor = acceptor;
  }

  /**
   * Installs the listener for this connection generation. The session is not ready until Jetty reports it open and the acceptor has
   * accepted the message connection. A second start, or start after termination, is an invalid state.
   *
   * @param listener the Net4j callbacks for this connection
   * @throws IllegalArgumentException if {@code listener} is {@code null}
   * @throws IllegalStateException if the connection was already started or terminated
   */
  @Override
  public void start(Listener listener)
  {
    if (listener == null)
    {
      throw new IllegalArgumentException("listener == null"); //$NON-NLS-1$
    }

    synchronized (lock)
    {
      if (this.listener != null || terminated)
      {
        throw new IllegalStateException("Connection has already been started or terminated"); //$NON-NLS-1$
      }

      this.listener = listener;
    }
  }

  /**
   * Borrows and copies an accepted Net4j frame before submitting it to Jetty. The copy lets Net4j release its buffer when this method's
   * terminal callback runs without extending the borrowed buffer's lifetime to the asynchronous network write. Only one send is
   * accepted at a time by the Net4j connector; this adapter also rejects calls while unopened, closing, or terminated.
   * <p>
   * A {@code true} result means the callback will be completed exactly once, including synchronous session/send failures and abort
   * races. A {@code false} result leaves the buffer and callback untouched. The Net4j callback is invoked outside {@link #lock}; it may
   * run before this method returns or later on whichever thread processes send completion or termination.
   *
   * @param buffer the frame borrowed while it is copied
   * @param callback completion for an accepted send
   * @return {@code true} if accepted, or {@code false} if rejected before borrowing the buffer
   */
  @Override
  public boolean send(IBuffer buffer, SendCallback callback)
  {
    Session current;
    PendingSend pending;

    synchronized (lock)
    {
      if (terminated || closing || session == null)
      {
        return false;
      }

      current = session;
      outstandingSends++;

      pending = new PendingSend(callback);
      pendingSends.add(pending);
    }

    Callback jettyCallback = Callback.from(() -> completeJettySend(pending, null), failure -> completeJettySend(pending, failure));

    try
    {
      if (!current.isOpen())
      {
        finishAcceptedSend(pending, new IOException("WebSocket session is not open"), true);
        return true;
      }

      synchronized (lock)
      {
        if (pending.terminal || terminated)
        {
          return true;
        }

        pending.copying = true;
      }

      ByteBuffer source = Net4jUtil.prepareForSending(buffer);
      ByteBuffer owned = ByteBuffer.allocate(source.remaining());
      owned.put(source).flip();

      boolean submit;
      Throwable failure;

      synchronized (lock)
      {
        pending.copying = false;
        submit = !pending.terminal && !terminated;
        failure = pending.terminal //
            ? pending.failure //
            : terminated && terminationCleanupComplete //
                ? terminationSendFailure //
                : null;

        if (!submit && !pending.terminal && terminated && terminationCleanupComplete)
        {
          pending.terminal = true;
          pending.failure = failure;
        }
      }

      if (!submit)
      {
        if (failure != null)
        {
          notifySend(pending, failure, false);
        }

        return true;
      }

      current.sendBinary(owned, jettyCallback);
    }
    catch (RuntimeException | Error ex)
    {
      finishAcceptedSend(pending, ex, true);
    }

    return true;
  }

  /**
   * Adds credit for complete inbound binary messages and asks Jetty for a callback when credit is available. Credit is consumed when a
   * demanded binary callback is admitted, not when its Net4j storage lease is released.
   *
   * @param messages positive number of additional message credits
   * @throws IllegalArgumentException if {@code messages} is not positive
   */
  @Override
  public void request(long messages)
  {
    if (messages <= 0)
    {
      throw new IllegalArgumentException("messages must be positive"); //$NON-NLS-1$
    }

    synchronized (lock)
    {
      receiveDemand = Math.min(Long.MAX_VALUE - receiveDemand, messages) + receiveDemand;
    }

    demandIfNeeded();
  }

  /**
   * Stops accepting sends and begins graceful closure after all accepted sends and their Net4j completion callbacks have settled. A
   * pending Jetty callback permit is retained: its binary callback must still be processed if it was demanded before closure, and
   * closure demand is replenished after that callback so the peer close notification can progress.
   */
  @Override
  public void closeGracefully()
  {
    Session current;

    synchronized (lock)
    {
      if (terminated || closing)
      {
        return;
      }

      closing = true;
      current = claimGracefulCloseLocked();
    }

    if (current != null)
    {
      startGracefulClose(current);
    }
  }

  /**
   * Terminates the connection and disconnects the Jetty session when present. Accepted sends are failed once cleanup has made their
   * borrowed-buffer access safe; a send still copying its buffer completes only after that copy stops. Repeated calls and late Jetty
   * callbacks do not repeat terminal notification.
   */
  @Override
  public void abort()
  {
    terminate(new IOException("WebSocket connection aborted"), true);
  }

  /**
   * Accepts a newly opened Jetty session, transfers this message connection to the configured Net4j acceptor, then announces readiness.
   * If termination won the race with open, the session is disconnected. Acceptor or listener activation failures terminate the
   * connection and disconnect the session.
   *
   * @param session the opened Jetty transport session
   */
  @Override
  public void onWebSocketOpen(Session session)
  {
    boolean reject;

    synchronized (lock)
    {
      reject = terminated;
      if (!reject)
      {
        this.session = session;
      }
    }

    if (reject)
    {
      session.disconnect();
      return;
    }

    try
    {
      acceptor.accept(this);
    }
    catch (RuntimeException | Error ex)
    {
      terminate(ex, true);
      return;
    }

    Listener target;

    synchronized (lock)
    {
      target = listener;
    }

    if (target == null)
    {
      terminate(new IllegalStateException("Accepted connection has no Net4j listener"), true); //$NON-NLS-1$
      return;
    }

    try
    {
      target.ready();
    }
    catch (RuntimeException | Error ex)
    {
      terminate(ex, true);
      return;
    }

    Session closeSession;

    synchronized (lock)
    {
      closeSession = claimGracefulCloseLocked();
    }

    if (closeSession != null)
    {
      startGracefulClose(closeSession);
    }
    else
    {
      demandIfNeeded();
    }
  }

  /**
   * Admits a complete binary WebSocket message only when Net4j receive credit is available; Jetty invokes this callback under its demand
   * protocol. The payload is expected to contain one complete Net4j frame, whose framing and size validation are performed by the Net4j
   * receive path rather than by this adapter. The read-only view is passed to Net4j with a release action that calls Jetty's callback
   * exactly once. Jetty's callback remains pending until Net4j releases its storage lease; failing it on graceful-close entry would
   * discard input for which credit was already
   * consumed.
   *
   * @param payload complete WebSocket binary-message payload
   * @param callback Jetty completion for this demanded message
   */
  @Override
  public void onWebSocketBinary(ByteBuffer payload, Callback callback)
  {
    Listener target;
    Session current;
    boolean accepted;

    synchronized (lock)
    {
      jettyDemanded = false;

      accepted = !terminated && receiveDemand > 0;
      if (accepted)
      {
        receiveDemand--;
      }

      target = listener;
      current = session;
    }

    if (!accepted)
    {
      callback.fail(new IllegalStateException("Jetty delivered a binary message without Net4j receive demand")); //$NON-NLS-1$
      terminate(new IllegalStateException("Unexpected WebSocket message"), true);
      return;
    }

    AtomicBoolean completed = new AtomicBoolean();

    Runnable release = () -> {
      if (completed.compareAndSet(false, true))
      {
        callback.succeed();
        demandForClose(current);
      }
    };

    try
    {
      target.received(payload.asReadOnlyBuffer(), release);
    }
    catch (RuntimeException | Error ex)
    {
      if (completed.compareAndSet(false, true))
      {
        callback.fail(ex);
      }

      terminate(ex, true);
    }
  }

  /**
   * Text messages cannot represent Net4j frames and therefore terminate the transport.
   */
  @Override
  public void onWebSocketText(String message)
  {
    terminate(new IllegalArgumentException("Text WebSocket messages are not supported"), true);
  }

  /**
   * Replenishes the Jetty callback demand consumed by a Ping without consuming Net4j binary receive credit. Jetty handles the
   * protocol-level Pong response independently of this adapter.
   */
  @Override
  public void onWebSocketPing(ByteBuffer payload)
  {
    demandAfterControlFrame();
  }

  /**
   * Replenishes the Jetty callback demand consumed by a Pong without consuming Net4j binary receive credit.
   */
  @Override
  public void onWebSocketPong(ByteBuffer payload)
  {
    demandAfterControlFrame();
  }

  /**
   * Propagates Jetty's transport failure through the at-most-once Net4j closure notification.
   */
  @Override
  public void onWebSocketError(Throwable cause)
  {
    terminate(cause, false);
  }

  /**
   * Completes Jetty's close callback even if Net4j closure notification throws, while recording normal versus abnormal peer closure.
   * Physical WebSocket closure and Net4j connector deactivation are separate events coordinated through this adapter.
   */
  @Override
  public void onWebSocketClose(int statusCode, String reason, Callback callback)
  {
    Throwable cause = statusCode == 1000 //
        ? null //
        : new IOException("WebSocket closed with status " + statusCode + ": " + reason);

    try
    {
      terminate(cause, false);
    }
    finally
    {
      callback.succeed();
    }
  }

  /**
   * Issues one Jetty callback demand only when a session, Net4j credit, and open receive state are present.
   */
  private void demandIfNeeded()
  {
    Session current;

    synchronized (lock)
    {
      if (terminated || closing || session == null || jettyDemanded || receiveDemand == 0)
      {
        return;
      }

      jettyDemanded = true;
      current = session;
    }

    try
    {
      current.demand();
    }
    catch (RuntimeException | Error ex)
    {
      terminate(ex, true);
    }
  }

  /**
   * Marks a control-frame callback permit as consumed, then routes the replacement through the existing normal or graceful-close
   * demand arbitration. The close path may request one callback without Net4j credit so the peer's close notification can progress.
   */
  private void demandAfterControlFrame()
  {
    Session current;

    synchronized (lock)
    {
      if (terminated || session == null)
      {
        return;
      }

      jettyDemanded = false;
      current = session;
    }

    demandIfNeeded();
    demandForClose(current);
  }

  /**
   * Starts the normal WebSocket close handshake; a failed or throwing close operation falls back to termination.
   */
  private void closeSession(Session session)
  {
    try
    {
      session.close(1000, null, Callback.from(() -> {
      }, failure -> terminate(failure, true)));
    }
    catch (RuntimeException | Error ex)
    {
      terminate(ex, true);
    }
  }

  /**
   * Replenishes the single Jetty demand used to observe close progress after an admitted binary callback completes. This is deliberately
   * independent of Net4j receive credit because no further application message is being authorized during closure; the demand permits
   * Jetty to deliver the peer's close notification.
   */
  private void demandForClose(Session session)
  {
    synchronized (lock)
    {
      if (terminated || !closing || session != this.session || jettyDemanded)
      {
        return;
      }

      jettyDemanded = true;
    }

    try
    {
      session.demand();
    }
    catch (RuntimeException | Error ex)
    {
      terminate(ex, true);
    }
  }

  /**
   * Must be called under {@link #lock}; claims the close handshake only after accepted sends and callbacks have settled.
   */
  private Session claimGracefulCloseLocked()
  {
    if (terminated || !closing || gracefulCloseStarted || outstandingSends != 0 || session == null)
    {
      return null;
    }

    gracefulCloseStarted = true;
    return session;
  }

  /**
   * Keeps close-progress demand active before starting the handshake so an in-flight demanded message cannot strand closure.
   */
  private void startGracefulClose(Session session)
  {
    demandForClose(session);
    boolean shouldClose;

    synchronized (lock)
    {
      shouldClose = !terminated && this.session == session;
    }

    if (shouldClose)
    {
      closeSession(session);
    }
  }

  /**
   * Arbitrates Jetty's asynchronous send result against termination and concurrent callback completion.
   */
  private void completeJettySend(PendingSend pending, Throwable failure)
  {
    boolean terminateConnection;

    synchronized (lock)
    {
      if (pending.terminal || pending.copying)
      {
        return;
      }

      terminateConnection = failure != null && !terminated;

      if (terminated)
      {
        if (!terminationCleanupComplete)
        {
          return;
        }

        failure = terminationSendFailure;
      }

      pending.terminal = true;
      pending.failure = failure;
    }

    notifySend(pending, failure, terminateConnection);
  }

  /**
   * Completes a send accepted before a synchronous failure, respecting cleanup and any competing terminal path.
   */
  private void finishAcceptedSend(PendingSend pending, Throwable failure, boolean terminateConnection)
  {
    boolean notify;

    synchronized (lock)
    {
      pending.copying = false;

      if (pending.terminal)
      {
        return;
      }

      if (terminated)
      {
        if (!terminationCleanupComplete)
        {
          return;
        }

        failure = terminationSendFailure;
        terminateConnection = false;
      }

      pending.terminal = true;
      pending.failure = failure;
      notify = true;
    }

    if (notify)
    {
      notifySend(pending, failure, terminateConnection);
    }
  }

  /**
   * Invokes the accepted send's terminal callback outside the lock, optionally terminates on send failure, and settles accounting even
   * if callback code throws. A callback exception is then propagated or attached to the primary transport failure.
   */
  private void notifySend(PendingSend pending, Throwable failure, boolean terminateConnection)
  {
    Throwable callbackFailure = null;

    try
    {
      if (failure == null)
      {
        pending.callback.completed();
      }
      else
      {
        pending.callback.failed(failure);
      }
    }
    catch (RuntimeException | Error ex)
    {
      callbackFailure = ex;
    }

    try
    {
      if (terminateConnection)
      {
        terminate(failure, false);
      }
    }
    catch (RuntimeException | Error ex)
    {
      callbackFailure = combineFailures(callbackFailure, ex);
    }
    finally
    {
      finishSend(pending);
    }

    if (callbackFailure != null)
    {
      if (failure != null)
      {
        addSuppressed(failure, callbackFailure);
      }
      else if (callbackFailure instanceof RuntimeException)
      {
        throw (RuntimeException)callbackFailure;
      }
      else
      {
        throw (Error)callbackFailure;
      }
    }
  }

  /**
   * Removes a send only after its Net4j callback has returned, then reevaluates whether graceful close can begin.
   */
  private void finishSend(PendingSend pending)
  {
    Session current;

    synchronized (lock)
    {
      pendingSends.remove(pending);
      outstandingSends--;
      current = claimGracefulCloseLocked();
    }

    if (current != null)
    {
      startGracefulClose(current);
    }
  }

  /**
   * Claims terminal state once, disconnects outside the lock when requested, then settles accepted sends and notifies Net4j outside the
   * lock. Sends whose source buffer is currently being copied are deferred until the copying thread has stopped reading it.
   */
  private void terminate(Throwable cause, boolean disconnect)
  {
    Session current;
    Listener target;
    List<PendingSend> sendsToFail = new ArrayList<>();

    synchronized (lock)
    {
      if (terminated)
      {
        return;
      }

      terminated = true;
      terminationCause = cause;
      terminationSendFailure = cause == null ? new IOException("WebSocket connection terminated during a send") : cause; //$NON-NLS-1$

      current = session;
      target = closedNotified ? null : listener;
      closedNotified = true;

      for (PendingSend pending : pendingSends)
      {
        if (!pending.copying && !pending.terminal)
        {
          pending.terminal = true;
          pending.failure = terminationSendFailure;
          sendsToFail.add(pending);
        }
      }
    }

    if (disconnect && current != null)
    {
      try
      {
        if (current.isOpen())
        {
          current.disconnect();
        }
      }
      catch (RuntimeException | Error ex)
      {
        synchronized (lock)
        {
          if (terminationCause == null)
          {
            terminationCause = ex;
            terminationSendFailure = ex;
          }
          else
          {
            addSuppressed(terminationCause, ex);
          }
        }
      }
    }

    synchronized (lock)
    {
      terminationCleanupComplete = true;

      for (PendingSend pending : pendingSends)
      {
        if (!pending.copying && !pending.terminal)
        {
          pending.terminal = true;
          pending.failure = terminationSendFailure;
          sendsToFail.add(pending);
        }
      }
    }

    for (PendingSend pending : sendsToFail)
    {
      try
      {
        notifySend(pending, pending.failure, false);
      }
      catch (RuntimeException | Error ex)
      {
        synchronized (lock)
        {
          if (terminationCause == null)
          {
            terminationCause = ex;
          }
          else
          {
            addSuppressed(terminationCause, ex);
          }
        }
      }
    }

    if (target != null)
    {
      target.closed(terminationCause);
    }
  }

  /**
   * Combines callback and termination failures without replacing the primary cause.
   */
  private static Throwable combineFailures(Throwable primary, Throwable secondary)
  {
    if (primary == null)
    {
      return secondary;
    }

    addSuppressed(primary, secondary);
    return primary;
  }

  /**
   * Adds a secondary failure without attempting to suppress a throwable onto itself.
   */
  private static void addSuppressed(Throwable primary, Throwable secondary)
  {
    if (primary != secondary)
    {
      primary.addSuppressed(secondary);
    }
  }

  /**
   * Tracks callback arbitration and the short interval in which the adapter reads a borrowed Net4j buffer for one send. After copying,
   * Jetty uses the adapter-owned byte array, so asynchronous transport completion no longer depends on the Net4j buffer.
   *
   * @author Eike Stepper
   */
  private static final class PendingSend
  {
    /**
     * The one Net4j terminal callback for this accepted send.
     */
    private final SendCallback callback;

    /**
     * True only while {@code send} is copying from the borrowed Net4j buffer; termination defers completion until this becomes false.
     */
    private boolean copying;

    /**
     * Set under the connection lock by whichever completion or termination path wins.
     */
    private boolean terminal;

    /**
     * Terminal failure selected by the winning path, or {@code null} for successful completion.
     */
    private Throwable failure;

    public PendingSend(SendCallback callback)
    {
      this.callback = callback;
    }
  }
}
