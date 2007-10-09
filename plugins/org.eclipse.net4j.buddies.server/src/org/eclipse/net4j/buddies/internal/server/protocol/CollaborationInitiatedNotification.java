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
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CollaborationInitiatedNotification extends Request
{
  private long collaborationID;

  private Set<IBuddy> buddies;

  public CollaborationInitiatedNotification(IChannel channel, long collaborationID, Set<IBuddy> buddies)
  {
    super(channel);
    this.collaborationID = collaborationID;
    this.buddies = buddies;
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_COLLABORATION_INITIATED;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeLong(collaborationID);
    if (buddies == null)
    {
      out.writeInt(0);
    }
    else
    {
      out.writeInt(buddies.size());
      for (IBuddy buddy : buddies)
      {
        out.writeString(buddy.getUserID());
      }
    }
  }
}
