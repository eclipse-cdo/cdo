package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.MessageIndication;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.internal.buddies.ClientSession;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Eike Stepper
 */
public class ClientMessageIndication extends MessageIndication
{
  public ClientMessageIndication()
  {
  }

  @Override
  protected void messageReceived(IMessage message)
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
        Self self = session.getSelf();
        long collaborationID = message.getCollaborationID();

        Collaboration collaboration = (Collaboration)self.getCollaborations().get(collaborationID);
        collaboration.notifyMessage(message);
        break;
      }
    }
  }
}