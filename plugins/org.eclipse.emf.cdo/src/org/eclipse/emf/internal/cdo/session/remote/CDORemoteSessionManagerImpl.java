/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.util.event.INotifier;

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

  public CDORemoteSessionManagerImpl(InternalCDOSession localSession)
  {
    this.localSession = localSession;
  }

  public InternalCDOSession getLocalSession()
  {
    return localSession;
  }

  public synchronized CDORemoteSession[] getRemoteSessions()
  {
    Collection<CDORemoteSession> remoteSessions;
    if (subscribed)
    {
      remoteSessions = this.remoteSessions.values();
    }
    else
    {
      remoteSessions = localSession.getSessionProtocol().getRemoteSessions(this, false);
    }

    return remoteSessions.toArray(new CDORemoteSession[remoteSessions.size()]);
  }

  public CDORemoteSession[] getElements()
  {
    return getRemoteSessions();
  }

  public synchronized boolean isSubscribed()
  {
    return subscribed;
  }

  public synchronized boolean isForceSubscription()
  {
    return forceSubscription;
  }

  public synchronized void setForceSubscription(boolean forceSubscription)
  {
    this.forceSubscription = forceSubscription;
    if (forceSubscription && !subscribed)
    {
      IContainerEvent<CDORemoteSession> event = subscribe();
      // TODO don't fire inside sync block!
      fireEvent(event);
    }
  }

  public CDORemoteSession createRemoteSession(int sessionID, String userID, boolean subscribed)
  {
    CDORemoteSessionImpl remoteSession = new CDORemoteSessionImpl(this, sessionID, userID);
    remoteSession.setSubscribed(subscribed);
    return remoteSession;
  }

  public synchronized void handleRemoteSessionOpened(int sessionID, String userID)
  {
    CDORemoteSession remoteSession = createRemoteSession(sessionID, userID, false);
    remoteSessions.put(sessionID, remoteSession);
    // TODO don't fire inside sync block!
    fireElementAddedEvent(remoteSession);
  }

  public synchronized void handleRemoteSessionClosed(int sessionID)
  {
    CDORemoteSession remoteSession = remoteSessions.remove(sessionID);
    if (remoteSession != null)
    {
      // TODO don't fire inside sync block!
      fireElementRemovedEvent(remoteSession);
    }
  }

  public synchronized void handleRemoteSessionSubscribed(int sessionID, final boolean subscribed)
  {
    final CDORemoteSessionImpl remoteSession = (CDORemoteSessionImpl)remoteSessions.get(sessionID);
    if (remoteSession != null)
    {
      remoteSession.setSubscribed(subscribed);
      // TODO don't fire inside sync block!
      fireEvent(new CDORemoteSessionEvent.SubscriptionChanged()
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
      });
    }
  }

  @Override
  protected synchronized void listenersEmptyChanged(boolean empty)
  {
    if (empty)
    {
      if (!forceSubscription)
      {
        IContainerEvent<CDORemoteSession> event = unsubscribe();
        // TODO don't fire inside sync block!
        fireEvent(event);
      }
    }
    else
    {
      if (!subscribed)
      {
        IContainerEvent<CDORemoteSession> event = subscribe();
        // TODO don't fire inside sync block!
        fireEvent(event);
      }
    }
  }

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
