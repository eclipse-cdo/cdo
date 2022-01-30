/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.net4j.util.collection.ArrayIterator;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSession;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDORemoteTopic;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionManagerImpl extends Container<CDORemoteSession> implements InternalCDORemoteSessionManager
{
  private static final InternalCDORemoteSession[] NO_REMOTE_SESSIONS = {};

  private InternalCDOSession localSession;

  private boolean forceSubscription;

  private boolean subscribed;

  private final Map<Integer, InternalCDORemoteSession> remoteSessions = new HashMap<>();

  private final Map<String, InternalCDORemoteTopic> remoteTopics = new HashMap<>();

  public CDORemoteSessionManagerImpl()
  {
  }

  @Override
  public InternalCDOSession getLocalSession()
  {
    return localSession;
  }

  @Override
  public void setLocalSession(InternalCDOSession localSession)
  {
    this.localSession = localSession;
  }

  @Override
  public InternalCDORemoteSession getRemoteSession(int sessionID)
  {
    synchronized (this)
    {
      return remoteSessions.get(sessionID);
    }
  }

  @Override
  public InternalCDORemoteSession[] getRemoteSessions()
  {
    synchronized (this)
    {
      if (localSession.isActive())
      {
        if (subscribed)
        {
          Collection<InternalCDORemoteSession> values = remoteSessions.values();
          return values.toArray(new InternalCDORemoteSession[values.size()]);
        }

        List<CDORemoteSession> loadedRemoteSessions = localSession.getSessionProtocol().getRemoteSessions(this, false);
        return loadedRemoteSessions.toArray(new InternalCDORemoteSession[loadedRemoteSessions.size()]);
      }

      return NO_REMOTE_SESSIONS;
    }
  }

  @Override
  public CDORemoteSession[] getElements()
  {
    return getRemoteSessions();
  }

  @Override
  public boolean isSubscribed()
  {
    synchronized (this)
    {
      return subscribed;
    }
  }

  @Override
  public boolean isForceSubscription()
  {
    synchronized (this)
    {
      return forceSubscription;
    }
  }

  @Override
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
        if (!hasListeners() && remoteTopics.isEmpty())
        {
          events = unsubscribe();
        }
      }
    }

    fireEvents(events);
  }

  @Override
  public Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message, CDORemoteSession... recipients)
  {
    return sendMessage(message, new ArrayIterator<>(recipients));
  }

  @Override
  public Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message, Collection<CDORemoteSession> recipients)
  {
    return sendMessage(message, recipients.iterator());
  }

  private Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message, Iterator<CDORemoteSession> recipients)
  {
    List<CDORemoteSession> subscribedSessions = new ArrayList<>();
    while (recipients.hasNext())
    {
      CDORemoteSession recipient = recipients.next();
      if (recipient.isSubscribed())
      {
        subscribedSessions.add(recipient);
      }
    }

    if (subscribedSessions.isEmpty())
    {
      return Collections.emptySet();
    }

    Set<Integer> sessionIDs = localSession.getSessionProtocol().sendRemoteMessage(message, null, subscribedSessions);
    Set<CDORemoteSession> result = new HashSet<>();
    
    for (CDORemoteSession recipient : subscribedSessions)
    {
      if (sessionIDs.contains(recipient.getSessionID()))
      {
        result.add(recipient);
      }
    }

    return result;
  }

  @Override
  public InternalCDORemoteTopic subscribeTopic(String id)
  {
    InternalCDORemoteTopic remoteTopic;
    IEvent[] events = null;

    synchronized (this)
    {
      remoteTopic = remoteTopics.get(id);
      if (remoteTopic == null)
      {
        if (!subscribed)
        {
          events = subscribe();
        }

        Set<Integer> remoteSessionIDs = localSession.getSessionProtocol().subscribeRemoteTopic(id, true);
        remoteTopic = new CDORemoteTopicImpl(this, id, remoteSessionIDs);
        remoteTopics.put(id, remoteTopic);
      }
    }

    fireEvents(events);
    return remoteTopic;
  }

  @Override
  public void unsubscribeTopic(InternalCDORemoteTopic remoteTopic)
  {
    String id = remoteTopic.getID();
    IEvent[] events = null;

    synchronized (this)
    {
      localSession.getSessionProtocol().subscribeRemoteTopic(id, false);
      remoteTopics.remove(id);

      if (!forceSubscription && remoteTopics.isEmpty() && !hasListeners())
      {
        events = unsubscribe();
      }
    }

    fireEvents(events);
  }

  @Override
  public InternalCDORemoteTopic[] getSubscribedTopics()
  {
    synchronized (this)
    {
      return remoteTopics.values().toArray(new InternalCDORemoteTopic[remoteTopics.size()]);
    }
  }

  @Override
  public InternalCDORemoteTopic getSubscribedTopic(String id)
  {
    synchronized (this)
    {
      return remoteTopics.get(id);
    }
  }

  @Override
  public InternalCDORemoteSession createRemoteSession(int sessionID, String userID, boolean subscribed)
  {
    InternalCDORemoteSession remoteSession = new CDORemoteSessionImpl(this, sessionID, userID);
    remoteSession.setSubscribed(subscribed);
    return remoteSession;
  }

  @Override
  public void handleRemoteSessionOpened(int sessionID, String userID)
  {
    InternalCDORemoteSession remoteSession = createRemoteSession(sessionID, userID, false);
    synchronized (this)
    {
      remoteSessions.put(sessionID, remoteSession);
    }

    fireElementAddedEvent(remoteSession);
  }

  @Override
  public void handleRemoteSessionClosed(int sessionID)
  {
    for (InternalCDORemoteTopic remoteTopic : getSubscribedTopics())
    {
      remoteTopic.handleRemoteSessionSubscribed(sessionID, false);
    }

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

  @Override
  @Deprecated
  public void handleRemoteSessionSubscribed(int sessionID, boolean subscribed)
  {
    handleRemoteSessionSubscribed(sessionID, null, subscribed);
  }

  @Override
  public void handleRemoteSessionSubscribed(int sessionID, String topicID, boolean subscribed)
  {
    if (topicID != null)
    {
      InternalCDORemoteTopic remoteTopic = getSubscribedTopic(topicID);
      if (remoteTopic != null)
      {
        remoteTopic.handleRemoteSessionSubscribed(sessionID, subscribed);
      }
    }
    else
    {
      IEvent event = null;
      synchronized (this)
      {
        InternalCDORemoteSession remoteSession = remoteSessions.get(sessionID);
        if (remoteSession != null)
        {
          remoteSession.setSubscribed(subscribed);
          event = new SubscriptionChangedEventImpl(remoteSession, subscribed);
        }
      }

      if (event != null)
      {
        fireEvent(event);
      }
    }
  }

  @Override
  @Deprecated
  public void handleRemoteSessionMessage(int sessionID, CDORemoteSessionMessage message)
  {
    handleRemoteSessionMessage(sessionID, null, message);
  }

  @Override
  public void handleRemoteSessionMessage(int sessionID, String topicID, final CDORemoteSessionMessage message)
  {
    InternalCDORemoteSession remoteSession = remoteSessions.get(sessionID);
    if (remoteSession != null)
    {
      if (topicID != null)
      {
        InternalCDORemoteTopic topic = getSubscribedTopic(topicID);
        if (topic != null)
        {
          topic.handleRemoteSessionMessage(remoteSession, message);
        }
      }
      else
      {
        fireEvent(new MessageReceivedImpl(remoteSession, message));
      }
    }
  }

  @Override
  protected void firstListenerAdded()
  {
    IEvent[] events = null;
    synchronized (this)
    {
      if (!subscribed)
      {
        events = subscribe();
      }
    }

    fireEvents(events);
  }

  @Override
  protected void lastListenerRemoved()
  {
    IEvent[] events = null;
    synchronized (this)
    {
      if (!forceSubscription && remoteTopics.isEmpty())
      {
        events = unsubscribe();
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
    ContainerEvent<CDORemoteSession> event = new ContainerEvent<>(this);
    for (CDORemoteSession remoteSession : result)
    {
      remoteSessions.put(remoteSession.getSessionID(), (InternalCDORemoteSession)remoteSession);
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
    if (localSession.isActive())
    {
      localSession.getSessionProtocol().unsubscribeRemoteSessions();
    }

    ContainerEvent<CDORemoteSession> event = new ContainerEvent<>(this);
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

    private final boolean subscribed;

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

    @Override
    public boolean isSubscribed()
    {
      return subscribed;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SubscriptionChangedEventImpl extends Event implements CDORemoteSessionEvent.SubscriptionChanged
  {
    private static final long serialVersionUID = 1L;

    private final InternalCDORemoteSession remoteSession;

    private final boolean subscribed;

    public SubscriptionChangedEventImpl(InternalCDORemoteSession remoteSession, boolean subscribed)
    {
      super(CDORemoteSessionManagerImpl.this);
      this.remoteSession = remoteSession;
      this.subscribed = subscribed;
    }

    @Override
    public CDORemoteSessionManager getSource()
    {
      return (CDORemoteSessionManager)super.getSource();
    }

    @Override
    public CDORemoteSession getRemoteSession()
    {
      return remoteSession;
    }

    @Override
    public boolean isSubscribed()
    {
      return subscribed;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class MessageReceivedImpl extends Event implements CDORemoteSessionEvent.MessageReceived
  {
    private static final long serialVersionUID = 1L;

    private final InternalCDORemoteSession remoteSession;

    private final CDORemoteSessionMessage message;

    public MessageReceivedImpl(InternalCDORemoteSession remoteSession, CDORemoteSessionMessage message)
    {
      super(CDORemoteSessionManagerImpl.this);
      this.remoteSession = remoteSession;
      this.message = message;
    }

    @Override
    public CDORemoteSessionManager getSource()
    {
      return (CDORemoteSessionManager)super.getSource();
    }

    @Override
    public CDORemoteSession getRemoteSession()
    {
      return remoteSession;
    }

    @Override
    public CDORemoteSessionMessage getMessage()
    {
      return message;
    }
  }
}
