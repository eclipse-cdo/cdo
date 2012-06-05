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

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.tests.AbstractOMTest;

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

  public void assertEvent(final EventAssertion assertion) throws Exception
  {
    final Exception[] exception = { null };
    final Error[] error = { null };

    new AbstractOMTest.PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        IEvent event;
        synchronized (events)
        {
          if (events.size() != 1)
          {
            return false;
          }

          event = events.get(0);
        }

        try
        {
          assertion.execute(event);
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
        catch (Error err)
        {
          error[0] = err;
        }

        return true;
      }
    }.assertNoTimeOut();

    if (exception[0] != null)
    {
      throw exception[0];
    }

    if (error[0] != null)
    {
      throw error[0];
    }
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

  /**
   * @author Eike Stepper
   */
  public interface EventAssertion<T extends IEvent>
  {
    public void execute(T event) throws Exception;
  }
}
