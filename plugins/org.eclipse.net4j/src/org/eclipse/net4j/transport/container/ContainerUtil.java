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
package org.eclipse.net4j.transport.container;

import org.eclipse.net4j.transport.BufferPool;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.transport.BufferFactoryImpl;
import org.eclipse.internal.net4j.transport.BufferPoolImpl;
import org.eclipse.internal.net4j.transport.BufferUtil;
import org.eclipse.internal.net4j.transport.ProtocolFactoryIDImpl;
import org.eclipse.internal.net4j.transport.container.DefaultContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class ContainerUtil
{
  private ContainerUtil()
  {
  }

  public static ProtocolFactoryID createProtocolFactoryID(ConnectorLocation connectorLocation, String protocolID)
  {
    return new ProtocolFactoryIDImpl(connectorLocation, protocolID);
  }

  public static Container createContainer()
  {
    return new DefaultContainer();
  }

  public static Container createContainer(short bufferCapacity)
  {
    return new DefaultContainer(bufferCapacity);
  }

  public static Container createContainer(IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry)
  {
    return new DefaultContainer(adapterFactoryRegistry);
  }

  public static Container createContainer(IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry,
      short bufferCapacity)
  {
    return new DefaultContainer(adapterFactoryRegistry, bufferCapacity);
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

  // public static Connector createEmbeddedConnector(BufferProvider
  // bufferProvider)
  // {
  // ClientEmbeddedConnectorImpl connector = new ClientEmbeddedConnectorImpl();
  // connector.setBufferProvider(bufferProvider);
  // return connector;
  // }
  //
  // public static TCPAcceptor createTCPAcceptor(BufferProvider bufferProvider,
  // TCPSelector selector, String address,
  // int port)
  // {
  // TCPAcceptorImpl acceptor = new TCPAcceptorImpl();
  // acceptor.setBufferProvider(bufferProvider);
  // acceptor.setSelector(selector);
  // acceptor.setListenPort(port);
  // acceptor.setListenAddr(address);
  // return acceptor;
  // }
  //
  // public static TCPAcceptor createTCPAcceptor(BufferProvider bufferProvider,
  // TCPSelector selector)
  // {
  // return createTCPAcceptor(bufferProvider, selector,
  // TCPAcceptor.DEFAULT_ADDRESS, TCPAcceptor.DEFAULT_PORT);
  // }
  //
  // public static Connector createTCPConnector(BufferProvider bufferProvider,
  // TCPSelector selector, String host, int port)
  // {
  // ClientTCPConnectorImpl connector = new ClientTCPConnectorImpl();
  // connector.setBufferProvider(bufferProvider);
  // connector.setSelector(selector);
  // connector.setDescription(new TCPConnectorDescriptionImpl(host, port));
  // return connector;
  // }
  //
  // public static Connector createTCPConnector(BufferProvider bufferProvider,
  // TCPSelector selector, String host)
  // {
  // return createTCPConnector(bufferProvider, selector, host,
  // TCPConnectorDescription.DEFAULT_PORT);
  // }
  //
  // public static TCPSelector createTCPSelector()
  // {
  // return new TCPSelectorImpl();
  // }

  public static Collection<Channel> getChannels(Container container, String protocolID, Set<ConnectorLocation> locations)
  {
    if (locations == null)
    {
      locations = ProtocolFactory.SYMMETRIC;
    }

    IRegistry<ChannelID, Channel> channelRegistry = container.getChannelRegistry();
    if (channelRegistry == null)
    {
      return null;
    }

    Collection<Channel> channels = channelRegistry.values();
    Collection<Channel> result = new ArrayList(channels.size());
    for (Channel channel : channels)
    {
      if (locations.contains(channel.getConnector().getLocation()))
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

  public static Collection<Channel> getChannels(Container container, String protocolID)
  {
    return getChannels(container, protocolID, null);
  }

  public static Collection<Channel> getChannels(Container container, Set<ConnectorLocation> locations)
  {
    return getChannels(container, null, locations);
  }

  // public static Map<Type, IRegistry<ProtocolFactoryID, ProtocolFactory>>
  // createProtocolFactoryRegistries(Set<Type> types)
  // {
  // Map<Type, IRegistry<ProtocolFactoryID, ProtocolFactory>> result = new
  // HashMap();
  // for (Type type : types)
  // {
  // result.put(type, new HashMapRegistry());
  // }
  //
  // return result;
  // }
  //
  // public static Set<Type> registerProtocolFactory(ProtocolFactory factory,
  // Map<Type, IRegistry<ProtocolFactoryID, ProtocolFactory>> registries)
  // {
  // Set<Type> result = new HashSet();
  // for (Entry<Type, IRegistry<ProtocolFactoryID, ProtocolFactory>> entry :
  // registries.entrySet())
  // {
  // Type type = entry.getKey();
  // IRegistry<ProtocolFactoryID, ProtocolFactory> registry = entry.getValue();
  // if (factory.getConnectorTypes().contains(type))
  // {
  // ProtocolFactoryID id = factory.createID(type);
  // if (!registry.containsKey(id))
  // {
  // registry.put(id, factory);
  // }
  // else
  // {
  // result.add(type);
  // }
  // }
  // }
  //
  // return result;
  // }
  //
  // public static Set<Type> deregisterProtocolFactory(ProtocolFactory factory,
  // Map<Type, IRegistry<ProtocolFactoryID, ProtocolFactory>> registries)
  // {
  // Set<Type> result = new HashSet();
  // for (Entry<Type, IRegistry<ProtocolFactoryID, ProtocolFactory>> entry :
  // registries.entrySet())
  // {
  // Type type = entry.getKey();
  // IRegistry<ProtocolFactoryID, ProtocolFactory> registry = entry.getValue();
  // if (factory.getConnectorTypes().contains(type))
  // {
  // ProtocolFactoryID id = factory.createID(type);
  // ProtocolFactory old = registry.remove(id);
  // if (old != null)
  // {
  // result.add(type);
  // }
  // }
  // }
  //
  // return result;
  // }
}
