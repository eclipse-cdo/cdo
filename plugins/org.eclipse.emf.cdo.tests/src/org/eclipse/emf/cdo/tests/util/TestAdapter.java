/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import static org.junit.Assert.assertEquals;

import org.eclipse.net4j.util.tests.AbstractOMTest.PollingTimeOuter;

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
  private final List<Notification> notifications = new ArrayList<>();

  private Notifier notifier;

  public TestAdapter(Notifier... notifiers)
  {
    for (Notifier notifier : notifiers)
    {
      notifier.eAdapters().add(this);
    }
  }

  @Override
  public Notifier getTarget()
  {
    return notifier;
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    notifier = newTarget;
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    synchronized (notifications)
    {
      notifications.add(notification);
    }
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

  public Notification[] assertNotifications(int expectedSize)
  {
    synchronized (notifications)
    {
      assertEquals(expectedSize, notifications.size());
      return notifications.toArray(new Notification[notifications.size()]);
    }
  }

  public Notification[] assertNoTimeout(final int expectedSize) throws InterruptedException
  {
    final Notification[][] result = { null };
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        result[0] = getNotifications();
        return result[0].length == expectedSize;
      }
    }.assertNoTimeOut();

    return result[0];
  }
}
