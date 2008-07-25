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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.tcp.ITCPActiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationContext;

import org.eclipse.internal.net4j.connector.Connector;

import org.eclipse.spi.net4j.InternalChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Eike Stepper
 */
public abstract class TCPConnector extends Connector implements ITCPConnector, ITCPActiveSelectorListener
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TCPConnector.class);

  private SocketChannel socketChannel;

  private ITCPSelector selector;

  private SelectionKey selectionKey;

  private BlockingQueue<InternalChannel> writeQueue = new LinkedBlockingQueue<InternalChannel>();

  private IBuffer inputBuffer;

  private ControlChannel controlChannel;

  private String host;

  private int port;

  public TCPConnector()
  {
  }

  public String getHost()
  {
    return host;
  }

  void setHost(String host)
  {
    this.host = host;
  }

  public int getPort()
  {
    return port;
  }

  void setPort(int port)
  {
    this.port = port;
  }

  public ITCPSelector getSelector()
  {
    return selector;
  }

  public void setSelector(ITCPSelector selector)
  {
    this.selector = selector;
  }

  public SocketChannel getSocketChannel()
  {
    return socketChannel;
  }

  /**
   * SocketChannel must already be non-blocking!
   */
  public void setSocketChannel(SocketChannel socketChannel)
  {
    this.socketChannel = socketChannel;
  }

  public String getURL()
  {
    return "tcp://" + host + ":" + port;
  }

  public void handleRegistration(ITCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      int interest = isClient() ? SelectionKey.OP_CONNECT : SelectionKey.OP_READ;
      selectionKey = socketChannel.register(selector.getSocketSelector(), interest, this);

      if (isServer())
      {
        leaveConnecting();
      }
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivate();
    }
  }

  public void handleConnect(ITCPSelector selector, SocketChannel channel)
  {
    try
    {
      if (channel.finishConnect())
      {
        selector.orderConnectInterest(selectionKey, true, false);
        selector.orderReadInterest(selectionKey, true, true);
        leaveConnecting();
      }
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivate();
    }
  }

  public void handleRead(ITCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      if (inputBuffer == null)
      {
        inputBuffer = getBufferProvider().provideBuffer();
      }

      ByteBuffer byteBuffer = inputBuffer.startGetting(socketChannel);
      if (byteBuffer != null)
      {
        short channelIndex = inputBuffer.getChannelIndex();
        InternalChannel channel = channelIndex == ControlChannel.CONTROL_CHANNEL_INDEX ? controlChannel
            : getChannel(channelIndex);
        if (channel != null)
        {
          channel.handleBufferFromMultiplexer(inputBuffer);
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Discarding buffer from unknown channel"); //$NON-NLS-1$
          }

          inputBuffer.release();
        }

        inputBuffer = null;
      }
    }
    catch (IOException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivate();
    }
  }

  /**
   * Called by an {@link IChannel} each time a new buffer is available for multiplexing. This or another buffer can be
   * dequeued from the outputQueue of the {@link IChannel}.
   */
  public void multiplexChannel(IChannel channel)
  {
    synchronized (writeQueue)
    {
      boolean firstChannel = writeQueue.isEmpty();

      try
      {
        writeQueue.put((InternalChannel)channel);
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }

      if (firstChannel)
      {
        checkSelectionKey();
        selector.orderWriteInterest(selectionKey, isClient(), true);
      }
    }
  }

  public void handleWrite(ITCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      synchronized (writeQueue)
      {
        InternalChannel channel = writeQueue.peek();
        if (channel != null)
        {
          Queue<IBuffer> bufferQueue = channel.getSendQueue();
          if (bufferQueue != null)
          {
            IBuffer buffer = bufferQueue.peek();
            if (buffer != null)
            {
              if (buffer.write(socketChannel))
              {
                writeQueue.poll();
                bufferQueue.poll();
                buffer.release();
              }
            }
          }
        }

        if (writeQueue.isEmpty())
        {
          checkSelectionKey();
          selector.orderWriteInterest(selectionKey, isClient(), false);
        }
      }
    }
    catch (NullPointerException ignore)
    {
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivate();
    }
  }

  @Override
  protected void registerChannelWithPeer(int channelID, short channelIndex, IProtocol protocol, long timeout)
      throws ConnectorException
  {
    try
    {
      if (!controlChannel.registerChannel(channelID, channelIndex, protocol, timeout))
      {
        throw new ConnectorException("Failed to register channel with peer"); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }
  }

  @Override
  public void inverseRemoveChannel(int channelID, short channelIndex)
  {
    try
    {
      InternalChannel channel = getChannel(channelIndex);
      if (channel instanceof ControlChannel)
      {
        return;
      }

      if (channel != null)
      {
        super.removeChannel(channel);
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.warn(ex);
    }
  }

  @Override
  public boolean removeChannel(IChannel channel)
  {
    if (channel instanceof ControlChannel)
    {
      return true;
    }

    if (super.removeChannel(channel))
    {
      if (controlChannel != null && isConnected())
      {
        controlChannel.deregisterChannel(channel.getChannelID(), channel.getChannelIndex());
      }

      return true;
    }

    return false;
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    return new TCPNegotiationContext();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (socketChannel == null)
    {
      throw new IllegalStateException("socketChannel == null");
    }

    if (selector == null)
    {
      throw new IllegalStateException("selector == null");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    controlChannel = new ControlChannel(getNextChannelID(), this);
    controlChannel.activate();
    selector.orderRegistration(socketChannel, isClient(), this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    selectionKey.cancel();

    LifecycleUtil.deactivate(controlChannel);
    IOUtil.closeSilent(socketChannel);
    controlChannel = null;
    socketChannel = null;
    super.doDeactivate();
  }

  private void checkSelectionKey()
  {
    if (selectionKey == null)
    {
      throw new IllegalStateException("selectionKey == null");
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TCPNegotiationContext extends NegotiationContext
  {
    private IBuffer buffer;

    public TCPNegotiationContext()
    {
    }

    public void setUserID(String userID)
    {
      TCPConnector.this.setUserID(userID);
    }

    public ByteBuffer getBuffer()
    {
      buffer = getBufferProvider().provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(ControlChannel.CONTROL_CHANNEL_INDEX);
      byteBuffer.put(ControlChannel.OPCODE_NEGOTIATION);
      return byteBuffer;
    }

    public void transmitBuffer(ByteBuffer byteBuffer)
    {
      if (buffer.getByteBuffer() != byteBuffer)
      {
        throw new IllegalArgumentException("The passed buffer is not the last that was produced");
      }

      controlChannel.sendBuffer(buffer);
    }

    @Override
    public void setFinished(boolean success)
    {
      if (success)
      {
        TCPConnector.this.setState(ConnectorState.CONNECTED);
      }
      else
      {
        OM.LOG.error("Connector negotiation failed: " + TCPConnector.this);
        deactivate();
      }

      super.setFinished(success);
    }
  }
}
