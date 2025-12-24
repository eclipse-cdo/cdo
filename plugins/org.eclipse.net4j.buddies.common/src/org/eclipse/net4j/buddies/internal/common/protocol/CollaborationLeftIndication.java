/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.buddies.common.IBuddyProvider;
import org.eclipse.net4j.buddies.common.ICollaborationProvider;
import org.eclipse.net4j.buddies.internal.common.Buddy;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class CollaborationLeftIndication extends Indication
{
  private IBuddyProvider buddyProvider;

  private ICollaborationProvider collaborationProvider;

  public CollaborationLeftIndication(SignalProtocol<?> protocol, IBuddyProvider buddyProvider, ICollaborationProvider collaborationProvider)
  {
    super(protocol, ProtocolConstants.SIGNAL_COLLABORATION_LEFT);
    this.buddyProvider = buddyProvider;
    this.collaborationProvider = collaborationProvider;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    long collaborationID = in.readLong();
    String userID = in.readString();

    Collaboration collaboration = getCollaboration(collaborationID);
    if (collaboration != null)
    {
      Buddy buddy = getBuddy(userID);
      if (buddy != null)
      {
        collaborationLeft(buddy, collaboration);
      }
    }
  }

  protected void collaborationLeft(Buddy buddy, Collaboration collaboration)
  {
    collaboration.removeMembership(buddy);
    buddy.removeMembership(collaboration);
  }

  protected Collaboration getCollaboration(long collaborationID)
  {
    return (Collaboration)collaborationProvider.getCollaboration(collaborationID);
  }

  protected Buddy getBuddy(String userID)
  {
    return (Buddy)buddyProvider.getBuddy(userID);
  }
}
