/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IncompatibleControlProtocolException;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationException;

import org.eclipse.internal.net4j.ControlChannel;
import org.eclipse.internal.net4j.RoundRobinScheduler;

import java.nio.ByteBuffer;

/**
 * A connector base for transports that send channel buffers over a shared connection. It owns the common channel scheduler and control
 * channel while leaving transport execution and I/O to subclasses. The lifecycle lock serializes scheduling state with shutdown; scheduler
 * operations acquire channel queue locks before scheduler state locks, and release scheduler locks before returning to transport hooks.
 *
 * @author Eike Stepper
 * @since 4.23
 */
public abstract class SchedulingConnector extends Connector
{
  /**
   * The reserved channel identifier used for shared control messages.
   */
  public static final short CONTROL_CHANNEL_ID = ControlChannel.CONTROL_CHANNEL_ID;

  /**
   * The opcode used for negotiation messages on the shared control channel.
   */
  public static final byte CONTROL_OPCODE_NEGOTIATION = ControlChannel.OPCODE_NEGOTIATION;

  private final Object schedulingLock = new Object();

  private final Object schedulingNotificationLock = new Object();

  private IScheduler scheduler = new RoundRobinScheduler();

  private volatile long schedulingNotificationRevision;

  private volatile ControlChannel controlChannel;

  private volatile boolean schedulingEnabled;

  private final Object controlHandshakeLock = new Object();

  private ControlHandshakeState controlHandshakeState = ControlHandshakeState.INACTIVE;

  private volatile long controlHandshakeGeneration;

  public SchedulingConnector()
  {
  }

  /**
   * Returns the scheduler used to select channels with pending outgoing buffers.
   *
   * @return the current channel scheduler
   */
  public final IScheduler getScheduler()
  {
    return scheduler;
  }

  /**
   * Sets the scheduler before activation.
   *
   * @param scheduler the scheduler to use
   */
  public final void setScheduler(IScheduler scheduler)
  {
    checkInactive();

    if (scheduler == null)
    {
      throw new IllegalArgumentException("scheduler == null"); //$NON-NLS-1$
    }

    this.scheduler = scheduler;
  }

  /**
   * Returns the shared control channel through the public channel interface. The channel is {@code null} before activation and after
   * deactivation.
   *
   * @return the shared control channel
   */
  public final IChannel getControlChannel()
  {
    return controlChannel;
  }

  @Override
  public final void multiplexChannel(InternalChannel channel)
  {
    long notificationRevision = -1;

    synchronized (schedulingLock)
    {
      if (schedulingEnabled && scheduler.offer(channel) && schedulingEnabled)
      {
        notificationRevision = ++schedulingNotificationRevision;
      }
    }

    notifySendingState(notificationRevision, true);
  }

  @Override
  public void closeChannel(InternalChannel channel) throws ChannelException
  {
    long notificationRevision = -1;
    boolean sendingRequired = false;

    synchronized (schedulingLock)
    {
      boolean workRemains = scheduler.remove(channel);
      if (workRemains && schedulingEnabled)
      {
        notificationRevision = ++schedulingNotificationRevision;
        sendingRequired = true;
      }
      else if (schedulingEnabled && scheduler.isEmpty())
      {
        notificationRevision = ++schedulingNotificationRevision;
      }
    }

    notifySendingState(notificationRevision, sendingRequired);
    super.closeChannel(channel);
  }

  /**
   * Returns the shared control channel for transport implementations that need the internal buffer-dispatch contract.
   *
   * @return the shared internal control channel
   */
  protected final InternalChannel getInternalControlChannel()
  {
    return controlChannel;
  }

  /**
   * Called when the scheduler transitions from idle to having pending work. Implementations should arrange a send attempt without
   * blocking the producer that enqueued the buffer. The hook runs outside the scheduling lifecycle lock, so it must only arrange
   * asynchronous work and must not perform transport I/O or block.
   */
  protected abstract void onSendingRequired();

  @Override
  protected void leaveConnecting()
  {
    ControlChannel control;
    long generation;

    synchronized (controlHandshakeLock)
    {
      if (controlHandshakeState != ControlHandshakeState.NOT_STARTED)
      {
        return;
      }

      controlHandshakeState = ControlHandshakeState.STARTED;
      generation = controlHandshakeGeneration;
      control = controlChannel;
    }

    if (control == null)
    {
      failControlHandshake(generation, new ConnectorException("Control channel is not available for protocol negotiation")); //$NON-NLS-1$
      return;
    }

    control.startHandshake(generation);
  }

