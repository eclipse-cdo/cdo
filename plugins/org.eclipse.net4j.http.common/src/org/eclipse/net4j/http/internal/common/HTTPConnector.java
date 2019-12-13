/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.internal.common;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.http.common.IHTTPConnector;
import org.eclipse.net4j.http.internal.common.bundle.OM;
import org.eclipse.net4j.http.internal.common.messages.Messages;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.spi.net4j.Connector;
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

  @Override
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

  @Override
  public void multiplexChannel(InternalChannel channel)
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
      TRACER.format("Multiplexing {0} (count={1})", buffer.formatContent(true), outputOperationCount); //$NON-NLS-1$
    }

    outputOperations.add(new BufferChannelOperation(httpChannel.getID(), outputOperationCount, buffer));

    if (buffer.isCCAM())
    {
      httpChannel.close();
    }
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
        throw new IOException("Invalid operation code: " + code); //$NON-NLS-1$
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
  protected InternalChannel createChannel()
  {
    return new HTTPChannel();
  }

  @Override
  protected void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol) throws ChannelException
  {
    String protocolID = Net4jUtil.getProtocolID(protocol);
    int protocolVersion = Net4jUtil.getProtocolVersion(protocol);

    ChannelOperation operation = new OpenChannelOperation(channelID, protocolID, protocolVersion);
    outputOperations.add(operation);

    HTTPChannel channel = (HTTPChannel)getChannel(channelID);
    channel.waitForOpenAck(timeout);
  }

  @Override
  protected void deregisterChannelFromPeer(InternalChannel channel) throws ChannelException
  {
    HTTPChannel httpChannel = (HTTPChannel)channel;
    if (!httpChannel.isInverseRemoved())
    {
      ChannelOperation operation = new CloseChannelOperation(httpChannel);
      outputOperations.add(operation);
    }
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
    private short channelID;

    private long operationCount;

    public ChannelOperation(short channelID, long operationCount)
    {
      this.channelID = channelID;
      this.operationCount = operationCount;
    }

    public ChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      channelID = in.readShort();
      operationCount = in.readLong();
    }

    public void write(ExtendedDataOutputStream out) throws IOException
    {
      out.writeByte(getOperation());
      out.writeShort(channelID);
      out.writeLong(operationCount);
    }

    public abstract byte getOperation();

    public short getChannelID()
    {
      return channelID;
    }

    public long getOperationCount()
    {
      return operationCount;
    }

    public void execute()
    {
      long operationCount = getOperationCount();

      short channelID = getChannelID();
      HTTPChannel channel = (HTTPChannel)getChannel(channelID);
      if (channel == null)
      {
        OM.LOG.error("Channel " + channelID + " not found");
        return;
      }

      synchronized (channel)
      {
        // Execute preceding operations if necessary
        while (operationCount < channel.getInputOperationCount())
        {
          ChannelOperation operation = channel.getQuarantinedInputOperation(channel.getInputOperationCount());
          if (operation != null)
          {
            operation.doExecute(channel);
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
          doExecute(channel);
          channel.increaseInputOperationCount();

          // Execute following operations if possible
          for (;;)
          {
            ChannelOperation operation = channel.getQuarantinedInputOperation(++operationCount);
            if (operation != null)
            {
              operation.doExecute(channel);
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

    public abstract void doExecute(HTTPChannel channel);

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenChannelOperation extends ChannelOperation
  {
    private String protocolID;

    private int protocolVersion;

    public OpenChannelOperation(short channelID, String protocolID, int protocolVersion)
    {
      super(channelID, 0);
      this.protocolID = protocolID;
      this.protocolVersion = protocolVersion;
    }

    public OpenChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      super(in);
      protocolID = in.readString();
      protocolVersion = in.readInt();
    }

    @Override
    public void write(ExtendedDataOutputStream out) throws IOException
    {
      super.write(out);
      out.writeString(protocolID);
      out.writeInt(protocolVersion);
    }

    @Override
    public byte getOperation()
    {
      return OPERATION_OPEN;
    }

    @Override
    public void execute()
    {
      HTTPChannel channel = (HTTPChannel)inverseOpenChannel(getChannelID(), protocolID, protocolVersion);
      if (channel == null)
      {
        throw new ConnectorException(Messages.getString("HTTPConnector.0")); //$NON-NLS-1$
      }

      channel.increaseInputOperationCount();
      doExecute(channel);
    }

    @Override
    public void doExecute(HTTPChannel channel)
    {
      ChannelOperation operation = new OpenAckChannelOperation(getChannelID(), true);
      outputOperations.add(operation);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenAckChannelOperation extends ChannelOperation
  {
    private boolean success;

    public OpenAckChannelOperation(short channelID, boolean success)
    {
      super(channelID, 0);
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
    public void doExecute(HTTPChannel channel)
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
      super(channel.getID(), channel.getOutputOperationCount());
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
    public void doExecute(HTTPChannel channel)
    {
      channel.setInverseRemoved();
      inverseCloseChannel(channel.getID());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class BufferChannelOperation extends ChannelOperation
  {
    private IBuffer buffer;

    public BufferChannelOperation(short channelID, long operationCount, IBuffer buffer)
    {
      super(channelID, operationCount);
      this.buffer = buffer;
    }

    public BufferChannelOperation(ExtendedDataInputStream in) throws IOException
    {
      super(in);
      int length = in.readShort();
      if (TRACER.isEnabled())
      {
        TRACER.format("Receiving Buffer operation: operationID={0}, length={1}", getOperationCount(), length); //$NON-NLS-1$
      }

      buffer = getConfig().getBufferProvider().provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(getChannelID());
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
      buffer.setPosition(IBuffer.HEADER_SIZE);

      int length = buffer.getLimit() - IBuffer.HEADER_SIZE;
      out.writeShort(length);
      if (TRACER.isEnabled())
      {
        TRACER.format("Transmitting Buffer operation: operationID={0}, length={1}", getOperationCount(), length); //$NON-NLS-1$
      }

      for (int i = 0; i < length; i++)
      {
        byte b = buffer.get();
        out.writeByte(b);
      }

      buffer.release();
    }

    @Override
    public byte getOperation()
    {
      return OPERATION_BUFFER;
    }

    @Override
    public void doExecute(HTTPChannel channel)
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
