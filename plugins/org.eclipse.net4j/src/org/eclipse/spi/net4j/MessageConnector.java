/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.security.NegotiationException;

import org.eclipse.internal.net4j.ControlChannel;
import org.eclipse.internal.net4j.bundle.OM;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multiplexes logical Net4j channels over an operator supplied asynchronous message connection. The inherited
 * {@link SchedulingConnector} scheduler selects buffers from the channels' send queues; this connector advances that scheduled work to
 * an {@link IMessageConnection} provider, which owns the transport and reports asynchronous send completion. A provider may represent
 * either an outgoing client connection or a server-side connection accepted by an independently managed server.
 * <p>
 * The connector submits at most one send transaction at a time. Scheduler completion means that a selected channel's queue head has
 * been consumed by the connector; provider acceptance is a separate event, and an accepted buffer remains pinned until the provider's
 * terminal callback proves that it no longer accesses the storage. The send pump serializes this progression without creating another
 * queue or scheduler. A rejected send retains no provider borrow and waits for a writable notification before retrying.
 * <p>
 * Incoming messages use one unit of provider demand at a time. Accepted frames enter a connection-ordered ingress queue, then per-channel
 * serial queues so a blocking handler on one channel does not stall another. Receive demand is replenished after ingress acceptance,
 * independently of handler completion and buffer release. The connector retains provider storage without copying within a per-activation
 * byte budget, then copies under a second byte budget; exhausting both fails the connection. Activation generations identify callbacks
 * and send records belonging to a particular provider binding, so stale callbacks cannot advance a later connection. Shutdown detaches
 * accepted sends but cannot reclaim their buffers until terminal completion; aborting a connection does not itself end a provider's borrow.
 *
 * @author Eike Stepper
 * @since 4.23
 */
public class MessageConnector extends SchedulingConnector
{
  private static final long DEFAULT_ZERO_COPY_RECEIVE_BUDGET = 1024L * 1024L;

  private static final long DEFAULT_COPY_RECEIVE_BUDGET = 4L * 1024L * 1024L;

  private static final int ACTIVATION_PENDING = 0;

  private static final int ACTIVATION_SUCCEEDED = 1;

  private static final int ACTIVATION_FAILED = 2;

  private static final int RECEIVE_ENQUEUE_REJECTED = 0;

  private static final int RECEIVE_ENQUEUE_QUEUED = 1;

  private static final int RECEIVE_ENQUEUE_START = 2;

  /**
   * Serializes bounded generation-dependent connector and scheduler actions against activation-epoch invalidation.
   */
  private final Object activationLock = new Object();

  /**
   * Protects connector transport state and send-record transitions; provider and lifecycle calls are made after releasing it.
   */
  private final Object transportLock = new Object();

  private final long zeroCopyReceiveBudgetBytes;

  private final long copyReceiveBudgetBytes;

  private IMessageConnection connection;

  private Location location = Location.CLIENT;

  /**
   * True after the provider reports readiness for the current activation epoch.
   */
  private volatile boolean ready;

  /**
   * Defers readiness processing until provider startup and connector activation have returned.
   */
  private boolean activationComplete;

  private Throwable activationFailure;

  /**
   * Whether abort has already been requested for the current provider generation.
   */
  private boolean abortIssued;

  /**
   * Activation completion decision guarded by activationLock.
   */
  private volatile int activationOutcome;

  /**
   * A provider closed after success was claimed but before lifecycle activation finished.
   */
  private boolean terminationPending;

  /**
   * A generation claimed by a queued lifecycle deactivation.
   */
  private long deactivationClaim;

  /**
   * Stops current-generation sends and receive demand during shutdown or transport failure.
   */
  private volatile boolean closing;

  /**
   * Monotonically increasing connector activation epoch, unrelated to channel or protocol versions.
   */
  private volatile long generation;

  /**
   * A wakeup arrived while the pump was scheduled or running and may require another pass.
   */
  private boolean pumpRequested;

  /**
   * A pump task has been queued on the receive executor.
   */
  private boolean pumpScheduled;

  /**
   * A pump task is currently evaluating a scheduled send transaction.
   */
  private boolean pumpRunning;

  /**
   * The sole selected send transaction; it may still be evaluating submission rather than borrowed by the provider.
   */
  private InFlight inFlight;

  /**
   * The current provider rejected its last submission and has not yet reported writability.
   */
  private boolean rejected;

  /**
   * Whether one incoming message has been requested and has not yet been delivered.
   */
  private boolean receiveCredit;

  private ReceiveAccounting receiveAccounting;

  private final Map<InternalChannel, ChannelReceiveQueue> channelReceiveQueues = new IdentityHashMap<>();

  private final ArrayDeque<IBuffer> receiveQueue = new ArrayDeque<>();

  private final ArrayDeque<IBuffer> pendingReceiveQueue = new ArrayDeque<>();

  private boolean receiveDispatchScheduled;

  private long nextReceiveControlSequence;

  private long receiveControlBarrierSequence;

  private short receiveControlBarrierChannelID = IBuffer.NO_CHANNEL;

  private byte receiveControlBarrierOpcode;

  /**
   * A receive-demand replenishment task is queued on the receive executor.
   */
  private boolean receiveRequestScheduled;

  /**
   * The caller requested drain-before-close after stopping further message production.
   */
  private boolean gracefulCloseRequested;

  /**
   * The provider's orderly close operation has been initiated after the scheduler drained.
   */
  private boolean gracefulCloseStarted;

  /**
   * Creates a message connector with bounded per-activation receive storage budgets.
   * <p>
   * The default budgets allow one megabyte of outstanding zero-copy storage and four megabytes of copied receive storage per connection.
   * When the zero-copy budget is full, incoming messages are copied while copy budget remains. Exhausting both budgets fails the
   * connection.
   *
   * @param zeroCopyReceiveBudgetBytes maximum bytes retained from provider-owned receive storage
   * @param copyReceiveBudgetBytes maximum bytes retained in copied receive storage
   * @throws IllegalArgumentException if either budget is negative
   * @since 4.23
   */
  public MessageConnector(long zeroCopyReceiveBudgetBytes, long copyReceiveBudgetBytes)
  {
    if (zeroCopyReceiveBudgetBytes < 0 || copyReceiveBudgetBytes < 0)
    {
      throw new IllegalArgumentException("Receive budgets must not be negative"); //$NON-NLS-1$
    }

    this.zeroCopyReceiveBudgetBytes = zeroCopyReceiveBudgetBytes;
    this.copyReceiveBudgetBytes = copyReceiveBudgetBytes;
  }

  /**
   * Creates a message connector with the default per-connection receive budgets.
   *
   * @since 4.23
   */
  public MessageConnector()
  {
    this(DEFAULT_ZERO_COPY_RECEIVE_BUDGET, DEFAULT_COPY_RECEIVE_BUDGET);
  }

  /**
   * Sets the provider connection before activation. The supplied connection may initiate a client connection or attach to an already
   * accepted server connection.
   *
   * @param connection the provider connection to bind
   */
  public final void setConnection(IMessageConnection connection)
  {
    checkInactive();
    if (connection == null)
    {
      throw new IllegalArgumentException("connection == null"); //$NON-NLS-1$
    }

    this.connection = connection;
  }

  /**
   * Updates this connector's transport and negotiation state. A {@link ConnectorState#CONNECTED} transition is accepted only for the
   * current ready provider generation and is arbitrated with deferred activation failure; other state transitions use the inherited
   * connector behavior.
   *
   * @param newState the state to publish
   * @throws ConnectorException if a connector operation required by the transition fails
   */
  @Override
  public void setState(ConnectorState newState) throws ConnectorException
  {
    if (newState == ConnectorState.CONNECTED)
    {
      IMessageConnection source;
      long callbackGeneration;
      synchronized (transportLock)
      {
        source = connection;
        callbackGeneration = generation;
      }

      completeConnection(source, callbackGeneration);
      return;
    }

    super.setState(newState);
  }

  /**
   * Returns the configured client or server role used for signed Net4j channel identifiers.
   *
   * @return this connector's configured location
   * @since 4.23
   */
  @Override
  public final Location getLocation()
  {
    return location;
  }

