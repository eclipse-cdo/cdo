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

import org.eclipse.net4j.buddies.internal.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.MessageIndication;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.ServerBuddy;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ServerProtocol extends SignalProtocol
{
  public ServerProtocol()
  {
  }

  public String getType()
  {
    return ProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication();

    case ProtocolConstants.SIGNAL_BUDDY_STATE:
      return new BuddyStateIndication()
      {
        @Override
        protected void stateChanged(String userID, State state)
        {
          synchronized (IBuddyAdmin.INSTANCE)
          {
            Map<String, ISession> sessions = IBuddyAdmin.INSTANCE.getSessions();
            ISession session = sessions.get(userID);
            if (session != null)
            {
              ServerBuddy buddy = (ServerBuddy)session.getSelf();
              buddy.setState(state);
            }
          }
        }
      };

    case ProtocolConstants.SIGNAL_INSTALL_FACILITY:
      return new InstallFacilityIndication();

    case ProtocolConstants.SIGNAL_MESSAGE:
      return new MessageIndication()
      {
        @Override
        protected void messageReceived(IMessage message)
        {
          synchronized (IBuddyAdmin.INSTANCE)
          {
            Map<String, ICollaboration> collaborations = IBuddyAdmin.INSTANCE.getCollaborations();
            Collaboration collaboration = (Collaboration)collaborations.get(message.getCollaborationID());
            if (collaboration != null)
            {
              collaboration.notifyMessage(message);
            }
          }
        }
      };
    }

    return null;
  }
}
