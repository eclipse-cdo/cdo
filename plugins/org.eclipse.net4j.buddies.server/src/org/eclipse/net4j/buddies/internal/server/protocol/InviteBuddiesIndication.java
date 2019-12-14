/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.buddies.internal.common.Membership;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class InviteBuddiesIndication extends Indication
{
  /**
   * @since 2.0
   */
  public InviteBuddiesIndication(BuddiesServerProtocol protocol)
  {
    super(protocol, ProtocolConstants.SIGNAL_INVITE_BUDDIES);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    long collaborationID = in.readLong();
    String[] userIDs = ProtocolUtil.readUserIDs(in);

    Collaboration collaboration = (Collaboration)BuddyAdmin.INSTANCE.getCollaboration(collaborationID);
    if (collaboration != null)
    {
      Set<IBuddy> added = new HashSet<>();
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
        String[] facilityTypes = null;
        Set<IBuddy> set = new HashSet<>();
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
            BuddiesServerProtocol protocol = (BuddiesServerProtocol)buddy.getSession().getProtocol();
            new CollaborationInitiatedNotification(protocol, collaborationID, set, facilityTypes).sendAsync();
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
