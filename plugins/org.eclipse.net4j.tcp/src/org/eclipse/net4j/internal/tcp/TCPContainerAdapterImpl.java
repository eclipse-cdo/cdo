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

import org.eclipse.net4j.tcp.TCPContainerAdapter;
import org.eclipse.net4j.tcp.TCPSelector;
import org.eclipse.net4j.transport.Acceptor;
import org.eclipse.net4j.transport.AcceptorFactory;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.container.Container;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.transport.container.TransportContainerAdapter;

/**
 * @author Eike Stepper
 */
public class TCPContainerAdapterImpl extends TransportContainerAdapter implements TCPContainerAdapter
{
  private TCPSelector selector;

  public TCPContainerAdapterImpl(Container container)
  {
    super(container, TYPE);
    selector = new TCPSelectorImpl();
  }

  public TCPSelector getSelector()
  {
    return selector;
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    LifecycleUtil.activate(selector);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(selector);
    super.onDeactivate();
  }

  protected AcceptorFactory createAcceptorFactory()
  {
    return new TCPAcceptorFactoryImpl();
  }

  protected ConnectorFactory createConnectorFactory()
  {
    return new TCPConnectorFactoryImpl();
  }

  public void initAcceptor(Acceptor acceptor)
  {
    TCPAcceptorImpl tcpAcceptor = (TCPAcceptorImpl)acceptor;
    tcpAcceptor.setSelector(getSelector());
  }

  public void initConnector(Connector connector)
  {
    AbstractTCPConnector tcpConnector = (AbstractTCPConnector)connector;
    tcpConnector.setSelector(getSelector());
  }
}
