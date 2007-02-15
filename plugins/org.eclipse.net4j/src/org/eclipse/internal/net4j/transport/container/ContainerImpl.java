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
package org.eclipse.internal.net4j.transport.container;

import org.eclipse.net4j.transport.Acceptor;
import org.eclipse.net4j.transport.AcceptorFactory;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.transport.container.ContainerAdapterFactory;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ContainerImpl extends AbstractContainer
{
  private ExecutorService executorService;

  private BufferProvider bufferProvider;

  private IRegistry<String, AcceptorFactory> acceptorFactoryRegistry;

  private IRegistry<String, ConnectorFactory> connectorFactoryRegistry;

  private IRegistry<ProtocolFactoryID, ProtocolFactory> protocolFactoryRegistry;

  private IRegistry<String, Acceptor> acceptorRegistry;

  private IRegistry<String, Connector> connectorRegistry;

  private IRegistry<ChannelID, Channel> channelRegistry;

  public ContainerImpl(IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry)
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

  public IRegistry<String, AcceptorFactory> getAcceptorFactoryRegistry()
  {
    return acceptorFactoryRegistry;
  }

  public void setAcceptorFactoryRegistry(IRegistry<String, AcceptorFactory> acceptorFactoryRegistry)
  {
    this.acceptorFactoryRegistry = acceptorFactoryRegistry;
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

  public IRegistry<String, Acceptor> getAcceptorRegistry()
  {
    return acceptorRegistry;
  }

  public void setAcceptorRegistry(IRegistry<String, Acceptor> acceptorRegistry)
  {
    this.acceptorRegistry = acceptorRegistry;
  }

  public IRegistry<String, Connector> getConnectorRegistry()
  {
    return connectorRegistry;
  }

  public void setConnectorRegistry(IRegistry<String, Connector> connectorRegistry)
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
}
