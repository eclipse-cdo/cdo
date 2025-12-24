/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.config;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.InternalAcceptor;
import org.eclipse.spi.net4j.InternalConnector;

/**
 * @author Eike Stepper
 */
public abstract class AbstractConfigTest extends AbstractOMTest
{
  protected TestConfig config;

  protected IManagedContainer acceptorContainer;

  // SSL, the server and client need separate container in order to operate handshake.
  protected IManagedContainer connectorContainer;

  protected InternalAcceptor acceptor;

  protected InternalConnector connector;

  protected AbstractConfigTest()
  {
  }

  protected TestConfig.Factory getDefaultFactory()
  {
    return new TCP();
  }

  public TestConfig getConfig()
  {
    return config;
  }

  public void setConfig(TestConfig config)
  {
    this.config = config;
  }

  @Override
  public String toString()
  {
    return super.toString() + " [" + (config == null ? getDefaultFactory().getClass().getSimpleName() : config) + "]";
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    if (config == null)
    {
      config = getDefaultFactory().createConfig();
    }

    config.setUp();
    initContainers();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    try
    {
      sleep(20);
      LifecycleUtil.deactivate(connectorContainer);
      LifecycleUtil.deactivate(acceptorContainer);
    }
    finally
    {
      connector = null;
      acceptor = null;
      acceptorContainer = null;
      connectorContainer = null;

      try
      {
        config.tearDown();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }

      super.doTearDown();
    }
  }

  private void initContainers()
  {
    acceptorContainer = createContainer();
    LifecycleUtil.activate(acceptorContainer);

    if (config.needsSeparateContainers())
    {
      // the SSL need separate container between client and server
      connectorContainer = createContainer();
      LifecycleUtil.activate(connectorContainer);
    }
    else
    {
      connectorContainer = acceptorContainer;
    }
  }

  protected IManagedContainer createContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    container.registerFactory(new TestSignalProtocol.Factory());

    Net4jUtil.prepareContainer(container);
    config.prepareContainer(container);

    return container;
  }

  protected IAcceptor getAcceptor()
  {
    return getAcceptor(true);
  }

  protected IAcceptor getAcceptor(boolean activate)
  {
    if (acceptor == null)
    {
      acceptor = (InternalAcceptor)config.getAcceptor(acceptorContainer, activate);
    }

    return acceptor;
  }

  protected IConnector getConnector()
  {
    return getConnector(true);
  }

  protected IConnector getConnector(boolean activate)
  {
    if (connector == null)
    {
      connector = (InternalConnector)config.getConnector(connectorContainer, activate);
    }

    return connector;
  }

  protected IServerConnector getServerConnector()
  {
    if (acceptor != null)
    {
      IConnector[] serverConnectors = acceptor.getAcceptedConnectors();
      if (serverConnectors.length > 1)
      {
        throw new IllegalStateException("More than one server connector!");
      }

      if (connector == null)
      {
        if (serverConnectors.length != 0)
        {
          throw new IllegalStateException("More is an unexpected server connector!");
        }

        return null;
      }

      return (IServerConnector)serverConnectors[0];
    }

    return null;
  }

  protected void startTransport() throws Exception
  {
    if (acceptorContainer != null)
    {
      IAcceptor acceptor = getAcceptor();
      LifecycleUtil.activate(acceptor);

      if (connectorContainer != null)
      {
        IConnector connector = getConnector();
        LifecycleUtil.activate(connector);
      }
    }
  }

  protected void stopTransport() throws Exception
  {
    if (connector != null)
    {
      connector.close();
      connector = null;
    }

    if (acceptor != null)
    {
      acceptor.close();
      acceptor = null;
    }
  }

  protected void restartContainer() throws Exception
  {
    msg("RESTARTING CONTAINER"); //$NON-NLS-1$
    stopTransport();

    LifecycleUtil.deactivate(connectorContainer);
    LifecycleUtil.deactivate(acceptorContainer);
    initContainers();

    startTransport();
    msg("RESTARTING CONTAINER - FINISHED"); //$NON-NLS-1$
  }
}
