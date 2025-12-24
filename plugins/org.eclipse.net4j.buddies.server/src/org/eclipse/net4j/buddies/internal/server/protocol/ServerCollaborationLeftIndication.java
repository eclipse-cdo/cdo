/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.common.Buddy;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.buddies.internal.common.protocol.CollaborationLeftIndication;
import org.eclipse.net4j.buddies.internal.common.protocol.CollaborationLeftNotification;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.util.WrappedException;

/**
 * @author Eike Stepper
 */
public class ServerCollaborationLeftIndication extends CollaborationLeftIndication
{
  /**
   * @since 2.0
   */
  public ServerCollaborationLeftIndication(BuddiesServerProtocol protocol)
  {
    super(protocol, IBuddyAdmin.INSTANCE, IBuddyAdmin.INSTANCE);
  }

  @Override
  protected void collaborationLeft(Buddy buddy, Collaboration collaboration)
  {
    for (IMembership membership : collaboration.getMemberships())
    {
      IBuddy member = membership.getBuddy();
      if (member != buddy)
      {
        try
        {
          BuddiesServerProtocol protocol = (BuddiesServerProtocol)buddy.getSession().getProtocol();
          new CollaborationLeftNotification(protocol, collaboration.getID(), buddy.getUserID()).sendAsync();
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }

    super.collaborationLeft(buddy, collaboration);
    if (collaboration.getBuddies().length == 0 && !collaboration.isPublic())
    {
      BuddyAdmin.INSTANCE.removeCollaboration(collaboration.getID());
    }
  }
}