  /**
   * Completes the normal connector connection progression after the peer's HELLO has been validated. This preserves the configured
   * authentication negotiation and connected transition while ensuring neither can begin before control compatibility is established.
   *
   * @param generation the activation generation that received and validated the HELLO
   */
  public final void controlHandshakeCompleted(long generation)
  {
    boolean won;

    synchronized (controlHandshakeLock)
    {
      won = generation == controlHandshakeGeneration && controlHandshakeState == ControlHandshakeState.STARTED;
      if (won)
      {
        controlHandshakeState = ControlHandshakeState.COMPLETED;
      }
    }

    if (won)
    {
      advanceConnectionAfterHandshake(generation);
    }
  }

  /**
   * Advances the connector after a compatible HELLO. The default implementation uses the base connector lifecycle transition and
   * guards it against activation changes. A transport with additional readiness arbitration can override this method, but must retain
   * the supplied handshake generation when publishing its state.
   *
   * @param generation the activation generation that completed the HELLO exchange
   */
  protected void advanceConnectionAfterHandshake(long generation)
  {
    ConnectorState nextState = getNegotiator() == null ? ConnectorState.CONNECTED : ConnectorState.NEGOTIATING;

    boolean progressed = setStateIf(nextState, () -> generation == controlHandshakeGeneration);
    if (progressed)
    {
      onControlHandshakeCompleted(generation);
    }
  }

  /**
   * Returns whether the supplied control-handshake generation still belongs to the current activation.
   *
   * @param generation the generation captured by a handshake callback
   * @return whether that callback may affect the current activation
   */
  protected final boolean isCurrentControlHandshakeGeneration(long generation)
  {
    return generation == controlHandshakeGeneration;
  }

  /**
   * Called after normal connector progression resumes so transports can restart work deferred during the handshake.
   */
  protected void onControlHandshakeCompleted(long generation)
  {
    // Do nothing.
  }

  /**
   * Fails a pending handshake when the transport closes before it advertises a control version.
   */
  protected final void failControlHandshakeIfPending(Throwable cause)
  {
    long generation;

    synchronized (controlHandshakeLock)
    {
      if (controlHandshakeState != ControlHandshakeState.STARTED)
      {
        return;
      }

      generation = controlHandshakeGeneration;
    }

    IncompatibleControlProtocolException failure = new IncompatibleControlProtocolException(
        "Incompatible Net4j control protocol: remote version is unknown (connection closed before HELLO)", //$NON-NLS-1$
        cause);
    failControlHandshake(generation, failure);
  }

  /**
   * Records and publishes a control-protocol failure so waiters receive its diagnostic instead of a connection timeout.
   *
   * @param generation the activation generation that observed the failure
   * @param failure the incompatibility or malformed-message failure
   */
  public final void failControlHandshake(long generation, ConnectorException failure)
  {
    boolean won;

    synchronized (controlHandshakeLock)
    {
      won = generation == controlHandshakeGeneration && controlHandshakeState == ControlHandshakeState.STARTED;
      if (won)
      {
        controlHandshakeState = ControlHandshakeState.FAILED;
      }
    }

    if (!won)
    {
      return;
    }

    ControlChannel control = controlChannel;
    if (control != null)
    {
      control.cancelHandshakeTimeout(generation);
    }

    synchronized (controlHandshakeLock)
    {
      if (generation != controlHandshakeGeneration || controlHandshakeState != ControlHandshakeState.FAILED)
      {
        return;
      }

      setNegotiationException(new NegotiationException(failure.getMessage(), failure));
    }

    deferredActivate(false);

    ConcurrencyUtil.execute(getConfig().getReceiveExecutor(), () -> {
      if (isCurrentFailedHandshake(generation))
      {
        deactivate();
      }
    });
  }

  /**
   * Completes the local half of HELLO after a transport confirms that the control buffer was sent successfully.
   *
   * @param channel the channel whose buffer completed
   * @param buffer the complete frame accepted by the transport
   */
  protected final void controlBufferSent(InternalChannel channel, IBuffer buffer)
  {
    controlBufferSent(channel, buffer, controlHandshakeGeneration());
  }

  /**
   * Identifies a completed control buffer only for the activation that submitted it.
   */
  protected final void controlBufferSent(InternalChannel channel, IBuffer buffer, long generation)
  {
    if (isControlHelloBuffer(channel, buffer))
    {
      controlHelloSent(generation);
    }
  }

