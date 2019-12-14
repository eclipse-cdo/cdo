/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.stylizer;

import org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer;
import org.eclipse.emf.cdo.dawn.appearance.IDawnElementStylizerFactory;
import org.eclipse.emf.cdo.dawn.internal.ui.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import java.util.HashMap;
import java.util.Map;

/**
 * This registry provides the DawnElementStylizer implementation for a given object.
 *
 * @see org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnElementStylizerRegistry
{
  private static final String ATTRIBUTE_PRIORITY = "priority";

  private static final String ATTRIIBUTE_FACTORY = "factory";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnElementStylizerRegistry.class);

  private static final String DAWN_STYLIZER_FACTORY_EXTENSION_POINT_ID = "org.eclipse.emf.cdo.dawn.elementstylizerfactory";

  public static DawnElementStylizerRegistry instance = new DawnElementStylizerRegistry();

  private Map<String, DawnElementStylizer> registeredStylizers = new HashMap<>();

  private Map<String, FactoryContainer> stylizerFactories = new HashMap<>();

  private DawnDefaultElementStylizer dawnDefaultElementStylizer;

  /**
   * @since 2.0
   */
  public DawnElementStylizer getStylizer(Object object)
  {
    DawnElementStylizer stylizer = registeredStylizers.get(object.getClass().getCanonicalName());

    if (stylizer == null)
    {
      stylizer = getStylizerFromExtensionPoint(object);
      registeredStylizers.put(object.getClass().getCanonicalName(), stylizer);
    }

    if (stylizer == null)
    {
      stylizer = getDefaultStylizer(object);
    }

    return stylizer;
  }

  private DawnElementStylizer getDefaultStylizer(Object object)
  {
    dawnDefaultElementStylizer = new DawnDefaultElementStylizer();
    return dawnDefaultElementStylizer;
  }

  private DawnElementStylizer getStylizerFromExtensionPoint(Object object)
  {
    Map<String, FactoryContainer> factories = getFactories();
    DawnElementStylizer elementStylizer = null;
    int lastPriority = -1;

    for (FactoryContainer factoryContainer : factories.values())
    {
      DawnElementStylizer currentElementStylizer = factoryContainer.getFactory().getElementStylizer(object);
      if (currentElementStylizer != null)
      {
        int priority = factoryContainer.getPriority();
        if (priority > lastPriority)
        {
          elementStylizer = currentElementStylizer;
          lastPriority = priority;
        }
      }
    }

    return elementStylizer;
  }

  private Map<String, FactoryContainer> getFactories()
  {
    try
    {
      IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(DAWN_STYLIZER_FACTORY_EXTENSION_POINT_ID);
      for (IConfigurationElement e : config)
      {
        String factoryClassName = e.getAttribute(ATTRIIBUTE_FACTORY);
        if (!stylizerFactories.containsKey(factoryClassName))
        {
          IDawnElementStylizerFactory stylizer = (IDawnElementStylizerFactory)e.createExecutableExtension(ATTRIIBUTE_FACTORY);

          int priority = Integer.parseInt(e.getAttribute(ATTRIBUTE_PRIORITY).substring(0, 1));

          FactoryContainer container = new FactoryContainer(priority, stylizer);

          stylizerFactories.put(factoryClassName, container);
          if (TRACER.isEnabled())
          {
            TRACER.format("Registered IDawnElementStylizerFactory {0} ", stylizer); //$NON-NLS-1$
          }
        }
      }
    }
    catch (InvalidRegistryObjectException e)
    {
      e.printStackTrace();
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }

    return stylizerFactories;
  }

  private class FactoryContainer
  {
    private int priority;

    private IDawnElementStylizerFactory factory;

    public FactoryContainer(int priority, IDawnElementStylizerFactory factory)
    {
      this.priority = priority;
      this.factory = factory;
    }

    public IDawnElementStylizerFactory getFactory()
    {
      return factory;
    }

    public int getPriority()
    {
      return priority;
    }
  }
}
