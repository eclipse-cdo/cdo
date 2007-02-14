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
package org.eclipse.internal.net4j.transport.container;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.transport.container.ContainerAdapterFactory;
import org.eclipse.net4j.transport.container.ContainerAdapterID;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ContainerImpl extends AbstractContainer
{
  private ExecutorService executorService;

  private BufferProvider bufferProvider;

  private IRegistry<String, ConnectorFactory> connectorFactoryRegistry;

  private IRegistry<ProtocolFactoryID, ProtocolFactory> protocolFactoryRegistry;

  private IRegistry<Integer, Connector> connectorRegistry;

  private IRegistry<ChannelID, Channel> channelRegistry;

  public ContainerImpl()
  {
  }

  public ContainerImpl(IRegistry<ContainerAdapterID, ContainerAdapterFactory> adapterFactoryRegistry)
  {
    super(adapterFactoryRegistry);
  }

  public ExecutorService getExecutorService()
  {
    return executorService;
  }

  public void setExecutorService(ExecutorService executorService)
  {
    this.executorService = executorService;
  }

  public BufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(BufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public IRegistry<String, ConnectorFactory> getConnectorFactoryRegistry()
  {
    return connectorFactoryRegistry;
  }

  public void setConnectorFactoryRegistry(IRegistry<String, ConnectorFactory> connectorFactoryRegistry)
  {
    this.connectorFactoryRegistry = connectorFactoryRegistry;
  }

  public IRegistry<ProtocolFactoryID, ProtocolFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<ProtocolFactoryID, ProtocolFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public IRegistry<Integer, Connector> getConnectorRegistry()
  {
    return connectorRegistry;
  }

  public void setConnectorRegistry(IRegistry<Integer, Connector> connectorRegistry)
  {
    this.connectorRegistry = connectorRegistry;
  }

  public IRegistry<ChannelID, Channel> getChannelRegistry()
  {
    return channelRegistry;
  }

  public void setChannelRegistry(IRegistry<ChannelID, Channel> channelRegistry)
  {
    this.channelRegistry = channelRegistry;
  }

  public Connector createConnector(String connectorFactoryID)
  {
    IRegistry<String, ConnectorFactory> registry = getConnectorFactoryRegistry();
    if (registry == null)
    {
      return null;
    }

    ConnectorFactory connectorFactory = registry.get(connectorFactoryID);
    if (connectorFactory == null)
    {
      return null;
    }

    return connectorFactory.createConnector(this);
  }

  public Protocol createProtocol(String protocolID)
  {
    // TODO Implement method ContainerUtil.createProtocol()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
