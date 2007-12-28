/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.event;

/**
 * @author Eike Stepper
 */
public final class EventUtil
{
  private static final IListener[] NO_LISTENERS = new IListener[0];

  private EventUtil()
  {
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
    if (notifier instanceof INotifier.Introspection)
    {
      return ((INotifier.Introspection)notifier).getListeners();
    }

    return NO_LISTENERS;
  }
}
