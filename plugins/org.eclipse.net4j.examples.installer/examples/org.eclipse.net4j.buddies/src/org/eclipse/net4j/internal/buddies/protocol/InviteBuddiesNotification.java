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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class InviteBuddiesNotification extends Request
{
  private long collaborationID;

  private Collection<IBuddy> buddies;

  public InviteBuddiesNotification(BuddiesClientProtocol protocol, long collaborationID, Collection<IBuddy> buddies)
  {
    super(protocol, ProtocolConstants.SIGNAL_INVITE_BUDDIES);
    this.buddies = buddies;
    this.collaborationID = collaborationID;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(collaborationID);
    ProtocolUtil.writeBuddies(out, buddies);
  }
}
