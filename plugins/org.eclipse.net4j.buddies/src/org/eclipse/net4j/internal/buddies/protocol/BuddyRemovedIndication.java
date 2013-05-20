/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.internal.buddies.ClientSession;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class BuddyRemovedIndication extends Indication
{
  public BuddyRemovedIndication(SignalProtocol<?> protocol)
  {
    super(protocol, ProtocolConstants.SIGNAL_BUDDY_REMOVED);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    String buddy = in.readString();
    ClientSession session = ((BuddiesClientProtocol)getProtocol()).getSession();
    session.buddyRemoved(buddy);
  }
}
