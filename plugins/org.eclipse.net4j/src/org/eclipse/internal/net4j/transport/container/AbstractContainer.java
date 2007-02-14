/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.container;

import org.eclipse.net4j.transport.container.Container;
import org.eclipse.net4j.transport.container.ContainerAdapter;
import org.eclipse.net4j.transport.container.ContainerAdapterFactory;
import org.eclipse.net4j.transport.container.ContainerAdapterID;
import org.eclipse.net4j.util.lifecycle.LifecycleImpl;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * @author Eike Stepper
 */
public abstract class AbstractContainer extends LifecycleImpl implements Container
{
  private IRegistry<ContainerAdapterID, ContainerAdapterFactory> adapterFactoryRegistry;

  private IRegistry<ContainerAdapterID, ContainerAdapter> adapters = new HashMapRegistry();

  protected AbstractContainer(IRegistry<ContainerAdapterID, ContainerAdapterFactory> adapterFactoryRegistry)
  {
    this.adapterFactoryRegistry = adapterFactoryRegistry;
  }

  protected AbstractContainer()
  {
    this(ContainerAdapterFactory.REGISTRY);
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (adapterFactoryRegistry == null)
    {
      throw new IllegalStateException("adapterFactoryRegistry == null");
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    for (ContainerAdapter adapter : adapters.values())
    {
      LifecycleUtil.deactivate(adapter);
    }

    super.onDeactivate();
  }

  public IRegistry<ContainerAdapterID, ContainerAdapterFactory> getAdapterFactoryRegistry()
  {
    return adapterFactoryRegistry;
  }

  public IRegistry<ContainerAdapterID, ContainerAdapter> getAdapters()
  {
    return adapters;
  }

  public ContainerAdapter getAdapter(ContainerAdapterID adapterID)
  {
    ContainerAdapter adapter = adapters.get(adapterID);
    if (adapter == null)
    {
      ContainerAdapterFactory factory = adapterFactoryRegistry.get(adapterID);
      if (factory != null)
      {
        adapter = createAdapter(factory);
        if (adapter != null)
        {
          adapters.put(adapterID, adapter);
        }
      }
    }

    return adapter;
  }

  private ContainerAdapter createAdapter(ContainerAdapterFactory factory)
  {
    try
    {
      ContainerAdapter adapter = factory.createAdapter(this);
      LifecycleUtil.activate(adapter);
      return adapter;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}
