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

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

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

  public synchronized CDORemoteSession[] getRemoteSessions()
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
    IContainerEvent<CDORemoteSession> event = null;
    synchronized (this)
    {
      this.forceSubscription = forceSubscription;
      if (forceSubscription)
      {
        if (!subscribed)
        {
          event = subscribe();
        }
      }
      else
      {
        if (!hasListeners())
        {
          event = unsubscribe();
        }
      }
    }

    if (event != null)
    {
      fireEvent(event);
    }
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
      final InternalCDORemoteSession remoteSession = (InternalCDORemoteSession)remoteSessions.get(sessionID);
      if (remoteSession != null)
      {
        remoteSession.setSubscribed(subscribed);
        event = new CDORemoteSessionEvent.SubscriptionChanged()
        {
          public INotifier getSource()
          {
            return CDORemoteSessionManagerImpl.this;
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

  @Override
  protected void listenersEmptyChanged(boolean empty)
  {
    IContainerEvent<CDORemoteSession> event = null;
    synchronized (this)
    {
      if (empty)
      {
        if (!forceSubscription)
        {
          event = unsubscribe();
        }
      }
      else
      {
        if (!subscribed)
        {
          event = subscribe();
        }
      }
    }

    if (event != null)
    {
      fireEvent(event);
    }
  }

  /**
   * Needs to be synchronized externally.
   */
  private IContainerEvent<CDORemoteSession> subscribe()
  {
    List<CDORemoteSession> result = localSession.getSessionProtocol().getRemoteSessions(this, true);
    ContainerEvent<CDORemoteSession> event = new ContainerEvent<CDORemoteSession>(this);
    for (CDORemoteSession remoteSession : result)
    {
      remoteSessions.put(remoteSession.getSessionID(), remoteSession);
      event.addDelta(remoteSession, IContainerDelta.Kind.ADDED);
    }

    subscribed = true;
    return event;
  }

  /**
   * Needs to be synchronized externally.
   */
  private IContainerEvent<CDORemoteSession> unsubscribe()
  {
    localSession.getSessionProtocol().unsubscribeRemoteSessions();
    ContainerEvent<CDORemoteSession> event = new ContainerEvent<CDORemoteSession>(this);
    for (CDORemoteSession remoteSession : remoteSessions.values())
    {
      event.addDelta(remoteSession, IContainerDelta.Kind.REMOVED);
    }

    remoteSessions.clear();
    subscribed = false;
    return event;
  }
}
