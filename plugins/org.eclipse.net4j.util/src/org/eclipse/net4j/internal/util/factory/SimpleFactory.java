/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.ProductDescriptionAware;
import org.eclipse.net4j.util.factory.PropertiesFactory;
import org.eclipse.net4j.util.factory.TreeFactory;

import org.eclipse.core.runtime.IConfigurationElement;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class SimpleFactory extends Factory implements MarkupNames
{
  private final IConfigurationElement configurationElement;

  public SimpleFactory(IConfigurationElement configurationElement)
  {
    super(productGroup(configurationElement), type(configurationElement));
    this.configurationElement = configurationElement;
  }

  @Override
  public Object create(String description) throws ProductCreationException
  {
    try
    {
      Object product = configurationElement.createExecutableExtension(PRODUCT_CLASS);
      if (product != null)
      {
        String setterName = configurationElement.getAttribute(SETTER_NAME);
        configure(product, description, setterName);
      }

      return product;
    }
    catch (Exception ex)
    {
      throw productCreationException(description, ex);
    }
  }

  public static void configure(Object product, String description, String setterName) throws NoSuchMethodException
  {
    if (!StringUtil.isEmpty(setterName))
    {
      Method treeSetter = setter(product, setterName, Tree.class);
      if (treeSetter != null)
      {
        Tree tree = TreeFactory.parseTree(description);
        ReflectUtil.invokeMethod(treeSetter, product, tree);
        return;
      }

      Method propertiesSetter = setter(product, setterName, Map.class);
      if (propertiesSetter != null)
      {
        Map<String, String> properties = PropertiesFactory.parseProperties(description);
        ReflectUtil.invokeMethod(propertiesSetter, product, properties);
        return;
      }

      Method descriptionSetter = setter(product, setterName, String.class);
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

  private static String productGroup(IConfigurationElement configurationElement)
  {
    return configurationElement.getAttribute(MarkupNames.PRODUCT_GROUP);
  }

  private static String type(IConfigurationElement configurationElement)
  {
    return configurationElement.getAttribute(MarkupNames.TYPE);
  }

  private static Method setter(Object product, String name, Class<?> paramType)
  {
    return ReflectUtil.getMethodOrNull(product.getClass(), name, paramType);
  }
}
