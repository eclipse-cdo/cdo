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

import org.eclipse.net4j.transport.Acceptor;
import org.eclipse.net4j.transport.AcceptorFactory;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface Container
{
  public ExecutorService getExecutorService();

  public BufferProvider getBufferProvider();

  /*
   * ContainerAdapterFactory
   */

  public IRegistry<String, ContainerAdapterFactory> getAdapterFactoryRegistry();

  public void register(ContainerAdapterFactory factory);

  public void deregister(ContainerAdapterFactory factory);

  public ContainerAdapter getAdapter(String type);

  /*
   * AcceptorFactory
   */

  public IRegistry<String, AcceptorFactory> getAcceptorFactoryRegistry();

  public void register(AcceptorFactory factory);

  public void deregister(AcceptorFactory factory);

  /*
   * ConnectorFactory
   */

  public IRegistry<String, ConnectorFactory> getConnectorFactoryRegistry();

  public void register(ConnectorFactory factory);

  public void deregister(ConnectorFactory factory);

  /*
   * ProtocolFactory
   */

  public IRegistry<ProtocolFactoryID, ProtocolFactory> getProtocolFactoryRegistry();

  public void register(ProtocolFactory factory);

  public void deregister(ProtocolFactory factory);

  /*
   * Acceptor
   */

  public IRegistry<String, Acceptor> getAcceptorRegistry();

  public Acceptor getAcceptor(String description);

  /*
   * Connector
   */

  public IRegistry<String, Connector> getConnectorRegistry();

  public Connector getConnector(String description);

  /*
   * Channel
   */

  public IRegistry<ChannelID, Channel> getChannelRegistry();
}
