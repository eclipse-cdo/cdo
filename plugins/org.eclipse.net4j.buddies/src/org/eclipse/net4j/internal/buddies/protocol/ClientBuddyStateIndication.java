package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.internal.buddies.ClientBuddy;
import org.eclipse.net4j.internal.buddies.ClientSession;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Eike Stepper
 */
public class ClientBuddyStateIndication extends BuddyStateIndication
{
  public ClientBuddyStateIndication()
  {
  }

  @Override
  protected void stateChanged(String userID, State state)
  {
    for (int i = 0; i < 50; i++)
    {
      ClientSession session = (ClientSession)getProtocol().getInfraStructure();
      if (session == null)
      {
        ConcurrencyUtil.sleep(100);
      }
      else
      {
        ClientBuddy buddy = (ClientBuddy)session.getBuddies().get(userID);
        if (buddy != null)
        {
          buddy.setState(state);
        }

        break;
      }
    }
  }
}