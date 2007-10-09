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

import org.eclipse.net4j.buddies.internal.protocol.Buddy;
import org.eclipse.net4j.buddies.protocol.IAccount;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ICollaboration;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Eike Stepper
 */
public class ServerBuddy extends Buddy
{
  private IAccount account;

  public ServerBuddy(IAccount account, String[] facilityTypes)
  {
    super(null, new HashSet<String>(Arrays.asList(facilityTypes)));
    this.account = account;
  }

  public String getUserID()
  {
    return account.getUserID();
  }

  public IAccount getAccount()
  {
    return account;
  }

  public ICollaboration initiate(IBuddy... buddies)
  {
    // TODO Implement method ServerBuddy.initiate()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public ICollaboration join(String collaborationID)
  {
    // TODO Implement method ServerBuddy.join()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
