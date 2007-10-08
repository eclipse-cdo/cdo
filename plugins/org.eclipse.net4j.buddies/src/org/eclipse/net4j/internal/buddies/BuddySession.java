/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.BuddyStateNotification;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.internal.buddies.bundle.OM;
import org.eclipse.net4j.internal.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class BuddySession extends Lifecycle implements IBuddySession, IListener
{
  private IChannel channel;

  private Self self;

  private Map<String, IBuddy> buddies = new HashMap<String, IBuddy>();

  public BuddySession(IChannel channel)
  {
    this.channel = channel;
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public Self getSelf()
  {
    return self;
  }

  public void setSelf(IBuddyAccount account)
  {
    self = new Self(this, account);
    self.addListener(this);
  }

  public Buddy addBuddy(String userID)
  {
    Buddy buddy = null;
    synchronized (buddies)
    {
      if (!buddies.containsKey(userID))
      {
        buddy = new Buddy(this, userID);
        buddies.put(userID, buddy);
      }
    }

    return buddy;
  }

  public Map<String, IBuddy> getBuddies()
  {
    synchronized (buddies)
    {
      return Collections.unmodifiableMap(buddies);
    }
  }

  public IBuddy[] getElements()
  {
    synchronized (buddies)
    {
      return buddies.values().toArray(new Buddy[buddies.size()]);
    }
  }

  public boolean isEmpty()
  {
    synchronized (buddies)
    {
      return buddies.isEmpty();
    }
  }

  public void close()
  {
    channel.close();
    deactivate();
  }

  public void notifyEvent(IEvent event)
  {
    if (event.getSource() == channel)
    {
      if (event instanceof ILifecycleEvent)
      {
        if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          deactivate();
        }
      }
    }
    else if (event.getSource() == self)
    {
      if (event instanceof IBuddyStateChangedEvent)
      {
        try
        {
          new BuddyStateNotification(channel, self.getUserID(), ((IBuddyStateChangedEvent)event).getNewState()).send();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  public void buddyAdded(String userID)
  {
    Buddy buddy = addBuddy(userID);
    if (buddy != null)
    {
      fireEvent(new SingleDeltaContainerEvent<IBuddy>(this, buddy, IContainerDelta.Kind.ADDED));
    }
  }

  public void buddyRemoved(String userID)
  {
    IBuddy buddy;
    synchronized (buddies)
    {
      buddy = buddies.remove(userID);
    }

    if (buddy != null)
    {
      fireEvent(new SingleDeltaContainerEvent<IBuddy>(this, buddy, IContainerDelta.Kind.REMOVED));
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    channel.addListener(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    channel.removeListener(this);
    super.doDeactivate();
  }
}
