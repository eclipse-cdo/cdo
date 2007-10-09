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
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.internal.buddies.protocol.InitiateCollaborationRequest;
import org.eclipse.net4j.util.WrappedException;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Self extends Buddy
{
  private IAccount account;

  protected Self(ClientSession session, IAccount account, Set<String> facilityTypes)
  {
    super(session, facilityTypes);
    this.account = account;
  }

  @Override
  public ClientSession getSession()
  {
    return (ClientSession)super.getSession();
  }

  public String getUserID()
  {
    return account.getUserID();
  }

  public IAccount getAccount()
  {
    return account;
  }

  public ICollaboration initiate(Set<IBuddy> buddies)
  {
    try
    {
      long id = new InitiateCollaborationRequest(getSession().getChannel(), buddies).send(5000L);
      BuddyCollaboration collaboration = new BuddyCollaboration(id, buddies);
      addCollaboration(collaboration);
      return collaboration;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public ICollaboration join(long collaborationID)
  {
    // TODO Implement method Self.join()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public ICollaboration join(Object invitationToken)
  {
    // TODO Implement method Self.join()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
