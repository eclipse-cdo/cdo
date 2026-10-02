/*
 * Copyright (c) 2009-2012, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;

/**
 * Resolves repository query handlers through the managed container, which holds the registered language-specific
 * {@link QueryHandlerFactory} extensions.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class ContainerQueryHandlerProvider implements IQueryHandlerProvider, IManagedContainerProvider
{
  private IManagedContainer container;

  public ContainerQueryHandlerProvider(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IManagedContainer getContainer()
  {
    return container;
  }

  /**
   * Selects the handler factory whose type matches the query language and asks the container for its handler instance.
   *
   * @since 3.0
   */
  @Override
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return (IQueryHandler)container.getElement(QueryHandlerFactory.PRODUCT_GROUP, info.getQueryLanguage(), null);
  }
}
