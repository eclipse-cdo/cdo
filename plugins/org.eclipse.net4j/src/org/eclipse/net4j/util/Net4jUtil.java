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
package org.eclipse.net4j.util;

import org.eclipse.net4j.transport.BufferPool;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.Connector.Type;
import org.eclipse.net4j.transport.tcp.TCPAcceptor;
import org.eclipse.net4j.transport.tcp.TCPConnectorDescription;
import org.eclipse.net4j.transport.tcp.TCPSelector;

import org.eclipse.internal.net4j.transport.BufferFactoryImpl;
import org.eclipse.internal.net4j.transport.BufferPoolImpl;
import org.eclipse.internal.net4j.transport.BufferUtil;
import org.eclipse.internal.net4j.transport.embedded.ClientEmbeddedConnectorImpl;
import org.eclipse.internal.net4j.transport.tcp.ClientTCPConnectorImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPAcceptorImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPConnectorDescriptionImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPSelectorImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class Net4jUtil
{
  private Net4jUtil()
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

  public static BufferPool createBufferPool(BufferProvider factory)
  {
    return new BufferPoolImpl(factory);
  }

  public static BufferPool createBufferPool(short bufferCapacity)
  {
    return createBufferPool(createBufferFactory(bufferCapacity));
  }

  public static BufferPool createBufferPool()
  {
    return createBufferPool(createBufferFactory());
  }

  public static Connector createEmbeddedConnector(BufferProvider bufferProvider)
  {
    ClientEmbeddedConnectorImpl connector = new ClientEmbeddedConnectorImpl();
    connector.setBufferProvider(bufferProvider);
    return connector;
  }

  public static TCPAcceptor createTCPAcceptor(BufferProvider bufferProvider, TCPSelector selector, String address,
      int port)
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
    return createTCPAcceptor(bufferProvider, selector, TCPAcceptor.DEFAULT_ADDRESS, TCPAcceptor.DEFAULT_PORT);
  }

  public static Connector<TCPConnectorDescription> createTCPConnector(BufferProvider bufferProvider,
      TCPSelector selector, String host, int port)
  {
    TCPConnectorDescriptionImpl description = new TCPConnectorDescriptionImpl();
    description.setHost(host);
    description.setPort(port);

    ClientTCPConnectorImpl connector = new ClientTCPConnectorImpl();
    connector.setBufferProvider(bufferProvider);
    connector.setSelector(selector);
    connector.setDescription(description);
    return connector;
  }

  public static Connector<TCPConnectorDescription> createTCPConnector(BufferProvider bufferProvider,
      TCPSelector selector, String host)
  {
    return createTCPConnector(bufferProvider, selector, host, TCPConnectorDescription.DEFAULT_PORT);
  }

  public static TCPSelector createTCPSelector()
  {
    return new TCPSelectorImpl();
  }

  public static Collection<Channel> getChannels(String protocolID, Set<Type> types)
  {
    if (types == null)
    {
      types = ProtocolFactory.SYMMETRIC;
    }

    Collection<Channel> channels = Channel.REGISTRY.values();
    Collection<Channel> result = new ArrayList(channels.size());
    for (Channel channel : channels)
    {
      if (types.contains(channel.getConnector().getType()))
      {
        if (protocolID == null || protocolID.length() == 0)
        {
          result.add(channel);
        }
        else
        {
          if (channel.getReceiveHandler() instanceof Protocol)
          {
            Protocol protocol = (Protocol)channel.getReceiveHandler();
            if (protocolID.equals(protocol.getProtocolID()))
            {
              result.add(channel);
            }
          }
        }
      }
    }

    return result;
  }

  public static Collection<Channel> getChannels(String protocolID)
  {
    return getChannels(protocolID, null);
  }

  public static Collection<Channel> getChannels(Set<Type> types)
  {
    return getChannels(null, types);
  }
}
