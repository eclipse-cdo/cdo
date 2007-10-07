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
import org.eclipse.net4j.buddies.protocol.BuddiesProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.internal.buddies.BuddySession;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;

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
    return BuddiesProtocolConstants.SIGNAL_OPEN_SESSION;
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
    boolean granted = in.readBoolean();
    if (!granted)
    {
      return null;
    }

    int size = in.readInt();
    String[] buddies = new String[size];
    for (int i = 0; i < size; i++)
    {
      buddies[i] = in.readString();
    }

    IBuddyAccount account;
    ObjectInputStream ois = new ObjectInputStream(in);
    try
    {
      account = (IBuddyAccount)ois.readObject();
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    BuddySession session = new BuddySession(getProtocol().getChannel(), account, buddies);
    getProtocol().setInfraStructure(session);
    return session;
  }
}
