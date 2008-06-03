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
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.buddies.common.IBuddy.State;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class BuddyStateIndication extends Indication
{
  public BuddyStateIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_BUDDY_STATE;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    String userID = in.readString();
    State state = ProtocolUtil.readState(in);
    stateChanged(userID, state);
  }

  protected abstract void stateChanged(String userID, State state);
}
