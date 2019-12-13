/*
 * Copyright (c) 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public abstract class AbstractPropertyAdapterFactory implements IAdapterFactory
{
  private static final Class<IPropertySourceProvider> CLASS_IPROPERTYSOURCEPROVIDER = IPropertySourceProvider.class;

  private static final Class<IActionFilter> CLASS_IACTIONFILTER = IActionFilter.class;

  private static final Class<?>[] CLASSES = { CLASS_IPROPERTYSOURCEPROVIDER, CLASS_IACTIONFILTER };

  public AbstractPropertyAdapterFactory()
  {
  }

  @Override
  public Class<?>[] getAdapterList()
  {
    return CLASSES;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType)
  {
    if (adapterType == CLASS_IPROPERTYSOURCEPROVIDER)
    {
      final IPropertySource propertySource = createPropertySource(adaptableObject);
      if (propertySource != null)
      {
        return (T)new IPropertySourceProvider()
        {
          @Override
          public IPropertySource getPropertySource(Object object)
          {
            return propertySource;
          }
        };
      }
    }

    if (adapterType == CLASS_IACTIONFILTER)
    {
      return (T)createActionFilter(adaptableObject);
    }

    return null;
  }

  protected abstract IPropertySource createPropertySource(Object object);

  /**
   * @since 3.4
   */
  protected IActionFilter createActionFilter(Object object)
  {
    return null;
  }
}
