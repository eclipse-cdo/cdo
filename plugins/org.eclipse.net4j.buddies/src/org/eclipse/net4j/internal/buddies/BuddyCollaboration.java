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

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.Message;
import org.eclipse.net4j.buddies.internal.protocol.MessageNotification;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.internal.buddies.protocol.InstallFacilityRequest;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class BuddyCollaboration extends Collaboration implements IBuddyCollaboration
{
  private static final String FACILITY_GROUP = ClientFacilityFactory.PRODUCT_GROUP;

  private IBuddySession session;

  public BuddyCollaboration(long id, Set<IBuddy> buddies)
  {
    super(id, buddies);
  }

  public IBuddySession getSession()
  {
    return session;
  }

  public IFacility installFacility(String type)
  {
    try
    {
      String description = String.valueOf(getID());
      IFacility facility = (IFacility)IPluginContainer.INSTANCE.getElement(FACILITY_GROUP, type, description);
      boolean success = new InstallFacilityRequest(session.getChannel(), getID(), type).send(ProtocolConstants.TIMEOUT);
      if (success)
      {
        addFacility(facility);
        return facility;
      }

      return null;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public void sendMessage(IMessage message)
  {
    if (message instanceof Message)
    {
      ((Message)message).setSenderID(session.getSelf().getUserID());

    }

    try
    {
      new MessageNotification(session.getChannel(), message).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public IBuddy invite(String userID)
  {
    return null;
  }

  public void leave()
  {
  }
}
