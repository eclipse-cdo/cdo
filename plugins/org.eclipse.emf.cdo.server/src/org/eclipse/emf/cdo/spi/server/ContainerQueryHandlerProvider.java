/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * If the meaning of this type isn't clear, there really should be more of a description here...
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
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @since 3.0
   */
  @Override
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return (IQueryHandler)container.getElement(QueryHandlerFactory.PRODUCT_GROUP, info.getQueryLanguage(), null);
  }
}
