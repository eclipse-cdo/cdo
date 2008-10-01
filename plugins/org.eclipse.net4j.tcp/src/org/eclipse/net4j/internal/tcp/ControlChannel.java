/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.internal.tcp.bundle.OM;
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
import org.eclipse.internal.net4j.channel.Channel;

import org.eclipse.spi.net4j.InternalChannel;

import java.nio.ByteBuffer;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ControlChannel extends Channel
{
  public static final short CONTROL_CHANNEL_INDEX = IBuffer.CONTROL_CHANNEL;

  public static final byte OPCODE_NEGOTIATION = 1;

  public static final byte OPCODE_REGISTRATION = 2;

  public static final byte OPCODE_REGISTRATION_ACK = 3;

  public static final byte OPCODE_DEREGISTRATION = 4;

  public static final byte OPCODE_DEREGISTRATION_ACK = 5;

  public static final byte SUCCESS = 1;

  public static final byte FAILURE = 0;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ControlChannel.class);

  private SynchronizingCorrelator<Short, Boolean> acknowledgements = new SynchronizingCorrelator<Short, Boolean>();

  public ControlChannel(TCPConnector connector)
  {
    setChannelIndex(CONTROL_CHANNEL_INDEX);
    setChannelMultiplexer(connector);
    setReceiveExecutor(connector.getConfig().getReceiveExecutor());
    setUserID(connector.getUserID());
  }

  public TCPConnector getConnector()
  {
    return (TCPConnector)getChannelMultiplexer();
  }

  public boolean registerChannel(short channelIndex, long timeout, IProtocol protocol)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering channel {0} with protocol {1}", channelIndex, protocol);
    }

    assertValidChannelIndex(channelIndex);
    ISynchronizer<Boolean> acknowledgement = acknowledgements.correlate(channelIndex);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(OPCODE_REGISTRATION);
    byteBuffer.putShort(channelIndex);
    BufferUtil.putUTF8(byteBuffer, protocol == null ? null : protocol.getType());
    handleBuffer(buffer);

    Boolean acknowledged = acknowledgement.get(timeout);
    if (acknowledged == null)
    {
      throw new TimeoutRuntimeException("Registration timeout after " + timeout + " milliseconds");
    }

    return acknowledged;
  }

  public boolean deregisterChannel(short channelIndex, long timeout)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering channel {0}", channelIndex);
    }

    assertValidChannelIndex(channelIndex);
    ISynchronizer<Boolean> acknowledgement = acknowledgements.correlate(channelIndex);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(OPCODE_DEREGISTRATION);
    byteBuffer.putShort(channelIndex);
    handleBuffer(buffer);

    Boolean acknowledged = acknowledgement.get(timeout);
    if (acknowledged == null)
    {
      throw new TimeoutRuntimeException("Deregistration timeout after " + timeout + " milliseconds");
    }

    return acknowledged;
  }

  @Override
  public void handleBufferFromMultiplexer(IBuffer buffer)
  {
    try
    {
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      byte opcode = byteBuffer.get();
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
        receiver.receiveBuffer(negotiationContext, byteBuffer);
        break;
      }

      case OPCODE_REGISTRATION:
      {
        assertConnected();
        short channelIndex = byteBuffer.getShort();
        assertValidChannelIndex(channelIndex);
        boolean success = true;

        try
        {
          byte[] handlerFactoryUTF8 = BufferUtil.getByteArray(byteBuffer);
          String protocolID = BufferUtil.fromUTF8(handlerFactoryUTF8);
          InternalChannel channel = getConnector().inverseOpenChannel(channelIndex, protocolID);
          if (channel == null)
          {
            throw new ConnectorException("Could not open channel");
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          success = false;
        }

        sendStatus(OPCODE_REGISTRATION_ACK, channelIndex, success);
        break;
      }

      case OPCODE_DEREGISTRATION:
      {
        assertConnected();
        boolean success = true;
        short channelIndex = byteBuffer.getShort();
        if (channelIndex == CONTROL_CHANNEL_INDEX)
        {
          throw new ImplementationError();
        }

        try
        {
          getConnector().inverseCloseChannel(channelIndex);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          success = false;
        }

        sendStatus(OPCODE_DEREGISTRATION_ACK, channelIndex, success);
        break;
      }

      case OPCODE_REGISTRATION_ACK:
      case OPCODE_DEREGISTRATION_ACK:
      {
        assertConnected();
        short channelIndex = byteBuffer.getShort();
        boolean success = byteBuffer.get() == SUCCESS;
        acknowledgements.put(channelIndex, success);
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
  protected void doDeactivate() throws Exception
  {
    finishDeactivate(true);
  }

  private void sendStatus(byte opcode, short channelIndex, boolean status)
  {
    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_INDEX);
    byteBuffer.put(opcode);
    byteBuffer.putShort(channelIndex);
    byteBuffer.put(status ? SUCCESS : FAILURE);
    handleBuffer(buffer);
  }

  private void assertNegotiating()
  {
    if (!getConnector().isNegotiating())
    {
      getConnector().deactivate();
      throw new IllegalStateException("Connector is not negotiating");
    }
  }

  private void assertConnected()
  {
    if (!getConnector().isConnected())
    {
      throw new IllegalStateException("Connector is not connected");
    }
  }

  private void assertValidChannelIndex(short channelIndex)
  {
    if (channelIndex <= CONTROL_CHANNEL_INDEX)
    {
      throw new IllegalArgumentException("channelIndex <= CONTROL_CHANNEL_ID"); //$NON-NLS-1$
    }
  }
}