  /**
   * Sets whether this connector represents a Net4j client or a server-side accepted connection. The server itself remains owned and
   * managed by its provider.
   *
   * @param location {@link org.eclipse.net4j.ILocationAware.Location#CLIENT ILocationAware.Location#CLIENT} for an outgoing client, or {@link org.eclipse.net4j.ILocationAware.Location#SERVER ILocationAware.Location#SERVER} for an accepted server connection
   * @since 4.23
   */
  public final void setLocation(Location location)
  {
    checkInactive();
    if (location == null)
    {
      throw new IllegalArgumentException("location == null"); //$NON-NLS-1$
    }

    this.location = location;
  }

  /**
   * Routes queued receive frames after a channel has been added to the multiplexer. The map update occurs before this container event,
   * so a dependent data frame can resolve its channel even if protocol connection setup continues with a nested signal.
   *
   * @param channel channel registered with this connector
   */
  @Override
  public void fireElementAddedEvent(IChannel channel)
  {
    receiveChannelChanged(channel, true);
    super.fireElementAddedEvent(channel);
  }

  /**
   * Removes a channel's receive queue after the multiplexer has removed it, releasing any frames that can no longer be delivered.
   *
   * @param channel channel removed from this connector
   */
  @Override
  public void fireElementRemovedEvent(IChannel channel)
  {
    receiveChannelChanged(channel, false);
    super.fireElementRemovedEvent(channel);
  }

  /**
   * Drains currently queued Net4j messages and then requests graceful local closure of the underlying connection. Callers should stop
   * producing new messages before invoking this method.
   */
  public final void closeGracefully()
  {
    IMessageConnection current;

    synchronized (transportLock)
    {
      gracefulCloseRequested = true;
      current = ready ? connection : null;
    }

    if (current != null)
    {
      schedulePump();
    }
  }

  @Override
  protected void onSendingRequired()
  {
    schedulePump();
  }

  @Override
  protected void onSendingNoLongerRequired()
  {
    // The message provider has no write-interest switch; work resumes from a completion or writable callback.
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    IMessageConnection contextConnection;
    long contextGeneration;

    synchronized (transportLock)
    {
      contextConnection = connection;
      contextGeneration = generation;
    }

    return new NegotiationContext()
    {
      private IBuffer buffer;

      @Override
      public ByteBuffer getBuffer()
      {
        buffer = provideBuffer();
        ByteBuffer bytes = buffer.startPutting(CONTROL_CHANNEL_ID);
        bytes.put(CONTROL_OPCODE_NEGOTIATION);
        return bytes;
      }

      @Override
      public void transmitBuffer(ByteBuffer buffer)
      {
        if (this.buffer == null || this.buffer.getByteBuffer() != buffer)
        {
          throw new IllegalArgumentException("The passed buffer is not the last that was produced"); //$NON-NLS-1$
        }

        IBuffer outgoing = this.buffer;
        this.buffer = null;

        try
        {
          getInternalControlChannel().sendBuffer(outgoing);
        }
        catch (RuntimeException | Error ex)
        {
          if (outgoing.getState() != BufferState.RELEASED)
          {
            outgoing.release();
          }

          throw ex;
        }
      }

      @Override
      public void setUserID(String userID)
      {
        MessageConnector.this.setUserID(userID);
      }

      @Override
      public void setFinished(boolean success)
      {
        if (success)
        {
          if (!completeConnection(contextConnection, contextGeneration))
          {
            return;
          }
        }
        else
        {
          if (!isCurrentConnection(contextConnection, contextGeneration))
          {
            return;
          }

          Object info = getInfo();
          NegotiationException failure = info instanceof NegotiationException //
              ? (NegotiationException)info //
              : new NegotiationException("Message connector negotiation failed" + (info == null ? "" : ": " + info)); //$NON-NLS-1$ //$NON-NLS-2$
          setNegotiationException(failure);
        }

        super.setFinished(success);

        if (!success)
        {
          ConcurrencyUtil.execute(getConfig().getReceiveExecutor(), MessageConnector.this::deactivate);
        }
      }
    };
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    synchronized (activationLock)
    {
      if (deactivationClaim != 0)
      {
        throw new ConnectorException("A previous connection generation is still being deactivated"); //$NON-NLS-1$
      }

      activationOutcome = ACTIVATION_PENDING;
      terminationPending = false;
    }

    super.doBeforeActivate();
    if (connection == null)
    {
      throw new IllegalStateException("connection == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    long currentGeneration;
    IMessageConnection current;

    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        currentGeneration = ++generation;
        ready = false;
        activationComplete = false;
        activationFailure = null;
        abortIssued = false;
        activationOutcome = ACTIVATION_PENDING;
        terminationPending = false;
        closing = false;
        rejected = false;
        receiveCredit = false;
        receiveAccounting = new ReceiveAccounting(zeroCopyReceiveBudgetBytes, copyReceiveBudgetBytes);
        receiveQueue.clear();
        pendingReceiveQueue.clear();
        channelReceiveQueues.clear();
        receiveDispatchScheduled = false;
        nextReceiveControlSequence = 0;
        receiveControlBarrierSequence = 0;
        receiveControlBarrierChannelID = IBuffer.NO_CHANNEL;
        receiveControlBarrierOpcode = 0;
        receiveRequestScheduled = false;
        gracefulCloseRequested = false;
        gracefulCloseStarted = false;
        pumpRequested = false;
        pumpScheduled = false;
        pumpRunning = false;
        inFlight = null;
        current = connection;
      }
    }

    try
    {
      current.start(new ConnectionListener(current, currentGeneration));

      boolean becomeReady;
      Throwable failure;

      synchronized (transportLock)
      {
        if (!isCurrent(current, currentGeneration))
        {
          return;
        }

        activationComplete = true;
        becomeReady = ready && !closing;
        failure = activationFailure;
      }

      if (failure != null)
      {
        throw new ConnectorException("Message connection closed during activation", failure); //$NON-NLS-1$
      }

      if (becomeReady)
      {
        connectionReady(current, currentGeneration);
      }
    }
    catch (RuntimeException | Error ex)
    {
      boolean abortCurrent;
      boolean currentAttempt;

      synchronized (activationLock)
      {
        synchronized (transportLock)
        {
          currentAttempt = isCurrent(current, currentGeneration);
          if (currentAttempt)
          {
            ++generation;
            ready = false;
            closing = true;
            receiveCredit = false;
            receiveRequestScheduled = false;
            connection = null;
            abortCurrent = !abortIssued;
            abortIssued = true;
          }
          else
          {
            abortCurrent = false;
          }
        }
      }

      if (!currentAttempt)
      {
        throw ex;
      }

      closeReceiveQueues();

      try
      {
        if (abortCurrent)
        {
          current.abort();
        }
      }
      catch (RuntimeException | Error abortException)
      {
        ex.addSuppressed(abortException);
      }

      try
      {
        super.doDeactivate();
      }
      catch (Exception cleanupException)
      {
        ex.addSuppressed(cleanupException);
      }

      throw ex;
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IMessageConnection current;
    InFlight pending;
    boolean abort;
    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        ++generation;
        ready = false;
        closing = true;
        receiveCredit = false;
        receiveRequestScheduled = false;
        gracefulCloseRequested = false;
        gracefulCloseStarted = false;
        rejected = false;

        pending = inFlight;
        if (pending != null)
        {
          pending.detached = true;
        }

        inFlight = null;
        pumpRequested = false;
        pumpRunning = false;
        pumpScheduled = false;
        current = connection;
        connection = null;
        abort = current != null && !abortIssued;
        abortIssued = true;
      }
    }

    closeReceiveQueues();

