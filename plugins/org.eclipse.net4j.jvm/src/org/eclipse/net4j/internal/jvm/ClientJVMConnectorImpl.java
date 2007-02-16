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

import org.eclipse.net4j.jvm.JVMAcceptor;
import org.eclipse.net4j.jvm.JVMAcceptorManager;
import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.util.lifecycle.LifecycleListener;
import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.transport.DescriptionUtil;

/**
 * @author Eike Stepper
 */
public class ClientJVMConnectorImpl extends AbstractJVMConnector
{
  private JVMAcceptorImpl acceptor;

  private LifecycleListener peerLifecycleListener = new LifecycleListener()
  {
    public void notifyLifecycleAboutToActivate(LifecycleNotifier notifier)
    {
    }

    public void notifyLifecycleActivated(LifecycleNotifier notifier)
    {
    }

    public void notifyLifecycleDeactivating(LifecycleNotifier notifier)
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
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    acceptor = JVMAcceptorManagerImpl.INSTANCE.getAcceptor(getName());
    if (acceptor == null)
    {
      throw new IllegalStateException("acceptor == null");
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    AbstractJVMConnector peer = acceptor.handleAccept(this);
    setPeer(peer);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.onDeactivate();
  }

  protected AbstractJVMConnector createServerPeer() throws Exception
  {
    ServerJVMConnectorImpl server = new ServerJVMConnectorImpl(this);
    server.setBufferProvider(getBufferProvider());
    server.setReceiveExecutor(getReceiveExecutor());
    server.addLifecycleListener(peerLifecycleListener);
    server.activate();
    return server;
  }
}
