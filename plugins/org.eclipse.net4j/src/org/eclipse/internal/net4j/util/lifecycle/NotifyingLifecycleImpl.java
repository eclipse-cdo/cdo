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
package org.eclipse.internal.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.internal.net4j.util.event.NotifierImpl;

/**
 * @author Eike Stepper
 */
public class NotifyingLifecycleImpl extends LifecycleImpl implements INotifier.Introspection
{
  private NotifierImpl notifier;

  public NotifyingLifecycleImpl()
  {
  }

  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  protected void fireEvent(IEvent event)
  {
    notifier.fireEvent(event);
  }
}
