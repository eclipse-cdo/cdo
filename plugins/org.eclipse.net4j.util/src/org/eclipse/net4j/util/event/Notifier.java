/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019-2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.event.IListener.NotifierAware;
import org.eclipse.net4j.util.event.INotifier.INotifier2;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.concurrent.ExecutorService;

/**
 * A default implementation of a {@link INotifier notifier}.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public class Notifier implements INotifier2
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Notifier.class);

  private final ConcurrentArray<IListener> listeners = new ConcurrentArray<IListener>()
  {
    @Override
    protected IListener[] newArray(int length)
    {
      return new IListener[length];
    }

    @Override
    protected void elementAdded(IListener element)
    {
      listenerAdded(element);
    }

    @Override
    protected void elementRemoved(IListener element)
    {
      listenerRemoved(element);
    }

    @Override
    protected void firstElementAdded()
    {
      firstListenerAdded();
    }

    @Override
    protected void lastElementRemoved()
    {
      lastListenerRemoved();
    }
  };

  public Notifier()
  {
  }

  @Override
  public boolean addUniqueListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    if (listeners.addUnique(listener))
    {
      if (listener instanceof NotifierAware)
      {
        ((NotifierAware)listener).addNotifier(this);
      }

      return true;
    }

    return false;
  }

  @Override
  @InjectElement(name = "listener", productGroup = EventUtil.PRODUCT_GROUP_LISTENERS)
  public void addListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.add(listener);

    if (listener instanceof NotifierAware)
    {
      ((NotifierAware)listener).addNotifier(this);
    }
  }

  @Override
  public void removeListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$

    if (listener instanceof NotifierAware)
    {
      ((NotifierAware)listener).removeNotifier(this);
    }

    listeners.remove(listener);
  }

  @Override
  public boolean hasListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    return listeners.contains(listener);
  }

  @Override
  public boolean hasListeners()
  {
    return !listeners.isEmpty();
  }

  @Override
  public IListener[] getListeners()
  {
    return listeners.get();
  }

  /**
   * @since 3.2
   */
  public void fireEvent()
  {
    fireEvent(new Event(this));
  }

  public void fireEvent(IEvent event)
  {
    if (event != null)
    {
      fireEvent(event, getListeners());
    }
  }

  /**
   * @since 3.0
   */
  public void fireEvent(final IEvent event, final IListener[] listeners)
  {
    if (event != null && listeners.length != 0)
    {
      ExecutorService notificationService = getNotificationService();
      if (notificationService != null)
      {
        notificationService.execute(() -> fireEventSafe(event, listeners));
      }
      else
      {
        fireEventSafe(event, listeners);
      }
    }
  }

  /**
   * @since 3.3
   */
  protected void fireThrowable(Throwable throwable)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(throwable);
    }

    IListener[] listeners = getListeners();
    if (listeners.length != 0)
    {
      IEvent event = new ThrowableEvent(this, throwable);
      fireEvent(event, listeners);
    }
  }

  /**
   * @since 3.0
   */
  protected ExecutorService getNotificationService()
  {
    return null;
  }

  /**
   * @since 3.13
   */
  protected void listenerAdded(IListener listener)
  {
    // Do nothing.
  }

  /**
   * @since 3.13
   */
  protected void listenerRemoved(IListener listener)
  {
    // Do nothing.
  }

  /**
   * @since 3.0
   */
  protected void firstListenerAdded()
  {
    // Do nothing.
  }

  /**
   * @since 3.0
   */
  protected void lastListenerRemoved()
  {
    // Do nothing.
  }

  private static void fireEventSafe(IEvent event, IListener[] listeners)
  {
    for (int i = 0; i < listeners.length; i++)
    {
      IListener listener = listeners[i];

      try
      {
        if (listener != null)
        {
          listener.notifyEvent(event);
        }
      }
      catch (Cancelation ex)
      {
        throw (RuntimeException)ex.getCause();
      }
      catch (Exception ex)
      {
        OM.LOG.warn(listener + " failed to process " + event + ": " + ex, ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.29
   */
  public static final class Cancelation extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public Cancelation(RuntimeException reason)
    {
      super(reason);
    }
  }
}
