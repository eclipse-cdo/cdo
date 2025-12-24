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
    super(null, new HashSet<>(Arrays.asList(facilityTypes)));
    this.account = account;
  }

  @Override
  public String getUserID()
  {
    return account.getUserID();
  }

  @Override
  public IAccount getAccount()
  {
    return account;
  }

  @Override
  public IMembership[] initiate(Collection<IBuddy> buddies)
  {
    // TODO Implement method ServerBuddy.initiate()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }

  @Override
  public IMembership join(long collaborationID)
  {
    // TODO Implement method ServerBuddy.join()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }

  @Override
  public IMembership join(Object invitationToken)
  {
    // TODO Implement method ServerBuddy.join()
    throw new UnsupportedOperationException("Not yet implemented"); //$NON-NLS-1$
  }
}
