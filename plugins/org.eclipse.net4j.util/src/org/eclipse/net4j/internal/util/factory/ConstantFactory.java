/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.factory;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.core.runtime.IConfigurationElement;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public final class ConstantFactory extends Factory implements MarkupNames
{
  private final String bundleSymbolicName;

  private final String constantClassName;

  private final String constantName;

  private Object product;

  public ConstantFactory(IConfigurationElement configurationElement)
  {
    super(productGroup(configurationElement), type(configurationElement));
    bundleSymbolicName = configurationElement.getContributor().getName();
    constantClassName = Objects.requireNonNull(configurationElement.getAttribute(MarkupNames.CLASS));
    constantName = constantName(configurationElement);
  }

  @Override
  public Object create(String description) throws ProductCreationException
  {
    return getProduct(description);
  }

  private synchronized Object getProduct(String description) throws ProductCreationException
  {
    if (product == null)
    {
      try
      {
        Class<?> constantClass = OM.BUNDLE.loadClass(bundleSymbolicName, constantClassName);
        Field constantField = constantClass.getField(constantName);
        product = constantField.get(null);
      }
      catch (Exception ex)
      {
        throw productCreationException(description, ex);
      }
    }

    return product;
  }

  private static String productGroup(IConfigurationElement configurationElement)
  {
    return configurationElement.getAttribute(MarkupNames.PRODUCT_GROUP);
  }

  private static String type(IConfigurationElement configurationElement)
  {
    return configurationElement.getAttribute(MarkupNames.TYPE);
  }

  private static String constantName(IConfigurationElement configurationElement)
  {
    String constantName = configurationElement.getAttribute(MarkupNames.NAME);
    if (StringUtil.isEmpty(constantName))
    {
      constantName = type(configurationElement).toUpperCase();
    }

    return constantName;
  }
}
