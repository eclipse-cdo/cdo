/*
 * Copyright (c) 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.checkouts;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 * @since 4.7
 * @deprecated As of 4.7.1 use org.eclipse.emf.cdo.common.util.ResourceSetConfigurer
 */
@Deprecated
public interface ResourceSetConfigurer
{
  @Deprecated
  public boolean configureResourceSet(ResourceSet resourceSet, CDOCheckout checkout);

  /**
   * @author Eike Stepper
   * @deprecated As of 4.7.1 use org.eclipse.emf.cdo.common.util.ResourceSetConfigurer.Factory
   */
  @Deprecated
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    @Deprecated
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.explorer.ResourceSetConfigurers"; //$NON-NLS-1$

    @Deprecated
    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Deprecated
    @Override
    public abstract ResourceSetConfigurer create(String description) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   * @deprecated As of 4.7.1 use org.eclipse.emf.cdo.common.util.ResourceSetConfigurer.Registry
   */
  @Deprecated
  public static final class Registry
  {
    @Deprecated
    public static final Registry INSTANCE = new Registry();

    private Registry()
    {
    }

    @Deprecated
    public boolean configureResourceSet(ResourceSet resourceSet, CDOCheckout checkout, IManagedContainer container)
    {
      boolean configured = false;

      for (String type : container.getFactoryTypes(Factory.PRODUCT_GROUP))
      {
        ResourceSetConfigurer configurer = (ResourceSetConfigurer)container.getElement(Factory.PRODUCT_GROUP, type, null);
        configured |= configurer.configureResourceSet(resourceSet, checkout);
      }

      return configured;
    }

    @Deprecated
    public boolean configureResourceSet(ResourceSet resourceSet, CDOCheckout checkout)
    {
      return configureResourceSet(resourceSet, checkout, IPluginContainer.INSTANCE);
    }
  }
}
