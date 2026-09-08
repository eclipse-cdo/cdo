/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2021, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 *    Maxime Porhel (Obeo) - Re-throw CloseChannelException in handleRead to avoid infinite loop in SSLConnector::handleRead
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * SSLConnector responses to perform tasks same as TCPConnector but it attached the SSL functionality into read and
 * write method.
 *
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public abstract class SSLConnector extends TCPConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, SSLConnector.class);

  private SSLEngineManager sslEngineManager;

  public SSLConnector()
  {
  }

  @Override
  public boolean needsBufferProvider()
  {
    return false;
  }

  @Override
  public String getProtocolString()
  {
    return "ssl://";
  }

  @Override
  public void handleConnect(ITCPSelector selector, SocketChannel channel)
  {
    super.handleConnect(selector, channel);

    if (!sslEngineManager.isHandshakeComplete() && isClient())
    {
      suspendInitialHandshakeInterests();
      getConfig().getReceiveExecutor().execute(createHandShakeTask(channel));
    }
  }

  @Override
  public void handleRegistration(ITCPSelector selector, SocketChannel socketChannel)
  {
    super.handleRegistration(selector, socketChannel);

    if (!sslEngineManager.isHandshakeComplete() && isServer())
    {
      suspendInitialHandshakeInterests();
      getConfig().getReceiveExecutor().execute(createHandShakeTask(socketChannel));
    }
  }

  @Override
  public void handleRead(ITCPSelector selector, SocketChannel socketChannel)
  {
    if (!sslEngineManager.isHandshakeComplete())
    {
      suspendInitialHandshakeInterests();
      return;
    }

    try
    {
      handleReadRethrowClosedChannelException(selector, socketChannel);
    }
    catch (ClosedChannelException ex)
    {
      // deactivateAsync() has already been called in handleReadWithClosedChannelExceptionRethrow()
      // the connector will be disabled asynchronously, do not pursue nor enter the loop below.
      return;
    }

    checkRehandShake(socketChannel);

    // Handle the left data from reading multiple data at once time.
    while (sslEngineManager.getPacketRecvBuf().position() > 0)
    {
      try
      {
        handleReadRethrowClosedChannelException(selector, socketChannel);
      }
      catch (ClosedChannelException ex)
      {
        // deactivateAsync() has already been called in handleReadWithClosedChannelExceptionRethrow()
        // the connector will be disabled asynchronously, break the loop to avoid infinite iteration as the condition on
        // the while loop will continue to be true.
        return;
      }

      checkRehandShake(socketChannel);
    }
  }

  @Override
  public void handleWrite(ITCPSelector selector, SocketChannel socketChannel)
  {
    if (!sslEngineManager.isHandshakeComplete())
    {
      suspendInitialHandshakeInterests();
      return;
    }

    super.handleWrite(selector, socketChannel);
    checkRehandShake(socketChannel);
  }

  @Override
  protected void doActivate() throws Exception
  {
    try
    {
      boolean isClient = isClient();
      String host = getHost();
      int port = getPort();
      ExecutorService receiveExecutor = getConfig().getReceiveExecutor();

      sslEngineManager = new SSLEngineManager(isClient, host, port, receiveExecutor);

      IBufferProvider bufferProvider = createBufferProvider(sslEngineManager);
      getConfig().setBufferProvider(bufferProvider);
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Cannot activate the ssl engine.", ex); //$NON-NLS-1$
      }

      throw ex;
    }

    super.doActivate();
  }

  protected abstract IBufferProvider createBufferProvider(SSLEngineManager sslEngineManager);

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      sslEngineManager.close();
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Cannot deactivate the ssl engine.", ex); //$NON-NLS-1$
      }
    }
    finally
    {
      super.doDeactivate();
    }
  }

  /**
   * Toggles between OP_READ and OP_WRITE
   * <p>
   * (Having both OP_READ and OP_WRITE interests on a socketChannel is not a good idea when the channel is used for
   * TLS/SSL communications.)
   */
  @Override
  protected void doOrderWriteInterest(boolean on)
  {
    ITCPSelector selector = getSelector();
    SelectionKey selectionKey = getSelectionKey();

    if (on)
    {
      selector.orderReadInterest(selectionKey, isClient(), false);
      selector.orderWriteInterest(selectionKey, isClient(), true);
    }
    else
    {
      // Note: order is different from above!
      selector.orderWriteInterest(selectionKey, isClient(), false);
      selector.orderReadInterest(selectionKey, isClient(), true);
    }
  }

  private void checkRehandShake(SocketChannel socketChannel)
  {
    if (!isClosed())
    {
      try
      {
        sslEngineManager.checkRehandShake(socketChannel);
      }
      catch (Exception ex)
      {
        deactivateAsync();
      }
    }
  }

  private void suspendInitialHandshakeInterests()
  {
    SelectionKey selectionKey = getSelectionKey();
    ITCPSelector selector = getSelector();

    if (selectionKey != null && selectionKey.isValid() && selector != null)
    {
      boolean client = isClient();
      selector.orderReadInterest(selectionKey, client, false);
      selector.orderWriteInterest(selectionKey, client, false);
    }
  }

  private void restoreInitialHandshakeInterests()
  {
    if (!isActive() || isClosed() || !sslEngineManager.isHandshakeComplete())
    {
      return;
    }

    SelectionKey selectionKey = getSelectionKey();
    ITCPSelector selector = getSelector();

    if (selectionKey == null || !selectionKey.isValid() || selector == null)
    {
      return;
    }

    BlockingQueue<InternalChannel> writeQueue = getWriteQueue();

    synchronized (writeQueue)
    {
      boolean client = isClient();

      if (writeQueue.isEmpty())
      {
        selector.orderWriteInterest(selectionKey, client, false);
        selector.orderReadInterest(selectionKey, client, true);
      }
      else
      {
        selector.orderReadInterest(selectionKey, client, false);
        selector.orderWriteInterest(selectionKey, client, true);
      }
    }
  }

  private Runnable createHandShakeTask(SocketChannel channel)
  {
    final SocketChannel socket = channel;
    Runnable task = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          sslEngineManager.checkInitialHandshake(socket);
          restoreInitialHandshakeInterests();
        }
        catch (Exception ex)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("ssl cannot handshake.", ex); //$NON-NLS-1$
          }

          deferredActivate(false);
        }
      }
    };

    return task;
  }
}
