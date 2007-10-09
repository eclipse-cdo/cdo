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

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.buddies.server.IBuddySession;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends IndicationWithResponse
{
  private IBuddyAccount account;

  private String[] buddies;

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
    synchronized (IBuddyAdmin.INSTANCE)
    {
      Map<String, IBuddySession> sessions = IBuddyAdmin.INSTANCE.getSessions();
      buddies = sessions.keySet().toArray(new String[sessions.size()]);

      IBuddySession session = IBuddyAdmin.INSTANCE.openSession(getProtocol().getChannel(), userID, password);
      if (session != null)
      {
        account = session.getBuddy().getAccount();
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
      for (String buddy : buddies)
      {
        out.writeString(buddy);
        IBuddySession buddySession = IBuddyAdmin.INSTANCE.getSessions().get(buddy);
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
