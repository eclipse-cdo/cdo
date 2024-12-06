/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.collection.ConcurrentArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link HashSet hash set}-based {@link CDOTopicProvider topic provider}.
 *
 * @author Eike Stepper
 * @since 4.17
 */
public class DefaultTopicProvider implements CDOTopicProvider
{
  private final ConcurrentArray<Listener> listeners = new ConcurrentArray<>()
  {
    @Override
    protected Listener[] newArray(int length)
    {
      return new Listener[length];
    }
  };

  private final Set<Topic> topics = new HashSet<>();

  public DefaultTopicProvider()
  {
  }

  @Override
  public Topic[] getTopics()
  {
    synchronized (topics)
    {
      return topics.toArray(new Topic[topics.size()]);
    }
  }

  public boolean setTopics(Topic... topics)
  {
    return setTopics(Arrays.asList(topics));
  }

  public boolean setTopics(Collection<Topic> topics)
  {
    List<Topic> removed = null;
    List<Topic> added = null;

    synchronized (topics)
    {
      for (Iterator<Topic> it = this.topics.iterator(); it.hasNext();)
      {
        Topic existingTopic = it.next();
        if (!topics.contains(existingTopic))
        {
          it.remove();

          if (removed == null)
          {
            removed = new ArrayList<>();
          }

          removed.add(existingTopic);
        }
      }

      for (Topic topic : topics)
      {
        if (this.topics.add(topic))
        {
          if (added == null)
          {
            added = new ArrayList<>();
          }

          added.add(topic);
        }
      }
    }

    boolean changed = false;

    if (removed != null)
    {
      changed = true;

      for (Topic topic : removed)
      {
        forEachTopicListener(l -> l.topicRemoved(this, topic));
      }
    }

    if (added != null)
    {
      changed = true;

      for (Topic topic : added)
      {
        forEachTopicListener(l -> l.topicAdded(this, topic));
      }
    }

    return changed;
  }

  public boolean addTopic(Topic topic)
  {
    boolean added;
    synchronized (topics)
    {
      added = topics.add(topic);
    }

    if (added)
    {
      forEachTopicListener(l -> l.topicAdded(this, topic));
      return true;
    }

    return false;
  }

  public boolean removeTopic(Topic topic)
  {
    boolean removed;
    synchronized (topics)
    {
      removed = topics.remove(topic);
    }

    if (removed)
    {
      forEachTopicListener(l -> l.topicRemoved(this, topic));
      return true;
    }

    return false;
  }

  @Override
  public void addTopicListener(Listener listener)
  {
    listeners.add(listener);
  }

  @Override
  public void removeTopicListener(Listener listener)
  {
    listeners.remove(listener);
  }

  private void forEachTopicListener(Consumer<Listener> handler)
  {
    for (Listener listener : listeners.get())
    {
      try
      {
        handler.accept(listener);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}
