package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftIndication;
import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftNotification;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;

/**
 * @author Eike Stepper
 */
public class ServerCollaborationLeftIndication extends CollaborationLeftIndication
{
  public ServerCollaborationLeftIndication()
  {
    super(IBuddyAdmin.INSTANCE);
  }

  @Override
  protected void collaborationLeft(Collaboration collaboration, String userID)
  {
    for (IBuddy buddy : collaboration.getBuddies())
    {
      if (!ObjectUtil.equals(buddy.getUserID(), userID))
      {
        try
        {
          new CollaborationLeftNotification(buddy.getSession().getChannel(), collaboration.getID(), userID).send();
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }

    super.collaborationLeft(collaboration, userID);
    if (collaboration.getBuddies().length == 0 && !collaboration.isPublic())
    {
      BuddyAdmin.INSTANCE.removeCollaboration(collaboration.getID());
    }
  }
}