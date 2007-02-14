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
package org.eclipse.net4j.transport.container;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface Container
{
  public ContainerAdapter getAdapter(ContainerAdapterID adapterID);

  public ExecutorService getExecutorService();

  public BufferProvider getBufferProvider();

  public IRegistry<String, ConnectorFactory> getConnectorFactoryRegistry();

  public IRegistry<ProtocolFactoryID, ProtocolFactory> getProtocolFactoryRegistry();

  public IRegistry<Integer, Connector> getConnectorRegistry();

  public IRegistry<ChannelID, Channel> getChannelRegistry();

  public Connector createConnector(String connectorFactoryID);

  public Protocol createProtocol(String protocolID);
}
