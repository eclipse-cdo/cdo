/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IProtocol;
import org.eclipse.net4j.util.concurrent.ISynchronizer;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.BufferUtil;
import org.eclipse.internal.net4j.transport.Channel;
import org.eclipse.internal.net4j.util.concurrent.SynchronizingCorrelator;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public final class ControlChannel extends Channel
{
  public static final short CONTROL_CHANNEL_ID = -1;

  public static final long REGISTRATION_TIMEOUT = 500000;

  public static final byte OPCODE_REGISTRATION = 1;

  public static final byte OPCODE_REGISTRATION_ACK = 2;

  public static final byte OPCODE_DEREGISTRATION = 3;

  public static final byte SUCCESS = 1;

  public static final byte FAILURE = 0;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CHANNEL, ControlChannel.class);

  private SynchronizingCorrelator<Short, Boolean> registrations = new SynchronizingCorrelator();

  public ControlChannel(TCPConnector connector)
  {
    super(connector.getReceiveExecutor());
    setChannelIndex(CONTROL_CHANNEL_ID);
    setConnector(connector);
  }

  @Override
  public boolean isInternal()
  {
    return true;
  }

  public boolean registerChannel(short channelIndex, IProtocol protocol)
  {
    assertValidChannelIndex(channelIndex);
    ISynchronizer<Boolean> registration = registrations.correlate(channelIndex);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
    byteBuffer.put(OPCODE_REGISTRATION);
    byteBuffer.putShort(channelIndex);
    BufferUtil.putUTF8(byteBuffer, protocol == null ? null : protocol.getType());
    handleBuffer(buffer);

    return registration.get(REGISTRATION_TIMEOUT);
  }

  public void deregisterChannel(short channelIndex)
  {
    assertValidChannelIndex(channelIndex);

    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
    byteBuffer.put(OPCODE_DEREGISTRATION);
    byteBuffer.putShort(channelIndex);
    handleBuffer(buffer);
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
      case OPCODE_REGISTRATION:
      {
        short channelIndex = byteBuffer.getShort();
        assertValidChannelIndex(channelIndex);
        boolean success = true;

        try
        {
          byte[] handlerFactoryUTF8 = BufferUtil.getByteArray(byteBuffer);
          String protocolID = BufferUtil.fromUTF8(handlerFactoryUTF8);
          Channel channel = ((TCPConnector)getConnector()).createChannel(channelIndex, protocolID);
          if (channel != null)
          {
            channel.activate();
          }
          else
          {
            success = false;
          }
        }
        catch (Exception ex)
        {
          Net4j.LOG.error(ex);
          success = false;
        }

        sendStatus(OPCODE_REGISTRATION_ACK, channelIndex, success);
        break;
      }

      case OPCODE_REGISTRATION_ACK:
      {
        short channelIndex = byteBuffer.getShort();
        boolean success = byteBuffer.get() == SUCCESS;
        registrations.put(channelIndex, success);
        break;
      }

      case OPCODE_DEREGISTRATION:
      {
        short channelIndex = byteBuffer.getShort();
        assertValidChannelIndex(channelIndex);

        try
        {
          Channel channel = ((TCPConnector)getConnector()).getChannel(channelIndex);
          if (channel != null)
          {
            channel.deactivate();
          }
          else
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Invalid channel id: " + channelIndex); //$NON-NLS-1$
            }
          }
        }
        catch (Exception ex)
        {
          Net4j.LOG.error(ex);
        }

        break;
      }

      default:
        Net4j.LOG.error("Invalid opcode: " + opcode); //$NON-NLS-1$
        ((TCPConnector)getConnector()).deactivate();
      }
    }
    finally
    {
      buffer.release();
    }
  }

  private void sendStatus(byte opcode, short channelIndex, boolean status)
  {
    IBuffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
    byteBuffer.put(opcode);
    byteBuffer.putShort(channelIndex);
    byteBuffer.put(status ? SUCCESS : FAILURE);
    handleBuffer(buffer);
  }

  private void assertValidChannelIndex(short channelIndex)
  {
    if (channelIndex <= CONTROL_CHANNEL_ID)
    {
      throw new IllegalArgumentException("channelIndex <= CONTROL_CHANNEL_ID"); //$NON-NLS-1$
    }
  }
}
