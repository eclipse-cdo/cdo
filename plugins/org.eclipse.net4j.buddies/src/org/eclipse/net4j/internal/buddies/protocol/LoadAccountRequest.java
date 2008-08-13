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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadAccountRequest extends RequestWithConfirmation<IAccount>
{
  private String userID;

  public LoadAccountRequest(BuddiesClientProtocol protocol, String userID)
  {
    super(protocol);
    this.userID = userID;
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_LOAD_ACCOUNT;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(userID);
  }

  @Override
  protected IAccount confirming(ExtendedDataInputStream in) throws IOException
  {
    return ProtocolUtil.readAccount(in);
  }
}
