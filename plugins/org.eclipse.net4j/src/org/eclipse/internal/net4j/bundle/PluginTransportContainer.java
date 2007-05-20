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
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.transport.TransportContainer;
import org.eclipse.internal.net4j.util.container.PluginElementProcessorList;
import org.eclipse.internal.net4j.util.factory.PluginFactoryRegistry;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class PluginTransportContainer extends TransportContainer implements IPluginTransportContainer
{
  private static PluginTransportContainer instance;

  private PluginTransportContainer()
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

  static void dispose()
  {
    if (instance != null)
    {
      instance.deactivate();
      instance = null;
    }
  }

  public static synchronized PluginTransportContainer getInstance()
  {
    if (instance == null)
    {
      instance = new PluginTransportContainer();

      try
      {
        instance.activate();
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
        instance = null;
      }
    }

    return instance;
  }
}
