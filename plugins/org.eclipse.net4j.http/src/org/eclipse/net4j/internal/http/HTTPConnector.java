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
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.buffer.Buffer;
import org.eclipse.internal.net4j.channel.Channel;
import org.eclipse.internal.net4j.connector.Connector;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class HTTPConnector extends Connector implements IHTTPConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPConnector.class);

  private String connectorID;

  private Queue<QueuedBuffer> outputQueue = new ConcurrentLinkedQueue<QueuedBuffer>();

  public HTTPConnector()
  {
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
}
