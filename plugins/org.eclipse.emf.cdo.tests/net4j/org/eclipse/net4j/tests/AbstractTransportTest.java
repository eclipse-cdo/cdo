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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTransportTest extends AbstractOMTest
{
  protected static final String HOST = "localhost";

  protected IManagedContainer container;

  private IAcceptor acceptor;

  private IConnector connector;

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

  protected boolean useJVMTransport()
  {
    return false;
  }

  protected IManagedContainer createContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    if (useJVMTransport())
    {
      JVMUtil.prepareContainer(container);
    }
    else
    {
      TCPUtil.prepareContainer(container);
    }

    return container;
  }

  protected IAcceptor getAcceptor()
  {
    if (acceptor == null)
    {
      if (useJVMTransport())
      {
        acceptor = JVMUtil.getAcceptor(container, "default");
      }
      else
      {
        acceptor = TCPUtil.getAcceptor(container, null);
      }
    }

    return acceptor;
  }

  protected IConnector getConnector()
  {
    if (connector == null)
    {
      if (useJVMTransport())
      {
        connector = JVMUtil.getConnector(container, "default");
      }
      else
      {
        connector = TCPUtil.getConnector(container, HOST);
      }
    }

    return connector;
  }

  protected void startTransport() throws Exception
  {
    if (container != null)
    {
      IAcceptor acceptor = getAcceptor();
      LifecycleUtil.activate(acceptor);

      IConnector connector = getConnector();
      LifecycleUtil.activate(connector);
    }
  }

  protected void stopTransport() throws Exception
  {
    LifecycleUtil.deactivate(connector);
    connector = null;

    LifecycleUtil.deactivate(acceptor);
    acceptor = null;
  }

  protected void restartContainer() throws Exception
  {
    msg("RESTARTING CONTAINER");
    stopTransport();
    LifecycleUtil.deactivate(container);
    container = createContainer();
    LifecycleUtil.activate(container);
    startTransport();
    msg("RESTARTING CONTAINER - FINISHED");
  }
}
