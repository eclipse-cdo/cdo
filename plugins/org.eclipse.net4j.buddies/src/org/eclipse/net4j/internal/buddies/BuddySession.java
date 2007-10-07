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
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class BuddySession extends Lifecycle implements IBuddySession, IListener
{
  private IChannel channel;

  private IBuddyAccount account;

  private Set<String> buddies = new HashSet<String>();

  public BuddySession(IChannel channel, IBuddyAccount account, String[] buddies)
  {
    this.channel = channel;
    this.account = account;
    for (String buddy : buddies)
    {
      this.buddies.add(buddy);
    }

    LifecycleUtil.activate(this);
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public IBuddyAccount getAccount()
  {
    return account;
  }

  public Set<String> getBuddies()
  {
    synchronized (buddies)
    {
      return Collections.unmodifiableSet(buddies);
    }
  }

  public String[] getElements()
  {
    synchronized (buddies)
    {
      return buddies.toArray(new String[buddies.size()]);
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
