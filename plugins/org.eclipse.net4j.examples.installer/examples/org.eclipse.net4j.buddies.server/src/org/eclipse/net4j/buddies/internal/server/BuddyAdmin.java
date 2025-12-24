/*
 * Copyright (c) 2007-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IBuddyStateEvent;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.Account;
import org.eclipse.net4j.buddies.internal.common.Buddy;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.buddies.internal.common.CollaborationContainer;
import org.eclipse.net4j.buddies.internal.common.Membership;
import org.eclipse.net4j.buddies.internal.common.protocol.BuddyStateNotification;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.internal.server.protocol.BuddiesServerProtocol;
import org.eclipse.net4j.buddies.internal.server.protocol.BuddyRemovedNotification;
import org.eclipse.net4j.buddies.internal.server.protocol.CollaborationInitiatedNotification;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.buddies.spi.common.ServerFacilityFactory;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class BuddyAdmin extends CollaborationContainer implements IBuddyAdmin
{
  public static final BuddyAdmin INSTANCE = new BuddyAdmin();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, BuddyAdmin.class);

  private ConcurrentMap<String, IAccount> accounts = new ConcurrentHashMap<>();

  private ConcurrentMap<String, ISession> sessions = new ConcurrentHashMap<>();

  private long lastCollaborationID;

  public BuddyAdmin()
  {
    activate();
  }

  @Override
  public Map<String, IAccount> getAccounts()
  {
    return accounts;
  }

  @Override
  public ISession getSession(IBuddy buddy)
  {
    return getSession(buddy.getUserID());
  }

  @Override
  public ISession getSession(String userID)
  {
    return sessions.get(userID);
  }

  @Override
  public ISession[] getSessions()
  {
    return sessions.values().toArray(new ISession[sessions.size()]);
  }

  @Override
  public IBuddy[] getBuddies()
  {
    List<IBuddy> buddies = new ArrayList<>();
    for (ISession session : sessions.values())
    {
      buddies.add(session.getSelf());
    }

    return buddies.toArray(new IBuddy[buddies.size()]);
  }

  @Override
  public IBuddy getBuddy(String userID)
  {
    ISession session = getSession(userID);
    if (session == null)
    {
      return null;
    }

    return session.getSelf();
  }

  @Override
  public synchronized ISession openSession(IChannel channel, String userID, String password, String[] facilityTypes)
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

    ServerBuddy buddy = new ServerBuddy(account, facilityTypes);
    buddy.activate();
    buddy.addListener(this);

    BuddiesServerProtocol protocol = (BuddiesServerProtocol)channel.getReceiveHandler();
    ServerSession session = new ServerSession(protocol, buddy);
    protocol.setInfraStructure(session);
    session.addListener(this);
    buddy.setSession(session);
    LifecycleUtil.activate(session);

    if (TRACER.isEnabled())
    {
      TRACER.trace("Opened session: " + userID); //$NON-NLS-1$
    }

    sessions.put(userID, session);
    return session;
  }

  @Override
  public ICollaboration initiateCollaboration(IBuddy initiator, String... userIDs)
  {
    long collaborationID;
    synchronized (this)
    {
      collaborationID = ++lastCollaborationID;
    }

    Collaboration collaboration = new Collaboration(collaborationID);
    collaboration.activate();
    Membership.create(initiator, collaboration);

    Set<IBuddy> buddies = new HashSet<>();
    buddies.add(initiator);
    for (String userID : userIDs)
    {
      Buddy buddy = (Buddy)getBuddy(userID);
      if (buddy != null)
      {
        buddies.add(buddy);
        Membership.create(buddy, collaboration);
      }
    }

    addCollaboration(collaboration);

    Set<IBuddy> invitations = new HashSet<>(buddies);
    for (IBuddy buddy : buddies)
    {
      if (buddy != initiator)
      {
        try
        {
          invitations.remove(buddy);
          BuddiesServerProtocol protocol = (BuddiesServerProtocol)buddy.getSession().getProtocol();
          new CollaborationInitiatedNotification(protocol, collaborationID, invitations, null).sendAsync();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
        finally
        {
          invitations.add(buddy);
        }
      }
    }

    return collaboration;
  }

  @Override
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
                  BuddiesServerProtocol protocol = (BuddiesServerProtocol)session.getProtocol();
                  new BuddyRemovedNotification(protocol, userID).sendAsync();
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
      if (event instanceof IBuddyStateEvent)
      {
        IBuddyStateEvent e = (IBuddyStateEvent)event;
        synchronized (this)
        {
          for (ISession session : sessions.values())
          {
            try
            {
              if (!ObjectUtil.equals(session.getSelf(), e.getSource()))
              {
                BuddiesServerProtocol protocol = (BuddiesServerProtocol)session.getProtocol();
                new BuddyStateNotification(protocol, e.getSource().getUserID(), e.getNewState()).sendAsync();
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

  public static Set<String> getFacilityTypes()
  {
    return IPluginContainer.INSTANCE.getFactoryTypes(ServerFacilityFactory.PRODUCT_GROUP);
  }
}
