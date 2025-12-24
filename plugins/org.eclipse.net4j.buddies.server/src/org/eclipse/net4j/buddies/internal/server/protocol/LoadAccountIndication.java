/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class LoadAccountIndication extends IndicationWithResponse
{
  private IAccount account;

  /**
   * @since 2.0
   */
  public LoadAccountIndication(BuddiesServerProtocol protocol)
  {
    super(protocol, ProtocolConstants.SIGNAL_LOAD_ACCOUNT);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    String userID = in.readString();
    account = IBuddyAdmin.INSTANCE.getAccounts().get(userID);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    ProtocolUtil.writeAccount(out, account);
  }
}
