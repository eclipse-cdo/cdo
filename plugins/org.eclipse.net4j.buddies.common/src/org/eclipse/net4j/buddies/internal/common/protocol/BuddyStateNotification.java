/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.buddies.common.IBuddy.State;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class BuddyStateNotification extends Request
{
  private String userID;

  private State state;

  public BuddyStateNotification(SignalProtocol<?> protocol, String userID, State state)
  {
    super(protocol, ProtocolConstants.SIGNAL_BUDDY_STATE);
    this.userID = userID;
    this.state = state;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(userID);
    ProtocolUtil.writeState(out, state);
  }
}
