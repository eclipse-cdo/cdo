/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ITCPConstants;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tcp.ITCPSelectorListener;

import org.eclipse.internal.net4j.Channel;
import org.eclipse.internal.net4j.Connector;
import org.eclipse.internal.net4j.bundle.OM;

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
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, TCPConnector.class);

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
    return ITCPConstants.TYPE + "://" + host + ":" + port;
  }

  /**
   * Called by {@link Channel} each time a new buffer is available for
   * multiplexing. This or another buffer can be dequeued from the outputQueue
   * of the {@link Channel}.
   */
  @Override
  public void multiplexBuffer(IChannel channel)
  {
    checkSelectionKey();
    selector.setWriteInterest(selectionKey, true);
  }

  public void registered(SelectionKey selectionKey)
  {
    this.selectionKey = selectionKey;
    if (isServer())
    {
      selector.setConnectInterest(selectionKey, false);
    }
  }

  public void handleConnect(ITCPSelector selector, SocketChannel channel)
  {
    try
    {
      if (!channel.finishConnect())
      {
        return;
      }
    }
    catch (Exception ex)
    {
      return;
    }

    try
    {
      checkSelectionKey();
      selector.setConnectInterest(selectionKey, false);
      setState(ConnectorState.NEGOTIATING);
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
        Channel channel = channelIndex == ControlChannel.CONTROL_CHANNEL_ID ? controlChannel : getChannel(channelIndex);
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
        selector.setWriteInterest(selectionKey, false);
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
  protected void registerChannelWithPeer(short channelIndex, IProtocol protocol) throws ConnectorException
  {
    try
    {
      if (!controlChannel.registerChannel(channelIndex, protocol))
      {
        throw new ConnectorException("Failed to register channel with peer"); //$NON-NLS-1$
      }
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
  protected boolean removeChannel(Channel channel, boolean actively)
  {
    if (super.removeChannel(channel, actively))
    {
      if (isConnected() && actively)
      {
        controlChannel.deregisterChannel(channel.getChannelIndex());
      }

      return true;
    }
    else
    {
      return false;
    }
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
    controlChannel = new ControlChannel(this);
    controlChannel.activate();
    selector.registerAsync(socketChannel, this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    Exception exception = null;

    try
    {
      controlChannel.deactivate();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      controlChannel = null;
    }

    try
    {
      socketChannel.close();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      socketChannel = null;
    }

    try
    {
      super.doDeactivate();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }

    if (exception != null)
    {
      throw exception;
    }
  }

  private void checkSelectionKey()
  {
    if (selectionKey == null)
    {
      throw new IllegalStateException("selectionKey == null");
    }
  }
}
