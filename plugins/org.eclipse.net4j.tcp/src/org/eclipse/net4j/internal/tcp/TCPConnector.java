/*
 * Copyright (c) 2007-2012, 2015, 2016, 2019-2021, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 *    Maxime Porhel (Obeo) - Re-throw CloseChannelException in handleRead to avoid infinite loop in SSLConnector::handleRead
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.internal.tcp.messages.Messages;
import org.eclipse.net4j.tcp.ITCPActiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ITCPNegotiationContext;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.security.NegotiationException;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.SchedulingConnector;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public abstract class TCPConnector extends SchedulingConnector implements ITCPConnector, ITCPActiveSelectorListener
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TCPConnector.class);

  private SocketChannel socketChannel;

  private ITCPSelector selector;

  @ExcludeFromDump
  private SelectionKey selectionKey;

  private final Object inputBufferLock = new Object();

  private IBuffer inputBuffer;

  private boolean inputBufferClosed = true;

  private String host;

  private int port;

  public TCPConnector()
  {
  }

  @Override
  public String getHost()
  {
    return host;
  }

  public void setHost(String host)
  {
    this.host = host;
  }

  @Override
  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  @Override
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

  public SelectionKey getSelectionKey()
  {
    return selectionKey;
  }

  public void setSelectionKey(SelectionKey selectionKey)
  {
    this.selectionKey = selectionKey;
  }

  public IBuffer getInputBuffer()
  {
    synchronized (inputBufferLock)
    {
      return inputBuffer;
    }
  }

  public void setInputBuffer(IBuffer inputBuffer)
  {
    synchronized (inputBufferLock)
    {
      this.inputBuffer = inputBuffer;
    }
  }

  @Override
  public String getURL()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(getProtocolString());
    builder.append(host);
    builder.append(":");
    builder.append(port);
    return builder.toString();
  }

  public String getProtocolString()
  {
    return "tcp://";
  }

  @Override
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
    catch (Exception ex)
    {
      deferredActivate(false);
    }
  }

  @Override
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
    catch (Exception ex)
    {
      deferredActivate(false);
    }
  }

  @Override
  public void handleRead(ITCPSelector selector, SocketChannel socketChannel)
  {
    try
    {
      handleReadRethrowClosedChannelException(selector, socketChannel);
    }
    catch (ClosedChannelException ex)
    {
      // Nothing to do here, deactivateAsync() has already been called in
      // handleReadWithClosedChannelExceptionRethrow().
    }
  }

  protected void handleReadRethrowClosedChannelException(ITCPSelector selector, SocketChannel socketChannel) throws ClosedChannelException
  {
    try
    {
      IBuffer buffer;
      ByteBuffer byteBuffer;

      synchronized (inputBufferLock)
      {
        if (inputBufferClosed)
        {
          return;
        }

        buffer = inputBuffer;
        if (buffer == null)
        {
          buffer = provideBuffer();
          inputBuffer = buffer;
        }

        byteBuffer = buffer.startGetting(socketChannel);
        if (byteBuffer != null)
        {
          inputBuffer = null;
        }
      }

      if (byteBuffer != null)
      {
        boolean bufferOwned = true;

        try
        {
          short channelID = buffer.getChannelID();

          InternalChannel channel = channelID == CONTROL_CHANNEL_ID ? getInternalControlChannel() : getChannel(channelID);
          if (channel != null)
          {
            bufferOwned = false;
            channel.handleBufferFromMultiplexer(buffer);
          }
          else
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Discarding buffer from unknown channel"); //$NON-NLS-1$
            }

            bufferOwned = false;
            buffer.release();
          }
        }
        finally
        {
          if (bufferOwned)
          {
            buffer.release();
          }
        }
      }
    }
    catch (NegotiationException ex)
    {
      OM.LOG.error(ex);
      setNegotiationException(ex);
      deactivateAsync();
    }
    catch (ClosedChannelException ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Socket channel closed: " + socketChannel); //$NON-NLS-1$
      }

      deactivateAsync();
      throw ex;
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        OM.LOG.error(ex);
        deactivateAsync();
      }
    }
  }

  @Override
  public void handleWrite(ITCPSelector selector, SocketChannel socketChannel)
  {
    // TCPSelector invokes write callbacks serially on its single selector thread.
    try
    {
      InternalChannel channel = selectScheduledChannel();
      if (channel == null)
      {
        stopSendingIfIdle();
        return;
      }

      Queue<IBuffer> sendQueue = channel.getSendQueue();
      if (sendQueue == null)
      {
        completeScheduledChannel(channel, true);
        return;
      }

      boolean completed = false;
      boolean closeChannelAfterMe = false;

      synchronized (sendQueue)
      {
        if (channel.isActive() && channel.getSendQueue() == sendQueue)
        {
          IBuffer buffer = sendQueue.peek();
          if (buffer == null)
          {
            completed = true;
          }
          else
          {
            // The CCAM flag must be remembered *before* the buffer.write() call below!
            closeChannelAfterMe = buffer.isCCAM();

            if (buffer.write(socketChannel))
            {
              sendQueue.poll();
              buffer.release();
              completed = true;
            }
          }
        }
      }

      if (completed)
      {
        if (closeChannelAfterMe)
        {
          channel.close();
        }

        completeScheduledChannel(channel, true);
      }
      else
      {
        completeScheduledChannel(channel, false);
      }
    }
    catch (ClosedChannelException ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Socket channel closed: " + socketChannel); //$NON-NLS-1$
      }

      deactivateAsync();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);

      if (isActive())
      {
        deactivateAsync();
      }
    }
  }

  protected void doOrderWriteInterest(boolean on)
  {
    ITCPSelector currentSelector = selector;
    SelectionKey currentSelectionKey = selectionKey;
    if (currentSelector != null && currentSelectionKey != null && currentSelectionKey.isValid())
    {
      currentSelector.orderWriteInterest(currentSelectionKey, isClient(), on);
    }
  }

  @Override
  protected void onSendingRequired()
  {
    doOrderWriteInterest(true);
  }

  @Override
  protected void onSendingNoLongerRequired()
  {
    doOrderWriteInterest(false);
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
      throw new IllegalStateException("socketChannel == null"); //$NON-NLS-1$
    }

    if (selector == null)
    {
      throw new IllegalStateException("selector == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    synchronized (inputBufferLock)
    {
      inputBufferClosed = false;
    }

    super.doActivate();
    selector.orderRegistration(socketChannel, isClient(), this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    cleanUp();

    IOUtil.closeSilent(socketChannel);
    socketChannel = null;
    super.doDeactivate();
  }

  protected void deactivateAsync()
  {
    // Cancel the selection immediately
    cleanUp();

    // Do the rest of the deactivation asynchronously
    getConfig().getReceiveExecutor().execute(new Runnable()
    {
      @Override
      public void run()
      {
        deactivate();
      }
    });
  }

  private void cleanUp()
  {
    IBuffer buffer;

    synchronized (inputBufferLock)
    {
      inputBufferClosed = true;
      buffer = inputBuffer;
      inputBuffer = null;
    }

    if (buffer != null)
    {
      buffer.release();
    }

    if (selectionKey != null)
    {
      selectionKey.cancel();
      selectionKey = null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TCPNegotiationContext extends NegotiationContext implements ITCPNegotiationContext
  {
    private IBuffer buffer;

    private boolean failed;

    public TCPNegotiationContext()
    {
    }

    @Override
    public TCPConnector getConnector()
    {
      return TCPConnector.this;
    }

    @Override
    public void setUserID(String userID)
    {
      TCPConnector.this.setUserID(userID);
    }

    @Override
    public ByteBuffer getBuffer()
    {
      buffer = provideBuffer();

      ByteBuffer byteBuffer = buffer.startPutting(CONTROL_CHANNEL_ID);
      byteBuffer.put(CONTROL_OPCODE_NEGOTIATION);
      return byteBuffer;
    }

    @Override
    public void transmitBuffer(ByteBuffer byteBuffer)
    {
      if (buffer.getByteBuffer() != byteBuffer)
      {
        throw new IllegalArgumentException("The passed buffer is not the last that was produced"); //$NON-NLS-1$
      }

      getControlChannel().sendBuffer(buffer);

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
        TCPConnector.this.setState(ConnectorState.CONNECTED);
      }
      else
      {
        OM.LOG.error(Messages.getString("TCPConnector.6") + TCPConnector.this); //$NON-NLS-1$
        failed = true;
      }

      super.setFinished(success);
    }
  }
}
