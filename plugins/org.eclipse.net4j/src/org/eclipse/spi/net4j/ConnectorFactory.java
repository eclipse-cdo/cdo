/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.factory.Factory;

/**
 * Creates connector implementations for the Net4j connector extension point. Each factory is registered under a connector type in the
 * connector product group.
 *
 * @author Eike Stepper
 */
public abstract class ConnectorFactory extends Factory implements IManagedContainer.ContainerAware, IManagedContainerProvider
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.connectors"; //$NON-NLS-1$

  private IManagedContainer container;

  public ConnectorFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public void setManagedContainer(IManagedContainer container)
  {
    this.container = container;
  }
}
