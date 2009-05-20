/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class Notifier implements INotifier.Introspection
{
  /**
   * TODO Optimize by storing an array
   */
  private List<IListener> listeners = new ArrayList<IListener>(0);

  public Notifier()
  {
  }

  public void addListener(IListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    boolean wasNotEmpty;
    boolean isNotEmpty;
    synchronized (listeners)
    {
      wasNotEmpty = !listeners.isEmpty();
      listeners.add(listener);
      isNotEmpty = !listeners.isEmpty();
    }

    if (wasNotEmpty ^ isNotEmpty)
    {
      listenersEmptyChanged(!isNotEmpty);
    }
  }

  public void removeListener(IListener listener)
  {
    boolean wasEmpty;
    boolean isEmpty;
    synchronized (listeners)
    {
      wasEmpty = listeners.isEmpty();
      listeners.remove(listener);
      isEmpty = listeners.isEmpty();
    }

    if (wasEmpty ^ isEmpty)
    {
      listenersEmptyChanged(isEmpty);
    }
  }

  public boolean hasListeners()
  {
    return !listeners.isEmpty();
  }

  public IListener[] getListeners()
  {
    synchronized (listeners)
    {
      return listeners.toArray(new IListener[listeners.size()]);
    }
  }

  public void fireEvent(IEvent event)
  {
    Runnable runnable = new FireEventRunnable(getListeners(), event);
    ExecutorService executorService = getNotificationExecutorService();
    if (executorService == null)
    {
      runnable.run();
    }
    else
    {
      executorService.execute(runnable);
    }
  }

  /**
   * @since 2.0
   */
  protected ExecutorService getNotificationExecutorService()
  {
    return null;
  }

  /**
   * @since 2.0
   */
  protected void listenersEmptyChanged(boolean empty)
  {
  }

  /**
   * @author Eike Stepper
   */
  private static final class FireEventRunnable implements Runnable
  {
    private IListener[] listeners;

    private IEvent event;

    public FireEventRunnable(IListener[] listeners, IEvent event)
    {
      this.listeners = listeners;
      this.event = event;
    }

    public void run()
    {
      for (IListener listener : listeners)
      {
        try
        {
          listener.notifyEvent(event);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }
}
