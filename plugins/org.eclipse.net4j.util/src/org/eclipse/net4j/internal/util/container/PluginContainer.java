/*
 * Copyright (c) 2007-2009, 2011-2013, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - private plug-in container instances
 *    Christian W. Damus (CEA) - bug 399641: container-aware factories
 */
package org.eclipse.net4j.internal.util.container;

import org.eclipse.net4j.internal.util.factory.PluginFactoryRegistry;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class PluginContainer extends ManagedContainer implements IPluginContainer
{
  private static PluginContainer instance;

  public PluginContainer()
  {
  }

  /**
   * @since 3.8
   */
  @Override
  protected String getTypeName()
  {
    return "PluginContainer"; //$NON-NLS-1$
  }

  @Override
  protected IRegistry<IFactoryKey, IFactory> createFactoryRegistry()
  {
    return new PluginFactoryRegistry(this);
  }

  @Override
  protected List<IElementProcessor> createPostProcessors()
  {
    return new PluginElementProcessorList();
  }

  public static void dispose()
  {
    if (instance != null)
    {
      LifecycleUtil.deactivate(instance, OMLogger.Level.WARN);
      instance = null;
    }
  }

  public static synchronized PluginContainer getInstance()
  {
    if (instance == null)
    {
      PluginContainer container = new PluginContainer();
      container.setName("INSTANCE");
      container.activate();

      instance = container; // Leave instance==null in case of a Throwable in activate()
    }

    return instance;
  }
}
