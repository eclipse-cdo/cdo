/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common;

import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.ICollaborationContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CollaborationContainer extends Lifecycle implements ICollaborationContainer, IListener
{
  private Map<Long, ICollaboration> collaborations = new HashMap<>();

  public CollaborationContainer(Collection<ICollaboration> collaborations)
  {
    if (collaborations != null)
    {
      for (ICollaboration collaboration : collaborations)
      {
        this.collaborations.put(collaboration.getID(), collaboration);
        collaboration.addListener(this);
      }
    }
  }

  public CollaborationContainer()
  {
  }

  public void addCollaboration(ICollaboration collaboration)
  {
    long id = collaboration.getID();
    synchronized (collaborations)
    {
      if (!collaborations.containsKey(id))
      {
        collaborations.put(id, collaboration);
      }
    }

    IListener[] listeners = getListeners();
    if (listeners.length != 0)
    {
      fireEvent(new SingleDeltaContainerEvent<>(this, collaboration, IContainerDelta.Kind.ADDED), listeners);
    }

    collaboration.addListener(this);
  }

  public ICollaboration removeCollaboration(long id)
  {
    ICollaboration collaboration;
    synchronized (collaborations)
    {
      collaboration = collaborations.remove(id);
    }

    if (collaboration != null)
    {
      collaboration.removeListener(this);
      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new SingleDeltaContainerEvent<>(this, collaboration, IContainerDelta.Kind.REMOVED), listeners);
      }
    }

    return collaboration;
  }

  @Override
  public ICollaboration[] getCollaborations()
  {
    synchronized (collaborations)
    {
      return collaborations.values().toArray(new ICollaboration[collaborations.size()]);
    }
  }

  @Override
  public ICollaboration getCollaboration(long id)
  {
    synchronized (collaborations)
    {
      return collaborations.get(id);
    }
  }

  @Override
  public ICollaboration[] getElements()
  {
    return getCollaborations();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (collaborations)
    {
      return collaborations.isEmpty();
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event.getSource() instanceof ICollaboration)
    {
      notifyCollaborationEvent(event);
      if (event instanceof LifecycleEvent)
      {
        LifecycleEvent e = (LifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          removeCollaboration(((ICollaboration)e.getSource()).getID());
        }
      }
    }
  }

  protected void notifyCollaborationEvent(IEvent event)
  {
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (ICollaboration collaboration : getCollaborations())
    {
      collaboration.removeListener(this);
    }

    super.doDeactivate();
  }
}
