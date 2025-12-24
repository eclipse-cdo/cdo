/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.spi.server.ContainerRepositoryProvider;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocolFactory extends ServerProtocolFactory implements IManagedContainer.ContainerAware
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  private IRepositoryProvider repositoryProvider;

  public CDOServerProtocolFactory()
  {
    super(TYPE);
  }

  public CDOServerProtocolFactory(IRepositoryProvider repositoryProvider)
  {
    super(TYPE);
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public void setManagedContainer(IManagedContainer container)
  {
    if (repositoryProvider == null)
    {
      repositoryProvider = new ContainerRepositoryProvider(container);
    }
  }

  public IRepositoryProvider getRepositoryProvider()
  {
    return repositoryProvider;
  }

  @Override
  public CDOServerProtocol create(String description)
  {
    return new CDOServerProtocol(repositoryProvider);
  }

  public static CDOServerProtocol get(IManagedContainer container, String description)
  {
    return (CDOServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
