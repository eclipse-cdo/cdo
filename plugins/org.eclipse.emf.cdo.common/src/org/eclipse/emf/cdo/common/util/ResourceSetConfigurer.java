/*
 * Copyright (c) 2020, 2021, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.internal.common.util.ResourceSetConfigurerRegistry;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.12
 */
public interface ResourceSetConfigurer
{
  public Object configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.ResourceSetConfigurers"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract ResourceSetConfigurer create(String description) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public interface Registry
  {
    public static final Registry INSTANCE = ResourceSetConfigurerRegistry.INSTANCE;

    public ResourceSetConfigurer getConfigurer(IManagedContainer container, String type);

    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container);

    /**
     * @since 4.15
     */
    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container, String type);

    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context);

    /**
     * @author Eike Stepper
     */
    public interface ResourceSetConfiguration
    {
      public ResourceSet getResourceSet();

      public Map<String, Object> getConfigurerResults();

      public boolean isActive();

      public static ResourceSetConfiguration of(ResourceSet resourceSet)
      {
        return (ResourceSetConfiguration)EcoreUtil.getAdapter(resourceSet.eAdapters(), ResourceSetConfiguration.class);
      }
    }
  }
}
