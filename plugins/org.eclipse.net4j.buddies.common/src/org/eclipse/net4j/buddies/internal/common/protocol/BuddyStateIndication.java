/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public abstract class BuddyStateIndication extends Indication
{
  public BuddyStateIndication(SignalProtocol<?> protocol)
  {
    super(protocol, ProtocolConstants.SIGNAL_BUDDY_STATE);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    String userID = in.readString();
    State state = ProtocolUtil.readState(in);
    stateChanged(userID, state);
  }

  protected abstract void stateChanged(String userID, State state);
}
