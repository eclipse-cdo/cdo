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
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.internal.jvm.bundle.OM;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.connector.Connector;

import org.eclipse.spi.net4j.InternalChannel;

import java.util.Queue;

/**
 * TODO Remove peer channels
 * 
 * @author Eike Stepper
 */
public abstract class JVMConnector extends Connector implements IJVMConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, JVMConnector.class);

  private JVMConnector peer;

  private String name;

  public JVMConnector()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public JVMConnector getPeer()
  {
    return peer;
  }

  public void setPeer(JVMConnector peer)
  {
    this.peer = peer;
  }

  public String getURL()
  {
    return "jvm://" + name;
  }

  public ConnectorLocation getLocation()
  {
    return null;
  }

  @Override
  public void setState(ConnectorState newState) throws ConnectorException
  {
    super.setState(newState);
  }

  @Override
  public boolean waitForConnection(long timeout) throws ConnectorException
  {
    return super.waitForConnection(timeout);
  }

  public void multiplexChannel(IChannel localChannel)
  {
    short channelIndex = localChannel.getChannelIndex();
    InternalChannel peerChannel = peer.getChannel(channelIndex);
    if (peerChannel == null)
    {
      throw new IllegalStateException("peerChannel == null"); //$NON-NLS-1$
    }

    Queue<IBuffer> localQueue = ((InternalChannel)localChannel).getSendQueue();
    IBuffer buffer = localQueue.poll();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Multiplexing " + buffer.formatContent(true));
    }

    buffer.flip();
    peerChannel.handleBufferFromMultiplexer(buffer);
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void registerChannelWithPeer(int channelID, short channelIndex, IProtocol protocol, long timeoutIgnored)
      throws ConnectorException
  {
    try
    {
      InternalChannel channel = getPeer().createChannel(channelID, channelIndex, protocol.getType());
      if (channel == null)
      {
        throw new ConnectorException("Failed to register channel with peer");
      }

      channel.activate();
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(name, "name");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    leaveConnecting();
  }
}
