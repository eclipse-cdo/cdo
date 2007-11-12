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
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.Membership;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class InviteBuddiesIndication extends Indication
{
  public InviteBuddiesIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_INVITE_BUDDIES;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    long collaborationID = in.readLong();
    String[] userIDs = ProtocolUtil.readUserIDs(in);

    Collaboration collaboration = (Collaboration)BuddyAdmin.INSTANCE.getCollaboration(collaborationID);
    if (collaboration != null)
    {
      Set<IBuddy> added = new HashSet<IBuddy>();
      for (String userID : userIDs)
      {
        IBuddy buddy = BuddyAdmin.INSTANCE.getBuddy(userID);
        if (buddy != null && collaboration.getMembership(buddy) == null)
        {
          Membership.create(buddy, collaboration);
          added.add(buddy);
        }
      }

      List<IBuddy> buddies = Arrays.asList(collaboration.getBuddies());
      for (IBuddy buddy : buddies)
      {
        IChannel channel = buddy.getSession().getChannel();
        String[] facilityTypes = null;
        Set<IBuddy> set = new HashSet<IBuddy>();
        if (added.contains(buddy))
        {
          set.addAll(buddies);
          set.remove(buddy);
          facilityTypes = collaboration.getFacilityTypes();
        }
        else
        {
          set.addAll(added);
        }

        if (!set.isEmpty())
        {
          try
          {
            new CollaborationInitiatedNotification(channel, collaborationID, set, facilityTypes).send();
          }
          catch (Exception ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }
  }
}
