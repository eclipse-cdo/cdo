/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.factory;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.factory.PluginFactoryRegistry.IFactoryDescriptor;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.factory.AnnotationFactory;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Eike Stepper
 */
public final class AnnotationFactoryDescriptor extends Factory implements IFactoryDescriptor, MarkupNames
{
  private final IConfigurationElement configurationElement;

  public AnnotationFactoryDescriptor(IConfigurationElement configurationElement)
  {
    super(createFactoryKey(configurationElement));
    this.configurationElement = configurationElement;
  }

  @Override
  public IFactory createFactory()
  {
    String bundleSymbolicName = configurationElement.getContributor().getName();
    String productClassName = configurationElement.getAttribute(PRODUCT_CLASS);

    Class<?> productClass;
    try
    {
      productClass = OM.BUNDLE.loadClass(bundleSymbolicName, productClassName);
    }
    catch (ClassNotFoundException ex)
    {
      throw WrappedException.wrap(ex);
    }

    IFactoryKey factoryKey = getKey();
    AnnotationFactory<?> factory = new AnnotationFactory<>(productClass, factoryKey);
    FactoryDescriptor.adjustFactoryType(factory, getType());
    return factory;
  }

  private static IFactoryKey createFactoryKey(IConfigurationElement element)
  {
    String name = element.getName();
    if (ANNOTATION_FACTORY.equals(name))
    {
      String productGroup = element.getAttribute(PRODUCT_GROUP);
      String type = element.getAttribute(TYPE);
      return new FactoryKey(productGroup, type);
    }

    throw new IllegalStateException("Wrong configuration element: " + name);
  }
}
