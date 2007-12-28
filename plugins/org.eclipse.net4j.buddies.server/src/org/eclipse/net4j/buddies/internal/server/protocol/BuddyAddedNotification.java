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
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class BuddyAddedNotification extends Request
{
  private String buddy;

  public BuddyAddedNotification(IChannel channel, String buddy)
  {
    super(channel);
    this.buddy = buddy;
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_BUDDY_ADDED;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(buddy);
  }
}
