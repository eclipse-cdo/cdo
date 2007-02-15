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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.internal.tcp.TCPContainerAdapterFactoryImpl;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.transport.container.Container;
import org.eclipse.net4j.transport.container.ContainerUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.transport.AbstractAcceptor;
import org.eclipse.internal.net4j.transport.AbstractConnector;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTCPTest extends AbstractOMTest
{
  protected static final String ACCEPTOR_DESCRIPTION = TCPUtil.createAcceptorDescription();

  protected static final String CONNECTOR_DESCRIPTION = TCPUtil.createConnectorDescription("localhost");

  protected Container container;

  @SuppressWarnings("unused")
  private AbstractAcceptor acceptor;

  private AbstractConnector connector;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    container = createContainer();
    LifecycleUtil.activate(container);

    System.out.println();
    System.out.println("---------------- START ----------------");
  }

  @Override
  protected void tearDown() throws Exception
  {
    Thread.sleep(200);
    System.out.println();
    System.out.println("---------------- END ------------------");

    try
    {
      LifecycleUtil.deactivate(container);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      connector = null;
      acceptor = null;
      container = null;
      super.tearDown();
    }
  }

  protected Container createContainer()
  {
    Container container = ContainerUtil.createContainer();
    if (container != null)
    {
      container.register(new TCPContainerAdapterFactoryImpl());
    }

    return container;
  }

  protected AbstractAcceptor getAcceptor()
  {
    if (acceptor == null)
    {
      acceptor = (AbstractAcceptor)container.getAcceptor(ACCEPTOR_DESCRIPTION);
    }

    return acceptor;
  }

  protected AbstractConnector getConnector()
  {
    if (connector == null)
    {
      connector = (AbstractConnector)container.getConnector(CONNECTOR_DESCRIPTION);
    }

    return connector;
  }

  protected void startTransport()
  {
    assertTrue(getAcceptor().isActive());
    assertTrue(getConnector().isActive());
  }
}
