/*
 * Copyright (c) 2007-2009, 2011, 2012, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.internal.tcp.messages.Messages;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ISynchronizer;
import org.eclipse.net4j.util.concurrent.SynchronizingCorrelator;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiationContext.Receiver;

import org.eclipse.internal.net4j.buffer.BufferUtil;

import org.eclipse.spi.net4j.Channel;
import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.InternalChannelMultiplexer;

import java.nio.ByteBuffer;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ControlChannel extends Channel
{
  public static final short CONTROL_CHANNEL_INDEX = InternalChannelMultiplexer.RESERVED_CHANNEL;

  public static final byte OPCODE_NEGOTIATION = 1;

  /**
   * @deprecated Indicates Net4j version before 4.2. As of 4.2 Net4j uses {@link #OPCODE_REGISTRATION_VERSIONED}.
   */
  @Deprecated
  public static final byte OPCODE_REGISTRATION = 2;

  public static final byte OPCODE_REGISTRATION_ACK = 3;

  public static final byte OPCODE_DEREGISTRATION = 4;

  public static final byte OPCODE_REGISTRATION_VERSIONED = 5;

  private static final String SUCCESS = "Success";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ControlChannel.class);

  private SynchronizingCorrelator<Short, String> acknowledgements = new SynchronizingCorrelator<Short, String>();

  public ControlChannel(TCPConnector connector)
  {
    setID(CONTROL_CHANNEL_INDEX);
    setMultiplexer(connector);
    setUserID(connector.getUserID());
  }

  public TCPConnector getConnector()
  {
    return (TCPConnector)getMultiplexer();
  }

  public void registerChannel(short channelID, long timeout, IProtocol<?> protocol)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering channel {0} with protocol {1}", channelID, protocol); //$NON-NLS-1$
    }

    assertValidChannelID(channelID);
    ISynchronizer<String> acknowledgement = acknowledgements.correlate(channelID);

    int protocolVersion = Net4jUtil.getProtocolVersion(protocol);
    String protocolID = Net4jUtil.getProtocolID(protocol);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(OPCODE_REGISTRATION_VERSIONED);
    byteBuffer.putShort(channelID);
    byteBuffer.putInt(protocolVersion);
    BufferUtil.putString(byteBuffer, protocolID, false);
    handleBuffer(buffer);

    String error = acknowledgement.get(timeout);
    if (error == null)
    {
      throw new TimeoutRuntimeException(MessageFormat.format(Messages.getString("ControlChannel_0"), timeout)); //$NON-NLS-1$
    }

    if (error != SUCCESS)
    {
      throw new ChannelException("Failed to register channel with peer: " + error); //$NON-NLS-1$
    }
  }

  public void deregisterChannel(short channelID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering channel {0}", channelID); //$NON-NLS-1$
    }

    assertValidChannelID(channelID);
    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(OPCODE_DEREGISTRATION);
    byteBuffer.putShort(channelID);
    handleBuffer(buffer);
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
        assertNegotiating();
        INegotiationContext negotiationContext = getConnector().getNegotiationContext();
        while (negotiationContext == null)
        {
          ConcurrencyUtil.sleep(20);
          negotiationContext = getConnector().getNegotiationContext();
        }

        Receiver receiver = negotiationContext.getReceiver();
        receiver.receiveBuffer(negotiationContext, buffer.getByteBuffer());
        break;
      }

      case OPCODE_REGISTRATION:
        OM.LOG.error("Deprecated opcode: Client should use newer Net4j version"); //$NON-NLS-1$
        getConnector().deactivate();
        break;

      case OPCODE_REGISTRATION_VERSIONED:
      {
        assertConnected();
        short channelID = buffer.getShort();
        assertValidChannelID(channelID);
        String error = null;

        try
        {
          int protocolVersion = buffer.getInt();
          String protocolID = buffer.getString();

          InternalChannel channel = getConnector().inverseOpenChannel(channelID, protocolID, protocolVersion);
          if (channel == null)
          {
            throw new ConnectorException(Messages.getString("ControlChannel_4")); //$NON-NLS-1$
          }
        }
        catch (Exception ex)
        {
          error = ex.getMessage();
          if (TRACER.isEnabled())
          {
            TRACER.trace("Problem during channel registration", ex); //$NON-NLS-1$
          }
        }

        sendStatus(OPCODE_REGISTRATION_ACK, channelID, error);
        break;
      }

      case OPCODE_DEREGISTRATION:
      {
        assertConnected();
        short channelID = buffer.getShort();
        if (channelID == CONTROL_CHANNEL_INDEX)
        {
          throw new ImplementationError();
        }

        try
        {
          getConnector().inverseCloseChannel(channelID);
        }
        catch (Exception ex)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Problem during channel deregistration", ex); //$NON-NLS-1$
          }
        }

        break;
      }

      case OPCODE_REGISTRATION_ACK:
      {
        assertConnected();
        short channelID = buffer.getShort();
        String error = buffer.getString();
        if (error == null)
        {
          error = SUCCESS;
        }

        acknowledgements.put(channelID, error);
        break;
      }

      default:
        OM.LOG.error("Invalid opcode: " + opcode); //$NON-NLS-1$
        getConnector().deactivate();
      }
    }
    finally
    {
      buffer.release();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Channel[Control, {0}]", getLocation()); //$NON-NLS-1$
  }

  @Override
  protected void unregisterFromMultiplexer()
  {
    // Do nothing
  }

  private void sendStatus(byte opcode, short channelID, String error)
  {
    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(opcode);
    byteBuffer.putShort(channelID);
    BufferUtil.putString(byteBuffer, error, true);
    handleBuffer(buffer);
  }

  private void assertNegotiating()
  {
    if (!getConnector().isNegotiating())
    {
      getConnector().deactivate();
      throw new IllegalStateException("Connector is not negotiating"); //$NON-NLS-1$
    }
  }

  private void assertConnected()
  {
    if (!getConnector().isConnected())
    {
      throw new IllegalStateException("Connector is not connected"); //$NON-NLS-1$
    }
  }

  private void assertValidChannelID(short channelID)
  {
    if (channelID == CONTROL_CHANNEL_INDEX)
    {
      throw new IllegalArgumentException("Bad channelID"); //$NON-NLS-1$
    }
  }
}
