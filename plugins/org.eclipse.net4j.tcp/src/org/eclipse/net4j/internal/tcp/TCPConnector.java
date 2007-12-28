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

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.ConnectorState;
import org.eclipse.net4j.IBuffer;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IProtocol;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.internal.util.security.NegotiationContext;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tcp.ITCPSelectorListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.Channel;
import org.eclipse.internal.net4j.Connector;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public abstract class TCPConnector extends Connector implements ITCPConnector, ITCPSelectorListener.Active
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TCPConnector.class);

  private SocketChannel socketChannel;

  private ITCPSelector selector;

  private SelectionKey selectionKey;

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

  /**
   * Called by {@link Channel} each time a new buffer is available for multiplexing. This or another buffer can be
   * dequeued from the outputQueue of the {@link Channel}.
   */
  @Override
  public void multiplexBuffer(IChannel channel)
  {
    checkSelectionKey();
    selector.orderWriteInterest(selectionKey, isClient(), true);
  }

  public void handleRegistration(SelectionKey selectionKey)
  {
    this.selectionKey = selectionKey;
    if (isServer())
    {
      leaveConnecting();
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
        Channel channel = channelIndex == ControlChannel.CONTROL_CHANNEL_INDEX ? controlChannel
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

  public void handleWrite(ITCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      boolean moreToWrite = false;
      for (Queue<IBuffer> bufferQueue : getChannelBufferQueues())
      {
        IBuffer buffer = bufferQueue.peek();
        if (buffer != null)
        {
          if (buffer.write(socketChannel))
          {
            bufferQueue.poll();
            buffer.release();

            if (!moreToWrite)
            {
              moreToWrite = !bufferQueue.isEmpty();
            }
          }
          else
          {
            moreToWrite = true;
            break;
          }
        }
      }

      if (!moreToWrite)
      {
        checkSelectionKey();
        selector.orderWriteInterest(selectionKey, isClient(), false);
      }
    }
    catch (NullPointerException ignore)
    {
      ;
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
  protected List<Queue<IBuffer>> getChannelBufferQueues()
  {
    List<Queue<IBuffer>> queues = super.getChannelBufferQueues();
    Queue<IBuffer> controlQueue = controlChannel.getSendQueue();
    if (!controlQueue.isEmpty())
    {
      queues.add(controlQueue);
    }

    return queues;
  }

  @Override
  protected void registerChannelWithPeer(int channelID, short channelIndex, IProtocol protocol)
      throws ConnectorException
  {
    try
    {
      if (!controlChannel.registerChannel(channelID, channelIndex, protocol))
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
      Channel channel = getChannel(channelIndex);
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
  protected boolean removeChannel(Channel channel)
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

  private void leaveConnecting()
  {
    if (getNegotiator() == null)
    {
      setState(ConnectorState.CONNECTED);
    }
    else
    {
      setState(ConnectorState.NEGOTIATING);
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
