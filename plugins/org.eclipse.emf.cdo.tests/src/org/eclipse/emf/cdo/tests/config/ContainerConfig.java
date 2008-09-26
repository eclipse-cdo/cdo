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
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public abstract class ContainerConfig extends Config implements ContainerProvider
{
  public static final ContainerConfig[] CONFIGS = { Combined.INSTANCE, Separated.INSTANCE };

  public ContainerConfig(String name)
  {
    super(name);
  }

  protected IManagedContainer clientContainer;

  protected IManagedContainer serverContainer;

  public boolean hasClientContainer()
  {
    return clientContainer != null;
  }

  public boolean hasServerContainer()
  {
    return serverContainer != null;
  }

  @Override
  protected void tearDown() throws Exception
  {
    LifecycleUtil.deactivate(clientContainer);
    clientContainer = null;

    LifecycleUtil.deactivate(serverContainer);
    serverContainer = null;

    super.tearDown();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Combined extends ContainerConfig
  {
    public static final String NAME = "Combined";

    public static final Combined INSTANCE = new Combined();

    public Combined()
    {
      super(NAME);
    }

    public synchronized IManagedContainer getClientContainer()
    {
      if (clientContainer == null)
      {
        clientContainer = createContainer();
        LifecycleUtil.activate(clientContainer);
        serverContainer = clientContainer;
      }

      return clientContainer;
    }

    public IManagedContainer getServerContainer()
    {
      return getClientContainer();
    }

    private IManagedContainer createContainer()
    {
      IManagedContainer container = ContainerUtil.createContainer();
      Net4jUtil.prepareContainer(container);
      CDOUtil.prepareContainer(container);
      CDOServerUtil.prepareContainer(container);
      return container;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Separated extends ContainerConfig
  {
    public static final String NAME = "Separated";

    public static final Separated INSTANCE = new Separated();

    public Separated()
    {
      super(NAME);
    }

    public synchronized IManagedContainer getClientContainer()
    {
      if (clientContainer == null)
      {
        clientContainer = createClientContainer();
        LifecycleUtil.activate(clientContainer);
      }

      return clientContainer;
    }

    public synchronized IManagedContainer getServerContainer()
    {
      if (serverContainer == null)
      {
        serverContainer = createServerContainer();
        LifecycleUtil.activate(serverContainer);
      }

      return serverContainer;
    }

    private IManagedContainer createClientContainer()
    {
      IManagedContainer container = ContainerUtil.createContainer();
      Net4jUtil.prepareContainer(container);
      CDOUtil.prepareContainer(container);
      return container;
    }

    private IManagedContainer createServerContainer()
    {
      IManagedContainer container = ContainerUtil.createContainer();
      Net4jUtil.prepareContainer(container);
      CDOServerUtil.prepareContainer(container);
      return container;
    }
  }
}
