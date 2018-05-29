/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

/**
 * @author Eike Stepper
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 */
public abstract class AbstractTransportTest extends AbstractOMTest
{
  protected static final String HOST = "localhost"; //$NON-NLS-1$

  protected IManagedContainer container;

  // SSL, the server and client need separate container in order to operate handshake.
  protected IManagedContainer separateContainer;

  private IAcceptor acceptor;

  private IConnector connector;

  protected AbstractTransportTest()
  {
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    // create container for JVM or TCP only.
    container = createContainer();
    LifecycleUtil.activate(container);

    if (!useJVMTransport() && useSSLTransport())
    {
      // the SSL need separate container between client and server
      separateContainer = createContainer();
      LifecycleUtil.activate(separateContainer);
    }
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
      if (!useJVMTransport() && useSSLTransport())
      {
        separateContainer = null;
      }
      super.doTearDown();
    }
  }

  protected boolean useJVMTransport()
  {
    return false;
  }

  protected boolean useSSLTransport()
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
      if (useSSLTransport())
      {
        SSLUtil.prepareContainer(container);
      }
      else
      {
        TCPUtil.prepareContainer(container);
      }
    }

    return container;
  }

  protected IAcceptor getAcceptor()
  {
    if (acceptor == null)
    {
      if (useJVMTransport())
      {
        acceptor = JVMUtil.getAcceptor(container, "default"); //$NON-NLS-1$
      }
      else
      {
        if (useSSLTransport())
        {
          acceptor = SSLUtil.getAcceptor(container, null);
        }
        else
        {
          acceptor = TCPUtil.getAcceptor(container, null);
        }
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
        connector = JVMUtil.getConnector(container, "default"); //$NON-NLS-1$
      }
      else
      {
        if (useSSLTransport())
        {
          // cannot use same container with the acceptor.
          connector = SSLUtil.getConnector(separateContainer, HOST);
        }
        else
        {
          connector = TCPUtil.getConnector(container, HOST);
        }
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
    connector.close();
    connector = null;

    acceptor.close();
    acceptor = null;
  }

  protected void restartContainer() throws Exception
  {
    msg("RESTARTING CONTAINER"); //$NON-NLS-1$
    stopTransport();

    LifecycleUtil.deactivate(container);
    container = createContainer();
    LifecycleUtil.activate(container);

    if (!useJVMTransport() && useSSLTransport())
    {
      LifecycleUtil.deactivate(separateContainer);
      separateContainer = createContainer();
      LifecycleUtil.activate(separateContainer);
    }
    startTransport();
    msg("RESTARTING CONTAINER - FINISHED"); //$NON-NLS-1$
  }
}
