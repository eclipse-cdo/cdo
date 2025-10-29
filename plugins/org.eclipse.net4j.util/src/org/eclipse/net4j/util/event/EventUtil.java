/*
 * Copyright (c) 2007, 2009, 2011-2013, 2020-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import org.eclipse.net4j.util.event.INotifier.INotifier2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Various static helper methods for dealing with {@link IEvent events}, {@link INotifier notifiers} and
 * {@link IListener listeners}.
 *
 * @author Eike Stepper
 */
public final class EventUtil
{
  /**
   * @since 3.29
   */
  public static final String PRODUCT_GROUP_LISTENERS = "org.eclipse.net4j.util.event.listeners";

  /**
   * @since 3.0
   */
  public static final IListener[] NO_LISTENERS = {};

  private EventUtil()
  {
  }

  /**
   * @since 3.2
   */
  public static boolean addUniqueListener(Object notifier, IListener listener)
  {
    if (notifier instanceof INotifier2)
    {
      return ((INotifier2)notifier).addUniqueListener(listener);
    }

    if (notifier instanceof INotifier)
    {
      if (!hasListener(notifier, listener))
      {
        ((INotifier)notifier).addListener(listener);
        return true;
      }
    }

    return false;
  }

  public static boolean addListener(Object notifier, IListener listener)
  {
    if (notifier instanceof INotifier)
    {
      ((INotifier)notifier).addListener(listener);
      return true;
    }

    return false;
  }

  /**
   * @since 3.15
   */
  public static <E extends IEvent> AutoCloseable addListener(Object notifier, Class<E> eventType, Consumer<E> eventConsumer)
  {
    if (notifier instanceof INotifier)
    {
      INotifier n = (INotifier)notifier;

      AutoCloseableListener listener = new AutoCloseableListener()
      {
        @Override
        public void notifyEvent(IEvent event)
        {
          if (eventType.isInstance(event))
          {
            eventConsumer.accept(eventType.cast(event));
          }
        }

        @Override
        public void close() throws Exception
        {
          n.removeListener(this);
        }
      };

      n.addListener(listener);
      return listener;
    }

    return () -> {
    };
  }

  public static boolean removeListener(Object notifier, IListener listener)
  {
    if (notifier instanceof INotifier)
    {
      ((INotifier)notifier).removeListener(listener);
      return true;
    }

    return false;
  }

  /**
   * @since 3.20
   */
  public static IListener[] removeListeners(Object notifier, Predicate<IListener> predicate)
  {
    IListener[] listeners = getListeners(notifier, predicate);
    for (IListener listener : listeners)
    {
      removeListener(notifier, listener);
    }

    return listeners;
  }

  public static IListener[] getListeners(Object notifier)
  {
    if (notifier instanceof INotifier)
    {
      return ((INotifier)notifier).getListeners();
    }

    return NO_LISTENERS;
  }

  /**
   * @since 3.20
   */
  public static IListener[] getListeners(Object notifier, Predicate<IListener> predicate)
  {
    if (notifier instanceof INotifier)
    {
      List<IListener> result = null;
      for (IListener listener : ((INotifier)notifier).getListeners())
      {
        if (predicate.test(listener))
        {
          if (result == null)
          {
            result = new ArrayList<>(1);
          }

          result.add(listener);
        }
      }

      if (result != null)
      {
        return result.toArray(new IListener[result.size()]);
      }
    }

    return NO_LISTENERS;
  }

  /**
   * @since 3.2
   */
  public static boolean hasListener(Object notifier, IListener listener)
  {
    if (notifier instanceof INotifier2)
    {
      return ((INotifier2)notifier).hasListener(listener);
    }

    if (notifier instanceof INotifier)
    {
      IListener[] listeners = ((INotifier)notifier).getListeners();
      for (int i = 0; i < listeners.length; i++)
      {
        if (listeners[i] == listener)
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @since 3.29
   */
  public static boolean fireEvent(Object notifier, IEvent event)
  {
    if (notifier instanceof Notifier)
    {
      ((Notifier)notifier).fireEvent(event);
      return true;
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  private static interface AutoCloseableListener extends IListener, AutoCloseable
  {
  }
}
