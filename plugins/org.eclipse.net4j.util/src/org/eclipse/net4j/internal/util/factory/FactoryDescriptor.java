/*
 * Copyright (c) 2007, 2008, 2022, 2023 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.util.factory.PluginFactoryRegistry.IFactoryDescriptor;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.FactoryCreationException;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

/**
 * A {@link IFactory factory} that delegates to an {@link IExtensionRegistry extension registry} contribution.
 * <p>
 * Example contribution:
 *
 * <pre>
 *    &lt;extension point="org.eclipse.net4j.util.factories"&gt;
 *       &lt;factory
 *             class="org.eclipse.net4j.util.concurrent.TimerLifecycle$DaemonFactory"
 *             productGroup="org.eclipse.net4j.util.timers"
 *             type="daemon"/&gt;
 *    &lt;/extension&gt;
 * </pre>
 *
 * @author Eike Stepper
 */
public final class FactoryDescriptor extends Factory implements IFactoryDescriptor, MarkupNames
{
  private final IConfigurationElement configurationElement;

  public FactoryDescriptor(IConfigurationElement configurationElement)
  {
    super(createFactoryKey(configurationElement));
    this.configurationElement = configurationElement;
  }

  @Override
  public IFactory createFactory()
  {
    try
    {
      IConfigurationElement element = configurationElement;

      String name = element.getName();
      if (TYPE.equals(name))
      {
        element = (IConfigurationElement)element.getParent();
      }

      IFactory factory = (IFactory)element.createExecutableExtension(CLASS);
      adjustFactoryType(factory, getType());
      return factory;
    }
    catch (

    CoreException ex)
    {
      throw new FactoryCreationException(ex);
    }
  }

  public static void adjustFactoryType(IFactory factory, String type)
  {
    IFactoryKey key = factory.getKey();
    if (key instanceof FactoryKey && key.getType() == null)
    {
      ((FactoryKey)key).setType(type);
    }
  }

  private static IFactoryKey createFactoryKey(IConfigurationElement element)
  {
    String name = element.getName();
    if (FACTORY.equals(name))
    {
      String productGroup = element.getAttribute(PRODUCT_GROUP);
      String type = element.getAttribute(TYPE);
      return new FactoryKey(productGroup, type);
    }

    if (TYPE.equals(name))
    {
      IConfigurationElement parent = (IConfigurationElement)element.getParent();
      String productGroup = parent.getAttribute(PRODUCT_GROUP);

      String value = element.getAttribute(VALUE);
      return new FactoryKey(productGroup, value);
    }

    throw new IllegalStateException("Wrong configuration element: " + name);
  }
}
