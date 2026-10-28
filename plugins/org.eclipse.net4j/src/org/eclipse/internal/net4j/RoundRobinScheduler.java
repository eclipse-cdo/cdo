/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.internal.net4j;

import org.eclipse.net4j.buffer.IBuffer;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.SchedulingConnector.IScheduler;

import java.util.LinkedHashSet;
import java.util.Queue;

/**
 * Selects queued channels in insertion order and rotates only after a complete buffer has been transmitted. The set monitor protects only
 * scheduler state. Channel queues are inspected before acquiring that monitor, so producers can enqueue and notify without a lock cycle.
 *
 * @author Eike Stepper
 */
public class RoundRobinScheduler implements IScheduler
{
  private final LinkedHashSet<InternalChannel> channels = new LinkedHashSet<>();

  private InternalChannel current;

  public RoundRobinScheduler()
  {
  }

  @Override
  public boolean offer(InternalChannel channel)
  {
    if (channel == null)
    {
      return false;
    }

    Queue<IBuffer> queue = channel.getSendQueue();
    if (queue == null)
    {
      return false;
    }

    synchronized (queue)
    {
      if (!channel.isActive() || channel.getSendQueue() != queue || queue.isEmpty())
      {
        return false;
      }

      synchronized (channels)
      {
        if (!channel.isActive() || channel.getSendQueue() != queue || queue.isEmpty())
        {
          return false;
        }

        boolean wasEmpty = channels.isEmpty();
        return channels.add(channel) && wasEmpty;
      }
    }
  }

  @Override
  public InternalChannel select()
  {
    for (;;)
    {
      InternalChannel selected;

      synchronized (channels)
      {
        if (current == null)
        {
          current = channels.isEmpty() ? null : channels.iterator().next();
        }

        selected = current;
      }

      if (selected == null)
      {
        return null;
      }

      if (removeIfEmpty(selected))
      {
        continue;
      }

      synchronized (channels)
      {
        if (current == selected && channels.contains(selected))
        {
          return selected;
        }
      }
    }
  }

  @Override
  public boolean complete(InternalChannel channel, boolean bufferCompleted)
  {
    Queue<IBuffer> queue = channel == null ? null : channel.getSendQueue();
    if (queue == null)
    {
      return false;
    }

    synchronized (queue)
    {
      synchronized (channels)
      {
        if (current != channel || !channels.contains(channel))
        {
          return false;
        }

        if (!bufferCompleted)
        {
          return true;
        }

        current = null;
        channels.remove(channel);

        if (channel.isActive() && channel.getSendQueue() == queue && !queue.isEmpty())
        {
          channels.add(channel);
        }

        return !channels.isEmpty();
      }
    }
  }

  @Override
  public boolean remove(InternalChannel channel)
  {
    synchronized (channels)
    {
      boolean wasCurrent = current == channel;

      if (!channels.remove(channel))
      {
        return false;
      }

      if (wasCurrent)
      {
        current = null;
      }

      return wasCurrent && !channels.isEmpty();
    }
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (channels)
    {
      return channels.isEmpty();
    }
  }

  @Override
  public void clear()
  {
    synchronized (channels)
    {
      channels.clear();
      current = null;
    }
  }

  private boolean removeIfEmpty(InternalChannel channel)
  {
    Queue<IBuffer> queue = channel.getSendQueue();
    if (queue == null)
    {
      synchronized (channels)
      {
        if (current == channel)
        {
          channels.remove(channel);
          current = null;
        }
      }

      return true;
    }

    synchronized (queue)
    {
      if (channel.isActive() && channel.getSendQueue() == queue && !queue.isEmpty())
      {
        return false;
      }

      synchronized (channels)
      {
        if (current == channel)
        {
          channels.remove(channel);
          current = null;
        }
      }

      return true;
    }
  }
}
