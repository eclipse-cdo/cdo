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
import org.eclipse.internal.net4j.channel.InternalChannel;
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

  private Queue<IBuffer> outputQueue = new ConcurrentLinkedQueue<IBuffer>();

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

  public Queue<IBuffer> getOutputQueue()
  {
    return outputQueue;
  }

  public void multiplexChannel(IChannel channel)
  {
    Queue<IBuffer> channelQueue = ((InternalChannel)channel).getSendQueue();
    IBuffer buffer = channelQueue.poll();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Multiplexing " + ((Buffer)buffer).formatContent(true));
    }

    outputQueue.add(buffer);
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }
}
