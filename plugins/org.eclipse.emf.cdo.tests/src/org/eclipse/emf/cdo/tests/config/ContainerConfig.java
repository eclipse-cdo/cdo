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

  /**
   * @author Eike Stepper
   */
  public static final class Combined extends ContainerConfig
  {
    public static final String NAME = "Combined";

    public static final Combined INSTANCE = new Combined();

    private IManagedContainer container;

    public Combined()
    {
      super(NAME);
    }

    public synchronized IManagedContainer getClientContainer()
    {
      if (container == null)
      {
        container = createContainer();
      }

      return container;
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

    private IManagedContainer clientContainer;

    private IManagedContainer serverContainer;

    public Separated()
    {
      super(NAME);
    }

    public synchronized IManagedContainer getClientContainer()
    {
      if (clientContainer == null)
      {
        clientContainer = createClientContainer();
      }

      return clientContainer;
    }

    public synchronized IManagedContainer getServerContainer()
    {
      if (serverContainer == null)
      {
        serverContainer = createServerContainer();
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
