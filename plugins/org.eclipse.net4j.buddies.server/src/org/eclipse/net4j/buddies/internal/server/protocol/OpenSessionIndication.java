/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.internal.server.messages.Messages;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends IndicationWithResponse
{
  private IAccount account;

  private IBuddy[] buddies;

  /**
   * @since 2.0
   */
  public OpenSessionIndication(BuddiesServerProtocol protocol)
  {
    super(protocol, ProtocolConstants.SIGNAL_OPEN_SESSION);
  }

  @Override
  protected boolean closeChannelAfterException()
  {
    return true;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
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
        OM.LOG.info(MessageFormat.format(Messages.getString("OpenSessionIndication.0"), userID)); //$NON-NLS-1$
      }
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    ProtocolUtil.writeAccount(out, account);
    if (account != null)
    {
      List<BuddiesServerProtocol> protocols = new ArrayList<>();
      out.writeInt(buddies.length);
      for (IBuddy buddy : buddies)
      {
        out.writeString(buddy.getUserID());
        ISession buddySession = IBuddyAdmin.INSTANCE.getSession(buddy);
        if (buddySession != null)
        {
          protocols.add((BuddiesServerProtocol)buddySession.getProtocol());
        }
      }

      for (BuddiesServerProtocol protocol : protocols)
      {
        try
        {
          new BuddyAddedNotification(protocol, account.getUserID()).sendAsync();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }
}
