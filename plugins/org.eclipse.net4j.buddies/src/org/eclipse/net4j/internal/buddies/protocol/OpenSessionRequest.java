/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.internal.buddies.ClientSession;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends RequestWithConfirmation<IBuddySession>
{
  private String userID;

  private String password;

  private Set<String> facilityTypes;

  public OpenSessionRequest(BuddiesClientProtocol protocol, String userID, String password, Set<String> facilityTypes)
  {
    super(protocol, ProtocolConstants.SIGNAL_OPEN_SESSION);
    this.userID = userID;
    this.password = password;
    this.facilityTypes = facilityTypes;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(userID);
    out.writeString(password);
    out.writeInt(facilityTypes.size());
    for (String facilityType : facilityTypes)
    {
      out.writeString(facilityType);
    }
  }

  @Override
  protected IBuddySession confirming(ExtendedDataInputStream in) throws Exception
  {
    IAccount account = ProtocolUtil.readAccount(in);
    if (account == null)
    {
      return null;
    }

    BuddiesClientProtocol protocol = (BuddiesClientProtocol)getProtocol();
    ClientSession session = new ClientSession(protocol);
    protocol.setInfraStructure(session);
    session.setSelf(account, facilityTypes);

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      session.buddyAdded(in.readString());
    }

    session.activate();
    return session;
  }
}
