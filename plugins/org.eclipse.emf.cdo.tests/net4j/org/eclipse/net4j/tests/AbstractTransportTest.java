/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.internal.util.container.ManagedContainer;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.Acceptor;
import org.eclipse.internal.net4j.Connector;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTransportTest extends AbstractOMTest
{
  protected static final String HOST = "localhost";

  protected IManagedContainer container;

  private Acceptor acceptor;

  private Connector connector;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    container = createContainer();
    LifecycleUtil.activate(container);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    try
    {
      sleep(20);
      LifecycleUtil.deactivate(container);
    }
    finally
    {
      connector = null;
      acceptor = null;
      container = null;
      super.doTearDown();
    }
  }

  protected IManagedContainer createContainer()
  {
    IManagedContainer container = new ManagedContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    return container;
  }

  protected Acceptor getAcceptor()
  {
    if (acceptor == null)
    {
      acceptor = (Acceptor)TCPUtil.getAcceptor(container, null);
    }

    return acceptor;
  }

  protected Connector getConnector()
  {
    if (connector == null)
    {
      connector = (Connector)TCPUtil.getConnector(container, HOST);
    }

    return connector;
  }

  protected void startTransport() throws Exception
  {
    if (container != null)
    {
      Acceptor acceptor = getAcceptor();
      acceptor.activate();

      Connector connector = getConnector();
      connector.activate();
    }
  }
}
