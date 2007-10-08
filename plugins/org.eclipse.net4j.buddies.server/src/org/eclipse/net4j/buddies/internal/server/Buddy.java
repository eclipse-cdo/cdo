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

import org.eclipse.net4j.buddies.internal.protocol.AbstractBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;

/**
 * @author Eike Stepper
 */
public class Buddy extends AbstractBuddy
{
  private IBuddyAccount account;

  public Buddy(IBuddyAccount account)
  {
    this.account = account;
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
