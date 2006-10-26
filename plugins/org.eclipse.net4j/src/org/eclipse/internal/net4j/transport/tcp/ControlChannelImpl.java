package org.eclipse.internal.net4j.transport.tcp;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.util.concurrent.Synchronizer;
import org.eclipse.net4j.util.concurrent.SynchronizingCorrelator;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.BufferUtil;
import org.eclipse.internal.net4j.transport.ChannelImpl;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public final class ControlChannelImpl extends ChannelImpl
{
  public static final short CONTROL_CHANNEL_ID = -1;

  public static final long REGISTRATION_TIMEOUT = 500000;

  public static final byte OPCODE_REGISTRATION = 1;

  public static final byte OPCODE_REGISTRATION_ACK = 2;

  public static final byte OPCODE_DEREGISTRATION = 3;

  public static final byte SUCCESS = 1;

  public static final byte FAILURE = 0;

  private SynchronizingCorrelator<Short, Boolean> registrations = new SynchronizingCorrelator();

  public ControlChannelImpl(AbstractTCPConnector connector)
  {
    super(connector.getReceiveExecutor());
    setChannelID(CONTROL_CHANNEL_ID);
    setConnector(connector);
  }

  public boolean registerChannel(short channelID, String protocolID)
  {
    assertValidChannelID(channelID);
    Synchronizer<Boolean> registration = registrations.correlate(channelID);

    Buffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
    byteBuffer.put(OPCODE_REGISTRATION);
    byteBuffer.putShort(channelID);
    BufferUtil.putUTF8(byteBuffer, protocolID);
    handleBuffer(buffer);

    return registration.get(REGISTRATION_TIMEOUT);
  }

  public void deregisterChannel(short channelID)
  {
    assertValidChannelID(channelID);

    Buffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
    byteBuffer.put(OPCODE_DEREGISTRATION);
    byteBuffer.putShort(channelID);
    handleBuffer(buffer);
  }

  private void assertValidChannelID(short channelID)
  {
    if (channelID <= CONTROL_CHANNEL_ID)
    {
      throw new IllegalArgumentException("channelID <= CONTROL_CHANNEL_ID"); //$NON-NLS-1$
    }
  }

  public void handleBufferFromMultiplexer(Buffer buffer)
  {
    try
    {
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      byte opcode = byteBuffer.get();
      switch (opcode)
      {
      case OPCODE_REGISTRATION:
      {
        short channelID = byteBuffer.getShort();
        assertValidChannelID(channelID);
        boolean success = true;

        try
        {
          byte[] handlerFactoryUTF8 = BufferUtil.getByteArray(byteBuffer);
          String protocolID = BufferUtil.fromUTF8(handlerFactoryUTF8);
          ChannelImpl channel = ((AbstractTCPConnector)getConnector()).createChannel(channelID,
              protocolID);
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

        sendStatus(OPCODE_REGISTRATION_ACK, channelID, success);
        break;
      }

      case OPCODE_REGISTRATION_ACK:
      {
        short channelID = byteBuffer.getShort();
        boolean success = byteBuffer.get() == SUCCESS;
        registrations.put(channelID, success);
        break;
      }

      case OPCODE_DEREGISTRATION:
        throw new UnsupportedOperationException();

      default:
        Net4j.LOG.error("Invalid opcode: " + opcode); //$NON-NLS-1$
        ((AbstractTCPConnector)getConnector()).deactivate();
      }
    }
    finally
    {
      buffer.release();
    }
  }

  // private Buffer getBuffer()
  // {
  // return
  // ((AbstractTCPConnector)getConnector()).getBufferProvider().provideBuffer();
  // }

  private void sendStatus(byte opcode, short channelID, boolean status)
  {
    Buffer buffer = provideBuffer();
    ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
    byteBuffer.put(opcode);
    byteBuffer.putShort(channelID);
    byteBuffer.put(status ? SUCCESS : FAILURE);
    handleBuffer(buffer);
  }
}
