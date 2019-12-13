/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOSessionInvalidationEventQueue
{
  private CDOSession session;

  private IListener sessionListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      try
      {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
          handleEvent(e);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  };

  private Queue<CDOSessionInvalidationEvent> queue = new LinkedList<CDOSessionInvalidationEvent>();

  public CDOSessionInvalidationEventQueue(CDOSession session)
  {
    this.session = session;
    session.addListener(sessionListener);
  }

  public void dispose()
  {
    reset();
    session.removeListener(sessionListener);
    session = null;
  }

  public CDOSession getSession()
  {
    return session;
  }

  /**
   * Removes and returns the first event from this queue.
   * @deprecated As of 4.3 use {@link #poll()}.
   */
  @Deprecated
  public CDOChangeSetData getChangeSetData()
  {
    return poll();
  }

  /**
   * Removes and returns the first event from this queue.
   * @since 4.3
   */
  public CDOSessionInvalidationEvent poll()
  {
    synchronized (queue)
    {
      return queue.poll();
    }
  }

  /**
   * @since 4.3
   */
  public CDOSessionInvalidationEvent remove(long timestamp)
  {
    synchronized (queue)
    {
      for (Iterator<CDOSessionInvalidationEvent> it = queue.iterator(); it.hasNext();)
      {
        CDOSessionInvalidationEvent event = it.next();
        if (event.getTimeStamp() == timestamp)
        {
          it.remove();
          return event;
        }
      }

      return null;
    }
  }

  public void reset()
  {
    synchronized (queue)
    {
      queue.clear();
    }
  }

  protected void handleEvent(CDOSessionInvalidationEvent event) throws Exception
  {
    synchronized (queue)
    {
      queue.offer(event);
    }
  }
}
