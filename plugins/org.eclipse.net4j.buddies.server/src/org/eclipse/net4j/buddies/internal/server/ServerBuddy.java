/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.common.Buddy;

import java.util.Arrays;
import java.util.Collection;
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

  public IMembership[] initiate(Collection<IBuddy> buddies)
  {
    // TODO Implement method ServerBuddy.initiate()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }

  public IMembership join(long collaborationID)
  {
    // TODO Implement method ServerBuddy.join()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }

  public IMembership join(Object invitationToken)
  {
    // TODO Implement method ServerBuddy.join()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }
}
