/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

/**
 * @author Caspar De Groot
 */
public class TestListener2 implements IListener
{
  private final static long DEFAULT_TIMEOUT = 3000; // 3 seconds

  private List<IEvent> events = new LinkedList<IEvent>();

  private Class<? extends IEvent> eventClass;

  private long timeout;

  private String name;

  public TestListener2(Class<? extends IEvent> eventClass)
  {
    this(eventClass, null);
  }

  public TestListener2(Class<? extends IEvent> eventClass, String name)
  {
    this.eventClass = eventClass;
    this.name = name;
    timeout = DEFAULT_TIMEOUT;
  }

  public synchronized void notifyEvent(IEvent event)
  {
    if (eventClass == null || eventClass.isAssignableFrom(event.getClass()))
    {
      events.add(event);
      notify();
    }
  }

  public List<IEvent> getEvents()
  {
    return events;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  public synchronized void waitFor(int n, long timeout)
  {
    long t = 0;

    while (events.size() < n)
    {
      if (timeout <= 0)
      {
        Assert.fail("Timed out");
      }

      try
      {
        t = System.currentTimeMillis();
        wait(timeout);
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }

      timeout -= System.currentTimeMillis() - t;
    }
  }

  public void waitFor(int i)
  {
    waitFor(i, timeout);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder(TestListener2.class.getSimpleName());
    builder.append('[');
    if (name != null)
    {
      builder.append("name=\"");
      builder.append(name);
      builder.append('\"');
    }
    
    if (eventClass != null)
    {
      if (builder.charAt(builder.length() - 1) != '[')
      {
        builder.append(';');
      }
      builder.append("eventClass=");
      builder.append(eventClass.getSimpleName());
    }
    
    builder.append(']');
    return builder.toString();
  }
}
