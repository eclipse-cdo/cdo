/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
