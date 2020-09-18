/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;

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
  public static final class ResourceSetConfiguration implements IDeactivateable
  {
    private final ResourceSet resourceSet;

    private final Object context;

    private final IManagedContainer container;

    private final Map<String, Object> configurerResults = new HashMap<>();

    private ResourceSetConfiguration(ResourceSet resourceSet, Object context, IManagedContainer container)
    {
      this.resourceSet = resourceSet;
      this.context = context;
      this.container = container;
    }

    public ResourceSet getResourceSet()
    {
      return resourceSet;
    }

    public Object getContext()
    {
      return context;
    }

    public IManagedContainer getContainer()
    {
      return container;
    }

    public Map<String, Object> getConfigurerResults()
    {
      return configurerResults;
    }

    @Override
    public Exception deactivate()
    {
      Exception exception = null;

      try
      {
        for (Object configurerResult : configurerResults.values())
        {
          Exception ex = LifecycleUtil.deactivate(configurerResult);
          if (ex != null && exception == null)
          {
            exception = ex;
          }
        }
      }
      finally
      {
        configurerResults.clear();
      }

      return exception;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.ResourceSetConfigurers";

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
      Map<String, Object> configurerResults = configuration.getConfigurerResults();

      for (String type : container.getFactoryTypes(Factory.PRODUCT_GROUP))
      {
        ResourceSetConfigurer configurer = getConfigurer(container, type);

        Object configurerResult = configurer.configureResourceSet(resourceSet, context, container);
        if (configurerResult != null)
        {
          configurerResults.put(type, configurerResult);
        }
      }

      return configuration;
    }

    public ResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context)
    {
      return configureResourceSet(resourceSet, context, IPluginContainer.INSTANCE);
    }
  }
}
