package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.internal.buddies.ClientBuddy;
import org.eclipse.net4j.internal.buddies.ClientSession;

/**
 * @author Eike Stepper
 */
public class ClientBuddyStateIndication extends BuddyStateIndication
{
  public ClientBuddyStateIndication()
  {
  }

  @Override
  protected void stateChanged(final String userID, final State state)
  {
    ClientSession session = ((ClientProtocol)getProtocol()).getSession();
    ClientBuddy buddy = (ClientBuddy)session.getBuddy(userID);
    if (buddy != null)
    {
      buddy.setState(state);
    }
  }
}