    try
    {
      if (abort)
      {
        current.abort();
      }

      // The send record owns its pin until a rejection or terminal callback proves provider access has ended.
    }
    finally
    {
      super.doDeactivate();
    }
  }

  @Override
  protected void doCancelActivation()
  {
    terminatePendingActivation();
  }

  /**
   * Coalesces wakeups and queues one serialized pass on the receive executor. The pump is a mechanism for advancing scheduler work,
   * not a thread, queue, or second scheduler. Its task captures the activation generation so a queued old wakeup cannot start work on
   * a later provider binding; executor submission and the send itself happen outside {@code transportLock}.
   */
  private void schedulePump()
  {
    long expectedGeneration;

    synchronized (transportLock)
    {
      expectedGeneration = generation;
    }

    schedulePump(expectedGeneration);
  }

  /**
   * Queues pump work only while the supplied activation epoch remains current.
   *
   * @param expectedGeneration activation epoch that requested the pump
   */
  private void schedulePump(long expectedGeneration)
  {
    ExecutorService executor = getConfig().getReceiveExecutor();
    long scheduledGeneration = expectedGeneration;

    synchronized (transportLock)
    {
      if (generation != expectedGeneration)
      {
        return;
      }

      if (pumpRunning || pumpScheduled)
      {
        pumpRequested = true;
        return;
      }

      if (!ready //
          || closing //
          || gracefulCloseStarted //
          || inFlight != null //
          || rejected //
          || !isSendPumpAvailable())
      {
        return;
      }

      pumpScheduled = true;
    }

    try
    {
      ConcurrencyUtil.execute(executor, () -> {
        synchronized (transportLock)
        {
          if (generation != scheduledGeneration)
          {
            return;
          }

          pumpScheduled = false;

          if (!ready //
              || closing //
              || gracefulCloseStarted //
              || inFlight != null //
              || rejected //
              || !isSendPumpAvailable())
          {
            pumpRequested = false;
            return;
          }

          pumpRunning = true;
        }

        try
        {
          sendNext(scheduledGeneration);
        }
        catch (RuntimeException | Error ex)
        {
          transportFailed(scheduledGeneration, ex);
        }
      });
    }
    catch (RuntimeException ex)
    {
      synchronized (transportLock)
      {
        if (generation == scheduledGeneration)
        {
          pumpScheduled = false;
        }
      }

      transportFailed(scheduledGeneration, ex);
    }
  }

  private boolean isSendPumpAvailable()
  {
    return isActive() || isNegotiating();
  }

  /**
   * Selects and submits at most one scheduled channel buffer for the expected activation generation. The selected queue head is pinned
   * while it remains in the channel queue, and the transaction is installed under {@code transportLock} before provider code is called.
   * Provider submission occurs without that lock because it may block or invoke its callback synchronously.
   *
   * @param expectedGeneration activation epoch captured by the pump task
   */
  private void sendNext(long expectedGeneration)
  {
    IMessageConnection current;
    InternalChannel channel;
    Queue<IBuffer> queue = null;
    IBuffer buffer = null;
    InFlight send = null;
    boolean selected = false;
    boolean applicationBufferDeferred = false;

    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        if (generation != expectedGeneration //
            || !ready //
            || closing //
            || gracefulCloseStarted //
            || !isSendPumpAvailable() //
            || inFlight != null //
            || rejected)
        {
          return;
        }

        current = connection;
      }

      channel = selectScheduledChannel();
      if (channel == null)
      {
        stopSendingIfIdle();
      }
      else if (!isActive() && channel != getInternalControlChannel())
      {
        // Keep application buffers queued until deferred negotiation activation completes.
        applicationBufferDeferred = true;
      }
      else
      {
        queue = channel.getSendQueue();

        synchronized (queue == null ? transportLock : queue)
        {
          if (queue != null && channel.isActive() && channel.getSendQueue() == queue)
          {
            buffer = queue.peek();
            if (buffer != null && channel instanceof Channel && !((Channel)channel).pinSendBuffer(buffer))
            {
              buffer = null;
            }
          }
        }

        if (buffer != null)
        {
          send = new InFlight(expectedGeneration, channel, queue, buffer, buffer.isCCAM());

          synchronized (transportLock)
          {
            selected = generation == expectedGeneration && !closing && inFlight == null && ready;
            if (selected)
            {
              inFlight = send;
            }
            else
            {
              send.detached = true;
            }
          }
        }
      }
    }

    if (applicationBufferDeferred)
    {
      finishPump(expectedGeneration, false);
      return;
    }

    if (channel == null)
    {
      if (isActive())
      {
        startGracefulCloseIfIdle(expectedGeneration);
      }

      finishPump(expectedGeneration, true);
      return;
    }

    if (buffer == null)
    {
      finishPump(expectedGeneration, true);
      return;
    }

    if (!selected)
    {
      finishSendOwnership(send);
      finishPump(expectedGeneration, false);
      return;
    }

    try
    {
      boolean submit;

      synchronized (transportLock)
      {
        submit = !closing && generation == expectedGeneration && inFlight == send;
        if (submit)
        {
          send.state = SendState.SUBMITTING;
        }
        else
        {
          send.submitting = false;
          send.state = SendState.REJECTED;
        }
      }

      if (!submit)
      {
        finishSendOwnership(send);
        finishPump(send.generation, false);
        return;
      }

      try
      {
        Net4jUtil.prepareForSending(buffer);
      }
      catch (RuntimeException | Error ex)
      {
        finishSubmission(send, false, ex);
        return;
      }

      boolean accepted;

      try
      {
        accepted = current.send(buffer, new SendCallback(send));
      }
      catch (RuntimeException | Error ex)
      {
        finishSubmission(send, false, ex);
        return;
      }

      finishSubmission(send, accepted, null);
    }
    catch (RuntimeException | Error ex)
    {
      transportFailed(expectedGeneration, ex);
    }
  }

  /**
   * Records the result of evaluating one provider submission. Rejection ends any provider borrow and permits ownership cleanup;
   * acceptance retains the pinned buffer until its terminal callback, including when that callback arrived before {@code send()}
   * returned. Only a still-current transaction may update the current generation's rejection state or failure lifecycle.
   *
   * @param send the captured transaction
   * @param accepted whether the provider accepted the buffer
   * @param failure a submission error, if any
   */
  private void finishSubmission(InFlight send, boolean accepted, Throwable failure)
  {
    boolean completeNow = false;
    boolean releaseNow = false;
    boolean detached;
    boolean currentGeneration;

    synchronized (transportLock)
    {
      send.submitting = false;
      send.accepted = accepted;
      detached = send.detached;
      currentGeneration = send.generation == generation && inFlight == send && !detached;

      if (!accepted)
      {
        send.state = SendState.REJECTED;

        if (inFlight == send)
        {
          inFlight = null;
        }

        if (failure == null && currentGeneration)
        {
          rejected = true;
        }

        releaseNow = true;
      }
      else
      {
        send.state = SendState.ACCEPTED;
        completeNow = send.callbackSeen;
      }
    }

    if (releaseNow)
    {
      if (detached)
      {
        finishSendOwnership(send);
      }
      else if (send.channel instanceof Channel)
      {
        ((Channel)send.channel).finishPinnedSendBuffer(send.queue, send.buffer, false);
      }

      finishPump(send.generation, false);
    }

    if (failure != null)
    {
      transportFailed(send.generation, failure);
    }
    else if (completeNow)
    {
      processCompletion(send, send.completion);
    }
  }

  /**
   * Records the provider's terminal callback, which may race with or precede the return from {@code send()}. The callback is accepted
   * once per transaction; if submission is still being evaluated, completion processing is deferred until its outcome is known.
   * This method may receive an obsolete transaction and leaves its captured ownership available for cleanup.
   *
   * @param send the transaction associated with the callback
   * @param failure the terminal failure, or {@code null} after successful transmission
   */
  private void providerCompleted(InFlight send, Throwable failure)
  {
    synchronized (transportLock)
    {
      if (send.callbackSeen || send.state == SendState.TERMINAL || send.state == SendState.REJECTED)
      {
        return;
      }

      send.callbackSeen = true;
      send.completion = failure;

      if (send.submitting)
      {
        return;
      }

      if (!send.accepted)
      {
        return;
      }
    }

    processCompletion(send, failure);
  }

  /**
   * Completes the transaction after provider access has ended. Captured buffer and queue cleanup is generation-independent and always
   * runs exactly once; scheduler, channel, and connector effects are permitted only while the transaction still belongs to the active
   * generation. State inspection is protected by {@code transportLock}, while release and lifecycle work is performed outside it.
   *
   * @param send the completed transaction
   * @param failure the terminal failure, or {@code null} on success
   */
  private void processCompletion(InFlight send, Throwable failure)
  {
    boolean currentGeneration;

    synchronized (transportLock)
    {
      if (send.state == SendState.TERMINAL)
      {
        return;
      }

      send.state = SendState.TERMINAL;
      currentGeneration = send.generation == generation && inFlight == send && !send.detached;

      if (inFlight == send)
      {
        inFlight = null;
      }
    }

    finishSendOwnership(send);

    if (failure != null)
    {
      if (currentGeneration)
      {
        transportFailed(send.generation, failure);
      }

      return;
    }

    if (!currentGeneration)
    {
      return;
    }

    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        currentGeneration = send.generation == generation && !closing;
      }

      if (!currentGeneration)
      {
        return;
      }

      if (send.closeChannelAfterMe && send.channel.isActive())
      {
        send.channel.close();
      }

      completeScheduledChannel(send.channel, true);
    }

    finishPump(send.generation, true);
  }

  /**
   * Ends this transaction's buffer ownership once, using the queue captured at selection time even if channel lifecycle has since
   * replaced or cleared its active send queue. This is deliberately valid after shutdown and after a later activation because it touches
   * only the old transaction's queue and pinned buffer.
   *
   * @param send the transaction whose provider borrow has ended or whose submission was rejected
   */
  private void finishSendOwnership(InFlight send)
  {
    if (send.ownershipFinished.compareAndSet(false, true))
    {
      if (send.channel instanceof Channel)
      {
        ((Channel)send.channel).finishPinnedSendBuffer(send.queue, send.buffer, true);
      }
      else
      {
        synchronized (send.queue)
        {
          send.queue.remove(send.buffer);
          send.buffer.release();
        }
      }
    }
  }

  /**
   * Ends the current pump pass only if its activation epoch is still current, then schedules a coalesced follow-up when requested.
   *
   * @param expectedGeneration activation epoch that owns the pass
   * @param allowPending whether a recorded wakeup should start another pass
   */
  private void finishPump(long expectedGeneration, boolean allowPending)
  {
    boolean rerun;

    synchronized (transportLock)
    {
      if (generation != expectedGeneration)
      {
        return;
      }

      pumpRunning = false;
      rerun = allowPending && pumpRequested;
      pumpRequested = false;
    }

    if (rerun)
    {
      schedulePump();
    }
  }

  /**
   * Accepts provider messages into a bounded byte-accounted queue. Provider demand is replenished as soon as a frame is safely queued;
   * channel handlers run on per-channel serial workers, so one blocking handler cannot stall receive progress on another channel.
   *
   * @param source provider that delivered the frame
   * @param callbackGeneration activation epoch associated with the listener
   * @param data complete framed message storage
   * @param releaseAction returns the provider's storage lease
   */
  private void receive(IMessageConnection source, long callbackGeneration, ByteBuffer data, Runnable releaseAction)
  {
    ReceiveAccounting accounting;
    boolean discard;

    synchronized (transportLock)
    {
      discard = !isCurrent(source, callbackGeneration) || closing || !receiveCredit;
      if (!discard)
      {
        receiveCredit = false;
      }

      accounting = receiveAccounting;
    }

    if (discard)
    {
      runRelease(releaseAction);
      return;
    }

    int bytes = data.remaining();
    boolean copy;

    if (accounting.reserveZeroCopy(bytes))
    {
      copy = false;
    }
    else if (accounting.reserveCopy(bytes))
    {
      copy = true;
    }
    else
    {
      IllegalStateException failure = accounting.createExhaustionFailure(bytes);

      try
      {
        runRelease(releaseAction);
      }
      catch (RuntimeException | Error releaseFailure)
      {
        failure.addSuppressed(releaseFailure);
      }

      transportFailed(callbackGeneration, failure);
      return;
    }

    IBuffer buffer;

    if (copy)
    {
      ByteBuffer owned;

      try
      {
        owned = ByteBuffer.allocate(bytes);
        owned.put(data.duplicate()).flip();
        runRelease(releaseAction);
      }
      catch (RuntimeException | Error ex)
      {
        accounting.releaseCopy(bytes);
        transportFailed(callbackGeneration, ex);
        return;
      }

      try
      {
        buffer = Net4jUtil.wrapBuffer(owned, once(() -> accounting.releaseCopy(bytes)));
      }
      catch (RuntimeException | Error ex)
      {
        transportFailed(callbackGeneration, ex);
        return;
      }
    }
    else
    {
      Runnable release = once(() -> {
        try
        {
          runRelease(releaseAction);
        }
        finally
        {
          accounting.releaseZeroCopy(bytes);
        }
      });

      try
      {
        buffer = Net4jUtil.wrapBuffer(data, release);
      }
      catch (RuntimeException | Error ex)
      {
        transportFailed(callbackGeneration, ex);
        return;
      }
    }

    boolean accepted;
    boolean scheduleDispatch;

    synchronized (transportLock)
    {
      if (!isCurrent(source, callbackGeneration) || closing)
      {
        accepted = false;
        scheduleDispatch = false;
      }
      else
      {
        accepted = true;
        receiveQueue.addLast(buffer);

        scheduleDispatch = !receiveDispatchScheduled;
        if (scheduleDispatch)
        {
          receiveDispatchScheduled = true;
        }
      }
    }

    if (!accepted)
    {
      buffer.release();
      return;
    }

    List<ChannelReceiveQueue> dispatchers = scheduleDispatch ? dispatchReceivedBuffers(callbackGeneration) : new ArrayList<>();
    if (dispatchers.isEmpty())
    {
      scheduleReceiveRequest(source, callbackGeneration);
    }
    else
    {
      for (ChannelReceiveQueue dispatcher : dispatchers)
      {
        dispatcher.start();
      }
    }
  }

  private List<ChannelReceiveQueue> dispatchReceivedBuffers(long callbackGeneration)
  {
    List<ChannelReceiveQueue> dispatchers = new ArrayList<>();

    while (true)
    {
      IBuffer buffer;

      synchronized (transportLock)
      {
        if (generation != callbackGeneration || closing)
        {
          if (generation == callbackGeneration)
          {
            receiveDispatchScheduled = false;
          }

          return dispatchers;
        }

        buffer = receiveQueue.pollFirst();
        if (buffer == null)
        {
          receiveDispatchScheduled = false;
          return dispatchers;
        }
      }

      InternalChannel channel;
      ChannelReceiveQueue dispatch;
      boolean active;
      boolean stale;
      boolean rejectedByChannel = false;
      long controlSequence = 0;
      short channelID = buffer.getChannelID();
      ControlMessage controlMessage = channelID == CONTROL_CHANNEL_ID ? readControlMessage(buffer) : null;
      boolean lifecycleControl = controlMessage != null && controlMessage.isLifecycleOperation();
      boolean held = false;
      boolean discarded = false;

      synchronized (transportLock)
      {
        if (generation == callbackGeneration && !closing && receiveControlBarrierSequence != 0)
        {
          if (channelID == CONTROL_CHANNEL_ID)
          {
            receiveQueue.addFirst(buffer);
            receiveDispatchScheduled = false;
            return dispatchers;
          }

          if (channelID == receiveControlBarrierChannelID)
          {
            if (receiveControlBarrierOpcode == ControlChannel.OPCODE_REGISTRATION)
            {
              pendingReceiveQueue.addLast(buffer);
              held = true;
            }
            else if (receiveControlBarrierOpcode == ControlChannel.OPCODE_DEREGISTRATION)
            {
              discarded = true;
            }
          }
        }
      }

      if (held)
      {
        continue;
      }

      if (discarded)
      {
        releaseReceivedBuffer(buffer);
        continue;
      }

      try
      {
        synchronized (activationLock)
        {
          active = isReceiveGenerationActive(callbackGeneration);
          if (active)
          {
            channel = channelID == CONTROL_CHANNEL_ID ? getInternalControlChannel() : getChannel(channelID);
          }
          else
          {
            channel = null;
          }

          synchronized (transportLock)
          {
            stale = !active //
                || channel == null //
                || !channel.isActive() //
                || generation != callbackGeneration //
                || closing //
                || channelID != CONTROL_CHANNEL_ID && getChannel(channelID) != channel;

            if (!stale)
            {
              dispatch = channelReceiveQueues.computeIfAbsent(channel, key -> new ChannelReceiveQueue(key, callbackGeneration));

              if (lifecycleControl)
              {
                controlSequence = ++nextReceiveControlSequence;
                receiveControlBarrierSequence = controlSequence;
                receiveControlBarrierChannelID = controlMessage.channelID;
                receiveControlBarrierOpcode = controlMessage.opcode;
                receiveDispatchScheduled = false;
              }

              int enqueueResult = dispatch.enqueue(buffer, controlSequence);
              if (enqueueResult == RECEIVE_ENQUEUE_START)
              {
                dispatchers.add(dispatch);
              }
              else if (enqueueResult == RECEIVE_ENQUEUE_REJECTED)
              {
                rejectedByChannel = true;
              }
            }
          }
        }
      }
      catch (RuntimeException | Error ex)
      {
        releaseReceivedBuffer(buffer);
        transportFailed(callbackGeneration, ex);
        return dispatchers;
      }

      if (stale)
      {
        buffer.release();
        continue;
      }

      if (rejectedByChannel)
      {
        releaseReceivedBuffer(buffer);
        continue;
      }

      if (controlSequence != 0)
      {
        return dispatchers;
      }
    }
  }

  private void receiveChannelChanged(IChannel channel, boolean added)
  {
    if (channel instanceof InternalChannel)
    {
      ChannelReceiveQueue dispatcher;

      synchronized (activationLock)
      {
        synchronized (transportLock)
        {
          dispatcher = added ? null : channelReceiveQueues.remove(channel);
        }
      }

      if (dispatcher != null)
      {
        dispatcher.close();
      }
    }

    long expectedGeneration;
    long expectedSequence;

    synchronized (transportLock)
    {
      expectedGeneration = generation;
      expectedSequence = receiveControlBarrierSequence;

      if (expectedSequence == 0 //
          || receiveControlBarrierChannelID != channel.getID() //
          || added && receiveControlBarrierOpcode != ControlChannel.OPCODE_REGISTRATION //
          || !added && receiveControlBarrierOpcode != ControlChannel.OPCODE_DEREGISTRATION)
      {
        return;
      }

    }

    resumeReceiveDispatch(expectedGeneration, expectedSequence, added);
  }

  private void resumeReceiveDispatch(long callbackGeneration, long completedControlSequence, boolean channelAvailable)
  {
    boolean scheduleDispatch = false;
    List<IBuffer> buffers = new ArrayList<>();

    synchronized (transportLock)
    {
      if (generation != callbackGeneration || closing || receiveControlBarrierSequence != completedControlSequence)
      {
        return;
      }

      receiveControlBarrierSequence = 0;
      receiveControlBarrierChannelID = IBuffer.NO_CHANNEL;
      receiveControlBarrierOpcode = 0;

      if (channelAvailable)
      {
        while (!pendingReceiveQueue.isEmpty())
        {
          receiveQueue.addFirst(pendingReceiveQueue.removeLast());
        }
      }

      else
      {
        while (!pendingReceiveQueue.isEmpty())
        {
          buffers.add(pendingReceiveQueue.removeFirst());
        }
      }

      if (!receiveDispatchScheduled && !receiveQueue.isEmpty())
      {
        receiveDispatchScheduled = true;
        scheduleDispatch = true;
      }
    }

    for (IBuffer buffer : buffers)
    {
      releaseReceivedBuffer(buffer);
    }

    if (scheduleDispatch)
    {
      List<ChannelReceiveQueue> dispatchers = dispatchReceivedBuffers(callbackGeneration);

      for (ChannelReceiveQueue dispatcher : dispatchers)
      {
        dispatcher.start();
      }
    }
  }

  private boolean isReceiveGenerationActive(long callbackGeneration)
  {
    synchronized (transportLock)
    {
      return generation == callbackGeneration && !closing;
    }
  }

  private void closeReceiveQueues()
  {
    List<IBuffer> buffers;
    List<ChannelReceiveQueue> dispatchers;

    synchronized (transportLock)
    {
      buffers = new ArrayList<>(receiveQueue);
      buffers.addAll(pendingReceiveQueue);

      receiveQueue.clear();
      pendingReceiveQueue.clear();

      receiveControlBarrierSequence = 0;
      receiveControlBarrierChannelID = IBuffer.NO_CHANNEL;
      receiveControlBarrierOpcode = 0;
      dispatchers = new ArrayList<>(channelReceiveQueues.values());
      channelReceiveQueues.clear();
      receiveDispatchScheduled = false;
    }

    for (IBuffer buffer : buffers)
    {
      releaseReceivedBuffer(buffer);
    }

    for (ChannelReceiveQueue dispatcher : dispatchers)
    {
      dispatcher.close();
    }
  }

  /**
   * Grants one unit of receive demand after reserving the credit under {@code transportLock}. The provider call is outside the lock;
   * failures clear the reservation only for the same source and generation.
   *
   * @param source provider to request from
   * @param callbackGeneration activation epoch that owns the demand
   */
  private void requestReceive(IMessageConnection source, long callbackGeneration)
  {
    synchronized (transportLock)
    {
      if (!isCurrent(source, callbackGeneration) || closing || !ready || receiveCredit)
      {
        return;
      }

      receiveCredit = true;
    }

    try
    {
      source.request(1);
    }
    catch (RuntimeException | Error ex)
    {
      synchronized (transportLock)
      {
        if (isCurrent(source, callbackGeneration))
        {
          receiveCredit = false;
        }
      }

      transportFailed(callbackGeneration, ex);
    }
  }

  private void requestReceiveForGeneration(long callbackGeneration)
  {
    IMessageConnection source;

    synchronized (transportLock)
    {
      source = generation == callbackGeneration ? connection : null;
    }

    if (source != null)
    {
      requestReceive(source, callbackGeneration);
    }
  }

  /**
   * Coalesces demand replenishment after a frame has entered the ordered receive queue. The queued task retains its provider and
   * generation, so a late continuation cannot grant demand to a reactivated connection.
   *
   * @param source provider that supplied the released frame
   * @param callbackGeneration activation epoch that owns the demand
   */
  private void scheduleReceiveRequest(IMessageConnection source, long callbackGeneration)
  {
    synchronized (transportLock)
    {
      if (!isCurrent(source, callbackGeneration) || closing || !ready || receiveRequestScheduled || receiveCredit)
      {
        return;
      }

      receiveRequestScheduled = true;
    }

    try
    {
      ConcurrencyUtil.execute(getConfig().getReceiveExecutor(), () -> {
        synchronized (transportLock)
        {
          if (isCurrent(source, callbackGeneration))
          {
            receiveRequestScheduled = false;
          }
        }

        requestReceive(source, callbackGeneration);
      });
    }
    catch (RuntimeException | Error ex)
    {
      synchronized (transportLock)
      {
        if (isCurrent(source, callbackGeneration))
        {
          receiveRequestScheduled = false;
        }
      }

      transportFailed(callbackGeneration, ex);
    }
  }

  /**
   * Starts provider orderly closure once the caller-requested drain has left both the scheduler and the in-flight transaction empty.
   * This differs from {@link IMessageConnection#abort()}, which terminates immediately and may leave accepted buffers borrowed until
   * their terminal callbacks.
   */
  private void startGracefulCloseIfIdle(long expectedGeneration)
  {
    IMessageConnection current;
    long currentGeneration;

    synchronized (transportLock)
    {
      if (generation != expectedGeneration //
          || !gracefulCloseRequested //
          || gracefulCloseStarted //
          || closing //
          || !ready //
          || inFlight != null //
          || !isActive() //
          || !getScheduler().isEmpty())
      {
        return;
      }

      gracefulCloseStarted = true;
      current = connection;
      currentGeneration = expectedGeneration;
    }

    if (current != null)
    {
      try
      {
        current.closeGracefully();
      }
      catch (RuntimeException | Error ex)
      {
        transportFailed(currentGeneration, ex);
      }
    }
  }

  /**
   * Fails the expected provider generation. Active generations abort the provider and queue deactivation; deferred activation is
   * terminated by its activation owner so negotiation failures can be reported before the lifecycle becomes active.
   *
   * @param expectedGeneration activation epoch reporting the failure
   * @param failure cause to log, or {@code null} for an orderly closure
   */
  private void transportFailed(long expectedGeneration, Throwable failure)
  {
    IMessageConnection current;
    boolean activationPending;

    synchronized (transportLock)
    {
      if (generation != expectedGeneration || closing)
      {
        return;
      }

      activationPending = LifecycleUtil.getLifecycleState(this) == LifecycleState.ACTIVATING;

      if (!isActive() && !activationPending)
      {
        return;
      }

      if (activationPending)
      {
        current = null;
      }
      else
      {
        closing = true;
        ready = false;
        receiveCredit = false;
        current = connection;
        abortIssued = current != null;
      }
    }

    if (activationPending)
    {
      if (failure instanceof NegotiationException && isNegotiating())
      {
        setNegotiationException((NegotiationException)failure);
      }

      if (failure != null)
      {
        OM.LOG.error("Message connection failed during activation", failure); //$NON-NLS-1$
      }

      terminatePendingActivation(expectedGeneration);
      return;
    }

    closeReceiveQueues();

    if (current != null)
    {
      try
      {
        current.abort();
      }
      catch (RuntimeException | Error abortFailure)
      {
        if (failure == null || failure == abortFailure)
        {
          OM.LOG.error("Message connection abort failed", abortFailure); //$NON-NLS-1$
        }
        else
        {
          failure.addSuppressed(abortFailure);
        }
      }
    }

    if (failure != null)
    {
      OM.LOG.error("Message connection failed", failure); //$NON-NLS-1$
    }

    scheduleDeactivation(expectedGeneration);
  }

  private void transportClosed(long expectedGeneration, Throwable cause)
  {
    synchronized (transportLock)
    {
      if (generation != expectedGeneration || closing || !isActive())
      {
        return;
      }

      closing = true;
      ready = false;
      receiveCredit = false;
      abortIssued = true;
    }

    closeReceiveQueues();

    if (cause != null)
    {
      OM.LOG.error("Message connection closed", cause); //$NON-NLS-1$
    }

    scheduleDeactivation(expectedGeneration);
  }

  private void scheduleDeactivation(long expectedGeneration)
  {
    ExecutorService executor = getConfig().getReceiveExecutor();

    ConcurrencyUtil.execute(executor, () -> {
      synchronized (activationLock)
      {
        if (!isCurrentGeneration(expectedGeneration) || deactivationClaim != 0 || LifecycleUtil.getLifecycleState(this) != LifecycleState.ACTIVE)
        {
          return;
        }

        deactivationClaim = expectedGeneration;
      }

      try
      {
        deactivate();
      }
      finally
      {
        synchronized (activationLock)
        {
          if (deactivationClaim == expectedGeneration)
          {
            deactivationClaim = 0;
          }
        }
      }
    });
  }

  private boolean isCurrentGeneration(long expectedGeneration)
  {
    synchronized (transportLock)
    {
      return generation == expectedGeneration;
    }
  }

  private boolean isCurrent(IMessageConnection source, long callbackGeneration)
  {
    return source == connection && callbackGeneration == generation;
  }

  /**
   * Advances activation after provider readiness. Startup callbacks can arrive synchronously, so activation completion is also required.
   * The current source and epoch are checked before changing connector state; later receive and pump actions independently retain the same
   * epoch identity.
   *
   * @param source provider that became ready
   * @param callbackGeneration activation epoch associated with its listener
   */
  private void connectionReady(IMessageConnection source, long callbackGeneration)
  {
    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        if (!isCurrent(source, callbackGeneration) || closing || !activationComplete || !ready)
        {
          return;
        }
      }
    }

    leaveConnecting();

    synchronized (transportLock)
    {
      if (!isCurrent(source, callbackGeneration) || closing || activationOutcome == ACTIVATION_FAILED)
      {
        return;
      }
    }

    restoreSendingInterest(false);
    requestReceive(source, callbackGeneration);
    schedulePump(callbackGeneration);
  }

  private boolean completeConnection(IMessageConnection source, long callbackGeneration)
  {
    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        if (!isCurrent(source, callbackGeneration) || closing || !ready || activationOutcome == ACTIVATION_FAILED)
        {
          return false;
        }
      }

      if (LifecycleUtil.getLifecycleState(this) == LifecycleState.ACTIVATING)
      {
        if (activationOutcome != ACTIVATION_PENDING)
        {
          return false;
        }

        activationOutcome = ACTIVATION_SUCCEEDED;
      }
    }

    super.setState(ConnectorState.CONNECTED);
    boolean terminate;

    synchronized (activationLock)
    {
      terminate = terminationPending && activationOutcome == ACTIVATION_SUCCEEDED && LifecycleUtil.getLifecycleState(this) == LifecycleState.ACTIVE;
      if (terminate)
      {
        terminationPending = false;
      }
    }

    if (terminate)
    {
      deactivate();
    }

    schedulePump(callbackGeneration);
    return true;
  }

  private void terminatePendingActivation(long expectedGeneration)
  {
    boolean terminate = false;
    boolean closeReceives = false;

    synchronized (activationLock)
    {
      synchronized (transportLock)
      {
        if (generation != expectedGeneration || LifecycleUtil.getLifecycleState(this) != LifecycleState.ACTIVATING)
        {
          return;
        }

        if (activationOutcome == ACTIVATION_SUCCEEDED)
        {
          closing = true;
          ready = false;
          receiveCredit = false;
          terminationPending = true;
          closeReceives = true;
        }
        else if (activationOutcome == ACTIVATION_PENDING)
        {
          activationOutcome = ACTIVATION_FAILED;
          closing = true;
          ready = false;
          receiveCredit = false;
          receiveRequestScheduled = false;
          terminate = true;
          closeReceives = true;
        }
      }
    }

    if (closeReceives)
    {
      closeReceiveQueues();
    }

    if (terminate)
    {
      try
      {
        doDeactivate();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
      finally
      {
        deferredActivate(false);
        onActivationFailed();
      }
    }
  }

  /**
   * Ends a deferred activation when its owner explicitly aborts the provider connection. The method arbitrates against readiness and
   * negotiation completion, detaches the current provider generation, and completes the lifecycle attempt as failed. It is safe to call
   * after a provider has already reported closure and does not depend on {@link IMessageConnection#abort()} producing a callback.
   *
   * @since 4.23
   */
  protected final void terminatePendingActivation()
  {
    long expectedGeneration;

    synchronized (transportLock)
    {
      expectedGeneration = generation;
    }

    terminatePendingActivation(expectedGeneration);
  }

  /**
   * Called after a provider terminates while this connector is still undergoing deferred activation. Subclasses that are tracked by
   * an owner can remove themselves from that owner's registry here because the failed deferred activation does not publish a normal
   * {@code DEACTIVATED} lifecycle event.
   *
   * @since 4.23
   */
  protected void onActivationFailed()
  {
  }

  private boolean isCurrentConnection(IMessageConnection source, long callbackGeneration)
  {
    synchronized (transportLock)
    {
      return isCurrent(source, callbackGeneration) && !closing;
    }
  }

  private static ControlMessage readControlMessage(IBuffer buffer)
  {
    ByteBuffer byteBuffer = buffer.getByteBuffer().duplicate();

    try
    {
      short channelID = IBuffer.NO_CHANNEL;

      byte opcode = byteBuffer.get();
      if (opcode == ControlChannel.OPCODE_REGISTRATION || opcode == ControlChannel.OPCODE_DEREGISTRATION)
      {
        channelID = byteBuffer.getShort();
      }

      return new ControlMessage(opcode, channelID);
    }
    catch (RuntimeException ex)
    {
      return new ControlMessage((byte)0, IBuffer.NO_CHANNEL);
    }
  }

  private static void releaseReceivedBuffer(IBuffer buffer)
  {
    try
    {
      buffer.release();
    }
    catch (RuntimeException | Error ex)
    {
      OM.LOG.error("Failed to release a queued receive buffer", ex); //$NON-NLS-1$
    }
  }

  private static void runRelease(Runnable releaseAction)
  {
    if (releaseAction != null)
    {
      releaseAction.run();
    }
  }

  private static Runnable once(Runnable action)
  {
    AtomicBoolean released = new AtomicBoolean();

    return () -> {
      if (released.compareAndSet(false, true))
      {
        action.run();
      }
    };
  }

  /**
   * Binds provider readiness, input, writability, and closure callbacks to the provider and activation epoch that installed the listener.
   * Some providers can race callbacks with shutdown, so every state-changing callback validates both identities. Late input is released
   * and late send callbacks still clean up their separately retained {@link InFlight} ownership, while stale lifecycle notifications are
   * ignored.
   *
   * @author Eike Stepper
   */
  private final class ConnectionListener implements IMessageConnection.Listener
  {
    private final IMessageConnection source;

    private final long callbackGeneration;

    public ConnectionListener(IMessageConnection source, long callbackGeneration)
    {
      this.source = source;
      this.callbackGeneration = callbackGeneration;
    }

    @Override
    public void ready()
    {
      synchronized (transportLock)
      {
        if (!isCurrent(source, callbackGeneration) || closing)
        {
          return;
        }

        ready = true;

        if (!activationComplete)
        {
          return;
        }
      }

      connectionReady(source, callbackGeneration);
    }

    @Override
    public void received(ByteBuffer data, Runnable releaseAction)
    {
      receive(source, callbackGeneration, data, releaseAction);
    }

    @Override
    public void writable()
    {
      synchronized (transportLock)
      {
        if (!isCurrent(source, callbackGeneration) || closing)
        {
          return;
        }

        rejected = false;
      }

      schedulePump();
    }

    @Override
    public void closed(Throwable cause)
    {
      boolean terminateActivation;
      boolean activationIncomplete;

      synchronized (transportLock)
      {
        if (!isCurrent(source, callbackGeneration))
        {
          return;
        }

        if (!activationComplete)
        {
          activationFailure = cause == null ? new ConnectorException("Message connection closed during activation") : cause; //$NON-NLS-1$
          activationOutcome = ACTIVATION_FAILED;
          closing = true;
          ready = false;
          receiveCredit = false;
          activationIncomplete = true;
          terminateActivation = false;
        }
        else
        {
          activationIncomplete = false;
          abortIssued = true;
          terminateActivation = LifecycleUtil.getLifecycleState(MessageConnector.this) == LifecycleState.ACTIVATING;
        }
      }

      if (activationIncomplete)
      {
        closeReceiveQueues();
        return;
      }

      if (terminateActivation)
      {
        terminatePendingActivation(callbackGeneration);
        return;
      }

      transportClosed(callbackGeneration, cause);
    }
  }

  /**
   * Adapts the terminal callback for exactly one {@link InFlight} transaction. Completion can arrive synchronously from inside
   * {@link IMessageConnection#send(IBuffer, IMessageConnection.SendCallback)} before the acceptance result is known, so the transaction
   * records the callback and defers processing until submission evaluation finishes. A stale callback still ends its captured buffer
   * ownership, but cannot advance current-generation state.
   *
   * @author Eike Stepper
   */
  private final class SendCallback implements IMessageConnection.SendCallback
  {
    private final InFlight send;

    public SendCallback(InFlight send)
    {
      this.send = send;
    }

    @Override
    public void completed()
    {
      providerCompleted(send, null);
    }

    @Override
    public void failed(Throwable failure)
    {
      providerCompleted(send, failure == null ? new ConnectorException("Message send failed") : failure);
    }
  }

  /**
   * Submission phases for an outgoing transaction. {@code SELECTED} means the scheduler chose and the connector pinned a queue head;
   * {@code SUBMITTING} means the provider call is evaluating acceptance. The result is either {@code REJECTED}, which ends without a
   * provider borrow, or {@code ACCEPTED}, which remains borrowed until a terminal callback moves it to {@code TERMINAL}. A callback
   * observed during {@code SUBMITTING} is remembered separately and processed after acceptance is known. Rejection is not terminal
   * completion of a borrowed send.
   *
   * @author Eike Stepper
   */
  private enum SendState
  {
    SELECTED, SUBMITTING, REJECTED, ACCEPTED, TERMINAL;
  }

  /**
   * One outgoing send transaction from scheduler selection through provider terminal completion. The record retains the selected channel,
   * original send queue, pinned buffer, CCAM close flag, and activation generation. It may represent work still being evaluated by
   * {@code send()}, rather than a send already accepted by the provider. The separate {@code submitting}, {@code accepted}, and
   * {@code callbackSeen} flags distinguish evaluation in progress, the provider's borrow decision, and an early terminal callback;
   * {@code state} records the transaction phase, while {@code detached} records lifecycle removal from the active connector. The atomic
   * cleanup guard makes queue removal and buffer release exactly once. Retaining the original queue is essential because channel
   * deactivation or reactivation may replace its active queue before an old provider callback arrives.
   *
   * @author Eike Stepper
   */
  private static final class InFlight
  {
    private final long generation;

    private final InternalChannel channel;

    private final Queue<IBuffer> queue;

    private final IBuffer buffer;

    private final boolean closeChannelAfterMe;

    private final AtomicBoolean ownershipFinished = new AtomicBoolean();

    private boolean submitting = true;

    private boolean accepted;

    private boolean callbackSeen;

    private boolean detached;

    private SendState state = SendState.SELECTED;

    private Throwable completion;

    public InFlight(long generation, InternalChannel channel, Queue<IBuffer> queue, IBuffer buffer, boolean closeChannelAfterMe)
    {
      this.generation = generation;
      this.channel = channel;
      this.queue = queue;
      this.buffer = buffer;
      this.closeChannelAfterMe = closeChannelAfterMe;
    }
  }

  /**
   * Tracks receive bytes retained by one provider activation.
   *
   * @author Eike Stepper
   */
  private static final class ReceiveAccounting
  {
    private final long zeroCopyLimit;

    private final long copyLimit;

    private long zeroCopyBytes;

    private long copyBytes;

    public ReceiveAccounting(long zeroCopyLimit, long copyLimit)
    {
      this.zeroCopyLimit = zeroCopyLimit;
      this.copyLimit = copyLimit;
    }

    public synchronized boolean reserveZeroCopy(int bytes)
    {
      if (bytes > zeroCopyLimit - zeroCopyBytes)
      {
        return false;
      }

      zeroCopyBytes += bytes;
      return true;
    }

    public synchronized boolean reserveCopy(int bytes)
    {
      if (bytes > copyLimit - copyBytes)
      {
        return false;
      }

      copyBytes += bytes;
      return true;
    }

    public synchronized void releaseZeroCopy(int bytes)
    {
      zeroCopyBytes -= bytes;
      if (zeroCopyBytes < 0)
      {
        throw new IllegalStateException("Zero-copy receive accounting became negative"); //$NON-NLS-1$
      }
    }

    public synchronized void releaseCopy(int bytes)
    {
      copyBytes -= bytes;
      if (copyBytes < 0)
      {
        throw new IllegalStateException("Copied receive accounting became negative"); //$NON-NLS-1$
      }
    }

    public synchronized IllegalStateException createExhaustionFailure(int incomingBytes)
    {
      return new IllegalStateException("Receive memory budget exceeded: incoming=" + incomingBytes + " bytes, zero-copy=" + zeroCopyBytes + "/" + zeroCopyLimit //$NON-NLS-3$
          + " bytes, copied=" + copyBytes + "/" + copyLimit + " bytes"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * Serializes delivery to one channel while allowing another channel's handler to make progress independently.
   *
   * @author Eike Stepper
   */
  private final class ChannelReceiveQueue
  {
    private final InternalChannel channel;

    private final long callbackGeneration;

    private final ArrayDeque<ReceivedFrame> buffers = new ArrayDeque<>();

    private boolean running;

    private boolean closed;

    public ChannelReceiveQueue(InternalChannel channel, long callbackGeneration)
    {
      this.channel = channel;
      this.callbackGeneration = callbackGeneration;
    }

    public int enqueue(IBuffer buffer, long controlSequence)
    {
      synchronized (this)
      {
        if (closed)
        {
          return RECEIVE_ENQUEUE_REJECTED;
        }

        buffers.addLast(new ReceivedFrame(buffer, controlSequence));
        boolean start = !running;
        running = true;
        return start ? RECEIVE_ENQUEUE_START : RECEIVE_ENQUEUE_QUEUED;
      }
    }

    public void start()
    {
      synchronized (this)
      {
        if (closed)
        {
          running = false;
          return;
        }
      }

      try
      {
        ConcurrencyUtil.execute(getConfig().getReceiveExecutor(), this::dispatch);
      }
      catch (RuntimeException | Error ex)
      {
        close();
        transportFailed(callbackGeneration, ex);
      }
    }

    public void close()
    {
      List<ReceivedFrame> pending;

      synchronized (this)
      {
        closed = true;
        pending = new ArrayList<>(buffers);
        buffers.clear();
      }

      for (ReceivedFrame frame : pending)
      {
        releaseReceivedBuffer(frame.buffer);
      }
    }

    private void dispatch()
    {
      while (true)
      {
        ReceivedFrame frame;

        synchronized (this)
        {
          if (closed)
          {
            running = false;
            return;
          }

          frame = buffers.pollFirst();
          if (frame == null)
          {
            running = false;
            return;
          }
        }

        IBuffer buffer = frame.buffer;

        if (!isReceiveGenerationActive(callbackGeneration) || !channel.isActive())
        {
          releaseReceivedBuffer(buffer);
          completeControlDispatch(frame.controlSequence, callbackGeneration);
          continue;
        }

        requestReceiveForGeneration(callbackGeneration);

        try
        {
          channel.handleBufferFromMultiplexer(buffer);
        }
        catch (RuntimeException | Error ex)
        {
          releaseReceivedBuffer(buffer);
          transportFailed(callbackGeneration, ex);
          return;
        }
        finally
        {
          completeControlDispatch(frame.controlSequence, callbackGeneration);
        }
      }
    }

    private void completeControlDispatch(long controlSequence, long callbackGeneration)
    {
      if (controlSequence != 0)
      {
        resumeReceiveDispatch(callbackGeneration, controlSequence, false);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ReceivedFrame
  {
    private final IBuffer buffer;

    private final long controlSequence;

    public ReceivedFrame(IBuffer buffer, long controlSequence)
    {
      this.buffer = buffer;
      this.controlSequence = controlSequence;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ControlMessage
  {
    private final byte opcode;

    private final short channelID;

    public ControlMessage(byte opcode, short channelID)
    {
      this.opcode = opcode;
      this.channelID = channelID;
    }

    public boolean isLifecycleOperation()
    {
      return opcode == ControlChannel.OPCODE_REGISTRATION || opcode == ControlChannel.OPCODE_DEREGISTRATION;
    }
  }

  /**
   * A reliable, ordered, bidirectional message connection for complete Net4j frames. Providers assemble transport fragments before
   * delivering a message. Each message includes the four-byte Net4j header.
   * <p>
   * Receive demand counts complete messages. Each received callback consumes one credit, and callbacks for a connection must be delivered
   * in message order. The provider leases the supplied storage until its release action is invoked, and must not mutate or reuse the
   * storage before then. After a complete message has been accepted by the listener, the connector may grant more demand independently
   * of when that message's Net4j buffer is released. Providers must retain each leased frame's storage until its matching release action.
   * <p>
   * A rejected send must not retain or access its buffer after returning and must not invoke its callback. An exception thrown before
   * acceptance has the same ownership result: the provider has not borrowed the buffer, must cease accessing it, and must not invoke its
   * callback. Acceptance is reported by returning {@code true}. Once the provider borrows the buffer, it must return {@code true} and
   * deliver exactly one terminal callback; the callback may happen before {@link #send(IBuffer, SendCallback)} returns. A terminal callback
   * guarantees that provider access to the buffer has ended. Throwing after borrowing the buffer violates this contract; an accepted
   * send must return {@code true} and arrange its terminal callback. The provider must deliver
   * terminal callbacks for every accepted send, including sends outstanding during shutdown.
   * <p>
   * If a provider permanently omits a terminal callback, safe reuse and eventual reclamation of that buffer cannot both be guaranteed.
   * The connector bounds this risk to at most one buffer for each connection generation. Repeated activations can therefore leave one
   * retained buffer in each generation whose provider omitted completion; this is not a global one-buffer limit. The connector must not use
   * timeouts or premature release to conceal the missing ownership transfer.
   *
   * @author Eike Stepper
   * @since 4.23
   */
  public interface IMessageConnection
  {
    /**
     * Installs a listener and begins or attaches the connection. Implementations may call {@link Listener#ready()} synchronously. A
     * listener belongs to this connection generation and must not be used to signal readiness or input for another generation.
     *
     * @param listener the callbacks for this connection generation
     */
    public void start(Listener listener);

    /**
     * Attempts to send one complete framed buffer. At most one accepted send is submitted by a {@link MessageConnector} at a time. Returning
     * {@code false} rejects the send: the provider must not borrow, retain, modify, release, or later access the buffer, and must not invoke
     * the callback. Throwing before acceptance has the same ownership result and the provider must no longer access the buffer. Acceptance
     * is reported by returning {@code true}; once the provider borrows the buffer, it must return {@code true} and deliver exactly one
     * terminal callback, including after transport failure. Throwing after borrowing violates this contract; the provider must return
     * {@code true} and arrange that accepted-send completion. All provider access to borrowed storage must
     * have ended before the terminal callback.
     *
     * @param buffer the Net4j buffer borrowed for the send
     * @param callback completion of an accepted send
     * @return {@code true} when accepted with exactly one terminal callback, or {@code false} when rejected without borrowing the buffer
     */
    public boolean send(IBuffer buffer, SendCallback callback);

    /**
     * Grants demand for complete incoming messages. Each delivered message consumes one credit. The provider must not deliver more
     * messages than requested and must retain each supplied storage lease until the listener invokes its matching release action. The
     * connector may grant further demand while earlier messages remain leased.
     *
     * @param messages positive number of additional messages requested
     */
    public void request(long messages);

    /**
     * Starts graceful local closure after all sends already accepted by the provider have completed. Once closure begins, the provider
     * rejects new sends and later reports closure through its listener.
     */
    public void closeGracefully();

    /**
     * Initiates immediate termination, prevents further sends from being accepted, and causes outstanding accepted sends to complete
     * unsuccessfully unless already successfully completed. This method is safe concurrently with {@link #send(IBuffer, SendCallback)} and
     * must not wait indefinitely for callbacks. Borrowed buffers remain owned by the provider until their terminal callbacks, which may
     * occur after this method returns.
     */
    public void abort();

    /**
     * Receives connection readiness, complete inbound messages, writability, and closure.
     *
     * @author Eike Stepper
     */
    public interface Listener
    {
      /**
       * Called once the transport can send and receive.
       */
      public void ready();

      /**
       * Delivers one complete framed message and transfers a lease on its storage.
       *
       * @param data storage whose current position and limit delimit the complete frame
       * @param releaseAction action that returns or releases the storage
       */
      public void received(ByteBuffer data, Runnable releaseAction);

      /**
       * Called when a previously rejected send may be retried.
       */
      public void writable();

      /**
       * Called when the peer or transport closes the connection.
       *
       * @param cause the reason for an abnormal closure, or {@code null} for an orderly closure
       */
      public void closed(Throwable cause);
    }

    /**
     * Reports completion of one accepted send.
     *
     * @author Eike Stepper
     */
    public interface SendCallback
    {
      /**
       * Called once after successful transmission and after provider access to the borrowed buffer has ended.
       */
      public void completed();

      /**
       * Called once when transmission failed, delivery became ambiguous, or an accepted send was cancelled during termination. Provider
       * access to the borrowed buffer must have ended before this callback.
       *
       * @param failure the transmission or cancellation failure
       */
      public void failed(Throwable failure);
    }
  }
}
