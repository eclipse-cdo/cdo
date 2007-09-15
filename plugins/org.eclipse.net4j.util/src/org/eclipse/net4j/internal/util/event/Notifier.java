/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.event;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;

import java.util.ArrayList;
import java.util.List;

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
    synchronized (listeners)
    {
      listeners.add(listener);
    }
  }

  public void removeListener(IListener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
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
    for (IListener listener : getListeners())
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
