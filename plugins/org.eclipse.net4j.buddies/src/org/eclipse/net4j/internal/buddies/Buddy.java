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

import org.eclipse.net4j.buddies.internal.protocol.AbstractBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;

/**
 * @author Eike Stepper
 */
public class Buddy extends AbstractBuddy
{
  private BuddySession session;

  private String userID;

  private IBuddyAccount account;

  public Buddy(BuddySession session, String userID)
  {
    this.session = session;
    this.userID = userID;
  }

  protected Buddy(BuddySession session, IBuddyAccount account)
  {
  }

  public BuddySession getSession()
  {
    return session;
  }

  public String getUserID()
  {
    return userID;
  }

  public IBuddyAccount getAccount()
  {
    if (account == null)
    {
      account = loadAccount(userID);
    }

    return account;
  }

  protected IBuddyAccount loadAccount(String userID)
  {
    return null;
  }
}
