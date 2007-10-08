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
import org.eclipse.net4j.buddies.protocol.AccountUtil;
import org.eclipse.net4j.buddies.protocol.BuddiesProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadAccountRequest extends RequestWithConfirmation<IBuddyAccount>
{
  private String userID;

  public LoadAccountRequest(IChannel channel, String userID)
  {
    super(channel);
    this.userID = userID;
  }

  @Override
  protected short getSignalID()
  {
    return BuddiesProtocolConstants.SIGNAL_LOAD_ACCOUNT;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(userID);
  }

  @Override
  protected IBuddyAccount confirming(ExtendedDataInputStream in) throws IOException
  {
    return AccountUtil.readAccount(in);
  }
}
