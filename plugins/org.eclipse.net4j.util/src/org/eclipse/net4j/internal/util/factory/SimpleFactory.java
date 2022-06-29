/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.factory;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.ProductDescriptionAware;
import org.eclipse.net4j.util.factory.PropertiesFactory;

import org.eclipse.core.runtime.IConfigurationElement;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class SimpleFactory extends Factory
{
  public static final String ELEM = "simpleFactory"; //$NON-NLS-1$

  private static final String ATTR_PRODUCT_CLASS = "productClass";

  private static final String ATTR_SETTER_NAME = "setterName";

  private final IConfigurationElement configurationElement;

  public SimpleFactory(IConfigurationElement configurationElement)
  {
    super(getProductGroup(configurationElement), getType(configurationElement));
    this.configurationElement = configurationElement;
  }

  @Override
  public Object create(String description) throws ProductCreationException
  {
    try
    {
      Object product = configurationElement.createExecutableExtension(ATTR_PRODUCT_CLASS);
      if (product != null)
      {
        configure(product, description);
      }

      return product;
    }
    catch (Exception ex)
    {
      throw new ProductCreationException(
          "Could not create product of type '" + getType(configurationElement) + "' in group '" + getProductGroup(configurationElement) + "'", ex);
    }
  }

  private void configure(Object product, String description) throws NoSuchMethodException
  {
    String setterName = configurationElement.getAttribute(ATTR_SETTER_NAME);
    if (!StringUtil.isEmpty(setterName))
    {
      Method propertiesSetter = getSetter(product, setterName, Map.class);
      if (propertiesSetter != null)
      {
        Map<String, String> properties = PropertiesFactory.parseProperties(description);
        ReflectUtil.invokeMethod(propertiesSetter, product, properties);
        return;
      }

      Method descriptionSetter = getSetter(product, setterName, String.class);
      if (descriptionSetter != null)
      {
        ReflectUtil.invokeMethod(descriptionSetter, product, description);
        return;
      }

      throw new NoSuchMethodException(
          "Class " + product.getClass().getName() + " has no " + setterName + "(Map<String, String>) and no " + setterName + "(String) method");
    }

    if (product instanceof ProductDescriptionAware)
    {
      ((ProductDescriptionAware)product).setDescription(description);
      return;
    }
  }

  private static String getProductGroup(IConfigurationElement configurationElement)
  {
    return configurationElement.getAttribute(FactoryDescriptor.ATTR_PRODUCT_GROUP);
  }

  private static String getType(IConfigurationElement configurationElement)
  {
    return configurationElement.getAttribute(FactoryDescriptor.ATTR_TYPE);
  }

  private static Method getSetter(Object product, String name, Class<?> paramType)
  {
    return ReflectUtil.getMethodOrNull(product.getClass(), name, paramType);
  }
}
