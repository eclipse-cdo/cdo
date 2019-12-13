/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * A {@link IListener listener} that dispatches container {@link IContainerEvent events} to methods that can be
 * overridden by extenders.
 *
 * @author Eike Stepper
 */
public class ContainerEventAdapter<E> implements IListener
{
  private boolean waitForActive;

  /**
   * @since 3.3
   */
  public ContainerEventAdapter(boolean waitForActive)
  {
    this.waitForActive = waitForActive;
  }

  public ContainerEventAdapter()
  {
    this(false);
  }

  /**
   * @since 3.3
   */
  public boolean isWaitForActive()
  {
    return waitForActive;
  }

  @Override
  public final void notifyEvent(IEvent event)
  {
    if (event instanceof IContainerEvent<?>)
    {
      @SuppressWarnings("unchecked")
      IContainerEvent<E> e = (IContainerEvent<E>)event;
      notifyContainerEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyContainerEvent(IContainerEvent<E> event)
  {
    final IContainer<E> container = event.getSource();
    event.accept(new IContainerEventVisitor<E>()
    {
      @Override
      public void added(final E element)
      {
        if (waitForActive && !LifecycleUtil.isActive(element))
        {
          EventUtil.addListener(element, new LifecycleEventAdapter()
          {
            @Override
            protected void onActivated(ILifecycle lifecycle)
            {
              onAdded(container, element);
              lifecycle.removeListener(this);
            }
          });
        }
        else
        {
          onAdded(container, element);
        }
      }

      @Override
      public void removed(E element)
      {
        onRemoved(container, element);
      }
    });
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAdded(IContainer<E> container, E element)
  {
  }

  protected void onRemoved(IContainer<E> container, E element)
  {
  }
}
