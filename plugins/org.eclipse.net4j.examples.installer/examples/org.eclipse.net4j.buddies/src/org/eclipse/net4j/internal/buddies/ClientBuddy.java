/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.common.Buddy;

import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ClientBuddy extends Buddy
{
  private String userID;

  private IAccount account;

  public ClientBuddy(ClientSession session, String userID)
  {
    super(session, null);
    this.userID = userID;
  }

  @Override
  public ClientSession getSession()
  {
    return (ClientSession)super.getSession();
  }

  @Override
  public String getUserID()
  {
    return userID;
  }

  @Override
  public IAccount getAccount()
  {
    if (account == null)
    {
      account = loadAccount(userID);
    }

    return account;
  }

  @Override
  public IMembership[] initiate(Collection<IBuddy> buddies)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IMembership join(long collaborationID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IMembership join(Object invitationToken)
  {
    throw new UnsupportedOperationException();
  }

  protected IAccount loadAccount(String userID)
  {
    // TODO Implement method ClientBuddy.loadAccount()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }

  @Override
  protected Set<String> loadFacilityTypes()
  {
    // TODO Implement method ClientBuddy.loadFacilityTypes()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }
}
