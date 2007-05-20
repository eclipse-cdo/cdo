/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.factory;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

/**
 * @author Eike Stepper
 */
public class PluginFactoryRegistry extends HashMapRegistry<IFactoryKey, IFactory>
{
  public static final String NAMESPACE = Net4j.BUNDLE_ID;

  public static final String EXT_POINT = "factories";

  private IRegistryChangeListener extensionRegistryListener = new IRegistryChangeListener()
  {
    public void registryChanged(IRegistryChangeEvent event)
    {
      IExtensionDelta[] deltas = event.getExtensionDeltas(NAMESPACE, EXT_POINT);
      for (IExtensionDelta delta : deltas)
      {
        // TODO Handle ExtensionDelta
        Net4j.LOG.warn("ExtensionDelta not handled: " + delta);
      }
    }
  };

  public PluginFactoryRegistry()
  {
  }

  @Override
  public IFactory get(Object key)
  {
    IFactory factory = super.get(key);
    if (factory instanceof FactoryDescriptor)
    {
      FactoryDescriptor descriptor = (FactoryDescriptor)factory;
      factory = descriptor.createFactory();
    }

    return factory;
  }

  public void registerFactory(FactoryDescriptor factory)
  {
    put(factory.getKey(), factory);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(NAMESPACE, EXT_POINT);
    for (IConfigurationElement element : elements)
    {
      registerFactory(new FactoryDescriptor(element));
    }

    extensionRegistry.addRegistryChangeListener(extensionRegistryListener, NAMESPACE);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    extensionRegistry.removeRegistryChangeListener(extensionRegistryListener);
    clear();
    super.doDeactivate();
  }
}
