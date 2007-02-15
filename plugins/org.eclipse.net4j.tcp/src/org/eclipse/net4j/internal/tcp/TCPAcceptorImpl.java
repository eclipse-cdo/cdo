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

import org.eclipse.net4j.tcp.TCPSelector;
import org.eclipse.net4j.tcp.TCPSelectorListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.AbstractAcceptor;
import org.eclipse.internal.net4j.transport.DescriptionUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class TCPAcceptorImpl extends AbstractAcceptor implements TCPSelectorListener.Passive
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_ACCEPTOR, TCPAcceptorImpl.class);

  private TCPSelector selector;

  private ServerSocketChannel serverSocketChannel;

  public TCPAcceptorImpl()
  {
  }

  public TCPSelector getSelector()
  {
    return selector;
  }

  public void setSelector(TCPSelector selector)
  {
    this.selector = selector;
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
    ServerTCPConnectorImpl connector = new ServerTCPConnectorImpl();
    connector.setSocketChannel(socketChannel);
    connector.setReceiveExecutor(getReceiveExecutor());
    connector.setProtocolFactoryRegistry(getProtocolFactoryRegistry());
    connector.setBufferProvider(getBufferProvider());
    connector.setSelector(selector);
    return connector;
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (getDescription() == null)
    {
      throw new IllegalStateException("getDescription() == null"); //$NON-NLS-1$
    }

    if (selector == null)
    {
      throw new IllegalStateException("selector == null");
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();

    String[] elements = DescriptionUtil.getElements(getDescription());
    String address = elements[1];
    int port = Integer.parseInt(elements[2]);

    InetAddress addr = InetAddress.getByName(address);
    InetSocketAddress sAddr = new InetSocketAddress(addr, port);

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(sAddr);

    selector.registerAsync(serverSocketChannel, this);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    serverSocketChannel.close();
    super.onDeactivate();
  }
}
