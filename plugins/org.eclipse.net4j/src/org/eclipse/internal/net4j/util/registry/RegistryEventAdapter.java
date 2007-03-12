/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.registry;

import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.registry.IRegistryEvent;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RegistryEventAdapter<K, V> implements IListener
{
  public final void notifyEvent(IEvent event)
  {
    if (event instanceof IRegistryEvent)
    {
      IRegistryEvent e = (IRegistryEvent)event;
      notifyRegistryEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyRegistryEvent(IRegistryEvent<K, V> event)
  {
    final IRegistry<K, V> registry = event.getRegistry();
    event.accept(new IContainerEventVisitor<Map.Entry<K, V>>()
    {
      public void added(Map.Entry<K, V> entry)
      {
        onAdded(registry, entry.getKey(), entry.getValue());
      }

      public void removed(Map.Entry<K, V> entry)
      {
        onRemoved(registry, entry.getKey(), entry.getValue());
      }
    });
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAdded(IRegistry<K, V> registry, K key, V value)
  {
  }

  protected void onRemoved(IRegistry<K, V> registry, K key, V value)
  {
  }
}
