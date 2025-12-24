/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.properties;

import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.internal.client.properties.LMModuleCheckoutProperties;

import org.eclipse.net4j.util.ui.AbstractPropertyAdapterFactory;
import org.eclipse.net4j.util.ui.DefaultActionFilter;
import org.eclipse.net4j.util.ui.DefaultPropertySource;

import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Eike Stepper
 */
public class LMPropertiesAdapterFactory extends AbstractPropertyAdapterFactory
{
  private static final IActionFilter MODULE_CHECKOUT_ACTION_FILTER = new DefaultActionFilter<>(LMModuleCheckoutProperties.INSTANCE);

  public LMPropertiesAdapterFactory()
  {
  }

  @Override
  protected IPropertySource createPropertySource(Object object)
  {
    if (object instanceof IAssemblyDescriptor)
    {
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)object;
      return new DefaultPropertySource<>(descriptor, LMModuleCheckoutProperties.INSTANCE);
    }

    return null;
  }

  @Override
  protected IActionFilter createActionFilter(Object object)
  {
    if (object instanceof IAssemblyDescriptor)
    {
      return MODULE_CHECKOUT_ACTION_FILTER;
    }

    return super.createActionFilter(object);
  }
}
