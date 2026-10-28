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
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

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

  private final AtomicBoolean handShakeTaskRunning = new AtomicBoolean();

  private SSLEngineManager sslEngineManager;

  private volatile boolean sslDeactivating;

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
      startHandshakeTask(channel, true);
    }
  }

  @Override
  public void handleRegistration(ITCPSelector selector, SocketChannel socketChannel)
  {
    super.handleRegistration(selector, socketChannel);

    if (!sslEngineManager.isHandshakeComplete() && isServer())
    {
      suspendInitialHandshakeInterests();
      startHandshakeTask(socketChannel, true);
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

    // Handle complete records left in the packet buffer without waiting for another read event.
    while (sslEngineManager.isHandshakeComplete() //
        && sslEngineManager.hasBufferedReceiveData() //
        && !sslEngineManager.readNeedsMoreNetworkData())
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

    if (sslEngineManager.hasBufferedReceiveData() && !sslEngineManager.readNeedsMoreNetworkData())
    {
      handleRead(selector, socketChannel);
    }

    if (!sslEngineManager.isHandshakeComplete())
    {
      return;
    }

    super.handleWrite(selector, socketChannel);
    checkRehandShake(socketChannel);
  }

  @Override
  protected void doActivate() throws Exception
  {
    sslDeactivating = false;

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
    sslDeactivating = true;
    suspendInitialHandshakeInterests();

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

  @Override
  protected void onSendingNoLongerRequired()
  {
    if (!sslDeactivating //
        && sslEngineManager != null //
        && sslEngineManager.hasBufferedReceiveData() //
        && !sslEngineManager.readNeedsMoreNetworkData())
    {
      super.onSendingRequired();
    }
    else
    {
      super.onSendingNoLongerRequired();
    }
  }

  private void checkRehandShake(SocketChannel socketChannel)
  {
    if (!isClosed())
    {
      try
      {
        sslEngineManager.checkRehandShake(socketChannel);

        if (!sslEngineManager.isHandshakeComplete())
        {
          suspendInitialHandshakeInterests();
          startHandshakeTask(socketChannel, false);
        }
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
    if (sslDeactivating //
        || getState() == ConnectorState.DISCONNECTED //
        || !sslEngineManager.isHandshakeComplete())
    {
      return;
    }

    SelectionKey selectionKey = getSelectionKey();
    ITCPSelector selector = getSelector();

    if (selectionKey == null || !selectionKey.isValid() || selector == null)
    {
      return;
    }

    if (sslDeactivating || getState() == ConnectorState.DISCONNECTED)
    {
      return;
    }

    boolean bufferedReceiveData = sslEngineManager.hasBufferedReceiveData() && !sslEngineManager.readNeedsMoreNetworkData();
    restoreSendingInterest(bufferedReceiveData);
  }

  private void startHandshakeTask(SocketChannel channel, boolean initial)
  {
    if (handShakeTaskRunning.compareAndSet(false, true))
    {
      try
      {
        ExecutorService receiveExecutor = getConfig().getReceiveExecutor();
        receiveExecutor.execute(createHandShakeTask(channel, initial));
      }
      catch (RejectedExecutionException ex)
      {
        handShakeTaskRunning.set(false);

        if (!sslDeactivating)
        {
          if (initial)
          {
            deferredActivate(false);
          }
          else
          {
            deactivateAsync();
          }
        }
      }
    }
  }

  private Runnable createHandShakeTask(SocketChannel channel, boolean initial)
  {
    SocketChannel socket = channel;

    Runnable task = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          if (!sslDeactivating)
          {
            sslEngineManager.checkInitialHandshake(socket);
          }
        }
        catch (Exception ex)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("ssl cannot handshake.", ex); //$NON-NLS-1$
          }

          if (initial)
          {
            deferredActivate(false);
          }
          else
          {
            deactivateAsync();
          }
        }
        finally
        {
          handShakeTaskRunning.set(false);
        }

        restoreInitialHandshakeInterests();
      }
    };

    return task;
  }
}
