/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

  private Queue<CDOChangeSetData> queue = new ConcurrentLinkedQueue<CDOChangeSetData>();

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
   */
  public CDOChangeSetData getChangeSetData()
  {
    return queue.poll();
  }

  public void reset()
  {
    queue.clear();
  }

  protected void handleEvent(CDOSessionInvalidationEvent event) throws Exception
  {
    queue.offer(event);
  }
}
