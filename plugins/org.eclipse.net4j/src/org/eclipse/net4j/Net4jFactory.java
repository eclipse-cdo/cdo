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
package org.eclipse.net4j;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.tcp.TCPAcceptor;
import org.eclipse.net4j.transport.tcp.TCPConnector;
import org.eclipse.net4j.transport.tcp.TCPSelector;

import org.eclipse.internal.net4j.transport.BufferFactoryImpl;
import org.eclipse.internal.net4j.transport.BufferPoolImpl;
import org.eclipse.internal.net4j.transport.BufferUtil;
import org.eclipse.internal.net4j.transport.tcp.ClientTCPConnectorImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPAcceptorImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPSelectorImpl;

/**
 * @author Eike Stepper
 */
public final class Net4jFactory
{
  private Net4jFactory()
  {
  }

  public static BufferProvider createBufferFactory(short bufferCapacity)
  {
    return new BufferFactoryImpl(bufferCapacity);
  }

  public static BufferProvider createBufferFactory()
  {
    return new BufferFactoryImpl(BufferUtil.DEFAULT_BUFFER_CAPACITY);
  }

  public static BufferProvider createBufferPool(BufferProvider factory)
  {
    return new BufferPoolImpl(factory);
  }

  public static BufferProvider createBufferPool(short bufferCapacity)
  {
    return createBufferPool(createBufferFactory(bufferCapacity));
  }

  public static BufferProvider createBufferPool()
  {
    return createBufferPool(BufferUtil.DEFAULT_BUFFER_CAPACITY);
  }

  public static TCPSelector createTCPSelector()
  {
    TCPSelectorImpl selector = new TCPSelectorImpl();
    return selector;
  }

  public static TCPAcceptor createTCPAcceptor(BufferProvider bufferProvider, TCPSelector selector,
      String address, int port)
  {
    TCPAcceptorImpl acceptor = new TCPAcceptorImpl();
    acceptor.setBufferProvider(bufferProvider);
    acceptor.setSelector(selector);
    acceptor.setListenPort(port);
    acceptor.setListenAddr(address);
    return acceptor;
  }

  public static TCPAcceptor createTCPAcceptor(BufferProvider bufferProvider, TCPSelector selector)
  {
    return createTCPAcceptor(bufferProvider, selector, TCPAcceptor.DEFAULT_ADDRESS,
        TCPAcceptor.DEFAULT_PORT);
  }

  public static TCPConnector createTCPConnector(BufferProvider bufferProvider,
      TCPSelector selector, String host, int port)
  {
    ClientTCPConnectorImpl connector = new ClientTCPConnectorImpl();
    connector.setBufferProvider(bufferProvider);
    connector.setSelector(selector);
    connector.setHost(host);
    connector.setPort(port);
    return connector;
  }

  public static TCPConnector createTCPConnector(BufferProvider bufferProvider,
      TCPSelector selector, String host)
  {
    return createTCPConnector(bufferProvider, selector, host, TCPConnector.DEFAULT_PORT);
  }
}
