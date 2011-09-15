/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class TestAdapter implements Adapter
{
  private List<Notification> notifications = new ArrayList<Notification>();

  private Notifier notifier;

  public TestAdapter()
  {
  }

  public Notifier getTarget()
  {
    return notifier;
  }

  public Notification[] getNotifications()
  {
    synchronized (notifications)
    {
      return notifications.toArray(new Notification[notifications.size()]);
    }
  }

  public void clearNotifications()
  {
    synchronized (notifications)
    {
      notifications.clear();
    }
  }

  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  public void notifyChanged(Notification notification)
  {
    synchronized (notifications)
    {
      notifications.add(notification);
    }
  }

  public void setTarget(Notifier newTarget)
  {
    notifier = newTarget;
  }
}
