/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.tcp;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.tcp.TCPConnector;
import org.eclipse.net4j.transport.tcp.TCPSelector;
import org.eclipse.net4j.transport.tcp.TCPSelectorListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.transport.AbstractConnector;
import org.eclipse.internal.net4j.transport.ChannelImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTCPConnector extends AbstractConnector implements TCPConnector,
    TCPSelectorListener.Active
{
  private SocketChannel socketChannel;

  private TCPSelector selector;

  private SelectionKey selectionKey;

  private Buffer inputBuffer;

  private ControlChannelImpl controlChannel;

  public AbstractTCPConnector()
  {
    try
    {
      socketChannel = SocketChannel.open();
      socketChannel.configureBlocking(false);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * SocketChannel must already be non-blocking!
   */
  public AbstractTCPConnector(SocketChannel socketChannel)
  {
    this.socketChannel = socketChannel;
  }

  public TCPSelector getSelector()
  {
    return selector;
  }

  public void setSelector(TCPSelector selector)
  {
    this.selector = selector;
  }

  public SocketChannel getSocketChannel()
  {
    return socketChannel;
  }

  public SelectionKey getSelectionKey()
  {
    return selectionKey;
  }

  /**
   * Called by {@link ChannelImpl} each time a new buffer is available for
   * multiplexing. This or another buffer can be dequeued from the outputQueue
   * of the {@link ChannelImpl}.
   */
  public void multiplexBuffer(Channel channel)
  {
    TCPUtil.setWriteInterest(selectionKey, true);
  }

  public void handleConnect(TCPSelector selector, SocketChannel channel)
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
      TCPUtil.setConnectInterest(selectionKey, false);
      TCPUtil.setReadInterest(selectionKey, true);
      setState(State.NEGOTIATING);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      deactivate();
    }
  }

  public void handleRead(TCPSelector selector, SocketChannel socketChannel)
  {
    // TODO Is this needed?
    if (!socketChannel.isConnected())
    {
      deactivate();
      return;
    }

    try
    {
      if (inputBuffer == null)
      {
        inputBuffer = getBufferProvider().provideBuffer();
      }

      ByteBuffer byteBuffer = inputBuffer.startGetting(socketChannel);
      if (byteBuffer != null)
      {
        short channelID = inputBuffer.getChannelID();
        ChannelImpl channel = channelID == ControlChannelImpl.CONTROL_CHANNEL_ID ? controlChannel
            : getChannel(channelID);
        if (channel != null)
        {
          channel.handleBufferFromMultiplexer(inputBuffer);
        }
        else
        {
          System.out.println(toString() + ": Discarding buffer from unknown channel");
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
      ex.printStackTrace();
      deactivate();
    }
  }

  public void handleWrite(TCPSelector selector, SocketChannel socketChannel)
  {
    // TODO Is this needed?
    if (!socketChannel.isConnected())
    {
      deactivate();
      return;
    }

    try
    {
      boolean moreToWrite = false;
      for (Queue<Buffer> bufferQueue : getChannelBufferQueues())
      {
        Buffer buffer = bufferQueue.peek();
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
        TCPUtil.setWriteInterest(selectionKey, false);
      }
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      deactivate();
    }
  }

  @Override
  protected List<Queue<Buffer>> getChannelBufferQueues()
  {
    List<Queue<Buffer>> queues = super.getChannelBufferQueues();
    Queue<Buffer> controlQueue = controlChannel.getSendQueue();
    if (!controlQueue.isEmpty())
    {
      queues.add(controlQueue);
    }

    return queues;
  }

  @Override
  protected void registerChannelWithPeer(short channelID, String protocolID)
      throws ConnectorException
  {
    if (!controlChannel.registerChannel(channelID, protocolID))
    {
      throw new ConnectorException("Failed to register channel with peer");
    }
  }

  @Override
  protected void removeChannel(ChannelImpl channel)
  {
    if (isConnected())
    {
      controlChannel.deregisterChannel(channel.getChannelID());
    }

    super.removeChannel(channel);
  }

  @Override
  protected void onAccessBeforeActivate() throws Exception
  {
    super.onAccessBeforeActivate();
    if (selector == null)
    {
      selector = TCPUtil.createTCPSelector();
      LifecycleUtil.activate(selector);
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    controlChannel = new ControlChannelImpl(this);
    controlChannel.activate();

    selectionKey = selector.register(socketChannel, this);
    if (getType() == Type.SERVER)
    {
      TCPUtil.setConnectInterest(selectionKey, false);
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    Exception exception = null;

    try
    {
      selectionKey.cancel();
    }
    catch (RuntimeException ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      selectionKey = null;
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
      super.onDeactivate();
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
}
