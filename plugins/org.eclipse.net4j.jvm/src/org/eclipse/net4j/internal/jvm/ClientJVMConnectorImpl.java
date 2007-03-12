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
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.util.lifecycle.LifecycleEventAdapter;

/**
 * @author Eike Stepper
 */
public class ClientJVMConnectorImpl extends AbstractJVMConnector
{
  private JVMAcceptorImpl acceptor;

  private IListener peerLifecycleListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onAboutToDeactivate(ILifecycle lifecycle)
    {
      setPeer(null);
      deactivate();
    }
  };

  public ClientJVMConnectorImpl()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  public JVMAcceptorImpl getAcceptor()
  {
    return acceptor;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    acceptor = JVMAcceptorManagerImpl.INSTANCE.getAcceptor(getName());
    if (acceptor == null)
    {
      throw new IllegalStateException("acceptor == null");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    AbstractJVMConnector peer = acceptor.handleAccept(this);
    setPeer(peer);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.doDeactivate();
  }

  protected AbstractJVMConnector createServerPeer() throws Exception
  {
    ServerJVMConnectorImpl server = new ServerJVMConnectorImpl(this);
    server.setBufferProvider(getBufferProvider());
    server.setReceiveExecutor(getReceiveExecutor());
    server.addListener(peerLifecycleListener);
    server.activate();
    return server;
  }
}
