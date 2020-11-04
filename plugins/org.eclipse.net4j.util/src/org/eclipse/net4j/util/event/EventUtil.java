/*
 * Copyright (c) 2007, 2009, 2011-2013, 2020 Eike Stepper (Loehne, Germany) and others.
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

/**
 * Various static helper methods for dealing with {@link IEvent events}, {@link INotifier notifiers} and
 * {@link IListener listeners}.
 *
 * @author Eike Stepper
 */
public final class EventUtil
{
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

  public static boolean removeListener(Object notifier, IListener listener)
  {
    if (notifier instanceof INotifier)
    {
      ((INotifier)notifier).removeListener(listener);
      return true;
    }

    return false;
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
}
