/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.properties;

import org.eclipse.net4j.util.registry.IRegistry;

/**
 * @author Eike Stepper
 * @since 3.15
 */
public final class PropertiesContainerUtil
{
  private PropertiesContainerUtil()
  {
  }

  public static IRegistry<String, Object> getProperties(Object object)
  {
    if (object instanceof IPropertiesContainer)
    {
      return ((IPropertiesContainer)object).properties();
    }

    return null;
  }

  public static <T> T getProperty(Object object, String key, Class<T> type)
  {
    return getProperty(getProperties(object), key, type);
  }

  public static <T> T getProperty(IRegistry<String, Object> properties, String key, Class<T> type)
  {
    if (properties != null)
    {
      Object value = properties.get(key);
      if (type.isInstance(value))
      {
        return type.cast(value);
      }
    }

    return null;
  }
}
