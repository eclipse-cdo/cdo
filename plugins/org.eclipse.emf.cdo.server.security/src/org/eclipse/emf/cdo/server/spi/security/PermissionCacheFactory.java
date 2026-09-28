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
 * A managed-container factory for {@link PermissionCache.Creator} products.
 * <p>
 * The product description is qualified with the name of the repository whose {@code SecurityManager} resolves the
 * creator. This keeps creators configured for different repositories from being shared accidentally. The optional
 * description after the repository name is passed to the implementation as implementation-specific configuration.
 *
 * @since 4.13
 * @author Eike Stepper
 */
public abstract class PermissionCacheFactory extends Factory
{
  /**
   * The managed-container product group for permission-cache creator factories.
   */
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.permissionCacheCreators"; //$NON-NLS-1$

  /**
   * Repository property selecting the permission-cache creator factory type.
   */
  public static final String PROP_TYPE = "security.permissionCache.type"; //$NON-NLS-1$

  /**
   * Repository property supplying implementation-specific permission-cache creator configuration.
   */
  public static final String PROP_DESCRIPTION = "security.permissionCache.description"; //$NON-NLS-1$

  /**
   * Creates a factory for the given managed-container product type.
   *
   * @param type the product type registered in {@link #PRODUCT_GROUP}
   */
  public PermissionCacheFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  /**
   * Creates a creator from a repository-qualified product description.
   * <p>
   * The part before the first colon identifies the repository; the remaining part, if any, is passed to
   * {@link #create(String, String)} as implementation-specific configuration.
   *
   * @param description the repository name, optionally followed by a colon and implementation-specific description
   * @return a creator for the named repository
   * @throws ProductCreationException if the creator cannot be created
   */
  @Override
  public final PermissionCache.Creator create(String description) throws ProductCreationException
  {
    int separator = description == null ? -1 : description.indexOf(':');
    String repositoryName = separator < 0 ? description : description.substring(0, separator);
    String productDescription = separator < 0 ? null : description.substring(separator + 1);
    return create(repositoryName, productDescription);
  }

  /**
   * Creates a creator for the named repository with implementation-specific configuration.
   *
   * @param repositoryName the name of the repository whose {@code SecurityManager} resolves this factory
   * @param description implementation-specific configuration, or {@code null} when none was supplied
   * @return the creator for the repository
   * @throws ProductCreationException if the creator cannot be created
   */
  protected abstract PermissionCache.Creator create(String repositoryName, String description) throws ProductCreationException;

  /**
   * Resolves a creator using the repository name and an optional implementation-specific description.
   *
   * @param container the managed container that owns the factory
   * @param type the factory type in {@link #PRODUCT_GROUP}
   * @param repository the repository whose creator is being resolved
   * @param description implementation-specific configuration, or {@code null} when none was supplied
   * @return the creator returned by the selected factory
   */
  public static PermissionCache.Creator get(IManagedContainer container, String type, IRepository repository, String description)
  {
    String repositoryName = repository.getName();
    String qualifiedDescription = description == null || description.isEmpty() ? repositoryName : repositoryName + ":" + description; //$NON-NLS-1$
    return get(container, type, qualifiedDescription);
  }

  /**
   * Resolves a creator using a complete repository-qualified managed-container product description.
   *
   * @param container the managed container that owns the factory
   * @param type the factory type in {@link #PRODUCT_GROUP}
   * @param qualifiedDescription the repository name, optionally followed by implementation-specific configuration
   * @return the creator returned by the selected factory
   */
  public static PermissionCache.Creator get(IManagedContainer container, String type, String qualifiedDescription)
  {
    return (PermissionCache.Creator)container.getElement(PRODUCT_GROUP, type, qualifiedDescription);
  }

  /**
   * Built-in bounded, LRU-like permission-cache creator factory.
   * <p>
   * Its configured capacity limits the creator-wide backing store; it is not a separate capacity for every
   * repository, user, or branch. The returned logical cache generations remain isolated by their repository/user/
   * branch scope even though the backing store is shared. The default capacity is {@value #DEFAULT_CAPACITY}. A
   * configured capacity that is not a positive integer causes product creation, and therefore repository startup,
   * to fail.
   *
   * @author Eike Stepper
   */
  public static class Default extends PermissionCacheFactory
  {
    /**
     * The built-in factory type.
     */
    public static final String TYPE = "default"; //$NON-NLS-1$

    /**
     * Repository property configuring the capacity of the default creator's shared backing store.
     */
    public static final String PROP_CAPACITY = "security.permissionCache.default.capacity"; //$NON-NLS-1$

    /**
     * Default capacity of the built-in creator's shared backing store.
     */
    public static final int DEFAULT_CAPACITY = 100000;

    /**
     * Creates the built-in default permission-cache factory.
     */
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
