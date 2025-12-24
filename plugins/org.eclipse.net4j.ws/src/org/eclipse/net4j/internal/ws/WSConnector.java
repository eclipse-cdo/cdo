/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.internal.ws.bundle.OM;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.security.NegotiationException;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.IWSNegotiationContext;
import org.eclipse.net4j.ws.jetty.Net4jWebSocket;

import org.eclipse.spi.net4j.Connector;
import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.InternalChannelMultiplexer.BufferMultiplexer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public abstract class WSConnector extends Connector implements IWSConnector, BufferMultiplexer
{
  public static final String ACCEPTOR_NAME_HEADER = "Net4jAcceptor"; //$NON-NLS-1$

  private Net4jWebSocket webSocket;

  public WSConnector()
  {
  }

  public Net4jWebSocket getWebSocket()
  {
    return webSocket;
  }

  public void setWebSocket(Net4jWebSocket webSocket)
  {
    this.webSocket = webSocket;
  }

  /**
   * Override to make the method publicly visible.
   */
  @Override
  public void setNegotiationException(NegotiationException negotiationException)
  {
    super.setNegotiationException(negotiationException);
  }

  /**
   * Override to make the method publicly visible.
   */
  @Override
  public void leaveConnecting()
  {
    super.leaveConnecting();
  }

  public void multiplexBuffer(InternalChannel channel, IBuffer buffer)
  {
    boolean closeChannelAfterMe = buffer.isCCAM();

    webSocket.sendBuffer(buffer);

    // The buffer is released through a WriteCallback in Net4jWebSocket.sendBytes()!!!
    // buffer.release();

    if (closeChannelAfterMe)
    {
      if (channel != null)
      {
        // TODO With Net4jWebSocket.ASYNC==true this can be before the buffer is actually written!
        channel.close();
      }
    }
  }

  @Override
  public void multiplexChannel(InternalChannel channel)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol) throws ChannelException
  {
    try
    {
      webSocket.registerChannel(channelID, timeout, protocol);
    }
    catch (IOException ex)
    {
      throw new ChannelException(ex);
    }
  }

  @Override
  protected void deregisterChannelFromPeer(InternalChannel channel) throws ChannelException
  {
    if (channel != null)
    {
      try
      {
        webSocket.deregisterChannel(channel.getID());
      }
      catch (IOException ex)
      {
        throw new ChannelException(ex);
      }
    }
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    return new WSNegotiationContext();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    webSocket.close();
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class WSNegotiationContext extends NegotiationContext implements IWSNegotiationContext
  {
    private IBuffer buffer;

    private boolean failed;

    public WSNegotiationContext()
    {
    }

    @Override
    public WSConnector getConnector()
    {
      return WSConnector.this;
    }

    @Override
    public void setUserID(String userID)
    {
      WSConnector.this.setUserID(userID);
    }

    @Override
    public ByteBuffer getBuffer()
    {
      buffer = getConfig().getBufferProvider().provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(Net4jWebSocket.CONTROL_CHANNEL_ID);
      byteBuffer.put(Net4jWebSocket.OPCODE_NEGOTIATION);
      return byteBuffer;
    }

    @Override
    public void transmitBuffer(ByteBuffer byteBuffer)
    {
      if (buffer.getByteBuffer() != byteBuffer)
      {
        throw new IllegalArgumentException("The passed buffer is not the last that was produced"); //$NON-NLS-1$
      }

      webSocket.sendBuffer(buffer);
      if (failed)
      {
        deactivate();
      }
    }

    @Override
    public void setFinished(boolean success)
    {
      if (success)
      {
        WSConnector.this.setState(ConnectorState.CONNECTED);
      }
      else
      {
        OM.LOG.error("Connector negotiation failed: " + WSConnector.this); //$NON-NLS-1$
        failed = true;
      }

      super.setFinished(success);
    }
  }
}
