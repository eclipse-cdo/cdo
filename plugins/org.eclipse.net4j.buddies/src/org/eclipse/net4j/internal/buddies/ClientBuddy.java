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

import org.eclipse.net4j.buddies.internal.protocol.Buddy;
import org.eclipse.net4j.buddies.protocol.IAccount;

/**
 * @author Eike Stepper
 */
public class ClientBuddy extends Buddy
{
  private String userID;

  private IAccount account;

  public ClientBuddy(ClientSession session, String userID)
  {
    super(session);
    this.userID = userID;
  }

  @Override
  public ClientSession getSession()
  {
    return (ClientSession)super.getSession();
  }

  public String getUserID()
  {
    return userID;
  }

  public IAccount getAccount()
  {
    if (account == null)
    {
      account = loadAccount(userID);
    }

    return account;
  }

  protected IAccount loadAccount(String userID)
  {
    return null;
  }
}
