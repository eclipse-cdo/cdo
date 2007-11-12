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

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.ClientFacilityFactory;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftNotification;
import org.eclipse.net4j.buddies.internal.protocol.Message;
import org.eclipse.net4j.buddies.internal.protocol.MessageNotification;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.internal.buddies.protocol.InstallFacilityRequest;
import org.eclipse.net4j.internal.buddies.protocol.InviteBuddiesNotification;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class BuddyCollaboration extends Collaboration implements IBuddyCollaboration
{
  private static final String FACILITY_GROUP = ClientFacilityFactory.PRODUCT_GROUP;

  private IBuddySession session;

  public BuddyCollaboration(IBuddySession session, long id)
  {
    super(id);
    this.session = session;
  }

  public IBuddySession getSession()
  {
    return session;
  }

  public IFacility installFacility(String type)
  {
    try
    {
      IFacility facility = createFacility(type);
      IChannel channel = session.getChannel();
      boolean success = new InstallFacilityRequest(channel, getID(), type).send(ProtocolConstants.TIMEOUT);
      if (success)
      {
        addFacility(facility, false);
        return facility;
      }

      return null;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public IFacility createFacility(String type)
  {
    IFacility facility = (IFacility)getContainer().getElement(FACILITY_GROUP, type, String.valueOf(getID()));
    facility.setCollaboration(this);
    return facility;
  }

  @Override
  public void sendMessage(long collaborationID, String facilityType, IMessage message)
  {
    if (message instanceof Message)
    {
      ((Message)message).setSenderID(session.getSelf().getUserID());
    }

    try
    {
      new MessageNotification(session.getChannel(), collaborationID, facilityType, message).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public IBuddy[] invite(String... userIDs)
  {
    List<IBuddy> buddies = new ArrayList<IBuddy>();
    for (String userID : userIDs)
    {
      IBuddy buddy = session.getBuddy(userID);
      buddies.add(buddy);
    }

    IBuddy[] array = buddies.toArray(new IBuddy[buddies.size()]);
    invite(array);
    return array;
  }

  public void invite(IBuddy... buddies)
  {
    List<IBuddy> invitations = new ArrayList<IBuddy>();
    for (IBuddy buddy : buddies)
    {
      if (getMembership(buddy) == null)
      {
        invitations.add(buddy);
      }
    }

    if (!invitations.isEmpty())
    {
      try
      {
        new InviteBuddiesNotification(session.getChannel(), getID(), invitations).send();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  public void leave()
  {
    try
    {
      new CollaborationLeftNotification(session.getChannel(), getID(), session.getSelf().getUserID()).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    deactivate();
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
