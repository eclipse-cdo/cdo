/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * @author Eike Stepper
 */
public class TestListener implements IListener
{
  private List<IEvent> events = new ArrayList<IEvent>();

  private int nextAssertion;

  public TestListener()
  {
  }

  public <T extends IEvent> void assertEvent(final Class<?> eventType, final EventAssertion<T> assertion) throws Exception
  {
    new AbstractOMTest.PollingTimeOuter()
    {
      @SuppressWarnings("unchecked")
      @Override
      protected boolean successful()
      {
        synchronized (events)
        {
          for (int i = nextAssertion; i < events.size(); i++)
          {
            IEvent event = events.get(i);
            System.out.println(event);
            ++nextAssertion;

            if (eventType.isAssignableFrom(event.getClass()))
            {
              try
              {
                assertion.execute((T)event);
                return true;
              }
              catch (AssertionFailedError ignore)
              {
                // This is not the expected event. Either it'll come later or we'll time out below.
              }
            }
          }
        }

        return false;
      }
    }.assertNoTimeOut();
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
      nextAssertion = 0;
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    synchronized (events)
    {
      events.add(event);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface EventAssertion<T extends IEvent>
  {
    public void execute(T event);
  }
}
