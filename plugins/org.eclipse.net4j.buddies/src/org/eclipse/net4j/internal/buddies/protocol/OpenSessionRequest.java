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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.internal.buddies.BuddySession;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends RequestWithConfirmation<IBuddySession>
{
  private String userID;

  private String password;

  public OpenSessionRequest(IChannel channel, String userID, String password)
  {
    super(channel);
    this.userID = userID;
    this.password = password;
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(userID);
    out.writeString(password);
  }

  @Override
  protected IBuddySession confirming(ExtendedDataInputStream in) throws IOException
  {
    IBuddyAccount account = ProtocolUtil.readAccount(in);
    if (account == null)
    {
      return null;
    }

    BuddySession session = new BuddySession(getProtocol().getChannel());
    getProtocol().setInfraStructure(session);
    session.setSelf(account);

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      session.buddyAdded(in.readString());
    }

    LifecycleUtil.activate(session);
    return session;
  }
}
