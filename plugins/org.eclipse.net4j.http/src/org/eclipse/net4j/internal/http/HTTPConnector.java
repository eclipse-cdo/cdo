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
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.http.IHTTPConnector;
import org.eclipse.net4j.internal.http.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.buffer.Buffer;
import org.eclipse.internal.net4j.channel.Channel;
import org.eclipse.internal.net4j.connector.Connector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class HTTPConnector extends Connector implements IHTTPConnector
{
  public static final short NO_MORE_BUFFERS = -1;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPConnector.class);

  private String connectorID;

  private transient Queue<QueuedBuffer> outputQueue = new ConcurrentLinkedQueue<QueuedBuffer>();

  private transient long lastTraffic;

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

  public Queue<QueuedBuffer> getOutputQueue()
  {
    return outputQueue;
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
    long outputBufferCount;

    HTTPChannel httpChannel = (HTTPChannel)channel;
    synchronized (httpChannel)
    {
      Queue<IBuffer> channelQueue = httpChannel.getSendQueue();
      buffer = channelQueue.poll();
      outputBufferCount = httpChannel.getOutputBufferCount();
      httpChannel.increaseOutputBufferCount();
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Multiplexing {0} (count={1})", ((Buffer)buffer).formatContent(true), outputBufferCount);
    }

    outputQueue.add(new QueuedBuffer(buffer, outputBufferCount));
  }

  /**
   * Writes buffers from the {@link #outputQueue} to the passed stream. After each written buffer
   * {@link #writeMoreBuffers()} is asked whether to send more buffers.
   * 
   * @return <code>true</code> if more buffers are in the {@link #outputQueue}, <code>false</code> otherwise.
   */
  public boolean writeOutputBuffers(ExtendedDataOutputStream out) throws IOException
  {
    do
    {
      QueuedBuffer queuedBuffer = outputQueue.poll();
      if (queuedBuffer == null)
      {
        break;
      }

      IBuffer buffer = queuedBuffer.getBuffer();
      short channelIndex = buffer.getChannelIndex();
      out.writeShort(channelIndex);
      System.out.println("WRITING BUFFER");

      long channelCount = queuedBuffer.getChannelCount();
      out.writeLong(channelCount);

      buffer.flip();
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      byteBuffer.position(IBuffer.HEADER_SIZE);
      int length = byteBuffer.limit() - byteBuffer.position();
      out.writeShort(length);
      for (int i = 0; i < length; i++)
      {
        byte b = byteBuffer.get();
        System.out.println("Payload: " + b);
        out.writeByte(b);
      }

      buffer.release();
      markLastTraffic();
    } while (writeMoreBuffers());

    out.writeShort(NO_MORE_BUFFERS);
    return !outputQueue.isEmpty();
  }

  public void readInputBuffers(ExtendedDataInputStream in) throws IOException
  {
    short channelIndex = in.readShort();
    while (channelIndex != NO_MORE_BUFFERS)
    {
      HTTPChannel channel = (HTTPChannel)getChannel(channelIndex);
      if (channel == null)
      {
        throw new IllegalArgumentException("Invalid channelIndex: " + channelIndex);
      }

      long bufferCount = in.readLong();
      int length = in.readShort();

      IBuffer buffer = getBufferProvider().provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channelIndex);
      for (int i = 0; i < length; i++)
      {
        byte b = in.readByte();
        System.out.println("Payload: " + b);
        byteBuffer.put(b);
      }

      buffer.flip();
      System.out.println("READ BUFFER");
      handleInputBuffer(channel, bufferCount, buffer);
      markLastTraffic();
      channelIndex = in.readShort();
    }
  }

  private void handleInputBuffer(HTTPChannel channel, long bufferCount, IBuffer buffer)
  {
    synchronized (channel)
    {
      while (bufferCount < channel.getInputBufferCount())
      {
        IBuffer quarantinedBuffer = channel.getQuarantinedInputBuffer(channel.getInputBufferCount());
        if (quarantinedBuffer != null)
        {
          channel.handleBufferFromMultiplexer(buffer);
          channel.increaseInputBufferCount();
        }
        else
        {
          break;
        }
      }

      if (bufferCount == channel.getInputBufferCount())
      {
        channel.handleBufferFromMultiplexer(buffer);
        channel.increaseInputBufferCount();
      }
      else
      {
        channel.quarantineInputBuffer(bufferCount, buffer);
      }
    }
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Channel createChannelInstance()
  {
    return new HTTPChannel();
  }

  protected boolean writeMoreBuffers()
  {
    return true;
  }
}
