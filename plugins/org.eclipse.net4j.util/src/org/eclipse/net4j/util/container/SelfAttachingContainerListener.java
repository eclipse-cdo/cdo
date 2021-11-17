/*
 * Copyright (c) 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class SelfAttachingContainerListener implements IListener.NotifierAware
{
  public SelfAttachingContainerListener()
  {
  }

  @Override
  public void addNotifier(INotifier notifier)
  {
    attach(notifier);
  }

  @Override
  public void removeNotifier(INotifier notifier)
  {
    detach(notifier);
  }

  public void attach(Object element)
  {
    if (shouldDescend(element))
    {
      try
      {
        Object[] children = ContainerUtil.getElements(element);
        if (children != null)
        {
          for (Object child : children)
          {
            try
            {
              EventUtil.addUniqueListener(child, this);
            }
            catch (Exception ex)
            {
              handleException(ex);
            }
          }
        }
      }
      catch (Exception ex)
      {
        handleException(ex);
      }
    }
  }

  public void detach(Object element)
  {
    if (shouldDescend(element))
    {
      try
      {
        Object[] children = ContainerUtil.getElements(element);
        if (children != null)
        {
          for (Object child : children)
          {
            try
            {
              EventUtil.removeListener(child, this);
            }
            catch (Exception ex)
            {
              handleException(ex);
            }
          }
        }
      }
      catch (Exception ex)
      {
        handleException(ex);
      }
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof IContainerEvent<?>)
    {
      notifyContainerEvent((IContainerEvent<?>)event);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyContainerEvent(IContainerEvent<?> event)
  {
    for (IContainerDelta<?> delta : event.getDeltas())
    {
      final Object element = delta.getElement();

      if (delta.getKind() == Kind.ADDED)
      {
        if (isWaitForActive() && !isActive(element))
        {
          EventUtil.addListener(element, new LifecycleEventAdapter()
          {
            @Override
            protected void onActivated(ILifecycle lifecycle)
            {
              lifecycle.removeListener(this);
              attach(element);
            }
          });
        }
        else
        {
          EventUtil.addUniqueListener(element, this);
        }
      }
      else
      {
        EventUtil.removeListener(element, this);
      }
    }
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  /**
   * @deprecated As of 3.16 not used anymore.
   */
  @Deprecated
  protected boolean shouldAttach(Object element)
  {
    throw new UnsupportedOperationException();
  }

  protected boolean shouldDescend(Object element)
  {
    return !(element instanceof DoNotDescend);
  }

  protected boolean isWaitForActive()
  {
    return true;
  }

  protected boolean isActive(Object element)
  {
    return LifecycleUtil.isActive(element);
  }

  protected void handleException(Exception ex)
  {
  }

  /**
   * @author Eike Stepper
   */
  public interface DoNotDescend
  {
  }

  /**
   * @author Eike Stepper
   */
  public static class Delegating extends SelfAttachingContainerListener
  {
    private final IListener delegate;

    private final boolean delegateContainerEvents;

    public Delegating(IListener delegate, boolean delegateContainerEvents)
    {
      this.delegate = delegate;
      this.delegateContainerEvents = delegateContainerEvents;
    }

    public Delegating(IListener delegate)
    {
      this(delegate, false);
    }

    @Override
    protected void notifyContainerEvent(IContainerEvent<?> event)
    {
      super.notifyContainerEvent(event);

      if (delegateContainerEvents)
      {
        delegate.notifyEvent(event);
      }
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      delegate.notifyEvent(event);
    }
  }
}
