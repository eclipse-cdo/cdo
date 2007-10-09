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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.internal.buddies.BuddyCollaboration;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CollaborationInitiatedIndication extends Indication
{
  public CollaborationInitiatedIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_COLLABORATION_INITIATED;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    IBuddySession session = (IBuddySession)getProtocol().getInfraStructure();
    Self self = (Self)session.getSelf();

    long collaborationID = in.readLong();
    int size = in.readInt();
    Set<IBuddy> buddies = new HashSet<IBuddy>();
    for (int i = 0; i < size; i++)
    {
      String userID = in.readString();
      IBuddy buddy = session.getBuddies().get(userID);
      if (buddy != null)
      {
        buddies.add(buddy);
      }
    }

    BuddyCollaboration collaboration = new BuddyCollaboration(collaborationID, buddies);
    self.addCollaboration(collaboration);
  }
}
