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

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RegistryEventAdapter implements IListener
{
  public final void notifyEvent(IEvent event)
  {
    if (event instanceof IContainerEvent)
    {
      IContainerEvent e = (IContainerEvent)event;
      notifyContainerEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyContainerEvent(IContainerEvent event)
  {
    IContainer container = event.getContainer();
    if (container instanceof IRegistry)
    {
      notifyRegistryEvent(event);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyRegistryEvent(IContainerEvent event)
  {
    final IRegistry registry = (IRegistry)event.getContainer();
    event.accept(new IContainerEventVisitor()
    {
      public void added(Object element)
      {
        Map.Entry entry = (Map.Entry)element;
        onAdded(registry, entry.getKey(), entry.getValue());
      }

      public void removed(Object element)
      {
        Map.Entry entry = (Map.Entry)element;
        onRemoved(registry, entry.getKey(), entry.getValue());
      }
    });
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAdded(IRegistry registry, Object key, Object value)
  {
  }

  protected void onRemoved(IRegistry registry, Object key, Object value)
  {
  }
}
