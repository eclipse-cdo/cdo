/*
 * Copyright (c) 2007-2013, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.TransportConfigurator.AcceptorDescriptionParser;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPPassiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.spi.net4j.Acceptor;

import org.w3c.dom.Element;

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

  public static final long DEFAULT_SYNCHRONOUS_START_TIMEOUT = Worker.DEFAULT_TIMEOUT;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TCPAcceptor.class);

  private TCPSelector selector;

  @ExcludeFromDump
  private SelectionKey selectionKey;

  private boolean startSynchronously = DEFAULT_START_SYNCHRONOUSLY;

  private long synchronousStartTimeout = DEFAULT_SYNCHRONOUS_START_TIMEOUT;

  @ExcludeFromDump
  private CountDownLatch startLatch;

  private ServerSocketChannel serverSocketChannel;

  private String address = DEFAULT_ADDRESS;

  private int port = DEFAULT_PORT;

  public TCPAcceptor()
  {
  }

  @Override
  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
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

  @Override
  public void handleRegistration(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
  {
    InetSocketAddress addr = null;

    try
    {
      if (address != null)
      {
        addr = new InetSocketAddress(InetAddress.getByName(address), port);
      }

      ServerSocket socket = serverSocketChannel.socket();
      socket.setReuseAddress(true);
      socket.bind(addr);

      if (addr == null)
      {
        address = socket.getInetAddress().toString();
        if (address.startsWith("/")) //$NON-NLS-1$
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
      OM.LOG.error("Problem while binding " + addr, ex);
      deactivateAsync();
    }
    finally
    {
      if (startLatch != null)
      {
        startLatch.countDown();
      }
    }
  }

  @Override
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

        TCPConnector connector = createConnector();
        prepareConnector(connector);
        connector.setSocketChannel(socketChannel);
        connector.setSelector(selector);
        connector.activate();
      }
    }
    catch (ClosedChannelException ex)
    {
      deactivateAsync();
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        OM.LOG.error(ex);
      }

      deactivateAsync();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("TCPAcceptor[{0}:{1}]", address, port); //$NON-NLS-1$
  }

  protected TCPConnector createConnector()
  {
    return new TCPServerConnector(this);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (selector == null)
    {
      throw new IllegalStateException("selector == null"); //$NON-NLS-1$
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
        throw new IOException("Registration with selector timed out after " + synchronousStartTimeout + " millis"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (startLatch != null)
    {
      startLatch.countDown();
    }

    cancelSelectionKey();

    Exception ex = IOUtil.closeSilent(serverSocketChannel);
    if (ex != null && TRACER.isEnabled())
    {
      TRACER.trace(ex);
    }

    serverSocketChannel = null;
    super.doDeactivate();
  }

  protected void deactivateAsync()
  {
    // Cancel the selection immediately
    cancelSelectionKey();

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

  private void cancelSelectionKey()
  {
    if (selectionKey != null)
    {
      selectionKey.cancel();
      selectionKey = null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class DescriptionParserFactory extends AcceptorDescriptionParser.Factory implements AcceptorDescriptionParser
  {
    public DescriptionParserFactory()
    {
      super(TCPUtil.FACTORY_TYPE);
    }

    @Override
    public AcceptorDescriptionParser create(String description) throws ProductCreationException
    {
      return this;
    }

    @Override
    public String getAcceptorDescription(Element acceptorConfig)
    {
      String listenAddr = acceptorConfig.getAttribute("listenAddr"); //$NON-NLS-1$
      String port = acceptorConfig.getAttribute("port"); //$NON-NLS-1$
      return (listenAddr == null ? "" : listenAddr) + (StringUtil.isEmpty(port) ? "" : ":" + port); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }
}
