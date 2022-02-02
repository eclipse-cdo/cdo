/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTopic;
import org.eclipse.emf.cdo.spi.server.InternalTopicManager;

import org.eclipse.net4j.util.container.Container;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Topic extends Container<ISession> implements InternalTopic
{
  private final InternalTopicManager manager;

  private final Set<InternalSession> sessions = new HashSet<>();

  private final String id;

  public Topic(InternalTopicManager manager, String id)
  {
    this.manager = manager;
    this.id = id;
  }

  @Override
  public InternalTopicManager getManager()
  {
    return manager;
  }

  @Override
  public String getID()
  {
    return id;
  }

  @Override
  public boolean containsSession(InternalSession session)
  {
    synchronized (sessions)
    {
      return sessions.contains(session);
    }
  }

  @Override
  public InternalSession[] getSessions()
  {
    synchronized (sessions)
    {
      return sessions.toArray(new InternalSession[sessions.size()]);
    }
  }

  @Override
  public InternalSession[] getElements()
  {
    return getSessions();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (sessions)
    {
      return sessions.isEmpty();
    }
  }

  @Override
  public InternalSession[] addSession(InternalSession session)
  {
    InternalSession[] otherSessions;
    boolean added;

    synchronized (sessions)
    {
      otherSessions = sessions.toArray(new InternalSession[sessions.size()]);
      added = sessions.add(session);
    }

    if (added)
    {
      fireElementAddedEvent(session);
    }

    return otherSessions;
  }

  @Override
  public boolean removeSession(InternalSession session)
  {
    boolean lastSession;
    boolean removed;

    synchronized (sessions)
    {
      removed = sessions.remove(session);
      lastSession = sessions.isEmpty();
    }

    if (removed)
    {
      fireElementRemovedEvent(session);
    }

    if (lastSession)
    {
      manager.removeTopic(this);
    }

    return removed;
  }

  @Override
  public String toString()
  {
    return "Topic[" + id + "]";
  }
}
