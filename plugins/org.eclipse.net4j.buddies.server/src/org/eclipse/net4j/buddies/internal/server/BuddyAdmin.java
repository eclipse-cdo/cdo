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
package org.eclipse.net4j.buddies.internal.server;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.BuddyAccount;
import org.eclipse.net4j.buddies.internal.protocol.BuddyStateNotification;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.internal.server.protocol.BuddyRemovedNotification;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.buddies.server.IBuddySession;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class BuddyAdmin extends Lifecycle implements IBuddyAdmin, IListener
{
  public static final BuddyAdmin INSTANCE = new BuddyAdmin();

  private Map<String, IBuddyAccount> accounts = new HashMap<String, IBuddyAccount>();

  private Map<String, IBuddySession> sessions = new HashMap<String, IBuddySession>();

  public BuddyAdmin()
  {
    LifecycleUtil.activate(this);
  }

  public Map<String, IBuddyAccount> getAccounts()
  {
    return accounts;
  }

  public Map<String, IBuddySession> getSessions()
  {
    return sessions;
  }

  public synchronized IBuddySession openSession(IChannel channel, String userID, String password)
  {
    if (sessions.containsKey(userID))
    {
      return null;
    }

    IBuddyAccount account = accounts.get(userID);
    if (account != null)
    {
      if (!account.authenticate(password))
      {
        return null;
      }
    }
    else
    {
      account = new BuddyAccount(userID, password);
      accounts.put(userID, account);
    }

    Buddy buddy = new Buddy(account);
    buddy.addListener(this);

    BuddySession session = new BuddySession(channel, buddy);
    sessions.put(userID, session);
    session.addListener(this);
    return session;
  }

  public void notifyEvent(IEvent event)
  {
    if (event.getSource() instanceof BuddySession)
    {
      if (event instanceof ILifecycleEvent)
      {
        if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          String userID = ((BuddySession)event.getSource()).getBuddy().getUserID();
          synchronized (this)
          {
            BuddySession removed = (BuddySession)sessions.remove(userID);
            if (removed != null)
            {
              removed.removeListener(this);
              removed.getBuddy().removeListener(this);
              for (IBuddySession session : sessions.values())
              {
                try
                {
                  new BuddyRemovedNotification(session.getChannel(), userID).send();
                }
                catch (Exception ex)
                {
                  OM.LOG.error(ex);
                }
              }
            }
          }
        }
      }
    }
    else if (event.getSource() instanceof Buddy)
    {
      if (event instanceof IBuddyStateChangedEvent)
      {
        IBuddyStateChangedEvent e = (IBuddyStateChangedEvent)event;
        synchronized (this)
        {
          for (IBuddySession session : sessions.values())
          {
            try
            {
              if (!ObjectUtil.equals(session.getBuddy(), e.getBuddy()))
              {
                new BuddyStateNotification(session.getChannel(), e.getBuddy().getUserID(), e.getNewState()).send();
              }
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        }
      }
    }
  }
}
