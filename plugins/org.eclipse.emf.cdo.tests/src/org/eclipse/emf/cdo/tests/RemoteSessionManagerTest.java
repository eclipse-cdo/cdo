/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionEvent;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class RemoteSessionManagerTest extends AbstractCDOTest
{
  public void testRemoteSessionOpened() throws Exception
  {
    final AsyncResult<Integer> result1 = new AsyncResult<Integer>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new ContainerEventAdapter<CDORemoteSession>()
    {
      @Override
      protected void onAdded(IContainer<CDORemoteSession> container, CDORemoteSession session)
      {
        result1.setValue(session.getSessionID());
      }
    });

    CDOSession session2 = openSession();

    assertEquals(session2.getSessionID(), (int)result1.getValue());
    session2.close();
    session1.close();
  }

  public void testRemoteSessionClosed() throws Exception
  {
    final AsyncResult<Integer> result1 = new AsyncResult<Integer>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new ContainerEventAdapter<CDORemoteSession>()
    {
      @Override
      protected void onRemoved(IContainer<CDORemoteSession> container, CDORemoteSession session)
      {
        result1.setValue(session.getSessionID());
      }
    });

    CDOSession session2 = openSession();
    session2.close();

    assertEquals(session2.getSessionID(), (int)result1.getValue());
    session2.close();
    session1.close();
  }

  public void testSubscribeByForce() throws Exception
  {
    final AsyncResult<Integer> result1 = new AsyncResult<Integer>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORemoteSessionEvent.SubscriptionChanged)
        {
          CDORemoteSessionEvent.SubscriptionChanged e = (CDORemoteSessionEvent.SubscriptionChanged)event;
          if (e.isSubscribed())
          {
            result1.setValue(e.getRemoteSession().getSessionID());
          }
          else
          {
            result1.setValue(-1);
          }
        }
      }
    });

    CDOSession session2 = openSession();
    session2.getRemoteSessionManager().setForceSubscription(true);

    assertEquals(session2.getSessionID(), (int)result1.getValue());
    session2.close();
    session1.close();
  }

  public void testSubscribeByListen() throws Exception
  {
    final AsyncResult<Integer> result1 = new AsyncResult<Integer>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORemoteSessionEvent.SubscriptionChanged)
        {
          CDORemoteSessionEvent.SubscriptionChanged e = (CDORemoteSessionEvent.SubscriptionChanged)event;
          if (e.isSubscribed())
          {
            result1.setValue(e.getRemoteSession().getSessionID());
          }
          else
          {
            result1.setValue(-1);
          }
        }
      }
    });

    CDOSession session2 = openSession();
    session2.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
      }
    });

    assertEquals(session2.getSessionID(), (int)result1.getValue());
    session2.close();
    session1.close();
  }

  public void testUnsubscribeByForce() throws Exception
  {
    final AsyncResult<Integer> result1 = new AsyncResult<Integer>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORemoteSessionEvent.SubscriptionChanged)
        {
          CDORemoteSessionEvent.SubscriptionChanged e = (CDORemoteSessionEvent.SubscriptionChanged)event;
          if (!e.isSubscribed())
          {
            result1.setValue(e.getRemoteSession().getSessionID());
          }
        }
      }
    });

    CDOSession session2 = openSession();
    session2.getRemoteSessionManager().setForceSubscription(true);
    session2.getRemoteSessionManager().setForceSubscription(false);

    assertEquals(session2.getSessionID(), (int)result1.getValue());
    session2.close();
    session1.close();
  }

  public void testUnsubscribeByListen() throws Exception
  {
    final AsyncResult<Integer> result1 = new AsyncResult<Integer>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORemoteSessionEvent.SubscriptionChanged)
        {
          CDORemoteSessionEvent.SubscriptionChanged e = (CDORemoteSessionEvent.SubscriptionChanged)event;
          if (!e.isSubscribed())
          {
            result1.setValue(e.getRemoteSession().getSessionID());
          }
        }
      }
    });

    CDOSession session2 = openSession();
    IListener listener = new IListener()
    {
      public void notifyEvent(IEvent event)
      {
      }
    };

    session2.getRemoteSessionManager().addListener(listener);
    session2.getRemoteSessionManager().removeListener(listener);

    assertEquals(session2.getSessionID(), (int)result1.getValue());
    session2.close();
    session1.close();
  }

  public void testCustomData() throws Exception
  {
    final AsyncResult<byte[]> result1 = new AsyncResult<byte[]>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORemoteSessionEvent.CustomData)
        {
          CDORemoteSessionEvent.CustomData e = (CDORemoteSessionEvent.CustomData)event;
          result1.setValue(e.getData());
        }
      }
    });

    CDOSession session2 = openSession();
    session2.getRemoteSessionManager().setForceSubscription(true);

    byte[] data = "This is a custom data test".getBytes();
    CDORemoteSession remoteSession = session2.getRemoteSessionManager().getRemoteSessions()[0];
    remoteSession.sendCustomData("type", data);

    assertEquals(true, Arrays.equals(data, result1.getValue()));
    session2.close();
    session1.close();
  }

  public void testCustomDataLocallyUnsubscribed() throws Exception
  {
    final AsyncResult<byte[]> result1 = new AsyncResult<byte[]>();

    CDOSession session1 = openSession();
    session1.getRemoteSessionManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORemoteSessionEvent.CustomData)
        {
          CDORemoteSessionEvent.CustomData e = (CDORemoteSessionEvent.CustomData)event;
          result1.setValue(e.getData());
        }
      }
    });

    CDOSession session2 = openSession();

    byte[] data = "This is a custom data test".getBytes();
    CDORemoteSession remoteSession = session2.getRemoteSessionManager().getRemoteSessions()[0];

    try
    {
      remoteSession.sendCustomData("type", data);
      fail("CDOException expected");
    }
    catch (CDOException expected)
    {
    }

    session2.close();
    session1.close();
  }

  public void testCustomDataRemotelyUnsubscribed() throws Exception
  {
    CDOSession session1 = openSession();

    CDOSession session2 = openSession();
    session2.getRemoteSessionManager().setForceSubscription(true);

    byte[] data = "This is a custom data test".getBytes();
    CDORemoteSession remoteSession = session2.getRemoteSessionManager().getRemoteSessions()[0];

    try
    {
      remoteSession.sendCustomData("type", data);
      fail("CDOException expected");
    }
    catch (CDOException expected)
    {
    }

    session2.close();
    session1.close();
  };
}
