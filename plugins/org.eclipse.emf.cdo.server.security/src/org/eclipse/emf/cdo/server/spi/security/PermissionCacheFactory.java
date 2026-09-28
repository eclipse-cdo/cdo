/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.internal.security.DefaultPermissionCacheCreator;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * Factory for {@link PermissionCache.Creator} products.
 *
 * @since 4.13
 * @author Eike Stepper
 */
public abstract class PermissionCacheFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.permissionCacheCreators"; //$NON-NLS-1$

  public static final String PROP_TYPE = "security.permissionCache.type"; //$NON-NLS-1$

  public static final String PROP_DESCRIPTION = "security.permissionCache.description"; //$NON-NLS-1$

  public PermissionCacheFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public final PermissionCache.Creator create(String description) throws ProductCreationException
  {
    int separator = description == null ? -1 : description.indexOf(':');
    String repositoryName = separator < 0 ? description : description.substring(0, separator);
    String productDescription = separator < 0 ? null : description.substring(separator + 1);
    return create(repositoryName, productDescription);
  }

  protected abstract PermissionCache.Creator create(String repositoryName, String description) throws ProductCreationException;

  public static PermissionCache.Creator get(IManagedContainer container, String type, IRepository repository, String description)
  {
    String repositoryName = repository.getName();
    String qualifiedDescription = description == null || description.isEmpty() ? repositoryName : repositoryName + ":" + description; //$NON-NLS-1$
    return get(container, type, qualifiedDescription);
  }

  public static PermissionCache.Creator get(IManagedContainer container, String type, String qualifiedDescription)
  {
    return (PermissionCache.Creator)container.getElement(PRODUCT_GROUP, type, qualifiedDescription);
  }

  /**
   * Built-in bounded LRU cache creator.
   *
   * @author Eike Stepper
   */
  public static class Default extends PermissionCacheFactory
  {
    public static final String TYPE = "default"; //$NON-NLS-1$

    public static final String PROP_CAPACITY = "security.permissionCache.default.capacity"; //$NON-NLS-1$

    public static final int DEFAULT_CAPACITY = 100000;

    public Default()
    {
      super(TYPE);
    }

    @Override
    protected PermissionCache.Creator create(String repositoryName, String description) throws ProductCreationException
    {
      int capacity = DEFAULT_CAPACITY;

      if (description != null)
      {
        int split = description.lastIndexOf("|capacity="); //$NON-NLS-1$
        if (split >= 0)
        {
          String value = description.substring(split + "|capacity=".length()); //$NON-NLS-1$

          try
          {
            capacity = Integer.parseInt(value);
          }
          catch (NumberFormatException ex)
          {
            throw new ProductCreationException("Invalid permission cache capacity: " + value, ex);
          }
        }
      }

      if (capacity <= 0)
      {
        throw new ProductCreationException("Permission cache capacity must be positive: " + capacity);
      }

      return new DefaultPermissionCacheCreator(capacity);
    }
  }
}
