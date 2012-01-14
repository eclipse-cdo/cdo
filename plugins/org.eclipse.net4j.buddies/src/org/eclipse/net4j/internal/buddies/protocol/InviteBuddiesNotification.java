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
