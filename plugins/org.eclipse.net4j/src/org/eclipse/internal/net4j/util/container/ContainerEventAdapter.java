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
package org.eclipse.internal.net4j.util.container;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 */
public class ContainerEventAdapter implements IListener
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
    final IContainer container = event.getContainer();
    event.accept(new IContainerEventVisitor()
    {
      public void added(Object element)
      {
        onAdded(container, element);
      }

      public void removed(Object element)
      {
        onRemoved(container, element);
      }
    });
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAdded(IContainer container, Object element)
  {
  }

  protected void onRemoved(IContainer container, Object element)
  {
  }
}
