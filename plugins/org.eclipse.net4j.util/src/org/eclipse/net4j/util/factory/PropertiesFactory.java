/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import org.eclipse.net4j.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.8
 */
public abstract class PropertiesFactory extends Factory
{
  public static final String PROPERTY_SEPARATOR = "|";

  public static final String DEFAULT_KEY = "_";

  public PropertiesFactory(FactoryKey key)
  {
    super(key);
  }

  public PropertiesFactory(String productGroup, String type)
  {
    super(productGroup, type);
  }

  @Override
  public Object create(String description) throws ProductCreationException
  {
    Map<String, String> properties = new HashMap<>();

    if (!StringUtil.isEmpty(description))
    {
      String[] segments = description.split("\\" + PROPERTY_SEPARATOR);
      for (String segment : segments)
      {
        if (!StringUtil.isEmpty(segment))
        {
          int pos = segment.indexOf('=');
          if (pos != -1)
          {
            String key = segment.substring(0, pos).trim();
            String value = segment.substring(pos + 1).trim();
            properties.put(key, value);
          }
          else
          {
            properties.put(DEFAULT_KEY, segment);
          }
        }
      }
    }

    return create(properties);
  }

  protected abstract Object create(Map<String, String> properties) throws ProductCreationException;

  public static String createDescription(Map<String, String> properties)
  {
    StringBuilder builder = new StringBuilder();

    String defaultValue = properties.remove(DEFAULT_KEY);
    if (!StringUtil.isEmpty(defaultValue))
    {
      builder.append(defaultValue);
    }

    for (Map.Entry<String, String> entry : properties.entrySet())
    {
      if (builder.length() != 0)
      {
        builder.append(PROPERTY_SEPARATOR);
      }

      builder.append(entry.getKey());
      builder.append("=");
      builder.append(entry.getValue());
    }

    return builder.toString();
  }
}
