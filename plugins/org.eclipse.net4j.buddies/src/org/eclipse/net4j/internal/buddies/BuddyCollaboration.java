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

  public BuddyCollaboration(IBuddySession session, long id, Set<IBuddy> buddies)
  {
    super(id, buddies);
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

  public IBuddy invite(String userID)
  {
    return null;
  }

  public void leave()
  {
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
