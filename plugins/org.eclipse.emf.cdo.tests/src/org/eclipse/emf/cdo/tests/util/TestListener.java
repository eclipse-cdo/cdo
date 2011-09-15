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

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class TestListener implements IListener
{
  private List<IEvent> events = new ArrayList<IEvent>();

  public TestListener()
  {
  }

  public IEvent[] getEvents()
  {
    synchronized (events)
    {
      return events.toArray(new IEvent[events.size()]);
    }
  }

  public void clearEvents()
  {
    synchronized (events)
    {
      events.clear();
    }
  }

  public void notifyEvent(IEvent event)
  {
    synchronized (events)
    {
      events.add(event);
    }
  }
}
