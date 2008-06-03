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
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 */
public class LifecycleEventAdapter implements IListener
{
  public LifecycleEventAdapter()
  {
  }

  public final void notifyEvent(IEvent event)
  {
    if (event instanceof ILifecycleEvent)
    {
      ILifecycleEvent e = (ILifecycleEvent)event;
      notifyLifecycleEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyLifecycleEvent(ILifecycleEvent event)
  {
    switch (event.getKind())
    {
    case ABOUT_TO_ACTIVATE:
      onAboutToActivate(event.getLifecycle());
      break;
    case ACTIVATED:
      onActivated(event.getLifecycle());
      break;
    case ABOUT_TO_DEACTIVATE:
      onAboutToDeactivate(event.getLifecycle());
      break;
    case DEACTIVATED:
      onDeactivated(event.getLifecycle());
      break;
    }
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAboutToActivate(ILifecycle lifecycle)
  {
  }

  protected void onActivated(ILifecycle lifecycle)
  {
  }

  protected void onAboutToDeactivate(ILifecycle lifecycle)
  {
  }

  protected void onDeactivated(ILifecycle lifecycle)
  {
  }
}
