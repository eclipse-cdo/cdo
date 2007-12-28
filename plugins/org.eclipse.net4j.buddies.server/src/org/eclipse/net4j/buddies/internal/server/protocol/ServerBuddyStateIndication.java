/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.internal.server.ServerBuddy;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;

/**
 * @author Eike Stepper
 */
public class ServerBuddyStateIndication extends BuddyStateIndication
{
  public ServerBuddyStateIndication()
  {
  }

  @Override
  protected void stateChanged(String userID, State state)
  {
    synchronized (IBuddyAdmin.INSTANCE)
    {
      ISession session = IBuddyAdmin.INSTANCE.getSession(userID);
      if (session != null)
      {
        ServerBuddy buddy = (ServerBuddy)session.getSelf();
        buddy.setState(state);
      }
    }
  }
}