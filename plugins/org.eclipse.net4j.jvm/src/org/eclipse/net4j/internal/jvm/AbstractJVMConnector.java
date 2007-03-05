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
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IChannel;

import org.eclipse.internal.net4j.transport.Channel;
import org.eclipse.internal.net4j.transport.Connector;
import org.eclipse.internal.net4j.transport.DescriptionUtil;

import java.util.Queue;

/**
 * TODO Remove peer channels
 * 
 * @author Eike Stepper
 */
public abstract class AbstractJVMConnector extends Connector
{
  private AbstractJVMConnector peer;

  private String name;

  public AbstractJVMConnector()
  {
  }

  public String getName()
  {
    return name;
  }

  public AbstractJVMConnector getPeer()
  {
    return peer;
  }

  public void setPeer(AbstractJVMConnector peer)
  {
    this.peer = peer;
  }

  @Override
  protected void registerChannelWithPeer(short channelIndex, String protocolID) throws ConnectorException
  {
    try
    {
      Channel channel = getPeer().createChannel(channelIndex, protocolID, null);
      if (channel == null)
      {
        throw new ConnectorException("Failed to register channel with peer"); //$NON-NLS-1$
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

  public void multiplexBuffer(IChannel localChannel)
  {
    short channelIndex = localChannel.getChannelIndex();
    Channel peerChannel = peer.getChannel(channelIndex);
    if (peerChannel == null)
    {
      throw new IllegalStateException("peerChannel == null"); //$NON-NLS-1$
    }

    Queue<IBuffer> localQueue = ((Channel)localChannel).getSendQueue();
    IBuffer buffer = localQueue.poll();
    buffer.flip();
    peerChannel.handleBufferFromMultiplexer(buffer);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    name = DescriptionUtil.getElement(getDescription(), 2);
    if (name == null)
    {
      throw new IllegalStateException("name == null");
    }
  }
}
