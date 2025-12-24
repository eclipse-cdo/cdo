/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session.remote;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopic;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopicEvent;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.Event;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSession;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDORemoteTopic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDORemoteTopicImpl extends Container<CDORemoteSession> implements InternalCDORemoteTopic
{
  private final InternalCDORemoteSessionManager manager;

  private final String id;

  private final Set<Integer> remoteSessionIDs;

  public CDORemoteTopicImpl(InternalCDORemoteSessionManager manager, String id, Set<Integer> remoteSessionIDs)
  {
    this.manager = manager;
    this.id = id;
    this.remoteSessionIDs = remoteSessionIDs;
  }

  @Override
  public InternalCDORemoteSessionManager getManager()
  {
    return manager;
  }

  @Override
  public String getID()
  {
    return id;
  }

  @Override
  public InternalCDORemoteSession[] getRemoteSessions()
  {
    List<CDORemoteSession> remoteSessions = getRemoteSessionsList();
    return remoteSessions.toArray(new InternalCDORemoteSession[remoteSessions.size()]);
  }

  private List<CDORemoteSession> getRemoteSessionsList()
  {
    List<CDORemoteSession> remoteSessions = new ArrayList<CDORemoteSession>();

    synchronized (remoteSessionIDs)
    {
      for (Integer remoteSessionID : remoteSessionIDs)
      {
        InternalCDORemoteSession remoteSession = manager.getRemoteSession(remoteSessionID);
        if (remoteSession != null)
        {
          remoteSessions.add(remoteSession);
        }
      }
    }

    return remoteSessions;
  }

  @Override
  public Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message)
  {
    List<CDORemoteSession> recipients = getRemoteSessionsList();
    CDOSessionProtocol sessionProtocol = manager.getLocalSession().getSessionProtocol();
    Set<Integer> sessionIDs = sessionProtocol.sendRemoteMessage(message, this, recipients);

    Set<CDORemoteSession> result = new HashSet<CDORemoteSession>();
    for (CDORemoteSession recipient : recipients)
    {
      if (sessionIDs.contains(recipient.getSessionID()))
      {
        result.add(recipient);
      }
    }

    return result;

  }

  @Override
  public CDORemoteSession[] getElements()
  {
    return getRemoteSessions();
  }

  @Override
  public boolean isSubscribed()
  {
    return manager.getSubscribedTopic(id) != null;
  }

  @Override
  public void unsubscribe()
  {
    manager.unsubscribeTopic(this);
  }

  @Override
  public void handleRemoteSessionSubscribed(int sessionID, boolean subscribed)
  {
    synchronized (remoteSessionIDs)
    {
      if (subscribed)
      {
        remoteSessionIDs.add(sessionID);
      }
      else
      {
        remoteSessionIDs.remove(sessionID);
      }
    }

    InternalCDORemoteSession remoteSession = manager.getRemoteSession(sessionID);
    if (remoteSession != null)
    {
      fireContainerEvent(remoteSession, subscribed ? IContainerDelta.Kind.ADDED : IContainerDelta.Kind.REMOVED);
    }
  }

  @Override
  public void handleRemoteSessionMessage(InternalCDORemoteSession remoteSession, CDORemoteSessionMessage message)
  {
    fireEvent(new MessageReceivedImpl(remoteSession, message));
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (id == null ? 0 : id.hashCode());
    result = prime * result + (manager == null ? 0 : manager.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    CDORemoteTopicImpl other = (CDORemoteTopicImpl)obj;
    if (id == null)
    {
      if (other.id != null)
      {
        return false;
      }
    }
    else if (!id.equals(other.id))
    {
      return false;
    }

    if (manager == null)
    {
      if (other.manager != null)
      {
        return false;
      }
    }
    else if (!manager.equals(other.manager))
    {
      return false;
    }

    return true;
  }

  @Override
  public int compareTo(CDORemoteTopic o)
  {
    return id.compareTo(o.getID());
  }

  @Override
  public String toString()
  {
    return id;
  }

  /**
   * @author Eike Stepper
   */
  private final class MessageReceivedImpl extends Event implements CDORemoteTopicEvent.MessageReceived
  {
    private static final long serialVersionUID = 1L;

    private final InternalCDORemoteSession remoteSession;

    private final CDORemoteSessionMessage message;

    public MessageReceivedImpl(InternalCDORemoteSession remoteSession, CDORemoteSessionMessage message)
    {
      super(CDORemoteTopicImpl.this);
      this.remoteSession = remoteSession;
      this.message = message;
    }

    @Override
    public CDORemoteTopic getSource()
    {
      return (CDORemoteTopic)super.getSource();
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
