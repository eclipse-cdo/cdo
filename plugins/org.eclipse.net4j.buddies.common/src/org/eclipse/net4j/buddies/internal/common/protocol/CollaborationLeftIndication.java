/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

  public CollaborationLeftIndication(SignalProtocol<?> protocol, IBuddyProvider buddyProvider,
      ICollaborationProvider collaborationProvider)
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
