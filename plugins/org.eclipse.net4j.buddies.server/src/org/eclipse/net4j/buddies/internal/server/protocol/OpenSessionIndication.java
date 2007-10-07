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

import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.protocol.BuddiesProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.buddies.server.IBuddySession;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
    return BuddiesProtocolConstants.SIGNAL_OPEN_SESSION;
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
        getProtocol().setInfraStructure(session);
        account = session.getAccount();
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
    if (account != null)
    {
      out.writeBoolean(true);
      out.writeInt(buddies.length);
      for (String buddy : buddies)
      {
        out.writeString(buddy);
      }

      ObjectOutputStream oos = new ObjectOutputStream(out);
      oos.writeObject(account);
    }
    else
    {
      out.writeBoolean(false);
    }
  }
}
