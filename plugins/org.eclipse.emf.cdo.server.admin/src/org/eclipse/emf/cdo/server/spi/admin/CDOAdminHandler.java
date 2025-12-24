/*
 * Copyright (c) 2012, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.spi.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Map;

/**
 * Handles requests from server-side {@link CDOAdmin} instances to {@link #createRepository(String, Map) create} or
 * {@link #deleteRepository(IRepository) delete} repositories.
 * <p>
 * A handler can be contributed by {@link IManagedContainer#registerFactory(org.eclipse.net4j.util.factory.IFactory) registering}
 * a {@link CDOAdminHandler.Factory factory} with the {@link IManagedContainer managed container}.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface CDOAdminHandler
{
  public String getType();

  public IRepository createRepository(String name, Map<String, Object> properties);

  public void deleteRepository(IRepository delegate);

  /**
   * Creates {@link CDOAdminHandler} instances.
   *
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.admin.adminHandlers"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract CDOAdminHandler create(String description) throws ProductCreationException;
  }
}
