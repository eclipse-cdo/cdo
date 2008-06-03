/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.container;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.factory.PluginFactoryRegistry;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class PluginContainer extends ManagedContainer implements IPluginContainer
{
  private static PluginContainer instance;

  private PluginContainer()
  {
  }

  @Override
  protected IRegistry<IFactoryKey, IFactory> createFactoryRegistry()
  {
    return new PluginFactoryRegistry();
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
      instance.deactivate();
      instance = null;
    }
  }

  public static synchronized PluginContainer getInstance()
  {
    if (instance == null)
    {
      instance = new PluginContainer();

      try
      {
        instance.activate();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        instance = null;
      }
    }

    return instance;
  }
}
