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

import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;

/**
 * @author Eike Stepper
 */
public class Self implements IBuddy
{
  private BuddySession session;

  private IBuddyAccount account;

  protected Self(BuddySession session, IBuddyAccount account)
  {
    this.session = session;
    this.account = account;
  }

  public BuddySession getSession()
  {
    return session;
  }

  public String getUserID()
  {
    return account.getUserID();
  }

  public IBuddyAccount getAccount()
  {
    return account;
  }
}
