/*
 * Copyright (c) 2020, 2021, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.internal.common.util.ResourceSetConfigurerRegistry;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Map;

/**
 * Customizable strategy to {@link #configureResourceSet(ResourceSet, Object, IManagedContainer) configure}
 * resource sets based on context objects (usually {@link CDOCommonView  views}).
 *
 * @author Eike Stepper
 * @since 4.12
 */
public interface ResourceSetConfigurer
{
  public Object configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container);

  /**
   * Creates {@link ResourceSetConfigurer resource set configurer} instances.
   *
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
   * A global registry for {@link ResourceSetConfigurer resource set configurers}.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
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
     * A resource set {@link Adapter adapter} that carries the {@link #getConfigurerResults() results} of all
     * {@link ResourceSetConfigurer resource set configurers} that configured the adapted resource set.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface ResourceSetConfiguration extends Adapter
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
