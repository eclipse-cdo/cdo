/*
 * Copyright (c) 2007-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IBuddyStateEvent;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.common.BuddyContainer;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.buddies.internal.common.protocol.BuddyStateNotification;
import org.eclipse.net4j.internal.buddies.bundle.OM;
import org.eclipse.net4j.internal.buddies.protocol.BuddiesClientProtocol;
import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ClientSession extends BuddyContainer implements IBuddySession
{
  private BuddiesClientProtocol protocol;

  private Self self;

  public ClientSession(BuddiesClientProtocol protocol)
  {
    this.protocol = protocol;
  }

  @Override
  public BuddiesClientProtocol getProtocol()
  {
    return protocol;
  }

  @Override
  public Self getSelf()
  {
    return self;
  }

  public void setSelf(IAccount account, Set<String> facilityTypes)
  {
    self = new Self(this, account, facilityTypes);
    self.activate();
    self.addListener(this);
  }

  @Override
  public void close()
  {
    protocol.close();
    protocol = null;
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event.getSource() == protocol)
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
      if (event instanceof IBuddyStateEvent)
      {
        try
        {
          new BuddyStateNotification(protocol, self.getUserID(), ((IBuddyStateEvent)event).getNewState()).sendAsync();
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
    ClientBuddy buddy = new ClientBuddy(this, userID);
    buddy.activate();
    addBuddy(buddy);
  }

  public void buddyRemoved(String userID)
  {
    IBuddy buddy = removeBuddy(userID);
    if (buddy != null)
    {
      for (IMembership membership : self.getMemberships())
      {
        Collaboration collaboration = (Collaboration)membership.getCollaboration();
        collaboration.removeMembership(buddy);
      }

      LifecycleUtil.deactivate(buddy);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    protocol.addListener(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    protocol.removeListener(this);
    LifecycleUtil.deactivate(self);
    super.doDeactivate();
  }
}
