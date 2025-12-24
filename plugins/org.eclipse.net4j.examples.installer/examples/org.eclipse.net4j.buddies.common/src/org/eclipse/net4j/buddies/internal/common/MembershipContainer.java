/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.common.IMembershipContainer;
import org.eclipse.net4j.buddies.common.IMembershipKey;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class MembershipContainer extends Lifecycle implements IMembershipContainer, IListener
{
  private ConcurrentMap<IMembershipKey, IMembership> memberships = new ConcurrentHashMap<>();

  public MembershipContainer()
  {
  }

  public void addMembership(IMembership membership)
  {
    if (memberships.putIfAbsent(membership, membership) == null)
    {
      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new SingleDeltaContainerEvent<>(this, membership, IContainerDelta.Kind.ADDED), listeners);
      }

      membership.addListener(this);
    }
  }

  public IMembership removeMembership(IBuddy buddy, ICollaboration collaboration)
  {
    return removeMembership(new MembershipKey(buddy, collaboration));
  }

  public IMembership removeMembership(IMembershipKey key)
  {
    IMembership membership = memberships.remove(key);
    if (membership != null)
    {
      membership.removeListener(this);
      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new SingleDeltaContainerEvent<>(this, membership, IContainerDelta.Kind.REMOVED), listeners);
      }
    }

    return membership;
  }

  @Override
  public IMembership[] getMemberships()
  {
    return memberships.values().toArray(new IMembership[memberships.size()]);
  }

  @Override
  public IMembership getMembership(IBuddy buddy, ICollaboration collaboration)
  {
    return memberships.get(new MembershipKey(buddy, collaboration));
  }

  @Override
  public IMembership[] getElements()
  {
    return getMemberships();
  }

  @Override
  public boolean isEmpty()
  {
    return memberships.isEmpty();
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event.getSource() instanceof IMembership)
    {
      IMembership membership = (IMembership)event.getSource();
      notifyMembershipEvent(event);
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          removeMembership(membership);
        }
      }
    }
  }

  protected void notifyMembershipEvent(IEvent event)
  {
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IMembership membership : getMemberships())
    {
      membership.removeListener(this);
    }

    super.doDeactivate();
  }
}
