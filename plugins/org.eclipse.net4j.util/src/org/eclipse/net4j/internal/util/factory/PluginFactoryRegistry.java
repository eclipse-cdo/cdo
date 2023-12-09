/*
 * Copyright (c) 2007-2009, 2011-2013, 2015, 2016, 2019, 2021-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - bug 399641: container-aware factories
 */
package org.eclipse.net4j.internal.util.factory;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.factory.IFactoryKeyAware;
import org.eclipse.net4j.util.registry.HashMapRegistry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;

/**
 * @author Eike Stepper
 */
public class PluginFactoryRegistry extends HashMapRegistry<IFactoryKey, IFactory> implements MarkupNames
{
  public static final String NAMESPACE = OM.BUNDLE_ID;

  public static final String EXT_POINT = "factories"; //$NON-NLS-1$

  private final IManagedContainer container;

  private Object extensionRegistryListener;

  public PluginFactoryRegistry(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IFactory get(Object key)
  {
    IFactory factory = super.get(key);

    if (factory instanceof IFactoryDescriptor)
    {
      IFactoryDescriptor descriptor = (IFactoryDescriptor)factory;

      factory = descriptor.createFactory();
      if (factory != null && factory != descriptor)
      {
        if (factory instanceof IFactoryKeyAware)
        {
          IFactoryKeyAware keyAware = (IFactoryKeyAware)factory;
          keyAware.setFactoryKey(descriptor.getKey());
        }

        if (factory instanceof ContainerAware)
        {
          ContainerAware containerAware = (ContainerAware)factory;
          containerAware.setManagedContainer(container);
        }

        put(factory.getKey(), factory);
      }
    }

    return factory;
  }

  public void registerFactory(IFactory factory)
  {
    put(factory.getKey(), factory);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    try
    {
      doActivateOSGi();
    }
    catch (Throwable t)
    {
      OM.LOG.warn(t);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      doDeactivateOSGi();
    }
    catch (Throwable t)
    {
      OM.LOG.warn(t);
    }

    clear();
    super.doDeactivate();
  }

  protected final IManagedContainer getManagedContainer()
  {
    return container;
  }

  private void doActivateOSGi()
  {
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    if (extensionRegistry == null)
    {
      return;
    }

    for (IConfigurationElement element : extensionRegistry.getConfigurationElementsFor(NAMESPACE, EXT_POINT))
    {
      String name = element.getName();

      try
      {
        if (SIMPLE_FACTORY.equals(name))
        {
          registerFactory(new SimpleFactory(element));
        }
        else if (CONSTANT_FACTORY.equals(name))
        {
          registerFactory(new ConstantFactory(element));
        }
        else if (ANNOTATION_FACTORY.equals(name))
        {
          registerFactory(new AnnotationFactoryDescriptor(element));
        }
        else if (FACTORY.equals(name))
        {
          registerFactory(new FactoryDescriptor(element));
        }
        else if (FACTORIES.equals(name))
        {
          for (IConfigurationElement child : element.getChildren())
          {
            registerFactory(new FactoryDescriptor(child));
          }
        }
      }
      catch (Throwable t)
      {
        OM.LOG.warn(t);
      }
    }

    IRegistryChangeListener listener = new IRegistryChangeListener()
    {
      @Override
      public void registryChanged(IRegistryChangeEvent event)
      {
        IExtensionDelta[] deltas = event.getExtensionDeltas(NAMESPACE, EXT_POINT);
        for (IExtensionDelta delta : deltas)
        {
          // TODO Handle ExtensionDelta
          OM.LOG.warn("ExtensionDelta not handled: " + delta); //$NON-NLS-1$
        }
      }
    };

    extensionRegistry.addRegistryChangeListener(listener, NAMESPACE);
    extensionRegistryListener = listener;
  }

  private void doDeactivateOSGi()
  {
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    if (extensionRegistry == null)
    {
      return;
    }

    extensionRegistry.removeRegistryChangeListener((IRegistryChangeListener)extensionRegistryListener);
  }

  /**
   * @author Eike Stepper
   */
  public interface IFactoryDescriptor extends IFactory
  {
    public IFactory createFactory();

    @Override
    public default Object create(String description)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public default String getDescriptionFor(Object product)
    {
      throw new UnsupportedOperationException();
    }
  }
}
