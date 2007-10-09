package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.MessageIndication;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ServerMessageIndication extends MessageIndication
{
  public ServerMessageIndication()
  {
  }

  @Override
  protected void messageReceived(IMessage message)
  {
    synchronized (IBuddyAdmin.INSTANCE)
    {
      Map<Long, ICollaboration> collaborations = IBuddyAdmin.INSTANCE.getCollaborations();
      Collaboration collaboration = (Collaboration)collaborations.get(message.getCollaborationID());
      if (collaboration != null)
      {
        collaboration.notifyMessage(message);
      }
    }
  }
}