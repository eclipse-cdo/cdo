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
package org.eclipse.net4j.http.internal.common;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.http.common.IHTTPConnector;
import org.eclipse.net4j.http.internal.common.bundle.OM;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.connector.Connector;

import org.eclipse.spi.net4j.InternalChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class HTTPConnector extends Connector implements IHTTPConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPConnector.class);

  private static final byte OPERATION_NONE = 0;

  private static final byte OPERATION_OPEN = 1;

  private static final byte OPERATION_OPEN_ACK = 2;

  private static final byte OPERATION_CLOSE = 3;

  private static final byte OPERATION_BUFFER = 4;

  private String connectorID;

  private transient Queue<ChannelOperation> outputOperations = new ConcurrentLinkedQueue<ChannelOperation>();

  private transient long lastTraffic;

  public static final int OPCODE_CONNECT = 1;

  public static final int OPCODE_DISCONNECT = 2;

  public static final int OPCODE_OPERATIONS = 3;

  public HTTPConnector()
  {
    markLastTraffic();
  }

  public String getConnectorID()
  {
    return connectorID;
  }

  public void setConnectorID(String connectorID)
  {
    this.connectorID = connectorID;
  }

  public Queue<ChannelOperation> getOutputQueue()
  {
    return outputOperations;
  }

  public long getLastTraffic()
  {
    return lastTraffic;
  }

  private void markLastTraffic()
  {
    lastTraffic = System.currentTimeMillis();
  }

  public void multiplexChannel(IChannel channel)
  {
    IBuffer buffer;
    long outputOperationCount;

    HTTPChannel httpChannel = (HTTPChannel)channel;
    synchronized (httpChannel)
    {
      Queue<IBuffer> channelQueue = httpChannel.getSendQueue();
      buffer = channelQueue.poll();
      outputOperationCount = httpChannel.getOutputOperationCount();
      httpChannel.increaseOutputOperationCount();
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Multiplexing {0} (count={1})", buffer.formatContent(true), outputOperationCount);
    }

    outputOperations.add(new BufferChannelOperation(httpChannel.getChannelIndex(), outputOperationCount, buffer));
  }

  /**
   * Writes operations from the {@link #outputOperations} to the passed stream. After each written operation
   * {@link #writeMoreOperations()} is asked whether to send more operations.
   * 
   * @return <code>true</code> if more operations are in the {@link #outputOperations}, <code>false</code> otherwise.
   */
  public boolean writeOutputOperations(ExtendedDataOutputStream out) throws IOException
  {
    do
    {
      ChannelOperation operation = outputOperations.poll();
      if (operation == null && pollAgain())
      {
        operation = outputOperations.poll();
      }

      if (operation == null)
      {
        break;
      }

      operation.write(out);
      markLastTraffic();
    } while (writeMoreOperations());

    out.writeByte(OPERATION_NONE);
    return !outputOperations.isEmpty();
  }

  public void readInputOperations(ExtendedDataInputStream in) throws IOException
  {
    for (;;)
    {
      ChannelOperation operation;
      byte code = in.readByte();
      switch (code)
      {
      case OPERATION_OPEN:
        operation = new OpenChannelOperation(in);
        break;

      case OPERATION_OPEN_ACK:
        operation = new OpenAckChannelOperation(in);
        break;

      case OPERATION_CLOSE:
        operation = new CloseChannelOperation(in);
        break;

      case OPERATION_BUFFER:
        operation = new BufferChannelOperation(in);
        break;

      case OPERATION_NONE:
        return;

      default:
        throw new IOException("Invalid operation code: " + code);
      }

      markLastTraffic();
      operation.execute();
    }
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected InternalChannel createChannelInstance()
  {
    return new HTTPChannel();
  }

  @Override
  protected void registerChannelWithPeer(final int channelID, final short channelIndex, final IProtocol protocol,
      long timeout) throws ConnectorException
  {
    ChannelOperation operation = new OpenChannelOperation(channelIndex, channelID, protocol.getType());
    outputOperations.add(operation);

    HTTPChannel channel = (HTTPChannel)getChannel(channelIndex);
    channel.waitForOpenAck(timeout);
  }

  @Override
  public boolean removeChannel(IChannel channel)
  {
    if (super.removeChannel(channel))
    {
      HTTPChannel httpChannel = (HTTPChannel)channel;
      if (!httpChannel.isInverseRemoved())
      {
        ChannelOperation operation = new CloseChannelOperation(httpChannel);
        outputOperations.add(operation);
      }

      return true;
    }

    return false;
  }

  protected boolean pollAgain()
  {
    return false;
  }

  protected boolean writeMoreOperations()
  {
    return true;
  }

  /**
   * @author Eike Stepper
   */
  public abstract class ChannelOperation
  {
    private short channelIndex;

    private long operationCount;

    public ChannelOperation(short channelIndex, long operationCount)
    {
      this.channelIndex = channelIndex;
      this.operationCount = operationCount;
    }

    public ChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      channelIndex = in.readShort();
      operationCount = in.readLong();
    }

    public void write(ExtendedDataOutputStream out) throws IOException
    {
      out.writeByte(getOperation());
      out.writeShort(channelIndex);
      out.writeLong(operationCount);
    }

    public abstract byte getOperation();

    public short getChannelIndex()
    {
      return channelIndex;
    }

    public long getOperationCount()
    {
      return operationCount;
    }

    public void execute()
    {
      HTTPChannel channel = (HTTPChannel)getChannel(getChannelIndex());
      long operationCount = getOperationCount();
      synchronized (channel)
      {
        // Execute preceding operations if necessary
        while (operationCount < channel.getInputOperationCount())
        {
          ChannelOperation operation = channel.getQuarantinedInputOperation(channel.getInputOperationCount());
          if (operation != null)
          {
            operation.doEexecute(channel);
            channel.increaseInputOperationCount();
          }
          else
          {
            break;
          }
        }

        if (operationCount == channel.getInputOperationCount())
        {
          // Execute operation if possible
          doEexecute(channel);
          channel.increaseInputOperationCount();

          // Execute following operations if possible
          for (;;)
          {
            ChannelOperation operation = channel.getQuarantinedInputOperation(++operationCount);
            if (operation != null)
            {
              operation.doEexecute(channel);
              channel.increaseInputOperationCount();
            }
            else
            {
              break;
            }
          }
        }
        else
        {
          channel.quarantineInputOperation(operationCount, this);
        }
      }
    }

    public abstract void doEexecute(HTTPChannel channel);

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenChannelOperation extends ChannelOperation
  {
    private int channelID;

    private String protocolID;

    public OpenChannelOperation(short channelIndex, int channelID, String protocolID)
    {
      super(channelIndex, 0);
      this.channelID = channelID;
      this.protocolID = protocolID;
    }

    public OpenChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      super(in);
      channelID = in.readInt();
      protocolID = in.readString();
    }

    @Override
    public void write(ExtendedDataOutputStream out) throws IOException
    {
      super.write(out);
      out.writeInt(channelID);
      out.writeString(protocolID);
    }

    @Override
    public byte getOperation()
    {
      return OPERATION_OPEN;
    }

    public int getChannelID()
    {
      return channelID;
    }

    public String getProtocolID()
    {
      return protocolID;
    }

    @Override
    public void execute()
    {
      HTTPChannel channel = (HTTPChannel)createChannel(channelID, getChannelIndex(), protocolID);
      if (channel == null)
      {
        throw new IllegalStateException("Could not open channel");
      }

      channel.increaseInputOperationCount();
      doEexecute(channel);
    }

    @Override
    public void doEexecute(HTTPChannel channel)
    {
      boolean success = false;
      try
      {
        channel.activate();
        success = true;
      }
      finally
      {
        ChannelOperation operation = new OpenAckChannelOperation(getChannelIndex(), success);
        outputOperations.add(operation);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenAckChannelOperation extends ChannelOperation
  {
    private boolean success;

    public OpenAckChannelOperation(short channelIndex, boolean success)
    {
      super(channelIndex, 0);
      this.success = success;
    }

    public OpenAckChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      super(in);
      success = in.readBoolean();
    }

    @Override
    public void write(ExtendedDataOutputStream out) throws IOException
    {
      super.write(out);
      out.writeBoolean(success);
    }

    @Override
    public byte getOperation()
    {
      return OPERATION_OPEN_ACK;
    }

    @Override
    public void doEexecute(HTTPChannel channel)
    {
      channel.openAck();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CloseChannelOperation extends ChannelOperation
  {
    public CloseChannelOperation(HTTPChannel channel)
    {
      super(channel.getChannelIndex(), channel.getOutputOperationCount());
      channel.increaseOutputOperationCount();
    }

    public CloseChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      super(in);
    }

    @Override
    public byte getOperation()
    {
      return OPERATION_CLOSE;
    }

    @Override
    public void doEexecute(HTTPChannel channel)
    {
      // TODO Fix protocol between Channel.close and Connector.removeChannel/inverserRemoveChannel
      channel.setInverseRemoved();
      inverseRemoveChannel(channel.getChannelID(), channel.getChannelIndex());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class BufferChannelOperation extends ChannelOperation
  {
    private IBuffer buffer;

    public BufferChannelOperation(short channelIndex, long operationCount, IBuffer buffer)
    {
      super(channelIndex, operationCount);
      this.buffer = buffer;
    }

    public BufferChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      super(in);
      int length = in.readShort();
      if (TRACER.isEnabled())
      {
        TRACER.format("Receiving Buffer operation: operationID={0}, length={1}", getOperationCount(), length);
      }

      buffer = getBufferProvider().provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(getChannelIndex());
      for (int i = 0; i < length; i++)
      {
        byte b = in.readByte();
        byteBuffer.put(b);
      }

      buffer.flip();
    }

    @Override
    public void write(ExtendedDataOutputStream out) throws IOException
    {
      super.write(out);

      buffer.flip();
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      byteBuffer.position(IBuffer.HEADER_SIZE);

      int length = byteBuffer.limit() - byteBuffer.position();
      out.writeShort(length);
      if (TRACER.isEnabled())
      {
        TRACER.format("Transmitting Buffer operation: operationID={0}, length={1}", getOperationCount(), length);
      }

      for (int i = 0; i < length; i++)
      {
        byte b = byteBuffer.get();
        out.writeByte(b);
      }

      buffer.release();
    }

    @Override
    public byte getOperation()
    {
      return OPERATION_BUFFER;
    }

    public IBuffer getBuffer()
    {
      return buffer;
    }

    @Override
    public void doEexecute(HTTPChannel channel)
    {
      channel.handleBufferFromMultiplexer(buffer);
      buffer = null;
    }

    @Override
    public void dispose()
    {
      if (buffer != null)
      {
        buffer.release();
        buffer = null;
      }

      super.dispose();
    }
  }
}
