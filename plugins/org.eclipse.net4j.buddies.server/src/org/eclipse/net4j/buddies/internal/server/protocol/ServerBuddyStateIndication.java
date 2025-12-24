/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IBuddy.State;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.internal.server.ServerBuddy;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;

/**
 * @author Eike Stepper
 */
public class ServerBuddyStateIndication extends BuddyStateIndication
{
  /**
   * @since 2.0
   */
  public ServerBuddyStateIndication(BuddiesServerProtocol protocol)
  {
    super(protocol);
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
