/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * Initializes the JVM-wide {@code EPackage.Registry.INSTANCE}.
 * <p>
 * CDO invokes the initializer selected by the system property {@code org.eclipse.emf.cdo.common.packageRegistryInitializer}
 * once when the first CDO session implementation is initialized. If the property is absent, the {@code default}
 * initializer processes EMF's generated-package extension metadata. The reserved value {@code none} skips factory
 * lookup and initialization. Any other value selects one registered factory type from the global managed container.
 *
 * @author Eike Stepper
 * @since 4.29
 */
public interface EMFPackageRegistryInitializer
{
  /**
   * Initializes the global EMF package registry.
   */
  public void initializePackageRegistry();

  /**
   * Creates global EMF package registry initializers.
   *
   * @author Eike Stepper
   */
  public abstract static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    /**
     * The product group of all EMF package registry initializer factories.
     */
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.packageRegistryInitializers"; //$NON-NLS-1$

    /**
     * The special factory type "none" leads to all global EMF package registry initialization being skipped.
     */
    public static final String NONE = "none"; //$NON-NLS-1$

    /**
     * Creates a factory with the given type.
     *
     * @param type the factory type
     */
    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract EMFPackageRegistryInitializer create(String description) throws ProductCreationException;
  }
}
