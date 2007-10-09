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

import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadAccountIndication extends IndicationWithResponse
{
  private IBuddyAccount account;

  public LoadAccountIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_LOAD_ACCOUNT;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    String userID = in.readString();
    account = IBuddyAdmin.INSTANCE.getAccounts().get(userID);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    ProtocolUtil.writeAccount(out, account);
  }
}
