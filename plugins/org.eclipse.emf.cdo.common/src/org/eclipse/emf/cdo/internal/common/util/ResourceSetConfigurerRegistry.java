/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.util;

import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer;
import org.eclipse.emf.cdo.internal.common.bundle.OM;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class ResourceSetConfigurerRegistry implements ResourceSetConfigurer.Registry
{
  public static final ResourceSetConfigurerRegistry INSTANCE = new ResourceSetConfigurerRegistry();

  private ResourceSetConfigurerRegistry()
  {
  }

  @Override
  public ResourceSetConfigurer getConfigurer(IManagedContainer container, String type)
  {
    return container.getElementOrNull(ResourceSetConfigurer.Factory.PRODUCT_GROUP, type);
  }

  @Override
  public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context)
  {
    return configureResourceSet(resourceSet, context, null);
  }

  @Override
  public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container)
  {
    ResourceSetConfiguration configuration = null;

    if (container == null)
    {
      container = ContainerUtil.getContainer(context);
    }

    if (container == null)
    {
      container = IPluginContainer.INSTANCE;
    }

    for (String type : container.getFactoryTypes(ResourceSetConfigurer.Factory.PRODUCT_GROUP))
    {
      ResourceSetConfiguration c = configureResourceSet(resourceSet, context, container, type);
      if (c != null)
      {
        configuration = c;
      }
    }

    return configuration;
  }

  /**
   * @since 4.15
   */
  @Override
  public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container, String type)
  {
    ResourceSetConfigurer configurer = getConfigurer(container, type);
    if (configurer != null)
    {
      Object configurerResult = configurer.configureResourceSet(resourceSet, context, container);
      if (configurerResult != null)
      {
        ResourceSetConfigurationImpl configuration = (ResourceSetConfigurationImpl)ResourceSetConfiguration.of(resourceSet);
        if (configuration == null)
        {
          configuration = new ResourceSetConfigurationImpl(resourceSet);
        }

        configuration.configurerResults.put(type, configurerResult);
        return configuration;
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static final class ResourceSetConfigurationImpl extends AdapterImpl implements ResourceSetConfiguration, IDeactivateable
  {
    private final ResourceSet resourceSet;

    private final Map<String, Object> configurerResults = new HashMap<>();

    private boolean active = true;

    private ResourceSetConfigurationImpl(ResourceSet resourceSet)
    {
      this.resourceSet = resourceSet;
      resourceSet.eAdapters().add(this);
    }

    @Override
    public ResourceSet getResourceSet()
    {
      return resourceSet;
    }

    @Override
    public Map<String, Object> getConfigurerResults()
    {
      return Collections.unmodifiableMap(configurerResults);
    }

    @Override
    public boolean isActive()
    {
      return active;
    }

    @Override
    public Exception deactivate()
    {
      Exception exception = null;

      if (active)
      {
        active = false;

        try
        {
          resourceSet.eAdapters().remove(this);

          for (Object configurerResult : configurerResults.values())
          {
            Exception ex = LifecycleUtil.deactivate(configurerResult);
            if (ex != null)
            {
              OM.LOG.error(ex);

              if (exception == null)
              {
                exception = ex;
              }
            }
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);

          if (exception == null)
          {
            exception = ex;
          }
        }
        finally
        {
          configurerResults.clear();
        }
      }

      return exception;
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type == ResourceSetConfiguration.class;
    }
  }
}
