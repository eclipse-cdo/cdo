package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.internal.server.ServerBuddy;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ServerBuddyStateIndication extends BuddyStateIndication
{
  public ServerBuddyStateIndication()
  {
  }

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
}