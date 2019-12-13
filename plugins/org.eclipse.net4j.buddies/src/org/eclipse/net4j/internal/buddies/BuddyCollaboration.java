/*
 * Copyright (c) 2007, 2008, 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IFacility;
import org.eclipse.net4j.buddies.common.IMessage;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.buddies.internal.common.protocol.CollaborationLeftNotification;
import org.eclipse.net4j.buddies.internal.common.protocol.MessageNotification;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.spi.common.ClientFacilityFactory;
import org.eclipse.net4j.buddies.spi.common.Message;
import org.eclipse.net4j.internal.buddies.protocol.BuddiesClientProtocol;
import org.eclipse.net4j.internal.buddies.protocol.InstallFacilityRequest;
import org.eclipse.net4j.internal.buddies.protocol.InviteBuddiesNotification;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
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

  @Override
  public IBuddySession getSession()
  {
    return session;
  }

  @Override
  public IFacility installFacility(String type)
  {
    return installFacility(type, true);
  }

  public IFacility installFacility(String type, boolean request)
  {
    IFacility facility = createFacility(type);
    if (request)
    {
      try
      {
        BuddiesClientProtocol protocol = (BuddiesClientProtocol)session.getProtocol();
        boolean success = new InstallFacilityRequest(protocol, getID(), type).send(ProtocolConstants.TIMEOUT);
        if (!success)
        {
          return null;
        }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    addFacility(facility, !request);
    return facility;
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
      BuddiesClientProtocol protocol = (BuddiesClientProtocol)session.getProtocol();
      new MessageNotification(protocol, collaborationID, facilityType, message).sendAsync();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
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

  @Override
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
        BuddiesClientProtocol protocol = (BuddiesClientProtocol)session.getProtocol();
        new InviteBuddiesNotification(protocol, getID(), invitations).sendAsync();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  @Override
  public void leave()
  {
    try
    {
      BuddiesClientProtocol protocol = (BuddiesClientProtocol)session.getProtocol();
      new CollaborationLeftNotification(protocol, getID(), session.getSelf().getUserID()).sendAsync();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    deactivate();
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
