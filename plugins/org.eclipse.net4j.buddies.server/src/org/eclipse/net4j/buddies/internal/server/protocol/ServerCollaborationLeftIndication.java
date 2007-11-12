package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.Buddy;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftIndication;
import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftNotification;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IMembership;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.util.WrappedException;

/**
 * @author Eike Stepper
 */
public class ServerCollaborationLeftIndication extends CollaborationLeftIndication
{
  public ServerCollaborationLeftIndication()
  {
    super(IBuddyAdmin.INSTANCE, IBuddyAdmin.INSTANCE);
  }

  @Override
  protected void collaborationLeft(Buddy buddy, Collaboration collaboration)
  {
    for (IMembership membership : collaboration.getMemberships())
    {
      IBuddy member = membership.getBuddy();
      if (member != buddy)
      {
        try
        {
          IChannel channel = member.getSession().getChannel();
          new CollaborationLeftNotification(channel, collaboration.getID(), buddy.getUserID()).send();
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }

    super.collaborationLeft(buddy, collaboration);
    if (collaboration.getBuddies().length == 0 && !collaboration.isPublic())
    {
      BuddyAdmin.INSTANCE.removeCollaboration(collaboration.getID());
    }
  }
}