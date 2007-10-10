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
package org.eclipse.net4j.buddies.internal.server;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.Facility;
import org.eclipse.net4j.buddies.internal.protocol.MessageNotification;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public class ServerFacility extends Facility
{
  public ServerFacility(String type)
  {
    super(type);
  }

  public void handleMessage(IMessage message)
  {
    ICollaboration collaboration = getCollaboration();
    for (IBuddy buddy : collaboration.getElements())
    {
      if (!ObjectUtil.equals(buddy.getUserID(), message.getSenderID()))
      {
        try
        {
          IChannel channel = buddy.getSession().getChannel();
          new MessageNotification(channel, message).send();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }
}