  /**
   * Returns whether a complete frame is the HELLO sent on this connector's control channel.
   */
  protected final boolean isControlHelloBuffer(InternalChannel channel, IBuffer buffer)
  {
    ControlChannel control = controlChannel;
    if (channel != control || buffer == null)
    {
      return false;
    }

    if (buffer.getState() == BufferState.WRITING)
    {
      synchronized (controlHandshakeLock)
      {
        return controlHandshakeState == ControlHandshakeState.STARTED;
      }
    }

    ByteBuffer bytes = buffer.getByteBuffer().duplicate();
    return bytes.limit() > IBuffer.HEADER_SIZE && bytes.get(IBuffer.HEADER_SIZE) == ControlChannel.OPCODE_HELLO;
  }

  /**
   * Completes the local HELLO half after a transport finishes transmitting it.
   */
  protected final void controlHelloSent()
  {
    controlHelloSent(controlHandshakeGeneration());
  }

  /**
   * Completes the local HELLO half only if the captured activation still owns the pending handshake.
   */
  protected final void controlHelloSent(long generation)
  {
    ControlChannel control = controlChannel;
    if (control != null)
    {
      control.helloSent(generation);
    }
  }

  /**
   * Returns the activation generation associated with the current control handshake.
   */
  protected final long controlHandshakeGeneration()
  {
    synchronized (controlHandshakeLock)
    {
      return controlHandshakeGeneration;
    }
  }

  /**
   * Called when the scheduler becomes idle. Implementations should asynchronously stop requesting transport write readiness without
   * performing transport I/O or blocking.
   */
  protected void onSendingNoLongerRequired()
  {
    // Do nothing.
  }

  /**
   * Called by a transport sender after a send attempt. An incomplete current buffer keeps that channel selected; a completed buffer
   * allows it to rotate to the tail if more work remains. After a successful completion, this method requests another send attempt when
   * scheduled work remains. The transport hook must enqueue or wake an asynchronous sender rather than sending reentrantly.
   *
   * @param channel the serviced channel
   * @param bufferCompleted whether the current buffer completed successfully
   * @return {@code true} if the sender should continue because scheduled work remains
   */
  protected final boolean completeScheduledChannel(InternalChannel channel, boolean bufferCompleted)
  {
    long notificationRevision = -1;
    boolean sendingRequired = false;
    boolean workRemains;

    synchronized (schedulingLock)
    {
      workRemains = schedulingEnabled && scheduler.complete(channel, bufferCompleted);
      if (workRemains && bufferCompleted && schedulingEnabled)
      {
        notificationRevision = ++schedulingNotificationRevision;
        sendingRequired = true;
      }
      else if (bufferCompleted && schedulingEnabled && scheduler.isEmpty())
      {
        notificationRevision = ++schedulingNotificationRevision;
      }
    }

    notifySendingState(notificationRevision, sendingRequired);
    return workRemains;
  }

  /**
   * Selects the current channel for a transport sender.
   *
   * @return the current eligible channel or {@code null}
   */
  protected final InternalChannel selectScheduledChannel()
  {
    synchronized (schedulingLock)
    {
      return schedulingEnabled ? scheduler.select() : null;
    }
  }

  /**
   * Restores transport write readiness according to scheduler work and any transport-specific work that must be serviced on the write
   * path. The check and notification are serialized with channel offers and removals to prevent lost selector wakeups.
   *
   * @param additionalWork whether the transport has work that must be serviced by its write-ready callback
   */
  protected final void restoreSendingInterest(boolean additionalWork)
  {
    long notificationRevision = -1;
    boolean sendingRequired = false;

    synchronized (schedulingLock)
    {
      if (schedulingEnabled && (!scheduler.isEmpty() || additionalWork))
      {
        notificationRevision = ++schedulingNotificationRevision;
        sendingRequired = true;
      }
      else if (schedulingEnabled)
      {
        notificationRevision = ++schedulingNotificationRevision;
      }
    }

    notifySendingState(notificationRevision, sendingRequired);
  }

  /**
   * Stops transport write readiness if no channel remains scheduled. The check is serialized with channel offers and removals.
   */
  protected final void stopSendingIfIdle()
  {
    long notificationRevision = -1;

    synchronized (schedulingLock)
    {
      if (schedulingEnabled && scheduler.isEmpty())
      {
        notificationRevision = ++schedulingNotificationRevision;
      }
    }

    notifySendingState(notificationRevision, false);
  }

