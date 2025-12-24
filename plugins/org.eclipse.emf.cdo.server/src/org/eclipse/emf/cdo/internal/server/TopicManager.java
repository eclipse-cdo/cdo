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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.ITopic;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTopic;
import org.eclipse.emf.cdo.spi.server.InternalTopicManager;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class TopicManager extends Container<ITopic> implements InternalTopicManager
{
  private final InternalSessionManager sessionManager;

  private final Map<String, InternalTopic> topics = new HashMap<>();

  public TopicManager(InternalSessionManager sessionManager)
  {
    this.sessionManager = sessionManager;
  }

  @Override
  public InternalSessionManager getSessionManager()
  {
    return sessionManager;
  }

  @Override
  public InternalTopic getTopic(String id)
  {
    return getTopic(id, false);
  }

  @Override
  public InternalTopic getTopic(String id, boolean createOnDemand)
  {
    InternalTopic topic;
    boolean added = false;

    synchronized (topics)
    {
      topic = topics.get(id);
      if (topic == null && createOnDemand)
      {
        topic = new Topic(this, id);
        LifecycleUtil.activate(topic);
        topics.put(id, topic);
        added = true;
      }
    }

    if (added)
    {
      fireElementAddedEvent(topic);
    }

    return topic;
  }

  @Override
  public InternalTopic[] getTopics()
  {
    synchronized (topics)
    {
      return topics.values().toArray(new InternalTopic[topics.size()]);
    }
  }

  @Override
  public InternalTopic[] getElements()
  {
    return getTopics();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (topics)
    {
      return topics.isEmpty();
    }
  }

  @Override
  public InternalSession[] addSubscription(String id, InternalSession session)
  {
    InternalTopic topic = getTopic(id, true);
    InternalSession[] result = topic.addSession(session);
    sessionManager.sendRemoteSessionNotification(session, null, topic, CDOProtocolConstants.REMOTE_SESSION_SUBSCRIBED);
    return result;
  }

  @Override
  public void removeSubscription(String id, InternalSession session)
  {
    InternalTopic topic = getTopic(id, false);
    if (topic != null)
    {
      if (topic.removeSession(session))
      {
        sessionManager.sendRemoteSessionNotification(session, null, topic, CDOProtocolConstants.REMOTE_SESSION_UNSUBSCRIBED);
      }
    }
  }

  @Override
  public void removeTopic(InternalTopic topic)
  {
    InternalTopic removedTopic;
    String id = topic.getID();

    synchronized (topics)
    {
      removedTopic = topics.remove(id);

      if (removedTopic != null && removedTopic != topic)
      {
        // A new topic was registered under the same id, put it back.
        topics.put(id, topic);
        removedTopic = null;
      }
    }

    if (removedTopic != null)
    {
      LifecycleUtil.deactivate(removedTopic);
      fireElementRemovedEvent(removedTopic);
    }
  }

  @Override
  public List<InternalTopic> sessionClosed(InternalSession session)
  {
    List<InternalTopic> affectedTopics = new ArrayList<>();

    synchronized (topics)
    {
      for (InternalTopic topic : topics.values())
      {
        if (topic.removeSession(session))
        {
          affectedTopics.add(topic);
        }
      }
    }

    return affectedTopics;
  }
}
