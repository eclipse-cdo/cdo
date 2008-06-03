/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class JVMClientConnector extends JVMConnector
{
  private JVMAcceptor acceptor;

  private IListener peerLifecycleListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onAboutToDeactivate(ILifecycle lifecycle)
    {
      setPeer(null);
      deactivate();
    }
  };

  public JVMClientConnector()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  public JVMAcceptor getAcceptor()
  {
    return acceptor;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    acceptor = JVMAcceptorManager.INSTANCE.getAcceptor(getName());
    if (acceptor == null)
    {
      throw new IllegalStateException("acceptor == null");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    JVMConnector peer = acceptor.handleAccept(this);
    peer.addListener(peerLifecycleListener);
    setPeer(peer);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.doDeactivate();
  }
}
