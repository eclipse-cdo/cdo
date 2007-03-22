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

import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorInjector;
import org.eclipse.net4j.tcp.ITCPConstants;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.transport.Acceptor;
import org.eclipse.internal.net4j.transport.Connector;
import org.eclipse.internal.net4j.transport.TransportContainer;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTransportTest extends AbstractOMTest
{
  protected static final String HOST = "localhost";

  protected TransportContainer container;

  private Acceptor acceptor;

  private Connector connector;

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

  protected TransportContainer createContainer()
  {
    TransportContainer container = new TransportContainer();
    container.registerFactory(new TCPSelectorFactory());
    container.registerFactory(new TCPAcceptorFactory());
    container.registerFactory(new TCPConnectorFactory());
    container.addPostProcessor(new TCPSelectorInjector());
    return container;
  }

  protected Acceptor getAcceptor()
  {
    if (acceptor == null)
    {
      acceptor = container.getAcceptor(ITCPConstants.TYPE, null);
    }

    return acceptor;
  }

  protected Connector getConnector()
  {
    if (connector == null)
    {
      connector = container.getConnector(ITCPConstants.TYPE, HOST);
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
