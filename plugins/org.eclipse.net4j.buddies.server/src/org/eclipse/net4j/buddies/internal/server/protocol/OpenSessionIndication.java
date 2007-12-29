/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.protocol.IAccount;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.buddies.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends IndicationWithResponse
{
  private IAccount account;

  private IBuddy[] buddies;

  public OpenSessionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    String userID = in.readString();
    String password = in.readString();
    int size = in.readInt();
    String[] facilityTypes = new String[size];
    for (int i = 0; i < size; i++)
    {
      facilityTypes[i] = in.readString();
    }

    synchronized (IBuddyAdmin.INSTANCE)
    {
      buddies = IBuddyAdmin.INSTANCE.getBuddies();
      ISession session = IBuddyAdmin.INSTANCE.openSession(getProtocol().getChannel(), userID, password, facilityTypes);
      if (session != null)
      {
        account = session.getSelf().getAccount();
      }
      else
      {
        OM.LOG.info("User denied: " + userID);
      }
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    ProtocolUtil.writeAccount(out, account);
    if (account != null)
    {
      List<IChannel> channels = new ArrayList<IChannel>();
      out.writeInt(buddies.length);
      for (IBuddy buddy : buddies)
      {
        out.writeString(buddy.getUserID());
        ISession buddySession = IBuddyAdmin.INSTANCE.getSession(buddy);
        if (buddySession != null)
        {
          channels.add(buddySession.getChannel());
        }
      }

      for (IChannel channel : channels)
      {
        try
        {
          new BuddyAddedNotification(channel, account.getUserID()).send();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }
}
