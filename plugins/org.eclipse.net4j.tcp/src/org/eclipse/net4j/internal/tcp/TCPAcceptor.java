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

import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPPassiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Worker;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.acceptor.Acceptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class TCPAcceptor extends Acceptor implements ITCPAcceptor, ITCPPassiveSelectorListener
{
  public static final boolean DEFAULT_START_SYNCHRONOUSLY = true;

  public static final long DEFAULT_SYNCHRONOUS_START_TIMEOUT = 2 * Worker.DEFAULT_TIMEOUT;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TCPAcceptor.class);

  private TCPSelector selector;

  private SelectionKey selectionKey;

  private boolean startSynchronously = DEFAULT_START_SYNCHRONOUSLY;

  private long synchronousStartTimeout = DEFAULT_SYNCHRONOUS_START_TIMEOUT;

  private CountDownLatch startLatch;

  private ServerSocketChannel serverSocketChannel;

  private String address = DEFAULT_ADDRESS;

  private int port = DEFAULT_PORT;

  public TCPAcceptor()
  {
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public TCPSelector getSelector()
  {
    return selector;
  }

  public void setSelector(TCPSelector selector)
  {
    this.selector = selector;
  }

  public boolean isStartSynchronously()
  {
    return startSynchronously;
  }

  public void setStartSynchronously(boolean startSynchronously)
  {
    this.startSynchronously = startSynchronously;
  }

  public SelectionKey getSelectionKey()
  {
    return selectionKey;
  }

  public long getSynchronousStartTimeout()
  {
    return synchronousStartTimeout;
  }

  public void setSynchronousStartTimeout(long synchronousStartTimeout)
  {
    this.synchronousStartTimeout = synchronousStartTimeout;
  }

  public void handleRegistration(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
  {
    try
    {
      InetSocketAddress addr = null;
      if (address != null)
      {
        addr = new InetSocketAddress(InetAddress.getByName(address), port);
      }

      ServerSocket socket = serverSocketChannel.socket();
      socket.bind(addr);

      if (addr == null)
      {
        address = socket.getInetAddress().toString();
        if (address.startsWith("/"))
        {
          address = address.substring(1);
        }

        int colon = address.indexOf(':');
        if (colon != -1)
        {
          port = Integer.parseInt(address.substring(colon + 1));
          address = address.substring(0, colon);
        }
      }

      // [MACOSX] Must occur AFTER binding!
      selectionKey = serverSocketChannel.register(selector.getSocketSelector(), SelectionKey.OP_ACCEPT, this);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivate();
    }
    finally
    {
      if (startLatch != null)
      {
        startLatch.countDown();
      }
    }
  }

  public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
  {
    try
    {
      SocketChannel socketChannel = serverSocketChannel.accept();
      if (socketChannel != null)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Accepted socketChannel " + socketChannel); //$NON-NLS-1$
        }

        // socketChannel.socket().setReuseAddress(true);
        // socketChannel.socket().setKeepAlive(true);
        socketChannel.configureBlocking(false);

        TCPServerConnector connector = createConnector();
        prepareConnector(connector);
        connector.setSocketChannel(socketChannel);
        connector.setSelector(selector);
        connector.activate();
      }
    }
    catch (ClosedChannelException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        OM.LOG.error(ex);
      }

      deactivate();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("TCPAcceptor[{0}:{1}]", address, port); //$NON-NLS-1$ 
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (selector == null)
    {
      throw new IllegalStateException("selector == null");
    }

    if (startSynchronously)
    {
      startLatch = new CountDownLatch(1);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    selector.orderRegistration(serverSocketChannel, this);

    if (startLatch != null)
    {
      if (!startLatch.await(synchronousStartTimeout, TimeUnit.MILLISECONDS))
      {
        startLatch = null;
        IOUtil.closeSilent(serverSocketChannel);
        throw new IOException("Registration with selector timed out after " + synchronousStartTimeout + " millis");
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSocketChannel.close();
    super.doDeactivate();
  }

  protected TCPServerConnector createConnector()
  {
    return new TCPServerConnector(this);
  }
}
