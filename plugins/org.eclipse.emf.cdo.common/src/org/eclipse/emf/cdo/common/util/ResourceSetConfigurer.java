/*
 * Copyright (c) 2020, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.internal.common.bundle.OM;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.12
 */
public interface ResourceSetConfigurer
{
  public Object configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.ResourceSetConfigurers"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract ResourceSetConfigurer create(String description) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Registry
  {
    public static final Registry INSTANCE = new Registry();

    private Registry()
    {
    }

    public ResourceSetConfigurer getConfigurer(IManagedContainer container, String type)
    {
      return (ResourceSetConfigurer)container.getElement(Factory.PRODUCT_GROUP, type, null);
    }

    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container)
    {
      ResourceSetConfiguration configuration = new ResourceSetConfiguration(resourceSet, context, container);

      for (String type : container.getFactoryTypes(Factory.PRODUCT_GROUP))
      {
        configureResourceSet(resourceSet, context, container, type, configuration);
      }

      return configuration;
    }

    /**
     * @since 4.15
     */
    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container, String type)
    {
      ResourceSetConfiguration configuration = ResourceSetConfiguration.of(resourceSet);
      if (configuration == null)
      {
        configuration = new ResourceSetConfiguration(resourceSet, context, container);
      }

      configureResourceSet(resourceSet, context, container, type, configuration);
      return configuration;
    }

    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context)
    {
      return configureResourceSet(resourceSet, context, IPluginContainer.INSTANCE);
    }

    private void configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container, String type, ResourceSetConfiguration configuration)
    {
      ResourceSetConfigurer configurer = getConfigurer(container, type);

      Object configurerResult = configurer.configureResourceSet(resourceSet, context, container);
      if (configurerResult != null)
      {
        configuration.configurerResults.put(type, configurerResult);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class ResourceSetConfiguration extends AdapterImpl implements IDeactivateable, IManagedContainerProvider
    {
      private final ResourceSet resourceSet;

      private final Object context;

      private final IManagedContainer container;

      private final Map<String, Object> configurerResults = new HashMap<>();

      private boolean active = true;

      private ResourceSetConfiguration(ResourceSet resourceSet, Object context, IManagedContainer container)
      {
        this.resourceSet = resourceSet;
        this.context = context;
        this.container = container;

        resourceSet.eAdapters().add(this);
      }

      public ResourceSet getResourceSet()
      {
        return resourceSet;
      }

      public Object getContext()
      {
        return context;
      }

      @Override
      public IManagedContainer getContainer()
      {
        return container;
      }

      public Map<String, Object> getConfigurerResults()
      {
        return Collections.unmodifiableMap(configurerResults);
      }

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

      public static ResourceSetConfiguration of(ResourceSet resourceSet)
      {
        return (ResourceSetConfiguration)EcoreUtil.getAdapter(resourceSet.eAdapters(), ResourceSetConfiguration.class);
      }
    }
  }
}