  @Override
  protected void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol) throws ChannelException
  {
    ControlChannel control = controlChannel;
    if (control == null)
    {
      throw new ConnectorException("Control channel is not available"); //$NON-NLS-1$
    }

    control.registerChannel(channelID, timeout, protocol);
  }

  @Override
  protected void deregisterChannelFromPeer(InternalChannel channel) throws ChannelException
  {
    ControlChannel control = controlChannel;
    if (control != null && channel != null && channel.getID() != RESERVED_CHANNEL && isConnected())
    {
      control.deregisterChannel(channel.getID());
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    synchronized (controlHandshakeLock)
    {
      synchronized (this)
      {
        ++controlHandshakeGeneration;
      }

      controlHandshakeState = ControlHandshakeState.NOT_STARTED;
    }

    super.doActivate();

    synchronized (schedulingLock)
    {
      ++schedulingNotificationRevision;
      scheduler.clear();
    }

    ControlChannel control = new ControlChannel(this);
    controlChannel = control;

    try
    {
      LifecycleUtil.activate(control);

      synchronized (schedulingLock)
      {
        schedulingEnabled = true;
      }
    }
    catch (Exception | Error ex)
    {
      schedulingEnabled = false;

      synchronized (schedulingLock)
      {
        scheduler.clear();
      }

      controlChannel = null;
      LifecycleUtil.deactivate(control);

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
    synchronized (controlHandshakeLock)
    {
      controlHandshakeState = ControlHandshakeState.INACTIVE;

      synchronized (this)
      {
        ++controlHandshakeGeneration;
      }
    }

    schedulingEnabled = false;

    synchronized (schedulingLock)
    {
      ++schedulingNotificationRevision;
      scheduler.clear();
    }

    synchronized (schedulingNotificationLock)
    {
      // Wait for a notification that passed its revision check before deactivation began.
    }

    ControlChannel control = controlChannel;
    controlChannel = null;

    try
    {
      LifecycleUtil.deactivate(control);
    }
    finally
    {
      super.doDeactivate();
    }
  }

  @Override
  protected abstract INegotiationContext createNegotiationContext();

  private void notifySendingState(long revision, boolean sendingRequired)
  {
    if (revision == -1)
    {
      return;
    }

    synchronized (schedulingNotificationLock)
    {
      if (schedulingEnabled && revision == schedulingNotificationRevision)
      {
        if (sendingRequired)
        {
          onSendingRequired();
        }
        else
        {
          onSendingNoLongerRequired();
        }
      }
    }
  }

  private boolean isCurrentFailedHandshake(long generation)
  {
    synchronized (controlHandshakeLock)
    {
      return generation == controlHandshakeGeneration && controlHandshakeState == ControlHandshakeState.FAILED;
    }
  }

  /**
   * Tracks the one terminal outcome permitted for a control handshake activation.
   *
   * @author Eike Stepper
   */
  private enum ControlHandshakeState
  {
    INACTIVE, NOT_STARTED, STARTED, COMPLETED, FAILED
  }

  /**
   * Schedules channels that have outgoing buffers. Implementations must not perform transport I/O.
   *
   * @author Eike Stepper
   */
  public interface IScheduler
  {
    /**
     * Offers a channel with queued outgoing work.
     *
     * @param channel a channel whose send queue may contain pending buffers
     * @return {@code true} if this offer caused an idle scheduler to become nonempty
     */
    public boolean offer(InternalChannel channel);

    /**
     * Selects the current channel without rotating it. Repeated calls return the same channel until its buffer completes or it is removed.
     *
     * @return the selected channel, or {@code null} if no channel has pending work
     */
    public InternalChannel select();

    /**
     * Completes or retains the current channel after a send attempt. An incomplete buffer remains selected so a transport can resume it
     * without interleaving another channel's buffer. After a successful completion, the connector requests another send attempt if any
     * channel remains scheduled. This request must be asynchronous so it cannot send reentrantly from the completion callback.
     *
     * @param channel the channel that was selected
     * @param bufferCompleted {@code true} only after the complete buffer was transmitted successfully
     * @return {@code true} if the completion was for the current channel and at least one channel remains scheduled
     */
    public boolean complete(InternalChannel channel, boolean bufferCompleted);

    /**
     * Removes a channel from the scheduling order, for example after deactivation.
     *
     * @param channel the channel to remove
     * @return {@code true} if the removed channel was current and other scheduled work remains
     */
    public boolean remove(InternalChannel channel);

    /**
     * Returns whether channels are scheduled.
     *
     * @return {@code true} if the scheduler has no pending channels
     */
    public boolean isEmpty();

    /**
     * Clears the current selection and all scheduled channels, typically during connector deactivation.
     */
    public void clear();
  }
}
