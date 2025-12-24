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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.common.IBuddy.State;
import org.eclipse.net4j.buddies.internal.common.protocol.BuddyStateIndication;
import org.eclipse.net4j.internal.buddies.ClientBuddy;
import org.eclipse.net4j.internal.buddies.ClientSession;

/**
 * @author Eike Stepper
 */
public class ClientBuddyStateIndication extends BuddyStateIndication
{
  public ClientBuddyStateIndication(BuddiesClientProtocol protocol)
  {
    super(protocol);
  }

  @Override
  protected void stateChanged(final String userID, final State state)
  {
    ClientSession session = ((BuddiesClientProtocol)getProtocol()).getSession();
    ClientBuddy buddy = (ClientBuddy)session.getBuddy(userID);
    if (buddy != null)
    {
      buddy.setState(state);
    }
  }
}
