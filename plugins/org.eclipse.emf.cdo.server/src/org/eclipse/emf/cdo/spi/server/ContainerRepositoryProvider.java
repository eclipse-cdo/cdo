/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class ContainerRepositoryProvider implements IRepositoryProvider, IManagedContainerProvider
{
  private IManagedContainer container;

  public ContainerRepositoryProvider(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public IRepository getRepository(String name)
  {
    try
    {
      return RepositoryFactory.get(container, name);
    }
    catch (Exception ex)
    {
      return null;
    }
  }
}
