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
package org.eclipse.internal.net4j.transport.embedded;

import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.util.lifecycle.LifecycleListener;
import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class ClientEmbeddedConnectorImpl extends AbstractEmbeddedConnector implements LifecycleListener
{
  public ClientEmbeddedConnectorImpl()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

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

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    setPeer(createServerPeer());
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.onDeactivate();
  }

  protected AbstractEmbeddedConnector createServerPeer() throws Exception
  {
    ServerEmbeddedConnectorImpl server = new ServerEmbeddedConnectorImpl(this);
    server.setBufferProvider(getBufferProvider());
    server.setReceiveExecutor(getReceiveExecutor());
    server.addLifecycleListener(this);
    server.activate();
    return server;
  }
}
