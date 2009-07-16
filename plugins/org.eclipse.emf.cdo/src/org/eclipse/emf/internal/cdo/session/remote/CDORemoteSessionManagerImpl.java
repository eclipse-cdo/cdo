/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session.remote;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSession;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionManagerImpl extends Container<CDORemoteSession> implements InternalCDORemoteSessionManager
{
  private InternalCDOSession localSession;

  private boolean forceSubscription;

  private boolean subscribed;

  private Map<Integer, CDORemoteSession> remoteSessions = new HashMap<Integer, CDORemoteSession>();

  public CDORemoteSessionManagerImpl()
  {
  }

  public InternalCDOSession getLocalSession()
  {
    return localSession;
  }

  public void setLocalSession(InternalCDOSession localSession)
  {
    this.localSession = localSession;
  }

  public CDORemoteSession[] getRemoteSessions()
  {
    Collection<CDORemoteSession> remoteSessions;
    synchronized (this)
    {
      if (subscribed)
      {
        remoteSessions = this.remoteSessions.values();
      }
      else
      {
        remoteSessions = localSession.getSessionProtocol().getRemoteSessions(this, false);
      }
    }

    return remoteSessions.toArray(new CDORemoteSession[remoteSessions.size()]);
  }

  public CDORemoteSession[] getElements()
  {
    return getRemoteSessions();
  }

  public boolean isSubscribed()
  {
    synchronized (this)
    {
      return subscribed;
    }
  }

  public boolean isForceSubscription()
  {
    synchronized (this)
    {
      return forceSubscription;
    }
  }

  public void setForceSubscription(boolean forceSubscription)
  {
    IEvent[] events = null;
    synchronized (this)
    {
      this.forceSubscription = forceSubscription;
      if (forceSubscription)
      {
        if (!subscribed)
        {
          events = subscribe();
        }
      }
      else
      {
        if (!hasListeners())
        {
          events = unsubscribe();
        }
      }
    }

    fireEvents(events);
  }

  public InternalCDORemoteSession createRemoteSession(int sessionID, String userID, boolean subscribed)
  {
    InternalCDORemoteSession remoteSession = new CDORemoteSessionImpl(this, sessionID, userID);
    remoteSession.setSubscribed(subscribed);
    return remoteSession;
  }

  public void handleRemoteSessionOpened(int sessionID, String userID)
  {
    CDORemoteSession remoteSession = createRemoteSession(sessionID, userID, false);
    synchronized (this)
    {
      remoteSessions.put(sessionID, remoteSession);
    }

    fireElementAddedEvent(remoteSession);
  }

  public void handleRemoteSessionClosed(int sessionID)
  {
    CDORemoteSession remoteSession = null;
    synchronized (this)
    {
      remoteSession = remoteSessions.remove(sessionID);
    }

    if (remoteSession != null)
    {
      fireElementRemovedEvent(remoteSession);
    }
  }

  public void handleRemoteSessionSubscribed(int sessionID, final boolean subscribed)
  {
    IEvent event = null;
    synchronized (this)
    {
      final CDORemoteSessionManager source = this;
      final InternalCDORemoteSession remoteSession = (InternalCDORemoteSession)remoteSessions.get(sessionID);
      if (remoteSession != null)
      {
        remoteSession.setSubscribed(subscribed);
        event = new CDORemoteSessionEvent.SubscriptionChanged()
        {
          public CDORemoteSessionManager getSource()
          {
            return source;
          }

          public CDORemoteSession getRemoteSession()
          {
            return remoteSession;
          }

          public boolean isSubscribed()
          {
            return subscribed;
          }
        };
      }
    }

    if (event != null)
    {
      fireEvent(event);
    }
  }

  public void handleRemoteSessionCustomData(int sessionID, final String type, final byte[] data)
  {
    IEvent event = null;
    synchronized (this)
    {
      final CDORemoteSessionManager source = this;
      final InternalCDORemoteSession remoteSession = (InternalCDORemoteSession)remoteSessions.get(sessionID);
      if (remoteSession != null)
      {
        event = new CDORemoteSessionEvent.CustomData()
        {
          public CDORemoteSessionManager getSource()
          {
            return source;
          }

          public CDORemoteSession getRemoteSession()
          {
            return remoteSession;
          }

          public String getType()
          {
            return type;
          }

          public byte[] getData()
          {
            return data;
          }
        };
      }
    }

    if (event != null)
    {
      fireEvent(event);
    }
  }

  @Override
  protected void listenersEmptyChanged(boolean empty)
  {
    IEvent[] events = null;
    synchronized (this)
    {
      if (empty)
      {
        if (!forceSubscription)
        {
          events = unsubscribe();
        }
      }
      else
      {
        if (!subscribed)
        {
          events = subscribe();
        }
      }
    }

    fireEvents(events);
  }

  /**
   * Needs to be synchronized externally.
   */
  private IEvent[] subscribe()
  {
    List<CDORemoteSession> result = localSession.getSessionProtocol().getRemoteSessions(this, true);
    ContainerEvent<CDORemoteSession> event = new ContainerEvent<CDORemoteSession>(this);
    for (CDORemoteSession remoteSession : result)
    {
      remoteSessions.put(remoteSession.getSessionID(), remoteSession);
      event.addDelta(remoteSession, IContainerDelta.Kind.ADDED);
    }

    subscribed = true;
    IEvent[] events = { new LocalSubscriptionChangedEventImpl(true), event.isEmpty() ? null : event };
    return events;
  }

  /**
   * Needs to be synchronized externally.
   */
  private IEvent[] unsubscribe()
  {
    localSession.getSessionProtocol().unsubscribeRemoteSessions();
    ContainerEvent<CDORemoteSession> event = new ContainerEvent<CDORemoteSession>(this);
    for (CDORemoteSession remoteSession : remoteSessions.values())
    {
      event.addDelta(remoteSession, IContainerDelta.Kind.REMOVED);
    }

    remoteSessions.clear();
    subscribed = false;
    IEvent[] events = { new LocalSubscriptionChangedEventImpl(false), event.isEmpty() ? null : event };
    return events;
  }

  private void fireEvents(IEvent[] events)
  {
    if (events != null)
    {
      for (int i = 0; i < events.length; i++)
      {
        IEvent event = events[i];
        if (event != null)
        {
          fireEvent(event);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LocalSubscriptionChangedEventImpl extends Event implements LocalSubscriptionChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private boolean subscribed;

    public LocalSubscriptionChangedEventImpl(boolean subscribed)
    {
      super(CDORemoteSessionManagerImpl.this);
      this.subscribed = subscribed;
    }

    @Override
    public CDORemoteSessionManager getSource()
    {
      return (CDORemoteSessionManager)super.getSource();
    }

    public boolean isSubscribed()
    {
      return subscribed;
    }
  }
}
