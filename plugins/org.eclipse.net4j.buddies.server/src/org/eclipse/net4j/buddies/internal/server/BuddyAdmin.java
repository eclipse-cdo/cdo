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
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.buddies.server.IBuddySession;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class BuddyAdmin extends Lifecycle implements IBuddyAdmin
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

    BuddySession session = new BuddySession(channel, account);
    sessions.put(userID, session);
    return session;
  }
}
