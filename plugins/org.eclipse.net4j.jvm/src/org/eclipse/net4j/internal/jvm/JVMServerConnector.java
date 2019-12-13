/*
 * Copyright (c) 2007, 2008, 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class JVMServerConnector extends JVMConnector implements IServerConnector
{
  private final JVMAcceptor acceptor;

  public JVMServerConnector(JVMAcceptor acceptor, JVMClientConnector clientPeer)
  {
    this.acceptor = acceptor;
    setPeer(clientPeer);
  }

  @Override
  public JVMAcceptor getAcceptor()
  {
    return acceptor;
  }

  @Override
  public Location getLocation()
  {
    return Location.SERVER;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.doDeactivate();
  }
}
