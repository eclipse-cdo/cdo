/*
 * Copyright (c) 2007-2012, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.internal.jvm.bundle.OM;
import org.eclipse.net4j.internal.jvm.messages.Messages;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.spi.net4j.Connector;
import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.InternalChannelMultiplexer.BufferMultiplexer;

/**
 * TODO Remove peer channels
 *
 * @author Eike Stepper
 */
public abstract class JVMConnector extends Connector implements IJVMConnector, BufferMultiplexer
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, JVMConnector.class);

  private JVMConnector peer;

  private String name;

  public JVMConnector()
  {
  }

  @Override
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

  @Override
  public String getURL()
  {
    return "jvm://" + name; //$NON-NLS-1$
  }

  @Override
  public void multiplexBuffer(InternalChannel channel, IBuffer buffer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Multiplexing " + buffer.formatContent(true)); //$NON-NLS-1$
    }

    buffer.flip();

    InternalChannel peerChannel = ((JVMChannel)channel).getPeer();
    peerChannel.handleBufferFromMultiplexer(buffer);
  }

  @Override
  public void multiplexChannel(InternalChannel localChannel)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected InternalChannel createChannel()
  {
    return new JVMChannel();
  }

  @Override
  protected void registerChannelWithPeer(short channelID, long timeoutIgnored, IProtocol<?> protocol) throws ChannelException
  {
    try
    {
      String protocolID = Net4jUtil.getProtocolID(protocol);
      int protocolVersion = Net4jUtil.getProtocolVersion(protocol);

      JVMChannel peerChannel = (JVMChannel)peer.inverseOpenChannel(channelID, protocolID, protocolVersion);
      if (peerChannel == null)
      {
        throw new ChannelException(Messages.getString("JVMConnector.2")); //$NON-NLS-1$
      }

      JVMChannel c = (JVMChannel)getChannel(channelID);
      c.setPeer(peerChannel);
      peerChannel.setPeer(c);
    }
    catch (ChannelException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ChannelException(ex);
    }
  }

  @Override
  protected void deregisterChannelFromPeer(InternalChannel channel) throws ChannelException
  {
    try
    {
      getPeer().inverseCloseChannel(channel.getID());
    }
    catch (ChannelException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ChannelException(ex);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(name, "name"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    leaveConnecting();
  }
}
