/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.internal.net4j;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IncompatibleControlProtocolException;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ISynchronizer;
import org.eclipse.net4j.util.concurrent.SynchronizingCorrelator;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.concurrent.TimerLifecycle;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiationContext.Receiver;

import org.eclipse.internal.net4j.buffer.BufferUtil;

import org.eclipse.spi.net4j.Channel;
import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.SchedulingConnector;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements negotiation, control-version HELLO, and channel registration messages for a {@link SchedulingConnector} shared connection.
 * Both peers send HELLO simultaneously before normal connector establishment. Its payload is opcode 0, the four-byte big-endian
 * magic, and the four-byte big-endian control version, inside the normal Net4j frame on reserved channel 0. A connector
 * stays in {@code CONNECTING} until it validates the peer HELLO; configured authentication negotiation then runs before the connector
 * reaches {@code CONNECTED}. Version 1 denotes the shared control format plus this mandatory handshake; pre-handshake implementations do
 * not implicitly use version 1. Increment {@link #CONTROL_PROTOCOL_VERSION} only when changing the control wire contract incompatibly.
 * Acknowledgements are correlated by channel ID, so a genuinely late acknowledgement can still satisfy a later registration if that ID
 * has already been reused.
 *
 * @author Eike Stepper
 */
public class ControlChannel extends Channel
{
  /**
   * Initial shared control wire version. Version 1 uses the shared control messages and requires the HELLO exchange described by this
   * class; it does not identify pre-handshake peers. This value is independent of bundle and application protocol versions. Increment it
   * for every future incompatible control wire change, including a change to HELLO encoding or to registration and acknowledgement
   * semantics. Compatible additions that preserve existing peers' interpretation do not require an increment.
   */
  public static final int CONTROL_PROTOCOL_VERSION = 1;

  /**
   * The reserved ID used for control messages.
   */
  public static final short CONTROL_CHANNEL_ID = 0;

  /**
   * The HELLO opcode used to exchange the shared control wire-protocol version.
   */
  public static final byte OPCODE_HELLO = 0;

  /**
   * The negotiation message opcode.
   */
  public static final byte OPCODE_NEGOTIATION = 1;

  /**
   * The channel registration message opcode.
   */
  public static final byte OPCODE_REGISTRATION = 2;

  /**
   * The registration acknowledgement message opcode.
   */
  public static final byte OPCODE_REGISTRATION_ACK = 3;

  /**
   * The channel deregistration message opcode.
   */
  public static final byte OPCODE_DEREGISTRATION = 4;

  private static final int HELLO_MAGIC = 0x45494B45;

  private static final long HELLO_TIMEOUT_MILLIS = 10000;

  private static final String SUCCESS = ""; //$NON-NLS-1$

  private final SynchronizingCorrelator<Short, String> acknowledgements = new SynchronizingCorrelator<>();

  private final Map<Short, ISynchronizer<String>> pendingAcknowledgements = new HashMap<>();

  private final Object registrationLock = new Object();

  private boolean shuttingDown;

  private volatile boolean helloReceived;

  private volatile boolean helloSent;

  private volatile boolean helloCompleted;

  private TimerTask helloTimeout;

  private int remoteVersion = -1;

  private long handshakeGeneration;

  /**
   * Creates the control channel for its owning connector.
   *
   * @param connector the owning connector
   */
  public ControlChannel(SchedulingConnector connector)
  {
    setID(CONTROL_CHANNEL_ID);
    setMultiplexer(connector);
    setUserID(connector.getUserID());
  }

  /** Returns whether a complete frame has the HELLO channel, opcode, magic, and fixed payload length. */
  public static boolean isWellFormedHelloFrame(ByteBuffer frame)
  {
    int offset = frame.position();
    if (frame.remaining() != IBuffer.HEADER_SIZE + 9 || frame.getShort(offset + IBuffer.CHANNEL_ID_POS) != CONTROL_CHANNEL_ID
        || frame.getShort(offset + IBuffer.PAYLOAD_SIZE_POS) != 9)
    {
      return false;
    }

    int payloadOffset = offset + IBuffer.HEADER_SIZE;
    return frame.get(payloadOffset) == OPCODE_HELLO && frame.getInt(payloadOffset + 1) == HELLO_MAGIC;
  }

  /**
   * Registers a local channel with its peer and waits for the correlated acknowledgement.
   *
   * @param channelID the local channel ID
   * @param timeout the maximum wait in milliseconds
   * @param protocol the channel protocol
   */
  public void registerChannel(short channelID, long timeout, IProtocol<?> protocol)
  {
    assertValidChannelID(channelID);
    ISynchronizer<String> acknowledgement;

    synchronized (registrationLock)
    {
      if (shuttingDown)
      {
        throw new ChannelException("Control channel is shutting down"); //$NON-NLS-1$
      }

      acknowledgement = acknowledgements.correlateUnique(channelID);
      pendingAcknowledgements.put(channelID, acknowledgement);
    }

    try
    {
      IBuffer buffer = provideBuffer();
      boolean handedOff = false;

      try
      {
        ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
        byteBuffer.put(OPCODE_REGISTRATION);
        byteBuffer.putShort(channelID);
        byteBuffer.putInt(Net4jUtil.getProtocolVersion(protocol));
        BufferUtil.putString(byteBuffer, Net4jUtil.getProtocolID(protocol), false);

        handedOff = true;
        handleBuffer(buffer);
      }
      catch (RuntimeException | Error ex)
      {
        if (!handedOff)
        {
          buffer.release();
        }

        throw ex;
      }

      String error = acknowledgement.get(timeout);
      if (error == null)
      {
        throw new TimeoutRuntimeException(MessageFormat.format("Registration timeout after {0} milliseconds", timeout)); //$NON-NLS-1$
      }

      if (!SUCCESS.equals(error))
      {
        throw new ChannelException("Failed to register channel with peer: " + error); //$NON-NLS-1$
      }
    }
    finally
    {
      synchronized (registrationLock)
      {
        pendingAcknowledgements.remove(channelID, acknowledgement);
        acknowledgements.uncorrelate(channelID);
      }
    }
  }

  /**
   * Begins the mandatory control-protocol handshake after transport readiness by queueing this peer's HELLO and starting the bounded
   * wait for the peer's HELLO. The receive path completes the connector lifecycle only after validating the peer's magic and version.
   *
   * @param handshakeGeneration the activation generation that owns this exchange
   */
  public void startHandshake(long handshakeGeneration)
  {
    synchronized (registrationLock)
    {
      this.handshakeGeneration = handshakeGeneration;
      helloReceived = false;
      helloSent = false;
      helloCompleted = false;
      remoteVersion = -1;
    }

    IBuffer buffer = provideBuffer();
    boolean handedOff = false;

    try
    {
      ByteBuffer bytes = buffer.startPutting(CONTROL_CHANNEL_ID);
      bytes.put(OPCODE_HELLO);
      bytes.putInt(HELLO_MAGIC);
      bytes.putInt(CONTROL_PROTOCOL_VERSION);

      handedOff = true;
      handleBuffer(buffer);
    }
    finally
    {
      if (!handedOff)
      {
        buffer.release();
      }
    }

    TimerTask timeout = new TimerTask()
    {
      @Override
      public void run()
      {
        String diagnostic = null;

        synchronized (registrationLock)
        {
          if (!helloCompleted && !shuttingDown)
          {
            diagnostic = helloReceived //
                ? "Net4j control protocol HELLO exchange timed out after receiving remote version " + remoteVersion //$NON-NLS-1$
                : "Incompatible Net4j control protocol: remote version is unknown (HELLO timeout)"; //$NON-NLS-1$
          }
        }

        if (diagnostic != null)
        {
          IncompatibleControlProtocolException failure = new IncompatibleControlProtocolException(diagnostic);
          ConcurrencyUtil.execute(getConnector().getConfig().getReceiveExecutor(), //
              () -> getConnector().failControlHandshake(handshakeGeneration, failure));
        }
      }
    };

    IManagedContainer container = getContainer();
    if (container == null)
    {
      container = IManagedContainer.INSTANCE;
    }

    Timer timer = TimerLifecycle.DaemonFactory.getTimer(container, null);
    timer.schedule(timeout, HELLO_TIMEOUT_MILLIS);

    synchronized (registrationLock)
    {
      if (helloCompleted || shuttingDown)
      {
        timeout.cancel();
      }
      else
      {
        helloTimeout = timeout;
      }
    }
  }

  /**
   * Records that this activation's HELLO was accepted by the transport and completes the exchange if the peer's HELLO was already
   * validated.
   *
   * @param handshakeGeneration the activation generation that sent the HELLO
   */
  public void helloSent(long handshakeGeneration)
  {
    synchronized (registrationLock)
    {
      if (handshakeGeneration != this.handshakeGeneration || helloSent || helloCompleted || shuttingDown)
      {
        return;
      }

      helloSent = true;
    }

    completeHandshakeIfReady();
  }

  /**
   * Cancels the timeout associated with the supplied activation after the connector has accepted a handshake failure. A stale failure
   * callback cannot cancel a later activation's timeout.
   *
   * @param handshakeGeneration the activation generation whose timeout is to be cancelled
   */
  public void cancelHandshakeTimeout(long handshakeGeneration)
  {
    synchronized (registrationLock)
    {
      if (this.handshakeGeneration == handshakeGeneration)
      {
        cancelHelloTimeout();
      }
    }
  }

  /**
   * Sends a deregistration message for a channel.
   *
   * @param channelID the channel ID
   */
  public void deregisterChannel(short channelID)
  {
    assertValidChannelID(channelID);

    IBuffer buffer = provideBuffer();
    boolean handedOff = false;

    try
    {
      ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
      byteBuffer.put(OPCODE_DEREGISTRATION);
      byteBuffer.putShort(channelID);

      handedOff = true;
      handleBuffer(buffer);
    }
    catch (RuntimeException | Error ex)
    {
      if (!handedOff)
      {
        buffer.release();
      }

      throw ex;
    }
  }

  @Override
  public void handleBufferFromMultiplexer(IBuffer buffer)
  {
    try
    {
      byte opcode = buffer.get();
      if (opcode != OPCODE_HELLO && !helloCompleted)
      {
        throw new IncompatibleControlProtocolException(
            "Incompatible Net4j control protocol: unexpected control opcode " + opcode + " before HELLO; remote version is unknown"); //$NON-NLS-1$
      }

      switch (opcode)
      {
      case OPCODE_HELLO:
      {
        synchronized (registrationLock)
        {
          if (helloCompleted)
          {
            throw new IllegalArgumentException("Unexpected duplicate HELLO for Net4j control protocol version " + CONTROL_PROTOCOL_VERSION); //$NON-NLS-1$
          }

          if (helloReceived)
          {
            throw new IncompatibleControlProtocolException("Unexpected duplicate Net4j control protocol HELLO"); //$NON-NLS-1$
          }
        }

        ByteBuffer bytes = buffer.getByteBuffer();
        if (bytes.remaining() != 8 || bytes.getInt() != HELLO_MAGIC)
        {
          throw new IncompatibleControlProtocolException("Malformed Net4j control protocol HELLO"); //$NON-NLS-1$
        }

        int peerVersion = bytes.getInt();
        if (peerVersion != CONTROL_PROTOCOL_VERSION)
        {
          throw new IncompatibleControlProtocolException(
              "Incompatible Net4j control protocol: local version " + CONTROL_PROTOCOL_VERSION + ", remote version " + peerVersion); //$NON-NLS-1$
        }

        synchronized (registrationLock)
        {
          helloReceived = true;
          remoteVersion = peerVersion;
        }

        completeHandshakeIfReady();
        break;
      }

      case OPCODE_NEGOTIATION:
      {
        SchedulingConnector connector = getConnector();
        if (!connector.isNegotiating())
        {
          throw new IllegalStateException("Connector is not negotiating"); //$NON-NLS-1$
        }

        INegotiationContext context = connector.getNegotiationContext();
        if (context == null)
        {
          throw new IllegalStateException("Negotiation context is not available"); //$NON-NLS-1$
        }

        Receiver receiver = context.getReceiver();
        receiver.receiveBuffer(context, buffer.getByteBuffer());
        break;
      }

      case OPCODE_REGISTRATION:
      {
        SchedulingConnector connector = getConnector();
        assertConnected(connector);

        short channelID = buffer.getShort();
        assertValidChannelID(channelID);
        String error = SUCCESS;

        try
        {
          int version = buffer.getInt();
          String protocolID = buffer.getString();

          InternalChannel channel = connector.inverseOpenChannel(channelID, protocolID, version);
          if (channel == null)
          {
            throw new ConnectorException("Could not open channel"); //$NON-NLS-1$
          }
        }
        catch (Exception ex)
        {
          error = ex.getMessage();
          if (error == null || error.isEmpty())
          {
            error = ex.getClass().getSimpleName();
          }
        }

        sendStatus(OPCODE_REGISTRATION_ACK, channelID, error);
        break;
      }

      case OPCODE_REGISTRATION_ACK:
      {
        SchedulingConnector connector = getConnector();
        assertConnected(connector);

        short channelID = buffer.getShort();
        assertValidChannelID(channelID);
        String error = buffer.getString();

        synchronized (registrationLock)
        {
          if (!shuttingDown)
          {
            acknowledgements.putIfCorrelated(channelID, error == null ? "Malformed acknowledgement" : error);
          }
        }

        break;
      }

      case OPCODE_DEREGISTRATION:
      {
        SchedulingConnector connector = getConnector();
        assertConnected(connector);

        short channelID = buffer.getShort();
        assertValidChannelID(channelID);
        connector.inverseCloseChannel(channelID);
        break;
      }

      default:
        throw new IllegalArgumentException("Invalid control opcode " + opcode + " for Net4j control protocol version " + CONTROL_PROTOCOL_VERSION); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ex)
    {
      if (!helloCompleted)
      {
        synchronized (registrationLock)
        {
          cancelHelloTimeout();
        }

        IncompatibleControlProtocolException failure = ex instanceof IncompatibleControlProtocolException //
            ? (IncompatibleControlProtocolException)ex //
            : new IncompatibleControlProtocolException("Malformed or unexpected Net4j control protocol message before HELLO", ex); //$NON-NLS-1$
        getConnector().failControlHandshake(handshakeGeneration, failure);
        throw failure;
      }

      throw ex;
    }
    finally
    {
      buffer.release();
    }
  }

  @Override
  protected void unregisterFromMultiplexer()
  {
    // The control channel is owned by the connector, not the application channel map.
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (registrationLock)
    {
      shuttingDown = true;
      cancelHelloTimeout();

      for (Map.Entry<Short, ISynchronizer<String>> entry : pendingAcknowledgements.entrySet())
      {
        entry.getValue().put("Control channel deactivated"); //$NON-NLS-1$
      }
    }

    super.doDeactivate();
  }

  private SchedulingConnector getConnector()
  {
    return (SchedulingConnector)getMultiplexer();
  }

  private void completeHandshakeIfReady()
  {
    boolean complete = false;
    long generation;

    synchronized (registrationLock)
    {
      if (helloSent && helloReceived && !helloCompleted && !shuttingDown)
      {
        helloCompleted = true;
        cancelHelloTimeout();

        complete = true;
      }

      generation = handshakeGeneration;
    }

    if (complete)
    {
      getConnector().controlHandshakeCompleted(generation);
    }
  }

  private void cancelHelloTimeout()
  {
    if (helloTimeout != null)
    {
      helloTimeout.cancel();
      helloTimeout = null;
    }
  }

  private void sendStatus(byte opcode, short channelID, String error)
  {
    IBuffer buffer = provideBuffer();
    boolean handedOff = false;

    try
    {
      ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
      byteBuffer.put(opcode);
      byteBuffer.putShort(channelID);
      BufferUtil.putString(byteBuffer, error, true);

      handedOff = true;
      handleBuffer(buffer);
    }
    catch (RuntimeException | Error ex)
    {
      if (!handedOff)
      {
        buffer.release();
      }

      throw ex;
    }
  }

  private void assertConnected(SchedulingConnector connector)
  {
    if (!connector.isConnected())
    {
      throw new IllegalStateException("Connector is not connected"); //$NON-NLS-1$
    }
  }

  private void assertValidChannelID(short channelID)
  {
    if (channelID == CONTROL_CHANNEL_ID || channelID == IBuffer.NO_CHANNEL)
    {
      throw new IllegalArgumentException("Invalid channel ID: " + channelID); //$NON-NLS-1$
    }
  }
}
