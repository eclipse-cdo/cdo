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
package org.eclipse.internal.net4j.util.event;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class NotifierImpl implements INotifier
{
  private List<IListener> listeners = new ArrayList(0);

  public NotifierImpl()
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
        Net4j.LOG.error(ex);
      }
    }
  }
}
