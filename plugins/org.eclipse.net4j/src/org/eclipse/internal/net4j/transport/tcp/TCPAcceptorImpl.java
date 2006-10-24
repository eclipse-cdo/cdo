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

import org.eclipse.net4j.Net4jFactory;
import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.tcp.TCPAcceptor;
import org.eclipse.net4j.transport.tcp.TCPAcceptorListener;
import org.eclipse.net4j.transport.tcp.TCPConnector;
import org.eclipse.net4j.transport.tcp.TCPSelector;
import org.eclipse.net4j.transport.tcp.TCPSelectorListener;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleListener;
import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.transport.ChannelImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class TCPAcceptorImpl extends AbstractLifecycle implements TCPAcceptor, BufferProvider,
    TCPSelectorListener.Passive, LifecycleListener
{
  private IRegistry<String, ProtocolFactory> protocolFactoryRegistry;

  private BufferProvider bufferProvider;

  private TCPSelector selector;

  private String listenAddr = DEFAULT_ADDRESS;

  private int listenPort = DEFAULT_PORT;

  private ServerSocketChannel serverSocketChannel;

  private SelectionKey selectionKey;

  private Set<TCPConnector> acceptedConnectors = new HashSet();

  /**
   * An optional executor to be used by the {@link Channel}s to process their
   * {@link ChannelImpl#receiveQueue} instead of the current thread. If not
   * <code>null</code> the calling thread of
   * {@link ChannelImpl#handleBufferFromMultiplexer(Buffer)} becomes decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  /**
   * Don't initialize lazily to circumvent synchronization!
   */
  private Queue<TCPAcceptorListener> listeners = new ConcurrentLinkedQueue();

  public TCPAcceptorImpl()
  {
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<String, ProtocolFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<String, ProtocolFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public BufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(BufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public short getBufferCapacity()
  {
    return bufferProvider.getBufferCapacity();
  }

  public Buffer provideBuffer()
  {
    return bufferProvider.provideBuffer();
  }

  public void retainBuffer(Buffer buffer)
  {
    bufferProvider.retainBuffer(buffer);
  }

  public TCPSelector getSelector()
  {
    return selector;
  }

  public void setSelector(TCPSelector selector)
  {
    this.selector = selector;
  }

  public String getAddress()
  {
    return listenAddr;
  }

  public void setListenAddr(String listenAddr)
  {
    this.listenAddr = listenAddr;
  }

  public int getPort()
  {
    return listenPort;
  }

  public void setListenPort(int listenPort)
  {
    this.listenPort = listenPort;
  }

  public TCPConnector[] getAcceptedConnectors()
  {
    ArrayList<TCPConnector> result;
    synchronized (acceptedConnectors)
    {
      result = new ArrayList<TCPConnector>(acceptedConnectors);
    }

    return result.toArray(new TCPConnector[result.size()]);
  }

  public void addListener(TCPAcceptorListener listener)
  {
    listeners.add(listener);
  }

  public void removeListener(TCPAcceptorListener listener)
  {
    listeners.remove(listener);
  }

  public void notifyLifecycleAboutToActivate(LifecycleNotifier notifier)
  {
    // Do nothing
  }

  public void notifyLifecycleActivated(LifecycleNotifier notifier)
  {
    // Do nothing
  }

  public void notifyLifecycleDeactivating(LifecycleNotifier notifier)
  {
    synchronized (acceptedConnectors)
    {
      notifier.removeLifecycleListener(this);
      acceptedConnectors.remove(notifier);
    }
  }

  public void handleAccept(TCPSelector selector, ServerSocketChannel serverSocketChannel)
  {
    try
    {
      SocketChannel socketChannel = serverSocketChannel.accept();
      if (socketChannel != null)
      {
        socketChannel.configureBlocking(false);
        addConnector(socketChannel);
      }
    }
    catch (ClosedByInterruptException ex)
    {
      deactivate();
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        ex.printStackTrace();
      }

      deactivate();
    }
  }

  @Override
  public String toString()
  {
    return "TCPAcceptor[" + "/" + listenAddr + ":" + listenPort + "]";
  }

  protected void addConnector(SocketChannel socketChannel)
  {
    try
    {
      AbstractTCPConnector connector = createConnector(socketChannel);
      connector.activate();
      connector.addLifecycleListener(this);

      synchronized (acceptedConnectors)
      {
        acceptedConnectors.add(connector);
      }

      fireConnectorAccepted(connector);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      try
      {
        socketChannel.close();
      }
      catch (IOException ioex)
      {
        ioex.printStackTrace();
      }
    }
  }

  protected AbstractTCPConnector createConnector(SocketChannel socketChannel)
  {
    return new ServerTCPConnectorImpl(socketChannel, getReceiveExecutor(),
        getProtocolFactoryRegistry(), bufferProvider, selector);
  }

  protected void fireConnectorAccepted(TCPConnector connector)
  {
    for (TCPAcceptorListener listener : listeners)
    {
      try
      {
        listener.notifyConnectorAccepted(this, connector);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null");
    }

    if (protocolFactoryRegistry == null)
    {
      System.out.println(toString() + ": (INFO) protocolFactoryRegistry == null");
    }

    if (receiveExecutor == null)
    {
      System.out.println(toString() + ": (INFO) receiveExecutor == null");
    }

    if (selector == null)
    {
      selector = Net4jFactory.createTCPSelector();
      LifecycleUtil.activate(selector);
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    InetAddress addr = InetAddress.getByName(listenAddr);
    InetSocketAddress sAddr = new InetSocketAddress(addr, listenPort);

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(sAddr);

    selectionKey = selector.register(serverSocketChannel, this);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    for (TCPConnector connector : getAcceptedConnectors())
    {
      try
      {
        LifecycleUtil.deactivate(connector);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

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
      serverSocketChannel.close();
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
      serverSocketChannel = null;
    }

    if (exception != null)
    {
      throw exception;
    }
  }
}
