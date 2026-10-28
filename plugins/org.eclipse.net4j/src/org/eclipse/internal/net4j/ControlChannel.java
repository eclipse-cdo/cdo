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
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.concurrent.ISynchronizer;
import org.eclipse.net4j.util.concurrent.SynchronizingCorrelator;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
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

/**
 * Implements negotiation and channel registration messages for a {@link SchedulingConnector} shared connection. Acknowledgements are
 * correlated by channel ID, so a genuinely late acknowledgement can still satisfy a later registration if that ID has already been reused.
 *
 * @author Eike Stepper
 */
public class ControlChannel extends Channel
{
  /**
   * The reserved ID used for control messages.
   */
  public static final short CONTROL_CHANNEL_ID = 0;

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

  private static final String SUCCESS = ""; //$NON-NLS-1$

  private final SynchronizingCorrelator<Short, String> acknowledgements = new SynchronizingCorrelator<>();

  private final Map<Short, ISynchronizer<String>> pendingAcknowledgements = new HashMap<>();

  private final Object registrationLock = new Object();

  private boolean shuttingDown;

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
      switch (opcode)
      {
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
        throw new IllegalArgumentException("Invalid control opcode: " + opcode); //$NON-NLS-1$
      }
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
