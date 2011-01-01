/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.FastList;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class Notifier implements INotifier
{
  private FastList<IListener> listeners = new FastList<IListener>()
  {
    @Override
    protected IListener[] newArray(int length)
    {
      return new IListener[length];
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

  public void addListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.add(listener);
  }

  public void removeListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.remove(listener);
  }

  public boolean hasListeners()
  {
    return listeners.get() != null;
  }

  public IListener[] getListeners()
  {
    return listeners.get();
  }

  public void fireEvent(IEvent event)
  {
    fireEvent(event, getListeners());
  }

  /**
   * @since 3.0
   */
  public void fireEvent(final IEvent event, final IListener[] listeners)
  {
    if (listeners != null)
    {
      ExecutorService notificationService = getNotificationService();
      if (notificationService != null)
      {
        notificationService.execute(new Runnable()
        {
          public void run()
          {
            fireEventSafe(event, listeners);
          }
        });
      }
      else
      {
        fireEventSafe(event, listeners);
      }
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
   * @since 3.0
   */
  protected void firstListenerAdded()
  {
  }

  /**
   * @since 3.0
   */
  protected void lastListenerRemoved()
  {
  }

  private static void fireEventSafe(IEvent event, IListener[] listeners)
  {
    for (int i = 0; i < listeners.length; i++)
    {
      try
      {
        IListener listener = listeners[i];
        if (listener != null)
        {
          listener.notifyEvent(event);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}
