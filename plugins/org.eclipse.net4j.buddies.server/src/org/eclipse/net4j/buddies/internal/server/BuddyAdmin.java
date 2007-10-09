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
import org.eclipse.net4j.IProtocol;
import org.eclipse.net4j.buddies.internal.protocol.Account;
import org.eclipse.net4j.buddies.internal.protocol.BuddyStateNotification;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.internal.server.protocol.BuddyRemovedNotification;
import org.eclipse.net4j.buddies.protocol.IAccount;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
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

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, BuddyAdmin.class);

  private Map<String, IAccount> accounts = new HashMap<String, IAccount>();

  private Map<String, ISession> sessions = new HashMap<String, ISession>();

  public BuddyAdmin()
  {
    LifecycleUtil.activate(this);
  }

  public Map<String, IAccount> getAccounts()
  {
    return accounts;
  }

  public Map<String, ISession> getSessions()
  {
    return sessions;
  }

  public synchronized ISession openSession(IChannel channel, String userID, String password)
  {
    if (sessions.containsKey(userID))
    {
      return null;
    }

    IAccount account = accounts.get(userID);
    if (account != null)
    {
      if (!account.authenticate(password))
      {
        return null;
      }
    }
    else
    {
      account = new Account(userID, password);
      accounts.put(userID, account);
    }

    ServerBuddy buddy = new ServerBuddy(account);
    buddy.addListener(this);

    ServerSession session = new ServerSession(channel, buddy);
    ((IProtocol)channel.getReceiveHandler()).setInfraStructure(session);

    sessions.put(userID, session);
    session.addListener(this);
    if (TRACER.isEnabled()) TRACER.trace("Opened session: " + userID);
    return session;
  }

  public void notifyEvent(IEvent event)
  {
    if (event.getSource() instanceof ServerSession)
    {
      if (event instanceof ILifecycleEvent)
      {
        if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          String userID = ((ServerSession)event.getSource()).getSelf().getUserID();
          synchronized (this)
          {
            ServerSession removed = (ServerSession)sessions.remove(userID);
            if (removed != null)
            {
              removed.removeListener(this);
              removed.getSelf().removeListener(this);
              for (ISession session : sessions.values())
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
    else if (event.getSource() instanceof ServerBuddy)
    {
      if (event instanceof IBuddyStateChangedEvent)
      {
        IBuddyStateChangedEvent e = (IBuddyStateChangedEvent)event;
        synchronized (this)
        {
          for (ISession session : sessions.values())
          {
            try
            {
              if (!ObjectUtil.equals(session.getSelf(), e.getBuddy()))
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
