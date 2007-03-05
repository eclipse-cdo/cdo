/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.TCPAcceptor;
import org.eclipse.net4j.tcp.TCPSelector;
import org.eclipse.net4j.tcp.TCPSelectorListener;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.Acceptor;
import org.eclipse.internal.net4j.transport.DescriptionUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class TCPAcceptorImpl extends Acceptor implements TCPAcceptor, TCPSelectorListener.Passive
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_ACCEPTOR, TCPAcceptorImpl.class);

  private static final String DEFAULT_ADDRESS = "0.0.0.0";

  private TCPSelectorImpl selector;

  private SelectionKey selectionKey;

  private boolean startSynchronously = true;

  private CountDownLatch startLatch;

  private ServerSocketChannel serverSocketChannel;

  private String address;

  private int port;

  public TCPAcceptorImpl()
  {
  }

  public String getAddress()
  {
    return address;
  }

  public int getPort()
  {
    return port;
  }

  public TCPSelectorImpl getSelector()
  {
    return selector;
  }

  public void setSelector(TCPSelectorImpl selector)
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

  public void registered(SelectionKey selectionKey)
  {
    this.selectionKey = selectionKey;
    if (startSynchronously)
    {
      startLatch.countDown();
    }
  }

  public void handleAccept(TCPSelector selector, ServerSocketChannel serverSocketChannel)
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

        socketChannel.configureBlocking(false);
        ServerTCPConnectorImpl connector = createConnector(socketChannel);
        addConnector(connector);
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
        Net4j.LOG.error(ex);
      }

      deactivate();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("TCPAcceptor[{0}]", getDescription()); //$NON-NLS-1$ 
  }

  protected ServerTCPConnectorImpl createConnector(SocketChannel socketChannel)
  {
    String description = createConnectorDescription(socketChannel);

    ServerTCPConnectorImpl connector = new ServerTCPConnectorImpl();
    connector.setDescription(description);
    connector.setSocketChannel(socketChannel);
    connector.setReceiveExecutor(getReceiveExecutor());
    connector.setProtocolFactoryRegistry(getProtocolFactoryRegistry());
    connector.setBufferProvider(getBufferProvider());
    connector.setSelector(selector);
    return connector;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getDescription() == null)
    {
      throw new IllegalStateException("description == null"); //$NON-NLS-1$
    }
    else
    {
      String[] elements = DescriptionUtil.getElements(getDescription());
      address = elements[1];
      if (address.length() == 0)
      {
        address = DEFAULT_ADDRESS;
      }

      port = Integer.parseInt(elements[2]);
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
    InetSocketAddress addr = null;
    if (address != null)
    {
      addr = new InetSocketAddress(InetAddress.getByName(address), port);
    }

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    ServerSocket socket = serverSocketChannel.socket();
    socket.bind(addr);

    if (address == null)
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

    if (startSynchronously)
    {
      startLatch = new CountDownLatch(1);
    }

    selector.registerAsync(serverSocketChannel, this);
    if (startSynchronously)
    {
      startLatch.await();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSocketChannel.close();
    super.doDeactivate();
  }

  private String createConnectorDescription(SocketChannel socketChannel)
  {
    SocketAddress addr = socketChannel.socket().getRemoteSocketAddress();
    String host = addr.toString();
    if (host.startsWith("/"))
    {
      host = host.substring(1);
    }

    int port = 0;
    int colon = host.indexOf(':');
    if (colon != -1)
    {
      port = Integer.parseInt(host.substring(colon + 1));
      host = host.substring(0, colon);
    }

    return TCPUtil.createConnectorDescription(host, port);
  }
}
